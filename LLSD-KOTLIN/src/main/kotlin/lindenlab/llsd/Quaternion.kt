/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd

/**
 * Represents an immutable quaternion used for 3D rotations.
 */
data class Quaternion(
    /** The x-component of the quaternion. */
    val x: Float,
    /** The y-component of the quaternion. */
    val y: Float,
    /** The z-component of the quaternion. */
    val z: Float,
    /** The w-component (scalar part) of the quaternion. */
    val w: Float
) {
    override fun toString(): String = String.format("<%f, %f, %f, %f>", x, y, z, w)
    
    companion object {
        /** The identity quaternion, representing no rotation. */
        @JvmField
        val IDENTITY = Quaternion(0.0f, 0.0f, 0.0f, 1.0f)
    }
}
