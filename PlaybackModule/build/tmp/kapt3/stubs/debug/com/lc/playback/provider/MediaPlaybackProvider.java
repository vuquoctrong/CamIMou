package com.lc.playback.provider;

import java.lang.System;

@com.alibaba.android.arouter.facade.annotation.Route(path = "/PlaybackProvider/activity/DeviceRecordPlayActivity")
@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0012\u0010\u000f\u001a\u00020\u00062\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004H\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/lc/playback/provider/MediaPlaybackProvider;", "Lcom/mm/android/mobilecommon/route/IPlaybackProvider;", "()V", "context", "Landroid/content/Context;", "gotoPlayLocal", "", "filePath", "", "gotoPlayback", "deviceDetail", "recordInfo", "recordType", "", "gotoRecordList", "init", "PlaybackModule_debug"})
public final class MediaPlaybackProvider implements com.mm.android.mobilecommon.route.IPlaybackProvider {
    private android.content.Context context;
    
    @java.lang.Override()
    public void gotoRecordList(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceDetail, int recordType) {
    }
    
    @java.lang.Override()
    public void gotoPlayback(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceDetail, @org.jetbrains.annotations.NotNull()
    java.lang.String recordInfo, int recordType) {
    }
    
    @java.lang.Override()
    public void gotoPlayLocal(@org.jetbrains.annotations.NotNull()
    java.lang.String filePath) {
    }
    
    @java.lang.Override()
    public void init(@org.jetbrains.annotations.Nullable()
    android.content.Context context) {
    }
    
    public MediaPlaybackProvider() {
        super();
    }
}