package com.mm.android.deviceaddmodule.provider;

import com.company.NetSDK.INetSDK;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.IDeviceAdd;
import com.mm.android.mobilecommon.base.BaseHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;

public class DeviceAddProvider implements IDeviceAdd {

    private volatile static DeviceAddProvider deviceAddProvider;

    public static DeviceAddProvider newInstance() {
        if (deviceAddProvider == null) {
            synchronized (DeviceAddProvider.class) {
                if (deviceAddProvider == null) {
                    deviceAddProvider = new DeviceAddProvider();
                }
            }
        }
        return deviceAddProvider;
    }

    @Override
    public void uninit() {

    }

    @Override
    public void stopSearchDevicesAsync(final long ret, final BaseHandler handler) {
        new BusinessRunnable(null) {
            @Override
            public void doBusiness() throws BusinessException {
                boolean result = INetSDK.StopSearchDevices(ret);
                if (handler != null)
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, result).sendToTarget();
            }
        };
    }

    @Override
    public boolean stopSearchDevices(long ret, String requestId) {
        boolean result = INetSDK.StopSearchDevices(ret);
        //搜索接口只报失败
        return result;
    }
}
