package com.jjkit.svgkit


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.jjkit.svgkit.utils.CommonProps
import com.jjkit.svgkit.utils.SVGUtil.clamp
import com.jjkit.svgkit.utils.SVGUtil.toMaxViewBox
import com.jjkit.svgkit.utils.SVGUtil.uClamp
import com.jjkit.svgkit.utils.SVGUtil.viewBoxToHeight
import com.jjkit.svgkit.utils.SVGUtil.viewBoxToWidth
import com.jjkit.svgkit.utils.PainterKit
import com.jjkit.svgkit.utils.SVGViewBox
import com.jjkit.svgkit.utils.TransformProps

//invalidateSelf not working in compose
// just Modifier.graphicsLayer{} is not calling draw with a state, parameter state.value draw is called
//every canvas should be individual in a new composable function or a drawable in a new canvas for static draw
//U can use CanvasDrawer for static or more options in the same canvas.
abstract class SVGDrawable(painterKit: PainterKit = PainterKit()) : Drawable()  {

    companion object{
        var isShadowEnabled :Boolean = true


        private var mDensity: Float = 1f

        //Viewport Density for a correct transformation
        fun setDensity(density: Float) {
            mDensity = density
        }
    }


    protected var mIsFillEnabled: Boolean = true
    protected var mIsStrokeEnabled:Boolean = true


    protected var mPainterKit = painterKit
    private val mProps = CommonProps()
    private val mTransform = TransformProps()

    protected var mCache: SVGDrawableCache = SVGDrawableCache()
    private var mmBoundsChangeStatus:Boolean = true
    private val mViewBoxCache = SVGViewBox()
    private val mViewBoxDensityCache = SVGViewBox()
    private var mPathChangeStatus : Boolean = true


    fun toDensity(v:Float):Float{
        return v * mDensity
    }

    open fun setFillColor(color: Int) : SVGDrawable {
        mProps.fillColor = color
        mProps.fillColorStatus = true
        return this
    }
    fun setFillOpacity(opacity: Float) : SVGDrawable {
        mProps.fillOpacity = opacity
        mProps.fillOpacityStatus = true
        return this
    }

    fun setFillRule(fillRule: DrawableTypes.FillRule) : SVGDrawable {
        mProps.fillRule = fillRule
        mProps.fillRuleStatus = true
        return this
    }

    fun setStrokeColor(color: Int) : SVGDrawable {
        mProps.strokeColor = color
        mProps.strokeColorStatus = true
        return this
    }
    fun setStrokeOpacity(opacity: Float) : SVGDrawable {
        mProps.strokeOpacity = opacity
        mProps.strokeOpacityStatus = true
        return this
    }
    fun setStrokeWidth(w: Float) : SVGDrawable {
        if(mProps.strokeWidth != w){
            mProps.strokeWidth = w
            mProps.strokeWidthStatus = true
        }
        return this
    }
    fun setStrokeCap(cap: DrawableTypes.LineCap) : SVGDrawable {
        mProps.strokeCap = cap
        mProps.strokeCapStatus = true
        return this
    }
    fun setStrokeJoin(join: DrawableTypes.LineJoin) : SVGDrawable {
        mProps.strokeJoin = join
        mProps.strokeJoinStatus = true
        return this
    }
    fun setStrokeMiter(value: Float) : SVGDrawable {
        mProps.strokeMiter = value
        mProps.strokeMiterStatus = true
        return this
    }
    fun setStrokeStart(v: Float) : SVGDrawable {
        if(mProps.strokeStart != v){
            mProps.strokeStart = v
            mProps.strokeStartEndChangeStatus = true
        }
        return this
    }
    fun setStrokeEnd(v: Float) : SVGDrawable {
        if(mProps.strokeEnd != v) {
            mProps.strokeEnd = v
            mProps.strokeStartEndChangeStatus = true
        }
        return this
    }

    fun setShadowColor(c: Int) : SVGDrawable {
        mProps.shadowColor = c
        return this
    }

    fun setShadowRadius(v: Float) : SVGDrawable {
        if(mProps.shadowRadius != v){
            mProps.shadowRadius = v
            mProps.shadowRadiusStatus = true
        }
        return this
    }
    fun setShadowOffset(x: Float,y:Float,percentageValue:Boolean = false) : SVGDrawable {
        if(mProps.shadowOffsetX != x || mProps.shadowOffsetY != y || mProps.shadowOffsetIsPercent != percentageValue ){
            mProps.shadowOffsetX = x
            mProps.shadowOffsetY = y
            mProps.shadowOffsetIsPercent = percentageValue
            mProps.shadowOffsetStatus = true
        }
        return this
    }

