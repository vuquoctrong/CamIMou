package com.mm.android.mobilecommon.AppConsume;

import android.content.Context;

import com.mm.android.mobilecommon.base.IBaseProvider;


/**
 * Devices add modules, App needs different implementation of the interface
 *
 * 设备添加模块，App需不同实现的接口
 **/
public interface IDeviceAddCustom extends IBaseProvider {
    void goFAQWebview(Context context);                                         //调整至FAQ
    void goHomePage(Context context);//回到设备列表页
    String getDevAddCachePath();                                     //设备添加文件缓存路径

}
