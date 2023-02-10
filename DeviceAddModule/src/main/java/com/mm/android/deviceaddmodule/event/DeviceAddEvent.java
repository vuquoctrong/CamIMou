package com.mm.android.deviceaddmodule.event;

import android.os.Bundle;

import com.mm.android.mobilecommon.eventbus.event.BaseEvent;

/**
 * Device Adds a message communication class
 *
 * 设备添加消息通信类
 **/
public class DeviceAddEvent extends BaseEvent {
    //EVENT ACTION
    public static String TITLE_MODE_ACTION="title_mode_action";                                             //标题栏模式控制
    public static String GET_DEVICE_SN = "get_device_sn";                                                //获取到设备SN的通知
    public static String CONFIG_PAGE_NAVIGATION_ACTION="config_page_navigation_action";                 //添加流程页跳转通知
    public static String SOFTAP_REFRSH_WIFI_LIST="softap_refresh_wifi_list";                            //软AP刷新wifi列表
    public static String SOFTAP_REFRSH_WIFI_LIST_DISABLE_ACTION ="softap_refresh_wifi_list_disable_action";           //软AP刷新wifi列表按钮置灰
    public static String SOFTAP_REFRSH_WIFI_LIST_ENABLE_ACTION ="softap_refresh_wifi_list_enable_action";             //软AP刷新wifi列表按钮高亮
    public static String SHOW_LOADING_VIEW_ACTION="show_loading_view_action";                            //显示加载框
    public static String DISMISS_LOADING_VIEW_ACTION="dismiss_loading_view_action";                     //隐藏加载框
    public static String CHANGE_TO_WIRELESS_ACTION="change_to_wireless_action";                          //切换到无线
    public static String CHANGE_TO_WIRED_ACTION="change_to_wired_action";                                //切换到有线
    public static String CHANGE_TO_SOFTAP_ACTION="change_to_softap_action";                              //切换到软AP
    public static String SHOW_TYPE_CHOSE_ACTION="show_type_chose_action";                              //切换到软AP
    public static String REFRESH_BATTERY_CAMERA_LIST = "refresh_battery_camera_list";                    //更新电池相机列表
    public static String DESTROY_ACTION = "destroy_action";                                                //销毁设备添加使用的缓存数据
    public static String OFFLINE_CONFIG_SUCCESS_ACTION = "offline_config_success";                         //离线配网成功

    //EVENT KEY
    public interface KEY{
        String TITLE_MODE="title_mode";                              //标题栏模式
    }

    Bundle bundle;
    public DeviceAddEvent(String code) {
        super(code);
    }

    public DeviceAddEvent(String code,Bundle bundle){
        super(code);
        this.bundle=bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
