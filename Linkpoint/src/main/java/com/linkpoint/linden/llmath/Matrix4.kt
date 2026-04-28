package com.linkpoint.linden.llmath

import kotlin.math.abs
import kotlin.math.sqrt

class Matrix4 private constructor(val values: FloatArray) {

    init {
        require(values.size == 16)
    }

    constructor() : this(FloatArray(16).also { identityInto(it) })

    constructor(q: Quaternion) : this() {
        initRotation(q)
    }

    constructor(q: Quaternion, pos: Vector4) : this() {
        initRotTrans(q, pos)
    }

    constructor(q: Quaternion, pos: Vector3) : this() {
        initRotTrans(q, pos)
    }

    constructor(m3: Matrix3) : this() {
        initMatrix(m3)
    }

    constructor(m3: Matrix3, pos: Vector4) : this() {
        initMatrix(m3, pos)
    }

    // row-major: values[row * 4 + col]
    operator fun get(row: Int, col: Int): Float = values[row * 4 + col]
    operator fun set(row: Int, col: Int, v: Float) { values[row * 4 + col] = v }

    fun identity(): Matrix4 {
        identityInto(values)
        return this
    }

    fun isIdentity(): Boolean =
        values[0]  == 1f && values[1]  == 0f && values[2]  == 0f && values[3]  == 0f &&
        values[4]  == 0f && values[5]  == 1f && values[6]  == 0f && values[7]  == 0f &&
        values[8]  == 0f && values[9]  == 0f && values[10] == 1f && values[11] == 0f &&
        values[12] == 0f && values[13] == 0f && values[14] == 0f && values[15] == 1f

    fun initRotation(q: Quaternion): Matrix4 {
        initMatrix(Matrix3.fromQuaternion(q))
        return this
    }

    fun initMatrix(m3: Matrix3): Matrix4 {
        val v = m3.values
        values[0] = v[0]; values[1] = v[1]; values[2] = v[2];  values[3] = 0f
        values[4] = v[3]; values[5] = v[4]; values[6] = v[5];  values[7] = 0f
        values[8] = v[6]; values[9] = v[7]; values[10] = v[8]; values[11] = 0f
        values[12] = 0f; values[13] = 0f; values[14] = 0f;     values[15] = 1f
        return this
    }

    fun initMatrix(m3: Matrix3, pos: Vector4): Matrix4 {
        initMatrix(m3)
        values[12] = pos.x; values[13] = pos.y; values[14] = pos.z
        return this
    }

    fun initRotTrans(q: Quaternion, pos: Vector4): Matrix4 {
        initMatrix(Matrix3.fromQuaternion(q))
        values[12] = pos.x; values[13] = pos.y; values[14] = pos.z
        return this
    }

    fun initRotTrans(q: Quaternion, pos: Vector3): Matrix4 {
        initMatrix(Matrix3.fromQuaternion(q))
        values[12] = pos.x; values[13] = pos.y; values[14] = pos.z
        return this
    }

    fun setTranslation(x: Float, y: Float, z: Float): Matrix4 {
        values[12] = x; values[13] = y; values[14] = z
        return this
    }

    fun setTranslation(v: Vector3): Matrix4 = setTranslation(v.x, v.y, v.z)

    fun setTranslation(v: Vector4): Matrix4 = setTranslation(v.x, v.y, v.z)

    fun getTranslation(): Vector3 = Vector3(values[12], values[13], values[14])

    fun transpose(): Matrix4 {
        val t = FloatArray(16)
        for (i in 0..3) for (j in 0..3) t[j * 4 + i] = values[i * 4 + j]
        t.copyInto(values)
        return this
    }

    fun transposed(): Matrix4 = Matrix4(values.copyOf()).also { it.transpose() }

    fun determinant(): Float {
        val m = values
        return  m[3]  * m[6]  * m[9]  * m[12] - m[2]  * m[7]  * m[9]  * m[12] -
                m[3]  * m[5]  * m[10] * m[12] + m[1]  * m[7]  * m[10] * m[12] +
                m[2]  * m[5]  * m[11] * m[12] - m[1]  * m[6]  * m[11] * m[12] -
                m[3]  * m[6]  * m[8]  * m[13] + m[2]  * m[7]  * m[8]  * m[13] +
                m[3]  * m[4]  * m[10] * m[13] - m[0]  * m[7]  * m[10] * m[13] -
                m[2]  * m[4]  * m[11] * m[13] + m[0]  * m[6]  * m[11] * m[13] +
                m[3]  * m[5]  * m[8]  * m[14] - m[1]  * m[7]  * m[8]  * m[14] -
                m[3]  * m[4]  * m[9]  * m[14] + m[0]  * m[7]  * m[9]  * m[14] +
                m[1]  * m[4]  * m[11] * m[14] - m[0]  * m[5]  * m[11] * m[14] -
                m[2]  * m[5]  * m[8]  * m[15] + m[1]  * m[6]  * m[8]  * m[15] +
                m[2]  * m[4]  * m[9]  * m[15] - m[0]  * m[6]  * m[9]  * m[15] -
                m[1]  * m[4]  * m[10] * m[15] + m[0]  * m[5]  * m[10] * m[15]
    }

