package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;


public interface HiddenWifiPwdConstract {
    interface Presenter extends IBasePresenter {

        void setIsNotNeedLogin(boolean isNotNeedLogin);
        String getCurWifiName();
        void updateWifiCache();
        void connectWifi();
        boolean isDevSupport5G();
    }

    interface View extends IBaseView<Presenter> {
        String getWifiPwd();
        String getWifiSSID();
        void goCloudConnectPage();

    }
}
