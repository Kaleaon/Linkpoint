package com.linkpoint.protocol.types

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

/**
 * Second Life Vector3d type (double precision)
 * Used for global positions that span multiple regions
 */
data class LLVector3d(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
) {
    companion object {
        fun zero() = LLVector3d(0.0, 0.0, 0.0)
        
        fun fromBytes(bytes: ByteArray, offset: Int = 0): LLVector3d {
            val buffer = ByteBuffer.wrap(bytes, offset, 24).order(ByteOrder.LITTLE_ENDIAN)
            return LLVector3d(buffer.double, buffer.double, buffer.double)
        }
        
        /**
         * Create from region handle and local position
         */
        fun fromRegionPosition(regionHandle: Long, localPos: LLVector3): LLVector3d {
            val regionX = ((regionHandle shr 32) and 0xFFFFFFFFL).toDouble()
            val regionY = (regionHandle and 0xFFFFFFFFL).toDouble()
            return LLVector3d(
                regionX + localPos.x,
                regionY + localPos.y,
                localPos.z.toDouble()
            )
        }
    }
    
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putDouble(x)
        buffer.putDouble(y)
        buffer.putDouble(z)
        return buffer.array()
    }
    
    fun length(): Double = sqrt(x * x + y * y + z * z)
    fun lengthSquared(): Double = x * x + y * y + z * z
    
    fun normalize(): LLVector3d {
        val len = length()
        return if (len > 0.0) LLVector3d(x / len, y / len, z / len) else zero()
    }
    
    operator fun plus(other: LLVector3d) = LLVector3d(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: LLVector3d) = LLVector3d(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Double) = LLVector3d(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Double) = LLVector3d(x / scalar, y / scalar, z / scalar)
    
    fun dot(other: LLVector3d): Double = x * other.x + y * other.y + z * other.z
    
    fun cross(other: LLVector3d): LLVector3d = LLVector3d(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    
    fun toFloat(): LLVector3 = LLVector3(x.toFloat(), y.toFloat(), z.toFloat())
    
    /**
     * Get local position within a region given a region handle
     */
    fun toLocalPosition(regionHandle: Long): LLVector3 {
        val regionX = ((regionHandle shr 32) and 0xFFFFFFFFL).toDouble()
        val regionY = (regionHandle and 0xFFFFFFFFL).toDouble()
        return LLVector3(
            (x - regionX).toFloat(),
            (y - regionY).toFloat(),
            z.toFloat()
        )
    }
    
    override fun toString(): String = "LLVector3d($x, $y, $z)"
}
