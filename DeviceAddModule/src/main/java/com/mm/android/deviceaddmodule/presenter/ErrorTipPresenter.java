package com.mm.android.deviceaddmodule.presenter;

import static com.mm.android.mobilecommon.utils.ContextHelper.getResources;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.ErrorTipConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.DeviceHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.p_errortip.TipAboutWifiPwdFragment;
import com.mm.android.deviceaddmodule.p_errortip.TipConnectFailFragment;
import com.mm.android.deviceaddmodule.p_errortip.TipNotSupport5GFragment;
import com.mm.android.deviceaddmodule.p_errortip.TipTimeoutFragment;
import com.mm.android.deviceaddmodule.p_errortip.TipUserBindFragment;
import com.mm.android.deviceaddmodule.p_errortip.TipWifiNameFragment;

import java.lang.ref.WeakReference;

public class ErrorTipPresenter implements ErrorTipConstract.Presenter {
	WeakReference<ErrorTipConstract.View> mView;
	DeviceAddHelper.TimeoutDevTypeModel mCurTypeModel;
	DeviceIntroductionInfo mTips;
	int mErrorCode;
	boolean isDoorbellType = false;

	private String defaultTip;


	public ErrorTipPresenter(ErrorTipConstract.View view) {
		mView = new WeakReference<>(view);
		mTips = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
		mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.COMMON_MODEL;
		String errorType = "";
		if (mTips != null && mTips.getStrInfos() != null) {
			errorType = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ERROR_TIPS_TYPE);
		}
		if ("Doorbell".equalsIgnoreCase(errorType)
				|| "Small Bell".equalsIgnoreCase(errorType)) {//doorbell类型不显示语音提示
			isDoorbellType = true;
		}

