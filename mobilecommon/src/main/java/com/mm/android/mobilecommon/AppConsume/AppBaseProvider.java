package com.mm.android.mobilecommon.AppConsume;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;

import com.mm.android.mobilecommon.AppConsume.IApp;
import com.mm.android.mobilecommon.base.BaseProvider;
//import com.zxing.ContextHelper;

import java.util.Locale;

public class AppBaseProvider extends BaseProvider implements IApp {

    private static int appType = 0;

    @Override
    public void setAppType(int type) {
        appType = type;
    }

    @Override
    public int getAppType() {
        return appType;
    }

    @Override
    public Context getAppContext() {
        return null;
    }

    @Override
    public void goDeviceSharePage(Activity activity, Bundle bundle) {

    }

    @Override
    public void goBuyCloudPage(Activity activity, Bundle bundle) {

    }

    @Override
    public void goModifyDevicePwdGuidePage(Activity activity) {

    }

    @Override
    public String getAppLanguage() {
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (LocaleList.getDefault().size() > 0) {
                locale = LocaleList.getDefault().get(0);
            }

        } else {
//            locale = ContextHelper.getResources().getConfiguration().locale;
        }
        return locale != null ? locale.getLanguage() + "_" + locale.getCountry() : "en_US";
    }
}
