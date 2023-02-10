package com.lc.media.contacts;

public class MethodConst {
    //获取设备版本和可升级信息
    public static String METHOD_LIST_DEVICE_DETAIL_BATCH = "listDeviceDetailsByIds";
    //单个设备通道的详细信息获取
    public static String METHOD_DEVICE_CHANNEL_INFO = "bindDeviceChannelInfo";
    //倒序查询设备云录像片段
    public static String METHOD_GET_CLOUND_RECORDS = "getCloudRecords";
    //查询设备设备录像片段
    public static String METHOD_QUERY_LOCAL_RECORD = "queryLocalRecords";
    //云台移动控制接口（V2版本）
    public static String METHOD_CONTROL_MOVE_PTZ = "controlMovePTZ";
    //删除设备云录像片段
    public static String METHOD_DELETE_CLOUND_RECORDS = "deleteCloudRecords";

    public static String SUB_ACCOUNT_DEVICE_LIST = "subAccountDeviceList";     // 老接口查询设备列表接口

    public static String QUERY_DEVICEDETAIL_PAGE = "listDeviceDetailsByPage";   //  新接口查询设备列表接口  V3.11.9

    public static String SD_STATUE_QUERY = "deviceSdcardStatus";
    public static String GET_DEVICE_CLOUD = "getDeviceCloud";



    public interface ParamConst {
        String deviceDetail = "deviceDetail";
        int recordTypeLocal = 2;
        int recordTypeCloud = 1;
    }
}
