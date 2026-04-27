// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind.util;

import java.util.Calendar;
import java.text.ParseException;
import java.io.Serializable;
import java.text.ParsePosition;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;

public class ISO8601Utils
{
    private static final TimeZone TIMEZONE_UTC;
    private static final String UTC_ID = "UTC";
    
    static {
        TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    }
    
    private static boolean checkOffset(final String s, final int index, final char c) {
        return index < s.length() && s.charAt(index) == c;
    }
    
    public static String format(final Date date) {
        return format(date, false, ISO8601Utils.TIMEZONE_UTC);
    }
    
    public static String format(final Date date, final boolean b) {
        return format(date, b, ISO8601Utils.TIMEZONE_UTC);
    }
    
    public static String format(final Date time, final boolean b, final TimeZone zone) {
        int length = 0;
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(zone, Locale.US);
        gregorianCalendar.setTime(time);
        final int length2 = "yyyy-MM-ddThh:mm:ss".length();
        if (b) {
            length = ".sss".length();
        }
        int n;
        if (zone.getRawOffset() != 0) {
            n = "+hh:mm".length();
        }
        else {
            n = "Z".length();
        }
        final StringBuilder sb = new StringBuilder(n + (length2 + length));
        padInt(sb, gregorianCalendar.get(1), "yyyy".length());
        sb.append('-');
        padInt(sb, gregorianCalendar.get(2) + 1, "MM".length());
        sb.append('-');
        padInt(sb, gregorianCalendar.get(5), "dd".length());
        sb.append('T');
        padInt(sb, gregorianCalendar.get(11), "hh".length());
        sb.append(':');
        padInt(sb, gregorianCalendar.get(12), "mm".length());
        sb.append(':');
        padInt(sb, gregorianCalendar.get(13), "ss".length());
        if (b) {
            sb.append('.');
            padInt(sb, gregorianCalendar.get(14), "sss".length());
        }
        final int offset = zone.getOffset(gregorianCalendar.getTimeInMillis());
        if (offset == 0) {
            sb.append('Z');
        }
        else {
            final int abs = Math.abs(offset / 60000 / 60);
            final int abs2 = Math.abs(offset / 60000 % 60);
            char c;
            if (offset >= 0) {
                c = '+';
            }
            else {
                c = '-';
            }
            sb.append(c);
            padInt(sb, abs, "hh".length());
            sb.append(':');
            padInt(sb, abs2, "mm".length());
        }
        return sb.toString();
    }
    
