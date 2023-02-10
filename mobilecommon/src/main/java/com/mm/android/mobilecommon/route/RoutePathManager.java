package com.mm.android.mobilecommon.route;

public class RoutePathManager {
    private final static String ActivityPrefix = "activity/";

    //模块名
    private interface ModuleName {
        String COMMON_MODULE = "/app/";
        String USER_MODULE = "/usermodule/";
        String DETAIL_MODULE = "/DeviceDetailmodule/";
        String PREVIEW_PROVIDER = "/PreviewProvider/";
        String PLAYBACK_PROVIDER = "/PlaybackProvider/";
    }

    public interface ActivityPath {
        String DeviceListActivityPath = ModuleName.COMMON_MODULE + ActivityPrefix + "DeviceListActivity";
        String UserRegesiterPath = ModuleName.USER_MODULE + ActivityPrefix + "UserRegesiterActivity";
        String LoginActivityPath = ModuleName.USER_MODULE + ActivityPrefix + "LoginActivity";
        String DEVICE_DETAIL_ACTIVITY = ModuleName.DETAIL_MODULE + ActivityPrefix + "DeviceDetailActivity";

        String MEDIA_PREVIEW_PROVIDER = ModuleName.PREVIEW_PROVIDER + ActivityPrefix + "DeviceOnlineMediaPlayActivity";
        String MEDIA_PLAYBACK_PROVIDER = ModuleName.PLAYBACK_PROVIDER + ActivityPrefix + "DeviceRecordPlayActivity";

    }
}
