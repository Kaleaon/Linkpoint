// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;

public class PrimProfileParams
{
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
    private final int hashValue;
    
    public PrimProfileParams(final byte b, final float begin, final float end, final float hollow) {
        this.CurveType = b;
        this.Begin = begin;
        this.End = end;
        this.Hollow = hollow;
        this.hashValue = this.getHashValue();
    }
    
    public static PrimProfileParams createFromObjectUpdate(final ObjectUpdate.ObjectData objectData) {
        return new PrimProfileParams((byte)objectData.ProfileCurve, (objectData.ProfileBegin & 0xFFFF) * 2.0E-5f, 1.0f - (objectData.ProfileEnd & 0xFFFF) * 2.0E-5f, (objectData.ProfileHollow & 0xFFFF) * 2.0E-5f);
    }
    
    public static PrimProfileParams createFromPackedData(final ByteBuffer byteBuffer) {
        return new PrimProfileParams(byteBuffer.get(), (byteBuffer.getShort() & 0xFFFF) * 2.0E-5f, 1.0f - (byteBuffer.getShort() & 0xFFFF) * 2.0E-5f, (byteBuffer.getShort() & 0xFFFF) * 2.0E-5f);
    }
    
    private int getHashValue() {
        return this.CurveType * 17 + Float.floatToIntBits(this.Begin) + Float.floatToIntBits(this.End) + Float.floatToIntBits(this.Hollow);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrimProfileParams)) {
            return false;
        }
        final PrimProfileParams primProfileParams = (PrimProfileParams)o;
        if (this.CurveType != primProfileParams.CurveType || this.Begin != primProfileParams.Begin || this.End != primProfileParams.End || this.Hollow != primProfileParams.Hollow) {
            b = false;
        }
        return b;
    }
    
    @Override
    public final int hashCode() {
        return this.hashValue;
    }
    
    @Override
    public String toString() {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", this.CurveType, this.Begin, this.End, this.Hollow);
    }
}
