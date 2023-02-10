package com.lc.playback.utils;

import java.lang.System;

/**
 * Video recording timer
 *
 * 录像定时器
 */
@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\u0012B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fJ\u0006\u0010\u0011\u001a\u00020\u000fR\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/lc/playback/utils/RecordTimerTaskHelper;", "", "callback", "Lcom/lc/playback/utils/RecordTimerTaskHelper$OnUpdateRecordTimeCallback;", "period", "", "(Lcom/lc/playback/utils/RecordTimerTaskHelper$OnUpdateRecordTimeCallback;J)V", "handler", "Landroid/os/Handler;", "mRecordingTime", "timer", "Ljava/util/Timer;", "timerTask", "Ljava/util/TimerTask;", "onDestory", "", "startTimer", "stopTimer", "OnUpdateRecordTimeCallback", "PlaybackModule_debug"})
public final class RecordTimerTaskHelper {
    private java.util.Timer timer;
    private java.util.TimerTask timerTask;
    private final android.os.Handler handler = null;
    private long mRecordingTime = 0L;
    private final com.lc.playback.utils.RecordTimerTaskHelper.OnUpdateRecordTimeCallback callback = null;
    private final long period = 0L;
    
    public final void startTimer() {
    }
    
    public final void stopTimer() {
    }
    
    public final void onDestory() {
    }
    
    public RecordTimerTaskHelper(@org.jetbrains.annotations.Nullable()
    com.lc.playback.utils.RecordTimerTaskHelper.OnUpdateRecordTimeCallback callback, long period) {
        super();
    }
    
    public RecordTimerTaskHelper() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/lc/playback/utils/RecordTimerTaskHelper$OnUpdateRecordTimeCallback;", "", "updateRecordTime", "", "time", "", "PlaybackModule_debug"})
    public static abstract interface OnUpdateRecordTimeCallback {
        
        public abstract void updateRecordTime(long time);
    }
}