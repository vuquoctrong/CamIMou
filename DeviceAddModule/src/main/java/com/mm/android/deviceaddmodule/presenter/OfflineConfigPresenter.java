package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;

import com.mm.android.deviceaddmodule.contract.OfflineConfigConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class OfflineConfigPresenter implements OfflineConfigConstract.Presenter {
    WeakReference<OfflineConfigConstract.View> mView;

    public OfflineConfigPresenter(OfflineConfigConstract.View view) {
        mView = new WeakReference<>(view);
    }

    @Override
    public void resetCache() {
        // 离线配网时需要将设备密码带入，故不适用clearCache方法清理缓存
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        deviceAddInfo.getWifiInfo().setPwd(null);
        deviceAddInfo.getWifiInfo().setSsid(null);
        deviceAddInfo.setCurDeviceAddType(DeviceAddInfo.DeviceAddType.WLAN);
    }

    @Override
    public void getDeviceInfo(String deviceSn, final String deviceModelName) {
        mView.get().showProgressDialog();
        LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    checkDevIntroductionInfo(deviceModelName);
                }else {
                    mView.get().cancelProgressDialog();
                    mView.get().showToastInfo( BusinessErrorTip.getErrorTip(msg));
                    mView.get().onGetDeviceInfoError();
                }
            }
        };
        DeviceAddModel.newInstance().getDeviceInfo(deviceSn, "", deviceModelName, DeviceAddModel.newInstance().getDeviceInfoCache().getProductId(), getDeviceHandler);
    }

    private void checkDevIntroductionInfo(final String deviceModelName) {
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
                    dispatchIntroctionResult();
                }
            }
        };
        DeviceAddModel.newInstance().checkDevIntroductionInfo(deviceModelName, checkDevIntroductionHandler);
    }

    private void getDevIntroductionInfoSync(String deviceModel) {
        LCBusinessHandler getDevIntroductionHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                dispatchIntroctionResult();
            }
        };
        DeviceAddModel.newInstance().getDevIntroductionInfo(deviceModel, getDevIntroductionHandler);
    }

    private void dispatchIntroctionResult() {
        mView.get().cancelProgressDialog();
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CONFIG_PAGE_NAVIGATION_ACTION));
    }
}
