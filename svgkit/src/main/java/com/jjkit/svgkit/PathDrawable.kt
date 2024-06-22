package com.jjkit.svgkit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.jjkit.svgkit.utils.CommonProps
import com.jjkit.svgkit.utils.SVGUtil
import com.jjkit.svgkit.utils.TransformProps

//shared objects
open class PathDrawable(paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
                        paintStroke:Paint = Paint(Paint.ANTI_ALIAS_FLAG),
                        pathMeasure:PathMeasure = PathMeasure(),
                        matrix:Matrix = Matrix()
) : Drawable() {

    companion object{
        var isShadowEnabled :Boolean = true
        private var mDensity = 1f

        fun setDensity(v:Float){
            mDensity = v
        }
    }

    private val mProps = CommonProps()
    private val mTransform = TransformProps()

    init {
        mProps.fillColor = Color.TRANSPARENT
    }

    private val mPaint: Paint = paint
    private val mPaintStroke: Paint = paintStroke
    private val mPathMeasure: PathMeasure = pathMeasure

    private var mPath = Path()
    private val mPathStroke = Path()
    private val mMatrix = matrix
    
    private val mBounds : RectF = RectF()
    private var mmBoundsChangeStatus:Boolean = true
    private var mPathChangeStatus : Boolean = true


    fun setFillColor(color: Int) : PathDrawable {
        mProps.fillColor = color
        mProps.fillColorStatus = true
        return this
    }
    fun setFillOpacity(opacity: Float) : PathDrawable {
        mProps.fillOpacity = opacity
        mProps.fillOpacityStatus = true
        return this
    }

    fun setFillRule(fillRule: DrawableTypes.FillRule) : PathDrawable {
        mProps.fillRule = fillRule
        mProps.fillRuleStatus = true
        return this
    }

    fun setStrokeColor(color: Int) : PathDrawable {
        mProps.strokeColor = color
        mProps.strokeColorStatus = true
        return this
    }
    fun setStrokeOpacity(opacity: Float) : PathDrawable {
        mProps.strokeOpacity = opacity
        mProps.strokeOpacityStatus = true
        return this
    }
    fun setStrokeWidth(w: Float,pixel:Boolean = false) : PathDrawable {
        if(mProps.strokeWidth != w){
            mProps.strokeWidth = w.pixelOrDp(pixel)
            mProps.strokeWidthStatus = true
        }
        return this
    }
    fun setStrokeCap(cap: DrawableTypes.LineCap) : PathDrawable {
        mProps.strokeCap = cap
        mProps.strokeCapStatus = true
        return this
    }
    fun setStrokeJoin(join: DrawableTypes.LineJoin) : PathDrawable {
        mProps.strokeJoin = join
        mProps.strokeJoinStatus = true
        return this
    }
    fun setStrokeMiter(value: Float) : PathDrawable {
        mProps.strokeMiter = value
        mProps.strokeMiterStatus = true
        return this
    }
    fun setStrokeStart(v: Float) : PathDrawable {
        if(mProps.strokeStart != v){
            mProps.strokeStart = v
            mProps.strokeStartEndChangeStatus = true
        }
        return this
    }
    fun setStrokeEnd(v: Float) : PathDrawable {
        if(mProps.strokeEnd != v) {
            mProps.strokeEnd = v
            mProps.strokeStartEndChangeStatus = true
        }
        return this
    }

    fun setShadowColor(c: Int) : PathDrawable {
        mProps.shadowColor = c
        return this
    }

    fun setShadowRadius(v: Float,pixel:Boolean = false) : PathDrawable {
        if(mProps.shadowRadius != v){
            mProps.shadowRadius = v.pixelOrDp(pixel)
            mProps.shadowRadiusStatus = true
        }
        return this
    }
    fun setShadowOffset(x: Float,y:Float,pixel:Boolean = false) : PathDrawable {
        if(mProps.shadowOffsetX != x || mProps.shadowOffsetY != y  ){
            mProps.shadowOffsetX = x.pixelOrDp(pixel)
            mProps.shadowOffsetY = y.pixelOrDp(pixel)
            mProps.shadowOffsetStatus = true
        }
        return this
    }


    //MARK: TRANSFORM PROPS

    fun setTranslationX(x: Float,y:Float,pixel:Boolean = false) : PathDrawable {
        if (mTransform.translationX != x || mTransform.translationY != y ){
            mTransform.translationX = x.pixelOrDp(pixel)
            mTransform.translationY = y.pixelOrDp(pixel)
            mTransform.translationStatus = true
        }
        return this
    }

    fun setRotation(v: Float): PathDrawable {
        mTransform.rotation = v
        return this
    }

    fun setRotationOrigin(x: Float,y:Float,pixel:Boolean = false): PathDrawable {
        if (mTransform.rotationOx != x || mTransform.rotationOy != y ){
            mTransform.rotationOx = x.pixelOrDp(pixel)
            mTransform.rotationOy = y.pixelOrDp(pixel)
            mTransform.rotationOriginStatus = true
        }
        return this
    }

    fun setScaleX(v: Float): PathDrawable {
        mTransform.scaleX = v
        return this
    }
    fun setScaleY(v: Float): PathDrawable {
        mTransform.scaleY = v
        return this
    }

    fun setScaleOrigin(x: Float,y:Float,pixel:Boolean = false) : PathDrawable {
        if(mTransform.scaleOriginX != x || mTransform.scaleOriginY != y ){
            mTransform.scaleOriginX = x.pixelOrDp(pixel)
            mTransform.scaleOriginY = y.pixelOrDp(pixel)
            mTransform.scaleOriginStatus = true
        }
        return this
    }

    override fun onBoundsChange(bounds: Rect) {
        mBounds.set(bounds)
    }

    //should be called after setBounds() -> setupAll or setup individually (3) -> this
    override fun draw(canvas: Canvas) {
        if(mBounds.width() > 0f && mBounds.height() > 0f ) {
            onDraw(canvas,mBounds)
            cleanStatus()
        }

    }

    protected  open fun onDraw(canvas: Canvas, bounds: RectF) {
        val checkpoint = canvas.save()
        canvas.concat(mMatrix)
        try {
            drawContent(canvas,bounds)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    protected open fun drawContent(canvas: Canvas, bounds: RectF) {
        if (fill()) canvas.drawPath(mPath, mPaint)
        if (stroke()) canvas.drawPath(mPathStroke, mPaintStroke)
    }



    fun setupPaints() : PathDrawable {
        mPath.fillType = getFillRule()

        if (fill()) {
            setupPaintFill()
            setupShadow(false)
        }

        if (stroke()) {
            setupPaintStroke()
            if (!fill()) setupShadow(true)
        }

        mPaintStroke.colorFilter = mColorFilter
        mPaint.colorFilter = mColorFilter

        return this
    }


    protected open fun cleanStatus(){
        inactiveStatus()
    }

    protected fun inactiveStatus(){
        mProps.inactiveStatus()
        mTransform.inactiveStatus()
        mmBoundsChangeStatus = false
        mPathChangeStatus = false
    }

    protected fun activeStatus(){
        mProps.activeStatus()
        mTransform.activeStatus()
        mmBoundsChangeStatus = true
        mPathChangeStatus = true
    }


    //common props
    private fun setupPaintFill() {
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.setColor(mProps.fillColor)
        mPaint.setAlpha((getValidatedFillOpacity() * 255f).toInt())
    }

    //common props
    private fun setupPaintStroke() {
        mPaintStroke.reset()
        mPaintStroke.style = Paint.Style.STROKE
        mPaintStroke.isAntiAlias = true
        mPaintStroke.setColor( mProps.strokeColor)
        mPaintStroke.setAlpha((getValidatedStrokeOpacity() * 255f).toInt() )
        mPaintStroke.strokeCap = getStrokeCap()
        mPaintStroke.strokeMiter = mProps.strokeMiter
        mPaintStroke.strokeJoin = getStrokeJoin()
        mPaintStroke.strokeWidth = getValidatedStrokeWidth()
    }



//stroke start and end, morphing path
    fun setupStrokePath() : PathDrawable {
        if (stroke()){
            if(mProps.strokeStartEndChangeStatus || didPathChange()){
                mPathStroke.reset()
                if (getValidatedStrokeStart() != 0f || getValidatedStrokeEnd() != 1f) {
                    mPathMeasure.setPath(mPath, false)
                    //untouched dst if start >= end
                    mPathMeasure.getSegment(
                        (mPathMeasure.length * getValidatedStrokeStart()),
                        (mPathMeasure.length * getValidatedStrokeEnd()), mPathStroke, true
                    )
                    mPathStroke.rLineTo(0f, 0f)
                } else {
                    mPathStroke.set(mPath)
                }
            }
        }
        return this
    }

    private fun setupShadow(stroke: Boolean) {
        mPaintStroke.clearShadowLayer()
        mPaint.clearShadowLayer()

        if (isShadowEnabled && mProps.shadowColor != Color.TRANSPARENT){
            val paint: Paint = if (stroke) mPaintStroke else  mPaint
            paint.setShadowLayer(mProps.shadowRadius, mProps.shadowOffsetX,mProps.shadowOffsetY, mProps.shadowColor)
        }
    }


    fun setupTransform(): PathDrawable {
        mMatrix.reset()

        if (mTransform.rotation != 0f){
            mMatrix.postRotate(mTransform.rotation, mTransform.rotationOx, mTransform.rotationOy)
        }
        if (mTransform.scaleX != 1f || mTransform.scaleY != 1f){
            mMatrix.postScale(mTransform.scaleX, mTransform.scaleY, mTransform.scaleOriginX,  mTransform.scaleOriginY)
        }
        if (mTransform.translationX != 0f || mTransform.translationY != 0f){
            mMatrix.postTranslate(mTransform.translationX, mTransform.translationY)
        }
        return this
    }


    private fun stroke(): Boolean {
        return mProps.strokeColor != Color.TRANSPARENT
    }

    private fun fill(): Boolean {
        return  mProps.fillColor != Color.TRANSPARENT
    }

    private fun getValidatedFillOpacity(): Float {
        return SVGUtil.clamp(mProps.fillOpacity)
    }

    private fun getValidatedStrokeOpacity(): Float {
        return SVGUtil.clamp(mProps.strokeOpacity)
    }

    private fun getValidatedStrokeWidth(): Float {
        return SVGUtil.uClamp(mProps.strokeWidth, 1f)
    }
    private fun getStrokeCap(): Paint.Cap {
        return if (mProps.strokeCap == DrawableTypes.LineCap.ROUND) Paint.Cap.ROUND else (if (mProps.strokeCap == DrawableTypes.LineCap.SQUARE) Paint.Cap.SQUARE else Paint.Cap.BUTT)
    }

    private fun getStrokeJoin(): Paint.Join {
        return if (mProps.strokeJoin == DrawableTypes.LineJoin.ROUND) Paint.Join.ROUND else (if (mProps.strokeJoin == DrawableTypes.LineJoin.BEVEL) Paint.Join.BEVEL else Paint.Join.MITER)
    }
    private fun getFillRule(): Path.FillType = when(mProps.fillRule){
        DrawableTypes.FillRule.EVEN_ODD -> Path.FillType.EVEN_ODD
        DrawableTypes.FillRule.INVERSE_EVEN_ODD -> Path.FillType.INVERSE_EVEN_ODD
        DrawableTypes.FillRule.INVERSE_NONZERO -> Path.FillType.INVERSE_WINDING
        else -> Path.FillType.WINDING
    }


    private fun getValidatedStrokeStart(): Float {
        return SVGUtil.clamp(mProps.strokeStart)
    }

    private fun getValidatedStrokeEnd(): Float {
        return SVGUtil.clamp(mProps.strokeEnd)
    }

    private fun getValidatedShadowRadius(): Float {
        return SVGUtil.uClamp(mProps.shadowRadius)
    }


    private fun didBoundsChange():Boolean{
        return mmBoundsChangeStatus
    }
    private fun notifyBoundsChange(){
        mmBoundsChangeStatus = true
    }

    //notify to path stroke for be calculated again
    open fun notifyPathChanged(){
        mPathChangeStatus = true
    }

    protected fun didPathChange():Boolean{
        return mPathChangeStatus
    }


   inline fun makePath(block:Path.()->Boolean): PathDrawable {
        if(block(getPath())) notifyPathChanged()
        return this
    }

    fun setPath(path:Path): PathDrawable {
        mPath.reset()
        mPath.set(path)
        notifyPathChanged()
        return this
    }

    fun getPath():Path{
        return mPath
    }
    fun getStrokePath():Path{
        return mPathStroke
    }

    fun getFillPaint():Paint{
        return mPaint
    }
    fun getStrokePaint():Paint{
        return mPaintStroke
    }

    fun getTransform():Matrix{
        return mMatrix
    }

    fun getFillColor():Int{
        return mProps.fillColor
    }
    fun getStrokeColor():Int{
        return mProps.strokeColor
    }

    override fun setAlpha(alpha: Int) {

    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
    //not working
    @Deprecated("Not Working")
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @Deprecated("get nothing" ,
        ReplaceWith("getPaint", "Paint.colorFilter"))
    override fun getColorFilter(): ColorFilter? {
        return null
    }

    private var mColorFilter : ColorFilter? = null
    fun setColorFilterToPaints(colorFilter: ColorFilter?) : PathDrawable {
        mColorFilter = colorFilter
        return this
    }


    private fun Float.toDp():Float{
        return this * mDensity
    }
    private fun Float.pixelOrDp(p:Boolean):Float{
        return if(p) this else this * mDensity
    }


}

