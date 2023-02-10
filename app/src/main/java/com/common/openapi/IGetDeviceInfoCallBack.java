package com.common.openapi;

import com.common.openapi.entity.DeviceLocalCacheData;

public interface IGetDeviceInfoCallBack {

    interface IDeviceCacheCallBack {
        /**
         * Obtain device cache information
         * @param deviceLocalCacheData  The device locally caches Data class objects
         *
         * 获取设备缓存信息
         * @param deviceLocalCacheData  设备本地缓存Data类对象
         */
        void deviceCache(DeviceLocalCacheData deviceLocalCacheData);

        /**
         * Error correction
         * @param throwable  Exception class object
         *
         * 错误回调
         * @param throwable  异常类对象
         */
        void onError(Throwable throwable);
    }

    interface ISubAccountDevice<T>{
        void DeviceList(T response);

        void onError(Throwable throwable);
    }

}
