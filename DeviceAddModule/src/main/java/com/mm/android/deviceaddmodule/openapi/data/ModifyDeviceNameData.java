package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;

public class ModifyDeviceNameData implements Serializable {
    public ModifyDeviceNameData.RequestData data = new ModifyDeviceNameData.RequestData();

    public static class Response {
        public ModifyDeviceNameData.ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), ModifyDeviceNameData.ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
    }

    public static class RequestData implements Serializable {
        public String deviceId;
        public String channelId;
        public String name;
        public String token;

        @Override
        public String toString() {
            return "RequestData{" +
                    "deviceId='" + deviceId + '\'' +
                    ", channelId='" + channelId + '\'' +
                    ", name='" + name + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }
}
