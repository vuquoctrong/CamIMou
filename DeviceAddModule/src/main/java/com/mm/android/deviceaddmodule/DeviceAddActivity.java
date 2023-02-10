package com.mm.android.deviceaddmodule;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.View;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.contract.DeviceAddConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.BaseFragment;
import com.mm.android.mobilecommon.base.BaseFragmentActivity;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;
import com.mm.android.mobilecommon.eventbus.event.CommonEvent;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.widget.CommonTitle;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.p_ap.ApBindSuccessFragment;
import com.mm.android.deviceaddmodule.p_ap.ApPairFragment;
import com.mm.android.deviceaddmodule.p_bindsuccess.BindSuccessFragment;
import com.mm.android.deviceaddmodule.p_cloudconnect.CloudConnectFragment;
import com.mm.android.deviceaddmodule.p_devlogin.DevLoginFragment;
import com.mm.android.deviceaddmodule.p_devlogin.DevSecCodeFragment;
import com.mm.android.deviceaddmodule.p_errortip.ErrorTipFragment;
import com.mm.android.deviceaddmodule.p_init.InitFragment;
import com.mm.android.deviceaddmodule.p_init.SecurityCheckFragment;
import com.mm.android.deviceaddmodule.p_softap.DevWifiListFragment;
import com.mm.android.deviceaddmodule.p_softap.oversea.SoftApResultFragment;
import com.mm.android.deviceaddmodule.p_wiredwireless.SmartConfigFragment;
import com.mm.android.deviceaddmodule.p_wiredwireless.WifiPwdFragment;
import com.mm.android.deviceaddmodule.presenter.DeviceAddPresenter;
import com.mm.android.deviceaddmodule.views.popwindow.BasePopWindow;
import com.mm.android.deviceaddmodule.views.popwindow.PopWindowFactory;
import com.mm.android.iotdeviceaddmodule.IotWifiChooseFragment;

import org.greenrobot.eventbus.EventBus;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.mm.android.mobilecommon.common.Constants.PERMISSION_REQUEST_ID;

/**
 * The device main interface is added, and function modules are attached in fragments
 * <p>
 * 添加设备主界面，各功能模块以Fragment形式依附于此类
 */
