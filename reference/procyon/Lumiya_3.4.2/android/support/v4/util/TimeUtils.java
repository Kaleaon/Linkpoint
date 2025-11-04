// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import java.io.PrintWriter;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public final class TimeUtils
{
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static char[] sFormatStr;
    private static final Object sFormatSync;
    
    static {
        sFormatSync = new Object();
        TimeUtils.sFormatStr = new char[24];
    }
    
    private TimeUtils() {
    }
    
    private static int accumField(final int n, final int n2, final boolean b, final int n3) {
        if (n > 99 || (b && n3 >= 3)) {
            return n2 + 3;
        }
        if (n > 9 || (b && n3 >= 2)) {
            return n2 + 2;
        }
        if (!b && n <= 0) {
            return 0;
        }
        return n2 + 1;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static void formatDuration(final long n, final long n2, final PrintWriter printWriter) {
        if (n == 0L) {
            printWriter.print("--");
            return;
        }
        formatDuration(n - n2, printWriter, 0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static void formatDuration(final long n, final PrintWriter printWriter) {
        formatDuration(n, printWriter, 0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static void formatDuration(final long n, final PrintWriter printWriter, int formatDurationLocked) {
        synchronized (TimeUtils.sFormatSync) {
            formatDurationLocked = formatDurationLocked(n, formatDurationLocked);
            printWriter.print(new String(TimeUtils.sFormatStr, 0, formatDurationLocked));
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static void formatDuration(final long n, final StringBuilder sb) {
        synchronized (TimeUtils.sFormatSync) {
            sb.append(TimeUtils.sFormatStr, 0, formatDurationLocked(n, 0));
        }
    }
    
    private static int formatDurationLocked(long n, int printField) {
        if (TimeUtils.sFormatStr.length < printField) {
            TimeUtils.sFormatStr = new char[printField];
        }
        final char[] sFormatStr = TimeUtils.sFormatStr;
        if (n == 0L) {
            while (printField - 1 > 0) {
                sFormatStr[0] = 32;
            }
            sFormatStr[0] = 48;
            return 1;
        }
        int n2;
        if (n <= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        int n3;
        if (n2 == 0) {
            n3 = 43;
        }
        else {
            n = -n;
            n3 = 45;
        }
        final int n4 = (int)(n % 1000L);
        int n5 = (int)Math.floor((double)(n / 1000L));
        int n6 = 0;
        if (n5 > 86400) {
            n6 = n5 / 86400;
            n5 -= 86400 * n6;
        }
        int n7;
        if (n5 <= 3600) {
            n7 = 0;
        }
        else {
            n5 -= (n7 = n5 / 3600) * 3600;
        }
        int n8;
        int n9;
        if (n5 <= 60) {
            n8 = 0;
            n9 = n5;
        }
        else {
            n9 = n5 - (n8 = n5 / 60) * 60;
        }
        int n10;
        if (printField == 0) {
            n10 = 0;
        }
        else {
            final int accumField = accumField(n6, 1, false, 0);
            final int n11 = accumField + accumField(n7, 1, accumField > 0, 2);
            final int n12 = n11 + accumField(n8, 1, n11 > 0, 2);
            final int n13 = n12 + accumField(n9, 1, n12 > 0, 2);
            int n14;
            if (n13 <= 0) {
                n14 = 0;
            }
            else {
                n14 = 3;
            }
            final int accumField2 = accumField(n4, 2, true, n14);
            int n15 = 0;
            int n16 = accumField2 + 1 + n13;
            while (true) {
                n10 = n15;
                if (n16 >= printField) {
                    break;
                }
                sFormatStr[n15] = 32;
                ++n16;
                ++n15;
            }
        }
        sFormatStr[n10] = (char)n3;
        final int n17 = n10 + 1;
        if (printField == 0) {
            printField = 0;
        }
        else {
            printField = 1;
        }
        final int printField2 = printField(sFormatStr, n6, 'd', n17, false, 0);
        final boolean b = printField2 != n17;
        int n18;
        if (printField == 0) {
            n18 = 0;
        }
        else {
            n18 = 2;
        }
        final int printField3 = printField(sFormatStr, n7, 'h', printField2, b, n18);
        final boolean b2 = printField3 != n17;
        int n19;
        if (printField == 0) {
            n19 = 0;
        }
        else {
            n19 = 2;
        }
        final int printField4 = printField(sFormatStr, n8, 'm', printField3, b2, n19);
        final boolean b3 = printField4 != n17;
        int n20;
        if (printField == 0) {
            n20 = 0;
        }
        else {
            n20 = 2;
        }
        final int printField5 = printField(sFormatStr, n9, 's', printField4, b3, n20);
        if (printField != 0 && printField5 != n17) {
            printField = 3;
        }
        else {
            printField = 0;
        }
        printField = printField(sFormatStr, n4, 'm', printField5, true, printField);
        sFormatStr[printField] = 115;
        return printField + 1;
    }
    
    private static int printField(final char[] array, int n, final char c, int n2, final boolean b, final int n3) {
        if (b || n > 0) {
            int n4;
            if ((!b || n3 < 3) && n <= 99) {
                n4 = n2;
            }
            else {
                final int n5 = n / 100;
                array[n2] = (char)(n5 + 48);
                n4 = n2 + 1;
                n -= n5 * 100;
            }
            Label_0056: {
                if (!b || n3 < 2) {
                    if (n <= 9 && n2 == n4) {
                        break Label_0056;
                    }
                }
                n2 = n / 10;
                array[n4] = (char)(n2 + 48);
                ++n4;
                n -= n2 * 10;
            }
            array[n4] = (char)(n + 48);
            n = n4 + 1;
            array[n] = c;
            n2 = n + 1;
        }
        return n2;
    }
}
