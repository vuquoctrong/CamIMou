package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.ApBindSuccessConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.AddApResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class ApBindSuccessPresenter implements ApBindSuccessConstract.Presenter {
    WeakReference<ApBindSuccessConstract.View> mView;
    AddApResult mAddApResult;

    public ApBindSuccessPresenter(ApBindSuccessConstract.View view) {
        mView = new WeakReference<>(view);
    }

    private void initApView() {
        DeviceIntroductionInfo deviceIntroductionInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String img = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_FINISH_DEVICE_IMAGE);
            mView.get().setApImg(img);
        }
        if (mAddApResult != null) {
            mView.get().setApName(mAddApResult.getApName());
        }
    }

    @Override
    public void modifyApName() {
        if(!DHNetworkUtil.isConnected(mView.get().getContextInfo())){
            mView.get().showToastInfo(R.string.mobile_common_bec_common_network_exception);
            return;
        }

        final String apName = mView.get().getApName();
        if (TextUtils.isEmpty(apName)) {
            mView.get().showToastInfo(R.string.device_manager_input_device_name);
            return;
        }
        final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        mView.get().showProgressDialog();
        final String deviceId = deviceAddInfo.getGatewayInfo().getSn();
        final String apId = deviceAddInfo.getDeviceSn();
        LCBusinessHandler modifyHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() != null && mView.get().isViewActive()) {
                    mView.get().cancelProgressDialog();
                    if (HandleMessageCode.HMC_SUCCESS == msg.what) {
                        mView.get().completeAction();
                    } else {
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                    }
                }
            }
        };
        boolean hasToDeviceAbility = deviceAddInfo.hasAbility(DeviceAbility.ModifyName);
        DeviceAddModel.newInstance().modifyAPDevice(deviceId, apId, apName, hasToDeviceAbility, modifyHandler);
    }

    @Override
    public void setData(AddApResult addApResult) {
        mAddApResult = addApResult;
        initApView();
    }
}
