package com.mm.android.mobilecommon.businesstip;


public class SMBErrorCode {
    public static final int SUCCESS = 10000;    //成功

    //踢出相关错误码
    public static final int REQUEST_AUTHORITY_ERROR = 10001;    //鉴权服务异常
    public static final int REQUEST_INVALID_TOKEN = 10003;    //无效的登录信息
    public static final int REQUEST_LOGIN_EXPIRED = 10005;    //登录过期
    public static final int REQUEST_SIGNATURE_FORMAT_ERROR = 10015;    //接口签名类型不合规

}