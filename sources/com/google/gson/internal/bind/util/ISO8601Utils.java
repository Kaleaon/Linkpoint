package com.google.gson.internal.bind.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone(UTC_ID);
    private static final String UTC_ID = "UTC";

    private static boolean checkOffset(String str, int i, char c) {
        return i < str.length() && str.charAt(i) == c;
    }

    public static String format(Date date) {
        return format(date, false, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean z) {
        return format(date, z, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean z, TimeZone timeZone) {
        int i = 0;
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone, Locale.US);
        gregorianCalendar.setTime(date);
        int length = "yyyy-MM-ddThh:mm:ss".length();
        if (z) {
            i = ".sss".length();
        }
        StringBuilder sb = new StringBuilder((timeZone.getRawOffset() != 0 ? "+hh:mm".length() : "Z".length()) + length + i);
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
        if (z) {
            sb.append('.');
            padInt(sb, gregorianCalendar.get(14), "sss".length());
        }
        int offset = timeZone.getOffset(gregorianCalendar.getTimeInMillis());
        if (offset == 0) {
            sb.append('Z');
        } else {
            int abs = Math.abs((offset / 60000) / 60);
            int abs2 = Math.abs((offset / 60000) % 60);
            sb.append(offset >= 0 ? '+' : '-');
            padInt(sb, abs, "hh".length());
            sb.append(':');
            padInt(sb, abs2, "mm".length());
        }
        return sb.toString();
    }

    private static int indexOfNonDigit(String str, int i) {
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (charAt < '0' || charAt > '9') {
                return i;
            }
            i++;
        }
        return str.length();
    }

    private static void padInt(StringBuilder sb, int i, int i2) {
        String num = Integer.toString(i);
        for (int length = i2 - num.length(); length > 0; length--) {
            sb.append('0');
        }
        sb.append(num);
    }

    /* JADX WARNING: Removed duplicated region for block: B:64:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0265  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Date parse(java.lang.String r14, java.text.ParsePosition r15) throws java.text.ParseException {
        /*
            r13 = 43
            r12 = 5
            r11 = 45
            r1 = 0
            r0 = 0
            int r3 = r15.getIndex()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r2 = r3 + 4
            int r7 = parseInt(r14, r3, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3 = 45
            boolean r3 = checkOffset(r14, r2, r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r3 != 0) goto L_0x00ae
            r3 = r2
        L_0x001a:
            int r2 = r3 + 2
            int r8 = parseInt(r14, r3, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3 = 45
            boolean r3 = checkOffset(r14, r2, r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r3 != 0) goto L_0x00b3
            r3 = r2
        L_0x0029:
            int r2 = r3 + 2
            int r9 = parseInt(r14, r3, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3 = 84
            boolean r3 = checkOffset(r14, r2, r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r3 == 0) goto L_0x00b8
        L_0x0037:
            if (r3 != 0) goto L_0x00cd
            r3 = r0
            r4 = r0
            r5 = r0
            r6 = r0
        L_0x003d:
            int r0 = r14.length()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r0 <= r2) goto L_0x0155
            char r0 = r14.charAt(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r10 = 90
            if (r0 == r10) goto L_0x01d1
            if (r0 != r13) goto L_0x01d7
        L_0x004d:
            java.lang.String r0 = r14.substring(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r10 = r0.length()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r10 >= r12) goto L_0x006b
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r10.<init>()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r0 = r10.append(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r10 = "00"
            java.lang.StringBuilder r0 = r0.append(r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r0 = r0.toString()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x006b:
            int r10 = r0.length()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r2 = r2 + r10
            java.lang.String r10 = "+0000"
            boolean r10 = r10.equals(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r10 == 0) goto L_0x01fd
        L_0x0079:
            java.util.TimeZone r0 = TIMEZONE_UTC     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x007b:
            java.util.GregorianCalendar r10 = new java.util.GregorianCalendar     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r10.<init>(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 0
            r10.setLenient(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 1
            r10.set(r0, r7)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r0 = r8 + -1
            r7 = 2
            r10.set(r7, r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 5
            r10.set(r0, r9)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 11
            r10.set(r0, r6)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 12
            r10.set(r0, r5)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 13
            r10.set(r0, r4)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r0 = 14
            r10.set(r0, r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r15.setIndex(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.util.Date r0 = r10.getTime()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            return r0
        L_0x00ae:
            int r2 = r2 + 1
            r3 = r2
            goto L_0x001a
        L_0x00b3:
            int r2 = r2 + 1
            r3 = r2
            goto L_0x0029
        L_0x00b8:
            int r4 = r14.length()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r4 > r2) goto L_0x0037
            java.util.GregorianCalendar r0 = new java.util.GregorianCalendar     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r3 = r8 + -1
            r0.<init>(r7, r3, r9)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r15.setIndex(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.util.Date r0 = r0.getTime()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            return r0
        L_0x00cd:
            int r3 = r2 + 1
            int r2 = r3 + 2
            int r4 = parseInt(r14, r3, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3 = 58
            boolean r3 = checkOffset(r14, r2, r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r3 != 0) goto L_0x00f8
            r3 = r2
        L_0x00de:
            int r2 = r3 + 2
            int r3 = parseInt(r14, r3, r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r5 = 58
            boolean r5 = checkOffset(r14, r2, r5)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r5 != 0) goto L_0x00fc
        L_0x00ec:
            int r5 = r14.length()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r5 > r2) goto L_0x00ff
            r5 = r3
            r6 = r4
            r3 = r0
            r4 = r0
            goto L_0x003d
        L_0x00f8:
            int r2 = r2 + 1
            r3 = r2
            goto L_0x00de
        L_0x00fc:
            int r2 = r2 + 1
            goto L_0x00ec
        L_0x00ff:
            char r5 = r14.charAt(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r6 = 90
            if (r5 != r6) goto L_0x010d
        L_0x0107:
            r5 = r3
            r6 = r4
            r3 = r0
            r4 = r0
            goto L_0x003d
        L_0x010d:
            if (r5 == r13) goto L_0x0107
            if (r5 == r11) goto L_0x0107
            int r5 = r2 + 2
            int r2 = parseInt(r14, r2, r5)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r6 = 59
            if (r2 > r6) goto L_0x012a
        L_0x011b:
            r6 = 46
            boolean r6 = checkOffset(r14, r5, r6)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r6 != 0) goto L_0x0131
            r6 = r4
            r4 = r2
            r2 = r5
            r5 = r3
            r3 = r0
            goto L_0x003d
        L_0x012a:
            r6 = 63
            if (r2 >= r6) goto L_0x011b
            r2 = 59
            goto L_0x011b
        L_0x0131:
            int r6 = r5 + 1
            int r0 = r6 + 1
            int r5 = indexOfNonDigit(r14, r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r0 = r6 + 3
            int r10 = java.lang.Math.min(r5, r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r0 = parseInt(r14, r6, r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r6 = r10 - r6
            switch(r6) {
                case 1: goto L_0x0152;
                case 2: goto L_0x014f;
                default: goto L_0x0148;
            }     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x0148:
            r6 = r4
            r4 = r2
            r2 = r5
            r5 = r3
            r3 = r0
            goto L_0x003d
        L_0x014f:
            int r0 = r0 * 10
            goto L_0x0148
        L_0x0152:
            int r0 = r0 * 100
            goto L_0x0148
        L_0x0155:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r2 = "No time zone indicator"
            r0.<init>(r2)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            throw r0     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x015e:
            r0 = move-exception
        L_0x015f:
            r2 = r0
        L_0x0160:
            if (r14 == 0) goto L_0x0265
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 34
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r14)
            java.lang.String r1 = "'"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
        L_0x017c:
            java.lang.String r1 = r2.getMessage()
            if (r1 != 0) goto L_0x0268
        L_0x0182:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "("
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.Class r3 = r2.getClass()
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r3 = ")"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
        L_0x01a5:
            java.text.ParseException r3 = new java.text.ParseException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Failed to parse date ["
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r0 = r4.append(r0)
            java.lang.String r4 = "]: "
            java.lang.StringBuilder r0 = r0.append(r4)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = r15.getIndex()
            r3.<init>(r0, r1)
            r3.initCause(r2)
            throw r3
        L_0x01d1:
            java.util.TimeZone r0 = TIMEZONE_UTC     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            int r2 = r2 + 1
            goto L_0x007b
        L_0x01d7:
            if (r0 == r11) goto L_0x004d
            java.lang.IndexOutOfBoundsException r2 = new java.lang.IndexOutOfBoundsException     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3.<init>()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r4 = "Invalid time zone indicator '"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r3 = "'"
            java.lang.StringBuilder r0 = r0.append(r3)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r0 = r0.toString()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r2.<init>(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            throw r2     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x01fa:
            r0 = move-exception
            goto L_0x015f
        L_0x01fd:
            java.lang.String r10 = "+00:00"
            boolean r10 = r10.equals(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r10 != 0) goto L_0x0079
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r10.<init>()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r11 = "GMT"
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r0 = r10.append(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r10 = r0.toString()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.util.TimeZone r0 = java.util.TimeZone.getTimeZone(r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r11 = r0.getID()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            boolean r12 = r11.equals(r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r12 != 0) goto L_0x007b
            java.lang.String r12 = ":"
            java.lang.String r13 = ""
            java.lang.String r11 = r11.replace(r12, r13)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            boolean r11 = r11.equals(r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            if (r11 != 0) goto L_0x007b
            java.lang.IndexOutOfBoundsException r2 = new java.lang.IndexOutOfBoundsException     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r3.<init>()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r4 = "Mismatching time zone indicator: "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r3 = r3.append(r10)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r4 = " given, resolves to "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r0 = r0.getID()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            java.lang.String r0 = r0.toString()     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            r2.<init>(r0)     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
            throw r2     // Catch:{ IndexOutOfBoundsException -> 0x015e, NumberFormatException -> 0x01fa, IllegalArgumentException -> 0x0261 }
        L_0x0261:
            r0 = move-exception
            r2 = r0
            goto L_0x0160
        L_0x0265:
            r0 = r1
            goto L_0x017c
        L_0x0268:
            boolean r3 = r1.isEmpty()
            if (r3 != 0) goto L_0x0182
            goto L_0x01a5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.bind.util.ISO8601Utils.parse(java.lang.String, java.text.ParsePosition):java.util.Date");
    }

    private static int parseInt(String str, int i, int i2) throws NumberFormatException {
        int i3;
        int i4 = 0;
        if (i >= 0 && i2 <= str.length() && i <= i2) {
            if (i >= i2) {
                i3 = i;
            } else {
                i3 = i + 1;
                int digit = Character.digit(str.charAt(i), 10);
                if (digit >= 0) {
                    i4 = -digit;
                } else {
                    throw new NumberFormatException("Invalid number: " + str.substring(i, i2));
                }
            }
            while (i3 < i2) {
                int i5 = i3 + 1;
                int digit2 = Character.digit(str.charAt(i3), 10);
                if (digit2 >= 0) {
                    i4 = (i4 * 10) - digit2;
                    i3 = i5;
                } else {
                    throw new NumberFormatException("Invalid number: " + str.substring(i, i2));
                }
            }
            return -i4;
        }
        throw new NumberFormatException(str);
    }
}
