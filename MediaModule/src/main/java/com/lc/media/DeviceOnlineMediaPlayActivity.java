package com.lc.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lc.media.adapter.MediaPlayRecordAdapter;
import com.lc.media.annotation.Direction;
import com.lc.media.api.ControlMovePTZData;
import com.lc.media.api.DeviceLocalCacheService;
import com.lc.media.api.DeviceRecordService;
import com.lc.media.api.IGetDeviceInfoCallBack;
import com.lc.media.contacts.MethodConst;
import com.lc.media.entity.CloudRecordsData;
import com.lc.media.entity.DeviceDetailListData;
import com.lc.media.entity.DeviceLocalCacheData;
import com.lc.media.entity.LocalRecordsData;
import com.lc.media.entity.RecordsData;
import com.lc.media.handler.ActivityHandler;
import com.lc.media.ui.EncryptKeyInputDialog;
import com.lc.media.ui.LcCloudRudderView;
import com.lc.media.ui.LcPopupWindow;
import com.lc.media.utils.DateHelper;
import com.lc.media.utils.DeviceAbilityHelper;
import com.lc.media.utils.MediaPlayHelper;
import com.lechange.common.log.Logger;
import com.lechange.opensdk.listener.LCOpenSDK_EventListener;
import com.lechange.opensdk.listener.LCOpenSDK_TalkerListener;
import com.lechange.opensdk.media.LCOpenSDK_ParamReal;
import com.lechange.opensdk.media.LCOpenSDK_ParamTalk;
import com.lechange.opensdk.media.LCOpenSDK_PlayWindow;
import com.lechange.opensdk.media.LCOpenSDK_Talk;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.route.ProviderManager;
import com.mm.android.mobilecommon.utils.LCUtils;
import com.mm.android.mobilecommon.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeviceOnlineMediaPlayActivity extends AppCompatActivity implements View.OnClickListener, IGetDeviceInfoCallBack.IDeviceCacheCallBack {
    private static final String TAG = DeviceOnlineMediaPlayActivity.class.getSimpleName();

    public LCOpenSDK_PlayWindow mPlayWin = new LCOpenSDK_PlayWindow();
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    private Bundle bundle;
    private FrameLayout frLiveWindow, frLiveWindowContent;
    private TextView tvNoVideo, tvDeviceName, tvCloudVideo, tvLocalVideo, tvLoadingMsg, tvPixel;
    private RecyclerView rcvVideoList;
    private LinearLayout llVideoContent, llVideo, llSpeak, llScreenShot, llCloudStage, llFullScreen, llSound, llPlayStyle, llPlayPause, llDetail, llBack, llVideo1, llSpeak1, llScreenShot1, llCloudStage1;
    private ImageView ivPalyPause, ivPlayStyle, ivSound, ivCloudStage, ivScreenShot, ivSpeak, ivVideo, ivCloudStage1, ivScreenShot1, ivSpeak1, ivVideo1;
    private ProgressBar pbLoading;
    private RelativeLayout rlLoading;
    private LcCloudRudderView rudderView;
    private boolean cloudvideo = true;
    private boolean isShwoRudder = false;
    private boolean showHD = true;//显示HD和SD的切换
    private AudioTalkerListener audioTalkerListener = new AudioTalkerListener();

    private VideoMode videoMode = VideoMode.MODE_HD;
    private SoundStatus soundStatus = SoundStatus.PLAY;
    private SoundStatus soundStatusPre = SoundStatus.PLAY;
    private SpeakStatus speakStatus = SpeakStatus.STOP;
    private RecordStatus recordStatus = RecordStatus.STOP;
    private PlayStatus playStatus = PlayStatus.ERROR;
    private LinearLayoutManager linearLayoutManager;
    private MediaPlayRecordAdapter mediaPlayRecordAdapter;
    private List<RecordsData> recordsDataList = new ArrayList<>();
    private List<RecordsData> cloudRecordsDataList;
    private List<RecordsData> localRecordsDataList;
    private String cloudRecordsDataTip = "";
    private String localRecordsDataTip = "";
    private DeviceRecordService deviceRecordService = DeviceRecordService.newInstance();
    private Direction mPTZPreDirection = null;
//    private int mCurrentOrientation;
    private LinearLayout llController;
    private FrameLayout frRecord;
    private RelativeLayout rlTitle;
    private ImageView ivChangeScreen;
    private EncryptKeyInputDialog encryptKeyInputDialog;
    private String encryptKey;
    private boolean supportPTZ;
    private String videoPath = null;
    // 屏幕方向改变监听器
    private OrientationEventListener mOrientationEventListener;

    private int mVideoViewCurrentOrientationRequest = NO_ORIENTATION_REQUEST;
    private LcPopupWindow lcPopupWindow;
    private int imageSize = -1;//视频播放分辨率
    private int bateMode;
    private TextView tvCoverStream; //debug模式显示拉流方式
    private ImageView ivLimitLeft;
    private ImageView ivLimitRight;
    private ImageView ivLimitUp;
    private ImageView ivLimitDown;
    private long startVideoClickTime;
    private long endVideoClickTime;


    public enum PlayStatus {
        PLAY, PAUSE, ERROR
    }

    public enum LoadStatus {
        LOADING, LOAD_SUCCESS, LOAD_ERROR
    }

    public enum SoundStatus {
        PLAY, STOP, NO_SUPPORT
    }

    public enum SpeakStatus {
        PLAY, STOP, NO_SUPPORT, OPENING
    }

    public enum VideoMode {
        MODE_HD, MODE_SD
    }

    public enum RecordStatus {
        START, STOP
    }

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mHandler = new ActivityHandler(this) {
            @Override
            public void handleMsg(Message msg) {
                switch (msg.what) {
                    case MSG_REQUEST_ORIENTATION:
                        requestedOrientation(msg.arg1, false);
                        break;
                    case 11:
                        ivLimitLeft.setVisibility(View.VISIBLE);
                        ivLimitRight.setVisibility(View.GONE);
                        ivLimitDown.setVisibility(View.GONE);
                        ivLimitUp.setVisibility(View.GONE);
                        break;
                    case 22:
                        ivLimitLeft.setVisibility(View.GONE);
                        ivLimitRight.setVisibility(View.VISIBLE);
                        ivLimitDown.setVisibility(View.GONE);
                        ivLimitUp.setVisibility(View.GONE);
                        break;
                    case 33:
                        ivLimitLeft.setVisibility(View.GONE);
                        ivLimitRight.setVisibility(View.GONE);
                        ivLimitDown.setVisibility(View.VISIBLE);
                        ivLimitUp.setVisibility(View.GONE);
                        break;
                    case 44:
                        ivLimitLeft.setVisibility(View.GONE);
                        ivLimitRight.setVisibility(View.GONE);
                        ivLimitDown.setVisibility(View.GONE);
                        ivLimitUp.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                        ivLimitLeft.setVisibility(View.GONE);
                        ivLimitRight.setVisibility(View.GONE);
                        ivLimitDown.setVisibility(View.GONE);
                        ivLimitUp.setVisibility(View.GONE);
                    default:
                        break;
                }
                // handlePlayMessage(msg);
            }

        };
        super.onCreate(savedInstanceState);
        initOrientationEventListener();
        setContentView(R.layout.activity_device_online_media_play);
        initView();
        initData();
        operatePTZ();
    }

    protected void requestedOrientation(int requestedOrientation, boolean isForce) {
        if (!isForce) {
            if (mVideoViewCurrentOrientationRequest == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                /*
                 * // 如果是开启全屏开关，禁止竖屏 if (requestedOrientation ==
                 * ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || requestedOrientation ==
                 * ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) { return; }
                 */
                mVideoViewCurrentOrientationRequest = NO_ORIENTATION_REQUEST;
            } else {
                if (mVideoViewCurrentOrientationRequest == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        // 如果监听器发出的请求是横屏，表示屏幕处于横屏，直接返回；
                        mVideoViewCurrentOrientationRequest = NO_ORIENTATION_REQUEST;
                        return;
                    }
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        // 如果监听器发出的请求和当前开关方向保持一致时，表示屏幕已经恢复到制定的竖屏，将开关请求状态重置；
                        mVideoViewCurrentOrientationRequest = NO_ORIENTATION_REQUEST;
                        return;
                    }
                    // 如果监听器发出的请求与当前开关方向不一致时，表示屏幕已经转到其他方向，将开关请求状态重置，并发出相应的请求；
                    mVideoViewCurrentOrientationRequest = NO_ORIENTATION_REQUEST;
                }
                // 如果开关没有发出请求，或者开关请求状态已经重置，则直接发出相应请求；
            }
        } else {
            mVideoViewCurrentOrientationRequest = requestedOrientation;
        }

        try {
            setRequestedOrientation(requestedOrientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switchScreenDirection();
    }

    private void operatePTZ() {
        rudderView.setRudderListener(new LcCloudRudderView.RudderListener() {
            @Override
            public void onSteeringWheelChangedBegin() {

            }

            @Override
            public void onSteeringWheelChangedContinue(Direction direction) {
                if (direction == null && mPTZPreDirection != null) {
                    controlPTZ(mPTZPreDirection, 200, true);
                    mPTZPreDirection = null;
                } else if (direction != mPTZPreDirection) {
                    // 方向不同才更新云台指示图标
                    mPTZPreDirection = direction;
                    controlPTZ(mPTZPreDirection, 30000, false);
                }
            }

            @Override
            public void onSteeringWheelChangedSingle(Direction direction) {
                controlPTZ(direction, 200, false);
            }

            @Override
            public void onSteeringWheelChangedEnd() {
            }
        });
    }

    private void controlPTZ(Direction em, long time, boolean stop) {
        String operation = "";
        if (em == Direction.Left) {
            operation = "2";
        } else if (em == Direction.Right) {
            operation = "3";
        } else if (em == Direction.Up) {
            operation = "0";
        } else if (em == Direction.Down) {
            operation = "1";
        }
        if (stop) {
            operation = "10";
        }
        ControlMovePTZData controlMovePTZData = new ControlMovePTZData();
        controlMovePTZData.data.deviceId = deviceListBean.deviceId;
        controlMovePTZData.data.channelId = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId;
        controlMovePTZData.data.operation = operation;
        controlMovePTZData.data.duration = time;
        deviceRecordService.controlMovePTZ(controlMovePTZData);
    }

    private void initCloudRecord() {
        if ((cloudRecordsDataTip != null && !cloudRecordsDataTip.isEmpty()) || cloudRecordsDataList != null) {
            if (cloudRecordsDataList != null) {
                showRecordList(cloudRecordsDataList);
            } else {
                showRecordListTip(cloudRecordsDataTip);
            }
        } else {
            getCloudRecord();
           /* deviceRecordService.queryCloudUse(deviceListBean.deviceId, deviceListBean.channels.get(deviceListBean.checkedChannel).channelId, new IGetDeviceInfoCallBack.ICommon<Integer>() {

                @Override
                public void onCommonBack(Integer response) {
                    if (response==-1||response==0){
                        cloudRecordsDataTip = getResources().getString(R.string.lc_demo_device_cloud_not_open);
                        showRecordListTip(cloudRecordsDataTip);
                    }else{
                        getCloudRecord();
                    }

                }

                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(DeviceOnlineMediaPlayActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }


    private void getCloudRecord() {
        CloudRecordsData cloudRecordsData = new CloudRecordsData();
        cloudRecordsData.data.deviceId = deviceListBean.deviceId;
        cloudRecordsData.data.channelId = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId;
        cloudRecordsData.data.beginTime = DateHelper.dateFormat(new Date(System.currentTimeMillis())) + " 00:00:00";
        cloudRecordsData.data.endTime = DateHelper.dateFormat(new Date(System.currentTimeMillis())) + " 23:59:59";
        cloudRecordsData.data.count = 6;
        cloudRecordsData.data.productId = deviceListBean.productId;
        deviceRecordService.getCloudRecords(cloudRecordsData, new IGetDeviceInfoCallBack.IDeviceCloudRecordCallBack() {
            @Override
            public void deviceCloudRecord(CloudRecordsData.Response result) {
                List<CloudRecordsData.ResponseData.RecordsBean> cloudRecords = result.data.records;
                if (cloudRecords != null && cloudRecords.size() > 0) {
                    cloudRecordsDataList = new ArrayList<>();
                    for (CloudRecordsData.ResponseData.RecordsBean recordsBean : cloudRecords) {
                        RecordsData recordsData = RecordsData.parseCloudData(recordsBean);
                        cloudRecordsDataList.add(recordsData);
                    }
                    showRecordList(cloudRecordsDataList);
                } else {
                    if ("notExist".equalsIgnoreCase(deviceListBean.channelList.get(deviceListBean.checkedChannel).storageStrategyStatus)) {
                        showRecordListTip(getString(R.string.device_manager_no_cloud_storage));
                    } else {
                        showRecordListTip(getString(R.string.lc_demo_device_today_no_record));
                    }
//                    queryCloudUseState(deviceListBean.deviceId, deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                cloudRecordsDataTip = throwable.getMessage();
                showRecordListTip(cloudRecordsDataTip);
            }
        });
    }

    /**
     * Query the usage of the package
     * @param deviceId  Device Id
     * @param channelId  Channel Id
     *
     * 查询套餐使用情况
     * @param deviceId  设备Id
     * @param channelId  通道Id
     */
    private void queryCloudUseState(String deviceId, String channelId) {
        deviceRecordService.queryCloudUse(deviceId, channelId, new IGetDeviceInfoCallBack.ICommon<Integer>() {
            @Override
            public void onCommonBack(Integer response) {
                switch (response) {
                    case -1://未开通
                        cloudRecordsDataTip = getResources().getString(R.string.lc_demo_device_cloud_state_not_opened);
                        break;
                    case 0://过期
                        cloudRecordsDataTip = getResources().getString(R.string.lc_demo_device_cloud_state_expired);
                        break;
                    case 1://使用中
                        cloudRecordsDataTip = getResources().getString(R.string.lc_demo_device_more_video);
                        break;
                    case 2://暂停
                        cloudRecordsDataTip = getResources().getString(R.string.lc_demo_device_cloud_state_suspended);
                        break;
                }
                showRecordListTip(cloudRecordsDataTip);
            }

            @Override
            public void onError(Throwable throwable) {
                cloudRecordsDataTip = throwable.getMessage();
                showRecordListTip(cloudRecordsDataTip);
            }
        });
    }

    private void showRecordList(List<RecordsData> list) {
        rcvVideoList.setVisibility(View.VISIBLE);
        tvNoVideo.setVisibility(View.GONE);
        recordsDataList.clear();
        for (RecordsData a : list) {
            recordsDataList.add(a);
        }
        if (mediaPlayRecordAdapter == null) {
            mediaPlayRecordAdapter = new MediaPlayRecordAdapter(recordsDataList, DeviceOnlineMediaPlayActivity.this);
            rcvVideoList.setAdapter(mediaPlayRecordAdapter);
        } else {
            mediaPlayRecordAdapter.notifyDataSetChanged();
        }
        //加载更多
        mediaPlayRecordAdapter.setLoadMoreClickListener(new MediaPlayRecordAdapter.LoadMoreClickListener() {
            @Override
            public void loadMore() {
                gotoRecordList();
            }
        });
        //进入视频片段播放页
        mediaPlayRecordAdapter.setOnItemClickListener(new MediaPlayRecordAdapter.OnItemClickListener() {
            @Override
            public void click(int recordType, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(MethodConst.ParamConst.deviceDetail, deviceListBean);
//                bundle.putSerializable(MethodConst.ParamConst.recordData, recordsDataList.get(position));
//                bundle.putInt(MethodConst.ParamConst.recordType, recordsDataList.get(position).recordType == 0 ? MethodConst.ParamConst.recordTypeCloud : MethodConst.ParamConst.recordTypeLocal);
//                Intent intent = new Intent(DeviceOnlineMediaPlayActivityNew.this, DeviceRecordPlayActivityNew.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
                Gson gson = new Gson();
                ProviderManager.getPlaybackProvider().gotoPlayback(gson.toJson(deviceListBean),
                        gson.toJson(recordsDataList.get(position)),
                        recordsDataList.get(position).recordType == 0 ? MethodConst.ParamConst.recordTypeCloud : MethodConst.ParamConst.recordTypeLocal);
            }
        });
    }

    private void gotoRecordList() {
        if (cloudvideo) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(MethodConst.ParamConst.deviceDetail, deviceListBean);
//            bundle.putInt(MethodConst.ParamConst.recordType, MethodConst.ParamConst.recordTypeCloud);
//            Intent intent = new Intent(DeviceOnlineMediaPlayActivityNew.this, DeviceRecordListActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
            Gson gson = new Gson();
            ProviderManager.getPlaybackProvider().gotoRecordList(gson.toJson(deviceListBean), MethodConst.ParamConst.recordTypeCloud);
        } else {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(MethodConst.ParamConst.deviceDetail, deviceListBean);
//            bundle.putInt(MethodConst.ParamConst.recordType, MethodConst.ParamConst.recordTypeLocal);
//            Intent intent = new Intent(DeviceOnlineMediaPlayActivityNew.this, DeviceRecordListActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
            Gson gson = new Gson();
            ProviderManager.getPlaybackProvider().gotoRecordList(gson.toJson(deviceListBean), MethodConst.ParamConst.recordTypeLocal);
        }
    }

    private void showRecordListTip(String txt) {
        rcvVideoList.setVisibility(View.GONE);
        tvNoVideo.setVisibility(View.VISIBLE);
        tvNoVideo.setText(txt);
    }

    private void initLocalRecord() {
        if ((localRecordsDataTip != null && !localRecordsDataTip.isEmpty()) || localRecordsDataList != null) {
            if (localRecordsDataList != null) {
                showRecordList(localRecordsDataList);
            } else {
                showRecordListTip(localRecordsDataTip);
            }
        } else {
            getLocalRecord();
          /*  deviceRecordService.querySDUse(deviceListBean.deviceId, new IGetDeviceInfoCallBack.ICommon<String>() {
                @Override
                public void onCommonBack(String response) {
                    if (!"empty".equals(response)){
                        getLocalRecord();
                    }else{
                        localRecordsDataTip = getResources().getString(R.string.lc_demo_device_local_sd);
                        showRecordListTip(localRecordsDataTip);
                    }
                }
                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(DeviceOnlineMediaPlayActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });*/

        }
    }

    private void getLocalRecord() {
        LocalRecordsData localRecordsData = new LocalRecordsData();
        localRecordsData.data.deviceId = deviceListBean.deviceId;
        localRecordsData.data.channelId = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId;
        localRecordsData.data.beginTime = DateHelper.dateFormat(new Date(System.currentTimeMillis())) + " 00:00:00";
        localRecordsData.data.endTime = DateHelper.dateFormat(new Date(System.currentTimeMillis())) + " 23:59:59";
        localRecordsData.data.type = "All";
        localRecordsData.data.queryRange = "1-6";
        localRecordsData.data.count = "6";
        deviceRecordService.queryLocalRecords(localRecordsData, new IGetDeviceInfoCallBack.IDeviceLocalRecordCallBack() {
            @Override
            public void deviceLocalRecord(LocalRecordsData.Response result) {
                List<LocalRecordsData.ResponseData.RecordsBean> localRecords = result.data.records;
                if (localRecords != null && localRecords.size() > 0) {
                    localRecordsDataList = new ArrayList<>();
                    for (LocalRecordsData.ResponseData.RecordsBean recordsBean : localRecords) {
                        RecordsData recordsData = RecordsData.parseLocalData(recordsBean, deviceListBean.deviceId);
                        localRecordsDataList.add(recordsData);
                    }
                    showRecordList(localRecordsDataList);
                } else {
                    localRecordsDataTip = getResources().getString(R.string.lc_demo_device_more_video);
                    showRecordListTip(localRecordsDataTip);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                localRecordsDataTip = throwable.getMessage();
                showRecordListTip(localRecordsDataTip);
            }
        });
    }

    private void initView() {
        frLiveWindow = findViewById(R.id.fr_live_window);
        frLiveWindowContent = findViewById(R.id.fr_live_window_content);
        tvCoverStream = findViewById(R.id.tv_cover_stream);
        tvCloudVideo = findViewById(R.id.tv_cloud_video);
        tvLocalVideo = findViewById(R.id.tv_local_video);
        llBack = findViewById(R.id.ll_back);
        tvDeviceName = findViewById(R.id.tv_device_name);
        llDetail = findViewById(R.id.ll_detail);
        llPlayPause = findViewById(R.id.ll_paly_pause);
        llPlayStyle = findViewById(R.id.ll_play_style);
        llSound = findViewById(R.id.ll_sound);
        llFullScreen = findViewById(R.id.ll_fullscreen);
        llCloudStage = findViewById(R.id.ll_cloudstage);
        llScreenShot = findViewById(R.id.ll_screenshot);
        llSpeak = findViewById(R.id.ll_speak);
        llVideo = findViewById(R.id.ll_video);
        llVideoContent = findViewById(R.id.ll_video_content);
        rcvVideoList = findViewById(R.id.rcv_video_list);
        tvNoVideo = findViewById(R.id.tv_no_video);
        rudderView = findViewById(R.id.rudder_view);
        ivPalyPause = findViewById(R.id.iv_paly_pause);
        ivPlayStyle = findViewById(R.id.iv_play_style);
        tvPixel = findViewById(R.id.tv_play_pixel);
        ivSound = findViewById(R.id.iv_sound);
        ivCloudStage = findViewById(R.id.iv_cloudStage);
        ivScreenShot = findViewById(R.id.iv_screen_shot);
        ivSpeak = findViewById(R.id.iv_speak);
        ivVideo = findViewById(R.id.iv_video);

        llCloudStage1 = findViewById(R.id.ll_cloudstage1);
        llScreenShot1 = findViewById(R.id.ll_screenshot1);
        llSpeak1 = findViewById(R.id.ll_speak1);
        llVideo1 = findViewById(R.id.ll_video1);
        ivCloudStage1 = findViewById(R.id.iv_cloudStage1);
        ivScreenShot1 = findViewById(R.id.iv_screen_shot1);
        ivSpeak1 = findViewById(R.id.iv_speak1);
        ivVideo1 = findViewById(R.id.iv_video1);

        rlLoading = findViewById(R.id.rl_loading);
        pbLoading = findViewById(R.id.pb_loading);
        tvLoadingMsg = findViewById(R.id.tv_loading_msg);
        llController = findViewById(R.id.ll_controller);
        frRecord = findViewById(R.id.fr_record);
        rlTitle = findViewById(R.id.rl_title);
        ivChangeScreen = findViewById(R.id.iv_change_screen);
        ivLimitLeft = findViewById(R.id.iv_direction_limit_left);
        ivLimitRight = findViewById(R.id.iv_direction_limit_right);
        ivLimitUp = findViewById(R.id.iv_direction_limit_up);
        ivLimitDown = findViewById(R.id.iv_direction_limit_down);
        linearLayoutManager = new LinearLayoutManager(DeviceOnlineMediaPlayActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvVideoList.setLayoutManager(linearLayoutManager);
        initCommonClickListener();
        // 初始化播放窗口
        switchScreenDirection();
        mPlayWin.initPlayWindow(this, frLiveWindowContent, 0, false);
        setWindowListener(mPlayWin);
        mPlayWin.openTouchListener();//开启收拾监听


    }

    private void switchScreenDirection() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(frLiveWindow.getLayoutParams());
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            mLayoutParams.width = metric.widthPixels; // 屏幕宽度（像素）
            mLayoutParams.height = metric.widthPixels * 9 / 16;
            mLayoutParams.setMargins(0, 0, 0, 0);
            mLayoutParams.addRule(RelativeLayout.BELOW, R.id.rl_title);
            frLiveWindow.setLayoutParams(mLayoutParams);
            MediaPlayHelper.quitFullScreen(DeviceOnlineMediaPlayActivity.this);
            llController.setVisibility(View.VISIBLE);
            rlTitle.setVisibility(View.VISIBLE);
            llSpeak1.setVisibility(View.GONE);
            llCloudStage1.setVisibility(View.GONE);
            llVideo1.setVisibility(View.GONE);
            llScreenShot1.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(frRecord
                    .getLayoutParams());
            layoutParams.addRule(RelativeLayout.BELOW, R.id.ll_controller);
            frRecord.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(rudderView.getLayoutParams());
            layoutParams3.gravity = Gravity.CENTER;
            rudderView.setLayoutParams(layoutParams3);
            switchCloudRudder(false);
        } else {
//            DisplayMetrics metric = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metric);
            RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutParams.setMargins(0, 0, 0, 0);
            mLayoutParams.removeRule(RelativeLayout.BELOW);
            frLiveWindow.setLayoutParams(mLayoutParams);
            MediaPlayHelper.setFullScreen(DeviceOnlineMediaPlayActivity.this);
            llController.setVisibility(View.GONE);
            rlTitle.setVisibility(View.GONE);
            llSpeak1.setVisibility(View.VISIBLE);
            llCloudStage1.setVisibility(View.VISIBLE);
            llVideo1.setVisibility(View.VISIBLE);
            llScreenShot1.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(frRecord
                    .getLayoutParams());
            layoutParams.removeRule(RelativeLayout.BELOW);
            frRecord.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(rudderView.getLayoutParams());
            layoutParams3.gravity = Gravity.CENTER_VERTICAL;
            rudderView.setLayoutParams(layoutParams3);
            switchCloudRudder(false);
        }
    }

    private void initData() {
        bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
//        deviceListBean = (DeviceDetailListData.ResponseData.DeviceListBean) bundle.getSerializable(MethodConst.ParamConst.deviceDetail);
        String json = bundle.getString(MethodConst.ParamConst.deviceDetail);
        Gson gson = new Gson();
        deviceListBean = gson.fromJson(json, DeviceDetailListData.ResponseData.DeviceListBean.class);
        switchVideoList(true);
        getDeviceLocalCache();
        tvDeviceName.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);

        if (deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions != null && deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions.size() > 0) {
            showHD = false;
            bateMode = deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions.get(0).streamType;
            imageSize = deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions.get(0).imageSize;
            tvPixel.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions.get(0).name);
        } else {
            showHD = true;
        }
        lcPopupWindow = new LcPopupWindow(DeviceOnlineMediaPlayActivity.this, deviceListBean.channelList.get(deviceListBean.checkedChannel).resolutions);
        lcPopupWindow.getContentView().measure(lcPopupWindow.makeDropDownMeasureSpec(lcPopupWindow.getWidth()),
                lcPopupWindow.makeDropDownMeasureSpec(lcPopupWindow.getHeight()
                ));
    }

    /**
     * Obtain device cache information
     *
     * 获取设备缓存信息
     */
    private void getDeviceLocalCache() {
        DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
        deviceLocalCacheData.setDeviceId(deviceListBean.deviceId);
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
            deviceLocalCacheData.setChannelId(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
        }
        DeviceLocalCacheService deviceLocalCacheService = DeviceLocalCacheService.getInstance();
        deviceLocalCacheService.findLocalCache(deviceLocalCacheData, this);
    }


    @Override
    public void deviceCache(DeviceLocalCacheData deviceLocalCacheData) {
        BitmapDrawable bitmapDrawable = MediaPlayHelper.picDrawable(deviceLocalCacheData.getPicPath());
        if (bitmapDrawable != null) {
            rlLoading.setBackground(bitmapDrawable);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), deviceListBean.deviceId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
        recordStatus = RecordStatus.STOP;
    }

    private void initAbility(boolean loadSuccess) {
        String deviceAbility = deviceListBean.getAbility();
        String channelAbility = deviceListBean.channelList.get(deviceListBean.checkedChannel).getAbility();
        //云台
        supportPTZ = DeviceAbilityHelper.isHasAbility(deviceAbility, channelAbility, "PT", "PTZ") && loadSuccess;
        cloudStageClickListener(supportPTZ);
        //对讲
        speakClickListener(DeviceAbilityHelper.isHasAbility(deviceAbility, channelAbility, "AudioTalkV1", "AudioTalk") && loadSuccess);
    }

    private void switchVideoList(boolean b) {
        this.cloudvideo = b;
        tvCloudVideo.setSelected(cloudvideo);
        tvLocalVideo.setSelected(!cloudvideo);
        if (cloudvideo) {
            initCloudRecord();
        } else {
            initLocalRecord();
        }
    }

    private void initCommonClickListener() {
        llBack.setOnClickListener(this);
        llDetail.setOnClickListener(this);
        tvCloudVideo.setOnClickListener(this);
        tvLocalVideo.setOnClickListener(this);
        tvNoVideo.setOnClickListener(this);
        llFullScreen.setOnClickListener(this);
    }

    private void featuresClickListener(boolean loadSuccess) {
        llPlayPause.setOnClickListener(loadSuccess ? this : null);
        llPlayStyle.setOnClickListener(loadSuccess ? this : null);

        llScreenShot.setOnClickListener(loadSuccess ? this : null);
        llScreenShot1.setOnClickListener(loadSuccess ? this : null);
        llVideo.setOnClickListener(loadSuccess ? this : null);
        llVideo1.setOnClickListener(loadSuccess ? this : null);
        llSound.setOnClickListener(loadSuccess ? this : null);
        ivPalyPause.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_pause_disable));
        if (showHD) {
            ivPlayStyle.setVisibility(View.VISIBLE);
            tvPixel.setVisibility(View.GONE);
            ivPlayStyle.setImageDrawable(videoMode == VideoMode.MODE_HD ?
                    (loadSuccess ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_hd) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_hd_disable)) :
                    (loadSuccess ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_sd) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_hd_disable)));
        } else {
            ivPlayStyle.setVisibility(View.GONE);
            tvPixel.setVisibility(View.VISIBLE);
        }

        ivScreenShot.setImageDrawable(loadSuccess
                ? getDrawable(R.drawable.lc_demo_photo_capture_selector)
                : getDrawable(R.mipmap.lc_demo_livepreview_icon_screenshot_disable));
        ivVideo.setImageDrawable(loadSuccess
                ? getDrawable(R.mipmap.lc_demo_livepreview_icon_video)
                : getDrawable(R.mipmap.lc_demo_livepreview_icon_video_disable));

        ivScreenShot1.setImageDrawable(loadSuccess
                ? getDrawable(R.mipmap.live_video_icon_h_screenshot)
                : getDrawable(R.mipmap.live_video_icon_h_screenshot_disable));
        ivVideo1.setImageDrawable(loadSuccess
                ? getDrawable(R.mipmap.live_video_icon_h_video_off)
                : getDrawable(R.mipmap.live_video_icon_h_video_off_disable));
        ivSound.setImageDrawable(loadSuccess ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off_disable));
