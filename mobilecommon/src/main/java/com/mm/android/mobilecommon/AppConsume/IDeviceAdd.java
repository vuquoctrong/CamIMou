package com.mm.android.mobilecommon.AppConsume;

import com.mm.android.mobilecommon.base.BaseHandler;
import com.mm.android.mobilecommon.base.IBaseProvider;

/**
 * External interfaces are added to the device, and functions can be invoked by external modules
 *
 * 设备添加对外接口，外部模块可调用的功能
 */
public interface IDeviceAdd extends IBaseProvider {
    void stopSearchDevicesAsync(long ret, BaseHandler handler);      //异步停止设备搜索
    boolean stopSearchDevices(long ret, String requestId);          //同步停止设备搜索
}
