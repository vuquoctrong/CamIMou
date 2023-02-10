package com.mm.android.deviceaddmodule.presenter;

import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.music.DHMusicPlayer;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.ScanContract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.AppConsume.ScanResult;
import com.mm.android.mobilecommon.AppConsume.ScanResultFactory;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import static com.mm.android.deviceaddmodule.helper.Utils4AddDevice.isDeviceTypeBox;

public class ScanPresenter implements ScanContract.Presenter {
	WeakReference<ScanContract.View> mView;
	DHMusicPlayer mDHMusicPlayer;

	public ScanPresenter(ScanContract.View view) {
		mView = new WeakReference<>(view);
		mDHMusicPlayer = new DHMusicPlayer(mView.get().getContextInfo(), false, true, R.raw.beep);
		mDHMusicPlayer.setSupportVibrate(true);
	}

	@Override
	public ScanResult parseScanStr(String scanStr, String sc) {
		if (!isManualInputPage()) {
			mDHMusicPlayer.playRing(false);
		}
		ScanResult scanResult = ScanResultFactory.scanResult(scanStr.trim());
		LogUtil.debugLog("ScanPresenter", "scanResult : " + scanResult);
		// 手动输入的安全验证码
		if (!TextUtils.isEmpty(sc)) {
			scanResult.setSc(sc);
		}
		if (!TextUtils.isEmpty(scanResult.getSn())) {
			updateDeviceAddInfo(scanResult.getSn().trim(), scanResult.getMode(), scanResult.getRegcode(), scanResult.getNc(), scanResult.getSc());
//            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.GET_DEVICE_SN));
		}
		return scanResult;
	}

