package com.mm.android.deviceaddmodule.contract;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface TipWifiConnectConstract {
    interface Presenter extends IBasePresenter {
        void searchDevice();
        void stopSearchDevice();
        String getConfigMode();
    }

    interface View extends IBaseView<Presenter> {
        /**
         * The device initialization page is displayed
         * @param device_net_info_ex  Device Network Information
         *
         * 进入设备初始化页
         * @param device_net_info_ex  设备网络信息
         */
        void goDevInitPage(DeviceInitInfo device_net_info_ex);

        /**
         * Enter the configuration page
         *
         * 进入配网页
         */
        void goWifiConfigPage();

        /**
         * The cloud connection page is displayed
         *
         * 进入云连接页
         */
        void goCloudConnectPage();
    }
}
