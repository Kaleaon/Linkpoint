package com.linkpoint.linden.llmath

import kotlin.math.*

class Color4u(var r: UByte, var g: UByte, var b: UByte, var a: UByte) {

    constructor() : this(0u, 0u, 0u, 255u)
    constructor(r: UByte, g: UByte, b: UByte) : this(r, g, b, 255u)

    fun setToBlack(): Color4u { r = 0u; g = 0u; b = 0u; a = 255u; return this }
    fun setToWhite(): Color4u { r = 255u; g = 255u; b = 255u; a = 255u; return this }

    fun set(rv: UByte, gv: UByte, bv: UByte, av: UByte): Color4u { r = rv; g = gv; b = bv; a = av; return this }
    fun set(rv: UByte, gv: UByte, bv: UByte): Color4u { r = rv; g = gv; b = bv; return this }
    fun set(other: Color4u): Color4u { r = other.r; g = other.g; b = other.b; a = other.a; return this }

    fun setAlpha(av: UByte): Color4u { a = av; return this }

    fun length(): Float {
        val rf = r.toFloat(); val gf = g.toFloat(); val bf = b.toFloat()
        return sqrt(rf * rf + gf * gf + bf * bf)
    }

    fun lengthSquared(): Float {
        val rf = r.toFloat(); val gf = g.toFloat(); val bf = b.toFloat()
        return rf * rf + gf * gf + bf * bf
    }

    fun addClampMax(color: Color4u): Color4u = Color4u(
        minOf(r.toInt() + color.r.toInt(), 255).toUByte(),
        minOf(g.toInt() + color.g.toInt(), 255).toUByte(),
        minOf(b.toInt() + color.b.toInt(), 255).toUByte(),
        minOf(a.toInt() + color.a.toInt(), 255).toUByte()
    )

    fun multAll(k: Float): Color4u = Color4u(
        (r.toFloat() * k).roundToUByte(),
        (g.toFloat() * k).roundToUByte(),
        (b.toFloat() * k).roundToUByte(),
        (a.toFloat() * k).roundToUByte()
    )

    fun setVecScaleClamp(color: Color4): Color4u {
        val maxColor = maxOf(color.r, color.g, color.b)
        val scale = if (maxColor > 1f) 255f / maxColor else 255f
        r = color.r.scaleClampToUByte(scale)
        g = color.g.scaleClampToUByte(scale)
        b = color.b.scaleClampToUByte(scale)
        a = color.a.scaleClampToUByte(255f)
        return this
    }

    fun setVecScaleClamp(color: Color3): Color4u {
        val maxColor = maxOf(color.r, color.g, color.b)
        val scale = if (maxColor > 1f) 255f / maxColor else 255f
        r = color.r.scaleClampToUByte(scale)
        g = color.g.scaleClampToUByte(scale)
        b = color.b.scaleClampToUByte(scale)
        a = 255u
        return this
    }

    fun asRGBA(): UInt =
        (a.toUInt() shl 24) or (b.toUInt() shl 16) or (g.toUInt() shl 8) or r.toUInt()

    fun fromRGBA(v: UInt) {
        r = (v and 0xFFu).toUByte()
        g = ((v shr 8) and 0xFFu).toUByte()
        b = ((v shr 16) and 0xFFu).toUByte()
        a = ((v shr 24) and 0xFFu).toUByte()
    }

    fun toColor4(): Color4 {
        val scale = 1f / 255f
        return Color4(r.toFloat() * scale, g.toFloat() * scale,
                      b.toFloat() * scale, a.toFloat() * scale)
    }

    operator fun plus(o: Color4u): Color4u = Color4u(
        (r.toInt() + o.r.toInt()).toUByte(),
        (g.toInt() + o.g.toInt()).toUByte(),
        (b.toInt() + o.b.toInt()).toUByte(),
        (a.toInt() + o.a.toInt()).toUByte()
    )

    operator fun minus(o: Color4u): Color4u = Color4u(
        (r.toInt() - o.r.toInt()).toUByte(),
        (g.toInt() - o.g.toInt()).toUByte(),
        (b.toInt() - o.b.toInt()).toUByte(),
        (a.toInt() - o.a.toInt()).toUByte()
    )

    operator fun times(o: Color4u): Color4u = Color4u(
        (r.toInt() * o.r.toInt()).toUByte(),
        (g.toInt() * o.g.toInt()).toUByte(),
        (b.toInt() * o.b.toInt()).toUByte(),
        (a.toInt() * o.a.toInt()).toUByte()
    )

    operator fun plusAssign(o: Color4u) { r = (r.toInt() + o.r.toInt()).toUByte(); g = (g.toInt() + o.g.toInt()).toUByte(); b = (b.toInt() + o.b.toInt()).toUByte(); a = (a.toInt() + o.a.toInt()).toUByte() }
    operator fun minusAssign(o: Color4u) { r = (r.toInt() - o.r.toInt()).toUByte(); g = (g.toInt() - o.g.toInt()).toUByte(); b = (b.toInt() - o.b.toInt()).toUByte(); a = (a.toInt() - o.a.toInt()).toUByte() }

    fun scaleRgb(k: UByte) { r = (r.toInt() * k.toInt()).toUByte(); g = (g.toInt() * k.toInt()).toUByte(); b = (b.toInt() * k.toInt()).toUByte() }
    fun scaleAlpha(k: UByte) { a = (a.toInt() * k.toInt()).toUByte() }

    override fun equals(other: Any?): Boolean {
        if (other !is Color4u) return false
        return r == other.r && g == other.g && b == other.b && a == other.a
    }
    override fun hashCode(): Int = listOf(r, g, b, a).fold(0) { acc, v -> 31 * acc + v.hashCode() }
    override fun toString(): String = "{ $r, $g, $b, $a }"

    companion object {
        val WHITE = Color4u(255u, 255u, 255u, 255u)
        val BLACK = Color4u(0u, 0u, 0u, 255u)
        val RED   = Color4u(255u, 0u, 0u, 255u)
        val GREEN = Color4u(0u, 255u, 0u, 255u)
        val BLUE  = Color4u(0u, 0u, 255u, 255u)
    }
}

fun distVec(a: Color4u, b: Color4u): Float = (a - b).length()
fun distVecSquared(a: Color4u, b: Color4u): Float = (a - b).lengthSquared()

private fun Float.roundToUByte(): UByte = kotlin.math.round(this).toInt().coerceIn(0, 255).toUByte()
private fun Float.scaleClampToUByte(scale: Float): UByte =
    kotlin.math.round(this * scale).toInt().coerceIn(0, 255).toUByte()
