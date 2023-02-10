package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;

import com.mm.android.deviceaddmodule.contract.HubApGuide1Constract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class HubApGuide1Presenter implements HubApGuide1Constract.Presenter {

    WeakReference<HubApGuide1Constract.View> mView;

    public HubApGuide1Presenter(HubApGuide1Constract.View view) {
        mView = new WeakReference<>(view);
    }


    private void getDevIntroductionInfoSync(String deviceModel) {
        LCBusinessHandler getDevIntroductionHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                dispatchIntroductionResult();
            }
        };
        DeviceAddModel.newInstance().getDevIntroductionInfo(deviceModel, getDevIntroductionHandler);
    }

    @Override
    public void checkDevIntroductionInfo(final String deviceModelName) {
        mView.get().showProgressDialog();
        LCBusinessHandler checkDevIntroductionHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                DeviceIntroductionInfo deviceIntroductionInfo = null;
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    deviceIntroductionInfo = (DeviceIntroductionInfo) msg.obj;
                }
                if (deviceIntroductionInfo == null) {        //表示需要更新
                    getDevIntroductionInfoSync(deviceModelName);
                } else {
                    dispatchIntroductionResult();
                }
            }
        };
        DeviceAddModel.newInstance().checkDevIntroductionInfo(deviceModelName, checkDevIntroductionHandler);
    }

    private void dispatchIntroductionResult() {
        mView.get().cancelProgressDialog();
        mView.get().showInfoView();
        DeviceIntroductionInfo deviceIntroductionInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_PAIR_OPERATION_INTRODUCTION);
            String tipImg = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_PAIR_STATUS_IMAGE);
            String helpTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.HUB_MODE_RESET_GUIDE_INTRODUCTION);
            mView.get().updateTip(tipImg, tipTxt, helpTxt);
        }
    }
}
