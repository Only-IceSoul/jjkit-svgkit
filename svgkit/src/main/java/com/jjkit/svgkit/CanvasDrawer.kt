package com.jjkit.svgkit

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path


object CanvasDrawer {

    var helperOne = true


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






}