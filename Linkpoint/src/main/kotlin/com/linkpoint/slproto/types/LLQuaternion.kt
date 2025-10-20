package com.linkpoint.slproto.types

import com.linkpoint.slproto.llsd.LLSD
import kotlin.math.*

/**
 * LLQuaternion - Quaternion rotation class
 * 
 * Based on Firestorm's llquaternion.h/cpp
 * 
 * IMPORTANT: All quaternions are assumed to be UNIT quaternions!
 * All vectors and matrices passed as arguments must be normalized.
 * Bad things will happen if these assumptions are violated.
 */
class LLQuaternion {
    
    // Components [x, y, z, w]
    val mQ = FloatArray(LENGTHOFQUAT)
    
    companion object {
        const val LENGTHOFQUAT = 4
        const val VX = 0
        const val VY = 1
        const val VZ = 2
        const val VS = 3  // S for scalar (w component)
        
        val DEFAULT = LLQuaternion()  // Identity quaternion
        
        const val FP_MAG_THRESHOLD = 1e-7f
    }
    
    // Accessors
    var x: Float
        get() = mQ[VX]
        set(value) { mQ[VX] = value }
    
    var y: Float
        get() = mQ[VY]
        set(value) { mQ[VY] = value }
    
    var z: Float
        get() = mQ[VZ]
        set(value) { mQ[VZ] = value }
    
    var w: Float
        get() = mQ[VS]
        set(value) { mQ[VS] = value }
    
    // Constructors
    constructor() {
        // Identity quaternion
        mQ[VX] = 0f
        mQ[VY] = 0f
        mQ[VZ] = 0f
        mQ[VS] = 1f
    }
    
    constructor(x: Float, y: Float, z: Float, w: Float) {
        mQ[VX] = x
        mQ[VY] = y
        mQ[VZ] = z
        mQ[VS] = w
        normalize()
    }
    
    constructor(q: FloatArray) {
        mQ[VX] = q[VX]
        mQ[VY] = q[VY]
        mQ[VZ] = q[VZ]
        mQ[VS] = q[VS]
        normalize()
    }
    
    constructor(other: LLQuaternion) {
        mQ[VX] = other.mQ[VX]
        mQ[VY] = other.mQ[VY]
        mQ[VZ] = other.mQ[VZ]
        mQ[VS] = other.mQ[VS]
    }
    
    // Axis-angle constructor
    constructor(angle: Float, axis: LLVector3) {
        setAngleAxis(angle, axis)
    }
    
    // Euler angles constructor
    constructor(roll: Float, pitch: Float, yaw: Float) {
        setEulerAngles(roll, pitch, yaw)
    }
    
    // LLSD constructor
    constructor(llsd: LLSD) {
        setValue(llsd)
    }
    
    // Matrix3 constructor (from rotation matrix)
    constructor(mat: Array<FloatArray>) {
        setFromMatrix3(mat)
    }
    
    // LLSD conversion
    fun getValue(): LLSD {
        val array = LLSD()
        array.add(LLSD(mQ[VX]))
        array.add(LLSD(mQ[VY]))
        array.add(LLSD(mQ[VZ]))
        array.add(LLSD(mQ[VS]))
        return array
    }
    
    fun setValue(llsd: LLSD) {
        mQ[VX] = llsd[0].asFloat()
        mQ[VY] = llsd[1].asFloat()
        mQ[VZ] = llsd[2].asFloat()
        mQ[VS] = llsd[3].asFloat()
    }
    
    // Validation
    fun isFinite(): Boolean {
        return mQ[VX].isFinite() && mQ[VY].isFinite() && 
               mQ[VZ].isFinite() && mQ[VS].isFinite()
    }
    
    fun isIdentity(): Boolean {
        return mQ[VX] == 0f && mQ[VY] == 0f && mQ[VZ] == 0f && mQ[VS] == 1f
    }
    
    fun isNotIdentity(): Boolean = !isIdentity()
    