	/**
	 * Query device information on the platform based on the scanned data
	 *
	 * @param deviceSn        Device Serial Number
	 * @param deviceCodeModel Device code model
	 *                        <p>
	 *                        根据扫码的数据去平台查询设备信息
	 * @param deviceSn        设备序列号
	 * @param deviceCodeModel 设备码
	 */
	@Override
	public void getDeviceInfo(final String deviceSn, String deviceCodeModel, final String productId, final boolean isScan) {
		if (isSnInValid(deviceSn)) {
			mView.get().showToastInfo(R.string.add_device_scan_lechange_device_qr_code);
		} else {
			mView.get().showProgressDialog();
			LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {
				@Override
				public void handleBusiness(Message msg) {
					if (mView.get() == null
							|| (mView.get() != null && !mView.get().isViewActive())) {
						return;
					}
					mView.get().cancelProgressDialog();
					if (msg.what == HandleMessageCode.HMC_SUCCESS) {

					} else {
						String errorDesp = ((BusinessException) msg.obj).errorDescription;
						if (errorDesp.contains("DV1037")) {
							mView.get().showToastInfo(R.string.add_device_device_sn_or_imei_not_match);
							return;
						}
						if (errorDesp.contains("DV1003")) {
							//添加到子账户
							addDeviceToPolicy(deviceSn);
							return;
						}

						if (errorDesp.contains("DV1001")) {
							String defaultTip = null;
							if (errorDesp.startsWith("DV1001")) {
								String[] arr = errorDesp.split("DV1001");
								if (arr.length > 1) {
									defaultTip = arr[1];
								}
							}
							mView.get().goOtherUserBindTipPage(defaultTip);
							return;
						}
						if (errorDesp.contains("DV1003")) {
							mView.get().showToastInfo(R.string.add_device_device_bind_by_yourself);
							return;
						}
						mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
						return;
					}

					if (!TextUtils.isEmpty(productId)) {
						// 进入IOT设备的处理逻辑
						DeviceAddModel.newInstance().getDeviceInfoCache().setProductId(productId);
						dispatchIotResult();
					} else {
						dispatchResult();
					}
				}
			};
			DeviceAddModel.newInstance().getDeviceInfo(deviceSn, deviceCodeModel, "", productId, getDeviceHandler);
		}
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

	private void getDevIntroductionInfoSync(String deviceModel, final boolean isOnlineAction) {
		LCBusinessHandler getDevIntroductionHandler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (mView.get() == null
						|| (mView.get() != null && !mView.get().isViewActive())) {
					return;
				}
				dispatchIntroductionResult(isOnlineAction);
			}
		};
		DeviceAddModel.newInstance().getDevIntroductionInfo(deviceModel, getDevIntroductionHandler);
	}

	private void checkDevIntroductionInfo(final String deviceModelName, final boolean isOnlineAction) {
		mView.get().showProgressDialog();
		LCBusinessHandler checkDevIntroductionHandler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (mView.get() == null
						|| (mView.get() != null && !mView.get().isViewActive())) {
					return;
				}
				DeviceIntroductionInfo deviceIntroductionInfo = null;
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					deviceIntroductionInfo = (DeviceIntroductionInfo) msg.obj;
				}
				if (deviceIntroductionInfo == null) {        //表示需要更新
					getDevIntroductionInfoSync(deviceModelName, isOnlineAction);
				} else {
					dispatchIntroductionResult(isOnlineAction);
				}
			}
		};
		DeviceAddModel.newInstance().checkDevIntroductionInfo(deviceModelName, checkDevIntroductionHandler);
	}

	private void dispatchIntroductionResult(boolean isOnlineAction) {
		mView.get().cancelProgressDialog();
		if (isOnlineAction) {
			gotoPage();
		} else {
			EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CONFIG_PAGE_NAVIGATION_ACTION));
		}
	}

	//扫描出的二维码是否有效
	@Override
	public boolean isSnInValid(String sn) {
		if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) {
			return (sn.length() == 0
					|| sn.getBytes().length > 64);
		} else {
			return TextUtils.isEmpty(sn);
		}
	}

	@Override
	public boolean isScCodeInValid(String scCode) {
		return false;
	}

	@Override
	public void recycle() {
		if (mDHMusicPlayer != null) {
			mDHMusicPlayer.release();
		}
	}

	@Override
	public void resetCache() {
		DeviceAddModel.newInstance().getDeviceInfoCache().clearCache();
	}

	@Override
	public boolean isManualInputPage() {
		return false;
	}

	protected void updateDeviceAddInfo(final String deviceSn, final String model, String regCode, String nc, String sc) {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		deviceAddInfo.setDeviceSn(deviceSn);
		deviceAddInfo.setDeviceCodeModel(model);
		deviceAddInfo.setDeviceModel(model);
		deviceAddInfo.setRegCode(regCode);
		deviceAddInfo.setSc(sc);
		deviceAddInfo.setNc(nc);  // 将16进制的字符串转换为数字
		// 支持SC码的设备，使用SC码作为设备密码
		if (DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
			deviceAddInfo.setDevicePwd(sc);
		}
	}

	/**
	 * Process device information returned by the IOT device service
	 * <p>
	 * 处理IOT设备的服务返回的设备信息
	 */
	private void dispatchIotResult() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (DeviceAddInfo.BindStatus.bindByMe.name().equals(deviceAddInfo.getBindStatus())) {
			// 设备已经被自己绑定
			mView.get().showToastInfo(R.string.add_device_device_bind_by_yourself);
		} else if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {
			//设备被其他帐户绑定（弱绑定状态 该状态不会返回，如果被其他人绑定返回bind）
			mView.get().goOtherUserBindTipPage(null);
		} else if (DeviceAddInfo.BindStatus.unbind.name().equals(deviceAddInfo.getNextStep()) ||
				DeviceAddInfo.IotDeviceAddNextStep.bind.name().equals(deviceAddInfo.getNextStep())) { // 如果是 networkConfig 或者 bind 则直接进入IOT添加组件                                                    //设备未在平台上注册
			// 设备需要配网 或 设备已经被绑定
			iotDeviceAction(deviceAddInfo);
		} else if (DeviceAddInfo.Status.online.name().equals(deviceAddInfo.getStatus()) && DeviceAddInfo.BindStatus.unbind.name().equals(deviceAddInfo.getBindStatus())) {
			mView.get().goDeviceBindPage();
		} else {
			deviceAddInfo.setNextStep(DeviceAddInfo.IotDeviceAddNextStep.networkConfig.name());
			iotDeviceAction(deviceAddInfo);
		}
	}

	/**
	 * IOT Device Binding
	 * <p>
	 * IOT设备绑定
	 */
	private void iotDeviceAction(DeviceAddInfo deviceAddInfo) {
		mView.get().gotoIotAddModule(deviceAddInfo);
	}

	/**
	 * Process device information returned by the service
	 * <p>
	 * 处理服务返回的设备信息
	 */
	private void dispatchResult() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (!deviceAddInfo.isSupport()) {
			mView.get().goNotSupportBindTipPage();
		} else if (DeviceAddInfo.BindStatus.bindByMe.name().equals(deviceAddInfo.getBindStatus())) {                     //设备被当前帐户绑定
			mView.get().showToastInfo(R.string.add_device_device_bind_by_yourself);
		} else if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {           //设备被其他帐户绑定
			mView.get().goOtherUserBindTipPage(null);
		} else if (DeviceAddInfo.DeviceType.ap.name().equals(deviceAddInfo.getType())) {        //配件
			checkDevIntroductionInfo(deviceAddInfo.getDeviceModel(), false);
		} else if (DeviceAddInfo.DeviceType.iot.name().equals(deviceAddInfo.getType())) {
			//iot
			iotDeviceAction(deviceAddInfo);
		} else {    // 设备
			if (isManualInputPage()  // 若二维码中无sc码处理成与ios一致
					&& deviceAddInfo.hasAbility(DeviceAbility.SCCode) && (deviceAddInfo.getSc() == null || deviceAddInfo.getSc().length() != 8)) {   // 已上平台有sc码能力但sc码输入错误
				mView.get().showToastInfo(R.string.add_device_input_corrent_sc_tip);
			} else if (!deviceAddInfo.isDeviceInServer()) {                                                            //设备未在平台上注册
				//走设备离线添加流程
				deviceOfflineAction();
			} else if (DeviceAddInfo.Status.offline.name().equals(deviceAddInfo.getStatus())) {                        //设备离线
				deviceOfflineAction();
			} else if (DeviceAddInfo.Status.online.name().equals(deviceAddInfo.getStatus())
					|| DeviceAddInfo.Status.sleep.name().equals(deviceAddInfo.getStatus())
					|| DeviceAddInfo.Status.upgrading.name().equals(deviceAddInfo.getStatus())) {                         //设备在线/休眠/升级中
				deviceOnlineAction();
			}
		}

		if (isManualInputPage()) {
			deviceAddInfo.setStartTime(System.currentTimeMillis());
		}
	}

	/**
	 * If the device information fails to be obtained or the device is offline, you need to handle the result
	 * <p>
	 * 获取设备信息失败，或者设备离线状态下，需要对结果进行处理
	 */
	private void deviceOfflineAction() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (isDeviceTypeBox(deviceAddInfo.getDeviceCodeModel())) {// 如果是乐盒设备，直接提示设备不在线
			mView.get().showToastInfo(R.string.add_device_box_is_offline);
		} else {
			if ((!TextUtils.isEmpty(deviceAddInfo.getDeviceCodeModel())
					|| !TextUtils.isEmpty(deviceAddInfo.getDeviceModel()))) { //扫码信息中存在设备类型
				String deviceModel = deviceAddInfo.getDeviceModel();
				if (TextUtils.isEmpty(deviceModel)) {
					deviceModel = deviceAddInfo.getDeviceCodeModel();
				}
				checkDevIntroductionInfo(deviceModel, false);
			} else {
				mView.get().goTypeChoosePage();                 //设备选择
			}
		}
	}

	/**
	 * The device information is obtained, and the device is online, and the result is processed
	 * <p>
	 * 获取到设备信息，并且设备在线，对结果进行处理
	 */
	private void deviceOnlineAction() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (isDeviceTypeBox(deviceAddInfo.getDeviceCodeModel())) {
			// 盒子，不支持
			mView.get().showToastInfo(R.string.add_device_not_support_to_bind);
			return;
		} else {// 其他设备
			if (!TextUtils.isEmpty(deviceAddInfo.getDeviceCodeModel())
					|| !TextUtils.isEmpty(deviceAddInfo.getDeviceModel())) { //扫码信息中存在设备类型
				String deviceModel = deviceAddInfo.getDeviceModel();
				if (TextUtils.isEmpty(deviceModel)) {
					deviceModel = deviceAddInfo.getDeviceCodeModel();
				}
				checkDevIntroductionInfo(deviceModel, true);
			} else {
				gotoPage();
			}
		}
	}

	private void gotoPage() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		deviceAddInfo.setCurDeviceAddType(DeviceAddInfo.DeviceAddType.ONLINE);

		if (DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
			mView.get().goCloudConnectPage();
		} else {
			if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) { // 海外
				mView.get().goDeviceLoginPage();
			} else {
				if (deviceAddInfo.hasAbility(DeviceAbility.Auth)) {
					if (TextUtils.isEmpty(deviceAddInfo.getDevicePwd())) {
						mView.get().goDeviceLoginPage();//输入设备密码
					} else {
						mView.get().goDeviceBindPage();//直接绑定
					}
				} else if (deviceAddInfo.hasAbility(DeviceAbility.RegCode)) {
					if (TextUtils.isEmpty(deviceAddInfo.getRegCode())) {
						mView.get().goSecCodePage();//输入安全码
					} else {
						mView.get().goDeviceBindPage();//直接绑定
					}
				}
			}
		}
	}
}
