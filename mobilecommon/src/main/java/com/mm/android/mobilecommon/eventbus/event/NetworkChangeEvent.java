package com.mm.android.mobilecommon.eventbus.event;


public class NetworkChangeEvent {
    public static boolean mIsShowPlayNetworkTip = false;

    public static boolean mIsShowShareNetworkTip = true;

    public static boolean mIsShowDownloadNetworkTip = true;

    public final String networkType;
    public final String lastNetworkType;
    public final boolean  isNetworkAvailable;
    public NetworkChangeEvent(String networkType, String lastNetworkType, boolean isNetworkAvailable){
        this.networkType = networkType;
        this.lastNetworkType = lastNetworkType;
        this.isNetworkAvailable = isNetworkAvailable;
    }
}
