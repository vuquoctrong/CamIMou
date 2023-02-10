package com.mm.android.mobilecommon.entity.deviceadd;

import android.text.TextUtils;


import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.entity.DataInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;

import java.io.Serializable;


/**
 * Device Add module Device information storage class
 *
 * 设备添加模块设备信息存储类
 */
public class DeviceAddInfo extends DataInfo {
    public static final int NC_TYPE_SOUND_WAVE_V2 = 1;  // 新声波


    public static class GatewayInfo implements Serializable {       //若是添加配件，存储对应的网关信息
        String sn;
        String ability;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getAbility() {
            return ability;
        }

        public void setAbility(String ability) {
            this.ability = ability;
        }

        @Override
        public String toString() {
            return "GatewayInfo{" +
                    "sn='" + sn + '\'' +
                    ", ability='" + ability + '\'' +
                    '}';
        }
    }

    public class WifiInfo implements Serializable {
        String ssid = "";
        String pwd = "";

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        @Override
        public String toString() {
            return "WifiInfo{" +
                    "ssid='" + ssid + '\'' +
                    ", pwd='" + pwd + '\'' +
                    '}';
        }
    }

    public class GPSInfo implements Serializable {
        String longitude;
        String latitude;

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        @Override
        public String toString() {
            return "GPSInfo{" +
                    "longitude='" + longitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    '}';
        }
    }

    //设备配网模式
    public enum ConfigMode {
        SmartConfig,        // SmartConfig方式
        SoundWave,          // 声波方式
        SoftAP,             // 软AP方式
        LAN,                // 有线局域网
        //        SIMCard,          // SIMCARD方式
        QRCode,              // 二维码
        SoundWaveV2,          // 声波V2版本，优化声波算法
        Location,           //设备本地配网
        Bluetooth,           //设备本地配网
        wifi,             //iotWifi
        iotLan,             //iotLan
        NBIOT               //NB配网,
    }

    //设备绑定状态
    public enum BindStatus {
        bindByMe,           //被当前帐户绑定
        bindByOther,       //被其他帐户绑定
        unbind              //未被绑定
    }

    //设备状态
    public enum Status {
        online,         //在线
        offline,       //离线
        upgrading,     //升级中
        sleep          //休眠
    }

    //设备类型
    public enum DeviceType {
        ap,             //配件
        device,         //配件外的设备
        iot         //iot设备
    }

    public enum DeviceAddType {
        SOFTAP,         //软AP添加
        AP,             //配件添加
        //        SIMCARD,       //SIM卡添加
        HUB,             //HUB配对
        WLAN,           //无线
        LAN,            //有线
        ONLINE,         //在线
        LOCAL,          //设备本地配网
        NBIOT,             //NB配网
        Bluetooth;           //设备本地配网
    }

    boolean support = true;        //是否支持绑定
    String deviceSn;               //设备序列号
    String deviceCodeModel;        //二维码中扫描得到的设备型号
    boolean deviceExist;             //设备是否在服务上注册
    String bindStatus;              //设备绑定状态
    String bindAcount;              //设备绑定帐号
    String accessType = "PaaS";     //设备接入类型 PaaS-表示Paas程序接入、Lechange-表示乐橙非PaaS设备、Easy4IP表示Easy4IP程序设备、P2P表示P2P程序设备
    String status;                   //设备状态
    String configMode;              //配对模式SmartConfig，SoundWave，SoftAP，LAN，SIMCard
    String defaultWifiConfigMode;              //配对模式SmartConfig，SoundWave，SoftAP，LAN，SIMCard
    boolean wifiConfigModeOptional = false;  //true,表示可让用户自行选择可用的配网模式   小微这边没有这个配置功能，默认false
    String wifiTransferMode;        //wifi频段2.4Ghz,5Ghz

    //    String imeiCode = "";                // iot设备唯一标识码
    String deviceDefaultName;                  //设备默认名，绑定成功后服务返回
    String regCode;                 //设备安全码，国内乐橙乐盒设备才有
    String nc;                      // 设备二维码上的能力，之前的QR(二维码配网)不在支持，目前使用为新声波使用
    WifiInfo wifiInfo = new WifiInfo();              //无线配置当前wifi信息
    public String curConfigMode = ConfigMode.SmartConfig.name();     //当前配对模式
    String devicePwd;               //设备密码

    String recordSaveDays;     // 录像保存天数（免费套餐信息,设备有可赠送免费套餐时返回）
    String streamType;        // 码流类型：main：主码流extra1：辅码流（免费套餐信息,设备有可赠送免费套餐时返回）
    String serviceTime;        //服务时长(秒)（免费套餐信息,设备有可赠送免费套餐时返回）

