package com.mm.android.deviceaddmodule.device_wifi;

;

import androidx.annotation.StringDef;

import java.io.Serializable;
import java.lang.annotation.Retention;

import static com.mm.android.deviceaddmodule.device_wifi.WifiInfo.Status.connected;
import static com.mm.android.deviceaddmodule.device_wifi.WifiInfo.Status.connecting;
import static com.mm.android.deviceaddmodule.device_wifi.WifiInfo.Status.unconnected;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Wifi status Information
 *
 * Wifi状态信息
 */
public class WifiInfo implements Serializable {
    //该WIFI的唯一标示符，通常是一个MAC地址
    private String bssid ;
    //若连接了热点，填热点的名称；若未连接，填空
    private String ssid;
    //连接状态。unconnected：未连接；connecting：连接中；connected：已连接
    private String linkStatus;
    //强度, 0最弱，5最强
    private int intensity;
    //WIFI认证模式：OPEN，WEP，WPA/WPA2 PSK，WPA/WPA2
    private String auth;

    @Retention(SOURCE)
    @StringDef({unconnected, connecting, connected,""})
    public @interface Status {
        String unconnected = "0";     //未连接；
        String connecting = "1";       //连接中；
        String connected = "2";         //已连接
    }

    public String getBSSID() {
        return bssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
