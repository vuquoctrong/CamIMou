package com.mm.android.deviceaddmodule.model;

import android.os.Handler;

import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;

/**
 * 设备添加数据请求接口类
 **/
public interface IDeviceAddModel {
    public void getDeviceInfo(final String sn, final String deviceCodeModel, final String deviceModelName, final String productId, final Handler handler);  //从服务请求设备相关信息

    void getDeviceInfoLoop(String sn, String model, final String productId, int timeout, Handler handler);  //从服务请求设备相关信息,轮询设备是否已上线

    void setLoop(boolean loop);

    void setMiddleTimeUp(boolean middleTimeUp);

    DeviceAddInfo getDeviceInfoCache();         //获取本地缓存设备信息


    void checkDevIntroductionInfo(String deviceModelName,Handler handler);              //检查设备引导信息是否有更新

    void getDevIntroductionInfo(String deviceModelName,Handler handler);               //获取设备添加引导信息

    void getDevIntroductionInfoCache(String deviceModelName,Handler handler);               //获取设备添加引导信息本地缓存

    void deviceIPLogin(String ip, String devPwd, LCOpenSDK_DeviceInit.ILogInDeviceListener  listener);                                   //设备IP登录

    void modifyDeviceName(String deviceId, String channelId, String name, Handler handler);                //修改设备名

    //配件
    void addApDevice(String deviceId, String apId, String apType, String apModel, Handler handle);                  //添加配件

    void modifyAPDevice(String deviceId, String apId, String apName, boolean toDevice, Handler handle);               //修改配件名

    void getAddApResultAsync(String deviceId, String apId, Handler handle);          //同步添加结果

    /**
     * 绑定设备
     *
     * @param sn          设备序列号
     * @param devPwd      设备密码
     * @param handler
     */
    void bindDevice(String sn, String devPwd, Handler handler);

    void addPolicy(String sn,Handler handler);
}
