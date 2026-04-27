// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Strings
{
    private Strings() {
    }
    
    public static String commonPrefix(final CharSequence charSequence, final CharSequence charSequence2) {
        Preconditions.checkNotNull(charSequence);
        Preconditions.checkNotNull(charSequence2);
        int min;
        int n;
        for (min = Math.min(charSequence.length(), charSequence2.length()), n = 0; n < min && charSequence.charAt(n) == charSequence2.charAt(n); ++n) {}
        if (validSurrogatePairAt(charSequence, n - 1) || validSurrogatePairAt(charSequence2, n - 1)) {
            --n;
        }
        return charSequence.subSequence(0, n).toString();
    }
    
    public static String commonSuffix(final CharSequence charSequence, final CharSequence charSequence2) {
        int n = 0;
        Preconditions.checkNotNull(charSequence);
        Preconditions.checkNotNull(charSequence2);
        while (n < Math.min(charSequence.length(), charSequence2.length()) && charSequence.charAt(charSequence.length() - n - 1) == charSequence2.charAt(charSequence2.length() - n - 1)) {
            ++n;
        }
        if (validSurrogatePairAt(charSequence, charSequence.length() - n - 1) || validSurrogatePairAt(charSequence2, charSequence2.length() - n - 1)) {
            --n;
        }
        return charSequence.subSequence(charSequence.length() - n, charSequence.length()).toString();
    }
    
    @Nullable
    public static String emptyToNull(@Nullable String s) {
        if (isNullOrEmpty(s)) {
            s = null;
        }
        return s;
    }
    
    public static boolean isNullOrEmpty(@Nullable final String s) {
        boolean b = false;
        if (s == null || s.length() == 0) {
            b = true;
        }
        return b;
    }
    
    public static String nullToEmpty(@Nullable String s) {
        if (s == null) {
            s = "";
        }
        return s;
    }
    
    public static String padEnd(final String str, final int capacity, final char c) {
        Preconditions.checkNotNull(str);
        if (str.length() < capacity) {
            final StringBuilder sb = new StringBuilder(capacity);
            sb.append(str);
            for (int i = str.length(); i < capacity; ++i) {
                sb.append(c);
            }
            return sb.toString();
        }
        return str;
    }
    
    public static String padStart(final String str, final int capacity, final char c) {
        Preconditions.checkNotNull(str);
        if (str.length() < capacity) {
            final StringBuilder sb = new StringBuilder(capacity);
            for (int i = str.length(); i < capacity; ++i) {
                sb.append(c);
            }
            sb.append(str);
            return sb.toString();
        }
        return str;
    }
    
    public static String repeat(String s, int i) {
        Preconditions.checkNotNull(s);
        if (i <= 1) {
            Preconditions.checkArgument(i >= 0, "invalid count: %s", i);
            if (i == 0) {
                s = "";
            }
            return s;
        }
        final int length = s.length();
        final long lng = length * (long)i;
        final int n = (int)lng;
        if (n != lng) {
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + lng);
        }
        final char[] array = new char[n];
        s.getChars(0, length, array, 0);
        for (i = length; i < n - i; i <<= 1) {
            System.arraycopy(array, 0, array, i, i);
        }
        System.arraycopy(array, 0, array, i, n - i);
        return new String(array);
    }
    
    @VisibleForTesting
    static boolean validSurrogatePairAt(final CharSequence charSequence, final int n) {
        final boolean b = false;
        boolean b2;
        if (n < 0) {
            b2 = b;
        }
        else {
            b2 = b;
            if (n <= charSequence.length() - 2) {
                b2 = b;
                if (Character.isHighSurrogate(charSequence.charAt(n))) {
                    b2 = b;
                    if (Character.isLowSurrogate(charSequence.charAt(n + 1))) {
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
}