//        //媒体声
//        if (soundStatus != SoundStatus.PLAY) {
//            return;
//        }
        if (loadSuccess && openAudio()) {
            soundStatus = SoundStatus.PLAY;
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_on));
        }
    }

    private void cloudStageClickListener(boolean isSupport) {
        llCloudStage.setOnClickListener(isSupport ? this : null);
        ivCloudStage.setImageDrawable(isSupport
                ? getDrawable(R.mipmap.lc_demo_livepreview_icon_cloudstage)
                : getDrawable(R.mipmap.lc_demo_livepreview_icon_cloudstage_disable));
        llCloudStage1.setOnClickListener(isSupport ? this : null);
        ivCloudStage1.setImageDrawable(isSupport
                ? getDrawable(R.mipmap.live_video_icon_h_cloudterrace_off)
                : getDrawable(R.mipmap.live_video_icon_h_cloudterrace_off_disable));
    }

    private void speakClickListener(boolean isSupport) {
        ivSpeak.setOnClickListener(isSupport ? this : null);
        ivSpeak.setImageDrawable(isSupport
                ? getDrawable(R.mipmap.lc_demo_livepreview_icon_speak)
                : getDrawable(R.mipmap.lc_demo_livepreview_icon_speak_disable));
        ivSpeak1.setOnClickListener(isSupport ? this : null);
        ivSpeak1.setImageDrawable(isSupport
                ? getDrawable(R.mipmap.live_video_icon_h_talk_off)
                : getDrawable(R.mipmap.live_video_icon_h_talk_off_disable));
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
                //mPlayWin.setSEnhanceMode(4);//设置降噪等级最大
                super.onPlayerResult(index, code, resultSource);
                LogUtil.debugLog(TAG, "onPlayerResult: index= " + index + " , code= " + code + " , resultSource= " + resultSource);
                boolean failed = false;
                if (resultSource == 99) {
                    //code  -1000 HTTP交互出错或超时
                    failed = true;
                } else {
                    if (resultSource == 5 && (!(code.equals("1000") || code.equals("0") || code.equals("4000")))) {
                        // code 1000-开启播放成功  0-开始拉流
                        failed = true;
                        if (code.equals("1000005")) {
                            inputEncryptKey();
                        }
                    } else if (resultSource == 0 && (code.equals("0") || code.equals("1") || code.equals("3") || code.equals("7"))) {
                        // code
                        // 0-组帧失败，错误状态
                        // 1-内部要求关闭,如连接断开等，错误状态
                        // 3-RTSP鉴权失败，错误状态
                        // 7-秘钥错误
                        failed = true;
                        if (code.equals("7")) {
                            inputEncryptKey();
                        }
                    }
                }
                if (failed) {
                    loadingStatus(LoadStatus.LOAD_ERROR, getResources().getString(R.string.lc_demo_device_video_play_error) + ":" + code + "." + resultSource, "");
                    playStatus = PlayStatus.ERROR;
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
                playStatus = PlayStatus.PLAY;
            }

            //接收数据回调
            @Override
            public void onReceiveData(int index, int len) {
                super.onReceiveData(index, len);
                LogUtil.debugLog(TAG, "onReceiveData: index= " + index + " , len= " + len);
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
            }

            //播放时间信息回调
            @Override
            public void onPlayerTime(int index, long time) {
                super.onPlayerTime(index, time);
                LogUtil.debugLog(TAG, "onPlayerTime: index= " + index + " , time= " + time);
            }


            @Override
            public void onIVSInfo(int index, final String ivsInfo, long type, long len, long realLen) {
                super.onIVSInfo(index, ivsInfo, type, len, realLen);
                LogUtil.debugLog(TAG, "onIVSInfo: index= " + index + " , ivsInfo= " + ivsInfo);

                if (playStatus != PlayStatus.PLAY) {
                    return;
                }
                try {
                    if (ivsInfo != null && ivsInfo.contains("PtzLimitStatus")) {
                        final String source = ivsInfo.substring(ivsInfo.lastIndexOf("[") + 1, ivsInfo.lastIndexOf("]")).replace(" ", "");
                        String[] target = source.split(",");
                        if (target != null && target.length == 2) {
                            final String hor = target[0];
                            final String ver = target[1];
                            if (hor.equals("1") || hor.equals("-1") || ver.equals("1") || ver.equals("-1")) {
                                if (hor.equals("1")) {
                                    mHandler.sendEmptyMessage(11);
                                } else if (hor.equals("-1")) {
                                    mHandler.sendEmptyMessage(22);
                                } else if (ver.equals("1")) {
                                    mHandler.sendEmptyMessage(33);
                                } else if (ver.equals("-1")) {
                                    mHandler.sendEmptyMessage(44);
                                }
                                mHandler.removeMessages(0);
                                mHandler.sendEmptyMessageDelayed(0, 2000);//两秒消失
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * Enter the secret key
     *
     * 输入秘钥
     */
    private void inputEncryptKey() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (encryptKeyInputDialog == null) {
                    encryptKeyInputDialog = new EncryptKeyInputDialog(DeviceOnlineMediaPlayActivity.this);
                }
                encryptKeyInputDialog.show();
                encryptKeyInputDialog.setOnClick(new EncryptKeyInputDialog.OnClick() {
                    @Override
                    public void onSure(String txt) {
                        encryptKey = txt;
                        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), txt);
                    }
                });
            }
        });
    }

    /**
     * Playing status
     * @param loadStatus Load status
     * @param msg  message
     *
     * 播放状态
     * @param loadStatus 播放状态
     * @param msg  消息
     */
    private void loadingStatus(final LoadStatus loadStatus, final String msg, final String psk) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadStatus == LoadStatus.LOADING) {
                    //先关闭
                    stop();
                    //开始播放
                    play(psk);
                    rlLoading.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.VISIBLE);
                    tvLoadingMsg.setText(msg);
                } else if (loadStatus == LoadStatus.LOAD_SUCCESS) {
                    //播放成功
                    rlLoading.setVisibility(View.GONE);
                    rudderView.enable(true);
                    if (LCUtils.isDebug(DeviceOnlineMediaPlayActivity.this)) {
                        showStreamTypeCover();
                    }
                    initAbility(true);
                    featuresClickListener(true);


                } else {
                    //播放失败
                    stop();
                    rlLoading.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.GONE);
                    tvLoadingMsg.setText(msg);
                    initAbility(false);
                    featuresClickListener(false);
                }
            }
        });
    }


    public void showStreamTypeCover() {
        tvCoverStream.setVisibility(View.VISIBLE);
        if (mPlayWin.isP2pTag()) {
            tvCoverStream.setText("P2P");
        } else {
            tvCoverStream.setText("MTS");
        }
    }

    /**
     * Start playing
     *
     * 开始播放
     */
    public void play(String psk) {

        if (showHD) {
            bateMode = videoMode == VideoMode.MODE_HD ? 0 : 1;
        }
        LCOpenSDK_ParamReal paramReal = new LCOpenSDK_ParamReal(
                TokenHelper.getInstance().subAccessToken,
                deviceListBean.deviceId,
                Integer.parseInt(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId),
                psk,
                deviceListBean.playToken,
                bateMode,
                true, true, imageSize, deviceListBean.productId
        );
        mPlayWin.playRtspReal(paramReal);

    }

    /**
     * Stop playing
     *
     * 停止播放
     */
    public void stop() {
        captureLastPic();
        // 关闭录像
        if (recordStatus == RecordStatus.START) {
            recordStatus = RecordStatus.STOP;
            if (stopRecord()) {
                Toast.makeText(this, getResources().getString(R.string.lc_demo_device_record_stop), Toast.LENGTH_SHORT).show();
                LogUtil.debugLog(TAG, "videopath:::" + videoPath);
                MediaPlayHelper.updatePhotoVideo(videoPath);
            }
        }
//        stopRecord();
        closeAudio();// 关闭音频
        stopTalk1();//关闭对讲
        rudderView.enable(false);
        mPlayWin.stopRtspReal(true);// 关闭视频
    }

    /**
     * Save the last frame for the cover
     *
     * 保存最后一帧做封面
     */
    private void captureLastPic() {
        if (playStatus == PlayStatus.ERROR) {
            return;
        }
        String capturePath;
        try {
            capturePath = capture(false);
        } catch (Throwable e) {
            capturePath = null;
        }
        if (capturePath == null) {
            return;
        }
        DeviceLocalCacheService deviceLocalCacheService = DeviceLocalCacheService.getInstance();
        DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
        deviceLocalCacheData.setPicPath(capturePath);
        deviceLocalCacheData.setDeviceName(deviceListBean.deviceName);
        deviceLocalCacheData.setDeviceId(deviceListBean.deviceId);
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
            deviceLocalCacheData.setChannelId(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
            deviceLocalCacheData.setChannelName(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);
        }
        deviceLocalCacheService.addLocalCache(deviceLocalCacheData);
    }

    /**
     * Start the video
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
        videoPath = MediaPlayHelper.getCaptureAndVideoPath(MediaPlayHelper.LCFilesType.LCVideo, channelName);
        LogUtil.debugLog(TAG, "videopath:::----start" + videoPath);
        //MediaScannerConnection.scanFile(this, new String[]{videoPath}, null, null);
        // 开始录制 1
        int ret = mPlayWin.startRecord(videoPath, 1, 0x7FFFFFFF);
        return ret == 0;
    }

    /**
     * Close the video
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
    public String capture(boolean notify) {
        String captureFilePath = null;
        String channelName = null;
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
            channelName = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName;
        } else {
            channelName = deviceListBean.deviceName;
        }
        // 去除通道中在目录中的非法字符
        channelName = channelName.replace("-", "");
        captureFilePath = MediaPlayHelper.getCaptureAndVideoPath(notify ? MediaPlayHelper.LCFilesType.LCImage : MediaPlayHelper.LCFilesType.LCImageCache, channelName);
        int ret = mPlayWin.snapShot(captureFilePath);
        if (ret == 0) {
            if (notify) {
                // 扫描到相册中
                MediaPlayHelper.updatePhotoAlbum(captureFilePath);
                // MediaScannerConnection.scanFile(this, new String[]{captureFilePath}, null, null);
            }
        } else {
            captureFilePath = null;
        }
        return captureFilePath;
    }

    /**
     * Open audio
     *
     * 打开声音
     */
    public boolean openAudio() {
        return mPlayWin.playAudio() == 0;
    }

    /**
     * stop audio
     *
     * 关闭声音
     */
    public boolean closeAudio() {
        return mPlayWin.stopAudio() == 0;
    }

    /**
     * Start talk,Multichannel Channel ID Indicates the corresponding channel ID,Single-channel transmission -1
     *
     * 开始对讲 多通道通道号参数传入对应的通道号，单通道传-1
     */
    public void startTalk() {
        closeAudio();
        soundStatus = SoundStatus.STOP;
        speakStatus = SpeakStatus.OPENING;
        ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off_disable));
        llSound.setClickable(false);//声音按钮不允许点击
        LCOpenSDK_Talk.setListener(audioTalkerListener);//对讲前先设备监听
        int channelId = -1;

        if (Integer.parseInt(deviceListBean.channelNum) > 1) {
           /* if(null != deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId && !"".equals(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId)) {
                channelId = Integer.parseInt(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
            } else {
                stopTalk();
                Logger.e(TAG, "server returned NVR device channelId is null or empty charators");
                return;
            }*/
            channelId = Integer.parseInt(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
            Logger.d(TAG, "deviceCatalog = " + deviceListBean.catalog + ", routing device talk...");
        }
        Logger.d(TAG, "playTalk, channelId = " + channelId);
        LCOpenSDK_ParamTalk paramTalk = new LCOpenSDK_ParamTalk(
                TokenHelper.getInstance().subAccessToken,
                deviceListBean.deviceId,
                channelId,
                TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey,
                deviceListBean.playToken,
                true, "talk"
                , deviceListBean.productId
        );
        LCOpenSDK_Talk.playTalk(paramTalk);
    }

    /**
     * Stop talk
     *
     * 停止对讲
     */
    public void stopTalk1() {
        ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off_disable));
        speakStatus = SpeakStatus.STOP;
        ivSpeak.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_speak));
        ivSpeak1.setImageDrawable(getDrawable(R.mipmap.live_video_icon_h_talk_off));

        LCOpenSDK_Talk.stopTalk();
        LCOpenSDK_Talk.setListener(null);//停止对讲后对讲监听置为空
        llSound.setClickable(true);
    }

    /**
     * Stop talk
     *
     * 停止对讲
     */
    public void stopTalk() {
        soundStatus = soundStatusPre;
        if (SoundStatus.PLAY == soundStatus) {
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_on));
            openAudio();
        } else {
            ivSound.setImageDrawable(getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off));
        }
        speakStatus = SpeakStatus.STOP;
        ivSpeak.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_speak));
        ivSpeak1.setImageDrawable(getDrawable(R.mipmap.live_video_icon_h_talk_off));

        LCOpenSDK_Talk.stopTalk();
        LCOpenSDK_Talk.setListener(null);//停止对讲后对讲监听置为空
        llSound.setClickable(true);
    }

    class AudioTalkerListener extends LCOpenSDK_TalkerListener {
        public AudioTalkerListener() {
            super();
        }

        @Override
        public void onTalkResult(String error, int type) {
            super.onTalkResult(error, type);
            boolean talkResult = false;
            Logger.e(TAG, "onTalkResult: error：" + error + "  ,type:" + type);
            if (type == 99 || error.equals("-1000") || error.equals("0") || error.equals("1") || error.equals("3")) {
                talkResult = false;
            } else if (error.equals("4")) {
                talkResult = true;
            }
            final boolean finalTalkResult = talkResult;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!finalTalkResult) {
                        stopTalk();
                        // 提示对讲打开失败
                        Toast.makeText(DeviceOnlineMediaPlayActivity.this, R.string.lc_demo_device_talk_open_failed, Toast.LENGTH_SHORT).show();
                        speakStatus = SpeakStatus.STOP;
                        ivSpeak.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_speak));
                        ivSpeak1.setImageDrawable(getDrawable(R.mipmap.live_video_icon_h_talk_off));
                    } else {
                        // 提示对讲打开成功
                        Toast.makeText(DeviceOnlineMediaPlayActivity.this, R.string.lc_demo_device_talk_open_success, Toast.LENGTH_SHORT).show();
                        speakStatus = SpeakStatus.PLAY;
                        ivSpeak.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_speak_ing));
                        ivSpeak1.setImageDrawable(getDrawable(R.mipmap.live_video_icon_h_talk_on));

                    }
                }
            });
        }

        @Override
        public void onTalkPlayReady() {
            super.onTalkPlayReady();
        }

        @Override
        public void onAudioRecord(byte[] bytes, int i, int i1, int i2, int i3) {
            super.onAudioRecord(bytes, i, i1, i2, i3);
        }

        @Override
        public void onAudioReceive(byte[] bytes, int i, int i1, int i2, int i3) {
            super.onAudioReceive(bytes, i, i1, i2, i3);
        }

        @Override
        public void onDataLength(int i) {
            super.onDataLength(i);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_back) {
            //返回
            onBackPressed();
        } else if (id == R.id.ll_fullscreen) {
            //横竖屏切换
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            ivChangeScreen.setImageDrawable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? getResources().getDrawable(R.mipmap.live_btn_smallscreen) : getResources().getDrawable(R.mipmap.icon_hengping));
        } else if (id == R.id.ll_detail) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(MethodConst.ParamConst.deviceDetail, deviceListBean);
