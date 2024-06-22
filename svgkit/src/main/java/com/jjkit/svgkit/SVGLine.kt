package com.jjkit.svgkit

import android.graphics.Path

class SVGLine : SVGDrawableWithMask() {

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    private var mStatus = true

    init {
        mIsFillEnabled = false
    }

    fun setX1(v: Float) : SVGLine {
        if (x1 != v) {
            x1 = v
            mStatus = true
        }
        return this
    }

    fun setY1(v: Float) : SVGLine {
        if (y1 != v) {
            y1 = v
            mStatus = true
        }
        return this
    }

    fun setX2(v: Float) : SVGLine {
        if (x2 != v) {
            x2 = v
            mStatus = true
        }
        return this
    }

    fun setY2(v: Float) : SVGLine {
        if (y2 != v) {
            y2 = v
            mStatus = true
        }
        return this
    }

    override fun setupPath(path: Path) {
        if(mStatus){
            path.reset()
            if(x1 != 0f || x2 != 0f || y1 != 0f || y2 != 0f) {
                path.moveTo(toDensity(x1),toDensity(y1))
                path.lineTo(toDensity(x2),toDensity(y2))
            }
        }
    }


    override fun cleanStatus() {
        mStatus = false
        super.cleanStatus()
    }

}