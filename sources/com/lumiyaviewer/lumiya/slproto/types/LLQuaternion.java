// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;

import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLVector3, LLTersePacking

public class LLQuaternion
{
    public static final class Order extends Enum
    {

        private static final Order $VALUES[];
        public static final Order XYZ;
        public static final Order XZY;
        public static final Order YXZ;
        public static final Order YZX;
        public static final Order ZXY;
        public static final Order ZYX;

        public static Order valueOf(String s)
        {
            return (Order)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order, s);
        }

        public static Order[] values()
        {
            return $VALUES;
        }

        static 
        {
            XYZ = new Order("XYZ", 0);
            YZX = new Order("YZX", 1);
            ZXY = new Order("ZXY", 2);
            XZY = new Order("XZY", 3);
            YXZ = new Order("YXZ", 4);
            ZYX = new Order("ZYX", 5);
            $VALUES = (new Order[] {
                XYZ, YZX, ZXY, XZY, YXZ, ZYX
            });
        }

        private Order(String s, int i)
        {
            super(s, i);
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues[];
    public static final float FP_MAG_THRESHOLD = 1E-07F;
    private float inverseMatrix[];
    private float matrix[];
    public float w;
    public float x;
    public float y;
    public float z;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues;
        }
        int ai[] = new int[Order.values().length];
        try
        {
            ai[Order.XYZ.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[Order.XZY.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[Order.YXZ.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[Order.YZX.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[Order.ZXY.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[Order.ZYX.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues = ai;
        return ai;
    }

    public LLQuaternion()
    {
        matrix = null;
        inverseMatrix = null;
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 1.0F;
    }

    public LLQuaternion(float f, float f1, float f2, float f3)
    {
        matrix = null;
        inverseMatrix = null;
        x = f;
        y = f1;
        z = f2;
        w = f3;
    }

    public LLQuaternion(LLQuaternion llquaternion)
    {
        matrix = null;
        inverseMatrix = null;
        x = llquaternion.x;
        y = llquaternion.y;
        z = llquaternion.z;
        w = llquaternion.w;
    }

    public LLQuaternion(float af[])
    {
        matrix = null;
        inverseMatrix = null;
        float f = af[0] + 1.0F + af[5] + af[10];
        if (f > 0.5F)
        {
            f = (float)(Math.sqrt(f) * 2D);
            x = (af[9] - af[6]) / f;
            y = (af[2] - af[8]) / f;
            z = (af[4] - af[1]) / f;
            w = f * 0.25F;
            return;
        }
        if (af[0] > af[5] && af[0] > af[10])
        {
            float f1 = (float)(Math.sqrt((af[0] + 1.0F) - af[5] - af[10]) * 2D);
            x = 0.25F * f1;
            y = (af[4] + af[1]) / f1;
            z = (af[2] + af[8]) / f1;
            w = (af[9] - af[6]) / f1;
            return;
        }
        if (af[5] > af[10])
        {
            float f2 = (float)(Math.sqrt((af[5] + 1.0F) - af[0] - af[10]) * 2D);
            x = (af[4] + af[1]) / f2;
            y = 0.25F * f2;
            z = (af[9] + af[6]) / f2;
            w = (af[2] - af[8]) / f2;
            return;
        } else
        {
            float f3 = (float)(Math.sqrt((af[10] + 1.0F) - af[0] - af[5]) * 2D);
            x = (af[2] + af[8]) / f3;
            y = (af[9] + af[6]) / f3;
            z = 0.25F * f3;
            w = (af[4] - af[1]) / f3;
            return;
        }
    }

    public static LLQuaternion fromEuler(float f, float f1, float f2)
    {
        double d = Math.cos(f / 2.0F);
        double d1 = Math.sin(f / 2.0F);
        double d2 = Math.cos(f1 / 2.0F);
        double d3 = Math.sin(f1 / 2.0F);
        double d4 = Math.cos(f2 / 2.0F);
        double d5 = Math.sin(f2 / 2.0F);
        double d6 = d * d2;
        double d7 = d1 * d3;
        return new LLQuaternion((float)(d1 * d2 * d4 + d * d3 * d5), (float)(d * d3 * d4 - d1 * d2 * d5), (float)(d6 * d5 + d7 * d4), (float)(d6 * d4 - d7 * d5));
    }

    public static LLQuaternion lerp(LLQuaternion llquaternion, LLQuaternion llquaternion1, float f)
    {
        return new LLQuaternion(llquaternion.x + (llquaternion1.x - llquaternion.x) * f, llquaternion.y + (llquaternion1.y - llquaternion.y) * f, llquaternion.z + (llquaternion1.z - llquaternion.z) * f, llquaternion.w + (llquaternion1.w - llquaternion.w) * f);
    }

    public static LLQuaternion mayaQ(float f, float f1, float f2, Order order)
    {
        LLQuaternion llquaternion = new LLQuaternion();
        LLQuaternion llquaternion1 = new LLQuaternion();
        LLQuaternion llquaternion2 = new LLQuaternion();
        llquaternion.setQuat(f * 0.01745329F, new LLVector3(1.0F, 0.0F, 0.0F));
        llquaternion1.setQuat(f1 * 0.01745329F, new LLVector3(0.0F, 1.0F, 0.0F));
        llquaternion2.setQuat(f2 * 0.01745329F, new LLVector3(0.0F, 0.0F, 1.0F));
        LLQuaternion llquaternion3 = new LLQuaternion();
        LLQuaternion llquaternion4 = new LLQuaternion();
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_types_2D_LLQuaternion$OrderSwitchesValues()[order.ordinal()])
        {
        default:
            return llquaternion4;

        case 1: // '\001'
            llquaternion3.setMul(llquaternion, llquaternion1);
            llquaternion4.setMul(llquaternion3, llquaternion2);
            return llquaternion4;

        case 4: // '\004'
            llquaternion3.setMul(llquaternion1, llquaternion2);
            llquaternion4.setMul(llquaternion3, llquaternion);
            return llquaternion4;

        case 5: // '\005'
            llquaternion3.setMul(llquaternion2, llquaternion);
            llquaternion4.setMul(llquaternion3, llquaternion1);
            return llquaternion4;

        case 2: // '\002'
            llquaternion3.setMul(llquaternion, llquaternion2);
            llquaternion4.setMul(llquaternion3, llquaternion1);
            return llquaternion4;

        case 3: // '\003'
            llquaternion3.setMul(llquaternion1, llquaternion);
            llquaternion4.setMul(llquaternion3, llquaternion2);
            return llquaternion4;

        case 6: // '\006'
            llquaternion3.setMul(llquaternion2, llquaternion1);
            break;
        }
        llquaternion4.setMul(llquaternion3, llquaternion);
        return llquaternion4;
    }

    public static LLQuaternion parseFloatVec3(ByteBuffer bytebuffer)
    {
        float f = 0.0F;
        float f1 = bytebuffer.getFloat();
        float f2 = bytebuffer.getFloat();
        float f3 = bytebuffer.getFloat();
        float f4 = 1.0F - (f1 * f1 + f2 * f2 + f3 * f3);
        if (f4 > 0.0F)
        {
            f = (float)Math.sqrt(f4);
        }
        return new LLQuaternion(f1, f2, f3, f);
    }

    public static LLQuaternion parseU16Vec3(ByteBuffer bytebuffer, float f, float f1)
    {
        return new LLQuaternion(LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1), LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1), LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1), LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1));
    }

    public static LLQuaternion parseU8Vec3(ByteBuffer bytebuffer, float f, float f1)
    {
        return new LLQuaternion(LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1), LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1), LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1), LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1));
    }

    public static LLQuaternion shortestArc(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        llvector3 = new LLVector3(llvector3);
        LLVector3 llvector3_2 = new LLVector3(llvector3_1);
        float f = llvector3.normVec();
        float f1 = llvector3_2.normVec();
        if (f < 1E-07F || f1 < 1E-07F)
        {
            return new LLQuaternion();
        }
        llvector3_1 = LLVector3.cross(llvector3, llvector3_2);
        f = llvector3.dot(llvector3_2);
        if (f > 0.9999999F)
        {
            return new LLQuaternion();
        }
        if (f < -0.9999999F)
        {
            llvector3_1 = new LLVector3(llvector3);
            llvector3_1.mul(llvector3.x / llvector3.dot(llvector3));
            llvector3 = new LLVector3(1.0F, 0.0F, 0.0F);
            llvector3.sub(llvector3_1);
            if (llvector3.normVec() < 1E-07F)
            {
                llvector3.set(0.0F, 0.0F, 1.0F);
            }
            return new LLQuaternion(llvector3.x, llvector3.y, llvector3.z, 0.0F);
        } else
        {
            f = (float)Math.acos(f);
            llvector3 = new LLQuaternion();
            llvector3.setQuat(f, llvector3_1);
            return llvector3;
        }
    }

    public static LLQuaternion unpackFromVector3(LLVector3 llvector3)
    {
        float f = 0.0F;
        float f4 = 1.0F - llvector3.magVecSquared();
        float f1 = llvector3.x;
        float f2 = llvector3.y;
        float f3 = llvector3.z;
        if (f4 > 0.0F)
        {
            f = (float)Math.sqrt(f4);
        }
        return new LLQuaternion(f1, f2, f3, f);
    }

    public void addMul(LLQuaternion llquaternion, float f)
    {
        x = x + llquaternion.x * f;
        y = y + llquaternion.y * f;
        z = z + llquaternion.z * f;
        w = w + llquaternion.w * f;
    }

    public LLQuaternion conjQuat()
    {
        return new LLQuaternion(x * -1F, y * -1F, z * -1F, w);
    }

    public float getAngleAxis(LLVector3 llvector3)
    {
        float f1 = -1F;
        float f2 = w;
        float f = f2;
        if (f2 > 1.0F)
        {
            f = 1.0F;
        }
        if (f < -1F)
        {
            f = f1;
        }
        f1 = (float)Math.sqrt(1.0F - f * f);
        if (Math.abs(f1) < 0.0005F)
        {
            f1 = 1.0F;
        } else
        {
            f1 = 1.0F / f1;
        }
        f = (float)Math.acos(f) * 2.0F;
        if (f > 3.141593F)
        {
            llvector3.x = -x * f1;
            llvector3.y = -y * f1;
            llvector3.z = f1 * -z;
            return 6.283185F - f;
        } else
        {
            llvector3.x = x * f1;
            llvector3.y = y * f1;
            llvector3.z = f1 * z;
            return f;
        }
    }

    public void getInverseMatrix(float af[], int i)
    {
        float f = x * x;
        float f1 = y * y;
        float f2 = z * z;
        float f3 = -x * -y;
        float f4 = -x * -z;
        float f5 = -y * -z;
        float f6 = w * -x;
        float f7 = w * -y;
        float f8 = w * -z;
        af[i + 0] = 1.0F - (f1 + f2) * 2.0F;
        af[i + 1] = (f3 - f8) * 2.0F;
        af[i + 2] = (f4 + f7) * 2.0F;
        af[i + 3] = 0.0F;
        af[i + 4] = (f3 + f8) * 2.0F;
        af[i + 5] = 1.0F - (f2 + f) * 2.0F;
        af[i + 6] = (f5 - f6) * 2.0F;
        af[i + 7] = 0.0F;
        af[i + 8] = (f4 - f7) * 2.0F;
        af[i + 9] = (f5 + f6) * 2.0F;
        af[i + 10] = 1.0F - (f + f1) * 2.0F;
        af[i + 11] = 0.0F;
        af[i + 12] = 0.0F;
        af[i + 13] = 0.0F;
        af[i + 14] = 0.0F;
        af[i + 15] = 1.0F;
    }

    public float[] getInverseMatrix()
    {
        if (inverseMatrix != null)
        {
            return inverseMatrix;
        } else
        {
            float f = x * x;
            float f1 = y * y;
            float f2 = z * z;
            float f3 = -x * -y;
            float f4 = -x * -z;
            float f5 = -y * -z;
            float f6 = w * -x;
            float f7 = w * -y;
            float f8 = w * -z;
            inverseMatrix = (new float[] {
                1.0F - (f1 + f2) * 2.0F, (f3 - f8) * 2.0F, (f4 + f7) * 2.0F, 0.0F, (f3 + f8) * 2.0F, 1.0F - (f2 + f) * 2.0F, (f5 - f6) * 2.0F, 0.0F, (f4 - f7) * 2.0F, (f5 + f6) * 2.0F, 
                1.0F - (f + f1) * 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
            });
            return inverseMatrix;
        }
    }

    public float[] getMatrix()
    {
        if (matrix != null)
        {
            return matrix;
        } else
        {
            float f = x * x;
            float f1 = y * y;
            float f2 = z * z;
            float f3 = x * y;
            float f4 = x * z;
            float f5 = y * z;
            float f6 = w * x;
            float f7 = w * y;
            float f8 = w * z;
            matrix = (new float[] {
                1.0F - (f1 + f2) * 2.0F, (f3 - f8) * 2.0F, (f4 + f7) * 2.0F, 0.0F, (f3 + f8) * 2.0F, 1.0F - (f2 + f) * 2.0F, (f5 - f6) * 2.0F, 0.0F, (f4 - f7) * 2.0F, (f5 + f6) * 2.0F, 
                1.0F - (f + f1) * 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
            });
            return matrix;
        }
    }

    public float normalize()
    {
        float f = (float)Math.sqrt(x * x + y * y + z * z + w * w);
        if (f > 1E-07F)
        {
            if (Math.abs(1.0F - f) > 1E-06F)
            {
                float f1 = 1.0F / f;
                x = x * f1;
                y = y * f1;
                z = z * f1;
                w = f1 * w;
            }
        } else
        {
            x = 0.0F;
            y = 0.0F;
            z = 0.0F;
            w = 1.0F;
        }
        matrix = null;
        inverseMatrix = null;
        return f;
    }

    public void set(LLQuaternion llquaternion)
    {
        x = llquaternion.x;
        y = llquaternion.y;
        z = llquaternion.z;
        w = llquaternion.w;
        matrix = null;
        inverseMatrix = null;
    }

    public void setIdentity()
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 1.0F;
    }

    public void setLerp(LLQuaternion llquaternion, float f, LLQuaternion llquaternion1, float f1)
    {
        x = llquaternion.x * f + llquaternion1.x * f1;
        y = llquaternion.y * f + llquaternion1.y * f1;
        z = llquaternion.z * f + llquaternion1.z * f1;
        w = llquaternion.w * f + llquaternion1.w * f1;
        matrix = null;
        inverseMatrix = null;
    }

    public void setMul(LLQuaternion llquaternion, LLQuaternion llquaternion1)
    {
        x = (llquaternion1.w * llquaternion.x + llquaternion1.x * llquaternion.w + llquaternion1.y * llquaternion.z) - llquaternion1.z * llquaternion.y;
        y = (llquaternion1.w * llquaternion.y + llquaternion1.y * llquaternion.w + llquaternion1.z * llquaternion.x) - llquaternion1.x * llquaternion.z;
        z = (llquaternion1.w * llquaternion.z + llquaternion1.z * llquaternion.w + llquaternion1.x * llquaternion.y) - llquaternion1.y * llquaternion.x;
        w = llquaternion1.w * llquaternion.w - llquaternion1.x * llquaternion.x - llquaternion1.y * llquaternion.y - llquaternion1.z * llquaternion.z;
        matrix = null;
        inverseMatrix = null;
    }

    public void setQuat(float f, float f1, float f2, float f3)
    {
        LLVector3 llvector3 = new LLVector3(f1, f2, f3);
        llvector3.normVec();
        f1 = 0.5F * f;
        f = (float)Math.cos(f1);
        f1 = (float)Math.sin(f1);
        x = llvector3.x * f1;
        y = llvector3.y * f1;
        z = llvector3.z * f1;
        w = f;
        normalize();
        matrix = null;
        inverseMatrix = null;
    }

    public void setQuat(float f, LLVector3 llvector3)
    {
        llvector3 = new LLVector3(llvector3);
        llvector3.normVec();
        float f1 = 0.5F * f;
        f = (float)Math.cos(f1);
        f1 = (float)Math.sin(f1);
        x = llvector3.x * f1;
        y = llvector3.y * f1;
        z = llvector3.z * f1;
        w = f;
        normalize();
        matrix = null;
        inverseMatrix = null;
    }

    public void setRaw(float f, float f1, float f2, float f3)
    {
        x = f;
        y = f1;
        z = f2;
        w = f3;
    }

    public void setZero()
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 0.0F;
    }

    public String toString()
    {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y), Float.valueOf(z), Float.valueOf(w)
        });
    }
}
