package com.mm.android.deviceaddmodule.presenter;

import android.os.Build;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.BaseSoftApTipConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class BaseSoftApTipPresenter implements BaseSoftApTipConstract.Presenter {
    private int TIME_OUT_TIME = 10 * 1000;        //连接设备热点超时时间
    private static final int CONNECT_FAILED = 0;                            //连接热点失败
    DHWifiUtil mDHWifiUtil;
    String mDeviceSn;
    boolean mConnectResult;
    WeakReference<BaseSoftApTipConstract.View> mView;
    DeviceIntroductionInfo mTips;
    String mResetTxt;               //reset引导信息
    int mCurStep = 0;              //当前引导页索引，从0开始
    int mMaxStep = 1;               //总引导页数


    public BaseSoftApTipPresenter(BaseSoftApTipConstract.View view, int curStep) {
        mView = new WeakReference<>(view);
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
        mTips = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        mDeviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        mCurStep = curStep;
        initPageTip();
    }

    private void initPageTip() {
        if(mTips!=null) {
            mResetTxt = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESET_GUIDE_INTRODUCTION);
            String oneTips = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_ONE_INTRODUCTION);
            String oneTipsImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_ONE_IMAGE);
            String twoTips = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_TWO_INTRODUCTION);
            String twoTipsImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_TWO_IMAGE);
            String threeTips = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_THREE_INTRODUCTION);
            String threeTipsImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_THREE_IMAGE);
            String fourTips = mTips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_FOUR_INTRODUCTION);
            String fourTipsImg = mTips.getImageInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_GUIDING_STEP_FOUR_IMAGE);

            if (!TextUtils.isEmpty(oneTips)) {
                mMaxStep = 1;
            }
            if (!TextUtils.isEmpty(twoTips)) {
                mMaxStep = 2;
            }
            if (!TextUtils.isEmpty(threeTips)) {
                mMaxStep = 3;
            }
            if (!TextUtils.isEmpty(fourTips)) {
                mMaxStep = 4;
            }
            if(mCurStep==mMaxStep-1){
                mView.get().updateResetTxt(mResetTxt);
            }
            if(mCurStep==0){
                mView.get().updateTipImage(!TextUtils.isEmpty(oneTipsImg) ? oneTipsImg : "drawable://" + R.drawable.adddevice_supportsoftap);
                mView.get().updateTipTxt(oneTips);
            }else if(mCurStep==1){
                mView.get().updateTipImage(twoTipsImg);
                mView.get().updateTipTxt(twoTips);
            }else if(mCurStep==2){
                mView.get().updateTipImage(threeTipsImg);
                mView.get().updateTipTxt(threeTips);
            }else if(mCurStep==3){
                mView.get().updateTipImage(fourTipsImg);
                mView.get().updateTipTxt(fourTips);
            }
        }
    }

    @Override
    public boolean isWifiConnect(){
        return DHNetworkUtil.NetworkType.NETWORK_WIFI.equals(DHNetworkUtil.getNetworkType(mView.get().getContextInfo()));
    }


    @Override
    public boolean isLastTipPage() {
        return mCurStep==mMaxStep-1;
    }


    @Override
    public void verifyWifiOrLocationPermission(){
        if(!isWifiConnect()){   //预先打开wifi
            mDHWifiUtil.openWifi();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mView.get().applyLocationPermission();
        }else{
            mView.get().gotoSoftApTipConnectWifiPage();
        }
    }

    @Override
    public void dealWithUnknownSsid() {

        String curWifiName = mDHWifiUtil.getCurrentWifiInfo().getSSID().replaceAll("\"", "");
        if(LCConfiguration.UNKNOWN_SSID.equals(curWifiName)){
            mView.get().applyLocationPermission();
        }else {
           mView.get().gotoSoftApTipConnectWifiPage();
        }
    }

}
