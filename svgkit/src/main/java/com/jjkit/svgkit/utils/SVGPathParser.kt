package com.jjkit.svgkit.utils

import android.graphics.Path
import android.graphics.RectF
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object SVGPathParser {
       
    private var mScale: Float = 1f
    
    private var i = 0
    private var l = 0
    private var s: String = ""
    private var mPath = Path()
    
    private var mPenX = 0f
    private var mPenY = 0f
    private var mPivotX = 0f
    private var mPivotY = 0f
    private var mPenDownX = 0f
    private var mPenDownY = 0f
    private var mPenDown = false

    //viewport density for a correct transformation, 1f = pixel unit
    fun setDensity(density:Float){
        mScale = density
    }

    fun parse(d: String): Path {
        parse(d, mPath)
       return mPath
    }
    fun parse(d: String,path:Path) {
        path.reset()
    
        var prev_cmd = ' '
        l = d.length
        s = d
        i = 0
    
        mPenX = 0f
        mPenY = 0f
        mPivotX = 0f
        mPivotY = 0f
        mPenDownX = 0f
        mPenDownY = 0f
        mPenDown = false
    
        while (i < l) {
            skip_spaces()
    
            if (i >= l) {
               break
            }
    
            val has_prev_cmd = prev_cmd != ' '
            val first_char = s[i]
    
            if (!has_prev_cmd && first_char != 'M' && first_char != 'm') {
                // The first segment must be a MoveTo.
                throw Error(String.format("Unexpected character '%c' (i=%d, s=%s)", first_char, i, s))
            }
    

            var is_implicit_move_to: Boolean
            var cmd : Char
            if (is_cmd(first_char)) {
                is_implicit_move_to = false
                cmd = first_char
                i += 1;
            } else if (is_number_start(first_char) && has_prev_cmd) {
                if (prev_cmd == 'Z' || prev_cmd == 'z') {
                    // ClosePath cannot be followed by a number.
                    throw Error(String.format("Unexpected number after 'z' (s=%s)", s))
                }
    
                if (prev_cmd == 'M' || prev_cmd == 'm') {
                    // 'If a moveto is followed by multiple pairs of coordinates,
                    // the subsequent pairs are treated as implicit lineto commands.'
                    // So we parse them as LineTo.
                    is_implicit_move_to = true
                    cmd = if (is_absolute(prev_cmd)) {
                        'L'
                    } else {
                        'l'
                    }
                } else {
                    is_implicit_move_to = false
                    cmd = prev_cmd;
                }
            } else {
                throw  Error(String.format("Unexpected character '%c' (i=%d, s=%s)", first_char, i, s))
            }
    
            val absolute = is_absolute(cmd)
            
            when(cmd) {
                'm' -> {
                    move(parse_list_number(), parse_list_number(),path)

                }
                 'M' -> {
                     moveTo(parse_list_number(), parse_list_number(),path)
                }
                 'l' -> {
                     line(parse_list_number(), parse_list_number(),path)

                }
                 'L' -> {
                     lineTo(parse_list_number(), parse_list_number(),path)

                }
                 'h' -> {
                     line(parse_list_number(), 0f,path)

                }
                 'H' -> {
                     lineTo(parse_list_number(), mPenY,path)

                }
                 'v' -> {
                     line(0f, parse_list_number(),path)

                }
                 'V' -> {
                     lineTo(mPenX, parse_list_number(),path)

                }
                 'c' -> {
                     curve(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 'C' -> {
                     curveTo(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 's' -> {
                     smoothCurve(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 'S' -> {
                     smoothCurveTo(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 'q' -> {
                     quadraticBezierCurve(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 'Q' -> {
                     quadraticBezierCurveTo(parse_list_number(), parse_list_number(), parse_list_number(), parse_list_number(),path)

                }
                 't' -> {
                     smoothQuadraticBezierCurve(parse_list_number(), parse_list_number(),path)

                }
                 'T' -> {
                     smoothQuadraticBezierCurveTo(parse_list_number(), parse_list_number(),path)
                }
                 'a' -> {
                     arc(parse_list_number(), parse_list_number(), parse_list_number(), parse_flag(), parse_flag(), parse_list_number(), parse_list_number(),path)
                }
                 'A' ->{
                     arcTo(parse_list_number(), parse_list_number(), parse_list_number(), parse_flag(), parse_flag(), parse_list_number(), parse_list_number(),path)
                }
                'z',
                'Z'-> {
                    close(path)
                }
                else -> {
                    throw  Error(String.format("Unexpected comand '%c' (s=%s)", cmd, s))
                }
            }


            prev_cmd = if (is_implicit_move_to) {
                    if (absolute) {
                        'M';
                    } else {
                        'm';
                    }
                } else {
                    cmd;
                }
    
        }

    }

    private fun move(x:Float,y:Float,path:Path) {
        moveTo(x + mPenX, y + mPenY,path);
    }

    private fun moveTo(x:Float,y:Float,path:Path) {
        mPenDownX = x
        mPivotX = x
        mPenX = x
        mPenDownY = y
        mPivotY = y
        mPenY = y
        path.moveTo(x * mScale, y * mScale);
    }

    private fun line(x: Float, y: Float,path:Path) {
        lineTo(x + mPenX, y + mPenY,path)
    }

    private fun lineTo(x: Float, y: Float,path:Path) {
        setPenDown()
        mPenX = x
        mPivotX = mPenX
        mPenY = y
        mPivotY = mPenY
        path.lineTo(x * mScale, y * mScale)

    }

    private fun curve(c1x: Float, c1y: Float, c2x: Float, c2y: Float, ex: Float, ey: Float,path:Path) {
        curveTo(
            c1x + mPenX,
            c1y + mPenY,
            c2x + mPenX,
            c2y + mPenY,
            ex + mPenX,
            ey + mPenY,
            path
        )
    }

    private fun curveTo(c1x: Float, c1y: Float, c2x: Float, c2y: Float, ex: Float, ey: Float,path:Path) {
        mPivotX = c2x
        mPivotY = c2y
        cubicTo(c1x, c1y, c2x, c2y, ex, ey,path)
    }

    private fun cubicTo(c1x: Float, c1y: Float, c2x: Float, c2y: Float, ex: Float, ey: Float,path:Path) {
        setPenDown()
        mPenX = ex
        mPenY = ey
        path.cubicTo(
            c1x * mScale,
            c1y * mScale,
            c2x * mScale,
            c2y * mScale,
            ex * mScale,
            ey * mScale
        )
    }

    private fun smoothCurve(c1x: Float, c1y: Float, ex: Float, ey: Float,path:Path) {
        smoothCurveTo(
            c1x + mPenX,
            c1y + mPenY,
            ex + mPenX,
            ey + mPenY,
            path
        )
    }

    private fun smoothCurveTo(c1x: Float, c1y: Float, ex: Float, ey: Float, path: Path) {
        val c1xx = (mPenX * 2) - mPivotX
        val c1yy = (mPenY * 2) - mPivotY
        mPivotX = c1x
        mPivotY = c1y
        cubicTo(c1xx, c1yy, c1x, c1y, ex, ey, path)
    }

    private fun quadraticBezierCurve(c1x: Float, c1y: Float, c2x: Float, c2y: Float,path: Path) {
        quadraticBezierCurveTo(
            c1x + mPenX,
            c1y + mPenY,
            c2x + mPenX,
            c2y + mPenY,
            path
        )
    }

    private fun quadraticBezierCurveTo(c1x: Float, c1y: Float, c2x: Float, c2y: Float,path: Path) {
        mPivotX = c1x
        mPivotY = c1y
        val c2xx = (c2x + c1x * 2) / 3
        val c2yy = (c2y + c1y * 2) / 3
        val c1xx = (mPenX + c1x * 2) / 3
        val c1yy = (mPenY + c1y * 2) / 3
        cubicTo(c1xx, c1yy, c2xx, c2yy, c2x, c2y, path)
    }

    private fun smoothQuadraticBezierCurve(c1x: Float, c1y: Float,path: Path) {
        smoothQuadraticBezierCurveTo(c1x + mPenX, c1y + mPenY,path)
    }

    private fun smoothQuadraticBezierCurveTo(c1x: Float, c1y: Float,path: Path) {
        val c1xx = (mPenX * 2) - mPivotX
        val c1yy = (mPenY * 2) - mPivotY
        quadraticBezierCurveTo(c1xx, c1yy, c1x, c1y,path)
    }

    private fun arc(rx: Float, ry: Float, rotation: Float, outer: Boolean, clockwise: Boolean, x: Float, y: Float, path: Path) {
        arcTo(rx, ry, rotation, outer, clockwise, x + mPenX, y + mPenY,path)
    }

    private fun arcTo(rx: Float, ry: Float, rotation: Float, outer: Boolean, clockwise: Boolean, x: Float, y: Float, path: Path) {

        val tX = mPenX
        val tY = mPenY

        var ryMain = abs(if (ry == 0f) (if (rx == 0f) (y - tY) else rx) else ry)
        var rxMain = abs(if (rx == 0f) (x - tX) else rx)

        if (rxMain == 0f || ryMain == 0f || (x == tX && y == tY)) {
            lineTo(x, y,path)
            return
        }

        val rad = Math.toRadians(rotation.toDouble()).toFloat()
        val cos = cos(rad)
        val sin = sin(rad)
        var xMain = x - tX
        var yMain = y - tY

        // Ellipse Center
        var cx = cos * xMain / 2 + sin * yMain / 2
        var cy = -sin * xMain / 2 + cos * yMain / 2
        val rxry = rxMain * rxMain * ryMain * ryMain
        val rycx = ryMain * ryMain * cx * cx
        val rxcy = rxMain * rxMain * cy * cy
        var a = rxry - rxcy - rycx

        if (a < 0) {
            a = sqrt(1 - a / rxry)
            rxMain *= a
            ryMain *= a
            cx = xMain / 2
            cy = yMain / 2
        } else {
            a = sqrt(a / (rxcy + rycx))

            if (outer == clockwise) {
                a = -a
            }
            val cxd = -a * cy * rxMain / ryMain
            val cyd = a * cx * ryMain / rxMain
            cx = cos * cxd - sin * cyd + xMain / 2
            cy = sin * cxd + cos * cyd + yMain / 2
        }

        // Rotation + Scale Transform
        val xx = cos / rxMain
        val yx = sin / rxMain
        val xy = -sin / ryMain
        val yy = cos / ryMain

        // Start and End Angle
        val sa = atan2(xy * -cx + yy * -cy, xx * -cx + yx * -cy)
        val ea = atan2(xy * (xMain - cx) + yy * (yMain - cy), xx * (xMain - cx) + yx * (yMain - cy))
              

        cx += tX
        cy += tY
        xMain += tX
        yMain += tY

        setPenDown()

        mPivotX = xMain
        mPenX = mPivotX
        mPivotY = yMain
        mPenY = mPivotY

        if (rxMain != ryMain || rad != 0f) {
            arcToBezier(cx, cy, rxMain, ryMain, sa, ea, clockwise, rad)
        } else {
            val start = Math.toDegrees(sa.toDouble()).toFloat()
            val end = Math.toDegrees(ea.toDouble()).toFloat()
            var sweep = abs((start - end) % 360f)

            if (outer) {
                if (sweep < 180) {
                    sweep = 360 - sweep
                }
            } else {
                if (sweep > 180) {
                    sweep = 360 - sweep
                }
            }

            if (!clockwise) {
                sweep = -sweep
            }

            val oval = RectF(
                (cx - rxMain) * mScale,
                (cy - rxMain) * mScale,
                (cx + rxMain) * mScale,
                (cy + rxMain) * mScale
            )


            path.arcTo(oval, start, sweep)
        }
    }

    private fun arcToBezier(cx: Float, cy: Float, rx: Float, ry: Float, sa: Float, ea: Float, clockwise: Boolean, rad: Float) {
        // Inverse Rotation + Scale Transform

        val cos = cos(rad)
        val sin = sin(rad)
        val xx = cos * rx
        val yx = -sin * ry
        val xy = sin * rx
        val yy = cos * ry

        // Bezier Curve Approximation
        var arc = ea - sa
        if (arc < 0 && clockwise) {
            arc += (PI * 2).toFloat()
        } else if (arc > 0 && !clockwise) {
            arc -= (PI * 2).toFloat()
        }

        val n = ceil(abs(round(arc / (Math.PI / 2))))
            .toInt()

        val step = arc / n
        val k = (4f / 3f) * tan(step / 4f)

        var x = cos(sa)
        var y = sin(sa)
        
        var saMain = sa
        
        for (i in 0 until n) {
            val cp1x = x - k * y
            val cp1y = y + k * x

            saMain += step
            x = cos(saMain)
            y = sin(saMain)

            val cp2x = x + k * y
            val cp2y = y - k * x

            val c1x = (cx + xx * cp1x + yx * cp1y)
            val c1y = (cy + xy * cp1x + yy * cp1y)
            val c2x = (cx + xx * cp2x + yx * cp2y)
            val c2y = (cy + xy * cp2x + yy * cp2y)
            val ex = (cx + xx * x + yx * y)
            val ey = (cy + xy * x + yy * y)

            mPath.cubicTo(
                c1x * mScale,
                c1y * mScale,
                c2x * mScale,
                c2y * mScale,
                ex * mScale,
                ey * mScale
            )
        }
    }


    private fun close(path:Path) {
        if (mPenDown) {
            mPenX = mPenDownX
            mPenY = mPenDownY
            mPenDown = false
            path.close()
        }
    }
    private fun setPenDown() {
        if (!mPenDown) {
            mPenDownX = mPenX
            mPenDownY = mPenY
            mPenDown = true
        }
    }

    private fun round(value: Double): Float {
        val multiplier = 10f.pow(4f)
        return round(value * multiplier) / multiplier
    }

    private fun skip_spaces() {
        while (i < l && (s[i]).isWhitespace() ) i++
    }

    private fun is_cmd(c: Char): Boolean {
        when (c) {
            'M', 'm', 'Z', 'z', 'L', 'l', 'H', 'h', 'V', 'v', 'C', 'c', 'S', 's', 'Q', 'q', 'T', 't', 'A', 'a' -> return true
        }
        return false
    }

    private fun is_number_start(c: Char): Boolean {
        return (c in '0'..'9') || (c == '.') || (c == '-') || (c == '+')
    }

    private fun is_absolute(c: Char): Boolean {
        return c.isUpperCase()
    }

    // By the SVG spec 'large-arc' and 'sweep' must contain only one char
    // and can be written without any separators, e.g.: 10 20 30 01 10 20.
    private fun parse_flag(): Boolean {
        skip_spaces()

        val c = s[i]
        when (c) {
            '0', '1' -> {
                i += 1
                if (i < l && s[i] == ',') {
                    i += 1
                }
                skip_spaces()
            }

            else -> throw Error(
                String.format(
                    "Unexpected flag '%c' (i=%d, s=%s)",
                    c,
                    i,
                    s
                )
            )
        }
        return c == '1'
    }

    private fun parse_list_number(): Float {
        if (i == l) {
            throw Error(String.format("Unexpected end (s=%s)", s))
        }

        val n = parse_number()
        skip_spaces()
        parse_list_separator()

        return n
    }

    private fun parse_number(): Float {
        // Strip off leading whitespaces.
        skip_spaces()

        if (i == l) {
            throw Error(String.format("Unexpected end (s=%s)", s))
        }

        val start = i

        var c = s[i]

        // Consume sign.
        if (c == '-' || c == '+') {
            i += 1
            c = s[i]
        }

        // Consume integer.
        if (c in '0'..'9') {
            skip_digits()
            if (i < l) {
                c = s[i]
            }
        } else if (c != '.') {
            throw Error(
                String.format(
                    "Invalid number formating character '%c' (i=%d, s=%s)",
                    c,
                    i,
                    s
                )
            )
        }

        // Consume fraction.
        if (c == '.') {
            i += 1
            skip_digits()
            if (i < l) {
                c = s[i]
            }
        }

        if ((c == 'e' || c == 'E') && i + 1 < l) {
            val c2 = s[i + 1]
            // Check for `em`/`ex`.
            if (c2 != 'm' && c2 != 'x') {
                i += 1
                c = s[i]

                if (c == '+' || c == '-') {
                    i += 1
                    skip_digits()
                } else if (c in '0'..'9') {
                    skip_digits()
                } else {
                    throw Error(
                        String.format(
                            "Invalid number formating character '%c' (i=%d, s=%s)",
                            c,
                            i,
                            s
                        )
                    )
                }
            }
        }

        val num = s.substring(start, i)
        val n = num.toFloat()

        // inf, nan, etc. are an error.
        if (n.isInfinite() || n.isNaN()) {
            throw Error(
                String.format(
                    "Invalid number '%s' (start=%d, i=%d, s=%s)",
                    num,
                    start,
                    i,
                    s
                )
            )
        }

        return n
    }

    private fun parse_list_separator() {
        if (i < l && s[i] == ',') {
            i += 1
        }
    }

    private fun skip_digits() {
        while ( i < l && (s[i]).isDigit() ) i++
    }


}