package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.LCBaseHandler;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.device.LCDevice;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.mm.android.mobilecommon.openapi.TokenHelper;

import java.util.ArrayList;
import java.util.List;

public class DeviceWifiListPresenter<T extends DeviceWifiListConstract.View>
        extends BasePresenter<T> implements DeviceWifiListConstract.Presenter {

    protected LCDevice mLCDevice;
    protected CurWifiInfo mCurWifiInfo;
    protected List<WifiInfo> mWifiInfos;
    protected LCBusinessHandler mGetWifiConfigHandler;
    protected boolean mIsLoading = false;

    public DeviceWifiListPresenter(T view) {
        super(view);
        initModel();
        mWifiInfos = new ArrayList<>();
    }

    protected void initModel() {

    }

    @Override
    public void dispatchIntentData(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            mLCDevice = (LCDevice) bundle.getSerializable(DeviceConstant.IntentKey.LCDEVICE_INFO);
            if (bundle.containsKey(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO))
                mCurWifiInfo = (CurWifiInfo) bundle.getSerializable(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO);
        }
        if (mLCDevice == null)
            mView.get().viewFinish();
    }

    @Override
    public void unInit() {
        if (mGetWifiConfigHandler != null) {
            mGetWifiConfigHandler.cancle();
            mGetWifiConfigHandler.removeCallbacksAndMessages(null);
            mGetWifiConfigHandler = null;
        }
    }

    @Override
    public void getDeviceWifiListAsync() {
        if (mIsLoading)
            return;
        if (mGetWifiConfigHandler != null) {
            mGetWifiConfigHandler.cancle();
            mGetWifiConfigHandler = null;
        }
        mGetWifiConfigHandler = new LCBaseHandler<T>(mView) {
            @Override
            public void onStart() {
                mView.get().showProgressDialog();
                mIsLoading = true;
            }

            @Override
            protected void onCompleted() {
                mView.get().cancelProgressDialog();
                mIsLoading = false;
            }

            @Override
            public void handleBusinessFinally(Message msg) {
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    WifiConfig wifiConfig = (WifiConfig) msg.obj;
                    if (wifiConfig != null && wifiConfig.isEnable()) {
                        List<WifiInfo> wifiInfos = processWifiInfos(wifiConfig.getwLan());
                        mWifiInfos.clear();
                        mWifiInfos.addAll(wifiInfos);
                        mView.get().refreshListView(mWifiInfos);
                        mView.get().onLoadSucceed(mWifiInfos.isEmpty(), false);
                        mView.get().updateCurWifiLayout(mCurWifiInfo);
                    } else {
                        mView.get().onLoadSucceed(true, false);
                        mView.get().updateCurWifiLayout(mCurWifiInfo);
                        mView.get().showToastInfo(mView.get().getContextInfo().getString(R.string.device_manager_wifi_disable));
                    }
                } else {
                    mView.get().onLoadSucceed(true, true);
                    mView.get().updateCurWifiLayout(null);
                    mView.get().showToastInfo(mView.get().getContextInfo().getString(R.string.mobile_common_get_info_failed));
                }
            }
        };
        new BusinessRunnable(mGetWifiConfigHandler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    WifiConfig response = DeviceAddOpenApiManager.wifiAround(TokenHelper.getInstance().subAccessToken, mLCDevice.getDeviceId());
                    mGetWifiConfigHandler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    @Override
    public WifiInfo getWifiInfo(int position) {
        if (position >= mWifiInfos.size())
            return null;
        return mWifiInfos.get(position);
    }

    public boolean isSupport5G(String wifiMode) {
        if (!TextUtils.isEmpty(wifiMode)) {
            return wifiMode.toUpperCase().contains("5GHZ");
        }
        return false;
    }

    @Override
    public CurWifiInfo getCurWifiInfo() {
        return mCurWifiInfo;
    }

    @Override
    public LCDevice getLCDevice() {
        return mLCDevice;
    }

    /**
     * Remove the same wifi and the currently connected wifi
     * @param wifiInfos  Wifi information class object collection
     * @return List<WifiInfo>  Wifi information class object collection
     *
     * 处理去掉相同的wifi,和当前已连接的wifi
     * @param wifiInfos  Wifi信息类对象集合
     * @return List<WifiInfo>  Wifi信息类对象集合
     */
    protected List<WifiInfo> processWifiInfos(List<WifiInfo> wifiInfos) {
        List<WifiInfo> tems = new ArrayList<>();
        if (mCurWifiInfo == null) {
            mCurWifiInfo = new CurWifiInfo();
        }
        if (wifiInfos == null || wifiInfos.isEmpty())
            return tems;
        // 获取当前连接的wifi
        for (WifiInfo wifiInfo : wifiInfos) {
            if ("2".equals(wifiInfo.getLinkStatus())) {
                mCurWifiInfo.setIntensity(wifiInfo.getIntensity());
                mCurWifiInfo.setSsid(wifiInfo.getSsid());
                mCurWifiInfo.setAuth(wifiInfo.getAuth());
                mCurWifiInfo.setLinkEnable(true);
                break;
            }
        }

        for (WifiInfo wifiInfo : wifiInfos) {
            boolean isContains = false;
            for (WifiInfo tem : tems) {
                if (wifiInfo.getSsid() != null && wifiInfo.getSsid().equalsIgnoreCase(tem.getSsid())) {
                    isContains = true;
                    break;
                }
            }

            if (isContains) {
                //已包含，去重
                continue;
            }

            if (mCurWifiInfo.getSsid() == null) {
                //当前热点为空
                tems.add(wifiInfo);
            } else if (!mCurWifiInfo.getSsid().equalsIgnoreCase(wifiInfo.getSsid())) {
                //当前热点不为空，去除当前已连接wifi
                tems.add(wifiInfo);
            }
        }

        return tems;
    }

    public boolean isDevSupport5G() {
        String wifiMode = "";
        if (!TextUtils.isEmpty(wifiMode)) {
            return wifiMode.toUpperCase().contains("5GHZ");
        }
        return false;
    }
}
