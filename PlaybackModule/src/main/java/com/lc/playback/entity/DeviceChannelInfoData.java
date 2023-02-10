package com.lc.playback.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class DeviceChannelInfoData implements Serializable {
    public RequestData data = new RequestData();

    public static class Response {
        public ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(),  ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
        public String deviceId;
        public int alarmStatus;
        public int channelId;
        public String channelName;
        public String channelOnline;
        public String channelPicUrl;
        public String shareStatus;
        public String shareFunctions;
        public String csStatus;
        public String channelAbility;
    }

    public static class RequestData implements Serializable {
        public String token;
        public String deviceId;
        public String channelId="0";
    }

}
