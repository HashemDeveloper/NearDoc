package com.project.neardoc.utils.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HeartBeatsView constructor(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    companion object {
        @JvmStatic private val mMatrix: Matrix = Matrix()
        @JvmStatic private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private var parentWidth: Int?= 0
    private var parentHeight: Int?= 0
    private var onRequestHeartBeatIndicator: OnRequestHeartBeatIndicator? = null

    fun setHeartBeatIndicatorListener(listener: OnRequestHeartBeatIndicator) {
        this.onRequestHeartBeatIndicator = listener
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        this.parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        this.parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(this.parentWidth!!, this.parentHeight!!)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            var bitmap: Bitmap?= null
            if (this.onRequestHeartBeatIndicator != null) {
                bitmap = this.onRequestHeartBeatIndicator!!.requestHeartBeatIndicator()
            }
            if (bitmap != null) {
                val bitmapX: Int = bitmap.width / 2
                val bitmapY: Int = bitmap.height / 2
                val parentX: Int = this.parentWidth!! / 2
                val parentY: Int = this.parentHeight!! / 2
                val centerX: Int = parentX - bitmapX
                val centerY: Int = parentY - bitmapY
                mMatrix.reset()
                mMatrix.postTranslate(centerX.toFloat(), centerY.toFloat())
                canvas.drawBitmap(bitmap, mMatrix, mPaint)
            }
        }
    }
    interface OnRequestHeartBeatIndicator {
        fun requestHeartBeatIndicator(): Bitmap
    }
}