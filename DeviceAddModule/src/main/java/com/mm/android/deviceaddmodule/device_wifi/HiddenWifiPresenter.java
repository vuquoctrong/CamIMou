package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Intent;
import android.os.Handler;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.LCBaseHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.mm.android.mobilecommon.openapi.TokenHelper;

public class HiddenWifiPresenter<T extends HiddenWifiConstract.View> extends BasePresenter<T> implements HiddenWifiConstract.Presenter {

    protected String mDeviceId;
    private LCBaseHandler mWifiOperateHandler;

    public HiddenWifiPresenter(T view) {
        super(view);
        initModel();
    }

    protected void initModel() {

    }

    @Override
    public void dispatchIntentData(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            mDeviceId = intent.getStringExtra(LCConfiguration.Device_ID);
        }
    }


    @Override
    public void wifiOperate() {
        if (mView.get().getWifiSSID().equalsIgnoreCase("")) {
            return;
        }
        new BusinessRunnable(mWifiOperateHandler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceAddOpenApiManager.controlDeviceWifi(TokenHelper.getInstance().accessToken, mDeviceId, mView.get().getWifiSSID(),"", true, mView.get().getWifiPassword());
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
    public void unInit() {

    }
}
