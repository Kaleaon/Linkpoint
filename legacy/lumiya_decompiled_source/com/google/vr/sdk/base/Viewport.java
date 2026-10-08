package com.google.vr.sdk.base;

import android.opengl.GLES20;
import com.google.vr.cardboard.UsedByNative;
@UsedByNative
/* loaded from: classes.dex */
public class Viewport {
    public int height;
    public int width;
    public int x;
    public int y;

    public boolean equals(Object obj) {
        if (obj != this) {
            if (obj instanceof Viewport) {
                Viewport viewport = (Viewport) obj;
                return this.x == viewport.x && this.y == viewport.y && this.width == viewport.width && this.height == viewport.height;
            }
            return false;
        }
        return true;
    }

    public void getAsArray(int[] iArr, int i) {
        if (i + 4 > iArr.length) {
            throw new IllegalArgumentException("Not enough space to write the result");
        }
        iArr[i] = this.x;
        iArr[i + 1] = this.y;
        iArr[i + 2] = this.width;
        iArr[i + 3] = this.height;
    }

    public int hashCode() {
        return ((Integer.valueOf(this.x).hashCode() ^ Integer.valueOf(this.y).hashCode()) ^ Integer.valueOf(this.width).hashCode()) ^ Integer.valueOf(this.height).hashCode();
    }

    public void setGLScissor() {
        GLES20.glScissor(this.x, this.y, this.width, this.height);
    }

    public void setGLViewport() {
        GLES20.glViewport(this.x, this.y, this.width, this.height);
    }

    @UsedByNative
    public void setViewport(int i, int i2, int i3, int i4) {
        this.x = i;
        this.y = i2;
        this.width = i3;
        this.height = i4;
    }

    public String toString() {
        return "{\n" + new StringBuilder(18).append("  x: ").append(this.x).append(",\n").toString() + new StringBuilder(18).append("  y: ").append(this.y).append(",\n").toString() + new StringBuilder(22).append("  width: ").append(this.width).append(",\n").toString() + new StringBuilder(23).append("  height: ").append(this.height).append(",\n").toString() + "}";
    }
}
