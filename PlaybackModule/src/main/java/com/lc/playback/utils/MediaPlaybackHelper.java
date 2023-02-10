package com.lc.playback.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.WindowManager;

import com.mm.android.mobilecommon.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

public class MediaPlaybackHelper {

    private final static String ProjectName = "LechangeDemo";

    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }

    public enum LCFilesType {
        LCImage,
        LCVideo,
        LCImageCache
    }

    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void quitFullScreen(Activity activity) {
        final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    /**
     * Creating a file Path
     * @param file     File
     * @param filePath File path
     * @return boolean  Boolean value
     * <p>
     * 创建文件路径
     * @param file     文件
     * @param filePath 文件路径
     * @return boolean  布尔值
     */
    public static boolean createFilePath(File file, String filePath) {
        LogUtil.debugLog("20210513", "filePath::" + filePath);
        int index = filePath.indexOf("/");
        if (index == -1) {
            LogUtil.debugLog("20210513", "filePath---------:" + file.getAbsolutePath() + "  filepath::" + filePath);
            LogUtil.debugLog("20210513", "filePath---------::" + file.getPath() + " " + file.getName() + " " + file.getParent());
            // scanFile(file);
            return false;
        }
        if (index == 0) {
            filePath = filePath.substring(index + 1, filePath.length());
            index = filePath.indexOf("/");
        }
        String path = filePath.substring(0, index);
        File fPath;
        if (file == null) {
            fPath = new File(path);
        } else {
            fPath = new File(file.getPath() + "/" + path);
        }
        if (!fPath.exists()) {
            if (!fPath.mkdir()) // SD卡已满无法在下载文件
            {
                return false;
            }
        }
        if (index < (filePath.length() - 1)) {
            String exPath = filePath.substring(index + 1, filePath.length());
            createFilePath(fPath, exPath);
        }
        return true;
    }

    /**
     * Generate a capture path or video storage path
     * <p>
     * 生成抓图路径或录像存放路径
     */
    public static String getCaptureAndVideoPath(LCFilesType type, String cameraName) {
        String path = null;
        String picType = null;
        java.util.Date now = new java.util.Date();
        SimpleDateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sdPath = mContext.getExternalFilesDir("demo").getAbsolutePath();
        } else {
            sdPath = Environment.getExternalStorageDirectory().getPath();
        }


        if (type == LCFilesType.LCImage) {
            picType = "image";
            path = sdPath + "/" + ProjectName + "/" + tf.format(now) + "_" + picType + "_"
                    + cameraName + ".jpg";
        } else {
            picType = "video";
            path = sdPath + "/" + ProjectName + "/" + tf.format(now) + "_" + picType + "_"
                    + cameraName + ".mp4";
        }
        if (type == LCFilesType.LCImageCache) {
            picType = "imageCache";
            path = sdPath + "/" + ProjectName + "/" + picType + "/" + tf.format(now) + "_"
                    + cameraName + ".jpg";
        }
        createFilePath(null, path);
        return path;
    }

    public static String getCaptureAndVideoPath(String cameraName) {
        String path = null;
        String picType = null;
        java.util.Date now = new java.util.Date();
        SimpleDateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sdPath = mContext.getExternalFilesDir("demo").getAbsolutePath();
        } else {
            sdPath = Environment.getExternalStorageDirectory().getPath();
        }
        picType = "image";
        path = sdPath + "/" + ProjectName + "/" + tf.format(now) + "_" + picType + "_"
                + cameraName + ".txt";
        createFilePath(null, path);
        return path;
    }


    public static void updatePhotoAlbum(String path) {
        if (path == null) return;
        String[] split = path.split("/");
        String fileName = split[split.length - 1];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                return;
            }
            OutputStream out = null;
            FileInputStream fis = null;
            try {
                out = contentResolver.openOutputStream(uri);
                fis = new FileInputStream(path);
                FileUtils.copy(fis, out);
                fis.close();
                out.close();

                //导出相册成功后，删除App沙盒内的临时文件
                MediaPlaybackHelper.delete(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(mContext.getApplicationContext(), new String[]{path}, new String[]{"image/jpeg"}, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        }
    }

    public static void updatePhotoVideo(String path) {
        if (path == null) return;
        String[] split = path.split("/");
        String fileName = split[split.length - 1];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.VideoColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Video.VideoColumns.MIME_TYPE, "video/3gp");
            values.put(MediaStore.Video.VideoColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                return;
            }
            OutputStream out = null;
            FileInputStream fis = null;
            try {
                out = contentResolver.openOutputStream(uri);
                fis = new FileInputStream(path);
                FileUtils.copy(fis, out);
                fis.close();
                out.close();

                //导出相册成功后，删除App沙盒内的临时文件
                MediaPlaybackHelper.delete(path);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(mContext.getApplicationContext(), new String[]{path}, new String[]{"video/3gp"}, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        }
    }


    public static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getName());
        return type;
    }


    /**
     * Generate a path for storing downloaded videos
     * <p>
     * 生成下载录像存放路径
     */
    public static String getDownloadVideoPath(int type, String recordID, long startTime) {
        String path = null;
        String sdPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sdPath = mContext.getExternalFilesDir("demo").getAbsolutePath();
        } else {
            sdPath = Environment.getExternalStorageDirectory().getPath();
        }

        String picType = "download";
        picType += type == 0 ? "_cloud" : "_remote";
        java.util.Date now = new java.util.Date(startTime);
        SimpleDateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss");
        path = sdPath + "/" + ProjectName + "/" + tf.format(now) + "_" + picType + "_"
                + recordID + ".mp4";
        createFilePath(null, path);
        return path;
    }

    /**
     * Delete downloaded videos stored in the video
     * <p>
     * 删除下载录像存放的录像
     */
    public static void deleteDownloadVideo(String recordID, long startTime) {
        String path = null;
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String picType = "download";
        java.util.Date now = new java.util.Date(startTime);
        SimpleDateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss");
        path = sdPath + "/" + ProjectName + "/" + tf.format(now) + "_" + picType + "_"
                + recordID + ".mp4";
        File soFile = new File(path);
        if (soFile.exists()) {
            soFile.delete();
        }
    }

    /**
     * Deleting cache files
     * <p>
     * 删除缓存文件
     */
    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static BitmapDrawable picDrawable(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            return bd;
        } catch (Throwable e) {
            return null;
        }
    }
}
