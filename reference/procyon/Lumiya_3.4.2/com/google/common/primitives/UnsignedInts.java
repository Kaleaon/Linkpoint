// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import java.util.Comparator;
import com.google.common.base.Preconditions;
import javax.annotation.CheckReturnValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class UnsignedInts
{
    static final long INT_MASK = 4294967295L;
    
    private UnsignedInts() {
    }
    
    @CheckReturnValue
    public static int compare(final int n, final int n2) {
        return Ints.compare(flip(n), flip(n2));
    }
    
    public static int decode(final String str) {
        final ParseRequest fromString = ParseRequest.fromString(str);
        try {
            return parseUnsignedInt(fromString.rawValue, fromString.radix);
        }
        catch (final NumberFormatException cause) {
            final NumberFormatException ex = new NumberFormatException("Error parsing value: " + str);
            ex.initCause(cause);
            throw ex;
        }
    }
    
    @CheckReturnValue
    public static int divide(final int n, final int n2) {
        return (int)(toLong(n) / toLong(n2));
    }
    
    static int flip(final int n) {
        return Integer.MIN_VALUE ^ n;
    }
    
    @CheckReturnValue
    public static String join(final String str, final int... array) {
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
    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    @CheckReturnValue
    public static int max(final int... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        int flip = flip(array[0]);
        while (i < array.length) {
            final int flip2 = flip(array[i]);
            if (flip2 > flip) {
                flip = flip2;
            }
            ++i;
        }
        return flip(flip);
    }
    
    @CheckReturnValue
    public static int min(final int... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        int flip = flip(array[0]);
        while (i < array.length) {
            final int flip2 = flip(array[i]);
            if (flip2 < flip) {
                flip = flip2;
            }
            ++i;
        }
        return flip(flip);
    }
    
    public static int parseUnsignedInt(final String s) {
        return parseUnsignedInt(s, 10);
    }
    
    public static int parseUnsignedInt(final String s, final int n) {
        Preconditions.checkNotNull(s);
        final long long1 = Long.parseLong(s, n);
        if ((0xFFFFFFFFL & long1) != long1) {
            throw new NumberFormatException("Input " + s + " in base " + n + " is not in the range of an unsigned integer");
        }
        return (int)long1;
    }
    
    @CheckReturnValue
    public static int remainder(final int n, final int n2) {
        return (int)(toLong(n) % toLong(n2));
    }
    
    @CheckReturnValue
    public static long toLong(final int n) {
        return (long)n & 0xFFFFFFFFL;
    }
    
    @CheckReturnValue
    public static String toString(final int n) {
        return toString(n, 10);
    }
    
    @CheckReturnValue
    public static String toString(final int n, final int radix) {
        return Long.toString((long)n & 0xFFFFFFFFL, radix);
    }
    
    enum LexicographicalComparator implements Comparator<int[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final int[] array, final int[] array2) {
            for (int min = Math.min(array.length, array2.length), i = 0; i < min; ++i) {
                if (array[i] != array2[i]) {
                    return UnsignedInts.compare(array[i], array2[i]);
                }
            }
            return array.length - array2.length;
        }
    }
}
