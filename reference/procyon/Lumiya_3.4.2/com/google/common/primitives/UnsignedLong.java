// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import javax.annotation.Nullable;
import javax.annotation.CheckReturnValue;
import java.math.BigInteger;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
public final class UnsignedLong extends Number implements Comparable<UnsignedLong>, Serializable
{
    public static final UnsignedLong MAX_VALUE;
    public static final UnsignedLong ONE;
    private static final long UNSIGNED_MASK = Long.MAX_VALUE;
    public static final UnsignedLong ZERO;
    private final long value;
    
    static {
        ZERO = new UnsignedLong(0L);
        ONE = new UnsignedLong(1L);
        MAX_VALUE = new UnsignedLong(-1L);
    }
    
    private UnsignedLong(final long value) {
        this.value = value;
    }
    
    public static UnsignedLong fromLongBits(final long n) {
        return new UnsignedLong(n);
    }
    
    public static UnsignedLong valueOf(final long l) {
        boolean b;
        if (l < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "value (%s) is outside the range for an unsigned long value", l);
        return fromLongBits(l);
    }
    
    public static UnsignedLong valueOf(final String s) {
        return valueOf(s, 10);
    }
    
    public static UnsignedLong valueOf(final String s, final int n) {
        return fromLongBits(UnsignedLongs.parseUnsignedLong(s, n));
    }
    
    public static UnsignedLong valueOf(final BigInteger bigInteger) {
        Preconditions.checkNotNull(bigInteger);
        Preconditions.checkArgument(bigInteger.signum() >= 0 && bigInteger.bitLength() <= 64, "value (%s) is outside the range for an unsigned long value", bigInteger);
        return fromLongBits(bigInteger.longValue());
    }
    
    public BigInteger bigIntegerValue() {
        final BigInteger value = BigInteger.valueOf(this.value & Long.MAX_VALUE);
        int n;
        if (this.value >= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        BigInteger setBit = value;
        if (n == 0) {
            setBit = value.setBit(63);
        }
        return setBit;
    }
    
    @Override
    public int compareTo(final UnsignedLong unsignedLong) {
        Preconditions.checkNotNull(unsignedLong);
        return UnsignedLongs.compare(this.value, unsignedLong.value);
    }
    
    @CheckReturnValue
    public UnsignedLong dividedBy(final UnsignedLong unsignedLong) {
        return fromLongBits(UnsignedLongs.divide(this.value, Preconditions.checkNotNull(unsignedLong).value));
    }
    
    @Override
    public double doubleValue() {
        final double n = (double)(this.value & Long.MAX_VALUE);
        int n2;
        if (this.value >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        double n3 = n;
        if (n2 == 0) {
            n3 = n + 9.223372036854776E18;
        }
        return n3;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof UnsignedLong)) {
            return false;
        }
        if (this.value == ((UnsignedLong)o).value) {
            b = true;
        }
        return b;
    }
    
    @Override
    public float floatValue() {
        final float n = (float)(this.value & Long.MAX_VALUE);
        int n2;
        if (this.value >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        float n3 = n;
        if (n2 == 0) {
            n3 = n + 9.223372E18f;
        }
        return n3;
    }
    
    @Override
    public int hashCode() {
        return Longs.hashCode(this.value);
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @CheckReturnValue
    public UnsignedLong minus(final UnsignedLong unsignedLong) {
        return fromLongBits(this.value - Preconditions.checkNotNull(unsignedLong).value);
    }
    
    @CheckReturnValue
    public UnsignedLong mod(final UnsignedLong unsignedLong) {
        return fromLongBits(UnsignedLongs.remainder(this.value, Preconditions.checkNotNull(unsignedLong).value));
    }
    
    @CheckReturnValue
    public UnsignedLong plus(final UnsignedLong unsignedLong) {
        return fromLongBits(Preconditions.checkNotNull(unsignedLong).value + this.value);
    }
    
    @CheckReturnValue
    public UnsignedLong times(final UnsignedLong unsignedLong) {
        return fromLongBits(Preconditions.checkNotNull(unsignedLong).value * this.value);
    }
    
    @Override
    public String toString() {
        return UnsignedLongs.toString(this.value);
    }
    
    public String toString(final int n) {
        return UnsignedLongs.toString(this.value, n);
    }
}
