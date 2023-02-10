package com.mm.android.mobilecommon.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static com.mm.android.mobilecommon.annotation.DeviceAbility.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@StringDef({WLAN, AlarmPIR, AlarmMD, AudioTalk, AudioTalkV1, VVP2P, PTZ, PT, HSEncrypt, CloudStorage, AGW, BreathingLight,
        PlaybackByFilename, LocalStorage, RegCode, RemoteControl, Panorama, RD, SLAlarm, CK, AudioEncodeOff, MDS, MDW, HeaderDetect,
        SR, AGWDisarm, CollectionPoint, TimedCruise, SmartTrack, ZoomFocus, SmartLocate, LocalRecord, XUpgrade, Auth, NumberStat,

        HoveringAlarm, BeOpenedDoor, NonAccessoriesAdd, CloseCamera, MobileDetect, Siren, LinkageSiren, WhiteLight, WLV2, Dormant,
        /*NoVA, */NoAccessories, UnsupportLiveShare, RTSV1, PBSV1, SearchLight, CallByRtsp, SirenTime,NVM, LEDS, TimingGraphics, HumanDetect, AlarmPIRV2, HUBAlarmPIRV2,
        AlarmPIRV3, AlarmPIRV4, LocalStorageEnable, DaySummerTime, WeekSummerTime,SummerTimeOffset, AiHuman, AiCar, Electric, WIFI, DLOCS, OpenDoorByFace, OpenDoorByTouch,
        PlaySoundModify, WideDynamic, TalkSoundModify, LinkDevAlarm, LinkAccDevAlarm, AbAlarmSound, CheckAbDecible, Reboot, PlaySound,
        AudioEncodeControl, SceneMode, SIMCA, TimeFormat, SASQ, MGOCS, ModifyName, ElecInfo, SigInfo, ACT, AlarmSound, DDT, OnlyArmed, TSV1, NoPlan, ChnLocalStorage,
        AccessoryAlarmSound, RingAlarmSound, SCCode, CustomRing, InfraredLight, AudioEncodeControlV2, InstantDisAlarm,IDAP, RDV2,RDV3, DeviceAlarmSound, FaceDetect,
        CallAbility, CAV2, HAV2, Ring, RTSV2, PBSV2, TSV2, PT1, AX, HAV3, VideoMotionSMD, TLSEnable, TCM, CLDA, ChnWhiteLight, ChnSiren, LED,
        CCR, CLS, CLW, ""})