    // Setters
    fun set(x: Float, y: Float, z: Float, w: Float): LLQuaternion {
        mQ[VX] = x
        mQ[VY] = y
        mQ[VZ] = z
        mQ[VS] = w
        normalize()
        return this
    }
    
    fun set(other: LLQuaternion): LLQuaternion {
        mQ[VX] = other.mQ[VX]
        mQ[VY] = other.mQ[VY]
        mQ[VZ] = other.mQ[VZ]
        mQ[VS] = other.mQ[VS]
        return this
    }
    
    fun loadIdentity() {
        mQ[VX] = 0f
        mQ[VY] = 0f
        mQ[VZ] = 0f
        mQ[VS] = 1f
    }
    
    // Axis-angle conversion
    fun setAngleAxis(angle: Float, x: Float, y: Float, z: Float): LLQuaternion {
        val axis = LLVector3(x, y, z)
        axis.normalize()
        return setAngleAxis(angle, axis)
    }
    
    fun setAngleAxis(angle: Float, axis: LLVector3): LLQuaternion {
        val halfAngle = angle * 0.5f
        val sinHalf = sin(halfAngle)
        
        mQ[VX] = axis.x * sinHalf
        mQ[VY] = axis.y * sinHalf
        mQ[VZ] = axis.z * sinHalf
        mQ[VS] = cos(halfAngle)
        
        return this
    }
    
    fun getAngleAxis(): Pair<Float, LLVector3> {
        val angle = 2f * acos(mQ[VS].coerceIn(-1f, 1f))
        val sinHalf = sin(angle * 0.5f)
        
        val axis = if (abs(sinHalf) > FP_MAG_THRESHOLD) {
            LLVector3(
                mQ[VX] / sinHalf,
                mQ[VY] / sinHalf,
                mQ[VZ] / sinHalf
            )
        } else {
            LLVector3(1f, 0f, 0f)  // Default axis
        }
        
        return Pair(angle, axis)
    }
    
    // Euler angles (roll, pitch, yaw)
    fun setEulerAngles(roll: Float, pitch: Float, yaw: Float): LLQuaternion {
        // Convert Euler angles to quaternion
        // Order: Z (yaw) * Y (pitch) * X (roll)
        val cy = cos(yaw * 0.5f)
        val sy = sin(yaw * 0.5f)
        val cp = cos(pitch * 0.5f)
        val sp = sin(pitch * 0.5f)
        val cr = cos(roll * 0.5f)
        val sr = sin(roll * 0.5f)
        
        mQ[VS] = cy * cp * cr + sy * sp * sr
        mQ[VX] = cy * cp * sr - sy * sp * cr
        mQ[VY] = sy * cp * sr + cy * sp * cr
        mQ[VZ] = sy * cp * cr - cy * sp * sr
        
        normalize()
        return this
    }
    
    fun getEulerAngles(): Triple<Float, Float, Float> {
        // Extract Euler angles from quaternion
        val sinr_cosp = 2f * (mQ[VS] * mQ[VX] + mQ[VY] * mQ[VZ])
        val cosr_cosp = 1f - 2f * (mQ[VX] * mQ[VX] + mQ[VY] * mQ[VY])
        val roll = atan2(sinr_cosp, cosr_cosp)
        
        val sinp = 2f * (mQ[VS] * mQ[VY] - mQ[VZ] * mQ[VX])
        val pitch = if (abs(sinp) >= 1f) {
            (PI.toFloat() / 2f) * sign(sinp) // Use 90 degrees if out of range
        } else {
            asin(sinp)
        }
        
        val siny_cosp = 2f * (mQ[VS] * mQ[VZ] + mQ[VX] * mQ[VY])
        val cosy_cosp = 1f - 2f * (mQ[VY] * mQ[VY] + mQ[VZ] * mQ[VZ])
        val yaw = atan2(siny_cosp, cosy_cosp)
        
        return Triple(roll, pitch, yaw)
    }
    
