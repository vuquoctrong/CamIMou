package com.mm.android.deviceaddmodule.device_wifi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Wifi Configuration Information
 *
 * Wifi配置信息
 */
public class WifiConfig implements Serializable {
    public static class Response {
        public WifiConfig data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), WifiConfig.class);
        }
    }

    //设备是否开启WIFI。true:开启，false:关闭
    private boolean enable;
    //设备搜索到的wifi列表
    private List<WifiInfo> wLan;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<WifiInfo> getwLan() {
        return wLan;
    }

    public void setwLan(List<WifiInfo> wLan) {
        this.wLan = wLan;
    }
}
