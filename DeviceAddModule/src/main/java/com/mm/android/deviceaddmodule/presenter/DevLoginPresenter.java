package com.mm.android.deviceaddmodule.presenter;

import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.company.NetSDK.FinalVar;
import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.DevLoginConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.p2pDevice.P2PErrorHelper;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

public class DevLoginPresenter implements DevLoginConstract.Presenter {
    WeakReference<DevLoginConstract.View> mView;
    String mDeviceSn;
    boolean mIsWifiOfflineMode;
    DHWifiUtil mDHWifiUtil;

    public DevLoginPresenter(DevLoginConstract.View view) {
        mView = new WeakReference<>(view);
        mDeviceSn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        mIsWifiOfflineMode = DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode();
        mDHWifiUtil = new DHWifiUtil(mView.get().getContextInfo().getApplicationContext());
    }

    @Override
    public void devLogin() {
        if (!DHNetworkUtil.isConnected(mView.get().getContextInfo())) {
            mView.get().showToastInfo(R.string.mobile_common_bec_common_network_exception);
            return;
        }
        if (!checkInput(mView.get().getDevicePassword())) {
            mView.get().showToastInfo(R.string.device_manager_password_error);
            return;
        }

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        DeviceAddInfo.DeviceAddType deviceAddType = deviceAddInfo.getCurDeviceAddType();
        if (DeviceAddInfo.DeviceAddType.SOFTAP.equals(deviceAddType) || deviceAddInfo.getType().equals("iot")) {
            String status = deviceAddInfo.getStatus();
            if (TextUtils.isEmpty(status)) {
                status = DeviceAddInfo.Status.offline.name();
            }

            if (!DeviceAddInfo.Status.offline.name().equals(status)) {//??????????????????????????????
                mView.get().showProgressDialog();
                verifyPassword(deviceAddInfo.getDeviceSn(), "admin", mView.get().getDevicePassword());
            } else {
                goIPLogin();
            }
        } else {
            if (!deviceAddInfo.isEasy4ipP2PDev() && deviceAddInfo.hasAbility(DeviceAbility.Auth)) {//pass???????????????Auth?????????????????????????????????
                mView.get().showProgressDialog();
                verifyPassword(deviceAddInfo.getDeviceSn(), "admin", mView.get().getDevicePassword());
            } else  {
                mView.get().cancelProgressDialog();
                mView.get().showToastInfo(R.string.add_device_not_support_to_bind);
            }
        }
    }

    private boolean checkInput(String password) {
        return !(password.length() == 0);
    }

    //ip??????
    private void goIPLogin() {
        String ip = mDHWifiUtil.getGatewayIp();
        DeviceAddModel.newInstance().deviceIPLogin(ip, mView.get().getDevicePassword(), new LCOpenSDK_DeviceInit.ILogInDeviceListener() {
            @Override
            public void onLogInResult(int resultCode) {
                onIPLogInResult(resultCode);
            }
        });
    }

    //???auth?????????????????????????????????(????????????????????????)
    private void verifyPassword(final String sn, String user, String pwd) {
        String encryptPwd = pwd;
        final DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        LCBusinessHandler bindHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() == null
                        || (mView.get() != null && !mView.get().isViewActive())) {
                    return;
                }
                mView.get().cancelProgressDialog();
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    if (DeviceAddInfo.BindStatus.bindByOther.name().equals(deviceAddInfo.getBindStatus())) {           //???????????????????????????
                        mView.get().goOtherUserBindTipPage();
                        return;
                    }
                    addDeviceToPolicy(sn);
                } else {
                    String errorDesp = ((BusinessException) msg.obj).errorDescription;
                    if (errorDesp.contains("DV1014")) {  //??????????????????????????????10??????????????????30??????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN);
                    } else if (errorDesp.contains("DV1015")) {  //??????????????????????????????20??????????????????24??????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE);
                    } else if (errorDesp.contains("DV1044")) {  //??????IP????????????????????????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_IP_ERROR);
                    } else if (errorDesp.contains("DV1045")) {  // ????????????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_CODE_CONFLICT);
                    } else if (errorDesp.contains("DV1042")) {  // ????????????????????????????????????????????????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_LOCKED);
                    } else if (errorDesp.contains("DV1027")) {  // ?????????????????????
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                        mView.get().goDevSecCodePage();
                        deviceAddInfo.setRegCode("");
                    } else if (errorDesp.contains("DV1016") || errorDesp.contains("DV1025")) { // ??????????????????(?????????????????????/??????SC????????????????????????
                        mView.get().showToastInfo(R.string.add_device_verify_device_pwd_failed);
                    } else if (errorDesp.contains("DV1037")) {  //NB?????????imei???device id ?????????
                        mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH);
                    } else {
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                    }
                }
            }
        };
        DeviceAddModel.newInstance().bindDevice(sn, encryptPwd, bindHandler);
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

    private void onIPLogInResult(int result) {
        if (mView.get() == null
                || (mView.get() != null && !mView.get().isViewActive())) {
            return;
        }
        mView.get().cancelProgressDialog();

        DeviceAddInfo.DeviceAddType deviceAddType = DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType();

        if (result != FinalVar.NET_NOERROR) {
            if (DeviceAddHelper.isDevPwdError(result)) {//????????????
                mView.get().showToastInfo(R.string.add_device_password_error_and_will_lock);
            } else {
                if (P2PErrorHelper.LOGIN_ERROR_USER_LOCKED == result
                        || FinalVar.NET_LOGIN_ERROR_LOCKED == result) {//???????????????
                    mView.get().goErrorTipPage(DeviceAddHelper.ErrorCode.COMMON_ERROR_DEVICE_LOCKED);
                } else {
                    //???????????????????????????
                    mView.get().showToastInfo(R.string.add_device_connect_error_and_quit_retry);
                }
            }
            return;
        }
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        deviceAddInfo.setDevicePwd(mView.get().getDevicePassword());
        if (mIsWifiOfflineMode) {
            if (DeviceAddInfo.DeviceAddType.SOFTAP.equals(deviceAddType)) {
                mView.get().goSoftAPWifiListPage();
            } else {
                //TODO ????????????????????????????????????deviceAddInfo.getDevicePwd()?????????????????????????????????
                mView.get().showToastInfo(R.string.add_device_wifi_config_success);
                EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
                ProviderManager.getDeviceAddCustomProvider().goHomePage(mView.get().getContextInfo());
            }
        } else {
            if (DeviceAddInfo.DeviceAddType.SOFTAP.equals(deviceAddType)) {
                mView.get().goSoftAPWifiListPage();
            } else {
                mView.get().goDeviceBindPage();
            }
        }
    }
}
