package com.example.audiorecord

import android.os.Handler
import android.os.Looper
import android.os.Vibrator

class Timer(listener: OnTimeTickListener) {

    interface OnTimeTickListener{
        fun onTimeTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L




    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)
            listener.onTimeTick(format())
        }
    }

    fun start(){
        handler.postDelayed(runnable, delay)
    }

    fun pause(){
        handler.removeCallbacks(runnable)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
        duration = 0L
    }

    fun format(): String{
        val mil = duration % 1000
        val seconds = (duration/1000) % 60
        val minutes = (duration / (1000*60)) % 60
        val hours = (duration / (1000 * 60 * 60))

        var format = if (hours > 0)
            "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, mil/10)
        else
            "%02d:%02d.%02d".format(minutes, seconds, mil/10)
        return format
    }
}