package com.mm.android.mobilecommon.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static com.mm.android.mobilecommon.annotation.DeviceState.OFFLINE;
import static com.mm.android.mobilecommon.annotation.DeviceState.ONLINE;
import static com.mm.android.mobilecommon.annotation.DeviceState.SLEEP;
import static com.mm.android.mobilecommon.annotation.DeviceState.UPGRADE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Enumerates the value of the device when it is offline
 *
 * 设备在离线状态枚举值
 */
@Retention(SOURCE)
@StringDef({ONLINE, OFFLINE, SLEEP,UPGRADE, ""})
public @interface DeviceState {
       String ONLINE = "online";
       String OFFLINE = "offline";
       String SLEEP = "sleep";
       String UPGRADE = "upgrading";

}
