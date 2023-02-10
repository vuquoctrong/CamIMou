package com.mm.android.deviceaddmodule.service;

import android.text.TextUtils;

import com.mm.android.deviceaddmodule.openapi.data.AddDevicePolicyData;
import com.mm.android.deviceaddmodule.openapi.data.BindDeviceData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceInfoBeforeBindData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceLeadingInfoData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceModelOrLeadingInfoCheckData;
import com.mm.android.deviceaddmodule.openapi.data.ModifyDeviceNameData;
import com.mm.android.deviceaddmodule.openapi.data.PolicyData;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceBindResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;

/**
 * Device adds module network protocol stack
 *
 * 设备添加模块网络协议栈
 */
public class DeviceAddService{
    private static final String TAG = "DeviceAddService";

    /**
     * openApi
     * Obtain device information before binding the device
     * @param deviceId        String Must be the device serial number
     * @param deviceCodeModel String Must be the model of two-dimensional code
     * @param deviceModelName String must be the device model name displayed by the APP (the product is called the market model, and the market model is selected when the user selects the device type)
     * @param ncCode          String Indicates the network distribution capability of the device
     * @param timeout  Timeout
     * @return DeviceAddInfo  Adding Device Information
     * @throws BusinessException
     *
     * openApi
     * 绑定设备前，获取设备信息
     * @param deviceId        String 必须 设备序列号
     * @param deviceCodeModel String 必须 二维码型号
     * @param deviceModelName String 必须 APP展示的设备型号名称(产品称之为市场型号,用户自己选择设备类型时选的是市场型号)
     * @param ncCode          String 必须 用于标识设备配网能力
     * @param timeout  超时时间
     * @return DeviceAddInfo  设备添加信息
     * @throws BusinessException
     */
    public DeviceAddInfo deviceInfoBeforeBind(String deviceId, String deviceCodeModel, String deviceModelName, String ncCode, String productId, int timeout) throws BusinessException {
        DeviceInfoBeforeBindData beforeBindData = new DeviceInfoBeforeBindData();
        beforeBindData.data.token = TokenHelper.getInstance().accessToken;
        beforeBindData.data.deviceId = deviceId;
        beforeBindData.data.productId = productId;
        beforeBindData.data.deviceCodeModel = deviceCodeModel;
        beforeBindData.data.deviceModelName = deviceModelName;
        beforeBindData.data.ncCode = ncCode;
        DeviceInfoBeforeBindData.Response response = DeviceAddOpenApiManager.deviceInfoBeforeBind(beforeBindData);
        return DeviceAddEntityChangeHelper.parse2DeviceAddInfo(response.data);
    }

    /**
     * openApi
     * Verify that the device model or device boot information configuration is updated
     * @param checkType       String Type that must be verified. DEVICE_MODEL: device model. DEVICE_LEADING_INFO: device boot information
     * @param deviceModelName String Optional device market model. If the checkType is DEVICE_LEADING_INFO, this parameter must be passed
     * @param updateTime      After the configuration is cached locally in the APP, the String must use the time returned from the last request to the configuration service to check whether the configuration needs to be updated
     * @param timeout
     * @return String  A string value
     * @throws BusinessException
     *
     * openApi
     * 校验设备型号或者设备引导信息配置信息是否更新
     * @param checkType       String 必须 要校验的类型，DEVICE_MODEL:设备产品型号信息；DEVICE_LEADING_INFO:设备引导信息
     * @param deviceModelName String 可选 设备市场型号，checkType为DEVICE_LEADING_INFO时要传
     * @param updateTime      String 必须 APP本地缓存了配置后，请求使用上次请求配置服务返回的时间，检查是否需要更新配置
     * @param timeout
     * @return String  字符串值
     * @throws BusinessException
     */
    public String deviceModelOrLeadingInfoCheck(String checkType, String deviceModelName, String updateTime, int timeout) throws BusinessException {
        DeviceModelOrLeadingInfoCheckData req=new DeviceModelOrLeadingInfoCheckData();
        req.data.token = TokenHelper.getInstance().accessToken;
        req.data.deviceModelName = deviceModelName;
        req.data.updateTime = updateTime;
        DeviceModelOrLeadingInfoCheckData.Response response = DeviceAddOpenApiManager.deviceModelOrLeadingInfoCheck(req);
        return response.data.isUpdated + "";
    }

    /**
     * openApi
     * Obtain the device add process guide page configuration information based on the device market model. The index is deviceModel_ language
     * @param deviceModel String Must be the device market model
     * @param timeout  Timeout
     * @return DeviceIntroductionInfo  Device Product Information
     * @throws BusinessException
     *
     * openApi
     * 根据设备市场型号获取设备添加流程引导页配置信息  以deviceModel_语言为索引
     * @param deviceModel String 必须 设备市场型号
     * @param timeout  超时时间
     * @return DeviceIntroductionInfo  设备产品信息
     * @throws BusinessException
     */
    public DeviceIntroductionInfo deviceLeadingInfo(String deviceModel, int timeout) throws BusinessException {
        DeviceLeadingInfoData req=new DeviceLeadingInfoData();
        req.data.token = TokenHelper.getInstance().accessToken;
        req.data.deviceModelName = deviceModel;
        DeviceLeadingInfoData.Response response = DeviceAddOpenApiManager.deviceLeadingInfo(req);
        FileSaveHelper.saveToJsonInfo(response.body, deviceModel + "_" + "zh_CN" + "_" + FileSaveHelper.INTRODUCTION_INFO_NAME);
        return DeviceAddEntityChangeHelper.parse2DeviceIntroductionInfo(response.data);
    }

