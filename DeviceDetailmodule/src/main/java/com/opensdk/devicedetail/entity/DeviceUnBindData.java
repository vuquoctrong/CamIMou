package com.opensdk.devicedetail.entity;

import java.io.Serializable;

public class DeviceUnBindData implements Serializable {
    public RequestData data = new RequestData();

    public static class RequestData implements Serializable {
        public String token;
        public String deviceId;

    }
}
