package com.opensdk.devicedetail.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetailListData implements Serializable {
    public RequestData data = new RequestData();

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
            public String channelList;
            public String apList;

            @Override
            public String toString() {
                return "DeviceListBean{" +
                        "deviceId='" + deviceId + '\'' +
                        ", channelList='" + channelList + '\'' +
                        ", apList='" + apList + '\'' +
                        '}';
            }
        }
    }

    public static class Response {
        public ResponseData data;
        public long baseBindId=-1;
        public long openBindId=-1;
        public void parseData(JsonObject json) {
            Gson gson = new Gson();
            this.data = gson.fromJson(json.toString(),  ResponseData.class);
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
            public String deviceStatus;
            public String deviceModel;
            public String catalog;
            public String brand;
            public String deviceVersion;
            public String deviceName;
            public String deviceAbility;
            public String accessType;
            public int checkedChannel;
            public String playToken;
            public String channelNum;
            public String permission;
            //1：开放平台添加  2：乐橙App添加
            public int deviceSource;
            public List<ChannelsBean> channelList=new ArrayList<>();
            public List<AplistBean> aplist;
            public String productId = "";

            public String getDeviceStatus() {
                return deviceStatus;
            }

            public String getAbility() {
                return deviceAbility;
            }

            public void setAbility(String deviceAbility) {
                this.deviceAbility = deviceAbility;
            }

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }

            @Override
            public String toString() {
                return "DeviceListBean{" +
                        "deviceId='" + deviceId + '\'' +
                        ", status='" + deviceStatus + '\'' +
                        ", deviceModel='" + deviceModel + '\'' +
                        ", catalog='" + catalog + '\'' +
                        ", brand='" + brand + '\'' +
                        ", version='" + deviceVersion + '\'' +
                        ", name='" + deviceName + '\'' +
                        ", ability='" + deviceAbility + '\'' +
                        ", accessType='" + accessType + '\'' +
                        ", checkedChannel=" + checkedChannel +
                        ", channelNum=" + channelNum +
                        ", playToken='" + playToken + '\'' +
                        ", deviceSource=" + deviceSource +
                        ", channels=" + channelList +
                        ", aplist=" + aplist +
                        '}';
            }

            public static class ChannelsBean implements Serializable {
                public String channelId;
                public String deviceId;
                public String channelName;
                public String channelAbility;
                public String channelStatus;
                public String channelPicUrl;
                public String remindStatus;
                public String cameraStatus;
                public String storageStrategyStatus;
                public String shareStatus;
                public String shareFunctions;
                public String permission;
                public List<ResolutionBean>  resolutions = new ArrayList<>();

                public String getAbility() {
                    return channelAbility;
                }

                public void setAbility(String channelAbility) {
                    this.channelAbility = channelAbility;
                }

                @Override
                public String toString() {
                    return "ChannelsBean{" +
                            "channelId='" + channelId + '\'' +
                            ", deviceId='" + deviceId + '\'' +
                            ", channelName='" + channelName + '\'' +
                            ", ability='" + channelAbility + '\'' +
                            ", channelStatus='" + channelStatus + '\'' +
                            ", picUrl='" + channelPicUrl + '\'' +
                            ", remindStatus='" + remindStatus + '\'' +
                            ", cameraStatus='" + cameraStatus + '\'' +
                            ", storageStrategyStatus='" + storageStrategyStatus + '\'' +
                            ", shareStatus='" + shareStatus + '\'' +
                            ", shareFunctions='" + shareFunctions + '\'' +
                            ", permission='" + permission + '\'' +
                            ", resolutions=" + resolutions +
                            '}';
                }

                public static class ResolutionBean implements Serializable{
                    public String name;
                    public int imageSize;
                    public int streamType;

                    public ResolutionBean(String name, int imageSize, int streamType) {
                        this.name = name;
                        this.imageSize = imageSize;
                        this.streamType = streamType;
                    }

                    @Override
                    public String toString() {
                        return "ResolutionBean{" +
                                "name='" + name + '\'' +
                                ", imageSize=" + imageSize +
                                ", streamType=" + streamType +
                                '}';
                    }
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