public @interface DeviceAbility {
    String WLAN = "WLAN"; // 设备支持接入无线局域网
    String AlarmPIR = "AlarmPIR";  // 设备支持人体红外报警
    String AlarmMD = "AlarmMD";  // 设备支持动检报警
    String AudioTalk = "AudioTalk"; // 设备支持语音对讲
    String AudioTalkV1 = "AudioTalkV1"; // 通道支持语音对讲
    String VVP2P = "VVP2P";    // 设备支持威威网络P2P服务
    String PTZ = "PTZ";  // 设备支持云台方向操作,及云台缩放
    String PT = "PT";  // 设备支持云台方向操作
    String PT1 = "PT1";  // 设备支持云台四方向操作
    String HSEncrypt = "HSEncrypt";   // 设备支持华视微讯码流加密
    String CloudStorage = "CloudStorage";   // 设备支持华视微讯平台云存储
    String CloudUpdate = "CloudUpdate";   // easy4ip支持云升级，自己本地判断使用
    String AGW = "AGW";   // 设备支持网关功能
    String BreathingLight = "BreathingLight";    // 设备有呼吸灯
    String PlaybackByFilename = "PlaybackByFilename";  // 设备支持根据文件名回放
    String LocalStorage = "LocalStorage";  // 支持设备本地存储，如有SD卡或硬盘
    String RegCode = "RegCode";  // 设备添加需要支持验证码
    String RemoteControl = "RemoteControl";   // 支持远程联动
    String Panorama = "Panorama"; //支持全景图
    String RD = "RD"; //设备具有远程调试能力（Remote Debug）
    String RDV2 = "RDV2"; //支持RD能力，支持数据埋点控制，支持调试日志上传 对应设备体验计划开关
    String RDV3 = "RDV3"; //支持RD能力，支持数据埋点控制，支持级别控制
    String SLAlarm = "SLAlarm"; //设备支持声光告警（sound and light alarm）
    String CK = "CK"; // 设备视频加密
    String AudioEncodeOff = "AudioEncodeOff"; //支持音频编码关闭（无伴音）  AudioEncode：支持音频编码（伴音） 老设备不上报，因此只需要使用AudioEncodeOff进行判断即可
    String MDS = "MDS"; //通道 motion-detect-sensitive支持动检灵敏度设置
    String MDW = "MDW"; //通道 motion-detect-window支持动检窗口设置
    String HeaderDetect = "HeaderDetect"; //通道 支持人头检测
    String SR = "SR"; ////设备，设备支持语音识别
    String AGWDisarm = "AGWDisarm"; // 网关告警解除配置(APP2.8，网关支持布撤防（过滤配件告警，但保留告警配置）功能)
    String CollectionPoint = "CollectionPoint"; //支持收藏点
    String TimedCruise = "TimedCruise"; //支持定时巡航
    String SmartTrack = "SmartTrack"; //智能追踪
    String ZoomFocus = "ZoomFocus"; //支持变倍聚焦 变焦相机能力集
    String SmartLocate = "SmartLocate"; //听声辨位
    String LocalRecord = "LocalRecord"; //支持设备设备录像设置
    String XUpgrade = "XUpgrade";  // 云升级
    String Auth = "Auth";  // 设备端环回RTSP需认证
    String NumberStat = "NumberStat";  // 客流量数据采集
    String HoveringAlarm = "HoveringAlarm"; //徘徊报警
    String BeOpenedDoor = "BeOpenedDoor"; //普通开门，即成功开门（K5电池门锁）
    String NonAccessoriesAdd = "NonAccessoriesAdd";  //表示不支持C端信令添加方式
    String CloseCamera = "CloseCamera"; //支持关闭摄像头
    String MobileDetect = "MobileDetect";    //动检+PIR
    String Siren = "Siren";  //警笛
    String LinkageSiren = "LinkageSiren";  //报警联动警笛
    String WhiteLight = "WhiteLight";  //白光灯
    String WLV2 = "WLV2";    //  白光灯，不支持亮度调节能力
    String Dormant = "Dormant";  //可休眠，具有唤醒、休眠状态
    //    String NoVA = "NoVA";    //不支持语音播报(Voice Announcements)
    String NoAccessories = "NoAccessories";  //不支持配件使能(不支持布撤防)
    String UnsupportLiveShare = "UnsupportLiveShare";  //是否支持直播分享,easy4ip独有, 客户端自己的能力级，相当于控制开关
    String RTSV1 = "RTSV1";//实时流支持私有协议拉流
    String PBSV1 = "PBSV1";//回放流支持私有协议拉流
    String TSV1 = "TSV1"; //对讲支持私有协议拉流
    String RTSV2 = "RTSV2";//实时流支持私有协议拉流(TLS)
    String PBSV2 = "PBSV2";//回放流支持私有协议拉流(TLS)
    String TSV2 = "TSV2"; //对讲支持私有协议拉流(TLS)
    String TimingGraphics = "TimingGraphics"; //人形录像服务能力
    String HumanDetect = "HumanDetect"; //人形检测（海外）
    String AlarmPIRV2 = "AlarmPIRV2";   //支持PIR开关
    String HUBAlarmPIRV2 = "HUBAlarmPIRV2";   //支持Hub PIR开关
    String AlarmPIRV3 = "AlarmPIRV3";   //支持PIR扇形区域，同时支持PIR使能开关
    String AlarmPIRV4 = "AlarmPIRV4";   //支持PIR扇形区域
    String LocalStorageEnable = "LocalStorageEnable"; //支持录像存储开关
    String SearchLight = "SearchLight"; //探照灯
    String CallByRtsp = "CallByRtsp"; //表示接听、挂断可直接基于RTSP协议实现
    String SirenTime = "SirenTime"; //支持警笛时长设置
    String NVM = "NVM"; //支持夜视模式设置
    String LEDS = "LEDS"; //支持补光灯灵敏度
    String DaySummerTime = "DaySummerTime"; //按日夏令时
    String WeekSummerTime = "WeekSummerTime";//按周夏令时
    String SummerTimeOffset = "SummerTimeOffset";  //支持夏令时偏移量设置
    String TimeFormat = "TimeFormat";//支持时间格式设置
    String SceneMode = "SceneMode";  //支持布撤防情景模式设置
    String SIMCA = "SIMCA";         //支持SIM卡相关配置
    String SASQ = "SASQ";//配件防拆状态能力集
    String MGOCS = "MGOCS";//支持门磁开关状态获取
    String ModifyName = "ModifyName";//配件支持修改名称
    String ElecInfo = "ElecInfo";//支持电量信息查询上报
    String SigInfo = "SigInfo";//支持信号信息查询上报
    String ACT = "ACT";//支持报警持续时间配置
    String AlarmSound = "AlarmSound"; //支持报警音设置
    String AiHuman = "AiHuman";//人形智能   TF8P
    String AiCar = "AiCar";//车辆智能  TF8P
    String Electric = "Electric";//设备支持电池电量能力
    String WIFI = "WIFI";//设备支持获取WIFI信号强度能力
    String DLOCS = "DLOCS";//门锁开关状态

