package android.support.v4.util;

import android.support.annotation.RestrictTo;
import java.io.PrintWriter;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class TimeUtils {
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static char[] sFormatStr = new char[24];
    private static final Object sFormatSync = new Object();

    private TimeUtils() {
    }

    private static int accumField(int i, int i2, boolean z, int i3) {
        if (i > 99 || (z && i3 >= 3)) {
            return i2 + 3;
        }
        if (i > 9 || (z && i3 >= 2)) {
            return i2 + 2;
        }
        if (!z && i <= 0) {
            return 0;
        }
        return i2 + 1;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, long j2, PrintWriter printWriter) {
        if (j == 0) {
            printWriter.print("--");
        } else {
            formatDuration(j - j2, printWriter, 0);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, PrintWriter printWriter) {
        formatDuration(j, printWriter, 0);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, PrintWriter printWriter, int i) {
        synchronized (sFormatSync) {
            printWriter.print(new String(sFormatStr, 0, formatDurationLocked(j, i)));
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, StringBuilder sb) {
        synchronized (sFormatSync) {
            sb.append(sFormatStr, 0, formatDurationLocked(j, 0));
        }
    }

    private static int formatDurationLocked(long j, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        if (sFormatStr.length < i) {
            sFormatStr = new char[i];
        }
        char[] cArr = sFormatStr;
        if (j == 0) {
            int i8 = i - 1;
            while (i8 > 0) {
                cArr[0] = ' ';
            }
            cArr[0] = '0';
            return 1;
        }
        if (!(j <= 0)) {
            i2 = 43;
        } else {
            j = -j;
            i2 = 45;
        }
        int i9 = (int) (j % 1000);
        int floor = (int) Math.floor((double) (j / 1000));
        int i10 = 0;
        if (floor > SECONDS_PER_DAY) {
            i10 = floor / SECONDS_PER_DAY;
            floor -= SECONDS_PER_DAY * i10;
        }
        if (floor <= SECONDS_PER_HOUR) {
            i3 = 0;
            i4 = floor;
        } else {
            int i11 = floor / SECONDS_PER_HOUR;
            i3 = i11;
            i4 = floor - (i11 * SECONDS_PER_HOUR);
        }
        if (i4 <= 60) {
            i5 = 0;
            i6 = i4;
        } else {
            int i12 = i4 / 60;
            i5 = i12;
            i6 = i4 - (i12 * 60);
        }
        if (i == 0) {
            i7 = 0;
        } else {
            int accumField = accumField(i10, 1, false, 0);
            int accumField2 = accumField + accumField(i3, 1, accumField > 0, 2);
            int accumField3 = accumField2 + accumField(i5, 1, accumField2 > 0, 2);
            int accumField4 = accumField3 + accumField(i6, 1, accumField3 > 0, 2);
            i7 = 0;
            int accumField5 = accumField(i9, 2, true, accumField4 <= 0 ? 0 : 3) + 1 + accumField4;
            while (accumField5 < i) {
                cArr[i7] = ' ';
                accumField5++;
                i7++;
            }
        }
        cArr[i7] = (char) i2;
        int i13 = i7 + 1;
        boolean z = i != 0;
        int printField = printField(cArr, i10, 'd', i13, false, 0);
        int printField2 = printField(cArr, i3, 'h', printField, printField != i13, !z ? 0 : 2);
        int printField3 = printField(cArr, i5, 'm', printField2, printField2 != i13, !z ? 0 : 2);
        int printField4 = printField(cArr, i6, 's', printField3, printField3 != i13, !z ? 0 : 2);
        int printField5 = printField(cArr, i9, 'm', printField4, true, (z && printField4 != i13) ? 3 : 0);
        cArr[printField5] = 's';
        return printField5 + 1;
    }

    private static int printField(char[] cArr, int i, char c, int i2, boolean z, int i3) {
        int i4;
        int i5;
        if (!z && i <= 0) {
            return i2;
        }
        if ((z && i3 >= 3) || i > 99) {
            int i6 = i / 100;
            cArr[i2] = (char) ((char) (i6 + 48));
            i4 = i2 + 1;
            i5 = i - (i6 * 100);
        } else {
            i4 = i2;
            i5 = i;
        }
        if ((z && i3 >= 2) || i5 > 9 || i2 != i4) {
            int i7 = i5 / 10;
            cArr[i4] = (char) ((char) (i7 + 48));
            i4++;
            i5 -= i7 * 10;
        }
        cArr[i4] = (char) ((char) (i5 + 48));
        int i8 = i4 + 1;
        cArr[i8] = (char) c;
        return i8 + 1;
    }
}
