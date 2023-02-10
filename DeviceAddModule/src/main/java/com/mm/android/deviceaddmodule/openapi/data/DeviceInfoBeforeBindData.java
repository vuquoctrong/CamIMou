package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;

public class DeviceInfoBeforeBindData implements Serializable {
    public DeviceInfoBeforeBindData.RequestData data = new DeviceInfoBeforeBindData.RequestData();

    public static class RequestData implements Serializable {
        public String token;
        public String deviceId;
        public String deviceCodeModel;
        public String deviceModelName;
        public String ncCode;
        public String productId = "";

        @Override
        public String toString() {
            return "RequestData{" +
                    "token='" + token + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", deviceCodeModel='" + deviceCodeModel + '\'' +
                    ", deviceModelName='" + deviceModelName + '\'' +
                    ", ncCode='" + ncCode + '\'' +
                    ", productId='" + productId + '\'' +
                    '}';
        }
    }

    public static class Response {
        public DeviceInfoBeforeBindData.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(),DeviceInfoBeforeBindData.ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
        public String ability;
        public String bindStatus;
        public String catalog;
        public String deviceCodeModel;
        public String deviceExist;
        public String deviceModelName;
        public String status;
        public boolean support;
        public String wifiConfigMode;
        public String defaultWifiConfigMode;
        public boolean wifiConfigModeOptional;
        public String wifiTransferMode;
        public String type;
        public String userAccount;
        public String deviceImageURI;
        public String productId = "";

        @Override
        public String toString() {
            return "ResponseData{" +
                    "ability='" + ability + '\'' +
                    ", bindStatus='" + bindStatus + '\'' +
                    ", catalog='" + catalog + '\'' +
                    ", deviceCodeModel='" + deviceCodeModel + '\'' +
                    ", deviceExist='" + deviceExist + '\'' +
                    ", deviceModelName='" + deviceModelName + '\'' +
                    ", status='" + status + '\'' +
                    ", support=" + support +
                    ", wifiConfigMode='" + wifiConfigMode + '\'' +
                    ", defaultWifiConfigMode='" + defaultWifiConfigMode + '\'' +
                    ", wifiConfigModeOptional='" + wifiConfigModeOptional + '\'' +
                    ", wifiTransferMode='" + wifiTransferMode + '\'' +
                    ", type='" + type + '\'' +
                    ", userAccount='" + userAccount + '\'' +
                    ", deviceImageURI='" + deviceImageURI + '\'' +
                    '}';
        }
    }
}
