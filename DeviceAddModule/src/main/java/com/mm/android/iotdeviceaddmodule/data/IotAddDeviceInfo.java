package com.mm.android.iotdeviceaddmodule.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class IotAddDeviceInfo {

    public static class Response {
        public IotAddDeviceInfo data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), IotAddDeviceInfo.class);
        }
    }

    public String pid;
    public String sn;
    public String token;
    public String serviceversion;
}
