package com.mm.android.deviceaddmodule.p_ap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

public class TipApLightFragment extends BaseTipFragment {

    public static TipApLightFragment newInstance() {
        TipApLightFragment fragment = new TipApLightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mConfirmCheck.setVisibility(View.VISIBLE);
        mNextBtn.setEnabled(mConfirmCheck.isChecked());
        mTipImg.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceIntroductionInfo deviceIntroductionInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_PAIR_OPERATION_INTRODUCTION);
            String tipImg = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_PAIR_STATUS_IMAGE);
            String helpTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_RESET_GUIDE_INTRODUCTION);
            String confirmTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_PAIR_CONFIRM_INTRODUCTION);
            if (!TextUtils.isEmpty(tipImg)) {
                Glide.with(TipApLightFragment.this.getContext()).load(tipImg).into(mTipImg);
            }
            if (!TextUtils.isEmpty(tipTxt)) {
                mTipTxt.setText(tipTxt);
            }
            if (!TextUtils.isEmpty(helpTxt)) {
                mHelpTxt.setVisibility(View.VISIBLE);
                mHelpTxt.setText(helpTxt);
            }
            if (!TextUtils.isEmpty(confirmTxt)) {
                mConfirmCheck.setText(confirmTxt);
            }
        }
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoApPairPage(this);
    }

    @Override
    protected void helpAction() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_RESET);
    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }
}
