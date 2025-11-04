// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import java.util.Comparator;
import com.google.common.base.Preconditions;
import javax.annotation.CheckReturnValue;
import java.math.BigInteger;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class UnsignedLongs
{
    public static final long MAX_VALUE = -1L;
    private static final int[] maxSafeDigits;
    private static final long[] maxValueDivs;
    private static final int[] maxValueMods;
    
    static {
        maxValueDivs = new long[37];
        maxValueMods = new int[37];
        maxSafeDigits = new int[37];
        final BigInteger bigInteger = new BigInteger("10000000000000000", 16);
        for (int i = 2; i <= 36; ++i) {
            UnsignedLongs.maxValueDivs[i] = divide(-1L, i);
            UnsignedLongs.maxValueMods[i] = (int)remainder(-1L, i);
            UnsignedLongs.maxSafeDigits[i] = bigInteger.toString(i).length() - 1;
        }
    }
    
    private UnsignedLongs() {
    }
    
    @CheckReturnValue
    public static int compare(final long n, final long n2) {
        return Longs.compare(flip(n), flip(n2));
    }
    
    public static long decode(final String str) {
        final ParseRequest fromString = ParseRequest.fromString(str);
        try {
            return parseUnsignedLong(fromString.rawValue, fromString.radix);
        }
        catch (final NumberFormatException cause) {
            final NumberFormatException ex = new NumberFormatException("Error parsing value: " + str);
            ex.initCause(cause);
            throw ex;
        }
    }
    
    @CheckReturnValue
    public static long divide(final long n, final long n2) {
        final int n3 = 0;
        int n4;
        if (n2 >= 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            if (compare(n, n2) >= 0) {
                return 1L;
            }
            return 0L;
        }
        else {
            int n5;
            if (n < 0L) {
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            if (n5 == 0) {
                return n / n2;
            }
            final long n6 = (n >>> 1) / n2 << 1;
            int n7;
            if (compare(n - n6 * n2, n2) < 0) {
                n7 = n3;
            }
            else {
                n7 = 1;
            }
            return n7 + n6;
        }
    }
    
    private static long flip(final long n) {
        return Long.MIN_VALUE ^ n;
    }
    
    @CheckReturnValue
    public static String join(final String str, final long... array) {
        Preconditions.checkNotNull(str);
        if (array.length != 0) {
            final StringBuilder sb = new StringBuilder(array.length * 5);
            sb.append(toString(array[0]));
            for (int i = 1; i < array.length; ++i) {
                sb.append(str).append(toString(array[i]));
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
    public static long max(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long flip = flip(array[0]);
        for (int i = 1; i < array.length; ++i) {
            final long flip2 = flip(array[i]);
            int n;
            if (flip2 <= flip) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                flip = flip2;
            }
        }
        return flip(flip);
    }
    
    @CheckReturnValue
    public static long min(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long flip = flip(array[0]);
        for (int i = 1; i < array.length; ++i) {
            final long flip2 = flip(array[i]);
            int n;
            if (flip2 >= flip) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                flip = flip2;
            }
        }
        return flip(flip);
    }
    
    private static boolean overflowInParse(final long n, final int n2, final int n3) {
        boolean b = false;
        int n4;
        if (n < 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 != 0) {
            return true;
        }
        int n5;
        if (n >= UnsignedLongs.maxValueDivs[n3]) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (n5 == 0) {
            return false;
        }
        int n6;
        if (n <= UnsignedLongs.maxValueDivs[n3]) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        if (n6 == 0) {
            return true;
        }
        if (n2 > UnsignedLongs.maxValueMods[n3]) {
            b = true;
        }
        return b;
    }
    
    public static long parseUnsignedLong(final String s) {
        return parseUnsignedLong(s, 10);
    }
    
    public static long parseUnsignedLong(final String s, final int n) {
        int i = 0;
        Preconditions.checkNotNull(s);
        if (s.length() == 0) {
            throw new NumberFormatException("empty string");
        }
        if (n >= 2 && n <= 36) {
            final int n2 = UnsignedLongs.maxSafeDigits[n];
            long n3 = 0L;
            while (i < s.length()) {
                final int digit = Character.digit(s.charAt(i), n);
                if (digit == -1) {
                    throw new NumberFormatException(s);
                }
                if (i > n2 - 1 && overflowInParse(n3, digit, n)) {
                    throw new NumberFormatException("Too large for unsigned long: " + s);
                }
                n3 = n3 * n + digit;
                ++i;
            }
            return n3;
        }
        throw new NumberFormatException("illegal radix: " + n);
    }
    
    @CheckReturnValue
    public static long remainder(long n, final long n2) {
        final int n3 = 0;
        int n4;
        if (n2 >= 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            if (compare(n, n2) >= 0) {
                return n - n2;
            }
            return n;
        }
        else {
            int n5 = n3;
            if (n < 0L) {
                n5 = 1;
            }
            if (n5 == 0) {
                return n % n2;
            }
            final long n6 = n - ((n >>> 1) / n2 << 1) * n2;
            n = n2;
            if (compare(n6, n2) < 0) {
                n = 0L;
            }
            return n6 - n;
        }
    }
    
    @CheckReturnValue
    public static String toString(final long n) {
        return toString(n, 10);
    }
    
    @CheckReturnValue
    public static String toString(long n, final int radix) {
        Preconditions.checkArgument(radix >= 2 && radix <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", radix);
        if (n == 0L) {
            return "0";
        }
        final char[] value = new char[64];
        final int length = value.length;
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        int offset;
        if (n2 == 0) {
            final long divide = divide(n, radix);
            final long n3 = radix;
            offset = length - 1;
            value[offset] = Character.forDigit((int)(n - n3 * divide), radix);
            n = divide;
        }
        else {
            offset = length;
        }
        while (true) {
            int n4;
            if (n <= 0L) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 != 0) {
                break;
            }
            --offset;
            value[offset] = Character.forDigit((int)(n % radix), radix);
            n /= radix;
        }
        return new String(value, offset, value.length - offset);
    }
    
    enum LexicographicalComparator implements Comparator<long[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final long[] array, final long[] array2) {
            for (int min = Math.min(array.length, array2.length), i = 0; i < min; ++i) {
                if (array[i] != array2[i]) {
                    return UnsignedLongs.compare(array[i], array2[i]);
                }
            }
            return array.length - array2.length;
        }
    }
}
