package com.mm.android.deviceaddmodule.entity;


import com.mm.android.mobilecommon.entity.DataInfo;

/**
 * @author 32752
 * @version Version    Time                 Description<br>
 * 1.0        2017/7/7 9:59      Create<br>
 */

public class TimeSlice extends DataInfo {

    public boolean enable;
    private String period;
    private String beginTime;
    private String endTime;

    public TimeSlice(String period, String beginTime, String endTime) {
        this.period = period;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimePeriod() {
        return this.beginTime + " - " + this.endTime;
    }

    @Override
    public TimeSlice clone() {
        TimeSlice info = null;
        try {
            info = (TimeSlice) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return info;
    }
}
