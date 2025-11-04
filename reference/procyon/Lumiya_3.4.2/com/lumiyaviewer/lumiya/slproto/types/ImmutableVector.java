// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import android.annotation.SuppressLint;
import javax.annotation.Nullable;

public class ImmutableVector
{
    public final float x;
    public final float y;
    public final float z;
    
    public ImmutableVector(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public ImmutableVector(final LLVector3 llVector3) {
        this.x = llVector3.x;
        this.y = llVector3.y;
        this.z = llVector3.z;
    }
    
    public float distanceTo(float n, float n2, float n3) {
        n -= this.x;
        n2 -= this.y;
        n3 -= this.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public float distanceTo(@Nullable final ImmutableVector immutableVector) {
        if (immutableVector == null) {
            return Float.NaN;
        }
        final float n = this.x - immutableVector.x;
        final float n2 = this.y - immutableVector.y;
        final float n3 = this.z - immutableVector.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (o instanceof ImmutableVector) {
            final ImmutableVector immutableVector = (ImmutableVector)o;
            if (this.x != immutableVector.x || this.y != immutableVector.y || this.z != immutableVector.z) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public float getDistanceTo(@Nullable final LLVector3 llVector3) {
        if (llVector3 == null) {
            return Float.NaN;
        }
        final float n = this.x - llVector3.x;
        final float n2 = this.y - llVector3.y;
        final float n3 = this.z - llVector3.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
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
    
    @Override
    public int hashCode() {
        return Float.floatToRawIntBits(this.x) + Float.floatToRawIntBits(this.y) + Float.floatToRawIntBits(this.z);
    }
    
    @SuppressLint({ "DefaultLocale" })
    @Override
    public String toString() {
        return String.format("(%f,%f,%f)", this.x, this.y, this.z);
    }
}
