package com.mm.android.deviceaddmodule;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.lechange.opensdk.device.LCOpenSDK_SearchDevices;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.WifiUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.util.concurrent.CopyOnWriteArrayList;

public class SeachDeviceService extends Service {
	public static final String TAG = "SeachDeviceService";
	private CopyOnWriteArrayList<SearchDeviceManager.ISearchDeviceListener> mListenerList;
	private DeviceInitInfo deviceInitInfo;
	private BroadcastReceiver mReceiver;
	private WifiUtil mWifiUtil;


	@Override
	public void onCreate() {
		super.onCreate();
		mWifiUtil = new WifiUtil(this);
		mListenerList = new CopyOnWriteArrayList<>();
		if (deviceInitInfo == null) {
			deviceInitInfo = new DeviceInitInfo();
		}
		// 注册广播监听器监听网络变化
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LCConfiguration.CONNECTIVITY_CHAGET_ACTION);
		mReceiver = new Broadcast();
		registerReceiver(mReceiver, intentFilter);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new SearchDeviceBinder();
	}

	/**
	 * Search for device information on the LAN
	 * <p>
	 * 局域网内搜索设备信息
	 */
	private void startSearchDevices() {

		LCOpenSDK_SearchDevices.getInstance().stopSearchDevices();
		LogUtil.debugLog(TAG, "startSearchDevices():::::::");
		LCOpenSDK_SearchDevices.getInstance().startSearchDevices(this, DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn(), 60 * 1000, new LCOpenSDK_SearchDevices.ISearchDeviceListener() {
			@Override
			public void onDeviceSearched(String snCode, DeviceInitInfo info) {
				LogUtil.debugLog(TAG, "onDeviceSearched()  sn:" + snCode + "\tdeviceInitInfo" + info + "\t listenerList:" + mListenerList);
				if (mListenerList != null) {
					for (int i = 0; i < mListenerList.size(); i++) {
						SearchDeviceManager.ISearchDeviceListener listener = mListenerList.get(i);
						if (listener != null) {
							listener.onDeviceSearched(snCode, info);
						}
					}
				}
			}
		});
	}

	private void stopSearchDevices() {
		LCOpenSDK_SearchDevices.getInstance().stopSearchDevices();
	}

	private void registerListener(SearchDeviceManager.ISearchDeviceListener listener) {
		if (mListenerList != null && !mListenerList.contains(listener)) {
			mListenerList.add(listener);
		}
	}

	private void unRegisterListener(SearchDeviceManager.ISearchDeviceListener listener) {
		if (mListenerList != null && mListenerList.contains(listener)) {
			mListenerList.remove(listener);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		stopSearchDevices();
		if (mListenerList != null) {
			mListenerList.clear();
		}
		mListenerList = null;
	}

	public class SearchDeviceBinder extends Binder {

		public void startSearchDevices() {
			SeachDeviceService.this.startSearchDevices();
		}

		public void stopSearchDevices() {
			SeachDeviceService.this.stopSearchDevices();
		}

		public void registerListener(SearchDeviceManager.ISearchDeviceListener listener) {
			SeachDeviceService.this.registerListener(listener);
		}

		public void unRegisterListener(SearchDeviceManager.ISearchDeviceListener listener) {
			SeachDeviceService.this.unRegisterListener(listener);
		}
	}

	private class Broadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (LCConfiguration.CONNECTIVITY_CHAGET_ACTION.equals(intent.getAction())) {
				//监听到网络变化
				LogUtil.debugLog(TAG, "检测到网络变化");
				SearchDeviceManager.getInstance().clearDevice();
//                startSearchDevices();
			}
		}
	}
}
