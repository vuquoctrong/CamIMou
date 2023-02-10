package com.mm.android.deviceaddmodule.p_softap.oversea;


import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;


/**
 * Soft Ap device wifi connection judgment page, the user manually determine whether the device is connected to wifi (foreign Le Orange)
 *
 * 软Ap 设备连接wifi判断页，用户手动判断设备是否已连接上wifi（国外乐橙）
 */
public class SoftApResultFragment extends BaseTipFragment implements View.OnClickListener {
    public static SoftApResultFragment newInstance() {
        SoftApResultFragment fragment = new SoftApResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void nextAction() {
        if(DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode()){
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
            ProviderManager.getDeviceAddCustomProvider().goHomePage(getActivity());
        }else {
            PageNavigationHelper.gotoDeviceBindPage(this);
        }
    }

    @Override
    protected void helpAction() {

    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }

    protected void initView(View view){
        super.initView(view);
        tipImageMatch();
        mTipImg.setImageResource(R.drawable.adddevice_netsetting_connectrouter);
        mTipTxt.setText(R.string.add_device_softap_dev_connect_tip);
        mConfirmCheck.setText(R.string.add_device_softap_dev_connect_check_tip);
        mConfirmCheck.setVisibility(View.VISIBLE);
    }

    protected void initData(){
        super.initData();
        DeviceIntroductionInfo deviceIntroductionInfo= DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if(deviceIntroductionInfo!=null){
            String tipImg=deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESULT_PROMPT_IMAGE);
            String tipTxt=deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESULT_INTRODUCTION);
            String checkTxt=deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_CONFIRM_INTRODUCTION);
            mTipTxt.setText(tipTxt);
            mConfirmCheck.setText(checkTxt);
            Glide.with(SoftApResultFragment.this.getContext()).load(tipImg).into(mTipImg);
        }
    }
}
