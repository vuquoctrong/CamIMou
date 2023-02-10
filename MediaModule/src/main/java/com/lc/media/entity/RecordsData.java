package com.lc.media.entity;

import com.mm.android.mobilecommon.utils.TimeUtils;

import java.io.Serializable;

public class RecordsData implements Serializable {
    //0 云录像 1设备录像
    public int recordType;
    //通用字段
    public String recordId;
    public String beginTime;
    public String endTime;
    public String type;
    public boolean check;
    //设备录像
    public long fileLength;
    public String channelID;
    //云录像
    public String deviceId;
    public String channelId;
    public String size;
    public String thumbUrl;
    public int encryptMode;
    public String recordRegionId;
    public String productId = "";

    public static RecordsData parseCloudData(CloudRecordsData.ResponseData.RecordsBean recordsBean){
        RecordsData recordsData=new RecordsData();
        recordsData.recordType=0;
        recordsData.recordId=recordsBean.recordId;
        recordsData.beginTime=recordsBean.beginTime;
        recordsData.endTime=recordsBean.endTime;
        recordsData.type=recordsBean.type;
        recordsData.deviceId=recordsBean.deviceId;
        recordsData.channelId=recordsBean.channelId;
        recordsData.size=recordsBean.size;
        recordsData.thumbUrl=recordsBean.thumbUrl;
        recordsData.encryptMode=recordsBean.encryptMode;
        recordsData.recordRegionId=recordsBean.recordRegionId;
        recordsData.productId=recordsBean.productId;
        return recordsData;
    }

    public static RecordsData parseLocalData(LocalRecordsData.ResponseData.RecordsBean recordsBean, String deviceId){
        RecordsData recordsData=new RecordsData();
        recordsData.recordType=1;
        recordsData.recordId=recordsBean.recordId;
        recordsData.beginTime=recordsBean.beginTime;
        recordsData.endTime=recordsBean.endTime;
        recordsData.type=recordsBean.type;
        recordsData.size = recordsBean.fileLength + "";
        recordsData.fileLength=recordsBean.fileLength;
        recordsData.channelID=recordsBean.channelID;
        recordsData.productId=recordsBean.productId;
        recordsData.deviceId = deviceId;
        return recordsData;
    }
}
