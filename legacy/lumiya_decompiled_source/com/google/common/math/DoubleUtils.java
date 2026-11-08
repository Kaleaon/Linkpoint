package com.google.common.math;

import com.google.common.base.Preconditions;
import java.math.BigInteger;
/* loaded from: classes.dex */
final class DoubleUtils {
    static final int EXPONENT_BIAS = 1023;
    static final long EXPONENT_MASK = 9218868437227405312L;
    static final long IMPLICIT_BIT = 4503599627370496L;
    private static final long ONE_BITS = Double.doubleToRawLongBits(1.0d);
    static final int SIGNIFICAND_BITS = 52;
    static final long SIGNIFICAND_MASK = 4503599627370495L;
    static final long SIGN_MASK = Long.MIN_VALUE;

    private DoubleUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double bigToDouble(BigInteger bigInteger) {
        BigInteger abs = bigInteger.abs();
        int bitLength = abs.bitLength() - 1;
        if (bitLength >= 63) {
            if (bitLength <= EXPONENT_BIAS) {
                int i = (bitLength - 52) - 1;
                long longValue = abs.shiftRight(i).longValue();
                long j = (longValue >> 1) & SIGNIFICAND_MASK;
                return Double.longBitsToDouble(((!(((longValue & 1) > 0L ? 1 : ((longValue & 1) == 0L ? 0 : -1)) != 0 && (((1 & j) > 0L ? 1 : ((1 & j) == 0L ? 0 : -1)) != 0 || abs.getLowestSetBit() < i)) ? j : 1 + j) + ((bitLength + EXPONENT_BIAS) << 52)) | (bigInteger.signum() & SIGN_MASK));
            }
            return bigInteger.signum() * Double.POSITIVE_INFINITY;
        }
        return bigInteger.longValue();
    }

    static double ensureNonNegative(double d) {
        Preconditions.checkArgument(Double.isNaN(d) ? false : true);
        if (d > 0.0d) {
            return d;
        }
        return 0.0d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getSignificand(double d) {
        Preconditions.checkArgument(isFinite(d), "not a normal value");
        int exponent = Math.getExponent(d);
        long doubleToRawLongBits = Double.doubleToRawLongBits(d) & SIGNIFICAND_MASK;
        return exponent != -1023 ? IMPLICIT_BIT | doubleToRawLongBits : doubleToRawLongBits << 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isFinite(double d) {
        return Math.getExponent(d) <= EXPONENT_BIAS;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNormal(double d) {
        return Math.getExponent(d) >= -1022;
    }

    static double nextDown(double d) {
        return -Math.nextUp(-d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double scaleNormalize(double d) {
        return Double.longBitsToDouble((Double.doubleToRawLongBits(d) & SIGNIFICAND_MASK) | ONE_BITS);
    }
}
