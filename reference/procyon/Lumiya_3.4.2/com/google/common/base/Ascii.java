// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Ascii
{
    public static final byte ACK = 6;
    public static final byte BEL = 7;
    public static final byte BS = 8;
    public static final byte CAN = 24;
    public static final byte CR = 13;
    public static final byte DC1 = 17;
    public static final byte DC2 = 18;
    public static final byte DC3 = 19;
    public static final byte DC4 = 20;
    public static final byte DEL = Byte.MAX_VALUE;
    public static final byte DLE = 16;
    public static final byte EM = 25;
    public static final byte ENQ = 5;
    public static final byte EOT = 4;
    public static final byte ESC = 27;
    public static final byte ETB = 23;
    public static final byte ETX = 3;
    public static final byte FF = 12;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final char MAX = '\u007f';
    public static final char MIN = '\0';
    public static final byte NAK = 21;
    public static final byte NL = 10;
    public static final byte NUL = 0;
    public static final byte RS = 30;
    public static final byte SI = 15;
    public static final byte SO = 14;
    public static final byte SOH = 1;
    public static final byte SP = 32;
    public static final byte SPACE = 32;
    public static final byte STX = 2;
    public static final byte SUB = 26;
    public static final byte SYN = 22;
    public static final byte US = 31;
    public static final byte VT = 11;
    public static final byte XOFF = 19;
    public static final byte XON = 17;
    
    private Ascii() {
    }
    
    @Beta
    public static boolean equalsIgnoreCase(final CharSequence charSequence, final CharSequence charSequence2) {
        final int length = charSequence.length();
        if (charSequence == charSequence2) {
            return true;
        }
        if (length == charSequence2.length()) {
            for (int i = 0; i < length; ++i) {
                final char char1 = charSequence.charAt(i);
                final char char2 = charSequence2.charAt(i);
                if (char1 != char2) {
                    final int alphaIndex = getAlphaIndex(char1);
                    if (alphaIndex >= 26 || alphaIndex != getAlphaIndex(char2)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private static int getAlphaIndex(final char c) {
        return (char)((c | ' ') - 97);
    }
    
    public static boolean isLowerCase(final char c) {
        return c >= 'a' && c <= 'z';
    }
    
    public static boolean isUpperCase(final char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    public static char toLowerCase(char c) {
        if (isUpperCase(c)) {
            c ^= ' ';
        }
        return c;
    }
    
    public static String toLowerCase(final CharSequence charSequence) {
        int i = 0;
        if (!(charSequence instanceof String)) {
            final int length = charSequence.length();
            final StringBuilder sb = new StringBuilder(length);
            while (i < length) {
                sb.append(toLowerCase(charSequence.charAt(i)));
                ++i;
            }
            return sb.toString();
        }
        return toLowerCase((String)charSequence);
    }
    
    public static String toLowerCase(final String s) {
        for (int i = 0, length = s.length(); i < length; ++i) {
            if (isUpperCase(s.charAt(i))) {
                final char[] charArray = s.toCharArray();
                while (i < length) {
                    final char c = charArray[i];
                    if (isUpperCase(c)) {
                        charArray[i] = (char)(c ^ ' ');
                    }
                    ++i;
                }
                return String.valueOf(charArray);
            }
        }
        return s;
    }
    
    public static char toUpperCase(char c) {
        if (isLowerCase(c)) {
            c &= '_';
        }
        return c;
    }
    
    public static String toUpperCase(final CharSequence charSequence) {
        int i = 0;
        if (!(charSequence instanceof String)) {
            final int length = charSequence.length();
            final StringBuilder sb = new StringBuilder(length);
            while (i < length) {
                sb.append(toUpperCase(charSequence.charAt(i)));
                ++i;
            }
            return sb.toString();
        }
        return toUpperCase((String)charSequence);
    }
    
    public static String toUpperCase(final String s) {
        for (int i = 0, length = s.length(); i < length; ++i) {
            if (isLowerCase(s.charAt(i))) {
                final char[] charArray = s.toCharArray();
                while (i < length) {
                    final char c = charArray[i];
                    if (isLowerCase(c)) {
                        charArray[i] = (char)(c & '_');
                    }
                    ++i;
                }
                return String.valueOf(charArray);
            }
        }
        return s;
    }
    
    @Beta
    public static String truncate(CharSequence string, final int n, final String str) {
        Preconditions.checkNotNull(string);
        final int end = n - str.length();
        Preconditions.checkArgument(end >= 0, "maxLength (%s) must be >= length of the truncation indicator (%s)", n, str.length());
        if (string.length() <= n) {
            final String s = (String)(string = string.toString());
            if (s.length() <= n) {
                return s;
            }
        }
        return new StringBuilder(n).append(string, 0, end).append(str).toString();
    }
}
