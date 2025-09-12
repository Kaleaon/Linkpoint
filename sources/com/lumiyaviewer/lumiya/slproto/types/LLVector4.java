// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLVector3

public class LLVector4
{

    public static final float FP_MAG_THRESHOLD = 1E-07F;
    public float w;
    public float x;
    public float y;
    public float z;

    public LLVector4()
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 0.0F;
    }

    public LLVector4(float f, float f1, float f2)
    {
        x = f;
        y = f1;
        z = f2;
        w = 0.0F;
    }

    public LLVector4(float f, float f1, float f2, float f3)
    {
        x = f;
        y = f1;
        z = f2;
        w = f3;
    }

    public LLVector4(LLVector3 llvector3)
    {
        x = llvector3.x;
        y = llvector3.y;
        z = llvector3.z;
        w = 0.0F;
    }

    public LLVector4(LLVector4 llvector4)
    {
        x = llvector4.x;
        y = llvector4.y;
        z = llvector4.z;
        w = llvector4.w;
    }

    public static LLVector4 add(LLVector4 llvector4, LLVector4 llvector4_1)
    {
        return new LLVector4(llvector4.x + llvector4_1.x, llvector4.y + llvector4_1.y, llvector4.z + llvector4_1.z, llvector4.w + llvector4_1.w);
    }

    public static LLVector4 cross3(LLVector4 llvector4, LLVector4 llvector4_1)
    {
        return new LLVector4(llvector4.y * llvector4_1.z - llvector4.z * llvector4_1.y, llvector4.z * llvector4_1.x - llvector4.x * llvector4_1.z, llvector4.x * llvector4_1.y - llvector4.y * llvector4_1.x, 0.0F);
    }

    public static LLVector4 sub(LLVector4 llvector4, LLVector4 llvector4_1)
    {
        return new LLVector4(llvector4.x - llvector4_1.x, llvector4.y - llvector4_1.y, llvector4.z - llvector4_1.z, llvector4.w - llvector4_1.w);
    }

    public void add(LLVector4 llvector4)
    {
        x = x + llvector4.x;
        y = y + llvector4.y;
        z = z + llvector4.z;
        w = w + llvector4.w;
    }

    public void clear()
    {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 0.0F;
    }

    public float dot3(LLVector4 llvector4)
    {
        return x * llvector4.x + y * llvector4.y + z * llvector4.z;
    }

    public void mul(float f)
    {
        x = x * f;
        y = y * f;
        z = z * f;
        w = w * f;
    }

    public float normalize3()
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
        w = 0.0F;
    }

    public void set(LLVector4 llvector4)
    {
        x = llvector4.x;
        y = llvector4.y;
        z = llvector4.z;
        w = llvector4.w;
    }

    public void setMax(LLVector4 llvector4)
    {
        x = Math.max(x, llvector4.x);
        y = Math.max(y, llvector4.y);
        z = Math.max(z, llvector4.z);
        w = Math.max(w, llvector4.w);
    }

    public void setMin(LLVector4 llvector4)
    {
        x = Math.min(x, llvector4.x);
        y = Math.min(y, llvector4.y);
        z = Math.min(z, llvector4.z);
        w = Math.min(w, llvector4.w);
    }

    public String toString()
    {
        return String.format("(%f, %f, %f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y), Float.valueOf(z)
        });
    }
}
