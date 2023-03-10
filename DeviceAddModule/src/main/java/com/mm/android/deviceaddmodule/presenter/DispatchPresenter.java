package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.DispatchContract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import static com.mm.android.deviceaddmodule.helper.Utils4AddDevice.isDeviceTypeBox;

public class DispatchPresenter implements DispatchContract.Presenter {
    WeakReference<DispatchContract.View> mView;

    public DispatchPresenter(DispatchContract.View view) {
        mView = new WeakReference<>(view);
    }

    private void getDevIntroductionInfoSync(String deviceModel, final boolean isOnlineAction) {
        LCBusinessHandler getDevIntroductionHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                dispatchIntroductionResult(isOnlineAction);
            }
        };
        DeviceAddModel.newInstance().getDevIntroductionInfo(deviceModel, getDevIntroductionHandler);
    }

    private void checkDevIntroductionInfo(final String deviceModelName, final boolean isOnlineAction) {

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
                if (deviceIntroductionInfo == null) {        //??????????????????
                    getDevIntroductionInfoSync(deviceModelName, isOnlineAction);
                } else {
                    dispatchIntroductionResult(isOnlineAction);
                }
            }
        };
        DeviceAddModel.newInstance().checkDevIntroductionInfo(deviceModelName, checkDevIntroductionHandler);
    }

    private void dispatchIntroductionResult(boolean isOnlineAction) {
        mView.get().cancelProgressDialog();
        if(isOnlineAction){
            gotoPage();
        }else {
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CONFIG_PAGE_NAVIGATION_ACTION));
        }
    }

    //?????????????????????????????????
    @Override
    public boolean isSnInValid(String sn) {
        if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) {
            return (sn.length() == 0
                    || sn.getBytes().length > 64);
        } else {
            return TextUtils.isEmpty(sn);
        }
    }

    @Override
    public boolean isScCodeInValid(String scCode) {
        return false;
    }


    @Override
    public boolean isManualInputPage() {
        return false;
    }

    protected void updateDeviceAddInfo(final String deviceSn, final String model, String regCode, String nc, String sc) {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        deviceAddInfo.setDeviceSn(deviceSn);
        deviceAddInfo.setDeviceCodeModel(model);
        deviceAddInfo.setDeviceModel(model);
        deviceAddInfo.setRegCode(regCode);
        deviceAddInfo.setSc(sc);
        deviceAddInfo.setNc(nc);  // ???16?????????????????????????????????
        // ??????SC?????????????????????SC?????????????????????
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            deviceAddInfo.setDevicePwd(sc);
        }
    }

    /**
     * Process device information returned by the service
     *
     * ?????????????????????????????????
     */
    public void dispatchResult() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (!deviceAddInfo.isSupport()) {
            mView.get().goNotSupportBindTipPage();
        }else if (DeviceAddInfo.BindStatus.bindByMe.name().equals(deviceAddInfo.getBindStatus())) {                     //???????????????????????????
            mView.get().showToastInfo(R.string.add_device_device_bind_by_yourself);
        } else if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {           //???????????????????????????
            mView.get().goOtherUserBindTipPage();
        } else if (DeviceAddInfo.DeviceType.ap.name().equals(deviceAddInfo.getType())) {        //??????
            checkDevIntroductionInfo(deviceAddInfo.getDeviceModel(),false);
        } else {    // ??????
           if (isManualInputPage()  // ??????????????????sc???????????????ios??????
                    && deviceAddInfo.hasAbility(DeviceAbility.SCCode) && (deviceAddInfo.getSc() == null || deviceAddInfo.getSc().length() != 8)) {   // ???????????????sc????????????sc???????????????
                mView.get().showToastInfo(R.string.add_device_input_corrent_sc_tip);
            } else if (!deviceAddInfo.isDeviceInServer()) {                                                            //???????????????????????????
                //???????????????????????????
                deviceOfflineAction();
            } else if (DeviceAddInfo.Status.offline.name().equals(deviceAddInfo.getStatus())) {                        //????????????
                deviceOfflineAction();
            } else if (DeviceAddInfo.Status.online.name().equals(deviceAddInfo.getStatus())
                    || DeviceAddInfo.Status.sleep.name().equals(deviceAddInfo.getStatus())
                    || DeviceAddInfo.Status.upgrading.name().equals(deviceAddInfo.getStatus())) {                         //????????????/??????/?????????
                deviceOnlineAction();
            }
        }

        if(isManualInputPage()) {
            deviceAddInfo.setStartTime(System.currentTimeMillis());
        }
    }

    /**
     * If the device information fails to be obtained or the device is offline, you need to handle the result
     *
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    private void deviceOfflineAction() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (isDeviceTypeBox(deviceAddInfo.getDeviceCodeModel())) {// ???????????????????????????????????????????????????
            mView.get().showToastInfo(R.string.add_device_box_is_offline);
        } else {
            if ((!TextUtils.isEmpty(deviceAddInfo.getDeviceCodeModel())
                    || !TextUtils.isEmpty(deviceAddInfo.getDeviceModel()))) { //?????????????????????????????????
                String deviceModel = deviceAddInfo.getDeviceModel();
                if (TextUtils.isEmpty(deviceModel)) {
                    deviceModel = deviceAddInfo.getDeviceCodeModel();
                }
                checkDevIntroductionInfo(deviceModel,false);
            } else {
                mView.get().goTypeChoosePage();                 //????????????
            }
        }
    }

    /**
     * The device information is obtained, and the device is online, and the result is processed
     *
     * ??????????????????????????????????????????????????????????????????
     */
    private void deviceOnlineAction() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (isDeviceTypeBox(deviceAddInfo.getDeviceCodeModel())) {
            // ??????????????????
            mView.get().showToastInfo(R.string.add_device_not_support_to_bind);
            return;
        } else {// ????????????
            if (!TextUtils.isEmpty(deviceAddInfo.getDeviceCodeModel())
                    || !TextUtils.isEmpty(deviceAddInfo.getDeviceModel())) { //?????????????????????????????????
                String deviceModel = deviceAddInfo.getDeviceModel();
                if (TextUtils.isEmpty(deviceModel)) {
                    deviceModel = deviceAddInfo.getDeviceCodeModel();
                }
                checkDevIntroductionInfo(deviceModel,true);
            } else {
                gotoPage();
            }
        }
    }

    private void gotoPage() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        deviceAddInfo.setCurDeviceAddType(DeviceAddInfo.DeviceAddType.ONLINE);

        if (DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            mView.get().goCloudConnectPage();
        } else {
            if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) { // ??????
                mView.get().goDeviceLoginPage();
            } else {
                if (deviceAddInfo.hasAbility(DeviceAbility.Auth)) {
                    if (TextUtils.isEmpty(deviceAddInfo.getDevicePwd())) {
                        mView.get().goDeviceLoginPage();//??????????????????
                    } else {
                        mView.get().goDeviceBindPage();//????????????
                    }
                } else if (deviceAddInfo.hasAbility(DeviceAbility.RegCode)) {
                    if (TextUtils.isEmpty(deviceAddInfo.getRegCode())) {
                        mView.get().goSecCodePage();//???????????????
                    } else {
                        mView.get().goDeviceBindPage();//????????????
                    }
                }
            }
        }
    }
}
