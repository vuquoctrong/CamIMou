package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.lechange.opensdk.searchwifi.WlanInfo;

public interface SoftApWifiPwdConstract {
    interface Presenter extends IBasePresenter {
        void setWlanInfo(WlanInfo wlanInfo);
        void setIsNotNeedLogin(boolean isNotNeedLogin);
        boolean isDevSupport5G();
        String getCurWifiName();
        void updateWifiCache();
        String getSavedWifiPwd();
        void connectWifi(String wifiName,String wifiPwd,String ip);
        boolean getSavedWifiCheckBoxStatus();
    }

    interface View extends IBaseView<Presenter> {
        String getWifiPwd();
        boolean isSavePwdChecked();
        void goCloudConnectPage();
    }
}
