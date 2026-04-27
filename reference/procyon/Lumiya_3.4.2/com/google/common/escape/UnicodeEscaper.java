// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class UnicodeEscaper extends Escaper
{
    private static final int DEST_PAD = 32;
    
    protected UnicodeEscaper() {
    }
    
    protected static int codePointAt(final CharSequence charSequence, final int n, final int n2) {
        Preconditions.checkNotNull(charSequence);
        if (n >= n2) {
            throw new IndexOutOfBoundsException("Index exceeds specified range");
        }
        final int i = n + 1;
        final char char1 = charSequence.charAt(n);
        if (char1 < '\ud800' || char1 > '\udfff') {
            return char1;
        }
        if (char1 > '\udbff') {
            throw new IllegalArgumentException("Unexpected low surrogate character '" + char1 + "' with value " + (int)char1 + " at index " + (i - 1) + " in '" + (Object)charSequence + "'");
        }
        if (i == n2) {
            return -char1;
        }
        final char char2 = charSequence.charAt(i);
        if (!Character.isLowSurrogate(char2)) {
            throw new IllegalArgumentException("Expected low surrogate but got char '" + char2 + "' with value " + (int)char2 + " at index " + i + " in '" + (Object)charSequence + "'");
        }
        return Character.toCodePoint(char1, char2);
    }
    
    private static char[] growBuffer(final char[] array, final int n, final int n2) {
        final char[] array2 = new char[n2];
        if (n > 0) {
            System.arraycopy(array, 0, array2, 0, n);
        }
        return array2;
    }
    
    @Override
    public String escape(final String s) {
        Preconditions.checkNotNull(s);
        final int length = s.length();
        final int nextEscapeIndex = this.nextEscapeIndex(s, 0, length);
        String escapeSlow = s;
        if (nextEscapeIndex != length) {
            escapeSlow = this.escapeSlow(s, nextEscapeIndex);
        }
        return escapeSlow;
    }
    
    protected abstract char[] escape(final int p0);
    
    protected final String escapeSlow(final String s, int count) {
        final int length = s.length();
        char[] value = Platform.charBufferFromThreadLocal();
        int n = 0;
        final int n2 = 0;
        int i = count;
        count = n2;
        while (i < length) {
            final int codePoint = codePointAt(s, i, length);
            if (codePoint < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }
            final char[] escape = this.escape(codePoint);
            int n3;
            if (!Character.isSupplementaryCodePoint(codePoint)) {
                n3 = 1;
            }
            else {
                n3 = 2;
            }
            final int n4 = i + n3;
            int n6;
            if (escape == null) {
                final int n5 = n;
                n6 = count;
                count = n5;
            }
            else {
                final int n7 = i - n;
                final int n8 = count + n7 + escape.length;
                if (value.length < n8) {
                    value = growBuffer(value, count, n8 + (length - i) + 32);
                }
                if (n7 > 0) {
                    s.getChars(n, i, value, count);
                    count += n7;
                }
                if (escape.length > 0) {
                    System.arraycopy(escape, 0, value, count, escape.length);
                    count += escape.length;
                }
                n6 = count;
                count = n4;
            }
            final int nextEscapeIndex = this.nextEscapeIndex(s, n4, length);
            final int n9 = count;
            count = n6;
            n = n9;
            i = nextEscapeIndex;
        }
        final int n10 = length - n;
        if (n10 > 0) {
            final int n11 = n10 + count;
            if (value.length < n11) {
                value = growBuffer(value, count, n11);
            }
            s.getChars(n, length, value, count);
            count = n11;
        }
        return new String(value, 0, count);
    }
    
    protected int nextEscapeIndex(final CharSequence charSequence, int i, final int n) {
        while (i < n) {
            final int codePoint = codePointAt(charSequence, i, n);
            if (codePoint < 0 || this.escape(codePoint) != null) {
                break;
            }
            int n2;
            if (!Character.isSupplementaryCodePoint(codePoint)) {
                n2 = 1;
            }
            else {
                n2 = 2;
            }
            i += n2;
        }
        return i;
    }
}
