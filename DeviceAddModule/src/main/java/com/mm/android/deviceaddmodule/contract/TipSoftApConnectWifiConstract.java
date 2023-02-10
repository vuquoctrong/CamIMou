package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface TipSoftApConnectWifiConstract {
    interface Presenter extends IBasePresenter {
        void copyWifiPwd();

        /**
         * The system starts to connect the device hotspot
         * @param isFirst Is the first one
         *
         * 开始连接设备热点
         * @param isFirst 是否第一个
         */
        void connectWifiAction(boolean isFirst);

        /**
         * Check whether the current wifi is connected to the hotspot
         *
         * 处理热点连接，判断当前wifi是否已连接至热点
         */
        void dispatchHotConnected();

        /**
         * Obtain the hotspot ssid
         * @return String A string value
         *
         * 获取热点ssid
         * @return String 字符串值
         */
        String getHotSSID();

        /**
         * Hotspot connection mode after Android Q
         *
         * Android Q之后热点连接方式
         */
        void connectToWifiByNetworkRequest();

        /**
         * Obtain device information from a service
         *
         * 从服务获取设备信息
         */
        void isDeviceOnline();
        boolean isSupportAddBySc();
        boolean isWifiEnable();
        void finilize();
    }

    interface View extends IBaseView<Presenter> {
        void updateWifiName(String wifiName);
        void updateConnectFailedTipText(String wifiName, String wifiPwd, boolean isSupportAddBySc, boolean isManualInput);
        void goSecurityCheckPage();
        void gotoCloudConnectPage();
        void openWifiPannel();
    }
}
