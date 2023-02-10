package com.mm.android.deviceaddmodule.model;

import android.os.Handler;
import android.text.TextUtils;

import com.company.NetSDK.CFG_NETAPP_WLAN;
import com.company.NetSDK.EM_LOGIN_SPAC_CAP_TYPE;
import com.company.NetSDK.EM_WLAN_SCAN_AND_CONFIG_TYPE;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.company.NetSDK.NET_IN_GET_DEV_WIFI_LIST;
import com.company.NetSDK.NET_IN_INIT_DEVICE_ACCOUNT;
import com.company.NetSDK.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY;
import com.company.NetSDK.NET_IN_SET_DEV_WIFI;
import com.company.NetSDK.NET_IN_WLAN_ACCESSPOINT;
import com.company.NetSDK.NET_OUT_GET_DEV_WIFI_LIST;
import com.company.NetSDK.NET_OUT_INIT_DEVICE_ACCOUNT;
import com.company.NetSDK.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY;
import com.company.NetSDK.NET_OUT_SET_DEV_WIFI;
import com.company.NetSDK.NET_OUT_WLAN_ACCESSPOINT;
import com.company.NetSDK.NET_WLAN_ACCESSPOINT_INFO;
import com.company.NetSDK.SDKDEV_NETINTERFACE_INFO;
import com.company.NetSDK.SDKDEV_WLAN_DEVICE_LIST_EX;
import com.company.NetSDK.SDK_PRODUCTION_DEFNITION;
import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceBindResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.deviceaddmodule.service.DeviceAddService;

import java.util.ArrayList;
import java.util.List;

import static com.mm.android.deviceaddmodule.helper.DeviceAddHelper.printError;

/**
 * Device add data request class, because the device add page process is single, here is designed as a singleton
 *
 * 设备添加数据请求类,由于设备添加页面流程单一，这里设计为单例
 **/
public class DeviceAddModel implements IDeviceAddModel {
    private static final int NET_ERROR_DEVICE_ALREADY_INIT = 1017;         // 设备已经初始化
    private volatile static DeviceAddModel deviceAddModel;
    DeviceAddInfo mDeviceAddInfo;
    final int DMS_TIMEOUT = 45 * 1000;
    int TIME_OUT = 10 * 1000;
    boolean loop = true;                 //获取设备信息轮询标志变量
    boolean middleTimeUp = false;      //设定的中间时间时间到，此时间之后如设备还是已在服务注册，且不在线以及仍为P2P类型，走绑定流程
    int LOOP_ONCE_TIME = 3 * 1000;            //轮询间隔时间3S

    DeviceAddService deviceAddService;

    private DeviceAddModel() {
        deviceAddService = new DeviceAddService();
    }

    public static DeviceAddModel newInstance() {
        if (deviceAddModel == null) {
            synchronized (DeviceAddModel.class) {
                if (deviceAddModel == null) {
                    deviceAddModel = new DeviceAddModel();
                }
            }
        }
        return deviceAddModel;
    }

    public void updateDeviceCache(DeviceAddInfo deviceAddInfo) {
        mDeviceAddInfo.setDeviceExist(deviceAddInfo.getDeviceExist());
        mDeviceAddInfo.setBindStatus(deviceAddInfo.getBindStatus());
        mDeviceAddInfo.setBindAcount(deviceAddInfo.getBindAcount());
        mDeviceAddInfo.setAccessType(deviceAddInfo.getAccessType());
        mDeviceAddInfo.setStatus(deviceAddInfo.getStatus());
        mDeviceAddInfo.setConfigMode(deviceAddInfo.getConfigMode());
        mDeviceAddInfo.setBrand(deviceAddInfo.getBrand());
        mDeviceAddInfo.setFamily(deviceAddInfo.getFamily());
        mDeviceAddInfo.setDeviceModel(deviceAddInfo.getDeviceModel());
        mDeviceAddInfo.setModelName(deviceAddInfo.getModelName());
        mDeviceAddInfo.setCatalog(deviceAddInfo.getCatalog());
        mDeviceAddInfo.setAbility(deviceAddInfo.getAbility());
        mDeviceAddInfo.setType(deviceAddInfo.getType());
        mDeviceAddInfo.setWifiTransferMode(deviceAddInfo.getWifiTransferMode());
        mDeviceAddInfo.setChannelNum(deviceAddInfo.getChannelNum());
        mDeviceAddInfo.setWifiConfigModeOptional(deviceAddInfo.isWifiConfigModeOptional());
        mDeviceAddInfo.setSupport(deviceAddInfo.isSupport());
        if (!TextUtils.isEmpty(deviceAddInfo.getProductId())) {
            mDeviceAddInfo.setProductId(deviceAddInfo.getProductId());
        }
        mDeviceAddInfo.setDefaultWifiConfigMode(deviceAddInfo.getDefaultWifiConfigMode());
    }

