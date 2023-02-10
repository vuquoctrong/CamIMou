package com.mm.android.deviceaddmodule.presenter;

import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.DevSecCodeConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class DevSecCodePresenter implements DevSecCodeConstract.Presenter {
    WeakReference<DevSecCodeConstract.View> mView;
    String mDeviceSn;
    boolean mIsWifiOfflineMode;
    DHWifiUtil mDHWifiUtil;

    public DevSecCodePresenter(DevSecCodeConstract.View view) {
        mView = new WeakReference<>(view);
        mDeviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        mIsWifiOfflineMode = DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode();
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo());
    }

    @Override
    public void validate() {
        if (!checkInput(mView.get().getDeviceSecCode())) {
            mView.get().showToastInfo(R.string.mobile_common_bec_add_device_valid_error);
            return;
        }
        mView.get().showProgressDialog();
        bindDevice(mView.get().getDeviceSecCode());
    }

    private boolean checkInput(String regCode) {
        return !(TextUtils.isEmpty(regCode) || regCode.length() < 6);
    }

    // 无auth能力集的设备没有设备密码(进入该流程肯定无auth能力集)
    private void bindDevice(final String code) {
        final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        String sn = deviceAddInfo.getDeviceSn();
        LCBusinessHandler bindHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                mView.get().cancelProgressDialog();
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {           //设备被其他帐户绑定
                        mView.get().goOtherUserBindTipPage();
                        return;
                    }
                    addDeviceToPolicy(code);
                } else {
                    String errorDesp = ((BusinessException) msg.obj).errorDescription;
                    if (errorDesp.contains("DV1014")) {  //设备绑定错误连续超过10次，限制绑定30分钟
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN);
                    } else if (errorDesp.contains("DV1015")) {  //设备绑定错误连续超过20次，限制绑定24小时
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE);
                    } else if (errorDesp.contains("DV1045")) {  // 设备冲突
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_CODE_CONFLICT);
                    } else if (errorDesp.contains("DV1044")) {  //设备IP不在统一局域网内
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_IP_ERROR);
                    } else if (errorDesp.contains("DV1027")) { // 设备安全码错误
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                    } else if (errorDesp.contains("DV1016")||errorDesp.contains("DV1025")) {  // 设备密码错误(环回认证失败）/设备SC码或设备密码错误
                        mView.get().showToastInfo(R.string.add_device_verify_device_pwd_failed);
                        mView.get().goDevLoginPage();
                        deviceAddInfo.setDevicePwd("");
                    } else if (errorDesp.contains("DV1037")) {  //NB设备的imei和device id 不匹配
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH);
                    } else {
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                    }
                }
            }
        };
        DeviceAddModel.newInstance().bindDevice(sn, code, bindHandler);
    }

    private void addDeviceToPolicy(String sn){
        LCBusinessHandler policyHandler = new LCBusinessHandler(Looper.getMainLooper()) {

            @Override
            public void handleBusiness(Message msg) {
                if (msg.what == HandleMessageCode.HMC_SUCCESS){
                    mView.get().goBindSuceesPage();
                }else{
                    mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                }
            }
        };
        DeviceAddModel.newInstance().addPolicy(sn,policyHandler);
    }
}
