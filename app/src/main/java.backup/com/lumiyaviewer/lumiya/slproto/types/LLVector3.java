package com.lumiyaviewer.lumiya.slproto.types;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import java.nio.ByteBuffer;

public class LLVector3 {
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    public static final LLVector3 Zero = new LLVector3(0.0f, 0.0f, 0.0f);
    public static final LLVector3 z_axis = new LLVector3(0.0f, 0.0f, 1.0f);
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public LLVector3() {
    }

    public LLVector3(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public LLVector3(LLVector3 lLVector3) {
        this.x = lLVector3.x;
        this.y = lLVector3.y;
        this.z = lLVector3.z;
    }

    public static LLVector3 cross(LLVector3 lLVector3, LLVector3 lLVector32) {
        return new LLVector3((lLVector3.y * lLVector32.z) - (lLVector32.y * lLVector3.z), (lLVector3.z * lLVector32.x) - (lLVector32.z * lLVector3.x), (lLVector3.x * lLVector32.y) - (lLVector32.x * lLVector3.y));
    }

    public static LLVector3 lerp(LLVector3 lLVector3, LLVector3 lLVector32, float f) {
        return new LLVector3(lLVector3.x + ((lLVector32.x - lLVector3.x) * f), lLVector3.y + ((lLVector32.y - lLVector3.y) * f), lLVector3.z + ((lLVector32.z - lLVector3.z) * f));
    }

    public static LLVector3 parseFloatVec(ByteBuffer byteBuffer) {
        return new LLVector3(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
    }

    public static LLVector3 parseU16Vec(ByteBuffer byteBuffer, float f, float f2, float f3, float f4) {
        return new LLVector3(LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f3, f4));
    }

    public static LLVector3 parseU8Vec(ByteBuffer byteBuffer, float f, float f2, float f3, float f4) {
        return new LLVector3(LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f3, f4));
    }

    public static LLVector3 scaleFromMatrix(float[] fArr) {
        return new LLVector3((float) Math.sqrt((double) ((fArr[0] * fArr[0]) + (fArr[1] * fArr[1]) + (fArr[2] * fArr[2]))), (float) Math.sqrt((double) ((fArr[4] * fArr[4]) + (fArr[5] * fArr[5]) + (fArr[6] * fArr[6]))), (float) Math.sqrt((double) ((fArr[8] * fArr[8]) + (fArr[9] * fArr[9]) + (fArr[10] * fArr[10]))));
    }

    public static LLVector3 sub(LLVector3 lLVector3, LLVector3 lLVector32) {
        return new LLVector3(lLVector3.x - lLVector32.x, lLVector3.y - lLVector32.y, lLVector3.z - lLVector32.z);
    }

    public void add(LLVector3 lLVector3) {
        this.x += lLVector3.x;
        this.y += lLVector3.y;
        this.z += lLVector3.z;
    }

    public void addMul(ImmutableVector immutableVector, float f) {
        this.x += immutableVector.x * f;
        this.y += immutableVector.y * f;
        this.z += immutableVector.z * f;
    }

    public void addMul(LLVector3 lLVector3, float f) {
        this.x += lLVector3.x * f;
        this.y += lLVector3.y * f;
        this.z += lLVector3.z * f;
    }

