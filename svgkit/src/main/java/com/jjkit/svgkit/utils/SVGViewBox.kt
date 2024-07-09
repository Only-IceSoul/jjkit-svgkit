package com.jjkit.svgkit.utils

import android.graphics.Matrix
import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min


class SVGViewBox{
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = -1f
    var height: Float = -1f
    var align: Align = Align.xMidYMid
    var aspect: MeetOrSlice = MeetOrSlice.MEET
    var changeStatus: Boolean = true


    enum class Align {
        none,xMinYMin,xMidYMin,xMaxYMin,xMinYMid,xMidYMid,xMaxYMid,xMinYMax,xMidYMax,xMaxYMax
    }

    enum class MeetOrSlice {
       MEET,SLICE,NONE
    }


    companion object {

        private var mScale: Float = 1f

        //Viewport Density for a correct transformation
        fun setDensity(density: Float) {
            mScale = density
        }


        //we are transforming from viewBox density (scale) to bounds pixel, (pixel to pixel Artifacts)
        fun matrixTransform(viewBox: SVGViewBox, srcBounds: RectF, dst: Matrix) {
            // based on https://svgwg.org/svg2-draft/coords.html#ComputingAViewportsTransform

            // Let vb-x, vb-y, vb-width, vb-height be the min-x, min-y, width and height values of the viewBox attribute respectively.

            val vbX = viewBox.x * mScale
            val vbY = viewBox.y * mScale
            val vbWidth = viewBox.width * mScale
            val vbHeight = viewBox.height * mScale

            // Let e-x, e-y, e-width, e-height be the position and size of the element respectively.
            val eX = srcBounds.left
            val eY = srcBounds.top
            val eWidth = srcBounds.width()
            val eHeight = srcBounds.height()


            // Initialize scale-x to e-width/vb-width.
            var scaleX = eWidth / vbWidth

            // Initialize scale-y to e-height/vb-height.
            var scaleY = eHeight / vbHeight

            // Initialize translate-x to e-x - (vb-x * scale-x).
            // Initialize translate-y to e-y - (vb-y * scale-y).
            var translateX = eX - (vbX * scaleX)
            var translateY = eY - (vbY * scaleY)

            // If align is 'none'
            if (viewBox.aspect == MeetOrSlice.NONE) {
                // Let scale be set the smaller value of scale-x and scale-y.
                // Assign scale-x and scale-y to scale.
                scaleY = min(scaleX, scaleY)
                scaleX = scaleY
                val scale = scaleX

                // If scale is greater than 1
                if (scale > 1) {
                    // Minus translateX by (eWidth / scale - vbWidth) / 2
                    // Minus translateY by (eHeight / scale - vbHeight) / 2
                    translateX -= (eWidth / scale - vbWidth) / 2f
                    translateY -= (eHeight / scale - vbHeight) / 2f
                } else {
                    translateX -= (eWidth - vbWidth * scale) / 2f
                    translateY -= (eHeight - vbHeight * scale) / 2f
                }
            } else {
                // If align is not 'none' and meetOrSlice is 'meet', set the larger of scale-x and scale-y to the smaller.
                // Otherwise, if align is not 'none' and meetOrSlice is 'slice', set the smaller of scale-x and scale-y to the larger.

                if (viewBox.align != Align.none && viewBox.aspect == MeetOrSlice.MEET) {
                    scaleY = min(scaleX, scaleY)
                    scaleX = scaleY
                } else if (viewBox.align != Align.none && viewBox.aspect == MeetOrSlice.SLICE) {
                    scaleY = max(scaleX, scaleY)
                    scaleX = scaleY
                }

                // If align contains 'xMid', add (e-width - vb-width * scale-x) / 2 to translate-x.
                if (viewBox.align.name.contains("xMid")) {
                    translateX += (eWidth - vbWidth * scaleX) / 2f
                }

                // If align contains 'xMax', add (e-width - vb-width * scale-x) to translate-x.
                if (viewBox.align.name.contains("xMax")) {
                    translateX += (eWidth - vbWidth * scaleX)
                }

                // If align contains 'yMid', add (e-height - vb-height * scale-y) / 2 to translate-y.
                if (viewBox.align.name.contains("YMid")) {
                    translateY += (eHeight - vbHeight * scaleY) / 2f
                }

                // If align contains 'yMax', add (e-height - vb-height * scale-y) to translate-y.
                if (viewBox.align.name.contains("YMax")) {
                    translateY += (eHeight - vbHeight * scaleY)
                }
            }

            // The transform applied to content contained by the element is given by
            // translate(translate-x, translate-y) scale(scale-x, scale-y).
            dst.reset()
            dst.postTranslate(translateX, translateY)
            dst.preScale(scaleX, scaleY)
        }

        fun matrixTransform(viewBox: SVGViewBox, boundsWidth: Float, boundsHeight:Float, dst: Matrix) {
            // based on https://svgwg.org/svg2-draft/coords.html#ComputingAViewportsTransform

            // Let vb-x, vb-y, vb-width, vb-height be the min-x, min-y, width and height values of the viewBox attribute respectively.

            val vbX = viewBox.x * mScale
            val vbY = viewBox.y * mScale
            val vbWidth = viewBox.width * mScale
            val vbHeight = viewBox.height * mScale

            // Let e-x, e-y, e-width, e-height be the position and size of the element respectively.
            val eX = 0f
            val eY = 0f
            // Initialize scale-x to e-width/vb-width.
            var scaleX = boundsWidth / vbWidth

            // Initialize scale-y to e-height/vb-height.
            var scaleY = boundsHeight / vbHeight

            // Initialize translate-x to e-x - (vb-x * scale-x).
            // Initialize translate-y to e-y - (vb-y * scale-y).
            var translateX = eX - (vbX * scaleX)
            var translateY = eY - (vbY * scaleY)

            // If align is 'none'
            if (viewBox.aspect == MeetOrSlice.NONE) {
                // Let scale be set the smaller value of scale-x and scale-y.
                // Assign scale-x and scale-y to scale.
                scaleY = min(scaleX, scaleY)
                scaleX = scaleY
                val scale = scaleX

                // If scale is greater than 1
                if (scale > 1) {
                    // Minus translateX by (eWidth / scale - vbWidth) / 2
                    // Minus translateY by (eHeight / scale - vbHeight) / 2
                    translateX -= (boundsWidth / scale - vbWidth) / 2f
                    translateY -= (boundsHeight / scale - vbHeight) / 2f
                } else {
                    translateX -= (boundsWidth - vbWidth * scale) / 2f
                    translateY -= (boundsHeight - vbHeight * scale) / 2f
                }
            } else {
                // If align is not 'none' and meetOrSlice is 'meet', set the larger of scale-x and scale-y to the smaller.
                // Otherwise, if align is not 'none' and meetOrSlice is 'slice', set the smaller of scale-x and scale-y to the larger.

                if (viewBox.align != Align.none && viewBox.aspect == MeetOrSlice.MEET) {
                    scaleY = min(scaleX, scaleY)
                    scaleX = scaleY
                } else if (viewBox.align != Align.none && viewBox.aspect == MeetOrSlice.SLICE) {
                    scaleY = max(scaleX, scaleY)
                    scaleX = scaleY
                }

                // If align contains 'xMid', add (e-width - vb-width * scale-x) / 2 to translate-x.
                if (viewBox.align.name.contains("xMid")) {
                    translateX += (boundsWidth - vbWidth * scaleX) / 2f
                }

                // If align contains 'xMax', add (e-width - vb-width * scale-x) to translate-x.
                if (viewBox.align.name.contains("xMax")) {
                    translateX += (boundsWidth - vbWidth * scaleX)
                }

                // If align contains 'yMid', add (e-height - vb-height * scale-y) / 2 to translate-y.
                if (viewBox.align.name.contains("YMid")) {
                    translateY += (boundsHeight - vbHeight * scaleY) / 2f
                }

                // If align contains 'yMax', add (e-height - vb-height * scale-y) to translate-y.
                if (viewBox.align.name.contains("YMax")) {
                    translateY += (boundsHeight - vbHeight * scaleY)
                }
            }

            // The transform applied to content contained by the element is given by
            // translate(translate-x, translate-y) scale(scale-x, scale-y).
            dst.reset()
            dst.postTranslate(translateX, translateY)
            dst.preScale(scaleX, scaleY)
        }

        fun matrixTransform(viewBox: RectF, align: Align, aspect: MeetOrSlice, srcBounds: RectF, dst: Matrix) {
            // based on https://svgwg.org/svg2-draft/coords.html#ComputingAViewportsTransform

            // Let vb-x, vb-y, vb-width, vb-height be the min-x, min-y, width and height values of the viewBox attribute respectively.

            val vbX = viewBox.left * mScale
            val vbY = viewBox.top * mScale
            val vbWidth = viewBox.width() * mScale
            val vbHeight = viewBox.height() * mScale

            // Let e-x, e-y, e-width, e-height be the position and size of the element respectively.
            val eX = srcBounds.left
            val eY = srcBounds.top
            val eWidth = srcBounds.width()
            val eHeight = srcBounds.height()


            // Initialize scale-x to e-width/vb-width.
            var scaleX = eWidth / vbWidth

            // Initialize scale-y to e-height/vb-height.
            var scaleY = eHeight / vbHeight

            // Initialize translate-x to e-x - (vb-x * scale-x).
            // Initialize translate-y to e-y - (vb-y * scale-y).
            var translateX = eX - (vbX * scaleX)
            var translateY = eY - (vbY * scaleY)

            // If align is 'none'
            if (aspect == MeetOrSlice.NONE) {
                // Let scale be set the smaller value of scale-x and scale-y.
                // Assign scale-x and scale-y to scale.
                scaleY = min(scaleX, scaleY)
                scaleX = scaleY
                val scale = scaleX

                // If scale is greater than 1
                if (scale > 1) {
                    // Minus translateX by (eWidth / scale - vbWidth) / 2
                    // Minus translateY by (eHeight / scale - vbHeight) / 2
                    translateX -= (eWidth / scale - vbWidth) / 2f
                    translateY -= (eHeight / scale - vbHeight) / 2f
                } else {
                    translateX -= (eWidth - vbWidth * scale) / 2f
                    translateY -= (eHeight - vbHeight * scale) / 2f
                }
            } else {
                // If align is not 'none' and meetOrSlice is 'meet', set the larger of scale-x and scale-y to the smaller.
                // Otherwise, if align is not 'none' and meetOrSlice is 'slice', set the smaller of scale-x and scale-y to the larger.

                if (align != Align.none && aspect == MeetOrSlice.MEET) {
                    scaleY = min(scaleX, scaleY)
                    scaleX = scaleY
                } else if (align != Align.none && aspect == MeetOrSlice.SLICE) {
                    scaleY = max(scaleX, scaleY)
                    scaleX = scaleY
                }

                // If align contains 'xMid', add (e-width - vb-width * scale-x) / 2 to translate-x.
                if (align.name.contains("xMid")) {
                    translateX += (eWidth - vbWidth * scaleX) / 2f
                }

                // If align contains 'xMax', add (e-width - vb-width * scale-x) to translate-x.
                if (align.name.contains("xMax")) {
                    translateX += (eWidth - vbWidth * scaleX)
                }

                // If align contains 'yMid', add (e-height - vb-height * scale-y) / 2 to translate-y.
                if (align.name.contains("YMid")) {
                    translateY += (eHeight - vbHeight * scaleY) / 2f
                }

                // If align contains 'yMax', add (e-height - vb-height * scale-y) to translate-y.
                if (align.name.contains("YMax")) {
                    translateY += (eHeight - vbHeight * scaleY)
                }
            }

            // The transform applied to content contained by the element is given by
            // translate(translate-x, translate-y) scale(scale-x, scale-y).
            dst.reset()
            dst.postTranslate(translateX, translateY)
            dst.preScale(scaleX, scaleY)
        }

        fun viewBoxToDensity(viewBox: SVGViewBox, dst: SVGViewBox) {
            dst.x = viewBox.x * mScale
            dst.y = viewBox.y * mScale
            dst.width = viewBox.width * mScale
            dst.height = viewBox.height * mScale
        }

        fun viewBoxToDensity(viewBox: SVGViewBox, dst: RectF) {
            dst.set(
                viewBox.x * mScale,
                viewBox.y * mScale,
                (viewBox.x + viewBox.width) * mScale,
                (viewBox.y + viewBox.height) * mScale
            )
        }

        fun viewBoxToDensity(viewBox: RectF, dst: RectF) {
            dst.set(
                viewBox.left * mScale,
                viewBox.top * mScale,
                viewBox.right * mScale,
                viewBox.bottom * mScale
            )
        }


        fun toDensity(value: Float): Float {
            return value * mScale
        }
    }

}
