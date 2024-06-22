package com.jjkit.svgkit

import android.graphics.Path
import android.graphics.RectF


class SVGEllipse : SVGDrawableWithMask() {

    private var cx = 0f
    private var cy = 0f
    private var rx = 0f
    private var ry = 0f

    private val mRect = RectF()
    private var mStatus = true

    fun setCx(v: Float): SVGEllipse {
        if (cx != v) {
            cx = v
            mStatus = true
        }
        return this
    }

    fun setCy(v: Float) : SVGEllipse {
        if (cy != v) {
            cy = v
            mStatus = true
        }
        return this
    }

    fun setRx(v: Float) : SVGEllipse {
        if (rx != v) {
            rx = v
            mStatus = true
        }
        return this
    }

    fun setRy(v: Float): SVGEllipse {
        if (ry != v) {
            ry = v
            mStatus = true
        }
        return this
    }

    override fun setupPath(path: Path) {
        if(mStatus){
            path.reset()
            if(rx != 0f && ry != 0f){

                val cxx: Float = toDensity(cx)
                val cyy: Float = toDensity(cy)
                val rxx: Float = toDensity(rx)
                val ryy: Float = toDensity(ry)

                mRect[cxx - rxx, cyy - ryy, cxx + rxx] = cyy + ryy
                path.addOval(mRect, Path.Direction.CW)
            }
            notifyPathChanged()
        }

    }

    override fun cleanStatus() {
        mStatus = false
        super.cleanStatus()
    }

}