package com.mm.android.deviceaddmodule;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import com.company.NetSDK.CB_fSDKLogCallBack;
import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.entity.DeviceNetInfo;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Search local area network device information management class
 *
 * 搜索局域网内设备信息的管理类
 */

public class SearchDeviceManager {
    private static final String TAG = "SearchDeviceService";

    private static volatile SearchDeviceManager sInstance;
    private volatile ConcurrentHashMap<String, DeviceNetInfo> mDeviceNetInfos = new ConcurrentHashMap<>();
    private SeachDeviceService.SearchDeviceBinder searchDevice;
    private ISearchDeviceListener mListener;
    private ISearchDeviceListener mListener2;
    boolean mIsConnected;               //service是否已连接
    volatile  boolean  mIsExist = false;

    private SearchDeviceManager() {
        mListener = new SearchDeviceImpl();
        mDeviceNetInfos = new ConcurrentHashMap<>();

    }

    public static SearchDeviceManager getInstance() {
        if (sInstance == null) {
            synchronized (SearchDeviceManager.class) {
                if (sInstance == null) {
                    sInstance = new SearchDeviceManager();

                }
            }
        }
        return sInstance;
    }


    public synchronized void connectService(ISearchDeviceListener listener) {
        if(listener!=null){
            mListener2 = listener;
        }

        if(!mIsConnected){
            Intent intent = new Intent(ProviderManager.getAppProvider().getAppContext(), SeachDeviceService.class);
            mIsConnected = ProviderManager.getAppProvider().getAppContext().bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        }
        if(!mIsExist){
            mIsExist = true;
        }
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            LogUtil.debugLog(TAG, "onServiceConnected");

            searchDevice = (SeachDeviceService.SearchDeviceBinder) arg1;

            if (searchDevice != null) {
                try {
                    searchDevice.linkToDeath(mBinderPoolDeathRecipient, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            registerListener(mListener);
            registerListener(mListener2);
            startSearch();
        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            LogUtil.debugLog(TAG, "binderDied");
            if (searchDevice != null) {
                searchDevice.unlinkToDeath(mBinderPoolDeathRecipient, 0);
                searchDevice = null;
            }
            connectService(null);
        }
    };

    public void registerListener(ISearchDeviceListener listener) {
        if (searchDevice != null) {
            searchDevice.registerListener(listener);
        }
    }

    public void unRegisterListener(ISearchDeviceListener listener) {
        if (searchDevice != null) {
            searchDevice.unRegisterListener(listener);
        }
    }

    public void stopSearch() {
        if (searchDevice != null) {
            searchDevice.stopSearchDevices();
            if(mIsConnected){
                ProviderManager.getAppProvider().getAppContext().unbindService(mBinderPoolConnection);
                mIsConnected = false;
            }

        }
    }

    public synchronized DeviceInitInfo getDeviceNetInfo(String snCode) {
        if (TextUtils.isEmpty(snCode))
            return null;
        if (mDeviceNetInfos != null&&mDeviceNetInfos.get(snCode)!=null&&mDeviceNetInfos.get(snCode).isValid()) {
            return mDeviceNetInfos.get(snCode).getDevNetInfoEx();
        }
        return null;
    }

    /**
     * Begin your search
     *
     * 开始搜索
     */
    public synchronized void startSearch() {
        removeInvalidDevice();
        if (searchDevice != null) {
            connectService(null);
            searchDevice.startSearchDevices();
        }
    }

    public synchronized void clearDevice() {
        if (mDeviceNetInfos != null) {
            mDeviceNetInfos.clear();
            LogUtil.debugLog(TAG, "clear");
        }
    }

    /**
     * The invalid device information was removed from the list
     *
     * 列表中移除无效的设备信息
     */
    public synchronized void removeInvalidDevice() {
        if (mDeviceNetInfos != null) {
            LogUtil.debugLog(TAG, "removeInvalidDevice： " + mDeviceNetInfos);
            for (Map.Entry<String, DeviceNetInfo> entry : mDeviceNetInfos.entrySet()) {
                if (entry.getValue() != null) {
                    if (!entry.getValue().isValid()) {
                        // 移除无效的DeviceNetInfo
                        mDeviceNetInfos.remove(entry.getKey());
                        LogUtil.debugLog(TAG, "remove： " + entry.getKey());
                    } else {
                        // 将标志位重置
                        entry.getValue().setValid(false);
                    }
                }
            }
            LogUtil.debugLog(TAG, "removeInvalidDevice： " + mDeviceNetInfos);
        }
    }

    /**
     * Release resources
     *
     * 释放资源
     */
    public synchronized void checkSearchDeviceServiceDestory() {
        stopSearch();
        unRegisterListener(mListener);
        clearDevice();
        mIsExist = false;
    }

    public  synchronized boolean checkSearchDeviceServiceIsExist() {
        return mIsExist;
    }

    private class SearchDeviceImpl implements ISearchDeviceListener {

        @Override
        public void onDeviceSearched(String sncode, DeviceInitInfo info) {
            if (mDeviceNetInfos != null) {
                DeviceNetInfo deviceNetInfo=new DeviceNetInfo(info);
                mDeviceNetInfos.put(sncode, deviceNetInfo);
                LogUtil.debugLog(TAG, "onDeviceSearched： " + mDeviceNetInfos);
            }
        }
    }

    public interface ISearchDeviceListener {
        void onDeviceSearched(String sncode, DeviceInitInfo info);
    }

}
