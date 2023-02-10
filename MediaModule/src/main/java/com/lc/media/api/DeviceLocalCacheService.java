package com.lc.media.api;

import android.os.Message;

import com.lc.media.entity.DeviceLocalCacheData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;

public class DeviceLocalCacheService {

    private volatile static DeviceLocalCacheService deviceLocalCacheService;

    public static DeviceLocalCacheService getInstance() {
        if (deviceLocalCacheService == null) {
            synchronized (DeviceLocalCacheService.class) {
                if (deviceLocalCacheService == null) {
                    deviceLocalCacheService = new DeviceLocalCacheService();
                }
            }
        }
        return deviceLocalCacheService;
    }

    /**
     * Example Add device cache information
     * @param localCacheData  Locally cached data
     *
     * 添加设备缓存信息
     * @param localCacheData  本地缓存数据
     */
    public void addLocalCache(final DeviceLocalCacheData localCacheData) {
        new BusinessRunnable(null) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceLocalCacheManager deviceLocalCacheManager = DeviceLocalCacheManager.getInstance();
                    deviceLocalCacheManager.addLocalCache(localCacheData);
                } catch (Throwable e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Obtain device cache information
     * @param localCacheData  Locally cached data
     * @param deviceCacheCallBack  Device cache callback
     *
     * 获取设备缓存信息
     * @param localCacheData  本地缓存数据
     * @param deviceCacheCallBack  设备缓存回调
     */
    public void findLocalCache(final DeviceLocalCacheData localCacheData, final IGetDeviceInfoCallBack.IDeviceCacheCallBack deviceCacheCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceCacheCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceCacheCallBack.deviceCache((DeviceLocalCacheData) msg.obj);
                } else {
                    //失败
                    deviceCacheCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceLocalCacheData localCache = DeviceLocalCacheManager.getInstance().findLocalCache(localCacheData);
                    if (localCache != null) {
                        handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, localCache).sendToTarget();
                    } else {
                        throw new BusinessException("null point");
                    }
                } catch (Throwable e) {
                    throw e;
                }
            }
        };
    }
}
