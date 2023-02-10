package com.mm.android.deviceaddmodule.presenter;

import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.CloudConnectConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class CloudConnectPresenter implements CloudConnectConstract.Presenter {
	static int TIME_OUT = 120;            //查询超时时间、单位S
	static int MIDDLE_TIME = 50;          //P2P设备最大允许等待时间，单位S，若已在服务注册、DMS不在线的P2P类型设备，在该时间到后，直接走绑定流程
	static int SOFT_AP_TIME_OUT = 120;            //软AP查询超时时间、单位S
	static int SOFT_AP_MIDDLE_TIME = 100;          //P2P设备最大允许等待时间，单位S，若已在服务注册、DMS不在线的P2P类型设备，在该时间到后，直接走绑定流程

	WeakReference<CloudConnectConstract.View> mView;
	long mEventStartTime;       //统计开始时间

	public CloudConnectPresenter(CloudConnectConstract.View view) {
		mView = new WeakReference<>(view);
		int timeout = TIME_OUT;
		int middleTime = MIDDLE_TIME;
		DeviceAddInfo.DeviceAddType deviceAddType = DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType();
		if (DeviceAddInfo.DeviceAddType.SOFTAP.equals(deviceAddType)) {
			timeout = SOFT_AP_TIME_OUT;
			middleTime = SOFT_AP_MIDDLE_TIME;
		}
		mView.get().setCountDownTime(timeout);
		mView.get().setMiddleTime(middleTime);
	}


	@Override
	public void bindDevice() {
		final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		final String sn = deviceAddInfo.getDeviceSn();
		String pwd = deviceAddInfo.getDevicePwd();
		// 无auth能力集的设备没有设备密码
		String devPwd = pwd;
		LCBusinessHandler bindHandler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (mView.get() == null
						|| (mView.get() != null && !mView.get().isViewActive())) {
					return;
				}
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {           //设备被其他帐户绑定
						mView.get().goOtherUserBindTipPage();
						return;
					}
					addDeviceToPolicy(sn);
				} else {
					String errorDesp = ((BusinessException) msg.obj).errorDescription;
					if (errorDesp.contains("DV1014")) { //设备绑定错误连续超过10次，限制绑定30分钟
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN);
					} else if (errorDesp.contains("DV1015")) { //设备绑定错误连续超过20次，限制绑定24小时
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE);
					} else if (errorDesp.contains("DV1044")) { //设备IP不在统一局域网内
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_IP_ERROR);
					} else if (errorDesp.contains("DV1045")) {  // 设备冲突
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_CODE_CONFLICT);
					} else if (errorDesp.contains("DV1042")) {  // 设备密码错误达限制次数，设备锁定
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_LOCKED);
					} else if (errorDesp.contains("DV1027")) {  // 设备安全码错误
						mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
						mView.get().goDevSecCodePage();
						deviceAddInfo.setRegCode("");
					} else if (errorDesp.contains("DV1016") || errorDesp.contains("DV1025") || errorDesp.contains("DV1005")) {   // 设备密码错误(环回认证失败）/设备SC码或设备密码错误
						mView.get().goDevLoginPage();
						deviceAddInfo.setDevicePwd("");
					} else if (errorDesp.contains("DV1037")) { //NB设备的imei和device id 不匹配
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH);
					} else {
						mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
						mView.get().goErrorTipPage();
					}
				}
			}
		};
		DeviceAddModel.newInstance().bindDevice(sn, devPwd, bindHandler);
	}


	private void addDeviceToPolicy(String sn) {
		LCBusinessHandler policyHandler = new LCBusinessHandler(Looper.getMainLooper()) {

			@Override
			public void handleBusiness(Message msg) {
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					mView.get().goBindSuccessPage();
				} else {
					mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
				}
			}
		};
		DeviceAddModel.newInstance().addPolicy(sn, policyHandler);
	}


	@Override
	public void getDeviceInfo() {
		String deviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
		String model = DeviceAddModel.newInstance().getDeviceInfoCache().getModelName();
		String productId = DeviceAddModel.newInstance().getDeviceInfoCache().getProductId();
		LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (mView.get() == null
						|| (mView.get() != null && !mView.get().isViewActive())) {
					return;
				}
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					dispatchResult();
				} else {//发生异常且倒计时未完成，重新查询
					String errorDesp = ((BusinessException) msg.obj).errorDescription;
					if (errorDesp.contains("DV1037")) {
						DeviceAddModel.newInstance().setLoop(false);
						mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH);
					} else {
						getDeviceInfo();
					}
				}

			}
		};
		DeviceAddModel.newInstance().getDeviceInfoLoop(deviceSn, model, productId, TIME_OUT, getDeviceHandler);
	}

	@Override
	public void recycle() {

		DeviceAddModel.newInstance().setMiddleTimeUp(false);
	}

	@Override
	public boolean isWifiOfflineConfiMode() {
		return DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode();
	}

	@Override
	public void notifyMiddleTimeUp() {
		DeviceAddModel.newInstance().setMiddleTimeUp(true);
	}

	@Override
	public void startConnectTiming() {
		mEventStartTime = System.currentTimeMillis();
	}

	@Override
	public void stopConnectTiming() {

	}

	private void dispatchResult() {
		stopConnectTiming();
		DeviceAddModel.newInstance().setLoop(false);
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		String status = deviceAddInfo.getStatus();
		if (TextUtils.isEmpty(status)) {
			status = DeviceAddInfo.Status.offline.name();
		}
		if (DeviceAddInfo.Status.online.name().equals(status)
				|| DeviceAddInfo.Status.sleep.name().equals(status)
				|| DeviceAddInfo.Status.upgrading.name().equals(status)                             //设备已在线
				|| (DeviceAddInfo.Status.offline.name().equals(status) && deviceAddInfo.isDeviceInServer() && deviceAddInfo.isP2PDev())) {//或者设备已在服务上注册，但不在线的P2P类型设备
			if (isWifiOfflineConfiMode()) {//wifi离线配置
				EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.OFFLINE_CONFIG_SUCCESS_ACTION));
				mView.get().showToastInfo(R.string.add_device_wifi_config_success);
				return;
			}

			//设备不支持绑定
			if (!deviceAddInfo.isSupport()) {
				mView.get().goNotSupportBuindTipPage();
				return;
			}

			String devPwd = DeviceAddModel.newInstance().getDeviceInfoCache().getDevicePwd();
			if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) {
				if (TextUtils.isEmpty(devPwd)) {
					mView.get().goDevLoginPage();
				} else {
					mView.get().goBindDevicePage();
				}
			} else {

				if (deviceAddInfo.hasAbility(DeviceAbility.Auth)) {
					if (TextUtils.isEmpty(devPwd)) {
						mView.get().goDevLoginPage();//输入设备密码
					} else {
						mView.get().goBindDevicePage();//直接绑定
					}
				} else if (deviceAddInfo.hasAbility(DeviceAbility.RegCode)) {
					if (TextUtils.isEmpty(deviceAddInfo.getRegCode())) {
						mView.get().goDevSecCodePage();//输入安全码
					} else {
						mView.get().goBindDevicePage();//直接绑定
					}
				} else {
					mView.get().goBindDevicePage();//直接绑定
				}
			}
		} else {
			if (isWifiOfflineConfiMode()) {//wifi离线配置
				mView.get().showToastInfo(R.string.add_device_config_failed);
				mView.get().completeAction();
			} else {
				mView.get().goErrorTipPage();
			}
		}
	}
}
