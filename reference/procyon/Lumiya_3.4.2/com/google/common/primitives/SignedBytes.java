// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import java.util.Comparator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class SignedBytes
{
    public static final byte MAX_POWER_OF_TWO = 64;
    
    private SignedBytes() {
    }
    
    public static byte checkedCast(final long lng) {
        final byte b = (byte)lng;
        if (b != lng) {
            throw new IllegalArgumentException("Out of range: " + lng);
        }
        return b;
    }
    
    public static int compare(final byte b, final byte b2) {
        return b - b2;
    }
    
    public static String join(final String str, final byte... array) {
        Preconditions.checkNotNull(str);
        if (array.length != 0) {
            final StringBuilder sb = new StringBuilder(array.length * 5);
            sb.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                sb.append(str).append(array[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public static Comparator<byte[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static byte max(final byte... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        byte b = array[0];
        while (i < array.length) {
            byte b2;
            if (array[i] <= b) {
                b2 = b;
            }
            else {
                b2 = array[i];
            }
            ++i;
            b = b2;
        }
        return b;
    }
    
    public static byte min(final byte... array) {
        int i = 1;
        Preconditions.checkArgument(array.length > 0);
        byte b = array[0];
        while (i < array.length) {
            byte b2;
            if (array[i] >= b) {
                b2 = b;
            }
            else {
                b2 = array[i];
            }
            ++i;
            b = b2;
        }
        return b;
    }
    
    public static byte saturatedCast(final long n) {
        final int n2 = 1;
        int n3;
        if (n <= 127L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            return 127;
        }
        int n4;
        if (n >= -128L) {
            n4 = n2;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            return -128;
        }
        return (byte)n;
    }
    
    private enum LexicographicalComparator implements Comparator<byte[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final byte[] array, final byte[] array2) {
            for (int i = 0; i < Math.min(array.length, array2.length); ++i) {
                final int compare = SignedBytes.compare(array[i], array2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return array.length - array2.length;
        }
    }
}
