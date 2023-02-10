package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.opensdk.softap.LCOpenSDK_SoftAPConfig;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.SoftApWifiPwdConstract;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import java.lang.ref.WeakReference;

public class SoftApWifiPwdPresenter implements SoftApWifiPwdConstract.Presenter {
    WeakReference<SoftApWifiPwdConstract.View> mView;
    WlanInfo mCurWlanInfo;
    public String WIFI_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_ADD_";
    private String WIFI_CHECKBOX_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_CHECKBOX_ADD_";
    DHWifiUtil mDHWifiUtil;
    private boolean mIsNotNeedLogin;
    private boolean isSupport5G;
    public String TAG = "SoftApWifiPwdPresenter";

    public SoftApWifiPwdPresenter(SoftApWifiPwdConstract.View view) {
        mView = new WeakReference<>(view);
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
        String wifiMode = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiTransferMode();
        if (!TextUtils.isEmpty(wifiMode)) {
            isSupport5G = wifiMode.toUpperCase().contains("5GHZ");
        }
    }

    @Override
    public void setWlanInfo(WlanInfo wlanInfo) {
        mCurWlanInfo = wlanInfo;
    }

    @Override
    public void setIsNotNeedLogin(boolean isNotNeedLogin){
        mIsNotNeedLogin = isNotNeedLogin;
    }

    @Override
    public boolean isDevSupport5G() {
        return isSupport5G;
    }

    @Override
    public String getCurWifiName() {
        return mCurWlanInfo.getWlanSSID();
    }

    @Override
    public void updateWifiCache() {
        DeviceAddInfo.WifiInfo wifiInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiInfo();
        wifiInfo.setSsid(getCurWifiName());
        wifiInfo.setPwd(mView.get().getWifiPwd());
        if (mView.get().isSavePwdChecked()) {
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + getCurWifiName(), mView.get().getWifiPwd());
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName(), true);
        } else {
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + getCurWifiName(), "");
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName(), false);
        }
    }

    @Override
    public String getSavedWifiPwd() {
        return PreferencesHelper.getInstance(mView.get().getContextInfo()).getString(WIFI_SAVE_PREFIX + getCurWifiName());
    }

    @Override
    public boolean getSavedWifiCheckBoxStatus() {
        return PreferencesHelper.getInstance(mView.get().getContextInfo()).getBoolean(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName());
    }

    @Override
    public void connectWifi(String wifiName,String wifiPwd,String ip) {
        mView.get().showProgressDialog();
        updateWifiCache();//更新wifi信息到缓存

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        LCBusinessHandler connectWifiHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() != null && mView.get().isViewActive()) {
                    mView.get().cancelProgressDialog();
                    if (msg.arg1 == 0) {
                        dispatchConnectResult(msg);
                    }
                } else {
                    mView.get().showToastInfo(R.string.device_disturb_state_failed);
                }
            }
        };


        LogUtil.debugLog(TAG,"wifiName::::"+wifiName+"wifiPwd::::"+wifiPwd+"deviceSn::::"+deviceAddInfo.getDeviceSn()+
                "devicePwd::::"+deviceAddInfo.getDevicePwd()+"deviceSc::"+deviceAddInfo.getSc());
        LCOpenSDK_SoftAPConfig.startSoftAPConfig(mCurWlanInfo.getWlanSSID(), wifiPwd,mCurWlanInfo.getWlanEncry(),ip,
                deviceAddInfo.getDevicePwd(),deviceAddInfo.hasSc(),connectWifiHandler,30*1000);
    }

    private void dispatchConnectResult(Message message) {
        mView.get().cancelProgressDialog();
        //恢复网络
        DeviceAddHelper.clearNetWork();
        DeviceAddHelper.connectPreviousWifi();
        boolean connectResult = (boolean) (message.arg1==0?true:false);
        mView.get().goCloudConnectPage();
    }
}
