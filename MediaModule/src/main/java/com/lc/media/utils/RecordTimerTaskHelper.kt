package com.lc.media.utils

import android.os.Handler
import android.os.Looper
import java.lang.RuntimeException
import java.util.*

/**
 * Video recording timer
 *
 * 录像定时器
 */
class RecordTimerTaskHelper(
    private val callback: OnUpdateRecordTimeCallback? = null,
    private val period: Long = 1000
) {

    private var timer: Timer? = null

    private var timerTask: TimerTask? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private var mRecordingTime = 0L

    fun startTimer() {
        stopTimer()
        if (period <= 0) throw RuntimeException("period must be greater than 0")
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                mRecordingTime += 1000
                handler.post {
                    callback?.updateRecordTime(mRecordingTime)
                }
            }
        }
        timer?.schedule(timerTask, 0, period)
    }

    fun stopTimer() {
        mRecordingTime = 0L
        timer?.cancel()
        timer = null
        timerTask?.cancel()
        timerTask = null
    }

    fun onDestory() {
        stopTimer()
        handler.removeCallbacksAndMessages(null)
    }

    interface OnUpdateRecordTimeCallback {
        fun updateRecordTime(time: Long)
    }
}