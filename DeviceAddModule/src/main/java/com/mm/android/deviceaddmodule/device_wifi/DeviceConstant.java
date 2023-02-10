package com.mm.android.deviceaddmodule.device_wifi;

/**
 * Device management constants
 *
 * 设备管理常量类
 */

public class DeviceConstant {



    /**
     * The key constant of an Intent jump or some constant key values
     *
     * Intent跳转的key常量或者一些常量key值
     */
    public interface IntentKey {
        String DEVICE_CURRENT_WIFI_INFO = "DEVICE_CURRENT_WIFI_INFO";
        String DEVICE_WIFI_CONFIG_INFO = "DEVICE_WIFI_CONFIG_INFO";
        String LCDEVICE_INFO = "LCDEVICE_INFO";
        String LCDEVICE_UNBIND = "LCDEVICE_UNBIND";
        String LCDEVICE_NEW_NAME = "LCDEVICE_NEW_NAME";
        String TIME_SLICES_LIST = "TIME_SLICES_LIST";
        String PERIOD_POS = "PERIOD_POS";


    }

    /**
     * Intent redirect request code return code constant
     *
     * Intent跳转请求码返回码常量
     */
    public interface IntentCode {
        int DEVICE_SETTING_WIFI_OPERATE = 208;
        int DEVICE_SETTING_WIFI_LIST = 209;
        int PERIOD_SETTING = 204;

    }

    public enum PeriodSettingMode {
        common,
        work,
        custom
    }
    public interface PeriodSettingValidity {
        String TIME_VALIDITY_OK = "TIME_VALIDITY_OK";
        String TIME_VALIDITY_END_LESS_THAN_START = "TIME_VALIDITY_END_LESS_THAN_START";//结束时间小于开始时间
        String TIME_VALIDITY_END_TOO_LITTLE = "TIME_VALIDITY_END_TOO_LITTLE";//结束时间与开始时间的差值没有大于限定值
        String TIME_VALIDITY_MAX = "TIME_VALIDITY_MAX";//时间段的条数已到达最大值
    }
}
