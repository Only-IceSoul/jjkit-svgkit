package com.jjkit.svgkit

import android.graphics.Path
import com.jjkit.svgkit.utils.SVGPathParser

class SVGPath: SVGDrawableWithMask() {

    private var mPathD:String =""
    private var mPathDStatus = true

    fun setPath(d: String) : SVGPath {
        if (mPathD != d){
            mPathD = d
            mPathDStatus = true
        }
        return this
    }

    override fun setupPath(path: Path) {
        if (mPathDStatus){
            path.reset()
            val pat = SVGPathParser.parse(mPathD)
            path.set(pat)
            notifyPathChanged()
        }
    }

    override fun cleanStatus() {
        mPathDStatus = false
        super.cleanStatus()
    }
}