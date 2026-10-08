package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
@Beta
@GwtCompatible
/* loaded from: classes.dex */
public abstract class UnicodeEscaper extends Escaper {
    private static final int DEST_PAD = 32;

    protected static int codePointAt(CharSequence charSequence, int i, int i2) {
        Preconditions.checkNotNull(charSequence);
        if (i >= i2) {
            throw new IndexOutOfBoundsException("Index exceeds specified range");
        }
        int i3 = i + 1;
        char charAt = charSequence.charAt(i);
        if (charAt >= 55296 && charAt <= 57343) {
            if (charAt > 56319) {
                throw new IllegalArgumentException("Unexpected low surrogate character '" + charAt + "' with value " + ((int) charAt) + " at index " + (i3 - 1) + " in '" + ((Object) charSequence) + "'");
            }
            if (i3 != i2) {
                char charAt2 = charSequence.charAt(i3);
                if (Character.isLowSurrogate(charAt2)) {
                    return Character.toCodePoint(charAt, charAt2);
                }
                throw new IllegalArgumentException("Expected low surrogate but got char '" + charAt2 + "' with value " + ((int) charAt2) + " at index " + i3 + " in '" + ((Object) charSequence) + "'");
            }
            return -charAt;
        }
        return charAt;
    }

    private static char[] growBuffer(char[] cArr, int i, int i2) {
        char[] cArr2 = new char[i2];
        if (i > 0) {
            System.arraycopy(cArr, 0, cArr2, 0, i);
        }
        return cArr2;
    }

    @Override // com.google.common.escape.Escaper
    public String escape(String str) {
        Preconditions.checkNotNull(str);
        int length = str.length();
        int nextEscapeIndex = nextEscapeIndex(str, 0, length);
        return nextEscapeIndex != length ? escapeSlow(str, nextEscapeIndex) : str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract char[] escape(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public final String escapeSlow(String str, int i) {
        int i2;
        char[] cArr;
        int i3;
        int length = str.length();
        char[] charBufferFromThreadLocal = Platform.charBufferFromThreadLocal();
        int i4 = 0;
        int i5 = 0;
        while (i < length) {
            int codePointAt = codePointAt(str, i, length);
            if (codePointAt < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }
            char[] escape = escape(codePointAt);
            int i6 = i + (!Character.isSupplementaryCodePoint(codePointAt) ? 1 : 2);
            if (escape == null) {
                i3 = i4;
                cArr = charBufferFromThreadLocal;
            } else {
                int i7 = i - i4;
                int length2 = i5 + i7 + escape.length;
                if (charBufferFromThreadLocal.length < length2) {
                    charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i5, length2 + (length - i) + 32);
                }
                if (i7 <= 0) {
                    i2 = i5;
                } else {
                    str.getChars(i4, i, charBufferFromThreadLocal, i5);
                    i2 = i7 + i5;
                }
                if (escape.length > 0) {
                    System.arraycopy(escape, 0, charBufferFromThreadLocal, i2, escape.length);
                    i2 += escape.length;
                }
                i5 = i2;
                cArr = charBufferFromThreadLocal;
                i3 = i6;
            }
            i = nextEscapeIndex(str, i6, length);
            charBufferFromThreadLocal = cArr;
            i4 = i3;
        }
        int i8 = length - i4;
        if (i8 > 0) {
            int i9 = i8 + i5;
            if (charBufferFromThreadLocal.length < i9) {
                charBufferFromThreadLocal = growBuffer(charBufferFromThreadLocal, i5, i9);
            }
            str.getChars(i4, length, charBufferFromThreadLocal, i5);
            i5 = i9;
        }
        return new String(charBufferFromThreadLocal, 0, i5);
    }

    protected int nextEscapeIndex(CharSequence charSequence, int i, int i2) {
        while (i < i2) {
            int codePointAt = codePointAt(charSequence, i, i2);
            if (codePointAt < 0 || escape(codePointAt) != null) {
                break;
            }
            i += !Character.isSupplementaryCodePoint(codePointAt) ? 1 : 2;
        }
        return i;
    }
}
