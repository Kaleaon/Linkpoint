package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
@CheckReturnValue
public final class Strings {
    private Strings() {
    }

    public static String commonPrefix(CharSequence charSequence, CharSequence charSequence2) {
        Preconditions.checkNotNull(charSequence);
        Preconditions.checkNotNull(charSequence2);
        int min = Math.min(charSequence.length(), charSequence2.length());
        int i = 0;
        while (i < min && charSequence.charAt(i) == charSequence2.charAt(i)) {
            i++;
        }
        if (validSurrogatePairAt(charSequence, i - 1) || validSurrogatePairAt(charSequence2, i - 1)) {
            i--;
        }
        return charSequence.subSequence(0, i).toString();
    }

    public static String commonSuffix(CharSequence charSequence, CharSequence charSequence2) {
        int i = 0;
        Preconditions.checkNotNull(charSequence);
        Preconditions.checkNotNull(charSequence2);
        int min = Math.min(charSequence.length(), charSequence2.length());
        while (i < min && charSequence.charAt((charSequence.length() - i) - 1) == charSequence2.charAt((charSequence2.length() - i) - 1)) {
            i++;
        }
        if (validSurrogatePairAt(charSequence, (charSequence.length() - i) - 1) || validSurrogatePairAt(charSequence2, (charSequence2.length() - i) - 1)) {
            i--;
        }
        return charSequence.subSequence(charSequence.length() - i, charSequence.length()).toString();
    }

    @Nullable
    public static String emptyToNull(@Nullable String str) {
        if (!isNullOrEmpty(str)) {
            return str;
        }
        return null;
    }

    public static boolean isNullOrEmpty(@Nullable String str) {
        return str == null || str.length() == 0;
    }

    public static String nullToEmpty(@Nullable String str) {
        return str != null ? str : "";
    }

    public static String padEnd(String str, int i, char c) {
        Preconditions.checkNotNull(str);
        if (str.length() >= i) {
            return str;
        }
        StringBuilder sb = new StringBuilder(i);
        sb.append(str);
        for (int length = str.length(); length < i; length++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String padStart(String str, int i, char c) {
        Preconditions.checkNotNull(str);
        if (str.length() >= i) {
            return str;
        }
        StringBuilder sb = new StringBuilder(i);
        for (int length = str.length(); length < i; length++) {
            sb.append(c);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String repeat(String str, int i) {
        Preconditions.checkNotNull(str);
        if (i > 1) {
            int length = str.length();
            long j = ((long) length) * ((long) i);
            int i2 = (int) j;
            if (((long) i2) != j) {
                throw new ArrayIndexOutOfBoundsException("Required array size too large: " + j);
            }
            char[] cArr = new char[i2];
            str.getChars(0, length, cArr, 0);
            while (length < i2 - length) {
                System.arraycopy(cArr, 0, cArr, length, length);
                length <<= 1;
            }
            System.arraycopy(cArr, 0, cArr, length, i2 - length);
            return new String(cArr);
        }
        Preconditions.checkArgument(i >= 0, "invalid count: %s", Integer.valueOf(i));
        return i != 0 ? str : "";
    }

    @VisibleForTesting
    static boolean validSurrogatePairAt(CharSequence charSequence, int i) {
        return i >= 0 && i <= charSequence.length() + -2 && Character.isHighSurrogate(charSequence.charAt(i)) && Character.isLowSurrogate(charSequence.charAt(i + 1));
    }
}
