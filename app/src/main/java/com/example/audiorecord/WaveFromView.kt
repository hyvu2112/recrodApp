package com.example.audiorecord

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveFromView : View {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var w = 9f
    private var d = 6f

    private var sw = 0f
    private var sh = 100f

    private var maxSpikes = 0



    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        paint.color = Color.rgb(244,81,30)

        sw = resources.displayMetrics.widthPixels.toFloat()

        maxSpikes = (sw /(w+d)).toInt()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach {
            canvas.drawRoundRect(it, radius, radius, paint)
        }
    }

    fun addAmplitude(amp: Float){
        var norm = Math.min(amp.toInt()/7, 100).toFloat()
        amplitudes.add(norm)

        spikes.clear()
        var amps = amplitudes.takeLast(maxSpikes)
        for (i in amps.indices){
            var left = sw - i*(w+d)
            var top = sh/2 - amps[i]/2
            var right = left + w
            var bottom = top + amps[i]
            spikes.add(RectF(left, top, right, bottom))
        }
        invalidate()
    }
}