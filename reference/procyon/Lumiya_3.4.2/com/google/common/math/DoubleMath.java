// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.math;

import java.math.BigInteger;
import java.util.Iterator;
import java.math.RoundingMode;
import com.google.common.primitives.Booleans;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class DoubleMath
{
    private static final double LN_2;
    @VisibleForTesting
    static final int MAX_FACTORIAL = 170;
    private static final double MAX_INT_AS_DOUBLE = 2.147483647E9;
    private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18;
    private static final double MIN_INT_AS_DOUBLE = -2.147483648E9;
    private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E18;
    @VisibleForTesting
    static final double[] everySixteenthFactorial;
    
    static {
        LN_2 = Math.log(2.0);
        everySixteenthFactorial = new double[] { 1.0, 2.0922789888E13, 2.631308369336935E35, 1.2413915592536073E61, 1.2688693218588417E89, 7.156945704626381E118, 9.916779348709496E149, 1.974506857221074E182, 3.856204823625804E215, 5.5502938327393044E249, 4.7147236359920616E284 };
    }
    
    private DoubleMath() {
    }
    
    @GwtIncompatible("com.google.common.math.DoubleUtils")
    private static double checkFinite(final double n) {
        Preconditions.checkArgument(DoubleUtils.isFinite(n));
        return n;
    }
    
    public static double factorial(final int n) {
        MathPreconditions.checkNonNegative("n", n);
        if (n <= 170) {
            double n2 = 1.0;
            for (int i = (n & 0xFFFFFFF0) + 1; i <= n; ++i) {
                n2 *= i;
            }
            return DoubleMath.everySixteenthFactorial[n >> 4] * n2;
        }
        return Double.POSITIVE_INFINITY;
    }
    
    public static int fuzzyCompare(final double v, final double v2, final double n) {
        if (fuzzyEquals(v, v2, n)) {
            return 0;
        }
        if (v < v2) {
            return -1;
        }
        if (v > v2) {
            return 1;
        }
        return Booleans.compare(Double.isNaN(v), Double.isNaN(v2));
    }
    
    public static boolean fuzzyEquals(final double v, final double v2, final double n) {
        boolean b = false;
        MathPreconditions.checkNonNegative("tolerance", n);
        boolean b2;
        if (Math.copySign(v - v2, 1.0) <= n) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (b2 || v == v2 || (Double.isNaN(v) && Double.isNaN(v2))) {
            b = true;
        }
        return b;
    }
    
    @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
    public static boolean isMathematicalInteger(final double d) {
        boolean b = false;
        if (DoubleUtils.isFinite(d) && (d == 0.0 || 52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(d)) <= Math.getExponent(d))) {
            b = true;
        }
        return b;
    }
    
    @GwtIncompatible("com.google.common.math.DoubleUtils")
    public static boolean isPowerOfTwo(final double n) {
        boolean b2;
        final boolean b = b2 = false;
        if (n > 0.0) {
            if (!DoubleUtils.isFinite(n)) {
                b2 = b;
            }
            else {
                b2 = b;
                if (LongMath.isPowerOfTwo(DoubleUtils.getSignificand(n))) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public static double log2(final double a) {
        return Math.log(a) / DoubleMath.LN_2;
    }
    
    @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
    public static int log2(double scaleNormalize, final RoundingMode roundingMode) {
        final int n = 1;
        boolean b = false;
        final int n2 = 0;
        final int n3 = 0;
        Preconditions.checkArgument(scaleNormalize > 0.0 && DoubleUtils.isFinite(scaleNormalize), (Object)"x must be positive and finite");
        final int exponent = Math.getExponent(scaleNormalize);
        if (!DoubleUtils.isNormal(scaleNormalize)) {
            return log2(4.503599627370496E15 * scaleNormalize, roundingMode) - 52;
        }
        int n4 = n3;
        while (true) {
            switch (roundingMode) {
                default: {
                    throw new AssertionError();
                }
                case UNNECESSARY: {
                    MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(scaleNormalize));
                    n4 = n3;
                    break Label_0142;
                }
                case HALF_EVEN:
                case HALF_UP:
                case HALF_DOWN: {
                    scaleNormalize = DoubleUtils.scaleNormalize(scaleNormalize);
                    if (scaleNormalize * scaleNormalize > 2.0) {
                        n4 = n;
                    }
                    else {
                        n4 = 0;
                    }
                    break Label_0142;
                }
                case UP: {
                    boolean b2;
                    if (exponent < 0) {
                        b2 = false;
                    }
                    else {
                        b2 = true;
                    }
                    int n5;
                    if (isPowerOfTwo(scaleNormalize)) {
                        n5 = n2;
                    }
                    else {
                        n5 = 1;
                    }
                    n4 = (n5 & (b2 ? 1 : 0));
                    break Label_0142;
                }
                case DOWN: {
                    boolean b3;
                    if (exponent >= 0) {
                        b3 = false;
                    }
                    else {
                        b3 = true;
                    }
                    if (!isPowerOfTwo(scaleNormalize)) {
                        b = true;
                    }
                    n4 = ((b & b3) ? 1 : 0);
                }
                case FLOOR: {
                    int n6;
                    if (n4 == 0) {
                        n6 = exponent;
                    }
                    else {
                        n6 = exponent + 1;
                    }
                    return n6;
                }
                case CEILING: {
                    n4 = n3;
                    if (!isPowerOfTwo(scaleNormalize)) {
                        n4 = 1;
                    }
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("com.google.common.math.DoubleUtils")
    public static double mean(final Iterable<? extends Number> iterable) {
        return mean(iterable.iterator());
    }
    
    @GwtIncompatible("com.google.common.math.DoubleUtils")
    public static double mean(final Iterator<? extends Number> iterator) {
        Preconditions.checkArgument(iterator.hasNext(), (Object)"Cannot take mean of 0 values");
        double checkFinite = checkFinite(iterator.next().doubleValue());
        long n = 1L;
        while (iterator.hasNext()) {
            final double checkFinite2 = checkFinite(iterator.next().doubleValue());
            ++n;
            checkFinite += (checkFinite2 - checkFinite) / n;
        }
        return checkFinite;
    }
    
    @GwtIncompatible("com.google.common.math.DoubleUtils")
    public static double mean(final double... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0, (Object)"Cannot take mean of 0 values");
        double checkFinite = checkFinite(array[0]);
        long n = 1L;
        while (i < array.length) {
            checkFinite(array[i]);
            ++n;
            checkFinite += (array[i] - checkFinite) / n;
            ++i;
        }
        return checkFinite;
    }
    
    public static double mean(final int... array) {
        int i = 0;
        Preconditions.checkArgument(array.length > 0, (Object)"Cannot take mean of 0 values");
        long n = 0L;
        while (i < array.length) {
            n += array[i];
            ++i;
        }
        return n / (double)array.length;
    }
    
    public static double mean(final long... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0, (Object)"Cannot take mean of 0 values");
        double n = (double)array[0];
        long n2 = 1L;
        while (i < array.length) {
            ++n2;
            n += (array[i] - n) / n2;
            ++i;
        }
        return n;
    }
    
    @GwtIncompatible("#isMathematicalInteger, com.google.common.math.DoubleUtils")
    static double roundIntermediate(final double a, final RoundingMode roundingMode) {
        final int n = 1;
        boolean b = true;
        if (!DoubleUtils.isFinite(a)) {
            throw new ArithmeticException("input is infinite or NaN");
        }
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(a));
                return a;
            }
            case FLOOR: {
                if (a < 0.0) {
                    b = false;
                }
                if (!b && !isMathematicalInteger(a)) {
                    return a - 1.0;
                }
                return a;
            }
            case CEILING: {
                int n2;
                if (a <= 0.0) {
                    n2 = n;
                }
                else {
                    n2 = 0;
                }
                if (n2 == 0 && !isMathematicalInteger(a)) {
                    return a + 1.0;
                }
                return a;
            }
            case DOWN: {
                return a;
            }
            case UP: {
                if (!isMathematicalInteger(a)) {
                    return Math.copySign(1.0, a) + a;
                }
                return a;
            }
            case HALF_EVEN: {
                return Math.rint(a);
            }
            case HALF_UP: {
                final double rint = Math.rint(a);
                if (Math.abs(a - rint) == 0.5) {
                    return Math.copySign(0.5, a) + a;
                }
                return rint;
            }
            case HALF_DOWN: {
                final double rint2 = Math.rint(a);
                if (Math.abs(a - rint2) == 0.5) {
                    return a;
                }
                return rint2;
            }
        }
    }
    
    @GwtIncompatible("#roundIntermediate, java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
    public static BigInteger roundToBigInteger(double roundIntermediate, final RoundingMode roundingMode) {
        boolean b = true;
        roundIntermediate = roundIntermediate(roundIntermediate, roundingMode);
        boolean b2;
        if (-9.223372036854776E18 - roundIntermediate < 1.0) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (roundIntermediate >= 9.223372036854776E18) {
            b = false;
        }
        if (!(b & b2)) {
            BigInteger bigInteger = BigInteger.valueOf(DoubleUtils.getSignificand(roundIntermediate)).shiftLeft(Math.getExponent(roundIntermediate) - 52);
            if (roundIntermediate < 0.0) {
                bigInteger = bigInteger.negate();
            }
            return bigInteger;
        }
        return BigInteger.valueOf((long)roundIntermediate);
    }
    
    @GwtIncompatible("#roundIntermediate")
    public static int roundToInt(double roundIntermediate, final RoundingMode roundingMode) {
        boolean b = true;
        roundIntermediate = roundIntermediate(roundIntermediate, roundingMode);
        boolean b2;
        if (roundIntermediate > -2.147483649E9) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (roundIntermediate >= 2.147483648E9) {
            b = false;
        }
        MathPreconditions.checkInRange(b & b2);
        return (int)roundIntermediate;
    }
    
    @GwtIncompatible("#roundIntermediate")
    public static long roundToLong(double roundIntermediate, final RoundingMode roundingMode) {
        boolean b = true;
        roundIntermediate = roundIntermediate(roundIntermediate, roundingMode);
        boolean b2;
        if (-9.223372036854776E18 - roundIntermediate < 1.0) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (roundIntermediate >= 9.223372036854776E18) {
            b = false;
        }
        MathPreconditions.checkInRange(b & b2);
        return (long)roundIntermediate;
    }
}
