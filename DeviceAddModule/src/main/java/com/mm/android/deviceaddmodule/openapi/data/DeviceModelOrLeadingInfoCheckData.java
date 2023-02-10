package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;

public class DeviceModelOrLeadingInfoCheckData implements Serializable {
    public DeviceModelOrLeadingInfoCheckData.RequestData data = new DeviceModelOrLeadingInfoCheckData.RequestData();

    public static class Response {
        public DeviceModelOrLeadingInfoCheckData.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), DeviceModelOrLeadingInfoCheckData.ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
        public boolean isUpdated;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "isUpdated=" + isUpdated +
                    '}';
        }
    }

    public static class RequestData implements Serializable {
        public String token;
        public String deviceModelName;
        public String updateTime;

        @Override
        public String toString() {
            return "RequestData{" +
                    "token='" + token + '\'' +
                    ", deviceModelName='" + deviceModelName + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    '}';
        }
    }
}
