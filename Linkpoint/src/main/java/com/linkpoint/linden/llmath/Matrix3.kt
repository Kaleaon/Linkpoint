package com.linkpoint.linden.llmath

import kotlin.math.*

class Matrix3 private constructor(val values: FloatArray) {

    init {
        require(values.size == 9)
    }

    constructor() : this(FloatArray(9).also { identity(it) })

    constructor(q: Quaternion) : this() {
        setFromQuaternion(q)
    }

    constructor(roll: Float, pitch: Float, yaw: Float) : this() {
        setEulerAngles(roll, pitch, yaw)
    }

    // row-major: values[row * 3 + col]
    operator fun get(row: Int, col: Int): Float = values[row * 3 + col]
    operator fun set(row: Int, col: Int, v: Float) { values[row * 3 + col] = v }

    fun identity(): Matrix3 {
        identity(values)
        return this
    }

    fun setEulerAngles(roll: Float, pitch: Float, yaw: Float): Matrix3 {
        val cx = cos(roll); val sx = sin(roll)
        val cy = cos(pitch); val sy = sin(pitch)
        val cz = cos(yaw); val sz = sin(yaw)
        val cxsy = cx * sy; val sxsy = sx * sy
        values[0] =  cy * cz
        values[1] = -cy * sz
        values[2] =  sy
        values[3] =  sxsy * cz + cx * sz
        values[4] = -sxsy * sz + cx * cz
        values[5] = -sx * cy
        values[6] = -cxsy * cz + sx * sz
        values[7] =  cxsy * sz + sx * cz
        values[8] =  cx * cy
        return this
    }

    fun setFromQuaternion(q: Quaternion): Matrix3 {
        val xx = q.x * q.x; val xy = q.x * q.y; val xz = q.x * q.z; val xw = q.x * q.w
        val yy = q.y * q.y; val yz = q.y * q.z; val yw = q.y * q.w
        val zz = q.z * q.z; val zw = q.z * q.w
        values[0] = 1f - 2f * (yy + zz); values[1] = 2f * (xy + zw); values[2] = 2f * (xz - yw)
        values[3] = 2f * (xy - zw); values[4] = 1f - 2f * (xx + zz); values[5] = 2f * (yz + xw)
        values[6] = 2f * (xz + yw); values[7] = 2f * (yz - xw); values[8] = 1f - 2f * (xx + yy)
        return this
    }

    fun transpose(): Matrix3 {
        var t: Float
        t = values[1]; values[1] = values[3]; values[3] = t
        t = values[2]; values[2] = values[6]; values[6] = t
        t = values[5]; values[5] = values[7]; values[7] = t
        return this
    }

    fun transposed(): Matrix3 = Matrix3(values.copyOf()).also { it.transpose() }

    fun determinant(): Float =
        values[0] * (values[4] * values[8] - values[5] * values[7]) +
        values[1] * (values[5] * values[6] - values[3] * values[8]) +
        values[2] * (values[3] * values[7] - values[4] * values[6])

    fun inverse(): Matrix3 {
        val det = determinant()
        val threshold = 0.000001f
        if (abs(det) > threshold) {
            val inv = FloatArray(9)
            inv[0] = (values[4] * values[8] - values[5] * values[7]) / det
            inv[3] = (values[5] * values[6] - values[3] * values[8]) / det
            inv[6] = (values[3] * values[7] - values[4] * values[6]) / det
            inv[1] = (values[7] * values[2] - values[8] * values[1]) / det
            inv[4] = (values[8] * values[0] - values[6] * values[2]) / det
            inv[7] = (values[6] * values[1] - values[7] * values[0]) / det
            inv[2] = (values[1] * values[5] - values[2] * values[4]) / det
            inv[5] = (values[2] * values[3] - values[0] * values[5]) / det
            inv[8] = (values[0] * values[4] - values[1] * values[3]) / det
            inv.copyInto(values)
        }
        return this
    }

    fun orthogonalize(): Matrix3 {
        var xx = values[0]; var xy = values[1]; var xz = values[2]
        var yx = values[3]; var yy = values[4]; var yz = values[5]

        var xLen = sqrt(xx * xx + xy * xy + xz * xz)
        if (xLen > FP_MAG_THRESHOLD) { xx /= xLen; xy /= xLen; xz /= xLen }

        val dotXY = xx * yx + xy * yy + xz * yz
        yx -= xx * dotXY; yy -= xy * dotXY; yz -= xz * dotXY

        var yLen = sqrt(yx * yx + yy * yy + yz * yz)
        if (yLen > FP_MAG_THRESHOLD) { yx /= yLen; yy /= yLen; yz /= yLen }

        val zx = xy * yz - xz * yy
        val zy = xz * yx - xx * yz
        val zz = xx * yy - xy * yx

        values[0] = xx; values[1] = xy; values[2] = xz
        values[3] = yx; values[4] = yy; values[5] = yz
        values[6] = zx; values[7] = zy; values[8] = zz
        return this
    }

    operator fun times(b: Matrix3): Matrix3 {
        val r = FloatArray(9)
        for (j in 0..2) {
            for (i in 0..2) {
                r[j * 3 + i] = values[j * 3 + 0] * b.values[0 * 3 + i] +
                                values[j * 3 + 1] * b.values[1 * 3 + i] +
                                values[j * 3 + 2] * b.values[2 * 3 + i]
            }
        }
        return Matrix3(r)
    }

    operator fun times(a: Vector3): Vector3 = Vector3(
        a.x * values[0] + a.y * values[3] + a.z * values[6],
        a.x * values[1] + a.y * values[4] + a.z * values[7],
        a.x * values[2] + a.y * values[5] + a.z * values[8]
    )

    fun toQuaternion(): Quaternion {
        val tr = values[0] + values[4] + values[8]
        val nxt = intArrayOf(1, 2, 0)
        val q = FloatArray(4)
        if (tr > 0f) {
            var s = sqrt(tr + 1f)
            q[3] = s * 0.5f
            s = 0.5f / s
            q[0] = (values[5] - values[7]) * s
            q[1] = (values[6] - values[2]) * s
            q[2] = (values[1] - values[3]) * s
        } else {
            var i = 0
            if (values[4] > values[0]) i = 1
            if (values[8] > values[i * 3 + i]) i = 2
            val j = nxt[i]
            val k = nxt[j]
            var s = sqrt((values[i * 3 + i] - (values[j * 3 + j] + values[k * 3 + k])) + 1f)
            q[i] = s * 0.5f
            if (s != 0f) s = 0.5f / s
            q[3] = (values[j * 3 + k] - values[k * 3 + j]) * s
            q[j] = (values[i * 3 + j] + values[j * 3 + i]) * s
            q[k] = (values[i * 3 + k] + values[k * 3 + i]) * s
        }
        val result = Quaternion(q[0], q[1], q[2], q[3])
        result.normalize()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix3) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int = values.contentHashCode()

    override fun toString(): String =
        "{ ${values[0]}, ${values[1]}, ${values[2]}; " +
        "${values[3]}, ${values[4]}, ${values[5]}; " +
        "${values[6]}, ${values[7]}, ${values[8]} }"

    companion object {
        private fun identity(v: FloatArray) {
            v[0] = 1f; v[1] = 0f; v[2] = 0f
            v[3] = 0f; v[4] = 1f; v[5] = 0f
            v[6] = 0f; v[7] = 0f; v[8] = 1f
        }

        fun fromQuaternion(q: Quaternion): Matrix3 = Matrix3(q)

        fun fromEulerAngles(roll: Float, pitch: Float, yaw: Float): Matrix3 =
            Matrix3(roll, pitch, yaw)
    }
}
