// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.nio.ByteBuffer;

public class LLVector3
{
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    public static final LLVector3 Zero;
    public static final LLVector3 z_axis;
    public float x;
    public float y;
    public float z;
    
    static {
        z_axis = new LLVector3(0.0f, 0.0f, 1.0f);
        Zero = new LLVector3(0.0f, 0.0f, 0.0f);
    }
    
    public LLVector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    public LLVector3(final float x, final float y, final float z) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public LLVector3(final LLVector3 llVector3) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.x = llVector3.x;
        this.y = llVector3.y;
        this.z = llVector3.z;
    }
    
    public static LLVector3 cross(final LLVector3 llVector3, final LLVector3 llVector4) {
        return new LLVector3(llVector3.y * llVector4.z - llVector4.y * llVector3.z, llVector3.z * llVector4.x - llVector4.z * llVector3.x, llVector3.x * llVector4.y - llVector4.x * llVector3.y);
    }
    
    public static LLVector3 lerp(final LLVector3 llVector3, final LLVector3 llVector4, final float n) {
        return new LLVector3(llVector3.x + (llVector4.x - llVector3.x) * n, llVector3.y + (llVector4.y - llVector3.y) * n, llVector3.z + (llVector4.z - llVector3.z) * n);
    }
    
    public static LLVector3 parseFloatVec(final ByteBuffer byteBuffer) {
        return new LLVector3(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
    }
    
    public static LLVector3 parseU16Vec(final ByteBuffer byteBuffer, final float n, final float n2, final float n3, final float n4) {
        return new LLVector3(LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n3, n4));
    }
    
    public static LLVector3 parseU8Vec(final ByteBuffer byteBuffer, final float n, final float n2, final float n3, final float n4) {
        return new LLVector3(LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2), LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2), LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n3, n4));
    }
    
    public static LLVector3 scaleFromMatrix(final float[] array) {
        return new LLVector3((float)Math.sqrt(array[0] * array[0] + array[1] * array[1] + array[2] * array[2]), (float)Math.sqrt(array[4] * array[4] + array[5] * array[5] + array[6] * array[6]), (float)Math.sqrt(array[8] * array[8] + array[9] * array[9] + array[10] * array[10]));
    }
    
    public static LLVector3 sub(final LLVector3 llVector3, final LLVector3 llVector4) {
        return new LLVector3(llVector3.x - llVector4.x, llVector3.y - llVector4.y, llVector3.z - llVector4.z);
    }
    
    public void add(final LLVector3 llVector3) {
        this.x += llVector3.x;
        this.y += llVector3.y;
        this.z += llVector3.z;
    }
    
    public void addMul(final ImmutableVector immutableVector, final float n) {
        this.x += immutableVector.x * n;
        this.y += immutableVector.y * n;
        this.z += immutableVector.z * n;
    }
    
    public void addMul(final LLVector3 llVector3, final float n) {
        this.x += llVector3.x * n;
        this.y += llVector3.y * n;
        this.z += llVector3.z * n;
    }
    
    public float dot(final LLVector3 llVector3) {
        return this.x * llVector3.x + this.y * llVector3.y + this.z * llVector3.z;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof LLVector3)) {
            return false;
        }
        final LLVector3 llVector3 = (LLVector3)o;
        if (this.x != llVector3.x || this.y != llVector3.y || this.z != llVector3.z) {
            b = false;
        }
        return b;
    }
    
    public float getDistanceTo(final LLVector3 llVector3) {
        final float n = this.x - llVector3.x;
        final float n2 = this.y - llVector3.y;
        final float n3 = this.z - llVector3.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public float getMax() {
        return Math.max(Math.max(this.x, this.y), this.z);
    }
    
    public LLVector3 getRotatedOffset(final float n, float n2) {
        n2 = 3.1415927f * n2 / 180.0f;
        return new LLVector3((float)Math.cos(n2) * n + this.x, (float)Math.sin(n2) * n + this.y, this.z);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y) + Float.floatToIntBits(this.z);
    }
    
    public boolean isZero() {
        boolean b2;
        final boolean b = b2 = false;
        if (this.x == 0.0f) {
            b2 = b;
            if (this.y == 0.0f) {
                b2 = b;
                if (this.z == 0.0f) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public float magVec() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public float magVecSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public void mul(final float n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }
    
    public void mul(final LLQuaternion llQuaternion) {
        final float n = -llQuaternion.x * this.x - llQuaternion.y * this.y - llQuaternion.z * this.z;
        final float n2 = llQuaternion.w * this.x + llQuaternion.y * this.z - llQuaternion.z * this.y;
        final float n3 = llQuaternion.w * this.y + llQuaternion.z * this.x - llQuaternion.x * this.z;
        final float n4 = llQuaternion.w * this.z + llQuaternion.x * this.y - llQuaternion.y * this.x;
        this.x = -n * llQuaternion.x + llQuaternion.w * n2 - llQuaternion.z * n3 + llQuaternion.y * n4;
        this.y = -n * llQuaternion.y + llQuaternion.w * n3 - llQuaternion.x * n4 + llQuaternion.z * n2;
        this.z = -n * llQuaternion.z + n4 * llQuaternion.w - n2 * llQuaternion.y + llQuaternion.x * n3;
    }
    
    public void mul(final LLVector3 llVector3) {
        this.x *= llVector3.x;
        this.y *= llVector3.y;
        this.z *= llVector3.z;
    }
    
    public void mulWeighted(final ImmutableVector immutableVector, final float n) {
        this.x *= immutableVector.x * n + 1.0f;
        this.y *= immutableVector.y * n + 1.0f;
        this.z *= immutableVector.z * n + 1.0f;
    }
    
    public void mulWeighted(final LLVector3 llVector3, final float n) {
        this.x *= llVector3.x * n + 1.0f;
        this.y *= llVector3.y * n + 1.0f;
        this.z *= llVector3.z * n + 1.0f;
    }
    
    public float normVec() {
        final float n = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (n > 1.0E-7f) {
            final float n2 = 1.0f / n;
            this.x *= n2;
            this.y *= n2;
            this.z *= n2;
        }
        else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }
        return n;
    }
    
    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(final LLVector3 llVector3) {
        if (llVector3 != null) {
            this.x = llVector3.x;
            this.y = llVector3.y;
            this.z = llVector3.z;
        }
    }
    
    public void setAdd(final LLVector3 llVector3, final LLVector3 llVector4) {
        this.x = llVector3.x + llVector4.x;
        this.y = llVector3.y + llVector4.y;
        this.z = llVector3.z + llVector4.z;
    }
    
    public void setCross(final LLVector3 llVector3) {
        final float y = this.y;
        final float z = llVector3.z;
        final float y2 = llVector3.y;
        final float z2 = this.z;
        final float z3 = this.z;
        final float x = llVector3.x;
        final float z4 = llVector3.z;
        final float x2 = this.x;
        final float x3 = this.x;
        final float y3 = llVector3.y;
        final float x4 = llVector3.x;
        final float y4 = this.y;
        this.x = y * z - y2 * z2;
        this.y = z3 * x - z4 * x2;
        this.z = x3 * y3 - x4 * y4;
    }
    
    public void setLerp(final LLVector3 llVector3, final float n, final LLVector3 llVector4, final float n2) {
        this.x = llVector3.x * n + llVector4.x * n2;
        this.y = llVector3.y * n + llVector4.y * n2;
        this.z = llVector3.z * n + llVector4.z * n2;
    }
    
    public void setLerp(final LLVector3 llVector3, final LLVector3 llVector4, final float n) {
        this.x = llVector3.x + (llVector4.x - llVector3.x) * n;
        this.y = llVector3.y + (llVector4.y - llVector3.y) * n;
        this.z = llVector3.z + (llVector4.z - llVector3.z) * n;
    }
    
    public void setMul(final LLVector3 llVector3, final float n) {
        this.x = llVector3.x * n;
        this.y = llVector3.y * n;
        this.z = llVector3.z * n;
    }
    
    public void setMul(final LLVector3 llVector3, final LLVector3 llVector4) {
        this.x = llVector3.x * llVector4.x;
        this.y = llVector3.y * llVector4.y;
        this.z = llVector3.z * llVector4.z;
    }
    
    public void setSub(final LLVector3 llVector3, final LLVector3 llVector4) {
        this.x = llVector3.x - llVector4.x;
        this.y = llVector3.y - llVector4.y;
        this.z = llVector3.z - llVector4.z;
    }
    
    public void sub(final LLVector3 llVector3) {
        this.x -= llVector3.x;
        this.y -= llVector3.y;
        this.z -= llVector3.z;
    }
    
    public LLSDNode toLLSD() {
        return new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("X", new LLSDDouble(this.x)), new LLSDMap.LLSDMapEntry("Y", new LLSDDouble(this.y)), new LLSDMap.LLSDMapEntry("Z", new LLSDDouble(this.z)) });
    }
    
    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", this.x, this.y, this.z);
    }
}
