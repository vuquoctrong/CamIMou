package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface DevSecCodeConstract {
    interface Presenter extends IBasePresenter {
        void validate();
    }

    interface View extends IBaseView<Presenter> {
        /**
         * Obtain the device security code from the input box
         * @return String string
         *
         * 从输入框中获取设备安全码
         * @return String 字符串
         */
        String getDeviceSecCode();

        /**
         * Error message page
         * @param errorCode  Error code
         *
         * 错误提示页
         * @param errorCode  错误码
         */
        void goErrorTipPage(int errorCode);

        /**
         * The binding success page is displayed
         *
         * 进入绑定成功页
         */
        void goBindSuceesPage();

        void goOtherUserBindTipPage();

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
