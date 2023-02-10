package com.lc.playback.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.lc.playback.R;
import com.lc.playback.api.DeviceRecordService;
import com.lc.playback.api.IRecordInfoCallBack;
import com.lc.playback.contacts.MethodConst;
import com.lc.playback.entity.DeleteCloudRecordsData;
import com.lc.playback.entity.DeviceDetailListData;
import com.lc.playback.entity.RecordsData;
import com.lc.playback.utils.DateHelper;
import com.lc.playback.utils.MediaPlaybackHelper;
import com.lc.playback.view.EncryptKeyInputDialog;
import com.lechange.opensdk.listener.LCOpenSDK_DownloadListener;
import com.lechange.opensdk.listener.LCOpenSDK_EventListener;
import com.lechange.opensdk.media.LCOpenSDK_Download;
import com.lechange.opensdk.media.LCOpenSDK_ParamCloudRecord;
import com.lechange.opensdk.media.LCOpenSDK_ParamDeviceRecord;
import com.lechange.opensdk.media.LCOpenSDK_PlayWindow;
import com.lechange.opensdk.media.LCOpenSDK_StatusCode;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.utils.DialogUtils;
import com.mm.android.mobilecommon.utils.LCUtils;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.widget.LcProgressBar;
import com.mm.android.mobilecommon.widget.RecoderSeekBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DeviceRecordPlayActivity extends AppCompatActivity implements View.OnClickListener, IRecordInfoCallBack.IDeviceDeleteRecordCallBack {
    private static final String TAG = DeviceRecordPlayActivity.class.getSimpleName();
    private FrameLayout frLiveWindow, frLiveWindowContent;
    private TextView tvDeviceName, tvLoadingMsg, recordStartTime, recordEndTime, recordStream;
    private LinearLayout llFullScreen, llSound, llPlayStyle, llPlayPause, llDelete, llBack, llScreenshot, llVideo;
    private ImageView ivPalyPause, ivPlayStyle, ivSound, ivScreenShot, ivVideo;
    private ProgressBar pbLoading;
    private RelativeLayout rlLoading;
    private LcProgressBar pgDownload;
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    private Bundle bundle;
    private LCOpenSDK_PlayWindow mPlayWin = new LCOpenSDK_PlayWindow();
    private RecordsData recordsData;
    //1 云录像 2 设备录像
    private int recordType;
    private RecoderSeekBar recordSeekbar;
    private int progress;
    private String beginTime;

    private SoundStatus soundStatus = SoundStatus.PLAY;
    private PlayStatus playStatus = PlayStatus.PAUSE;
    private RecordStatus recordStatus = RecordStatus.STOP;
    private DownloadStatus downloadStatus = DownloadStatus.UNBEGIN;
    //倍速位置
    private int speedPosition = 0;
    //倍速数组
    private int[] speed = {1, 2, 4, 8, 16, 32};
    //倍速图片
    private Drawable[] speedImage = new Drawable[6];
    private DeviceRecordService deviceRecordService = DeviceRecordService.newInstance();
    private String totalMb;
    private ImageView ivChangeScreen;
    private LinearLayout llOperate;
    private RelativeLayout rlTitle;
    private EncryptKeyInputDialog encryptKeyInputDialog;
    private String encryptKey;
    private String path;
    private LCAlertDialog mLCAlertDialog;
    private int passcode;
    private String videoPath;
    private String endTime;
    private long startVideoClickTime;
    private long endVideoClickTime;

    public enum LoadStatus {
        LOADING, LOAD_SUCCESS, LOAD_ERROR
    }

    public enum SoundStatus {
        PLAY, STOP, NO_SUPPORT
    }

    public enum PlayStatus {
        PLAY, PAUSE, ERROR, STOP
    }

    public enum RecordStatus {
        START, STOP
    }

    public enum DownloadStatus {
        UNBEGIN, ING, FINISH, ERROR
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_record_play);
        initView();
        initData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(frLiveWindow.getLayoutParams());
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            mLayoutParams.width = metric.widthPixels; // 屏幕宽度（像素）
            mLayoutParams.height = metric.widthPixels * 9 / 16;
            mLayoutParams.setMargins(0, 0, 0, 0);
            frLiveWindow.setLayoutParams(mLayoutParams);
            MediaPlaybackHelper.quitFullScreen(DeviceRecordPlayActivity.this);
            llOperate.setVisibility(View.VISIBLE);
//            if (recordType == MethodConst.ParamConst.recordTypeLocal) {
//                pgDownload.setVisibility(View.GONE);
//            } else {
                pgDownload.setVisibility(View.VISIBLE);
//            }
            rlTitle.setVisibility(View.VISIBLE);
        } else {
//            DisplayMetrics metric = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metric);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.setMargins(0, 0, 0, 0);
            frLiveWindow.setLayoutParams(mLayoutParams);
            MediaPlaybackHelper.setFullScreen(DeviceRecordPlayActivity.this);
            llOperate.setVisibility(View.GONE);
            pgDownload.setVisibility(View.GONE);
            rlTitle.setVisibility(View.GONE);
        }
    }

    private void initData() {
        speedImage[0] = getResources().getDrawable(R.mipmap.icon_1x);
        speedImage[1] = getResources().getDrawable(R.mipmap.icon_2x);
        speedImage[2] = getResources().getDrawable(R.mipmap.icon_4x);
        speedImage[3] = getResources().getDrawable(R.mipmap.icon_8x);
        speedImage[4] = getResources().getDrawable(R.mipmap.icon_16x);
        speedImage[5] = getResources().getDrawable(R.mipmap.icon_32x);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        Gson gson = new Gson();
//        deviceListBean = (DeviceDetailListData.ResponseData.DeviceListBean) bundle.getSerializable(MethodConst.ParamConst.deviceDetail);
//        recordsData = (RecordsData) bundle.getSerializable(MethodConst.ParamConst.recordData);
        deviceListBean = gson.fromJson(bundle.getString(MethodConst.ParamConst.deviceDetail), DeviceDetailListData.ResponseData.DeviceListBean.class);
        recordsData = gson.fromJson(bundle.getString(MethodConst.ParamConst.recordData), RecordsData.class);
        recordType = bundle.getInt(MethodConst.ParamConst.recordType);
        tvDeviceName.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);
        if (recordType == MethodConst.ParamConst.recordTypeLocal) {
            llDelete.setVisibility(View.GONE);
//            pgDownload.setVisibility(View.GONE);
            totalMb = byte2mb(Long.parseLong(recordsData.fileLength + ""));
        } else {
            totalMb = byte2mb(Long.parseLong(recordsData.size));
        }
        //初始化时间
        initSeekBarAndTime();
        //初始化控件
        initCommonClickListener();
        //播放视频
        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), deviceListBean.deviceId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAsync();
        playStatus = PlayStatus.PAUSE;
        ivPalyPause.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_play));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        mPlayWin.uninitPlayWindow();// 销毁底层资源
    }

    private void playVideo(String psk) {
        if (recordType == MethodConst.ParamConst.recordTypeCloud) {
            //云录像
            LCOpenSDK_ParamCloudRecord paramCloudRecord = new LCOpenSDK_ParamCloudRecord(
                    TokenHelper.getInstance().subAccessToken,
                    deviceListBean.deviceId,
                    Integer.parseInt(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId),
                    psk,
                    deviceListBean.playToken,
                    recordsData.recordRegionId,
                    1000,
                    0,
                    24 * 3600,
                    deviceListBean.productId
            );

            mPlayWin.playCloud(paramCloudRecord);
        } else if (recordType == MethodConst.ParamConst.recordTypeLocal) {
            //设备录像
            LCOpenSDK_ParamDeviceRecord paramDeviceRecord = new LCOpenSDK_ParamDeviceRecord(
                    TokenHelper.getInstance().subAccessToken,
                    deviceListBean.deviceId,
                    Integer.parseInt(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId),
                    psk,
                    deviceListBean.playToken,
                    recordsData.recordId,
                    DateHelper.parseMills(recordsData.beginTime),
                    DateHelper.parseMills(recordsData.endTime),
                    0,
                    0,
                    true,
                    deviceListBean.productId
            );

            mPlayWin.playRtspPlayback(paramDeviceRecord);
        }
    }

    private void stop() {
        stopPlayWindow();
//        //禁止拖动
//        setCanSeekChanged(false);
    }

    /**
     * Turn off Play separately,Note: The component requires that the main thread call be required
     *
     * 单独关闭播放  注意：组件要求必须要主线程调用
     */
    private void stopPlayWindow() {
        closeAudio();// 关闭音频
        if (recordType == MethodConst.ParamConst.recordTypeCloud) {
            mPlayWin.stopCloud(true);
        } else {
            mPlayWin.stopRtspPlayback(true);// 关闭视频
        }
    }

    /**
     * Sets whether the drag progress bar can be used
     *
     * 设置拖动进度条是否能使用
     */
    public void setCanSeekChanged(boolean canSeek) {
        recordSeekbar.setCanTouchAble(canSeek);
    }

    /**
     * Start record
     *
     * 开始录像
     */
    public boolean startRecord() {
        // 录像的路径
        String channelName = null;
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
            channelName = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName;
        } else {
            channelName = deviceListBean.deviceName;
        }
        // 去除通道中在目录中的非法字符
        channelName = channelName.replace("-", "");
        videoPath = MediaPlaybackHelper.getCaptureAndVideoPath(MediaPlaybackHelper.LCFilesType.LCVideo, channelName);