public class DeviceAddActivity extends BaseFragmentActivity implements DeviceAddConstract.View, CommonTitle.OnTitleClickListener {
	public static final String TAG = DeviceAddActivity.class.getSimpleName();
	private CommonTitle mTitle;                      //标题栏
	DeviceAddConstract.Presenter mPresenter;
	private final String STOP_ADD_DIALOG = "stop_add_dialog";
	LCAlertDialog mLCAlertDialog;
	PopWindowFactory mPopWindowFactory;
	BasePopWindow mMoreOptionPopWindow, mLoadingPopWindow, mTypeChosePopWindow;
	private DefaultPermissionListener defaultPermissionListener;
	private PermissionHelper permissionHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_add);
		initView();
		initData();
	}

	//接收EventBus消息
	@Override
	public void onMessageEvent(BaseEvent event) {
		super.onMessageEvent(event);
		if (event instanceof DeviceAddEvent) {
			String code = event.getCode();
			if (DeviceAddEvent.CONFIG_PAGE_NAVIGATION_ACTION.equals(code)) {
				mPresenter.dispatchPageNavigation();
			} else if (DeviceAddEvent.CHANGE_TO_WIRELESS_ACTION.equals(code)) {
				mPresenter.changeToWireless();
			} else if (DeviceAddEvent.CHANGE_TO_WIRED_ACTION.equals(code)) {
				mPresenter.changeToWired();
			} else if (DeviceAddEvent.CHANGE_TO_SOFTAP_ACTION.equals(code)) {
				mPresenter.changeToSoftAp();
			} else if (DeviceAddEvent.TITLE_MODE_ACTION.equals(code)) {
				Bundle bundle = ((DeviceAddEvent) event).getBundle();
				String titleMode = bundle.getString(DeviceAddEvent.KEY.TITLE_MODE);
				mPresenter.setCurTitleMode(titleMode);
				dispatchTitle(titleMode);
			} else if (DeviceAddEvent.SHOW_LOADING_VIEW_ACTION.equals(code)) {
				synchronized (DeviceAddActivity.this) {
					if (mLoadingPopWindow != null && !this.isFinishing()) {
						if (!mLoadingPopWindow.isShowing()) {
							mLoadingPopWindow.showAsDropDown(mTitle);
						}
					} else if (!this.isFinishing() && hasWindowFocus()) {
						mLoadingPopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.LOADING);
					}
				}
			} else if (DeviceAddEvent.DISMISS_LOADING_VIEW_ACTION.equals(code)) {
				synchronized (DeviceAddActivity.this) {
					LogUtil.debugLog(TAG, "LoadingDismiss-->" + mLoadingPopWindow);
					if (mLoadingPopWindow != null) {
						mLoadingPopWindow.dismiss();
					}
				}
			} else if (DeviceAddEvent.DESTROY_ACTION.equals(code)) {
				destroy();
			} else if (DeviceAddEvent.OFFLINE_CONFIG_SUCCESS_ACTION.equals(code)) {
				offlineConfigSucceed();
			} else if (DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST_DISABLE_ACTION.equals(code)) {
				mTitle.setEnabled(false, CommonTitle.ID_RIGHT);
			} else if (DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST_ENABLE_ACTION.equals(code)) {
				mTitle.setEnabled(true, CommonTitle.ID_RIGHT);
			} else if (DeviceAddEvent.SHOW_TYPE_CHOSE_ACTION.equals(code)) {
				mTypeChosePopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.CHOSETYPE);
			} else if (DeviceAddEvent.GET_DEVICE_SN.equals(code)) {
//                mPresenter.startSearchService();
			}
		}
	}

	/**
	 * Initialize the page layout
	 * <p>
	 * 初始化页面布局
	 */
	private void initView() {
		mPopWindowFactory = new PopWindowFactory();
		initTitle();
	}

	private void initTitle() {
		mTitle = findViewById(R.id.title);
		mTitle.initView(R.drawable.mobile_common_title_back, 0, R.string.add_device_title);
		mTitle.setOnTitleClickListener(this);
	}


	/**
	 * Initialize the page data
	 * <p>
	 * 初始化页面数据
	 */
	private void initData() {
		mPresenter = new DeviceAddPresenter(this);
		//添加逻辑入库
		mPresenter.dispatchIntentData(getIntent());
		permissionHelper = new PermissionHelper(this);
		defaultPermissionListener = new DefaultPermissionListener() {
			@Override
			public void onGranted() {
				mPresenter.getGPSLocation();
			}

			@Override
			public boolean onDenied() {
				mPresenter.getGPSLocation();
				return false;
			}
		};
		permissionHelper.requestLocationWithBackgroundPermission(defaultPermissionListener);
//        permissionHelper.requestPermissions(new String[]{
//                ACCESS_COARSE_LOCATION}, defaultPermissionListener);
	}

	/**
	 * Callback of dynamic permission application
	 *
	 * @param requestCode  Request code
	 * @param permissions  Privilege set
	 * @param grantResults Authorization result set
	 *                     <p>
	 *                     动态权限申请回调
	 * @param requestCode  请求码
	 * @param permissions  权限集合
	 * @param grantResults 授权结果集
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_ID) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (defaultPermissionListener != null) {
					defaultPermissionListener.onGranted();
				}
			} else {
				if (defaultPermissionListener != null && permissionHelper != null) {
					if (!defaultPermissionListener.onDenied()) {      //返回false，默认处理，向用户说明权限的必要性并引导
						permissionHelper.gotoSettingPage(permissions, defaultPermissionListener);
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPresenter = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		if (curFragment != null) {
			curFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}

	private void goBack() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		int entryCount = getSupportFragmentManager().getBackStackEntryCount();
		boolean isWifiOfflineMode = DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode();
		if (curFragment == null
				|| entryCount <= 0
				|| (isWifiOfflineMode && entryCount == 1)
				|| curFragment instanceof ApBindSuccessFragment) {
			if (mLoadingPopWindow != null) {
				mLoadingPopWindow.dismiss();
			}
			if (curFragment instanceof ApBindSuccessFragment) {
				if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) {
					showStopDevAddDialog(true);
				}
				return;
			}
			destroy();
		} else {
			if (curFragment instanceof CloudConnectFragment
					|| curFragment instanceof InitFragment
					|| curFragment instanceof DevLoginFragment
					|| curFragment instanceof DevSecCodeFragment
					|| curFragment instanceof SmartConfigFragment
					|| curFragment instanceof DevWifiListFragment
					|| curFragment instanceof SoftApResultFragment
					|| curFragment instanceof SecurityCheckFragment
					|| curFragment instanceof ApPairFragment
			) {//以上界面退出需二次确认
				showStopDevAddDialog(curFragment instanceof ApPairFragment);
			} else {
				if (!((BaseFragment) curFragment).onBackPressed()) {//子类未消耗返回事件
					if (curFragment instanceof ErrorTipFragment) {
						showStopDevAddDialog(false);
						return;
					} else if (curFragment instanceof BindSuccessFragment) {
						destroy();
						return;
					}
					if (mLoadingPopWindow != null) {
						mLoadingPopWindow.dismiss();
					}

					//从输入wifi密码页面返回，统一回到电源页面
					if (curFragment instanceof WifiPwdFragment) {
						getSupportFragmentManager().popBackStackImmediate(PageNavigationHelper.TIP_POWER_FRAGMENT_TAG, 0);
						return;
					}

					getSupportFragmentManager().popBackStack();          //退回到上个界面
				}
			}
		}
	}

	private void dispatchTitle(String titleMode) {
		mTitle.setVisibleRight(View.VISIBLE);
		if (DeviceAddHelper.TitleMode.MORE.name().equals(titleMode)
				|| DeviceAddHelper.TitleMode.MORE2.name().equals(titleMode)
				|| DeviceAddHelper.TitleMode.MORE3.name().equals(titleMode)
				|| DeviceAddHelper.TitleMode.MORE4.name().equals(titleMode)) {
			mTitle.setIconRight(R.drawable.common_icon_nav_more);
		} else if (DeviceAddHelper.TitleMode.REFRESH.name().equals(titleMode)) {
			mTitle.setIconRight(R.drawable.common_image_nav_refresh_selector);
		} else if (DeviceAddHelper.TitleMode.SHARE.name().equals(titleMode)) {
			mTitle.setTitleCenter(R.string.mobile_common_device);
			mTitle.setIconRight(R.drawable.mobile_common_share_selector);
			boolean isOversea = ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA;
			boolean canBeShare = mPresenter.canBeShare();
			mTitle.setVisibleRight(isOversea && canBeShare ? View.VISIBLE : View.GONE);
		} else if (DeviceAddHelper.TitleMode.FREE_CLOUD_STORAGE.name().equals(titleMode)) {
			mTitle.setVisibleLeft(View.GONE);
			mTitle.setVisibleRight(View.GONE);
		} else if (DeviceAddHelper.TitleMode.MODIFY_DEVICE_NAME.name().equals(titleMode)) {
			mTitle.setTitleCenter(R.string.mobile_common_modify_device_pwd);
			mTitle.setVisibleRight(View.GONE);
		} else {
			mTitle.setIconRight(0);
		}
	}

	/**
	 * Stop adding a flow dialog box
	 * <p>
	 * 停止添加流程提示框
	 */
	private void showStopDevAddDialog(final boolean isExitApAdd) {
		dismissLCAlertDialog();
		LCAlertDialog.Builder builder = new LCAlertDialog.Builder(this);
		builder.setTitle(R.string.add_device_confrim_to_quit);
		builder.setMessage(R.string.add_device_not_complete_tip);
		builder.setCancelButton(R.string.common_cancel, null);
		builder.setConfirmButton(R.string.common_confirm,
				new LCAlertDialog.OnClickListener() {
					@Override
					public void onClick(LCAlertDialog dialog, int which,
										boolean isChecked) {
						destroy();
					}
				});

		mLCAlertDialog = builder.create();
		mLCAlertDialog.show(getSupportFragmentManager(),
				STOP_ADD_DIALOG);
	}

	private void dismissLCAlertDialog() {
		if (mLCAlertDialog != null && mLCAlertDialog.isVisible()) {
			mLCAlertDialog.dismissAllowingStateLoss();
			mLCAlertDialog = null;
		}
	}

	@Override
	public void onCommonTitleClick(int id) {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		switch (id) {
			case CommonTitle.ID_LEFT:
				goBack();
				break;
			case CommonTitle.ID_RIGHT: {
				if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.MORE.name())) {
					mMoreOptionPopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.OPTION1);
				} else if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.MORE2.name())) {
					mMoreOptionPopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.OPTION2);
				} else if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.MORE3.name())) {
					mMoreOptionPopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.OPTION3);
				} else if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.MORE4.name())) {
					mMoreOptionPopWindow = mPopWindowFactory.createPopWindow(this, mTitle, PopWindowFactory.PopWindowType.OPTION4);
				} else if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.REFRESH.name())) {
					EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST));
				} else if (mPresenter.getCurTitleMode().equals(DeviceAddHelper.TitleMode.SHARE.name())) {
					mPresenter.getDeviceShareInfo();
				}
			}
		}
	}

	@Override
	public Context getContextInfo() {
		return this;
	}

	@Override
	public boolean isViewActive() {
		return !isActivityDestory();
	}

	@Override
	public void showToastInfo(String msg) {
		toast(msg);
	}

	@Override
	public void showToastInfo(int msgId) {
		toast(msgId);
	}


	@Override
	public void showProgressDialog() {
		EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
	}

	@Override
	public void cancelProgressDialog() {
		EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DISMISS_LOADING_VIEW_ACTION));
	}

	@Override
	public void setTitle(int titleId) {
		mTitle.setTitleCenter(titleId);
	}

	@Override
	public void goScanPage() {
		PageNavigationHelper.gotoScanPage(this);
	}

	@Override
	public void goDispatchPage() {
		PageNavigationHelper.gotoDispatchPage(this);
	}

	@Override
	public void goHubPairPage(String sn, String hubType) {
		PageNavigationHelper.gotoHubGuide1Page(this, sn, hubType);
	}

	@Override
	public void goApConfigPage(boolean hasSelecteGateway) {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoGatewayListPage(curFragment, hasSelecteGateway);

	}

	@Override
	public void goTypeChoosePage() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoTypeChoosePage(curFragment);
	}

	@Override
	public void goWiredWirelessPage(boolean isWifi) {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoPowerTipPage(curFragment, isWifi);
	}

	@Override
	public void goWiredWirelessPageNoAnim(boolean isWifi) {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoPowerTipPageNoAnim(curFragment, isWifi);
	}

	@Override
	public void goSoftApPage() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoSoftApTipPage(curFragment);
	}

	@Override
	public void goSoftApPageNoAnim() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoSoftApTipPageNoAnim(curFragment);
	}


	@Override
	public void goIMEIInputPage() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoIMEIInputPage(curFragment);
	}

	@Override
	public void goNotSupportBindTipPage() {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoErrorTipPage(curFragment, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND);
	}


	@Override
	public void goOfflineConfigPage(String sn, String devModelName, String imei) {
		PageNavigationHelper.gotoOfflineConfigPage(this, sn, devModelName, imei);
	}


	@Override
	public void goInitPage(DeviceInitInfo device_net_info_ex) {
		final Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoInitPage(curFragment, device_net_info_ex);
	}

	@Override
	public void goCloudConnectPage() {
		Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.content);
		PageNavigationHelper.gotoCloudConnectPage(curFragment);
	}

	@Override
	public void completeAction(boolean isAp) {

		DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
		boolean isDeviceDetail = deviceAddInfo.isDeviceDetail();

		String code = CommonEvent.AP_PAIR_SUCCEED_2_MAIN_ACTION;
		if (isDeviceDetail) {
			code = CommonEvent.AP_PAIR_SUCCEED_2_MID_ACTION;
		}

		if (isAp && deviceAddInfo.getGatewayInfo() != null) {
			String deviceId = deviceAddInfo.getGatewayInfo().getSn();
			String apId = deviceAddInfo.getDeviceSn();

			Bundle bundle = new Bundle();
			bundle.putString(LCConfiguration.Device_ID, deviceId);
			bundle.putString(LCConfiguration.AP_ID, apId);
			CommonEvent commonEvent = new CommonEvent(code);
			commonEvent.setBundle(bundle);
			EventBus.getDefault().post(commonEvent);
		}

		destroy();

		if (!isDeviceDetail) {
			ProviderManager.getDeviceAddCustomProvider().goHomePage(getContextInfo());
		}
	}


	/**
	 * Proactively release the resource and end the activity without relying on the onDestroy method to release the resource (the timing of the onDestroy method is not controllable).
	 * <p>
	 * 主动释放资源、结束activity，而不依赖于onDestroy方法来释放资源（onDestroy方法时机不可控）
	 */
	private void destroy() {
		finish();
		mPresenter.unInit();
		if (mLoadingPopWindow != null) {
			if (mLoadingPopWindow.isShowing()) {
				mLoadingPopWindow.dismiss();
			}
			mLoadingPopWindow = null;
		}
	}

	/**
	 * If the offline network configuration is successful, the upper level page is displayed
	 * <p>
	 * 离线配网成功，返回上一级页面
	 */
	private void offlineConfigSucceed() {
		setResult(RESULT_OK);
		finish();
		mPresenter.unInit();
	}
}
