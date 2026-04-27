// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import java.nio.ByteBuffer;

public class PrimProfileParams
{

    public static final float CUT_QUANTA = 2E-05F;
    public static final float HOLLOW_QUANTA = 2E-05F;
    public static final byte LL_PCODE_HOLE_CIRCLE = 16;
    public static final byte LL_PCODE_HOLE_MASK = -16;
    public static final byte LL_PCODE_HOLE_SAME = 0;
    public static final byte LL_PCODE_HOLE_SQUARE = 32;
    public static final byte LL_PCODE_HOLE_TRIANGLE = 48;
    public static final byte LL_PCODE_PROFILE_CIRCLE = 0;
    public static final byte LL_PCODE_PROFILE_CIRCLE_HALF = 5;
    public static final byte LL_PCODE_PROFILE_EQUALTRI = 3;
    public static final byte LL_PCODE_PROFILE_ISOTRI = 2;
    public static final byte LL_PCODE_PROFILE_MASK = 15;
    public static final byte LL_PCODE_PROFILE_RIGHTTRI = 4;
    public static final byte LL_PCODE_PROFILE_SQUARE = 1;
    public final float Begin;
    public final byte CurveType;
    public final float End;
    public final float Hollow;
    private final int hashValue = getHashValue();

    public PrimProfileParams(byte byte0, float f, float f1, float f2)
    {
        CurveType = byte0;
        Begin = f;
        End = f1;
        Hollow = f2;
    }

    public static PrimProfileParams createFromObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData objectdata)
    {
        return new PrimProfileParams((byte)objectdata.ProfileCurve, (float)(objectdata.ProfileBegin & 0xffff) * 2E-05F, 1.0F - (float)(objectdata.ProfileEnd & 0xffff) * 2E-05F, (float)(objectdata.ProfileHollow & 0xffff) * 2E-05F);
    }

    public static PrimProfileParams createFromPackedData(ByteBuffer bytebuffer)
    {
        return new PrimProfileParams(bytebuffer.get(), (float)(bytebuffer.getShort() & 0xffff) * 2E-05F, 1.0F - (float)(bytebuffer.getShort() & 0xffff) * 2E-05F, (float)(bytebuffer.getShort() & 0xffff) * 2E-05F);
    }

    private int getHashValue()
    {
        return CurveType * 17 + Float.floatToIntBits(Begin) + Float.floatToIntBits(End) + Float.floatToIntBits(Hollow);
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof PrimProfileParams))
        {
            return false;
        }
        obj = (PrimProfileParams)obj;
        return CurveType == ((PrimProfileParams) (obj)).CurveType && Begin == ((PrimProfileParams) (obj)).Begin && End == ((PrimProfileParams) (obj)).End && Hollow == ((PrimProfileParams) (obj)).Hollow;
    }

    public final int hashCode()
    {
        return hashValue;
    }

    public String toString()
    {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", new Object[] {
            Byte.valueOf(CurveType), Float.valueOf(Begin), Float.valueOf(End), Float.valueOf(Hollow)
        });
    }
}