    private static int indexOfNonDigit(final String s, int i) {
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 < '0' || char1 > '9') {
                return i;
            }
            ++i;
        }
        return s.length();
    }
    
    private static void padInt(final StringBuilder sb, int i, final int n) {
        final String string = Integer.toString(i);
        for (i = n - string.length(); i > 0; --i) {
            sb.append('0');
        }
        sb.append(string);
    }
    
    public static Date parse(String s, final ParsePosition parsePosition) throws ParseException {
        try {
            final int index = parsePosition.getIndex();
            int n = index + 4;
            final int int1 = parseInt(s, index, n);
            if (checkOffset(s, n, '-')) {
                ++n;
            }
            final int n2 = n + 2;
            final int int2 = parseInt(s, n, n2);
            int n3;
            if (!checkOffset(s, n2, '-')) {
                n3 = n2;
            }
            else {
                n3 = n2 + 1;
            }
            int indexOfNonDigit = n3 + 2;
            final int int3 = parseInt(s, n3, indexOfNonDigit);
            final boolean checkOffset = checkOffset(s, indexOfNonDigit, 'T');
            if (!checkOffset && s.length() <= indexOfNonDigit) {
                final Serializable s2 = new GregorianCalendar(int1, int2 - 1, int3);
                parsePosition.setIndex(indexOfNonDigit);
                return ((Calendar)s2).getTime();
            }
            int value;
            int value2;
            int value3;
            int value4;
            if (!checkOffset) {
                value = 0;
                value2 = 0;
                value3 = 0;
                value4 = 0;
            }
            else {
                final int n4 = indexOfNonDigit + 1;
                int n5 = n4 + 2;
                final int int4 = parseInt(s, n4, n5);
                if (checkOffset(s, n5, ':')) {
                    ++n5;
                }
                indexOfNonDigit = n5 + 2;
                final int int5 = parseInt(s, n5, indexOfNonDigit);
                if (checkOffset(s, indexOfNonDigit, ':')) {
                    ++indexOfNonDigit;
                }
                if (s.length() <= indexOfNonDigit) {
                    value3 = int5;
                    value4 = int4;
                    value = 0;
                    value2 = 0;
                }
                else {
                    final char char1 = s.charAt(indexOfNonDigit);
                    if (char1 != 'Z' && char1 != '+' && char1 != '-') {
                        final int n6 = indexOfNonDigit + 2;
                        final int int6 = parseInt(s, indexOfNonDigit, n6);
                        int n7;
                        if (int6 <= 59) {
                            n7 = int6;
                        }
                        else if ((n7 = int6) < 63) {
                            n7 = 59;
                        }
                        if (!checkOffset(s, n6, '.')) {
                            value4 = int4;
                            value2 = n7;
                            indexOfNonDigit = n6;
                            value3 = int5;
                            value = 0;
                        }
                        else {
                            final int n8 = n6 + 1;
                            indexOfNonDigit = indexOfNonDigit(s, n8 + 1);
                            final int min = Math.min(indexOfNonDigit, n8 + 3);
                            int int7 = parseInt(s, n8, min);
                            switch (min - n8) {
                                case 2: {
                                    int7 *= 10;
                                    break;
                                }
                                case 1: {
                                    int7 *= 100;
                                    break;
                                }
                            }
                            final int n9 = int4;
                            value2 = n7;
                            value3 = int5;
                            value = int7;
                            value4 = n9;
                        }
                    }
                    else {
                        value3 = int5;
                        value4 = int4;
                        value = 0;
                        value2 = 0;
                    }
                }
            }
            if (s.length() <= indexOfNonDigit) {
                throw new IllegalArgumentException("No time zone indicator");
            }
            final char char2 = s.charAt(indexOfNonDigit);
            if (char2 == 'Z' || char2 != '+') {
                goto Label_0788;
            }
            Serializable s2;
            final String str = (String)(s2 = s.substring(indexOfNonDigit));
            if (str.length() < 5) {
                s2 = new StringBuilder();
                s2 = ((StringBuilder)s2).append(str).append("00").toString();
            }
            final int n10 = indexOfNonDigit + ((String)s2).length();
            if ("+0000".equals(s2)) {
                s2 = ISO8601Utils.TIMEZONE_UTC;
                indexOfNonDigit = n10;
                final GregorianCalendar gregorianCalendar = new GregorianCalendar((TimeZone)s2);
                gregorianCalendar.setLenient(false);
                gregorianCalendar.set(1, int1);
                gregorianCalendar.set(2, int2 - 1);
                gregorianCalendar.set(5, int3);
                gregorianCalendar.set(11, value4);
                gregorianCalendar.set(12, value3);
                gregorianCalendar.set(13, value2);
                gregorianCalendar.set(14, value);
                parsePosition.setIndex(indexOfNonDigit);
                return gregorianCalendar.getTime();
            }
            goto Label_0854;
        }
        catch (final IndexOutOfBoundsException ex) {}
        catch (final NumberFormatException s2) {
            goto Label_0664;
        }
        catch (final IllegalArgumentException s2) {
            goto Label_0664;
        }
        s = null;
        goto Label_0693;
        goto Label_0738;
    }
    
    private static int parseInt(final String s, final int beginIndex, final int n) throws NumberFormatException {
        int n2 = 0;
        if (beginIndex >= 0 && n <= s.length() && beginIndex <= n) {
            int i;
            if (beginIndex >= n) {
                i = beginIndex;
            }
            else {
                i = beginIndex + 1;
                final int digit = Character.digit(s.charAt(beginIndex), 10);
                if (digit < 0) {
                    throw new NumberFormatException("Invalid number: " + s.substring(beginIndex, n));
                }
                n2 = -digit;
            }
            while (i < n) {
                final int digit2 = Character.digit(s.charAt(i), 10);
                if (digit2 < 0) {
                    throw new NumberFormatException("Invalid number: " + s.substring(beginIndex, n));
                }
                n2 = n2 * 10 - digit2;
                ++i;
            }
            return -n2;
        }
        throw new NumberFormatException(s);
    }
}