//        MediaScannerConnection.scanFile(this, new String[]{videoPath}, null, null);
        // 开始录制 1
        int ret = mPlayWin.startRecord(videoPath, 1, 0x7FFFFFFF);
        return ret == 0;
    }


    /**
     * Stop record
     *
     * 关闭录像
     */
    public boolean stopRecord() {

        return mPlayWin.stopRecord() == 0;
    }

    /**
     * Screenshot
     *
     * 截图
     */
    public String capture() {
        String captureFilePath = null;
        String channelName = null;
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
            channelName = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName;
        } else {
            channelName = deviceListBean.deviceName;
        }
        // 去除通道中在目录中的非法字符
        channelName = channelName.replace("-", "");
        captureFilePath = MediaPlaybackHelper.getCaptureAndVideoPath(MediaPlaybackHelper.LCFilesType.LCImage, channelName);
        int ret = mPlayWin.snapShot(captureFilePath);
        if (ret == 0) {
            // 扫描到相册中
//            MediaScannerConnection.scanFile(this, new String[]{captureFilePath}, null, null);
            MediaPlaybackHelper.updatePhotoAlbum(captureFilePath);
        } else {
            captureFilePath = null;
        }
        return captureFilePath;
    }


    private void initView() {
        frLiveWindow = findViewById(R.id.fr_live_window);
        frLiveWindowContent = findViewById(R.id.fr_live_window_content);
        recordStream = findViewById(R.id.tv_record_stream);
        llBack = findViewById(R.id.ll_back);
        tvDeviceName = findViewById(R.id.tv_device_name);
        llDelete = findViewById(R.id.ll_delete);
        llPlayPause = findViewById(R.id.ll_paly_pause);
        llPlayStyle = findViewById(R.id.ll_play_style);
        llSound = findViewById(R.id.ll_sound);
        llFullScreen = findViewById(R.id.ll_fullscreen);
        ivPalyPause = findViewById(R.id.iv_paly_pause);
        ivPlayStyle = findViewById(R.id.iv_play_style);
        ivSound = findViewById(R.id.iv_sound);
        rlLoading = findViewById(R.id.rl_loading);
        pbLoading = findViewById(R.id.pb_loading);
        tvLoadingMsg = findViewById(R.id.tv_loading_msg);
        recordStartTime = findViewById(R.id.record_startTime);
        recordSeekbar = findViewById(R.id.record_seekbar);
        recordEndTime = findViewById(R.id.record_endTime);
        llScreenshot = findViewById(R.id.ll_screenshot);
        llVideo = findViewById(R.id.ll_video);
        ivScreenShot = findViewById(R.id.iv_screen_shot);
        ivVideo = findViewById(R.id.iv_video);
        pgDownload = findViewById(R.id.pg_download);
        ivChangeScreen = findViewById(R.id.iv_change_screen);
        llOperate = findViewById(R.id.ll_operate);
        rlTitle = findViewById(R.id.rl_title);
        pgDownload.setText(getResources().getString(R.string.lc_demo_device_record_download));
        // 初始化播放窗口
        LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) frLiveWindow
                .getLayoutParams();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLayoutParams.width = metric.widthPixels; // 屏幕宽度（像素）
        mLayoutParams.height = metric.widthPixels * 9 / 16;
        mLayoutParams.setMargins(0, 0, 0, 0);
        frLiveWindow.setLayoutParams(mLayoutParams);
        mPlayWin.initPlayWindow(this, frLiveWindowContent, 0, false);
        setWindowListener(mPlayWin);
        mPlayWin.openTouchListener();//开启收拾监听
    }

    private void initCommonClickListener() {
        llBack.setOnClickListener(this);
        llFullScreen.setOnClickListener(this);
        if (recordType == MethodConst.ParamConst.recordTypeCloud) {
            llDelete.setOnClickListener(this);

//            LCOpenSDK_Download.setListener(new CloudDownloadListener());
        }
        pgDownload.setOnClickListener(this);
    }

    private void featuresClickListener(boolean loadSuccess) {
        llPlayStyle.setOnClickListener(loadSuccess ? this : null);
        llPlayPause.setOnClickListener(loadSuccess ? this : null);
        llSound.setOnClickListener(loadSuccess && speedPosition == 0 ? this : null);
        ivScreenShot.setOnClickListener(loadSuccess ? this : null);
        ivVideo.setOnClickListener(loadSuccess ? this : null);
        ivPalyPause.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause_disable));
        ivSound.setImageDrawable(loadSuccess && speedPosition == 0 ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off_disable));
        ivScreenShot.setImageDrawable(loadSuccess ? getDrawable(R.drawable.lc_demo_photo_capture_selector) : getDrawable(R.mipmap.lc_demo_livepreview_icon_screenshot_disable));
        ivVideo.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_livepreview_icon_video) : getDrawable(R.mipmap.lc_demo_livepreview_icon_video_disable));
        //媒体声
        if (soundStatus != SoundStatus.PLAY) {
            return;
        }
        if (loadSuccess && speedPosition == 0 && openAudio()) {
            soundStatus = SoundStatus.PLAY;
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_on));
        }
    }

    private void featuresSoundClickListener(boolean enable) {
        llSound.setOnClickListener(enable ? this : null);
        ivSound.setImageDrawable(enable ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_on) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off_disable));
        if (enable && openAudio()) {
            soundStatus = SoundStatus.PLAY;
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_on));
        } else {
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off_disable));
        }

    }

    private void initSeekBarAndTime() {
        String startTime = recordsData.beginTime.substring(11);
        endTime = recordsData.endTime.substring(11);
        recordSeekbar.setMax((int) ((DateHelper.parseMills(recordsData.endTime) - DateHelper.parseMills(recordsData.beginTime)) / 1000));
        recordSeekbar.setProgress(0);
        recordStartTime.setText(startTime);
        recordEndTime.setText(endTime);
    }


    private void setSeekBarListener() {
        recordSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (recordSeekbar.getMax() - DeviceRecordPlayActivity.this.progress <= 2) {
                    seek(recordSeekbar.getMax() >= 2 ? recordSeekbar.getMax() - 2 : 0);
                } else {
                    seek(DeviceRecordPlayActivity.this.progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {
                if (byUser) {
                    DeviceRecordPlayActivity.this.progress = progress;
                }
            }
        });
    }

    /**
     * Continue playing (asynchronous)
     *
     * 继续播放(异步)
     */
    public void resumeAsync() {
        mPlayWin.resumeAsync();
    }

    /**
     * Pause play (asynchronous)
     *
     * 暂停播放(异步)
     */
    public void pauseAsync() {
        mPlayWin.pauseAsync();
    }

    public boolean openAudio() {
        return mPlayWin.playAudio() == 0;
    }

    public boolean closeAudio() {
        return mPlayWin.stopAudio() == 0;
    }

    public void seek(int index) {
        long seekTime = DateHelper.parseMills(recordsData.beginTime) / 1000 + index;
        //先暂存时间记录
        beginTime = DateHelper.getTimeHMS(seekTime * 1000);
        this.progress = index;
        recordSeekbar.setProgress(index);
        recordStartTime.setText(this.beginTime);
        Log.i(TAG, "seeking----" + index);
        if (index == 0) {//拖动到初始位置，重新播放
            loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), deviceListBean.deviceId);
        } else {
            mPlayWin.seek(index);
        }

    }

