package com.mm.android.mobilecommon.openapi.data;

import com.mm.android.mobilecommon.utils.MD5Helper;
import com.mm.android.mobilecommon.openapi.CONST;


import java.io.Serializable;
import java.util.UUID;

public class SystemData implements Serializable {
    public String ver = "1.0";
    public String appId = CONST.APPID;
    public String sign;
    public long time;
    public String nonce= UUID.randomUUID().toString();

    public SystemData(){
        time = System.currentTimeMillis() / 1000;
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CONST.SECRET);
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
    }

}
