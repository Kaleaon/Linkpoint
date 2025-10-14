package com.linkpoint.slproto.types

import java.nio.ByteBuffer
import kotlin.math.*

/**
 * Quaternion type used for rotations in Second Life
 */
data class LLQuaternion(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 1f,
) {
    companion object {
        val IDENTITY = LLQuaternion(0f, 0f, 0f, 1f)

        fun unpack(buffer: ByteBuffer): LLQuaternion {
            return LLQuaternion(
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
            )
        }

        /**
         * Create quaternion from axis and angle
         */
        fun fromAxisAngle(
            axis: LLVector3,
            angle: Float,
        ): LLQuaternion {
            val halfAngle = angle / 2f
            val sinHalf = sin(halfAngle)
            val cosHalf = cos(halfAngle)

            val normalized = axis.normalize()
            return LLQuaternion(
                normalized.x * sinHalf,
                normalized.y * sinHalf,
                normalized.z * sinHalf,
                cosHalf,
            )
        }

        /**
         * Create quaternion from Euler angles (pitch, yaw, roll)
         */
        fun fromEuler(
            pitch: Float,
            yaw: Float,
            roll: Float,
        ): LLQuaternion {
            val cy = cos(yaw * 0.5f)
            val sy = sin(yaw * 0.5f)
            val cp = cos(pitch * 0.5f)
            val sp = sin(pitch * 0.5f)
            val cr = cos(roll * 0.5f)
            val sr = sin(roll * 0.5f)

            return LLQuaternion(
                sr * cp * cy - cr * sp * sy,
                cr * sp * cy + sr * cp * sy,
                cr * cp * sy - sr * sp * cy,
                cr * cp * cy + sr * sp * sy,
            )
        }
    }

    fun pack(buffer: ByteBuffer) {
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        buffer.putFloat(w)
    }

    /**
     * Get the length (magnitude) of the quaternion
     */
    fun length(): Float {
        return sqrt(x * x + y * y + z * z + w * w)
    }

    /**
     * Normalize the quaternion
     */
    fun normalize(): LLQuaternion {
        val len = length()
        return if (len > 0f) {
            LLQuaternion(x / len, y / len, z / len, w / len)
        } else {
            IDENTITY.copy()
        }
    }

    /**
     * Get the conjugate of the quaternion
     */
    fun conjugate(): LLQuaternion {
        return LLQuaternion(-x, -y, -z, w)
    }

    /**
     * Multiply two quaternions
     */
    operator fun times(other: LLQuaternion): LLQuaternion {
        return LLQuaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z,
        )
    }

    /**
     * Rotate a vector by this quaternion
     */
    fun rotate(vec: LLVector3): LLVector3 {
        val qVec = LLQuaternion(vec.x, vec.y, vec.z, 0f)
        val result = this * qVec * conjugate()
        return LLVector3(result.x, result.y, result.z)
    }

    /**
     * Convert to Euler angles (pitch, yaw, roll)
     */
    fun toEuler(): Triple<Float, Float, Float> {
        // Roll (x-axis rotation)
        val sinr_cosp = 2f * (w * x + y * z)
        val cosr_cosp = 1f - 2f * (x * x + y * y)
        val roll = atan2(sinr_cosp, cosr_cosp)

        // Pitch (y-axis rotation)
        val sinp = 2f * (w * y - z * x)
        val pitch =
            if (abs(sinp) >= 1f) {
                (PI / 2f).toFloat() * sign(sinp) // Use 90 degrees if out of range
            } else {
                asin(sinp)
            }

        // Yaw (z-axis rotation)
        val siny_cosp = 2f * (w * z + x * y)
        val cosy_cosp = 1f - 2f * (y * y + z * z)
        val yaw = atan2(siny_cosp, cosy_cosp)

        return Triple(pitch, yaw, roll)
    }

    /**
     * Get forward vector
     */
    fun getForward(): LLVector3 {
        return rotate(LLVector3.X_AXIS)
    }

    /**
     * Get up vector
     */
    fun getUp(): LLVector3 {
        return rotate(LLVector3.Z_AXIS)
    }

    /**
     * Get left vector
     */
    fun getLeft(): LLVector3 {
        return rotate(LLVector3.Y_AXIS)
    }

    /**
     * Spherical linear interpolation
     */
    fun slerp(
        other: LLQuaternion,
        t: Float,
    ): LLQuaternion {
        var dot = x * other.x + y * other.y + z * other.z + w * other.w

        // If the dot product is negative, slerp won't take the shorter path
        val q2 =
            if (dot < 0f) {
                dot = -dot
                LLQuaternion(-other.x, -other.y, -other.z, -other.w)
            } else {
                other
            }

        if (dot > 0.9995f) {
            // Quaternions are very close, use linear interpolation
            return LLQuaternion(
                x + t * (q2.x - x),
                y + t * (q2.y - y),
                z + t * (q2.z - z),
                w + t * (q2.w - w),
            ).normalize()
        }

        val theta0 = acos(dot)
        val theta = theta0 * t
        val sinTheta = sin(theta)
        val sinTheta0 = sin(theta0)

        val s0 = cos(theta) - dot * sinTheta / sinTheta0
        val s1 = sinTheta / sinTheta0

        return LLQuaternion(
            s0 * x + s1 * q2.x,
            s0 * y + s1 * q2.y,
            s0 * z + s1 * q2.z,
            s0 * w + s1 * q2.w,
        )
    }
}
