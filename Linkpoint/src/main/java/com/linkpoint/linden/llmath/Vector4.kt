package com.linkpoint.linden.llmath

import kotlin.math.sqrt

class Vector4(var x: Float, var y: Float, var z: Float, var w: Float) {

    constructor() : this(0f, 0f, 0f, 1f)

    constructor(x: Float, y: Float, z: Float) : this(x, y, z, 1f)

    constructor(v: Vector3, w: Float = 1f) : this(v.x, v.y, v.z, w)

    fun set(x: Float, y: Float, z: Float, w: Float) {
        this.x = x; this.y = y; this.z = z; this.w = w
    }

    fun set(x: Float, y: Float, z: Float) = set(x, y, z, 1f)

    fun set(other: Vector4) = set(other.x, other.y, other.z, other.w)

    fun set(v: Vector3, w: Float = 1f) = set(v.x, v.y, v.z, w)

    fun clear() = set(0f, 0f, 0f, 1f)

    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite() && w.isFinite()

    fun isExactlyClear(): Boolean = w == 1f && x == 0f && y == 0f && z == 0f

    fun isExactlyZero(): Boolean = x == 0f && y == 0f && z == 0f && w == 0f

    fun lengthSquared(): Float = x * x + y * y + z * z

    fun length(): Float = sqrt(lengthSquared())

    fun normalize(): Float {
        val mag = sqrt(x * x + y * y + z * z)
        if (mag > FP_MAG_THRESHOLD) {
            val oom = 1f / mag
            x *= oom; y *= oom; z *= oom
        } else {
            x = 0f; y = 0f; z = 0f
        }
        return mag
    }

    fun normalized(): Vector4 {
        val c = Vector4(x, y, z, w)
        c.normalize()
        return c
    }

    fun abs(): Boolean {
        var changed = false
        if (x < 0f) { x = -x; changed = true }
        if (y < 0f) { y = -y; changed = true }
        if (z < 0f) { z = -z; changed = true }
        if (w < 0f) { w = -w; changed = true }
        return changed
    }

    fun scaleVec(v: Vector4): Vector4 {
        x *= v.x; y *= v.y; z *= v.z; w *= v.w
        return this
    }

    operator fun get(idx: Int): Float = when (idx) {
        0 -> x; 1 -> y; 2 -> z; 3 -> w
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun set(idx: Int, v: Float) = when (idx) {
        0 -> x = v; 1 -> y = v; 2 -> z = v; 3 -> w = v
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun unaryMinus(): Vector4 = Vector4(-x, -y, -z, w)

    operator fun plus(other: Vector4): Vector4 =
        Vector4(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun minus(other: Vector4): Vector4 =
        Vector4(x - other.x, y - other.y, z - other.z, w - other.w)

    operator fun times(k: Float): Vector4 = Vector4(x * k, y * k, z * k, w)

    operator fun div(k: Float): Vector4 {
        val t = 1f / k
        return Vector4(x * t, y * t, z * t, w)
    }

    operator fun plusAssign(other: Vector4) { x += other.x; y += other.y; z += other.z }

    operator fun minusAssign(other: Vector4) { x -= other.x; y -= other.y; z -= other.z }

    operator fun timesAssign(k: Float) { x *= k; y *= k; z *= k }

    operator fun divAssign(k: Float) { val t = 1f / k; x *= t; y *= t; z *= t }

    fun dot(other: Vector4): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vector4): Vector4 = Vector4(
        y * other.z - other.y * z,
        z * other.x - other.z * x,
        x * other.y - other.x * y,
        w
    )

    override fun equals(other: Any?): Boolean {
        if (other !is Vector4) return false
        return x == other.x && y == other.y && z == other.z
    }

    override fun hashCode(): Int = 31 * (31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()) + w.hashCode()

    override fun toString(): String = "($x, $y, $z, $w)"

    companion object {
        val ZERO: Vector4 = Vector4(0f, 0f, 0f, 0f)
        val UNIT_W: Vector4 = Vector4(0f, 0f, 0f, 1f)
    }
}

operator fun Float.times(v: Vector4): Vector4 = Vector4(v.x * this, v.y * this, v.z * this, v.w)

fun angleBetween(a: Vector4, b: Vector4): Float {
    val d = a.dot(b) / (a.length() * b.length())
    return kotlin.math.acos(llClamp(d, -1f, 1f))
}

fun distVec(a: Vector4, b: Vector4): Float = (a - b).length()

fun distVecSquared(a: Vector4, b: Vector4): Float = (a - b).lengthSquared()

fun lerp(a: Vector4, b: Vector4, u: Float): Vector4 = Vector4(
    a.x + (b.x - a.x) * u,
    a.y + (b.y - a.y) * u,
    a.z + (b.z - a.z) * u,
    a.w + (b.w - a.w) * u
)

fun vec4to3(v: Vector4): Vector3 = Vector3(v.x, v.y, v.z)

fun vec3to4(v: Vector3): Vector4 = Vector4(v.x, v.y, v.z, 1f)
