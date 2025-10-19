/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd

/**
 * Represents an immutable RGBA color with single-precision components.
 *
 * Components are typically in the range [0.0, 1.0].
 */
data class Color4(
    /** The red component of the color. */
    val r: Float,
    /** The green component of the color. */
    val g: Float,
    /** The blue component of the color. */
    val b: Float,
    /** The alpha (transparency) component of the color. */
    val a: Float
) {
    override fun toString(): String = String.format("<%f, %f, %f, %f>", r, g, b, a)
    
    companion object {
        /** The color black (R=0, G=0, B=0, A=1). */
        @JvmField
        val BLACK = Color4(0.0f, 0.0f, 0.0f, 1.0f)
        
        /** The color white (R=1, G=1, B=1, A=1). */
        @JvmField
        val WHITE = Color4(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
