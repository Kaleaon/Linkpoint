package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated = true)
public final class IntMath {
    @VisibleForTesting
    static final int FLOOR_SQRT_MAX_INT = 46340;
    @VisibleForTesting
    static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
    @VisibleForTesting
    static int[] biggestBinomials = {Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33};
    private static final int[] factorials = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600};
    @VisibleForTesting
    static final int[] halfPowersOf10 = {3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = {9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0};
    @VisibleForTesting
    static final int[] powersOf10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

    /* renamed from: com.google.common.math.IntMath$1  reason: invalid class name */
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

    private IntMath() {
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int binomial(int i, int i2) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", i2);
        Preconditions.checkArgument(i2 <= i, "k (%s) > n (%s)", Integer.valueOf(i2), Integer.valueOf(i));
        if (i2 > (i >> 1)) {
            i2 = i - i2;
        }
        if (i2 >= biggestBinomials.length || i > biggestBinomials[i2]) {
            return Integer.MAX_VALUE;
        }
        switch (i2) {
            case 0:
                return 1;
            case 1:
                return i;
            default:
                long j = 1;
                for (int i3 = 0; i3 < i2; i3++) {
                    j = (j * ((long) (i - i3))) / ((long) (i3 + 1));
                }
                return (int) j;
        }
    }

    public static int checkedAdd(int i, int i2) {
        long j = ((long) i2) + ((long) i);
        MathPreconditions.checkNoOverflow(j == ((long) ((int) j)));
        return (int) j;
    }

    public static int checkedMultiply(int i, int i2) {
        long j = ((long) i2) * ((long) i);
        MathPreconditions.checkNoOverflow(j == ((long) ((int) j)));
        return (int) j;
    }

    public static int checkedPow(int i, int i2) {
        boolean z = false;
        MathPreconditions.checkNonNegative("exponent", i2);
        switch (i) {
            case -2:
                if (i2 < 32) {
                    z = true;
                }
                MathPreconditions.checkNoOverflow(z);
                return (i2 & 1) != 0 ? -1 << i2 : 1 << i2;
            case -1:
                return (i2 & 1) != 0 ? -1 : 1;
            case 0:
                return i2 != 0 ? 0 : 1;
            case 1:
                return 1;
            case 2:
                if (i2 < 31) {
                    z = true;
                }
                MathPreconditions.checkNoOverflow(z);
                return 1 << i2;
            default:
                int i3 = 1;
                while (true) {
                    switch (i2) {
                        case 0:
                            return i3;
                        case 1:
                            return checkedMultiply(i3, i);
                        default:
                            if ((i2 & 1) != 0) {
                                i3 = checkedMultiply(i3, i);
                            }
                            i2 >>= 1;
                            if (i2 > 0) {
                                MathPreconditions.checkNoOverflow((i <= FLOOR_SQRT_MAX_INT) & (-46340 <= i));
                                i *= i;
                            }
                    }
                }
        }
    }

    public static int checkedSubtract(int i, int i2) {
        long j = ((long) i) - ((long) i2);
        MathPreconditions.checkNoOverflow(j == ((long) ((int) j)));
        return (int) j;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0065, code lost:
        if ((((r2 & 1) != 0) & (r9 == java.math.RoundingMode.HALF_EVEN)) == false) goto L_0x0036;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int divide(int r7, int r8, java.math.RoundingMode r9) {
        /*
            r0 = 1
            r1 = 0
            com.google.common.base.Preconditions.checkNotNull(r9)
            if (r8 == 0) goto L_0x0026
            int r2 = r7 / r8
            int r3 = r8 * r2
            int r3 = r7 - r3
            if (r3 == 0) goto L_0x002f
            r4 = r7 ^ r8
            int r4 = r4 >> 31
            r5 = r4 | 1
            int[] r4 = com.google.common.math.IntMath.AnonymousClass1.$SwitchMap$java$math$RoundingMode
            int r6 = r9.ordinal()
            r4 = r4[r6]
            switch(r4) {
                case 1: goto L_0x0030;
                case 2: goto L_0x0036;
                case 3: goto L_0x0040;
                case 4: goto L_0x003a;
                case 5: goto L_0x003c;
                case 6: goto L_0x0044;
                case 7: goto L_0x0044;
                case 8: goto L_0x0044;
                default: goto L_0x0020;
            }
        L_0x0020:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0026:
            java.lang.ArithmeticException r0 = new java.lang.ArithmeticException
            java.lang.String r1 = "/ by zero"
            r0.<init>(r1)
            throw r0
        L_0x002f:
            return r2
        L_0x0030:
            if (r3 == 0) goto L_0x0033
            r0 = r1
        L_0x0033:
            com.google.common.math.MathPreconditions.checkRoundingUnnecessary(r0)
        L_0x0036:
            if (r1 != 0) goto L_0x006c
            r0 = r2
        L_0x0039:
            return r0
        L_0x003a:
            r1 = r0
            goto L_0x0036
        L_0x003c:
            if (r5 <= 0) goto L_0x0036
            r1 = r0
            goto L_0x0036
        L_0x0040:
            if (r5 >= 0) goto L_0x0036
            r1 = r0
            goto L_0x0036
        L_0x0044:
            int r3 = java.lang.Math.abs(r3)
            int r4 = java.lang.Math.abs(r8)
            int r4 = r4 - r3
            int r3 = r3 - r4
            if (r3 == 0) goto L_0x0054
            if (r3 <= 0) goto L_0x0036
            r1 = r0
            goto L_0x0036
        L_0x0054:
            java.math.RoundingMode r3 = java.math.RoundingMode.HALF_UP
            if (r9 != r3) goto L_0x005a
        L_0x0058:
            r1 = r0
            goto L_0x0036
        L_0x005a:
            java.math.RoundingMode r3 = java.math.RoundingMode.HALF_EVEN
            if (r9 == r3) goto L_0x0068
            r4 = r1
        L_0x005f:
            r3 = r2 & 1
            if (r3 != 0) goto L_0x006a
            r3 = r1
        L_0x0064:
            r3 = r3 & r4
            if (r3 != 0) goto L_0x0058
            goto L_0x0036
        L_0x0068:
            r4 = r0
            goto L_0x005f
        L_0x006a:
            r3 = r0
            goto L_0x0064
        L_0x006c:
            int r0 = r2 + r5
            goto L_0x0039
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.math.IntMath.divide(int, int, java.math.RoundingMode):int");
    }

    public static int factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        if (i >= factorials.length) {
            return Integer.MAX_VALUE;
        }
        return factorials[i];
    }

    public static int gcd(int i, int i2) {
        MathPreconditions.checkNonNegative("a", i);
        MathPreconditions.checkNonNegative("b", i2);
        if (i == 0) {
            return i2;
        }
        if (i2 == 0) {
            return i;
        }
        int numberOfTrailingZeros = Integer.numberOfTrailingZeros(i);
        int i3 = i >> numberOfTrailingZeros;
        int numberOfTrailingZeros2 = Integer.numberOfTrailingZeros(i2);
        int i4 = i2 >> numberOfTrailingZeros2;
        while (i3 != i4) {
            int i5 = i3 - i4;
            int i6 = (i5 >> 31) & i5;
            int i7 = (i5 - i6) - i6;
            i4 += i6;
            i3 = i7 >> Integer.numberOfTrailingZeros(i7);
        }
        return i3 << Math.min(numberOfTrailingZeros, numberOfTrailingZeros2);
    }

    public static boolean isPowerOfTwo(int i) {
        boolean z = false;
        boolean z2 = i > 0;
        if (((i - 1) & i) == 0) {
            z = true;
        }
        return z & z2;
    }

    @VisibleForTesting
    static int lessThanBranchFree(int i, int i2) {
        return (((i - i2) ^ -1) ^ -1) >>> 31;
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int log10(int i, RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", i);
        int log10Floor = log10Floor(i);
        int i2 = powersOf10[log10Floor];
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(i == i2);
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return lessThanBranchFree(i2, i) + log10Floor;
            case 6:
            case 7:
            case 8:
                return lessThanBranchFree(halfPowersOf10[log10Floor], i) + log10Floor;
            default:
                throw new AssertionError();
        }
        return log10Floor;
    }

    private static int log10Floor(int i) {
        byte b = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(i)];
        return b - lessThanBranchFree(i, powersOf10[b]);
    }

    public static int log2(int i, RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", i);
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(i));
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return 32 - Integer.numberOfLeadingZeros(i - 1);
            case 6:
            case 7:
            case 8:
                int numberOfLeadingZeros = Integer.numberOfLeadingZeros(i);
                return (31 - numberOfLeadingZeros) + lessThanBranchFree(MAX_POWER_OF_SQRT2_UNSIGNED >>> numberOfLeadingZeros, i);
            default:
                throw new AssertionError();
        }
        return 31 - Integer.numberOfLeadingZeros(i);
    }

    public static int mean(int i, int i2) {
        return (i & i2) + ((i ^ i2) >> 1);
    }

    public static int mod(int i, int i2) {
        if (i2 > 0) {
            int i3 = i % i2;
            return i3 < 0 ? i3 + i2 : i3;
        }
        throw new ArithmeticException("Modulus " + i2 + " must be > 0");
    }

    @GwtIncompatible("failing tests")
    public static int pow(int i, int i2) {
        MathPreconditions.checkNonNegative("exponent", i2);
        switch (i) {
            case -2:
                if (i2 >= 32) {
                    return 0;
                }
                return (i2 & 1) != 0 ? -(1 << i2) : 1 << i2;
            case -1:
                return (i2 & 1) != 0 ? -1 : 1;
            case 0:
                return i2 != 0 ? 0 : 1;
            case 1:
                return 1;
            case 2:
                if (i2 >= 32) {
                    return 0;
                }
                return 1 << i2;
            default:
                int i3 = 1;
                int i4 = i;
                while (true) {
                    switch (i2) {
                        case 0:
                            return i3;
                        case 1:
                            return i4 * i3;
                        default:
                            int i5 = (i2 & 1) != 0 ? i4 : 1;
                            i4 *= i4;
                            i2 >>= 1;
                            i3 = i5 * i3;
                    }
                }
        }
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int sqrt(int i, RoundingMode roundingMode) {
        MathPreconditions.checkNonNegative("x", i);
        int sqrtFloor = sqrtFloor(i);
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == i);
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return lessThanBranchFree(sqrtFloor * sqrtFloor, i) + sqrtFloor;
            case 6:
            case 7:
            case 8:
                return lessThanBranchFree((sqrtFloor * sqrtFloor) + sqrtFloor, i) + sqrtFloor;
            default:
                throw new AssertionError();
        }
        return sqrtFloor;
    }

    private static int sqrtFloor(int i) {
        return (int) Math.sqrt((double) i);
    }
}
