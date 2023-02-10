package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;

public class BindDeviceData implements Serializable {
    public BindDeviceData.RequestData data = new BindDeviceData.RequestData();

    public static class ResponseData implements Serializable{
        public String deviceName;
        public String userAccount;
        public String bindStatus;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "deviceName='" + deviceName + '\'' +
                    ", userAccount='" + userAccount + '\'' +
                    ", bindStatus='" + bindStatus + '\'' +
                    '}';
        }
    }

    public static class Response {
        public BindDeviceData.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), BindDeviceData.ResponseData.class);
        }
    }

    public static class RequestData implements Serializable{
        public String code;
        public String token;
        public String deviceId;

        @Override
        public String toString() {
            return "RequestData{" +
                    "code='" + code + '\'' +
                    ", token='" + token + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    '}';
        }
    }
}
