package okhttp3;

import com.google.common.primitives.UnsignedBytes;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import okhttp3.internal.Util;
import okio.Buffer;
/* loaded from: classes.dex */
public final class HttpUrl {
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
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

    /* loaded from: classes.dex */
    public static final class Builder {
        String encodedFragment;
        List<String> encodedQueryNamesAndValues;
        String host;
        String scheme;
        String encodedUsername = "";
        String encodedPassword = "";
        int port = -1;
        final List<String> encodedPathSegments = new ArrayList();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum ParseResult {
            SUCCESS,
            MISSING_SCHEME,
            UNSUPPORTED_SCHEME,
            INVALID_PORT,
            INVALID_HOST
        }

        public Builder() {
            this.encodedPathSegments.add("");
        }

        private Builder addPathSegments(String str, boolean z) {
            int i = 0;
            do {
                int delimiterOffset = Util.delimiterOffset(str, i, str.length(), "/\\");
                push(str, i, delimiterOffset, delimiterOffset < str.length(), z);
                i = delimiterOffset + 1;
            } while (i <= str.length());
            return this;
        }

        private static String canonicalizeHost(String str, int i, int i2) {
            String percentDecode = HttpUrl.percentDecode(str, i, i2, false);
            if (percentDecode.contains(":")) {
                InetAddress decodeIpv6 = (percentDecode.startsWith("[") && percentDecode.endsWith("]")) ? decodeIpv6(percentDecode, 1, percentDecode.length() - 1) : decodeIpv6(percentDecode, 0, percentDecode.length());
                if (decodeIpv6 != null) {
                    byte[] address = decodeIpv6.getAddress();
                    if (address.length != 16) {
                        throw new AssertionError();
                    }
                    return inet6AddressToAscii(address);
                }
                return null;
            }
            return Util.domainToAscii(percentDecode);
        }

