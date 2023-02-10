package com.lc.message.api.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lc.message.entity.AlarmMassge;

import java.io.Serializable;
import java.util.List;

public class AlarmMessageData implements Serializable {
    public RequestData data = new RequestData();

    public static class ResponseData implements Serializable{
        public int count;
        public long  nextAlarmId;
        public String bindStatus;
        public List<AlarmMassge> alarms;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "count=" + count +
                    ", nextAlarmId=" + nextAlarmId +
                    ", bindStatus='" + bindStatus + '\'' +
                    ", alarms=" + alarms +
                    '}';
        }
    }

    public static class Response {
        public ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), ResponseData.class);
        }
    }

    public static class RequestData implements Serializable{
        public String channelId;
        public String token;
        public String deviceId;
        public String beginTime;
        public String endTime;
        public int count;
        public String nextAlarmId;

        @Override
        public String toString() {
            return "RequestData{" +
                    "channelId='" + channelId + '\'' +
                    ", token='" + token + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", beginTime='" + beginTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", count=" + count +
                    ", nextAlarmId='" + nextAlarmId + '\'' +
                    '}';
        }
    }
}
