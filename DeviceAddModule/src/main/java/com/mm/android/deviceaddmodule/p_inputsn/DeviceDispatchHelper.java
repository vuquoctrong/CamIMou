package com.mm.android.deviceaddmodule.p_inputsn;

public class DeviceDispatchHelper {
    private static boolean reAdd = false;
    public static void setReAdd(boolean reAdd){
        DeviceDispatchHelper.reAdd = reAdd;
    }

    public static boolean isReAdd(){
        return reAdd;
    }

    public static void reset(){
        reAdd = false;
    }
}