    boolean isWifiOfflineMode = false;        //是否为wifi离线配置
    GPSInfo gpsInfo = new GPSInfo();
    GatewayInfo gatewayInfo;
    DeviceAddType curDeviceAddType = DeviceAddType.WLAN;            //当前添加流程类型

    DeviceIntroductionInfo mDevIntroductionInfo;        //设备添加引导信息

    boolean isDeviceDetail = false;                             // 是否为中间页添加

    private String sc;                                  // 设备安全验证码

    private String requestId;

    private boolean isManualInput;                      // 手动输入序列号进入

    private String ssid;                                //软ap添加时，设备的SSID

    private String previousSsid;                        //连接软ap之前的ssid，方便退出软ap流程时恢复网络

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private long startTime;                               //

    //TODO SMB这里字段的默认值和上层判断逻辑需要处理
    //**********************网络获取缺失的字段********************************//
    String brand;                    //设备品牌信息lechange-乐橙设备，general-通用设备, 海外：imou-非乐橙设备，general-通用设备
    String family;                   //设备系列
    String deviceModel;             //设备型号
    String modelName;               //型号名称
    String catalog;                  //设备大类
    String ability;                  //设备能力项
    String type;                     //设备分类，ap配件device设备
    int channelNum;                 //视频通道的总数量（包含未接入的通道），网关的通道数可能为0
    private String watchSetupVideoUrl;  //String 可选 视频地址，海外3.100新增
    private String port;                 // String 可选 设备私有协议端口,海外使用
    //**********************网络获取缺失的字段********************************//

