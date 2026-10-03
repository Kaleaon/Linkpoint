/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd

/**
 * Represents an immutable four-dimensional vector with single-precision components.
 */
data class Vector4(
    /** The x-component of the vector. */
    val x: Float,
    /** The y-component of the vector. */
    val y: Float,
    /** The z-component of the vector. */
    val z: Float,
    /** The w-component of the vector. */
    val w: Float
) {
    override fun toString(): String = String.format(java.util.Locale.US, "<%f, %f, %f, %f>", x, y, z, w)
    
    companion object {
        /** A vector with all components set to zero. */
        @JvmField
        val ZERO = Vector4(0.0f, 0.0f, 0.0f, 0.0f)
    }
}
