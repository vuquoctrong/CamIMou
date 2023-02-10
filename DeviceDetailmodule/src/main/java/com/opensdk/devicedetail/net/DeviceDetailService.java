package com.opensdk.devicedetail.net;

import android.os.Message;

import com.mm.android.deviceaddmodule.device_wifi.CurWifiInfo;
import com.mm.android.mobilecommon.openapi.data.TimeSlice;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack;
import com.opensdk.devicedetail.entity.DevSdStoreData;
import com.opensdk.devicedetail.entity.DeviceAlarmStatusData;
import com.opensdk.devicedetail.entity.DeviceChannelInfoData;
import com.opensdk.devicedetail.entity.DeviceModifyNameData;
import com.opensdk.devicedetail.entity.DeviceUnBindData;
import com.opensdk.devicedetail.entity.DeviceVersionListData;

import java.util.List;

public class DeviceDetailService {


    /**
     * Obtain the device version and upgradable information
     * @param deviceVersionListData  Device Version Set
     * @param deviceVersionCallBack  Device version callback method
     *
     * 获取设备版本和可升级信息
     * @param deviceVersionListData  设备版本集合
     * @param deviceVersionCallBack  设备版本回调方法
     */
    public void deviceVersionList(final DeviceVersionListData deviceVersionListData,
                                  final IGetDeviceInfoCallBack.IDeviceVersionCallBack deviceVersionCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceVersionCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceVersionCallBack.deviceVersion((DeviceVersionListData.Response) msg.obj);
                } else {
                    //失败
                    deviceVersionCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceVersionListData.Response response =
                            DeviceInfoOpenApiManager.deviceVersionList(deviceVersionListData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Change the name of a device or channel
     * @param deviceModifyNameData  Device Name Change
     * @param modifyDeviceCallBack  Modify the device name callback function
     *
     * 修改设备或通道名称
     * @param deviceModifyNameData  设备修改名称
     * @param modifyDeviceCallBack  修改设备名称回调函数
     */
    public void modifyDeviceName(final DeviceModifyNameData deviceModifyNameData,
                                 final IGetDeviceInfoCallBack.IModifyDeviceCallBack modifyDeviceCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (modifyDeviceCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    modifyDeviceCallBack.deviceModify((boolean) msg.obj);
                } else {
                    //失败
                    modifyDeviceCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    boolean b = DeviceInfoOpenApiManager.modifyDeviceName(deviceModifyNameData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, b).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * UnBundling Equipment
     * @param deviceUnBindData  Device UnBind Data
     * @param unbindDeviceCallBack  UnBind Device CallBack
     *
     * 解绑设备
     * @param deviceUnBindData  设备未绑定时的数据
     * @param unbindDeviceCallBack  未绑定设备回调函数
     */
    public void unBindDevice(final DeviceUnBindData deviceUnBindData,
                             final IGetDeviceInfoCallBack.IUnbindDeviceCallBack unbindDeviceCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (unbindDeviceCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    unbindDeviceCallBack.unBindDevice((boolean) msg.obj);
                } else {
                    //失败
                    unbindDeviceCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    boolean b = DeviceInfoOpenApiManager.unBindDevice(deviceUnBindData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, b).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    public void deletePermission(final String deviceId, final String channelId,
                                 final IGetDeviceInfoCallBack.IUnbindDeviceCallBack unbindDeviceCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (unbindDeviceCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    unbindDeviceCallBack.unBindDevice((boolean) msg.obj);
                } else {
                    //失败
                    unbindDeviceCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    boolean b = DeviceInfoOpenApiManager.deletePermission(deviceId, channelId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, b).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Obtain detailed information about a single device channel
     * @param deviceChannelInfoData  Device Channel Information
     * @param deviceChannelInfoCallBack  Device channel information callback function
     *
     * 单个设备通道的详细信息获取
     * @param deviceChannelInfoData  设备通道信息
     * @param deviceChannelInfoCallBack  设备通道信息回调函数
     */
    public void bindDeviceChannelInfo(final DeviceChannelInfoData deviceChannelInfoData,
                                      final IGetDeviceInfoCallBack.IDeviceChannelInfoCallBack deviceChannelInfoCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceChannelInfoCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceChannelInfoCallBack.deviceChannelInfo((DeviceChannelInfoData.Response) msg.obj);
                } else {
                    //失败
                    deviceChannelInfoCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceChannelInfoData.Response response =
                            DeviceInfoOpenApiManager.bindDeviceChannelInfo(deviceChannelInfoData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Set the dynamic check switch
     * @param deviceAlarmStatusData  Device dynamic check status data
     * @param deviceAlarmStatusCallBack  Device check status callback function
     *
     * 设置动检开关
     * @param deviceAlarmStatusData  设备动检状态数据
     * @param deviceAlarmStatusCallBack  设备动检状态回调函数
     */
    public void modifyDeviceAlarmStatus(final DeviceAlarmStatusData deviceAlarmStatusData,
                                        final IGetDeviceInfoCallBack.IDeviceAlarmStatusCallBack deviceAlarmStatusCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceAlarmStatusCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceAlarmStatusCallBack.deviceAlarmStatus((boolean) msg.obj);
                } else {
                    //失败
                    deviceAlarmStatusCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    boolean b = DeviceInfoOpenApiManager.modifyDeviceAlarmStatus(deviceAlarmStatusData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, b).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Device upgrades
     * @param deviceId  Device Id
     * @param deviceUpdateCallBack  Device update callback function
     *
     * 设备升级
     * @param deviceId  设备Id
     * @param deviceUpdateCallBack  设备更新回调函数
     */
    public void upgradeDevice(final String deviceId,
                              final IGetDeviceInfoCallBack.IDeviceUpdateCallBack deviceUpdateCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceUpdateCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceUpdateCallBack.deviceUpdate((boolean) msg.obj);
                } else {
                    //失败
                    deviceUpdateCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() {
                boolean b = false;
                try {
                    b = DeviceInfoOpenApiManager.upgradeDevice(deviceId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, b).sendToTarget();
                } catch (BusinessException e) {
                    e.printStackTrace();
                    handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION, e).sendToTarget();
                }


            }
        };
    }

    /**
     * Information about the current connection hotspot of the device
     * @param deviceId  Device Id
     * @param deviceCurrentWifiInfoCallBack  Callback function for current Wifi information of the device
     *
     * 设备当前连接热点信息
     * @param deviceId  设备Id
     * @param deviceCurrentWifiInfoCallBack  设备当前Wifi信息回调函数
     */
    public void currentDeviceWifi(final String deviceId,
                                  final IGetDeviceInfoCallBack.IDeviceCurrentWifiInfoCallBack deviceCurrentWifiInfoCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceCurrentWifiInfoCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceCurrentWifiInfoCallBack.deviceCurrentWifiInfo((CurWifiInfo) msg.obj);
                } else {
                    //失败
                    deviceCurrentWifiInfoCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    CurWifiInfo curWifiInfo = DeviceAddOpenApiManager.currentDeviceWifi(deviceId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, curWifiInfo).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Obtain the defense time range
     * @param deviceId  Device Id
     * @param channelId  Channel Id
     * @param planCallBack  Deploy the callback function
     *
     * 布防时间段获取
     * @param deviceId  设备Id
     * @param channelId   通道Id
     * @param planCallBack  布防回调函数
     */
    public void deviceAlarmPlan(final String deviceId, final String channelId,
                                final IGetDeviceInfoCallBack.IDeviceAlarmPlanCallBack<List<TimeSlice>> planCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (planCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    planCallBack.getDeviceAlarmPlan((List<TimeSlice>) msg.obj);
                } else {
                    //失败
                    planCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    List<TimeSlice> list = DeviceAddOpenApiManager.deviceAlarmPlan(deviceId, channelId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, list).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Change the defense schedule
     * @param deviceId  Device Id
     * @param channelId  Channel Id
     * @param planCallBack  Deploy the callback function
     *
     * 布防时间段修改
     * @param deviceId  设备Id
     * @param channelId   通道Id
     * @param planCallBack  布防回调函数
     */
    public void modifyDeviceAlarmPlan(final String deviceId, final String channelId, final List<TimeSlice> rules,
                                      final IGetDeviceInfoCallBack.ICommon<Boolean> planCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (planCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    planCallBack.onCommonBack((Boolean) msg.obj);
                } else {
                    //失败
                    planCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    boolean res = DeviceAddOpenApiManager.modifyDeviceAlarmPlan(deviceId, channelId, rules);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, res).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Obtain the SD card status
     * @param deviceId       Device Id
     * @param statusCallBack Callback Function
     *
     * 获取SD卡状态
     * @param deviceId       设备Id
     * @param statusCallBack 回调函数
     */
    public void getDeviceSdCardStatus(final String deviceId, final IGetDeviceInfoCallBack.ICommon<String> statusCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {

            @Override
            public void handleBusiness(Message msg) {
                if (statusCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    statusCallBack.onCommonBack(String.valueOf(msg.obj));
                } else {
                    statusCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                String status = DeviceInfoOpenApiManager.deviceSdcardStatus(deviceId);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, status).sendToTarget();
            }
        };
    }

    /**
     * Obtain device storage information
     * @param deviceId        Device Id
     * @param storeCallBack   Callback Function
     *
     * 获取设备存储信息
     * @param deviceId        设备Id
     * @param storeCallBack   回调函数
     */
    public void getDeviceStoreInfo(final String deviceId, final IGetDeviceInfoCallBack.ICommon<DevSdStoreData> storeCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (storeCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    storeCallBack.onCommonBack((DevSdStoreData) msg.obj);
                } else {
                    storeCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                DevSdStoreData devSdStoreData = DeviceInfoOpenApiManager.getDeviceStoreInfo(deviceId);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, devSdStoreData).sendToTarget();

            }
        };
    }

    /**
     * Obtain device storage information
     * @param deviceId      Device Id
     * @param channelId     Channel Id
     * @param storeCallBack Callback Function
     *
     * 获取当前设备的云服务信息
     * @param deviceId      设备Id
     * @param channelId     通道Id
     * @param storeCallBack 回调函数
     */
    public void getDeviceCloudStatus(final String deviceId, final String channelId, final IGetDeviceInfoCallBack.ICommon<Integer> storeCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (storeCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    storeCallBack.onCommonBack((int) msg.obj);
                } else {
                    storeCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                int state = DeviceInfoOpenApiManager.queryCloudUse(deviceId, channelId);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, state).sendToTarget();

            }
        };
    }

    /**
     * Obtain device storage information
     * @param deviceId      Device Id
     * @param channelId     Channel Id
     * @param status        Status
     * @param storeCallBack Callback Function
     *
     * 设置当前设备的云存储服务开关
     * @param deviceId      设备Id
     * @param channelId     通道Id
     * @param status        云服务状态（开/关）
     * @param storeCallBack 回调函数
     */
    public void setAllStorageStrategy(final String deviceId, final String channelId, String status, final IGetDeviceInfoCallBack.ICommon<Object> storeCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (storeCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    storeCallBack.onCommonBack(msg.obj);
                } else {
                    storeCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                DeviceInfoOpenApiManager.setAllStorageStrategy(deviceId, channelId, status);
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();

            }
        };
    }
}
