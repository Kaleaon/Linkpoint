package com.linkpoint.protocol.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

/**
 * Second Life Vector3 type
 */
@Parcelize
data class LLVector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) : Parcelable {
    companion object {
        fun zero() = LLVector3(0f, 0f, 0f)
        fun one() = LLVector3(1f, 1f, 1f)
        fun unitX() = LLVector3(1f, 0f, 0f)
        fun unitY() = LLVector3(0f, 1f, 0f)
        fun unitZ() = LLVector3(0f, 0f, 1f)
        
        fun fromBytes(bytes: ByteArray, offset: Int = 0): LLVector3 {
            val buffer = ByteBuffer.wrap(bytes, offset, 12).order(ByteOrder.LITTLE_ENDIAN)
            return LLVector3(buffer.float, buffer.float, buffer.float)
        }
        
        /**
         * Decode from terse update format (used in UDP messages)
         */
        fun fromTerse(bytes: ByteArray, offset: Int = 0, range: Float = 256f): LLVector3 {
            val buffer = ByteBuffer.wrap(bytes, offset, 6).order(ByteOrder.LITTLE_ENDIAN)
            val x = (buffer.short.toInt() and 0xFFFF) / 65535f * range
            val y = (buffer.short.toInt() and 0xFFFF) / 65535f * range
            val z = (buffer.short.toInt() and 0xFFFF) / 65535f * range
            return LLVector3(x, y, z)
        }
    }
    
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        return buffer.array()
    }
    
    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun lengthSquared(): Float = x * x + y * y + z * z
    
    fun normalize(): LLVector3 {
        val len = length()
        return if (len > 0f) LLVector3(x / len, y / len, z / len) else zero()
    }
    
    fun dot(other: LLVector3): Float = x * other.x + y * other.y + z * other.z
    
    fun cross(other: LLVector3): LLVector3 = LLVector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    
    fun distance(other: LLVector3): Float = (this - other).length()

    fun distanceSquared(other: LLVector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx * dx + dy * dy + dz * dz
    }
    
    operator fun plus(other: LLVector3) = LLVector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: LLVector3) = LLVector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = LLVector3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = LLVector3(x / scalar, y / scalar, z / scalar)
    operator fun unaryMinus() = LLVector3(-x, -y, -z)
    
    fun lerp(target: LLVector3, t: Float): LLVector3 {
        return LLVector3(
            x + (target.x - x) * t,
            y + (target.y - y) * t,
            z + (target.z - z) * t
        )
    }
    
    override fun toString() = "($x, $y, $z)"
}

/**
 * Second Life Quaternion type
 */
