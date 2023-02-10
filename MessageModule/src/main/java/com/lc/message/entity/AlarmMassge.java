package com.lc.message.entity;


import com.mm.android.mobilecommon.entity.DataInfo;

/**
 * @author 32752
 * @version Version    Time                 Description<br>
 * 1.0        2017/7/7 9:59      Create<br>
 */

public class AlarmMassge extends DataInfo {

    private String token; // 消息唯一标识，用于查询云录像
    private long time; // 报警时间UNIX时间戳（单位：秒）
    private String channelId;
    private String name; // 通道名称
    private String alarmId; // 消息Id
    private String localDate; // 报警时设备本地时间
    private int type;// 报警类型，0：人体红外；1：动态检测；2：未知告警；3：低电压告警；4：配件人体红外检测；5：移动感应器发生移动事件；6：移动感应器长时间未发生移动事件；7：配件人体红外检测长久未报警事件；8：门磁报警事件；90：报警网关门磁探测报警；91：报警网关红外探测报警；92：报警网关低电量报警；93：报警网关防拆报警
    private String deviceId;
    private String[] picurlArray; // 报警图片url
    private String thumbUrl; // 缩略图Url
    private String msgType;

    public AlarmMassge(String token, long time, String channelId, String name, String alarmId, String localDate, int type, String deviceId, String[] picurlArray, String thumbUrl, String msgType) {
        this.token = token;
        this.time = time;
        this.channelId = channelId;
        this.name = name;
        this.alarmId = alarmId;
        this.localDate = localDate;
        this.type = type;
        this.deviceId = deviceId;
        this.picurlArray = picurlArray;
        this.thumbUrl = thumbUrl;
        this.msgType = msgType;
    }

    @Override
    public AlarmMassge clone() {
        AlarmMassge info = null;
        try {
            info = (AlarmMassge) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return info;
    }

    public String getToken() {
        return token;
    }

    public long getTime() {
        return time;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public String getLocalDate() {
        return localDate;
    }

    public int getType() {
        return type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String[] getPicurlArray() {
        return picurlArray;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getMsgType() {
        return msgType;
    }
}
