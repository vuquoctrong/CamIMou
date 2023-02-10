package com.lc.media.api;

import java.io.Serializable;

public class ControlMovePTZData implements Serializable {
    public RequestData data = new RequestData();

    public static class RequestData implements Serializable {
        public String token;
        public String deviceId;
        public String channelId = "0";
       //0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-停止
        public String operation;
        public long duration;
    }
}
