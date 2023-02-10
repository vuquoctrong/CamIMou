package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.SearchDeviceManager;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * Power Supply Connection Prompt
 *
 * 连接电源提示
 */
public class TipPowerFragment extends BaseTipFragment {
    private static String CONFIG_PARAM = "config_param";
    boolean mIsWirelessConfig = true;

    public static TipPowerFragment newInstance(boolean isWirelessConfig) {
        TipPowerFragment fragment = new TipPowerFragment();
        Bundle args = new Bundle();
        args.putBoolean(CONFIG_PARAM, isWirelessConfig);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoftAP.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE4);
        } else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        }
    }

    @Override
    protected void nextAction() {
        if (mIsWirelessConfig) {
            wirelessNavigation();
        } else {
            wiredNavigation();
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


    protected void initView(View view) {
        super.initView(view);
        tipImageMatch();
        mTipImg.setImageResource(R.drawable.common_netsetting_power);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mTipTxt.setText(R.string.add_device_plug_power);
    }

    protected void initData() {
        super.initData();
        if (getArguments() != null) {
            mIsWirelessConfig = getArguments().getBoolean(CONFIG_PARAM);
        }
    }

    private void wirelessNavigation() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (Utils4AddDevice.isWifi(getActivity())) {
            // 手机已连接wifi
            SearchDeviceManager manager = SearchDeviceManager.getInstance();
            String sn = deviceAddInfo.getDeviceSn();
            DeviceInitInfo info = manager.getDeviceNetInfo(sn);
            if (info != null) {
//                Toast.makeText(getContext(),"sn--"+info.mSerialNo+"info--"+info.mInitStatus,Toast.LENGTH_LONG).show();
                // 支持sc码,进入云配置流程
                if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
                    PageNavigationHelper.gotoCloudConnectPage(this);
                } else {
                    // 搜索到设备进入初始化流程
                    PageNavigationHelper.gotoSecurityCheckPage(this);
                }
            } else {
                // 未搜到设备，进入配网流程
                PageNavigationHelper.gotoWifiPwdPage(this, null);
            }
        } else {
            // 未连接wifi，进入将手机连接WiFi网络提示页
            PageNavigationHelper.gotoWifiConnectTipPage(this);
        }
    }

    private void wiredNavigation() {
        SearchDeviceManager manager = SearchDeviceManager.getInstance();
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        String sn = deviceAddInfo.getDeviceSn();
        DeviceInitInfo info = manager.getDeviceNetInfo(sn);
        if (info != null) {
            // 支持sc码,进入云配置流程
            if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
                PageNavigationHelper.gotoCloudConnectPage(this);
            } else {
                // 搜索到设备，进入初始化流程
                PageNavigationHelper.gotoSecurityCheckPage(this);
            }
        } else {
            PageNavigationHelper.gotoNetCableTipPage(this);
        }
    }
}
