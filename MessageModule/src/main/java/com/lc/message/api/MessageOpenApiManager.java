package com.lc.message.api;

import com.google.gson.JsonObject;
import com.lc.message.api.data.AlarmMessageData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.CONST;
import com.mm.android.mobilecommon.openapi.HttpSend;

import java.util.HashMap;

public class MessageOpenApiManager {

    private static int TIME_OUT = 10 * 1000;
    private static int DMS_TIME_OUT = 45 * 1000;

    /**
     * Message query
     * @param alarmMessageData  Alarm message Data
     * @return response  Alarm message response data
     * @throws BusinessException
     *
     * 消息查询
     * @param alarmMessageData  告警消息数据
     * @return response  告警消息响应数据
     * @throws BusinessException
     */
    public static AlarmMessageData.Response getAlarmMessage(AlarmMessageData alarmMessageData) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", alarmMessageData.data.token);
        paramsMap.put("deviceId", alarmMessageData.data.deviceId);
        paramsMap.put("channelId", alarmMessageData.data.channelId);
        paramsMap.put("beginTime", alarmMessageData.data.beginTime);
        paramsMap.put("endTime", alarmMessageData.data.endTime);
        paramsMap.put("count", alarmMessageData.data.count);
        paramsMap.put("nextAlarmId", alarmMessageData.data.nextAlarmId);
        JsonObject json = HttpSend.execute(paramsMap, CONST.METHOD_MESSAGE, DMS_TIME_OUT);
        AlarmMessageData.Response response = new AlarmMessageData.Response();
        response.parseData(json);
        return response;
    }

    /**
     * Deletes the specified message
     * @param token
     * @param indexIds  Delete data in batches using commas
     * @param deviceId  Device Id
     * @param channelId  Channel Id
     * @return boolean  Boolean value
     * @throws BusinessException
     *
     * 删除指定的消息
     * @param token
     * @param indexIds  批量删除用逗号隔开
     * @param deviceId  设备Id
     * @param channelId  通道Id
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public static Boolean deleteAlarmMessage(String token, String indexIds, String deviceId, String channelId) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", token);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        paramsMap.put("indexId", indexIds);
        HttpSend.execute(paramsMap, CONST.METHOD_DELETE_ALARM_MESSAGE, DMS_TIME_OUT);
        return true;
    }

}
