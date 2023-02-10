package com.mm.android.deviceaddmodule.p_softap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.TipSoftApConnectWifiConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.IotSoftApHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.eventbus.event.CloseTimeFilterEvent;
import com.mm.android.mobilecommon.eventbus.event.NoticeToBackEvent;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.TipSoftApConnectWifiPresenter;
import com.mm.android.deviceaddmodule.services.TimeFilterService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Soft AP added boot prompt page - automatically connect to wifi
 * <p>
 * 软AP添加引导提示页-自动连接wifi
 */
public class TipSoftApConnectWifiFragment extends BaseDevAddFragment implements TipSoftApConnectWifiConstract.View {
	TipSoftApConnectWifiConstract.Presenter mPresenter;
	TextView mShowTipTv, mWifiNameTv, mWifiPwdTv, mShowTip1Tv;
	TextView mGotoWifiSetting, mAboutWifiPwdTv;
	LinearLayout mWifiPwdLayout;
	ImageView mCopyIv;
	private boolean mIsBack;  //是否需要返回到上一页（主要是应用在长时间未连接热点，点击通知栏需要返回到上一页）
	public static String SOFTAP_ISIOT = "softap_isIot";
	public static String SOFTAP_CONNECT_STATUS = "softap_connect_status";
	public static int REQUEST_OPEN_WIFI = 0x1001;
	private boolean isIot;
	private boolean mIsAutoConnectWIFI = true;  //是否需要自动连接wifi

	public static TipSoftApConnectWifiFragment newInstance(boolean isIot) {
		TipSoftApConnectWifiFragment fragment = new TipSoftApConnectWifiFragment();
		Bundle args = new Bundle();
		args.putBoolean(SOFTAP_ISIOT, isIot);
		fragment.setArguments(args);
		return fragment;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(NoticeToBackEvent event) {
		mIsBack = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mIsAutoConnectWIFI = PageNavigationHelper.isAutoConnectNetwork(getContext());
//        mIsAutoConnectWIFI = !getArguments().getBoolean(LCConfiguration.IS_SOFT_AP_CONNECTED_FAILED,false);
		return inflater.inflate(R.layout.fragment_tip_soft_ap_connect_wifi, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void initView(View view) {
		mShowTipTv = view.findViewById(R.id.tv_image_show_tip);
		mShowTipTv.setText(R.string.add_device_wait_to_connect_wifi);
		mShowTip1Tv = view.findViewById(R.id.tv_show_tip);
		mWifiNameTv = view.findViewById(R.id.tv_wifi_name);

		mWifiPwdLayout = view.findViewById(R.id.layout_wifi_pwd);
		mWifiPwdTv = view.findViewById(R.id.tv_wifi_pwd);
		mWifiPwdTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		mWifiPwdTv.getPaint().setAntiAlias(true);//抗锯齿
		mWifiPwdTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPresenter.copyWifiPwd();
			}
		});

		mCopyIv = view.findViewById(R.id.iv_copy);
		mCopyIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPresenter.copyWifiPwd();
			}
		});

		mGotoWifiSetting = (TextView) view.findViewById(R.id.tv_goto_connect);
		mGotoWifiSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//取消之前的定时任务
				EventBus.getDefault().post(new CloseTimeFilterEvent());
				Intent intent = new Intent(getActivity(), TimeFilterService.class);
				intent.putExtra(LCConfiguration.SSID, mPresenter.getHotSSID());
				getActivity().startService(intent);
//                  CommonHelper.gotoWifiSetting(getActivity());
				Intent wifiIntent = new Intent();
				wifiIntent.setFlags(intent.FLAG_ACTIVITY_NO_HISTORY | intent.FLAG_ACTIVITY_NEW_TASK);
				wifiIntent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
				startActivity(wifiIntent);
