package com.opensdk.devicedetail.callback;

import com.mm.android.deviceaddmodule.device_wifi.CurWifiInfo;
import com.mm.android.deviceaddmodule.openapi.data.DeviceDetailListData;
import com.opensdk.devicedetail.entity.DeviceChannelInfoData;
import com.opensdk.devicedetail.entity.DeviceVersionListData;

public interface IGetDeviceInfoCallBack {
    public interface IDeviceVersionCallBack {
        /**
         * get device version
         * @param responseData response data
         *
         * 获取设备版本和可升级信息
         * @param responseData 返回的数据
         */
        void deviceVersion(DeviceVersionListData.Response responseData);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IModifyDeviceCallBack {
        /**
         * modify device name
         * @param result result
         *
         * 修改设备或通道名称
         * @param result 修改结果
         */
        void deviceModify(boolean result);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IUnbindDeviceCallBack {
        /**
         * unbind device
         * @param result result
         *
         * 解绑设备
         * @param result 解绑结果
         */
        void unBindDevice(boolean result);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceChannelInfoCallBack {
        /**
         * get device channel info
         * @param result result
         *
         * 单个设备通道的详细信息获取
         * @param result 设备通道信息实体类
         */
        void deviceChannelInfo(DeviceChannelInfoData.Response result);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceAlarmStatusCallBack {
        /**
         * set device alarm
         * @param result result
         *
         * 设置动检开关
         * @param result 修改结果
         */
        void deviceAlarmStatus(boolean result);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceUpdateCallBack {
        /**
         * device update
         * @param result result
         *
         * 设备升级
         * @param result 升级结果
         */
        void deviceUpdate(boolean result);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IDeviceCurrentWifiInfoCallBack {
        /**
         * device current wifi info
         * @param curWifiInfo current wifi info
         *
         * 设备当前连接热点信息
         * @param curWifiInfo 当前wifi信息
         */
        void deviceCurrentWifiInfo(CurWifiInfo curWifiInfo);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    public interface IModifyDeviceName {
        /**
         * modify device name
         * @param newName new name
         *
         * 设备修改之后的名字
         * @param newName 新名字
         */
        void deviceName(String newName);
    }

    public interface ICommon<T>{

        /**
         * common back
         * @param response response
         *
         * 公共回调
         * @param response 响应体
         */
        void onCommonBack(T response);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

    interface IDeviceAlarmPlanCallBack<T> {
        /**
         * get device alarm plan
         * @param response response
         *
         * 获取设备动检计划
         * @param response 响应体
         */
        void getDeviceAlarmPlan(T response);

        /**
         * error
         * @param throwable throwable
         *
         * 错误回调
         * @param throwable 错误异常
         */
        void onError(Throwable throwable);
    }

}
