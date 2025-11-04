// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.Arrays;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collection;
import okhttp3.internal.Util;
import java.util.Collections;
import java.util.ArrayList;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import okio.Buffer;
import java.util.List;

public final class HttpUrl
{
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    private static final char[] HEX_DIGITS;
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_COMPONENT_ENCODE_SET = " \"'<>#&=";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    private final String fragment;
    final String host;
    private final String password;
    private final List<String> pathSegments;
    final int port;
    private final List<String> queryNamesAndValues;
    final String scheme;
    private final String url;
    private final String username;
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    HttpUrl(final Builder builder) {
        final String s = null;
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = this.percentDecode(builder.encodedPathSegments, false);
        List<String> percentDecode;
        if (builder.encodedQueryNamesAndValues == null) {
            percentDecode = null;
        }
        else {
            percentDecode = this.percentDecode(builder.encodedQueryNamesAndValues, true);
        }
        this.queryNamesAndValues = percentDecode;
        String percentDecode2;
        if (builder.encodedFragment == null) {
            percentDecode2 = s;
        }
        else {
            percentDecode2 = percentDecode(builder.encodedFragment, false);
        }
        this.fragment = percentDecode2;
        this.url = builder.toString();
    }
    
    static String canonicalize(final String s, final int beginIndex, final int endIndex, final String s2, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        int i = beginIndex;
        while (i < endIndex) {
            final int codePoint = s.codePointAt(i);
            Label_0031: {
                if (codePoint >= 32 && codePoint != 127) {
                    if (codePoint < 128 || !b4) {
                        if (s2.indexOf(codePoint) == -1) {
                            if (codePoint == 37) {
                                if (!b) {
                                    break Label_0031;
                                }
                                if (b2 && !percentEncoded(s, i, endIndex)) {
                                    break Label_0031;
                                }
                            }
                            if (codePoint != 43 || !b3) {
                                i += Character.charCount(codePoint);
                                continue;
                            }
                        }
                    }
                }
            }
            final Buffer buffer = new Buffer();
            buffer.writeUtf8(s, beginIndex, i);
            canonicalize(buffer, s, i, endIndex, s2, b, b2, b3, b4);
            return buffer.readUtf8();
        }
        return s.substring(beginIndex, endIndex);
    }
    
    static String canonicalize(final String s, final String s2, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        return canonicalize(s, 0, s.length(), s2, b, b2, b3, b4);
    }
    
    static void canonicalize(final Buffer buffer, final String s, int i, final int n, final String s2, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        Buffer buffer2 = null;
        while (i < n) {
            final int codePoint = s.codePointAt(i);
            Buffer buffer3 = null;
            Label_0060: {
                if (b) {
                    buffer3 = buffer2;
                    if (codePoint == 9) {
                        break Label_0060;
                    }
                    buffer3 = buffer2;
                    if (codePoint == 10) {
                        break Label_0060;
                    }
                    buffer3 = buffer2;
                    if (codePoint == 12) {
                        break Label_0060;
                    }
                    buffer3 = buffer2;
                    if (codePoint == 13) {
                        break Label_0060;
                    }
                }
                if (codePoint == 43 && b3) {
                    String s3;
                    if (!b) {
                        s3 = "%2B";
                    }
                    else {
                        s3 = "+";
                    }
                    buffer.writeUtf8(s3);
                    buffer3 = buffer2;
                }
                else {
                    Label_0035: {
                        if (codePoint >= 32 && codePoint != 127) {
                            if (codePoint < 128 || !b4) {
                                if (s2.indexOf(codePoint) == -1) {
                                    if (codePoint == 37) {
                                        if (!b) {
                                            break Label_0035;
                                        }
                                        if (b2) {
                                            if (!percentEncoded(s, i, n)) {
                                                break Label_0035;
                                            }
                                        }
                                    }
                                    buffer.writeUtf8CodePoint(codePoint);
                                    buffer3 = buffer2;
                                    break Label_0060;
                                }
                            }
                        }
                    }
                    if (buffer2 == null) {
                        buffer2 = new Buffer();
                    }
                    buffer2.writeUtf8CodePoint(codePoint);
                    while (!buffer2.exhausted()) {
                        final int n2 = buffer2.readByte() & 0xFF;
                        buffer.writeByte(37);
                        buffer.writeByte((int)HttpUrl.HEX_DIGITS[n2 >> 4 & 0xF]);
                        buffer.writeByte((int)HttpUrl.HEX_DIGITS[n2 & 0xF]);
                    }
                    buffer3 = buffer2;
                }
            }
            i += Character.charCount(codePoint);
            buffer2 = buffer3;
        }
    }
    
