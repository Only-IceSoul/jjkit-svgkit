package com.jjkit.svgkit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.os.Build
import com.jjkit.svgkit.utils.PainterKit

abstract class SVGDrawableWithMask: SVGDrawable() {

    private var mIsMask = false
    fun setIsMak(b:Boolean) : SVGDrawableWithMask {
        mIsMask = true
        return this
    }

    override fun onDraw(canvas: Canvas, painterKit: PainterKit, width: Float, height: Float) {
        if(mIsMask){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                painterKit.paintMask.setXfermode(painterKit.dstOut)
                canvas.saveLayer(0f, 0f, width, height, painterKit.paintMask)
                canvas.drawColor(Color.BLACK)
                painterKit.paintMask.setXfermode(painterKit.dstOut)
                canvas.saveLayer(0f, 0f, width, height, painterKit.paintMask)
                super.onDraw(canvas, painterKit, width, height)
                canvas.restore()
                canvas.restore()
            } else {
                painterKit.paintMask.setXfermode(painterKit.dstIn)
                canvas.saveLayer(0f, 0f, width, height, painterKit.paintMask)
                super.onDraw(canvas, painterKit, width, height)
                canvas.restore()
            }
        }else{
            super.onDraw(canvas, painterKit, width, height)
        }

    }



    abstract override fun setupPath(path: Path)



}