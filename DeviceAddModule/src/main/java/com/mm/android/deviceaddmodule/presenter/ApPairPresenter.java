package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;

import com.mm.android.deviceaddmodule.contract.ApPairConstract;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.AddApResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class ApPairPresenter implements ApPairConstract.Presenter {
    WeakReference<ApPairConstract.View> mView;

    public ApPairPresenter(ApPairConstract.View view) {
        mView = new WeakReference<>(view);
    }

    @Override
    public void pair() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        DeviceAddInfo.GatewayInfo gatewayInfo = deviceAddInfo.getGatewayInfo();
        if(gatewayInfo == null){    //异常保护
            return;
        }
        String deviceId = gatewayInfo.getSn();
        String apId = deviceAddInfo.getDeviceSn();
        getPariResultSync(deviceId, apId);
    }

    @Override
    public void stopPair() {
        DeviceAddModel.newInstance().setLoop(false);
    }

    private void getPariResultSync(final String deviceId, final String apId) {
        LCBusinessHandler resultHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (HandleMessageCode.HMC_SUCCESS == msg.what) {
                    AddApResult addApResult = (AddApResult) msg.obj;
                    apPairSucceed(addApResult);
                }else{
                    getPariResultSync(deviceId, apId);
                }
            }
        };
        DeviceAddModel.newInstance().getAddApResultAsync(deviceId, apId, resultHandler);
    }

    private void apPairSucceed(AddApResult addApResult) {
        if(mView.get() != null){
            mView.get().goApBindSuccessPage(addApResult);
        }
    }

}
