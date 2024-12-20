package com.example.voicerecoder

import android.os.Handler
import android.os.Looper

class Timer(listener: onTimerTickListener) {
    private var duration: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            duration += 50L
            handler.postDelayed(this, 50L)
            listener.onTick(duration)
        }
    }

    fun start() {
        handler.postDelayed(runnable, 50L)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }

}

interface onTimerTickListener {
    fun onTick(duration: Long)
}