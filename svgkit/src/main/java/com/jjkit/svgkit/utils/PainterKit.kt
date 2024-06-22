package com.jjkit.svgkit.utils

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF

data class PainterKit(

    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
    val paintStroke: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
    val paintMask: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
    val dstIn : PorterDuffXfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_IN),
    val srcIn :PorterDuffXfermode  =  PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
    val dstOut: PorterDuffXfermode  =  PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
    val rect: RectF = RectF(),
    val matrix: Matrix = Matrix(),
    val pathMeasure: PathMeasure = PathMeasure()
)
