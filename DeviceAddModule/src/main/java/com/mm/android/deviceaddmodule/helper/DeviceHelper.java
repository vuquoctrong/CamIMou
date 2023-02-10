package com.mm.android.deviceaddmodule.helper;

import android.text.TextUtils;

/**
 * Device module help class
 *
 * 设备模块帮助类
 **/
public class DeviceHelper {
    public static boolean isH1G(String deviceModelName){
        return !TextUtils.isEmpty(deviceModelName) && "H1G".equalsIgnoreCase(deviceModelName);
    }
}
