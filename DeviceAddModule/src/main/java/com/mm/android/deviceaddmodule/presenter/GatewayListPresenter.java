package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.GatewayListConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorCode;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.device.LCDevice;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GatewayListPresenter implements GatewayListConstract.Presenter {
    WeakReference<GatewayListConstract.View> mView;
    List<LCDevice> mGatewayData;
    int mSelectedPos=-1;

    public GatewayListPresenter(GatewayListConstract.View view) {
        mView = new WeakReference<>(view);
        mGatewayData = new ArrayList<>();
        initApView();
    }

    private void initApView() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        mView.get().setApSn(deviceAddInfo.getDeviceSn());
        DeviceIntroductionInfo deviceIntroductionInfo = deviceAddInfo.getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String img = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.ACCESSORY_MODE_FINISH_DEVICE_IMAGE);
            mView.get().setApImg(img);
        }
    }

    @Override
    public List<LCDevice> getGatewayData(boolean selectedGateway) {
        //TODO 网关列表选择不能依赖本地缓存，需要开新接口从服务拉数据
        return mGatewayData;
    }

    @Override
    public int gatewaySize() {
        return mGatewayData.size();
    }

    @Override
    public void dispatchCurSelect(int curPos) {
        if (curPos >= 0 && curPos < mGatewayData.size()) {
            LCDevice device = mGatewayData.get(curPos);
            DeviceAddInfo.GatewayInfo gatewayInfo = new DeviceAddInfo.GatewayInfo();
            gatewayInfo.setSn(device.getDeviceId());
            DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
            deviceAddInfo.setGatewayInfo(gatewayInfo);
            String deviceId = deviceAddInfo.getGatewayInfo().getSn();
            String apId = deviceAddInfo.getDeviceSn();
            String apType = deviceAddInfo.getCatalog();
            String apModel = deviceAddInfo.getDeviceModel();
            pairSync(deviceId, apId, apType, apModel);
        } else {
            mView.get().goTipPage();
        }
    }

    @Override
    public int getSelectedpos() {
        return mSelectedPos;
    }

    private void pairSync(final String deviceId, final String apId, String apType, String apModel) {
        mView.get().showProgressDialog();
        LCBusinessHandler pairHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() != null && mView.get().isViewActive()) {
                    mView.get().cancelProgressDialog();
                    if (HandleMessageCode.HMC_SUCCESS == msg.what) {
                        boolean isSucceed = (boolean) msg.obj;
                        if (isSucceed) {
                            mView.get().goTipPage();
                            return;
                        }
                    }
                    apPairFailed(msg.what);
                }
            }
        };
        DeviceAddModel.newInstance().addApDevice(deviceId, apId, apType, apModel, pairHandler);
    }

    private void apPairFailed(int errorCode) {
        if(errorCode == BusinessErrorCode.BEC_DEVICE_ADD_AP_UPPER_LIMIT){
            mView.get().showToastInfo(R.string.add_device_add_ap_upper_limit);
        } else {
            mView.get().showToastInfo(R.string.add_device_config_failed);
        }
    }
}