@Parcelize
data class LLQuaternion(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 1f
) : Parcelable {
    companion object {
        fun identity() = LLQuaternion(0f, 0f, 0f, 1f)
        
        fun fromBytes(bytes: ByteArray, offset: Int = 0): LLQuaternion {
            val buffer = ByteBuffer.wrap(bytes, offset, 12).order(ByteOrder.LITTLE_ENDIAN)
            val x = buffer.float
            val y = buffer.float
            val z = buffer.float
            // W is calculated to normalize
            val wSquared = 1f - x * x - y * y - z * z
            val w = if (wSquared > 0f) sqrt(wSquared) else 0f
            return LLQuaternion(x, y, z, w)
        }
        
        /**
         * Decode from terse update format (3 shorts, W calculated)
         */
        fun fromTerse(bytes: ByteArray, offset: Int = 0): LLQuaternion {
            val buffer = ByteBuffer.wrap(bytes, offset, 6).order(ByteOrder.LITTLE_ENDIAN)
            val x = buffer.short / 32768f
            val y = buffer.short / 32768f
            val z = buffer.short / 32768f
            val wSquared = 1f - x * x - y * y - z * z
            val w = if (wSquared > 0f) sqrt(wSquared) else 0f
            return LLQuaternion(x, y, z, w)
        }
        
        fun fromEuler(pitch: Float, roll: Float, yaw: Float): LLQuaternion {
            val cy = kotlin.math.cos(yaw * 0.5f)
            val sy = kotlin.math.sin(yaw * 0.5f)
            val cp = kotlin.math.cos(pitch * 0.5f)
            val sp = kotlin.math.sin(pitch * 0.5f)
            val cr = kotlin.math.cos(roll * 0.5f)
            val sr = kotlin.math.sin(roll * 0.5f)
            
            return LLQuaternion(
                x = sr * cp * cy - cr * sp * sy,
                y = cr * sp * cy + sr * cp * sy,
                z = cr * cp * sy - sr * sp * cy,
                w = cr * cp * cy + sr * sp * sy
            )
        }
    }
    
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        return buffer.array()
    }
    
    fun normalize(): LLQuaternion {
        val len = sqrt(x * x + y * y + z * z + w * w)
        return if (len > 0f) LLQuaternion(x / len, y / len, z / len, w / len) else identity()
    }
    
    fun conjugate() = LLQuaternion(-x, -y, -z, w)
    
    operator fun times(other: LLQuaternion): LLQuaternion {
        return LLQuaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        )
    }
    
    fun rotate(v: LLVector3): LLVector3 {
        val qv = LLQuaternion(v.x, v.y, v.z, 0f)
        val result = this * qv * conjugate()
        return LLVector3(result.x, result.y, result.z)
    }
    
    fun slerp(target: LLQuaternion, t: Float): LLQuaternion {
        var dot = x * target.x + y * target.y + z * target.z + w * target.w
        
        var tx = target.x
        var ty = target.y
        var tz = target.z
        var tw = target.w
        
        if (dot < 0f) {
            dot = -dot
            tx = -tx
            ty = -ty
            tz = -tz
            tw = -tw
        }
        
        if (dot > 0.9995f) {
            return LLQuaternion(
                x + (tx - x) * t,
                y + (ty - y) * t,
                z + (tz - z) * t,
                w + (tw - w) * t
            ).normalize()
        }
        
        val theta0 = kotlin.math.acos(dot)
        val theta = theta0 * t
        val sinTheta = kotlin.math.sin(theta)
        val sinTheta0 = kotlin.math.sin(theta0)
        
        val s0 = kotlin.math.cos(theta) - dot * sinTheta / sinTheta0
        val s1 = sinTheta / sinTheta0
        
        return LLQuaternion(
            s0 * x + s1 * tx,
            s0 * y + s1 * ty,
            s0 * z + s1 * tz,
            s0 * w + s1 * tw
        )
    }
    
    /**
     * Convert to 4x4 rotation matrix (column-major for OpenGL/Filament)
     */
    fun toMatrix(out: FloatArray = FloatArray(16)): FloatArray {
        val xx = x * x
        val yy = y * y
        val zz = z * z
        val xy = x * y
        val xz = x * z
        val yz = y * z
        val wx = w * x
        val wy = w * y
        val wz = w * z
        
        out[0] = 1f - 2f * (yy + zz)
        out[1] = 2f * (xy + wz)
        out[2] = 2f * (xz - wy)
        out[3] = 0f
        
        out[4] = 2f * (xy - wz)
        out[5] = 1f - 2f * (xx + zz)
        out[6] = 2f * (yz + wx)
        out[7] = 0f
        
        out[8] = 2f * (xz + wy)
        out[9] = 2f * (yz - wx)
        out[10] = 1f - 2f * (xx + yy)
        out[11] = 0f
        
        out[12] = 0f
        out[13] = 0f
        out[14] = 0f
        out[15] = 1f
        
        return out
    }
    
    /**
     * Convert to Euler angles (pitch, roll, yaw in radians)
     */
    fun toEuler(): LLVector3 {
        // Roll (x-axis rotation)
        val sinrCosp = 2f * (w * x + y * z)
        val cosrCosp = 1f - 2f * (x * x + y * y)
        val roll = kotlin.math.atan2(sinrCosp, cosrCosp)
        
        // Pitch (y-axis rotation)
        val sinp = 2f * (w * y - z * x)
        val pitch = if (kotlin.math.abs(sinp) >= 1f) {
            kotlin.math.PI.toFloat() / 2f * kotlin.math.sign(sinp)
        } else {
            kotlin.math.asin(sinp)
        }
        
        // Yaw (z-axis rotation)
        val sinyCosp = 2f * (w * z + x * y)
        val cosyCosp = 1f - 2f * (y * y + z * z)
        val yaw = kotlin.math.atan2(sinyCosp, cosyCosp)
        
        return LLVector3(pitch, roll, yaw)
    }
    
    override fun toString() = "($x, $y, $z, $w)"
}

/**
 * Second Life Color4 type
 */
@Parcelize
data class LLColor4(
    val r: Float = 1f,
    val g: Float = 1f,
    val b: Float = 1f,
    val a: Float = 1f
) : Parcelable {
    companion object {
        fun white() = LLColor4(1f, 1f, 1f, 1f)
        fun black() = LLColor4(0f, 0f, 0f, 1f)
        fun transparent() = LLColor4(0f, 0f, 0f, 0f)
        
        fun fromBytes(bytes: ByteArray, offset: Int = 0): LLColor4 {
            return LLColor4(
                (bytes[offset].toInt() and 0xFF) / 255f,
                (bytes[offset + 1].toInt() and 0xFF) / 255f,
                (bytes[offset + 2].toInt() and 0xFF) / 255f,
                (bytes[offset + 3].toInt() and 0xFF) / 255f
            )
        }
    }
    
    fun toBytes(): ByteArray = byteArrayOf(
        (r * 255).toInt().toByte(),
        (g * 255).toInt().toByte(),
        (b * 255).toInt().toByte(),
        (a * 255).toInt().toByte()
    )
    
    fun toInt(): Int {
        val ri = (r * 255).toInt() and 0xFF
        val gi = (g * 255).toInt() and 0xFF
        val bi = (b * 255).toInt() and 0xFF
        val ai = (a * 255).toInt() and 0xFF
        return (ai shl 24) or (ri shl 16) or (gi shl 8) or bi
    }
    
    fun lerp(target: LLColor4, t: Float): LLColor4 {
        return LLColor4(
            r + (target.r - r) * t,
            g + (target.g - g) * t,
            b + (target.b - b) * t,
            a + (target.a - a) * t
        )
    }
}
