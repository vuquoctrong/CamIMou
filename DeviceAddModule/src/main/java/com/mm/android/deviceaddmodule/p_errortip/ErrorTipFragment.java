package com.mm.android.deviceaddmodule.p_errortip;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.ErrorTipConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.deviceaddmodule.presenter.ErrorTipPresenter;

/**
 * Device addition error page
 * <p>
 * 设备添加错误提示页
 */
public class ErrorTipFragment extends BaseDevAddFragment implements ErrorTipConstract.View, View.OnClickListener {
	ErrorTipConstract.Presenter mPresenter;
	public static String ERROR_PARAMS = "error_params";
	ImageView mTipImage;
	TextView mTipTxt, mTipTxt2, mHelpLinkTxt, mHelpPhoneTv;
	View mHelpLayout;
	private String defaultTip;

	private PermissionHelper mPermissionHelper;

	public static ErrorTipFragment newInstance(int errorCode, String defaultTip) {
		ErrorTipFragment fragment = new ErrorTipFragment();
		Bundle args = new Bundle();
		args.putInt(ERROR_PARAMS, errorCode);
		args.putString(LCConfiguration.ERROR_TIP, defaultTip);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_error_tip, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mPresenter.isUserBindTipPage()) {
			DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.BLANK);
		} else {
			DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
		}
	}

	protected void initView(View view) {
		mTipImage = view.findViewById(R.id.tip_img);
		mTipTxt = view.findViewById(R.id.tip_txt);
		mTipTxt2 = view.findViewById(R.id.tip_txt2);
		mHelpLinkTxt = view.findViewById(R.id.help_link);
		mHelpLinkTxt.setOnClickListener(this);
		mHelpLayout = view.findViewById(R.id.ll_help);
		mHelpPhoneTv = view.findViewById(R.id.tv_help_phone);
		mHelpLayout.setVisibility(View.GONE);

	}

	protected void initData() {
		mPresenter = new ErrorTipPresenter(this);
		if (getArguments() != null) {
			int errorcode = getArguments().getInt(ERROR_PARAMS);
			defaultTip = getArguments().getString(LCConfiguration.ERROR_TIP);
			mPresenter.dispatchError(errorcode);
		}
	}

	@Override
	public Fragment getParent() {
		return this;
	}

	@Override
	public boolean onBackPressed() {
		if (mPresenter.isUserBindTipPageByBind()) {
			if (getActivity() != null) getActivity().finish();
		} else if (mPresenter.isResetPage()) {
			if (getActivity() != null)
				getActivity().getSupportFragmentManager().popBackStack();          //退回到上个界面
		}
		return mPresenter.isResetPage();
	}

	@Override
	public void updateInfo(String info, String img, boolean isNeedMatch) {
		if (isNeedMatch) {
			setImageMatchScreen();
		}
		if (!TextUtils.isEmpty(img)) {
			Glide.with(ErrorTipFragment.this).load(img).into(mTipImage);
		}
		if (defaultTip != null) {
			mTipTxt.setText(defaultTip);
		} else if (!TextUtils.isEmpty(info)) {
			mTipTxt.setText(info);
		}
	}

	@Override
	public void updateInfo(int infoId, int tip2Id, String img, boolean isNeedMatch) {
		updateInfo(infoId, img, isNeedMatch);
		if (tip2Id != 0)
			mTipTxt2.setText(tip2Id);

	}

	@Override
	public void updateInfo(int infoId, String img, boolean isNeedMatch) {
		if (isNeedMatch) {
			setImageMatchScreen();
		}
		if (!TextUtils.isEmpty(img)) {
			Glide.with(ErrorTipFragment.this).load(img).into(mTipImage);
		}
		if (defaultTip != null) {
			mTipTxt.setText(defaultTip);
		} else {
			mTipTxt.setText(infoId);
		}
	}

	@Override
	public void hideTipTxt() {
		mTipTxt.setVisibility(View.GONE);
		mTipTxt2.setVisibility(View.GONE);
	}

	@Override
	public void hideHelp() {
		mHelpLayout.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.help_link) {
			ProviderManager.getDeviceAddCustomProvider().goFAQWebview(getActivity());
		}
	}

	private void setImageMatchScreen() {
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTipImage.getLayoutParams();
		layoutParams.height = /*LinearLayout.LayoutParams.WRAP_CONTENT*/UIUtils.dp2px(getContextInfo(), 300);
		layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
		layoutParams.topMargin = 0;
		mTipImage.setLayoutParams(layoutParams);
		mTipImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
	}
}
