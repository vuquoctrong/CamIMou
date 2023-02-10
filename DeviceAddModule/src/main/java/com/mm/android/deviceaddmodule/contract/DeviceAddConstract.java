package com.mm.android.deviceaddmodule.contract;

import android.content.Intent;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface DeviceAddConstract {
    interface Presenter extends IBasePresenter {
        String getCurTitleMode();

        void setCurTitleMode(String titleMode);

        void dispatchIntentData(Intent intent);

        /**
         * Access to gps information
         *
         * 获取gps信息
         */
        void getGPSLocation();

        /**
         * Add a process page jump
         *
         * 添加流程页跳转
         */
        void dispatchPageNavigation();

        /**
         * Releasing Related Resources
         *
         * 释放相关资源
         */
        void unInit();

        void getDeviceShareInfo();

        boolean canBeShare();

        void changeToWireless();

        void changeToWired();

        void changeToSoftAp();

        void startSearchService();

    }

    interface View extends IBaseView<Presenter> {
        /**
         * Set the title
         * @param titleId  Title Id
         *
         *
         * 设置标题
         * @param titleId  标题Id
         */
        void setTitle(int titleId);

        /**
         * Scan the pages
         *
         * 扫描页
         */
        void goScanPage();

        /**
         * Distribution of page
         *
         * 分发页
         */
        void goDispatchPage();

        /**
         * hub Battery Camera guide page
         * @param sn  The serial number
         * @param hubType  Guide page type
         *
         * hub电池相机引导页
         * @param sn  序列号
         * @param hubType  引导页类型
         */
        void goHubPairPage(String sn, String hubType);

        /**
         * Go to the accessory Add page
         * @param hasSelectGateway  The gateway has been selected
         *
         * 跳转至配件添加页
         * @param hasSelectGateway  已选择门路
         */
        void goApConfigPage(boolean hasSelectGateway);

        /**
         * Jump to Wired/Wireless Add
         * @param isWifi  Is wireless add
         *
         * 跳转至有线/无线添加
         * @param isWifi  是否无线添加
         */
        void goWiredWirelessPage(boolean isWifi);

        /**
         * Jump to Wired/Wireless Add
         * @param isWifi  Is wireless add
         *
         * 跳转至有线/无线添加
         * @param isWifi  是否无线添加
         */
        void goWiredWirelessPageNoAnim(boolean isWifi);

        /**
         * Go to Soft AP Add
         *
         * 跳转至软AP添加
         */
        void goSoftApPage();

        /**
         * Go to Soft AP Add
         *
         * 跳转至软AP添加
         */
        void goSoftApPageNoAnim();

        /**
         * Go to the offline configuration page
         * @param sn  The serial number
         * @param devModelName  Device Module Name
         * @param iMei  iMei
         *
         * 跳转到离线配网页面
         * @param sn  序列号
         * @param devModelName  设备模块名
         * @param iMei  iMei
         */
        void goOfflineConfigPage(String sn, String devModelName, String iMei);


        /**
         * The initialization page is displayed
         * @param device_net_info_ex  Device Network Information
         *
         * 跳转至初始化界面
         * @param device_net_info_ex  设备网络信息
         */
        void goInitPage(DeviceInitInfo device_net_info_ex);

        /**
         * The Connect cloud platform page is displayed
         *
         * 跳转至连接云平台界面
         */
        void goCloudConnectPage();

        void goTypeChoosePage();

        void completeAction(boolean isAp);

        /**
         * The input iMei page is displayed
         *
         * 跳转至输入iMei页
         */
        void goIMEIInputPage();

        /**
         * The page for devices that do not support binding is displayed
         *
         * 跳转到不支持绑定的设备页面
         */
        void goNotSupportBindTipPage();
    }
}
