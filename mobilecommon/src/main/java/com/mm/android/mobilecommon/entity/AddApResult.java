package com.mm.android.mobilecommon.entity;

public class AddApResult extends DataInfo{
    private boolean isExist;   //配件是否存在（配件是否上报了硬件信息）
    private String apName;   //配件名称
    private String apType;   //配件的类型
    private String apModel;   //配件的型号
    private String apVersion;   //配件的版本号
    private int apStatus;   //配件的在线状态：1-在线 0-离线
    private String apEnable;   //配件的使能:on-使能开启 ，off-使能关闭
    private int ioType;   //配件输入输出类型，-1:未知 0-输入 1-输出

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public String getApType() {
        return apType;
    }

    public void setApType(String apType) {
        this.apType = apType;
    }

    public String getApModel() {
        return apModel;
    }

    public void setApModel(String apModel) {
        this.apModel = apModel;
    }

    public String getApVersion() {
        return apVersion;
    }

    public void setApVersion(String apVersion) {
        this.apVersion = apVersion;
    }

    public int getApStatus() {
        return apStatus;
    }

    public void setApStatus(int apStatus) {
        this.apStatus = apStatus;
    }

    public String getApEnable() {
        return apEnable;
    }

    public void setApEnable(String apEnable) {
        this.apEnable = apEnable;
    }

    public int getIoType() {
        return ioType;
    }

    public void setIoType(int ioType) {
        this.ioType = ioType;
    }
}
