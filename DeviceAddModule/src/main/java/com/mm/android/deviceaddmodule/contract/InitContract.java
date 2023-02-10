package com.mm.android.deviceaddmodule.contract;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface InitContract {
    interface Presenter extends IBasePresenter {
        void setDeviceEX(DeviceInitInfo deviceEX);

        /**
         * Play prompt audio
         *
         * 播放提示音频
         */
        void playTipSound();

        /**
         * The device unicast was initialized. Procedure
         *
         * 设备单播初始化
         */
        void startDevInitByIp();

        /**
         * The device multicast was initialized. Procedure
         *
         * 设备组播初始化
         */
        void startDevInit();

        /**
         * Whether the password is valid
         * @return boolean Boolean value
         *
         * 密码是否有效
         * @return boolean 布尔值
         */
        boolean isPwdValid();
        void checkDevice();
        void recycle();
    }

    interface View extends IBaseView<Presenter> {
        /**
         * Obtaining Audio Resources
         * @return int
         *
         * 获取音频资源
         * @return int
         */
        int getMusicRes();

        /**
         * Obtaining the Device Password
         * @return String String Value
         *
         * 获取设备密码
         * @return String 字符串
         */
        String getInitPwd();

        /**
         * Soft AP adds wifi selection page
         *
         * 软AP添加wifi选择页
         */
        void goSoftAPWifiListPage();

        /**
         * The Connect Cloud platform page is displayed
         *
         * 进入连接云平台页
         */
        void goConnectCloudPage();
        void goErrorTipPage();
    }
}
