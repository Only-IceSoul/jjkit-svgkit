
```kotlin

@Composable
fun LoadingCircle(){

    val infiniteTransition = rememberInfiniteTransition(label = "JJCircleProgress")
    val animValue = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f ,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "JJCircleProgressRotation"
    )

    val animValue2 = infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 0.4f ,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "JJCircleProgressRotation2"
    )

    val m = MaterialTheme.colorScheme
    val colors = remember {
        intArrayOf(m.primary.toArgb(), Color.Gray.toArgb())
    }
    val circle = remember {
        PathDrawable().setStrokeColor(Color.Red.toArgb())
            .setStrokeWidth(5f)
            .setStrokeEnd(0.5f)
            //individual setups
            .setupPaints()
    }

    val svgCircle = remember {
        SVGCircle()
            .setR(40f)
            .setCy(25f).setCx(25f)
            .setViewBox(0f,0f,50f,50f)
            .setStrokeColor(m.primary.toArgb())
            .setStrokeWidth(5f)
            .setStrokeEnd(0.5f)
            .setFillColor(Color.Transparent.toArgb())
    }


    Canvas(
        modifier = Modifier
            .graphicsLayer {
                rotationZ = animValue.value
            }
            .size(60.dp) 
    ) {

        drawIntoCanvas {

            //SVG DRAWABLE

            //ANIMATION
            svgCircle.setStrokeStart(  SVGUtil.clamp( 1f - (-animValue2.value),0f,1f,1f,0f))
            svgCircle.setStrokeEnd( SVGUtil.clamp( animValue2.value,0f,1f,1f,1f)  )

            //SETUP PAINT PATH STROKE
            svgCircle.setup(size.width,size.height)

            //DRAW

            //DRAW A BACKGROUND WITH BASE PATH
            svgCircle.getStrokePaint().color = colors[1]
            CanvasDrawer.drawPath(it.nativeCanvas,svgCircle.getPath(),svgCircle.getStrokePaint())

            //RESTORE COLOR AND DRAW THE STROKE ANIMATION
            svgCircle.getStrokePaint().color = svgCircle.getStrokeColor()
            svgCircle.draw(it.nativeCanvas)

        }

        OR

        drawIntoCanvas{
              //PATH DRAWABLE 
            circle.setBounds(0,0,size.width.toInt(),size.height.toInt())

                CanvasDrawer.oneSetup {
                    circle.makePath {
                        addCircle(size.width/2f,size.height/2f,min(size.width,size.height),
                            Path.Direction.CW)
                        return@makePath true
                    }
                }

            //***ANIMATION**

            circle.setStrokeStart(  ModUtil.clamp( 1f - (-animValue2.value),0f,1f,1f,0f))
            circle.setStrokeEnd( ModUtil.clamp( animValue2.value,0f,1f,1f,1f)  )

            //***SETUP**
            //
            // SETUP THE ANIMATION
            circle.setupStrokePath()

            //**DRAW***

            //DRAW BACKGROUND
           circle.getStrokePaint().color = colors[1]
           CanvasDrawer.drawPath(it.nativeCanvas,circle.getPath(),circle.getStrokePaint())

            //RESTORE DRAW ANIMATION
            circle.getStrokePaint().color = colors[0]
            circle.draw(it.nativeCanvas)

        }


    }


}


```