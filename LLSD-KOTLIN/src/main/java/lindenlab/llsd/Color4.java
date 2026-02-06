/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents an immutable RGBA color with single-precision components.
 * <p>
 * Components are typically in the range [0.0, 1.0].
 */
public final class Color4 {
    /** The color black (R=0, G=0, B=0, A=1). */
    public static final Color4 BLACK = new Color4(0.0f, 0.0f, 0.0f, 1.0f);
    /** The color white (R=1, G=1, B=1, A=1). */
    public static final Color4 WHITE = new Color4(1.0f, 1.0f, 1.0f, 1.0f);

    /** The red component of the color. */
    public final float r;
    /** The green component of the color. */
    public final float g;
    /** The blue component of the color. */
    public final float b;
    /** The alpha (transparency) component of the color. */
    public final float a;

    /**
     * Constructs a new Color4 with the specified components.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @param a The alpha component.
     */
    public Color4(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public String toString() {
        return String.format("<%f, %f, %f, %f>", r, g, b, a);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Color4 color4 = (Color4) obj;
        return Float.compare(color4.r, r) == 0 &&
               Float.compare(color4.g, g) == 0 &&
               Float.compare(color4.b, b) == 0 &&
               Float.compare(color4.a, a) == 0;
    }

    @Override
    public int hashCode() {
        int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
        result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
        result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
        result = 31 * result + (a != +0.0f ? Float.floatToIntBits(a) : 0);
        return result;
    }
}
