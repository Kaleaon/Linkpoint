package com.linkpoint.linden.llmath

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

class Vector3d(var x: Double, var y: Double, var z: Double) {

    constructor() : this(0.0, 0.0, 0.0)

    constructor(vec: Vector3) : this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

    companion object {
        val ZERO = Vector3d(0.0, 0.0, 0.0)
        val X_AXIS = Vector3d(1.0, 0.0, 0.0)
        val Y_AXIS = Vector3d(0.0, 1.0, 0.0)
        val Z_AXIS = Vector3d(0.0, 0.0, 1.0)
        val X_AXIS_NEG = Vector3d(-1.0, 0.0, 0.0)
        val Y_AXIS_NEG = Vector3d(0.0, -1.0, 0.0)
        val Z_AXIS_NEG = Vector3d(0.0, 0.0, -1.0)
    }

    fun set(x: Double, y: Double, z: Double): Vector3d { this.x = x; this.y = y; this.z = z; return this }
    fun set(other: Vector3d): Vector3d { x = other.x; y = other.y; z = other.z; return this }
    fun set(vec: Vector3): Vector3d { x = vec.x.toDouble(); y = vec.y.toDouble(); z = vec.z.toDouble(); return this }
    fun setZero(): Vector3d { x = 0.0; y = 0.0; z = 0.0; return this }

    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()

    fun isNull(): Boolean = F_APPROXIMATELY_ZERO > (x * x + y * y + z * z).toFloat()

    fun isExactlyZero(): Boolean = x == 0.0 && y == 0.0 && z == 0.0

    fun length(): Double = sqrt(lengthSquared())

    fun lengthSquared(): Double = x * x + y * y + z * z

    fun normalize(): Double {
        // Cast to F32 first for numerical stability (mirrors C++ source)
        val mag = sqrt(x * x + y * y + z * z).toFloat().toDouble()
        if (mag > FP_MAG_THRESHOLD) {
            val ooMag = 1.0 / mag
            x *= ooMag; y *= ooMag; z *= ooMag
        } else {
            x = 0.0; y = 0.0; z = 0.0
            return 0.0
        }
        return mag
    }

    fun clamp(min: Double, max: Double): Boolean {
        var changed = false
        if (x < min) { x = min; changed = true } else if (x > max) { x = max; changed = true }
        if (y < min) { y = min; changed = true } else if (y > max) { y = max; changed = true }
        if (z < min) { z = min; changed = true } else if (z > max) { z = max; changed = true }
        return changed
    }

    fun abs(): Boolean {
        var changed = false
        if (x < 0.0) { x = -x; changed = true }
        if (y < 0.0) { y = -y; changed = true }
        if (z < 0.0) { z = -z; changed = true }
        return changed
    }

    operator fun get(idx: Int): Double = when (idx) {
        0 -> x; 1 -> y; 2 -> z
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun set(idx: Int, v: Double) = when (idx) {
        0 -> x = v; 1 -> y = v; 2 -> z = v
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun plus(b: Vector3d): Vector3d = Vector3d(x + b.x, y + b.y, z + b.z)

    operator fun minus(b: Vector3d): Vector3d = Vector3d(x - b.x, y - b.y, z - b.z)

    operator fun times(b: Vector3d): Double = x * b.x + y * b.y + z * b.z

    operator fun rem(b: Vector3d): Vector3d = Vector3d(
        y * b.z - b.y * z,
        z * b.x - b.z * x,
        x * b.y - b.x * y
    )

    operator fun times(k: Double): Vector3d = Vector3d(x * k, y * k, z * k)

    operator fun div(k: Double): Vector3d {
        val t = 1.0 / k
        return Vector3d(x * t, y * t, z * t)
    }

    operator fun unaryMinus(): Vector3d = Vector3d(-x, -y, -z)

    operator fun plusAssign(b: Vector3d) { x += b.x; y += b.y; z += b.z }

    operator fun minusAssign(b: Vector3d) { x -= b.x; y -= b.y; z -= b.z }

    operator fun remAssign(b: Vector3d) {
        val nx = y * b.z - b.y * z
        val ny = z * b.x - b.z * x
        val nz = x * b.y - b.x * y
        x = nx; y = ny; z = nz
    }

    operator fun timesAssign(k: Double) { x *= k; y *= k; z *= k }

    operator fun divAssign(k: Double) {
        val t = 1.0 / k
        x *= t; y *= t; z *= t
    }

    override fun equals(other: Any?): Boolean =
        other is Vector3d && x == other.x && y == other.y && z == other.z

    override fun hashCode(): Int = 31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()

    operator fun compareTo(b: Vector3d): Int {
        if (x != b.x) return x.compareTo(b.x)
        if (y != b.y) return y.compareTo(b.y)
        return z.compareTo(b.z)
    }

    override fun toString(): String = "{ $x, $y, $z }"
}

operator fun Double.times(v: Vector3d): Vector3d = Vector3d(v.x * this, v.y * this, v.z * this)

fun distVec(a: Vector3d, b: Vector3d): Double {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

fun distVecSquared(a: Vector3d, b: Vector3d): Double {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return dx * dx + dy * dy + dz * dz
}

fun distVecSquared2D(a: Vector3d, b: Vector3d): Double {
    val dx = a.x - b.x; val dy = a.y - b.y
    return dx * dx + dy * dy
}

fun angleBetween(a: Vector3d, b: Vector3d): Double {
    val an = Vector3d(a.x, a.y, a.z).also { it.normalize() }
    val bn = Vector3d(b.x, b.y, b.z).also { it.normalize() }
    val cosine = an * bn
    return when {
        cosine >= 1.0 -> 0.0
        cosine <= -1.0 -> F_PI.toDouble()
        else -> acos(cosine)
    }
}

fun areParallel(a: Vector3d, b: Vector3d, epsilon: Double = F_APPROXIMATELY_ZERO.toDouble()): Boolean {
    val an = Vector3d(a.x, a.y, a.z).also { it.normalize() }
    val bn = Vector3d(b.x, b.y, b.z).also { it.normalize() }
    val dot = an * bn
    return 1.0 - abs(dot) < epsilon
}

fun projectedVec(a: Vector3d, b: Vector3d): Vector3d {
    val axis = Vector3d(b.x, b.y, b.z).also { it.normalize() }
    return axis * (a * axis)
}

fun inverseProjVec(a: Vector3d, b: Vector3d): Vector3d {
    val normalizedA = Vector3d(a.x, a.y, a.z).also { it.normalize() }
    val normalizedB = Vector3d(b.x, b.y, b.z)
    val bLength = normalizedB.normalize()
    val dot = normalizedA * normalizedB
    return normalizedA * (bLength / dot)
}

fun lerp(a: Vector3d, b: Vector3d, u: Double): Vector3d = Vector3d(
    a.x + (b.x - a.x) * u,
    a.y + (b.y - a.y) * u,
    a.z + (b.z - a.z) * u
)
