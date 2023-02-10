package com.mm.android.deviceaddmodule.presenter;

import android.os.Handler;
import android.os.Message;

import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.SearchDeviceManager;
import com.mm.android.deviceaddmodule.contract.TipWifiConnectConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import java.lang.ref.WeakReference;

public class TipWifiConnectPresenter implements TipWifiConnectConstract.Presenter {
    private static final int SEARCH_SUCCESS = 1;
    private static final int SEARCH_FAILED = 2;
    private static final int NEED_INIT = 3;
    private static final long SEARCH_TIME=3*1000;       //搜索时间
    private int searchIndex=0;                            //当前搜索次数
    private long searchIntervalTime=500;                //搜索间隔时间

    WeakReference<TipWifiConnectConstract.View> mView;
    DeviceInitInfo mDeviceNetInfoEx;
    String mDeviceSn;

    public TipWifiConnectPresenter(TipWifiConnectConstract.View view) {
        mView = new WeakReference<>(view);
        mDeviceSn= DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mView.get().cancelProgressDialog();
            stopSearchDevice();
            switch (msg.what) {
                case SEARCH_SUCCESS:
                    mView.get().goCloudConnectPage();
                    break;
                case NEED_INIT:
                    mView.get().goDevInitPage(mDeviceNetInfoEx);
                    break;
                case SEARCH_FAILED:
                    // 未搜到设备，进入配网流程
                    mView.get().goWifiConfigPage();
                    break;
            }

        }
    };

    @Override
    public void searchDevice() {
        if (Utils4AddDevice.isWifi(mView.get().getContextInfo())) {
            if(DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode()){//离线配网
                mView.get().goWifiConfigPage();
                return;
            }
            mView.get().showProgressDialog();
            startSearch();
        }

    }

    @Override
    public void stopSearchDevice() {
        mHandler.removeCallbacks(SearchRunnable);
        searchIndex=0;
    }

    @Override
    public String getConfigMode() {
        DeviceAddInfo deviceAddInfo= DeviceAddModel.newInstance().getDeviceInfoCache();
        return deviceAddInfo.getConfigMode();
    }

    private void startSearch(){
        stopSearchDevice();
        mHandler.post(SearchRunnable);
    }

    private Runnable SearchRunnable=new Runnable() {
        @Override
        public void run() {
            searchIndex++;
            SearchDeviceManager manager = SearchDeviceManager.getInstance();
            mDeviceNetInfoEx = manager.getDeviceNetInfo(mDeviceSn);
            if(mDeviceNetInfoEx!=null){
                if(DeviceAddHelper.isDeviceNeedInit(mDeviceNetInfoEx) && !DeviceAddHelper.isSupportAddBySc(DeviceAddModel.newInstance().getDeviceInfoCache())) {
                    mHandler.obtainMessage(NEED_INIT).sendToTarget();
                }else{
                    mHandler.obtainMessage(SEARCH_SUCCESS).sendToTarget();
                }
            }else{
                if(searchIndex<(SEARCH_TIME/searchIntervalTime))
                    mHandler.postDelayed(this,searchIntervalTime);
                else{
                    mHandler.obtainMessage(SEARCH_FAILED).sendToTarget();
                }
            }

        }
    };
}
