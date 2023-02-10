package com.lc.playback.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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

import com.lc.playback.R;
import com.lc.playback.contacts.MethodConst;
import com.lc.playback.utils.MediaPlaybackHelper;
import com.lechange.opensdk.media.local.LCOpenSDK_PlayLocalWindow;
import com.lechange.opensdk.media.local.listener.LCOpenSDK_PlayLocalListener;
import com.mm.android.mobilecommon.widget.RecoderSeekBar;


public class PlayLocalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PlayLocalActivity.class.getSimpleName();
    private FrameLayout frLiveWindow, frLiveWindowContent;
    private TextView tvDeviceName, tvLoadingMsg, recordStartTime, recordEndTime, recordStream;
    private LinearLayout llFullScreen, llSound, llPlayPause, llBack;
    private ImageView ivPalyPause, ivSound, ivScreenShot;
    private ProgressBar pbLoading;
    private RelativeLayout rlLoading;
    private LCOpenSDK_PlayLocalWindow mPlayWin = new LCOpenSDK_PlayLocalWindow();
    private String filePath;
    private RecoderSeekBar recordSeekbar;
    private int progress;
    private String beginTime;

    private SoundStatus soundStatus = SoundStatus.PLAY;
    private PlayStatus playStatus = PlayStatus.PAUSE;

    private ImageView ivChangeScreen;
    private LinearLayout llOperate;
    private RelativeLayout rlTitle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
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
            MediaPlaybackHelper.quitFullScreen(PlayLocalActivity.this);
            llOperate.setVisibility(View.VISIBLE);
            rlTitle.setVisibility(View.VISIBLE);
        } else {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(metric.widthPixels, metric.heightPixels);
            mLayoutParams.setMargins(0, 0, 0, 0);
            frLiveWindow.setLayoutParams(mLayoutParams);
            MediaPlaybackHelper.setFullScreen(PlayLocalActivity.this);
            llOperate.setVisibility(View.GONE);
            rlTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        if (getIntent().getExtras() == null) {
            return;
        }
        filePath = getIntent().getExtras().getString(MethodConst.ParamConst.filePath, "");
        if (TextUtils.isEmpty(filePath)) {
            onBackPressed();
            return;
        }
        try {
            tvDeviceName.setText(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length() - 4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化时间
        initSeekBarAndTime();
        //初始化控件
        initCommonClickListener();
        //播放视频
        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), filePath);
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

    private void playVideo(String filePath) {
        mPlayWin.playFile(filePath);
    }

    private void stop() {
        stopPlayWindow();
//        //禁止拖动
//        setCanSeekChanged(false);
    }

    /**
     * Turn off Play separately,Note: The component requires that the main thread call be required
     * <p>
     * 单独关闭播放  注意：组件要求必须要主线程调用
     */
    private void stopPlayWindow() {
        closeAudio();// 关闭音频
//        if (recordType == MethodConst.ParamConst.recordTypeCloud) {
        mPlayWin.stopFilePlay(true);
//        } else {
//            mPlayWin.stopRtspPlayback(true);// 关闭视频
//        }
    }

    /**
     * Sets whether the drag progress bar can be used
     * <p>
     * 设置拖动进度条是否能使用
     */
    public void setCanSeekChanged(boolean canSeek) {
        recordSeekbar.setCanTouchAble(canSeek);
    }

    /**
     * Start record
     * <p>
     * 开始录像
     */
    public boolean startRecord() {
        // 录像的路径
        String channelName = null;
//        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
//            channelName = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName;
//        } else {
//            channelName = deviceListBean.deviceName;
//        }
        // 去除通道中在目录中的非法字符
        channelName = channelName.replace("-", "");
        videoPath = MediaPlaybackHelper.getCaptureAndVideoPath(MediaPlaybackHelper.LCFilesType.LCVideo, channelName);
//        MediaScannerConnection.scanFile(this, new String[]{videoPath}, null, null);
        // 开始录制 1
//        int ret = mPlayWin.startRecord(videoPath, 1, 0x7FFFFFFF);
        return mPlayWin.startRecord(videoPath, 1, 0x7FFFFFFF);
    }


    /**
     * Stop record
     * <p>
     * 关闭录像
     */
    public boolean stopRecord() {
        return mPlayWin.stopRecord();
    }

    /**
     * Screenshot
     * <p>
     * 截图
     */
    public String capture() {
        String captureFilePath = MediaPlaybackHelper.getCaptureAndVideoPath(MediaPlaybackHelper.LCFilesType.LCImage, "local_capture");
        boolean ret = mPlayWin.snapShot(captureFilePath);
        if (ret) {
            // 扫描到相册中
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
        llPlayPause = findViewById(R.id.ll_paly_pause);
        llSound = findViewById(R.id.ll_sound);
        llFullScreen = findViewById(R.id.ll_fullscreen);
        ivPalyPause = findViewById(R.id.iv_paly_pause);
        ivSound = findViewById(R.id.iv_sound);
        rlLoading = findViewById(R.id.rl_loading);
        pbLoading = findViewById(R.id.pb_loading);
        tvLoadingMsg = findViewById(R.id.tv_loading_msg);
        recordStartTime = findViewById(R.id.record_startTime);
        recordSeekbar = findViewById(R.id.record_seekbar);
        recordEndTime = findViewById(R.id.record_endTime);
        ivScreenShot = findViewById(R.id.iv_screen_shot);
        ivChangeScreen = findViewById(R.id.iv_change_screen);
        llOperate = findViewById(R.id.ll_operate);
        rlTitle = findViewById(R.id.rl_title);
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
    }

    private void initCommonClickListener() {
        llBack.setOnClickListener(this);
        llFullScreen.setOnClickListener(this);
    }

    private void featuresClickListener(boolean loadSuccess) {
        llPlayPause.setOnClickListener(loadSuccess ? this : null);
        llSound.setOnClickListener(loadSuccess ? this : null);
        ivScreenShot.setOnClickListener(loadSuccess ? this : null);
        ivPalyPause.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_pause_disable));
        ivSound.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off) : getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_off_disable));
        ivScreenShot.setImageDrawable(loadSuccess ? getDrawable(R.drawable.lc_demo_photo_capture_selector) : getDrawable(R.mipmap.lc_demo_livepreview_icon_screenshot_disable));
        //媒体声
        if (soundStatus != SoundStatus.PLAY) {
            return;
        }
        if (loadSuccess && openAudio()) {
            soundStatus = SoundStatus.PLAY;
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_back_video_icon_h_sound_on));
        }
    }

    private void initSeekBarAndTime() {
//        String startTime = recordsData.beginTime.substring(11);
//        endTime = recordsData.endTime.substring(11);
//        recordSeekbar.setMax((int) ((DateHelper.parseMills(recordsData.endTime) - DateHelper.parseMills(recordsData.beginTime)) / 1000));
//        recordSeekbar.setProgress(0);
//        recordStartTime.setText(startTime);
//        recordEndTime.setText(endTime);
    }


    private void setSeekBarListener() {
        recordSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (recordSeekbar.getMax() - PlayLocalActivity.this.progress <= 2) {
                    seek(recordSeekbar.getMax() >= 2 ? recordSeekbar.getMax() - 2 : 0);
                } else {
                    seek(PlayLocalActivity.this.progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {
                if (byUser) {
                    PlayLocalActivity.this.progress = progress;
                }
            }
        });
    }

    /**
     * Continue playing (asynchronous)
     * <p>
     * 继续播放(异步)
     */
    public void resumeAsync() {
        mPlayWin.resumeAsync();
    }

    /**
     * Pause play (asynchronous)
     * <p>
     * 暂停播放(异步)
     */
    public void pauseAsync() {
        mPlayWin.pauseAsync();
    }

    public boolean openAudio() {
        return mPlayWin.playAudio();
    }

    public boolean closeAudio() {
        return mPlayWin.stopAudio();
    }

    public void seek(int index) {
//        long seekTime = DateHelper.parseMills(recordsData.beginTime) / 1000 + index;
        //先暂存时间记录
//        beginTime = DateHelper.getTimeHMS(seekTime * 1000);
        this.progress = index;
        recordSeekbar.setProgress(index);
        recordStartTime.setText(this.beginTime);
        Log.i(TAG, "seeking----" + index);
//        if (index == 0) {//拖动到初始位置，重新播放
        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), filePath);
