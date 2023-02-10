package com.mm.android.mobilecommon.eventbus.event;

import android.os.Bundle;

/**
 * Cross-module general event
 *
 * 跨模块通用事件
 **/
public class CommonEvent extends BaseEvent{
    Bundle bundle;
    public static String REFRESH_SINGLE_DEVICE_SYNC_ACTION="refresh_single_device_sync_action";                           //刷新单个设备,从服务

    public static String AP_PAIR_SUCCEED_2_MAIN_ACTION = "ap_pair_succeed_2_main_action";                             //添加配件完成(回主页)
    public static String AP_PAIR_SUCCEED_2_MID_ACTION = "ap_pair_succeed_2_mid_action";                             //添加配件完成(回中间页)
    public static String DEVICE_ADD_SUCCESS_ACTION = "device_add_success_action";                                   //设备添加成功


    public CommonEvent(String code) {
        super(code);
    }

    public CommonEvent(String code,Bundle bundle) {
        super(code);
        this.bundle = bundle;
    }
    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
