package com.mm.android.deviceaddmodule.p_ap.hubap;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.contract.HubApGuide1Constract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.deviceaddmodule.presenter.HubApGuide1Presenter;

public class HubapGuide1Fragment extends BaseTipFragment implements HubApGuide1Constract.View {
    HubApGuide1Constract.Presenter mPresenter;
    Handler mHandler;

    public static HubapGuide1Fragment newInstance(String sn, String hubType) {
        HubapGuide1Fragment fragment = new HubapGuide1Fragment();
        Bundle args = new Bundle();
        args.putString(LCConfiguration.DEVICESN_PARAM, sn);
        args.putString(LCConfiguration.HUB_TYPE_PARAM, hubType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mHelpTxt.setVisibility(View.GONE);
        mTipTxt.setVisibility(View.GONE);
        mTipImg.setVisibility(View.GONE);
        mNextBtn.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new HubApGuide1Presenter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isDestoryView())
                        mPresenter.checkDevIntroductionInfo(getArguments().getString(LCConfiguration.HUB_TYPE_PARAM));
                }
            }, 100);

        }
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoHubGuide2Page(this);
    }

    @Override
    protected void helpAction() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_RESET);
    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }

    @Override
    public void updateTip(String tipImg, String tipTxt, String helpTxt) {
        if (!TextUtils.isEmpty(tipTxt)) {
            mTipTxt.setText(tipTxt);
        }
        if (!TextUtils.isEmpty(tipTxt)) {
            Glide.with(HubapGuide1Fragment.this.getContext()).load(tipImg).into(mTipImg);
        }
        if (!TextUtils.isEmpty(tipTxt)) {
            mHelpTxt.setVisibility(View.VISIBLE);
            mHelpTxt.setText(helpTxt);
        }
    }

    @Override
    public void showInfoView() {
        mTipTxt.setVisibility(View.VISIBLE);
        mTipImg.setVisibility(View.VISIBLE);
        mNextBtn.setVisibility(View.VISIBLE);
    }
}
