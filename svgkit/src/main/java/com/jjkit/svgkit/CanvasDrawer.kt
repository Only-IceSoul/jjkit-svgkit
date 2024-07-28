package com.jjkit.svgkit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build


object CanvasDrawer {

    var helperOne = true
    val dstIn : PorterDuffXfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    val dstOut: PorterDuffXfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun resetOneSetup(){
        helperOne = true
    }

    inline fun oneSetup(block: ()->Unit){
        if(helperOne){
            block()
        }
        helperOne = false
    }

    fun drawPath(canvas: Canvas, path: Path, paint: Paint,matrix: Matrix = Matrix()){
        val checkpoint = canvas.save()
        canvas.concat(matrix)
        try {
            canvas.drawPath(path, paint)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }


    fun drawMask(canvas: Canvas,width:Float,height:Float,drawContent:()->Unit){
        paint.reset()
        paint.clearShadowLayer()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            paint.setXfermode(dstOut)
            canvas.saveLayer(0f, 0f, width, height, paint)
            canvas.drawColor(Color.BLACK)
            paint.setXfermode(dstOut)
            canvas.saveLayer(0f, 0f, width, height, paint)
            drawContent()
            canvas.restore()
            canvas.restore()
        } else {
            paint.setXfermode(dstIn)
            canvas.saveLayer(0f, 0f, width, height, paint)
            drawContent()
            canvas.restore()
        }
        paint.setXfermode(null)
    }

}