package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.text.TextUtils;


public class LCUtils {

    private static long mLastClickTime;
    private static final String DEBUG_STATE = "DEBUG_STATE";

    /**
     * Check for repeated click events. The default time is 800 milliseconds
     * @return boolean  Boolean value
     *
     * 检测是否重复点击事件,默认时间为800毫秒
     * @return boolean  布尔值
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < 1200) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    public static boolean isDebug(Context context){
        return  PreferencesHelper.getInstance(context).getBoolean(DEBUG_STATE, false);
    }

    public static void setDebug(Context context,boolean debug){
        PreferencesHelper.getInstance(context).set(DEBUG_STATE, debug);
    }

}
