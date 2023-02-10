package com.mm.android.deviceaddmodule.device_wifi;

import com.mm.android.mobilecommon.base.mvp.IBasePresenter;
import com.mm.android.mobilecommon.base.mvp.IBaseView;

public interface DeviceWifiPasswordConstract {
    interface Presenter extends IBasePresenter {
        void wifiOperate();
        String getWifiSSID();
        void updateWifiCache();
        String getSavedWifiPassword();
        boolean getSavedWifiCheckBoxStatus();
        boolean getSupport5G();
    }

    interface View extends IBaseView {
        String getWifiPassword();
        boolean isSavePwdChecked();
        void onWifiOperateSucceed(CurWifiInfo curWifiInfo);
    }
}
