package com.mm.android.deviceaddmodule.device_wifi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;

/**
 * Indicates the current Wifi status
 *
 * 当前连接Wifi状态信息
 */
public class CurWifiInfo implements Serializable {
    public static class Response {
        public CurWifiInfo data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), CurWifiInfo.class);
        }
    }

    //当前有无连接热点
    private boolean linkEnable;
    //若连接了热点，填热点的名称；若未连接，填空
    private String ssid;
    //强度, 0最弱，5最强
    private int intensity;
    //强度，单位为dbm
    private String sigStrength;
    //WIFI认证模式
    private String auth;

    public boolean isLinkEnable() {
        return linkEnable;
    }

    public void setLinkEnable(boolean linkEnable) {
        this.linkEnable = linkEnable;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getSigStrength() {
        return sigStrength;
    }

    public void setSigStrength(String sigStrength) {
        this.sigStrength = sigStrength;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
