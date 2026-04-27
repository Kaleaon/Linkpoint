package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedLongs;
import java.math.RoundingMode;

@GwtCompatible(emulated = true)
public final class LongMath {
    @VisibleForTesting
    static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
    @VisibleForTesting
    static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
    static final int[] biggestBinomials = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66};
    @VisibleForTesting
    static final int[] biggestSimpleBinomials = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61};
    static final long[] factorials = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] halfPowersOf10 = {3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L};
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = {19, Ascii.DC2, Ascii.DC2, Ascii.DC2, Ascii.DC2, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, Ascii.SO, Ascii.SO, Ascii.SO, Ascii.CR, Ascii.CR, Ascii.CR, Ascii.FF, Ascii.FF, Ascii.FF, Ascii.FF, Ascii.VT, Ascii.VT, Ascii.VT, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};
    private static final long[][] millerRabinBaseSets = {new long[]{291830, 126401071349994536L}, new long[]{885594168, 725270293939359937L, 3569819667048198375L}, new long[]{273919523040L, 15, 7363882082L, 992620450144556L}, new long[]{47636622961200L, 2, 2570940, 211991001, 3749873356L}, new long[]{7999252175582850L, 2, 4130806001517L, 149795463772692060L, 186635894390467037L, 3967304179347715805L}, new long[]{585226005592931976L, 2, 123635709730000L, 9233062284813009L, 43835965440333360L, 761179012939631437L, 1263739024124850375L}, new long[]{Long.MAX_VALUE, 2, 325, 9375, 28178, 450775, 9780504, 1795265022}};
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] powersOf10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};

    /* renamed from: com.google.common.math.LongMath$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$math$RoundingMode = new int[RoundingMode.values().length];

        static {
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.UNNECESSARY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.DOWN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.FLOOR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.UP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.CEILING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_DOWN.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_UP.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_EVEN.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private enum MillerRabinTester {
        SMALL {
            /* access modifiers changed from: package-private */
            public long mulMod(long j, long j2, long j3) {
                return (j * j2) % j3;
            }

            /* access modifiers changed from: package-private */
            public long squareMod(long j, long j2) {
                return (j * j) % j2;
            }
        },
        LARGE {
            private long plusMod(long j, long j2, long j3) {
                return !((j > (j3 - j2) ? 1 : (j == (j3 - j2) ? 0 : -1)) < 0) ? (j + j2) - j3 : j + j2;
            }

            private long times2ToThe32Mod(long j, long j2) {
                int i = 32;
                do {
                    int min = Math.min(i, Long.numberOfLeadingZeros(j));
                    j = UnsignedLongs.remainder(j << min, j2);
                    i -= min;
                } while (i > 0);
                return j;
            }

            /* access modifiers changed from: package-private */
            public long mulMod(long j, long j2, long j3) {
                long j4 = j >>> 32;
                long j5 = j2 >>> 32;
                long j6 = j & 4294967295L;
                long j7 = j2 & 4294967295L;
                long times2ToThe32Mod = (j4 * j7) + times2ToThe32Mod(j4 * j5, j3);
                if (!(times2ToThe32Mod >= 0)) {
                    times2ToThe32Mod = UnsignedLongs.remainder(times2ToThe32Mod, j3);
                }
                return plusMod(times2ToThe32Mod(times2ToThe32Mod + (j6 * j5), j3), UnsignedLongs.remainder(j6 * j7, j3), j3);
            }

            /* access modifiers changed from: package-private */
            public long squareMod(long j, long j2) {
                long j3 = j >>> 32;
                long j4 = j & 4294967295L;
                long times2ToThe32Mod = times2ToThe32Mod(j3 * j3, j2);
                long j5 = j3 * j4 * 2;
                if (!(j5 >= 0)) {
                    j5 = UnsignedLongs.remainder(j5, j2);
                }
                return plusMod(times2ToThe32Mod(j5 + times2ToThe32Mod, j2), UnsignedLongs.remainder(j4 * j4, j2), j2);
            }
        };

        private long powMod(long j, long j2, long j3) {
            long j4 = 1;
            long j5 = j;
            while (j2 != 0) {
                if ((1 & j2) != 0) {
                    j4 = mulMod(j4, j5, j3);
                }
                j5 = squareMod(j5, j3);
                j2 >>= 1;
            }
            return j4;
        }

        static boolean test(long j, long j2) {
            return (!((j2 > LongMath.FLOOR_SQRT_MAX_LONG ? 1 : (j2 == LongMath.FLOOR_SQRT_MAX_LONG ? 0 : -1)) > 0) ? SMALL : LARGE).testWitness(j, j2);
        }

        private boolean testWitness(long j, long j2) {
            int numberOfTrailingZeros = Long.numberOfTrailingZeros(j2 - 1);
            long j3 = (j2 - 1) >> numberOfTrailingZeros;
            long j4 = j % j2;
            if (j4 == 0) {
                return true;
            }
            long powMod = powMod(j4, j3, j2);
            if (powMod == 1) {
                return true;
            }
            int i = 0;
            while (powMod != j2 - 1) {
                i++;
                if (i == numberOfTrailingZeros) {
                    return false;
                }
                powMod = squareMod(powMod, j2);
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public abstract long mulMod(long j, long j2, long j3);

        /* access modifiers changed from: package-private */
        public abstract long squareMod(long j, long j2);
    }

    private LongMath() {
    }

    public static long binomial(int i, int i2) {
        long j;
        long j2;
        long j3;
        int i3;
        long j4 = 1;
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", i2);
        Preconditions.checkArgument(i2 <= i, "k (%s) > n (%s)", Integer.valueOf(i2), Integer.valueOf(i));
        if (i2 > (i >> 1)) {
            i2 = i - i2;
        }
        switch (i2) {
            case 0:
                return 1;
            case 1:
                return (long) i;
            default:
                if (i < factorials.length) {
                    return factorials[i] / (factorials[i2] * factorials[i - i2]);
                }
                if (i2 >= biggestBinomials.length || i > biggestBinomials[i2]) {
                    return Long.MAX_VALUE;
                }
                if (i2 < biggestSimpleBinomials.length && i <= biggestSimpleBinomials[i2]) {
                    int i4 = i - 1;
                    long j5 = (long) i;
                    for (int i5 = 2; i5 <= i2; i5++) {
                        j5 = (j5 * ((long) i4)) / ((long) i5);
                        i4--;
                    }
                    return j5;
                }
                int log2 = log2((long) i, RoundingMode.CEILING);
                int i6 = 2;
                int i7 = log2;
                int i8 = i - 1;
                long j6 = (long) i;
                long j7 = 1;
                while (i6 <= i2) {
                    if (i7 + log2 >= 63) {
                        j3 = multiplyFraction(j7, j6, j4);
                        j2 = (long) i8;
                        j = (long) i6;
                        i3 = log2;
                    } else {
                        long j8 = ((long) i8) * j6;
                        j = j4 * ((long) i6);
                        j2 = j8;
                        j3 = j7;
                        i3 = i7 + log2;
                    }
                    i7 = i3;
                    i8--;
                    i6++;
                    j7 = j3;
                    long j9 = j2;
                    j4 = j;
                    j6 = j9;
                }
                return multiplyFraction(j7, j6, j4);
        }
    }

    @GwtIncompatible("TODO")
    public static long checkedAdd(long j, long j2) {
        boolean z = true;
        long j3 = j + j2;
        boolean z2 = !(((j ^ j2) > 0 ? 1 : ((j ^ j2) == 0 ? 0 : -1)) >= 0);
        if ((j ^ j3) < 0) {
            z = false;
        }
        MathPreconditions.checkNoOverflow(z | z2);
        return j3;
    }

    @GwtIncompatible("TODO")
    public static long checkedMultiply(long j, long j2) {
        boolean z = false;
        int numberOfLeadingZeros = Long.numberOfLeadingZeros(j) + Long.numberOfLeadingZeros(j ^ -1) + Long.numberOfLeadingZeros(j2) + Long.numberOfLeadingZeros(j2 ^ -1);
        if (numberOfLeadingZeros > 65) {
            return j * j2;
        }
        MathPreconditions.checkNoOverflow(numberOfLeadingZeros >= 64);
        MathPreconditions.checkNoOverflow((j2 != Long.MIN_VALUE) | (!((j > 0 ? 1 : (j == 0 ? 0 : -1)) < 0)));
        long j3 = j * j2;
        if (j == 0 || j3 / j == j2) {
            z = true;
        }
        MathPreconditions.checkNoOverflow(z);
        return j3;
    }

    @GwtIncompatible("TODO")
    public static long checkedPow(long j, int i) {
        boolean z;
        long j2 = 1;
        boolean z2 = false;
        MathPreconditions.checkNonNegative("exponent", i);
        if (!(!((j > 2 ? 1 : (j == 2 ? 0 : -1)) > 0)) || !(!((j > -2 ? 1 : (j == -2 ? 0 : -1)) < 0))) {
            while (true) {
                switch (i) {
                    case 0:
                        return j2;
                    case 1:
                        return checkedMultiply(j2, j);
                    default:
                        if ((i & 1) != 0) {
                            j2 = checkedMultiply(j2, j);
                        }
                        i >>= 1;
                        if (i > 0) {
                            if (!(-3037000499L > j)) {
                                if (!(j > FLOOR_SQRT_MAX_LONG)) {
                                    z = true;
                                    MathPreconditions.checkNoOverflow(z);
                                    j *= j;
                                }
                            }
                            z = false;
                            MathPreconditions.checkNoOverflow(z);
                            j *= j;
                        }
                }
            }
        } else {
            switch ((int) j) {
                case -2:
                    if (i < 64) {
                        z2 = true;
                    }
                    MathPreconditions.checkNoOverflow(z2);
                    return (i & 1) != 0 ? -1 << i : 1 << i;
                case -1:
                    return (i & 1) != 0 ? -1 : 1;
                case 0:
                    return i != 0 ? 0 : 1;
                case 1:
                    return 1;
                case 2:
                    MathPreconditions.checkNoOverflow(i < 63);
                    return 1 << i;
                default:
                    throw new AssertionError();
            }
        }
    }

    @GwtIncompatible("TODO")
    public static long checkedSubtract(long j, long j2) {
        boolean z = true;
        long j3 = j - j2;
        boolean z2 = !(((j ^ j2) > 0 ? 1 : ((j ^ j2) == 0 ? 0 : -1)) < 0);
        if ((j ^ j3) < 0) {
            z = false;
        }
        MathPreconditions.checkNoOverflow(z | z2);
        return j3;
    }

    @GwtIncompatible("TODO")
    public static long divide(long j, long j2, RoundingMode roundingMode) {
        boolean z;
        Preconditions.checkNotNull(roundingMode);
        long j3 = j / j2;
        long j4 = j - (j2 * j3);
        if (j4 == 0) {
            return j3;
        }
        int i = ((int) ((j ^ j2) >> 63)) | 1;
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(j4 == 0);
                break;
            case 2:
                break;
            case 3:
                if (i < 0) {
                    z = true;
                    break;
                } else {
                    z = false;
                    break;
                }
            case 4:
                z = true;
                break;
            case 5:
                if (i > 0) {
                    z = true;
                    break;
                } else {
                    z = false;
                    break;
                }
            case 6:
            case 7:
            case 8:
                long abs = Math.abs(j4);
                long abs2 = abs - (Math.abs(j2) - abs);
                if (abs2 != 0) {
                    if (abs2 <= 0) {
                        z = false;
                        break;
                    } else {
                        z = true;
                        break;
                    }
                } else {
                    z = (roundingMode == RoundingMode.HALF_UP) | (((1 & j3) != 0) & (roundingMode == RoundingMode.HALF_EVEN));
                    break;
                }
            default:
                throw new AssertionError();
        }
        z = false;
        return !z ? j3 : ((long) i) + j3;
    }

    @GwtIncompatible("TODO")
    public static long factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        if (i >= factorials.length) {
            return Long.MAX_VALUE;
        }
        return factorials[i];
    }

    static boolean fitsInInt(long j) {
        return ((long) ((int) j)) == j;
    }

    public static long gcd(long j, long j2) {
        MathPreconditions.checkNonNegative("a", j);
        MathPreconditions.checkNonNegative("b", j2);
        if (j == 0) {
            return j2;
        }
        if (j2 == 0) {
            return j;
        }
        int numberOfTrailingZeros = Long.numberOfTrailingZeros(j);
        long j3 = j >> numberOfTrailingZeros;
        int numberOfTrailingZeros2 = Long.numberOfTrailingZeros(j2);
        long j4 = j2 >> numberOfTrailingZeros2;
        while (j3 != j4) {
            long j5 = j3 - j4;
            long j6 = (j5 >> 63) & j5;
            long j7 = (j5 - j6) - j6;
            j4 += j6;
            j3 = j7 >> Long.numberOfTrailingZeros(j7);
        }
        return j3 << Math.min(numberOfTrailingZeros, numberOfTrailingZeros2);
    }

    public static boolean isPowerOfTwo(long j) {
        boolean z = true;
        boolean z2 = !((j > 0 ? 1 : (j == 0 ? 0 : -1)) <= 0);
        if (((j - 1) & j) != 0) {
            z = false;
        }
        return z & z2;
    }

    @VisibleForTesting
    static int lessThanBranchFree(long j, long j2) {
        return (int) ((((j - j2) ^ -1) ^ -1) >>> 63);
    }

    @GwtIncompatible("TODO")
    public static int log10(long j, RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", j);
        int log10Floor = log10Floor(j);
        long j2 = powersOf10[log10Floor];
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(j == j2);
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return lessThanBranchFree(j2, j) + log10Floor;
            case 6:
            case 7:
            case 8:
                return lessThanBranchFree(halfPowersOf10[log10Floor], j) + log10Floor;
            default:
                throw new AssertionError();
        }
        return log10Floor;
    }

    @GwtIncompatible("TODO")
    static int log10Floor(long j) {
        byte b = maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(j)];
        return b - lessThanBranchFree(j, powersOf10[b]);
    }

    public static int log2(long j, RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", j);
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(j));
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return 64 - Long.numberOfLeadingZeros(j - 1);
            case 6:
            case 7:
            case 8:
                int numberOfLeadingZeros = Long.numberOfLeadingZeros(j);
                return (63 - numberOfLeadingZeros) + lessThanBranchFree(MAX_POWER_OF_SQRT2_UNSIGNED >>> numberOfLeadingZeros, j);
            default:
                throw new AssertionError("impossible");
        }
        return 63 - Long.numberOfLeadingZeros(j);
    }

    public static long mean(long j, long j2) {
        return (j & j2) + ((j ^ j2) >> 1);
    }

    @GwtIncompatible("TODO")
    public static int mod(long j, int i) {
        return (int) mod(j, (long) i);
    }

    @GwtIncompatible("TODO")
    public static long mod(long j, long j2) {
        boolean z = true;
        if (!(j2 > 0)) {
            throw new ArithmeticException("Modulus must be positive");
        }
        long j3 = j % j2;
        if (j3 >= 0) {
            z = false;
        }
        return !z ? j3 : j3 + j2;
    }

    static long multiplyFraction(long j, long j2, long j3) {
        if (j == 1) {
            return j2 / j3;
        }
        long gcd = gcd(j, j3);
        return (j2 / (j3 / gcd)) * (j / gcd);
    }

    @GwtIncompatible("TODO")
    public static long pow(long j, int i) {
        boolean z = true;
        MathPreconditions.checkNonNegative("exponent", i);
        if (!(-2 > j)) {
            if (j <= 2) {
                z = false;
            }
            if (!z) {
                switch ((int) j) {
                    case -2:
                        if (i >= 64) {
                            return 0;
                        }
                        return (i & 1) != 0 ? -(1 << i) : 1 << i;
                    case -1:
                        return (i & 1) != 0 ? -1 : 1;
                    case 0:
                        return i != 0 ? 0 : 1;
                    case 1:
                        return 1;
                    case 2:
                        if (i >= 64) {
                            return 0;
                        }
                        return 1 << i;
                    default:
                        throw new AssertionError();
                }
            }
        }
        long j2 = 1;
        long j3 = j;
        while (true) {
            switch (i) {
                case 0:
                    return j2;
                case 1:
                    return j2 * j3;
                default:
                    long j4 = (i & 1) != 0 ? j3 : 1;
                    j3 *= j3;
                    i >>= 1;
                    j2 = j4 * j2;
            }
        }
    }

    @GwtIncompatible("TODO")
    public static long sqrt(long j, RoundingMode roundingMode) {
        boolean z = true;
        MathPreconditions.checkNonNegative("x", j);
        if (fitsInInt(j)) {
            return (long) IntMath.sqrt((int) j, roundingMode);
        }
        long sqrt = (long) Math.sqrt((double) j);
        long j2 = sqrt * sqrt;
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                if (j2 != j) {
                    z = false;
                }
                MathPreconditions.checkRoundingUnnecessary(z);
                return sqrt;
            case 2:
            case 3:
                if (j < j2) {
                    z = false;
                }
                return !z ? sqrt - 1 : sqrt;
            case 4:
            case 5:
                if (j > j2) {
                    z = false;
                }
                return !z ? sqrt + 1 : sqrt;
            case 6:
            case 7:
            case 8:
                if (j >= j2) {
                    z = false;
                }
                long j3 = sqrt - (z ? 1 : 0);
                return j3 + ((long) lessThanBranchFree((j3 * j3) + j3, j));
            default:
                throw new AssertionError();
        }
    }
}
