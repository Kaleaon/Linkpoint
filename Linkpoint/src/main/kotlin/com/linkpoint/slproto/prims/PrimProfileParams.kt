package com.linkpoint.slproto.prims

import android.support.v4.internal.view.SupportMenu
import com.linkpoint.slproto.messages.ObjectUpdate
import java.nio.ByteBuffer

class PrimProfileParams {
    const val Float CUT_QUANTA = 2.0E-5f
    const val Float HOLLOW_QUANTA = 2.0E-5f
    const val Byte LL_PCODE_HOLE_CIRCLE = 16
    const val Byte LL_PCODE_HOLE_MASK = -16
    const val Byte LL_PCODE_HOLE_SAME = 0
    const val Byte LL_PCODE_HOLE_SQUARE = 32
    const val Byte LL_PCODE_HOLE_TRIANGLE = 48
    const val Byte LL_PCODE_PROFILE_CIRCLE = 0
    const val Byte LL_PCODE_PROFILE_CIRCLE_HALF = 5
    const val Byte LL_PCODE_PROFILE_EQUALTRI = 3
    const val Byte LL_PCODE_PROFILE_ISOTRI = 2
    const val Byte LL_PCODE_PROFILE_MASK = 15
    const val Byte LL_PCODE_PROFILE_RIGHTTRI = 4
    const val Byte LL_PCODE_PROFILE_SQUARE = 1
    val Float Begin
    val Byte CurveType
    val Float End
    val Float Hollow
    private val Int hashValue = getHashValue()

    public PrimProfileParams(Byte b, Float f, Float f2, Float f3) {
        this.CurveType = b
        this.Begin = f
        this.End = f2
        this.Hollow = f3
    }

    @JvmStatic
    PrimProfileParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        return PrimProfileParams((Byte) objectData.ProfileCurve, ((Float) (objectData.ProfileBegin & SupportMenu.USER_MASK)) * 2.0E-5f, 1.0f - (((Float) (objectData.ProfileEnd & SupportMenu.USER_MASK)) * 2.0E-5f), ((Float) (objectData.ProfileHollow & SupportMenu.USER_MASK)) * 2.0E-5f)
    }

    @JvmStatic
    PrimProfileParams createFromPackedData(ByteBuffer byteBuffer) {
        return PrimProfileParams(byteBuffer.get(), ((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f, 1.0f - (((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f), ((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f)
    }

    private Int getHashValue() {
        return (this.CurveType * 17) + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.Hollow)
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PrimProfileParams)) {
            return false
        }
        PrimProfileParams primProfileParams = (PrimProfileParams) obj
        return this.CurveType == primProfileParams.CurveType && this.Begin == primProfileParams.Begin && this.End == primProfileParams.End && this.Hollow == primProfileParams.Hollow
    }

    val Int hashCode() {
        return this.hashValue
    }

    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", Object[]{Byte.valueOf(this.CurveType), Float.valueOf(this.Begin), Float.valueOf(this.End), Float.valueOf(this.Hollow)})
    }
}
