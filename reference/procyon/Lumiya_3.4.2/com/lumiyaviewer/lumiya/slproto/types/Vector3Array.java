// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import android.opengl.Matrix;

public class Vector3Array extends VectorArray
{
    public Vector3Array(final int n) {
        super(3, n);
    }
    
    public Vector3Array(final VectorArray vectorArray, final int n) {
        super(vectorArray, n);
    }
    
    public final void MatrixScale(final float[] array, final int n, int n2) {
        n2 = this.offset + this.numComponents * n2;
        Matrix.scaleM(array, n, this.data[n2 + 0], this.data[n2 + 1], this.data[n2 + 2]);
    }
    
    public final void MatrixTranslate(final float[] array, final int n, final float[] array2, final int n2, int n3) {
        n3 = this.offset + this.numComponents * n3;
        Matrix.translateM(array, n, array2, n2, this.data[n3 + 0], this.data[n3 + 1], this.data[n3 + 2]);
    }
    
    public final void add(int n, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        final float[] data = this.data;
        final int n2 = n + 0;
        data[n2] += llVector3.x;
        final float[] data2 = this.data;
        final int n3 = n + 1;
        data2[n3] += llVector3.y;
        final float[] data3 = this.data;
        n += 2;
        data3[n] += llVector3.z;
    }
    
    public final void addToVector(int n, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        llVector3.x += this.data[n + 0];
        llVector3.y += this.data[n + 1];
        llVector3.z += this.data[n + 2];
    }
    
    public final void clear() {
        int offset = this.offset;
        for (int i = 0; i < this.length; ++i) {
            this.data[offset + 0] = 0.0f;
            this.data[offset + 1] = 0.0f;
            this.data[offset + 2] = 0.0f;
            offset += this.numComponents;
        }
    }
    
    public final float distToPlane(int n, final LLVector3 llVector3, final LLVector3 llVector4) {
        n = this.offset + this.numComponents * n;
        return (this.data[n + 2] - llVector3.z) * llVector4.z + ((this.data[n + 0] - llVector3.x) * llVector4.x + (this.data[n + 1] - llVector3.y) * llVector4.y);
    }
    
    public final void fill(int n, final int n2, final LLVector3 llVector3) {
        n = this.numComponents * n + this.offset;
        for (int i = 0; i < n2; ++i) {
            this.data[n + 0] = llVector3.x;
            this.data[n + 1] = llVector3.y;
            this.data[n + 2] = llVector3.z;
            n += this.numComponents;
        }
    }
    
    public final LLVector3 get(int n) {
        n = this.offset + this.numComponents * n;
        return new LLVector3(this.data[n + 0], this.data[n + 1], this.data[n + 2]);
    }
    
    public final void get(int n, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        llVector3.x = this.data[n + 0];
        llVector3.y = this.data[n + 1];
        llVector3.z = this.data[n + 2];
    }
    
    public final float getDistanceTo(int n, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        final float n2 = this.data[n + 0] - llVector3.x;
        final float n3 = this.data[n + 1] - llVector3.y;
        final float n4 = this.data[n + 2] - llVector3.z;
        return (float)Math.sqrt(n4 * n4 + (n2 * n2 + n3 * n3));
    }
    
    public final float getMaxComponent(int n) {
        n = this.numComponents * n + this.offset;
        float n2;
        if (this.data[n + 1] > (n2 = this.data[n + 0])) {
            n2 = this.data[n + 1];
        }
        float n3 = n2;
        if (this.data[n + 2] > n2) {
            n3 = this.data[n + 2];
        }
        return n3;
    }
    
    public final void getSub(int n, int n2, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        n2 = this.offset + this.numComponents * n2;
        llVector3.x = this.data[n + 0] - this.data[n2 + 0];
        llVector3.y = this.data[n + 1] - this.data[n2 + 1];
        llVector3.z = this.data[n + 2] - this.data[n2 + 2];
    }
    
    public final void getSub(int n, final Vector3Array vector3Array, int n2, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        n2 = vector3Array.offset + vector3Array.numComponents * n2;
        llVector3.x = this.data[n + 0] - vector3Array.data[n2 + 0];
        llVector3.y = this.data[n + 1] - vector3Array.data[n2 + 1];
        llVector3.z = this.data[n + 2] - vector3Array.data[n2 + 2];
    }
    
