package com.lc.playback.entity;

import java.io.Serializable;
import java.util.List;

public class DeleteCloudRecordsData implements Serializable {
    public RequestData data = new RequestData();

    public static class RequestData implements Serializable {
        public String token;
        public String productId = "";
        public List<String> recordRegionIds;

    }
}
