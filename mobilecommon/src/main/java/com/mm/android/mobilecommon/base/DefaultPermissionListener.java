package com.mm.android.mobilecommon.base;

import com.mm.android.mobilecommon.common.PermissionHelper;

/**
 * Default implementation class for dynamic permission callbacks
 *
 * 动态权限回调的默认实现类
 */
public abstract class DefaultPermissionListener  implements PermissionHelper.IPermissionListener {

    @Override
    public boolean onDenied() {
        return false;
    }

    @Override
    public void onGiveUp() {

    }
}