    // Invert for pure orthonormal homogeneous transform matrices.
    fun inverse(): Matrix4 {
        // Transpose the 3x3 rotation block
        var t: Float
        t = values[1]; values[1] = values[4]; values[4] = t
        t = values[2]; values[2] = values[8]; values[8] = t
        t = values[6]; values[6] = values[9]; values[9] = t

        // Rotate the translation part by the new rotation, store temporarily in column 3
        for (j in 0..2) {
            values[j * 4 + 3] = values[12] * values[0 * 4 + j] +
                                 values[13] * values[1 * 4 + j] +
                                 values[14] * values[2 * 4 + j]
        }
        values[12] = -values[0 * 4 + 3]
        values[13] = -values[1 * 4 + 3]
        values[14] = -values[2 * 4 + 3]
        values[3] = 0f; values[7] = 0f; values[11] = 0f
        return this
    }

    fun inversed(): Matrix4 = Matrix4(values.copyOf()).also { it.inverse() }

    operator fun times(b: Matrix4): Matrix4 {
        val r = FloatArray(16)
        for (j in 0..3) {
            for (i in 0..3) {
                r[j * 4 + i] = values[j * 4 + 0] * b.values[0 * 4 + i] +
                                values[j * 4 + 1] * b.values[1 * 4 + i] +
                                values[j * 4 + 2] * b.values[2 * 4 + i] +
                                values[j * 4 + 3] * b.values[3 * 4 + i]
            }
        }
        return Matrix4(r)
    }

    operator fun timesAssign(b: Matrix4) {
        val r = (this * b).values
        r.copyInto(values)
    }

    // Transform a Vector3: full transform including translation (row-vector * matrix)
    operator fun times(a: Vector3): Vector3 = Vector3(
        a.x * values[0]  + a.y * values[4]  + a.z * values[8]  + values[12],
        a.x * values[1]  + a.y * values[5]  + a.z * values[9]  + values[13],
        a.x * values[2]  + a.y * values[6]  + a.z * values[10] + values[14]
    )

    // Transform a Vector4: full 4-component transform (row-vector * matrix)
    operator fun times(a: Vector4): Vector4 = Vector4(
        a.x * values[0]  + a.y * values[4]  + a.z * values[8]  + a.w * values[12],
        a.x * values[1]  + a.y * values[5]  + a.z * values[9]  + a.w * values[13],
        a.x * values[2]  + a.y * values[6]  + a.z * values[10] + a.w * values[14],
        a.x * values[3]  + a.y * values[7]  + a.z * values[11] + a.w * values[15]
    )

    // Rotate a Vector3 without applying translation
    fun rotateVector(a: Vector3): Vector3 = Vector3(
        a.x * values[0] + a.y * values[4] + a.z * values[8],
        a.x * values[1] + a.y * values[5] + a.z * values[9],
        a.x * values[2] + a.y * values[6] + a.z * values[10]
    )

    // Rotate a Vector4 without applying translation
    fun rotateVector(a: Vector4): Vector4 = Vector4(
        a.x * values[0] + a.y * values[4] + a.z * values[8],
        a.x * values[1] + a.y * values[5] + a.z * values[9],
        a.x * values[2] + a.y * values[6] + a.z * values[10],
        a.w
    )

    fun toQuaternion(): Quaternion {
        val tr = values[0] + values[5] + values[10]
        val nxt = intArrayOf(1, 2, 0)
        val q = FloatArray(4)
        if (tr > 0f) {
            var s = sqrt(tr + 1f)
            q[3] = s * 0.5f
            s = 0.5f / s
            q[0] = (values[6]  - values[9])  * s
            q[1] = (values[8]  - values[2])  * s
            q[2] = (values[1]  - values[4])  * s
        } else {
            // indices into the 4x4 flat array: [row*4+col]
            val diag = intArrayOf(0, 5, 10)
            var i = 0
            if (values[5] > values[0]) i = 1
            if (values[10] > values[diag[i]]) i = 2
            val j = nxt[i]
            val k = nxt[j]
            var s = sqrt((values[diag[i]] - (values[diag[j]] + values[diag[k]])) + 1f)
            q[i] = s * 0.5f
            if (s != 0f) s = 0.5f / s
            q[3] = (values[j * 4 + k] - values[k * 4 + j]) * s
            q[j] = (values[i * 4 + j] + values[j * 4 + i]) * s
            q[k] = (values[i * 4 + k] + values[k * 4 + i]) * s
        }
        val result = Quaternion(q[0], q[1], q[2], q[3])
        result.normalize()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix4) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int = values.contentHashCode()

    override fun toString(): String {
        val v = values
        return "{ ${v[0]}, ${v[1]}, ${v[2]}, ${v[3]}; " +
               "${v[4]}, ${v[5]}, ${v[6]}, ${v[7]}; " +
               "${v[8]}, ${v[9]}, ${v[10]}, ${v[11]}; " +
               "${v[12]}, ${v[13]}, ${v[14]}, ${v[15]} }"
    }

    companion object {
        private fun identityInto(v: FloatArray) {
            v[0]  = 1f; v[1]  = 0f; v[2]  = 0f; v[3]  = 0f
            v[4]  = 0f; v[5]  = 1f; v[6]  = 0f; v[7]  = 0f
            v[8]  = 0f; v[9]  = 0f; v[10] = 1f; v[11] = 0f
            v[12] = 0f; v[13] = 0f; v[14] = 0f; v[15] = 1f
        }

        fun fromQuaternion(q: Quaternion): Matrix4 = Matrix4(q)

        fun fromQuaternionAndTranslation(q: Quaternion, pos: Vector4): Matrix4 = Matrix4(q, pos)

        fun fromQuaternionAndTranslation(q: Quaternion, pos: Vector3): Matrix4 = Matrix4(q, pos)
    }
}
