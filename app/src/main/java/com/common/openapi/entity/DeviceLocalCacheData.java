package com.common.openapi.entity;

import java.io.Serializable;

public class DeviceLocalCacheData implements Serializable {
    private long cacheId;
    private String deviceId;
    private String deviceName;
    private String channelId;
    private String channelName;
    private String picPath;
    private long creationTime;
    private long modifyTime;

    public long getCacheId() {
        return cacheId;
    }

    public void setCacheId(long cacheId) {
        this.cacheId = cacheId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
}
