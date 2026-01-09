package com.linkpoint.slproto.types

import java.nio.ByteBuffer
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Quaternion class for Second Life rotations
 * Used throughout the protocol for object and avatar orientations
 */
class LLQuaternion(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var z: Float = 0.0f,
    var w: Float = 1.0f
) {
    enum class Order {
        XYZ, YZX, ZXY, XZY, YXZ, ZYX
    }
    
    companion object {
        const val FP_MAG_THRESHOLD: Float = 1.0E-7f
        
        @JvmField
        val Identity = LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f)
        
        @JvmStatic
        fun fromMatrix(matrix: FloatArray): LLQuaternion {
            val quat = LLQuaternion()
            val trace = matrix[0] + 1.0f + matrix[5] + matrix[10]
            
            if (trace > 0.5f) {
                val s = (sqrt(trace.toDouble()) * 2.0).toFloat()
                quat.x = (matrix[9] - matrix[6]) / s
                quat.y = (matrix[2] - matrix[8]) / s
                quat.z = (matrix[4] - matrix[1]) / s
                quat.w = s * 0.25f
            } else if (matrix[0] > matrix[5] && matrix[0] > matrix[10]) {
                val s = (sqrt(((matrix[0] + 1.0f) - matrix[5] - matrix[10]).toDouble()) * 2.0).toFloat()
                quat.x = 0.25f * s
                quat.y = (matrix[4] + matrix[1]) / s
                quat.z = (matrix[2] + matrix[8]) / s
                quat.w = (matrix[9] - matrix[6]) / s
            } else if (matrix[5] > matrix[10]) {
                val s = (sqrt(((matrix[5] + 1.0f) - matrix[0] - matrix[10]).toDouble()) * 2.0).toFloat()
                quat.x = (matrix[4] + matrix[1]) / s
                quat.y = 0.25f * s
                quat.z = (matrix[9] + matrix[6]) / s
                quat.w = (matrix[2] - matrix[8]) / s
            } else {
                val s = (sqrt(((matrix[10] + 1.0f) - matrix[0] - matrix[5]).toDouble()) * 2.0).toFloat()
                quat.x = (matrix[2] + matrix[8]) / s
                quat.y = (matrix[9] + matrix[6]) / s
                quat.z = 0.25f * s
                quat.w = (matrix[4] - matrix[1]) / s
            }
            return quat
        }
        
        @JvmStatic
        fun lerp(a: LLQuaternion, b: LLQuaternion, t: Float): LLQuaternion {
            return LLQuaternion(
                a.x + ((b.x - a.x) * t),
                a.y + ((b.y - a.y) * t),
                a.z + ((b.z - a.z) * t),
                a.w + ((b.w - a.w) * t)
            ).also { it.normalize() }
        }
        
        @JvmStatic
        fun slerp(a: LLQuaternion, b: LLQuaternion, t: Float): LLQuaternion {
            var bx = b.x
            var by = b.y
            var bz = b.z
            var bw = b.w
            
            var dot = a.x * bx + a.y * by + a.z * bz + a.w * bw
            
            // If negative dot, negate one quaternion to take shortest path
            if (dot < 0.0f) {
                bx = -bx
                by = -by
                bz = -bz
                bw = -bw
                dot = -dot
            }
            
            // If quaternions are very close, use linear interpolation
            if (dot > 0.9995f) {
                return lerp(a, LLQuaternion(bx, by, bz, bw), t)
            }
            
            val theta = acos(dot.toDouble()).toFloat()
            val sinTheta = sin(theta.toDouble()).toFloat()
            
            val wa = sin(((1.0f - t) * theta).toDouble()).toFloat() / sinTheta
            val wb = sin((t * theta).toDouble()).toFloat() / sinTheta
            
            return LLQuaternion(
                wa * a.x + wb * bx,
                wa * a.y + wb * by,
                wa * a.z + wb * bz,
                wa * a.w + wb * bw
            )
        }
        
        @JvmStatic
        fun parseFloatQuat(buffer: ByteBuffer): LLQuaternion {
            return LLQuaternion(
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat()
            )
        }
        
        @JvmStatic
        fun parseU16Quat(buffer: ByteBuffer): LLQuaternion {
            val x = LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, -1.0f, 1.0f)
            val y = LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, -1.0f, 1.0f)
            val z = LLTersePacking.U16_to_float(buffer.getShort().toInt() and 0xFFFF, -1.0f, 1.0f)
            val wSquared = 1.0f - (x * x) - (y * y) - (z * z)
            val w = if (wSquared > 0.0f) sqrt(wSquared.toDouble()).toFloat() else 0.0f
            return LLQuaternion(x, y, z, w)
        }
    }
    
    private var matrix: FloatArray? = null
    private var inverseMatrix: FloatArray? = null
    
    constructor(other: LLQuaternion) : this(other.x, other.y, other.z, other.w)
    
    constructor(matrix: FloatArray) : this() {
        val quat = fromMatrix(matrix)
        x = quat.x
        y = quat.y
        z = quat.z
        w = quat.w
    }
    
    fun dot(other: LLQuaternion): Float {
        return x * other.x + y * other.y + z * other.z + w * other.w
    }
    
    fun cross(other: LLQuaternion): LLVector3 {
        return LLVector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }
    
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is LLQuaternion) return false
        return x == other.x && y == other.y && z == other.z && w == other.w
    }
    
    override fun hashCode(): Int {
        var result = java.lang.Float.floatToIntBits(x)
        result = 31 * result + java.lang.Float.floatToIntBits(y)
        result = 31 * result + java.lang.Float.floatToIntBits(z)
        result = 31 * result + java.lang.Float.floatToIntBits(w)
        return result
    }
    
    fun magnitude(): Float {
        return sqrt((x * x + y * y + z * z + w * w).toDouble()).toFloat()
    }
    
    fun magVecSquared(): Float {
        return x * x + y * y + z * z + w * w
    }
    
    fun normalize(): Float {
        val mag = magnitude()
        if (mag > FP_MAG_THRESHOLD) {
            val invMag = 1.0f / mag
            x *= invMag
            y *= invMag
            z *= invMag
            w *= invMag
        } else {
            x = 0.0f
            y = 0.0f
            z = 0.0f
            w = 1.0f
        }
        matrix = null
        inverseMatrix = null
        return mag
    }
    
    fun conjugate(): LLQuaternion {
        return LLQuaternion(-x, -y, -z, w)
    }
    
    fun inverse(): LLQuaternion {
        val magSq = magVecSquared()
        if (magSq < FP_MAG_THRESHOLD) {
            return LLQuaternion()
        }
        val invMag = 1.0f / magSq
        return LLQuaternion(-x * invMag, -y * invMag, -z * invMag, w * invMag)
    }
    
    fun mul(other: LLQuaternion) {
        val newW = w * other.w - x * other.x - y * other.y - z * other.z
        val newX = w * other.x + x * other.w + y * other.z - z * other.y
        val newY = w * other.y - x * other.z + y * other.w + z * other.x
        val newZ = w * other.z + x * other.y - y * other.x + z * other.w
        
        w = newW
        x = newX
        y = newY
        z = newZ
        
        matrix = null
        inverseMatrix = null
    }
    
    fun set(newX: Float, newY: Float, newZ: Float, newW: Float) {
        x = newX
        y = newY
        z = newZ
        w = newW
        matrix = null
        inverseMatrix = null
    }
    
    fun set(other: LLQuaternion?) {
        if (other != null) {
            x = other.x
            y = other.y
            z = other.z
            w = other.w
            matrix = null
            inverseMatrix = null
        }
    }
    
    fun setFromAxisAngle(axis: LLVector3, angleRadians: Float) {
        val halfAngle = angleRadians * 0.5f
        val s = sin(halfAngle.toDouble()).toFloat()
        x = axis.x * s
        y = axis.y * s
        z = axis.z * s
        w = cos(halfAngle.toDouble()).toFloat()
        matrix = null
        inverseMatrix = null
    }
    
    fun setFromEuler(roll: Float, pitch: Float, yaw: Float, order: Order = Order.XYZ) {
        val halfRoll = roll * 0.5f
        val halfPitch = pitch * 0.5f
        val halfYaw = yaw * 0.5f
        
        val cr = cos(halfRoll.toDouble()).toFloat()
        val sr = sin(halfRoll.toDouble()).toFloat()
        val cp = cos(halfPitch.toDouble()).toFloat()
        val sp = sin(halfPitch.toDouble()).toFloat()
        val cy = cos(halfYaw.toDouble()).toFloat()
        val sy = sin(halfYaw.toDouble()).toFloat()
        
        when (order) {
            Order.XYZ -> {
                w = cr * cp * cy + sr * sp * sy
                x = sr * cp * cy - cr * sp * sy
                y = cr * sp * cy + sr * cp * sy
                z = cr * cp * sy - sr * sp * cy
            }
            Order.YZX -> {
                w = cr * cp * cy - sr * sp * sy
                x = sr * cp * cy + cr * sp * sy
                y = cr * sp * cy + sr * cp * sy
                z = cr * cp * sy - sr * sp * cy
            }
            Order.ZXY -> {
                w = cr * cp * cy + sr * sp * sy
                x = sr * cp * cy - cr * sp * sy
                y = cr * sp * cy - sr * cp * sy
                z = cr * cp * sy + sr * sp * cy
            }
            Order.XZY -> {
                w = cr * cp * cy + sr * sp * sy
                x = sr * cp * cy + cr * sp * sy
                y = cr * sp * cy + sr * cp * sy
                z = cr * cp * sy - sr * sp * cy
            }
            Order.YXZ -> {
                w = cr * cp * cy - sr * sp * sy
                x = sr * cp * cy - cr * sp * sy
                y = cr * sp * cy + sr * cp * sy
                z = cr * cp * sy + sr * sp * cy
            }
            Order.ZYX -> {
                w = cr * cp * cy + sr * sp * sy
                x = sr * cp * cy - cr * sp * sy
                y = cr * sp * cy + sr * cp * sy
                z = cr * cp * sy - sr * sp * cy
            }
        }
        
        matrix = null
        inverseMatrix = null
    }
    
    fun getMatrix(): FloatArray {
        if (matrix == null) {
            matrix = FloatArray(16)
            updateMatrix(matrix!!)
        }
        return matrix!!
    }
    
    fun getInverseMatrix(): FloatArray {
        if (inverseMatrix == null) {
            inverseMatrix = FloatArray(16)
            val conj = conjugate()
            conj.updateMatrix(inverseMatrix!!)
        }
        return inverseMatrix!!
    }
    
    private fun updateMatrix(m: FloatArray) {
        val xx = x * x
        val yy = y * y
        val zz = z * z
        val xy = x * y
        val xz = x * z
        val yz = y * z
        val wx = w * x
        val wy = w * y
        val wz = w * z
        
        m[0] = 1.0f - 2.0f * (yy + zz)
        m[1] = 2.0f * (xy + wz)
        m[2] = 2.0f * (xz - wy)
        m[3] = 0.0f
        
        m[4] = 2.0f * (xy - wz)
        m[5] = 1.0f - 2.0f * (xx + zz)
        m[6] = 2.0f * (yz + wx)
        m[7] = 0.0f
        
        m[8] = 2.0f * (xz + wy)
        m[9] = 2.0f * (yz - wx)
        m[10] = 1.0f - 2.0f * (xx + yy)
        m[11] = 0.0f
        
        m[12] = 0.0f
        m[13] = 0.0f
        m[14] = 0.0f
        m[15] = 1.0f
    }
    
    fun rotateVector(v: LLVector3): LLVector3 {
        val result = LLVector3(v)
        result.mul(this)
        return result
    }
    
    override fun toString(): String {
        return String.format("(%.4f, %.4f, %.4f, %.4f)", x, y, z, w)
    }
    
    // Operator overloads
    operator fun times(other: LLQuaternion): LLQuaternion {
        val result = LLQuaternion(this)
        result.mul(other)
        return result
    }
}
