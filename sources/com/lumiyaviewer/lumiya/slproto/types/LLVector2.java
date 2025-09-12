// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


public class LLVector2
{

    public static final float FP_MAG_THRESHOLD = 1E-07F;
    public float x;
    public float y;

    public LLVector2()
    {
        x = 0.0F;
        y = 0.0F;
    }

    public LLVector2(float f, float f1)
    {
        x = f;
        y = f1;
    }

    public LLVector2(LLVector2 llvector2)
    {
        x = llvector2.x;
        y = llvector2.y;
    }

    public static LLVector2 sub(LLVector2 llvector2, LLVector2 llvector2_1)
    {
        return new LLVector2(llvector2.x - llvector2_1.x, llvector2.y - llvector2_1.y);
    }

    public static LLVector2 sum(LLVector2 llvector2, LLVector2 llvector2_1)
    {
        return new LLVector2(llvector2.x + llvector2_1.x, llvector2.y + llvector2_1.y);
    }

    public void add(LLVector2 llvector2)
    {
        x = x + llvector2.x;
        y = y + llvector2.y;
    }

    public float dot(LLVector2 llvector2)
    {
        return x * llvector2.x + y * llvector2.y;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof LLVector2))
        {
            return false;
        }
        obj = (LLVector2)obj;
        return x == ((LLVector2) (obj)).x && y == ((LLVector2) (obj)).y;
    }

    public int hashCode()
    {
        return Float.floatToIntBits(x) + Float.floatToIntBits(y);
    }

    public float magVec()
    {
        return (float)Math.sqrt(x * x + y * y);
    }

    public void mul(float f)
    {
        x = x * f;
        y = y * f;
    }

    public float normVec()
    {
        float f = (float)Math.sqrt(x * x + y * y);
        if (f > 1E-07F)
        {
            float f1 = 1.0F / f;
            x = x * f1;
            y = f1 * y;
            return f;
        } else
        {
            x = 0.0F;
            y = 0.0F;
            return f;
        }
    }

    public void set(float f, float f1)
    {
        x = f;
        y = f1;
    }

    public void setMax(LLVector2 llvector2)
    {
        x = Math.max(x, llvector2.x);
        y = Math.max(y, llvector2.y);
    }

    public void setMin(LLVector2 llvector2)
    {
        x = Math.min(x, llvector2.x);
        y = Math.min(y, llvector2.y);
    }

    public String toString()
    {
        return String.format("(%f, %f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y)
        });
    }
}
