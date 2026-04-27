// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLVector3

public class ImmutableVector
{

    public final float x;
    public final float y;
    public final float z;

    public ImmutableVector(float f, float f1, float f2)
    {
        x = f;
        y = f1;
        z = f2;
    }

    public ImmutableVector(LLVector3 llvector3)
    {
        x = llvector3.x;
        y = llvector3.y;
        z = llvector3.z;
    }

    public float distanceTo(float f, float f1, float f2)
    {
        f -= x;
        f1 -= y;
        f2 -= z;
        return (float)Math.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public float distanceTo(ImmutableVector immutablevector)
    {
        if (immutablevector == null)
        {
            return (0.0F / 0.0F);
        } else
        {
            float f = x - immutablevector.x;
            float f1 = y - immutablevector.y;
            float f2 = z - immutablevector.z;
            return (float)Math.sqrt(f * f + f1 * f1 + f2 * f2);
        }
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof ImmutableVector)
        {
            obj = (ImmutableVector)obj;
            return x == ((ImmutableVector) (obj)).x && y == ((ImmutableVector) (obj)).y && z == ((ImmutableVector) (obj)).z;
        } else
        {
            return false;
        }
    }

    public float getDistanceTo(LLVector3 llvector3)
    {
        if (llvector3 == null)
        {
            return (0.0F / 0.0F);
        } else
        {
            float f = x - llvector3.x;
            float f1 = y - llvector3.y;
            float f2 = z - llvector3.z;
            return (float)Math.sqrt(f * f + f1 * f1 + f2 * f2);
        }
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

    public int hashCode()
    {
        return Float.floatToRawIntBits(x) + Float.floatToRawIntBits(y) + Float.floatToRawIntBits(z);
    }

    public String toString()
    {
        return String.format("(%f,%f,%f)", new Object[] {
            Float.valueOf(x), Float.valueOf(y), Float.valueOf(z)
        });
    }
}
