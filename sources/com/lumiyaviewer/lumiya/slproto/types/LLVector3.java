// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLTersePacking, ImmutableVector, LLQuaternion

public class LLVector3
{

    public static final float FP_MAG_THRESHOLD = 1E-07F;
    public static final LLVector3 Zero = new LLVector3(0.0F, 0.0F, 0.0F);
    public static final LLVector3 z_axis = new LLVector3(0.0F, 0.0F, 1.0F);
    public float x;
    public float y;
    public float z;

    public LLVector3()
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
    }

    public LLVector3(float f, float f1, float f2)
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        x = f;
        y = f1;
        z = f2;
    }

    public LLVector3(LLVector3 llvector3)
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        x = llvector3.x;
        y = llvector3.y;
        z = llvector3.z;
    }

    public static LLVector3 cross(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        return new LLVector3(llvector3.y * llvector3_1.z - llvector3_1.y * llvector3.z, llvector3.z * llvector3_1.x - llvector3_1.z * llvector3.x, llvector3.x * llvector3_1.y - llvector3_1.x * llvector3.y);
    }

    public static LLVector3 lerp(LLVector3 llvector3, LLVector3 llvector3_1, float f)
    {
        return new LLVector3(llvector3.x + (llvector3_1.x - llvector3.x) * f, llvector3.y + (llvector3_1.y - llvector3.y) * f, llvector3.z + (llvector3_1.z - llvector3.z) * f);
    }

    public static LLVector3 parseFloatVec(ByteBuffer bytebuffer)
    {
        return new LLVector3(bytebuffer.getFloat(), bytebuffer.getFloat(), bytebuffer.getFloat());
    }

    public static LLVector3 parseU16Vec(ByteBuffer bytebuffer, float f, float f1, float f2, float f3)
    {
        return new LLVector3(LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1), LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f, f1), LLTersePacking.U16_to_float(bytebuffer.getShort() & 0xffff, f2, f3));
    }

    public static LLVector3 parseU8Vec(ByteBuffer bytebuffer, float f, float f1, float f2, float f3)
    {
        return new LLVector3(LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1), LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f, f1), LLTersePacking.U8_to_float(bytebuffer.get() & 0xff, f2, f3));
    }

    public static LLVector3 scaleFromMatrix(float af[])
    {
        return new LLVector3((float)Math.sqrt(af[0] * af[0] + af[1] * af[1] + af[2] * af[2]), (float)Math.sqrt(af[4] * af[4] + af[5] * af[5] + af[6] * af[6]), (float)Math.sqrt(af[8] * af[8] + af[9] * af[9] + af[10] * af[10]));
    }

    public static LLVector3 sub(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        return new LLVector3(llvector3.x - llvector3_1.x, llvector3.y - llvector3_1.y, llvector3.z - llvector3_1.z);
    }

    public void add(LLVector3 llvector3)
    {
        x = x + llvector3.x;
        y = y + llvector3.y;
        z = z + llvector3.z;
    }

    public void addMul(ImmutableVector immutablevector, float f)
    {
        x = x + immutablevector.x * f;
        y = y + immutablevector.y * f;
        z = z + immutablevector.z * f;
    }

    public void addMul(LLVector3 llvector3, float f)
    {
        x = x + llvector3.x * f;
        y = y + llvector3.y * f;
        z = z + llvector3.z * f;
    }

    public float dot(LLVector3 llvector3)
    {
        return x * llvector3.x + y * llvector3.y + z * llvector3.z;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof LLVector3))
        {
            return false;
        }
        obj = (LLVector3)obj;
        return x == ((LLVector3) (obj)).x && y == ((LLVector3) (obj)).y && z == ((LLVector3) (obj)).z;
    }

    public float getDistanceTo(LLVector3 llvector3)
    {
        float f = x - llvector3.x;
        float f1 = y - llvector3.y;
        float f2 = z - llvector3.z;
        return (float)Math.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public float getMax()
    {
        return Math.max(Math.max(x, y), z);
    }

    public LLVector3 getRotatedOffset(float f, float f1)
    {
        f1 = (3.141593F * f1) / 180F;
        return new LLVector3((float)Math.cos(f1) * f + x, (float)Math.sin(f1) * f + y, z);
    }

    public int hashCode()
    {
        return Float.floatToIntBits(x) + Float.floatToIntBits(y) + Float.floatToIntBits(z);
    }

    public boolean isZero()
    {
        boolean flag1 = false;
        boolean flag = flag1;
        if (x == 0.0F)
        {
            flag = flag1;
            if (y == 0.0F)
            {
                flag = flag1;
                if (z == 0.0F)
                {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public float magVec()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float magVecSquared()
    {
        return x * x + y * y + z * z;
    }

    public void mul(float f)
    {
        x = x * f;
        y = y * f;
        z = z * f;
    }

    public void mul(LLQuaternion llquaternion)
    {
        float f = -llquaternion.x * x - llquaternion.y * y - llquaternion.z * z;
        float f1 = (llquaternion.w * x + llquaternion.y * z) - llquaternion.z * y;
        float f2 = (llquaternion.w * y + llquaternion.z * x) - llquaternion.x * z;
        float f3 = (llquaternion.w * z + llquaternion.x * y) - llquaternion.y * x;
        x = ((-f * llquaternion.x + llquaternion.w * f1) - llquaternion.z * f2) + llquaternion.y * f3;
        y = ((-f * llquaternion.y + llquaternion.w * f2) - llquaternion.x * f3) + llquaternion.z * f1;
        z = ((-f * llquaternion.z + f3 * llquaternion.w) - f1 * llquaternion.y) + llquaternion.x * f2;
    }

    public void mul(LLVector3 llvector3)
    {
        x = x * llvector3.x;
        y = y * llvector3.y;
        z = z * llvector3.z;
    }

    public void mulWeighted(ImmutableVector immutablevector, float f)
    {
        x = x * (immutablevector.x * f + 1.0F);
        y = y * (immutablevector.y * f + 1.0F);
        z = z * (immutablevector.z * f + 1.0F);
    }

    public void mulWeighted(LLVector3 llvector3, float f)
    {
        x = x * (llvector3.x * f + 1.0F);
        y = y * (llvector3.y * f + 1.0F);
        z = z * (llvector3.z * f + 1.0F);
    }

    public float normVec()
    {
        float f = (float)Math.sqrt(x * x + y * y + z * z);
        if (f > 1E-07F)
        {
            float f1 = 1.0F / f;
            x = x * f1;
            y = y * f1;
            z = f1 * z;
            return f;
        } else
        {
            x = 0.0F;
            y = 0.0F;
            z = 0.0F;
            return f;
        }
    }

    public void set(float f, float f1, float f2)
    {
        x = f;
        y = f1;
        z = f2;
    }

    public void set(LLVector3 llvector3)
    {
        if (llvector3 != null)
        {
            x = llvector3.x;
            y = llvector3.y;
            z = llvector3.z;
        }
    }

    public void setAdd(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        x = llvector3.x + llvector3_1.x;
        y = llvector3.y + llvector3_1.y;
        z = llvector3.z + llvector3_1.z;
    }

    public void setCross(LLVector3 llvector3)
    {
        float f = y;
        float f1 = llvector3.z;
        float f2 = llvector3.y;
        float f3 = z;
        float f4 = z;
        float f5 = llvector3.x;
        float f6 = llvector3.z;
        float f7 = x;
        float f8 = x;
        float f9 = llvector3.y;
        float f10 = llvector3.x;
        float f11 = y;
        x = f * f1 - f2 * f3;
        y = f4 * f5 - f6 * f7;
        z = f8 * f9 - f10 * f11;
    }

    public void setLerp(LLVector3 llvector3, float f, LLVector3 llvector3_1, float f1)
    {
        x = llvector3.x * f + llvector3_1.x * f1;
        y = llvector3.y * f + llvector3_1.y * f1;
        z = llvector3.z * f + llvector3_1.z * f1;
    }

    public void setLerp(LLVector3 llvector3, LLVector3 llvector3_1, float f)
    {
        x = llvector3.x + (llvector3_1.x - llvector3.x) * f;
        y = llvector3.y + (llvector3_1.y - llvector3.y) * f;
        z = llvector3.z + (llvector3_1.z - llvector3.z) * f;
    }

    public void setMul(LLVector3 llvector3, float f)
    {
        x = llvector3.x * f;
        y = llvector3.y * f;
        z = llvector3.z * f;
    }

    public void setMul(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        x = llvector3.x * llvector3_1.x;
        y = llvector3.y * llvector3_1.y;
        z = llvector3.z * llvector3_1.z;
    }

    public void setSub(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        x = llvector3.x - llvector3_1.x;
        y = llvector3.y - llvector3_1.y;
        z = llvector3.z - llvector3_1.z;
    }

    public void sub(LLVector3 llvector3)
    {
        x = x - llvector3.x;
        y = y - llvector3.y;
        z = z - llvector3.z;
    }

    public LLSDNode toLLSD()
    {
        return new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("X", new LLSDDouble(x)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("Y", new LLSDDouble(y)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("Z", new LLSDDouble(z))
        });
    }

    public String toString()
    {
        return String.format("(%f, %f, %f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y), Float.valueOf(z)
        });
    }

}
