package com.mm.android.deviceaddmodule.helper;

import android.content.Context;
import android.os.Message;

import androidx.fragment.app.Fragment;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.mm.android.iotdeviceaddmodule.data.IotAddDeviceInfo;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.ContextHelper;
import com.mm.android.mobilecommon.utils.StringUtils;

public class IotSoftApHelper {
	DHWifiUtil mDHWifiUtil;
	Fragment oriFragment;

	private static IotSoftApHelper instance;


	private IotSoftApHelper() {
		mDHWifiUtil = new DHWifiUtil(ContextHelper.getAppContext());
	}

	public static IotSoftApHelper getInstance() {
		if (instance == null) {
			instance = new IotSoftApHelper();
		}
		return instance;
	}


	public void setDeviceWifi(final LCBusinessHandler handler) {
		new BusinessRunnable(handler) {
			@Override
			public void doBusiness() throws BusinessException {
				IotAddDeviceInfo iotAddDeviceInfo = DeviceAddOpenApiManager.getIotDeviceInfoByHotsPort(mDHWifiUtil.getGatewayIp());
				if (iotAddDeviceInfo != null) {
					//版本大于等于1.3.0判断结果
					long serviceversion = StringUtils.checkVersion(iotAddDeviceInfo.serviceversion);

					if (serviceversion > 130) {
						DeviceAddOpenApiManager.regserverConf(mDHWifiUtil.getGatewayIp());
					}
					DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
					if (deviceAddInfo == null) {
						handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION, new Exception("缓存出错，请重试")).sendToTarget();
						return;
					}
					DeviceAddOpenApiManager.setIotNetworkInfo(mDHWifiUtil.getGatewayIp(), deviceAddInfo.getWifiInfo().getSsid(), deviceAddInfo.getWifiInfo().getPwd(), "5");
				} else {
					handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION, new Exception("数据解析失败，请重试")).sendToTarget();
					return;
				}


				handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, iotAddDeviceInfo).sendToTarget();
			}
		};
	}

	/**
	 * 打开wifi
	 */
	public void openWifi() {
		if (!isWifiConnect()) {
			mDHWifiUtil.openWifi();
		}
	}

	public boolean isWifiConnect() {
		return DHNetworkUtil.NetworkType.NETWORK_WIFI.equals(DHNetworkUtil.getNetworkType(ContextHelper.getAppContext()));
	}

}
