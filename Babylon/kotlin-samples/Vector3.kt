package com.babylonkt.math

import kotlin.math.sqrt

/**
 * Represents a 3D vector with x, y, z components.
 * Immutable data class with operator overloading for mathematical operations.
 */
data class Vector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    
    // ==================== Operators ====================
    
    operator fun plus(other: Vector3) = Vector3(
        x + other.x,
        y + other.y,
        z + other.z
    )
    
    operator fun minus(other: Vector3) = Vector3(
        x - other.x,
        y - other.y,
        z - other.z
    )
    
    operator fun times(scalar: Float) = Vector3(
        x * scalar,
        y * scalar,
        z * scalar
    )
    
    operator fun div(scalar: Float) = Vector3(
        x / scalar,
        y / scalar,
        z / scalar
    )
    
    operator fun unaryMinus() = Vector3(-x, -y, -z)
    
    // ==================== Vector Operations ====================
    
    /**
     * Calculates the dot product with another vector.
     */
    fun dot(other: Vector3): Float = x * other.x + y * other.y + z * other.z
    
    /**
     * Calculates the cross product with another vector.
     */
    fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    
    /**
     * Returns the length (magnitude) of this vector.
     */
    fun length(): Float = sqrt(x * x + y * y + z * z)
    
    /**
     * Returns the squared length of this vector (faster than length()).
     */
    fun lengthSquared(): Float = x * x + y * y + z * z
    
    /**
     * Returns a normalized version of this vector (length = 1).
     */
    fun normalize(): Vector3 {
        val len = length()
        return if (len > 0f) this / len else Zero()
    }
    
    /**
     * Returns the distance to another vector.
     */
    fun distanceTo(other: Vector3): Float = (this - other).length()
    
    /**
     * Returns the squared distance to another vector (faster than distanceTo()).
     */
    fun distanceSquaredTo(other: Vector3): Float = (this - other).lengthSquared()
    
    /**
     * Linear interpolation between this vector and another.
     * @param other The target vector
     * @param t Interpolation factor (0.0 to 1.0)
     */
    fun lerp(other: Vector3, t: Float): Vector3 {
        val clampedT = t.coerceIn(0f, 1f)
        return Vector3(
            x + (other.x - x) * clampedT,
            y + (other.y - y) * clampedT,
            z + (other.z - z) * clampedT
        )
    }
    
    /**
     * Clamps this vector between min and max values.
     */
    fun clamp(min: Vector3, max: Vector3) = Vector3(
        x.coerceIn(min.x, max.x),
        y.coerceIn(min.y, max.y),
        z.coerceIn(min.z, max.z)
    )
    
    /**
     * Returns a vector with absolute values of components.
     */
    fun abs() = Vector3(
        kotlin.math.abs(x),
        kotlin.math.abs(y),
        kotlin.math.abs(z)
    )
    
    /**
     * Projects this vector onto another vector.
     */
    fun projectOnto(other: Vector3): Vector3 {
        val otherLengthSq = other.lengthSquared()
        return if (otherLengthSq > 0f) {
            other * (this.dot(other) / otherLengthSq)
        } else {
            Zero()
        }
    }
    
    /**
     * Reflects this vector off a surface with the given normal.
     */
    fun reflect(normal: Vector3): Vector3 {
        val normalizedNormal = normal.normalize()
        return this - normalizedNormal * (2f * this.dot(normalizedNormal))
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Converts to a float array [x, y, z].
     */
    fun toFloatArray() = floatArrayOf(x, y, z)
    
    /**
     * Returns a string representation of this vector.
     */
    override fun toString() = "Vector3(x=$x, y=$y, z=$z)"
    
    /**
     * Checks if this vector is approximately equal to another (within epsilon).
     */
    fun equalsWithEpsilon(other: Vector3, epsilon: Float = 0.0001f): Boolean {
        return kotlin.math.abs(x - other.x) < epsilon &&
               kotlin.math.abs(y - other.y) < epsilon &&
               kotlin.math.abs(z - other.z) < epsilon
    }
    
    // ==================== Companion Object (Static Methods) ====================
    
    companion object {
        /**
         * Returns a zero vector (0, 0, 0).
         */
        fun Zero() = Vector3(0f, 0f, 0f)
        
        /**
         * Returns a unit vector (1, 1, 1).
         */
        fun One() = Vector3(1f, 1f, 1f)
        
        /**
         * Returns the up vector (0, 1, 0).
         */
        fun Up() = Vector3(0f, 1f, 0f)
        
        /**
         * Returns the down vector (0, -1, 0).
         */
        fun Down() = Vector3(0f, -1f, 0f)
        
        /**
         * Returns the left vector (-1, 0, 0).
         */
        fun Left() = Vector3(-1f, 0f, 0f)
        
        /**
         * Returns the right vector (1, 0, 0).
         */
        fun Right() = Vector3(1f, 0f, 0f)
        
        /**
         * Returns the forward vector (0, 0, 1).
         */
        fun Forward() = Vector3(0f, 0f, 1f)
        
        /**
         * Returns the backward vector (0, 0, -1).
         */
        fun Backward() = Vector3(0f, 0f, -1f)
        
        /**
         * Creates a vector from spherical coordinates.
         * @param radius Distance from origin
         * @param theta Angle from positive x-axis (radians)
         * @param phi Angle from positive z-axis (radians)
         */
        fun fromSpherical(radius: Float, theta: Float, phi: Float): Vector3 {
            val sinPhi = kotlin.math.sin(phi)
            return Vector3(
                x = radius * sinPhi * kotlin.math.cos(theta),
                y = radius * kotlin.math.cos(phi),
                z = radius * sinPhi * kotlin.math.sin(theta)
            )
        }
        
        /**
         * Linear interpolation between two vectors.
         */
        fun lerp(start: Vector3, end: Vector3, t: Float) = start.lerp(end, t)
        
        /**
         * Returns the minimum components of two vectors.
         */
        fun min(a: Vector3, b: Vector3) = Vector3(
            minOf(a.x, b.x),
            minOf(a.y, b.y),
            minOf(a.z, b.z)
        )
        
        /**
         * Returns the maximum components of two vectors.
         */
        fun max(a: Vector3, b: Vector3) = Vector3(
            maxOf(a.x, b.x),
            maxOf(a.y, b.y),
            maxOf(a.z, b.z)
        )
        
        /**
         * Calculates the angle between two vectors in radians.
         */
        fun angleBetween(a: Vector3, b: Vector3): Float {
            val dot = a.normalize().dot(b.normalize())
            return kotlin.math.acos(dot.coerceIn(-1f, 1f))
        }
    }
}

// ==================== Extension Functions ====================

/**
 * Extension function to multiply scalar * vector.
 */
operator fun Float.times(vector: Vector3) = vector * this

/**
 * Extension function to create a Vector3 from a list of floats.
 */
fun List<Float>.toVector3(): Vector3 {
    require(size >= 3) { "List must have at least 3 elements" }
    return Vector3(this[0], this[1], this[2])
}