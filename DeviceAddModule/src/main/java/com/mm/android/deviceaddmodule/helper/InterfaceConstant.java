package com.mm.android.deviceaddmodule.helper;

/**
 * Created by zhengcong on 2018/9/11.
 * A constant defined in the service interface
 *
 * Created by zhengcong on 2018/9/11.
 * 服务接口中定义的常量
 */
public class InterfaceConstant {

    public enum GearType {
        bright,//亮度
        fls;//补光灯灵敏度
    }

    public enum Period {
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    /**
     * Voice Type of broadcast voice
     *
     * 语音播报声音类型
     */
    public enum AlarmSoundType {
        alert,
        notice,
        mute
    }

    /**
     * Device wifi operation type
     *
     * 设备wifi操作类型
     */
    public enum DeviceWifiLink {
        connect,
        disconnect,
    }

    /**
     * Type of video stream
     *
     * 录像码流类型
     */
    public enum RecordStreamType {
        main,
        extra1,
    }

    /**
     * Enable type of the accessory
     *
     * 配件的使能类型
     */
    public interface ApEnableType {
        String ACCESSORIES = "accessories";
        String SOUND_LIGHT = "sound-light";
        String ALARM = "alarm";
        String LINKAGE_SIREN = "linkageSiren";
        String SOUND_SOS = "sound-sos";
    }
}
