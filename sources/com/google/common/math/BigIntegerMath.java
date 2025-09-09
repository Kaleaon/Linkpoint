package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@GwtCompatible(emulated = true)
public final class BigIntegerMath {
    private static final double LN_10 = Math.log(10.0d);
    private static final double LN_2 = Math.log(2.0d);
    @VisibleForTesting
    static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
    @VisibleForTesting
    static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;

    /* renamed from: com.google.common.math.BigIntegerMath$1  reason: invalid class name */
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

    private BigIntegerMath() {
    }

    public static BigInteger binomial(int i, int i2) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", i2);
        Preconditions.checkArgument(i2 <= i, "k (%s) > n (%s)", Integer.valueOf(i2), Integer.valueOf(i));
        if (i2 > (i >> 1)) {
            i2 = i - i2;
        }
        if (i2 < LongMath.biggestBinomials.length && i <= LongMath.biggestBinomials[i2]) {
            return BigInteger.valueOf(LongMath.binomial(i, i2));
        }
        BigInteger bigInteger = BigInteger.ONE;
        int log2 = LongMath.log2((long) i, RoundingMode.CEILING);
        long j = 1;
        long j2 = (long) i;
        BigInteger bigInteger2 = bigInteger;
        int i3 = log2;
        for (int i4 = 1; i4 < i2; i4++) {
            int i5 = i - i4;
            int i6 = i4 + 1;
            if (i3 + log2 < 63) {
                j2 *= (long) i5;
                j *= (long) i6;
                i3 += log2;
            } else {
                BigInteger divide = bigInteger2.multiply(BigInteger.valueOf(j2)).divide(BigInteger.valueOf(j));
                j2 = (long) i5;
                j = (long) i6;
                bigInteger2 = divide;
                i3 = log2;
            }
        }
        return bigInteger2.multiply(BigInteger.valueOf(j2)).divide(BigInteger.valueOf(j));
    }

    @GwtIncompatible("TODO")
    public static BigInteger divide(BigInteger bigInteger, BigInteger bigInteger2, RoundingMode roundingMode) {
        return new BigDecimal(bigInteger).divide(new BigDecimal(bigInteger2), 0, roundingMode).toBigIntegerExact();
    }

    public static BigInteger factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        if (i < LongMath.factorials.length) {
            return BigInteger.valueOf(LongMath.factorials[i]);
        }
        ArrayList arrayList = new ArrayList(IntMath.divide(IntMath.log2(i, RoundingMode.CEILING) * i, 64, RoundingMode.CEILING));
        int length = LongMath.factorials.length;
        long j = LongMath.factorials[length - 1];
        int numberOfTrailingZeros = Long.numberOfTrailingZeros(j);
        long j2 = j >> numberOfTrailingZeros;
        int log2 = LongMath.log2(j2, RoundingMode.FLOOR) + 1;
        int log22 = LongMath.log2((long) length, RoundingMode.FLOOR) + 1;
        int i2 = numberOfTrailingZeros;
        int i3 = log2;
        long j3 = j2;
        long j4 = (long) length;
        int i4 = log22;
        int i5 = 1 << (log22 - 1);
        while (true) {
            if (j4 > ((long) i)) {
                break;
            }
            if ((((long) i5) & j4) != 0) {
                i5 <<= 1;
                i4++;
            }
            int numberOfTrailingZeros2 = Long.numberOfTrailingZeros(j4);
            long j5 = j4 >> numberOfTrailingZeros2;
            i2 += numberOfTrailingZeros2;
            if ((i4 - numberOfTrailingZeros2) + i3 >= 64) {
                arrayList.add(BigInteger.valueOf(j3));
                j3 = 1;
            }
            long j6 = j5 * j3;
            j4 = 1 + j4;
            i3 = LongMath.log2(j6, RoundingMode.FLOOR) + 1;
            j3 = j6;
        }
        if (!(j3 <= 1)) {
            arrayList.add(BigInteger.valueOf(j3));
        }
        return listProduct(arrayList).shiftLeft(i2);
    }

    @GwtIncompatible("TODO")
    static boolean fitsInLong(BigInteger bigInteger) {
        return bigInteger.bitLength() <= 63;
    }

    public static boolean isPowerOfTwo(BigInteger bigInteger) {
        Preconditions.checkNotNull(bigInteger);
        return bigInteger.signum() > 0 && bigInteger.getLowestSetBit() == bigInteger.bitLength() + -1;
    }

    static BigInteger listProduct(List<BigInteger> list) {
        return listProduct(list, 0, list.size());
    }

    static BigInteger listProduct(List<BigInteger> list, int i, int i2) {
        switch (i2 - i) {
            case 0:
                return BigInteger.ONE;
            case 1:
                return list.get(i);
            case 2:
                return list.get(i).multiply(list.get(i + 1));
            case 3:
                return list.get(i).multiply(list.get(i + 1)).multiply(list.get(i + 2));
            default:
                int i3 = (i2 + i) >>> 1;
                return listProduct(list, i, i3).multiply(listProduct(list, i3, i2));
        }
    }

    @GwtIncompatible("TODO")
    public static int log10(BigInteger bigInteger, RoundingMode roundingMode) {
        int compareTo;
        int i;
        BigInteger bigInteger2;
        int i2;
        MathPreconditions.checkPositive("x", bigInteger);
        if (fitsInLong(bigInteger)) {
            return LongMath.log10(bigInteger.longValue(), roundingMode);
        }
        int log2 = (int) ((((double) log2(bigInteger, RoundingMode.FLOOR)) * LN_2) / LN_10);
        BigInteger pow = BigInteger.TEN.pow(log2);
        int compareTo2 = pow.compareTo(bigInteger);
        if (compareTo2 <= 0) {
            BigInteger multiply = BigInteger.TEN.multiply(pow);
            i = log2;
            bigInteger2 = pow;
            i2 = compareTo2;
            BigInteger bigInteger3 = multiply;
            int compareTo3 = multiply.compareTo(bigInteger);
            while (compareTo3 <= 0) {
                i++;
                BigInteger multiply2 = BigInteger.TEN.multiply(bigInteger3);
                i2 = compareTo3;
                compareTo3 = multiply2.compareTo(bigInteger);
                BigInteger bigInteger4 = multiply2;
                bigInteger2 = bigInteger3;
                bigInteger3 = bigInteger4;
            }
        } else {
            do {
                log2--;
                pow = pow.divide(BigInteger.TEN);
                compareTo = pow.compareTo(bigInteger);
            } while (compareTo > 0);
            int i3 = compareTo;
            i = log2;
            bigInteger2 = pow;
            i2 = i3;
        }
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(i2 == 0);
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return !bigInteger2.equals(bigInteger) ? i + 1 : i;
            case 6:
            case 7:
            case 8:
                return bigInteger.pow(2).compareTo(bigInteger2.pow(2).multiply(BigInteger.TEN)) > 0 ? i + 1 : i;
            default:
                throw new AssertionError();
        }
        return i;
    }

    public static int log2(BigInteger bigInteger, RoundingMode roundingMode) {
        MathPreconditions.checkPositive("x", (BigInteger) Preconditions.checkNotNull(bigInteger));
        int bitLength = bigInteger.bitLength() - 1;
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(bigInteger));
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                return !isPowerOfTwo(bigInteger) ? bitLength + 1 : bitLength;
            case 6:
            case 7:
            case 8:
                return bitLength >= 256 ? bigInteger.pow(2).bitLength() + -1 >= (bitLength * 2) + 1 ? bitLength + 1 : bitLength : bigInteger.compareTo(SQRT2_PRECOMPUTED_BITS.shiftRight(256 - bitLength)) > 0 ? bitLength + 1 : bitLength;
            default:
                throw new AssertionError();
        }
        return bitLength;
    }

    @GwtIncompatible("TODO")
    public static BigInteger sqrt(BigInteger bigInteger, RoundingMode roundingMode) {
        boolean z = false;
        MathPreconditions.checkNonNegative("x", bigInteger);
        if (fitsInLong(bigInteger)) {
            return BigInteger.valueOf(LongMath.sqrt(bigInteger.longValue(), roundingMode));
        }
        BigInteger sqrtFloor = sqrtFloor(bigInteger);
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case 1:
                MathPreconditions.checkRoundingUnnecessary(sqrtFloor.pow(2).equals(bigInteger));
                break;
            case 2:
            case 3:
                break;
            case 4:
            case 5:
                int intValue = sqrtFloor.intValue();
                if (intValue * intValue == bigInteger.intValue() && sqrtFloor.pow(2).equals(bigInteger)) {
                    z = true;
                }
                return !z ? sqrtFloor.add(BigInteger.ONE) : sqrtFloor;
            case 6:
            case 7:
            case 8:
                return sqrtFloor.pow(2).add(sqrtFloor).compareTo(bigInteger) < 0 ? sqrtFloor.add(BigInteger.ONE) : sqrtFloor;
            default:
                throw new AssertionError();
        }
        return sqrtFloor;
    }

    @GwtIncompatible("TODO")
    private static BigInteger sqrtApproxWithDoubles(BigInteger bigInteger) {
        return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(bigInteger)), RoundingMode.HALF_EVEN);
    }

    @GwtIncompatible("TODO")
    private static BigInteger sqrtFloor(BigInteger bigInteger) {
        BigInteger sqrtApproxWithDoubles;
        int log2 = log2(bigInteger, RoundingMode.FLOOR);
        if (log2 >= 1023) {
            int i = (log2 - 52) & -2;
            sqrtApproxWithDoubles = sqrtApproxWithDoubles(bigInteger.shiftRight(i)).shiftLeft(i >> 1);
        } else {
            sqrtApproxWithDoubles = sqrtApproxWithDoubles(bigInteger);
        }
        BigInteger shiftRight = sqrtApproxWithDoubles.add(bigInteger.divide(sqrtApproxWithDoubles)).shiftRight(1);
        if (sqrtApproxWithDoubles.equals(shiftRight)) {
            return sqrtApproxWithDoubles;
        }
        while (true) {
            BigInteger bigInteger2 = shiftRight;
            shiftRight = bigInteger2.add(bigInteger.divide(bigInteger2)).shiftRight(1);
            if (shiftRight.compareTo(bigInteger2) >= 0) {
                return bigInteger2;
            }
        }
    }
}
