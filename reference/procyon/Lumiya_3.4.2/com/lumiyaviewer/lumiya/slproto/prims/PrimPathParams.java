// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLTersePacking;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;

public class PrimPathParams
{
    public static final float CUT_QUANTA = 2.0E-5f;
    public static final byte LL_PCODE_PATH_CIRCLE = 32;
    public static final byte LL_PCODE_PATH_CIRCLE2 = 48;
    public static final byte LL_PCODE_PATH_FLEXIBLE = Byte.MIN_VALUE;
    public static final byte LL_PCODE_PATH_LINE = 16;
    public static final byte LL_PCODE_PATH_TEST = 64;
    public static final float REV_QUANTA = 0.015f;
    public static final float SCALE_QUANTA = 0.01f;
    public static final float SHEAR_QUANTA = 0.01f;
    public static final float TAPER_QUANTA = 0.01f;
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
    
    public PrimPathParams(final byte b, final float begin, final float end, final float scaleX, final float scaleY, final float shearX, final float shearY, final float twistBegin, final float twistEnd, final float radiusOffset, final float taperX, final float taperY, final float revolutions, final float skew) {
        this.CurveType = b;
        this.Begin = begin;
        this.End = end;
        this.ScaleX = scaleX;
        this.ScaleY = scaleY;
        this.ShearX = shearX;
        this.ShearY = shearY;
        this.TwistBegin = twistBegin;
        this.TwistEnd = twistEnd;
        this.RadiusOffset = radiusOffset;
        this.TaperX = taperX;
        this.TaperY = taperY;
        this.Revolutions = revolutions;
        this.Skew = skew;
        this.hashValue = this.getHashValue();
    }
    
    public PrimPathParams(final ObjectUpdate.ObjectData objectData) {
        this.CurveType = (byte)objectData.PathCurve;
        this.Begin = (objectData.PathBegin & 0xFFFF) * 2.0E-5f;
        this.End = (50000 - (objectData.PathEnd & 0xFFFF)) * 2.0E-5f;
        this.ScaleX = (200 - (objectData.PathScaleX & 0xFF)) * 0.01f;
        this.ScaleY = (200 - (objectData.PathScaleY & 0xFF)) * 0.01f;
        this.ShearX = LLTersePacking.getSignedByte(objectData.PathShearX) * 0.01f;
        this.ShearY = LLTersePacking.getSignedByte(objectData.PathShearY) * 0.01f;
        this.TwistEnd = LLTersePacking.getSignedByte(objectData.PathTwist) * 0.01f;
        this.TwistBegin = LLTersePacking.getSignedByte(objectData.PathTwistBegin) * 0.01f;
        this.RadiusOffset = LLTersePacking.getSignedByte(objectData.PathRadiusOffset) * 0.01f;
        this.TaperX = LLTersePacking.getSignedByte(objectData.PathTaperX) * 0.01f;
        this.TaperY = LLTersePacking.getSignedByte(objectData.PathTaperY) * 0.01f;
        this.Revolutions = (objectData.PathRevolutions & 0xFF) * 0.015f + 1.0f;
        this.Skew = LLTersePacking.getSignedByte(objectData.PathSkew) * 0.01f;
        this.hashValue = this.getHashValue();
    }
    
    public PrimPathParams(final ByteBuffer byteBuffer) {
        this.CurveType = byteBuffer.get();
        this.Begin = (byteBuffer.getShort() & 0xFFFF) * 2.0E-5f;
        this.End = (50000 - (byteBuffer.getShort() & 0xFFFF)) * 2.0E-5f;
        this.ScaleX = (200 - (byteBuffer.get() & 0xFF)) * 0.01f;
        this.ScaleY = (200 - (byteBuffer.get() & 0xFF)) * 0.01f;
        this.ShearX = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.ShearY = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.TwistEnd = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.TwistBegin = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.RadiusOffset = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.TaperX = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.TaperY = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.Revolutions = (byteBuffer.get() & 0xFF) * 0.015f + 1.0f;
        this.Skew = LLTersePacking.getSignedByte(byteBuffer.get()) * 0.01f;
        this.hashValue = this.getHashValue();
    }
    
    private int getHashValue() {
        return this.CurveType * 17 + 0 + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.ScaleX) + Float.floatToIntBits(this.ScaleY) + Float.floatToIntBits(this.ShearX) + Float.floatToIntBits(this.ShearY) + Float.floatToIntBits(this.TwistBegin) + Float.floatToIntBits(this.TwistEnd) + Float.floatToIntBits(this.RadiusOffset) + Float.floatToIntBits(this.TaperX) + Float.floatToIntBits(this.TaperY) + Float.floatToIntBits(this.Revolutions) + Float.floatToIntBits(this.Skew);
    }
    
    @Override
    public final boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrimPathParams)) {
            return false;
        }
        final PrimPathParams primPathParams = (PrimPathParams)o;
        if (this.CurveType == primPathParams.CurveType && this.Begin == primPathParams.Begin && this.End == primPathParams.End && this.ScaleX == primPathParams.ScaleX && this.ScaleY == primPathParams.ScaleY && this.ShearX == primPathParams.ShearX && this.ShearY == primPathParams.ShearY && this.TwistBegin == primPathParams.TwistBegin && this.TwistEnd == primPathParams.TwistEnd && this.RadiusOffset == primPathParams.RadiusOffset && this.TaperX == primPathParams.TaperX && this.TaperY == primPathParams.TaperY && this.Revolutions == primPathParams.Revolutions) {
            if (this.Skew != primPathParams.Skew) {
                b = false;
            }
        }
        else {
            b = false;
        }
        return b;
    }
    
    public LLVector2 getBeginScale() {
        final LLVector2 llVector2 = new LLVector2(1.0f, 1.0f);
        if (this.ScaleX > 1.0f) {
            llVector2.x = 2.0f - this.ScaleX;
        }
        if (this.ScaleY > 1.0f) {
            llVector2.y = 2.0f - this.ScaleY;
        }
        return llVector2;
    }
    
    public LLVector2 getEndScale() {
        final LLVector2 llVector2 = new LLVector2(1.0f, 1.0f);
        if (this.ScaleX < 1.0f) {
            llVector2.x = this.ScaleX;
        }
        if (this.ScaleY < 1.0f) {
            llVector2.y = this.ScaleY;
        }
        return llVector2;
    }
    
    @Override
    public final int hashCode() {
        return this.hashValue;
    }
    
    @Override
    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Scale: (%f, %f), Shear: (%f, %f), TwistBegin: %f, TwistEnd: %f, RadiusOffset: %f, Taper: (%f, %f), Revolutions: %f, Skew: %f", this.CurveType, this.Begin, this.End, this.ScaleX, this.ScaleY, this.ShearX, this.ShearY, this.TwistBegin, this.TwistEnd, this.RadiusOffset, this.TaperX, this.TaperY, this.Revolutions, this.Skew);
    }
}
