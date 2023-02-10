package com.mm.android.deviceaddmodule.service;

import android.text.TextUtils;

import com.mm.android.deviceaddmodule.openapi.data.DeviceInfoBeforeBindData;
import com.mm.android.deviceaddmodule.openapi.data.DeviceLeadingInfoData;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;

import java.util.HashMap;

public class DeviceAddEntityChangeHelper {


    public static DeviceAddInfo parse2DeviceAddInfo(DeviceInfoBeforeBindData.ResponseData data) {
        DeviceAddInfo deviceAddInfo = new DeviceAddInfo();
        deviceAddInfo.setDeviceExist("exist".equals(data.deviceExist));
        deviceAddInfo.setBindStatus(data.bindStatus);
        deviceAddInfo.setBindAcount(data.userAccount);
        deviceAddInfo.setStatus(data.status);
        deviceAddInfo.setDeviceModel(data.deviceCodeModel);
        deviceAddInfo.setModelName(data.deviceModelName);
        deviceAddInfo.setCatalog(data.catalog);
        deviceAddInfo.setAbility(data.ability);
        deviceAddInfo.setSupport(data.support);
        String wifiConfigMode;
        if (TextUtils.isEmpty(data.wifiConfigMode)) { // 若配网模式未返回，默认可进行有线无线切换 V5.1默认增加软AP
            wifiConfigMode = DeviceAddInfo.ConfigMode.SmartConfig.name() + ","
                    + DeviceAddInfo.ConfigMode.LAN.name() + ","
                    + DeviceAddInfo.ConfigMode.SoundWave.name() + ","
                    + DeviceAddInfo.ConfigMode.SoftAP.name();
        } else {
            wifiConfigMode = data.wifiConfigMode;
        }
        deviceAddInfo.setConfigMode(wifiConfigMode);
        deviceAddInfo.setDefaultWifiConfigMode(data.defaultWifiConfigMode);
        deviceAddInfo.setWifiConfigModeOptional(data.wifiConfigModeOptional);
        deviceAddInfo.setWifiTransferMode(data.wifiTransferMode);
        deviceAddInfo.setType(data.type);
        deviceAddInfo.setProductId(data.productId);
        return deviceAddInfo;
    }

    public static DeviceIntroductionInfo parse2DeviceIntroductionInfo(DeviceLeadingInfoData.ResponseData data) {
        DeviceIntroductionInfo deviceIntroductionInfo = new DeviceIntroductionInfo();
        HashMap<String, String> imagesList = new HashMap<>();
        HashMap<String, String> strList = new HashMap<>();
        for (DeviceLeadingInfoData.ResponseData.ImagesElement imageElement : data.images) {
            imagesList.put(imageElement.imageName, imageElement.imageUrl);
        }
        for (DeviceLeadingInfoData.ResponseData.IntroductionsElement strElement : data.introductions) {
            strList.put(strElement.introductionName, strElement.introductionContent);
        }

        deviceIntroductionInfo.setUpdateTime(data.updateTime);
        deviceIntroductionInfo.setImageInfos(imagesList);
        deviceIntroductionInfo.setStrInfos(strList);
        return deviceIntroductionInfo;
    }
}
