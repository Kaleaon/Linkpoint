package com.lumiyaviewer.lumiya.slproto.types;

import com.google.common.primitives.UnsignedBytes;
import java.nio.ByteBuffer;

public class LLQuaternion {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues = null;
    public static final float FP_MAG_THRESHOLD = 1.0E-7f;
    private float[] inverseMatrix;
    private float[] matrix;
    public float w;
    public float x;
    public float y;
    public float z;

    public enum Order {
        XYZ,
        YZX,
        ZXY,
        XZY,
        YXZ,
        ZYX
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m258getcomlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues() {
        if (f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues != null) {
            return f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues;
        }
        int[] iArr = new int[Order.values().length];
        try {
            iArr[Order.XYZ.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Order.XZY.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Order.YXZ.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Order.YZX.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[Order.ZXY.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[Order.ZYX.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues = iArr;
        return iArr;
    }

    public LLQuaternion() {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 1.0f;
    }

    public LLQuaternion(float f, float f2, float f3, float f4) {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public LLQuaternion(LLQuaternion lLQuaternion) {
        this.matrix = null;
        this.inverseMatrix = null;
        this.x = lLQuaternion.x;
        this.y = lLQuaternion.y;
        this.z = lLQuaternion.z;
        this.w = lLQuaternion.w;
    }

    public LLQuaternion(float[] fArr) {
        this.matrix = null;
        this.inverseMatrix = null;
        float f = fArr[0] + 1.0f + fArr[5] + fArr[10];
        if (f > 0.5f) {
            float sqrt = (float) (Math.sqrt((double) f) * 2.0d);
            this.x = (fArr[9] - fArr[6]) / sqrt;
            this.y = (fArr[2] - fArr[8]) / sqrt;
            this.z = (fArr[4] - fArr[1]) / sqrt;
            this.w = sqrt * 0.25f;
        } else if (fArr[0] > fArr[5] && fArr[0] > fArr[10]) {
            float sqrt2 = (float) (Math.sqrt((double) (((fArr[0] + 1.0f) - fArr[5]) - fArr[10])) * 2.0d);
            this.x = 0.25f * sqrt2;
            this.y = (fArr[4] + fArr[1]) / sqrt2;
            this.z = (fArr[2] + fArr[8]) / sqrt2;
            this.w = (fArr[9] - fArr[6]) / sqrt2;
        } else if (fArr[5] > fArr[10]) {
            float sqrt3 = (float) (Math.sqrt((double) (((fArr[5] + 1.0f) - fArr[0]) - fArr[10])) * 2.0d);
            this.x = (fArr[4] + fArr[1]) / sqrt3;
            this.y = 0.25f * sqrt3;
            this.z = (fArr[9] + fArr[6]) / sqrt3;
            this.w = (fArr[2] - fArr[8]) / sqrt3;
        } else {
            float sqrt4 = (float) (Math.sqrt((double) (((fArr[10] + 1.0f) - fArr[0]) - fArr[5])) * 2.0d);
            this.x = (fArr[2] + fArr[8]) / sqrt4;
            this.y = (fArr[9] + fArr[6]) / sqrt4;
            this.z = 0.25f * sqrt4;
            this.w = (fArr[4] - fArr[1]) / sqrt4;
        }
    }

    public static LLQuaternion fromEuler(float f, float f2, float f3) {
        double cos = Math.cos((double) (f / 2.0f));
        double sin = Math.sin((double) (f / 2.0f));
        double cos2 = Math.cos((double) (f2 / 2.0f));
        double sin2 = Math.sin((double) (f2 / 2.0f));
        double cos3 = Math.cos((double) (f3 / 2.0f));
        double sin3 = Math.sin((double) (f3 / 2.0f));
        double d = cos * cos2;
        double d2 = sin * sin2;
        return new LLQuaternion((float) ((sin * cos2 * cos3) + (cos * sin2 * sin3)), (float) (((cos * sin2) * cos3) - ((sin * cos2) * sin3)), (float) ((d * sin3) + (d2 * cos3)), (float) ((d * cos3) - (d2 * sin3)));
    }

    public static LLQuaternion lerp(LLQuaternion lLQuaternion, LLQuaternion lLQuaternion2, float f) {
        return new LLQuaternion(lLQuaternion.x + ((lLQuaternion2.x - lLQuaternion.x) * f), lLQuaternion.y + ((lLQuaternion2.y - lLQuaternion.y) * f), lLQuaternion.z + ((lLQuaternion2.z - lLQuaternion.z) * f), lLQuaternion.w + ((lLQuaternion2.w - lLQuaternion.w) * f));
    }

    public static LLQuaternion mayaQ(float f, float f2, float f3, Order order) {
        LLQuaternion lLQuaternion = new LLQuaternion();
        LLQuaternion lLQuaternion2 = new LLQuaternion();
        LLQuaternion lLQuaternion3 = new LLQuaternion();
        lLQuaternion.setQuat(f * 0.017453292f, new LLVector3(1.0f, 0.0f, 0.0f));
        lLQuaternion2.setQuat(f2 * 0.017453292f, new LLVector3(0.0f, 1.0f, 0.0f));
        lLQuaternion3.setQuat(f3 * 0.017453292f, new LLVector3(0.0f, 0.0f, 1.0f));
        LLQuaternion lLQuaternion4 = new LLQuaternion();
        LLQuaternion lLQuaternion5 = new LLQuaternion();
        switch (m258getcomlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues()[order.ordinal()]) {
            case 1:
                lLQuaternion4.setMul(lLQuaternion, lLQuaternion2);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion3);
                break;
            case 2:
                lLQuaternion4.setMul(lLQuaternion, lLQuaternion3);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion2);
                break;
            case 3:
                lLQuaternion4.setMul(lLQuaternion2, lLQuaternion);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion3);
                break;
            case 4:
                lLQuaternion4.setMul(lLQuaternion2, lLQuaternion3);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion);
                break;
            case 5:
                lLQuaternion4.setMul(lLQuaternion3, lLQuaternion);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion2);
                break;
            case 6:
                lLQuaternion4.setMul(lLQuaternion3, lLQuaternion2);
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion);
                break;
        }
        return lLQuaternion5;
    }

