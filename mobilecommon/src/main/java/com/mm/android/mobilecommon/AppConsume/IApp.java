package com.mm.android.mobilecommon.AppConsume;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mm.android.mobilecommon.base.IBaseProvider;

/**
 * App general interface class
 *
 * App通用接口类
 */

public interface IApp extends IBaseProvider {
    /*设备App使用类型，0表示国内，1表示海外*/
    public void setAppType(int type);

    public int getAppType();              //APP类型(0:乐橙/1:Easy4ip)

    public Context getAppContext();              //获取App上下文


    public void goDeviceSharePage(Activity activity , Bundle bundle);        //进入设备分享页

    void goBuyCloudPage(Activity activity , Bundle bundle);

    //跳转修改设备密码引导页
    void goModifyDevicePwdGuidePage(Activity activity);

    String getAppLanguage();
}
