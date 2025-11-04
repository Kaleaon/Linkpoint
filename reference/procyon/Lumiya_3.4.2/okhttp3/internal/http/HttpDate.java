// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import java.io.Serializable;
import java.text.ParsePosition;
import java.util.Date;
import okhttp3.internal.Util;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.DateFormat;

public final class HttpDate
{
    private static final DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS;
    private static final String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
    public static final long MAX_DATE = 253402300799999L;
    private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT;
    
    static {
        STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                simpleDateFormat.setLenient(false);
                simpleDateFormat.setTimeZone(Util.UTC);
                return simpleDateFormat;
            }
        };
        BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM d yyyy HH:mm:ss z" };
        BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];
    }
    
    private HttpDate() {
    }
    
    public static String format(final Date date) {
        return HttpDate.STANDARD_DATE_FORMAT.get().format(date);
    }
    
    public static Date parse(final String s) {
        Serializable s2 = null;
        Label_0068: {
            if (s.length() == 0) {
                break Label_0068;
            }
            final ParsePosition parsePosition = new ParsePosition(0);
            s2 = HttpDate.STANDARD_DATE_FORMAT.get().parse(s, parsePosition);
            if (parsePosition.getIndex() == s.length()) {
                return (Date)s2;
            }
            synchronized (HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS) {
                for (int length = HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length, i = 0; i < length; ++i) {
                    s2 = HttpDate.BROWSER_COMPATIBLE_DATE_FORMATS[i];
                    if (s2 == null) {
                        s2 = new SimpleDateFormat(HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS[i], Locale.US);
                        ((DateFormat)s2).setTimeZone(Util.UTC);
                        HttpDate.BROWSER_COMPATIBLE_DATE_FORMATS[i] = (DateFormat)s2;
                    }
                    parsePosition.setIndex(0);
                    s2 = ((DateFormat)s2).parse(s, parsePosition);
                    if (parsePosition.getIndex() != 0) {
                        break Label_0068;
                    }
                }
                return null;
                return null;
            }
        }
        final String[] array;
        monitorexit(array);
        return (Date)s2;
    }
}
