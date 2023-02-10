package com.mm.android.mobilecommon.route

import com.alibaba.android.arouter.launcher.ARouter

object ProviderManager {

    @JvmStatic
    fun getPreviewProvider(): IPreviewProvider? {
        return ARouter.getInstance().build(RoutePathManager.ActivityPath.MEDIA_PREVIEW_PROVIDER)
            .navigation() as? IPreviewProvider
    }

    @JvmStatic
    fun getPlaybackProvider(): IPlaybackProvider? {
        return ARouter.getInstance().build(RoutePathManager.ActivityPath.MEDIA_PLAYBACK_PROVIDER)
            .navigation() as? IPlaybackProvider
    }

    @JvmStatic
    fun getDeviceDetailProvider(): IDeviceDetailProvider? {
        return ARouter.getInstance().build(RoutePathManager.ActivityPath.DEVICE_DETAIL_ACTIVITY)
            .navigation() as? IDeviceDetailProvider
    }
}