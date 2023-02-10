package com.mm.android.deviceaddmodule.presenter;

import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.deviceaddmodule.contract.WifiPwdConstract;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;

import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class WifiPwdPresenter implements WifiPwdConstract.Presenter {
	WeakReference<WifiPwdConstract.View> mView;
	private boolean isSupport5G;
	DHWifiUtil mDHWifiUtil;
	private String WIFI_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_ADD_";
	private String WIFI_CHECKBOX_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_CHECKBOX_ADD_";

	public WifiPwdPresenter(WifiPwdConstract.View view) {
		mView = new WeakReference<>(view);
		String wifiMode = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiTransferMode();
		if (!TextUtils.isEmpty(wifiMode)) {
			isSupport5G = wifiMode.toUpperCase().contains("5GHZ");
		}
		mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
	}

	@Override
	public boolean isDevSupport5G() {
		return isSupport5G;
	}

	@Override
	public String getCurWifiName() {
		String curWifiName = mDHWifiUtil.getCurrentWifiInfo().getSSID().replaceAll("\"", "");
		if (LCConfiguration.UNKNOWN_SSID.equals(curWifiName)) {
			curWifiName = "";
		}
		return curWifiName;
	}

	@Override
	public void updateWifiCache() {
		DeviceAddInfo.WifiInfo wifiInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiInfo();
		wifiInfo.setSsid(getCurWifiName());
		wifiInfo.setPwd(mView.get().getWifiPwd());
		if (mView.get().isSavePwdChecked()) {
			PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + getCurWifiName(), mView.get().getWifiPwd());
			PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName(), true);
		} else {
			PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + getCurWifiName(), "");
			PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName(), false);
		}
	}

	@Override
	public String getSavedWifiPwd() {
		return PreferencesHelper.getInstance(mView.get().getContextInfo()).getString(WIFI_SAVE_PREFIX + getCurWifiName());
	}

	@Override
	public boolean getSavedWifiCheckBoxStatus() {
		return PreferencesHelper.getInstance(mView.get().getContextInfo()).getBoolean(WIFI_CHECKBOX_SAVE_PREFIX + getCurWifiName());
	}

	@Override
	public String getConfigMode() {
		return DeviceAddModel.newInstance().getDeviceInfoCache().getConfigMode();
	}

}
