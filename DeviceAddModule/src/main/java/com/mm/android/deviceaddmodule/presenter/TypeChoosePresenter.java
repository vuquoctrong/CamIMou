package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.mm.android.deviceaddmodule.contract.TypeChooseConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceTypeInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

public class TypeChoosePresenter implements TypeChooseConstract.Presenter {
    WeakReference<TypeChooseConstract.View> mView;
    private List<DeviceTypeInfo.DeviceModelInfo> mModelInfoList;         //设备类型集合

    public TypeChoosePresenter(TypeChooseConstract.View view) {
        mView = new WeakReference<>(view);
    }

    @Override
    public void getDeviceInfoSync(final String deviceModelName) {
        mView.get().showProgressDialog();
        String deviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        String productId = DeviceAddModel.newInstance().getDeviceInfoCache().getProductId();
        LCBusinessHandler getDeviceHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }

                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    checkDevIntroductionInfo(deviceModelName);
                } else {
                    mView.get().cancelProgressDialog();
                    mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                }
            }
        };
        DeviceAddModel.newInstance().getDeviceInfo(deviceSn, "", deviceModelName,productId, getDeviceHandler);
    }

    @Override
    public void checkDevIntroductionInfo(final String deviceModelName) {
        LCBusinessHandler checkDevIntroductionHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                DeviceIntroductionInfo deviceIntroductionInfo = null;
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //TODO:获取失败也当做有更新
                    deviceIntroductionInfo = (DeviceIntroductionInfo) msg.obj;
                }
                if (deviceIntroductionInfo == null) {        //表示需要更新引导
                    getDevIntroductionInfoSync(deviceModelName);
                } else {
                    dispatchIntroctionResult();
                }
            }
        };
        DeviceAddModel.newInstance().checkDevIntroductionInfo(deviceModelName, checkDevIntroductionHandler);
    }

    @Override
    public void resetDevPwdCache() {
        DeviceAddModel.newInstance().getDeviceInfoCache().clearCache();
    }

    /**
     * Asynchronously obtaining device boot information
     * @param deviceModel  Device Model
     *
     * 异步获取设备引导信息
     * @param deviceModel  设备模型
     */
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
        // 设备选择页面清除imei号
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (TextUtils.isEmpty(deviceAddInfo.getModelName()) && TextUtils.isEmpty(deviceAddInfo.getNc())) {
            if (mView.get() == null || (mView.get() != null && !mView.get().isViewActive())) {
                return;
            }
            mView.get().showSearchError();
            return;
        }
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CONFIG_PAGE_NAVIGATION_ACTION));
    }

}
