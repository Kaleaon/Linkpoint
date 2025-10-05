package com.linkpoint.slproto.prims

import android.support.v4.internal.view.SupportMenu
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.messages.ObjectUpdate
import com.linkpoint.slproto.types.LLTersePacking
import com.linkpoint.slproto.types.LLVector2
import java.nio.ByteBuffer

class PrimPathParams {
    const val Float CUT_QUANTA = 2.0E-5f
    const val Byte LL_PCODE_PATH_CIRCLE = 32
    const val Byte LL_PCODE_PATH_CIRCLE2 = 48
    const val Byte LL_PCODE_PATH_FLEXIBLE = Byte.MIN_VALUE
    const val Byte LL_PCODE_PATH_LINE = 16
    const val Byte LL_PCODE_PATH_TEST = 64
    const val Float REV_QUANTA = 0.015f
    const val Float SCALE_QUANTA = 0.01f
    const val Float SHEAR_QUANTA = 0.01f
    const val Float TAPER_QUANTA = 0.01f
    val Float Begin
    val Byte CurveType
    val Float End
    val Float RadiusOffset
    val Float Revolutions
    val Float ScaleX
    val Float ScaleY
    val Float ShearX
    val Float ShearY
    val Float Skew
    val Float TaperX
    val Float TaperY
    val Float TwistBegin
    val Float TwistEnd
    private val Int hashValue = getHashValue()

    public PrimPathParams(Byte b, Float f, Float f2, Float f3, Float f4, Float f5, Float f6, Float f7, Float f8, Float f9, Float f10, Float f11, Float f12, Float f13) {
        this.CurveType = b
        this.Begin = f
        this.End = f2
        this.ScaleX = f3
        this.ScaleY = f4
        this.ShearX = f5
        this.ShearY = f6
        this.TwistBegin = f7
        this.TwistEnd = f8
        this.RadiusOffset = f9
        this.TaperX = f10
        this.TaperY = f11
        this.Revolutions = f12
        this.Skew = f13
    }

    public PrimPathParams(ObjectUpdate.ObjectData objectData) {
        this.CurveType = (Byte) objectData.PathCurve
        this.Begin = ((Float) (objectData.PathBegin & SupportMenu.USER_MASK)) * 2.0E-5f
        this.End = ((Float) (50000 - (objectData.PathEnd & SupportMenu.USER_MASK))) * 2.0E-5f
        this.ScaleX = ((Float) (200 - (objectData.PathScaleX & 255))) * 0.01f
        this.ScaleY = ((Float) (200 - (objectData.PathScaleY & 255))) * 0.01f
        this.ShearX = ((Float) LLTersePacking.getSignedByte(objectData.PathShearX)) * 0.01f
        this.ShearY = ((Float) LLTersePacking.getSignedByte(objectData.PathShearY)) * 0.01f
        this.TwistEnd = ((Float) LLTersePacking.getSignedByte(objectData.PathTwist)) * 0.01f
        this.TwistBegin = ((Float) LLTersePacking.getSignedByte(objectData.PathTwistBegin)) * 0.01f
        this.RadiusOffset = ((Float) LLTersePacking.getSignedByte(objectData.PathRadiusOffset)) * 0.01f
        this.TaperX = ((Float) LLTersePacking.getSignedByte(objectData.PathTaperX)) * 0.01f
        this.TaperY = ((Float) LLTersePacking.getSignedByte(objectData.PathTaperY)) * 0.01f
        this.Revolutions = (((Float) (objectData.PathRevolutions & 255)) * 0.015f) + 1.0f
        this.Skew = ((Float) LLTersePacking.getSignedByte(objectData.PathSkew)) * 0.01f
    }

    public PrimPathParams(ByteBuffer byteBuffer) {
        this.CurveType = byteBuffer.get()
        this.Begin = ((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f
        this.End = ((Float) (50000 - (byteBuffer.getShort() & 65535))) * 2.0E-5f
        this.ScaleX = ((Float) (200 - (byteBuffer.get() & UnsignedBytes.MAX_VALUE))) * 0.01f
        this.ScaleY = ((Float) (200 - (byteBuffer.get() & UnsignedBytes.MAX_VALUE))) * 0.01f
        this.ShearX = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.ShearY = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.TwistEnd = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.TwistBegin = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.RadiusOffset = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.TaperX = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.TaperY = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
        this.Revolutions = (((Float) (byteBuffer.get() & UnsignedBytes.MAX_VALUE)) * 0.015f) + 1.0f
        this.Skew = ((Float) LLTersePacking.getSignedByte(byteBuffer.get())) * 0.01f
    }

    private Int getHashValue() {
        return (this.CurveType * 17) + 0 + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.ScaleX) + Float.floatToIntBits(this.ScaleY) + Float.floatToIntBits(this.ShearX) + Float.floatToIntBits(this.ShearY) + Float.floatToIntBits(this.TwistBegin) + Float.floatToIntBits(this.TwistEnd) + Float.floatToIntBits(this.RadiusOffset) + Float.floatToIntBits(this.TaperX) + Float.floatToIntBits(this.TaperY) + Float.floatToIntBits(this.Revolutions) + Float.floatToIntBits(this.Skew)
    }

    val Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PrimPathParams)) {
            return false
        }
        PrimPathParams primPathParams = (PrimPathParams) obj
        if (this.CurveType == primPathParams.CurveType && this.Begin == primPathParams.Begin && this.End == primPathParams.End && this.ScaleX == primPathParams.ScaleX && this.ScaleY == primPathParams.ScaleY && this.ShearX == primPathParams.ShearX && this.ShearY == primPathParams.ShearY && this.TwistBegin == primPathParams.TwistBegin && this.TwistEnd == primPathParams.TwistEnd && this.RadiusOffset == primPathParams.RadiusOffset && this.TaperX == primPathParams.TaperX && this.TaperY == primPathParams.TaperY && this.Revolutions == primPathParams.Revolutions) {
            return this.Skew == primPathParams.Skew
        }
        return false
    }

    public LLVector2 getBeginScale() {
        LLVector2 lLVector2 = LLVector2(1.0f, 1.0f)
        if (this.ScaleX > 1.0f) {
            lLVector2.x = 2.0f - this.ScaleX
        }
        if (this.ScaleY > 1.0f) {
            lLVector2.y = 2.0f - this.ScaleY
        }
        return lLVector2
    }

    public LLVector2 getEndScale() {
        LLVector2 lLVector2 = LLVector2(1.0f, 1.0f)
        if (this.ScaleX < 1.0f) {
            lLVector2.x = this.ScaleX
        }
        if (this.ScaleY < 1.0f) {
            lLVector2.y = this.ScaleY
        }
        return lLVector2
    }

    val Int hashCode() {
        return this.hashValue
    }

    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Scale: (%f, %f), Shear: (%f, %f), TwistBegin: %f, TwistEnd: %f, RadiusOffset: %f, Taper: (%f, %f), Revolutions: %f, Skew: %f", Object[]{Byte.valueOf(this.CurveType), Float.valueOf(this.Begin), Float.valueOf(this.End), Float.valueOf(this.ScaleX), Float.valueOf(this.ScaleY), Float.valueOf(this.ShearX), Float.valueOf(this.ShearY), Float.valueOf(this.TwistBegin), Float.valueOf(this.TwistEnd), Float.valueOf(this.RadiusOffset), Float.valueOf(this.TaperX), Float.valueOf(this.TaperY), Float.valueOf(this.Revolutions), Float.valueOf(this.Skew)})
    }
}
