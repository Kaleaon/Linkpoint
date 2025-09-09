package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@GwtCompatible
@Beta
public abstract class CharEscaper extends Escaper {
    private static final int DEST_PAD_MULTIPLIER = 2;

    protected CharEscaper() {
    }

    private static char[] growBuffer(char[] cArr, int i, int i2) {
        char[] cArr2 = new char[i2];
        if (i > 0) {
            System.arraycopy(cArr, 0, cArr2, 0, i);
        }
        return cArr2;
    }

    public String escape(String str) {
        Preconditions.checkNotNull(str);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (escape(str.charAt(i)) != null) {
                return escapeSlow(str, i);
            }
        }
        return str;
    }

    /* access modifiers changed from: protected */
    public abstract char[] escape(char c);

    /* access modifiers changed from: protected */
    public final String escapeSlow(String str, int i) {
        int i2;
        int length = str.length();
        char[] charBufferFromThreadLocal = Platform.charBufferFromThreadLocal();
        int length2 = charBufferFromThreadLocal.length;
        int i3 = 0;
        int i4 = 0;
        while (i < length) {
            char[] escape = escape(str.charAt(i));
            if (escape != null) {
                int length3 = escape.length;
                int i5 = i - i3;
                int i6 = i4 + i5 + length3;
                if (length2 < i6) {
                    length2 = ((length - i) * 2) + i6;
                    charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i4, length2);
                }
                if (i5 <= 0) {
                    i2 = i4;
                } else {
                    str.getChars(i3, i, charBufferFromThreadLocal, i4);
                    i2 = i4 + i5;
                }
                if (length3 > 0) {
                    System.arraycopy(escape, 0, charBufferFromThreadLocal, i2, length3);
                    i2 += length3;
                }
                i4 = i2;
                i3 = i + 1;
            }
            i++;
        }
        int i7 = length - i3;
        if (i7 > 0) {
            int i8 = i7 + i4;
            if (length2 < i8) {
                charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i4, i8);
            }
            str.getChars(i3, length, charBufferFromThreadLocal, i4);
            i4 = i8;
        }
        return new String(charBufferFromThreadLocal, 0, i4);
    }
}