    static int decodeHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }
    
    public static int defaultPort(final String s) {
        if (s.equals("http")) {
            return 80;
        }
        if (!s.equals("https")) {
            return -1;
        }
        return 443;
    }
    
    public static HttpUrl get(final URI uri) {
        return parse(uri.toString());
    }
    
    public static HttpUrl get(final URL url) {
        return parse(url.toString());
    }
    
    static HttpUrl getChecked(final String s) throws MalformedURLException, UnknownHostException {
        final Builder builder = new Builder();
        final ParseResult parse = builder.parse(null, s);
        switch (parse) {
            default: {
                throw new MalformedURLException("Invalid URL: " + parse + " for " + s);
            }
            case SUCCESS: {
                return builder.build();
            }
            case INVALID_HOST: {
                throw new UnknownHostException("Invalid host: " + s);
            }
        }
    }
    
    static void namesAndValuesToQueryString(final StringBuilder sb, final List<String> list) {
        for (int size = list.size(), i = 0; i < size; i += 2) {
            final String str = list.get(i);
            final String str2 = list.get(i + 1);
            if (i > 0) {
                sb.append('&');
            }
            sb.append(str);
            if (str2 != null) {
                sb.append('=');
                sb.append(str2);
            }
        }
    }
    
    public static HttpUrl parse(final String s) {
        final HttpUrl httpUrl = null;
        final Builder builder = new Builder();
        HttpUrl build;
        if (builder.parse(null, s) != ParseResult.SUCCESS) {
            build = httpUrl;
        }
        else {
            build = builder.build();
        }
        return build;
    }
    
    static void pathSegmentsToString(final StringBuilder sb, final List<String> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            sb.append('/');
            sb.append((String)list.get(i));
        }
    }
    
    static String percentDecode(final String s, final int beginIndex, final int endIndex, final boolean b) {
        for (int i = beginIndex; i < endIndex; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '%' || (char1 == '+' && b)) {
                final Buffer buffer = new Buffer();
                buffer.writeUtf8(s, beginIndex, i);
                percentDecode(buffer, s, i, endIndex, b);
                return buffer.readUtf8();
            }
        }
        return s.substring(beginIndex, endIndex);
    }
    
    static String percentDecode(final String s, final boolean b) {
        return percentDecode(s, 0, s.length(), b);
    }
    
    private List<String> percentDecode(final List<String> list, final boolean b) {
        final int size = list.size();
        final ArrayList list2 = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            final String s = list.get(i);
            Object percentDecode;
            if (s == null) {
                percentDecode = null;
            }
            else {
                percentDecode = percentDecode(s, b);
            }
            list2.add(percentDecode);
        }
        return Collections.unmodifiableList((List<? extends String>)list2);
    }
    
    static void percentDecode(final Buffer buffer, final String s, int i, final int n, final boolean b) {
        while (i < n) {
            final int codePoint = s.codePointAt(i);
            Label_0034: {
                if (codePoint == 37 && i + 2 < n) {
                    final int decodeHexDigit = decodeHexDigit(s.charAt(i + 1));
                    final int decodeHexDigit2 = decodeHexDigit(s.charAt(i + 2));
                    if (decodeHexDigit != -1 && decodeHexDigit2 != -1) {
                        buffer.writeByte((decodeHexDigit << 4) + decodeHexDigit2);
                        i += 2;
                        break Label_0034;
                    }
                }
                else if (codePoint == 43 && b) {
                    buffer.writeByte(32);
                    break Label_0034;
                }
                buffer.writeUtf8CodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
    }
    
    static boolean percentEncoded(final String s, final int index, final int n) {
        return index + 2 < n && s.charAt(index) == '%' && decodeHexDigit(s.charAt(index + 1)) != -1 && decodeHexDigit(s.charAt(index + 2)) != -1;
    }
    
    static List<String> queryStringToNamesAndValues(final String s) {
        final ArrayList list = new ArrayList();
        int n;
        for (int i = 0; i <= s.length(); i = n + 1) {
            n = s.indexOf(38, i);
            if (n == -1) {
                n = s.length();
            }
            final int index = s.indexOf(61, i);
            if (index != -1 && index <= n) {
                list.add(s.substring(i, index));
                list.add(s.substring(index + 1, n));
            }
            else {
                list.add(s.substring(i, n));
                list.add(null);
            }
        }
        return list;
    }
    
    public String encodedFragment() {
        if (this.fragment != null) {
            return this.url.substring(this.url.indexOf(35) + 1);
        }
        return null;
    }
    
    public String encodedPassword() {
        if (!this.password.isEmpty()) {
            return this.url.substring(this.url.indexOf(58, this.scheme.length() + 3) + 1, this.url.indexOf(64));
        }
        return "";
    }
    
    public String encodedPath() {
        final int index = this.url.indexOf(47, this.scheme.length() + 3);
        return this.url.substring(index, Util.delimiterOffset(this.url, index, this.url.length(), "?#"));
    }
    
    public List<String> encodedPathSegments() {
        int i = this.url.indexOf(47, this.scheme.length() + 3);
        final int delimiterOffset = Util.delimiterOffset(this.url, i, this.url.length(), "?#");
        final ArrayList list = new ArrayList();
        while (i < delimiterOffset) {
            final int beginIndex = i + 1;
            i = Util.delimiterOffset(this.url, beginIndex, delimiterOffset, '/');
            list.add(this.url.substring(beginIndex, i));
        }
        return list;
    }
    
    public String encodedQuery() {
        if (this.queryNamesAndValues != null) {
            final int beginIndex = this.url.indexOf(63) + 1;
            return this.url.substring(beginIndex, Util.delimiterOffset(this.url, beginIndex + 1, this.url.length(), '#'));
        }
        return null;
    }
    
    public String encodedUsername() {
        if (!this.username.isEmpty()) {
            final int beginIndex = this.scheme.length() + 3;
            return this.url.substring(beginIndex, Util.delimiterOffset(this.url, beginIndex, this.url.length(), ":@"));
        }
        return "";
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof HttpUrl && ((HttpUrl)o).url.equals(this.url)) {
            b = true;
        }
        return b;
    }
    
    public String fragment() {
        return this.fragment;
    }
    
    @Override
    public int hashCode() {
        return this.url.hashCode();
    }
    
    public String host() {
        return this.host;
    }
    
    public boolean isHttps() {
        return this.scheme.equals("https");
    }
    
    public Builder newBuilder() {
        final Builder builder = new Builder();
        builder.scheme = this.scheme;
        builder.encodedUsername = this.encodedUsername();
        builder.encodedPassword = this.encodedPassword();
        builder.host = this.host;
        int port;
        if (this.port == defaultPort(this.scheme)) {
            port = -1;
        }
        else {
            port = this.port;
        }
        builder.port = port;
        builder.encodedPathSegments.clear();
        builder.encodedPathSegments.addAll(this.encodedPathSegments());
        builder.encodedQuery(this.encodedQuery());
        builder.encodedFragment = this.encodedFragment();
        return builder;
    }
    
    public Builder newBuilder(final String s) {
        Builder builder;
        if ((builder = new Builder()).parse(this, s) != ParseResult.SUCCESS) {
            builder = null;
        }
        return builder;
    }
    
    public String password() {
        return this.password;
    }
    
    public List<String> pathSegments() {
        return this.pathSegments;
    }
    
    public int pathSize() {
        return this.pathSegments.size();
    }
    
    public int port() {
        return this.port;
    }
    
    public String query() {
        if (this.queryNamesAndValues != null) {
            final StringBuilder sb = new StringBuilder();
            namesAndValuesToQueryString(sb, this.queryNamesAndValues);
            return sb.toString();
        }
        return null;
    }
    
    public String queryParameter(final String s) {
        int i = 0;
        if (this.queryNamesAndValues != null) {
            while (i < this.queryNamesAndValues.size()) {
                if (s.equals(this.queryNamesAndValues.get(i))) {
                    return this.queryNamesAndValues.get(i + 1);
                }
                i += 2;
            }
            return null;
        }
        return null;
    }
    
    public String queryParameterName(final int n) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get(n * 2);
        }
        throw new IndexOutOfBoundsException();
    }
    
    public Set<String> queryParameterNames() {
        if (this.queryNamesAndValues != null) {
            final LinkedHashSet s = new LinkedHashSet();
            for (int i = 0; i < this.queryNamesAndValues.size(); i += 2) {
                s.add(this.queryNamesAndValues.get(i));
            }
            return (Set<String>)Collections.unmodifiableSet((Set<?>)s);
        }
        return Collections.emptySet();
    }
    
    public String queryParameterValue(final int n) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get(n * 2 + 1);
        }
        throw new IndexOutOfBoundsException();
    }
    
    public List<String> queryParameterValues(final String s) {
        int i = 0;
        if (this.queryNamesAndValues != null) {
            final ArrayList list = new ArrayList();
            while (i < this.queryNamesAndValues.size()) {
                if (s.equals(this.queryNamesAndValues.get(i))) {
                    list.add(this.queryNamesAndValues.get(i + 1));
                }
                i += 2;
            }
            return (List<String>)Collections.unmodifiableList((List<?>)list);
        }
        return Collections.emptyList();
    }
    
    public int querySize() {
        int n;
        if (this.queryNamesAndValues == null) {
            n = 0;
        }
        else {
            n = this.queryNamesAndValues.size() / 2;
        }
        return n;
    }
    
    public String redact() {
        return this.newBuilder("/...").username("").password("").build().toString();
    }
    
    public HttpUrl resolve(final String s) {
        final HttpUrl httpUrl = null;
        final Builder builder = this.newBuilder(s);
        HttpUrl build;
        if (builder == null) {
            build = httpUrl;
        }
        else {
            build = builder.build();
        }
        return build;
    }
    
    public String scheme() {
        return this.scheme;
    }
    
    @Override
    public String toString() {
        return this.url;
    }
    
    public URI uri() {
        final String string = this.newBuilder().reencodeForUri().toString();
        try {
            return new URI(string);
        }
        catch (final URISyntaxException cause) {
            try {
                return URI.create(string.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", ""));
            }
            catch (final Exception ex) {
                throw new RuntimeException(cause);
            }
        }
    }
    
    public URL url() {
        try {
            return new URL(this.url);
        }
        catch (final MalformedURLException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    public String username() {
        return this.username;
    }
    
    public static final class Builder
    {
        String encodedFragment;
        String encodedPassword;
        final List<String> encodedPathSegments;
        List<String> encodedQueryNamesAndValues;
        String encodedUsername;
        String host;
        int port;
        String scheme;
        
        public Builder() {
            this.encodedUsername = "";
            this.encodedPassword = "";
            this.port = -1;
            (this.encodedPathSegments = new ArrayList<String>()).add("");
        }
        
        private Builder addPathSegments(final String s, final boolean b) {
            int n = 0;
            int i;
            do {
                i = Util.delimiterOffset(s, n, s.length(), "/\\");
                this.push(s, n, i, i < s.length(), b);
                n = ++i;
            } while (i <= s.length());
            return this;
        }
        
        private static String canonicalizeHost(String percentDecode, final int n, final int n2) {
            percentDecode = HttpUrl.percentDecode(percentDecode, n, n2, false);
            if (!percentDecode.contains(":")) {
                return Util.domainToAscii(percentDecode);
            }
            InetAddress inetAddress;
            if (percentDecode.startsWith("[") && percentDecode.endsWith("]")) {
                inetAddress = decodeIpv6(percentDecode, 1, percentDecode.length() - 1);
            }
            else {
                inetAddress = decodeIpv6(percentDecode, 0, percentDecode.length());
            }
            if (inetAddress == null) {
                return null;
            }
            final byte[] address = inetAddress.getAddress();
            if (address.length != 16) {
                throw new AssertionError();
            }
            return inet6AddressToAscii(address);
        }
        
        private static boolean decodeIpv4Suffix(final String s, int i, final int n, final byte[] array, final int n2) {
            int n3 = n2;
            while (i < n) {
                if (n3 == array.length) {
                    return false;
                }
                if (n3 != n2) {
                    if (s.charAt(i) != '.') {
                        return false;
                    }
                    ++i;
                }
                int n4 = 0;
                int j;
                for (j = i; j < n; ++j) {
                    final char char1 = s.charAt(j);
                    if (char1 < '0' || char1 > '9') {
                        break;
                    }
                    if (n4 == 0 && i != j) {
                        return false;
                    }
                    n4 = n4 * 10 + char1 - 48;
                    if (n4 > 255) {
                        return false;
                    }
                }
                if (j - i == 0) {
                    return false;
                }
                array[n3] = (byte)n4;
                ++n3;
                i = j;
            }
            return n3 == n2 + 4;
        }
        
        private static InetAddress decodeIpv6(final String s, int toffset, final int n) {
            final byte[] addr = new byte[16];
            int n2 = -1;
            int fromIndex = -1;
            int n3 = 0;
        Label_0019_Outer:
            while (true) {
                Label_0033: {
                    if (toffset < n) {
                        break Label_0033;
                    }
                    int n4;
                    int n5;
                    int n6;
                    int n7;
                    int index;
                    int decodeHexDigit;
                    int n8;
                    Block_9_Outer:Block_3_Outer:
                    while (true) {
                        Label_0289: {
                            if (n3 != addr.length) {
                                break Label_0289;
                            }
                            try {
                                return InetAddress.getByAddress(addr);
                                Label_0125: {
                                    toffset = n3;
                                }
                                fromIndex = n3;
                                n3 = toffset;
                                continue Block_9_Outer;
                                while (true) {
                                    Block_4_Outer:Block_11_Outer:
                                    while (true) {
                                        n3 += 2;
                                        continue Block_9_Outer;
                                    Label_0062_Outer:
                                        while (true) {
                                        Label_0068:
                                            while (true) {
                                                Label_0056: {
                                                    while (true) {
                                                    Block_10_Outer:
                                                        while (true) {
                                                            while (true) {
                                                            Block_6_Outer:
                                                                while (true) {
                                                                    iftrue(Label_0123:)(fromIndex != -1);
                                                                    while (true) {
                                                                        Block_5: {
                                                                            break Block_5;
                                                                            n4 = n3;
                                                                            fromIndex = n3;
                                                                            break Label_0056;
                                                                            n5 = n4 + 1;
                                                                            addr[n4] = (byte)(n6 >>> 8 & 0xFF);
                                                                            n7 = n5 + 1;
                                                                            addr[n5] = (byte)(n6 & 0xFF);
                                                                            n2 = toffset;
                                                                            toffset = index;
                                                                            n3 = n7;
                                                                            continue Label_0019_Outer;
                                                                            iftrue(Label_0174:)(s.regionMatches(toffset, ".", 0, 1));
                                                                            return null;
                                                                            Label_0079:
                                                                            return null;
                                                                            n6 = (n6 << 4) + decodeHexDigit;
                                                                            ++index;
                                                                            iftrue(Label_0197:)(index < n);
                                                                            break Label_0068;
                                                                        }
                                                                        toffset += 2;
                                                                        n3 += 2;
                                                                        iftrue(Label_0125:)(toffset == n);
                                                                        continue Block_11_Outer;
                                                                    }
                                                                    iftrue(Label_0047:)(toffset + 2 > n || !s.regionMatches(toffset, "::", 0, 2));
                                                                    continue Block_6_Outer;
                                                                }
                                                                n4 = n3;
                                                                break Label_0056;
                                                                Label_0197:
                                                                decodeHexDigit = HttpUrl.decodeHexDigit(s.charAt(index));
                                                                iftrue(Label_0068:)(decodeHexDigit == -1);
                                                                continue Label_0062_Outer;
                                                            }
                                                            iftrue(Label_0333:)(fromIndex == -1);
                                                            System.arraycopy(addr, fromIndex, addr, addr.length - (n3 - fromIndex), n3 - fromIndex);
                                                            Arrays.fill(addr, fromIndex, addr.length - n3 + fromIndex, (byte)0);
                                                            return InetAddress.getByAddress(addr);
                                                            Label_0333:
                                                            return null;
                                                            Label_0138:
                                                            iftrue(Label_0164:)(s.regionMatches(toffset, ":", 0, 1));
                                                            continue Block_10_Outer;
                                                        }
                                                        Label_0164:
                                                        ++toffset;
                                                        n4 = n3;
                                                        break Label_0056;
                                                        iftrue(Label_0081:)(n3 == addr.length);
                                                        continue Block_3_Outer;
                                                    }
                                                }
                                                n6 = 0;
                                                index = toffset;
                                                continue Block_3_Outer;
                                            }
                                            n8 = index - toffset;
                                            iftrue(Label_0079:)(n8 == 0 || n8 > 4);
                                            continue Label_0062_Outer;
                                        }
                                        Label_0174:
                                        iftrue(Label_0195:)(!decodeIpv4Suffix(s, n2, n, addr, n3 - 2));
                                        continue Block_4_Outer;
                                    }
                                    Label_0081:
                                    return null;
                                    Label_0123:
                                    return null;
                                    Label_0047:
                                    iftrue(Label_0138:)(n3 != 0);
                                    continue;
                                }
                                Label_0195:
                                return null;
                            }
                            catch (final UnknownHostException ex) {
                                throw new AssertionError();
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        private static String inet6AddressToAscii(final byte[] array) {
            final int n = 0;
            int n2 = 0;
            int n3 = -1;
            int n4;
            for (int i = 0; i < array.length; i = n4 + 2) {
                for (n4 = i; n4 < 16 && array[n4] == 0 && array[n4 + 1] == 0; n4 += 2) {}
                final int n5 = n4 - i;
                if (n5 > n2) {
                    n2 = n5;
                    n3 = i;
                }
            }
            final Buffer buffer = new Buffer();
            int j = n;
            while (j < array.length) {
                if (j != n3) {
                    if (j > 0) {
                        buffer.writeByte(58);
                    }
                    buffer.writeHexadecimalUnsignedLong((long)((array[j] & 0xFF) << 8 | (array[j + 1] & 0xFF)));
                    j += 2;
                }
                else {
                    buffer.writeByte(58);
                    final int n6 = j + n2;
                    if ((j = n6) != 16) {
                        continue;
                    }
                    buffer.writeByte(58);
                    j = n6;
                }
            }
            return buffer.readUtf8();
        }
        
        private boolean isDot(final String s) {
            boolean b = false;
            if (s.equals(".") || s.equalsIgnoreCase("%2e")) {
                b = true;
            }
            return b;
        }
        
        private boolean isDotDot(final String s) {
            boolean b = false;
            if (s.equals("..") || s.equalsIgnoreCase("%2e.") || s.equalsIgnoreCase(".%2e") || s.equalsIgnoreCase("%2e%2e")) {
                b = true;
            }
            return b;
        }
        
        private static int parsePort(final String s, int int1, final int n) {
            try {
                int1 = Integer.parseInt(HttpUrl.canonicalize(s, int1, n, "", false, false, false, true));
                if (int1 > 0 && int1 <= 65535) {
                    return int1;
                }
                return -1;
            }
            catch (final NumberFormatException ex) {
                return -1;
            }
        }
        
        private void pop() {
            if (this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1).isEmpty() && !this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            }
            else {
                this.encodedPathSegments.add("");
            }
        }
        
        private static int portColonOffset(final String s, int i, final int n) {
            while (i < n) {
                int index = i;
                Label_0040: {
                    switch (s.charAt(i)) {
                        case '[': {
                            while (++index < n) {
                                i = index;
                                if (s.charAt(index) != ']') {
                                    continue;
                                }
                                break Label_0040;
                            }
                            i = index;
                            break;
                        }
                        case ':': {
                            return i;
                        }
                    }
                }
                ++i;
            }
            return n;
        }
        
        private void push(String canonicalize, final int n, final int n2, final boolean b, final boolean b2) {
            canonicalize = HttpUrl.canonicalize(canonicalize, n, n2, " \"<>^`{}|/\\?#", b2, false, false, true);
            if (this.isDot(canonicalize)) {
                return;
            }
            if (!this.isDotDot(canonicalize)) {
                if (!this.encodedPathSegments.get(this.encodedPathSegments.size() - 1).isEmpty()) {
                    this.encodedPathSegments.add(canonicalize);
                }
                else {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, canonicalize);
                }
                if (b) {
                    this.encodedPathSegments.add("");
                }
                return;
            }
            this.pop();
        }
        
        private void removeAllCanonicalQueryParameters(final String s) {
            int size = this.encodedQueryNamesAndValues.size();
            while (true) {
                final int n = size - 2;
                if (n < 0) {
                    return;
                }
                size = n;
                if (!s.equals(this.encodedQueryNamesAndValues.get(n))) {
                    continue;
                }
                this.encodedQueryNamesAndValues.remove(n + 1);
                this.encodedQueryNamesAndValues.remove(n);
                size = n;
                if (this.encodedQueryNamesAndValues.isEmpty()) {
                    this.encodedQueryNamesAndValues = null;
                }
            }
        }
        
        private void resolvePath(final String s, int i, final int n) {
            if (i != n) {
                final char char1 = s.charAt(i);
                if (char1 != '/' && char1 != '\\') {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
                }
                else {
                    this.encodedPathSegments.clear();
                    this.encodedPathSegments.add("");
                    ++i;
                }
                while (i < n) {
                    final int delimiterOffset = Util.delimiterOffset(s, i, n, "/\\");
                    final boolean b = delimiterOffset < n;
                    this.push(s, i, delimiterOffset, b, true);
                    if (!b) {
                        i = delimiterOffset;
                    }
                    else {
                        i = delimiterOffset + 1;
                    }
                }
            }
        }
        
        private static int schemeDelimiterOffset(final String s, int i, final int n) {
            if (n - i < 2) {
                return -1;
            }
            final char char1 = s.charAt(i);
            if ((char1 < 'a' || char1 > 'z') && (char1 < 'A' || char1 > 'Z')) {
                return -1;
            }
            ++i;
            while (i < n) {
                final char char2 = s.charAt(i);
                if (char2 < 'a' || char2 > 'z') {
                    if (char2 < 'A' || char2 > 'Z') {
                        if (char2 < '0' || char2 > '9') {
                            if (char2 != '+' && char2 != '-' && char2 != '.') {
                                if (char2 != ':') {
                                    return -1;
                                }
                                return i;
                            }
                        }
                    }
                }
                ++i;
            }
            return -1;
        }
        
        private static int slashCount(final String s, int i, final int n) {
            int n2 = 0;
            while (i < n) {
                final char char1 = s.charAt(i);
                if (char1 != '\\' && char1 != '/') {
                    break;
                }
                ++n2;
                ++i;
            }
            return n2;
        }
        
        public Builder addEncodedPathSegment(final String s) {
            if (s != null) {
                this.push(s, 0, s.length(), false, true);
                return this;
            }
            throw new NullPointerException("encodedPathSegment == null");
        }
        
        public Builder addEncodedPathSegments(final String s) {
            if (s != null) {
                return this.addPathSegments(s, true);
            }
            throw new NullPointerException("encodedPathSegments == null");
        }
        
        public Builder addEncodedQueryParameter(String canonicalize, final String s) {
            if (canonicalize != null) {
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList<String>();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(canonicalize, " \"'<>#&=", true, false, true, true));
                final List<String> encodedQueryNamesAndValues = this.encodedQueryNamesAndValues;
                if (s == null) {
                    canonicalize = null;
                }
                else {
                    canonicalize = HttpUrl.canonicalize(s, " \"'<>#&=", true, false, true, true);
                }
                encodedQueryNamesAndValues.add(canonicalize);
                return this;
            }
            throw new NullPointerException("encodedName == null");
        }
        
        public Builder addPathSegment(final String s) {
            if (s != null) {
                this.push(s, 0, s.length(), false, false);
                return this;
            }
            throw new NullPointerException("pathSegment == null");
        }
        
        public Builder addPathSegments(final String s) {
            if (s != null) {
                return this.addPathSegments(s, false);
            }
            throw new NullPointerException("pathSegments == null");
        }
        
        public Builder addQueryParameter(String canonicalize, final String s) {
            if (canonicalize != null) {
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList<String>();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(canonicalize, " \"'<>#&=", false, false, true, true));
                final List<String> encodedQueryNamesAndValues = this.encodedQueryNamesAndValues;
                if (s == null) {
                    canonicalize = null;
                }
                else {
                    canonicalize = HttpUrl.canonicalize(s, " \"'<>#&=", false, false, true, true);
                }
                encodedQueryNamesAndValues.add(canonicalize);
                return this;
            }
            throw new NullPointerException("name == null");
        }
        
        public HttpUrl build() {
            if (this.scheme == null) {
                throw new IllegalStateException("scheme == null");
            }
            if (this.host != null) {
                return new HttpUrl(this);
            }
            throw new IllegalStateException("host == null");
        }
        
        int effectivePort() {
            int n;
            if (this.port == -1) {
                n = HttpUrl.defaultPort(this.scheme);
            }
            else {
                n = this.port;
            }
            return n;
        }
        
        public Builder encodedFragment(String canonicalize) {
            final String s = null;
            if (canonicalize == null) {
                canonicalize = s;
            }
            else {
                canonicalize = HttpUrl.canonicalize(canonicalize, "", true, false, false, false);
            }
            this.encodedFragment = canonicalize;
            return this;
        }
        
        public Builder encodedPassword(final String s) {
            if (s != null) {
                this.encodedPassword = HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedPassword == null");
        }
        
        public Builder encodedPath(final String str) {
            if (str == null) {
                throw new NullPointerException("encodedPath == null");
            }
            if (str.startsWith("/")) {
                this.resolvePath(str, 0, str.length());
                return this;
            }
            throw new IllegalArgumentException("unexpected encodedPath: " + str);
        }
        
        public Builder encodedQuery(final String s) {
            final List<String> list = null;
            List<String> queryStringToNamesAndValues;
            if (s == null) {
                queryStringToNamesAndValues = list;
            }
            else {
                queryStringToNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(s, " \"'<>#", true, false, true, true));
            }
            this.encodedQueryNamesAndValues = queryStringToNamesAndValues;
            return this;
        }
        
        public Builder encodedUsername(final String s) {
            if (s != null) {
                this.encodedUsername = HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedUsername == null");
        }
        
        public Builder fragment(String canonicalize) {
            final String s = null;
            if (canonicalize == null) {
                canonicalize = s;
            }
            else {
                canonicalize = HttpUrl.canonicalize(canonicalize, "", false, false, false, false);
            }
            this.encodedFragment = canonicalize;
            return this;
        }
        
        public Builder host(final String str) {
            if (str == null) {
                throw new NullPointerException("host == null");
            }
            final String canonicalizeHost = canonicalizeHost(str, 0, str.length());
            if (canonicalizeHost != null) {
                this.host = canonicalizeHost;
                return this;
            }
            throw new IllegalArgumentException("unexpected host: " + str);
        }
        
        ParseResult parse(final HttpUrl httpUrl, final String s) {
            int skipLeadingAsciiWhitespace = Util.skipLeadingAsciiWhitespace(s, 0, s.length());
            final int skipTrailingAsciiWhitespace = Util.skipTrailingAsciiWhitespace(s, skipLeadingAsciiWhitespace, s.length());
            if (schemeDelimiterOffset(s, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace) == -1) {
                if (httpUrl == null) {
                    return ParseResult.MISSING_SCHEME;
                }
                this.scheme = httpUrl.scheme;
            }
            else if (!s.regionMatches(true, skipLeadingAsciiWhitespace, "https:", 0, 6)) {
                if (!s.regionMatches(true, skipLeadingAsciiWhitespace, "http:", 0, 5)) {
                    return ParseResult.UNSUPPORTED_SCHEME;
                }
                this.scheme = "http";
                skipLeadingAsciiWhitespace += "http:".length();
            }
            else {
                this.scheme = "https";
                skipLeadingAsciiWhitespace += "https:".length();
            }
            final int slashCount = slashCount(s, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace);
            if (slashCount < 2 && httpUrl != null && httpUrl.scheme.equals(this.scheme)) {
                this.encodedUsername = httpUrl.encodedUsername();
                this.encodedPassword = httpUrl.encodedPassword();
                this.host = httpUrl.host;
                this.port = httpUrl.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(httpUrl.encodedPathSegments());
                if (skipLeadingAsciiWhitespace == skipTrailingAsciiWhitespace || s.charAt(skipLeadingAsciiWhitespace) == '#') {
                    this.encodedQuery(httpUrl.encodedQuery());
                }
            }
            else {
                final int n = 0;
                int n2 = 0;
                final int n3 = skipLeadingAsciiWhitespace + slashCount;
                int n4 = n;
                int n5 = n3;
                int delimiterOffset = 0;
            Label_0572:
                while (true) {
                    delimiterOffset = Util.delimiterOffset(s, n5, skipTrailingAsciiWhitespace, "@/\\?#");
                    int char1;
                    if (delimiterOffset == skipTrailingAsciiWhitespace) {
                        char1 = -1;
                    }
                    else {
                        char1 = s.charAt(delimiterOffset);
                    }
                    int n7 = 0;
                    int n8 = 0;
                    switch (char1) {
                        default: {
                            final int n6 = n4;
                            n7 = n5;
                            n8 = n6;
                            break;
                        }
                        case 64: {
                            if (n4 != 0) {
                                this.encodedPassword = this.encodedPassword + "%40" + HttpUrl.canonicalize(s, n5, delimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                            }
                            else {
                                final int delimiterOffset2 = Util.delimiterOffset(s, n5, delimiterOffset, ':');
                                String s2 = HttpUrl.canonicalize(s, n5, delimiterOffset2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                                if (n2 != 0) {
                                    s2 = this.encodedUsername + "%40" + s2;
                                }
                                this.encodedUsername = s2;
                                if (delimiterOffset2 != delimiterOffset) {
                                    n4 = 1;
                                    this.encodedPassword = HttpUrl.canonicalize(s, delimiterOffset2 + 1, delimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                                }
                                n2 = 1;
                            }
                            final int n9 = delimiterOffset + 1;
                            n8 = n4;
                            n7 = n9;
                            break;
                        }
                        case -1:
                        case 35:
                        case 47:
                        case 63:
                        case 92: {
                            break Label_0572;
                        }
                    }
                    final int n10 = n8;
                    n5 = n7;
                    n4 = n10;
                }
                final int portColonOffset = portColonOffset(s, n5, delimiterOffset);
                if (portColonOffset + 1 >= delimiterOffset) {
                    this.host = canonicalizeHost(s, n5, portColonOffset);
                    this.port = HttpUrl.defaultPort(this.scheme);
                }
                else {
                    this.host = canonicalizeHost(s, n5, portColonOffset);
                    this.port = parsePort(s, portColonOffset + 1, delimiterOffset);
                    if (this.port == -1) {
                        return ParseResult.INVALID_PORT;
                    }
                }
                if (this.host == null) {
                    return ParseResult.INVALID_HOST;
                }
                skipLeadingAsciiWhitespace = delimiterOffset;
            }
            final int delimiterOffset3 = Util.delimiterOffset(s, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace, "?#");
            this.resolvePath(s, skipLeadingAsciiWhitespace, delimiterOffset3);
            int delimiterOffset4;
            if (delimiterOffset3 < skipTrailingAsciiWhitespace && s.charAt(delimiterOffset3) == '?') {
                delimiterOffset4 = Util.delimiterOffset(s, delimiterOffset3, skipTrailingAsciiWhitespace, '#');
                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(s, delimiterOffset3 + 1, delimiterOffset4, " \"'<>#", true, false, true, true));
            }
            else {
                delimiterOffset4 = delimiterOffset3;
            }
            if (delimiterOffset4 < skipTrailingAsciiWhitespace && s.charAt(delimiterOffset4) == '#') {
                this.encodedFragment = HttpUrl.canonicalize(s, delimiterOffset4 + 1, skipTrailingAsciiWhitespace, "", true, false, false, false);
            }
            return ParseResult.SUCCESS;
        }
        
        public Builder password(final String s) {
            if (s != null) {
                this.encodedPassword = HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("password == null");
        }
        
        public Builder port(final int n) {
            if (n > 0 && n <= 65535) {
                this.port = n;
                return this;
            }
            throw new IllegalArgumentException("unexpected port: " + n);
        }
        
        public Builder query(final String s) {
            final List<String> list = null;
            List<String> queryStringToNamesAndValues;
            if (s == null) {
                queryStringToNamesAndValues = list;
            }
            else {
                queryStringToNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(s, " \"'<>#", false, false, true, true));
            }
            this.encodedQueryNamesAndValues = queryStringToNamesAndValues;
            return this;
        }
        
        Builder reencodeForUri() {
            for (int size = this.encodedPathSegments.size(), i = 0; i < size; ++i) {
                this.encodedPathSegments.set(i, HttpUrl.canonicalize(this.encodedPathSegments.get(i), "[]", true, true, false, true));
            }
            if (this.encodedQueryNamesAndValues != null) {
                for (int size2 = this.encodedQueryNamesAndValues.size(), j = 0; j < size2; ++j) {
                    final String s = this.encodedQueryNamesAndValues.get(j);
                    if (s != null) {
                        this.encodedQueryNamesAndValues.set(j, HttpUrl.canonicalize(s, "\\^`{|}", true, true, true, true));
                    }
                }
            }
            if (this.encodedFragment != null) {
                this.encodedFragment = HttpUrl.canonicalize(this.encodedFragment, " \"#<>\\^`{|}", true, true, false, false);
            }
            return this;
        }
        
        public Builder removeAllEncodedQueryParameters(final String s) {
            if (s == null) {
                throw new NullPointerException("encodedName == null");
            }
            if (this.encodedQueryNamesAndValues != null) {
                this.removeAllCanonicalQueryParameters(HttpUrl.canonicalize(s, " \"'<>#&=", true, false, true, true));
                return this;
            }
            return this;
        }
        
        public Builder removeAllQueryParameters(final String s) {
            if (s == null) {
                throw new NullPointerException("name == null");
            }
            if (this.encodedQueryNamesAndValues != null) {
                this.removeAllCanonicalQueryParameters(HttpUrl.canonicalize(s, " \"'<>#&=", false, false, true, true));
                return this;
            }
            return this;
        }
        
        public Builder removePathSegment(final int n) {
            this.encodedPathSegments.remove(n);
            if (this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            }
            return this;
        }
        
        public Builder scheme(final String str) {
            if (str != null) {
                if (!str.equalsIgnoreCase("http")) {
                    if (!str.equalsIgnoreCase("https")) {
                        throw new IllegalArgumentException("unexpected scheme: " + str);
                    }
                    this.scheme = "https";
                }
                else {
                    this.scheme = "http";
                }
                return this;
            }
            throw new NullPointerException("scheme == null");
        }
        
        public Builder setEncodedPathSegment(final int n, final String str) {
            if (str == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            final String canonicalize = HttpUrl.canonicalize(str, 0, str.length(), " \"<>^`{}|/\\?#", true, false, false, true);
            this.encodedPathSegments.set(n, canonicalize);
            if (!this.isDot(canonicalize) && !this.isDotDot(canonicalize)) {
                return this;
            }
            throw new IllegalArgumentException("unexpected path segment: " + str);
        }
        
        public Builder setEncodedQueryParameter(final String s, final String s2) {
            this.removeAllEncodedQueryParameters(s);
            this.addEncodedQueryParameter(s, s2);
            return this;
        }
        
        public Builder setPathSegment(final int n, final String str) {
            if (str == null) {
                throw new NullPointerException("pathSegment == null");
            }
            final String canonicalize = HttpUrl.canonicalize(str, 0, str.length(), " \"<>^`{}|/\\?#", false, false, false, true);
            if (!this.isDot(canonicalize) && !this.isDotDot(canonicalize)) {
                this.encodedPathSegments.set(n, canonicalize);
                return this;
            }
            throw new IllegalArgumentException("unexpected path segment: " + str);
        }
        
        public Builder setQueryParameter(final String s, final String s2) {
            this.removeAllQueryParameters(s);
            this.addQueryParameter(s, s2);
            return this;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.scheme);
            sb.append("://");
            if (!this.encodedUsername.isEmpty() || !this.encodedPassword.isEmpty()) {
                sb.append(this.encodedUsername);
                if (!this.encodedPassword.isEmpty()) {
                    sb.append(':');
                    sb.append(this.encodedPassword);
                }
                sb.append('@');
            }
            if (this.host.indexOf(58) == -1) {
                sb.append(this.host);
            }
            else {
                sb.append('[');
                sb.append(this.host);
                sb.append(']');
            }
            final int effectivePort = this.effectivePort();
            if (effectivePort != HttpUrl.defaultPort(this.scheme)) {
                sb.append(':');
                sb.append(effectivePort);
            }
            HttpUrl.pathSegmentsToString(sb, this.encodedPathSegments);
            if (this.encodedQueryNamesAndValues != null) {
                sb.append('?');
                HttpUrl.namesAndValuesToQueryString(sb, this.encodedQueryNamesAndValues);
            }
            if (this.encodedFragment != null) {
                sb.append('#');
                sb.append(this.encodedFragment);
            }
            return sb.toString();
        }
        
        public Builder username(final String s) {
            if (s != null) {
                this.encodedUsername = HttpUrl.canonicalize(s, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("username == null");
        }
        
        enum ParseResult
        {
            INVALID_HOST, 
            INVALID_PORT, 
            MISSING_SCHEME, 
            SUCCESS, 
            UNSUPPORTED_SCHEME;
        }
    }
}
