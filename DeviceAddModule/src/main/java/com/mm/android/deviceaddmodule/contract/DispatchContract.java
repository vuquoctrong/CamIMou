package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface DispatchContract {
    interface Presenter extends IBasePresenter {
        /**
         * Enter the device SN manually or not
         * @return boolean Boolean value
         *
         * 是否为手动输入设备序列号页面
         * @return boolean 布尔值
         */
        boolean isManualInputPage();
        boolean isSnInValid(String sn);
        boolean isScCodeInValid(String scCode);
        void dispatchResult();
    }

    interface View extends IBaseView<ScanContract.Presenter> {
        /**
         * The device type selection page is displayed
         *
         * 跳转到设备类型选择页
         */
        void goTypeChoosePage();

        /**
         * The page for devices that do not support binding is displayed
         *
         * 跳转到不支持绑定的设备页面
         */
        void goNotSupportBindTipPage();

        /**
         * The page indicating that the device is bound to another user is displayed
         *
         * 跳转至设备被其他用户绑定提示页
         */
        void goOtherUserBindTipPage();

        /**
         * Box addition Tips
         *
         * 盒子添加提示
         */
        void showAddBoxTip();

        /**
         * Go to the Cloud Platform Connections page
         *
         * 跳转至云平台连接页
         */
        void goCloudConnectPage();

        /**
         * The device login page is displayed
         *
         * 跳转至设备登录页
         */
        void goDeviceLoginPage();

        /**
         * The security code verification page is displayed
         *
         * 跳转至安全码验证页
         */
        void goSecCodePage();

        /**
         * The device binding page is displayed
         *
         * 跳转至设备绑定页
         */
        void goDeviceBindPage();

        /**
         * The input iMei page is displayed
         *
         * 跳转至输入iMei页
         */
        void goIMEIInputPage();
    }
}
