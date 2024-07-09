package com.jjkit.svgkit

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader

class SVGRadialGradient : SVGDrawableWithMask() {


    private var x: Float = 0f
    private var y: Float = 0f
    private var w: Float = 0f
    private var h: Float = 0f
    private var cx: Float = 0.5f
    private var cy: Float = 0.5f
    private var rx: Float = 0.5f
    private var ry: Float = 0.5f
    private val mRect = RectF()
    private var mPositions: FloatArray? = null
    private var mColors = intArrayOf(Color.WHITE, Color.BLACK)
    private var mCacheRadial:RadialGradient? = null
    private var mMatrix = Matrix()
    init {
        mIsStrokeEnabled = false
    }

    private var mStatus = true
    private var mStatus2 = true
    fun setY(v: Float) : SVGRadialGradient {
        if (y != v) {
            y = v
            mStatus = true
        }
        return this
    }

    fun setX(v: Float): SVGRadialGradient {
        if (x != v) {
            x = v
            mStatus = true
        }
        return this
    }

    fun setW(v: Float) : SVGRadialGradient {
        if (w != v) {
            w = v
            mStatus = true
        }
        return this
    }

    fun setH(v: Float): SVGRadialGradient {
        if (h != v) {
            h = v
            mStatus = true
        }
        return this
    }
    fun setCx(v: Float) :SVGRadialGradient {
        if (cx != v) {
            cx = v
            mStatus2 = true
        }
        return this
    }

    fun setCy(v: Float) :SVGRadialGradient{
        if (cy != v) {
            cy = v
            mStatus2 = true
        }
        return this
    }

    fun setRx(v: Float) :SVGRadialGradient {
        if (rx != v) {
            rx = v
            mStatus2 = true
        }
        return this
    }

    fun setRy(v: Float) :SVGRadialGradient {
        if (ry != v) {
            ry = v
            mStatus2 = true
        }
        return this
    }
    fun setColors(c: IntArray) :SVGRadialGradient{
        mColors = c
        mStatus2 = true
        return this
    }
    fun setPositions(p: FloatArray):SVGRadialGradient {
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
            val x = (cx * mRect.width()) + mRect.left
            val y = (cy * mRect.height()) + mRect.top
            val radius = rx * mRect.width()
            val ratio = (ry / rx).toDouble()
            mCacheRadial = RadialGradient(x, y, radius, mColors, mPositions, Shader.TileMode.CLAMP)
            mMatrix.reset()
            mMatrix.postScale(1f, ratio.toFloat(), x, y)
            mCacheRadial?.setLocalMatrix(mMatrix)

        }
        mPainterKit.paint.shader = mCacheRadial
    }


    override fun setFillColor(color: Int): SVGDrawableWithMask {
        return this
    }

    override fun cleanStatus() {
        mStatus = false
        mStatus2 = false
        super.cleanStatus()
    }
}