package com.jjkit.svgkit.utils

import android.graphics.Color
import com.jjkit.svgkit.DrawableTypes

data class CommonProps(
    var fillColor:Int = Color.BLACK,
    var fillColorStatus: Boolean = true,
    var fillOpacity: Float = 1f,
    var fillOpacityStatus: Boolean = true,
    var fillRule: DrawableTypes.FillRule = DrawableTypes.FillRule.NONZERO,
    var fillRuleStatus: Boolean = true,

    var strokeColor: Int = Color.TRANSPARENT,
    var strokeColorStatus: Boolean = true,
    var strokeOpacity: Float = 1f,
    var strokeOpacityStatus: Boolean = true,
    var strokeWidth: Float = 1f,
    var strokeWidthStatus: Boolean = true,
    var strokeCap: DrawableTypes.LineCap = DrawableTypes.LineCap.BUTT,
    var strokeCapStatus: Boolean = true,
    var strokeJoin: DrawableTypes.LineJoin = DrawableTypes.LineJoin.MITER,
    var strokeJoinStatus: Boolean = true,
    var strokeMiter: Float = 4f,
    var strokeMiterStatus: Boolean = true,
    var strokeStart: Float = 0f,
    var strokeEnd: Float = 1f,
    var strokeStartEndChangeStatus: Boolean = true,

    var shadowColor: Int = Color.TRANSPARENT,
    var shadowRadius: Float = 2f,
    var shadowRadiusStatus: Boolean = true,
    var shadowOffsetX: Float = 2f,
    var shadowOffsetY: Float = 2f,
    var shadowOffsetIsPercent: Boolean = false,
    var shadowOffsetStatus: Boolean = true,
){
     
     fun inactiveStatus(){
        fillColorStatus = false
        fillOpacityStatus = false
        fillRuleStatus = false
        strokeColorStatus = false
        strokeOpacityStatus = false
        strokeWidthStatus = false
        strokeCapStatus = false
        strokeJoinStatus = false
        strokeMiterStatus = false
        strokeStartEndChangeStatus = false
         shadowRadiusStatus = false
         shadowOffsetStatus = false
     }

     fun activeStatus(){
          fillColorStatus = true
          fillOpacityStatus = true
          fillRuleStatus = true
          strokeColorStatus = true
          strokeOpacityStatus = true
          strokeWidthStatus = true
          strokeCapStatus = true
          strokeJoinStatus = true
          strokeMiterStatus = true
          strokeStartEndChangeStatus = true
         shadowRadiusStatus = true
         shadowOffsetStatus = true
     }
}