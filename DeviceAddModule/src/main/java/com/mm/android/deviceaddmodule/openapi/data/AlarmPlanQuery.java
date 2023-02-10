package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mm.android.mobilecommon.openapi.data.TimeSlice;

import java.io.Serializable;
import java.util.List;

public class AlarmPlanQuery implements Serializable {
    public AlarmPlanQuery.RequestData data = new AlarmPlanQuery.RequestData();

    public static class ResponseData implements Serializable {
        public String channelId;
        public List<TimeSlice> rules;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "channelId='" + channelId + '\'' +
                    ", rules=" + rules +
                    '}';
        }
    }

    public static class Response {
        public AlarmPlanQuery.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), AlarmPlanQuery.ResponseData.class);
        }
    }

    public static class RequestData implements Serializable {
        public String deviceId;
        public String token;
        public String channelId;

        @Override
        public String toString() {
            return "RequestData{" +
                    "deviceId='" + deviceId + '\'' +
                    ", token='" + token + '\'' +
                    ", channelId='" + channelId + '\'' +
                    '}';
        }
    }
}

