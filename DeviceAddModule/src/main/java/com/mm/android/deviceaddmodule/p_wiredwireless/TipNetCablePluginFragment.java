package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * Network Cable Insertion Tips
 *
 * 网线插入提示
 */
public class TipNetCablePluginFragment extends BaseTipFragment {

    public static TipNetCablePluginFragment newInstance() {
        TipNetCablePluginFragment fragment = new TipNetCablePluginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void nextAction() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            PageNavigationHelper.gotoCloudConnectPage(this);
        } else {
            PageNavigationHelper.gotoSameNetworkTipPage(this);
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


    @Override
    protected void initView(View view) {
        super.initView(view);
        tipImageMatch();
        mTipImg.setImageResource(R.drawable.adddevice_netsetting_networkcable);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mTipTxt.setText(R.string.add_device_plug_cable_to_device);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo= DeviceAddModel.newInstance().getDeviceInfoCache();
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
