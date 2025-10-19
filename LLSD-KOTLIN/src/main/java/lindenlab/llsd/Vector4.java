/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents an immutable four-dimensional vector with single-precision components.
 */
public final class Vector4 {
    /** A vector with all components set to zero. */
    public static final Vector4 ZERO = new Vector4(0.0f, 0.0f, 0.0f, 0.0f);

    /** The x-component of the vector. */
    public final float x;
    /** The y-component of the vector. */
    public final float y;
    /** The z-component of the vector. */
    public final float z;
    /** The w-component of the vector. */
    public final float w;

    /**
     * Constructs a new Vector4 with the specified components.
     * @param x The x-component.
     * @param y The y-component.
     * @param z The z-component.
     * @param w The w-component.
     */
    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        return String.format("<%f, %f, %f, %f>", x, y, z, w);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector4 vector4 = (Vector4) obj;
        return Float.compare(vector4.x, x) == 0 &&
               Float.compare(vector4.y, y) == 0 &&
               Float.compare(vector4.z, z) == 0 &&
               Float.compare(vector4.w, w) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + (w != +0.0f ? Float.floatToIntBits(w) : 0);
        return result;
    }
}
