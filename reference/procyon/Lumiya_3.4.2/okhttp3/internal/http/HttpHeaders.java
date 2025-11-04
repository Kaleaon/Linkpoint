// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import okhttp3.internal.Util;
import okhttp3.Request;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.CookieJar;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.ArrayList;
import okhttp3.Challenge;
import java.util.List;
import okhttp3.Response;
import okhttp3.Headers;
import java.util.regex.Pattern;

public final class HttpHeaders
{
    private static final Pattern PARAMETER;
    private static final String QUOTED_STRING = "\"([^\"]*)\"";
    private static final String TOKEN = "([^ \"=]*)";
    
    static {
        PARAMETER = Pattern.compile(" +([^ \"=]*)=(:?\"([^\"]*)\"|([^ \"=]*)) *(:?,|$)");
    }
    
    private HttpHeaders() {
    }
    
    public static long contentLength(final Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }
    
    public static long contentLength(final Response response) {
        return contentLength(response.headers());
    }
    
    public static boolean hasBody(final Response response) {
        if (!response.request().method().equals("HEAD")) {
            final int code = response.code();
            if (code < 100 || code >= 200) {
                if (code != 204 && code != 304) {
                    return true;
                }
            }
            return contentLength(response) != -1L || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"));
        }
        return false;
    }
    
    public static boolean hasVaryAll(final Headers headers) {
        return varyFields(headers).contains("*");
    }
    
    public static boolean hasVaryAll(final Response response) {
        return hasVaryAll(response.headers());
    }
    
    public static List<Challenge> parseChallenges(final Headers headers, String input) {
        final ArrayList list = new ArrayList();
        final Iterator<String> iterator = headers.values(input).iterator();
        while (iterator.hasNext()) {
            input = iterator.next();
            final int index = input.indexOf(32);
            if (index != -1) {
                final Matcher matcher = HttpHeaders.PARAMETER.matcher(input);
                for (int end = index; matcher.find(end); end = matcher.end()) {
                    if (input.regionMatches(true, matcher.start(1), "realm", 0, 5)) {
                        final String substring = input.substring(0, index);
                        final String group = matcher.group(3);
                        if (group != null) {
                            list.add(new Challenge(substring, group));
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public static int parseSeconds(final String s, int n) {
        final int n2 = 1;
        try {
            final long long1 = Long.parseLong(s);
            if (long1 <= 2147483647L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                return Integer.MAX_VALUE;
            }
            if (long1 >= 0L) {
                n = n2;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                return 0;
            }
            return (int)long1;
        }
        catch (final NumberFormatException ex) {
            return n;
        }
    }
    
    public static void receiveHeaders(final CookieJar cookieJar, final HttpUrl httpUrl, final Headers headers) {
        if (cookieJar == CookieJar.NO_COOKIES) {
            return;
        }
        final List<Cookie> all = Cookie.parseAll(httpUrl, headers);
        if (!all.isEmpty()) {
            cookieJar.saveFromResponse(httpUrl, all);
        }
    }
    
    public static int skipUntil(final String s, int index, final String s2) {
        while (index < s.length() && s2.indexOf(s.charAt(index)) == -1) {
            ++index;
        }
        return index;
    }
    
    public static int skipWhitespace(final String s, int i) {
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 != ' ' && char1 != '\t') {
                break;
            }
            ++i;
        }
        return i;
    }
    
    private static long stringToLong(final String s) {
        if (s == null) {
            return -1L;
        }
        try {
            return Long.parseLong(s);
        }
        catch (final NumberFormatException ex) {
            return -1L;
        }
    }
    
    public static Set<String> varyFields(final Headers headers) {
        Set<Object> emptySet = Collections.emptySet();
        Object o;
        for (int size = headers.size(), i = 0; i < size; ++i, emptySet = (Set<Object>)o) {
            o = emptySet;
            if ("Vary".equalsIgnoreCase(headers.name(i))) {
                final String value = headers.value(i);
                if (emptySet.isEmpty()) {
                    emptySet = new TreeSet<Object>((Comparator<? super Object>)String.CASE_INSENSITIVE_ORDER);
                }
                final String[] split = value.split(",");
                for (int length = split.length, j = 0; j < length; ++j) {
                    emptySet.add(split[j].trim());
                }
                o = emptySet;
            }
        }
        return (Set<String>)emptySet;
    }
    
    private static Set<String> varyFields(final Response response) {
        return varyFields(response.headers());
    }
    
    public static Headers varyHeaders(final Headers headers, final Headers headers2) {
        int i = 0;
        final Set<String> varyFields = varyFields(headers2);
        if (!varyFields.isEmpty()) {
            final Headers.Builder builder = new Headers.Builder();
            while (i < headers.size()) {
                final String name = headers.name(i);
                if (varyFields.contains(name)) {
                    builder.add(name, headers.value(i));
                }
                ++i;
            }
            return builder.build();
        }
        return new Headers.Builder().build();
    }
    
    public static Headers varyHeaders(final Response response) {
        return varyHeaders(response.networkResponse().request().headers(), response.headers());
    }
    
    public static boolean varyMatches(final Response response, final Headers headers, final Request request) {
        for (final String s : varyFields(response)) {
            if (!Util.equal(headers.values(s), request.headers(s))) {
                return false;
            }
        }
        return true;
    }
}
