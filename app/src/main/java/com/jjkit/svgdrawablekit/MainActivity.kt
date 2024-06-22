package com.jjkit.svgdrawablekit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jjkit.svgdrawablekit.ui.theme.SVGDrawableKitTheme
import com.jjkit.svgkit.CanvasDrawer
import com.jjkit.svgkit.PathDrawable
import com.jjkit.svgkit.SVGCircle
import com.jjkit.svgkit.SVGDrawable
import com.jjkit.svgkit.utils.SVGPathParser
import com.jjkit.svgkit.utils.SVGUtil
import com.jjkit.svgkit.utils.SVGViewBox

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SVGPathParser.setDensity(resources.displayMetrics.density)
        SVGViewBox.setDensity(resources.displayMetrics.density)
        SVGDrawable.setDensity(resources.displayMetrics.density)
        PathDrawable.setDensity(resources.displayMetrics.density)

        enableEdgeToEdge()

        setContent {
            SVGDrawableKitTheme {


                        JJCircularProgress()


            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SVGDrawableKitTheme {
        Greeting("Android")
    }
}

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
            .size(60.dp) // Note size of 100 dp here
    ) {

        drawIntoCanvas {

            //PATH DRAWABLE
//            circle.setBounds(0,0,size.width.toInt(),size.height.toInt())

//                CanvasDrawer.oneSetup {
//                    circle.makePath {
//                        addCircle(size.width/2f,size.height/2f,min(size.width,size.height),
//                            Path.Direction.CW)
//                        return@makePath true
//                    }
//                }
//
            //***ANIMATION**

//            circle.setStrokeStart(  ModUtil.clamp( 1f - (-animValue2.value),0f,1f,1f,0f))
//            circle.setStrokeEnd( ModUtil.clamp( animValue2.value,0f,1f,1f,1f)  )

            //***SETUP**
            //
            // SETUP THE ANIMATION
//            circle.setupStrokePath()

            //**DRAW***

            //DRAW BACKGROUND
//            circle.getStrokePaint().color = colors[1]
//            CanvasDrawer.drawPath(it.nativeCanvas,circle.getPath(),circle.getStrokePaint())

            //RESTORE DRAW ANIMATION
//            circle.getStrokePaint().color = colors[0]
//            circle.draw(it.nativeCanvas)


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
    }


}

@Composable
fun JJCircularProgress(){


            Column(modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
                .background(Color.Gray.copy(alpha = 0.3f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingCircle()
            }
}