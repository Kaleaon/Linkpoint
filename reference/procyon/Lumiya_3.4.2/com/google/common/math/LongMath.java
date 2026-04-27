// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.math;

import com.google.common.primitives.UnsignedLongs;
import java.math.RoundingMode;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class LongMath
{
    @VisibleForTesting
    static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
    @VisibleForTesting
    static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
    static final int[] biggestBinomials;
    @VisibleForTesting
    static final int[] biggestSimpleBinomials;
    static final long[] factorials;
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] halfPowersOf10;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros;
    private static final long[][] millerRabinBaseSets;
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] powersOf10;
    
    static {
        maxLog10ForLeadingZeros = new byte[] { 19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0 };
        powersOf10 = new long[] { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
        halfPowersOf10 = new long[] { 3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L };
        factorials = new long[] { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
        biggestBinomials = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66 };
        biggestSimpleBinomials = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61 };
        millerRabinBaseSets = new long[][] { { 291830L, 126401071349994536L }, { 885594168L, 725270293939359937L, 3569819667048198375L }, { 273919523040L, 15L, 7363882082L, 992620450144556L }, { 47636622961200L, 2L, 2570940L, 211991001L, 3749873356L }, { 7999252175582850L, 2L, 4130806001517L, 149795463772692060L, 186635894390467037L, 3967304179347715805L }, { 585226005592931976L, 2L, 123635709730000L, 9233062284813009L, 43835965440333360L, 761179012939631437L, 1263739024124850375L }, { Long.MAX_VALUE, 2L, 325L, 9375L, 28178L, 450775L, 9780504L, 1795265022L } };
    }
    
    private LongMath() {
    }
    
    public static long binomial(int i, int j) {
        long n = 1L;
        final int n2 = 2;
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        if (j > i >> 1) {
            j = i - j;
        }
        switch (j) {
            default: {
                if (i < LongMath.factorials.length) {
                    return LongMath.factorials[i] / (LongMath.factorials[j] * LongMath.factorials[i - j]);
                }
                if (j >= LongMath.biggestBinomials.length || i > LongMath.biggestBinomials[j]) {
                    return Long.MAX_VALUE;
                }
                if (j < LongMath.biggestSimpleBinomials.length && i <= LongMath.biggestSimpleBinomials[j]) {
                    final int n3 = i - 1;
                    long n4 = i;
                    i = n2;
                    int n5 = n3;
                    while (i <= j) {
                        n4 = n4 * n5 / i;
                        --n5;
                        ++i;
                    }
                    return n4;
                }
                final int log2 = log2(i, RoundingMode.CEILING);
                long n6 = i;
                final int n7 = 2;
                final int n8 = log2;
                int n9 = i - 1;
                long n10 = 1L;
                i = n8;
                long multiplyFraction;
                long n11;
                long n12;
                long n16;
                for (int k = n7; k <= j; ++k, n16 = multiplyFraction, n = n12, n6 = n11, n10 = n16) {
                    if (i + log2 >= 63) {
                        multiplyFraction = multiplyFraction(n10, n6, n);
                        n11 = n9;
                        n12 = k;
                        i = log2;
                    }
                    else {
                        final long n13 = n9;
                        final long n14 = n * k;
                        final long n15 = n13 * n6;
                        multiplyFraction = n10;
                        i += log2;
                        n12 = n14;
                        n11 = n15;
                    }
                    --n9;
                }
                return multiplyFraction(n10, n6, n);
            }
            case 0: {
                return 1L;
            }
            case 1: {
                return i;
            }
        }
    }
    
    @GwtIncompatible("TODO")
    public static long checkedAdd(final long n, final long n2) {
        final int n3 = 1;
        final long n4 = n + n2;
        int n5;
        if ((n ^ n2) >= 0L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        boolean b;
        if (n5 == 0) {
            b = true;
        }
        else {
            b = false;
        }
        int n6;
        if ((n ^ n4) < 0L) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        int n7;
        if (n6 == 0) {
            n7 = n3;
        }
        else {
            n7 = 0;
        }
        MathPreconditions.checkNoOverflow((boolean)((n7 | (b ? 1 : 0)) != 0x0));
        return n4;
    }
    
    @GwtIncompatible("TODO")
    public static long checkedMultiply(final long i, final long j) {
        final boolean b = false;
        final int n = Long.numberOfLeadingZeros(i) + Long.numberOfLeadingZeros(~i) + Long.numberOfLeadingZeros(j) + Long.numberOfLeadingZeros(~j);
        if (n <= 65) {
            MathPreconditions.checkNoOverflow(n >= 64);
            int n2;
            if (i < 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            boolean b2;
            if (n2 == 0) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            boolean b3;
            if (j != Long.MIN_VALUE) {
                b3 = true;
            }
            else {
                b3 = false;
            }
            MathPreconditions.checkNoOverflow(b3 | b2);
            final long n3 = i * j;
            boolean b4 = false;
            Label_0117: {
                if (i != 0L) {
                    b4 = b;
                    if (n3 / i != j) {
                        break Label_0117;
                    }
                }
                b4 = true;
            }
            MathPreconditions.checkNoOverflow(b4);
            return n3;
        }
        return i * j;
    }
    
    @GwtIncompatible("TODO")
    public static long checkedPow(long n, int n2) {
        long n3 = 1L;
        boolean b = false;
        MathPreconditions.checkNonNegative("exponent", n2);
        int n4;
        if (n < -2L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        boolean b2;
        if (n4 == 0) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        int n5;
        if (n > 2L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        boolean b3;
        if (n5 == 0) {
            b3 = true;
        }
        else {
            b3 = false;
        }
        if (!(b3 & b2)) {
        Label_0140_Outer:
            while (true) {
                switch (n2) {
                    default: {
                        long checkedMultiply;
                        if ((n2 & 0x1) == 0x0) {
                            checkedMultiply = n3;
                        }
                        else {
                            checkedMultiply = checkedMultiply(n3, n);
                        }
                        final int n6 = n2 >> 1;
                        n3 = checkedMultiply;
                        n2 = n6;
                        if (n6 > 0) {
                            if (-3037000499L > n) {
                                n2 = 1;
                            }
                            else {
                                n2 = 0;
                            }
                            while (true) {
                                Label_0340: {
                                    if (n2 != 0) {
                                        break Label_0340;
                                    }
                                    if (n > 3037000499L) {
                                        n2 = 1;
                                    }
                                    else {
                                        n2 = 0;
                                    }
                                    if (n2 != 0) {
                                        break Label_0340;
                                    }
                                    final boolean b4 = true;
                                    MathPreconditions.checkNoOverflow(b4);
                                    n *= n;
                                    n3 = checkedMultiply;
                                    n2 = n6;
                                    continue Label_0140_Outer;
                                }
                                final boolean b4 = false;
                                continue;
                            }
                        }
                        continue;
                    }
                    case 0: {
                        return n3;
                    }
                    case 1: {
                        return checkedMultiply(n3, n);
                    }
                }
            }
        }
        else {
            switch ((int)n) {
                default: {
                    throw new AssertionError();
                }
                case 0: {
                    if (n2 != 0) {
                        n3 = 0L;
                    }
                    return n3;
                }
                case 1: {
                    return 1L;
                }
                case -1: {
                    if ((n2 & 0x1) != 0x0) {
                        n3 = -1L;
                    }
                    return n3;
                }
                case 2: {
                    MathPreconditions.checkNoOverflow(n2 < 63);
                    return 1L << n2;
                }
                case -2: {
                    if (n2 < 64) {
                        b = true;
                    }
                    MathPreconditions.checkNoOverflow(b);
                    if ((n2 & 0x1) != 0x0) {
                        n = -1L << n2;
                    }
                    else {
                        n = 1L << n2;
                    }
                    return n;
                }
            }
        }
    }
    
    @GwtIncompatible("TODO")
    public static long checkedSubtract(final long n, final long n2) {
        final int n3 = 1;
        final long n4 = n - n2;
        int n5;
        if ((n ^ n2) < 0L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        boolean b;
        if (n5 == 0) {
            b = true;
        }
        else {
            b = false;
        }
        int n6;
        if ((n ^ n4) < 0L) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        int n7;
        if (n6 == 0) {
            n7 = n3;
        }
        else {
            n7 = 0;
        }
        MathPreconditions.checkNoOverflow((boolean)((n7 | (b ? 1 : 0)) != 0x0));
        return n4;
    }
    
    @GwtIncompatible("TODO")
    public static long divide(long abs, final long a, final RoundingMode roundingMode) {
        Preconditions.checkNotNull(roundingMode);
        final long n = abs / a;
        final long a2 = abs - a * n;
        if (a2 == 0L) {
            return n;
        }
        final int n2 = (int)((abs ^ a) >> 63) | 0x1;
        int n3 = 0;
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(a2 == 0L);
            }
            case DOWN: {
                n3 = 0;
                break;
            }
            case UP: {
                n3 = 1;
                break;
            }
            case CEILING: {
                if (n2 <= 0) {
                    n3 = 0;
                    break;
                }
                n3 = 1;
                break;
            }
            case FLOOR: {
                if (n2 >= 0) {
                    n3 = 0;
                    break;
                }
                n3 = 1;
                break;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                abs = Math.abs(a2);
                abs -= Math.abs(a) - abs;
                if (abs == 0L) {
                    boolean b;
                    if (roundingMode != RoundingMode.HALF_UP) {
                        b = false;
                    }
                    else {
                        b = true;
                    }
                    boolean b2;
                    if (roundingMode != RoundingMode.HALF_EVEN) {
                        b2 = false;
                    }
                    else {
                        b2 = true;
                    }
                    boolean b3;
                    if ((0x1L & n) != 0x0L) {
                        b3 = true;
                    }
                    else {
                        b3 = false;
                    }
                    n3 = ((b | (b3 & b2)) ? 1 : 0);
                    break;
                }
                int n4;
                if (abs <= 0L) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                if (n4 == 0) {
                    n3 = 1;
                    break;
                }
                n3 = 0;
                break;
            }
        }
        if (n3 == 0) {
            abs = n;
        }
        else {
            abs = n2 + n;
        }
        return abs;
    }
    
    @GwtIncompatible("TODO")
    public static long factorial(final int n) {
        MathPreconditions.checkNonNegative("n", n);
        long n2;
        if (n >= LongMath.factorials.length) {
            n2 = Long.MAX_VALUE;
        }
        else {
            n2 = LongMath.factorials[n];
        }
        return n2;
    }
    
    static boolean fitsInInt(final long n) {
        return (int)n == n;
    }
    
    public static long gcd(long i, long j) {
        MathPreconditions.checkNonNegative("a", i);
        MathPreconditions.checkNonNegative("b", j);
        if (i == 0L) {
            return j;
        }
        if (j == 0L) {
            return i;
        }
        final int numberOfTrailingZeros = Long.numberOfTrailingZeros(i);
        i >>= numberOfTrailingZeros;
        final int numberOfTrailingZeros2 = Long.numberOfTrailingZeros(j);
        final long n = j >> numberOfTrailingZeros2;
        long n2;
        long k;
        for (j = i, i = n; j != i; j = (n2 >> 63 & n2), k = n2 - j - j, i += j, j = k >> Long.numberOfTrailingZeros(k)) {
            n2 = j - i;
        }
        return j << Math.min(numberOfTrailingZeros, numberOfTrailingZeros2);
    }
    
    public static boolean isPowerOfTwo(final long n) {
        boolean b = true;
        int n2;
        if (n <= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        boolean b2;
        if (n2 == 0) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        if ((n - 1L & n) != 0x0L) {
            b = false;
        }
        return b & b2;
    }
    
    @VisibleForTesting
    static int lessThanBranchFree(final long n, final long n2) {
        return (int)(~(~(n - n2)) >>> 63);
    }
    
    @GwtIncompatible("TODO")
    public static int log10(final long n, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", n);
        final int log10Floor = log10Floor(n);
        final long n2 = LongMath.powersOf10[log10Floor];
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
                return lessThanBranchFree(LongMath.halfPowersOf10[log10Floor], n) + log10Floor;
            }
        }
    }
    
    @GwtIncompatible("TODO")
    static int log10Floor(final long i) {
        final byte b = LongMath.maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(i)];
        return b - lessThanBranchFree(i, LongMath.powersOf10[b]);
    }
    
    public static int log2(final long n, final RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", n);
        switch (roundingMode) {
            default: {
                throw new AssertionError((Object)"impossible");
            }
            case UNNECESSARY: {
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(n));
            }
            case DOWN:
            case FLOOR: {
                return 63 - Long.numberOfLeadingZeros(n);
            }
            case UP:
            case CEILING: {
                return 64 - Long.numberOfLeadingZeros(n - 1L);
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                final int numberOfLeadingZeros = Long.numberOfLeadingZeros(n);
                return 63 - numberOfLeadingZeros + lessThanBranchFree(-5402926248376769404L >>> numberOfLeadingZeros, n);
            }
        }
    }
    
    public static long mean(final long n, final long n2) {
        return (n & n2) + ((n ^ n2) >> 1);
    }
    
    @GwtIncompatible("TODO")
    public static int mod(final long n, final int n2) {
        return (int)mod(n, (long)n2);
    }
    
    @GwtIncompatible("TODO")
    public static long mod(long n, final long n2) {
        final int n3 = 1;
        int n4;
        if (n2 > 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            throw new ArithmeticException("Modulus must be positive");
        }
        n %= n2;
        int n5;
        if (n < 0L) {
            n5 = n3;
        }
        else {
            n5 = 0;
        }
        if (n5 != 0) {
            n += n2;
        }
        return n;
    }
    
    static long multiplyFraction(long n, final long n2, final long n3) {
        if (n == 1L) {
            return n2 / n3;
        }
        final long gcd = gcd(n, n3);
        n /= gcd;
        return n2 / (n3 / gcd) * n;
    }
    
    @GwtIncompatible("TODO")
    public static long pow(long n, int n2) {
        final int n3 = 1;
        long n4 = 0L;
        final long n5 = 1L;
        MathPreconditions.checkNonNegative("exponent", n2);
        int n6;
        if (-2L > n) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        if (n6 == 0) {
            int n7;
            if (n > 2L) {
                n7 = n3;
            }
            else {
                n7 = 0;
            }
            if (n7 == 0) {
                switch ((int)n) {
                    default: {
                        throw new AssertionError();
                    }
                    case 0: {
                        if (n2 == 0) {
                            n4 = 1L;
                        }
                        return n4;
                    }
                    case 1: {
                        return 1L;
                    }
                    case -1: {
                        n = n5;
                        if ((n2 & 0x1) != 0x0) {
                            n = -1L;
                        }
                        return n;
                    }
                    case 2: {
                        if (n2 < 64) {
                            n4 = 1L << n2;
                        }
                        return n4;
                    }
                    case -2: {
                        if (n2 >= 64) {
                            return 0L;
                        }
                        if ((n2 & 0x1) != 0x0) {
                            n = -(1L << n2);
                        }
                        else {
                            n = 1L << n2;
                        }
                        return n;
                    }
                }
            }
        }
        long n8 = 1L;
        while (true) {
            switch (n2) {
                default: {
                    long n9;
                    if ((n2 & 0x1) != 0x0) {
                        n9 = n;
                    }
                    else {
                        n9 = 1L;
                    }
                    n *= n;
                    n2 >>= 1;
                    n8 *= n9;
                    continue;
                }
                case 0: {
                    return n8;
                }
                case 1: {
                    return n8 * n;
                }
            }
        }
    }
    
    @GwtIncompatible("TODO")
    public static long sqrt(final long n, final RoundingMode roundingMode) {
        int n2 = 1;
        final int n3 = 1;
        final int n4 = 1;
        boolean b = true;
        MathPreconditions.checkNonNegative("x", n);
        if (fitsInInt(n)) {
            return IntMath.sqrt((int)n, roundingMode);
        }
        final long n5 = (long)Math.sqrt((double)n);
        final long n6 = n5 * n5;
        switch (roundingMode) {
            default: {
                throw new AssertionError();
            }
            case UNNECESSARY: {
                if (n6 != n) {
                    b = false;
                }
                MathPreconditions.checkRoundingUnnecessary(b);
                return n5;
            }
            case DOWN:
            case FLOOR: {
                if (n < n6) {
                    n2 = 0;
                }
                if (n2 == 0) {
                    return n5 - 1L;
                }
                return n5;
            }
            case UP:
            case CEILING: {
                int n7;
                if (n <= n6) {
                    n7 = n3;
                }
                else {
                    n7 = 0;
                }
                if (n7 == 0) {
                    return n5 + 1L;
                }
                return n5;
            }
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN: {
                int n8;
                if (n >= n6) {
                    n8 = 1;
                }
                else {
                    n8 = 0;
                }
                int n9;
                if (n8 == 0) {
                    n9 = n4;
                }
                else {
                    n9 = 0;
                }
                final long n10 = n5 - n9;
                return n10 + lessThanBranchFree(n10 * n10 + n10, n);
            }
        }
    }
    
    private enum MillerRabinTester
    {
        LARGE {
            private long plusMod(long n, final long n2, final long n3) {
                int n4;
                if (n < n3 - n2) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                if (n4 == 0) {
                    n = n + n2 - n3;
                }
                else {
                    n += n2;
                }
                return n;
            }
            
            private long times2ToThe32Mod(long i, final long n) {
                int a = 32;
                int j;
                long remainder;
                do {
                    final int min = Math.min(a, Long.numberOfLeadingZeros(i));
                    remainder = UnsignedLongs.remainder(i << min, n);
                    j = (a -= min);
                    i = remainder;
                } while (j > 0);
                return remainder;
            }
            
            @Override
            long mulMod(long remainder, long n, final long n2) {
                final long n3 = remainder >>> 32;
                final long n4 = n >>> 32;
                final long n5 = remainder & 0xFFFFFFFFL;
                final long n6 = n & 0xFFFFFFFFL;
                n = n3 * n6 + this.times2ToThe32Mod(n3 * n4, n2);
                int n7;
                if (n >= 0L) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                remainder = n;
                if (n7 == 0) {
                    remainder = UnsignedLongs.remainder(n, n2);
                }
                return this.plusMod(this.times2ToThe32Mod(remainder + n5 * n4, n2), UnsignedLongs.remainder(n5 * n6, n2), n2);
            }
            
            @Override
            long squareMod(long remainder, final long n) {
                final long n2 = remainder >>> 32;
                final long n3 = remainder & 0xFFFFFFFFL;
                final long times2ToThe32Mod = this.times2ToThe32Mod(n2 * n2, n);
                final long n4 = n2 * n3 * 2L;
                int n5;
                if (n4 >= 0L) {
                    n5 = 1;
                }
                else {
                    n5 = 0;
                }
                remainder = n4;
                if (n5 == 0) {
                    remainder = UnsignedLongs.remainder(n4, n);
                }
                return this.plusMod(this.times2ToThe32Mod(remainder + times2ToThe32Mod, n), UnsignedLongs.remainder(n3 * n3, n), n);
            }
        }, 
        SMALL {
            @Override
            long mulMod(final long n, final long n2, final long n3) {
                return n * n2 % n3;
            }
            
            @Override
            long squareMod(final long n, final long n2) {
                return n * n % n2;
            }
        };
        
        private long powMod(long squareMod, long n, final long n2) {
            long n3 = 1L;
            while (n != 0L) {
                long mulMod = n3;
                if ((0x1L & n) != 0x0L) {
                    mulMod = this.mulMod(n3, squareMod, n2);
                }
                squareMod = this.squareMod(squareMod, n2);
                n >>= 1;
                n3 = mulMod;
            }
            return n3;
        }
        
        static boolean test(final long n, final long n2) {
            int n3;
            if (n2 > 3037000499L) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            MillerRabinTester millerRabinTester;
            if (n3 == 0) {
                millerRabinTester = MillerRabinTester.SMALL;
            }
            else {
                millerRabinTester = MillerRabinTester.LARGE;
            }
            return millerRabinTester.testWitness(n, n2);
        }
        
        private boolean testWitness(long n, final long n2) {
            final int numberOfTrailingZeros = Long.numberOfTrailingZeros(n2 - 1L);
            n %= n2;
            if (n == 0L) {
                return true;
            }
            n = this.powMod(n, n2 - 1L >> numberOfTrailingZeros, n2);
            if (n == 1L) {
                return true;
            }
            int n3 = 0;
            while (n != n2 - 1L) {
                if (++n3 == numberOfTrailingZeros) {
                    return false;
                }
                n = this.squareMod(n, n2);
            }
            return true;
        }
        
        abstract long mulMod(final long p0, final long p1, final long p2);
        
        abstract long squareMod(final long p0, final long p1);
    }
}
