package com.zxing.qrcode;///*

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zxing.DeviceHelper;
import com.zxing.Extras;
import com.zxing.IZxingActivity;
import com.zxing.R;
import com.zxing.camera.CameraManager;
import com.zxing.decode.DecodeThread;
import com.zxing.decode.RGBLuminanceSource;
import com.zxing.utils.BeepManager;
import com.zxing.utils.CaptureActivityHandler;
import com.zxing.utils.InactivityTimer;
import com.zxing.utils.Validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * Purpose: To sweep
 *
 * 用途：扫一扫
 */
public final class CaptureActivity extends Activity implements IZxingActivity, SurfaceHolder.Callback {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private final int REQUEST_CODE = 33;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private String fromType;

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void drawViewfinder() {

    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public int getRootLayoutId() {
        return R.layout.activity_capture;
    }

    protected int getTitleId() {
        return R.string.scan_title;
    }

    public void initParam(Bundle intent) {
        fromType = intent.getString(Extras.FROM.FROM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置根视图
        View mContentView = LayoutInflater.from(this).inflate(getRootLayoutId(), null);
        setContentView(mContentView);
        afterViewBind(mContentView, savedInstanceState);
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        scanPreview = findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        ImageView scanLine = findViewById(R.id.capture_scan_line);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    public void enterGallery() {
        // 进入图库
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.d(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     *
     * 找到了有效的条形码，因此给出成功的指示并显示结果。
     * @param rawResult 条形码的内容。
     * @param bundle 额外收费的
     */
    @Override
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        doProcess(rawResult.getText());
        restartPreviewAfterDelay(3000);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            Toast.makeText(this, R.string.capture_no_camera, Toast.LENGTH_SHORT).show();
            finish();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            Toast.makeText(this, R.string.capture_no_camera, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    @Override
    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * Initializes the truncated rectangular region
     *
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void doProcess(String result) {
        Log.d(TAG, "scanResult: " + result);
        if (!DeviceHelper.getNetworkState()) {
            Toast.makeText(this, R.string.capture_no_network, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(result)) {
            Toast.makeText(this, R.string.capture_no_result, Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(fromType)) {
                switch (fromType) {
                    case Extras.FROM.SCAN_BACK:
                        Intent intent = new Intent();
                        intent.putExtra("data", result);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case Extras.FROM.H5_SCAN_JUMP:
                        setResult(RESULT_OK);
                        finish();
                        break;
                    default:
                        break;
                }
            } else {
                jumpResult(result);
            }
        }
    }

    private void jumpResult(String result) {
        if (Validator.checkUrl(result) && !(result.toLowerCase().endsWith(".apk"))) {
            finish();
        } else if (Validator.checkUrl(result) && result.toLowerCase().endsWith(".apk")) {
            finish();
        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                String path = originalUri.getPath();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    path = cursor.getString(column_index);
                    cursor.close();
                }

                if (!TextUtils.isEmpty(path)) {
                    handleQRCodeFormPhoto(path);
                } else {
                    Toast.makeText(this, "图片已损坏，请重新选择！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Parse the two-dimensional code selected by the gallery
     *
     * 解析图库选择的二维码
     */
    public void handleQRCodeFormPhoto(final String filePath) {
        Thread dealThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Hashtable<DecodeHintType, String> hints = new Hashtable<>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
                RGBLuminanceSource source = null;
                try {
                    source = new RGBLuminanceSource(filePath);
                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
                }
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();
                Result result;
                try {
                    result = reader.decode(binaryBitmap, hints);
                    if (!TextUtils.isEmpty(result.getText())) {
                        dealUIInfo(result.getText());
                    } else {
                        dealUIInfo(null);
                    }
                } catch (NotFoundException | ChecksumException | FormatException e) {
                    dealUIInfo(null);
//                    e.printStackTrace();
                }
            }
        });
        dealThread.start();
    }

    private void dealUIInfo(final Object progressInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressInfo == null) {
                    Toast.makeText(CaptureActivity.this, R.string.capture_no_result2, Toast.LENGTH_SHORT).show();
                } else {
                    doProcess(progressInfo.toString());
                }
            }
        });
    }
}