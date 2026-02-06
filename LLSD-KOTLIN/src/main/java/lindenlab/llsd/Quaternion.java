/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents an immutable quaternion used for 3D rotations.
 */
public final class Quaternion {
    /** The identity quaternion, representing no rotation. */
    public static final Quaternion IDENTITY = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

    /** The x-component of the quaternion. */
    public final float x;
    /** The y-component of the quaternion. */
    public final float y;
    /** The z-component of the quaternion. */
    public final float z;
    /** The w-component (scalar part) of the quaternion. */
    public final float w;

    /**
     * Constructs a new Quaternion with the specified components.
     * @param x The x-component.
     * @param y The y-component.
     * @param z The z-component.
     * @param w The w-component.
     */
    public Quaternion(float x, float y, float z, float w) {
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
        Quaternion quaternion = (Quaternion) obj;
        return Float.compare(quaternion.x, x) == 0 &&
               Float.compare(quaternion.y, y) == 0 &&
               Float.compare(quaternion.z, z) == 0 &&
               Float.compare(quaternion.w, w) == 0;
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
