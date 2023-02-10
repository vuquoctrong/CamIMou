package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.opensdk.searchwifi.LCOpenSDK_SearchWiFi;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.HiddenWifiPwdConstract;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class HiddenWifiPresenter implements HiddenWifiPwdConstract.Presenter {

    WeakReference<HiddenWifiPwdConstract.View> mView;
    DHWifiUtil mDHWifiUtil;
    WlanInfo mCurWlanInfo;
    protected String mDeviceId;
    private boolean mIsNotNeedLogin;
    protected boolean isSupport5G = false;

    public HiddenWifiPresenter(HiddenWifiPwdConstract.View view) {
        mView = new WeakReference<>(view);
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
        mCurWlanInfo = new WlanInfo();

        String wifiMode = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiTransferMode();
        if (!TextUtils.isEmpty(wifiMode)) {
            isSupport5G = wifiMode.toUpperCase().contains("5GHZ");
        }
    }

    @Override
    public boolean isDevSupport5G() {
        return isSupport5G;
    }


    @Override
    public void setIsNotNeedLogin(boolean isNotNeedLogin){
        mIsNotNeedLogin = isNotNeedLogin;
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

    }


    @Override
    public void connectWifi() {
        mCurWlanInfo.setWlanSSID(mView.get().getWifiSSID());
        mView.get().showProgressDialog();
        updateWifiCache();//更新wifi信息到缓存
        String gatwayip = mDHWifiUtil.getGatewayIp();
        final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        String pwd = deviceAddInfo.getDevicePwd();
        String wifiPwd = deviceAddInfo.getWifiInfo().getPwd();

        LCBusinessHandler connectWifiHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() != null && mView.get().isViewActive()) {
                    mView.get().cancelProgressDialog();
                    if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                        dispatchConnectResult(msg);
                    }
                }  else {
                    mView.get().showToastInfo(R.string.device_disturb_state_failed);
                }
            }
        };

        if(mIsNotNeedLogin) {
            mCurWlanInfo.setWlanEncry(12);
            LCOpenSDK_SearchWiFi.connectWifi(gatwayip,"",mCurWlanInfo,wifiPwd,connectWifiHandler);
        } else {
            LCOpenSDK_SearchWiFi.connectWifi4Hidden(gatwayip,pwd,mCurWlanInfo,wifiPwd,connectWifiHandler);
        }
    }

    private void dispatchConnectResult(Message message) {
        mView.get().cancelProgressDialog();
        //恢复网络
        DeviceAddHelper.clearNetWork();
        DeviceAddHelper.connectPreviousWifi();
        mView.get().goCloudConnectPage();

    }

}
