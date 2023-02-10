package com.mm.android.deviceaddmodule.contract;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface SecurityCheckConstract {
    interface Presenter extends IBasePresenter {
        void checkDevice();
        void recyle();
    }

    interface View extends IBaseView<Presenter> {
        void goInitPage(DeviceInitInfo device_net_info_ex);
        void goErrorTipPage();

        /**
         * Device Login Page
         *
         * 设备登录页
         */
        void goDevLoginPage();
        void goSoftApWifiListPage(boolean isNotNeedLogin);
        void goCloudConnectPage();
    }
}
