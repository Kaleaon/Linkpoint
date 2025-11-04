// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal;

import java.util.concurrent.ThreadFactory;
import java.io.InterruptedIOException;
import okio.Buffer;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;
import java.util.Locale;
import java.net.IDN;
import java.util.concurrent.TimeUnit;
import okio.Source;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.Closeable;
import java.io.IOException;
import okio.BufferedSource;
import okhttp3.MediaType;
import java.util.regex.Pattern;
import okio.ByteString;
import java.nio.charset.Charset;
import java.util.TimeZone;
import okhttp3.ResponseBody;
import okhttp3.RequestBody;

public final class Util
{
    public static final byte[] EMPTY_BYTE_ARRAY;
    public static final RequestBody EMPTY_REQUEST;
    public static final ResponseBody EMPTY_RESPONSE;
    public static final String[] EMPTY_STRING_ARRAY;
    public static final TimeZone UTC;
    private static final Charset UTF_16_BE;
    private static final ByteString UTF_16_BE_BOM;
    private static final Charset UTF_16_LE;
    private static final ByteString UTF_16_LE_BOM;
    private static final Charset UTF_32_BE;
    private static final ByteString UTF_32_BE_BOM;
    private static final Charset UTF_32_LE;
    private static final ByteString UTF_32_LE_BOM;
    public static final Charset UTF_8;
    private static final ByteString UTF_8_BOM;
    private static final Pattern VERIFY_AS_IP_ADDRESS;
    
    static {
        EMPTY_BYTE_ARRAY = new byte[0];
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_RESPONSE = ResponseBody.create(null, Util.EMPTY_BYTE_ARRAY);
        EMPTY_REQUEST = RequestBody.create(null, Util.EMPTY_BYTE_ARRAY);
        UTF_8_BOM = ByteString.decodeHex("efbbbf");
        UTF_16_BE_BOM = ByteString.decodeHex("feff");
        UTF_16_LE_BOM = ByteString.decodeHex("fffe");
        UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
        UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
        UTF_8 = Charset.forName("UTF-8");
        UTF_16_BE = Charset.forName("UTF-16BE");
        UTF_16_LE = Charset.forName("UTF-16LE");
        UTF_32_BE = Charset.forName("UTF-32BE");
        UTF_32_LE = Charset.forName("UTF-32LE");
        UTC = TimeZone.getTimeZone("GMT");
        VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
    }
    
    private Util() {
    }
    
    public static Charset bomAwareCharset(final BufferedSource bufferedSource, final Charset charset) throws IOException {
        if (bufferedSource.rangeEquals(0L, Util.UTF_8_BOM)) {
            bufferedSource.skip(Util.UTF_8_BOM.size());
            return Util.UTF_8;
        }
        if (bufferedSource.rangeEquals(0L, Util.UTF_16_BE_BOM)) {
            bufferedSource.skip(Util.UTF_16_BE_BOM.size());
            return Util.UTF_16_BE;
        }
        if (bufferedSource.rangeEquals(0L, Util.UTF_16_LE_BOM)) {
            bufferedSource.skip(Util.UTF_16_LE_BOM.size());
            return Util.UTF_16_LE;
        }
        if (bufferedSource.rangeEquals(0L, Util.UTF_32_BE_BOM)) {
            bufferedSource.skip(Util.UTF_32_BE_BOM.size());
            return Util.UTF_32_BE;
        }
        if (!bufferedSource.rangeEquals(0L, Util.UTF_32_LE_BOM)) {
            return charset;
        }
        bufferedSource.skip(Util.UTF_32_LE_BOM.size());
        return Util.UTF_32_LE;
    }
    