    String DDT = "DDT"; //支持布防延时能力
    String OnlyArmed = "OnlyArmed"; //只支持布防(永久布防)
    String NoPlan = "NoPlan";//不支持布防计划
    String OpenDoorByFace = "OpenDoorByFace";//人脸开门
    String OpenDoorByTouch = "OpenDoorByTouch";//	触摸开门
    String PlaySoundModify = "PlaySoundModify";//	设备提示音调节能力
    String TalkSoundModify = "TalkSoundModify";//对讲音量调节
    String WideDynamic = "WideDynamic";//	宽动态
    String LinkDevAlarm = "LinkDevAlarm";//关联设备报警
    String LinkAccDevAlarm = "LinkAccDevAlarm";//关联配件报警
    String AbAlarmSound = "AbAlarmSound";//异常报警音
    String CheckAbDecible = "CheckAbDecible";//异常检测音分贝阈值
    String Reboot = "Reboot";//设备重启
    String PlaySound = "PlaySound"; //设备提示音开关能力
    String AudioEncodeControl = "AudioEncodeControl"; //支持音频编码控制(开或关)
    String AudioEncodeControlV2 = "AudioEncodeControlV2";//支持音频编码控制(开或关)，只影响实时视频、录像音频，不影响对讲音频控制
    String AccessoryAlarmSound = "AccessoryAlarmSound";//支持报警网关配件报警音效设置
    String DeviceAlarmSound = "DeviceAlarmSound";  //设备报警音效设置
    String RingAlarmSound = "RingAlarmSound";//支持门铃音量设置
    String ChnLocalStorage = "ChnLocalStorage";//支持通道本地存储

    String SCCode = "SCCode"; // 设备支持SC安全码
    String CustomRing = "CustomRing";//自定义铃声
    String InfraredLight = "InfraredLight";//红外灯能力集
    String InstantDisAlarm = "InstantDisAlarm";//支持一键撤防能力
    String IDAP = "IDAP";//支持一键撤防能力
    String FaceDetect = "FaceDetect";    //支持人脸检测

    String CallAbility = "CallAbility"; //支持呼叫能力
    String CAV2 = "CAV2";   //支持呼叫能力，且拒接时可选择播放自定义铃声
    String HAV2 = "HAV2";   //徘徊报警V2，支持统一的检测距离设置及逗留时长设置
    String Ring = "Ring";   //仅支持铃声设置 LoginAfter DS11，去除根据设备型号兼容逻辑 2019-4-3
    String AX = "AX";   // 	安消一体机
    String HAV3 = "HAV3";   //  徘徊报警V3，V2降级版，不支持逗留时长立即设置
    String VideoMotionSMD = "VideoMotionSMD";//包含人形和车辆能力

    /*3.15.0*/
    String TCM = "TCM"; //支持3码合一(Three code megre)
    String TLSEnable = "TLSEnable";// 2019-8-20 支持拉流、图片和云录像链路加密传输

    /*5.0.0*/
    String CLW = "CLW"; //通道报警联动白光灯
    String CLS = "CLS"; //通道报警联动警笛
    String CCR = "CCR"; //通道自定义铃声
    String LED = "LED"; //补光灯
    String ChnSiren = "ChnSiren"; //警笛
    String ChnWhiteLight = "ChnWhiteLight"; //通道白光灯
    String CLDA = "CLDA"; //通道关联设备报警

}
