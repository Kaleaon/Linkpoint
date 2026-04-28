package com.linkpoint.linden.llmath

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class Vector2(var x: Float, var y: Float) {

    constructor() : this(0f, 0f)

    companion object {
        val ZERO = Vector2(0f, 0f)
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun set(other: Vector2) {
        x = other.x
        y = other.y
    }

    fun setZero() {
        x = 0f
        y = 0f
    }

    fun isFinite(): Boolean = x.isFinite() && y.isFinite()

    fun isNull(): Boolean = F_APPROXIMATELY_ZERO > x * x + y * y

    fun isExactlyZero(): Boolean = x == 0f && y == 0f

    fun length(): Float = sqrt(lengthSquared())

    fun lengthSquared(): Float = x * x + y * y

    fun normalize(): Float {
        val mag = length()
        if (mag > FP_MAG_THRESHOLD) {
            val ooMag = 1f / mag
            x *= ooMag
            y *= ooMag
        } else {
            x = 0f
            y = 0f
            return 0f
        }
        return mag
    }

    fun abs(): Boolean {
        var changed = false
        if (x < 0f) { x = -x; changed = true }
        if (y < 0f) { y = -y; changed = true }
        return changed
    }

    fun scaleVec(vec: Vector2): Vector2 {
        x *= vec.x
        y *= vec.y
        return this
    }

    operator fun get(idx: Int): Float = when (idx) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun set(idx: Int, v: Float) = when (idx) {
        0 -> x = v
        1 -> y = v
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun plus(b: Vector2): Vector2 = Vector2(x + b.x, y + b.y)

    operator fun minus(b: Vector2): Vector2 = Vector2(x - b.x, y - b.y)

    operator fun times(b: Vector2): Float = x * b.x + y * b.y

    operator fun rem(b: Vector2): Vector2 =
        Vector2(x * b.y - b.x * y, y * b.x - b.y * x)

    operator fun times(k: Float): Vector2 = Vector2(x * k, y * k)

    operator fun div(k: Float): Vector2 {
        val t = 1f / k
        return Vector2(x * t, y * t)
    }

    operator fun unaryMinus(): Vector2 = Vector2(-x, -y)

    operator fun plusAssign(b: Vector2) { x += b.x; y += b.y }

    operator fun minusAssign(b: Vector2) { x -= b.x; y -= b.y }

    operator fun remAssign(b: Vector2) {
        val nx = x * b.y - b.x * y
        val ny = y * b.x - b.y * x
        x = nx; y = ny
    }

    operator fun timesAssign(k: Float) { x *= k; y *= k }

    operator fun divAssign(k: Float) { timesAssign(1f / k) }

    override fun equals(other: Any?): Boolean =
        other is Vector2 && x == other.x && y == other.y

    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()

    operator fun compareTo(b: Vector2): Int {
        if (x != b.x) return x.compareTo(b.x)
        return y.compareTo(b.y)
    }

    override fun toString(): String = "{ $x, $y }"
}

operator fun Float.times(v: Vector2): Vector2 = Vector2(v.x * this, v.y * this)

fun angleBetween(a: Vector2, b: Vector2): Float {
    val dot = a * b
    val cross = a.x * b.y - a.y * b.x
    return atan2(abs(cross), dot)
}

fun signedAngleBetween(a: Vector2, b: Vector2): Float {
    val dot = a * b
    val cross = a.x * b.y - a.y * b.x
    return atan2(cross, dot)
}

fun areParallel(a: Vector2, b: Vector2, epsilon: Float = F_APPROXIMATELY_ZERO): Boolean {
    val an = Vector2(a.x, a.y).also { it.normalize() }
    val bn = Vector2(b.x, b.y).also { it.normalize() }
    val dot = an * bn
    return 1.0f - abs(dot) < epsilon
}

fun distVec(a: Vector2, b: Vector2): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return sqrt(dx * dx + dy * dy)
}

fun distVecSquared(a: Vector2, b: Vector2): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return dx * dx + dy * dy
}

fun lerp(a: Vector2, b: Vector2, u: Float): Vector2 =
    Vector2(a.x + (b.x - a.x) * u, a.y + (b.y - a.y) * u)

fun updateMinMax(min: Vector2, max: Vector2, pos: Vector2) {
    if (min.x > pos.x) min.x = pos.x
    if (max.x < pos.x) max.x = pos.x
    if (min.y > pos.y) min.y = pos.y
    if (max.y < pos.y) max.y = pos.y
}