    public void updateDeviceAllCache(DeviceAddInfo deviceAddInfo) {
        mDeviceAddInfo.setDeviceExist(deviceAddInfo.getDeviceExist());
        mDeviceAddInfo.setBindStatus(deviceAddInfo.getBindStatus());
        mDeviceAddInfo.setBindAcount(deviceAddInfo.getBindAcount());
        mDeviceAddInfo.setAccessType(deviceAddInfo.getAccessType());
        mDeviceAddInfo.setStatus(deviceAddInfo.getStatus());
        mDeviceAddInfo.setConfigMode(deviceAddInfo.getConfigMode());
        mDeviceAddInfo.setBrand(deviceAddInfo.getBrand());
        mDeviceAddInfo.setFamily(deviceAddInfo.getFamily());
        mDeviceAddInfo.setDeviceModel(deviceAddInfo.getDeviceModel());
        mDeviceAddInfo.setModelName(deviceAddInfo.getModelName());
        mDeviceAddInfo.setCatalog(deviceAddInfo.getCatalog());
        mDeviceAddInfo.setAbility(deviceAddInfo.getAbility());
        mDeviceAddInfo.setType(deviceAddInfo.getType());
        mDeviceAddInfo.setWifiTransferMode(deviceAddInfo.getWifiTransferMode());
        mDeviceAddInfo.setChannelNum(deviceAddInfo.getChannelNum());
        mDeviceAddInfo.setWifiConfigModeOptional(deviceAddInfo.isWifiConfigModeOptional());
        mDeviceAddInfo.setSupport(deviceAddInfo.isSupport());

        mDeviceAddInfo.setDeviceSn(deviceAddInfo.getDeviceSn());
        mDeviceAddInfo.setDeviceCodeModel(deviceAddInfo.getDeviceCodeModel());
        mDeviceAddInfo.setRegCode(deviceAddInfo.getRegCode());
        mDeviceAddInfo.setSc(deviceAddInfo.getSc());
        mDeviceAddInfo.setNc(deviceAddInfo.getNc());  // 将16进制的字符串转换为数字
        // 支持SC码的设备，使用SC码作为设备密码
        mDeviceAddInfo.setDevicePwd(deviceAddInfo.getSc());

        String wifiConfigMode;
        if (TextUtils.isEmpty(deviceAddInfo.getConfigMode())) { // 若配网模式未返回，默认可进行有线无线切换 V5.1默认增加软AP
            wifiConfigMode = DeviceAddInfo.ConfigMode.SmartConfig.name() + ","
                    + DeviceAddInfo.ConfigMode.LAN.name() + ","
                    + DeviceAddInfo.ConfigMode.SoundWave.name() + ","
                    + DeviceAddInfo.ConfigMode.SoftAP.name();
        } else {
            wifiConfigMode = deviceAddInfo.getConfigMode();
        }
        mDeviceAddInfo.setConfigMode(wifiConfigMode);
    }

