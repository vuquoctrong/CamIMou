package com.lc.playback.api;

import com.google.gson.JsonObject;
import com.lc.playback.contacts.MethodConst;
import com.lc.playback.entity.CloudRecordsData;
import com.lc.playback.entity.DeviceDetailListData;
import com.lc.playback.entity.DeviceVersionListData;
import com.lc.playback.entity.LocalRecordsData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.openapi.TokenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordInfoOpenApiManager {

    private static int TIME_OUT = 10 * 1000;
    private static int DMS_TIME_OUT = 45 * 1000;





    /**
     * Obtain the device version and upgradable information
     * @param deviceVersionListData  Device version list data
     * @return DeviceVersionListData.Response  Device version list response data
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
     * Obtain detailed information about a single device channel
     * @param deviceChannelInfoData  Device channel information data
     * @return DeviceChannelInfoData.Response  Device channel information response data
     * @throws BusinessException
     *
     * 单个设备通道的详细信息获取
     * @param deviceChannelInfoData  设备通道信息数据
     * @return DeviceChannelInfoData.Response  设备通道信息响应数据
     * @throws BusinessException
     */


    /**
     * Query device cloud video clips in reverse order
     * @param cloudRecordsData  Cloud record data
     * @return CloudRecordsData.Response  Cloud record response data
     * @throws BusinessException
     *
     * 倒序查询设备云录像片段
     * @param cloudRecordsData  云录像数据
     * @return CloudRecordsData.Response  云录像响应数据
     * @throws BusinessException
     */
    public static CloudRecordsData.Response getCloudRecords(CloudRecordsData cloudRecordsData) throws BusinessException {
        // 倒序查询设备云录像片段
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", cloudRecordsData.data.deviceId);
        paramsMap.put("channelId", cloudRecordsData.data.channelId);
        paramsMap.put("beginTime", cloudRecordsData.data.beginTime);
        paramsMap.put("endTime", cloudRecordsData.data.endTime);
        paramsMap.put("nextRecordId", cloudRecordsData.data.nextRecordId);
        paramsMap.put("count", cloudRecordsData.data.count);
        paramsMap.put("productId", cloudRecordsData.data.productId);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_GET_CLOUND_RECORDS, TIME_OUT);
        CloudRecordsData.Response response = new CloudRecordsData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Querying device device video clips
     * @param localRecordsData  Local record data
     * @return LocalRecordsData.Response  Local record response data
     * @throws BusinessException
     *
     * 查询设备设备录像片段
     * @param localRecordsData  本地录像数据
     * @return LocalRecordsData.Response  本地录像响应数据
     * @throws BusinessException
     */
    public static LocalRecordsData.Response queryLocalRecords(LocalRecordsData localRecordsData) throws BusinessException {
        // 查询设备设备录像片段
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("deviceId", localRecordsData.data.deviceId);
        paramsMap.put("channelId", localRecordsData.data.channelId);
        paramsMap.put("beginTime", localRecordsData.data.beginTime);
        paramsMap.put("endTime", localRecordsData.data.endTime);
        paramsMap.put("type", localRecordsData.data.type);
        paramsMap.put("queryRange", localRecordsData.data.queryRange);
        paramsMap.put("count",localRecordsData.data.count);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.METHOD_QUERY_LOCAL_RECORD, DMS_TIME_OUT);
        LocalRecordsData.Response response = new LocalRecordsData.Response();
        response.parseData(json);
        return response;
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


    /**
     * Delete the device cloud video clip
     * @param recordRegionId  Record segment Id
     * @return boolean  Boolean value
     * @throws BusinessException
     *
     * 删除设备云录像片段
     * @param recordRegionId  录像片段Id
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public static boolean deleteCloudRecords(String recordRegionId, String productId) throws BusinessException {
        // 删除设备云录像片段
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("recordRegionId", recordRegionId);
        paramsMap.put("productId", productId);
        HttpSend.execute(paramsMap, MethodConst.METHOD_DELETE_CLOUND_RECORDS, TIME_OUT);
        return true;
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

}
