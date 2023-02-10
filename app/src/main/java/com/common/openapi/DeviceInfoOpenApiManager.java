package com.common.openapi;

import com.common.openapi.entity.DeviceDetailListData;
import com.google.gson.JsonObject;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.HttpSend;

import java.util.HashMap;

public class DeviceInfoOpenApiManager {

    private static int TIME_OUT = 10 * 1000;

    public static DeviceDetailListData.Response getSubAccountDeviceListNew(int pageNo, int pageSize) throws BusinessException {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", TokenHelper.getInstance().subAccessToken);
        paramsMap.put("page", pageNo);
        paramsMap.put("pageSize", pageSize);
        JsonObject json = HttpSend.execute(paramsMap, MethodConst.QUERY_DEVICEDETAIL_PAGE, TIME_OUT);
        DeviceDetailListData.Response response = new DeviceDetailListData.Response();
        response.parseData(json);
        return response;
    }

}