    @Override
    public void getDeviceInfo(final String sn, final String deviceCodeModel, final String deviceModelName,
                              final String productId, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceAddInfo deviceAddInfo = deviceAddService.deviceInfoBeforeBind(sn, deviceCodeModel, deviceModelName, mDeviceAddInfo.getNc(), productId, TIME_OUT);
                    updateDeviceCache(deviceAddInfo);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, mDeviceAddInfo).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    @Override
    public void getDeviceInfoLoop(final String sn, final String model, final String productId, final int timeout, final Handler handler) {
        loop = true;
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                while (loop) {
                    if (canInterruptLoop()) {
                        break;
                    }

                    DeviceAddInfo deviceAddInfo = deviceAddService.deviceInfoBeforeBind(sn, mDeviceAddInfo.getDeviceCodeModel(), model, mDeviceAddInfo.getNc(), productId, TIME_OUT);
                    updateDeviceCache(deviceAddInfo);
                    if (canInterruptLoop()) {
                        break;
                    }
                    try {//间隔3S查询一次
                        Thread.sleep(LOOP_ONCE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (loop)
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
            }
        };
    }

    /**
     * If the conditions for interrupting polling are met, you can follow the binding process
     * @return boolean  Boolean value
     *
     * 是否满足中断轮询的条件，可以走绑定流程
     * @return boolean  布尔值
     */
    private boolean canInterruptLoop() {
        String status = mDeviceAddInfo.getStatus();
        if (TextUtils.isEmpty(status)) {
            status = DeviceAddInfo.Status.offline.name();
        }
        if (mDeviceAddInfo.isDeviceInServer() &&
                (DeviceAddInfo.Status.online.name().equals(status)
                        || (DeviceAddInfo.Status.offline.name().equals(status) && mDeviceAddInfo.isP2PDev() && middleTimeUp))) {
            //以下两种情况走绑定流程：
            // 1.已注册上服务并且设备DMS在线
            // 2.已注册上服务，设备DMS不在线，设备类型为P2P（此时无法区分设备到底是P2P设备还是PAAS设备），并且设定的最大等待时间已到）
            return true;
        }
        return false;
    }

    @Override
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    public void setMiddleTimeUp(boolean middleTimeUp) {
        this.middleTimeUp = middleTimeUp;
    }

    @Override
    public DeviceAddInfo getDeviceInfoCache() {
        if (mDeviceAddInfo == null) {
            mDeviceAddInfo = new DeviceAddInfo();
            LogUtil.debugLog("DeviceAddModel", "getDeviceInfoCache");
        }
        return mDeviceAddInfo;
    }

    @Override
    public void checkDevIntroductionInfo(final String deviceModelName, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                String lan = ProviderManager.getAppProvider().getAppLanguage();
                DeviceIntroductionInfo deviceIntroductionInfo = deviceAddService.introductionInfosGetCache(deviceModelName);
                boolean result = true;
                if (deviceIntroductionInfo != null) {
                    mDeviceAddInfo.setDevIntroductionInfos(deviceIntroductionInfo);
                    if (!TextUtils.isEmpty(deviceIntroductionInfo.getUpdateTime())) {
                        String resultStr = deviceAddService.deviceModelOrLeadingInfoCheck("DEVICE_LEADING_INFO", deviceModelName, deviceIntroductionInfo.getUpdateTime(), TIME_OUT);
                        result = resultStr.equalsIgnoreCase("true");
                    }
                }
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, result ? null : deviceIntroductionInfo).sendToTarget();
            }
        };
    }

    @Override
    public void getDevIntroductionInfo(final String deviceModelName, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                String lan = ProviderManager.getAppProvider().getAppLanguage();
                String modelName = mDeviceAddInfo.getModelName();
                if (TextUtils.isEmpty(modelName)) {
                    modelName = deviceModelName;
                }
                DeviceIntroductionInfo introductionInfos = deviceAddService.deviceLeadingInfo(modelName, TIME_OUT);
                mDeviceAddInfo.setDevIntroductionInfos(introductionInfos);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
            }
        };
    }

    @Override
    public void getDevIntroductionInfoCache(final String deviceModelName, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                String lan = ProviderManager.getAppProvider().getAppLanguage();
                String modelName = mDeviceAddInfo.getModelName();
                if (TextUtils.isEmpty(modelName)) {
                    modelName = deviceModelName;
                }
                DeviceIntroductionInfo introductionInfos = deviceAddService.introductionInfosGetCache(modelName);
                mDeviceAddInfo.setDevIntroductionInfos(introductionInfos);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
            }
        };
    }

    @Override
    public void deviceIPLogin(final String ip, final String devPwd, final LCOpenSDK_DeviceInit.ILogInDeviceListener listener) {
        LCOpenSDK_DeviceInit.getInstance().deviceIPLogin(ip,devPwd,listener);
    }


    @Override
    public void modifyDeviceName(final String deviceId, final String channelId, final String name, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                boolean isSucceed = deviceAddService.modifyDeviceName(deviceId, channelId, name, TIME_OUT);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, isSucceed).sendToTarget();
            }
        };
    }

    @Override
    public void addApDevice(final String deviceId, final String apId, final String apType, final String apModel, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                //TODO 配件绑定需要实现接口，当前版本不支持

            }
        };
    }

    @Override
    public void modifyAPDevice(final String deviceId, final String apId, final String apName, final boolean toDevice, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                //TODO 配件绑定需要实现接口

            }
        };
    }

    @Override
    public void getAddApResultAsync(final String deviceId, final String apId, final Handler handler) {
        loop = true;
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                //TODO 配件绑定需要实现接口

            }
        };
    }


    @Override
    public void bindDevice(final String sn, final String devPwd, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                DeviceBindResult deviceBindResult = deviceAddService.userDeviceBind( sn, devPwd, DMS_TIMEOUT);
                mDeviceAddInfo.setDeviceDefaultName(deviceBindResult.getDeviceName());
                mDeviceAddInfo.setBindStatus(deviceBindResult.getBindStatus());
                mDeviceAddInfo.setBindAcount(deviceBindResult.getUserAccount());
                mDeviceAddInfo.setRecordSaveDays(deviceBindResult.getRecordSaveDays());
                mDeviceAddInfo.setStreamType(deviceBindResult.getStreamType());
                mDeviceAddInfo.setServiceTime(deviceBindResult.getServiceTime());
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
            }
        };
    }

    @Override
    public void addPolicy(final String sn, final Handler handler) {
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                boolean result = deviceAddService.addPolicyDevice(sn,DMS_TIMEOUT);
                if (result){
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
                }
            }
        };
    }

    /**
     * The add process is complete, and you need to call this method to free up the associated resources
     *
     * 添加流程完成，需调用此方法，以释放相关资源
     */
    public void recycle() {
        deviceAddModel.setLoop(false);
        mDeviceAddInfo = null;
        deviceAddModel = null;
    }
}
