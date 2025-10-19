/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd

/**
 * Represents an immutable three-dimensional vector with single-precision components.
 *
 * This class is used for 3D positions, directions, and other spatial calculations.
 */
data class Vector3(
    /** The x-component of the vector. */
    val x: Float,
    /** The y-component of the vector. */
    val y: Float,
    /** The z-component of the vector. */
    val z: Float
) {
    override fun toString(): String = String.format("<%f, %f, %f>", x, y, z)
    
    companion object {
        /** A vector with all components set to zero. */
        @JvmField
        val ZERO = Vector3(0.0f, 0.0f, 0.0f)
    }
}
