// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.net;

import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;
import com.google.common.escape.UnicodeEscaper;

@Beta
@GwtCompatible
public final class PercentEscaper extends UnicodeEscaper
{
    private static final char[] PLUS_SIGN;
    private static final char[] UPPER_HEX_DIGITS;
    private final boolean plusForSpace;
    private final boolean[] safeOctets;
    
    static {
        PLUS_SIGN = new char[] { '+' };
        UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    }
    
    public PercentEscaper(String string, final boolean plusForSpace) {
        Preconditions.checkNotNull(string);
        if (string.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
        }
        string += "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (plusForSpace && string.contains(" ")) {
            throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = createSafeOctets(string);
    }
    
    private static boolean[] createSafeOctets(final String s) {
        final int n = 0;
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        int max = -1;
        for (int i = 0; i < length; ++i) {
            max = Math.max(charArray[i], max);
        }
        final boolean[] array = new boolean[max + 1];
        for (int length2 = charArray.length, j = n; j < length2; ++j) {
            array[charArray[j]] = true;
        }
        return array;
    }
    
    @Override
    public String escape(final String s) {
        int i = 0;
        Preconditions.checkNotNull(s);
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 >= this.safeOctets.length || !this.safeOctets[char1]) {
                return this.escapeSlow(s, i);
            }
            ++i;
        }
        return s;
    }
    
    @Override
    protected char[] escape(int i) {
        if (i < this.safeOctets.length && this.safeOctets[i]) {
            return null;
        }
        if (i == 32 && this.plusForSpace) {
            return PercentEscaper.PLUS_SIGN;
        }
        if (i <= 127) {
            return new char[] { '%', PercentEscaper.UPPER_HEX_DIGITS[i >>> 4], PercentEscaper.UPPER_HEX_DIGITS[i & 0xF] };
        }
        if (i <= 2047) {
            final char c = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
            i >>>= 4;
            final char c2 = PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8];
            i >>>= 2;
            return new char[] { '%', PercentEscaper.UPPER_HEX_DIGITS[i >>> 4 | 0xC], PercentEscaper.UPPER_HEX_DIGITS[i & 0xF], '%', c2, c };
        }
        if (i <= 65535) {
            final char c3 = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
            i >>>= 4;
            final char c4 = PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8];
            i >>>= 2;
            final char c5 = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
            i >>>= 4;
            return new char[] { '%', 'E', PercentEscaper.UPPER_HEX_DIGITS[i >>> 2], '%', PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8], c5, '%', c4, c3 };
        }
        if (i > 1114111) {
            throw new IllegalArgumentException("Invalid unicode character value " + i);
        }
        final char c6 = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
        i >>>= 4;
        final char c7 = PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8];
        i >>>= 2;
        final char c8 = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
        i >>>= 4;
        final char c9 = PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8];
        i >>>= 2;
        final char c10 = PercentEscaper.UPPER_HEX_DIGITS[i & 0xF];
        i >>>= 4;
        return new char[] { '%', 'F', PercentEscaper.UPPER_HEX_DIGITS[i >>> 2 & 0x7], '%', PercentEscaper.UPPER_HEX_DIGITS[(i & 0x3) | 0x8], c10, '%', c9, c8, '%', c7, c6 };
    }
    
    @Override
    protected int nextEscapeIndex(final CharSequence charSequence, int i, final int n) {
        Preconditions.checkNotNull(charSequence);
        while (i < n) {
            final char char1 = charSequence.charAt(i);
            if (char1 >= this.safeOctets.length || !this.safeOctets[char1]) {
                break;
            }
            ++i;
        }
        return i;
    }
}
