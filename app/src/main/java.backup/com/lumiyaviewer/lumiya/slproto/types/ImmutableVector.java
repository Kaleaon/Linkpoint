package com.lumiyaviewer.lumiya.slproto.types;

import android.annotation.SuppressLint;
import javax.annotation.Nullable;

public class ImmutableVector {
    public final float x;
    public final float y;
    public final float z;

    public ImmutableVector(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public ImmutableVector(LLVector3 lLVector3) {
        this.x = lLVector3.x;
        this.y = lLVector3.y;
        this.z = lLVector3.z;
    }

    public float distanceTo(float f, float f2, float f3) {
        float f4 = f - this.x;
        float f5 = f2 - this.y;
        float f6 = f3 - this.z;
        return (float) Math.sqrt((double) ((f4 * f4) + (f5 * f5) + (f6 * f6)));
    }

    public float distanceTo(@Nullable ImmutableVector immutableVector) {
        if (immutableVector == null) {
            return Float.NaN;
        }
        float f = this.x - immutableVector.x;
        float f2 = this.y - immutableVector.y;
        float f3 = this.z - immutableVector.z;
        return (float) Math.sqrt((double) ((f * f) + (f2 * f2) + (f3 * f3)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImmutableVector)) {
            return false;
        }
        ImmutableVector immutableVector = (ImmutableVector) obj;
        return this.x == immutableVector.x && this.y == immutableVector.y && this.z == immutableVector.z;
    }

    public float getDistanceTo(@Nullable LLVector3 lLVector3) {
        if (lLVector3 == null) {
            return Float.NaN;
        }
        float f = this.x - lLVector3.x;
        float f2 = this.y - lLVector3.y;
        float f3 = this.z - lLVector3.z;
        return (float) Math.sqrt((double) ((f * f) + (f2 * f2) + (f3 * f3)));
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public int hashCode() {
        return Float.floatToRawIntBits(this.x) + Float.floatToRawIntBits(this.y) + Float.floatToRawIntBits(this.z);
    }

    @SuppressLint({"DefaultLocale"})
    public String toString() {
        return String.format("(%f,%f,%f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)});
    }
}