//    private int mCurrentOrientation;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_back) {
            if (downloadStatus == DownloadStatus.ING) {
                LCAlertDialog.Builder builder = new LCAlertDialog.Builder(this);
                builder.setTitle(R.string.lc_demo_device_delete_exit);
                builder.setMessage(R.string.lc_demo_device_delete_exit_tip);
                builder.setCancelButton(R.string.common_cancel, null);
                builder.setConfirmButton(R.string.common_confirm,
                        (dialog, which, isChecked) -> {
                            stopDownLoad();
                            finish();
                        });
                mLCAlertDialog = builder.create();
                mLCAlertDialog.show(getSupportFragmentManager(), "exit");
                return;
            }
            onBackPressed();
        } else if (id == R.id.ll_fullscreen) {
            //横竖屏切换
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            ivChangeScreen.setImageDrawable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? getResources().getDrawable(R.mipmap.live_btn_smallscreen) : getResources().getDrawable(R.mipmap.icon_hengping));
        } else if (id == R.id.pg_download) {
            //下载
            if (downloadStatus == DownloadStatus.ING || downloadStatus == DownloadStatus.FINISH) {
                return;
            }
            pgDownloadProgress(
                    getResources().getString(R.string.lc_demo_device_record_download_begin),
                    0,
                    100,
                    getResources().getColor(R.color.lc_demo_color_ffffff));
            downloadStatus = DownloadStatus.ING;
            startDownload(deviceListBean.deviceId);
        } else if (id == R.id.ll_play_style) {
            if (speedPosition == 5) {
                speedPosition = 0;
                featuresSoundClickListener(true);
            } else {
                speedPosition = speedPosition + 1;
                featuresSoundClickListener(false);
            }
            ivPlayStyle.setImageDrawable(speedImage[speedPosition]);
            mPlayWin.setPlaySpeed(speed[speedPosition]);
        } else if (id == R.id.ll_delete) {
            LCAlertDialog.Builder builder = new LCAlertDialog.Builder(this);
            builder.setTitle(R.string.lc_demo_device_delete_sure);
            builder.setMessage("");
            builder.setCancelButton(R.string.common_cancel, null);
            builder.setConfirmButton(R.string.common_confirm,
                    (dialog, which, isChecked) -> {
                        //删除
                        DialogUtils.show(DeviceRecordPlayActivity.this);
                        DeleteCloudRecordsData deleteCloudRecordsData = new DeleteCloudRecordsData();
                        List<String> recordRegionIds = new ArrayList<>();
                        recordRegionIds.add(recordsData.recordRegionId);
                        deleteCloudRecordsData.data.recordRegionIds = recordRegionIds;
                        deleteCloudRecordsData.data.productId = deviceListBean.productId;
                        deviceRecordService.deleteCloudRecords(deleteCloudRecordsData, DeviceRecordPlayActivity.this);
                    });
            mLCAlertDialog = builder.create();
            mLCAlertDialog.show(getSupportFragmentManager(), "delete");
        } else if (id == R.id.ll_paly_pause) {
            //播放暂停 重新播放
            if (playStatus == PlayStatus.PLAY) {
                pauseAsync();
                // 如果当前正在录制，则需要停止录制操作
                if (recordStatus == RecordStatus.START) {
                    recordStatus = RecordStatus.STOP;
                    if (stopRecord()) {
                        Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_stop), Toast.LENGTH_SHORT).show();
                        LogUtil.debugLog(TAG, "videopath:::" + videoPath);
                        MediaPlaybackHelper.updatePhotoVideo(videoPath);
                    }
                }
                featuresClickListener(false);
                llPlayPause.setOnClickListener(this);
                playStatus = (playStatus == PlayStatus.PLAY) ? PlayStatus.PAUSE : PlayStatus.PLAY;
                ivPalyPause.setImageDrawable(playStatus == PlayStatus.PLAY ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_play));
            } else if (playStatus == PlayStatus.PAUSE) {
                resumeAsync();
                featuresClickListener(true);
                llPlayPause.setOnClickListener(this);
                playStatus = (playStatus == PlayStatus.PLAY) ? PlayStatus.PAUSE : PlayStatus.PLAY;
                ivPalyPause.setImageDrawable(playStatus == PlayStatus.PLAY ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_play));
            } else {
                loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey);
            }
        } else if (id == R.id.ll_sound) {
            //媒体声 如果是开启去关闭，反之
            if (soundStatus == SoundStatus.NO_SUPPORT) {
                return;
            }
            boolean result = false;
            if (soundStatus == SoundStatus.PLAY) {
                result = closeAudio();
            } else {
                result = openAudio();
            }
            if (!result) {
                return;
            }
            soundStatus = (soundStatus == SoundStatus.PLAY) ? SoundStatus.STOP : SoundStatus.PLAY;
            ivSound.setImageDrawable(soundStatus == SoundStatus.PLAY ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_on) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off));
        } else if (id == R.id.iv_screen_shot) {
            //截图
            if (capture() != null) {
                Toast.makeText(this, getResources().getString(R.string.lc_demo_device_capture_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.lc_demo_device_capture_failed), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_video) {
            if (playStatus != PlayStatus.PLAY) {
                return;
            }
            //录像 如果是关闭状态去打开，反之
            if (recordStatus == RecordStatus.STOP) {
                if (startRecord()) {
                    startVideoClickTime = System.currentTimeMillis();
                    Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_begin), Toast.LENGTH_SHORT).show();
                } else {
                    return;
                }
            } else {
                endVideoClickTime = System.currentTimeMillis();
                if (endVideoClickTime - startVideoClickTime < 3 * 1000) {
                    Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_d_time), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (stopRecord()) {
                    Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_stop), Toast.LENGTH_SHORT).show();
                    LogUtil.debugLog(TAG, "videopath:::" + videoPath);
                    MediaPlaybackHelper.updatePhotoVideo(videoPath);
                } else {
                    return;
                }
            }
            recordStatus = (recordStatus == RecordStatus.START) ? RecordStatus.STOP : RecordStatus.START;
            ivVideo.setImageDrawable(recordStatus == RecordStatus.START ? getDrawable(R.mipmap.lc_demo_livepreview_icon_video_ing) : getDrawable(R.mipmap.lc_demo_livepreview_icon_video));
        }
    }

    private void setWindowListener(LCOpenSDK_PlayWindow playWin) {
        playWin.setWindowListener(new LCOpenSDK_EventListener() {
            //手势缩放开始事件
            @Override
            public void onZoomBegin(int index) {
                super.onZoomBegin(index);
                LogUtil.debugLog(TAG, "onZoomBegin: index= " + index);
            }

            //手势缩放中事件
            @Override
            public void onZooming(int index, float dScale) {
                super.onZooming(index, dScale);
                LogUtil.debugLog(TAG, "onZooming: index= " + index + " , dScale= " + dScale);
                mPlayWin.doScale(dScale);
            }

            //缩放结束事件
            @Override
            public void onZoomEnd(int index, ZoomType zoomType) {
                super.onZoomEnd(index, zoomType);
                LogUtil.debugLog(TAG, "onZoomEnd: index= " + index + " , zoomType= " + zoomType);
            }

            //窗口单击事件
            @Override
            public void onControlClick(int index, float dx, float dy) {
                super.onControlClick(index, dx, dy);
                LogUtil.debugLog(TAG, "onControlClick: index= " + index + " , dx= " + dx + " , dy= " + dy);
            }

            //窗口双击事件
            @Override
            public void onWindowDBClick(int index, float dx, float dy) {
                super.onWindowDBClick(index, dx, dy);
                LogUtil.debugLog(TAG, "onWindowDBClick: index= " + index + " , dx= " + dx + " , dy= " + dy);
            }

            //滑动开始事件
            @Override
            public boolean onSlipBegin(int index, Direction direction, float dx, float dy) {
                LogUtil.debugLog(TAG, "onSlipBegin: index= " + index + " , direction= " + direction + " , dx= " + dx + " , dy= " + dy);
                return super.onSlipBegin(index, direction, dx, dy);
            }

            //滑动中事件
            @Override
            public void onSlipping(int index, Direction direction, float prex, float prey, float dx, float dy) {
                super.onSlipping(index, direction, prex, prey, dx, dy);
                mPlayWin.doTranslate(dx, dy);
                LogUtil.debugLog(TAG, "onSlipping: index= " + index + " , direction= " + direction + " , prex= " + prex + " , prey= " + prey + " , dx= " + dx + " , dy= " + dy);
            }

            //滑动结束事件
            @Override
            public void onSlipEnd(int index, Direction direction, float dx, float dy) {
                super.onSlipEnd(index, direction, dx, dy);
                mPlayWin.doTranslateEnd();
                LogUtil.debugLog(TAG, "onSlipEnd: index= " + index + " , direction= " + direction + " , dx= " + dx + " , dy= " + dy);
            }

            //长按开始回调
            @Override
            public void onWindowLongPressBegin(int index, Direction direction, float dx, float dy) {
                super.onWindowLongPressBegin(index, direction, dx, dy);
                LogUtil.debugLog(TAG, "onWindowLongPressBegin: index= " + index + " , direction= " + direction + " , dx= " + dx + " , dy= " + dy);
            }

            //长按事件结束
            @Override
            public void onWindowLongPressEnd(int index) {
                super.onWindowLongPressEnd(index);
                LogUtil.debugLog(TAG, "onWindowLongPressEnd: index= " + index);
            }

            /**
             * Play event callback
             * resultSource:  0--RTSP  1--HLS  5--LCHTTP  99--OPENAPI
             *
             * 播放事件回调
             * resultSource:  0--RTSP  1--HLS  5--LCHTTP  99--OPENAPI
             */
            @Override
            public void onPlayerResult(int index, String code, int resultSource) {
                super.onPlayerResult(index, code, resultSource);
                LogUtil.debugLog(TAG, "onPlayerResult: index= " + index + " , code= " + code + " , resultSource= " + resultSource);
                boolean failed = false;

                if (resultSource == 99) {
                    //code  -1000 HTTP交互出错或超时
                    failed = true;
                } else if (resultSource == 1) {
                    //云录像
                    if (code.equals("0") || code.equals("4") || code.equals("7") || code.equals("11") || code.equals("14")) {
                        failed = true;
                    }
                    if (code.equals("11")) {
                        showInputKey();
                    }
                    if (code.equals("14")) {
                        passcode = 1;
                        showInputKey();
                    }
                } else if (resultSource == 0) {
                    //设备录像
                    if (code.equals("0") || code.equals("1") || code.equals("3") || code.equals("7")) {
                        failed = true;
                    }
                    if (code.equals("7")) {
                        showInputKey();
                    }
                } else if (resultSource == 5) {
                    //设备录像
                    if (!(code.equals("1000") || code.equals("0") || code.equals("2000") || code.equals("4000"))) {
                        failed = true;
                    }
                    if (code.equals("1000005")) {
                        showInputKey();
                    }
                }
                if (failed && playStatus != PlayStatus.STOP) {
                    loadingStatus(LoadStatus.LOAD_ERROR, getResources().getString(R.string.lc_demo_device_video_play_error) + ":" + code + "." + resultSource, "");
                }
            }

            //分辨率改变事件
            @Override
            public void onResolutionChanged(int index, int width, int height) {
                super.onResolutionChanged(index, width, height);
                LogUtil.debugLog(TAG, "onResolutionChanged: index= " + index + " , width= " + width + " , height= " + height);
            }

            //播放开始回调
            @Override
            public void onPlayBegan(int index) {
                super.onPlayBegan(index);
                LogUtil.debugLog(TAG, "onPlayBegan: index= " + index);
                loadingStatus(LoadStatus.LOAD_SUCCESS, "", "");
            }

            //接收数据回调
            @Override
            public void onReceiveData(int index, int len) {
                super.onReceiveData(index, len);
                // LogUtil.debugLog(TAG, "onReceiveData: index= " + index + " , len= " + len);
            }

            //接收帧流回调
            @Override
            public void onStreamCallback(int index, byte[] bytes, int len) {
                super.onStreamCallback(index, bytes, len);
                LogUtil.debugLog(TAG, "onStreamCallback: index= " + index + " , len= " + len);
            }

            //播放结束事件
            @Override
            public void onPlayFinished(int index) {
                super.onPlayFinished(index);
                LogUtil.debugLog(TAG, "onPlayFinished: index= " + index);
                runOnUiThread(() -> {
                    //一段录像播放结束，如果还在录制
                    if (recordStatus == RecordStatus.START) {
                        ivVideo.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_video));
                        if (stopRecord()) {
                            //Looper.prepare();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.lc_demo_device_record_stop), Toast.LENGTH_SHORT).show();
                            //Looper.loop();
                            recordStatus = RecordStatus.STOP;
                            LogUtil.debugLog(TAG, "videopath:::" + videoPath);
                            MediaPlaybackHelper.updatePhotoVideo(videoPath);
                        } else {
                            return;
                        }
                    }
//                        stop();
                    playStatus = PlayStatus.STOP;
                    featuresClickListener(false);
                    mPlayWin.setPlaySpeed(speed[speedPosition]);
                    recordStartTime.setText(endTime);//解决倍速播放时间有误差,同步起始时间
                    ivPlayStyle.setImageDrawable(speedImage[speedPosition]);
                    llPlayPause.setOnClickListener(DeviceRecordPlayActivity.this);
                    ivPalyPause.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_play));
                });
            }

            //播放时间信息回调
            @Override
            public void onPlayerTime(int index, final long current) {
                super.onPlayerTime(index, current);
                long startTime = DateHelper.parseMills(recordsData.beginTime) / 1000;
                LogUtil.debugLog(TAG, "onPlayerTime: index= " + index + " , time= " + current + "startTime= " + startTime);
                DeviceRecordPlayActivity.this.progress = (int) (current - startTime);
                LogUtil.debugLog(TAG, "onPlayerTime: progress= " + DeviceRecordPlayActivity.this.progress);
                runOnUiThread(() -> {
                    recordSeekbar.setProgress(DeviceRecordPlayActivity.this.progress);
                    DeviceRecordPlayActivity.this.beginTime = DateHelper.getTimeHMS(current * 1000);
                    recordStartTime.setText(DeviceRecordPlayActivity.this.beginTime);
                });
            }
        });
    }

    private void showInputKey() {
        runOnUiThread(() -> {
            if (encryptKeyInputDialog == null) {
                encryptKeyInputDialog = new EncryptKeyInputDialog(DeviceRecordPlayActivity.this);
            }
            encryptKeyInputDialog.show();
            if (1 == passcode) {
                encryptKeyInputDialog.setText(getText(R.string.lc_demo_device_encrypt_device_password_tip).toString());
            } else {
                encryptKeyInputDialog.setText(getText(R.string.lc_demo_device_encrypt_key_tip).toString());
            }
            encryptKeyInputDialog.setOnClick(txt -> {
                encryptKey = txt;
                loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), txt);
            });
        });
    }

    private void showInputKeyEx() {
        runOnUiThread(() -> {
            if (encryptKeyInputDialog == null) {
                encryptKeyInputDialog = new EncryptKeyInputDialog(DeviceRecordPlayActivity.this);
            }
            encryptKeyInputDialog.show();
            if (1 == passcode) {
                encryptKeyInputDialog.setText(getText(R.string.lc_demo_device_encrypt_device_password_tip).toString());
            } else {
                encryptKeyInputDialog.setText(getText(R.string.lc_demo_device_encrypt_key_tip).toString());
            }
            encryptKeyInputDialog.setOnClick(new EncryptKeyInputDialog.OnClick() {
                @Override
                public void onSure(String txt) {
                    encryptKey = txt;
                    startDownload(txt);
//                    path = MediaPlaybackHelper.getDownloadVideoPath(0, recordsData.recordId, DateHelper.parseMills(recordsData.beginTime));
//                    LCOpenSDK_Download.startCloudDownload(0,
//                            path,
//                            TokenHelper.getInstance().subAccessToken,
//                            recordsData.recordRegionId,
//                            recordsData.deviceId,
//                            String.valueOf(0),
//                            txt,
//                            1000,
//                            5000,
//                            recordsData.productId);
                }
            });
        });
    }

    /**
     * Play status
     * @param loadStatus Load Status
     * @param msg  message
     *
     * 播放状态
     * @param loadStatus 播放状态
     * @param msg  消息
     */
    private void loadingStatus(final LoadStatus loadStatus, final String msg, final String psk) {
        runOnUiThread(() -> {
            if (loadStatus == LoadStatus.LOADING) {
                //先关闭
                stop();
                //开始播放
                playVideo(psk);
                rlLoading.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
                tvLoadingMsg.setText(msg);
                //禁止拖动
                setCanSeekChanged(false);
            } else if (loadStatus == LoadStatus.LOAD_SUCCESS) {
                if (LCUtils.isDebug(DeviceRecordPlayActivity.this)) {
                    showStreamTypeCover();
                }
                //播放成功
                rlLoading.setVisibility(View.GONE);
                //允许拖动
                setCanSeekChanged(true);
                //SeekBar监听
                setSeekBarListener();
                playStatus = PlayStatus.PLAY;
                //mPlayWin.setSEnhanceMode(4);//设置降噪等级最大
                featuresClickListener(true);
//                if (recordType == MethodConst.ParamConst.recordTypeLocal) {
                    //解决本地录像在视频播放结束后设置播放速度不生效
                    mPlayWin.setPlaySpeed(speed[speedPosition]);
//                }
            } else {
                //播放失败
                stop();
                rlLoading.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
                tvLoadingMsg.setText(msg);
                //禁止拖动
                setCanSeekChanged(false);
                playStatus = PlayStatus.ERROR;
                featuresClickListener(false);
            }
        });
    }


    public void showStreamTypeCover() {
        recordStream.setVisibility(View.VISIBLE);
        if (mPlayWin.isP2pTag()) {
            recordStream.setText("P2P");
        } else {
            recordStream.setText("MTS");
        }
    }

    private void startDownload(String psk) {
        path = MediaPlaybackHelper.getDownloadVideoPath(0, recordsData.recordId, DateHelper.parseMills(recordsData.beginTime));
        if (recordsData.recordType == 1) {
            LCOpenSDK_Download.setListener(new DeviceDownloadListener());
            LCOpenSDK_Download.startDeviceDownload(0,
                    TokenHelper.getInstance().subAccessToken,
                    deviceListBean.playToken,
                    recordsData.deviceId,
                    path,
                    recordsData.recordId,
                    psk,
                    0,
                    1,
                    2,
                    recordsData.productId
            );
        } else {
            LCOpenSDK_Download.setListener(new CloudDownloadListener());
            LCOpenSDK_Download.startCloudDownload(0,
                    path,
                    TokenHelper.getInstance().subAccessToken,
                    recordsData.recordRegionId,
                    recordsData.deviceId,
                    String.valueOf(0),
                    psk,
                    1000,
                    5000,
                    recordsData.productId);
        }
    }

    private void stopDownLoad() {
        LCOpenSDK_Download.stopDownload(0);
        //更新數據到相冊
        MediaPlaybackHelper.updatePhotoVideo(path);
    }

    private int downloadProgress;

    private class DeviceDownloadListener extends LCOpenSDK_DownloadListener {
        @Override
        public void onDownloadReceiveData(int index, final int dataLen) {
            LogUtil.debugLog(TAG, "DeviceDownloadListener----" + "index=" + index + ", dataLen=" + dataLen);
            if (downloadStatus == DownloadStatus.ING) {
                downloadProgress = downloadProgress + dataLen;
                runOnUiThread(() -> pgDownloadProgress(
                        getResources().getString(R.string.lc_demo_device_record_download_begin) + byte2mb(downloadProgress) + "MB/" + totalMb + "M",
                        (int) ((float) downloadProgress / (float) Long.parseLong(recordsData.size) * 100),
                        100,
                        getResources().getColor(R.color.lc_demo_color_ffffff)));
            }
        }

        @Override
        public void onDownloadState(int index, String code, int type) {
            LogUtil.debugLog(TAG, "DeviceDownloadListener----" + "index=" + index + ", code=" + code + ", type=" + type);
            if (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_REST

                    || (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_LCHTTP
                    && (code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_COMPONENT_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_BAD_REQUEST)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_UNAUTHORIZED)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_FORBIDDEN)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_NOTFOUND)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_REQ_TIMEOUT)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_SERVER_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_SERVER_UNVALILABLE)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_HTTPLC_SERVICE_DISCONNECT)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_FLOWLIMIT)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_P2P_MAXCONNECT)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_GATEWAY_TIMEOUT)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_CLIENT_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_KEY_ERROR)))

                    || (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_RTSP
                    && (code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_PACKET_FRAME_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_TEARDOWN_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_AUTHORIZATION_FAIL)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_COMPONENT_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_SERVICE_UNAVAILABLE)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_KEY_MISMATCH)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_LIVE_PLAY_OVER)
                    || code.equals(LCOpenSDK_StatusCode.RTSPCode.RESULT_STREAM_LIMIT_NOTIFY)))) {
                //下载出错
                downloadStatus = DownloadStatus.ERROR;
                runOnUiThread(() -> {
                    pgDownloadProgress(getResources().getString(R.string.lc_demo_device_record_download_error),
                            0,
                            0,
                            getResources().getColor(R.color.c12));
                    downloadProgress = 0;
                });
            }
            // RTSP下载鉴权失败，要提示输入设备密码
            if (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_RTSP && code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_AUTHORIZATION_FAIL)) {
                passcode = 1;
            }
            if ((type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_RTSP && code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_KEY_MISMATCH))
                    || type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_LCHTTP && code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_HTTPLC_STREAM_MODIFY_ERROR)) {
                //下载出错
                downloadStatus = DownloadStatus.ERROR;
                showInputKeyEx();
            }
            if ((type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_RTSP && code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_PLAY_READY))
                    || type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_LCHTTP && code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_OK)) {
                //开始下载
                downloadStatus = DownloadStatus.ING;
            }

            if ((type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_RTSP && code.equals(LCOpenSDK_StatusCode.RTSPCode.STATE_RTSP_FILE_PLAY_OVER))
                    || type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_LCHTTP && code.equals(LCOpenSDK_StatusCode.LCHTTPCode.STATE_LCHTTP_PLAY_FILE_OVER)) {
                LCOpenSDK_Download.stopDownload(0);
                //下载完成
                downloadStatus = DownloadStatus.FINISH;
                // MediaScannerConnection.scanFile(DeviceRecordPlayActivity.this, new String[]{path}, null, null);
                MediaPlaybackHelper.updatePhotoVideo(path);
                runOnUiThread(() -> {
                    pgDownloadProgress(getResources().getString(R.string.lc_demo_device_record_download_finish),
                            100,
                            100,
                            getResources().getColor(R.color.lc_demo_color_ffffff));
                    downloadProgress = 0;
                });
            }
        }
    }

    private class CloudDownloadListener extends LCOpenSDK_DownloadListener {
        @Override
        public void onDownloadReceiveData(int index, final int dataLen) {
            LogUtil.debugLog(TAG, "CloudDownloadListener----" + "index=" + index + ", dataLen=" + dataLen);
            if (downloadStatus == DownloadStatus.ING) {
                downloadProgress = downloadProgress + dataLen;
                runOnUiThread(() -> pgDownloadProgress(
                        getResources().getString(R.string.lc_demo_device_record_download_begin) + byte2mb(downloadProgress) + "MB/" + totalMb + "M",
                        (int) ((float) downloadProgress / (float) Long.parseLong(recordsData.size) * 100),
                        100,
                        getResources().getColor(R.color.lc_demo_color_ffffff)));
            }
        }

        @Override
        public void onDownloadState(int index, String code, int type) {
            LogUtil.debugLog(TAG, "CloudDownloadListener----" + "index=" + index + ", code=" + code + ", type=" + type);
            if (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_REST
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DOWNLOAD_FAILD)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_SEEK_FAILD)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DOWNLOAD_TIMEOUT)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_PASSWORD_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_EXTRACT_FAILED)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_ENCRYPT_KEY_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DEVICE_PASSWORD_MISMATCH)) {
                //下载出错
                downloadStatus = DownloadStatus.ERROR;
                runOnUiThread(() -> {
                    pgDownloadProgress(getResources().getString(R.string.lc_demo_device_record_download_error),
                            0,
                            0,
                            getResources().getColor(R.color.c12));
                    downloadProgress = 0;
                });
            }
            if (code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DEVICE_PASSWORD_MISMATCH)) {
                passcode = 1;
            }
            if (code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_PASSWORD_ERROR)
                    || code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DEVICE_PASSWORD_MISMATCH)) {
                //下载出错
                downloadStatus = DownloadStatus.ERROR;
                showInputKeyEx();
            }
            if (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_HLS
                    && code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DOWNLOAD_BEGIN)) {
                //开始下载
                downloadStatus = DownloadStatus.ING;
            }

            if (type == LCOpenSDK_StatusCode.Protocol.RESULT_PROTO_TYPE_HLS
                    && code.equals(LCOpenSDK_StatusCode.HLSCode.HLS_DOWNLOAD_END)) {
                //下载完成
                downloadStatus = DownloadStatus.FINISH;
                // MediaScannerConnection.scanFile(DeviceRecordPlayActivity.this, new String[]{path}, null, null);
                MediaPlaybackHelper.updatePhotoVideo(path);
                runOnUiThread(() -> {
                    pgDownloadProgress(getResources().getString(R.string.lc_demo_device_record_download_finish),
                            100,
                            100,
                            getResources().getColor(R.color.lc_demo_color_ffffff));
                    downloadProgress = 0;
                });
            }
        }
    }

    private void pgDownloadProgress(String tip, int progress, int secondProgress, int textColor) {
        pgDownload.setText(tip);
        pgDownload.setProgress(progress);
        pgDownload.setSecondaryProgress(secondProgress);
        pgDownload.setTextColor(textColor);
    }

    @Override
    public void deleteDeviceRecord() {
        DialogUtils.dismiss();
        Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_delete_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("data", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onError(Throwable throwable) {
        DialogUtils.dismiss();
        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
            Toast.makeText(this, R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String byte2mb(long b) {
        double mb = (double) b / 1024 / 1024;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(mb);
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }
}