    // Matrix conversion
    fun setFromMatrix3(mat: Array<FloatArray>): LLQuaternion {
        // Convert 3x3 rotation matrix to quaternion
        val trace = mat[0][0] + mat[1][1] + mat[2][2]
        
        if (trace > 0f) {
            val s = sqrt(trace + 1f) * 2f
            mQ[VS] = 0.25f * s
            mQ[VX] = (mat[2][1] - mat[1][2]) / s
            mQ[VY] = (mat[0][2] - mat[2][0]) / s
            mQ[VZ] = (mat[1][0] - mat[0][1]) / s
        } else if (mat[0][0] > mat[1][1] && mat[0][0] > mat[2][2]) {
            val s = sqrt(1f + mat[0][0] - mat[1][1] - mat[2][2]) * 2f
            mQ[VS] = (mat[2][1] - mat[1][2]) / s
            mQ[VX] = 0.25f * s
            mQ[VY] = (mat[0][1] + mat[1][0]) / s
            mQ[VZ] = (mat[0][2] + mat[2][0]) / s
        } else if (mat[1][1] > mat[2][2]) {
            val s = sqrt(1f + mat[1][1] - mat[0][0] - mat[2][2]) * 2f
            mQ[VS] = (mat[0][2] - mat[2][0]) / s
            mQ[VX] = (mat[0][1] + mat[1][0]) / s
            mQ[VY] = 0.25f * s
            mQ[VZ] = (mat[1][2] + mat[2][1]) / s
        } else {
            val s = sqrt(1f + mat[2][2] - mat[0][0] - mat[1][1]) * 2f
            mQ[VS] = (mat[1][0] - mat[0][1]) / s
            mQ[VX] = (mat[0][2] + mat[2][0]) / s
            mQ[VY] = (mat[1][2] + mat[2][1]) / s
            mQ[VZ] = 0.25f * s
        }
        
        normalize()
        return this
    }
    
    fun toMatrix3(): Array<FloatArray> {
        // Convert quaternion to 3x3 rotation matrix
        val mat = Array(3) { FloatArray(3) }
        
        val xx = mQ[VX] * mQ[VX]
        val xy = mQ[VX] * mQ[VY]
        val xz = mQ[VX] * mQ[VZ]
        val xw = mQ[VX] * mQ[VS]
        
        val yy = mQ[VY] * mQ[VY]
        val yz = mQ[VY] * mQ[VZ]
        val yw = mQ[VY] * mQ[VS]
        
        val zz = mQ[VZ] * mQ[VZ]
        val zw = mQ[VZ] * mQ[VS]
        
        mat[0][0] = 1f - 2f * (yy + zz)
        mat[0][1] = 2f * (xy - zw)
        mat[0][2] = 2f * (xz + yw)
        
        mat[1][0] = 2f * (xy + zw)
        mat[1][1] = 1f - 2f * (xx + zz)
        mat[1][2] = 2f * (yz - xw)
        
        mat[2][0] = 2f * (xz - yw)
        mat[2][1] = 2f * (yz + xw)
        mat[2][2] = 1f - 2f * (xx + yy)
        
        return mat
    }
    
    fun toMatrix4(): FloatArray {
        // Convert to 4x4 matrix (for OpenGL)
        val mat = FloatArray(16)
        val m3 = toMatrix3()
        
        mat[0] = m3[0][0]; mat[1] = m3[1][0]; mat[2] = m3[2][0]; mat[3] = 0f
        mat[4] = m3[0][1]; mat[5] = m3[1][1]; mat[6] = m3[2][1]; mat[7] = 0f
        mat[8] = m3[0][2]; mat[9] = m3[1][2]; mat[10] = m3[2][2]; mat[11] = 0f
        mat[12] = 0f;      mat[13] = 0f;      mat[14] = 0f;       mat[15] = 1f
        
        return mat
    }
    
