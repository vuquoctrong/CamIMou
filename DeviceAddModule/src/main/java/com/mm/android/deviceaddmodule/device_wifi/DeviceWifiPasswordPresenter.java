package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Intent;
import android.os.Handler;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.LCBaseHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;

public class DeviceWifiPasswordPresenter<T extends DeviceWifiPasswordConstract.View> extends BasePresenter<T> implements DeviceWifiPasswordConstract.Presenter {

    public String WIFI_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_ADD_";
    public String WIFI_CHECKBOX_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_CHECKBOX_ADD_";

    protected String mDeviceId;
    protected WifiInfo mWifiInfo;
    private LCBaseHandler mWifiOperateHandler;
    protected boolean support5G = false;

    public DeviceWifiPasswordPresenter(T view) {
        super(view);
        initModel();
    }

    protected void initModel() {

    }

    @Override
    public void dispatchIntentData(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            mDeviceId = intent.getStringExtra(LCConfiguration.Device_ID);
            mWifiInfo = (WifiInfo) intent.getSerializableExtra(DeviceConstant.IntentKey.DEVICE_WIFI_CONFIG_INFO);
            support5G = intent.getBooleanExtra(LCConfiguration.SUPPORT_5G, false);
        }
    }

    @Override
    public boolean getSupport5G() {
        return support5G;
    }

    @Override
    public void updateWifiCache() {
        if (mView.get().isSavePwdChecked()) {
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + mWifiInfo.getSsid(), mView.get().getWifiPassword());
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + mWifiInfo.getSsid(), true);
        } else {
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_SAVE_PREFIX + mWifiInfo.getSsid(), "");
            PreferencesHelper.getInstance(mView.get().getContextInfo()).set(WIFI_CHECKBOX_SAVE_PREFIX + mWifiInfo.getSsid(), false);
        }
    }

    @Override
    public String getSavedWifiPassword() {
        return PreferencesHelper.getInstance(mView.get().getContextInfo()).getString(WIFI_SAVE_PREFIX + mWifiInfo.getSsid());
    }

    @Override
    public boolean getSavedWifiCheckBoxStatus() {
        return PreferencesHelper.getInstance(mView.get().getContextInfo()).getBoolean(WIFI_CHECKBOX_SAVE_PREFIX + mWifiInfo.getSsid());
    }

    @Override
    public void wifiOperate() {
        new BusinessRunnable(mWifiOperateHandler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceAddOpenApiManager.controlDeviceWifi(TokenHelper.getInstance().accessToken, mDeviceId, mWifiInfo.getSsid(), mWifiInfo.getBSSID(), true, mView.get().getWifiPassword());
                    mWifiOperateHandler.obtainMessage(HandleMessageCode.HMC_SUCCESS, true).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
        mView.get().showToastInfo(R.string.device_manager_wifi_connetting_tip);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.get().onWifiOperateSucceed(null);
            }
        }, 3000);
    }

    @Override
    public String getWifiSSID() {
        if (mWifiInfo == null)
            return "";
        return mWifiInfo.getSsid();
    }

    @Override
    public void unInit() {

    }
}
