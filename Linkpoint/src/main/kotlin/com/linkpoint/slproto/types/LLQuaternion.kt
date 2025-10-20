package com.linkpoint.slproto.types

import com.google.common.primitives.UnsignedBytes
import java.nio.ByteBuffer

class LLQuaternion {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues = null
    const val FP_MAG_THRESHOLD: Float = 1.0E-7f
    private Float[] inverseMatrix
    private Float[] matrix
    public Float w
    public Float x
    public Float y
    public Float z

    enum class Order {
        XYZ,
        YZX,
        ZXY,
        XZY,
        YXZ,
        ZYX
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-types-LLQuaternion$OrderSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m258getcomlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues() {
        if (f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues != null) {
            return f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues
        }
        Int[] iArr = Int[Order.values().length]
        try {
            iArr[Order.XYZ.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Order.XZY.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Order.YXZ.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Order.YZX.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[Order.ZXY.ordinal()] = 5
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[Order.ZYX.ordinal()] = 6
        } catch (NoSuchFieldError e6) {
        }
        f143comlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues = iArr
        return iArr
    }

    public LLQuaternion() {
        this.matrix = null
        this.inverseMatrix = null
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 1.0f
    }

    public LLQuaternion(Float f, Float f2, Float f3, Float f4) {
        this.matrix = null
        this.inverseMatrix = null
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    public LLQuaternion(LLQuaternion lLQuaternion) {
        this.matrix = null
        this.inverseMatrix = null
        this.x = lLQuaternion.x
        this.y = lLQuaternion.y
        this.z = lLQuaternion.z
        this.w = lLQuaternion.w
    }

    public LLQuaternion(Float[] fArr) {
        this.matrix = null
        this.inverseMatrix = null
        Float f = fArr[0] + 1.0f + fArr[5] + fArr[10]
        if (f > 0.5f) {
            Float sqrt = (Float) (Math.sqrt((Double) f) * 2.0d)
            this.x = (fArr[9] - fArr[6]) / sqrt
            this.y = (fArr[2] - fArr[8]) / sqrt
            this.z = (fArr[4] - fArr[1]) / sqrt
            this.w = sqrt * 0.25f
        } else if (fArr[0] > fArr[5] && fArr[0] > fArr[10]) {
            Float sqrt2 = (Float) (Math.sqrt((Double) (((fArr[0] + 1.0f) - fArr[5]) - fArr[10])) * 2.0d)
            this.x = 0.25f * sqrt2
            this.y = (fArr[4] + fArr[1]) / sqrt2
            this.z = (fArr[2] + fArr[8]) / sqrt2
            this.w = (fArr[9] - fArr[6]) / sqrt2
        } else if (fArr[5] > fArr[10]) {
            Float sqrt3 = (Float) (Math.sqrt((Double) (((fArr[5] + 1.0f) - fArr[0]) - fArr[10])) * 2.0d)
            this.x = (fArr[4] + fArr[1]) / sqrt3
            this.y = 0.25f * sqrt3
            this.z = (fArr[9] + fArr[6]) / sqrt3
            this.w = (fArr[2] - fArr[8]) / sqrt3
        } else {
            Float sqrt4 = (Float) (Math.sqrt((Double) (((fArr[10] + 1.0f) - fArr[0]) - fArr[5])) * 2.0d)
            this.x = (fArr[2] + fArr[8]) / sqrt4
            this.y = (fArr[9] + fArr[6]) / sqrt4
            this.z = 0.25f * sqrt4
            this.w = (fArr[4] - fArr[1]) / sqrt4
        }
    }

    @JvmStatic
    LLQuaternion fromEuler(Float f, Float f2, Float f3) {
        Double cos = Math.cos((Double) (f / 2.0f))
        Double sin = Math.sin((Double) (f / 2.0f))
        Double cos2 = Math.cos((Double) (f2 / 2.0f))
        Double sin2 = Math.sin((Double) (f2 / 2.0f))
        Double cos3 = Math.cos((Double) (f3 / 2.0f))
        Double sin3 = Math.sin((Double) (f3 / 2.0f))
        Double d = cos * cos2
        Double d2 = sin * sin2
        return LLQuaternion((Float) ((sin * cos2 * cos3) + (cos * sin2 * sin3)), (Float) (((cos * sin2) * cos3) - ((sin * cos2) * sin3)), (Float) ((d * sin3) + (d2 * cos3)), (Float) ((d * cos3) - (d2 * sin3)))
    }

    @JvmStatic
    LLQuaternion lerp(LLQuaternion lLQuaternion, LLQuaternion lLQuaternion2, Float f) {
        return LLQuaternion(lLQuaternion.x + ((lLQuaternion2.x - lLQuaternion.x) * f), lLQuaternion.y + ((lLQuaternion2.y - lLQuaternion.y) * f), lLQuaternion.z + ((lLQuaternion2.z - lLQuaternion.z) * f), lLQuaternion.w + ((lLQuaternion2.w - lLQuaternion.w) * f))
    }

    @JvmStatic
    LLQuaternion mayaQ(Float f, Float f2, Float f3, Order order) {
        LLQuaternion lLQuaternion = LLQuaternion()
        LLQuaternion lLQuaternion2 = LLQuaternion()
        LLQuaternion lLQuaternion3 = LLQuaternion()
        lLQuaternion.setQuat(f * 0.017453292f, LLVector3(1.0f, 0.0f, 0.0f))
        lLQuaternion2.setQuat(f2 * 0.017453292f, LLVector3(0.0f, 1.0f, 0.0f))
        lLQuaternion3.setQuat(f3 * 0.017453292f, LLVector3(0.0f, 0.0f, 1.0f))
        LLQuaternion lLQuaternion4 = LLQuaternion()
        LLQuaternion lLQuaternion5 = LLQuaternion()
        switch (m258getcomlumiyaviewerlumiyaslprototypesLLQuaternion$OrderSwitchesValues()[order.ordinal()]) {
            case 1:
                lLQuaternion4.setMul(lLQuaternion, lLQuaternion2)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion3)
                break
            case 2:
                lLQuaternion4.setMul(lLQuaternion, lLQuaternion3)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion2)
                break
            case 3:
                lLQuaternion4.setMul(lLQuaternion2, lLQuaternion)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion3)
                break
            case 4:
                lLQuaternion4.setMul(lLQuaternion2, lLQuaternion3)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion)
                break
            case 5:
                lLQuaternion4.setMul(lLQuaternion3, lLQuaternion)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion2)
                break
            case 6:
                lLQuaternion4.setMul(lLQuaternion3, lLQuaternion2)
                lLQuaternion5.setMul(lLQuaternion4, lLQuaternion)
                break
        }
        return lLQuaternion5
    }

    @JvmStatic
    LLQuaternion parseFloatVec3(ByteBuffer byteBuffer) {
        Float f = 0.0f
        Float f2 = byteBuffer.getFloat()
        Float f3 = byteBuffer.getFloat()
        Float f4 = byteBuffer.getFloat()
        Float f5 = 1.0f - (((f2 * f2) + (f3 * f3)) + (f4 * f4))
        if (f5 > 0.0f) {
            f = (Float) Math.sqrt((Double) f5)
        }
        return LLQuaternion(f2, f3, f4, f)
    }

    @JvmStatic
    LLQuaternion parseU16Vec3(ByteBuffer byteBuffer, Float f, Float f2) {
        return LLQuaternion(LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2), LLTersePacking.U16_to_float(byteBuffer.getShort() & 65535, f, f2))
    }

    @JvmStatic
    LLQuaternion parseU8Vec3(ByteBuffer byteBuffer, Float f, Float f2) {
        return LLQuaternion(LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2), LLTersePacking.U8_to_float(byteBuffer.get() & UnsignedBytes.MAX_VALUE, f, f2))
    }

    @JvmStatic
    LLQuaternion shortestArc(LLVector3 lLVector3, LLVector3 lLVector32) {
        LLVector3 lLVector33 = LLVector3(lLVector3)
        LLVector3 lLVector34 = LLVector3(lLVector32)
        Float normVec = lLVector33.normVec()
        Float normVec2 = lLVector34.normVec()
        if (normVec < 1.0E-7f || normVec2 < 1.0E-7f) {
            return LLQuaternion()
        }
        LLVector3 cross = LLVector3.cross(lLVector33, lLVector34)
        Float dot = lLVector33.dot(lLVector34)
        if (dot > 0.9999999f) {
            return LLQuaternion()
        }
        if (dot < -0.9999999f) {
            LLVector3 lLVector35 = LLVector3(lLVector33)
            lLVector35.mul(lLVector33.x / lLVector33.dot(lLVector33))
            LLVector3 lLVector36 = LLVector3(1.0f, 0.0f, 0.0f)
            lLVector36.sub(lLVector35)
            if (lLVector36.normVec() < 1.0E-7f) {
                lLVector36.set(0.0f, 0.0f, 1.0f)
            }
            return LLQuaternion(lLVector36.x, lLVector36.y, lLVector36.z, 0.0f)
        }
        LLQuaternion lLQuaternion = LLQuaternion()
        lLQuaternion.setQuat((Float) Math.acos((Double) dot), cross)
        return lLQuaternion
    }

    @JvmStatic
    LLQuaternion unpackFromVector3(LLVector3 lLVector3) {
        Float f = 0.0f
        Float magVecSquared = 1.0f - lLVector3.magVecSquared()
        Float f2 = lLVector3.x
        Float f3 = lLVector3.y
        Float f4 = lLVector3.z
        if (magVecSquared > 0.0f) {
            f = (Float) Math.sqrt((Double) magVecSquared)
        }
        return LLQuaternion(f2, f3, f4, f)
    }

    fun addMul(LLQuaternion lLQuaternion, Float f) {
        this.x += lLQuaternion.x * f
        this.y += lLQuaternion.y * f
        this.z += lLQuaternion.z * f
        this.w += lLQuaternion.w * f
    }

    public LLQuaternion conjQuat() {
        return LLQuaternion(this.x * -1.0f, this.y * -1.0f, this.z * -1.0f, this.w)
    }

    public Float getAngleAxis(LLVector3 lLVector3) {
        Float f = -1.0f
        Float f2 = this.w
        if (f2 > 1.0f) {
            f2 = 1.0f
        }
        if (f2 >= -1.0f) {
            f = f2
        }
        Float sqrt = (Float) Math.sqrt((Double) (1.0f - (f * f)))
        Float f3 = Math.abs(sqrt) < 5.0E-4f ? 1.0f : 1.0f / sqrt
        Float acos = ((Float) Math.acos((Double) f)) * 2.0f
        if (acos > 3.1415927f) {
            lLVector3.x = (-this.x) * f3
            lLVector3.y = (-this.y) * f3
            lLVector3.z = f3 * (-this.z)
            return 6.2831855f - acos
        }
        lLVector3.x = this.x * f3
        lLVector3.y = this.y * f3
        lLVector3.z = f3 * this.z
        return acos
    }

    fun getInverseMatrix(Float[] fArr, Int i) {
        Float f = this.x * this.x
        Float f2 = this.y * this.y
        Float f3 = this.z * this.z
        Float f4 = (-this.x) * (-this.y)
        Float f5 = (-this.x) * (-this.z)
        Float f6 = (-this.y) * (-this.z)
        Float f7 = this.w * (-this.x)
        Float f8 = this.w * (-this.y)
        Float f9 = this.w * (-this.z)
        fArr[i + 0] = 1.0f - ((f2 + f3) * 2.0f)
        fArr[i + 1] = (f4 - f9) * 2.0f
        fArr[i + 2] = (f5 + f8) * 2.0f
        fArr[i + 3] = 0.0f
        fArr[i + 4] = (f4 + f9) * 2.0f
        fArr[i + 5] = 1.0f - ((f3 + f) * 2.0f)
        fArr[i + 6] = (f6 - f7) * 2.0f
        fArr[i + 7] = 0.0f
        fArr[i + 8] = (f5 - f8) * 2.0f
        fArr[i + 9] = (f6 + f7) * 2.0f
        fArr[i + 10] = 1.0f - ((f + f2) * 2.0f)
        fArr[i + 11] = 0.0f
        fArr[i + 12] = 0.0f
        fArr[i + 13] = 0.0f
        fArr[i + 14] = 0.0f
        fArr[i + 15] = 1.0f
    }

    public Float[] getInverseMatrix() {
        if (this.inverseMatrix != null) {
            return this.inverseMatrix
        }
        Float f = this.x * this.x
        Float f2 = this.y * this.y
        Float f3 = this.z * this.z
        Float f4 = (-this.x) * (-this.y)
        Float f5 = (-this.x) * (-this.z)
        Float f6 = (-this.y) * (-this.z)
        Float f7 = this.w * (-this.x)
        Float f8 = this.w * (-this.y)
        Float f9 = this.w * (-this.z)
        this.inverseMatrix = Float[]{1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f, (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f, (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}
        return this.inverseMatrix
    }

    public Float[] getMatrix() {
        if (this.matrix != null) {
            return this.matrix
        }
        Float f = this.x * this.x
        Float f2 = this.y * this.y
        Float f3 = this.z * this.z
        Float f4 = this.x * this.y
        Float f5 = this.x * this.z
        Float f6 = this.y * this.z
        Float f7 = this.w * this.x
        Float f8 = this.w * this.y
        Float f9 = this.w * this.z
        this.matrix = Float[]{1.0f - ((f2 + f3) * 2.0f), (f4 - f9) * 2.0f, (f5 + f8) * 2.0f, 0.0f, (f4 + f9) * 2.0f, 1.0f - ((f3 + f) * 2.0f), (f6 - f7) * 2.0f, 0.0f, (f5 - f8) * 2.0f, (f6 + f7) * 2.0f, 1.0f - ((f + f2) * 2.0f), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}
        return this.matrix
    }

    public Float normalize() {
        Float sqrt = (Float) Math.sqrt((Double) ((this.x * this.x) + (this.y * this.y) + (this.z * this.z) + (this.w * this.w)))
        if (sqrt <= 1.0E-7f) {
            this.x = 0.0f
            this.y = 0.0f
            this.z = 0.0f
            this.w = 1.0f
        } else if (Math.abs(1.0f - sqrt) > 1.0E-6f) {
            Float f = 1.0f / sqrt
            this.x *= f
            this.y *= f
            this.z *= f
            this.w = f * this.w
        }
        this.matrix = null
        this.inverseMatrix = null
        return sqrt
    }

    fun set(LLQuaternion lLQuaternion) {
        this.x = lLQuaternion.x
        this.y = lLQuaternion.y
        this.z = lLQuaternion.z
        this.w = lLQuaternion.w
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setIdentity() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 1.0f
    }

    fun setLerp(LLQuaternion lLQuaternion, Float f, LLQuaternion lLQuaternion2, Float f2) {
        this.x = (lLQuaternion.x * f) + (lLQuaternion2.x * f2)
        this.y = (lLQuaternion.y * f) + (lLQuaternion2.y * f2)
        this.z = (lLQuaternion.z * f) + (lLQuaternion2.z * f2)
        this.w = (lLQuaternion.w * f) + (lLQuaternion2.w * f2)
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setMul(LLQuaternion lLQuaternion, LLQuaternion lLQuaternion2) {
        this.x = (((lLQuaternion2.w * lLQuaternion.x) + (lLQuaternion2.x * lLQuaternion.w)) + (lLQuaternion2.y * lLQuaternion.z)) - (lLQuaternion2.z * lLQuaternion.y)
        this.y = (((lLQuaternion2.w * lLQuaternion.y) + (lLQuaternion2.y * lLQuaternion.w)) + (lLQuaternion2.z * lLQuaternion.x)) - (lLQuaternion2.x * lLQuaternion.z)
        this.z = (((lLQuaternion2.w * lLQuaternion.z) + (lLQuaternion2.z * lLQuaternion.w)) + (lLQuaternion2.x * lLQuaternion.y)) - (lLQuaternion2.y * lLQuaternion.x)
        this.w = (((lLQuaternion2.w * lLQuaternion.w) - (lLQuaternion2.x * lLQuaternion.x)) - (lLQuaternion2.y * lLQuaternion.y)) - (lLQuaternion2.z * lLQuaternion.z)
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setQuat(Float f, Float f2, Float f3, Float f4) {
        LLVector3 lLVector3 = LLVector3(f2, f3, f4)
        lLVector3.normVec()
        Float f5 = 0.5f * f
        Float sin = (Float) Math.sin((Double) f5)
        this.x = lLVector3.x * sin
        this.y = lLVector3.y * sin
        this.z = lLVector3.z * sin
        this.w = (Float) Math.cos((Double) f5)
        normalize()
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setQuat(Float f, LLVector3 lLVector3) {
        LLVector3 lLVector32 = LLVector3(lLVector3)
        lLVector32.normVec()
        Float f2 = 0.5f * f
        Float sin = (Float) Math.sin((Double) f2)
        this.x = lLVector32.x * sin
        this.y = lLVector32.y * sin
        this.z = lLVector32.z * sin
        this.w = (Float) Math.cos((Double) f2)
        normalize()
        this.matrix = null
        this.inverseMatrix = null
    }

    fun setRaw(Float f, Float f2, Float f3, Float f4) {
        this.x = f
        this.y = f2
        this.z = f3
        this.w = f4
    }

    fun setZero() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.w = 0.0f
    }

    public String toString() {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.w)})
    }
}
