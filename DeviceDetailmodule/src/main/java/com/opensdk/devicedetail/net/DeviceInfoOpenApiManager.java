package com.opensdk.devicedetail.net;

import com.google.gson.JsonObject;
import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.deviceaddmodule.openapi.data.DeviceDetailListData;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.opensdk.devicedetail.constance.MethodConst;
import com.opensdk.devicedetail.entity.DeviceAlarmStatusData;
import com.opensdk.devicedetail.entity.DeviceChannelInfoData;
import com.opensdk.devicedetail.entity.DeviceModifyNameData;
import com.opensdk.devicedetail.entity.DeviceUnBindData;
import com.opensdk.devicedetail.entity.DeviceVersionListData;
import com.opensdk.devicedetail.entity.DevSdStoreData;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceInfoOpenApiManager {

    private static int TIME_OUT = 10 * 1000;
    private static int DMS_TIME_OUT = 45 * 1000;


    /**
     * Obtain detailed information about devices in batches based on the serial numbers, channel numbers, and parts numbers
     * @param deviceDetailListData  Device details Set data
     * @return DeviceDetailListData.Response  Device details Set response data
     * @throws BusinessException
     *
     * 批量根据设备序列号、通道号列表和配件号列表，获取设备的详细信息
     * @param deviceDetailListData  设备详情集合数据
     * @return DeviceDetailListData.Response  设备详情集合响应数据
     * @throws BusinessException
     */
    public static DeviceDetailListData.Response deviceOpenDetailList(DeviceDetailListData deviceDetailListData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceList", deviceDetailListData.data.deviceList);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_OPEN_DETAIL_LIST, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Obtain detailed information of devices added or shared by LeOrange app in batches according to device serial number, channel number list and accessory number list
     * @param deviceDetailListData  Device details Set data
     * @return DeviceDetailListData.Response  Device details Set response data
     * @throws BusinessException
     *
     * 批量根据设备序列号、通道号列表和配件号列表，获取乐橙app添加或分享的设备的详细信息
     * @param deviceDetailListData  设备详情集合数据
     * @return DeviceDetailListData.Response  设备详情集合响应数据
     * @throws BusinessException
     */
    public static DeviceDetailListData.Response deviceBaseDetailList(DeviceDetailListData deviceDetailListData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceList", deviceDetailListData.data.deviceList);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_BASE_DETAIL_LIST, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * UnBundling Device
     * @param deviceUnBindData  Device UnBind Data
     * @return boolean  Boolean Value
     * @throws BusinessException
     *
     * 解绑设备
     * @param deviceUnBindData  设备未绑定数据
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public static boolean unBindDevice(DeviceUnBindData deviceUnBindData) throws BusinessException {
        // 解绑设备
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceUnBindData.data.deviceId);
        HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_UN_BIND, TIME_OUT);
        return true;
    }

    public static boolean deletePermission(String deviceId, String channelId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("openid", TokenHelper.getInstance().openid);
        paramsMap.put("channelId", channelId);
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        HttpSend.execute(paramsMap, MethodConst.DELETE_DEVICE_PERMISSION, TIME_OUT);
        return true;
    }

    /**
     * Obtain the device version and upgradable information
     * @param deviceVersionListData  Device version collection data
     * @return DeviceVersionListData.Response  Device version sets response data
     * @throws BusinessException
     *
     * 获取设备版本和可升级信息
     * @param deviceVersionListData  设备版本集合数据
     * @return DeviceVersionListData.Response  设备版本集合响应数据
     * @throws BusinessException
     */
    public static DeviceVersionListData.Response deviceVersionList(DeviceVersionListData deviceVersionListData) throws BusinessException {

        ArrayList list = new ArrayList();
        HashMap<String, Object> devices = new HashMap<String, Object>();
        devices.put("deviceId", deviceVersionListData.data.deviceIds);
        devices.put("productId", deviceVersionListData.data.productId);
        list.add(devices);

        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceList", list);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_LIST_DEVICE_DETAIL_BATCH, TIME_OUT);
        DeviceVersionListData.Response response = new DeviceVersionListData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Change the name of a device or channel
     * @param deviceModifyNameData  Device Modify Name Data
     * @return boolean Boolean Value
     * @throws BusinessException
     *
     * 修改设备或通道名称
     * @param deviceModifyNameData  设备修改名称数据
     * @return boolean 布尔值
     * @throws BusinessException
     */
    public static boolean modifyDeviceName(DeviceModifyNameData deviceModifyNameData) throws BusinessException {
        // 解绑设备
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceModifyNameData.data.deviceId);
        paramsMap.put("channelId", deviceModifyNameData.data.channelId);
        paramsMap.put("name", deviceModifyNameData.data.name);
        HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_MODIFY_NAME, TIME_OUT);
        return true;
    }

    /**
     * Obtain detailed information about a single device channel
     * @param deviceChannelInfoData  Device Channel Info Data
     * @return DeviceChannelInfoData.Response  Device channel information Response data
     * @throws BusinessException
     *
     * 单个设备通道的详细信息获取
     * @param deviceChannelInfoData  设备通道信息数据
     * @return DeviceChannelInfoData.Response  设备通道信息响应数据
     * @throws BusinessException
     */
    public static DeviceChannelInfoData.Response bindDeviceChannelInfo(DeviceChannelInfoData deviceChannelInfoData) throws BusinessException {
        // 单个设备通道的详细信息获取
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceChannelInfoData.data.deviceId);
        paramsMap.put("channelId", deviceChannelInfoData.data.channelId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_CHANNEL_INFO, DMS_TIME_OUT);
        DeviceChannelInfoData.Response response = new DeviceChannelInfoData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Set the dynamic check switch
     * @param deviceAlarmStatusData  Device dynamic check status data
     * @return boolean  Boolean Value
     * @throws BusinessException
     *
     * 设置动检开关
     * @param deviceAlarmStatusData  设备动检状态数据
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public static boolean modifyDeviceAlarmStatus(DeviceAlarmStatusData deviceAlarmStatusData) throws BusinessException {
        // 解绑设备
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceAlarmStatusData.data.deviceId);
        paramsMap.put("channelId", deviceAlarmStatusData.data.channelId);
        paramsMap.put("enable", deviceAlarmStatusData.data.enable);
        HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_MODIFY_ALARM_STATUS, DMS_TIME_OUT);
        return true;
    }

    /**
     * Device upgrades
     * @param deviceId  Device Id
     * @return boolean  Boolean Value
     * @throws BusinessException
     *
     * 设备升级
     * @param deviceId  设备Id
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public static boolean upgradeDevice(String deviceId) throws BusinessException {
        // 解绑设备
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject execute = HttpSend.execute(paramsMap, MethodConst.METHOD_DEVICE_UPDATE, DMS_TIME_OUT);
        if (execute != null) {
            return true;
        } else {
            return false;
        }


    }


    public static int queryCloudUse(String deviceId, String channelId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.GET_DEVICE_CLOUD, DMS_TIME_OUT);

        Integer state = new Integer(0);
        if (json.has("strategyStatus")) {
            state = json.get("strategyStatus").getAsInt();
        } else {
            state = -1;
        }
        return state;
    }

    public static String querySDState(String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.SD_STATUE_QUERY, DMS_TIME_OUT);
        String state = json.get("status").getAsString();
        return state;
    }


    public static DeviceDetailListData.Response getSubAccountDeviceList(int pageNo, int pageSize) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("page", pageNo);
        paramsMap.put("pageSize", pageSize);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.SUB_ACCOUNT_DEVICE_LIST, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    public static DeviceDetailListData.Response getSubAccountDeviceListNew(int pageNo, int pageSize) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("page", pageNo);
        paramsMap.put("pageSize", pageSize);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.QUERY_DEVICEDETAIL_PAGE, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * SD card: Obtains storage media capacity information
     * @param deviceId Device Id
     *
     * SD卡：获取设备存储介质容量信息
     * @param deviceId 设备Id
     * @throws BusinessException
     */
    public static DevSdStoreData getDeviceStoreInfo(String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.GET_STORE_INFO, TIME_OUT);
        DevSdStoreData.Response response = new DevSdStoreData.Response();
        response.parseData(json);
        return response.getData();
    }

    /**
     * SD card: The SD card is formatted
     * @param deviceId Device Id
     *
     * SD卡：设备SD卡格式化
     * @param deviceId 设备Id
     * @throws BusinessException
     */
    public static String recoverSDCard(String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.RECOVER_SDCARD, TIME_OUT);
        return json.get("result").getAsString();
    }

    /**
     * SD card: Obtain the SD card status of the device
     * @param deviceId Device Id
     *
     * SD卡：获取设备SD卡状态
     * @param deviceId 设备Id
     * @throws BusinessException
     */
    public static String deviceSdcardStatus(String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.DEVICE_SD_CARD_STATUS, TIME_OUT);
        return json.get("status").getAsString();
    }

    /**
     * Cloud storage: Set the cloud storage service switch of the current device
     * @param deviceId  Device Id
     * @param channelId Channel Id
     * @param status    Status
     *
     * 云存储：设置当前设备的云存储服务开关
     * @param deviceId  设备Id
     * @param channelId 通道Id
     * @param status    云存储状态
     * @throws BusinessException
     */
    public static void setAllStorageStrategy(String deviceId, String channelId, String status) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        paramsMap.put("status", status);
        HttpSend.execute(paramsMap, MethodConst.SET_CLOUD_STORAGE, TIME_OUT);

    }
}