        private static boolean decodeIpv4Suffix(String str, int i, int i2, byte[] bArr, int i3) {
            int i4 = i;
            int i5 = i3;
            while (i4 < i2) {
                if (i5 == bArr.length) {
                    return false;
                }
                if (i5 != i3) {
                    if (str.charAt(i4) != '.') {
                        return false;
                    }
                    i4++;
                }
                int i6 = 0;
                int i7 = i4;
                while (i7 < i2) {
                    char charAt = str.charAt(i7);
                    if (charAt < '0' || charAt > '9') {
                        break;
                    } else if ((i6 == 0 && i4 != i7) || ((i6 * 10) + charAt) - 48 > 255) {
                        return false;
                    } else {
                        i7++;
                    }
                }
                if (i7 - i4 == 0) {
                    return false;
                }
                bArr[i5] = (byte) i6;
                i5++;
                i4 = i7;
            }
            return i5 == i3 + 4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:17:0x0027, code lost:
            return null;
         */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0062  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static java.net.InetAddress decodeIpv6(java.lang.String r12, int r13, int r14) {
            /*
                r11 = 1
                r6 = -1
                r10 = 0
                r4 = 0
                r0 = 16
                byte[] r7 = new byte[r0]
                r0 = r13
                r3 = r6
                r1 = r6
                r2 = r4
            Lc:
                if (r0 < r14) goto L16
            Le:
                int r0 = r7.length
                if (r2 != r0) goto L8a
            L11:
                java.net.InetAddress r0 = java.net.InetAddress.getByAddress(r7)     // Catch: java.net.UnknownHostException -> L9e
                return r0
            L16:
                int r5 = r7.length
                if (r2 == r5) goto L28
                int r5 = r0 + 2
                if (r5 <= r14) goto L29
            L1d:
                if (r2 != 0) goto L40
            L1f:
                r3 = r4
                r5 = r0
            L21:
                if (r5 < r14) goto L62
            L23:
                int r8 = r5 - r0
                if (r8 != 0) goto L72
            L27:
                return r10
            L28:
                return r10
            L29:
                java.lang.String r5 = "::"
                r8 = 2
                boolean r5 = r12.regionMatches(r0, r5, r4, r8)
                if (r5 == 0) goto L1d
                if (r1 != r6) goto L3d
                int r0 = r0 + 2
                int r1 = r2 + 2
                if (r0 == r14) goto L3e
                r2 = r1
                goto L1f
            L3d:
                return r10
            L3e:
                r2 = r1
                goto Le
            L40:
                java.lang.String r5 = ":"
                boolean r5 = r12.regionMatches(r0, r5, r4, r11)
                if (r5 != 0) goto L53
                java.lang.String r5 = "."
                boolean r0 = r12.regionMatches(r0, r5, r4, r11)
                if (r0 != 0) goto L56
                return r10
            L53:
                int r0 = r0 + 1
                goto L1f
            L56:
                int r0 = r2 + (-2)
                boolean r0 = decodeIpv4Suffix(r12, r3, r14, r7, r0)
                if (r0 == 0) goto L61
                int r2 = r2 + 2
                goto Le
            L61:
                return r10
            L62:
                char r8 = r12.charAt(r5)
                int r8 = okhttp3.HttpUrl.decodeHexDigit(r8)
                if (r8 == r6) goto L23
                int r3 = r3 << 4
                int r3 = r3 + r8
                int r5 = r5 + 1
                goto L21
            L72:
                r9 = 4
                if (r8 > r9) goto L27
                int r8 = r2 + 1
                int r9 = r3 >>> 8
                r9 = r9 & 255(0xff, float:3.57E-43)
                byte r9 = (byte) r9
                byte r9 = (byte) r9
                r7[r2] = r9
                int r2 = r8 + 1
                r3 = r3 & 255(0xff, float:3.57E-43)
                byte r3 = (byte) r3
                byte r3 = (byte) r3
                r7[r8] = r3
                r3 = r0
                r0 = r5
                goto Lc
            L8a:
                if (r1 == r6) goto L9d
                int r0 = r7.length
                int r3 = r2 - r1
                int r0 = r0 - r3
                int r3 = r2 - r1
                java.lang.System.arraycopy(r7, r1, r7, r0, r3)
                int r0 = r7.length
                int r0 = r0 - r2
                int r0 = r0 + r1
                java.util.Arrays.fill(r7, r1, r0, r4)
                goto L11
            L9d:
                return r10
            L9e:
                r0 = move-exception
                java.lang.AssertionError r0 = new java.lang.AssertionError
                r0.<init>()
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.HttpUrl.Builder.decodeIpv6(java.lang.String, int, int):java.net.InetAddress");
        }

        private static String inet6AddressToAscii(byte[] bArr) {
            int i = 0;
            int i2 = 0;
            int i3 = -1;
            int i4 = 0;
            while (i4 < bArr.length) {
                int i5 = i4;
                while (i5 < 16 && bArr[i5] == 0 && bArr[i5 + 1] == 0) {
                    i5 += 2;
                }
                int i6 = i5 - i4;
                if (i6 > i2) {
                    i2 = i6;
                    i3 = i4;
                }
                i4 = i5 + 2;
            }
            Buffer buffer = new Buffer();
            while (i < bArr.length) {
                if (i != i3) {
                    if (i > 0) {
                        buffer.writeByte(58);
                    }
                    buffer.writeHexadecimalUnsignedLong(((bArr[i] & UnsignedBytes.MAX_VALUE) << 8) | (bArr[i + 1] & UnsignedBytes.MAX_VALUE));
                    i += 2;
                } else {
                    buffer.writeByte(58);
                    i += i2;
                    if (i == 16) {
                        buffer.writeByte(58);
                    }
                }
            }
            return buffer.readUtf8();
        }

        private boolean isDot(String str) {
            return str.equals(".") || str.equalsIgnoreCase("%2e");
        }

        private boolean isDotDot(String str) {
            return str.equals("..") || str.equalsIgnoreCase("%2e.") || str.equalsIgnoreCase(".%2e") || str.equalsIgnoreCase("%2e%2e");
        }

        private static int parsePort(String str, int i, int i2) {
            try {
                int parseInt = Integer.parseInt(HttpUrl.canonicalize(str, i, i2, "", false, false, false, true));
                if (parseInt > 0 && parseInt <= 65535) {
                    return parseInt;
                }
                return -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private void pop() {
            if (this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1).isEmpty() && !this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            } else {
                this.encodedPathSegments.add("");
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private static int portColonOffset(String str, int i, int i2) {
            int i3 = i;
            while (i3 < i2) {
                switch (str.charAt(i3)) {
                    case ':':
                        return i3;
                    case '[':
                        break;
                    default:
                        i3++;
                }
                do {
                    i3++;
                    if (i3 >= i2) {
                        break;
                    }
                    i3++;
                } while (str.charAt(i3) != ']');
                i3++;
            }
            return i2;
        }

        private void push(String str, int i, int i2, boolean z, boolean z2) {
            String canonicalize = HttpUrl.canonicalize(str, i, i2, HttpUrl.PATH_SEGMENT_ENCODE_SET, z2, false, false, true);
            if (isDot(canonicalize)) {
                return;
            }
            if (isDotDot(canonicalize)) {
                pop();
                return;
            }
            if (this.encodedPathSegments.get(this.encodedPathSegments.size() - 1).isEmpty()) {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, canonicalize);
            } else {
                this.encodedPathSegments.add(canonicalize);
            }
            if (z) {
                this.encodedPathSegments.add("");
            }
        }

        private void removeAllCanonicalQueryParameters(String str) {
            int size = this.encodedQueryNamesAndValues.size();
            while (true) {
                size -= 2;
                if (size < 0) {
                    return;
                }
                if (str.equals(this.encodedQueryNamesAndValues.get(size))) {
                    this.encodedQueryNamesAndValues.remove(size + 1);
                    this.encodedQueryNamesAndValues.remove(size);
                    if (this.encodedQueryNamesAndValues.isEmpty()) {
                        this.encodedQueryNamesAndValues = null;
                        return;
                    }
                }
            }
        }

        private void resolvePath(String str, int i, int i2) {
            if (i != i2) {
                char charAt = str.charAt(i);
                if (charAt == '/' || charAt == '\\') {
                    this.encodedPathSegments.clear();
                    this.encodedPathSegments.add("");
                    i++;
                } else {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
                }
                int i3 = i;
                while (i3 < i2) {
                    int delimiterOffset = Util.delimiterOffset(str, i3, i2, "/\\");
                    boolean z = delimiterOffset < i2;
                    push(str, i3, delimiterOffset, z, true);
                    if (z) {
                        delimiterOffset++;
                    }
                    i3 = delimiterOffset;
                }
            }
        }

        private static int schemeDelimiterOffset(String str, int i, int i2) {
            if (i2 - i >= 2) {
                char charAt = str.charAt(i);
                if ((charAt >= 'a' && charAt <= 'z') || (charAt >= 'A' && charAt <= 'Z')) {
                    for (int i3 = i + 1; i3 < i2; i3++) {
                        char charAt2 = str.charAt(i3);
                        if ((charAt2 < 'a' || charAt2 > 'z') && ((charAt2 < 'A' || charAt2 > 'Z') && ((charAt2 < '0' || charAt2 > '9') && charAt2 != '+' && charAt2 != '-' && charAt2 != '.'))) {
                            if (charAt2 != ':') {
                                return -1;
                            }
                            return i3;
                        }
                    }
                    return -1;
                }
                return -1;
            }
            return -1;
        }

        private static int slashCount(String str, int i, int i2) {
            int i3 = 0;
            while (i < i2) {
                char charAt = str.charAt(i);
                if (charAt != '\\' && charAt != '/') {
                    break;
                }
                i3++;
                i++;
            }
            return i3;
        }

        public Builder addEncodedPathSegment(String str) {
            if (str != null) {
                push(str, 0, str.length(), false, true);
                return this;
            }
            throw new NullPointerException("encodedPathSegment == null");
        }

        public Builder addEncodedPathSegments(String str) {
            if (str != null) {
                return addPathSegments(str, true);
            }
            throw new NullPointerException("encodedPathSegments == null");
        }

        public Builder addEncodedQueryParameter(String str, String str2) {
            if (str != null) {
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(str, HttpUrl.QUERY_COMPONENT_ENCODE_SET, true, false, true, true));
                this.encodedQueryNamesAndValues.add(str2 == null ? null : HttpUrl.canonicalize(str2, HttpUrl.QUERY_COMPONENT_ENCODE_SET, true, false, true, true));
                return this;
            }
            throw new NullPointerException("encodedName == null");
        }

        public Builder addPathSegment(String str) {
            if (str != null) {
                push(str, 0, str.length(), false, false);
                return this;
            }
            throw new NullPointerException("pathSegment == null");
        }

        public Builder addPathSegments(String str) {
            if (str != null) {
                return addPathSegments(str, false);
            }
            throw new NullPointerException("pathSegments == null");
        }

        public Builder addQueryParameter(String str, String str2) {
            if (str != null) {
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(str, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                this.encodedQueryNamesAndValues.add(str2 == null ? null : HttpUrl.canonicalize(str2, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                return this;
            }
            throw new NullPointerException("name == null");
        }

        public HttpUrl build() {
            if (this.scheme != null) {
                if (this.host != null) {
                    return new HttpUrl(this);
                }
                throw new IllegalStateException("host == null");
            }
            throw new IllegalStateException("scheme == null");
        }

        int effectivePort() {
            return this.port == -1 ? HttpUrl.defaultPort(this.scheme) : this.port;
        }

        public Builder encodedFragment(String str) {
            this.encodedFragment = str != null ? HttpUrl.canonicalize(str, "", true, false, false, false) : null;
            return this;
        }

        public Builder encodedPassword(String str) {
            if (str != null) {
                this.encodedPassword = HttpUrl.canonicalize(str, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedPassword == null");
        }

        public Builder encodedPath(String str) {
            if (str != null) {
                if (str.startsWith("/")) {
                    resolvePath(str, 0, str.length());
                    return this;
                }
                throw new IllegalArgumentException("unexpected encodedPath: " + str);
            }
            throw new NullPointerException("encodedPath == null");
        }

        public Builder encodedQuery(String str) {
            this.encodedQueryNamesAndValues = str != null ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(str, HttpUrl.QUERY_ENCODE_SET, true, false, true, true)) : null;
            return this;
        }

        public Builder encodedUsername(String str) {
            if (str != null) {
                this.encodedUsername = HttpUrl.canonicalize(str, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedUsername == null");
        }

        public Builder fragment(String str) {
            this.encodedFragment = str != null ? HttpUrl.canonicalize(str, "", false, false, false, false) : null;
            return this;
        }

        public Builder host(String str) {
            if (str != null) {
                String canonicalizeHost = canonicalizeHost(str, 0, str.length());
                if (canonicalizeHost != null) {
                    this.host = canonicalizeHost;
                    return this;
                }
                throw new IllegalArgumentException("unexpected host: " + str);
            }
            throw new NullPointerException("host == null");
        }

        ParseResult parse(HttpUrl httpUrl, String str) {
            int delimiterOffset;
            int skipLeadingAsciiWhitespace = Util.skipLeadingAsciiWhitespace(str, 0, str.length());
            int skipTrailingAsciiWhitespace = Util.skipTrailingAsciiWhitespace(str, skipLeadingAsciiWhitespace, str.length());
            if (schemeDelimiterOffset(str, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace) == -1) {
                if (httpUrl == null) {
                    return ParseResult.MISSING_SCHEME;
                }
                this.scheme = httpUrl.scheme;
            } else if (str.regionMatches(true, skipLeadingAsciiWhitespace, "https:", 0, 6)) {
                this.scheme = "https";
                skipLeadingAsciiWhitespace += "https:".length();
            } else if (!str.regionMatches(true, skipLeadingAsciiWhitespace, "http:", 0, 5)) {
                return ParseResult.UNSUPPORTED_SCHEME;
            } else {
                this.scheme = "http";
                skipLeadingAsciiWhitespace += "http:".length();
            }
            boolean z = false;
            boolean z2 = false;
            int slashCount = slashCount(str, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace);
            if (slashCount < 2 && httpUrl != null && httpUrl.scheme.equals(this.scheme)) {
                this.encodedUsername = httpUrl.encodedUsername();
                this.encodedPassword = httpUrl.encodedPassword();
                this.host = httpUrl.host;
                this.port = httpUrl.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(httpUrl.encodedPathSegments());
                if (skipLeadingAsciiWhitespace == skipTrailingAsciiWhitespace || str.charAt(skipLeadingAsciiWhitespace) == '#') {
                    encodedQuery(httpUrl.encodedQuery());
                }
            } else {
                int i = skipLeadingAsciiWhitespace + slashCount;
                while (true) {
                    boolean z3 = z2;
                    boolean z4 = z;
                    int i2 = i;
                    int delimiterOffset2 = Util.delimiterOffset(str, i2, skipTrailingAsciiWhitespace, "@/\\?#");
                    switch (delimiterOffset2 == skipTrailingAsciiWhitespace ? (char) 65535 : str.charAt(delimiterOffset2)) {
                        case 65535:
                        case '#':
                        case '/':
                        case '?':
                        case '\\':
                            int portColonOffset = portColonOffset(str, i2, delimiterOffset2);
                            if (portColonOffset + 1 >= delimiterOffset2) {
                                this.host = canonicalizeHost(str, i2, portColonOffset);
                                this.port = HttpUrl.defaultPort(this.scheme);
                            } else {
                                this.host = canonicalizeHost(str, i2, portColonOffset);
                                this.port = parsePort(str, portColonOffset + 1, delimiterOffset2);
                                if (this.port == -1) {
                                    return ParseResult.INVALID_PORT;
                                }
                            }
                            if (this.host != null) {
                                skipLeadingAsciiWhitespace = delimiterOffset2;
                                break;
                            } else {
                                return ParseResult.INVALID_HOST;
                            }
                        case '@':
                            if (z3) {
                                this.encodedPassword += "%40" + HttpUrl.canonicalize(str, i2, delimiterOffset2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                            } else {
                                int delimiterOffset3 = Util.delimiterOffset(str, i2, delimiterOffset2, ':');
                                String canonicalize = HttpUrl.canonicalize(str, i2, delimiterOffset3, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                                if (z4) {
                                    canonicalize = this.encodedUsername + "%40" + canonicalize;
                                }
                                this.encodedUsername = canonicalize;
                                if (delimiterOffset3 != delimiterOffset2) {
                                    z3 = true;
                                    this.encodedPassword = HttpUrl.canonicalize(str, delimiterOffset3 + 1, delimiterOffset2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                                }
                                z4 = true;
                            }
                            i = delimiterOffset2 + 1;
                            z2 = z3;
                            break;
                        default:
                            z2 = z3;
                            i = i2;
                            break;
                    }
                    z = z4;
                }
            }
            int delimiterOffset4 = Util.delimiterOffset(str, skipLeadingAsciiWhitespace, skipTrailingAsciiWhitespace, "?#");
            resolvePath(str, skipLeadingAsciiWhitespace, delimiterOffset4);
            if (delimiterOffset4 < skipTrailingAsciiWhitespace && str.charAt(delimiterOffset4) == '?') {
                delimiterOffset = Util.delimiterOffset(str, delimiterOffset4, skipTrailingAsciiWhitespace, '#');
                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(str, delimiterOffset4 + 1, delimiterOffset, HttpUrl.QUERY_ENCODE_SET, true, false, true, true));
            } else {
                delimiterOffset = delimiterOffset4;
            }
            if (delimiterOffset < skipTrailingAsciiWhitespace && str.charAt(delimiterOffset) == '#') {
                this.encodedFragment = HttpUrl.canonicalize(str, delimiterOffset + 1, skipTrailingAsciiWhitespace, "", true, false, false, false);
            }
            return ParseResult.SUCCESS;
        }

        public Builder password(String str) {
            if (str != null) {
                this.encodedPassword = HttpUrl.canonicalize(str, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("password == null");
        }

        public Builder port(int i) {
            if (i > 0 && i <= 65535) {
                this.port = i;
                return this;
            }
            throw new IllegalArgumentException("unexpected port: " + i);
        }

        public Builder query(String str) {
            this.encodedQueryNamesAndValues = str != null ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(str, HttpUrl.QUERY_ENCODE_SET, false, false, true, true)) : null;
            return this;
        }

        Builder reencodeForUri() {
            int size = this.encodedPathSegments.size();
            for (int i = 0; i < size; i++) {
                this.encodedPathSegments.set(i, HttpUrl.canonicalize(this.encodedPathSegments.get(i), HttpUrl.PATH_SEGMENT_ENCODE_SET_URI, true, true, false, true));
            }
            if (this.encodedQueryNamesAndValues != null) {
                int size2 = this.encodedQueryNamesAndValues.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String str = this.encodedQueryNamesAndValues.get(i2);
                    if (str != null) {
                        this.encodedQueryNamesAndValues.set(i2, HttpUrl.canonicalize(str, HttpUrl.QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, true));
                    }
                }
            }
            if (this.encodedFragment != null) {
                this.encodedFragment = HttpUrl.canonicalize(this.encodedFragment, HttpUrl.FRAGMENT_ENCODE_SET_URI, true, true, false, false);
            }
            return this;
        }

        public Builder removeAllEncodedQueryParameters(String str) {
            if (str != null) {
                if (this.encodedQueryNamesAndValues != null) {
                    removeAllCanonicalQueryParameters(HttpUrl.canonicalize(str, HttpUrl.QUERY_COMPONENT_ENCODE_SET, true, false, true, true));
                    return this;
                }
                return this;
            }
            throw new NullPointerException("encodedName == null");
        }

        public Builder removeAllQueryParameters(String str) {
            if (str != null) {
                if (this.encodedQueryNamesAndValues != null) {
                    removeAllCanonicalQueryParameters(HttpUrl.canonicalize(str, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                    return this;
                }
                return this;
            }
            throw new NullPointerException("name == null");
        }

        public Builder removePathSegment(int i) {
            this.encodedPathSegments.remove(i);
            if (this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            }
            return this;
        }

        public Builder scheme(String str) {
            if (str != null) {
                if (str.equalsIgnoreCase("http")) {
                    this.scheme = "http";
                } else if (!str.equalsIgnoreCase("https")) {
                    throw new IllegalArgumentException("unexpected scheme: " + str);
                } else {
                    this.scheme = "https";
                }
                return this;
            }
            throw new NullPointerException("scheme == null");
        }

        public Builder setEncodedPathSegment(int i, String str) {
            if (str != null) {
                String canonicalize = HttpUrl.canonicalize(str, 0, str.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, true, false, false, true);
                this.encodedPathSegments.set(i, canonicalize);
                if (isDot(canonicalize) || isDotDot(canonicalize)) {
                    throw new IllegalArgumentException("unexpected path segment: " + str);
                }
                return this;
            }
            throw new NullPointerException("encodedPathSegment == null");
        }

        public Builder setEncodedQueryParameter(String str, String str2) {
            removeAllEncodedQueryParameters(str);
            addEncodedQueryParameter(str, str2);
            return this;
        }

        public Builder setPathSegment(int i, String str) {
            if (str != null) {
                String canonicalize = HttpUrl.canonicalize(str, 0, str.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, false, false, false, true);
                if (isDot(canonicalize) || isDotDot(canonicalize)) {
                    throw new IllegalArgumentException("unexpected path segment: " + str);
                }
                this.encodedPathSegments.set(i, canonicalize);
                return this;
            }
            throw new NullPointerException("pathSegment == null");
        }

        public Builder setQueryParameter(String str, String str2) {
            removeAllQueryParameters(str);
            addQueryParameter(str, str2);
            return this;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
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
            } else {
                sb.append('[');
                sb.append(this.host);
                sb.append(']');
            }
            int effectivePort = effectivePort();
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

        public Builder username(String str) {
            if (str != null) {
                this.encodedUsername = HttpUrl.canonicalize(str, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("username == null");
        }
    }

    HttpUrl(Builder builder) {
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        this.queryNamesAndValues = builder.encodedQueryNamesAndValues == null ? null : percentDecode(builder.encodedQueryNamesAndValues, true);
        this.fragment = builder.encodedFragment != null ? percentDecode(builder.encodedFragment, false) : null;
        this.url = builder.toString();
    }

    static String canonicalize(String str, int i, int i2, String str2, boolean z, boolean z2, boolean z3, boolean z4) {
        int i3 = i;
        while (i3 < i2) {
            int codePointAt = str.codePointAt(i3);
            if (codePointAt < 32 || codePointAt == 127 || ((codePointAt >= 128 && z4) || str2.indexOf(codePointAt) != -1 || ((codePointAt == 37 && (!z || (z2 && !percentEncoded(str, i3, i2)))) || (codePointAt == 43 && z3)))) {
                Buffer buffer = new Buffer();
                buffer.writeUtf8(str, i, i3);
                canonicalize(buffer, str, i3, i2, str2, z, z2, z3, z4);
                return buffer.readUtf8();
            }
            i3 += Character.charCount(codePointAt);
        }
        return str.substring(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String canonicalize(String str, String str2, boolean z, boolean z2, boolean z3, boolean z4) {
        return canonicalize(str, 0, str.length(), str2, z, z2, z3, z4);
    }

    static void canonicalize(Buffer buffer, String str, int i, int i2, String str2, boolean z, boolean z2, boolean z3, boolean z4) {
        Buffer buffer2 = null;
        while (i < i2) {
            int codePointAt = str.codePointAt(i);
            if (!z || (codePointAt != 9 && codePointAt != 10 && codePointAt != 12 && codePointAt != 13)) {
                if (codePointAt == 43 && z3) {
                    buffer.writeUtf8(!z ? "%2B" : "+");
                } else if (codePointAt >= 32 && codePointAt != 127 && ((codePointAt < 128 || !z4) && str2.indexOf(codePointAt) == -1 && (codePointAt != 37 || (z && (!z2 || percentEncoded(str, i, i2)))))) {
                    buffer.writeUtf8CodePoint(codePointAt);
                } else {
                    Buffer buffer3 = buffer2 != null ? buffer2 : new Buffer();
                    buffer3.writeUtf8CodePoint(codePointAt);
                    while (!buffer3.exhausted()) {
                        int readByte = buffer3.readByte() & UnsignedBytes.MAX_VALUE;
                        buffer.writeByte(37);
                        buffer.writeByte((int) HEX_DIGITS[(readByte >> 4) & 15]);
                        buffer.writeByte((int) HEX_DIGITS[readByte & 15]);
                    }
                    buffer2 = buffer3;
                }
            }
            i += Character.charCount(codePointAt);
        }
    }

    static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        return -1;
    }

    public static int defaultPort(String str) {
        if (str.equals("http")) {
            return 80;
        }
        return !str.equals("https") ? -1 : 443;
    }

    public static HttpUrl get(URI uri) {
        return parse(uri.toString());
    }

    public static HttpUrl get(URL url) {
        return parse(url.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HttpUrl getChecked(String str) throws MalformedURLException, UnknownHostException {
        Builder builder = new Builder();
        Builder.ParseResult parse = builder.parse(null, str);
        switch (parse) {
            case SUCCESS:
                return builder.build();
            case INVALID_HOST:
                throw new UnknownHostException("Invalid host: " + str);
            case UNSUPPORTED_SCHEME:
            case MISSING_SCHEME:
            case INVALID_PORT:
            default:
                throw new MalformedURLException("Invalid URL: " + parse + " for " + str);
        }
    }

    static void namesAndValuesToQueryString(StringBuilder sb, List<String> list) {
        int size = list.size();
        for (int i = 0; i < size; i += 2) {
            String str = list.get(i);
            String str2 = list.get(i + 1);
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

    public static HttpUrl parse(String str) {
        Builder builder = new Builder();
        if (builder.parse(null, str) != Builder.ParseResult.SUCCESS) {
            return null;
        }
        return builder.build();
    }

    static void pathSegmentsToString(StringBuilder sb, List<String> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sb.append('/');
            sb.append(list.get(i));
        }
    }

    static String percentDecode(String str, int i, int i2, boolean z) {
        for (int i3 = i; i3 < i2; i3++) {
            char charAt = str.charAt(i3);
            if (charAt == '%' || (charAt == '+' && z)) {
                Buffer buffer = new Buffer();
                buffer.writeUtf8(str, i, i3);
                percentDecode(buffer, str, i3, i2, z);
                return buffer.readUtf8();
            }
        }
        return str.substring(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String percentDecode(String str, boolean z) {
        return percentDecode(str, 0, str.length(), z);
    }

    private List<String> percentDecode(List<String> list, boolean z) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            String str = list.get(i);
            arrayList.add(str == null ? null : percentDecode(str, z));
        }
        return Collections.unmodifiableList(arrayList);
    }

    static void percentDecode(Buffer buffer, String str, int i, int i2, boolean z) {
        int i3 = i;
        while (i3 < i2) {
            int codePointAt = str.codePointAt(i3);
            if (codePointAt == 37 && i3 + 2 < i2) {
                int decodeHexDigit = decodeHexDigit(str.charAt(i3 + 1));
                int decodeHexDigit2 = decodeHexDigit(str.charAt(i3 + 2));
                if (decodeHexDigit != -1 && decodeHexDigit2 != -1) {
                    buffer.writeByte((decodeHexDigit << 4) + decodeHexDigit2);
                    i3 += 2;
                    i3 += Character.charCount(codePointAt);
                }
                buffer.writeUtf8CodePoint(codePointAt);
                i3 += Character.charCount(codePointAt);
            } else {
                if (codePointAt == 43 && z) {
                    buffer.writeByte(32);
                    i3 += Character.charCount(codePointAt);
                }
                buffer.writeUtf8CodePoint(codePointAt);
                i3 += Character.charCount(codePointAt);
            }
        }
    }

    static boolean percentEncoded(String str, int i, int i2) {
        return i + 2 < i2 && str.charAt(i) == '%' && decodeHexDigit(str.charAt(i + 1)) != -1 && decodeHexDigit(str.charAt(i + 2)) != -1;
    }

    static List<String> queryStringToNamesAndValues(String str) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i <= str.length()) {
            int indexOf = str.indexOf(38, i);
            if (indexOf == -1) {
                indexOf = str.length();
            }
            int indexOf2 = str.indexOf(61, i);
            if (indexOf2 != -1 && indexOf2 <= indexOf) {
                arrayList.add(str.substring(i, indexOf2));
                arrayList.add(str.substring(indexOf2 + 1, indexOf));
            } else {
                arrayList.add(str.substring(i, indexOf));
                arrayList.add(null);
            }
            i = indexOf + 1;
        }
        return arrayList;
    }

    public String encodedFragment() {
        if (this.fragment != null) {
            return this.url.substring(this.url.indexOf(35) + 1);
        }
        return null;
    }

    public String encodedPassword() {
        if (this.password.isEmpty()) {
            return "";
        }
        int indexOf = this.url.indexOf(64);
        return this.url.substring(this.url.indexOf(58, this.scheme.length() + 3) + 1, indexOf);
    }

    public String encodedPath() {
        int indexOf = this.url.indexOf(47, this.scheme.length() + 3);
        return this.url.substring(indexOf, Util.delimiterOffset(this.url, indexOf, this.url.length(), "?#"));
    }

    public List<String> encodedPathSegments() {
        int indexOf = this.url.indexOf(47, this.scheme.length() + 3);
        int delimiterOffset = Util.delimiterOffset(this.url, indexOf, this.url.length(), "?#");
        ArrayList arrayList = new ArrayList();
        while (indexOf < delimiterOffset) {
            int i = indexOf + 1;
            indexOf = Util.delimiterOffset(this.url, i, delimiterOffset, '/');
            arrayList.add(this.url.substring(i, indexOf));
        }
        return arrayList;
    }

    public String encodedQuery() {
        if (this.queryNamesAndValues != null) {
            int indexOf = this.url.indexOf(63) + 1;
            return this.url.substring(indexOf, Util.delimiterOffset(this.url, indexOf + 1, this.url.length(), '#'));
        }
        return null;
    }

    public String encodedUsername() {
        if (this.username.isEmpty()) {
            return "";
        }
        int length = this.scheme.length() + 3;
        return this.url.substring(length, Util.delimiterOffset(this.url, length, this.url.length(), ":@"));
    }

    public boolean equals(Object obj) {
        return (obj instanceof HttpUrl) && ((HttpUrl) obj).url.equals(this.url);
    }

    public String fragment() {
        return this.fragment;
    }

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
        Builder builder = new Builder();
        builder.scheme = this.scheme;
        builder.encodedUsername = encodedUsername();
        builder.encodedPassword = encodedPassword();
        builder.host = this.host;
        builder.port = this.port == defaultPort(this.scheme) ? -1 : this.port;
        builder.encodedPathSegments.clear();
        builder.encodedPathSegments.addAll(encodedPathSegments());
        builder.encodedQuery(encodedQuery());
        builder.encodedFragment = encodedFragment();
        return builder;
    }

    public Builder newBuilder(String str) {
        Builder builder = new Builder();
        if (builder.parse(this, str) != Builder.ParseResult.SUCCESS) {
            return null;
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
            StringBuilder sb = new StringBuilder();
            namesAndValuesToQueryString(sb, this.queryNamesAndValues);
            return sb.toString();
        }
        return null;
    }

    public String queryParameter(String str) {
        if (this.queryNamesAndValues != null) {
            int size = this.queryNamesAndValues.size();
            for (int i = 0; i < size; i += 2) {
                if (str.equals(this.queryNamesAndValues.get(i))) {
                    return this.queryNamesAndValues.get(i + 1);
                }
            }
            return null;
        }
        return null;
    }

    public String queryParameterName(int i) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get(i * 2);
        }
        throw new IndexOutOfBoundsException();
    }

    public Set<String> queryParameterNames() {
        if (this.queryNamesAndValues != null) {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            int size = this.queryNamesAndValues.size();
            for (int i = 0; i < size; i += 2) {
                linkedHashSet.add(this.queryNamesAndValues.get(i));
            }
            return Collections.unmodifiableSet(linkedHashSet);
        }
        return Collections.emptySet();
    }

    public String queryParameterValue(int i) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get((i * 2) + 1);
        }
        throw new IndexOutOfBoundsException();
    }

    public List<String> queryParameterValues(String str) {
        if (this.queryNamesAndValues != null) {
            ArrayList arrayList = new ArrayList();
            int size = this.queryNamesAndValues.size();
            for (int i = 0; i < size; i += 2) {
                if (str.equals(this.queryNamesAndValues.get(i))) {
                    arrayList.add(this.queryNamesAndValues.get(i + 1));
                }
            }
            return Collections.unmodifiableList(arrayList);
        }
        return Collections.emptyList();
    }

    public int querySize() {
        if (this.queryNamesAndValues == null) {
            return 0;
        }
        return this.queryNamesAndValues.size() / 2;
    }

    public String redact() {
        return newBuilder("/...").username("").password("").build().toString();
    }

    public HttpUrl resolve(String str) {
        Builder newBuilder = newBuilder(str);
        if (newBuilder == null) {
            return null;
        }
        return newBuilder.build();
    }

    public String scheme() {
        return this.scheme;
    }

    public String toString() {
        return this.url;
    }

    public URI uri() {
        String builder = newBuilder().reencodeForUri().toString();
        try {
            return new URI(builder);
        } catch (URISyntaxException e) {
            try {
                return URI.create(builder.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", ""));
            } catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }

    public URL url() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String username() {
        return this.username;
    }
}
