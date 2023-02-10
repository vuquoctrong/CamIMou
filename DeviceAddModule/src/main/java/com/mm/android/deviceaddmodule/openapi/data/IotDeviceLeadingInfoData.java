package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

public class IotDeviceLeadingInfoData implements Serializable {
    public IotDeviceLeadingInfoData.RequestData data = new IotDeviceLeadingInfoData.RequestData();

    public static class Response {
        public IotDeviceLeadingInfoData.ResponseData data;
        public String body;

        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), IotDeviceLeadingInfoData.ResponseData.class);
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("code","0");
            jsonObject.addProperty("desc","操作成功");
            jsonObject.add("data",json);
            body=jsonObject.toString();
        }
    }

    public static class ResponseData implements Serializable{


        public List<StepsDTO> steps;

        public static class StepsDTO {
            @Override
            public String toString() {
                return "StepsDTO{" +
                        "elementSeq=" + elementSeq +
                        ", help=" + help +
                        ", stepIcon=" + stepIcon +
                        ", batchSetLanguages=" + batchSetLanguages +
                        ", stepTitle='" + stepTitle + '\'' +
                        ", stepButton=" + stepButton +
                        ", id=" + id +
                        ", stepOperate='" + stepOperate + '\'' +
                        '}';
            }

            public List<String> elementSeq;
            public StepsDTO.HelpDTO help;
            public List<String> stepIcon;
            public List<?> batchSetLanguages;
            public String stepTitle;
            public List<String> stepButton;
            public Integer id;
            public String stepOperate;

            public static class HelpDTO {
                public String templateType;
                public String helpUrlTitle;
                public String helpTitle;
                public String helpUrl;
                public String helpContent;
                public List<String> helpIcon;
            }
        }
    }

    public static class RequestData implements Serializable{
        public String token;
        public String productId = "";
        public String language;
        public String communicate;

        @Override
        public String toString() {
            return "RequestData{" +
                    "token='" + token + '\'' +
                    ", productId='" + productId + '\'' +
                    '}';
        }
    }
}

