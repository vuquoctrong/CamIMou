package com.mm.android.mobilecommon.AppConsume;

/**
 * Function Interface management
 *
 * 功能接口管理类
 */

public class ProviderManager {

    public static IApp getAppProvider() {
        return AppProvider.newInstance();
    }


    public static IDeviceAddCustom getDeviceAddCustomProvider() {
        return DeviceAddCustomImpl.newInstance();
    }
}
