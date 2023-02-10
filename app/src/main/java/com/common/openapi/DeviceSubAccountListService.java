package com.common.openapi;

import android.os.Handler;
import android.os.Message;

import com.common.openapi.entity.DeviceDetailListData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;

public class DeviceSubAccountListService {
    public static final int pageSize = 10;

    public void getSubAccountDeviceList(final int pageNo, final IGetDeviceInfoCallBack.ISubAccountDevice<DeviceDetailListData.Response> getDeviceListCallBack) {
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (getDeviceListCallBack == null) {
                    return;
                }
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    getDeviceListCallBack.DeviceList((DeviceDetailListData.Response) msg.obj);
                } else {
                    //失败
                    getDeviceListCallBack.onError(BusinessErrorTip.throwError(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    getDeviceListAtSubAccount(handler, pageNo);
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }

    private void getDeviceListAtSubAccount(Handler handler, int pageNo) throws BusinessException {
        DeviceDetailListData.Response response = DeviceInfoOpenApiManager.getSubAccountDeviceListNew(pageNo, pageSize);
        handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
    }

}
