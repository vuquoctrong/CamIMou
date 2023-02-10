package com.mm.android.deviceaddmodule.device_wifi;

import com.mm.android.mobilecommon.base.mvp.IBasePresenter;
import com.mm.android.mobilecommon.base.mvp.IBaseView;
import com.mm.android.mobilecommon.entity.device.LCDevice;

import java.util.List;

public interface DeviceWifiListConstract {
    interface Presenter extends IBasePresenter {
        void getDeviceWifiListAsync();
        WifiInfo getWifiInfo(int position);
        CurWifiInfo getCurWifiInfo();
        boolean isSupport5G(String wifiMode);
        LCDevice getLCDevice();
    }

    interface View extends IBaseView {
        void refreshListView(List<WifiInfo> wlanInfos);
        void onLoadSucceed(boolean isEmpty, boolean isError);
        void updateCurWifiLayout(CurWifiInfo curWifiInfo);
        void viewFinish();
    }
}
