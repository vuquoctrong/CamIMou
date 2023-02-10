package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceLeadingInfoData implements Serializable {
    public DeviceLeadingInfoData.RequestData data = new DeviceLeadingInfoData.RequestData();

    public static class Response {
        public DeviceLeadingInfoData.ResponseData data;
        public String body;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), DeviceLeadingInfoData.ResponseData.class);
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("code","0");
            jsonObject.addProperty("desc","操作成功");
            jsonObject.add("data",json);
            body=jsonObject.toString();
        }
    }

    public static class ResponseData implements Serializable{
        public List<DeviceLeadingInfoData.ResponseData.IntroductionsElement> introductions = new ArrayList();
        public List<DeviceLeadingInfoData.ResponseData.ImagesElement> images = new ArrayList();
        public String updateTime;

        public static class ImagesElement implements Serializable{
            public String imageName;
            public String imageUrl;

            @Override
            public String toString() {
                return "ImagesElement{" +
                        "imageName='" + imageName + '\'' +
                        ", imageUrl='" + imageUrl + '\'' +
                        '}';
            }
        }

        public static class IntroductionsElement implements Serializable{
            public String introductionContent;
            public String introductionName;

            @Override
            public String toString() {
                return "IntroductionsElement{" +
                        "introductionContent='" + introductionContent + '\'' +
                        ", introductionName='" + introductionName + '\'' +
                        '}';
            }
        }
    }

    public static class RequestData implements Serializable{
        public String token;
        public String deviceModelName;
        public String productId = "";
        public String language;

        @Override
        public String toString() {
            return "RequestData{" +
                    "token='" + token + '\'' +
                    ", deviceModelName='" + deviceModelName + '\'' +
                    '}';
        }
    }
}
