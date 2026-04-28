package com.linkpoint.linden.llmath

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class Vector3(var x: Float, var y: Float, var z: Float) {

    constructor() : this(0f, 0f, 0f)

    companion object {
        val ZERO = Vector3(0f, 0f, 0f)
        val X_AXIS = Vector3(1f, 0f, 0f)
        val Y_AXIS = Vector3(0f, 1f, 0f)
        val Z_AXIS = Vector3(0f, 0f, 1f)
        val X_AXIS_NEG = Vector3(-1f, 0f, 0f)
        val Y_AXIS_NEG = Vector3(0f, -1f, 0f)
        val Z_AXIS_NEG = Vector3(0f, 0f, -1f)
        val ALL_ONE = Vector3(1f, 1f, 1f)
    }

    fun set(x: Float, y: Float, z: Float) { this.x = x; this.y = y; this.z = z }
    fun set(other: Vector3) { x = other.x; y = other.y; z = other.z }
    fun setZero() { x = 0f; y = 0f; z = 0f }

    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()

    fun isNull(): Boolean = F_APPROXIMATELY_ZERO > x * x + y * y + z * z

    fun isExactlyZero(): Boolean = x == 0f && y == 0f && z == 0f

    fun inRange(min: Float, max: Float): Boolean =
        x in min..max && y in min..max && z in min..max

    fun length(): Float = sqrt(lengthSquared())

    fun lengthSquared(): Float = x * x + y * y + z * z

    fun normalize(): Float {
        val mag = sqrt(x * x + y * y + z * z)
        if (mag > FP_MAG_THRESHOLD) {
            val ooMag = 1f / mag
            x *= ooMag; y *= ooMag; z *= ooMag
        } else {
            x = 0f; y = 0f; z = 0f
            return 0f
        }
        return mag
    }

    fun clamp(min: Float, max: Float): Boolean {
        var changed = false
        if (x < min) { x = min; changed = true } else if (x > max) { x = max; changed = true }
        if (y < min) { y = min; changed = true } else if (y > max) { y = max; changed = true }
        if (z < min) { z = min; changed = true } else if (z > max) { z = max; changed = true }
        return changed
    }

    fun clamp(minVec: Vector3, maxVec: Vector3): Boolean {
        var changed = false
        if (x < minVec.x) { x = minVec.x; changed = true } else if (x > maxVec.x) { x = maxVec.x; changed = true }
        if (y < minVec.y) { y = minVec.y; changed = true } else if (y > maxVec.y) { y = maxVec.y; changed = true }
        if (z < minVec.z) { z = minVec.z; changed = true } else if (z > maxVec.z) { z = maxVec.z; changed = true }
        return changed
    }

    fun clampLength(limit: Float): Boolean {
        val magSq = lengthSquared()
        if (magSq > limit * limit) {
            val scale = limit / sqrt(magSq)
            x *= scale; y *= scale; z *= scale
            return true
        }
        return false
    }

    fun abs(): Boolean {
        var changed = false
        if (x < 0f) { x = -x; changed = true }
        if (y < 0f) { y = -y; changed = true }
        if (z < 0f) { z = -z; changed = true }
        return changed
    }

    fun scaleVec(vec: Vector3): Vector3 {
        x *= vec.x; y *= vec.y; z *= vec.z
        return this
    }

    fun scaledVec(vec: Vector3): Vector3 = Vector3(x * vec.x, y * vec.y, z * vec.z)

    fun snap(sigDigits: Int) {
        x = snapToSigFigs(x, sigDigits)
        y = snapToSigFigs(y, sigDigits)
        z = snapToSigFigs(z, sigDigits)
    }

    operator fun get(idx: Int): Float = when (idx) {
        0 -> x; 1 -> y; 2 -> z
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun set(idx: Int, v: Float) = when (idx) {
        0 -> x = v; 1 -> y = v; 2 -> z = v
        else -> throw IndexOutOfBoundsException(idx)
    }

    operator fun plus(b: Vector3): Vector3 = Vector3(x + b.x, y + b.y, z + b.z)

    operator fun minus(b: Vector3): Vector3 = Vector3(x - b.x, y - b.y, z - b.z)

    operator fun times(b: Vector3): Float = x * b.x + y * b.y + z * b.z

    operator fun rem(b: Vector3): Vector3 = Vector3(
        y * b.z - b.y * z,
        z * b.x - b.z * x,
        x * b.y - b.x * y
    )

    operator fun times(k: Float): Vector3 = Vector3(x * k, y * k, z * k)

    operator fun div(k: Float): Vector3 {
        val t = 1f / k
        return Vector3(x * t, y * t, z * t)
    }

    operator fun unaryMinus(): Vector3 = Vector3(-x, -y, -z)

    operator fun plusAssign(b: Vector3) { x += b.x; y += b.y; z += b.z }

    operator fun minusAssign(b: Vector3) { x -= b.x; y -= b.y; z -= b.z }

    operator fun remAssign(b: Vector3) {
        val nx = y * b.z - b.y * z
        val ny = z * b.x - b.z * x
        val nz = x * b.y - b.x * y
        x = nx; y = ny; z = nz
    }

    operator fun timesAssign(k: Float) { x *= k; y *= k; z *= k }

    operator fun timesAssign(b: Vector3) { x *= b.x; y *= b.y; z *= b.z }

    operator fun divAssign(k: Float) { timesAssign(1f / k) }

    operator fun divAssign(b: Vector3) { x /= b.x; y /= b.y; z /= b.z }

    override fun equals(other: Any?): Boolean =
        other is Vector3 && x == other.x && y == other.y && z == other.z

    override fun hashCode(): Int = 31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()

    operator fun compareTo(b: Vector3): Int {
        if (x != b.x) return x.compareTo(b.x)
        if (y != b.y) return y.compareTo(b.y)
        return z.compareTo(b.z)
    }

    override fun toString(): String = "{ $x, $y, $z }"
}

operator fun Float.times(v: Vector3): Vector3 = Vector3(v.x * this, v.y * this, v.z * this)

fun angleBetween(a: Vector3, b: Vector3): Float {
    var ab = a * b
    if (ab == -0.0f) ab = 0.0f
    val c = a % b
    return atan2(c.length(), ab)
}

fun areParallel(a: Vector3, b: Vector3, epsilon: Float = F_APPROXIMATELY_ZERO): Boolean {
    val an = Vector3(a.x, a.y, a.z).also { it.normalize() }
    val bn = Vector3(b.x, b.y, b.z).also { it.normalize() }
    val dot = an * bn
    return 1.0f - abs(dot) < epsilon
}

fun distVec(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

fun distVecSquared(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return dx * dx + dy * dy + dz * dz
}

fun distVecSquared2D(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x; val dy = a.y - b.y
    return dx * dx + dy * dy
}

fun projectedVec(a: Vector3, b: Vector3): Vector3 {
    val bb = b * b
    return if (bb > FP_MAG_THRESHOLD * FP_MAG_THRESHOLD) {
        b * ((a * b) / bb)
    } else {
        Vector3.ZERO.copy()
    }
}

private fun Vector3.copy(): Vector3 = Vector3(x, y, z)

fun inverseProjVec(a: Vector3, b: Vector3): Vector3 {
    val normalizedA = Vector3(a.x, a.y, a.z).also { it.normalize() }
    val normalizedB = Vector3(b.x, b.y, b.z)
    val bLength = normalizedB.normalize()
    val dot = normalizedA * normalizedB
    return normalizedA * (bLength / dot)
}

fun parallelComponent(a: Vector3, b: Vector3): Vector3 = projectedVec(a, b)

fun orthogonalComponent(a: Vector3, b: Vector3): Vector3 = a - projectedVec(a, b)

fun lerp(a: Vector3, b: Vector3, u: Float): Vector3 = Vector3(
    a.x + (b.x - a.x) * u,
    a.y + (b.y - a.y) * u,
    a.z + (b.z - a.z) * u
)

fun updateMinMax(min: Vector3, max: Vector3, pos: Vector3) {
    if (min.x > pos.x) min.x = pos.x
    if (max.x < pos.x) max.x = pos.x
    if (min.y > pos.y) min.y = pos.y
    if (max.y < pos.y) max.y = pos.y
    if (min.z > pos.z) min.z = pos.z
    if (max.z < pos.z) max.z = pos.z
}
