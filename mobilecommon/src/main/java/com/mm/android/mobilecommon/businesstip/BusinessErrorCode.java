package com.mm.android.mobilecommon.businesstip;

public class BusinessErrorCode {
    private final static int BEC_COMMON_BASE = 0; // 通用

    private final static int BEC_DEVICE_BASE = 3000; // 设备管理模块

    /**
     * Unknown error
     *
     * 未知错误
     */
    public final static int BEC_COMMON_UNKNOWN = BEC_COMMON_BASE + 1; // 未知错误

    /**
     * The service null pointer is abnormal
     *
     * 业务空指针异常
     */
    public final static int BEC_COMMON_NULL_POINT = BEC_COMMON_BASE + 9;

    /**
     * Connection timeout exception
     *
     * 连接超时异常
     */
    public final static int BEC_COMMON_TIME_OUT = BEC_COMMON_BASE + 11;

    public static final int BEC_DEVICE_ADD_AP_UPPER_LIMIT = BEC_DEVICE_BASE + 65;
}