package com.linkpoint.linden.llmath

import kotlin.math.*

class Quaternion(var x: Float, var y: Float, var z: Float, var w: Float) {

    constructor() : this(0f, 0f, 0f, 1f)

    fun loadIdentity() {
        x = 0f; y = 0f; z = 0f; w = 1f
    }

    fun isIdentity(): Boolean = x == 0f && y == 0f && z == 0f && w == 1f

    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite() && w.isFinite()

    fun isEqualEps(other: Quaternion, epsilon: Float): Boolean =
        abs(x - other.x) < epsilon &&
        abs(y - other.y) < epsilon &&
        abs(z - other.z) < epsilon &&
        abs(w - other.w) < epsilon

    fun normalize(): Float {
        val mag = sqrt(x * x + y * y + z * z + w * w)
        if (mag > FP_MAG_THRESHOLD) {
            if (abs(1f - mag) > 0.000001f) {
                val oom = 1f / mag
                x *= oom; y *= oom; z *= oom; w *= oom
            }
        } else {
            x = 0f; y = 0f; z = 0f; w = 1f
        }
        return mag
    }

    fun conjugate(): Quaternion {
        x = -x; y = -y; z = -z
        return this
    }

    fun conjugated(): Quaternion = Quaternion(-x, -y, -z, w)

    fun setAngleAxis(angle: Float, ax: Float, ay: Float, az: Float): Quaternion {
        val mag = sqrt(ax * ax + ay * ay + az * az)
        if (mag > FP_MAG_THRESHOLD) {
            val halfAngle = angle * 0.5f
            val c = cos(halfAngle)
            val s = sin(halfAngle) / mag
            x = ax * s; y = ay * s; z = az * s; w = c
        } else {
            loadIdentity()
        }
        return this
    }

    fun setAngleAxis(angle: Float, vec: Vector3): Quaternion =
        setAngleAxis(angle, vec.x, vec.y, vec.z)

    fun setAngleAxis(angle: Float, vec: Vector4): Quaternion =
        setAngleAxis(angle, vec.x, vec.y, vec.z)

    fun setEulerAngles(roll: Float, pitch: Float, yaw: Float): Quaternion {
        val mat = Matrix3.fromEulerAngles(roll, pitch, yaw)
        mat.orthogonalize()
        val q = mat.toQuaternion()
        x = q.x; y = q.y; z = q.z; w = q.w
        normalize()
        return this
    }

    fun getAngleAxis(): Pair<Float, Vector3> {
        val v = sqrt(x * x + y * y + z * z)
        return if (v > FP_MAG_THRESHOLD) {
            var oom = 1f / v
            var ww = w
            if (ww < 0f) { ww = -ww; oom = -oom }
            val angle = 2f * atan2(v, ww)
            Pair(angle, Vector3(x * oom, y * oom, z * oom))
        } else {
            Pair(0f, Vector3(0f, 0f, 1f))
        }
    }

    fun getEulerAngles(): Triple<Float, Float, Float> {
        val sx = 2f * (x * w - y * z)
        val sy = 2f * (y * w + x * z)
        val ys = w * w - y * y
        val xz = x * x - z * z
        val cx = ys - xz
        val cy = sqrt(sx * sx + cx * cx)
        return if (cy > GIMBAL_THRESHOLD) {
            Triple(atan2(sx, cx), atan2(sy, cy), atan2(2f * (z * w - x * y), ys + xz))
        } else {
            if (sy > 0f) {
                Triple(0f, F_PI_BY_TWO, 2f * atan2(z + x, w + y))
            } else {
                Triple(0f, -F_PI_BY_TWO, 2f * atan2(z - x, w - y))
            }
        }
    }

    operator fun times(v: Vector3): Vector3 {
        val rw = -x * v.x - y * v.y - z * v.z
        val rx =  w * v.x + y * v.z - z * v.y
        val ry =  w * v.y + z * v.x - x * v.z
        val rz =  w * v.z + x * v.y - y * v.x
        return Vector3(
            -rw * x + rx * w - ry * z + rz * y,
            -rw * y + ry * w - rz * x + rx * z,
            -rw * z + rz * w - rx * y + ry * x
        )
    }

    operator fun times(v: Vector4): Vector4 {
        val rw = -x * v.x - y * v.y - z * v.z
        val rx =  w * v.x + y * v.z - z * v.y
        val ry =  w * v.y + z * v.x - x * v.z
        val rz =  w * v.z + x * v.y - y * v.x
        return Vector4(
            -rw * x + rx * w - ry * z + rz * y,
            -rw * y + ry * w - rz * x + rx * z,
            -rw * z + rz * w - rx * y + ry * x,
            v.w
        )
    }

