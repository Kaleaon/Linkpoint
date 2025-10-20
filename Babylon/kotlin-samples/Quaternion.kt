package com.babylonkt.math

import kotlin.math.*

/**
 * Represents a quaternion for 3D rotations.
 * Quaternions provide smooth interpolation and avoid gimbal lock.
 */
data class Quaternion(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 1f
) {
    
    // ==================== Operators ====================
    
    operator fun plus(other: Quaternion) = Quaternion(
        x + other.x,
        y + other.y,
        z + other.z,
        w + other.w
    )
    
    operator fun minus(other: Quaternion) = Quaternion(
        x - other.x,
        y - other.y,
        z - other.z,
        w - other.w
    )
    
    operator fun times(scalar: Float) = Quaternion(
        x * scalar,
        y * scalar,
        z * scalar,
        w * scalar
    )
    
    /**
     * Quaternion multiplication (Hamilton product).
     */
    operator fun times(other: Quaternion) = Quaternion(
        w * other.x + x * other.w + y * other.z - z * other.y,
        w * other.y - x * other.z + y * other.w + z * other.x,
        w * other.z + x * other.y - y * other.x + z * other.w,
        w * other.w - x * other.x - y * other.y - z * other.z
    )
    
    // ==================== Quaternion Operations ====================
    
    /**
     * Returns the length (magnitude) of this quaternion.
     */
    fun length(): Float = sqrt(x * x + y * y + z * z + w * w)
    
    /**
     * Returns the squared length of this quaternion.
     */
    fun lengthSquared(): Float = x * x + y * y + z * z + w * w
    
    /**
     * Returns a normalized version of this quaternion.
     */
    fun normalize(): Quaternion {
        val len = length()
        return if (len > 0f) this * (1f / len) else Identity()
    }
    
    /**
     * Returns the conjugate of this quaternion.
     */
    fun conjugate() = Quaternion(-x, -y, -z, w)
    
    /**
     * Returns the inverse of this quaternion.
     */
    fun inverse(): Quaternion {
        val lengthSq = lengthSquared()
        return if (lengthSq > 0f) {
            conjugate() * (1f / lengthSq)
        } else {
            Identity()
        }
    }
    
    /**
     * Calculates the dot product with another quaternion.
     */
    fun dot(other: Quaternion): Float = x * other.x + y * other.y + z * other.z + w * other.w
    
    /**
     * Rotates a vector by this quaternion.
     */
    fun rotateVector(v: Vector3): Vector3 {
        // v' = q * v * q^-1
        val qv = Quaternion(v.x, v.y, v.z, 0f)
        val result = this * qv * this.conjugate()
        return Vector3(result.x, result.y, result.z)
    }
    
    /**
     * Converts this quaternion to Euler angles (in radians).
     * Returns Vector3(pitch, yaw, roll) or (x, y, z rotation).
     */
    fun toEulerAngles(): Vector3 {
        // Roll (x-axis rotation)
        val sinr_cosp = 2f * (w * x + y * z)
        val cosr_cosp = 1f - 2f * (x * x + y * y)
        val roll = atan2(sinr_cosp, cosr_cosp)
        
        // Pitch (y-axis rotation)
        val sinp = 2f * (w * y - z * x)
        val pitch = if (abs(sinp) >= 1f) {
            (PI.toFloat() / 2f) * sign(sinp) // Use 90 degrees if out of range
        } else {
            asin(sinp)
        }
        
        // Yaw (z-axis rotation)
        val siny_cosp = 2f * (w * z + x * y)
        val cosy_cosp = 1f - 2f * (y * y + z * z)
        val yaw = atan2(siny_cosp, cosy_cosp)
        
        return Vector3(pitch, yaw, roll)
    }
    
    /**
     * Converts this quaternion to axis-angle representation.
     * Returns Pair(axis, angle in radians).
     */
    fun toAxisAngle(): Pair<Vector3, Float> {
        val normalized = normalize()
        val angle = 2f * acos(normalized.w)
        val s = sqrt(1f - normalized.w * normalized.w)
        
        val axis = if (s < 0.001f) {
            // If s is close to zero, direction doesn't matter
            Vector3(1f, 0f, 0f)
        } else {
            Vector3(normalized.x / s, normalized.y / s, normalized.z / s)
        }
        
        return Pair(axis, angle)
    }
    
    /**
     * Spherical linear interpolation between this quaternion and another.
     * @param other The target quaternion
     * @param t Interpolation factor (0.0 to 1.0)
     */
    fun slerp(other: Quaternion, t: Float): Quaternion {
        val clampedT = t.coerceIn(0f, 1f)
        
        var dot = this.dot(other)
        
        // If the dot product is negative, slerp won't take the shorter path.
        // Fix by reversing one quaternion.
        val q2 = if (dot < 0f) {
            dot = -dot
            other * -1f
        } else {
            other
        }
        
        return if (dot > 0.9995f) {
            // If quaternions are very close, use linear interpolation
            (this + (q2 - this) * clampedT).normalize()
        } else {
            // Calculate angle between quaternions
            val theta = acos(dot)
            val sinTheta = sin(theta)
            
            val w1 = sin((1f - clampedT) * theta) / sinTheta
            val w2 = sin(clampedT * theta) / sinTheta
            
            this * w1 + q2 * w2
        }
    }
    
    /**
     * Normalized linear interpolation (faster but less accurate than slerp).
     */
    fun nlerp(other: Quaternion, t: Float): Quaternion {
        val clampedT = t.coerceIn(0f, 1f)
        return (this * (1f - clampedT) + other * clampedT).normalize()
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Converts to a float array [x, y, z, w].
     */
    fun toFloatArray() = floatArrayOf(x, y, z, w)
    
    override fun toString() = "Quaternion(x=$x, y=$y, z=$z, w=$w)"
    
    /**
     * Checks if this quaternion is approximately equal to another.
     */
    fun equalsWithEpsilon(other: Quaternion, epsilon: Float = 0.0001f): Boolean {
        return abs(x - other.x) < epsilon &&
               abs(y - other.y) < epsilon &&
               abs(z - other.z) < epsilon &&
               abs(w - other.w) < epsilon
    }
    
    // ==================== Companion Object (Static Methods) ====================
    
    companion object {
        /**
         * Returns an identity quaternion (no rotation).
         */
        fun Identity() = Quaternion(0f, 0f, 0f, 1f)
        
        /**
         * Creates a quaternion from Euler angles (in radians).
         * @param pitch Rotation around X axis
         * @param yaw Rotation around Y axis
         * @param roll Rotation around Z axis
         */
        fun fromEulerAngles(pitch: Float, yaw: Float, roll: Float): Quaternion {
            val cy = cos(yaw * 0.5f)
            val sy = sin(yaw * 0.5f)
            val cp = cos(pitch * 0.5f)
            val sp = sin(pitch * 0.5f)
            val cr = cos(roll * 0.5f)
            val sr = sin(roll * 0.5f)
            
            return Quaternion(
                x = sr * cp * cy - cr * sp * sy,
                y = cr * sp * cy + sr * cp * sy,
                z = cr * cp * sy - sr * sp * cy,
                w = cr * cp * cy + sr * sp * sy
            )
        }
        
        fun fromEulerAngles(angles: Vector3) = 
            fromEulerAngles(angles.x, angles.y, angles.z)
        
        /**
         * Creates a quaternion from axis-angle representation.
         * @param axis The rotation axis (should be normalized)
         * @param angleRadians The rotation angle in radians
         */
        fun fromAxisAngle(axis: Vector3, angleRadians: Float): Quaternion {
            val halfAngle = angleRadians * 0.5f
            val s = sin(halfAngle)
            val normalizedAxis = axis.normalize()
            
            return Quaternion(
                x = normalizedAxis.x * s,
                y = normalizedAxis.y * s,
                z = normalizedAxis.z * s,
                w = cos(halfAngle)
            )
        }
        
        /**
         * Creates a quaternion from a rotation matrix.
         */
        fun fromRotationMatrix(matrix: Matrix4x4): Quaternion {
            val m00 = matrix[0, 0]
            val m11 = matrix[1, 1]
            val m22 = matrix[2, 2]
            
            val trace = m00 + m11 + m22
            
            return if (trace > 0f) {
                val s = sqrt(trace + 1f) * 2f // s = 4 * w
                Quaternion(
                    x = (matrix[2, 1] - matrix[1, 2]) / s,
                    y = (matrix[0, 2] - matrix[2, 0]) / s,
                    z = (matrix[1, 0] - matrix[0, 1]) / s,
                    w = 0.25f * s
                )
            } else if (m00 > m11 && m00 > m22) {
                val s = sqrt(1f + m00 - m11 - m22) * 2f // s = 4 * x
                Quaternion(
                    x = 0.25f * s,
                    y = (matrix[0, 1] + matrix[1, 0]) / s,
                    z = (matrix[0, 2] + matrix[2, 0]) / s,
                    w = (matrix[2, 1] - matrix[1, 2]) / s
                )
            } else if (m11 > m22) {
                val s = sqrt(1f + m11 - m00 - m22) * 2f // s = 4 * y
                Quaternion(
                    x = (matrix[0, 1] + matrix[1, 0]) / s,
                    y = 0.25f * s,
                    z = (matrix[1, 2] + matrix[2, 1]) / s,
                    w = (matrix[0, 2] - matrix[2, 0]) / s
                )
            } else {
                val s = sqrt(1f + m22 - m00 - m11) * 2f // s = 4 * z
                Quaternion(
                    x = (matrix[0, 2] + matrix[2, 0]) / s,
                    y = (matrix[1, 2] + matrix[2, 1]) / s,
                    z = 0.25f * s,
                    w = (matrix[1, 0] - matrix[0, 1]) / s
                )
            }
        }
        
        /**
         * Creates a quaternion that rotates from one direction to another.
         */
        fun fromToRotation(from: Vector3, to: Vector3): Quaternion {
            val fromNorm = from.normalize()
            val toNorm = to.normalize()
            
            val dot = fromNorm.dot(toNorm)
            
            return when {
                dot >= 0.999999f -> {
                    // Vectors are parallel
                    Identity()
                }
                dot <= -0.999999f -> {
                    // Vectors are opposite
                    // Find an orthogonal axis
                    var axis = Vector3.Right().cross(fromNorm)
                    if (axis.lengthSquared() < 0.000001f) {
                        axis = Vector3.Up().cross(fromNorm)
                    }
                    fromAxisAngle(axis.normalize(), PI.toFloat())
                }
                else -> {
                    val axis = fromNorm.cross(toNorm)
                    val angle = acos(dot)
                    fromAxisAngle(axis.normalize(), angle)
                }
            }
        }
        
        /**
         * Creates a quaternion that looks in a specific direction.
         * @param forward The forward direction
         * @param up The up direction (default is world up)
         */
        fun lookRotation(forward: Vector3, up: Vector3 = Vector3.Up()): Quaternion {
            val forwardNorm = forward.normalize()
            val rightNorm = up.cross(forwardNorm).normalize()
            val upNorm = forwardNorm.cross(rightNorm)
            
            // Create rotation matrix
            val matrix = Matrix4x4()
            matrix[0, 0] = rightNorm.x
            matrix[0, 1] = rightNorm.y
            matrix[0, 2] = rightNorm.z
            matrix[1, 0] = upNorm.x
            matrix[1, 1] = upNorm.y
            matrix[1, 2] = upNorm.z
            matrix[2, 0] = forwardNorm.x
            matrix[2, 1] = forwardNorm.y
            matrix[2, 2] = forwardNorm.z
            
            return fromRotationMatrix(matrix)
        }
        
        /**
         * Spherical linear interpolation between two quaternions.
         */
        fun slerp(start: Quaternion, end: Quaternion, t: Float) = start.slerp(end, t)
        
        /**
         * Calculates the angle between two quaternions in radians.
         */
        fun angleBetween(a: Quaternion, b: Quaternion): Float {
            val dot = a.normalize().dot(b.normalize())
            return acos(dot.coerceIn(-1f, 1f)) * 2f
        }
    }
}

// ==================== Extension Functions ====================

/**
 * Extension function to multiply scalar * quaternion.
 */
operator fun Float.times(quaternion: Quaternion) = quaternion * this