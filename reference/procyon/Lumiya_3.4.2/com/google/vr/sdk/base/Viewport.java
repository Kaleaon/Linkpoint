// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.opengl.GLES20;
import com.google.vr.cardboard.UsedByNative;

@UsedByNative
public class Viewport
{
    public int height;
    public int width;
    public int x;
    public int y;
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof Viewport) {
            final Viewport viewport = (Viewport)o;
            if (this.x != viewport.x || this.y != viewport.y || this.width != viewport.width || this.height != viewport.height) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public void getAsArray(final int[] array, final int n) {
        if (n + 4 <= array.length) {
            array[n] = this.x;
            array[n + 1] = this.y;
            array[n + 2] = this.width;
            array[n + 3] = this.height;
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(this.x).hashCode() ^ Integer.valueOf(this.y).hashCode() ^ Integer.valueOf(this.width).hashCode() ^ Integer.valueOf(this.height).hashCode();
    }
    
    public void setGLScissor() {
        GLES20.glScissor(this.x, this.y, this.width, this.height);
    }
    
    public void setGLViewport() {
        GLES20.glViewport(this.x, this.y, this.width, this.height);
    }
    
    @UsedByNative
    public void setViewport(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "{\n" + new StringBuilder(18).append("  x: ").append(this.x).append(",\n").toString() + new StringBuilder(18).append("  y: ").append(this.y).append(",\n").toString() + new StringBuilder(22).append("  width: ").append(this.width).append(",\n").toString() + new StringBuilder(23).append("  height: ").append(this.height).append(",\n").toString() + "}";
    }
}
