// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.math;

import java.math.RoundingMode;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class IntMath
{
    @VisibleForTesting
    static final int FLOOR_SQRT_MAX_INT = 46340;
    @VisibleForTesting
    static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
    @VisibleForTesting
    static int[] biggestBinomials;
    private static final int[] factorials;
    @VisibleForTesting
    static final int[] halfPowersOf10;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros;
    @VisibleForTesting
    static final int[] powersOf10;
    
    static {
        maxLog10ForLeadingZeros = new byte[] { 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0 };
        powersOf10 = new int[] { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
        halfPowersOf10 = new int[] { 3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE };
        factorials = new int[] { 1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600 };
        IntMath.biggestBinomials = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33 };
    }
    
    private IntMath() {
    }
    
    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int binomial(final int i, int j) {
        int k = 0;
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        if (j > i >> 1) {
            j = i - j;
        }
        if (j >= IntMath.biggestBinomials.length || i > IntMath.biggestBinomials[j]) {
            return Integer.MAX_VALUE;
        }
        switch (j) {
            default: {
                long n = 1L;
                while (k < j) {
                    n = n * (i - k) / (k + 1);
                    ++k;
                }
                return (int)n;
            }
            case 0: {
                return 1;
            }
            case 1: {
                return i;
            }
        }
    }
    
    public static int checkedAdd(final int n, final int n2) {
        final long n3 = n2 + (long)n;
        MathPreconditions.checkNoOverflow(n3 == (int)n3);
        return (int)n3;
    }
    
    public static int checkedMultiply(final int n, final int n2) {
        final long n3 = n2 * (long)n;
        MathPreconditions.checkNoOverflow(n3 == (int)n3);
        return (int)n3;
    }
    
    public static int checkedPow(int n, int checkedMultiply) {
        final int n2 = 1;
        boolean b = false;
        final boolean b2 = false;
        final int n3 = 0;
        MathPreconditions.checkNonNegative("exponent", checkedMultiply);
        switch (n) {
            default: {
                int n4 = 1;
                int n5 = checkedMultiply;
                while (true) {
                    switch (n5) {
                        default: {
                            if ((n5 & 0x1) == 0x0) {
                                checkedMultiply = n4;
                            }
                            else {
                                checkedMultiply = checkedMultiply(n4, n);
                            }
                            final int n6 = n5 >> 1;
                            n4 = checkedMultiply;
                            n5 = n6;
                            if (n6 > 0) {
                                boolean b3;
                                if (-46340 > n) {
                                    b3 = false;
                                }
                                else {
                                    b3 = true;
                                }
                                boolean b4;
                                if (n > 46340) {
                                    b4 = false;
                                }
                                else {
                                    b4 = true;
                                }
                                MathPreconditions.checkNoOverflow(b4 & b3);
                                n *= n;
                                n4 = checkedMultiply;
                                n5 = n6;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            return n4;
                        }
                        case 1: {
                            return checkedMultiply(n4, n);
                        }
                    }
                }
                break;
            }
            case 0: {
                if (checkedMultiply != 0) {
                    n = n3;
                }
                else {
                    n = 1;
                }
                return n;
            }
            case 1: {
                return 1;
            }
            case -1: {
                n = n2;
                if ((checkedMultiply & 0x1) != 0x0) {
                    n = -1;
                }
                return n;
            }
            case 2: {
                if (checkedMultiply < 31) {
                    b = true;
                }
                MathPreconditions.checkNoOverflow(b);
                return 1 << checkedMultiply;
            }
            case -2: {
                MathPreconditions.checkNoOverflow(checkedMultiply < 32 || b2);
                if ((checkedMultiply & 0x1) != 0x0) {
                    n = -1 << checkedMultiply;
                }
                else {
                    n = 1 << checkedMultiply;
                }
                return n;
            }
        }
    }
    
    public static int checkedSubtract(final int n, final int n2) {
        final long n3 = n - (long)n2;
        MathPreconditions.checkNoOverflow(n3 == (int)n3);
        return (int)n3;
    }
    
    public static int divide(int abs, int a, final RoundingMode roundingMode) {
        boolean b = true;
        final int n = 0;
        Preconditions.checkNotNull(roundingMode);
        if (a == 0) {
            throw new ArithmeticException("/ by zero");
        }
        final int n2 = abs / a;
        final int a2 = abs - a * n2;
        if (a2 == 0) {
            return n2;
        }
        final int n3 = (abs ^ a) >> 31 | 0x1;
        abs = n;
        while (true) {
            switch (roundingMode) {
                default: {
                    throw new AssertionError();
                }
                case UNNECESSARY: {
                    if (a2 != 0) {
                        b = false;
                    }
                    MathPreconditions.checkRoundingUnnecessary(b);
                    abs = n;
                    break Label_0135;
                }
                case UP: {
                    abs = 1;
                }
                case DOWN: {
                    if (abs == 0) {
                        abs = n2;
                    }
                    else {
                        abs = n2 + n3;
                    }
                    return abs;
                }
                case CEILING: {
                    abs = n;
                    if (n3 > 0) {
                        abs = 1;
                    }
                    continue;
                }
                case FLOOR: {
                    abs = n;
                    if (n3 < 0) {
                        abs = 1;
                    }
                    continue;
                }
                case HALF_DOWN:
                case HALF_UP:
                case HALF_EVEN: {
                    abs = Math.abs(a2);
                    a = abs - (Math.abs(a) - abs);
                    if (a == 0) {
                        if (roundingMode != RoundingMode.HALF_UP) {
                            if (roundingMode != RoundingMode.HALF_EVEN) {
                                abs = 0;
                            }
                            else {
                                abs = 1;
                            }
                            if ((n2 & 0x1) == 0x0) {
                                a = 0;
                            }
                            else {
                                a = 1;
                            }
                            if ((a & abs) == 0x0) {
                                abs = n;
                                continue;
                            }
                        }
                        abs = 1;
                        continue;
                    }
                    abs = n;
                    if (a > 0) {
                        abs = 1;
                    }
                    continue;
                }
            }
            break;
        }
    }
    
    public static int factorial(int n) {
        MathPreconditions.checkNonNegative("n", n);
        if (n >= IntMath.factorials.length) {
            n = Integer.MAX_VALUE;
        }
        else {
            n = IntMath.factorials[n];
        }
        return n;
    }
    
    public static int gcd(int i, int j) {
        MathPreconditions.checkNonNegative("a", i);
        MathPreconditions.checkNonNegative("b", j);
        if (i == 0) {
            return j;
        }
        if (j != 0) {
            final int numberOfTrailingZeros = Integer.numberOfTrailingZeros(i);
            final int n = i >> numberOfTrailingZeros;
            final int numberOfTrailingZeros2 = Integer.numberOfTrailingZeros(j);
            int n2;
            int k;
            for (i = j >> numberOfTrailingZeros2, j = n; j != i; j = (n2 >> 31 & n2), k = n2 - j - j, i += j, j = k >> Integer.numberOfTrailingZeros(k)) {
                n2 = j - i;
            }
            return j << Math.min(numberOfTrailingZeros, numberOfTrailingZeros2);
        }
        return i;
    }
    
    public static boolean isPowerOfTwo(int n) {
        final int n2 = 0;
        boolean b;
        if (n <= 0) {
            b = false;
        }
        else {
            b = true;
        }
        if ((n - 1 & n) != 0x0) {
            n = n2;
        }
        else {
            n = 1;
        }
        return (n & (b ? 1 : 0)) != 0x0;
    }
    
    @VisibleForTesting
    static int lessThanBranchFree(final int n, final int n2) {
        return ~(~(n - n2)) >>> 31;
    }
    
    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int log10(final int n, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", n);
        final int log10Floor = log10Floor(n);
        final int n2 = IntMath.powersOf10[log10Floor];
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(n == n2);
            }
            case DOWN:
            case FLOOR: {
                return log10Floor;
            }
            case UP:
            case CEILING: {
                return lessThanBranchFree(n2, n) + log10Floor;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                return lessThanBranchFree(IntMath.halfPowersOf10[log10Floor], n) + log10Floor;
            }
        }
    }
    
    private static int log10Floor(final int i) {
        final byte b = IntMath.maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(i)];
        return b - lessThanBranchFree(i, IntMath.powersOf10[b]);
    }
    
    public static int log2(final int n, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", n);
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(n));
            }
            case DOWN:
            case FLOOR: {
                return 31 - Integer.numberOfLeadingZeros(n);
            }
            case UP:
            case CEILING: {
                return 32 - Integer.numberOfLeadingZeros(n - 1);
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(n);
                return 31 - numberOfLeadingZeros + lessThanBranchFree(-1257966797 >>> numberOfLeadingZeros, n);
            }
        }
    }
    
    public static int mean(final int n, final int n2) {
        return (n & n2) + ((n ^ n2) >> 1);
    }
    
    public static int mod(int n, final int i) {
        if (i > 0) {
            final int n2 = n % i;
            if ((n = n2) < 0) {
                n = n2 + i;
            }
            return n;
        }
        throw new ArithmeticException("Modulus " + i + " must be > 0");
    }
    
    @GwtIncompatible("failing tests")
    public static int pow(int n, int n2) {
        final int n3 = 0;
        final int n4 = 0;
        final int n5 = 1;
        MathPreconditions.checkNonNegative("exponent", n2);
        switch (n) {
            default: {
                int n6 = 1;
                while (true) {
                    switch (n2) {
                        default: {
                            int n7;
                            if ((n2 & 0x1) != 0x0) {
                                n7 = n;
                            }
                            else {
                                n7 = 1;
                            }
                            n *= n;
                            n2 >>= 1;
                            n6 *= n7;
                            continue;
                        }
                        case 0: {
                            return n6;
                        }
                        case 1: {
                            return n * n6;
                        }
                    }
                }
                break;
            }
            case 0: {
                if (n2 != 0) {
                    n = n4;
                }
                else {
                    n = 1;
                }
                return n;
            }
            case 1: {
                return 1;
            }
            case -1: {
                n = n5;
                if ((n2 & 0x1) != 0x0) {
                    n = -1;
                }
                return n;
            }
            case 2: {
                if (n2 >= 32) {
                    n = n3;
                }
                else {
                    n = 1 << n2;
                }
                return n;
            }
            case -2: {
                if (n2 >= 32) {
                    return 0;
                }
                if ((n2 & 0x1) != 0x0) {
                    n = -(1 << n2);
                }
                else {
                    n = 1 << n2;
                }
                return n;
            }
        }
    }
    
    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int sqrt(final int n, final RoundingMode roundingMode) {
        MathPreconditions.checkNonNegative("x", n);
        final int sqrtFloor = sqrtFloor(n);
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == n);
            }
            case DOWN:
            case FLOOR: {
                return sqrtFloor;
            }
            case UP:
            case CEILING: {
                return lessThanBranchFree(sqrtFloor * sqrtFloor, n) + sqrtFloor;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                return lessThanBranchFree(sqrtFloor * sqrtFloor + sqrtFloor, n) + sqrtFloor;
            }
        }
    }
    
    private static int sqrtFloor(final int n) {
        return (int)Math.sqrt(n);
    }
}
