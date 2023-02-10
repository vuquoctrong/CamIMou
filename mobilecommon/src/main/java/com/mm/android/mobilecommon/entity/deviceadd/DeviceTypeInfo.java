package com.mm.android.mobilecommon.entity.deviceadd;

import com.mm.android.mobilecommon.entity.DataInfo;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * A device type information class. A category contains multiple small classes
 *
 * 设备类型信息类，大类包含多个小类
 **/
public class DeviceTypeInfo extends DataInfo {
    String updateTime;
    String language;
    LinkedHashMap<String,List<DeviceModelInfo>> modelMap;
    //设备详细类型数据结构
    public static class DeviceModelInfo extends DataInfo{
        String deviceType;          //分类名称，摄像机Camera, 门锁DoorBell, 报警器AlarmDevice, 网关Gateway, 硬盘录像机DVR
        String deviceModelName;     //设备型号名称,APP展示使用
        String deviceImageURI;      //设备正视图（图片尺寸参考UI）的URI

        public String getDeviceModelName() {
            return deviceModelName;
        }

        public void setDeviceModelName(String deviceModelName) {
            this.deviceModelName = deviceModelName;
        }

        public String getDeviceImageURI() {
            return deviceImageURI;
        }

        public void setDeviceImageURI(String deviceImageURI) {
            this.deviceImageURI = deviceImageURI;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public LinkedHashMap<String, List<DeviceModelInfo>> getModelMap() {
        return modelMap;
    }

    public void setModelMap(LinkedHashMap<String, List<DeviceModelInfo>> modelMap) {
        this.modelMap = modelMap;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
