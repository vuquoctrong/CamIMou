package com.mm.android.deviceaddmodule.p_ap.hubap;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.eventbus.event.CommonEvent;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

public class HubapGuide3Fragment extends BaseTipFragment {

    public static HubapGuide3Fragment newInstance() {
        HubapGuide3Fragment fragment = new HubapGuide3Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mHelpTxt.setVisibility(View.VISIBLE);
        mNextBtn.setText(R.string.common_complete);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceIntroductionInfo deviceIntroductionInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_RESULT_INTRODUCTION);
            String tipImg = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_RESULT_PROMPT_IMAGE);
            String helpTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_CONFIRM_INTRODUCTION);
            if (!TextUtils.isEmpty(tipTxt))
                mTipTxt.setText(tipTxt);
            if (!TextUtils.isEmpty(tipImg))
                 Glide.with(HubapGuide3Fragment.this.getContext()).load(tipImg).into(mTipImg);
            if (!TextUtils.isEmpty(helpTxt)) {
                mHelpTxt.setVisibility(View.VISIBLE);
                mHelpTxt.setText(helpTxt);
            }
        }
    }

    @Override
    protected void nextAction() {
        if(getActivity() != null){
            EventBus.getDefault().post(new CommonEvent(CommonEvent.REFRESH_SINGLE_DEVICE_SYNC_ACTION));
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
        }

    }

    @Override
    protected void helpAction() {
       if(getActivity() != null && getActivity().getSupportFragmentManager() != null)
           getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }
}