    //********************** IOT 新增字段 ********************************//
    private String nextStep;                //下个步骤
    private String productId;               //IOT小类选择后设备类型ID，IOT设备才有值
    public enum IotDeviceAddNextStep{ //IOT设备的下一步状态
        bind,
        networkConfig,
    }
    //********************** IOT 新增字段 ********************************//

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getDeviceSn() {
        return deviceSn == null ? "" : deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceCodeModel() {
        return deviceCodeModel == null ? "" : deviceCodeModel;
    }

    public void setDeviceCodeModel(String deviceCodeModel) {
        this.deviceCodeModel = deviceCodeModel;
    }

    public boolean isDeviceInServer() {
        return deviceExist;
    }

    public boolean isP2PDev() {      //是否为P2P设备
        return "p2p".equalsIgnoreCase(accessType);
    }

    public boolean isEasy4ipP2PDev() {      //是否为Easy4ip及P2P设备
        return "p2p".equalsIgnoreCase(accessType)
                || "easy4ip".equalsIgnoreCase(accessType);
    }

    public boolean getDeviceExist() {
        return deviceExist;
    }

    public void setDeviceExist(boolean deviceExist) {
        this.deviceExist = deviceExist;
    }

    public String getBindStatus() {
        return bindStatus == null ? "" : bindStatus;
    }

    public void setBindStatus(String bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getBindAcount() {
        return bindAcount == null ? "" : bindAcount;
    }

    public void setBindAcount(String bindAcount) {
        this.bindAcount = bindAcount;
    }

    public String getAccessType() {
        return accessType == null ? "" : accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfigMode() {
        return configMode == null ? "" : configMode;
    }

    public void setConfigMode(String configMode) {
        this.configMode = configMode;
    }

    public String getDefaultWifiConfigMode() {
        return defaultWifiConfigMode;
    }

    public void setDefaultWifiConfigMode(String defaultWifiConfigMode) {
        this.defaultWifiConfigMode = defaultWifiConfigMode;
    }

    public String getBrand() {
        return brand == null ? "" : brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getFamily() {
        return family == null ? "" : family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getDeviceModel() {
        return deviceModel == null ? "" : deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getModelName() {
        return modelName == null ? "" : modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCatalog() {
        return catalog == null ? "" : catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWifiTransferMode() {
        return wifiTransferMode == null ? "" : wifiTransferMode;
    }

    public void setWifiTransferMode(String wifiTransferMode) {
        this.wifiTransferMode = wifiTransferMode;
    }

    public String getRegCode() {
        return regCode == null ? "" : regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public DeviceIntroductionInfo getDevIntroductionInfo() {
        return mDevIntroductionInfo;
    }

    public void setDevIntroductionInfos(DeviceIntroductionInfo devIntroductionInfo) {
        this.mDevIntroductionInfo = devIntroductionInfo;
    }

    public WifiInfo getWifiInfo() {
        return wifiInfo;
    }

    public void setWifiInfo(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public String getCurConfigMode() {
        return curConfigMode == null ? "" : curConfigMode;
    }

    public void setCurConfigMode(String curConfigMode) {
        this.curConfigMode = curConfigMode;
    }

    public String getDevicePwd() {
        return devicePwd == null ? "" : devicePwd;
    }

    public void setDevicePwd(String devicePwd) {
        this.devicePwd = devicePwd;
    }

    public boolean isWifiOfflineMode() {
        return isWifiOfflineMode;
    }

    public void setWifiOfflineMode(boolean wifiOfflineMode) {
        isWifiOfflineMode = wifiOfflineMode;
    }


    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public GPSInfo getGpsInfo() {
        return gpsInfo;
    }

    public String getAbility() {
        return ability == null ? "" : ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }

    public void setGatewayInfo(GatewayInfo gatewayInfo) {
        this.gatewayInfo = gatewayInfo;
    }

    public String getDeviceDefaultName() {
        return deviceDefaultName == null ? "" : deviceDefaultName;
    }

    public void setDeviceDefaultName(String deviceDefaultName) {
        this.deviceDefaultName = deviceDefaultName;
    }

    public DeviceAddType getCurDeviceAddType() {
        return curDeviceAddType;
    }

    public void setCurDeviceAddType(DeviceAddType curDeviceAddType) {
        this.curDeviceAddType = curDeviceAddType;
    }

    public boolean hasAbility(@DeviceAbility String ability) {
        return !TextUtils.isEmpty(this.ability) && this.ability.contains(ability);
    }

    public void clearCache() {
        wifiInfo.setPwd(null);
        wifiInfo.setSsid(null);
        curDeviceAddType = DeviceAddType.WLAN;
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

    public boolean isDeviceDetail() {
        return isDeviceDetail;
    }

    public void setDeviceDetail(boolean deviceDetail) {
        isDeviceDetail = deviceDetail;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    //设备是否有安全码
    public boolean hasSc(){
        return !TextUtils.isEmpty(sc);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public boolean isManualInput() {
        return isManualInput;
    }

    public void setManualInput(boolean manualInput) {
        isManualInput = manualInput;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPreviousSsid() {
        return previousSsid;
    }

    public void setPreviousSsid(String previousSsid) {
        this.previousSsid = previousSsid;
    }

    public String getWatchSetupVideoUrl() {
        return watchSetupVideoUrl;
    }

    public void setWatchSetupVideoUrl(String watchSetupVideoUrl) {
        this.watchSetupVideoUrl = watchSetupVideoUrl;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isWifiConfigModeOptional() {
        return wifiConfigModeOptional;
    }

    public void setWifiConfigModeOptional(boolean wifiConfigModeOptional) {
        this.wifiConfigModeOptional = wifiConfigModeOptional;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    boolean isSupportCollectDeviceLog = false;        //是否支持设备日志采集

    public boolean isSupportCollectDeviceLog() {
        return isSupportCollectDeviceLog;
    }

    public void setSupportCollectDeviceLog(boolean supportCollectDeviceLog) {
        isSupportCollectDeviceLog = supportCollectDeviceLog;
    }


    @Override
    public String toString() {
        return "DeviceAddInfo{" +
                "deviceSn='" + deviceSn + '\'' +
                ", deviceCodeModel='" + deviceCodeModel + '\'' +
                ", deviceExist='" + deviceExist + '\'' +
                ", bindStatus='" + bindStatus + '\'' +
                ", bindAcount='" + bindAcount + '\'' +
                ", accessType='" + accessType + '\'' +
                ", status='" + status + '\'' +
                ", configMode='" + configMode + '\'' +
                ", brand='" + brand + '\'' +
                ", family='" + family + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", modelName='" + modelName + '\'' +
                ", catalog='" + catalog + '\'' +
                ", ability='" + ability + '\'' +
                ", type='" + type + '\'' +
                ", wifiTransferMode='" + wifiTransferMode + '\'' +
                ", deviceDefaultName='" + deviceDefaultName + '\'' +
                ", regCode='" + regCode + '\'' +
                ", nc='" + nc + '\'' +
                ", wifiInfo=" + wifiInfo +
                ", curConfigMode='" + curConfigMode + '\'' +
                ", devicePwd='" + devicePwd + '\'' +
                ", isWifiOfflineMode=" + isWifiOfflineMode +
                ", gpsInfo=" + gpsInfo +
                ", gatewayInfo=" + gatewayInfo +
                ", curDeviceAddType=" + curDeviceAddType +
                ", mDevIntroductionInfo=" + mDevIntroductionInfo +
                ",sc=" + sc +
                ",requestId=" + requestId +
                ",isManualInput=" + isManualInput +
                ", ssid=" + ssid +
                ", previousSsid=" + previousSsid +
                '}';
    }
}