    operator fun unaryMinus(): Quaternion = Quaternion(-x, -y, -z, -w)

    operator fun plus(other: Quaternion): Quaternion =
        Quaternion(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun minus(other: Quaternion): Quaternion =
        Quaternion(x - other.x, y - other.y, z - other.z, w - other.w)

    operator fun times(b: Float): Quaternion = Quaternion(x * b, y * b, z * b, w * b)

    operator fun times(b: Quaternion): Quaternion = Quaternion(
        b.w * x + b.x * w + b.y * z - b.z * y,
        b.w * y + b.y * w + b.z * x - b.x * z,
        b.w * z + b.z * w + b.x * y - b.y * x,
        b.w * w - b.x * x - b.y * y - b.z * z
    )

    operator fun timesAssign(b: Quaternion) {
        val nx = b.w * x + b.x * w + b.y * z - b.z * y
        val ny = b.w * y + b.y * w + b.z * x - b.x * z
        val nz = b.w * z + b.z * w + b.x * y - b.y * x
        val nw = b.w * w - b.x * x - b.y * y - b.z * z
        x = nx; y = ny; z = nz; w = nw
    }

    operator fun get(idx: Int): Float = when (idx) {
        0 -> x; 1 -> y; 2 -> z; 3 -> w
        else -> throw IndexOutOfBoundsException(idx)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Quaternion) return false
        return x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun hashCode(): Int = 31 * (31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()) + w.hashCode()

    override fun toString(): String = "{ $x, $y, $z, $w }"

    companion object {
        val DEFAULT: Quaternion = Quaternion(0f, 0f, 0f, 1f)
    }
}

operator fun Float.times(q: Quaternion): Quaternion = Quaternion(this * q.x, this * q.y, this * q.z, this * q.w)

fun dot(a: Quaternion, b: Quaternion): Float =
    a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w

fun lerp(t: Float, p: Quaternion, q: Quaternion): Quaternion {
    val invT = 1f - t
    val r = Quaternion(
        t * q.x + invT * p.x,
        t * q.y + invT * p.y,
        t * q.z + invT * p.z,
        t * q.w + invT * p.w
    )
    r.normalize()
    return r
}

fun lerp(t: Float, q: Quaternion): Quaternion {
    val r = Quaternion(
        t * q.x,
        t * q.y,
        t * q.z,
        t * (q.w - 1f) + 1f
    )
    r.normalize()
    return r
}

fun slerp(t: Float, a: Quaternion, b: Quaternion): Quaternion {
    var cosTheta = dot(a, b)
    val bFlip: Boolean
    if (cosTheta < 0f) {
        cosTheta = -cosTheta
        bFlip = true
    } else {
        bFlip = false
    }

    val alpha: Float
    val beta: Float
    if (1f - cosTheta < 0.00001f) {
        beta = 1f - t
        alpha = t
    } else {
        val theta = acos(cosTheta)
        val sinTheta = sin(theta)
        beta = sin(theta - t * theta) / sinTheta
        alpha = sin(t * theta) / sinTheta
    }

    val bFactor = if (bFlip) -beta else beta
    return Quaternion(
        bFactor * a.x + alpha * b.x,
        bFactor * a.y + alpha * b.y,
        bFactor * a.z + alpha * b.z,
        bFactor * a.w + alpha * b.w
    )
}

fun slerp(t: Float, q: Quaternion): Quaternion {
    val c = q.w
    if (t == 1f || c == 1f) return q

    val s = sqrt(1f - c * c)
    val angle: Float
    val stp: Float
    val stq: Float
    if (c < 0f) {
        angle = acos(-c)
        stp = -sin(angle * (1f - t))
        stq = sin(angle * t)
    } else {
        angle = acos(c)
        stp = sin(angle * (1f - t))
        stq = sin(angle * t)
    }
    return Quaternion(
        q.x * stq / s,
        q.y * stq / s,
        q.z * stq / s,
        (stp + q.w * stq) / s
    )
}

fun nlerp(t: Float, a: Quaternion, b: Quaternion): Quaternion =
    if (dot(a, b) < 0f) slerp(t, a, b) else lerp(t, a, b)

fun nlerp(t: Float, q: Quaternion): Quaternion =
    if (q.w < 0f) slerp(t, q) else lerp(t, q)
