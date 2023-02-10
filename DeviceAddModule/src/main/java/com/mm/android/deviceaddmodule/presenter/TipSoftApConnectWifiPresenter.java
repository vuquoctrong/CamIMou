package com.mm.android.deviceaddmodule.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Network;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.TipSoftApConnectWifiConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.eventbus.event.NetWorkChangeCheckEvent;
import com.mm.android.mobilecommon.utils.ConnectivityApi;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.WifiUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class TipSoftApConnectWifiPresenter implements TipSoftApConnectWifiConstract.Presenter {
    private int TIME_OUT_TIME = 15 * 1000;        //连接设备热点超时时间
    private static final int CONNECT_FAILED = 0;                            //连接热点失败
    WifiUtil mDHWifiUtil;
    String mDeviceSn;
    boolean mConnectResult;
    boolean mBinding;   //开始绑定了，防止绑定多次
    WeakReference<TipSoftApConnectWifiConstract.View> mView;
    DeviceIntroductionInfo mTips;


    private ConnectivityApi.ConnectivityCallback mConnectivityCallback = new ConnectivityApi.ConnectivityCallback() {
        @Override
        public void onSuccess(Network network) {
            mConnectResult = true;
            LogUtil.debugLog("29217", "onSuccess(TipSoftApConnectWifiPresenter.java:200)------->>" );
        }

        @Override
        public void onFailed(final int error) {
            LogUtil.debugLog("onFailed", "onFailed error: " + error);
            mConnectResult = false;
            mBinding = false;
            if (error == -1 && mView.get()!=null)
                ConnectivityApi.instance(mView.get().getContextInfo()).disconnectWifiByNetworkRequest();
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isDeviceOnline();
                        //connectHotFailed();
                    }
                });
            }
        }
    };


    Runnable mTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.obtainMessage(CONNECT_FAILED).sendToTarget();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mView.get() != null &&
                    mView.get().isViewActive()) {
                connectHotFailed();
            }
        }
    };

    Runnable mReconectWifiAgainRunnable = new Runnable() {
        @Override
        public void run() {
            connectWifiAction(false);
        }
    };


    public TipSoftApConnectWifiPresenter(TipSoftApConnectWifiConstract.View view) {
        mView = new WeakReference<>(view);
        mDHWifiUtil = new WifiUtil(mView.get().getContextInfo().getApplicationContext());
        mTips = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        mDeviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        mView.get().updateWifiName(getWifiName());
    }


    /**
     * Sc Supported or not
     * @return boolean Boolean value
     *
     * 是否支持Sc
     * @return boolean 布尔值
     */
    public boolean isSupportAddBySc() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        return DeviceAddHelper.isSupportAddBySc(deviceAddInfo);
    }

    @Override
    public boolean isWifiEnable() {
        return mDHWifiUtil.isWifiEnabled();
    }

    @Override
    public void copyWifiPwd() {
        ClipboardManager clipboardManager = (ClipboardManager) mView.get().getContextInfo().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager != null) {
            String wifiPwd = "";
            DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
            // 支持sc码的软ap  wifiw为加密的，需要密码进行连接，密码为SC码
            if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
                wifiPwd = deviceAddInfo.getSc();
            }

            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, wifiPwd));
            mView.get().showToastInfo(R.string.common_copy_success);
        }
    }

    @Override
    public void dispatchHotConnected() {
        if(mDHWifiUtil.getCurrentWifiInfo() == null)
            return;
        LogUtil.debugLog("bz", "isConnectedDevHot(): " + isConnectedDevHot() + " mConnectResult: " + mConnectResult);
        if (isConnectedDevHot() && !mBinding) {
            mBinding = true;
            //通知服务已连上热点
            EventBus.getDefault().post(new NetWorkChangeCheckEvent());
            DeviceAddHelper.bindNetwork(new DeviceAddHelper.BindNetworkListener() {
                @Override
                public void onBindWifiListener() {
                    mHandler.removeCallbacks(mTimeOutRunnable);
                    mHandler.removeCallbacks(mReconectWifiAgainRunnable);
                    mView.get().cancelProgressDialog();
                    mView.get().goSecurityCheckPage();
                }
            });
        }
    }

    private void connectHotFailed() {
        mView.get().cancelProgressDialog();
        mView.get().showToastInfo(R.string.common_connect_failed);
        mConnectResult = false;

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        // 支持sc码的软ap
        boolean isSupportAddBySc = false;
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            isSupportAddBySc = true;
        }
        String wifiPwd = "";
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            if(deviceAddInfo.isManualInput() || TextUtils.isEmpty(deviceAddInfo.getSc())) {
                wifiPwd = mView.get().getContextInfo().getString(R.string.add_device_wait_to_connect_wifi_sc);
            } else {
                wifiPwd = deviceAddInfo.getSc();
            }
        }

        mView.get().updateConnectFailedTipText(getHotSSID(), wifiPwd, isSupportAddBySc, deviceAddInfo.isManualInput() || TextUtils.isEmpty(deviceAddInfo.getSc()));
    }

    public void connectWifiAction(boolean isFirst) {
        mView.get().showProgressDialog();
        mConnectResult = false;
        mBinding = false;
        mDHWifiUtil.openWifi();
        if (isConnectedDevHot()) {//若已连上设备热点,不需要去连接了
            dispatchHotConnected();
            return;
        }
        String ssid = getHotSSID();
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        //缓存设备热点ssid，用于中间热点断开重新连时，作为是否连上热点的判断依据，需要重新调用bindNetwork
        deviceAddInfo.setSsid(ssid);
        //缓存当前连接的wifi ssid,便于退出软ap时恢复网络
        WifiInfo wifiInfo = mDHWifiUtil.getCurrentWifiInfo();
        if (wifiInfo != null) {
            WifiConfiguration currentConfig = mDHWifiUtil.IsExsits(wifiInfo.getSSID());
            if (currentConfig != null) {
                deviceAddInfo.setPreviousSsid(currentConfig.SSID);
            }
        }
        // 支持sc码的软ap  wifiw为加密的，需要密码进行连接，密码为SC码
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            final String wifiPwd = deviceAddInfo.getSc();
            mDHWifiUtil.openWifi();
            mDHWifiUtil.disconnectCurrentWifi(getHotSSID());
            mDHWifiUtil.startScan();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiUtil.WifiCipherType type = mDHWifiUtil.getCipherType(getHotSSID());
                    mConnectResult = mDHWifiUtil.connectWifiEx(getHotSSID(), wifiPwd, type);
                }
            }, 3000);

        } else {
            isFirst = false;
            mConnectResult = mDHWifiUtil.connectWifi(getHotSSID(), "");
        }

        LogUtil.debugLog("bz", "mConnectResult : " + mConnectResult);
        if(isFirst){
            mHandler.postDelayed(mReconectWifiAgainRunnable, TIME_OUT_TIME);
        } else {
            mHandler.postDelayed(mTimeOutRunnable, TIME_OUT_TIME);
        }
    }

    private String getWifiName() {
        String wifiName = "";   //默认空字符串，防止空指针
        if (mTips != null && mTips.getStrInfos() != null) {
            wifiName = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_WIFI_NAME);
        }

        String wifiVersion = "";
        if (mTips != null && mTips.getStrInfos() != null) {
            wifiVersion = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_WIFI_VERSION);
        }

        // wifiVersion为空，使用软Ap通用设备热点名称"DAP-XXXX"
        if(TextUtils.isEmpty(wifiVersion)){
            if(!TextUtils.isEmpty(wifiName) && wifiName.contains("Doorbell")){//适配海外门铃
                return wifiName;
            }
            return DeviceAddHelper.AP_WIFI_NAME_DAP;
        }

        return TextUtils.isEmpty(wifiName) ? "" : wifiName;
    }

    @Override
    public String getHotSSID() {
        int appType = ProviderManager.getAppProvider().getAppType();
        String wifiName = getWifiName();    //OMS后台配置的wifi名称例如“K5-xxxx”
        String wifiNamePre = wifiName.substring(0, wifiName.lastIndexOf("-") + 1);  //取前缀例如“K5-”

        String wifiVersion = "";
        if (mTips != null && mTips.getStrInfos() != null) {
            wifiVersion = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_WIFI_VERSION);
        }

        String ssid = wifiNamePre + mDeviceSn;
        if(!TextUtils.isEmpty(wifiVersion) && wifiVersion.equalsIgnoreCase(DeviceAddHelper.AP_WIFI_VERSION_V1)){
            if (appType == LCConfiguration.APP_LECHANGE_OVERSEA){
                ssid = wifiNamePre + mDeviceSn;
            } else if (appType == LCConfiguration.APP_LECHANGE){
                ssid = wifiNamePre + mDeviceSn.substring(mDeviceSn.length() - 4); //国内，序列号后四位
            }
        }

        return ssid;
    }

    /**
     * Check whether the device hotspot is connected
     * @return boolean Boolean value
     *
     * 是否已连上设备热点
     * @return boolean 布尔值
     */
    public boolean isConnectedDevHot() {
        WifiInfo wifiInfo = mDHWifiUtil.getCurrentWifiInfo();
        LogUtil.debugLog("bz", " " + (wifiInfo == null ? "wifiInfo == null" : wifiInfo.getSupplicantState()));

        if (wifiInfo == null || (SupplicantState.ASSOCIATED != wifiInfo.getSupplicantState() && SupplicantState.COMPLETED != wifiInfo.getSupplicantState())) {
            return false;
        } else {
            String ssid = getHotSSID();
            ssid = "\"" + ssid + "\"";
            LogUtil.debugLog("bz", ssid + " " + wifiInfo.getSSID() + " " + wifiInfo.getSSID().equals(ssid));
            int appType = ProviderManager.getAppProvider().getAppType();
            if (appType == LCConfiguration.APP_LECHANGE_OVERSEA){
                return wifiInfo.getSSID().contains(mDeviceSn);
            } else if (appType == LCConfiguration.APP_LECHANGE){
                return wifiInfo.getSSID().equals(ssid);
            }else
            {
                return true;
            }

        }
    }


    @Override
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void connectToWifiByNetworkRequest() {
        if (mView.get() == null) {
            return;
        }


        if(!mDHWifiUtil.isWifiEnabled()){
            mView.get().openWifiPannel();
            return;
        }

        mView.get().showProgressDialog();
        if (DeviceAddHelper.isConnectedDevHot(mDHWifiUtil,mDeviceSn)) {//若已连上设备热点,不需要去连接了
            dispatchHotConnected();
            return;
        }
        String ssid = getHotSSID();
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        //缓存设备热点ssid，用于中间热点断开重新连时，作为是否连上热点的判断依据，需要重新调用bindNetwork
        deviceAddInfo.setSsid(ssid);

        String wifiPwd = "";
        // 支持sc码的软ap  wifiw为加密的，需要密码进行连接，密码为SC码
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            wifiPwd = deviceAddInfo.getSc();
        }
        ConnectivityApi.instance(mView.get().getContextInfo()).addConnectivityCallback(mConnectivityCallback);
        ConnectivityApi.instance(mView.get().getContextInfo()).connectToWifiByNetworkRequest(getHotSSID(), wifiPwd);
    }

    @Override
    public void isDeviceOnline() {
        if(mView.get() == null){
            return;
        }

        mView.get().showProgressDialog();
        LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {

            @Override
            public void handleBusiness(Message msg) {
                if(mView.get()==null||!mView.get().isViewActive()){
                    return;
                }
                mView.get().cancelProgressDialog();
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
                    if (DeviceAddInfo.Status.online.name().equals(deviceAddInfo.getStatus())
                            || DeviceAddInfo.Status.sleep.name().equals(deviceAddInfo.getStatus())
                            || DeviceAddInfo.Status.upgrading.name().equals(deviceAddInfo.getStatus())) {
                        //设备在线/休眠/升级中
                        mView.get().gotoCloudConnectPage();
                    }else{
                        connectHotFailed();
                    }
                }else{

                    connectHotFailed();
                }
            }
        };
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        DeviceAddModel.newInstance().getDeviceInfo(deviceAddInfo.getDeviceSn(), deviceAddInfo.getDeviceCodeModel(), deviceAddInfo.getModelName(), "",getDeviceHandler);
    }

    @Override
    public void finilize(){
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        ConnectivityApi.instance(mView.get().getContextInfo()).removeConnectivityCallback(mConnectivityCallback);
    }
}