		if (view instanceof Fragment) {
			Bundle bundle = ((Fragment) view).getArguments();
			if (bundle != null) {
				defaultTip = bundle.getString(LCConfiguration.ERROR_TIP);
			}

		}
	}


	@Override
	public void dispatchError(int errorCode) {
		mErrorCode = errorCode;
		switch (errorCode) {
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_ABOUT_WIFI_PWD:
				goAboutWifiPwdTipPage();
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G:
				goNotSupport5GTipPage();
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_CONNECT_FAIL:
				goConnectFailTipPage();
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_WIFI_NAME:
				goWifiNameTipPage();
				break;
			case DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND://不支持绑定的型号
				goNotSupportTipPage();
				break;
			case DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER://被其他用户绑定
			case DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER:
				goUserBindTipPage();
				break;
			case DeviceAddHelper.ErrorCode.WIRED_WIRELESS_ERROR_CONFIG_TIMEOUT: //超时
			case DeviceAddHelper.ErrorCode.INIT_ERROR_SERCRITY_CHECK_TIMEOUT:
			case DeviceAddHelper.ErrorCode.CLOUND_CONNECT_ERROR_CONNECT_TIMEOUT:
			case DeviceAddHelper.ErrorCode.CLOUND_CONNECT_QUERY_STATUS_TIMEOUT:
			case DeviceAddHelper.ErrorCode.AP_ERROR_PAIR_TIMEOUT:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ROTATE:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ALWAYS:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_FLASH:
			case DeviceAddHelper.ErrorCode.INIT_ERROR_INIT_FAILED:

			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_IP_ERROR:
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_CODE_CONFLICT:

			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH:

			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_LOCKED:
				goTimeoutTipPage(errorCode);
				break;
			default:        //reset或其他提示
				dispatchReset(errorCode);
				break;
		}
	}


	@Override
	public boolean isResetPage() {
		if (mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_ABOUT_WIFI_PWD
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_RESET
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_AP_RESET
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_RESET
				|| mErrorCode == DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER
				|| mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ALWAYS
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ROTATE
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_WIFI_NAME
				|| mErrorCode == DeviceAddHelper.ErrorCode.COMMON_ERROR_CONNECT_FAIL
				|| mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isUserBindTipPage() {
		if (mErrorCode == DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER
				|| mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER
				|| mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isUserBindTipPageByBind() {
		if (mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER
				|| mErrorCode == DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND) {
			return true;
		} else {
			return false;
		}
	}

	private void dispatchReset(int errorcode) {
		switch (errorcode) {
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_RESET:
				String tipInfo = "";
				String tipImg = "";
				if (mTips != null) {
					tipInfo = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_RESET_OPERATION_INTRODUCTION);
					tipImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_RESET_IMAGE);
					if (TextUtils.isEmpty(tipInfo)) {
						tipInfo = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESET_OPERATION_INTRODUCTION);
						tipImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESET_IMAGE);
					}
					if (TextUtils.isEmpty(tipInfo)) {
						tipInfo = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_RESET_OPERATION_INTRODUCTION);
						tipImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_RESET_IMAGE);
					}
				}
				mView.get().updateInfo(tipInfo, tipImg, false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_RESET:
				tipInfo = "";
				tipImg = "";
				if (mTips != null) {
					tipInfo = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_RESET_OPERATION_INTRODUCTION);
					tipImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_RESET_IMAGE);
				}
				mView.get().updateInfo(tipInfo, tipImg, false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_AP_RESET:
				tipInfo = "";
				tipImg = "";
				if (mTips != null) {
					tipInfo = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_ACCESSORY_MODE_RESET_OPERATION_INTRODUCTION);
					tipImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.HUB_ACCESSORY_MODE_RESET_IMAGE);
				}
				mView.get().updateInfo(tipInfo, tipImg, false);
				break;
		}
	}

	/**
	 * The user has been bound.
	 * <p>
	 * 用户已绑定
	 */
	private void goUserBindTipPage() {
		String bindAcount = DeviceAddModel.newInstance().getDeviceInfoCache().getBindAcount();
		String info = String.format(mView.get().getContextInfo().getString(R.string.add_device_device_bind_by_other), bindAcount);
		mView.get().updateInfo(info, getDrawableUri(R.drawable.adddevice_user_binding), false);
		mView.get().hideHelp();
		TipUserBindFragment fragment = TipUserBindFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * The bound device is not supported
	 * <p>
	 * 不支持绑定的设备
	 */
	private void goNotSupportTipPage() {
		String errorStr = mView.get().getContextInfo().getString(R.string.add_device_not_support_to_bind);
		mView.get().updateInfo(errorStr, getDrawableUri(R.drawable.adddevice_icon_device_default), false);
		mView.get().hideHelp();
		TipUserBindFragment fragment = TipUserBindFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * About WIFI Password
	 * <p>
	 * 关于WIFI密码
	 */
	private void goAboutWifiPwdTipPage() {
		mView.get().updateInfo("", getDrawableUri(R.drawable.adddevice_pic_serialnumber), true);
		mView.get().hideTipTxt();
		TipAboutWifiPwdFragment fragment = TipAboutWifiPwdFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * About the soft Ap connection failure
	 * <p>
	 * 关于软Ap连接失败
	 */
	private void goConnectFailTipPage() {
		mView.get().updateInfo("", getDrawableUri(R.drawable.adddevice_icon_hotspotexplain_fail), true);
		mView.get().hideTipTxt();
		TipConnectFailFragment fragment = TipConnectFailFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * About the soft Ap connection failure
	 * <p>
	 * 关于软Ap连接失败
	 */
	private void goWifiNameTipPage() {
		mView.get().updateInfo("", getDrawableUri(R.drawable.adddevice_icon_wifiexplain_choosewifi), true);
		mView.get().hideTipTxt();
		TipWifiNameFragment fragment = TipWifiNameFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * Does not support 5 g
	 * <p>
	 * 不支持5G
	 */
	private void goNotSupport5GTipPage() {
		mView.get().updateInfo("", getDrawableUri(R.drawable.adddevice_icon_wifiexplain), false);
		mView.get().hideTipTxt();
		TipNotSupport5GFragment fragment = TipNotSupport5GFragment.newInstance();
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	/**
	 * timeout
	 *
	 * @param errorcode Error code
	 *                  <p>
	 *                  超时
	 * @param errorcode 错误码
	 */
	private void goTimeoutTipPage(int errorcode) {
		if (mTips != null) {
			String tipType = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ERROR_TIPS_TYPE);

			//国内需要根据配网模式获取不同的错误模式。海外目前仍取老字段，后面版本同步
			if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE) {
				DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
				if (deviceAddInfo == null) {
					tipType = tipType;
				} else if (DeviceAddInfo.DeviceType.ap.name().equals(deviceAddInfo.getType())) {
					tipType = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ERROR_ACCESSORY_TIPS_TYPE);    //配件
				} else if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoftAP.name())) {
					tipType = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ERROR_SOFTAP_TIPS_TYPE);       //软AP
				} else {
					tipType = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.ERROR_WIFI_TIPS_TYPE);         //有线无线
				}
			}


			if (!TextUtils.isEmpty(tipType)) {
				if (tipType.toLowerCase().contains("A Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.A_MODEL;
				} else if (tipType.toLowerCase().contains("CK Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.CK_MODEL;
				} else if (tipType.toLowerCase().contains("Accessory General".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.AP_MODEL;
				} else if (tipType.toLowerCase().contains("IPC General".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.COMMON_MODEL;
				} else if (tipType.toLowerCase().contains("Doorbell".toLowerCase())
						|| tipType.toLowerCase().contains("Small Bell".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.DOORBELL_MODEL;

					isReportH1GTimeout();
				} else if (tipType.toLowerCase().contains("TP1 Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.TP1_MODEL;
				} else if (tipType.toLowerCase().contains("TP1S Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.TP1S_MODEL;
				} else if (tipType.toLowerCase().contains("G1 Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.G1_MODEL;
				} else if (tipType.toLowerCase().contains("K5 Mode".toLowerCase())) {
					mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.K5_MODEL;
				} else if (tipType.toLowerCase().contains("HUB1".toLowerCase()) || tipType.toLowerCase().contains("HUB2".toLowerCase())) {
				}
			} else {

			}
		}
		switch (errorcode) {
			case DeviceAddHelper.ErrorCode.WIRED_WIRELESS_ERROR_CONFIG_TIMEOUT:
				String img = getDrawableUri(R.drawable.common_failhrlp_default);
				dispathTypeModel(img);
				break;
			case DeviceAddHelper.ErrorCode.AP_ERROR_PAIR_TIMEOUT:
				img = getDrawableUri(R.drawable.common_failhrlp_default);
				mCurTypeModel = DeviceAddHelper.TimeoutDevTypeModel.AP_MODEL;
				dispathTypeModel(img);
				break;
			case DeviceAddHelper.ErrorCode.SOFTAP_ERROR_CONNECT_HOT_FAILED:
				img = getDrawableUri(R.drawable.common_failhrlp_default);
				dispathTypeModel(img);
				break;
			case DeviceAddHelper.ErrorCode.CLOUND_CONNECT_QUERY_STATUS_TIMEOUT:
			case DeviceAddHelper.ErrorCode.CLOUND_CONNECT_ERROR_CONNECT_TIMEOUT:
				img = getDrawableUri(R.drawable.adddevice_fail_configurationfailure);
				if (DeviceAddInfo.DeviceAddType.SOFTAP.equals(DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType())) {//软Ap
					mView.get().updateInfo(R.string.add_device_connect_timeout, isDoorbellType ? 0 : R.string.add_device_operation_by_voice_tip, img, false);
				} else {
					mView.get().updateInfo(R.string.add_device_config_failed, img, false);
				}
				break;
			case DeviceAddHelper.ErrorCode.INIT_ERROR_SERCRITY_CHECK_TIMEOUT:
				img = getDrawableUri(R.drawable.adddevice_fail_undetectable);
				mView.get().updateInfo(R.string.add_device_detect_safe_network_config_failed, img, false);
				break;
			case DeviceAddHelper.ErrorCode.INIT_ERROR_INIT_FAILED:
				img = getDrawableUri(R.drawable.adddevice_fail_undetectable);
				mView.get().updateInfo(R.string.add_device_timeout_init_failed, img, false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ALWAYS:
				mView.get().updateInfo(R.string.add_device_red_light_always, R.string.add_device_disconnect_power_and_restart, getDrawableUri(R.drawable.common_netsetting_power), true);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ROTATE:
				mView.get().updateInfo(R.string.add_device_red_light_rotate, R.string.add_device_disconnect_power_and_restart, getDrawableUri(R.drawable.common_netsetting_power), true);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_FLASH:
				mView.get().updateInfo(R.string.add_device_red_light_twinkle, R.string.add_device_timeout_title_tip8, getDrawableUri(R.drawable.adddevice_failhrlp_g1), false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN:
				mView.get().updateInfo(R.string.add_device_commom_dev_more_than_ten_tip, getDrawableUri(R.drawable.adddevice_fail_rest), false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE:
				mView.get().updateInfo(R.string.add_device_commom_dev_more_than_ten_twice_tip, getDrawableUri(R.drawable.adddevice_fail_rest), false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_IP_ERROR:
				mView.get().updateInfo(R.string.add_device_commom_dev_ip_error_tip, getDrawableUri(R.drawable.common_netsetting_power), false);
				break;
			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_CODE_CONFLICT:
				mView.get().updateInfo(R.string.add_device_commom_dev_sn_code_conflict_tip, getDrawableUri(R.drawable.adddevice_fail_configurationfailure), false);
				break;

			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_LOCKED:
				mView.get().updateInfo(R.string.mobile_common_bec_device_locked, getDrawableUri(R.drawable.common_netsetting_power), true);
				break;

			case DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH:
				mView.get().updateInfo(R.string.add_device_device_sn_or_imei_not_match, getDrawableUri(R.drawable.adddevice_fail_configurationfailure), true);
				break;
			default:
				break;
		}
		String devtypeModel = "";
		if (mCurTypeModel != null) {
			devtypeModel = mCurTypeModel.name();
		}
		TipTimeoutFragment fragment = TipTimeoutFragment.newInstance(errorcode, devtypeModel);
		FragmentTransaction transaction = mView.get().getParent().getChildFragmentManager().beginTransaction();
		transaction.add(R.id.child_content, fragment);
		transaction.commit();
	}

	private void dispathTypeModel(String img) {
		if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.A_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_or_light, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.CK_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_or_light, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.COMMON_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_timeout_title_tip7, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.DOORBELL_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, isDoorbellType ? 0 : R.string.add_device_operation_by_voice_tip, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.AP_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.TP1_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_or_light, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.TP1S_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_or_light, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.G1_MODEL)) {
			img = getDrawableUri(R.drawable.adddevice_failhrlp_g1);
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_or_light, img, false);
		} else if (mCurTypeModel.equals(DeviceAddHelper.TimeoutDevTypeModel.K5_MODEL)) {
			mView.get().updateInfo(R.string.add_device_connect_timeout, R.string.add_device_operation_by_voice_tip, img, false);
		}
	}

	private boolean isReportH1GTimeout() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (deviceAddInfo != null && DeviceHelper.isH1G(deviceAddInfo.getModelName())) {
			return true;
		}

		return false;
	}

	private String getDrawableUri(@DrawableRes int resId) {
		Resources resources = getResources();
		if (resources == null) return "";
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
				resources.getResourcePackageName(resId) + '/' +
				resources.getResourceTypeName(resId) + '/' +
				resources.getResourceEntryName(resId)).toString();
	}
}
