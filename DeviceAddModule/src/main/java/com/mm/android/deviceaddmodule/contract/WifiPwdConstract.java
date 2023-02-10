package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface WifiPwdConstract {
    interface Presenter extends IBasePresenter {
        boolean isDevSupport5G();

        String getCurWifiName();

        void updateWifiCache();

        String getSavedWifiPwd();

        boolean getSavedWifiCheckBoxStatus();

        String getConfigMode();
    }

    interface View extends IBaseView<Presenter> {
        String getWifiPwd();

        boolean isSavePwdChecked();
    }
}
