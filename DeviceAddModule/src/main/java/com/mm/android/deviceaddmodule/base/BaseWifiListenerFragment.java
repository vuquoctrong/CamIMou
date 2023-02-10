package com.mm.android.deviceaddmodule.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;

import com.dahua.mobile.utility.network.DHWifiUtil;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * It is mainly used to monitor the change of wifi status. If it changes, it will be unbound. If it is still ours after connecting, it will be rebound
 *
 * 主要用于监听wifi状态的变化，变化的话就解除绑定，连接上后还是我们的那个的话就重新绑定
 */
public abstract class BaseWifiListenerFragment extends BaseDevAddFragment {
    protected DHWifiUtil mDHWifiUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDHWifiUtil = new DHWifiUtil(getContext().getApplicationContext());
    }

    @Override
    protected IntentFilter createBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        return filter;
    }



    @Override
    protected void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
       if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            Parcelable parcelable = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (parcelable != null) {
                NetworkInfo networkInfo = (NetworkInfo) parcelable;
                NetworkInfo.State state = networkInfo.getState();
                if (state == NetworkInfo.State.DISCONNECTED) {
                    DeviceAddHelper.clearNetWork();
                } else if (state == NetworkInfo.State.CONNECTED) {
                    DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
                        String mSsid = deviceAddInfo.getSsid();
                        String mCurrentSsid = getCurrentWifiInfo();
                        if (mCurrentSsid!=null){
                            String mTempStr = mCurrentSsid.startsWith("\"")  ? mCurrentSsid : "\""+mCurrentSsid+"\"";
                            String mTempStrCache =  "\"" + mSsid + "\"";
                            if (mTempStr.equals(mTempStrCache)){
                                DeviceAddHelper.bindNetwork(null);
                            }
                        }
                }
            }
        }

    }


    public String getCurrentWifiInfo(){
        WifiInfo wifiInfo = mDHWifiUtil.getCurrentWifiInfo();
        return wifiInfo!=null?wifiInfo.getSSID():null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterBroadCast();
    }

}
