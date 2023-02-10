package com.common.openapi;

import android.content.Context;

import com.common.openapi.entity.DeviceLocalCacheData;

public class DeviceLocalCacheManager {

    private DeviceLocalCacheDatabase deviceLocalCacheDatabase;

    public void init(Context context) {
        deviceLocalCacheDatabase = new DeviceLocalCacheDatabase(context);
    }

    public DeviceLocalCacheData findLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        DeviceLocalCacheData find = deviceLocalCacheDatabase.findLocalCache(deviceLocalCacheData);
        return find;
    }

}
