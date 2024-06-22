package com.jjkit.svgkit

import android.graphics.Matrix
import android.graphics.Path

class SVGDrawableCache {
    var scaleOx = 0f
    var scaleOy = 0f
    var rotationOx = 0f
    var rotationOy = 0f
    var translateX = 0f
    var translateY = 0f
    var shadowRadius : Float = 0f
    var shadowDx : Float = 0f
    var shadowDy : Float = 0f
    var strokeWidth: Float = 0f
    val path: Path = Path()
    val pathStroke = Path()
    val viewBoxMatrix: Matrix = Matrix()
    var width = 0f
    var height = 0f
}