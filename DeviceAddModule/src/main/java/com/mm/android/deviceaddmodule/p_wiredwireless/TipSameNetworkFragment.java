package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * Same web tip page
 *
 * 同一网络提示页
 */
public class TipSameNetworkFragment extends BaseTipFragment{

    public static TipSameNetworkFragment newInstance() {
        TipSameNetworkFragment fragment = new TipSameNetworkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void nextAction() {
        if(!Utils4AddDevice.isWifi(getActivity().getApplicationContext())){
            showToastInfo(R.string.add_device_con_wifi);
            return;
        }
        PageNavigationHelper.gotoSecurityCheckPage(this);
    }

    @Override
    protected void helpAction() {

    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tipImageMatch();
        mConfirmCheck.setVisibility(View.VISIBLE);
        mTipTxt.setText(R.string.add_device_same_network_tip);
        mTipImg.setImageResource(R.drawable.adddevice_samenet);
        mConfirmCheck.setText(R.string.add_device_confirm_same_network);
        mNextBtn.setEnabled(false);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoftAP.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE4);
        } else if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SmartConfig.name())
                || deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoundWave.name())
                || deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoundWaveV2.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE3);
        } else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        }
    }
}