//                goSecurityCheckPage();
			}
		});
		mAboutWifiPwdTv = (TextView) view.findViewById(R.id.tv_about_wifi_pwd);
		mAboutWifiPwdTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PageNavigationHelper.gotoErrorTipPage(TipSoftApConnectWifiFragment.this, DeviceAddHelper.ErrorCode.COMMON_ERROR_ABOUT_WIFI_PWD);
			}
		});
	}

	protected void initData() {
		if (getArguments() != null) {
			isIot = getArguments().getBoolean(SOFTAP_ISIOT);
		}
		mPresenter = new TipSoftApConnectWifiPresenter(this);
//        mPresenter.connectWifiAction(true);

		gotoConnectWifi();

		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name())) {
			DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
		} else {
			DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
		}
	}

	@Override
	public void updateWifiName(String wifiName) {
		mWifiNameTv.setText(wifiName);
	}

	@Override
	public void updateConnectFailedTipText(String wifiName, String wifiPwd, boolean isSupportAddBySc, boolean isManualInput) {
		mGotoWifiSetting.setVisibility(View.VISIBLE);
		mAboutWifiPwdTv.setVisibility(isSupportAddBySc ? View.VISIBLE : View.GONE);
		mShowTipTv.setText(getString(R.string.add_device_connect_wifi_failed));
		mShowTipTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adddevice_icon_help, 0);
		mShowTipTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PageNavigationHelper.gotoErrorTipPage(TipSoftApConnectWifiFragment.this, DeviceAddHelper.ErrorCode.COMMON_ERROR_CONNECT_FAIL);
			}
		});
		mShowTip1Tv.setText(getString(isSupportAddBySc ? R.string.add_device_wait_to_connect_wifi_failed_sc : R.string.add_device_wait_to_connect_wifi_failed, wifiName));
		mWifiPwdLayout.setVisibility(isSupportAddBySc ? View.VISIBLE : View.GONE);
		mWifiPwdTv.setText(wifiPwd);
		mWifiPwdTv.setClickable(!isManualInput);
		mCopyIv.setVisibility(isManualInput ? View.GONE : View.VISIBLE);
	}

	@Override
	protected IntentFilter createBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(LCConfiguration.CONNECTIVITY_CHAGET_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		return filter;
	}

	@Override
	protected void onReceive(Context context, Intent intent) {
		if (isDestoryView())
			return;
		if (LCConfiguration.CONNECTIVITY_CHAGET_ACTION.equals(intent.getAction())
				|| WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())
				|| WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			LogUtil.debugLog("bz", "onReceive");
			final int netWorkState = Utils4AddDevice.getNetWorkState(getContextInfo());
			LogUtil.debugLog("bz", "netWorkState : " + netWorkState);
			mPresenter.dispatchHotConnected();
		}
	}

	@Override
	public void goSecurityCheckPage() {
		if (isIot) {
			IotSoftApHelper.getInstance().setDeviceWifi(handler);

//			Intent i = new Intent();
//			i.putExtra(SOFTAP_CONNECT_STATUS, 1);
//			if (getTargetFragment() != null) {
//				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
//			}
//            getActivity().getSupportFragmentManager().popBackStack();
		} else {
			PageNavigationHelper.gotoSecurityCheckPage(this);
		}
	}

	final LCBusinessHandler handler = new LCBusinessHandler() {

		@Override
		public void handleBusiness(Message msg) {

			if (msg.what == HandleMessageCode.HMC_SUCCESS) {
				PageNavigationHelper.gotoCloudConnectPage(TipSoftApConnectWifiFragment.this);
			} else {
				Toast.makeText(requireContext(), getString(R.string.add_device_softap_connect_device_hotspot_fail), Toast.LENGTH_LONG).show();
				if (getActivity() == null)
					return;
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	};


	@Override
	public void onResume() {
		super.onResume();
		if (mIsBack) {
			if (getActivity() == null)
				return;
			getActivity().getSupportFragmentManager().popBackStack();
			mIsBack = false;
		} else {
			if (CommonHelper.isAndroidQOrLater())  //Q以上系统在没有授予后台定位权限时，后台无法获取到SSID，需要回到前台再获取下
				mPresenter.dispatchHotConnected();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mPresenter != null) {
			mPresenter.finilize();
		}

		cancelProgressDialog();
		//取消定时任务
		EventBus.getDefault().post(new CloseTimeFilterEvent());
	}

	@Override
	public void gotoCloudConnectPage() {
		PageNavigationHelper.gotoCloudConnectPage(this);
	}

	@Override
	public void openWifiPannel() {
		Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
		this.startActivityForResult(panelIntent, REQUEST_OPEN_WIFI);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OPEN_WIFI) {
			if (mPresenter.isWifiEnable()) {
				gotoConnectWifi();
			} else {
				if (CommonHelper.isAndroidQOrLater())  //Q以上系统在没有授予后台定位权限时，后台无法获取到SSID，需要回到前台再获取下
				{
					mPresenter.dispatchHotConnected();
				}
			}
		}
	}

	private void gotoConnectWifi() {
		boolean isNeedConnectBynetreq = CommonHelper.needConnectToWifiByNetworkRequest(getActivity());
		LogUtil.debugLog("bz", "gotoConnectWifi : " + mIsAutoConnectWIFI + " ,isNeedConnectBynetreq:" + isNeedConnectBynetreq);
		if (mIsAutoConnectWIFI) {
			if (isNeedConnectBynetreq) {
				mPresenter.connectToWifiByNetworkRequest();
			} else {
				mPresenter.connectWifiAction(true);
			}
		} else {
			mGotoWifiSetting.setVisibility(View.VISIBLE);
			mAboutWifiPwdTv.setVisibility(mPresenter.isSupportAddBySc() ? View.VISIBLE : View.GONE);
			mShowTipTv.setText(getString(R.string.soft_ap_auto_connect_failed_tip));
			mShowTipTv.setGravity(Gravity.START);
			PageNavigationHelper.setIsAutoConnectNetwork(true);
		}
	}
}
