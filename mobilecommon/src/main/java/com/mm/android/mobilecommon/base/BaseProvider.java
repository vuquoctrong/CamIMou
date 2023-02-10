package com.mm.android.mobilecommon.base;

import com.mm.android.mobilecommon.utils.LogUtil;

public class BaseProvider implements IBaseProvider {
    public static final String TAG = "BaseProvider";
    @Override
    public void uninit() {
        LogUtil.debugLog(TAG,"unint");
    }

}
