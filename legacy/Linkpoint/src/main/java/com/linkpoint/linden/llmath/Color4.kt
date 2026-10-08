package com.linkpoint.linden.llmath

import kotlin.math.*

class Color4(var r: Float, var g: Float, var b: Float, var a: Float) {

    constructor() : this(0f, 0f, 0f, 1f)
    constructor(r: Float, g: Float, b: Float) : this(r, g, b, 1f)
    constructor(c: Color3, a: Float = 1f) : this(c.r, c.g, c.b, a)
    constructor(packed: UInt) : this(
        (packed and 0xFFu).toFloat() / 255f,
        ((packed shr 8) and 0xFFu).toFloat() / 255f,
        ((packed shr 16) and 0xFFu).toFloat() / 255f,
        (packed shr 24).toFloat() / 255f
    )

    fun setToBlack(): Color4 { r = 0f; g = 0f; b = 0f; a = 1f; return this }
    fun setToWhite(): Color4 { r = 1f; g = 1f; b = 1f; a = 1f; return this }

    fun set(x: Float, y: Float, z: Float, w: Float): Color4 { r = x; g = y; b = z; a = w; return this }
    fun set(x: Float, y: Float, z: Float): Color4 { r = x; g = y; b = z; return this }
    fun set(other: Color4): Color4 { r = other.r; g = other.g; b = other.b; a = other.a; return this }
    fun set(c: Color3): Color4 { r = c.r; g = c.g; b = c.b; return this }
    fun set(c: Color3, alpha: Float): Color4 { r = c.r; g = c.g; b = c.b; a = alpha; return this }
    fun set(c: Color4u): Color4 {
        val scale = 1f / 255f
        r = c.r.toFloat() * scale; g = c.g.toFloat() * scale
        b = c.b.toFloat() * scale; a = c.a.toFloat() * scale
        return this
    }

    fun setAlpha(alpha: Float): Color4 { a = alpha; return this }

    fun isOpaque(): Boolean = a == 1f

    fun length(): Float = sqrt(r * r + g * g + b * b)
    fun lengthSquared(): Float = r * r + g * g + b * b

    fun normalize(): Float {
        val mag = length()
        if (mag != 0f) {
            val oom = 1f / mag
            r *= oom; g *= oom; b *= oom
        }
        return mag
    }

    fun clamp() {
        r = r.coerceIn(0f, 1f)
        g = g.coerceIn(0f, 1f)
        b = b.coerceIn(0f, 1f)
        a = a.coerceIn(0f, 1f)
    }

    fun setHSL(h: Float, s: Float, l: Float) {
        if (s < 0.00001f) {
            r = l; g = l; b = l
        } else {
            val interVal2 = if (l < 0.5f) l * (1f + s) else (l + s) - (s * l)
            val interVal1 = 2f * l - interVal2
            r = hueToRgb4(interVal1, interVal2, h + 1f / 3f)
            g = hueToRgb4(interVal1, interVal2, h)
            b = hueToRgb4(interVal1, interVal2, h - 1f / 3f)
        }
    }

    fun calcHSL(): Triple<Float, Float, Float> {
        val varMin = minOf(r, g, b)
        val varMax = maxOf(r, g, b)
        val delMax = varMax - varMin
        val l = (varMax + varMin) / 2f
        var h = 0f
        var s = 0f
        if (delMax != 0f) {
            s = if (l < 0.5f) delMax / (varMax + varMin) else delMax / (2f - varMax - varMin)
            val delR = (((varMax - r) / 6f) + (delMax / 2f)) / delMax
            val delG = (((varMax - g) / 6f) + (delMax / 2f)) / delMax
            val delB = (((varMax - b) / 6f) + (delMax / 2f)) / delMax
            h = when {
                r >= varMax -> delB - delG
                g >= varMax -> 1f / 3f + delR - delB
                else        -> 2f / 3f + delG - delR
            }
            if (h < 0f) h += 1f
            if (h > 1f) h -= 1f
        }
        return Triple(h, s, l)
    }

    fun toColor3(): Color3 = Color3(r, g, b)

    operator fun get(idx: Int): Float = when (idx) { 0 -> r; 1 -> g; 2 -> b; else -> a }
    operator fun set(idx: Int, v: Float) { when (idx) { 0 -> r = v; 1 -> g = v; 2 -> b = v; else -> a = v } }

