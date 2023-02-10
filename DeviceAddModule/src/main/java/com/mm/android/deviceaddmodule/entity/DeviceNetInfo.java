package com.mm.android.deviceaddmodule.entity;

import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;

/**
 * Encapsulation DEVICE_NET_INFO_EX
 *
 * 封装DEVICE_NET_INFO_EX
 */

public class DeviceNetInfo {
    private DeviceInitInfo mDevNetInfoEx;

    private boolean mIsValid;

    public DeviceNetInfo(DeviceInitInfo devNetInfoEx) {
        mDevNetInfoEx = devNetInfoEx;
        mIsValid = true;
    }

    public DeviceInitInfo getDevNetInfoEx() {
        return mDevNetInfoEx;
    }

    public void setDevNetInfoEx(DeviceInitInfo devNetInfoEx) {
        this.mDevNetInfoEx = devNetInfoEx;
    }

    public boolean isValid() {
        return mIsValid;
    }

    public void setValid(boolean isValid) {
        this.mIsValid = isValid;
    }

    @Override
    public String toString() {
        return "byInitStatus:" + mDevNetInfoEx.mInitStatus + "bySpecialAbility:" + mDevNetInfoEx.mSpecialAbility;/*.append(":").append(mDevNetInfoEx)*/
    }
}