    //MARK: VIEWBOX

    fun setViewBox(x:Float, y:Float, w:Float, h:Float, aspect: SVGViewBox.MeetOrSlice = SVGViewBox.MeetOrSlice.MEET, align: SVGViewBox.Align = SVGViewBox.Align.xMidYMid): SVGDrawable {
        if(x != mViewBoxCache.x || y != mViewBoxCache.y || w != mViewBoxCache.width || h != mViewBoxCache.height || aspect != mViewBoxCache.aspect || align != mViewBoxCache.align){
            mViewBoxCache.x = x
            mViewBoxCache.y = y
            mViewBoxCache.width = w
            mViewBoxCache.height = h
            mViewBoxCache.aspect = aspect
            mViewBoxCache.align = align
            mViewBoxCache.changeStatus = true
        }

        return this
    }


    //MARK: TRANSFORM PROPS

    fun setTranslationX(x: Float,y:Float,percentageValue: Boolean = false) : SVGDrawable {
        if (mTransform.translationX != x || mTransform.translationY != y || mTransform.translationIsPercent != percentageValue){
            mTransform.translationX = x
            mTransform.translationY = y
            mTransform.translationIsPercent = percentageValue
            mTransform.translationStatus = true
        }
        return this
    }

    fun setRotation(v: Float): SVGDrawable {
        mTransform.rotation = v
        return this
    }

    fun setRotationOrigin(x: Float,y:Float,percentageValue: Boolean = false): SVGDrawable {
       if (mTransform.rotationOx != x || mTransform.rotationOy != y || mTransform.rotationIsPercent != percentageValue){
           mTransform.rotationOx = x
           mTransform.rotationOy = y
           mTransform.rotationIsPercent = percentageValue
           mTransform.rotationOriginStatus = true
       }
        return this
    }

    fun setScaleX(v: Float): SVGDrawable {
        mTransform.scaleX = v
        return this
    }
    fun setScaleY(v: Float): SVGDrawable {
        mTransform.scaleY = v
        return this
    }

    fun setScaleOrigin(x: Float,y:Float,percentageValue: Boolean = false) : SVGDrawable {
       if(mTransform.scaleOriginX != x || mTransform.scaleOriginY != y || mTransform.scaleOriginIsPercent != percentageValue){
           mTransform.scaleOriginX = x
           mTransform.scaleOriginY = y
           mTransform.scaleOriginIsPercent = percentageValue
           mTransform.scaleOriginStatus = true
       }
        return this
    }

    override fun onBoundsChange(bounds: Rect) {
        setSize(bounds.width().toFloat(),bounds.height().toFloat())
    }

    private fun setSize(w:Float,h:Float){
        if(w != mCache.width || h != mCache.height){
            mCache.width = w
            mCache.height = h
            notifyBoundsChange()
        }
    }

    protected fun validateSize():Boolean{
       return mCache.width > 0f && mCache.height > 0f
    }

    //should be called after setup()
    override fun draw(canvas: Canvas) {
        if(validateSize()) {
            onDraw(canvas,mPainterKit,mCache.width,mCache.height)
            cleanStatus()
        }
    }


    open fun setup(width:Float,height:Float){
        setSize(width,height)
        reset()
        if(validateSize()) {

            setupPath()

            viewBoxTransform()

            props()

            transform()



        }
    }

    protected open fun reset(){
        mPainterKit.paint.reset()
        mPainterKit.paintStroke.reset()
        mPainterKit.paintStroke.clearShadowLayer()
        mPainterKit.paint.clearShadowLayer()
        mPainterKit.matrix.reset()
    }

