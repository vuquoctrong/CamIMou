package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


public class AppUtils {

    public static synchronized int getTargetSdkVersion(Context context) {
        int version = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            version = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

}
