package com.mm.android.deviceaddmodule.openapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mm.android.deviceaddmodule.device_wifi.CurWifiInfo;
import com.mm.android.deviceaddmodule.device_wifi.WifiConfig;
import com.mm.android.deviceaddmodule.openapi.data.AddDevicePolicyData;
import com.mm.android.deviceaddmodule.openapi.data.AlarmPlanQuery;
import com.mm.android.deviceaddmodule.openapi.data.BindDeviceData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceDetailListData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceInfoBeforeBindData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceLeadingInfoData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceModelOrLeadingInfoCheckData;
import com.mm.android.deviceaddmodule.openapi.data.IotDeviceLeadingInfoData;
import com.mm.android.deviceaddmodule.openapi.data.ModifyAlarmPlan;
import com.mm.android.deviceaddmodule.openapi.data.ModifyDeviceNameData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.CONST;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.openapi.TokenHelper;


import com.mm.android.iotdeviceaddmodule.data.IotAddDeviceInfo;
import com.mm.android.mobilecommon.openapi.data.TimeSlice;


import java.util.HashMap;
import java.util.List;

public class DeviceAddOpenApiManager {
    private static int TIME_OUT = 10 * 1000;

    private static int DMS_TIME_OUT = 45 * 1000;



    /**
     * The device is not bound
     * @param deviceInfoBeforeBindData  Device information object before binding
     * @return DeviceInfoBeforeBindData.Response  Pre-binding Device Information class Response subclass object
     * @throws BusinessException
     *
     * 设备未绑定状态
     * @param deviceInfoBeforeBindData  绑定前设备信息类对象
     * @return DeviceInfoBeforeBindData.Response  绑定前设备信息类Response子类对象
     * @throws BusinessException
     */
    public static DeviceInfoBeforeBindData.Response deviceInfoBeforeBind(DeviceInfoBeforeBindData deviceInfoBeforeBindData) throws BusinessException {
        // 未绑定设备信息获取
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", deviceInfoBeforeBindData.data.token);
        paramsMap.put("deviceId", deviceInfoBeforeBindData.data.deviceId);
        paramsMap.put("deviceCodeModel", deviceInfoBeforeBindData.data.deviceCodeModel);
        paramsMap.put("deviceModelName", deviceInfoBeforeBindData.data.deviceModelName);
        paramsMap.put("ncCode", deviceInfoBeforeBindData.data.ncCode);
        paramsMap.put("productId", deviceInfoBeforeBindData.data.productId);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_UNBINDDEVICEINFO, TIME_OUT);
        DeviceInfoBeforeBindData.Response response = new DeviceInfoBeforeBindData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Obtain the configuration information on the device adding process guide page based on the device market model
     * @param deviceLeadingInfoData  Device boot information class object
     * @return DeviceLeadingInfoData.Response  Device bootstrap Information class Response subclass object
     * @throws BusinessException
     *
     * 根据设备市场型号获取设备添加流程引导页配置信息
     * @param deviceLeadingInfoData  设备引导信息类对象
     * @return DeviceLeadingInfoData.Response  设备引导信息类Response子类对象
     * @throws BusinessException
     */
    public static DeviceLeadingInfoData.Response deviceLeadingInfo(DeviceLeadingInfoData deviceLeadingInfoData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", deviceLeadingInfoData.data.token);
        paramsMap.put("deviceModelName", deviceLeadingInfoData.data.deviceModelName);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_GUIDEINFOGET, TIME_OUT);
        DeviceLeadingInfoData.Response response = new DeviceLeadingInfoData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Obtain the configuration  information on the device adding process guide page based on the IOT device market model
     * @param deviceLeadingInfoData  Device boot information class object
     * @return IotDeviceLeadingInfoData.Response  The Iot device guides the information class Response subclass object
     * @throws BusinessException
     *
     * 根据IOT设备市场型号获取设备添加流程引导页配置信息
     * @param deviceLeadingInfoData  设备引导信息类对象
     * @return IotDeviceLeadingInfoData.Response  Iot设备引导信息类Response子类对象
     * @throws BusinessException
     */
    public static IotDeviceLeadingInfoData.Response iotDeviceLeadingInfo(IotDeviceLeadingInfoData deviceLeadingInfoData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", deviceLeadingInfoData.data.token);
        paramsMap.put("productId", deviceLeadingInfoData.data.productId);
        paramsMap.put("language", deviceLeadingInfoData.data.language);
        paramsMap.put("communicate", deviceLeadingInfoData.data.communicate);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_GETNETWORKCONFIG, TIME_OUT);
        IotDeviceLeadingInfoData.Response response = new IotDeviceLeadingInfoData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Check whether the configuration information needs to be updated
     * @param deviceModelOrLeadingInfoCheckData  Device module boot information check class object
     * @return DeviceModelOrLeadingInfoCheckData.Response  Device module bootstrap information validation class Response subclass object
     * @throws BusinessException
     *
     * 校验设备添加流程引导页配置信息是否需更新
     * @param deviceModelOrLeadingInfoCheckData  设备模块引导信息校验类对象
     * @return DeviceModelOrLeadingInfoCheckData.Response  设备模块引导信息校验类Response子类对象
     * @throws BusinessException
     */
    public static DeviceModelOrLeadingInfoCheckData.Response deviceModelOrLeadingInfoCheck(DeviceModelOrLeadingInfoCheckData deviceModelOrLeadingInfoCheckData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", deviceModelOrLeadingInfoCheckData.data.token);
        paramsMap.put("deviceModelName", deviceModelOrLeadingInfoCheckData.data.deviceModelName);
        paramsMap.put("updateTime", deviceModelOrLeadingInfoCheckData.data.updateTime);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_GUIDEINFOCHECK, TIME_OUT);
        DeviceModelOrLeadingInfoCheckData.Response response = new DeviceModelOrLeadingInfoCheckData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Binding equipment
     * @param bindDeviceData  Bind device class objects
     * @return BindDeviceData.Response  Bind device class Response subclass object
     * @throws BusinessException
     *
     * 绑定设备
     * @param bindDeviceData  绑定设备类对象
     * @return BindDeviceData.Response  绑定设备类Response子类对象
     * @throws BusinessException
     */
    public static BindDeviceData.Response userDeviceBind(BindDeviceData bindDeviceData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", bindDeviceData.data.token);
        paramsMap.put("deviceId", bindDeviceData.data.deviceId);
        paramsMap.put("code", bindDeviceData.data.code);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_BINDDEVICE, DMS_TIME_OUT);
        BindDeviceData.Response response = new BindDeviceData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Change the name of a device or channel
     * @param bindDeviceData  Bind device class objects
     * @return ModifyDeviceNameData.Response  Modify the device name response data
     * @throws BusinessException
     *
     * 修改设备或通道名称
     * @param bindDeviceData  绑定设备类对象
     * @return ModifyDeviceNameData.Response  修改设备名响应数据
     * @throws BusinessException
     */
    public static ModifyDeviceNameData.Response modifyDeviceName(ModifyDeviceNameData bindDeviceData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", bindDeviceData.data.token);
        paramsMap.put("deviceId", bindDeviceData.data.deviceId);
        paramsMap.put("channelId", bindDeviceData.data.channelId);
        paramsMap.put("name", bindDeviceData.data.name);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_MODIFYDEVICENAME, TIME_OUT);
        ModifyDeviceNameData.Response response = new ModifyDeviceNameData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Information about the current connection hotspot of the device
     * @param deviceId  Device Id
     * @return CurWifiInfo Current Wifi Information
     * @throws BusinessException
     *
     * 设备当前连接热点信息
     * @param deviceId  设备Id
     * @return CurWifiInfo 当前Wifi信息
     * @throws BusinessException
     */
    public static CurWifiInfo currentDeviceWifi(String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_CURRENT_DEVICE_WIFI, DMS_TIME_OUT);
        CurWifiInfo.Response response = new CurWifiInfo.Response();
        response.parseData(json);
        return response.data;
    }

    /**
     * WIFI information around the device
     * @param token  Device Token
     * @param deviceId  Device Id
     * @return WifiConfig  Wifi Configuration Information
     * @throws BusinessException
     *
     * 设备周边WIFI信息
     * @param token  设备Token
     * @param deviceId  设备Id
     * @return WifiConfig  Wifi配置信息
     * @throws BusinessException
     */
    public static WifiConfig wifiAround(String token, String deviceId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", token);
        paramsMap.put("deviceId", deviceId);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_WIFI_AROUND, DMS_TIME_OUT);
        WifiConfig.Response response = new WifiConfig.Response();
        response.parseData(json);
        return response.data;
    }

    /**
     * Controls the device connection to the hotspot
     * @param token  Device Token
     * @param deviceId  Device Id
     * @return boolean Boolean value
     * @throws BusinessException
     *
     * 控制设备连接热点
     * @param token  设备Token
     * @param deviceId  设备Id
     * @return boolean 布尔值
     * @throws BusinessException
     */
    public static boolean controlDeviceWifi(String token, String deviceId, String ssid, String bssid,
                                            boolean linkEnable, String password) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", token);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("ssid", ssid);
        paramsMap.put("bssid", bssid);
        paramsMap.put("linkEnable", linkEnable);
        paramsMap.put("password", password);
        HttpSend.execute(paramsMap, CONST.METHOD_CONTROL_DEVICE_WIFI, DMS_TIME_OUT);
        return true;
    }

    /**
     * You can obtain detailed information about devices in batches based on the serial numbers, channel numbers, and parts numbers
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
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceList", deviceDetailListData.data.deviceList);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_DEVICE_OPEN_DETAIL_LIST, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    public static DeviceDetailListData.Response subAccountDeviceInfo(DeviceDetailListData deviceDetailListData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceList", deviceDetailListData.data.deviceList);
        JsonObject json = HttpSend.execute(paramsMap, CONST.SUB_ACCOUNT_DEVICE_INFO, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    public static DeviceDetailListData.Response listDeviceDetailsByIds(DeviceDetailListData deviceDetailListData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceList", deviceDetailListData.data.deviceList);
        JsonObject json = HttpSend.execute(paramsMap, CONST.LIST_DEVICE_DETAIL_BATCH, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

    public static List<TimeSlice> deviceAlarmPlan(String deviceId, String channelId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        JsonObject json = HttpSend.execute(paramsMap, CONST.DEVICE_ALARM_PLAN, TIME_OUT);
        AlarmPlanQuery.Response response = new AlarmPlanQuery.Response();
        response.parseData(json);
        return response.data.rules;
    }

    public static boolean modifyDeviceAlarmPlan(String deviceId, String channelId, List<TimeSlice> rules) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().accessToken);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        paramsMap.put("rules",rules);
        JsonObject json = HttpSend.execute(paramsMap, CONST.MODIFY_DEVICE_ALARM_PLAN, TIME_OUT);
        ModifyAlarmPlan.Response response = new ModifyAlarmPlan.Response();
        response.parseData(json);
        return true;
    }





    public static boolean addPolicy(AddDevicePolicyData req) throws BusinessException {
        String jsonParam = new Gson().toJson(req);
        HttpSend.execute(jsonParam, CONST.ADD_POLICY, TIME_OUT);
        return true;
    }


    /**
     * IOT interface
     *
     * IOT接口
     */
    public static IotAddDeviceInfo getIotDeviceInfoByHotsPort(String wifiIp) throws BusinessException {
        JsonObject json = HttpSend.executeIot(null, String.format(CONST.IOT.GET_IOTDEVICEINFO_BYHOTSPORT,
                wifiIp), TIME_OUT);
        IotAddDeviceInfo.Response response = new IotAddDeviceInfo.Response();
        response.parseData(json);
        return response.data;
    }

    public static void regserverConf(String wifiIp) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("addr", CONST.IOT.IOT_REG_CLOUD_HOST);
        paramsMap.put("port", CONST.IOT.IOT_REG_CLOUD_PORT);
        HttpSend.executeIot(paramsMap, String.format(CONST.IOT.REGSERVER_CONF, wifiIp), TIME_OUT);
    }

    public static void setIotNetworkInfo(String wifiIp, String SSID,String password,String encryption) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("SSID", SSID);
        paramsMap.put("BSSID", SSID);
        paramsMap.put("encryption", encryption);
        paramsMap.put("password", password);
        HttpSend.executeIot(paramsMap, String.format(CONST.IOT.WIRELESS_WIFI_SET, wifiIp), TIME_OUT);
    }

}
