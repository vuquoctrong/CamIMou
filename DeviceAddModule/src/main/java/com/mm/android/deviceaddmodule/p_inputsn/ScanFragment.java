package com.mm.android.deviceaddmodule.p_inputsn;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.Result;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.ScanContract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ScanResult;
import com.mm.android.mobilecommon.annotation.DeviceState;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.deviceaddmodule.p_inputsn.scanresult.DecodeImgCallback;
import com.mm.android.deviceaddmodule.p_inputsn.scanresult.DecodeImgThread;
import com.mm.android.deviceaddmodule.p_inputsn.scanresult.ImageUtil;
import com.mm.android.deviceaddmodule.presenter.ScanPresenter;
import com.mm.android.deviceaddmodule.views.AddBoxTipDialog;
import com.mm.android.dhqrscanner.BaseScannerView;
import com.mm.android.dhqrscanner.DHScannerView;

import static android.app.Activity.RESULT_OK;

/**
 * Two-dimensional code scanning page
 * <p>
 * 二维码扫描页
 */
public class ScanFragment extends BaseDevAddFragment implements ScanContract.View, BaseScannerView.HandleDecodeResultListener
		, View.OnClickListener {
	ScanContract.Presenter mPresenter;
	DHScannerView mDHScannerView;           //二维码扫描控件
	TextView mNext, mFlashTv, mPhone;
	boolean isLight = false;                 //闪光灯是否开启
	public static final int REQUEST_IMAGE = 10;


	public static ScanFragment newInstance() {
		ScanFragment fragment = new ScanFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public void changeFlashLight() {
		isLight = !isLight;
		mDHScannerView.onFlash(isLight);
		Drawable drawable = getResources().getDrawable(isLight ? R.drawable.adddevice_pic_flashlight_on : R.drawable.adddevice_pic_flashlight_off);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mFlashTv.setCompoundDrawables(null, drawable, null, null);
		mFlashTv.setText(isLight ? R.string.add_device_falshlight_on : R.string.add_device_falshlight_off);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}


	protected void initData() {
		mPresenter = new ScanPresenter(this);
		mPresenter.resetCache();//进入扫描页，清空缓存


	}

	protected void initView(View view) {
		mDHScannerView = view.findViewById(R.id.lc_scanview);
		mDHScannerView.setHandleDecodeResuleListener(this);
		mDHScannerView.setScanInRect(true);
		mNext = view.findViewById(R.id.next_btn);
		mNext.setOnClickListener(this);
		mPhone = view.findViewById(R.id.phone_btn);
		mPhone.setOnClickListener(this);
		mFlashTv = view.findViewById(R.id.tv_flash);
		mFlashTv.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mDHScannerView.onScanResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mDHScannerView.onScanPause();
		mDHScannerView.onFlash(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPresenter.recycle();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mDHScannerView.stopCamera();
		mDHScannerView.onScanDestory();
	}


	@Override
	public void handleDecodeResult(String result, byte[] bytes, int with, int height) {
		if (!isViewActive()) return;
		if (TextUtils.isEmpty(result)) {
			mDHScannerView.onScanResume();
		} else {
			LogUtil.debugLog("ScanFragment", "result : " + result);
			ScanResult scanResult = mPresenter.parseScanStr(result, "");
			String sn = scanResult.getSn().trim();
			String productId = scanResult.getProductId().trim();
			String mode = scanResult.getMode().trim();
			if (!isLetterDigit(sn)) {
				toast(R.string.add_device_qrcode_error_tip);
				PageNavigationHelper.gotoManualInputPage(this);
				return;
			}

			mPresenter.getDeviceInfo(sn, mode, productId, true);
		}
	}

	private boolean isLetterDigit(String str) {
		// 序列号二维码规则字母 + 数字，长度 10 - 32位
		String regex = "^[a-z0-9A-Z]{10,32}$";
		return str.matches(regex);
	}

	@Override
	public void openCamerError() {

	}

	@Override
	public void showToastInfo(String msg) {
		toast(msg);
		mDHScannerView.onScanResume();
	}

	@Override
	public void showToastInfo(int msgId) {
		toast(msgId);
		mDHScannerView.onScanResume();
	}

	@Override
	public void showToastInfo(int msgId, String msg) {
		if (!TextUtils.isEmpty(msg)) {
			toast(msg);
		} else {
			toast(msgId);
		}
		mDHScannerView.onScanResume();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (R.id.next_btn == id) {
			PageNavigationHelper.gotoManualInputPage(this);
		} else if (R.id.tv_flash == id) {
			changeFlashLight();
		} else if (R.id.phone_btn == id) {
			/*打开相册*/
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_IMAGE);
		}
	}

	@Override
	public void goTypeChoosePage() {
		PageNavigationHelper.gotoTypeChoosePage(this);
	}

	@Override
	public void goNotSupportBindTipPage() {
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND);
	}

	@Override
	public void goOtherUserBindTipPage(String defaultTip) {
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER, defaultTip);
	}

	@Override
	public void showAddBoxTip() {
		if (!PreferencesHelper.getInstance(getActivity()).getBoolean(
				LCConfiguration.SHOW_ADD_BOX_TIP)) {
			AddBoxTipDialog a = new AddBoxTipDialog();
			a.setDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					goCloudConnectPage();
				}
			});
			a.show(getActivity().getSupportFragmentManager(), a.getClass().getName());
		} else {
			goCloudConnectPage();
		}
	}

	@Override
	public void goCloudConnectPage() {
		PageNavigationHelper.gotoCloudConnectPage(this);
	}

	@Override
	public void goDeviceLoginPage() {
		PageNavigationHelper.gotoDevLoginPage(this);
	}

	@Override
	public void goSecCodePage() {
		PageNavigationHelper.gotoDevSecCodePage(this);
	}

	@Override
	public void goDeviceBindPage() {
		PageNavigationHelper.gotoDeviceBindPage(this);
	}

	@Override
	public void goIMEIInputPage() {
		PageNavigationHelper.gotoIMEIInputPage(this);
	}

	@Override
	public void goBindSuccessPage() {
		PageNavigationHelper.gotoBindSuccessPage(this);
	}

	@Override
	public void gotoIotAddModule(DeviceAddInfo deviceAddInfo) {
		if (DeviceState.ONLINE.equals(deviceAddInfo.getStatus())) {
			PageNavigationHelper.gotoDeviceBindPage(ScanFragment.this, true);
		} else {
			if (deviceAddInfo != null && !TextUtils.isEmpty(deviceAddInfo.getDefaultWifiConfigMode()) && ("4g".equals(deviceAddInfo.getDefaultWifiConfigMode().toLowerCase())
					|| "lan".equals(deviceAddInfo.getDefaultWifiConfigMode().toLowerCase()))) {
				PageNavigationHelper.gotoIotGuidePage(this.getActivity());
			} else {
				PageNavigationHelper.gotoIotWifiChoosePage(this.getActivity());
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
			String path = ImageUtil.getImageAbsolutePath(getActivity(), data.getData());

			new DecodeImgThread(path, new DecodeImgCallback() {
				@Override
				public void onImageDecodeSuccess(Result result) {
					handleDecodeResult(result.getText(), null, 0, 0);
				}

				@Override
				public void onImageDecodeFailed() {
					toast(R.string.device_add_scan_error);
				}
			}).run();

		}
	}
}
