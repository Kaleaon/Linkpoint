/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd

/**
 * Represents an immutable two-dimensional vector with single-precision components.
 *
 * This class is a simple value object for holding 2D vector data, often used
 * for texture coordinates or 2D positions.
 */
data class Vector2(
    /** The x-component of the vector. */
    val x: Float,
    /** The y-component of the vector. */
    val y: Float
) {
    override fun toString(): String = String.format("<%f, %f>", x, y)
    
    companion object {
        /** A vector with all components set to zero. */
        @JvmField
        val ZERO = Vector2(0.0f, 0.0f)
    }
}
