package com.mm.android.mobilecommon.AppConsume;

import android.app.Activity;
import android.content.Context;

import com.mm.android.mobilecommon.helper.ActivityHelper;
import com.mm.android.mobilecommon.utils.SDsolutionUtility;


public class DeviceAddCustomImpl implements IDeviceAddCustom {

    private volatile static DeviceAddCustomImpl deviceAddCustom;

    public static DeviceAddCustomImpl newInstance() {
        if (deviceAddCustom == null) {
            synchronized (DeviceAddCustomImpl.class) {
                if (deviceAddCustom == null) {
                    deviceAddCustom = new DeviceAddCustomImpl();
                }
            }
        }
        return deviceAddCustom;
    }

    @Override
    public void goFAQWebview(Context context) {
        //TODO 跳转到问题反馈页面
    }

    @Override
    public void goHomePage(Context context) {
        Activity activity= ActivityHelper.getCurrentActivity();
        if (activity!=null){
            activity.finish();
        }
    }

    @Override
    public String getDevAddCachePath() {
        return SDsolutionUtility.getCachePath();
    }

    @Override
    public void uninit() {

    }
}