    public float dot(LLVector3 lLVector3) {
        return (this.x * lLVector3.x) + (this.y * lLVector3.y) + (this.z * lLVector3.z);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LLVector3)) {
            return false;
        }
        LLVector3 lLVector3 = (LLVector3) obj;
        return this.x == lLVector3.x && this.y == lLVector3.y && this.z == lLVector3.z;
    }

    public float getDistanceTo(LLVector3 lLVector3) {
        float f = this.x - lLVector3.x;
        float f2 = this.y - lLVector3.y;
        float f3 = this.z - lLVector3.z;
        return (float) Math.sqrt((double) ((f * f) + (f2 * f2) + (f3 * f3)));
    }

    public float getMax() {
        return Math.max(Math.max(this.x, this.y), this.z);
    }

    public LLVector3 getRotatedOffset(float f, float f2) {
        float f3 = (3.1415927f * f2) / 180.0f;
        return new LLVector3((((float) Math.cos((double) f3)) * f) + this.x, (((float) Math.sin((double) f3)) * f) + this.y, this.z);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y) + Float.floatToIntBits(this.z);
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }

    public float magVec() {
        return (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z)));
    }

    public float magVecSquared() {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    public void mul(LLQuaternion lLQuaternion) {
        float f = (((-lLQuaternion.x) * this.x) - (lLQuaternion.y * this.y)) - (lLQuaternion.z * this.z);
        float f2 = ((lLQuaternion.w * this.x) + (lLQuaternion.y * this.z)) - (lLQuaternion.z * this.y);
        float f3 = ((lLQuaternion.w * this.y) + (lLQuaternion.z * this.x)) - (lLQuaternion.x * this.z);
        float f4 = ((lLQuaternion.w * this.z) + (lLQuaternion.x * this.y)) - (lLQuaternion.y * this.x);
        this.x = ((((-f) * lLQuaternion.x) + (lLQuaternion.w * f2)) - (lLQuaternion.z * f3)) + (lLQuaternion.y * f4);
        this.y = ((((-f) * lLQuaternion.y) + (lLQuaternion.w * f3)) - (lLQuaternion.x * f4)) + (lLQuaternion.z * f2);
        this.z = ((((-f) * lLQuaternion.z) + (f4 * lLQuaternion.w)) - (f2 * lLQuaternion.y)) + (lLQuaternion.x * f3);
    }

    public void mul(LLVector3 lLVector3) {
        this.x *= lLVector3.x;
        this.y *= lLVector3.y;
        this.z *= lLVector3.z;
    }

    public void mulWeighted(ImmutableVector immutableVector, float f) {
        this.x *= (immutableVector.x * f) + 1.0f;
        this.y *= (immutableVector.y * f) + 1.0f;
        this.z *= (immutableVector.z * f) + 1.0f;
    }

    public void mulWeighted(LLVector3 lLVector3, float f) {
        this.x *= (lLVector3.x * f) + 1.0f;
        this.y *= (lLVector3.y * f) + 1.0f;
        this.z *= (lLVector3.z * f) + 1.0f;
    }

    public float normVec() {
        float sqrt = (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z)));
        if (sqrt > 1.0E-7f) {
            float f = 1.0f / sqrt;
            this.x *= f;
            this.y *= f;
            this.z = f * this.z;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }
        return sqrt;
    }

    public void set(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public void set(LLVector3 lLVector3) {
        if (lLVector3 != null) {
            this.x = lLVector3.x;
            this.y = lLVector3.y;
            this.z = lLVector3.z;
        }
    }

    public void setAdd(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x + lLVector32.x;
        this.y = lLVector3.y + lLVector32.y;
        this.z = lLVector3.z + lLVector32.z;
    }

    public void setCross(LLVector3 lLVector3) {
        float f = (this.y * lLVector3.z) - (lLVector3.y * this.z);
        float f2 = (this.z * lLVector3.x) - (lLVector3.z * this.x);
        this.x = f;
        this.y = f2;
        this.z = (this.x * lLVector3.y) - (lLVector3.x * this.y);
    }

    public void setLerp(LLVector3 lLVector3, float f, LLVector3 lLVector32, float f2) {
        this.x = (lLVector3.x * f) + (lLVector32.x * f2);
        this.y = (lLVector3.y * f) + (lLVector32.y * f2);
        this.z = (lLVector3.z * f) + (lLVector32.z * f2);
    }

    public void setLerp(LLVector3 lLVector3, LLVector3 lLVector32, float f) {
        this.x = lLVector3.x + ((lLVector32.x - lLVector3.x) * f);
        this.y = lLVector3.y + ((lLVector32.y - lLVector3.y) * f);
        this.z = lLVector3.z + ((lLVector32.z - lLVector3.z) * f);
    }

    public void setMul(LLVector3 lLVector3, float f) {
        this.x = lLVector3.x * f;
        this.y = lLVector3.y * f;
        this.z = lLVector3.z * f;
    }

    public void setMul(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x * lLVector32.x;
        this.y = lLVector3.y * lLVector32.y;
        this.z = lLVector3.z * lLVector32.z;
    }

    public void setSub(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.x = lLVector3.x - lLVector32.x;
        this.y = lLVector3.y - lLVector32.y;
        this.z = lLVector3.z - lLVector32.z;
    }

    public void sub(LLVector3 lLVector3) {
        this.x -= lLVector3.x;
        this.y -= lLVector3.y;
        this.z -= lLVector3.z;
    }

    public LLSDNode toLLSD() {
        return new LLSDMap(new LLSDMap.LLSDMapEntry("X", new LLSDDouble((double) this.x)), new LLSDMap.LLSDMapEntry("Y", new LLSDDouble((double) this.y)), new LLSDMap.LLSDMapEntry("Z", new LLSDDouble((double) this.z)));
    }

    public String toString() {
        return String.format("(%f, %f, %f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)});
    }
}
