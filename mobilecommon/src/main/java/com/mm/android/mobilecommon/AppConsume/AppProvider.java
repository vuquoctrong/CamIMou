package com.mm.android.mobilecommon.AppConsume;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class AppProvider extends AppBaseProvider implements IApp {

    private volatile static AppProvider appProvider;

    public static AppProvider newInstance() {
        if (appProvider == null) {
            synchronized (AppProvider.class) {
                if (appProvider == null) {
                    appProvider = new AppProvider();
                }
            }
        }
        return appProvider;
    }

    @Override
    public void uninit() {

    }

    @Override
    public void setAppType(int type) {
        super.setAppType(type);
    }

    @Override
    public int getAppType() {
        return super.getAppType();
    }

    @Override
    public void goDeviceSharePage(Activity activity, Bundle bundle) {
        super.goDeviceSharePage(activity, bundle);
    }

    @Override
    public void goBuyCloudPage(Activity activity, Bundle bundle) {
        super.goBuyCloudPage(activity, bundle);
    }

    @Override
    public void goModifyDevicePwdGuidePage(Activity activity) {
        super.goModifyDevicePwdGuidePage(activity);
    }

    @Override
    public String getAppLanguage() {
        return super.getAppLanguage();
    }

    @Override
    public Context getAppContext() {
        return EnvironmentConfig.mContext;
    }

}
