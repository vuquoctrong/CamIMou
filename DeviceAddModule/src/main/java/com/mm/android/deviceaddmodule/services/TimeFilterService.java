package com.mm.android.deviceaddmodule.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.eventbus.event.CloseTimeFilterEvent;
import com.mm.android.mobilecommon.eventbus.event.NetWorkChangeCheckEvent;
import com.mm.android.deviceaddmodule.receiver.SucceedClickReceiver;
import com.mm.android.deviceaddmodule.receiver.TimeoutClickReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class TimeFilterService extends IntentService {
    private static final String TAG = TimeFilterService.class.getSimpleName();
    private final static int WIFI_STATE_NOTIFY_ID =  2222;             //通知ID
    private static final int MAX = 40 * 1000;//40秒
    private Timer timer = new Timer();
    private DHWifiUtil mDHWifiUtil;
    private String mNetSsid;


    public TimeFilterService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDHWifiUtil = new DHWifiUtil(this.getApplicationContext());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        mNetSsid = intent.getStringExtra(LCConfiguration.SSID);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if (timer==null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(TimeFilterService.this,TimeoutClickReceiver.class);
                showNotification(getString(R.string.application_name), getString(R.string.add_device_time_filter_tip), intent);
            }
        }, MAX);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noticeTimeFilterListener(CloseTimeFilterEvent event) {
        closeService();
    }

    private void closeService(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void checkNetWorkChange(NetWorkChangeCheckEvent event){
        if(isHotWifiConnect()){
            Intent intent = new Intent();
            intent.setClass(TimeFilterService.this,SucceedClickReceiver.class);
            showNotification(getString(R.string.application_name), getString(R.string.add_device_connect_finish_to_next), intent);
            closeService();
        }
    }

    /**
     * Check whether the device hotspot is connected
     * @return boolean Boolean Value
     *
     * 是否已连上设备热点
     * @return boolean 布尔值
     */
    public boolean isHotWifiConnect() {
        boolean isWifiConnected = DHNetworkUtil.NetworkType.NETWORK_WIFI.equals(DHNetworkUtil.getNetworkType(this.getApplicationContext()));
        WifiInfo wifiInfo = mDHWifiUtil.getCurrentWifiInfo();
        return !(wifiInfo == null) && wifiInfo.getSSID().equals("\"" + mNetSsid + "\"");
    }

    private void showNotification(String title, String context, Intent intent) {
        if(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA){
            return;
        }

        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = null;
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.setBigContentTitle(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                title : "");//标题设置为消息推送带过来的消息类型
        style.bigText(context);//设置推送消息富文本

        String notificationId = "TimeFilterId";
        String notificationName = "TimeFilter";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //26以上，必须设置ChannelId
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notifyManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this)
                    .setStyle(style)
                    .setSmallIcon(R.drawable.small_icon)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setContentTitle(title)
                    .setContentText(context)
                    .setContentIntent(mPendingIntent)
                    .setChannelId(notificationId);

        } else {
            builder = new Notification.Builder(this)
                    .setStyle(style)
                    .setSmallIcon(R.drawable.small_icon)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(context)
                    .setContentIntent(mPendingIntent);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                builder.setShowWhen(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notifyManager.notify(WIFI_STATE_NOTIFY_ID, builder.build());
        }
    }


}