//            Intent intent = new Intent(DeviceOnlineMediaPlayActivity.this, DeviceDetailActivity.class);
//            intent.putExtras(bundle);
//            startActivityForResult(intent, 0);
            ProviderManager.getDeviceDetailProvider().gotoDeviceDetails(this, new Gson().toJson(deviceListBean), "");
        } else if (id == R.id.ll_paly_pause) {
            //播放暂停
            if (playStatus == PlayStatus.PLAY) {
                stop();
                initAbility(false);
                featuresClickListener(false);
                llPlayPause.setOnClickListener(this);
            } else {
//                getDeviceLocalCache();
                rlLoading.setBackground(null);
                loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_loading), TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey);
            }
            playStatus = (playStatus == PlayStatus.PLAY) ? PlayStatus.PAUSE : PlayStatus.PLAY;
            ivPalyPause.setImageDrawable(playStatus == PlayStatus.PLAY ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_pause) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_play));
        } else if (id == R.id.ll_play_style) {
            if (showHD) {
                //视频清晰度切换
                videoMode = (videoMode == VideoMode.MODE_HD) ? VideoMode.MODE_SD : VideoMode.MODE_HD;
                ivPlayStyle.setImageDrawable(videoMode == VideoMode.MODE_HD ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_hd) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_sd));
                loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey);
            } else {
                int offsetX = llPlayStyle.getWidth() / 3 * 2;
                int offsetY = -(lcPopupWindow.getContentView().getMeasuredHeight() + 10);
                PopupWindowCompat.showAsDropDown(lcPopupWindow, llPlayStyle, offsetX, offsetY, Gravity.START);
                lcPopupWindow.setPixelRecycleListener(new LcPopupWindow.onRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, String name, int image_size, int streamType) {
                        tvPixel.setText(name);
                        lcPopupWindow.dismiss();
                        imageSize = image_size;
                        bateMode = streamType;
                        loadingStatus(LoadStatus.LOADING, getResources().getString(R.string.lc_demo_device_video_play_change), TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey);
                    }
                });
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
            ivSound.setImageDrawable(soundStatus == SoundStatus.PLAY ? getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_on) : getDrawable(R.mipmap.lc_demo_live_video_icon_h_sound_off));
        } else if (id == R.id.ll_cloudstage || id == R.id.ll_cloudstage1) {
            //云台和录像列表切换
            switchCloudRudder(!isShwoRudder);
        } else if (id == R.id.ll_screenshot || id == R.id.ll_screenshot1) {
            //截图
            if (capture(true) != null) {
                Toast.makeText(this, getResources().getString(R.string.lc_demo_device_capture_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.lc_demo_device_capture_failed), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_speak || id == R.id.iv_speak1) {
            //对讲 如果是打开状态去关闭,反之
            if (speakStatus == SpeakStatus.NO_SUPPORT || speakStatus == SpeakStatus.OPENING) {
                return;
            }
            if (speakStatus == SpeakStatus.STOP) {
                soundStatusPre = soundStatus;
                startTalk();
            } else {
                Toast.makeText(DeviceOnlineMediaPlayActivity.this, R.string.lc_demo_device_talk_close_success, Toast.LENGTH_SHORT).show();
                stopTalk();

            }
        } else if (id == R.id.ll_video || id == R.id.ll_video1) {
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
                    MediaPlayHelper.updatePhotoVideo(videoPath);
                } else {
                    return;
                }
            }
            recordStatus = (recordStatus == RecordStatus.START) ? RecordStatus.STOP : RecordStatus.START;
            ivVideo.setImageDrawable(recordStatus == RecordStatus.START
                    ? getDrawable(R.mipmap.lc_demo_livepreview_icon_video_ing)
                    : getDrawable(R.mipmap.lc_demo_livepreview_icon_video));
            ivVideo1.setImageDrawable(recordStatus == RecordStatus.START
                    ? getDrawable(R.mipmap.live_video_icon_h_video_on)
                    : getDrawable(R.mipmap.live_video_icon_h_video_off));
        } else if (id == R.id.tv_cloud_video) {
            //切换至云录像
            switchVideoList(true);
        } else if (id == R.id.tv_local_video) {
            //切换至设备录像
            switchVideoList(false);
        } else if (id == R.id.tv_no_video) {
            //暂无录像切换
            gotoRecordList();
        }
    }

    private void switchCloudRudder(boolean isShow) {
        this.isShwoRudder = isShow;
        if (isShow) {
            ivCloudStage.setImageDrawable(getDrawable(R.mipmap.lc_demo_livepreview_icon_cloudstage_click));
            ivCloudStage1.setImageDrawable(getDrawable(R.mipmap.live_video_icon_h_cloudterrace_on));
            llVideoContent.setVisibility(View.GONE);
            rudderView.setVisibility(View.VISIBLE);
        } else {
            ivCloudStage.setImageDrawable(supportPTZ
                    ? getDrawable(R.mipmap.lc_demo_livepreview_icon_cloudstage)
                    : getDrawable(R.mipmap.lc_demo_livepreview_icon_cloudstage_disable));
            ivCloudStage1.setImageDrawable(supportPTZ
                    ? getDrawable(R.mipmap.live_video_icon_h_cloudterrace_off)
                    : getDrawable(R.mipmap.live_video_icon_h_cloudterrace_off_disable));
            llVideoContent.setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? View.VISIBLE : View.GONE);
            rudderView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayWin.uninitPlayWindow();// 销毁底层资源
        uninitOrientationEventListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            boolean unBind = data.getBooleanExtra("LCDEVICE_UNBIND", false);
            if (unBind) {
                finish();
            }
        }
        if (resultCode == 100 && data != null) {
            String name = data.getStringExtra("LCDEVICE_NEW_NAME");
            tvDeviceName.setText(name);
            deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName = name;
        }
    }


    public static final int NO_ORIENTATION_REQUEST = -1;

    public static final int MSG_REQUEST_ORIENTATION = 1;


    private void initOrientationEventListener() {

        mOrientationEventListener = new MediaPlayOrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL, mHandler);

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        } else {
            mOrientationEventListener.disable();
        }
    }


    private void uninitOrientationEventListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
            mOrientationEventListener = null;
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

    static class MediaPlayOrientationEventListener extends OrientationEventListener {

        private WeakReference<Handler> mWRHandler;

        private int mOrientationEventListenerLastOrientationRequest = NO_ORIENTATION_REQUEST;

        public MediaPlayOrientationEventListener(Context context, int rate, Handler handler) {
            super(context, rate);
            mWRHandler = new WeakReference<Handler>(handler);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            int requestedOrientation = createOrientationRequest(orientation);
            if (requestedOrientation != NO_ORIENTATION_REQUEST
                    && mOrientationEventListenerLastOrientationRequest != requestedOrientation) {
                Handler handler = mWRHandler.get();
                if (handler != null) {
                    handler.removeMessages(MSG_REQUEST_ORIENTATION);
                    Message msg = handler.obtainMessage(MSG_REQUEST_ORIENTATION);
                    msg.arg1 = requestedOrientation;
                    handler.sendMessageDelayed(msg, 200);
                    mOrientationEventListenerLastOrientationRequest = requestedOrientation;
                }
            }
        }

        private int createOrientationRequest(int rotation) {
            int requestedOrientation = NO_ORIENTATION_REQUEST;
            if (rotation == -1) {

            } else if (rotation < 10 || rotation > 350) {// 手机顶部向上
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else if (rotation < 100 && rotation > 80) {// 手机左边向上
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            } else if (rotation < 190 && rotation > 170) {// 手机底边向上
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            } else if (rotation < 280 && rotation > 260) {// 手机右边向上
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
            return requestedOrientation;
        }

    }
}
