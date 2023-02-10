package com.mm.android.mobilecommon.openapi;


public class CONST {
    //获取token
    public static String METHOD_ACCESSTOKEN="accessToken";
    //未绑定设备信息获取
    public static String METHOD_UNBINDDEVICEINFO="unBindDeviceInfo";
    //获取设备添加流程引导页配置信息
    public static String METHOD_GUIDEINFOGET="deviceAddingProcessGuideInfoGet";
    //获取设备添加流程引导页配置信息 新接口IOT设备获取设备信息引导页
    public static String METHOD_GETNETWORKCONFIG="getNetworkConfig";
    //校验设备添加流程引导页配置信息
    public static String METHOD_GUIDEINFOCHECK="deviceAddingProcessGuideInfoCheck";
    //绑定设备
    public static String METHOD_BINDDEVICE="bindDevice";

    public static String METHOD_MESSAGE = "getAlarmMessage";

    public static String METHOD_DELETE_ALARM_MESSAGE = "deleteAlarmMessage";
    //修改设备或通道名称
    public static String METHOD_MODIFYDEVICENAME="modifyDeviceName";
    //设备当前连接热点信息
    public static String METHOD_CURRENT_DEVICE_WIFI="currentDeviceWifi";
    //设备周边WIFI信息
    public static String METHOD_WIFI_AROUND="wifiAround";
    //控制设备连接热点
    public static String METHOD_CONTROL_DEVICE_WIFI="controlDeviceWifi";
    //批量根据设备序列号、通道号列表和配件号列表，获取设备的详细信息
    public static String METHOD_DEVICE_OPEN_DETAIL_LIST = "deviceOpenDetailList";

    public static String GET_OPEN_ID_AY_ACCOUNT = "getOpenIdByAccount";

    public static String SUB_ACCOUNT_TOKEN = "subAccountToken";

    public static String SUB_ACCOUNT_DEVICE_INFO = "subAccountDeviceInfo";

    public static String LIST_DEVICE_DETAIL_BATCH = "listDeviceDetailsByIds";

    public static String CREATE_SUB_ACCOUNT = "createSubAccount";

    //添加子账户权限
    public static String ADD_POLICY = "addPolicy";
    //获取设备布防计划
    public static String DEVICE_ALARM_PLAN = "deviceAlarmPlan";
    //设置设备布防计划
    public static String MODIFY_DEVICE_ALARM_PLAN = "modifyDeviceAlarmPlan";

    // URL地址
    public static String HOST = "";
    // 如果不知道appid，请登录open.lechange.com，开发者服务模块中创建应用
    public static String APPID = "";
    // 如果不知道appsecret，请登录open.lechange.com，开发者服务模块中创建应用
    public static String SECRET = "";

    public enum Envirment {
        CHINA_TEST("https://funcopenapi.lechange.cn:443"),
        CHINA_PRO("https://openapi.lechange.cn:443"),
        OVERSEAS_TEST("https://openapifunc.easy4ip.com:443"),
        OVERSEAS_PRO("https://openapi.easy4ip.com:443");
        public String url;

        Envirment(String url) {
            this.url = url;
        }
    }

    public static void makeEnv(String url,String appId,String secretKey) {
        APPID = appId;
        SECRET = secretKey;
        HOST = url;
    }

    public static class IOT{
        public static String IOT_REG_CLOUD_HOST = "iotaccess.easy4ipcloud.com";
        public static int IOT_REG_CLOUD_PORT = 10001;
        public static String GET_DEVICEINFO_FORBIND = "iot.manager.GetDeviceInfoForBind";
        public static String BIND_DEVICE = "iot.manager.BindDevice";

        private static String PREFIX = "http://";
        private static String THING_HOST = "%s:13015/things";
        private static String VERSION = "/v1";
        public static String GET_IOTDEVICEINFO_BYHOTSPORT = PREFIX + THING_HOST + VERSION + "/deviceinfo/get";
        public static String REGSERVER_CONF = PREFIX + THING_HOST + VERSION + "/dcs/cloud/regserver/conf";
        public static String WIRELESS_WIFI_SET = PREFIX + THING_HOST + VERSION + "/wireless/wifi/set";
        public static String KEEPALIVE = PREFIX + THING_HOST + VERSION + "/dcs/keepalive";
        public static String DEVICE_LOGIN = PREFIX + THING_HOST + VERSION + "/dcs/login";
    }
}
