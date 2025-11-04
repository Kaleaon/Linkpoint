// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import java.nio.ByteBuffer;

public class LLQuaternion
{
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    private float[] inverseMatrix;
    private float[] matrix;
    public float w;
    public float x;
    public float y;
    public float z;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues() {
        if (LLQuaternion.-com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues != null) {
            return LLQuaternion.-com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues = new int[Order.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.XYZ.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.XZY.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.YXZ.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.YZX.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.ZXY.ordinal()] = 5;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues[Order.ZYX.ordinal()] = 6;
                                    return -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues = -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues;
                                }
                                catch (final NoSuchFieldError noSuchFieldError) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError2) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError3) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError4) {}
                }
                catch (final NoSuchFieldError noSuchFieldError5) {}
            }
            catch (final NoSuchFieldError noSuchFieldError6) {
                continue;
            }
            break;
        }
    }
    
    public LLQuaternion() {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 1.0f;
    }
    
    public LLQuaternion(final float x, final float y, final float z, final float w) {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public LLQuaternion(final LLQuaternion llQuaternion) {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = llQuaternion.x;
        this.y = llQuaternion.y;
        this.z = llQuaternion.z;
        this.w = llQuaternion.w;
    }
    
    public LLQuaternion(final float[] array) {
        this.matrix = null;
        this.inverseMatrix = null;
        final float n = array[0] + 1.0f + array[5] + array[10];
        if (n > 0.5f) {
            final float n2 = (float)(Math.sqrt(n) * 2.0);
            this.x = (array[9] - array[6]) / n2;
            this.y = (array[2] - array[8]) / n2;
            this.z = (array[4] - array[1]) / n2;
            this.w = n2 * 0.25f;
        }
        else if (array[0] > array[5] && array[0] > array[10]) {
            final float n3 = (float)(Math.sqrt(array[0] + 1.0f - array[5] - array[10]) * 2.0);
            this.x = 0.25f * n3;
            this.y = (array[4] + array[1]) / n3;
            this.z = (array[2] + array[8]) / n3;
            this.w = (array[9] - array[6]) / n3;
        }
        else if (array[5] > array[10]) {
            final float n4 = (float)(Math.sqrt(array[5] + 1.0f - array[0] - array[10]) * 2.0);
            this.x = (array[4] + array[1]) / n4;
            this.y = 0.25f * n4;
            this.z = (array[9] + array[6]) / n4;
            this.w = (array[2] - array[8]) / n4;
        }
        else {
            final float n5 = (float)(Math.sqrt(array[10] + 1.0f - array[0] - array[5]) * 2.0);
            this.x = (array[2] + array[8]) / n5;
            this.y = (array[9] + array[6]) / n5;
            this.z = 0.25f * n5;
            this.w = (array[4] - array[1]) / n5;
        }
    }
    
    public static LLQuaternion fromEuler(final float n, final float n2, final float n3) {
        final double cos = Math.cos(n / 2.0f);
        final double sin = Math.sin(n / 2.0f);
        final double cos2 = Math.cos(n2 / 2.0f);
        final double sin2 = Math.sin(n2 / 2.0f);
        final double cos3 = Math.cos(n3 / 2.0f);
        final double sin3 = Math.sin(n3 / 2.0f);
        final double n4 = cos * cos2;
        final double n5 = sin * sin2;
        return new LLQuaternion((float)(sin * cos2 * cos3 + cos * sin2 * sin3), (float)(cos * sin2 * cos3 - sin * cos2 * sin3), (float)(n4 * sin3 + n5 * cos3), (float)(n4 * cos3 - n5 * sin3));
    }
    
    public static LLQuaternion lerp(final LLQuaternion llQuaternion, final LLQuaternion llQuaternion2, final float n) {
        return new LLQuaternion(llQuaternion.x + (llQuaternion2.x - llQuaternion.x) * n, llQuaternion.y + (llQuaternion2.y - llQuaternion.y) * n, llQuaternion.z + (llQuaternion2.z - llQuaternion.z) * n, llQuaternion.w + (llQuaternion2.w - llQuaternion.w) * n);
    }
    
    public static LLQuaternion mayaQ(final float n, final float n2, final float n3, final Order order) {
        final LLQuaternion llQuaternion = new LLQuaternion();
        final LLQuaternion llQuaternion2 = new LLQuaternion();
        final LLQuaternion llQuaternion3 = new LLQuaternion();
        llQuaternion.setQuat(n * 0.017453292f, new LLVector3(1.0f, 0.0f, 0.0f));
        llQuaternion2.setQuat(n2 * 0.017453292f, new LLVector3(0.0f, 1.0f, 0.0f));
        llQuaternion3.setQuat(n3 * 0.017453292f, new LLVector3(0.0f, 0.0f, 1.0f));
        final LLQuaternion llQuaternion4 = new LLQuaternion();
        final LLQuaternion llQuaternion5 = new LLQuaternion();
        switch (-getcom-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues()[order.ordinal()]) {
            case 1: {
                llQuaternion4.setMul(llQuaternion, llQuaternion2);
                llQuaternion5.setMul(llQuaternion4, llQuaternion3);
                break;
            }
            case 4: {
                llQuaternion4.setMul(llQuaternion2, llQuaternion3);
                llQuaternion5.setMul(llQuaternion4, llQuaternion);
                break;
            }
            case 5: {
                llQuaternion4.setMul(llQuaternion3, llQuaternion);
                llQuaternion5.setMul(llQuaternion4, llQuaternion2);
                break;
            }
            case 2: {
                llQuaternion4.setMul(llQuaternion, llQuaternion3);
                llQuaternion5.setMul(llQuaternion4, llQuaternion2);
                break;
            }
            case 3: {
                llQuaternion4.setMul(llQuaternion2, llQuaternion);
                llQuaternion5.setMul(llQuaternion4, llQuaternion3);
                break;
            }
            case 6: {
                llQuaternion4.setMul(llQuaternion3, llQuaternion2);
                llQuaternion5.setMul(llQuaternion4, llQuaternion);
                break;
            }
        }
        return llQuaternion5;
    }
    
    public static LLQuaternion parseFloatVec3(final ByteBuffer byteBuffer) {
        float n = 0.0f;
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        final float n2 = 1.0f - (float1 * float1 + float2 * float2 + float3 * float3);
        if (n2 > 0.0f) {
            n = (float)Math.sqrt(n2);
        }
        return new LLQuaternion(float1, float2, float3, n);
    }
    
    public static LLQuaternion parseU16Vec3(final ByteBuffer byteBuffer, final float n, final float n2) {
        return new LLQuaternion(LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 0xFFFF, n, n2));
    }
    
    public static LLQuaternion parseU8Vec3(final ByteBuffer byteBuffer, final float n, final float n2) {
        return new LLQuaternion(LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2), LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2), LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2), LLTersePacking.U8_to_float(byteBuffer.get() & 0xFF, n, n2));
    }
    
    public static LLQuaternion shortestArc(LLVector3 llVector3, LLVector3 cross) {
        llVector3 = new LLVector3(llVector3);
        final LLVector3 llVector4 = new LLVector3(cross);
        final float normVec = llVector3.normVec();
        final float normVec2 = llVector4.normVec();
        if (normVec < 1.0E-7f || normVec2 < 1.0E-7f) {
            return new LLQuaternion();
        }
        cross = LLVector3.cross(llVector3, llVector4);
        final float dot = llVector3.dot(llVector4);
        if (dot > 0.9999999f) {
            return new LLQuaternion();
        }
        if (dot < -0.9999999f) {
            cross = new LLVector3(llVector3);
            cross.mul(llVector3.x / llVector3.dot(llVector3));
            llVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
            llVector3.sub(cross);
            if (llVector3.normVec() < 1.0E-7f) {
                llVector3.set(0.0f, 0.0f, 1.0f);
            }
            return new LLQuaternion(llVector3.x, llVector3.y, llVector3.z, 0.0f);
        }
        final float n = (float)Math.acos(dot);
        final LLQuaternion llQuaternion = new LLQuaternion();
        llQuaternion.setQuat(n, cross);
        return llQuaternion;
    }
    
    public static LLQuaternion unpackFromVector3(final LLVector3 llVector3) {
        float n = 0.0f;
        final float n2 = 1.0f - llVector3.magVecSquared();
        final float x = llVector3.x;
        final float y = llVector3.y;
        final float z = llVector3.z;
        if (n2 > 0.0f) {
            n = (float)Math.sqrt(n2);
        }
        return new LLQuaternion(x, y, z, n);
    }
    
    public void addMul(final LLQuaternion llQuaternion, final float n) {
        this.x += llQuaternion.x * n;
        this.y += llQuaternion.y * n;
        this.z += llQuaternion.z * n;
        this.w += llQuaternion.w * n;
    }
    
    public LLQuaternion conjQuat() {
        return new LLQuaternion(this.x * -1.0f, this.y * -1.0f, this.z * -1.0f, this.w);
    }
    
    public float getAngleAxis(final LLVector3 llVector3) {
        final float n = -1.0f;
        float w;
        if ((w = this.w) > 1.0f) {
            w = 1.0f;
        }
        if (w < -1.0f) {
            w = n;
        }
        final float a = (float)Math.sqrt(1.0f - w * w);
        float n2;
        if (Math.abs(a) < 5.0E-4f) {
            n2 = 1.0f;
        }
        else {
            n2 = 1.0f / a;
        }
        final float n3 = (float)Math.acos(w) * 2.0f;
        if (n3 > 3.1415927f) {
            llVector3.x = -this.x * n2;
            llVector3.y = -this.y * n2;
            llVector3.z = n2 * -this.z;
            return 6.2831855f - n3;
        }
        llVector3.x = this.x * n2;
        llVector3.y = this.y * n2;
        llVector3.z = n2 * this.z;
        return n3;
    }
    
    public void getInverseMatrix(final float[] array, final int n) {
        final float n2 = this.x * this.x;
        final float n3 = this.y * this.y;
        final float n4 = this.z * this.z;
        final float n5 = -this.x * -this.y;
        final float n6 = -this.x * -this.z;
        final float n7 = -this.y * -this.z;
        final float n8 = this.w * -this.x;
        final float n9 = this.w * -this.y;
        final float n10 = this.w * -this.z;
        array[n + 0] = 1.0f - (n3 + n4) * 2.0f;
        array[n + 1] = (n5 - n10) * 2.0f;
        array[n + 2] = (n6 + n9) * 2.0f;
        array[n + 3] = 0.0f;
        array[n + 4] = (n5 + n10) * 2.0f;
        array[n + 5] = 1.0f - (n4 + n2) * 2.0f;
        array[n + 6] = (n7 - n8) * 2.0f;
        array[n + 7] = 0.0f;
        array[n + 8] = (n6 - n9) * 2.0f;
        array[n + 9] = (n7 + n8) * 2.0f;
        array[n + 10] = 1.0f - (n2 + n3) * 2.0f;
        array[n + 12] = (array[n + 11] = 0.0f);
        array[n + 14] = (array[n + 13] = 0.0f);
        array[n + 15] = 1.0f;
    }
    
    public float[] getInverseMatrix() {
        if (this.inverseMatrix != null) {
            return this.inverseMatrix;
        }
        final float n = this.x * this.x;
        final float n2 = this.y * this.y;
        final float n3 = this.z * this.z;
        final float n4 = -this.x * -this.y;
        final float n5 = -this.x * -this.z;
        final float n6 = -this.y * -this.z;
        final float n7 = this.w * -this.x;
        final float n8 = this.w * -this.y;
        final float n9 = this.w * -this.z;
        return this.inverseMatrix = new float[] { 1.0f - (n2 + n3) * 2.0f, (n4 - n9) * 2.0f, (n5 + n8) * 2.0f, 0.0f, (n4 + n9) * 2.0f, 1.0f - (n3 + n) * 2.0f, (n6 - n7) * 2.0f, 0.0f, (n5 - n8) * 2.0f, (n6 + n7) * 2.0f, 1.0f - (n + n2) * 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
    }
    
    public float[] getMatrix() {
        if (this.matrix != null) {
            return this.matrix;
        }
        final float n = this.x * this.x;
        final float n2 = this.y * this.y;
        final float n3 = this.z * this.z;
        final float n4 = this.x * this.y;
        final float n5 = this.x * this.z;
        final float n6 = this.y * this.z;
        final float n7 = this.w * this.x;
        final float n8 = this.w * this.y;
        final float n9 = this.w * this.z;
        return this.matrix = new float[] { 1.0f - (n2 + n3) * 2.0f, (n4 - n9) * 2.0f, (n5 + n8) * 2.0f, 0.0f, (n4 + n9) * 2.0f, 1.0f - (n3 + n) * 2.0f, (n6 - n7) * 2.0f, 0.0f, (n5 - n8) * 2.0f, (n6 + n7) * 2.0f, 1.0f - (n + n2) * 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
    }
    
    public float normalize() {
        final float n = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        if (n > 1.0E-7f) {
            if (Math.abs(1.0f - n) > 1.0E-6f) {
                final float n2 = 1.0f / n;
                this.x *= n2;
                this.y *= n2;
                this.z *= n2;
                this.w *= n2;
            }
        }
        else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 1.0f;
        }
        this.matrix = null;
        this.inverseMatrix = null;
        return n;
    }
    
    public void set(final LLQuaternion llQuaternion) {
        this.x = llQuaternion.x;
        this.y = llQuaternion.y;
        this.z = llQuaternion.z;
        this.w = llQuaternion.w;
        this.matrix = null;
        this.inverseMatrix = null;
    }
    
    public void setIdentity() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 1.0f;
    }
    
    public void setLerp(final LLQuaternion llQuaternion, final float n, final LLQuaternion llQuaternion2, final float n2) {
        this.x = llQuaternion.x * n + llQuaternion2.x * n2;
        this.y = llQuaternion.y * n + llQuaternion2.y * n2;
        this.z = llQuaternion.z * n + llQuaternion2.z * n2;
        this.w = llQuaternion.w * n + llQuaternion2.w * n2;
        this.matrix = null;
        this.inverseMatrix = null;
    }
    
    public void setMul(final LLQuaternion llQuaternion, final LLQuaternion llQuaternion2) {
        this.x = llQuaternion2.w * llQuaternion.x + llQuaternion2.x * llQuaternion.w + llQuaternion2.y * llQuaternion.z - llQuaternion2.z * llQuaternion.y;
        this.y = llQuaternion2.w * llQuaternion.y + llQuaternion2.y * llQuaternion.w + llQuaternion2.z * llQuaternion.x - llQuaternion2.x * llQuaternion.z;
        this.z = llQuaternion2.w * llQuaternion.z + llQuaternion2.z * llQuaternion.w + llQuaternion2.x * llQuaternion.y - llQuaternion2.y * llQuaternion.x;
        this.w = llQuaternion2.w * llQuaternion.w - llQuaternion2.x * llQuaternion.x - llQuaternion2.y * llQuaternion.y - llQuaternion2.z * llQuaternion.z;
        this.matrix = null;
        this.inverseMatrix = null;
    }
    
    public void setQuat(float w, float n, final float n2, final float n3) {
        final LLVector3 llVector3 = new LLVector3(n, n2, n3);
        llVector3.normVec();
        n = 0.5f * w;
        w = (float)Math.cos(n);
        n = (float)Math.sin(n);
        this.x = llVector3.x * n;
        this.y = llVector3.y * n;
        this.z = llVector3.z * n;
        this.w = w;
        this.normalize();
        this.matrix = null;
        this.inverseMatrix = null;
    }
    
    public void setQuat(float w, LLVector3 llVector3) {
        llVector3 = new LLVector3(llVector3);
        llVector3.normVec();
        final float n = 0.5f * w;
        w = (float)Math.cos(n);
        final float n2 = (float)Math.sin(n);
        this.x = llVector3.x * n2;
        this.y = llVector3.y * n2;
        this.z = llVector3.z * n2;
        this.w = w;
        this.normalize();
        this.matrix = null;
        this.inverseMatrix = null;
    }
    
    public void setRaw(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public void setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }
    
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", this.x, this.y, this.z, this.w);
    }
    
    public enum Order
    {
        XYZ("XYZ", 0), 
        XZY("XZY", 3), 
        YXZ("YXZ", 4), 
        YZX("YZX", 1), 
        ZXY("ZXY", 2), 
        ZYX("ZYX", 5);
        
        private Order(final String name, final int ordinal) {
        }
    }
}
