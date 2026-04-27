package com.linkpoint.protocol.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin
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
        private const val NORMALIZE_EPSILON_SQUARED = 1.0e-12f
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

    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()
    
    fun normalize(): LLVector3 {
        if (!isFinite()) return zero()
        val lenSq = lengthSquared()
        if (!lenSq.isFinite() || lenSq <= NORMALIZE_EPSILON_SQUARED) return zero()
        val invLen = 1f / sqrt(lenSq)
        return LLVector3(x * invLen, y * invLen, z * invLen)
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
        private const val NORMALIZE_EPSILON_SQUARED = 1.0e-12f
        fun identity() = LLQuaternion(0f, 0f, 0f, 1f)
        
        fun fromBytes(bytes: ByteArray, offset: Int = 0): LLQuaternion {
            val buffer = ByteBuffer.wrap(bytes, offset, 12).order(ByteOrder.LITTLE_ENDIAN)
            val x = buffer.float
            val y = buffer.float
            val z = buffer.float
            // W is calculated to normalize
            val wSquared = 1f - x * x - y * y - z * z
            val w = if (wSquared > 0f) sqrt(wSquared) else 0f
            return LLQuaternion(x, y, z, w).normalize()
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
            return LLQuaternion(x, y, z, w).normalize()
        }
        
        fun fromEuler(pitch: Float, roll: Float, yaw: Float): LLQuaternion {
            val cy = cos(yaw * 0.5f)
            val sy = sin(yaw * 0.5f)
            val cp = cos(pitch * 0.5f)
            val sp = sin(pitch * 0.5f)
            val cr = cos(roll * 0.5f)
            val sr = sin(roll * 0.5f)
            
            return LLQuaternion(
                x = sr * cp * cy - cr * sp * sy,
                y = cr * sp * cy + sr * cp * sy,
                z = cr * cp * sy - sr * sp * cy,
                w = cr * cp * cy + sr * sp * sy
            ).normalize()
        }

        fun fromAngleAxis(angle: Float, axis: LLVector3): LLQuaternion {
            val normalizedAxis = axis.normalize()
            if (!normalizedAxis.isFinite()) return identity()
            val halfAngle = angle * 0.5f
            val s = sin(halfAngle)
            return LLQuaternion(
                x = normalizedAxis.x * s,
                y = normalizedAxis.y * s,
                z = normalizedAxis.z * s,
                w = cos(halfAngle)
            ).normalize()
        }
    }
    
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        return buffer.array()
    }
    
    fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite() && w.isFinite()

    fun normalize(): LLQuaternion {
        if (!isFinite()) return identity()
        val lenSq = x * x + y * y + z * z + w * w
        if (!lenSq.isFinite() || lenSq <= NORMALIZE_EPSILON_SQUARED) return identity()
        val invLen = 1f / sqrt(lenSq)
        return LLQuaternion(x * invLen, y * invLen, z * invLen, w * invLen)
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
        if (!v.isFinite()) return LLVector3.zero()
        val rotation = normalize()
        val qv = LLQuaternion(v.x, v.y, v.z, 0f)
        val result = rotation * qv * rotation.conjugate()
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
        val q = normalize()
        val xx = q.x * q.x
        val yy = q.y * q.y
        val zz = q.z * q.z
        val xy = q.x * q.y
        val xz = q.x * q.z
        val yz = q.y * q.z
        val wx = q.w * q.x
        val wy = q.w * q.y
        val wz = q.w * q.z
        
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
        val q = normalize()
        // Roll (x-axis rotation)
        val sinrCosp = 2f * (q.w * q.x + q.y * q.z)
        val cosrCosp = 1f - 2f * (q.x * q.x + q.y * q.y)
        val roll = atan2(sinrCosp, cosrCosp)
        
        // Pitch (y-axis rotation)
        val sinp = 2f * (q.w * q.y - q.z * q.x)
        val pitch = if (abs(sinp) >= 1f) {
            Math.PI.toFloat() / 2f * sign(sinp)
        } else {
            asin(sinp)
        }
        
        // Yaw (z-axis rotation)
        val sinyCosp = 2f * (q.w * q.z + q.x * q.y)
        val cosyCosp = 1f - 2f * (q.y * q.y + q.z * q.z)
        val yaw = atan2(sinyCosp, cosyCosp)
        
        return LLVector3(pitch, roll, yaw)
    }

    fun toAngleAxis(): Pair<Float, LLVector3> {
        val q = normalize()
        val angle = 2f * acos(q.w.coerceIn(-1f, 1f))
        val axisScale = sqrt((1f - q.w * q.w).coerceAtLeast(0f))
        if (axisScale <= 1e-6f) return 0f to LLVector3.unitX()
        return angle to LLVector3(q.x / axisScale, q.y / axisScale, q.z / axisScale).normalize()
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
