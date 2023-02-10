package com.mm.android.deviceaddmodule.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.company.NetSDK.CFG_NETAPP_WLAN;
import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.common.login.LoginManager;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.DeviceAddActivity;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.annotation.DeviceAbility;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.p2pDevice.P2PErrorHelper;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.WifiUtil;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceAddHelper {
    public static final String DEVICE_MODEL_NAME_PARAM= "device_model_name_param";               //设备市场型号
    public static final int DEVICE_NAME_MAX_LENGTH = 20;        //设备名长度限制
    public static final int AP_NAME_MAX_LENGTH = 20;            //配件名长度限制
    public static final String AP_WIFI_NAME_DAP = "DAP-XXXX";         //软Ap通用设备热点名称
    public static final String AP_WIFI_VERSION_V1 = "V1";            //软Ap wifi version版本
    public static final String AP_WIFI_VERSION_V2 = "V2";            //软Ap wifi version版本

    // 是否支持新声波，二进制第1位，0表示否，1表示是
    public static final int ALLOW_SoundWaveV2 = 1; // 0001

    // 是否支持老声波，二进制第2位，0表示否，1表示是
    public static final int ALLOW_SoundWave = 1 << 1; // 0010

    // 是否支持SmartConfig，二进制第3位，0表示否，1表示是
    public static final int ALLOW_SmartConfig = 1 << 2; // 0100

    // 是否支持SoftAP，二进制第4位，0表示否，1表示是
    public static final int ALLOW_SoftAP = 1 << 3; // 1000

    // 是否支持LAN，二进制第4位，0表示否，1表示是
    public static final int ALLOW_LAN = 1 << 4; // 10000

    // 是否支持蓝牙，二进制第4位，0表示否，1表示是
    public static final int ALLOW_BT = 1 << 5; // 100000

    public enum TitleMode {
//        FLASH,          //闪光灯
        MORE,           //更多
        MORE2,           //更多模式2
        MORE3,           //更多模式3
        MORE4,           //更多模式4
        REFRESH,         //刷新
        BLANK,           //空白
        SHARE,            //设备共享
        FREE_CLOUD_STORAGE, //免费云存储
        MODIFY_DEVICE_NAME  //修改设备密码
    }

    public enum TimeoutDevTypeModel {
        A_MODEL,            //A系列
        CK_MODEL,           //CK系列
        COMMON_MODEL,      //通用设备
        DOORBELL_MODEL,    //门铃系列
        AP_MODEL,           //配件
        OTHER_MODEL,         //其他
        TP1_MODEL,          //TP1类型
        TP1S_MODEL,         //TP1S类型
        G1_MODEL,           //G1类型
        K5_MODEL     //门锁类型
    }

    /**
     * Description Error codes were added to the device
     *
     * 设备添加相关错误码
     */
    public interface ErrorCode {
        //输入设备序列号流程相关错误码
        int INPUT_SN_ERROR_BIND_BY_OTHER = 1000 + 1;                 //输入序列号时设备被其他用户绑定
        int DEVICE_BIND_ERROR_BIND_BY_OTHER = 1000 + 2;              //绑定时设备被其他用户绑定
        int DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND = 1000 + 3;         //扫码、选型号、设备上线绑定时不支持绑定的设备类型
        //设备选择流程相关错误码
        int TYPE_CHOOSE_ERROR_1 = 2000;
        //有线无线配置流程相关错误码
        int WIRED_WIRELESS_ERROR_CONFIG_TIMEOUT = 3000 + 1;      //配置超时
        //软AP添加流程相关错误码
        int SOFTAP_ERROR_CONNECT_HOT_FAILED = 4000 + 1;        //连接设备热点失败
        int SOFTAP_ERROR_CONNECT_WIFI_FAILED = 4000 + 2;        //设备连接wifi失败
        //设备初始化流程相关错误码
        int INIT_ERROR_SERCRITY_CHECK_TIMEOUT = 5000 + 1;        //安全检查超时
        int INIT_ERROR_INIT_FAILED = 5000 + 2;                     //初始化失败
        //连接云平台流程相关错误码
        int CLOUND_CONNECT_ERROR_CONNECT_TIMEOUT = 6000 + 1;                     //连接超时
        int CLOUND_CONNECT_QUERY_STATUS_TIMEOUT = 6000 + 2;                     //查询超时

        //通用
        int COMMON_ERROR_NOT_SUPPORT_5G = 7000 + 1;                             //不支持5G
        int COMMON_ERROR_NOT_SUPPORT_RESET = 7000 + 2;                             //重置设备
        int COMMON_ERROR_NOT_SUPPORT_HUB_AP_RESET = 7000 + 3;                             //重置设备
        int COMMON_ERROR_NOT_SUPPORT_HUB_RESET = 7000 + 4;                             //重置设备

        int COMMON_ERROR_DEVICE_LOCKED = 7000 + 5;                          //设备锁定
        int COMMON_ERROR_RED_ROTATE = 7000 + 6;                          //红灯旋转
        int COMMON_ERROR_RED_ALWAYS = 7000 + 7;                          //红灯长亮
        int COMMON_ERROR_RED_FLASH = 7000 + 8;                          //红灯闪烁
        int COMMON_ERROR_DEVICE_BIND_MROE_THAN_TEN = 7000 + 9;                          //超过10次
        int COMMON_ERROR_DEVICE_MROE_THAN_TEN_TWICE = 7000 + 10;                          //再次超过10次
        int COMMON_ERROR_DEVICE_IP_ERROR = 7000 + 11;                          //IP
        int COMMON_ERROR_DEVICE_SN_CODE_CONFLICT = 7000 + 12;                          //串号
        int COMMON_ERROR_DEVICE_SN_OR_IMEI_NOT_MATCH = 7000 + 13;                          //imei和device id不匹配

        int COMMON_ERROR_ABOUT_WIFI_PWD = 7000 + 14;                             //关于WIFI密码

        int COMMON_ERROR_CONNECT_FAIL = 7000 + 15;                             //软AP连接失败
        int COMMON_ERROR_WIFI_NAME = 7000 + 16;                             //软AP连接失败

        //配件
        int AP_ERROR_PAIR_TIMEOUT = 8000 + 2;                                   //配对超时
    }

    /**
     * OMS configuration key
     *
     * OMS配置key
     */
    public interface OMSKey {
        final static String ERROR_TIPS_TYPE = "ErrorTipsType";
        final static String ERROR_WIFI_TIPS_TYPE = "WifiErrorTipsType";             //有线无线配网错误页面模式
        final static String ERROR_SOFTAP_TIPS_TYPE = "SoftAPErrorTipsType";         //软AP配网错误页面模式
        final static String ERROR_ACCESSORY_TIPS_TYPE = "AccessoryErrorTipsType";   //配件配网错误页面模式
        //有线无线
        final static String WIFI_MODE_GUIDING_LIGHT_IMAGE = "WifiModeGuidingLightImage";
        final static String WIFI_MODE_CONFIG_INTRODUCTION = "WifiModeConfigIntroduction";
        final static String WIFI_MODE_CONFIG_CONFIRM_INTRODUCTION = "WifiModeConfigConfirmIntroduction";
        final static String WIFI_MODE_RESET_GUIDE_INTRODUCTION = "WifiModeResetGuideIntroduction";
        final static String WIFI_MODE_RESET_IMAGE = "WifiModeResetImage";
        final static String WIFI_MODE_RESET_OPERATION_INTRODUCTION = "WifiModeResetOperationIntroduction";
        final static String WIFI_MODE_FINISH_DEVICE_IMAGE = "WifiModeFinishDeviceImage";
        //软AP
        final static String SOFT_AP_MODE_WIFI_NAME = "SoftAPModeWifiName";
        final static String SOFT_AP_MODE_GUIDING_STEP_ONE_IMAGE = "SoftAPModeGuidingStepOneImage";
        final static String SOFT_AP_MODE_GUIDING_STEP_ONE_INTRODUCTION = "SoftAPModeGuidingStepOneIntroduction";
        final static String SOFT_AP_MODE_GUIDING_STEP_TWO_IMAGE = "SoftAPModeGuidingStepTwoImage";
        final static String SOFT_AP_MODE_GUIDING_STEP_TWO_INTRODUCTION = "SoftAPModeGuidingStepTwoIntroduction";
        final static String SOFT_AP_MODE_GUIDING_STEP_THREE_IMAGE = "SoftAPModeGuidingStepThreeImage";
        final static String SOFT_AP_MODE_GUIDING_STEP_THREE_INTRODUCTION = "SoftAPModeGuidingStepThreeIntroduction";
        final static String SOFT_AP_MODE_GUIDING_STEP_FOUR_IMAGE = "SoftAPModeGuidingStepFourImage";
        final static String SOFT_AP_MODE_GUIDING_STEP_FOUR_INTRODUCTION = "SoftAPModeGuidingStepFourIntroduction";
        final static String SOFT_AP_MODE_RESET_GUIDE_INTRODUCTION = "SoftAPModeResetGuideIntroduction";
        final static String SOFT_AP_MODE_RESET_IMAGE = "SoftAPModeResetImage";
        final static String SOFT_AP_MODE_RESET_OPERATION_INTRODUCTION = "SoftAPModeResetOperationIntroduction";
        final static String SOFT_AP_MODE_RESULT_PROMPT_IMAGE = "SoftAPModeResultPromptImage";
        final static String SOFT_AP_MODE_RESULT_INTRODUCTION = "SoftAPModeResultIntroduction";
        final static String SOFT_AP_MODE_CONFIRM_INTRODUCTION = "SoftAPModeConfirmIntroduction";
        final static String SOFT_AP_MODE_WIFI_VERSION = "SoftAPModeWifiVersion";
        //配件
        final static String ACCESSORY_MODE_PAIR_STATUS_IMAGE = "AccessoryModePairStatusImage";
        final static String ACCESSORY_MODE_PAIR_OPERATION_INTRODUCTION = "AccessoryModePairOperationIntroduction";
        final static String ACCESSORY_MODE_PAIR_CONFIRM_INTRODUCTION = "AccessoryModePairConfirmIntroduction";
        final static String ACCESSORY_MODE_RESET_GUIDE_INTRODUCTION = "AccessoryModeResetGuideIntroduction";
        final static String ACCESSORY_MODE_RESET_IMAGE = "AccessoryModeResetImage";
        final static String ACCESSORY_MODE_RESET_OPERATION_INTRODUCTION = "AccessoryModeResetOperationIntroduction";
        final static String ACCESSORY_MODE_FINISH_DEVICE_IMAGE = "AccessoryModeFinishDeviceImage";
        //Hub
        final static String HUB_MODE_PAIR_STATUS_IMAGE = "HubModePairStatusImage";
        final static String HUB_MODE_PAIR_OPERATION_INTRODUCTION = "HubModePairOperationIntroduction";
        final static String HUB_MODE_RESET_GUIDE_INTRODUCTION = "HubModeResetGuideIntroduction";
        final static String HUB_MODE_RESET_IMAGE = "HubModeResetImage";
        final static String HUB_MODE_RESET_OPERATION_INTRODUCTION = "HubModeResetOperationIntroduction";
        final static String HUB_ACCESSORY_MODE_PAIR_STATUS_IMAGE = "HubAccessoryModePairStatusImage";
        final static String HUB_ACCESSORY_MODE_PAIR_OPERATION_INTRODUCTION = "HubAccessoryModePairOperationIntroduction";
        final static String HUB_ACCESSORY_MODE_RESET_GUIDE_INTRODUCTION = "HubAccessoryModeResetGuideIntroduction";
        final static String HUB_ACCESSORY_MODE_RESET_IMAGE = "HubAccessoryModeResetImage";
        final static String HUB_ACCESSORY_MODE_RESET_OPERATION_INTRODUCTION = "HubAccessoryModeResetOperationIntroduction";
        final static String HUB_MODE_RESULT_PROMPT_IMAGE = "HUBModeResultPromptImage";
        final static String HUB_MODE_RESULT_INTRODUCTION = "HUBModeResultIntroduction";
        final static String HUB_MODE_CONFIRM_INTRODUCTION = "HUBModeConfirmIntroduction";
        //设备本地配网
        final static String LOCATION_MODE_OPERATION_IMAGE = "LocationOperationImages";  //引导图
        final static String LOCATION_MODE_OPERATION_INTRODUCTION = "LocationOperationIntroduction";    //引导文案
        final static String LOCATION_MODE_FINISH_DEVICE_IMAGE = "LocationModeFinishDeviceImage";    //添加完成正视图
        // NB
        final static String THIRD_PARTY_PLATFORM_MODE_GUIDING_LIGHT_IMAGE = "ThirdPartyPlatformModeGuidingLightImage";  //引导图
        final static String THIRD_PARTY_PLATFORM_MODE_RESULT_PROMPT_IMAGE = "ThirdPartyPlatformModeResultPromptImage";
    }

    public static void updateTile(TitleMode titleMode) {
        Bundle bundle = new Bundle();
        bundle.putString(DeviceAddEvent.KEY.TITLE_MODE, titleMode.name());
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.TITLE_MODE_ACTION, bundle));
    }

    /**
     * Determine whether the device needs to be initialized
     * @param device_net_info_ex  Device Network Information
     * @return boolean  Boolean value
     *
     * 判断设备是否需要初始化
     * @param device_net_info_ex  设备网络信息
     * @return boolean  布尔值
     */
    public static boolean isDeviceNeedInit(DeviceInitInfo device_net_info_ex) {
        byte[] s = getByteArray(device_net_info_ex.mInitStatus);
        // 设备初始化状态，按位确定初始化状态
        // bit0~1：0-老设备，没有初始化功能 1-未初始化帐号 2-已初始化帐户
        // bit2~3：0-老设备，保留 1-公网接入未使能 2-公网接入已使能
        // bit4~5：0-老设备，保留 1-手机直连未使能 2-手机直连使能
        if (s[s.length - 1] != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check whether the device is initialized
     * @param device_net_info_ex  Device Network Information
     * @return boolean  Boolean value
     *
     * 判断设备是否已经初始化
     * @param device_net_info_ex  设备网络信息
     * @return boolean  布尔值
     */
    public static boolean isDeviceInited(DeviceInitInfo device_net_info_ex) {
        byte[] s = getByteArray(device_net_info_ex.mInitStatus);
        // 设备初始化状态，按位确定初始化状态
        // bit0~1：0-老设备，没有初始化功能 1-未初始化帐号 2-已初始化帐户
        // bit2~3：0-老设备，保留 1-公网接入未使能 2-公网接入已使能
        // bit4~5：0-老设备，保留 1-手机直连未使能 2-手机直连使能
        if (s[s.length - 1] != 2) {
            return false;
        } else {
            return true;
        }
    }

    //判断设备是否支持初始化，仅在软AP添加流程中使用（为了兼容K5不支持初始化，需要用默认用admin登录设备）。其余添加流程仅判断是否需要初始化即可，需要的弹框用户输入，不需要的跳过。
    public static boolean isDeviceSupportInit(DeviceInitInfo device_net_info_ex){
        byte[] s = getByteArray(device_net_info_ex.mInitStatus);
        // 设备初始化状态，按位确定初始化状态
        // bit0~1：0-老设备，没有初始化功能 1-未初始化帐号 2-已初始化帐户
        // bit2~3：0-老设备，保留 1-公网接入未使能 2-公网接入已使能
        // bit4~5：0-老设备，保留 1-手机直连未使能 2-手机直连使能
        if (s[s.length - 2] != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get configuration, Generation 3 protocol
     * @param loginHandle  Log on to Handle...
     * @param channelID  Channel Id
     * @param strCommand  Command string
     * @param BUFFERLEN  Cache length
     * @param stCfg  WIFI Configuration Information
     * @param requestId  Request Id
     * @return boolean Boolean value
     *
     * 获取配置，3代协议
     * @param loginHandle  登录Handle
     * @param channelID  通道Id
     * @param strCommand  命令字符串
     * @param BUFFERLEN  缓存长度
     * @param stCfg  WIFI配置信息
     * @param requestId  请求Id
     * @return boolean 布尔值
     */
    public static boolean getNewDevConfig(long loginHandle, int channelID, String strCommand, int BUFFERLEN, CFG_NETAPP_WLAN stCfg, String requestId) {
        boolean ret = false;
        //netsdk 使用局限，只能使用 类对象传递参数。
        Integer error = new Integer(0);
        char szBuffer[] = new char[BUFFERLEN];
        ret = INetSDK.GetNewDevConfig(loginHandle, strCommand, channelID, szBuffer, BUFFERLEN, error, 5 * 1000);
        if (!ret) {
            return false;
        }
        ret = INetSDK.ParseData(strCommand, szBuffer, stCfg, null);
        if (!ret) {
            return false;
        }
        return true;
    }

    /**
     * Check whether the device user name or password is incorrect
     * @param error  Error code
     *
     * 判断是否是设备用户名或密码错误
     * @param error  错误码
     */
    public static boolean isDevPwdError(int error) {
        return (error == P2PErrorHelper.LOGIN_ERROR_KEY_OR_USER_MISMATCH
                || error == P2PErrorHelper.LOGIN_ERROR_KEY_MISMATCH
                || error == FinalVar.NET_LOGIN_ERROR_PASSWORD
                || error == FinalVar.NET_USER_FLASEPWD
                || error == FinalVar.NET_LOGIN_ERROR_USER_OR_PASSOWRD);
    }

    public static byte[] getByteArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * Characters of the filter
     * @param str  A string value
     * @return String  A string value
     *
     * 字符过滤
     * @param str  字符串值
     * @return String  字符串值
     */
    public static String strDeviceNameFilter(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        String strEx = "^[a-zA-Z0-9\\-\u4E00-\u9FA5\\_\\@\\s]+";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(strEx)) {
                builder.append(temp);
            }
        }
        return builder.toString();
    }

    /**
     * The sc code can be added
     * @param deviceAddInfo  Adding Device Information
     * @return boolean  Boolean value
     *
     * 支持sc码添加
     * @param deviceAddInfo  设备添加信息
     * @return boolean 布尔值
     */
    public static boolean isSupportAddBySc(DeviceAddInfo deviceAddInfo) {
        if(deviceAddInfo == null) {
            return false;
        }
        if(!TextUtils.isEmpty(deviceAddInfo.getSc()) && deviceAddInfo.getSc().length() == 8) {
            return true;
        }
        if(deviceAddInfo.hasAbility(DeviceAbility.SCCode)) {
            return true;
        }
        return false;
    }

    /**
     * If the device supports the SC code, use the SC code as the device password
     *
     * 支持SC码的设备，使用SC码作为设备密码
     */
    public static void setDevicePwdBySc() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if(DeviceAddHelper.isSupportAddBySc(deviceAddInfo)) {
            deviceAddInfo.setDevicePwd(deviceAddInfo.getSc());
        }
    }

    /**
     * Supports 2 generations of sound waves
     * @param deviceAddInfo  Adding Device Information
     * @return boolean Boolean value
     *
     * 支持2代声波
     * @param deviceAddInfo  设备添加信息
     * @return boolean 布尔值
     */
    public static boolean isSupportSoundWaveV2(DeviceAddInfo deviceAddInfo) {
        if(deviceAddInfo == null) {
            return false;
        }
        return deviceAddInfo.getConfigMode() != null && deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.SoundWaveV2.name());
    }

    public static  String getJsonString(DeviceAddInfo deviceAddInfo, boolean offlineConfigType) {
        JSONObject result = new JSONObject();
        try {
            result.put("SN", deviceAddInfo.getDeviceSn());
            if(offlineConfigType) {
                result.put("deviceModelName", deviceAddInfo.getDeviceModel());
            } else {
                result.put("SC", deviceAddInfo.getSc());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result.toString();
    }

    // 打印错误码
    public static int printError() {
        int error = (INetSDK.GetLastError() & 0x7fffffff);
        LogUtil.debugLog("DeviceAddHelper", "error:" + error);
        return error;
    }


    public interface BindNetworkListener{
        void onBindWifiListener();
    }

    /**
     * The mobile phone is specified to use the wifi link
     *
     * 指定手机走wifi链路
     */
    public static void bindNetwork(final BindNetworkListener bindNetworkListener){
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                ProviderManager.getAppProvider().getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new NetworkRequest.Builder();
            //set the transport type do WIFI
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            connectivityManager.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {

                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onAvailable(Network network) {
                    LogUtil.debugLog(DeviceAddActivity.TAG, "bindNetwork succuss");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectivityManager.bindProcessToNetwork(null);
                        connectivityManager.bindProcessToNetwork(network);
                    } else {
                        //This method was deprecated in API level 23
                        ConnectivityManager.setProcessDefaultNetwork(null);
                        ConnectivityManager.setProcessDefaultNetwork(network);
                    }
                    connectivityManager.unregisterNetworkCallback(this);
                    if(bindNetworkListener != null){
                        bindNetworkListener.onBindWifiListener();
                    }
                }
            });
        }
    }

    /**
     * Note that the soft ap has called {@link #bindNetwork}. Release the call at a proper time; otherwise, the network cannot be connected to the external network
     *
     * 清除网络链路配置，注意软ap调用过{@link #bindNetwork}，需要在合适的时机释放，否则无法连外网
     */
    public static void clearNetWork(){
        LogUtil.debugLog(DeviceAddActivity.TAG, "clearNetWork succuss");
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                ProviderManager.getAppProvider().getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.bindProcessToNetwork(null);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //This method was deprecated in API level 23
            ConnectivityManager.setProcessDefaultNetwork(null);
        }
    }

    /**
     * Connect to the previous wifi
     *
     * 连接之前的wifi
     */
    public static void connectPreviousWifi(){
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        String previousSsid = deviceAddInfo.getPreviousSsid();
        if(!TextUtils.isEmpty(previousSsid)) {
            deviceAddInfo.setPreviousSsid("");
            DHWifiUtil wifiUtil = new DHWifiUtil(ProviderManager.getAppProvider().getAppContext());
            wifiUtil.connectWifi(previousSsid, "");
        }
    }


    //是否已连上设备热点
    public static boolean isConnectedDevHot(WifiUtil wifiUtil, String deviceId) {
        if (wifiUtil == null)
            return false;
        WifiInfo wifiInfo = wifiUtil.getCurrentWifiInfo();
        LogUtil.debugLog("29217", "isConnectedDevHot(TipSoftApConnectWifiPresenter.java:239)------->>" + (wifiInfo == null ? "wifiInfo == null" : wifiInfo.getSupplicantState()));

        if (wifiInfo == null || (SupplicantState.ASSOCIATED != wifiInfo.getSupplicantState() && SupplicantState.COMPLETED != wifiInfo.getSupplicantState())) {
            return false;
        } else {
            String ssid = DeviceAddHelper.getHotSSID(deviceId);
            if (DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceModel().equals("IPC Other")) {
                return wifiInfo.getSSID().contains(deviceId);
            } else {
                ssid = "\"" + ssid + "\"";
                LogUtil.debugLog("29217", "isConnectedDevHot(TipSoftApConnectWifiPresenter.java:249)------->>" + ssid + " " + wifiInfo.getSSID() + " " + wifiInfo.getSSID().equals(ssid));
                return wifiInfo.getSSID().equals(ssid);
            }
        }
    }

    public static String getHotSSID(String deviceId) {
        String wifiName = getWifiNameFromNet();    //OMS后台配置的wifi名称例如“K5-xxxx”
        String wifiNamePre = wifiName.substring(0, wifiName.lastIndexOf("-") + 1);  //取前缀例如“K5-”

        String ssid = wifiNamePre + deviceId;
        ssid = wifiNamePre + deviceId;
        return ssid;
    }

    public static String getWifiNameFromNet() {
        DeviceIntroductionInfo tips = DeviceAddModel.newInstance().getDeviceInfoCache().getDevIntroductionInfo();
        String wifiName = "";   //默认空字符串，防止空指针
        if (tips != null && tips.getStrInfos() != null) {
            wifiName = tips.getStrInfos().get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_WIFI_NAME);
        }
        return TextUtils.isEmpty(wifiName) ? "DAP-xxxx" : wifiName;
    }

    public static boolean isSupportCollectDeviceLog(int loginFuncMask) {
        byte[] s = getByteArray(loginFuncMask);
        // 功能掩码(包括未登录功能和需要提前引导的功能)
        // Bit0 - Wifi列表扫描及WLan设置（未登录功能）
        // Bit1 - 设备搜索支持会话外修改过期密码
        // Bit2 - 设备是否支持串口日志重定向（提前引导功能）
        return s[s.length - 3] == 1;
    }

    public static byte[] getByteArray(int b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }


}
