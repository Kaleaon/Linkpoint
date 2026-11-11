package com.linkpoint.protocol.spatial

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Vector3U16 {
    fun dequantize(
        u16X: Int,
        u16Y: Int,
        u16Z: Int,
        minVec: FloatArray = floatArrayOf(-128.0f, -128.0f, -128.0f),
        maxVec: FloatArray = floatArrayOf(128.0f, 128.0f, 128.0f)
    ): FloatArray {
        val x = minVec[0] + (u16X / 65535.0f) * (maxVec[0] - minVec[0])
        val y = minVec[1] + (u16Y / 65535.0f) * (maxVec[1] - minVec[1])
        val z = minVec[2] + (u16Z / 65535.0f) * (maxVec[2] - minVec[2])
        return floatArrayOf(x, y, z)
    }

    fun quantize(
        x: Float,
        y: Float,
        z: Float,
        minVec: FloatArray = floatArrayOf(-128.0f, -128.0f, -128.0f),
        maxVec: FloatArray = floatArrayOf(128.0f, 128.0f, 128.0f)
    ): IntArray {
        val qX = (((x - minVec[0]) / (maxVec[0] - minVec[0])) * 65535.0f).roundToInt().coerceIn(0, 65535)
        val qY = (((y - minVec[1]) / (maxVec[1] - minVec[1])) * 65535.0f).roundToInt().coerceIn(0, 65535)
        val qZ = (((z - minVec[2]) / (maxVec[2] - minVec[2])) * 65535.0f).roundToInt().coerceIn(0, 65535)
        return intArrayOf(qX, qY, qZ)
    }
}

object Vector3U8 {
    fun dequantize(
        u8X: Int,
        u8Y: Int,
        u8Z: Int,
        minVec: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
        maxVec: FloatArray = floatArrayOf(255.0f, 255.0f, 255.0f)
    ): FloatArray {
        val x = minVec[0] + (u8X / 255.0f) * (maxVec[0] - minVec[0])
        val y = minVec[1] + (u8Y / 255.0f) * (maxVec[1] - minVec[1])
        val z = minVec[2] + (u8Z / 255.0f) * (maxVec[2] - minVec[2])
        return floatArrayOf(x, y, z)
    }

    fun quantize(
        x: Float,
        y: Float,
        z: Float,
        minVec: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
        maxVec: FloatArray = floatArrayOf(255.0f, 255.0f, 255.0f)
    ): IntArray {
        val qX = (((x - minVec[0]) / (maxVec[0] - minVec[0])) * 255.0f).roundToInt().coerceIn(0, 255)
        val qY = (((y - minVec[1]) / (maxVec[1] - minVec[1])) * 255.0f).roundToInt().coerceIn(0, 255)
        val qZ = (((z - minVec[2]) / (maxVec[2] - minVec[2])) * 255.0f).roundToInt().coerceIn(0, 255)
        return intArrayOf(qX, qY, qZ)
    }
}

object PackedQuaternion {
    fun unpack16(xI16: Short, yI16: Short, zI16: Short): FloatArray {
        val x = xI16.toFloat() / 32767.0f
        val y = yI16.toFloat() / 32767.0f
        val z = zI16.toFloat() / 32767.0f

        val wSq = 1.0f - (x * x + y * y + z * z)
        val w = if (wSq > 0.0f) sqrt(wSq) else 0.0f

        val mag = sqrt(x * x + y * y + z * z + w * w)
        if (mag > 0) {
            return floatArrayOf(x / mag, y / mag, z / mag, w / mag)
        }
        return floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }
}
