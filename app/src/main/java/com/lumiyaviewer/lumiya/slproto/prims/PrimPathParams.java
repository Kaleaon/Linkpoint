package com.lumiyaviewer.lumiya.slproto.prims;

import android.support.v4.internal.view.SupportMenu;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.types.LLTersePacking;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import java.nio.ByteBuffer;

public class PrimPathParams {
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
    private final int hashValue = getHashValue();

    public PrimPathParams(byte b, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13) {
        this.CurveType = b;
        this.Begin = f;
        this.End = f2;
        this.ScaleX = f3;
        this.ScaleY = f4;
        this.ShearX = f5;
        this.ShearY = f6;
        this.TwistBegin = f7;
        this.TwistEnd = f8;
        this.RadiusOffset = f9;
        this.TaperX = f10;
        this.TaperY = f11;
        this.Revolutions = f12;
        this.Skew = f13;
    }

    public PrimPathParams(ObjectUpdate.ObjectData objectData) {
        this.CurveType = (byte) objectData.PathCurve;
        this.Begin = ((float) (objectData.PathBegin & SupportMenu.USER_MASK)) * 2.0E-5f;
        this.End = ((float) (50000 - (objectData.PathEnd & SupportMenu.USER_MASK))) * 2.0E-5f;
        this.ScaleX = ((float) (200 - (objectData.PathScaleX & 255))) * 0.01f;
        this.ScaleY = ((float) (200 - (objectData.PathScaleY & 255))) * 0.01f;
        this.ShearX = ((float) LLTersePacking.getSignedByte(objectData.PathShearX)) * 0.01f;
        this.ShearY = ((float) LLTersePacking.getSignedByte(objectData.PathShearY)) * 0.01f;
        this.TwistEnd = ((float) LLTersePacking.getSignedByte(objectData.PathTwist)) * 0.01f;
        this.TwistBegin = ((float) LLTersePacking.getSignedByte(objectData.PathTwistBegin)) * 0.01f;
        this.RadiusOffset = ((float) LLTersePacking.getSignedByte(objectData.PathRadiusOffset)) * 0.01f;
        this.TaperX = ((float) LLTersePacking.getSignedByte(objectData.PathTaperX)) * 0.01f;
        this.TaperY = ((float) LLTersePacking.getSignedByte(objectData.PathTaperY)) * 0.01f;
        this.Revolutions = (((float) (objectData.PathRevolutions & 255)) * 0.015f) + 1.0f;
        this.Skew = ((float) LLTersePacking.getSignedByte(objectData.PathSkew)) * 0.01f;
    }

    public PrimPathParams(ByteBuffer byteBuffer) {
        this.CurveType = byteBuffer.get();
        this.Begin = ((float) (byteBuffer.getShort() & 65535)) * 2.0E-5f;
        this.End = ((float) (50000 - (byteBuffer.getShort() & 65535))) * 2.0E-5f;
        this.ScaleX = ((float) (200 - (byteBuffer.get() & UnsignedBytes.MAX_VALUE))) * 0.01f;
        this.ScaleY = ((float) (200 - (byteBuffer.get() & UnsignedBytes.MAX_VALUE))) * 0.01f;
        this.ShearX = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.ShearY = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.TwistEnd = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.TwistBegin = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.RadiusOffset = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.TaperX = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.TaperY = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
        this.Revolutions = (((float) (byteBuffer.get() & UnsignedBytes.MAX_VALUE)) * 0.015f) + 1.0f;
        this.Skew = ((float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f;
    }

    private int getHashValue() {
        return (this.CurveType * 17) + 0 + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.ScaleX) + Float.floatToIntBits(this.ScaleY) + Float.floatToIntBits(this.ShearX) + Float.floatToIntBits(this.ShearY) + Float.floatToIntBits(this.TwistBegin) + Float.floatToIntBits(this.TwistEnd) + Float.floatToIntBits(this.RadiusOffset) + Float.floatToIntBits(this.TaperX) + Float.floatToIntBits(this.TaperY) + Float.floatToIntBits(this.Revolutions) + Float.floatToIntBits(this.Skew);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrimPathParams)) {
            return false;
        }
        PrimPathParams primPathParams = (PrimPathParams) obj;
        if (this.CurveType == primPathParams.CurveType && this.Begin == primPathParams.Begin && this.End == primPathParams.End && this.ScaleX == primPathParams.ScaleX && this.ScaleY == primPathParams.ScaleY && this.ShearX == primPathParams.ShearX && this.ShearY == primPathParams.ShearY && this.TwistBegin == primPathParams.TwistBegin && this.TwistEnd == primPathParams.TwistEnd && this.RadiusOffset == primPathParams.RadiusOffset && this.TaperX == primPathParams.TaperX && this.TaperY == primPathParams.TaperY && this.Revolutions == primPathParams.Revolutions) {
            return this.Skew == primPathParams.Skew;
        }
        return false;
    }

    public LLVector2 getBeginScale() {
        LLVector2 lLVector2 = new LLVector2(1.0f, 1.0f);
        if (this.ScaleX > 1.0f) {
            lLVector2.x = 2.0f - this.ScaleX;
        }
        if (this.ScaleY > 1.0f) {
            lLVector2.y = 2.0f - this.ScaleY;
        }
        return lLVector2;
    }

    public LLVector2 getEndScale() {
        LLVector2 lLVector2 = new LLVector2(1.0f, 1.0f);
        if (this.ScaleX < 1.0f) {
            lLVector2.x = this.ScaleX;
        }
        if (this.ScaleY < 1.0f) {
            lLVector2.y = this.ScaleY;
        }
        return lLVector2;
    }

    public final int hashCode() {
        return this.hashValue;
    }

    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Scale: (%f, %f), Shear: (%f, %f), TwistBegin: %f, TwistEnd: %f, RadiusOffset: %f, Taper: (%f, %f), Revolutions: %f, Skew: %f", new Object[]{Byte.valueOf(this.CurveType), Float.valueOf(this.Begin), Float.valueOf(this.End), Float.valueOf(this.ScaleX), Float.valueOf(this.ScaleY), Float.valueOf(this.ShearX), Float.valueOf(this.ShearY), Float.valueOf(this.TwistBegin), Float.valueOf(this.TwistEnd), Float.valueOf(this.RadiusOffset), Float.valueOf(this.TaperX), Float.valueOf(this.TaperY), Float.valueOf(this.Revolutions), Float.valueOf(this.Skew)});
    }
}
