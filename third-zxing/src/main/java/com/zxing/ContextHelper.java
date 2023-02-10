package com.zxing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import com.zxing.utils.Strings;

public final class ContextHelper {

    private static Application application;
    private static Class splashCls;

    /**
     * Initialize
     * @param application app
     *
     * 初始化
     * @param application app
     */
    public static void init(Application application) {
        if (ContextHelper.application == null) {
            ContextHelper.application = application;
        }
    }

    public static Context getAppContext() {
        if (application != null) {
            return application.getApplicationContext();
        }
        return null;
    }

    public static Application getApp() {
        return application;
    }

    public static Resources getResources() {
        Context context = getAppContext();
        if (context != null) {
            return context.getResources();
        }
        return null;
    }

    public static void setSplashCls(Class cls) {
        ContextHelper.splashCls = cls;
    }

    public static Class getSplashCls() {
        return splashCls;
    }

    /**
     * Resource ID Obtain String
     *
     * 资源ID获取String
     */
    public static String getString(int stringId) {
        if (getAppContext() == null) {
            return Strings.EMPTY;
        }
        return getAppContext().getString(stringId);
    }

    public static String getString(int stringId, Object... formatArgs) {
        if (getAppContext() == null) {
            return Strings.EMPTY;
        }
        return getAppContext().getString(stringId, formatArgs);
    }


    public static int getDimensionPixelSize(int dimenId) {
        try {
            return getResources().getDimensionPixelSize(dimenId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getDimen(int dimenId) {
        try {
            return getResources().getDimensionPixelSize(dimenId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the version number of the application
     *
     * 获取应用的版本号
     */
    public static String getAppVersion() {
        Context context = getAppContext();
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo;
            try {
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                return packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Strings.EMPTY;
    }

    public static boolean isUsable(Context context) {
        if (context == null) {
            return false;
        }

        if (context instanceof Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !((Activity) context).isDestroyed();
        }
        return true;
    }

    public static void startAppSetting(){
        if(getAppContext()!=null) {
            Intent intent = new Intent("android.settings.SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getAppContext().startActivity(intent);
        }
    }

}

