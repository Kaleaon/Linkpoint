package com.lumiyaviewer.lumiya.slproto.prims;

import android.support.v4.internal.view.SupportMenu;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import java.nio.ByteBuffer;

public class PrimProfileParams {
    public static final float CUT_QUANTA = 2.0E-5f;
    public static final float HOLLOW_QUANTA = 2.0E-5f;
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

    public PrimProfileParams(byte b, float f, float f2, float f3) {
        this.CurveType = b;
        this.Begin = f;
        this.End = f2;
        this.Hollow = f3;
    }

    public static PrimProfileParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        return new PrimProfileParams((byte) objectData.ProfileCurve, ((float) (objectData.ProfileBegin & SupportMenu.USER_MASK)) * 2.0E-5f, 1.0f - (((float) (objectData.ProfileEnd & SupportMenu.USER_MASK)) * 2.0E-5f), ((float) (objectData.ProfileHollow & SupportMenu.USER_MASK)) * 2.0E-5f);
    }

    public static PrimProfileParams createFromPackedData(ByteBuffer byteBuffer) {
        return new PrimProfileParams(byteBuffer.get(), ((float) (byteBuffer.getShort() & 65535)) * 2.0E-5f, 1.0f - (((float) (byteBuffer.getShort() & 65535)) * 2.0E-5f), ((float) (byteBuffer.getShort() & 65535)) * 2.0E-5f);
    }

    private int getHashValue() {
        return (this.CurveType * 17) + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.Hollow);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrimProfileParams)) {
            return false;
        }
        PrimProfileParams primProfileParams = (PrimProfileParams) obj;
        return this.CurveType == primProfileParams.CurveType && this.Begin == primProfileParams.Begin && this.End == primProfileParams.End && this.Hollow == primProfileParams.Hollow;
    }

    public final int hashCode() {
        return this.hashValue;
    }

    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", new Object[]{Byte.valueOf(this.CurveType), Float.valueOf(this.Begin), Float.valueOf(this.End), Float.valueOf(this.Hollow)});
    }
}
