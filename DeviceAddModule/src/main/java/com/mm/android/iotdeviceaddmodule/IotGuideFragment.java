package com.mm.android.iotdeviceaddmodule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.lechange.common.log.Logger;
import com.mm.android.deviceaddmodule.openapi.data.IotDeviceLeadingInfoData;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.IotWifiChooseConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.AppProvider;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.annotation.DeviceState;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.mm.android.deviceaddmodule.p_softap.TipSoftApConnectWifiFragment;
import com.mm.android.deviceaddmodule.presenter.IotConnectPresenter;
import com.mm.android.deviceaddmodule.service.FileSaveHelper;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.views.fragment.WebViewDialogFragment;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class IotGuideFragment extends BaseDevAddFragment implements IotWifiChooseConstract.View, View.OnClickListener {

	private static final String TAG = "AddMoudle-IotGuideFragment";
	private TextView iotadd_guide_operate;
	private TextView iotadd_tv_tip;
	private TextView iotadd_common_title;
	private TextView stepTitle;
	private TextView stepOperate;
	private GifImageView banner_view;
	private IotDeviceLeadingInfoData.ResponseData.StepsDTO currentStep;
	private DeviceAddInfo deviceAddInfo;
	private IotConnectPresenter iotConnectPresenter;
	List<IotDeviceLeadingInfoData.ResponseData.StepsDTO> steps;
	private LCAlertDialog mLCAlertDialog;

	private PermissionHelper permissionHelper;
	private DefaultPermissionListener defaultPermissionListener;

	private final String BLE_NOT_OPEN = "ble_not_open";

	private final int BLE_OPEN = 0x11;
	private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
		@Override
		public void onTick(long l) {
			Logger.i(TAG, "onTick:" + l);
		}

		@Override
		public void onFinish() {
			Logger.i(TAG, "onFinish");
			dissmissProgressDialog();
			Toast.makeText(requireContext(), "设备不在线，请检查设备网络状态", Toast.LENGTH_LONG).show();
		}
	};

	final LCBusinessHandler handler = new LCBusinessHandler() {

		@Override
		public void handleBusiness(Message msg) {
			if (msg.what == HandleMessageCode.HMC_SUCCESS) {
				try {
					IotDeviceLeadingInfoData.ResponseData response = (IotDeviceLeadingInfoData.ResponseData) msg.obj;
					if (response != null) {
					}
					updateUI(response);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	private Handler resultHandler;

	private void updateUI(IotDeviceLeadingInfoData.ResponseData data) throws Exception {
		Logger.i(TAG, "updateUI:" + data);
		if (data == null) {
			return;
		}
		steps = data.steps;
		if (steps != null && steps.size() > 0) {
			currentStep = steps.get(0);
			String img = currentStep.stepIcon != null && currentStep.stepIcon.size() > 0 ? currentStep.stepIcon.get(0) : "";
			if (!TextUtils.isEmpty(img)) {
				Glide.with(IotGuideFragment.this.getContext()).load(img).into(banner_view);
			}
			if (currentStep.help != null && currentStep.help.helpUrlTitle != null) {
				iotadd_tv_tip.setText(currentStep.help.helpUrlTitle);
			} else {
				iotadd_tv_tip.setText("");
			}
			stepTitle.setText(currentStep.stepTitle);
			stepOperate.setText(currentStep.stepOperate);
		}
	}

	public static IotGuideFragment newInstance() {
		IotGuideFragment fragment = new IotGuideFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void initView(View view) {
		iotadd_guide_operate = view.findViewById(R.id.iotadd_guide_operate);
		iotadd_common_title = view.findViewById(R.id.iotadd_common_title);
		stepTitle = view.findViewById(R.id.stepTitle);
		stepOperate = view.findViewById(R.id.stepOperate);
		iotadd_tv_tip = view.findViewById(R.id.iotadd_tv_tip);
		banner_view = view.findViewById(R.id.banner_view);
		iotadd_guide_operate.setOnClickListener(this);
		iotadd_tv_tip.setOnClickListener(this);
//        getDeviceInfo();

		DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (hasBluetoothPermission()) {//如果有权限，则将权限提示dismiss
			dismissLCAlertDialog();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume");
	}

	public void dealDeivceConfig() {
		Logger.d(TAG, "dealDeivceConfig   status:" + deviceAddInfo.getStatus() + "  ,bindStatus:" + deviceAddInfo.getBindStatus());
		if (DeviceAddInfo.Status.online.name().equals(deviceAddInfo.getStatus())) {
			if (DeviceAddInfo.BindStatus.unbind.name().equals(deviceAddInfo.getBindStatus())) {
				goIotConnectCloud();
			}
		} else if (DeviceAddInfo.Status.offline.name().equals(deviceAddInfo.getStatus())) {

			if (DeviceAddInfo.ConfigMode.wifi.name().equals(deviceAddInfo.getDefaultWifiConfigMode())) {
				connectWifi();
			} else if (DeviceAddInfo.ConfigMode.Bluetooth.name().toLowerCase().equals(deviceAddInfo.getDefaultWifiConfigMode())) {
				goIotBluetoothConnect();
			} else if (DeviceAddInfo.ConfigMode.iotLan.name().toLowerCase().equals(deviceAddInfo.getDefaultWifiConfigMode())) {
				Toast.makeText(this.getContext(), "请检查设备网络是否正常", Toast.LENGTH_LONG).show();
			} else if ("4G".equals(deviceAddInfo.getDefaultWifiConfigMode()) || "4g".equals(deviceAddInfo.getDefaultWifiConfigMode())) {
				Toast.makeText(this.getContext(), "请检查设备网络是否正常", Toast.LENGTH_LONG).show();
			}
		} else {

		}
	}

	private void goIotBluetoothConnect() {
		if (!hasBluetoothPermission()) {
			showStopDevAddDialog();
			return;
		}

		defaultPermissionListener = new DefaultPermissionListener() {
			@Override
			public void onGranted() {
				startBleScan();
			}
		};
		if (Build.VERSION.SDK_INT >= 31) {
			if (permissionHelper != null)
				permissionHelper.requestBlueToothPermission(defaultPermissionListener);
		} else {
			startBleScan();
		}
	}

	private void startBleScan() {
		showProgressDialog(R.layout.mobile_common_progressdialog_layout);
		try {
			iotConnectPresenter.startScan(resultHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showStopDevAddDialog() {
		dismissLCAlertDialog();
		Context context = getContext();
		if (context == null) return;
		LCAlertDialog.Builder builder = new LCAlertDialog.Builder(context);
		builder.setTitle(R.string.device_manage_notice);
		builder.setMessage(R.string.add_device_bluetooth_turn_off_popup);
		builder.setCancelButton(R.string.common_cancel, null);
		builder.setConfirmButton(R.string.go_to_setting,
				new LCAlertDialog.OnClickListener() {
					@Override
					public void onClick(LCAlertDialog dialog, int which,
										boolean isChecked) {
						if (getActivity() != null) {
							CommonHelper.gotoBluetoothSetting(getActivity(), BLE_OPEN);
						}
					}
				});
		mLCAlertDialog = builder.create();
		mLCAlertDialog.show(getChildFragmentManager(),
				BLE_NOT_OPEN);
	}

	private boolean hasBluetoothPermission() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return bluetoothAdapter.isEnabled();
	}


	private void dismissLCAlertDialog() {
		if (mLCAlertDialog != null && mLCAlertDialog.isVisible()) {
			mLCAlertDialog.dismissAllowingStateLoss();
			mLCAlertDialog = null;
		}
	}


	private void connectWifi() {
		iotConnectPresenter.verifyWifiOrLocationPermission();
	}


	private void goIotConnectCloud() {
//        dissmissProgressDialog();
//        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
//        Logger.d(TAG, "getDeviceInfo  set Wifi Config success"+ deviceAddInfo.getStatus() + "  ,bindStatus:" + deviceAddInfo.getBindStatus());
//        PageNavigationHelper.gotoDeviceBindPage(IotGuideFragment.this, true);
		PageNavigationHelper.gotoCloudConnectPage(this);
	}

	public synchronized void getDeviceInfo() {
		deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		Logger.d(TAG, "getDeviceInfo   status:" + deviceAddInfo.getStatus() + "  ,bindStatus:" + deviceAddInfo.getBindStatus());
		String deviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
		String model = DeviceAddModel.newInstance().getDeviceInfoCache().getModelName();
		String productId = DeviceAddModel.newInstance().getDeviceInfoCache().getProductId();
		LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {

				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					dispatchResult();
				} else {//发生异常且倒计时未完成，重新查询
					String errorDesp = ((BusinessException) msg.obj).errorDescription;
					if (errorDesp.contains("DV1037")) {
						DeviceAddModel.newInstance().setLoop(false);
					} else {
						getDeviceInfo();
					}
				}
			}
		};
		DeviceAddModel.newInstance().getDeviceInfoLoop(deviceSn, model, productId, 10 * 1000, getDeviceHandler);
	}

	private void dispatchResult() {
		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		Logger.d(TAG, "dispatchResult   status:" + deviceAddInfo.getStatus() + "  ,bindStatus:" + deviceAddInfo.getBindStatus());
		if (deviceAddInfo != null) {
			if (DeviceState.ONLINE.equals(deviceAddInfo.getStatus())) {
				goIotConnectCloud();
			} else {

			}
		}
	}

	@Override
	protected void initData() {
		iotConnectPresenter = new IotConnectPresenter(this);
		resultHandler = new Handler(Looper.myLooper()) {
			@Override
			public void handleMessage(@NonNull Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 0:
						dissmissProgressDialog();
						goIotConnectCloud();
						break;
					case -1:
						dissmissProgressDialog();
						Toast.makeText(requireContext(), "配网失败，请重试", Toast.LENGTH_LONG).show();
						break;
				}
			}
		};


		if (permissionHelper == null) permissionHelper = new PermissionHelper(this);
//        iotNetSetingInterface = new IotNetSetingInterface() {
//
//            @Override
//            public void onSuccess() {
//                Logger.i(TAG, "ble set net onSuccess");
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast.makeText(requireContext(),"配网成功",Toast.LENGTH_LONG).show();
////                        countDownTimer.cancel();
////                        countDownTimer.start();
//                        goIotConnectCloud();
//                    }
//                });
//                dissmissProgressDialog();
//            }
//
//            @Override
//            public void onFailure(int error) {
//                Logger.i(TAG, "ble set net onFailure erroe:" + error);
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(requireContext(),"配网失败，请重试",Toast.LENGTH_LONG).show();
//                    }
//                });
//                dissmissProgressDialog();
//            }
//        };

		deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		new BusinessRunnable(handler) {
			@Override
			public void doBusiness() throws BusinessException {
				try {
					IotDeviceLeadingInfoData req = new IotDeviceLeadingInfoData();
					req.data.token = TokenHelper.getInstance().accessToken;
					req.data.productId = TextUtils.isEmpty(deviceAddInfo.getProductId()) ? "" : deviceAddInfo.getProductId();
					req.data.language = AppProvider.newInstance().getAppLanguage();
					req.data.communicate = deviceAddInfo.getDefaultWifiConfigMode();
					IotDeviceLeadingInfoData.Response response = DeviceAddOpenApiManager.iotDeviceLeadingInfo(req);
					FileSaveHelper.saveToJsonInfo(response.body, deviceAddInfo.getDeviceModel() + "_" + req.data.language + "_" + FileSaveHelper.INTRODUCTION_INFO_NAME);

					handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response.data).sendToTarget();
				} catch (BusinessException e) {
					e.printStackTrace();
					handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION).sendToTarget();
				}
			}
		};

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.iotadd_ac_guide, container, false);
	}

	//不支持5G
	private void goNotSupport5GTipPage() {
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iotadd_guide_operate) {
//            dealDeivceConfig();
//            connectWifi();
//            goIotBluetoothConnect();
			dealNextSteps();
		} else if (id == R.id.iotadd_tv_tip) {
			//展示帮助url
			IotDeviceLeadingInfoData.ResponseData.StepsDTO step = currentStep;
			if (step == null || step.help == null || TextUtils.isEmpty(step.help.helpUrl)) {
				return;
			}
			WebViewDialogFragment dialog = WebViewDialogFragment.Companion.newInstance(step.help.helpUrl);
			dialog.show(getChildFragmentManager(), "");
		}
	}

	private void dealNextSteps() {
		Logger.i(TAG, "dealNextSteps");
		if (steps != null && steps.size() > 0) {
			Logger.i(TAG, "currentStep:" + currentStep);
			if (currentStep != null && currentStep.id < steps.size()) {
				Logger.i(TAG, "currentStep id:" + currentStep.id);
				int index = currentStep.id;
				currentStep = steps.get(index);
				String img = currentStep.stepIcon != null && currentStep.stepIcon.size() > 0 ? currentStep.stepIcon.get(0) : "";
				if (!TextUtils.isEmpty(img)) {
					Glide.with(IotGuideFragment.this.getContext()).load(img).into(banner_view);
				}

				if (currentStep.help != null && currentStep.help.helpUrlTitle != null) {
					iotadd_tv_tip.setText(currentStep.help.helpUrlTitle);
				} else {
					iotadd_tv_tip.setText("");
				}
				stepTitle.setText(currentStep.stepTitle);
				stepOperate.setText(currentStep.stepOperate);

			} else {
				dealDeivceConfig();
			}
		}
	}

	private void dealBeforeSteps() {
		Logger.i(TAG, "dealBeforeSteps");
		if (steps != null && steps.size() > 0) {
			Logger.i(TAG, "currentStep:" + currentStep);
			if (currentStep != null && currentStep.id > 1) {
				Logger.i(TAG, "currentStep id:" + currentStep.id);
				int index = currentStep.id - 2;
				currentStep = steps.get(index);
				String img = currentStep.stepIcon != null && currentStep.stepIcon.size() > 0 ? currentStep.stepIcon.get(0) : "";
				if (!TextUtils.isEmpty(img)) {
					Glide.with(IotGuideFragment.this.getContext()).load(img).into(banner_view);
				}
				if (currentStep.help != null && currentStep.help.helpUrlTitle != null) {
					iotadd_tv_tip.setText(currentStep.help.helpUrlTitle);
				} else {
					iotadd_tv_tip.setText("");
				}
				stepTitle.setText(currentStep.stepTitle);
				stepOperate.setText(currentStep.stepOperate);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == BLE_OPEN) {
			if (hasBluetoothPermission()) {//如果有权限，则将权限提示dismiss
				dismissLCAlertDialog();
			}
		}
//		if (resultCode == Activity.RESULT_OK) {
//			int connectedStatus = data.getIntExtra(TipSoftApConnectWifiFragment.SOFTAP_CONNECT_STATUS, 0);
//			Logger.d(TAG, "onActivityResult connectedStatus:" + connectedStatus);
//			if (connectedStatus == 1) {
//				iotConnectPresenter.setDeviceWifi();
//			} else {
//
//			}
//		}
	}

	@Override
	public boolean onBackPressed() {
		if (currentStep != null ) {
				if(currentStep.id == 1 ){//第一个页面时，开始回退
					return false;
				}else{
					dealBeforeSteps();
					return true;
				}
		}else{
			return false;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		finlize();
	}

	public void finlize() {
		iotConnectPresenter.destroy();
		DeviceAddModel.newInstance().setLoop(false);
		countDownTimer.cancel();
	}

	@Override
	public void onDestroyView() {
		dismissLCAlertDialog();
		super.onDestroyView();
	}
}
