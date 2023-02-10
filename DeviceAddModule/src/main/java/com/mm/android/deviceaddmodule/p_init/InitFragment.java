package com.mm.android.deviceaddmodule.p_init;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.InitContract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.presenter.InitPresenter;

/**
 * Device initialization page
 * <p>
 * 设备初始化页面
 */
public class InitFragment extends BaseDevAddFragment implements InitContract.View, View.OnClickListener {
	static String DEVICE_PARAM = "device_param";
	InitContract.Presenter mPresenter;
	private ClearPasswordEditText mPasswordNewEdt;
	;
	private TextView mInitTv;
	long mEventStartTime;       //统计开始时间

	public static InitFragment newInstance(DeviceInitInfo device_net_info_ex) {
		InitFragment fragment = new InitFragment();
		Bundle args = new Bundle();
		args.putSerializable(DEVICE_PARAM, device_net_info_ex);
		fragment.setArguments(args);
		return fragment;
	}

	private final TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			String pwd = StringUtils.strPsswordFilter(s.toString());
			if (!pwd.equals(s.toString())) {
				mPasswordNewEdt.setText(pwd);
				mPasswordNewEdt.setSelection(pwd.length());
			}
			mInitTv.setEnabled(pwd.length() >= 8);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_init, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
		mEventStartTime = System.currentTimeMillis();
	}

	protected void initView(View view) {
		setProgressDialogCancelable(false);
		mPasswordNewEdt = view.findViewById(R.id.et_pwd_new);
		mPasswordNewEdt.openEyeMode(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA);
		mInitTv = view.findViewById(R.id.tv_init);
		mInitTv.setEnabled(false);
		mInitTv.setOnClickListener(this);
		mPasswordNewEdt.addTextChangedListener(mTextWatcher);
	}

	protected void initData() {
		LogUtil.debugLog("InitFragment", "initData");
		mPresenter = new InitPresenter(this);
		if (getArguments() != null) {
			DeviceInitInfo device_net_info_ex = (DeviceInitInfo) getArguments().getSerializable(DEVICE_PARAM);
			mPresenter.setDeviceEX(device_net_info_ex);
			mPresenter.checkDevice();
		}
		mPresenter.playTipSound();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPresenter.recycle();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_init) {
			if (mPresenter.isPwdValid()) {
				mPresenter.startDevInitByIp();
			}
		}
	}

	@Override
	public int getMusicRes() {
		return R.raw.voiceprompt_lc_device_psw;
	}

	@Override
	public String getInitPwd() {
		return mPasswordNewEdt.getText().toString();
	}

	@Override
	public void goSoftAPWifiListPage() {
		PageNavigationHelper.gotoSoftApWifiListPage(this);
	}

	@Override
	public void goConnectCloudPage() {
		PageNavigationHelper.gotoCloudConnectPage(this);
	}

	@Override
	public void goErrorTipPage() {
		//toast(R.string.add_device_init_failed_tip);
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.INIT_ERROR_INIT_FAILED);
	}
}
