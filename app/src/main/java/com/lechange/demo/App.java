package com.lechange.demo;

import android.annotation.SuppressLint;
import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.common.openapi.ClassInstanceManager;
import com.lc.media.utils.MediaPlayHelper;
import com.lc.playback.utils.MediaPlaybackHelper;
import com.lechange.common.log.Logger;
import com.lechange.opensdk.utils.LCOpenSDK_Utils;
import com.mm.android.mobilecommon.utils.ContextHelper;
import com.opensdk.devicedetail.manager.DetailInstanceManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //解决弹框提示  Detected problems with API
        disableAPIDialog();
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.init(this);
        MediaPlayHelper.initContext(getApplicationContext());
        MediaPlaybackHelper.initContext(getApplicationContext());
        ClassInstanceManager.newInstance().init(this);
        DetailInstanceManager.newInstance().init(this);
        ContextHelper.init(this);
        LCOpenSDK_Utils.enableLogPrint(true);
        Logger.setLogLevel(Logger.Verbose, "");
        Logger.setExtendLogLevel(Logger.Verbose);
    }

	private void disableAPIDialog() {
		try {
			Class clazz = Class.forName("android.app.ActivityThread");
			Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
			currentActivityThread.setAccessible(true);
			Object activityThread = currentActivityThread.invoke(null);
			@SuppressLint("SoonBlockedPrivateApi") Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
			mHiddenApiWarningShown.setAccessible(true);
			mHiddenApiWarningShown.setBoolean(activityThread, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}