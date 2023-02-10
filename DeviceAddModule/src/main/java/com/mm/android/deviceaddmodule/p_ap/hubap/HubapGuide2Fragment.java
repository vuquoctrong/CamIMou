package com.mm.android.deviceaddmodule.p_ap.hubap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

public class HubapGuide2Fragment extends BaseTipFragment {

    public static HubapGuide2Fragment newInstance() {
        HubapGuide2Fragment fragment = new HubapGuide2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        mHelpTxt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceIntroductionInfo deviceIntroductionInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_ACCESSORY_MODE_PAIR_OPERATION_INTRODUCTION);
            String tipImg = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.HUB_ACCESSORY_MODE_PAIR_STATUS_IMAGE);
            String helpTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_ACCESSORY_MODE_RESET_GUIDE_INTRODUCTION);
            if (!TextUtils.isEmpty(tipTxt))
                mTipTxt.setText(tipTxt);
            if (!TextUtils.isEmpty(tipImg))
              Glide.with(HubapGuide2Fragment.this.getContext()).load(tipImg).into(mTipImg);
            if (!TextUtils.isEmpty(helpTxt)) {
                mHelpTxt.setVisibility(View.VISIBLE);
                mHelpTxt.setText(helpTxt);
            }

        }
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoHubGuide3Page(this);
    }

    @Override
    protected void helpAction() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_HUB_AP_RESET);
    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }
}
