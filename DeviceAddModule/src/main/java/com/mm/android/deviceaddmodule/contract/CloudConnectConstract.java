package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface CloudConnectConstract {
    interface Presenter extends IBasePresenter {
        void bindDevice();
        void getDeviceInfo();
        void recycle();

        /**
         * Indicates whether the configuration mode is offline
         *
         * 是否为离线配置模式
         */
        boolean isWifiOfflineConfiMode();
        void notifyMiddleTimeUp();

        /**
         * Start Connecting to the cloud
         *
         * 开始连接云
         */
        void startConnectTiming();

        /**
         * End connections
         *
         * 结束连接
         */
        void stopConnectTiming();
    }


    interface View extends IBaseView<DeviceAddConstract.Presenter> {
        /**
         * Binding success page
         *
         * 绑定成功页
         */
        void goBindSuccessPage();

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

        /**
         * Error message page
         *
         * 错误提示页
         */
        void goErrorTipPage();

        /**
         * Error message page
         * @param errorCode Error Code
         *
         * 错误提示页
         * @param errorCode  错误码
         */
        void goErrorTipPage(int errorCode);

        /**
         * Binding Device page
         *
         * 绑定设备页
         */
        void goBindDevicePage();

        /**
         * End of the process
         *
         * 流程结束
         */
        void completeAction();

        /**
         * Set the countdown time
         * @param time  Time of countdown
         *
         * 设置倒计时时间
         * @param time  倒计时时间
         */
        void setCountDownTime(int time);

        /**
         * Set the intermediate time
         * @param time  In the middle of time
         *
         * 设置中间时间
         * @param time  中间时间
         */
        void setMiddleTime(int time);

        /**
         * Note page that the device is bound to others
         *
         * 设备被他人绑定提示页
         */
        void goOtherUserBindTipPage();

        /**
         * The device cannot be bound
         *
         * 设备不支持被绑定
         */
        void goNotSupportBuindTipPage();
    }
}