    // Normalization
    fun normalize(): Float {
        val mag = sqrt(mQ[VX] * mQ[VX] + mQ[VY] * mQ[VY] + 
                      mQ[VZ] * mQ[VZ] + mQ[VS] * mQ[VS])
        
        if (mag > FP_MAG_THRESHOLD) {
            val oomag = 1f / mag
            mQ[VX] *= oomag
            mQ[VY] *= oomag
            mQ[VZ] *= oomag
            mQ[VS] *= oomag
        } else {
            // Degenerate quaternion - set to identity
            loadIdentity()
        }
        
        return mag
    }
    
    // Conjugate (inverse for unit quaternions)
    fun conjugate(): LLQuaternion {
        mQ[VX] = -mQ[VX]
        mQ[VY] = -mQ[VY]
        mQ[VZ] = -mQ[VZ]
        return this
    }
    
    fun conjugated(): LLQuaternion {
        return LLQuaternion(this).conjugate()
    }
    
    // Transpose (same as conjugate)
    fun transpose() = conjugate()
    
    // Shortest arc from vector a to b
    fun shortestArc(a: LLVector3, b: LLVector3) {
        val normA = a.normalized()
        val normB = b.normalized()
        
        val cosAngle = normA dot normB
        
        if (cosAngle > 0.9999f) {
            // Vectors are nearly parallel
            loadIdentity()
            return
        }
        
        if (cosAngle < -0.9999f) {
            // Vectors are opposite - find perpendicular axis
            val axis = if (abs(normA.x) < 0.9f) {
                LLVector3(1f, 0f, 0f) cross normA
            } else {
                LLVector3(0f, 1f, 0f) cross normA
            }
            axis.normalize()
            setAngleAxis(PI.toFloat(), axis)
            return
        }
        
        // Normal case
        val axis = normA cross normB
        axis.normalize()
        
        val angle = acos(cosAngle)
        setAngleAxis(angle, axis)
    }
    
    // Constrain rotation to cone angle
    fun constrain(radians: Float): LLQuaternion {
        val (angle, axis) = getAngleAxis()
        
        if (angle > radians) {
            setAngleAxis(radians, axis)
        }
        
        return this
    }
    
    // Operators
    operator fun plus(other: LLQuaternion): LLQuaternion {
        return LLQuaternion(
            mQ[VX] + other.mQ[VX],
            mQ[VY] + other.mQ[VY],
            mQ[VZ] + other.mQ[VZ],
            mQ[VS] + other.mQ[VS]
        )
    }
    
    operator fun minus(other: LLQuaternion): LLQuaternion {
        return LLQuaternion(
            mQ[VX] - other.mQ[VX],
            mQ[VY] - other.mQ[VY],
            mQ[VZ] - other.mQ[VZ],
            mQ[VS] - other.mQ[VS]
        )
    }
    
    operator fun unaryMinus(): LLQuaternion {
        return LLQuaternion(-mQ[VX], -mQ[VY], -mQ[VZ], -mQ[VS])
    }
    
    operator fun times(scalar: Float): LLQuaternion {
        return LLQuaternion(
            mQ[VX] * scalar,
            mQ[VY] * scalar,
            mQ[VZ] * scalar,
            mQ[VS] * scalar
        )
    }
    
    // Quaternion multiplication
    operator fun times(other: LLQuaternion): LLQuaternion {
        return LLQuaternion(
            mQ[VS] * other.mQ[VX] + mQ[VX] * other.mQ[VS] + mQ[VY] * other.mQ[VZ] - mQ[VZ] * other.mQ[VY],
            mQ[VS] * other.mQ[VY] + mQ[VY] * other.mQ[VS] + mQ[VZ] * other.mQ[VX] - mQ[VX] * other.mQ[VZ],
            mQ[VS] * other.mQ[VZ] + mQ[VZ] * other.mQ[VS] + mQ[VX] * other.mQ[VY] - mQ[VY] * other.mQ[VX],
            mQ[VS] * other.mQ[VS] - mQ[VX] * other.mQ[VX] - mQ[VY] * other.mQ[VY] - mQ[VZ] * other.mQ[VZ]
        )
    }
    
