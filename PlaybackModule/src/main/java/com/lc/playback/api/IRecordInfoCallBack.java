package com.lc.playback.api;


import com.lc.playback.entity.CloudRecordsData;
import com.lc.playback.entity.DeviceLocalCacheData;
import com.lc.playback.entity.LocalRecordsData;

public interface IRecordInfoCallBack {


    public interface IDeviceCloudRecordCallBack {
        /**
         * Query device cloud video clips in reverse order
         * @param result  Query result
         *
         * 倒序查询设备云录像片段
         * @param result  查询结果
         */
        void deviceCloudRecord(CloudRecordsData.Response result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }


    public interface IDeviceLocalRecordCallBack {
        /**
         * Querying device device video clips
         * @param result  Query result
         *
         * 查询设备设备录像片段
         * @param result  查询结果
         */
        void deviceLocalRecord(LocalRecordsData.Response result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceDeleteRecordCallBack {
        /**
         * Delete the device cloud video clip
         *
         * 删除设备云录像片段
         */
        void deleteDeviceRecord();

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceCacheCallBack {
        /**
         * Obtain device cache information
         *
         * 获取设备缓存信息
         */
        void deviceCache(DeviceLocalCacheData deviceLocalCacheData);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

}
