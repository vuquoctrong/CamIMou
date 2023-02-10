package com.lc.media.api;

import android.os.Message;

import com.lc.media.entity.CloudRecordsData;
import com.lc.media.entity.LocalRecordsData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;

public class DeviceRecordService {
    private volatile static DeviceRecordService deviceRecordService;

    public static DeviceRecordService newInstance() {
        if (deviceRecordService == null) {
            synchronized (DeviceRecordService.class) {
                if (deviceRecordService == null) {
                    deviceRecordService = new DeviceRecordService();
                }
            }
        }
        return deviceRecordService;
    }


    /**
     * Query device cloud video clips in reverse order
     * @param cloudRecordsData  Cloud video data
     * @param cloudRecordCallBack  Cloud video callback
     *
     * 倒序查询设备云录像片段
     * @param cloudRecordsData  云录像数据
     * @param cloudRecordCallBack  云录像回调
     */
    public void getCloudRecords(final CloudRecordsData cloudRecordsData, final IGetDeviceInfoCallBack.IDeviceCloudRecordCallBack cloudRecordCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (cloudRecordCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    cloudRecordCallBack.deviceCloudRecord((CloudRecordsData.Response) msg.obj);
                } else {
                    //失败
                    cloudRecordCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    CloudRecordsData.Response response = DeviceInfoOpenApiManager.getCloudRecords(cloudRecordsData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Querying device device video clips
     * @param localRecordsData  Local video data
     * @param deviceLocalRecordCallBack  Callback of local video recording
     *
     * 查询设备设备录像片段
     * @param localRecordsData  本地录像数据
     * @param deviceLocalRecordCallBack  设备本地录像回调
     */
    public void queryLocalRecords(final LocalRecordsData localRecordsData, final IGetDeviceInfoCallBack.IDeviceLocalRecordCallBack deviceLocalRecordCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (deviceLocalRecordCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    deviceLocalRecordCallBack.deviceLocalRecord((LocalRecordsData.Response) msg.obj);
                } else {
                    //失败
                    deviceLocalRecordCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    LocalRecordsData.Response response = DeviceInfoOpenApiManager.queryLocalRecords(localRecordsData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    public void queryCloudUse(final String deviceId, final String channelId, final IGetDeviceInfoCallBack.ICommon iCommon) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (iCommon == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    iCommon.onCommonBack(msg.obj);
                } else {
                    //失败
                    iCommon.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    int response = DeviceInfoOpenApiManager.queryCloudUse(deviceId, channelId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    public void querySDUse(final String deviceId, final IGetDeviceInfoCallBack.ICommon iCommon) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (iCommon == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    iCommon.onCommonBack(msg.obj);
                } else {
                    //失败
                    iCommon.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    String response = DeviceInfoOpenApiManager.querySDState(deviceId);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    /**
     * Movement Control Interface (V2 version)
     * @param controlMovePTZData Move control PTZ data
     *
     * 云台移动控制接口（V2版本）
     * @param controlMovePTZData  移动控制PTZ数据
     */
    public void controlMovePTZ(final ControlMovePTZData controlMovePTZData) {
        new BusinessRunnable(new LCBusinessHandler() {

            @Override
            public void handleBusiness(Message msg) {
            }
        }) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    DeviceInfoOpenApiManager.controlMovePTZ(controlMovePTZData);
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

}