    operator fun plus(o: Color4): Color4 = Color4(r + o.r, g + o.g, b + o.b, a + o.a)
    operator fun minus(o: Color4): Color4 = Color4(r - o.r, g - o.g, b - o.b, a - o.a)
    operator fun times(o: Color4): Color4 = Color4(r * o.r, g * o.g, b * o.b, a * o.a)
    operator fun times(k: Float): Color4 = Color4(r * k, g * k, b * k, a)
    operator fun div(k: Float): Color4 = Color4(r / k, g / k, b / k, a)

    operator fun plusAssign(o: Color4) { r += o.r; g += o.g; b += o.b; a += o.a }
    operator fun minusAssign(o: Color4) { r -= o.r; g -= o.g; b -= o.b; a -= o.a }
    operator fun timesAssign(k: Float) { r *= k; g *= k; b *= k }
    operator fun timesAssign(o: Color4) { r *= o.r; g *= o.g; b *= o.b }

    fun scaleAlpha(k: Float): Color4 = Color4(r, g, b, a * k)
    fun scaleAlphaAssign(k: Float) { a *= k }

    operator fun compareTo(rhs: Color4): Int {
        if (r != rhs.r) return r.compareTo(rhs.r)
        if (g != rhs.g) return g.compareTo(rhs.g)
        if (b != rhs.b) return b.compareTo(rhs.b)
        return a.compareTo(rhs.a)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Color4) return false
        return r == other.r && g == other.g && b == other.b && a == other.a
    }
    override fun hashCode(): Int = listOf(r, g, b, a).fold(0) { acc, v -> 31 * acc + v.hashCode() }
    override fun toString(): String = "{ $r, $g, $b, $a }"

    companion object {
        val RED         = Color4(1f, 0f, 0f, 1f)
        val GREEN       = Color4(0f, 1f, 0f, 1f)
        val BLUE        = Color4(0f, 0f, 1f, 1f)
        val BLACK       = Color4(0f, 0f, 0f, 1f)
        val WHITE       = Color4(1f, 1f, 1f, 1f)
        val YELLOW      = Color4(1f, 1f, 0f, 1f)
        val MAGENTA     = Color4(1f, 0f, 1f, 1f)
        val CYAN        = Color4(0f, 1f, 1f, 1f)
        val SMOKE       = Color4(0.5f, 0.5f, 0.5f, 0.5f)
        val GREY        = Color4(0.5f, 0.5f, 0.5f, 1f)
        val ORANGE      = Color4(1f, 0.5f, 0f, 1f)
        val PURPLE      = Color4(0.6f, 0.2f, 0.8f, 1f)
        val PINK        = Color4(1f, 0.5f, 0.8f, 1f)
        val TRANSPARENT = Color4(0f, 0f, 0f, 0f)

        val GREY1       = Color4(0.8f, 0.8f, 0.8f, 1f)
        val GREY2       = Color4(0.6f, 0.6f, 0.6f, 1f)
        val GREY3       = Color4(0.4f, 0.4f, 0.4f, 1f)
        val GREY4       = Color4(0.3f, 0.3f, 0.3f, 1f)
        val SILHOUETTE  = Color4(0.05f, 0.05f, 0.05f, 1f)

        val RED1        = Color4(1f, 0f, 0f, 1f)
        val RED2        = Color4(0.6f, 0f, 0f, 1f)
        val RED3        = Color4(1f, 0.2f, 0.2f, 1f)
        val RED4        = Color4(0.5f, 0.1f, 0.1f, 1f)
        val RED5        = Color4(0.8f, 0.1f, 0f, 1f)

        val GREEN1      = Color4(0f, 1f, 0f, 1f)
        val GREEN2      = Color4(0f, 0.6f, 0f, 1f)
        val GREEN3      = Color4(0f, 0.4f, 0f, 1f)
        val GREEN4      = Color4(0f, 1f, 0.4f, 1f)
        val GREEN5      = Color4(0.2f, 0.6f, 0.4f, 1f)
        val GREEN6      = Color4(0.4f, 0.6f, 0.2f, 1f)

        val BLUE1       = Color4(0f, 0f, 1f, 1f)
        val BLUE2       = Color4(0f, 0.4f, 1f, 1f)
        val BLUE3       = Color4(0.2f, 0.2f, 0.8f, 1f)
        val BLUE4       = Color4(0f, 0f, 0.6f, 1f)
        val BLUE5       = Color4(0.4f, 0.2f, 1f, 1f)
        val BLUE6       = Color4(0.4f, 0.5f, 1f, 1f)

        val YELLOW1     = Color4(1f, 1f, 0f, 1f)
        val YELLOW2     = Color4(0.6f, 0.6f, 0f, 1f)
        val YELLOW3     = Color4(0.8f, 1f, 0.2f, 1f)
        val YELLOW4     = Color4(1f, 1f, 0.4f, 1f)
        val YELLOW5     = Color4(0.6f, 0.4f, 0.2f, 1f)
        val YELLOW6     = Color4(1f, 0.8f, 0.4f, 1f)
        val YELLOW7     = Color4(0.8f, 0.8f, 0f, 1f)
        val YELLOW8     = Color4(0.8f, 0.8f, 0.2f, 1f)
        val YELLOW9     = Color4(0.8f, 0.8f, 0.4f, 1f)

        val ORANGE1     = Color4(1f, 0.8f, 0f, 1f)
        val ORANGE2     = Color4(1f, 0.6f, 0f, 1f)
        val ORANGE3     = Color4(1f, 0.4f, 0.2f, 1f)
        val ORANGE4     = Color4(0.8f, 0.4f, 0f, 1f)
        val ORANGE5     = Color4(0.9f, 0.5f, 0f, 1f)
        val ORANGE6     = Color4(1f, 0.8f, 0.2f, 1f)

        val MAGENTA1    = Color4(1f, 0f, 1f, 1f)
        val MAGENTA2    = Color4(0.6f, 0.2f, 0.4f, 1f)
        val MAGENTA3    = Color4(1f, 0.4f, 0.6f, 1f)
        val MAGENTA4    = Color4(1f, 0.2f, 0.8f, 1f)

        val PURPLE1     = Color4(0.6f, 0.2f, 0.8f, 1f)
        val PURPLE2     = Color4(0.8f, 0.2f, 1f, 1f)
        val PURPLE3     = Color4(0.6f, 0f, 1f, 1f)
        val PURPLE4     = Color4(0.4f, 0f, 0.8f, 1f)
        val PURPLE5     = Color4(0.6f, 0f, 0.8f, 1f)
        val PURPLE6     = Color4(0.8f, 0f, 0.6f, 1f)

        val PINK1       = Color4(1f, 0.5f, 0.8f, 1f)
        val PINK2       = Color4(1f, 0.8f, 0.9f, 1f)

        val CYAN1       = Color4(0f, 1f, 1f, 1f)
        val CYAN2       = Color4(0.4f, 0.8f, 0.8f, 1f)
        val CYAN3       = Color4(0f, 1f, 0.6f, 1f)
        val CYAN4       = Color4(0.6f, 1f, 1f, 1f)
        val CYAN5       = Color4(0.2f, 0.6f, 1f, 1f)
        val CYAN6       = Color4(0.2f, 0.6f, 0.6f, 1f)

        fun fromColor4u(c: Color4u): Color4 {
            val scale = 1f / 255f
            return Color4(c.r.toFloat() * scale, c.g.toFloat() * scale,
                          c.b.toFloat() * scale, c.a.toFloat() * scale)
        }
    }
}

operator fun Float.times(c: Color4): Color4 = Color4(c.r * this, c.g * this, c.b * this, c.a)

fun distVec(a: Color4, b: Color4): Float = (a - b).length()
fun distVecSquared(a: Color4, b: Color4): Float = (a - b).lengthSquared()

fun lerp(a: Color4, b: Color4, u: Float): Color4 = Color4(
    a.r + (b.r - a.r) * u,
    a.g + (b.g - a.g) * u,
    a.b + (b.b - a.b) * u,
    a.a + (b.a - a.a) * u
)

private fun hueToRgb4(v1: Float, v2: Float, vH: Float): Float {
    var h = vH
    if (h < 0f) h += 1f
    if (h > 1f) h -= 1f
    return when {
        6f * h < 1f -> v1 + (v2 - v1) * 6f * h
        2f * h < 1f -> v2
        3f * h < 2f -> v1 + (v2 - v1) * (2f / 3f - h) * 6f
        else        -> v1
    }
}
