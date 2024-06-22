package com.jjkit.svgkit.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.jjkit.svgkit.SVGDrawable
import kotlin.math.max
import kotlin.math.min

object SVGUtil{

    fun clamp(v: Float,min:Float = 0f,max:Float = 1f): Float {
        return if (v > max) max else (if (v < min) min else v)
    }

    fun clamp(v: Float,min:Float,max:Float,minResult:Float,maxResult:Float): Float {
        return if (v > max) maxResult else (if (v < min) minResult else v)
    }

    fun uClamp(v: Float): Float {
        return if (v < 0f) 0f else v
    }

    fun uClamp(v: Float, optional: Float): Float {
        return if (v < 0f) optional else v
    }

    internal fun viewBoxEvaluator(value: Float, start: Float, end: Float): Float {
        return (value - start) / (end - start)
    }

    internal fun viewBoxToWidth(value: Float, viewBox: RectF, w: Float): Float {
        return viewBoxEvaluator(value, viewBox.left, viewBox.right) * w
    }
    internal fun viewBoxToWidth(value: Float, viewBox: SVGViewBox, w: Float): Float {
        return viewBoxEvaluator(value, viewBox.x, viewBox.x + viewBox.width) * w
    }

    internal fun viewBoxToHeight(value: Float, viewBox: RectF, h: Float): Float {
        return viewBoxEvaluator(value, viewBox.top, viewBox.bottom) * h
    }
    internal fun viewBoxToHeight(value: Float, viewBox: SVGViewBox, h: Float): Float {
        return viewBoxEvaluator(value, viewBox.y, viewBox.y + viewBox.height) * h
    }
    internal fun viewBoxToMax(value: Float, viewBox: RectF, w: Float, h: Float): Float {
        val size = max(w, h)
        val maxVb = if (w > h) viewBox.width() else viewBox.height()
        return (value / maxVb) * size
    }

    internal fun toMaxViewBox(value: Float, viewBox: RectF, viewBoxDensity: RectF): Float {
        val size = max(viewBoxDensity.width(), viewBoxDensity.height())
        val maxVb =  if (viewBoxDensity.width() > viewBoxDensity.height()) viewBox.width() else viewBox.height()
        return (value / maxVb) * size
    }
    internal fun toMaxViewBox(value: Float, viewBox: SVGViewBox, viewBoxDensity: SVGViewBox): Float {
        val size = max(viewBoxDensity.width, viewBoxDensity.height)
        val maxVb =  if (viewBoxDensity.width > viewBoxDensity.height) viewBox.width else viewBox.height
        return (value / maxVb) * size
    }

    internal fun viewBoxToMin(value: Float, viewBox: RectF, w: Float, h: Float): Float {
        val size = min(w, h)
        val minVb = if (w > h) viewBox.height() else viewBox.width()
        return (value / minVb) * size
    }

    //pixel unit
    fun toBitmap(svg: SVGDrawable,width: Float,height:Float): Bitmap {
        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        svg.setup(width,height)
        svg.draw(Canvas(bitmap))
        return bitmap
    }
}