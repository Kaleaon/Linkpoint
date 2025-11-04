// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.math;

import java.math.BigInteger;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class MathPreconditions
{
    private MathPreconditions() {
    }
    
    static void checkInRange(final boolean b) {
        if (b) {
            return;
        }
        throw new ArithmeticException("not in range");
    }
    
    static void checkNoOverflow(final boolean b) {
        if (b) {
            return;
        }
        throw new ArithmeticException("overflow");
    }
    
    static double checkNonNegative(@Nullable final String str, final double d) {
        int n;
        if (d >= 0.0) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException(str + " (" + d + ") must be >= 0");
        }
        return d;
    }
    
    static int checkNonNegative(@Nullable final String str, final int i) {
        if (i >= 0) {
            return i;
        }
        throw new IllegalArgumentException(str + " (" + i + ") must be >= 0");
    }
    
    static long checkNonNegative(@Nullable final String str, final long lng) {
        int n;
        if (lng >= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException(str + " (" + lng + ") must be >= 0");
        }
        return lng;
    }
    
    static BigInteger checkNonNegative(@Nullable final String str, final BigInteger obj) {
        if (obj.signum() >= 0) {
            return obj;
        }
        throw new IllegalArgumentException(str + " (" + obj + ") must be >= 0");
    }
    
    static int checkPositive(@Nullable final String str, final int i) {
        if (i > 0) {
            return i;
        }
        throw new IllegalArgumentException(str + " (" + i + ") must be > 0");
    }
    
    static long checkPositive(@Nullable final String str, final long lng) {
        int n;
        if (lng > 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException(str + " (" + lng + ") must be > 0");
        }
        return lng;
    }
    
    static BigInteger checkPositive(@Nullable final String str, final BigInteger obj) {
        if (obj.signum() > 0) {
            return obj;
        }
        throw new IllegalArgumentException(str + " (" + obj + ") must be > 0");
    }
    
    static void checkRoundingUnnecessary(final boolean b) {
        if (b) {
            return;
        }
        throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
    }
}
