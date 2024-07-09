package com.jjkit.svgkit

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader


class SVGLinearGradient : SVGDrawableWithMask() {


    private var x = 0f
    private var y = 0f
    private var w = 0f
    private var h = 0f
    private var startX: Float = 0.5f
    private var startY: Float = 0f
    private var endX: Float = 0.5f
    private var endY: Float = 1f
    private val mRect = RectF()
    private var mPositions: FloatArray? = null
    private var mColors = intArrayOf(Color.WHITE, Color.BLACK)

    private var mCacheLinear:LinearGradient? = null

    init {
        mIsStrokeEnabled = false
    }

    private var mStatus = true
    private var mStatus2 = true

    fun setY(v: Float) : SVGLinearGradient {
        if (y != v) {
            y = v
            mStatus = true
        }
        return this
    }

    fun setX(v: Float): SVGLinearGradient {
        if (x != v) {
            x = v
            mStatus = true
        }
        return this
    }

    fun setW(v: Float) : SVGLinearGradient {
        if (w != v) {
            w = v
            mStatus = true
        }
        return this
    }

    fun setH(v: Float): SVGLinearGradient {
        if (h != v) {
            h = v
            mStatus = true
        }
        return this
    }
    fun setStartPoint(x: Float, y: Float) :SVGLinearGradient {
        if (startX != x || startY != y) {
            startX = x
            startY = y
            mStatus2 = true
        }
        return this
    }

    fun setEndPoint(x: Float, y: Float):SVGLinearGradient {
        if (endX != x || endY != y) {
            endX = x
            endY = y
            mStatus2 = true
        }
        return this
    }
    fun setColors(c: IntArray) :SVGLinearGradient{
        mColors = c
        mStatus2 = true
        return this
    }
    fun setPositions(p: FloatArray):SVGLinearGradient {
        mPositions = p
        mStatus2 = true
        return this
    }



    override fun setupPath(path: Path) {
        if(mStatus){
            path.reset()
            if(w > 0f && h > 0f ) {
                mRect.set(toDensity(x), toDensity(y), toDensity(x + w), toDensity(y + h))
                path.addRect(mRect, Path.Direction.CW)
            }
            notifyPathChanged()
        }
    }

    override fun setupPaintFill() {
        super.setupPaintFill()
        if(mStatus || mStatus2) {
            mCache.viewBoxMatrix.mapRect(mRect)
            val x1 = (startX * mRect.width()) + mRect.left
            val y1 = (startY * mRect.height()) + mRect.top
            val x2 = (endX * mRect.width()) + mRect.left
            val y2 = (endY * mRect.height()) + mRect.top
            mCacheLinear =  LinearGradient(x1, y1, x2, y2, mColors, mPositions, Shader.TileMode.CLAMP)
        }
        mPainterKit.paint.shader = mCacheLinear
    }


    override fun setFillColor(color: Int): SVGLinearGradient {
        return this
    }

    override fun cleanStatus() {
        mStatus = false
        mStatus2 = false
        super.cleanStatus()
    }

}