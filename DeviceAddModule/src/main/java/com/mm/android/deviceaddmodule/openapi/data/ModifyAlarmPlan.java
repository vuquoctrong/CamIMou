package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mm.android.mobilecommon.openapi.data.TimeSlice;

import java.io.Serializable;
import java.util.List;

public class ModifyAlarmPlan implements Serializable {
    public ModifyAlarmPlan.RequestData data = new ModifyAlarmPlan.RequestData();

    public static class ResponseData implements Serializable {
    }

    public static class Response {
        public ModifyAlarmPlan.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), ModifyAlarmPlan.ResponseData.class);
        }
    }

    public static class RequestData implements Serializable {
        public String deviceId;
        public String token;
        public String channelId;
        public List<TimeSlice> rules;
    }
}

