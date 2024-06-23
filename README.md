# SVGKit

Draw svg path or custom paths with SVGKit

<img src="./demo.jpg" width="300" >

## INSTALLATION

1.-Add it in your root build.gradle at the end of repositories:
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
2.- Add the dependency
```
dependencies {
	        implementation 'com.github.Only-IceSoul:jjkit-svgkit:1.2'
	}
```

## USAGE


```kotlin


        Mainactivity
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            SVGPathParser.setDensity(resources.displayMetrics.density)
            SVGViewBox.setDensity(resources.displayMetrics.density)
            SVGDrawable.setDensity(resources.displayMetrics.density)
            PathDrawable.setDensity(resources.displayMetrics.density)
    
```

```kotlin

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

    Canvas(){

        drawIntoCanvas {

                //SVG DRAWABLE

                //ANIMATION  0 - 1f
                svgCircle.setStrokeStart(  SVGUtil.clamp( 1f - (-animValue.value),0f,1f,1f,0f))
                svgCircle.setStrokeEnd( SVGUtil.clamp( animValue.value,0f,1f,1f,1f)  )

                //SETUP PAINT PATH STROKE
                svgCircle.setup(size.width,size.height)

                //DRAW

                //DRAW A BACKGROUND 
                svgCircle.getStrokePaint().color = myColorGray
                CanvasDrawer.drawPath(it.nativeCanvas,svgCircle.getPath(),svgCircle.getStrokePaint())

                //RESTORE COLOR AND DRAW THE STROKE ANIMATION
                svgCircle.getStrokePaint().color = svgCircle.getStrokeColor()
                svgCircle.draw(it.nativeCanvas)
        }
    }

   
```

 [Example](./example.md)

## LICENSE 

**Apache 2.0**

