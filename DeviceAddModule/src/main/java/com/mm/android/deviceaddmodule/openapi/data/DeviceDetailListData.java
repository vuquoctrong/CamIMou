package com.mm.android.deviceaddmodule.openapi.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

public class DeviceDetailListData implements Serializable {
    public DeviceDetailListData.RequestData data = new DeviceDetailListData.RequestData();

    public static class RequestData implements Serializable {
        public String token;
        public List<DeviceListBean> deviceList;

        @Override
        public String toString() {
            return "RequestData{" +
                    "token='" + token + '\'' +
                    ", deviceList=" + deviceList +
                    '}';
        }

        public static class DeviceListBean implements Serializable {
            public String deviceId;
            public String productId = "";

            @Override
            public String toString() {
                return "DeviceListBean{" +
                        "deviceId='" + deviceId + '\'' +
                        ", channelList='" + productId + '\'' +
                        '}';
            }
        }
    }

    public static class Response {
        public DeviceDetailListData.ResponseData data;
        public long baseBindId=-1;
        public long openBindId=-1;
        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(), DeviceDetailListData.ResponseData.class);
        }
    }

    public static class ResponseData implements Serializable {
        public int count;

        public List<DeviceListBean> deviceList;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "count=" + count +
                    ", deviceList=" + deviceList +
                    '}';
        }

        public static class DeviceListBean implements Serializable {
            public String deviceId;
            public String status;
            public String deviceModel;
            public String catalog;
            public String brand;
            public String version;
            public String deviceName;
            public String ability;
            public String accessType;
            public int checkedChannel;
            public String permission;
            public String playToken;
            //1：开放平台添加  2：乐橙App添加
            public int deviceSource;
            public List<ChannelsBean> channels;
            public List<AplistBean> aplist;

            @Override
            public String toString() {
                return "DeviceListBean{" +
                        "deviceId='" + deviceId + '\'' +
                        ", status='" + status + '\'' +
                        ", deviceModel='" + deviceModel + '\'' +
                        ", catalog='" + catalog + '\'' +
                        ", brand='" + brand + '\'' +
                        ", version='" + version + '\'' +
                        ", name='" + deviceName + '\'' +
                        ", ability='" + ability + '\'' +
                        ", accessType='" + accessType + '\'' +
                        ", checkedChannel=" + checkedChannel +
                        ", playToken='" + playToken + '\'' +
                        ", deviceSource=" + deviceSource +
                        ", channels=" + channels +
                        ", aplist=" + aplist +
                        '}';
            }

            public static class ChannelsBean implements Serializable {
                public String channelId;
                public String deviceId;
                public String channelName;
                public String ability;
                public String status;
                public String picUrl;
                public String remindStatus;
                public String cameraStatus;
                public String storageStrategyStatus;
                public String shareStatus;
                public String shareFunctions;

                @Override
                public String toString() {
                    return "ChannelsBean{" +
                            "channelId='" + channelId + '\'' +
                            ", deviceId='" + deviceId + '\'' +
                            ", channelName='" + channelName + '\'' +
                            ", ability='" + ability + '\'' +
                            ", status='" + status + '\'' +
                            ", picUrl='" + picUrl + '\'' +
                            ", remindStatus='" + remindStatus + '\'' +
                            ", cameraStatus='" + cameraStatus + '\'' +
                            ", storageStrategyStatus='" + storageStrategyStatus + '\'' +
                            ", shareStatus='" + shareStatus + '\'' +
                            ", shareFunctions='" + shareFunctions + '\'' +
                            '}';
                }
            }

            public static class AplistBean implements Serializable {
                public String apId;
                public String apName;
                public String apType;
                public String apModel;
                public String ioType;
                public String apVersion;
                public String apStatus;
                public String apEnable;
                public String apCapacity;

                @Override
                public String toString() {
                    return "AplistBean{" +
                            "apId='" + apId + '\'' +
                            ", apName='" + apName + '\'' +
                            ", apType='" + apType + '\'' +
                            ", apModel='" + apModel + '\'' +
                            ", ioType='" + ioType + '\'' +
                            ", apVersion='" + apVersion + '\'' +
                            ", apStatus='" + apStatus + '\'' +
                            ", apEnable='" + apEnable + '\'' +
                            ", apCapacity='" + apCapacity + '\'' +
                            '}';
                }
            }
        }
    }

}