    operator fun get(index: Int): Float = mQ[index]
    operator fun set(index: Int, value: Float) { mQ[index] = value }
    
    // Rotate vector by quaternion
    operator fun times(vec: LLVector3): LLVector3 {
        // v' = q * v * q^-1
        // Optimized version
        val qvec = LLVector3(mQ[VX], mQ[VY], mQ[VZ])
        val uv = qvec cross vec
        val uuv = qvec cross uv
        
        return vec + (uv * mQ[VS] + uuv) * 2f
    }
    
    // Dot product
    infix fun dot(other: LLQuaternion): Float {
        return mQ[VX] * other.mQ[VX] + mQ[VY] * other.mQ[VY] + 
               mQ[VZ] * other.mQ[VZ] + mQ[VS] * other.mQ[VS]
    }
    
    // Interpolation
    fun lerp(other: LLQuaternion, t: Float): LLQuaternion {
        // Linear interpolation (not normalized)
        return LLQuaternion(
            mQ[VX] + (other.mQ[VX] - mQ[VX]) * t,
            mQ[VY] + (other.mQ[VY] - mQ[VY]) * t,
            mQ[VZ] + (other.mQ[VZ] - mQ[VZ]) * t,
            mQ[VS] + (other.mQ[VS] - mQ[VS]) * t
        )
    }
    
    fun nlerp(other: LLQuaternion, t: Float): LLQuaternion {
        // Normalized linear interpolation
        val result = lerp(other, t)
        result.normalize()
        return result
    }
    
    fun slerp(other: LLQuaternion, t: Float): LLQuaternion {
        // Spherical linear interpolation
        var dot = this dot other
        var q2 = LLQuaternion(other)
        
        // If dot < 0, negate q2 to take shorter path
        if (dot < 0f) {
            dot = -dot
            q2 = -q2
        }
        
        // If quaternions are very close, use linear interpolation
        if (dot > 0.9995f) {
            return nlerp(q2, t)
        }
        
        // Calculate interpolation
        val theta0 = acos(dot.coerceIn(-1f, 1f))
        val theta = theta0 * t
        val sinTheta = sin(theta)
        val sinTheta0 = sin(theta0)
        
        val s0 = cos(theta) - dot * sinTheta / sinTheta0
        val s1 = sinTheta / sinTheta0
        
        return (this * s0) + (q2 * s1)
    }
    
    // Comparison
    fun equals(other: LLQuaternion, epsilon: Float): Boolean {
        return abs(mQ[VX] - other.mQ[VX]) < epsilon &&
               abs(mQ[VY] - other.mQ[VY]) < epsilon &&
               abs(mQ[VZ] - other.mQ[VZ]) < epsilon &&
               abs(mQ[VS] - other.mQ[VS]) < epsilon
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLQuaternion) return false
        return mQ[VX] == other.mQ[VX] &&
               mQ[VY] == other.mQ[VY] &&
               mQ[VZ] == other.mQ[VZ] &&
               mQ[VS] == other.mQ[VS]
    }
    
    override fun hashCode(): Int {
        var result = mQ[VX].hashCode()
        result = 31 * result + mQ[VY].hashCode()
        result = 31 * result + mQ[VZ].hashCode()
        result = 31 * result + mQ[VS].hashCode()
        return result
    }
    
    override fun toString(): String {
        return "LLQuaternion(x=${mQ[VX]}, y=${mQ[VY]}, z=${mQ[VZ]}, w=${mQ[VS]})"
    }
}

// Extension function for scalar * quaternion
operator fun Float.times(quat: LLQuaternion): LLQuaternion {
    return quat * this
}

// Extension function for vector * quaternion (rotation)
operator fun LLVector3.times(quat: LLQuaternion): LLVector3 {
    return quat * this
}
