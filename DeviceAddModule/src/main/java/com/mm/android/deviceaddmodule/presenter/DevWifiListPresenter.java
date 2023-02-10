package com.mm.android.deviceaddmodule.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.opensdk.searchwifi.LCOpenSDK_SearchWiFi;
import com.lechange.opensdk.softap.LCOpenSDK_SoftAPConfig;
import com.mm.android.deviceaddmodule.contract.DevWifiListConstract;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DevWifiListPresenter implements DevWifiListConstract.Presenter {
    WeakReference<DevWifiListConstract.View> mView;
    private List<WlanInfo> mListData;
    DHWifiUtil mDHWifiUtil;
    String mDeviceSn;
    private boolean mIsNotNeedLogin;
    private boolean isSupport5G;

    public DevWifiListPresenter(DevWifiListConstract.View view, boolean isNotNeedLogin) {
        mView = new WeakReference<>(view);
        mDHWifiUtil=new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
        mDeviceSn=DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        mListData=new ArrayList<>();
        mIsNotNeedLogin = isNotNeedLogin;
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
    public void getWifiList(){
//        if (LCUtils.isFastDoubleClick()) {
//            return;
//        }
        mView.get().showProgressDialog();
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST_DISABLE_ACTION));
        mListData.clear();
        String gatwayip= mDHWifiUtil.getGatewayIp();
        final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        String pwd = deviceAddInfo.getDevicePwd();

        LCBusinessHandler getWifiListHandler=new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if(mView.get()!=null){
                    mView.get().cancelProgressDialog();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST_ENABLE_ACTION));
                        }
                    }, 500);

                    if(msg.what== HandleMessageCode.HMC_SUCCESS){
                        dispatchListResult(msg);
                    } else if(msg.what== HandleMessageCode.HMC_BATCH_MIDDLE_RESULT) { // 登陆失败
                            mView.get().goDevLoginPage();
                    } else {
                        mView.get().showErrorInfoView();
                    }
                }
            }
        };

        if(mIsNotNeedLogin) {
            LogUtil.debugLog("LCOpenSDK_SearchWiFi","wifiList::"+mIsNotNeedLogin);
            LCOpenSDK_SoftAPConfig.getSoftApWifiList(gatwayip,"",true,getWifiListHandler);//sc码设备
        } else {
            LCOpenSDK_SoftAPConfig.getSoftApWifiList(gatwayip,pwd,false,getWifiListHandler);
        }
    }

    private void dispatchListResult(Message msg){
        mView.get().showListView();
        mListData= (List<WlanInfo>) msg.obj;
        mView.get().updateWifiList(mListData);
    }
}
