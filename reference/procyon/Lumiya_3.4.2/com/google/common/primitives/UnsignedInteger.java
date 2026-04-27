// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;
import java.math.BigInteger;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible(emulated = true)
public final class UnsignedInteger extends Number implements Comparable<UnsignedInteger>
{
    public static final UnsignedInteger MAX_VALUE;
    public static final UnsignedInteger ONE;
    public static final UnsignedInteger ZERO;
    private final int value;
    
    static {
        ZERO = fromIntBits(0);
        ONE = fromIntBits(1);
        MAX_VALUE = fromIntBits(-1);
    }
    
    private UnsignedInteger(final int n) {
        this.value = (n & -1);
    }
    
    public static UnsignedInteger fromIntBits(final int n) {
        return new UnsignedInteger(n);
    }
    
    public static UnsignedInteger valueOf(final long l) {
        Preconditions.checkArgument((0xFFFFFFFFL & l) == l, "value (%s) is outside the range for an unsigned integer value", l);
        return fromIntBits((int)l);
    }
    
    public static UnsignedInteger valueOf(final String s) {
        return valueOf(s, 10);
    }
    
    public static UnsignedInteger valueOf(final String s, final int n) {
        return fromIntBits(UnsignedInts.parseUnsignedInt(s, n));
    }
    
    public static UnsignedInteger valueOf(final BigInteger bigInteger) {
        Preconditions.checkNotNull(bigInteger);
        Preconditions.checkArgument(bigInteger.signum() >= 0 && bigInteger.bitLength() <= 32, "value (%s) is outside the range for an unsigned integer value", bigInteger);
        return fromIntBits(bigInteger.intValue());
    }
    
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(this.longValue());
    }
    
    @Override
    public int compareTo(final UnsignedInteger unsignedInteger) {
        Preconditions.checkNotNull(unsignedInteger);
        return UnsignedInts.compare(this.value, unsignedInteger.value);
    }
    
    public UnsignedInteger dividedBy(final UnsignedInteger unsignedInteger) {
        return fromIntBits(UnsignedInts.divide(this.value, Preconditions.checkNotNull(unsignedInteger).value));
    }
    
    @Override
    public double doubleValue() {
        return (double)this.longValue();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof UnsignedInteger)) {
            return false;
        }
        if (this.value == ((UnsignedInteger)o).value) {
            b = true;
        }
        return b;
    }
    
    @Override
    public float floatValue() {
        return (float)this.longValue();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int intValue() {
        return this.value;
    }
    
    @Override
    public long longValue() {
        return UnsignedInts.toLong(this.value);
    }
    
    public UnsignedInteger minus(final UnsignedInteger unsignedInteger) {
        return fromIntBits(this.value - Preconditions.checkNotNull(unsignedInteger).value);
    }
    
    public UnsignedInteger mod(final UnsignedInteger unsignedInteger) {
        return fromIntBits(UnsignedInts.remainder(this.value, Preconditions.checkNotNull(unsignedInteger).value));
    }
    
    public UnsignedInteger plus(final UnsignedInteger unsignedInteger) {
        return fromIntBits(Preconditions.checkNotNull(unsignedInteger).value + this.value);
    }
    
    @GwtIncompatible("Does not truncate correctly")
    public UnsignedInteger times(final UnsignedInteger unsignedInteger) {
        return fromIntBits(Preconditions.checkNotNull(unsignedInteger).value * this.value);
    }
    
    @Override
    public String toString() {
        return this.toString(10);
    }
    
    public String toString(final int n) {
        return UnsignedInts.toString(this.value, n);
    }
}
