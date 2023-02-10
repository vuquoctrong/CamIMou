package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * Device light prompt page
 *
 * 设备灯光提示页
 */
public class TipLightFragment extends BaseTipFragment implements View.OnClickListener {


    public static TipLightFragment newInstance() {
        TipLightFragment fragment = new TipLightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoSoundTipPage(this);
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

    @Override
    protected void initView(View view) {
        super.initView(view);
        mConfirmCheck.setVisibility(View.VISIBLE);
        mNextBtn.setEnabled(false);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo=DeviceAddModel.newInstance().getDeviceInfoCache();
        if(DeviceAddInfo.ConfigMode.LAN.name().equalsIgnoreCase(deviceAddInfo.getConfigMode())
                || !deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name()))
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        }
        DeviceIntroductionInfo deviceIntroductionInfo = deviceAddInfo.getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipImage = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_GUIDING_LIGHT_IMAGE);
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_CONFIG_INTRODUCTION);
            String checkTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_CONFIG_CONFIRM_INTRODUCTION);
            String helpTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.WIFI_MODE_RESET_GUIDE_INTRODUCTION);
            if (!TextUtils.isEmpty(tipImage)) {
                Glide.with(TipLightFragment.this.getContext()).load(tipImage).into(mTipImg);
            }
            if (!TextUtils.isEmpty(tipTxt)) {
                mTipTxt.setText(tipTxt);

            }
            if (!TextUtils.isEmpty(checkTxt)) {
                mConfirmCheck.setText(checkTxt);
            }
            if (!TextUtils.isEmpty(helpTxt)) {
                mHelpTxt.setVisibility(View.VISIBLE);
                mHelpTxt.setText(helpTxt);
            }
        }
    }
}
