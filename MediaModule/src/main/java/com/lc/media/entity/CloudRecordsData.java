package com.lc.media.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

public class CloudRecordsData implements Serializable {
    public RequestData data = new RequestData();

    public static class Response {
        public ResponseData data;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(),  ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
        public List<RecordsBean> records;

        public static class RecordsBean implements Serializable {
            public String recordId;
            public String deviceId;
            public String channelId;
            public String beginTime;
            public String endTime;
            public String size;
            public String thumbUrl;
            public int encryptMode;
            public String recordRegionId;
            public String type;
            public String productId = "";
        }
    }

    public static class RequestData implements Serializable {
        public String token;
        public String deviceId;
        public String channelId = "0";
        public String beginTime;
        public String endTime;
        public long nextRecordId=-1;
        public long count;
        public String queryRange;
        public Object productId;
    }
}
