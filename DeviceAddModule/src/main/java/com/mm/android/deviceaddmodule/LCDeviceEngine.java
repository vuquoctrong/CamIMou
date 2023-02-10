package com.mm.android.deviceaddmodule;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lechange.opensdk.api.InitParams;
import com.lechange.opensdk.api.LCOpenSDK_Api;
import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.mm.android.deviceaddmodule.device_wifi.CurWifiInfo;
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant;
import com.mm.android.deviceaddmodule.device_wifi.DeviceWifiListActivity;
import com.mm.android.mobilecommon.common.CommonParam;
import com.mm.android.mobilecommon.entity.device.LCDevice;
import com.mm.android.mobilecommon.openapi.CONST;
import com.mm.android.mobilecommon.openapi.TokenHelper;
//import com.zxing.ContextHelper;

import static com.mm.android.deviceaddmodule.device_wifi.DeviceConstant.IntentCode.DEVICE_SETTING_WIFI_LIST;

/**
 * Add a unique entry for a device component
 *
 * 添加设备组件 唯一入口
 */
public class LCDeviceEngine {
	private boolean sdkHasInit = false;
	private volatile static LCDeviceEngine lcDeviceEngine;


	public static LCDeviceEngine newInstance() {
		if (lcDeviceEngine == null) {
			synchronized (LCDeviceEngine.class) {
				if (lcDeviceEngine == null) {
					lcDeviceEngine = new LCDeviceEngine();
				}
			}
		}
		return lcDeviceEngine;
	}

	public boolean init(CommonParam commonParam) throws Throwable {
		if (commonParam == null) {
			throw new Exception("commonParam must not null");
		}
		TokenHelper.getInstance().init(commonParam);

		return true;
	}

	public void initP2P(Application context) throws Throwable {
		this.sdkHasInit = false;
		//组件初始化
		InitParams initParams = new InitParams(context, CONST.HOST.replace("https://", ""), TokenHelper.getInstance().subAccessToken);
		LCOpenSDK_Api.initOpenApi(initParams);
		LCOpenSDK_DeviceInit.getInstance();
		sdkHasInit = true;
	}

	public void addDevice(Activity activity) throws Exception {
		if (!sdkHasInit) {
			throw new Exception("not init");
		}
		//开启添加页面
		activity.startActivity(new Intent(activity.getApplicationContext(), DeviceAddActivity.class));
	}

	public boolean deviceOnlineChangeNet(Activity activity, LCDevice device, CurWifiInfo wifiInfo) {
		if (!sdkHasInit) {
			return false;
		}
		//开启设备在线配网
		Intent intent = new Intent(activity.getApplicationContext(), DeviceWifiListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(DeviceConstant.IntentKey.LCDEVICE_INFO, device);
		bundle.putSerializable(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO, wifiInfo);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, DEVICE_SETTING_WIFI_LIST);
		return true;
	}



}
