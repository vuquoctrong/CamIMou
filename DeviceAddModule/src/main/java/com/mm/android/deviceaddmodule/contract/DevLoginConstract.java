package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface DevLoginConstract {
    interface Presenter extends IBasePresenter {
        void devLogin();
    }

    interface View extends IBaseView<Presenter> {
        /**
         * Obtain the device password from the input box
         * @return String String type
         *
         * 从输入框中获取设备密码
         * @return String 字符串类型
         */
        String getDevicePassword();

        /**
         * Soft AP adds wifi selection page
         *
         * 软AP添加wifi选择页
         */
        void goSoftAPWifiListPage();

        /**
         * The device binding page is displayed
         *
         * 进入设备绑定页
         */
        void goDeviceBindPage();

        /**
         * The binding success page is displayed
         *
         * 进入绑定成功页
         */
        void goBindSuceesPage();

        /**
         * The prompt page for binding other users is displayed
         *
         * 进入其他用户绑定提示页
         */
        void goOtherUserBindTipPage();

        /**
         * Error message page
         * @param errorCode  Error code
         *
         * 错误提示页
         * @param errorCode  错误码
         */
        void goErrorTipPage(int errorCode);

        /**
         * Complete exit
         *
         * 完成退出
         */
        void completeAction();

        /**
         * Device Login Page
         *
         * 设备登录页
         */
        void goDevLoginPage();

        /**
         * Device security code page
         *
         * 设备安全码页
         */
        void goDevSecCodePage();


    }
}