    protected open fun onDraw(canvas: Canvas, painterKit: PainterKit, width: Float, height: Float) {
        val checkpoint = canvas.save()
        canvas.concat(painterKit.matrix)
        try {
            drawContent(canvas,painterKit,width,height)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    protected open fun drawContent(canvas: Canvas, painterKit: PainterKit, width: Float, height: Float) {
        if (fill()) canvas.drawPath(mCache.path, painterKit.paint)
        if (stroke()) canvas.drawPath(mCache.pathStroke, painterKit.paintStroke)
    }



    protected open fun props() {
        mCache.path.fillType = getPathFillRule()

        if (fill()) {
            setupPaintFill()
            setupShadow(false)  //cached
        }
        if (stroke()) {
            setupPaintStroke()
            setupPathStroke() //cached
            if (!fill()) setupShadow(true)  //cached
        }
    }


    protected open fun cleanStatus(){
        inactiveStatus()
    }

    protected fun inactiveStatus(){
        mProps.inactiveStatus()
        mTransform.inactiveStatus()
        mViewBoxCache.changeStatus = false
        mmBoundsChangeStatus = false
        mPathChangeStatus = false
    }

    protected fun activeStatus(){
        mProps.activeStatus()
        mTransform.activeStatus()
        mViewBoxCache.changeStatus = true
        mmBoundsChangeStatus = true
        mPathChangeStatus = true
    }




    protected open fun setupPaintFill() {
//        mPainterKit.paint.reset()
        mPainterKit.paint.isAntiAlias = true
        mPainterKit.paint.style = Paint.Style.FILL
        mPainterKit.paint.setColor(mProps.fillColor)
        mPainterKit.paint.setAlpha((getValidatedFillOpacity() * 255f).toInt())
        mPainterKit.paint.colorFilter = mColorFilter
    }

    protected open fun setupPaintStroke() {
//        mPainterKit.paintStroke.reset()
        mPainterKit.paintStroke.style = Paint.Style.STROKE
        mPainterKit.paintStroke.isAntiAlias = true
        mPainterKit.paintStroke.setColor( mProps.strokeColor)
        mPainterKit.paintStroke.setAlpha((getValidatedStrokeOpacity() * 255f).toInt() )
        mPainterKit.paintStroke.strokeCap = getPaintStrokeCap()
        mPainterKit.paintStroke.strokeMiter = mProps.strokeMiter
        mPainterKit.paintStroke.strokeJoin = getPaintStrokeJoin()
        mPainterKit.paintStroke.colorFilter = mColorFilter
        setupPainStrokeWidth() //cached
    }



    private fun setupPainStrokeWidth(){
        if(mProps.strokeWidthStatus || didViewBoxChange()) {
             mCache.strokeWidth = if (validateViewBox()) {
                toMaxViewBox(
                    getValidatedStrokeWidth(),
                    mViewBoxCache,
                   mViewBoxDensityCache
                )
            } else toDensity(getValidatedStrokeWidth())
        }
        mPainterKit.paintStroke.strokeWidth =  mCache.strokeWidth
    }

    private fun setupPathStroke() {
        if(mProps.strokeStartEndChangeStatus || didPathChange()){
            mCache.pathStroke.reset()
            if (getValidatedStrokeStart() != 0f || getValidatedStrokeEnd() != 1f) {
                mPainterKit.pathMeasure.setPath(mCache.path, false)
                //untouched dst if start >= end
                mPainterKit.pathMeasure.getSegment(
                    (mPainterKit.pathMeasure.length * getValidatedStrokeStart()),
                    (mPainterKit.pathMeasure.length * getValidatedStrokeEnd()), mCache.pathStroke, true
                )
                mCache.pathStroke.rLineTo(0f, 0f)
            } else {
                mCache.pathStroke.set(mCache.path)
            }
        }
    }

    protected open fun setupShadow(stroke: Boolean) {

//        mPainterKit.paintStroke.clearShadowLayer()
//        mPainterKit.paint.clearShadowLayer()

        if (isShadowEnabled){

            val paint: Paint = if (stroke) mPainterKit.paintStroke else  mPainterKit.paint


            if (mProps.shadowOffsetIsPercent) {
                if((mProps.shadowOffsetStatus || didBoundsChange())){
                    mCache.shadowDx = mProps.shadowOffsetX * mCache.width
                    mCache.shadowDy = mProps.shadowOffsetY * mCache.height
                }
            } else if (mProps.shadowOffsetStatus || didViewBoxChange()){

                    if (validateViewBox()) {
                        mCache.shadowDx = (mProps.shadowOffsetX /  mViewBoxCache.width) * mViewBoxDensityCache.width
                        mCache.shadowDy = (mProps.shadowOffsetY /  mViewBoxCache.height) *  mViewBoxDensityCache.height
                    } else {
                        mCache.shadowDx = toDensity(mProps.shadowOffsetX)
                        mCache.shadowDy = toDensity(mProps.shadowOffsetY)
                    }
            }



            if(mProps.shadowRadiusStatus || didViewBoxChange()){
                mCache.shadowRadius  = if (validateViewBox()) toMaxViewBox(
                    getValidatedShadowRadius(),
                    mViewBoxCache,
                    mViewBoxDensityCache
                ) else toDensity(mProps.shadowRadius)
            }


            if (mProps.shadowColor != Color.TRANSPARENT)  paint.setShadowLayer( mCache.shadowRadius, mCache.shadowDx,mCache.shadowDy, mProps.shadowColor)
        }

    }


    protected open fun transform() {


        if (mTransform.rotationIsPercent ) {
            if((mTransform.rotationOriginStatus || didBoundsChange())){
                mCache.rotationOx = (mTransform.rotationOx * mCache.width)
                mCache.rotationOy = (mTransform.rotationOy * mCache.height)
            }
        }else if( mTransform.rotationOriginStatus || didViewBoxChange()){

            if (validateViewBox()) {
                mCache.rotationOx =  mViewBoxDensityCache.x + viewBoxToWidth(
                    mTransform.rotationOx,
                    mViewBoxCache,
                    mViewBoxDensityCache.width
                )
                mCache.rotationOy =  mViewBoxDensityCache.y + viewBoxToHeight(
                    mTransform.rotationOy,
                    mViewBoxCache,
                    mViewBoxDensityCache.height
                )
            } else {
                mCache.rotationOx = toDensity(mTransform.rotationOx)
                mCache.rotationOy = toDensity(mTransform.rotationOy)
            }
        }

        if (mTransform.rotation != 0f)  mPainterKit.matrix.postRotate(mTransform.rotation, mCache.rotationOx, mCache.rotationOy)




        if(mTransform.scaleOriginIsPercent  ) {
            if ((mTransform.scaleOriginStatus || didBoundsChange() )){
                mCache.scaleOx = (mTransform.scaleOriginX * mCache.width)
                mCache.scaleOy = (mTransform.scaleOriginY * mCache.height)
            }
        } else if( mTransform.scaleOriginStatus || didViewBoxChange()) {
            if (validateViewBox()) {
                mCache.scaleOx = mViewBoxDensityCache.x + viewBoxToWidth(
                    mTransform.scaleOriginX,
                    mViewBoxCache,
                    mViewBoxDensityCache.width
                )
                mCache.scaleOy = mViewBoxDensityCache.y + viewBoxToHeight(
                    mTransform.scaleOriginY,
                    mViewBoxCache,
                    mViewBoxDensityCache.height
                )
            } else {
                mCache.scaleOx = toDensity(mTransform.scaleOriginX)
                mCache.scaleOy = toDensity(mTransform.scaleOriginY)
            }
        }

        if (mTransform.scaleX != 1f || mTransform.scaleY != 1f)  mPainterKit.matrix.postScale(mTransform.scaleX, mTransform.scaleY, mCache.scaleOx, mCache.scaleOy)




        if (mTransform.translationIsPercent ) {
            if( (mTransform.translationStatus || didBoundsChange() )){
                mCache.translateX = (mTransform.translationX * mCache.width)
                mCache.translateY = (mTransform.translationY * mCache.height)
            }
        } else if(mTransform.translationStatus || didViewBoxChange()){
            if (validateViewBox()) {
                mCache.translateX =
                    (mTransform.translationX /  mViewBoxCache.width) * mViewBoxDensityCache.width
                mCache.translateY =
                    (mTransform.translationY /  mViewBoxCache.height) *  mViewBoxDensityCache.height
            } else {
                mCache.translateX = toDensity(mTransform.translationX)
                mCache.translateY = toDensity(mTransform.translationY)
            }
        }
        if (mTransform.translationX != 0f || mTransform.translationY != 0f)  mPainterKit.matrix.postTranslate( mCache.translateX,  mCache.translateY)

    }


    protected fun stroke(): Boolean {
        return mIsStrokeEnabled && mProps.strokeColor != Color.TRANSPARENT
    }

    protected fun fill(): Boolean {
        return mIsFillEnabled && mProps.fillColor != Color.TRANSPARENT
    }

    private fun getValidatedFillOpacity(): Float {
        return clamp(mProps.fillOpacity)
    }

    private fun getValidatedStrokeOpacity(): Float {
        return clamp(mProps.strokeOpacity)
    }

    private fun getValidatedStrokeWidth(): Float {
        return uClamp(mProps.strokeWidth, 1f)
    }
    private fun getPaintStrokeCap(): Paint.Cap {
        return if (mProps.strokeCap == DrawableTypes.LineCap.ROUND) Paint.Cap.ROUND else (if (mProps.strokeCap == DrawableTypes.LineCap.SQUARE) Paint.Cap.SQUARE else Paint.Cap.BUTT)
    }

    private fun getPaintStrokeJoin(): Paint.Join {
        return if (mProps.strokeJoin == DrawableTypes.LineJoin.ROUND) Paint.Join.ROUND else (if (mProps.strokeJoin == DrawableTypes.LineJoin.BEVEL) Paint.Join.BEVEL else Paint.Join.MITER)
    }
    private  fun getPathFillRule(): Path.FillType {
        return if (mProps.fillRule == DrawableTypes.FillRule.EVEN_ODD) Path.FillType.EVEN_ODD else Path.FillType.WINDING
    }
    private fun getValidatedStrokeStart(): Float {
        return clamp(mProps.strokeStart)
    }

    private fun getValidatedStrokeEnd(): Float {
        return clamp(mProps.strokeEnd)
    }

    private fun getValidatedShadowRadius(): Float {
        return uClamp(mProps.shadowRadius)
    }


    //-1 density mobile or pixel in html, 0f clear
    private fun validateViewBox(): Boolean {
        return  mViewBoxCache.width >= 0f && mViewBoxCache.height >= 0f
    }

    protected fun viewBoxTransform() {
        if(didViewBoxChange() || didBoundsChange() ){
            notifyViewBoxChange() //if come from bounds

            mCache.viewBoxMatrix.reset()

            if (validateViewBox()) {
                SVGViewBox.viewBoxToDensity(mViewBoxCache,mPainterKit.rect)
                SVGViewBox.matrixTransform(mViewBoxCache,mCache.width,mCache.height,mCache.viewBoxMatrix)
                mCache.viewBoxMatrix.mapRect(mPainterKit.rect)
                mViewBoxDensityCache.x = mPainterKit.rect.left
                mViewBoxDensityCache.y = mPainterKit.rect.top
                mViewBoxDensityCache.width = mPainterKit.rect.width()
                mViewBoxDensityCache.height = mPainterKit.rect.height()
            }
        }

        if(didPathChange() || didViewBoxChange()) {
                mCache.path.transform(mCache.viewBoxMatrix)
                notifyPathChanged() //if come from viewBox and path not changed
        }
    }
    private fun didViewBoxChange():Boolean{
        return mViewBoxCache.changeStatus
    }
    private fun notifyViewBoxChange(){
        mViewBoxCache.changeStatus = true
    }
    private fun didBoundsChange():Boolean{
        return mmBoundsChangeStatus
    }
    private fun notifyBoundsChange(){
        mmBoundsChangeStatus = true
    }

    protected fun notifyPathChanged(){
        mPathChangeStatus = true
    }

    protected fun didPathChange():Boolean{
        return mPathChangeStatus
    }


    //points * density , correct path
    protected abstract fun setupPath(path:Path = mCache.path)



    fun getPath():Path{
        return mCache.path
    }
    fun getStrokePath():Path{
        return mCache.pathStroke
    }
    fun getFillPaint():Paint{
        return mPainterKit.paint
    }
    fun getStrokePaint():Paint{
        return mPainterKit.paintStroke
    }
    fun getViewBoxTransform():Matrix{
        return mCache.viewBoxMatrix
    }
    fun getTransform():Matrix{
        return mPainterKit.matrix
    }
    fun getStrokeWidth():Float{
        return mCache.strokeWidth
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
    @Deprecated("Not Working", ReplaceWith("setColorFilterToPaints"))
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @Deprecated("get nothing" ,
        ReplaceWith("getPaint", "Paint.colorFilter"))
    override fun getColorFilter(): ColorFilter? {
        return null
    }

    private var mColorFilter : ColorFilter? = null

    fun setColorFilterToPaints(colorFilter: ColorFilter?) : SVGDrawable {
        mColorFilter = colorFilter
        return this
    }






}