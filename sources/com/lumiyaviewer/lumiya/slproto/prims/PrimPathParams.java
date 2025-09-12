// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLTersePacking;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import java.nio.ByteBuffer;

public class PrimPathParams
{

    public static final float CUT_QUANTA = 2E-05F;
    public static final byte LL_PCODE_PATH_CIRCLE = 32;
    public static final byte LL_PCODE_PATH_CIRCLE2 = 48;
    public static final byte LL_PCODE_PATH_FLEXIBLE = -128;
    public static final byte LL_PCODE_PATH_LINE = 16;
    public static final byte LL_PCODE_PATH_TEST = 64;
    public static final float REV_QUANTA = 0.015F;
    public static final float SCALE_QUANTA = 0.01F;
    public static final float SHEAR_QUANTA = 0.01F;
    public static final float TAPER_QUANTA = 0.01F;
    public final float Begin;
    public final byte CurveType;
    public final float End;
    public final float RadiusOffset;
    public final float Revolutions;
    public final float ScaleX;
    public final float ScaleY;
    public final float ShearX;
    public final float ShearY;
    public final float Skew;
    public final float TaperX;
    public final float TaperY;
    public final float TwistBegin;
    public final float TwistEnd;
    private final int hashValue;

    public PrimPathParams(byte byte0, float f, float f1, float f2, float f3, float f4, float f5, 
            float f6, float f7, float f8, float f9, float f10, float f11, float f12)
    {
        CurveType = byte0;
        Begin = f;
        End = f1;
        ScaleX = f2;
        ScaleY = f3;
        ShearX = f4;
        ShearY = f5;
        TwistBegin = f6;
        TwistEnd = f7;
        RadiusOffset = f8;
        TaperX = f9;
        TaperY = f10;
        Revolutions = f11;
        Skew = f12;
        hashValue = getHashValue();
    }

    public PrimPathParams(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData objectdata)
    {
        CurveType = (byte)objectdata.PathCurve;
        Begin = (float)(objectdata.PathBegin & 0xffff) * 2E-05F;
        End = (float)(50000 - (objectdata.PathEnd & 0xffff)) * 2E-05F;
        ScaleX = (float)(200 - (objectdata.PathScaleX & 0xff)) * 0.01F;
        ScaleY = (float)(200 - (objectdata.PathScaleY & 0xff)) * 0.01F;
        ShearX = (float)LLTersePacking.getSignedByte(objectdata.PathShearX) * 0.01F;
        ShearY = (float)LLTersePacking.getSignedByte(objectdata.PathShearY) * 0.01F;
        TwistEnd = (float)LLTersePacking.getSignedByte(objectdata.PathTwist) * 0.01F;
        TwistBegin = (float)LLTersePacking.getSignedByte(objectdata.PathTwistBegin) * 0.01F;
        RadiusOffset = (float)LLTersePacking.getSignedByte(objectdata.PathRadiusOffset) * 0.01F;
        TaperX = (float)LLTersePacking.getSignedByte(objectdata.PathTaperX) * 0.01F;
        TaperY = (float)LLTersePacking.getSignedByte(objectdata.PathTaperY) * 0.01F;
        Revolutions = (float)(objectdata.PathRevolutions & 0xff) * 0.015F + 1.0F;
        Skew = (float)LLTersePacking.getSignedByte(objectdata.PathSkew) * 0.01F;
        hashValue = getHashValue();
    }

    public PrimPathParams(ByteBuffer bytebuffer)
    {
        CurveType = bytebuffer.get();
        Begin = (float)(bytebuffer.getShort() & 0xffff) * 2E-05F;
        End = (float)(50000 - (bytebuffer.getShort() & 0xffff)) * 2E-05F;
        ScaleX = (float)(200 - (bytebuffer.get() & 0xff)) * 0.01F;
        ScaleY = (float)(200 - (bytebuffer.get() & 0xff)) * 0.01F;
        ShearX = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        ShearY = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        TwistEnd = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        TwistBegin = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        RadiusOffset = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        TaperX = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        TaperY = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        Revolutions = (float)(bytebuffer.get() & 0xff) * 0.015F + 1.0F;
        Skew = (float)LLTersePacking.getSignedByte(bytebuffer.get()) * 0.01F;
        hashValue = getHashValue();
    }

    private int getHashValue()
    {
        return CurveType * 17 + 0 + Float.floatToIntBits(Begin) + Float.floatToIntBits(End) + Float.floatToIntBits(ScaleX) + Float.floatToIntBits(ScaleY) + Float.floatToIntBits(ShearX) + Float.floatToIntBits(ShearY) + Float.floatToIntBits(TwistBegin) + Float.floatToIntBits(TwistEnd) + Float.floatToIntBits(RadiusOffset) + Float.floatToIntBits(TaperX) + Float.floatToIntBits(TaperY) + Float.floatToIntBits(Revolutions) + Float.floatToIntBits(Skew);
    }

    public final boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof PrimPathParams))
        {
            return false;
        }
        obj = (PrimPathParams)obj;
        if (CurveType == ((PrimPathParams) (obj)).CurveType && Begin == ((PrimPathParams) (obj)).Begin && End == ((PrimPathParams) (obj)).End && ScaleX == ((PrimPathParams) (obj)).ScaleX && ScaleY == ((PrimPathParams) (obj)).ScaleY && ShearX == ((PrimPathParams) (obj)).ShearX && ShearY == ((PrimPathParams) (obj)).ShearY && TwistBegin == ((PrimPathParams) (obj)).TwistBegin && TwistEnd == ((PrimPathParams) (obj)).TwistEnd && RadiusOffset == ((PrimPathParams) (obj)).RadiusOffset && TaperX == ((PrimPathParams) (obj)).TaperX && TaperY == ((PrimPathParams) (obj)).TaperY && Revolutions == ((PrimPathParams) (obj)).Revolutions)
        {
            return Skew == ((PrimPathParams) (obj)).Skew;
        } else
        {
            return false;
        }
    }

    public LLVector2 getBeginScale()
    {
        LLVector2 llvector2 = new LLVector2(1.0F, 1.0F);
        if (ScaleX > 1.0F)
        {
            llvector2.x = 2.0F - ScaleX;
        }
        if (ScaleY > 1.0F)
        {
            llvector2.y = 2.0F - ScaleY;
        }
        return llvector2;
    }

    public LLVector2 getEndScale()
    {
        LLVector2 llvector2 = new LLVector2(1.0F, 1.0F);
        if (ScaleX < 1.0F)
        {
            llvector2.x = ScaleX;
        }
        if (ScaleY < 1.0F)
        {
            llvector2.y = ScaleY;
        }
        return llvector2;
    }

    public final int hashCode()
    {
        return hashValue;
    }

    public String toString()
    {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Scale: (%f, %f), Shear: (%f, %f), TwistBegin: %f, TwistEnd: %f, RadiusOffset: %f, Taper: (%f, %f), Revolutions: %f, Skew: %f", new Object[] {
            Byte.valueOf(CurveType), Float.valueOf(Begin), Float.valueOf(End), Float.valueOf(ScaleX), Float.valueOf(ScaleY), Float.valueOf(ShearX), Float.valueOf(ShearY), Float.valueOf(TwistBegin), Float.valueOf(TwistEnd), Float.valueOf(RadiusOffset), 
            Float.valueOf(TaperX), Float.valueOf(TaperY), Float.valueOf(Revolutions), Float.valueOf(Skew)
        });
    }
}
