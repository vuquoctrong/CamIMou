package com.mm.android.deviceaddmodule.p_cloudconnect;

import android.os.Bundle;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;

/**
 * Device connection cloud platform process display page
 *
 * 设备连接云平台进程展示页
 */
public class CloudConnectFragment extends BaseCloudFragment{
    private static final String BASECLOUDTYPE = "BaseCloudType";

    public static CloudConnectFragment newInstance() {
        CloudConnectFragment fragment = new CloudConnectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static CloudConnectFragment newInstance(boolean isBindDevice) {
        CloudConnectFragment fragment = new CloudConnectFragment();
        Bundle args = new Bundle();
        args.putBoolean(BASECLOUDTYPE,isBindDevice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void goBindSuccessPage() {
        PageNavigationHelper.gotoBindSuccessPage(this);
    }

    @Override
    public void goErrorTipPage() {
        mPresenter.stopConnectTiming();
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.CLOUND_CONNECT_QUERY_STATUS_TIMEOUT);
    }

    @Override
    public void goBindDevicePage() {
        mTipTxt.setText(R.string.add_device_binding_to_account);
        mPresenter.bindDevice();
    }

    @Override
    public void setCountDownTime(int time) {
        mCountdown_view.setCountdownTime(time);
    }

    @Override
    public void setMiddleTime(int time) {
        mCountdown_view.setMiddleTime(time);
    }

    @Override
    public void goDevLoginPage() {
        PageNavigationHelper.gotoDevLoginPage(this);
    }

    @Override
    public void goDevSecCodePage() {
        PageNavigationHelper.gotoDevSecCodePage(this);
    }

    @Override
    public void initAction() {
        boolean isBindDevice = false;
        if(getArguments() != null && getArguments().containsKey(BASECLOUDTYPE)){
            isBindDevice = getArguments().getBoolean(BASECLOUDTYPE);
        }

        mCountdown_view.startCountDown();

        if(isBindDevice) {
            goBindDevicePage();
        } else {
            mTipTxt.setText(R.string.add_device_connect_cloud_please_wait);
            mPresenter.getDeviceInfo();
            mPresenter.startConnectTiming();
        }
    }

    @Override
    public void middleTimeUp() {
        mPresenter.notifyMiddleTimeUp();
    }
}