    public static void checkOffsetAndCount(final long n, final long n2, final long n3) {
        final int n4 = 1;
        int n5;
        if ((n2 | n3) < 0L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (n5 == 0) {
            int n6;
            if (n2 > n) {
                n6 = 1;
            }
            else {
                n6 = 0;
            }
            if (n6 == 0) {
                int n7;
                if (n - n2 >= n3) {
                    n7 = n4;
                }
                else {
                    n7 = 0;
                }
                if (n7 != 0) {
                    return;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final RuntimeException ex) {
                throw ex;
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static void closeQuietly(final ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (final RuntimeException ex) {
                throw ex;
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static void closeQuietly(final Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (final AssertionError assertionError) {
                if (!isAndroidGetsocknameError(assertionError)) {
                    throw assertionError;
                }
            }
            catch (final RuntimeException ex) {
                throw ex;
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static String[] concat(final String[] array, final String s) {
        final String[] array2 = new String[array.length + 1];
        System.arraycopy(array, 0, array2, 0, array.length);
        array2[array2.length - 1] = s;
        return array2;
    }
    
    private static boolean containsInvalidHostnameAsciiCodes(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 <= '\u001f' || char1 >= '\u007f') {
                return true;
            }
            if (" #%/:?@[\\]".indexOf(char1) != -1) {
                return true;
            }
        }
        return false;
    }
    
    public static int delimiterOffset(final String s, int i, final int n, final char c) {
        while (i < n) {
            if (s.charAt(i) == c) {
                return i;
            }
            ++i;
        }
        return n;
    }
    
    public static int delimiterOffset(final String s, int i, final int n, final String s2) {
        while (i < n) {
            if (s2.indexOf(s.charAt(i)) != -1) {
                return i;
            }
            ++i;
        }
        return n;
    }
    
    public static boolean discard(final Source source, final int n, final TimeUnit timeUnit) {
        try {
            return skipAll(source, n, timeUnit);
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public static String domainToAscii(String lowerCase) {
        try {
            lowerCase = IDN.toASCII(lowerCase).toLowerCase(Locale.US);
            if (lowerCase.isEmpty()) {
                return null;
            }
            if (!containsInvalidHostnameAsciiCodes(lowerCase)) {
                return lowerCase;
            }
            return null;
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static boolean equal(final Object o, final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    public static String format(final String format, final Object... args) {
        return String.format(Locale.US, format, args);
    }
    
    public static String hostHeader(final HttpUrl httpUrl, final boolean b) {
        String str;
        if (!httpUrl.host().contains(":")) {
            str = httpUrl.host();
        }
        else {
            str = "[" + httpUrl.host() + "]";
        }
        if (b || httpUrl.port() != HttpUrl.defaultPort(httpUrl.scheme())) {
            str = str + ":" + httpUrl.port();
        }
        return str;
    }
    
    public static <T> List<T> immutableList(final List<T> c) {
        return Collections.unmodifiableList((List<? extends T>)new ArrayList<T>((Collection<? extends T>)c));
    }
    
    public static <T> List<T> immutableList(final T... array) {
        return Collections.unmodifiableList((List<? extends T>)Arrays.asList((T[])array.clone()));
    }
    
    public static <T> int indexOf(final T[] array, final T t) {
        for (int i = 0; i < array.length; ++i) {
            if (equal(array[i], t)) {
                return i;
            }
        }
        return -1;
    }
    
    private static <T> List<T> intersect(final T[] array, final T[] array2) {
        final ArrayList list = new ArrayList();
        for (final T t : array) {
            for (final T obj : array2) {
                if (t.equals(obj)) {
                    list.add(obj);
                    break;
                }
            }
        }
        return list;
    }
    
    public static <T> T[] intersect(final Class<T> componentType, final T[] array, final T[] array2) {
        final List<T> intersect = intersect(array, array2);
        return intersect.toArray((T[])Array.newInstance(componentType, intersect.size()));
    }
    
    public static boolean isAndroidGetsocknameError(final AssertionError assertionError) {
        final boolean b = false;
        boolean b2;
        if (assertionError.getCause() == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (assertionError.getMessage() != null) {
                b2 = b;
                if (assertionError.getMessage().contains("getsockname failed")) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public static boolean skipAll(final Source source, final int n, final TimeUnit timeUnit) throws IOException {
        final long nanoTime = System.nanoTime();
        Label_0098: {
            if (source.timeout().hasDeadline()) {
                break Label_0098;
            }
            long a = Long.MAX_VALUE;
            while (true) {
                source.timeout().deadlineNanoTime(Math.min(a, timeUnit.toNanos(n)) + nanoTime);
                Label_0114: {
                    try {
                        final Buffer buffer = new Buffer();
                        while (source.read(buffer, 8192L) != -1L) {
                            buffer.clear();
                        }
                        break Label_0114;
                    }
                    catch (final InterruptedIOException ex) {
                        if (a == Long.MAX_VALUE) {
                            source.timeout().clearDeadline();
                        }
                        else {
                            source.timeout().deadlineNanoTime(a + nanoTime);
                        }
                        return false;
                        a = source.timeout().deadlineNanoTime() - nanoTime;
                        continue;
                        iftrue(Label_0135:)(a != Long.MAX_VALUE);
                        Block_7: {
                            break Block_7;
                            Label_0135: {
                                source.timeout().deadlineNanoTime(a + nanoTime);
                            }
                            return true;
                        }
                        source.timeout().clearDeadline();
                        return true;
                    }
                    finally {
                        while (true) {
                            if (a == Long.MAX_VALUE) {
                                source.timeout().clearDeadline();
                                break Label_0189;
                            }
                            source.timeout().deadlineNanoTime(a + nanoTime);
                            break Label_0189;
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public static int skipLeadingAsciiWhitespace(final String s, int i, final int n) {
        while (i < n) {
            switch (s.charAt(i)) {
                default: {
                    return i;
                }
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    ++i;
                    continue;
                }
            }
        }
        return n;
    }
    
    public static int skipTrailingAsciiWhitespace(final String s, final int n, int i) {
        --i;
        while (i >= n) {
            switch (s.charAt(i)) {
                default: {
                    return i + 1;
                }
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    --i;
                    continue;
                }
            }
        }
        return n;
    }
    
    public static ThreadFactory threadFactory(final String s, final boolean b) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable task) {
                final Thread thread = new Thread(task, s);
                thread.setDaemon(b);
                return thread;
            }
        };
    }
    
    public static void throwIfFatal(final Throwable t) {
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (!(t instanceof LinkageError)) {
            return;
        }
        throw (LinkageError)t;
    }
    
    public static String toHumanReadableAscii(final String s) {
        int codePoint;
        for (int length = s.length(), i = 0; i < length; i += Character.charCount(codePoint)) {
            codePoint = s.codePointAt(i);
            if (codePoint <= 31 || codePoint >= 127) {
                final Buffer buffer = new Buffer();
                buffer.writeUtf8(s, 0, i);
                while (i < length) {
                    final int codePoint2 = s.codePointAt(i);
                    int n;
                    if (codePoint2 > 31 && codePoint2 < 127) {
                        n = codePoint2;
                    }
                    else {
                        n = 63;
                    }
                    buffer.writeUtf8CodePoint(n);
                    i += Character.charCount(codePoint2);
                }
                return buffer.readUtf8();
            }
        }
        return s;
    }
    
    public static String trimSubstring(final String s, int skipLeadingAsciiWhitespace, final int n) {
        skipLeadingAsciiWhitespace = skipLeadingAsciiWhitespace(s, skipLeadingAsciiWhitespace, n);
        return s.substring(skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace(s, skipLeadingAsciiWhitespace, n));
    }
    
    public static boolean verifyAsIpAddress(final String input) {
        return Util.VERIFY_AS_IP_ADDRESS.matcher(input).matches();
    }
}
