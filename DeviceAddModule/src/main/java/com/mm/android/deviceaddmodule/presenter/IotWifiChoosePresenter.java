package com.mm.android.deviceaddmodule.presenter;


import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.contract.IotWifiChooseConstract;
import com.mm.android.mobilecommon.common.LCConfiguration;

import java.lang.ref.WeakReference;

public class IotWifiChoosePresenter implements IotWifiChooseConstract.Presenter {
    WeakReference<IotWifiChooseConstract.View> mView;
    DHWifiUtil mDHWifiUtil;

    public IotWifiChoosePresenter(IotWifiChooseConstract.View view) {
        mView = new WeakReference<>(view);
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
    }

    public String getCurWifiName() {
        String curWifiName = mDHWifiUtil.getCurrentWifiInfo().getSSID().replaceAll("\"", "");
        if(LCConfiguration.UNKNOWN_SSID.equals(curWifiName)){
            curWifiName = "";
        }
        return curWifiName;
    }


}
