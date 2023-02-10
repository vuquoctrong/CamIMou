package com.common.openapi;

import android.content.Context;

public class ClassInstanceManager {
    private volatile static ClassInstanceManager classInstanceManager;
    private DeviceLocalCacheManager deviceLocalCacheManager;
    private DeviceLocalCacheService deviceLocalCacheService;
    private DeviceSubAccountListService deviceSubAccountListService;

    public static ClassInstanceManager newInstance() {
        if (classInstanceManager == null) {
            synchronized (ClassInstanceManager.class) {
                if (classInstanceManager == null) {
                    classInstanceManager = new ClassInstanceManager();
                }
            }
        }
        return classInstanceManager;
    }

    public void init(Context context) {
        deviceLocalCacheManager = new DeviceLocalCacheManager();
        deviceLocalCacheManager.init(context);
        deviceLocalCacheService = new DeviceLocalCacheService();
        deviceSubAccountListService = new DeviceSubAccountListService();
    }

    public DeviceLocalCacheManager getDeviceLocalCacheManager() {
        return deviceLocalCacheManager;
    }

    public DeviceLocalCacheService getDeviceLocalCacheService() {
        return deviceLocalCacheService;
    }

    public DeviceSubAccountListService getDeviceSubAccountListService(){
        return deviceSubAccountListService;
    }
}
