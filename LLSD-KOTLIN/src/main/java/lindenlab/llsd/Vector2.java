/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents an immutable two-dimensional vector with single-precision components.
 * <p>
 * This class is a simple value object for holding 2D vector data, often used
 * for texture coordinates or 2D positions.
 */
public final class Vector2 {
    /** A vector with all components set to zero. */
    public static final Vector2 ZERO = new Vector2(0.0f, 0.0f);

    /** The x-component of the vector. */
    public final float x;
    /** The y-component of the vector. */
    public final float y;

    /**
     * Constructs a new Vector2 with the specified components.
     * @param x The x-component.
     * @param y The y-component.
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("<%f, %f>", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2 vector2 = (Vector2) obj;
        return Float.compare(vector2.x, x) == 0 && Float.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
