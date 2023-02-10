package com.mm.android.deviceaddmodule.p_inputsn;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.DispatchContract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.deviceaddmodule.presenter.DispatchPresenter;
import com.mm.android.deviceaddmodule.views.AddBoxTipDialog;

public class DeviceDispatchFragment extends BaseDevAddFragment implements DispatchContract.View {

	private DispatchContract.Presenter mPresenter;
	private boolean isFirst = true;

	public static DeviceDispatchFragment newInstance() {
		DeviceDispatchFragment fragment = new DeviceDispatchFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void initView(View view) {

	}

	@Override
	protected void initData() {
		mPresenter = new DispatchPresenter(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_device_dispatch, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (isFirst || DeviceDispatchHelper.isReAdd()) {
			mPresenter.dispatchResult();
			DeviceDispatchHelper.reset();
		} else {
			if (getActivity() != null) getActivity().finish();
		}
		isFirst = false;
	}

	@Override
	public void goTypeChoosePage() {
		PageNavigationHelper.gotoTypeChoosePage(this, false);
	}

	@Override
	public void goNotSupportBindTipPage() {
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND, null, false);
	}

	@Override
	public void goOtherUserBindTipPage() {
		PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER, null, false);
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
		PageNavigationHelper.gotoCloudConnectPage(this, false);
	}

	@Override
	public void goDeviceLoginPage() {
		PageNavigationHelper.gotoDevLoginPage(this, false);
	}

	@Override
	public void goSecCodePage() {
		PageNavigationHelper.gotoDevSecCodePage(this, false);
	}

	@Override
	public void goDeviceBindPage() {
		PageNavigationHelper.gotoDeviceBindPage(this, false);
	}

	@Override
	public void goIMEIInputPage() {
		PageNavigationHelper.gotoIMEIInputPage(this, false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DeviceDispatchHelper.reset();
	}
}
