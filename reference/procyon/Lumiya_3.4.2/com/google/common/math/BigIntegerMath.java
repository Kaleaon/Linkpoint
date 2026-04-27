// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.math;

import java.util.List;
import java.util.ArrayList;
import com.google.common.annotations.GwtIncompatible;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.math.BigInteger;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class BigIntegerMath
{
    private static final double LN_10;
    private static final double LN_2;
    @VisibleForTesting
    static final BigInteger SQRT2_PRECOMPUTED_BITS;
    @VisibleForTesting
    static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
    
    static {
        SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
        LN_10 = Math.log(10.0);
        LN_2 = Math.log(2.0);
    }
    
    private BigIntegerMath() {
    }
    
    public static BigInteger binomial(final int i, int j) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        int n;
        if (j <= i >> 1) {
            n = j;
        }
        else {
            n = i - j;
        }
        if (n < LongMath.biggestBinomials.length && i <= LongMath.biggestBinomials[n]) {
            return BigInteger.valueOf(LongMath.binomial(i, n));
        }
        BigInteger bigInteger = BigInteger.ONE;
        long n2 = i;
        final int log2 = LongMath.log2(i, RoundingMode.CEILING);
        long n3 = 1L;
        int k = 1;
        j = log2;
        while (k < n) {
            final int n4 = i - k;
            final int n5 = k + 1;
            if (j + log2 < 63) {
                n2 *= n4;
                n3 *= n5;
                j += log2;
            }
            else {
                bigInteger = bigInteger.multiply(BigInteger.valueOf(n2)).divide(BigInteger.valueOf(n3));
                n2 = n4;
                n3 = n5;
                j = log2;
            }
            ++k;
        }
        return bigInteger.multiply(BigInteger.valueOf(n2)).divide(BigInteger.valueOf(n3));
    }
    
    @GwtIncompatible("TODO")
    public static BigInteger divide(final BigInteger val, final BigInteger val2, final RoundingMode roundingMode) {
        return new BigDecimal(val).divide(new BigDecimal(val2), 0, roundingMode).toBigIntegerExact();
    }
    
    public static BigInteger factorial(int n) {
        MathPreconditions.checkNonNegative("n", n);
        if (n >= LongMath.factorials.length) {
            final ArrayList list = new ArrayList(IntMath.divide(IntMath.log2(n, RoundingMode.CEILING) * n, 64, RoundingMode.CEILING));
            final int length = LongMath.factorials.length;
            final long i = LongMath.factorials[length - 1];
            int numberOfTrailingZeros = Long.numberOfTrailingZeros(i);
            long n2 = i >> numberOfTrailingZeros;
            final int log2 = LongMath.log2(n2, RoundingMode.FLOOR);
            final int n3 = LongMath.log2(length, RoundingMode.FLOOR) + 1;
            long j = length;
            final int n4 = log2 + 1;
            int n5 = n3;
            int n6 = 1 << n3 - 1;
            int log3 = n4;
            while (true) {
                int n7;
                if (j > n) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                if (n7 != 0) {
                    break;
                }
                int n8 = n6;
                int n9 = n5;
                if (((long)n6 & j) != 0x0L) {
                    n8 = n6 << 1;
                    n9 = n5 + 1;
                }
                final int numberOfTrailingZeros2 = Long.numberOfTrailingZeros(j);
                numberOfTrailingZeros += numberOfTrailingZeros2;
                if (n9 - numberOfTrailingZeros2 + log3 >= 64) {
                    list.add(BigInteger.valueOf(n2));
                    n2 = 1L;
                }
                n2 *= j >> numberOfTrailingZeros2;
                log3 = LongMath.log2(n2, RoundingMode.FLOOR);
                ++j;
                ++log3;
                n6 = n8;
                n5 = n9;
            }
            if (n2 <= 1L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                list.add(BigInteger.valueOf(n2));
            }
            return listProduct(list).shiftLeft(numberOfTrailingZeros);
        }
        return BigInteger.valueOf(LongMath.factorials[n]);
    }
    
    @GwtIncompatible("TODO")
    static boolean fitsInLong(final BigInteger bigInteger) {
        return bigInteger.bitLength() <= 63;
    }
    
    public static boolean isPowerOfTwo(final BigInteger bigInteger) {
        boolean b = false;
        Preconditions.checkNotNull(bigInteger);
        if (bigInteger.signum() > 0 && bigInteger.getLowestSetBit() == bigInteger.bitLength() - 1) {
            b = true;
        }
        return b;
    }
    
    static BigInteger listProduct(final List<BigInteger> list) {
        return listProduct(list, 0, list.size());
    }
    
    static BigInteger listProduct(final List<BigInteger> list, final int n, final int n2) {
        switch (n2 - n) {
            default: {
                final int n3 = n2 + n >>> 1;
                return listProduct(list, n, n3).multiply(listProduct(list, n3, n2));
            }
            case 0: {
                return BigInteger.ONE;
            }
            case 1: {
                return list.get(n);
            }
            case 2: {
                return list.get(n).multiply(list.get(n + 1));
            }
            case 3: {
                return list.get(n).multiply(list.get(n + 1)).multiply(list.get(n + 2));
            }
        }
    }
    
    @GwtIncompatible("TODO")
    public static int log10(final BigInteger x, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", x);
        if (fitsInLong(x)) {
            return LongMath.log10(x.longValue(), roundingMode);
        }
        int exponent = (int)(log2(x, RoundingMode.FLOOR) * BigIntegerMath.LN_2 / BigIntegerMath.LN_10);
        BigInteger val = BigInteger.TEN.pow(exponent);
        int compareTo = val.compareTo(x);
        BigInteger bigInteger = val;
        int n = exponent;
        int n2;
        if (compareTo <= 0) {
            BigInteger multiply = BigInteger.TEN.multiply(val);
            BigInteger multiply2;
            int compareTo2;
            for (int i = multiply.compareTo(x); i <= 0; i = compareTo2, val = multiply, multiply = multiply2) {
                ++exponent;
                multiply2 = BigInteger.TEN.multiply(multiply);
                compareTo2 = multiply2.compareTo(x);
                compareTo = i;
            }
            n2 = exponent;
        }
        else {
            int j;
            int n3;
            do {
                n3 = n - 1;
                val = bigInteger.divide(BigInteger.TEN);
                j = val.compareTo(x);
                bigInteger = val;
                n = n3;
            } while (j > 0);
            n2 = n3;
            compareTo = j;
        }
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(compareTo == 0);
            }
            case DOWN:
            case FLOOR: {
                return n2;
            }
            case UP:
            case CEILING: {
                int n4 = n2;
                if (!val.equals(x)) {
                    n4 = n2 + 1;
                }
                return n4;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                int n5 = n2;
                if (x.pow(2).compareTo(val.pow(2).multiply(BigInteger.TEN)) > 0) {
                    n5 = n2 + 1;
                }
                return n5;
            }
        }
    }
    
    public static int log2(final BigInteger bigInteger, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", Preconditions.checkNotNull(bigInteger));
        final int n = bigInteger.bitLength() - 1;
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(bigInteger));
            }
            case DOWN:
            case FLOOR: {
                return n;
            }
            case UP:
            case CEILING: {
                int n2 = n;
                if (!isPowerOfTwo(bigInteger)) {
                    n2 = n + 1;
                }
                return n2;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                if (n >= 256) {
                    int n3 = n;
                    if (bigInteger.pow(2).bitLength() - 1 >= n * 2 + 1) {
                        n3 = n + 1;
                    }
                    return n3;
                }
                if (bigInteger.compareTo(BigIntegerMath.SQRT2_PRECOMPUTED_BITS.shiftRight(256 - n)) > 0) {
                    return n + 1;
                }
                return n;
            }
        }
    }
    
    @GwtIncompatible("TODO")
    public static BigInteger sqrt(final BigInteger val, final RoundingMode roundingMode) {
        int n = 0;
        MathPreconditions.checkNonNegative("x", val);
        if (fitsInLong(val)) {
            return BigInteger.valueOf(LongMath.sqrt(val.longValue(), roundingMode));
        }
        BigInteger val2 = sqrtFloor(val);
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(val2.pow(2).equals(val));
            }
            case DOWN:
            case FLOOR: {
                return val2;
            }
            case UP:
            case CEILING: {
                final int intValue = val2.intValue();
                if (intValue * intValue == val.intValue() && val2.pow(2).equals(val)) {
                    n = 1;
                }
                if (n == 0) {
                    val2 = val2.add(BigInteger.ONE);
                }
                return val2;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                BigInteger add = val2;
                if (val2.pow(2).add(val2).compareTo(val) < 0) {
                    add = val2.add(BigInteger.ONE);
                }
                return add;
            }
        }
    }
    
    @GwtIncompatible("TODO")
    private static BigInteger sqrtApproxWithDoubles(final BigInteger bigInteger) {
        return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(bigInteger)), RoundingMode.HALF_EVEN);
    }
    
    @GwtIncompatible("TODO")
    private static BigInteger sqrtFloor(final BigInteger bigInteger) {
        final int log2 = log2(bigInteger, RoundingMode.FLOOR);
        BigInteger val;
        if (log2 >= 1023) {
            final int n = log2 - 52 & 0xFFFFFFFE;
            val = sqrtApproxWithDoubles(bigInteger.shiftRight(n)).shiftLeft(n >> 1);
        }
        else {
            val = sqrtApproxWithDoubles(bigInteger);
        }
        final BigInteger shiftRight = val.add(bigInteger.divide(val)).shiftRight(1);
        if (!val.equals(shiftRight)) {
            BigInteger bigInteger2 = shiftRight;
            while (true) {
                final BigInteger shiftRight2 = bigInteger2.add(bigInteger.divide(bigInteger2)).shiftRight(1);
                if (shiftRight2.compareTo(bigInteger2) >= 0) {
                    break;
                }
                bigInteger2 = shiftRight2;
            }
            return bigInteger2;
        }
        return val;
    }
}
