package com.lumiyaviewer.lumiya.slproto.prims

import android.support.v4.internal.view.SupportMenu
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import java.nio.ByteBuffer

class PrimProfileParams {
    Float CUT_QUANTA = 2.0E-5f
    Float HOLLOW_QUANTA = 2.0E-5f
    Byte LL_PCODE_HOLE_CIRCLE = 16
    Byte LL_PCODE_HOLE_MASK = -16
    Byte LL_PCODE_HOLE_SAME = 0
    Byte LL_PCODE_HOLE_SQUARE = 32
    Byte LL_PCODE_HOLE_TRIANGLE = 48
    Byte LL_PCODE_PROFILE_CIRCLE = 0
    Byte LL_PCODE_PROFILE_CIRCLE_HALF = 5
    Byte LL_PCODE_PROFILE_EQUALTRI = 3
    Byte LL_PCODE_PROFILE_ISOTRI = 2
    Byte LL_PCODE_PROFILE_MASK = 15
    Byte LL_PCODE_PROFILE_RIGHTTRI = 4
    Byte LL_PCODE_PROFILE_SQUARE = 1
    Float Begin
    Byte CurveType
    Float End
    Float Hollow
    private Int hashValue = getHashValue()

    PrimProfileParams(Byte b, Float f, Float f2, Float f3) {
        this.CurveType = b
        this.Begin = f
        this.End = f2
        this.Hollow = f3
    }

    PrimProfileParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        return PrimProfileParams((Byte) objectData.ProfileCurve, ((Float) (objectData.ProfileBegin & SupportMenu.USER_MASK)) * 2.0E-5f, 1.0f - (((Float) (objectData.ProfileEnd & SupportMenu.USER_MASK)) * 2.0E-5f), ((Float) (objectData.ProfileHollow & SupportMenu.USER_MASK)) * 2.0E-5f)
    }

    PrimProfileParams createFromPackedData(ByteBuffer byteBuffer) {
        return PrimProfileParams(byteBuffer.get(), ((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f, 1.0f - (((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f), ((Float) (byteBuffer.getShort() & 65535)) * 2.0E-5f)
    }

    private Int getHashValue() {
        return (this.CurveType * 17) + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.Hollow)
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PrimProfileParams)) {
            return false
        }
        PrimProfileParams primProfileParams = (PrimProfileParams) obj
        return this.CurveType == primProfileParams.CurveType && this.Begin == primProfileParams.Begin && this.End == primProfileParams.End && this.Hollow == primProfileParams.Hollow
    }

    Int hashCode() {
        return this.hashValue
    }

    String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", Any[]{Byte.valueOf(this.CurveType), Float.valueOf(this.Begin), Float.valueOf(this.End), Float.valueOf(this.Hollow)})
    }
}
