package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.math.BigInteger;
import java.util.Comparator;
import javax.annotation.CheckReturnValue;
@Beta
@GwtCompatible
/* loaded from: classes.dex */
public final class UnsignedLongs {
    public static final long MAX_VALUE = -1;
    private static final long[] maxValueDivs = new long[37];
    private static final int[] maxValueMods = new int[37];
    private static final int[] maxSafeDigits = new int[37];

    /* loaded from: classes.dex */
    enum LexicographicalComparator implements Comparator<long[]> {
        INSTANCE;

        @Override // java.util.Comparator
        public int compare(long[] jArr, long[] jArr2) {
            int min = Math.min(jArr.length, jArr2.length);
            for (int i = 0; i < min; i++) {
                if (jArr[i] != jArr2[i]) {
                    return UnsignedLongs.compare(jArr[i], jArr2[i]);
                }
            }
            return jArr.length - jArr2.length;
        }
    }

    static {
        BigInteger bigInteger = new BigInteger("10000000000000000", 16);
        for (int i = 2; i <= 36; i++) {
            maxValueDivs[i] = divide(-1L, i);
            maxValueMods[i] = (int) remainder(-1L, i);
            maxSafeDigits[i] = bigInteger.toString(i).length() - 1;
        }
    }

    private UnsignedLongs() {
    }

    @CheckReturnValue
    public static int compare(long j, long j2) {
        return Longs.compare(flip(j), flip(j2));
    }

    public static long decode(String str) {
        ParseRequest fromString = ParseRequest.fromString(str);
        try {
            return parseUnsignedLong(fromString.rawValue, fromString.radix);
        } catch (NumberFormatException e) {
            NumberFormatException numberFormatException = new NumberFormatException("Error parsing value: " + str);
            numberFormatException.initCause(e);
            throw numberFormatException;
        }
    }

    @CheckReturnValue
    public static long divide(long j, long j2) {
        if (!(j2 >= 0)) {
            return compare(j, j2) >= 0 ? 1L : 0L;
        }
        if (j < 0) {
            long j3 = ((j >>> 1) / j2) << 1;
            return (compare(j - (j3 * j2), j2) >= 0 ? 1 : 0) + j3;
        }
        return j / j2;
    }

    private static long flip(long j) {
        return Long.MIN_VALUE ^ j;
    }

    @CheckReturnValue
    public static String join(String str, long... jArr) {
        Preconditions.checkNotNull(str);
        if (jArr.length != 0) {
            StringBuilder sb = new StringBuilder(jArr.length * 5);
            sb.append(toString(jArr[0]));
            for (int i = 1; i < jArr.length; i++) {
                sb.append(str).append(toString(jArr[i]));
            }
            return sb.toString();
        }
        return "";
    }

    @CheckReturnValue
    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    @CheckReturnValue
    public static long max(long... jArr) {
        Preconditions.checkArgument(jArr.length > 0);
        long flip = flip(jArr[0]);
        for (int i = 1; i < jArr.length; i++) {
            long flip2 = flip(jArr[i]);
            if (!(flip2 <= flip)) {
                flip = flip2;
            }
        }
        return flip(flip);
    }

    @CheckReturnValue
    public static long min(long... jArr) {
        Preconditions.checkArgument(jArr.length > 0);
        long flip = flip(jArr[0]);
        for (int i = 1; i < jArr.length; i++) {
            long flip2 = flip(jArr[i]);
            if (!(flip2 >= flip)) {
                flip = flip2;
            }
        }
        return flip(flip);
    }

    private static boolean overflowInParse(long j, int i, int i2) {
        if (j < 0) {
            return true;
        }
        if (j >= maxValueDivs[i2]) {
            return !((j > maxValueDivs[i2] ? 1 : (j == maxValueDivs[i2] ? 0 : -1)) <= 0) || i > maxValueMods[i2];
        }
        return false;
    }

    public static long parseUnsignedLong(String str) {
        return parseUnsignedLong(str, 10);
    }

    public static long parseUnsignedLong(String str, int i) {
        Preconditions.checkNotNull(str);
        if (str.length() != 0) {
            if (i >= 2 && i <= 36) {
                int i2 = maxSafeDigits[i] - 1;
                long j = 0;
                for (int i3 = 0; i3 < str.length(); i3++) {
                    int digit = Character.digit(str.charAt(i3), i);
                    if (digit == -1) {
                        throw new NumberFormatException(str);
                    }
                    if (i3 > i2 && overflowInParse(j, digit, i)) {
                        throw new NumberFormatException("Too large for unsigned long: " + str);
                    }
                    j = (j * i) + digit;
                }
                return j;
            }
            throw new NumberFormatException("illegal radix: " + i);
        }
        throw new NumberFormatException("empty string");
    }

    @CheckReturnValue
    public static long remainder(long j, long j2) {
        if (!(j2 >= 0)) {
            return compare(j, j2) >= 0 ? j - j2 : j;
        }
        if (j < 0) {
            long j3 = j - ((((j >>> 1) / j2) << 1) * j2);
            if (compare(j3, j2) < 0) {
                j2 = 0;
            }
            return j3 - j2;
        }
        return j % j2;
    }

    @CheckReturnValue
    public static String toString(long j) {
        return toString(j, 10);
    }

    @CheckReturnValue
    public static String toString(long j, int i) {
        int i2;
        Preconditions.checkArgument(i >= 2 && i <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", Integer.valueOf(i));
        if (j == 0) {
            return "0";
        }
        char[] cArr = new char[64];
        int length = cArr.length;
        if (j >= 0) {
            i2 = length;
        } else {
            long divide = divide(j, i);
            int i3 = length - 1;
            cArr[i3] = Character.forDigit((int) (j - (i * divide)), i);
            i2 = i3;
            j = divide;
        }
        while (true) {
            if (j <= 0) {
                return new String(cArr, i2, cArr.length - i2);
            }
            int i4 = i2 - 1;
            cArr[i4] = Character.forDigit((int) (j % i), i);
            j /= i;
            i2 = i4;
        }
    }
}
