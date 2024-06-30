package com.jjkit.svgkit

import android.graphics.Path
import android.graphics.RectF


class SVGRect : SVGDrawableWithMask() {

    private var x = 0f
    private var y = 0f
    private var w = 0f
    private var h = 0f
    private var rtl = 0f
    private var rtr = 0f
    private var rbl = 0f
    private var rbr = 0f
    private val mRect = RectF()
    private val mRadius = FloatArray(8)
    
    private var mStatus = true

    fun setY(v: Float) : SVGRect {
        if (y != v) {
            y = v
            mStatus = true
        }
        return this
    }

    fun setX(v: Float): SVGRect {
        if (x != v) {
            x = v
            mStatus = true
        }
        return this
    }

    fun setW(v: Float) : SVGRect {
        if (w != v) {
            w = v
            mStatus = true
        }
        return this
    }

    fun setH(v: Float): SVGRect {
        if (h != v) {
            h = v
            mStatus = true
        }
        return this
    }

    fun setRtl(v: Float): SVGRect {
        if (rtl != v) {
            rtl = v
            mStatus = true
        }
        return this
    }

    fun setRtr(v: Float): SVGRect {
        if (rtr != v) {
            rtr = v
            mStatus = true
        }
        return this
    }

    fun setRbl(v: Float): SVGRect {
        if (rbl != v) {
            rbl = v
            mStatus = true
        }
        return this
    }

    fun setRbr(v: Float): SVGRect {
        if (rbr != v) {
            rbr = v
            mStatus = true
        }
        return this
    }
    
    override fun setupPath(path: Path) {
        if(mStatus){
            path.reset()
            if(w > 0f && h > 0f ) {
                mRect.set(toDensity(x),toDensity(y),toDensity(x + w),toDensity(y + h))
                mRadius[0] = toDensity(rtl)
                mRadius[1] = toDensity(rtl)
                mRadius[2] = toDensity(rtr)
                mRadius[3] = toDensity(rtr)
                mRadius[4] = toDensity(rbr)
                mRadius[5] = toDensity(rbr)
                mRadius[6] = toDensity(rbl)
                mRadius[7] = toDensity(rbl)
                path.addRoundRect(mRect, mRadius, Path.Direction.CW)
            }
            notifyPathChanged()
        }
    }

    override fun cleanStatus() {
        mStatus = false
        super.cleanStatus()
    }
}