    /**
     * Get the device addition process guide page configuration information based on the device market Model. Get this interface from the local cache is not a network interaction. It needs to be removed and put into the model
     * @param deviceModel  Device Model
     * @return DeviceIntroductionInfo  Device Product Information
     * @throws BusinessException
     *
     * 根据设备市场型号获取设备添加流程引导页配置信息，从本地缓存获取 这个接口不属于网络交互，需要移出去，放到Model里面
     * @param deviceModel  设备模型
     * @return DeviceIntroductionInfo  设备产品信息
     * @throws BusinessException
     */
    public DeviceIntroductionInfo introductionInfosGetCache(String deviceModel) throws BusinessException {
        return FileSaveHelper.getIntroductionInfoCache(deviceModel, "zh_CN");
    }

    /**
     * openApi
     * Binding User Devices
     * @param deviceId  String Must Device Serial Number
     * @param code      Must Device verification code
     *                  code is collectively referred to as the device verification code, but the code value transmitted to different devices varies. Check whether the device to be bound has the auth capability.
     *                  1. If the device has the auth capability level, the code value is transmitted to the device password after initialization.
     *                  2. If the device does not have auth capability level but there is a security code with 6 digits in the bottom label of the device (or in the two-dimensional code), the code value is transmitted to the 6-digit number;
     *                  3. If the device does not have auth capability level and there is no security code with number 6 in the label at the bottom of the device (or in the two-dimensional code), the code value can be passed blank.
     *                  Only paas devices can be bound. Therefore, only the device password or SC code can be transmitted in code
     * @param timeout  Timeout
     * @return DeviceBindResult  Device Bind Result
     * @throws BusinessException
     *
     * openApi
     * 绑定用户设备
     * @param deviceId  String 必须 设备序列号
     * @param code      必须 设备验证码
     *                  code统称为设备验证码，但是针对不同的设备传的code值也会不一样。需要判断所需绑定设备是否有auth能力级：
     *                  1.如果该设备有auth能力级，code值传设备初始化后的设备密码；
     *                  2.如果该设备没有auth能力级但是设备底部标签中（或二维码中）有6为数字的安全码，code值传该6位数字；
     *                  3.如果该设备没有auth能力级并且设备底部标签中（或二维码中）没有6为数字的安全码，则code值传空即可；
     *                  只支持绑定paas设备，故code只会传设备密码或者SC码
     * @param timeout  超时时间
     * @return DeviceBindResult  设备绑定结果
     * @throws BusinessException
     */
    public DeviceBindResult userDeviceBind(String deviceId, String code, int timeout) throws BusinessException {
        BindDeviceData req=new BindDeviceData();
        req.data.token= TokenHelper.getInstance().accessToken;
        req.data.deviceId = deviceId;
        req.data.code = code;
        BindDeviceData.Response response = DeviceAddOpenApiManager.userDeviceBind(req);
        DeviceBindResult deviceBindResult = new DeviceBindResult();
        deviceBindResult.setBindStatus(response.data.bindStatus);
        deviceBindResult.setDeviceName(response.data.deviceName);
        deviceBindResult.setUserAccount(response.data.userAccount);
        return deviceBindResult;
    }

    /**
     * openApi
     * Change the device name or channel name. If the channelId is empty, the device name is changed. If the channel name is not empty, the channel name is changed
     * @param deviceId  String   Must   Device serial number
     * @param channelId String   Optional   Device channel ID
     * @param name      String   Must   Device Name
     * @param timeout  Timeout
     * @return boolean  Boolean value
     * @throws BusinessException
     *
     * openApi
     * 修改设备或者通道名，channelId为空则为修改设备名，不为空为修改通道名
     * @param deviceId  String 必须 设备序列号
     * @param channelId String 可选 设备通道号
     * @param name      String 必须 设备名称
     * @param timeout  超时时间
     * @return boolean  布尔值
     * @throws BusinessException
     */
    public boolean modifyDeviceName(String deviceId, String channelId, String name, int timeout) throws BusinessException {
        ModifyDeviceNameData req=new ModifyDeviceNameData();
        req.data.token = TokenHelper.getInstance().subAccessToken;
        req.data.deviceId = deviceId;
        if (!TextUtils.isEmpty(channelId)) {
            req.data.channelId = channelId;
        }
        req.data.name = name;
        ModifyDeviceNameData.Response response = DeviceAddOpenApiManager.modifyDeviceName(req);
        return response != null;
    }

    public boolean addPolicyDevice(String deviceId,int timeout) throws BusinessException{
        AddDevicePolicyData req = new AddDevicePolicyData();
        req.params.openid = TokenHelper.getInstance().openid;
        req.params.token = TokenHelper.getInstance().accessToken;

        PolicyData.StateMent stateMent = new PolicyData.StateMent();
        stateMent.permission="DevControl";
        StringBuffer paramStr = new StringBuffer();
        paramStr.append("dev:").append(deviceId);
        stateMent.resource.add(paramStr.toString());
        req.params.policy.statement.add(stateMent);
        boolean result = DeviceAddOpenApiManager.addPolicy(req);
        return result;
    }
}
