/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents an immutable three-dimensional vector with single-precision components.
 * <p>
 * This class is used for 3D positions, directions, and other spatial calculations.
 */
public final class Vector3 {
    /** A vector with all components set to zero. */
    public static final Vector3 ZERO = new Vector3(0.0f, 0.0f, 0.0f);

    /** The x-component of the vector. */
    public final float x;
    /** The y-component of the vector. */
    public final float y;
    /** The z-component of the vector. */
    public final float z;

    /**
     * Constructs a new Vector3 with the specified components.
     * @param x The x-component.
     * @param y The y-component.
     * @param z The z-component.
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3 vector3 = (Vector3) obj;
        return Float.compare(vector3.x, x) == 0 &&
               Float.compare(vector3.y, y) == 0 &&
               Float.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }
}
