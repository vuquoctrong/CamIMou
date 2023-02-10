package com.lc.media.api;

import com.lc.media.entity.CloudRecordsData;
import com.lc.media.entity.DeviceChannelInfoData;
import com.lc.media.entity.DeviceDetailListData;
import com.lc.media.entity.DeviceLocalCacheData;
import com.lc.media.entity.DeviceVersionListData;
import com.lc.media.entity.LocalRecordsData;

public interface IGetDeviceInfoCallBack {
    public interface IDeviceListCallBack {
        /**
         * The device list is successfully obtained. Procedure
         * @param responseData  Response Data
         *
         * 成功获取到设备列表
         * @param responseData  响应数据
         */
        void deviceList(DeviceDetailListData.Response responseData);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceVersionCallBack {
        /**
         * Obtain the device version and upgradable information
         * @param responseData
         *
         * 获取设备版本和可升级信息
         * @param responseData
         */
        void deviceVersion(DeviceVersionListData.Response responseData);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IModifyDeviceCallBack {
        /**
         * The device or channel name is changed successfully. Procedure
         * @param result  Modify the results
         *
         * 修改设备或通道名称成功
         * @param result  修改结果
         */
        void deviceModify(boolean result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IUnbindDeviceCallBack {
        /**
         * The device is successfully unbound. Procedure
         * @param result  UnBundling results
         *
         * 解绑设备成功
         * @param result  解绑结果
         */
        void unBindDevice(boolean result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceChannelInfoCallBack {
        /**
         * Obtain detailed information about a single device channel
         * @param result  To get the results
         *
         * 单个设备通道的详细信息获取
         * @param result  获取结果
         */
        void deviceChannelInfo(DeviceChannelInfoData.Response result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceAlarmStatusCallBack {
        /**
         * Set the dynamic check switch
         * @param result  Set the results
         *
         * 设置动检开关
         * @param result  设置结果
         */
        void deviceAlarmStatus(boolean result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceUpdateCallBack {
        /**
         * Device upgrade
         * @param result  The upgrade
         *
         * 设备升级
         * @param result  升级结果
         */
        void deviceUpdate(boolean result);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(Throwable throwable);
    }

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

//    public interface IDeviceCurrentWifiInfoCallBack {
//        /**
//         * 设备当前连接热点信息
//         */
//        void deviceCurrentWifiInfo(CurWifiInfo curWifiInfo);
//
//        /**
//         * 错误回调
//         *
//         * @param throwable
//         */
//        void onError(Throwable throwable);
//    }

    public interface IModifyDeviceName {
        /**
         * The modified name of the device
         *
         * 设备修改之后的名字
         */
        void deviceName(String newName);
    }

    public interface ISubAccountDevice<T>{
        void DeviceList(T response);

        void onError(Throwable throwable);
    }

    public interface ICommon<T>{
        void onCommonBack(T response);
        void onError(Throwable throwable);
    }



}
