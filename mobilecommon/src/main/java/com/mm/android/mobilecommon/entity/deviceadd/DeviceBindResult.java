package com.mm.android.mobilecommon.entity.deviceadd;

/**
 * Device binding result information class
 *
 * 设备绑定结果信息类
 */
public class DeviceBindResult {
    String deviceName;      //绑定成功返回设备默认名称
    String bindStatus;      //绑定状态，bindByMe，bindByOther
    String userAccount;     //绑定帐号

    //TODO SMB
    //*********************接口不返回，要处理下默认逻辑*************************//
    String recordSaveDays;	 // 录像保存天数（免费套餐信息,设备有可赠送免费套餐时返回）
    String streamType;	    // 码流类型：main：主码流extra1：辅码流（免费套餐信息,设备有可赠送免费套餐时返回）
    String serviceTime;	    //服务时长(秒)（免费套餐信息,设备有可赠送免费套餐时返回） 上层使用的时候，展示的是天
    //*********************接口不返回，要处理下默认逻辑*************************//

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(String bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getRecordSaveDays() {
        return recordSaveDays;
    }

    public void setRecordSaveDays(String recordSaveDays) {
        this.recordSaveDays = recordSaveDays;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }
}
