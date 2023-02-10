package com.mm.android.mobilecommon.entity.deviceadd;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The device adds the boot information store class
 *
 * 设备添加引导信息存储类
 **/
public class DeviceIntroductionInfo implements Serializable {
    String updateTime;                              //更新时间
    String language;                                //存储语言
    String deviceModel;                            //设备类型
    HashMap<String,String> imageInfos;         //图片信息
    HashMap<String,String> strInfos;             //文案信息

    public HashMap<String,String> getImageInfos() {
        return imageInfos;
    }

    public void setImageInfos(HashMap<String,String> imageInfos) {
        this.imageInfos = imageInfos;
    }

    public HashMap<String,String> getStrInfos() {
        return strInfos;
    }

    public void setStrInfos(HashMap<String,String> strInfos) {
        this.strInfos = strInfos;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}
