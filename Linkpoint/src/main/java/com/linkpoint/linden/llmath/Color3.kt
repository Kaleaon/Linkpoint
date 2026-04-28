package com.linkpoint.linden.llmath

import kotlin.math.*

class Color3(var r: Float, var g: Float, var b: Float) {

    constructor() : this(0f, 0f, 0f)
    constructor(hex: String) : this(0f, 0f, 0f) {
        if (hex.length >= 6) {
            r = hex.substring(0, 2).toInt(16) / 255f
            g = hex.substring(2, 4).toInt(16) / 255f
            b = hex.substring(4, 6).toInt(16) / 255f
        }
    }
    constructor(c: Color4) : this(c.r, c.g, c.b)

    fun setToBlack(): Color3 { r = 0f; g = 0f; b = 0f; return this }
    fun setToWhite(): Color3 { r = 1f; g = 1f; b = 1f; return this }

    fun set(x: Float, y: Float, z: Float): Color3 { r = x; g = y; b = z; return this }
    fun set(other: Color3): Color3 { r = other.r; g = other.g; b = other.b; return this }

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

    fun brightness(): Float = (r + g + b) / 3f

    fun clamp() {
        r = r.coerceIn(0f, 1f)
        g = g.coerceIn(0f, 1f)
        b = b.coerceIn(0f, 1f)
    }

    fun exp() {
        r = kotlin.math.exp(r)
        g = kotlin.math.exp(g)
        b = kotlin.math.exp(b)
    }

    fun divide(col2: Color3): Color3 = Color3(r / col2.r, g / col2.g, b / col2.b)

    fun colorNorm(): Color3 {
        val l = length()
        return Color3(r / l, g / l, b / l)
    }

    fun setHSL(h: Float, s: Float, l: Float) {
        if (s < 0.00001f) {
            r = l; g = l; b = l
        } else {
            val interVal2 = if (l < 0.5f) l * (1f + s) else (l + s) - (s * l)
            val interVal1 = 2f * l - interVal2
            r = hueToRgb(interVal1, interVal2, h + 1f / 3f)
            g = hueToRgb(interVal1, interVal2, h)
            b = hueToRgb(interVal1, interVal2, h - 1f / 3f)
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

    operator fun plus(b: Color3): Color3 = Color3(r + b.r, g + b.g, this.b + b.b)
    operator fun minus(b: Color3): Color3 = Color3(r - b.r, g - b.g, this.b - b.b)
    operator fun times(b: Color3): Color3 = Color3(r * b.r, g * b.g, this.b * b.b)
    operator fun times(k: Float): Color3 = Color3(r * k, g * k, b * k)
    operator fun unaryMinus(): Color3 = Color3(1f - r, 1f - g, 1f - b)

    operator fun plusAssign(other: Color3) { r += other.r; g += other.g; b += other.b }
    operator fun minusAssign(other: Color3) { r -= other.r; g -= other.g; b -= other.b }
    operator fun timesAssign(other: Color3) { r *= other.r; g *= other.g; b *= other.b }
    operator fun timesAssign(k: Float) { r *= k; g *= k; b *= k }

    override fun equals(other: Any?): Boolean {
        if (other !is Color3) return false
        return r == other.r && g == other.g && b == other.b
    }
    override fun hashCode(): Int = 31 * (31 * r.hashCode() + g.hashCode()) + b.hashCode()
    override fun toString(): String = "{ $r, $g, $b }"

    companion object {
        val WHITE = Color3(1f, 1f, 1f)
        val BLACK = Color3(0f, 0f, 0f)
        val GREY  = Color3(0.5f, 0.5f, 0.5f)
    }
}

operator fun Float.times(c: Color3): Color3 = Color3(c.r * this, c.g * this, c.b * this)

fun distVec(a: Color3, b: Color3): Float {
    val x = a.r - b.r; val y = a.g - b.g; val z = a.b - b.b
    return sqrt(x * x + y * y + z * z)
}

fun distVecSquared(a: Color3, b: Color3): Float {
    val x = a.r - b.r; val y = a.g - b.g; val z = a.b - b.b
    return x * x + y * y + z * z
}

fun lerp(a: Color3, b: Color3, u: Float): Color3 =
    Color3(a.r + (b.r - a.r) * u, a.g + (b.g - a.g) * u, a.b + (b.b - a.b) * u)

private fun hueToRgb(v1: Float, v2: Float, vH: Float): Float {
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
