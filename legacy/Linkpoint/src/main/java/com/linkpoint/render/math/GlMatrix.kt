package com.linkpoint.render.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure Kotlin column-major 4x4 matrix operations for OpenGL ES rendering.
 * Provides deterministic behavior across both Android runtime and host JVM unit tests.
 */
object GlMatrix {

    @JvmStatic
    fun setIdentityM(sm: FloatArray, smOffset: Int) {
        for (i in 0..15) {
            sm[smOffset + i] = 0f
        }
        sm[smOffset + 0] = 1f
        sm[smOffset + 5] = 1f
        sm[smOffset + 10] = 1f
        sm[smOffset + 15] = 1f
    }

    @JvmStatic
    fun translateM(m: FloatArray, mOffset: Int, x: Float, y: Float, z: Float) {
        for (i in 0..3) {
            m[mOffset + 12 + i] += m[mOffset + i] * x + m[mOffset + 4 + i] * y + m[mOffset + 8 + i] * z
        }
    }

    @JvmStatic
    fun scaleM(m: FloatArray, mOffset: Int, x: Float, y: Float, z: Float) {
        for (i in 0..3) {
            m[mOffset + i] *= x
            m[mOffset + 4 + i] *= y
            m[mOffset + 8 + i] *= z
        }
    }

    @JvmStatic
    fun multiplyMM(
        result: FloatArray,
        resultOffset: Int,
        lhs: FloatArray,
        lhsOffset: Int,
        rhs: FloatArray,
        rhsOffset: Int
    ) {
        val temp = FloatArray(16)
        for (i in 0..3) { // column of rhs
            val rhsI0 = rhs[rhsOffset + i * 4 + 0]
            val rhsI1 = rhs[rhsOffset + i * 4 + 1]
            val rhsI2 = rhs[rhsOffset + i * 4 + 2]
            val rhsI3 = rhs[rhsOffset + i * 4 + 3]

            for (j in 0..3) { // row of lhs
                temp[i * 4 + j] = (lhs[lhsOffset + 0 * 4 + j] * rhsI0 +
                        lhs[lhsOffset + 1 * 4 + j] * rhsI1 +
                        lhs[lhsOffset + 2 * 4 + j] * rhsI2 +
                        lhs[lhsOffset + 3 * 4 + j] * rhsI3)
            }
        }
        System.arraycopy(temp, 0, result, resultOffset, 16)
    }

    @JvmStatic
    fun rotateM(m: FloatArray, mOffset: Int, a: Float, x: Float, y: Float, z: Float) {
        if (a == 0f) return
        val rad = Math.toRadians(a.toDouble())
        val s = sin(rad).toFloat()
        val c = cos(rad).toFloat()

        var rx = x
        var ry = y
        var rz = z
        if (x != 0f || y != 0f || z != 1f) {
            val len = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            if (len == 0f) return
            rx /= len
            ry /= len
            rz /= len
        }

        val c1 = 1f - c
        val rm = FloatArray(16)
        rm[0] = rx * rx * c1 + c
        rm[1] = ry * rx * c1 + rz * s
        rm[2] = rz * rx * c1 - ry * s
        rm[3] = 0f

        rm[4] = rx * ry * c1 - rz * s
        rm[5] = ry * ry * c1 + c
        rm[6] = rz * ry * c1 + rx * s
        rm[7] = 0f

        rm[8] = rx * rz * c1 + ry * s
        rm[9] = ry * rz * c1 - rx * s
        rm[10] = rz * rz * c1 + c
        rm[11] = 0f

        rm[12] = 0f
        rm[13] = 0f
        rm[14] = 0f
        rm[15] = 1f

        multiplyMM(m, mOffset, m, mOffset, rm, 0)
    }
}