    public static LLQuaternion parseFloatVec3(ByteBuffer byteBuffer) {
        float f = 0.0f;
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        float f4 = byteBuffer.getFloat();
        float f5 = 1.0f - (((f2 * f2) + (f3 * f3)) + (f4 * f4));
        if (f5 > 0.0f) {
            f = (float) Math.sqrt((double) f5);
        }
        return new LLQuaternion(f2, f3, f4, f);
    }

    public static LLQuaternion parseU16Vec3(ByteBuffer byteBuffer, float f, float f2) {
        return new LLQuaternion(LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2));
    }

    public static LLQuaternion parseU8Vec3(ByteBuffer byteBuffer, float f, float f2) {
        return new LLQuaternion(LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2));
    }

    public static LLQuaternion shortestArc(LLVector3 lLVector3, LLVector3 lLVector32) {
        LLVector3 lLVector33 = new LLVector3(lLVector3);
        LLVector3 lLVector34 = new LLVector3(lLVector32);
        float normVec = lLVector33.normVec();
        float normVec2 = lLVector34.normVec();
        if (normVec < 1.0E-7f || normVec2 < 1.0E-7f) {
            return new LLQuaternion();
        }
        LLVector3 cross = LLVector3.cross(lLVector33, lLVector34);
        float dot = lLVector33.dot(lLVector34);
        if (dot > 0.9999999f) {
            return new LLQuaternion();
        }
        if (dot < -0.9999999f) {
            LLVector3 lLVector35 = new LLVector3(lLVector33);
            lLVector35.mul(lLVector33.x / lLVector33.dot(lLVector33));
            LLVector3 lLVector36 = new LLVector3(1.0f, 0.0f, 0.0f);
            lLVector36.sub(lLVector35);
            if (lLVector36.normVec() < 1.0E-7f) {
                lLVector36.set(0.0f, 0.0f, 1.0f);
            }
            return new LLQuaternion(lLVector36.x, lLVector36.y, lLVector36.z, 0.0f);
        }
        LLQuaternion lLQuaternion = new LLQuaternion();
        lLQuaternion.setQuat((float) Math.acos((double) dot), cross);
        return lLQuaternion;
    }

    public static LLQuaternion unpackFromVector3(LLVector3 lLVector3) {
        float f = 0.0f;
        float magVecSquared = 1.0f - lLVector3.magVecSquared();
        float f2 = lLVector3.x;
        float f3 = lLVector3.y;
        float f4 = lLVector3.z;
        if (magVecSquared > 0.0f) {
            f = (float) Math.sqrt((double) magVecSquared);
        }
        return new LLQuaternion(f2, f3, f4, f);
    }

    public void addMul(LLQuaternion lLQuaternion, float f) {
        this.x += lLQuaternion.x * f;
        this.y += lLQuaternion.y * f;
        this.z += lLQuaternion.z * f;
        this.w += lLQuaternion.w * f;
    }

    public LLQuaternion conjQuat() {
        return new LLQuaternion(this.x * -1.0f, this.y * -1.0f, this.z * -1.0f, this.w);
    }

    public float getAngleAxis(LLVector3 lLVector3) {
        float f = -1.0f;
        float f2 = this.w;
        if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        if (f2 >= -1.0f) {
            f = f2;
        }
        float sqrt = (float) Math.sqrt((double) (1.0f - (f * f)));
        float f3 = Math.abs(sqrt) < 5.0E-4f ? 1.0f : 1.0f / sqrt;
        float acos = ((float) Math.acos((double) f)) * 2.0f;
        if (acos > 3.1415927f) {
            lLVector3.x = (-this.x) * f3;
            lLVector3.y = (-this.y) * f3;
            lLVector3.z = f3 * (-this.z);
            return 6.2831855f - acos;
        }
        lLVector3.x = this.x * f3;
        lLVector3.y = this.y * f3;
        lLVector3.z = f3 * this.z;
        return acos;
    }

    public void getInverseMatrix(float[] fArr, int i) {
        float f = this.x * this.x;
        float f2 = this.y * this.y;
        float f3 = this.z * this.z;
        float f4 = (-this.x) * (-this.y);
        float f5 = (-this.x) * (-this.z);
        float f6 = (-this.y) * (-this.z);
        float f7 = this.w * (-this.x);
        float f8 = this.w * (-this.y);
        float f9 = this.w * (-this.z);
        fArr[i + 0] = 1.0f - ((f2 + f3) * 2.0f);
        fArr[i + 1] = (f4 - f9) * 2.0f;
        fArr[i + 2] = (f5 + f8) * 2.0f;
        fArr[i + 3] = 0.0f;
        fArr[i + 4] = (f4 + f9) * 2.0f;
        fArr[i + 5] = 1.0f - ((f3 + f) * 2.0f);
        fArr[i + 6] = (f6 - f7) * 2.0f;
        fArr[i + 7] = 0.0f;
        fArr[i + 8] = (f5 - f8) * 2.0f;
        fArr[i + 9] = (f6 + f7) * 2.0f;
        fArr[i + 10] = 1.0f - ((f + f2) * 2.0f);
        fArr[i + 11] = 0.0f;
        fArr[i + 12] = 0.0f;
        fArr[i + 13] = 0.0f;
        fArr[i + 14] = 0.0f;
        fArr[i + 15] = 1.0f;
    }

    public float[] getInverseMatrix() {
        if (this.inverseMatrix != null) {
            return this.inverseMatrix;
        }
        float f = this.x * this.x;
        float f2 = this.y * this.y;
        float f3 = this.z * this.z;
        float f4 = (-this.x) * (-this.y);
        float f5 = (-this.x) * (-this.z);
        float f6 = (-this.y) * (-this.z);
        float f7 = this.w * (-this.x);
        float f8 = this.w * (-this.y);
        float f9 = this.w * (-this.z);
        this.inverseMatrix = new float[]{1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f, (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f, (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        return this.inverseMatrix;
    }

    public float[] getMatrix() {
        if (this.matrix != null) {
            return this.matrix;
        }
        float f = this.x * this.x;
        float f2 = this.y * this.y;
        float f3 = this.z * this.z;
        float f4 = this.x * this.y;
        float f5 = this.x * this.z;
        float f6 = this.y * this.z;
        float f7 = this.w * this.x;
        float f8 = this.w * this.y;
        float f9 = this.w * this.z;
        this.matrix = new float[]{1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f, (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f, (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        return this.matrix;
    }

    public float normalize() {
        float sqrt = (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z) + (this.w * this.w)));
        if (sqrt <= 1.0E-7f) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 1.0f;
        } else if (Math.abs(1.0f - sqrt) > 1.0E-6f) {
            float f = 1.0f / sqrt;
            this.x *= f;
            this.y *= f;
            this.z *= f;
            this.w = f * this.w;
        }
        this.matrix = null;
        this.inverseMatrix = null;
        return sqrt;
    }

    public void set(LLQuaternion lLQuaternion) {
        this.x = lLQuaternion.x;
        this.y = lLQuaternion.y;
        this.z = lLQuaternion.z;
        this.w = lLQuaternion.w;
        this.matrix = null;
        this.inverseMatrix = null;
    }

    public void setIdentity() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 1.0f;
    }

    public void setLerp(LLQuaternion lLQuaternion, float f, LLQuaternion lLQuaternion2, float f2) {
        this.x = (lLQuaternion.x * f) + (lLQuaternion2.x * f2);
        this.y = (lLQuaternion.y * f) + (lLQuaternion2.y * f2);
        this.z = (lLQuaternion.z * f) + (lLQuaternion2.z * f2);
        this.w = (lLQuaternion.w * f) + (lLQuaternion2.w * f2);
        this.matrix = null;
        this.inverseMatrix = null;
    }

    public void setMul(LLQuaternion lLQuaternion, LLQuaternion lLQuaternion2) {
        this.x = (((lLQuaternion2.w * lLQuaternion.x) + (lLQuaternion2.x * lLQuaternion.w)) + (lLQuaternion2.y * lLQuaternion.z)) - (lLQuaternion2.z * lLQuaternion.y);
        this.y = (((lLQuaternion2.w * lLQuaternion.y) + (lLQuaternion2.y * lLQuaternion.w)) + (lLQuaternion2.z * lLQuaternion.x)) - (lLQuaternion2.x * lLQuaternion.z);
        this.z = (((lLQuaternion2.w * lLQuaternion.z) + (lLQuaternion2.z * lLQuaternion.w)) + (lLQuaternion2.x * lLQuaternion.y)) - (lLQuaternion2.y * lLQuaternion.x);
        this.w = (((lLQuaternion2.w * lLQuaternion.w) - (lLQuaternion2.x * lLQuaternion.x)) - (lLQuaternion2.y * lLQuaternion.y)) - (lLQuaternion2.z * lLQuaternion.z);
        this.matrix = null;
        this.inverseMatrix = null;
    }

    public void setQuat(float f, float f2, float f3, float f4) {
        LLVector3 lLVector3 = new LLVector3(f2, f3, f4);
        lLVector3.normVec();
        float f5 = 0.5f * f;
        float sin = (float) Math.sin((double) f5);
        this.x = lLVector3.x * sin;
        this.y = lLVector3.y * sin;
        this.z = lLVector3.z * sin;
        this.w = (float) Math.cos((double) f5);
        normalize();
        this.matrix = null;
        this.inverseMatrix = null;
    }

    public void setQuat(float f, LLVector3 lLVector3) {
        LLVector3 lLVector32 = new LLVector3(lLVector3);
        lLVector32.normVec();
        float f2 = 0.5f * f;
        float sin = (float) Math.sin((double) f2);
        this.x = lLVector32.x * sin;
        this.y = lLVector32.y * sin;
        this.z = lLVector32.z * sin;
        this.w = (float) Math.cos((double) f2);
        normalize();
        this.matrix = null;
        this.inverseMatrix = null;
    }

    public void setRaw(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public void setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    public String toString() {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.w)});
    }
}