    public final void minMaxVector(int n, final LLVector3 llVector3, final LLVector3 llVector4) {
        n = this.offset + this.numComponents * n;
        final float n2 = this.data[n + 0];
        final float n3 = this.data[n + 1];
        final float n4 = this.data[n + 2];
        if (llVector3.x > n2) {
            llVector3.x = n2;
        }
        if (llVector4.x < n2) {
            llVector4.x = n2;
        }
        if (llVector3.y > n3) {
            llVector3.y = n3;
        }
        if (llVector4.y < n3) {
            llVector4.y = n3;
        }
        if (llVector3.z > n4) {
            llVector3.z = n4;
        }
        if (llVector4.z < n4) {
            llVector4.z = n4;
        }
    }
    
    public final void minMaxVector(final LLVector3 llVector3, final LLVector3 llVector4) {
        int offset = this.offset;
        for (int i = 0; i < this.length; ++i) {
            final float n = this.data[offset + 0];
            final float n2 = this.data[offset + 1];
            final float n3 = this.data[offset + 2];
            if (llVector3.x > n) {
                llVector3.x = n;
            }
            if (llVector4.x < n) {
                llVector4.x = n;
            }
            if (llVector3.y > n2) {
                llVector3.y = n2;
            }
            if (llVector4.y < n2) {
                llVector4.y = n2;
            }
            if (llVector3.z > n3) {
                llVector3.z = n3;
            }
            if (llVector4.z < n3) {
                llVector4.z = n3;
            }
            offset += this.numComponents;
        }
    }
    
    public final void mul(int n, final LLQuaternion llQuaternion) {
        n = this.offset + this.numComponents * n;
        final float n2 = this.data[n + 0];
        final float n3 = this.data[n + 1];
        final float n4 = this.data[n + 2];
        final float n5 = -llQuaternion.x * n2 - llQuaternion.y * n3 - llQuaternion.z * n4;
        final float n6 = llQuaternion.w * n2 + llQuaternion.y * n4 - llQuaternion.z * n3;
        final float n7 = llQuaternion.w * n3 + llQuaternion.z * n2 - llQuaternion.x * n4;
        final float n8 = n3 * llQuaternion.x + n4 * llQuaternion.w - n2 * llQuaternion.y;
        this.data[n + 0] = -n5 * llQuaternion.x + llQuaternion.w * n6 - llQuaternion.z * n7 + llQuaternion.y * n8;
        this.data[n + 1] = -n5 * llQuaternion.y + llQuaternion.w * n7 - llQuaternion.x * n8 + llQuaternion.z * n6;
        this.data[n + 2] = n8 * llQuaternion.w + -n5 * llQuaternion.z - llQuaternion.y * n6 + llQuaternion.x * n7;
    }
    
    public final void set(int n, final float n2, final float n3, final float n4) {
        n = this.offset + this.numComponents * n;
        this.data[n + 0] = n2;
        this.data[n + 1] = n3;
        this.data[n + 2] = n4;
    }
    
    public final void set(int n, final LLVector3 llVector3) {
        n = this.offset + this.numComponents * n;
        this.data[n + 0] = llVector3.x;
        this.data[n + 1] = llVector3.y;
        this.data[n + 2] = llVector3.z;
    }
    
    public final void set(int n, final Vector3Array vector3Array, int n2) {
        n = this.offset + this.numComponents * n;
        n2 = vector3Array.offset + vector3Array.numComponents * n2;
        this.data[n + 0] = vector3Array.data[n2 + 0];
        this.data[n + 1] = vector3Array.data[n2 + 1];
        this.data[n + 2] = vector3Array.data[n2 + 2];
    }
    
    public final void setAdd(int i, int n) {
        final int n2 = this.numComponents * i + this.offset;
        i = this.offset;
        n = this.numComponents * n + i;
        float[] data;
        int n3;
        for (i = 0; i < 3; ++i) {
            data = this.data;
            n3 = n2 + i;
            data[n3] += this.data[n + i];
            this.data[n + i] = this.data[n2 + i];
        }
    }
    
    public final void subFromVector(final LLVector3 llVector3, int n) {
        n = this.offset + this.numComponents * n;
        llVector3.x -= this.data[n + 0];
        llVector3.y -= this.data[n + 1];
        llVector3.z -= this.data[n + 2];
    }
}
