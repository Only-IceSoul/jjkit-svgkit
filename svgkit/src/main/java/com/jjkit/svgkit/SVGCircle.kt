package com.jjkit.svgkit

import android.graphics.Path


class SVGCircle : SVGDrawableWithMask() {


    private var cx = 0f
    private var cy = 0f
    private var r = 0f

    fun setCx(v: Float): SVGCircle {
        if (cx != v) {
            cx = v
            notifyPathChanged()
        }
        return this
    }

    fun setR(v: Float): SVGCircle {
        if (r != v) {
            r = v
            notifyPathChanged()
        }
        return this
    }

    fun setCy(v: Float) : SVGCircle {
        if (cy != v) {
            cy = v
            notifyPathChanged()
        }
        return this
    }

    override fun setupPath(path:Path) {
        if(didPathChange()){
            path.reset()
            path.addCircle(
                toDensity(cx), toDensity(cy),
                toDensity(r), Path.Direction.CW)
        }
    }
}