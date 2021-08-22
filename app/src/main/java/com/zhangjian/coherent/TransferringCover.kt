package com.zhangjian.coherent

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by zhangjian on 2020/7/21.
 */
class TransferringCover : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#80000000")
    }

    private var paint: Paint = Paint()

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var progress: Float = 0F

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }

    private val path = Path()
    private val recent = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (progress in 0f..1f) {
            path.reset()
            val centerX = viewWidth.toDouble() / 2
            val centerY = viewHeight.toDouble() / 2
            val r = (sqrt(centerX) + sqrt(centerY)).pow(2.toDouble())  //外切圆半径
            recent.set(
                (centerX - r).toFloat(),
                (centerY - r).toFloat(),
                (centerX + r).toFloat(),
                (centerY + r).toFloat()
            )
            path.moveTo(centerX.toFloat(), centerY.toFloat())
            val startAngle = -90F
            val sweepAngle = progress * 360
            path.arcTo(
                recent,
                startAngle,
                sweepAngle
            )
            path.close()
            canvas.drawPath(path, paint)
        }
    }
}