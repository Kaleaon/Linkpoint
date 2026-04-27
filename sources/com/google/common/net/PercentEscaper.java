package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.UnicodeEscaper;

@GwtCompatible
@Beta
public final class PercentEscaper extends UnicodeEscaper {
    private static final char[] PLUS_SIGN = {'+'};
    private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private final boolean plusForSpace;
    private final boolean[] safeOctets;

    public PercentEscaper(String str, boolean z) {
        Preconditions.checkNotNull(str);
        if (!str.matches(".*[0-9A-Za-z].*")) {
            String str2 = str + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            if (z && str2.contains(" ")) {
                throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
            }
            this.plusForSpace = z;
            this.safeOctets = createSafeOctets(str2);
            return;
        }
        throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
    }

    private static boolean[] createSafeOctets(String str) {
        char[] charArray = str.toCharArray();
        int i = -1;
        for (char max : charArray) {
            i = Math.max(max, i);
        }
        boolean[] zArr = new boolean[(i + 1)];
        for (char c : charArray) {
            zArr[c] = true;
        }
        return zArr;
    }

    public String escape(String str) {
        Preconditions.checkNotNull(str);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt >= this.safeOctets.length || !this.safeOctets[charAt]) {
                return escapeSlow(str, i);
            }
        }
        return str;
    }

    /* access modifiers changed from: protected */
    public char[] escape(int i) {
        if (i < this.safeOctets.length && this.safeOctets[i]) {
            return null;
        }
        if (i == 32 && this.plusForSpace) {
            return PLUS_SIGN;
        }
        if (i <= 127) {
            char[] cArr = new char[3];
            cArr[0] = '%';
            cArr[2] = (char) UPPER_HEX_DIGITS[i & 15];
            cArr[1] = (char) UPPER_HEX_DIGITS[i >>> 4];
            return cArr;
        } else if (i <= 2047) {
            char[] cArr2 = new char[6];
            cArr2[0] = '%';
            cArr2[3] = '%';
            cArr2[5] = (char) UPPER_HEX_DIGITS[i & 15];
            int i2 = i >>> 4;
            cArr2[4] = (char) UPPER_HEX_DIGITS[(i2 & 3) | 8];
            int i3 = i2 >>> 2;
            cArr2[2] = (char) UPPER_HEX_DIGITS[i3 & 15];
            cArr2[1] = (char) UPPER_HEX_DIGITS[(i3 >>> 4) | 12];
            return cArr2;
        } else if (i <= 65535) {
            char[] cArr3 = new char[9];
            cArr3[0] = '%';
            cArr3[1] = 'E';
            cArr3[3] = '%';
            cArr3[6] = '%';
            cArr3[8] = (char) UPPER_HEX_DIGITS[i & 15];
            int i4 = i >>> 4;
            cArr3[7] = (char) UPPER_HEX_DIGITS[(i4 & 3) | 8];
            int i5 = i4 >>> 2;
            cArr3[5] = (char) UPPER_HEX_DIGITS[i5 & 15];
            int i6 = i5 >>> 4;
            cArr3[4] = (char) UPPER_HEX_DIGITS[(i6 & 3) | 8];
            cArr3[2] = (char) UPPER_HEX_DIGITS[i6 >>> 2];
            return cArr3;
        } else if (i > 1114111) {
            throw new IllegalArgumentException("Invalid unicode character value " + i);
        } else {
            char[] cArr4 = new char[12];
            cArr4[0] = '%';
            cArr4[1] = 'F';
            cArr4[3] = '%';
            cArr4[6] = '%';
            cArr4[9] = '%';
            cArr4[11] = (char) UPPER_HEX_DIGITS[i & 15];
            int i7 = i >>> 4;
            cArr4[10] = (char) UPPER_HEX_DIGITS[(i7 & 3) | 8];
            int i8 = i7 >>> 2;
            cArr4[8] = (char) UPPER_HEX_DIGITS[i8 & 15];
            int i9 = i8 >>> 4;
            cArr4[7] = (char) UPPER_HEX_DIGITS[(i9 & 3) | 8];
            int i10 = i9 >>> 2;
            cArr4[5] = (char) UPPER_HEX_DIGITS[i10 & 15];
            int i11 = i10 >>> 4;
            cArr4[4] = (char) UPPER_HEX_DIGITS[(i11 & 3) | 8];
            cArr4[2] = (char) UPPER_HEX_DIGITS[(i11 >>> 2) & 7];
            return cArr4;
        }
    }

    /* access modifiers changed from: protected */
    public int nextEscapeIndex(CharSequence charSequence, int i, int i2) {
        Preconditions.checkNotNull(charSequence);
        while (i < i2) {
            char charAt = charSequence.charAt(i);
            if (charAt >= this.safeOctets.length || !this.safeOctets[charAt]) {
                break;
            }
            i++;
        }
        return i;
    }
}
