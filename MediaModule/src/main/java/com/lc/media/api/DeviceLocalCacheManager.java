package com.lc.media.api;

import android.content.Context;

import com.lc.media.entity.DeviceLocalCacheData;
import com.lc.media.utils.MediaPlayHelper;


public class DeviceLocalCacheManager {

    private volatile static DeviceLocalCacheManager deviceLocalCacheManager;

    public static DeviceLocalCacheManager getInstance() {
        if (deviceLocalCacheManager == null) {
            synchronized (DeviceLocalCacheManager.class) {
                if (deviceLocalCacheManager == null) {
                    deviceLocalCacheManager = new DeviceLocalCacheManager();
                }
            }
        }
        return deviceLocalCacheManager;
    }

    private DeviceLocalCacheDatabase deviceLocalCacheDatabase;

    public void init(Context context) {
        deviceLocalCacheDatabase = new DeviceLocalCacheDatabase(context);
    }

    public int addLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        DeviceLocalCacheData find = deviceLocalCacheDatabase.findLocalCache(deviceLocalCacheData);
        if (find == null) {
            int add = deviceLocalCacheDatabase.addLocalCache(deviceLocalCacheData);
            return add;
        } else {
            deviceLocalCacheData.setCacheId(find.getCacheId());
            int update = deviceLocalCacheDatabase.updateLocalCache(deviceLocalCacheData);
            //删除之前缓存文件
            MediaPlayHelper.delete(find.getPicPath());
            return update;
        }
    }

    public DeviceLocalCacheData findLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        DeviceLocalCacheData find = deviceLocalCacheDatabase.findLocalCache(deviceLocalCacheData);
        return find;
    }

    public int updateLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        DeviceLocalCacheData find = deviceLocalCacheDatabase.findLocalCache(deviceLocalCacheData);
        if (find == null) {
            return 0;
        }
        int update = deviceLocalCacheDatabase.updateLocalCache(find);
        return update;
    }
}
