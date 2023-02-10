package com.mm.android.mobilecommon.utils;

import com.mm.android.mobilecommon.businesstip.SMBErrorCode;

public class BusinessAuthUtil {

    //被踢出的相关错误码
    private static int [] errorCodesInt = new int[]{SMBErrorCode.REQUEST_AUTHORITY_ERROR, SMBErrorCode.REQUEST_INVALID_TOKEN, SMBErrorCode.REQUEST_LOGIN_EXPIRED, SMBErrorCode.REQUEST_SIGNATURE_FORMAT_ERROR};//客户端转换，两组 对应

    public static boolean isAuthFailed(int codeFromHandle) {
        for (int errorCode : errorCodesInt){
            if( codeFromHandle == errorCode){
                return true;
            }
        }
        return false;
    }
}
