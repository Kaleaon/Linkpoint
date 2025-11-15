package com.linkpoint.slproto.types

import java.nio.ByteBuffer
import kotlin.math.sqrt

/**
 * 3D vector type used in Second Life protocol
 */
data class LLVector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
) {
    companion object {
        val ZERO = LLVector3(0f, 0f, 0f)
        val X_AXIS = LLVector3(1f, 0f, 0f)
        val Y_AXIS = LLVector3(0f, 1f, 0f)
        val Z_AXIS = LLVector3(0f, 0f, 1f)

        fun unpack(buffer: ByteBuffer): LLVector3 {
            return LLVector3(
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
            )
        }
    }

    fun pack(buffer: ByteBuffer) {
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
    }

    fun length(): Float {
        return sqrt(x * x + y * y + z * z)
    }

    fun lengthSquared(): Float {
        return x * x + y * y + z * z
    }

    fun normalize(): LLVector3 {
        val len = length()
        return if (len > 0f) {
            LLVector3(x / len, y / len, z / len)
        } else {
            ZERO.copy()
        }
    }

    operator fun plus(other: LLVector3): LLVector3 {
        return LLVector3(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: LLVector3): LLVector3 {
        return LLVector3(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(scalar: Float): LLVector3 {
        return LLVector3(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Float): LLVector3 {
        return LLVector3(x / scalar, y / scalar, z / scalar)
    }

    fun dot(other: LLVector3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun cross(other: LLVector3): LLVector3 {
        return LLVector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x,
        )
    }

    fun distanceTo(other: LLVector3): Float {
        return (this - other).length()
    }
}

/**
 * 3D vector with double precision
 */
data class LLVector3d(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
) {
    companion object {
        val ZERO = LLVector3d(0.0, 0.0, 0.0)

        fun unpack(buffer: ByteBuffer): LLVector3d {
            return LLVector3d(
                buffer.getDouble(),
                buffer.getDouble(),
                buffer.getDouble(),
            )
        }
    }

    fun pack(buffer: ByteBuffer) {
        buffer.putDouble(x)
        buffer.putDouble(y)
        buffer.putDouble(z)
    }

    fun length(): Double {
        return sqrt(x * x + y * y + z * z)
    }

    operator fun plus(other: LLVector3d): LLVector3d {
        return LLVector3d(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: LLVector3d): LLVector3d {
        return LLVector3d(x - other.x, y - other.y, z - other.z)
    }

    fun toVector3(): LLVector3 {
        return LLVector3(x.toFloat(), y.toFloat(), z.toFloat())
    }
}

/**
 * 4D vector
 */
data class LLVector4(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 0f,
) {
    companion object {
        fun unpack(buffer: ByteBuffer): LLVector4 {
            return LLVector4(
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
            )
        }
    }

    fun pack(buffer: ByteBuffer) {
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        buffer.putFloat(w)
    }
}