//        } else {
//            mPlayWin.seek(index);
//        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_back) {
            finish();
        } else if (id == R.id.ll_fullscreen) {
            //横竖屏切换
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            ivChangeScreen.setImageDrawable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? getResources().getDrawable(R.mipmap.live_btn_smallscreen) : getResources().getDrawable(R.mipmap.icon_hengping));
        } else if (id == R.id.ll_paly_pause) {
            //播放暂停 重新播放
            if (playStatus == PlayStatus.PLAY) {
                pauseAsync();
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
                loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), filePath);
            }
        } else if (id == R.id.ll_sound) {
            //媒体声 如果是开启去关闭，反之
            if (soundStatus == SoundStatus.NO_SUPPORT) {
                return;
            }
            boolean result;
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
        }
    }

    private void setWindowListener(LCOpenSDK_PlayLocalWindow playWin) {
        playWin.setPlayLocalListener(new LCOpenSDK_PlayLocalListener() {
            @Override
            public void onPlayBegin(int winID, String context) {
                super.onPlayBegin(winID, context);
                loadingStatus(LoadStatus.LOAD_SUCCESS, "", "");
            }

            @Override
            public void onPlayFail(int winID, String errorCode, int type) {
                super.onPlayFail(winID, errorCode, type);
                loadingStatus(LoadStatus.LOAD_ERROR, getResources().getString(R.string.lc_demo_device_video_play_error) + ":" + errorCode + "." + type, "");
            }

            @Override
            public void onPlayLoading(int winID) {
                super.onPlayLoading(winID);
            }
        });
    }


    /**
     * Play status
     * @param loadStatus Load Status
     * @param msg        message
     * <p>
     * 播放状态
     * @param loadStatus 播放状态
     * @param msg        消息
     */
    private void loadingStatus(final LoadStatus loadStatus, final String msg, final String filePath) {
        runOnUiThread(() -> {
            if (loadStatus == LoadStatus.LOADING) {
                //先关闭
                stop();
                //开始播放
                playVideo(filePath);
                rlLoading.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
                tvLoadingMsg.setText(msg);
                //禁止拖动
                setCanSeekChanged(false);
            } else if (loadStatus == LoadStatus.LOAD_SUCCESS) {
                //播放成功
                rlLoading.setVisibility(View.GONE);
                //允许拖动
                setCanSeekChanged(true);
                //SeekBar监听
                setSeekBarListener();
                playStatus = PlayStatus.PLAY;
                //mPlayWin.setSEnhanceMode(4);//设置降噪等级最大
                featuresClickListener(true);
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

}
