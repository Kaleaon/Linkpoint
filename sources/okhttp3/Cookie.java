package okhttp3;

import com.google.common.net.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;

public final class Cookie {
    private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;

    public static final class Builder {
        String domain;
        long expiresAt = HttpDate.MAX_DATE;
        boolean hostOnly;
        boolean httpOnly;
        String name;
        String path = "/";
        boolean persistent;
        boolean secure;
        String value;

        private Builder domain(String str, boolean z) {
            if (str != null) {
                String domainToAscii = Util.domainToAscii(str);
                if (domainToAscii != null) {
                    this.domain = domainToAscii;
                    this.hostOnly = z;
                    return this;
                }
                throw new IllegalArgumentException("unexpected domain: " + str);
            }
            throw new NullPointerException("domain == null");
        }

        public Cookie build() {
            return new Cookie(this);
        }

        public Builder domain(String str) {
            return domain(str, false);
        }

        public Builder expiresAt(long j) {
            long j2 = HttpDate.MAX_DATE;
            boolean z = false;
            long j3 = !((j > 0 ? 1 : (j == 0 ? 0 : -1)) > 0) ? Long.MIN_VALUE : j;
            if (j3 <= HttpDate.MAX_DATE) {
                z = true;
            }
            if (z) {
                j2 = j3;
            }
            this.expiresAt = j2;
            this.persistent = true;
            return this;
        }

        public Builder hostOnlyDomain(String str) {
            return domain(str, true);
        }

        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }

        public Builder name(String str) {
            if (str == null) {
                throw new NullPointerException("name == null");
            } else if (str.trim().equals(str)) {
                this.name = str;
                return this;
            } else {
                throw new IllegalArgumentException("name is not trimmed");
            }
        }

        public Builder path(String str) {
            if (str.startsWith("/")) {
                this.path = str;
                return this;
            }
            throw new IllegalArgumentException("path must start with '/'");
        }

        public Builder secure() {
            this.secure = true;
            return this;
        }

        public Builder value(String str) {
            if (str == null) {
                throw new NullPointerException("value == null");
            } else if (str.trim().equals(str)) {
                this.value = str;
                return this;
            } else {
                throw new IllegalArgumentException("value is not trimmed");
            }
        }
    }

    private Cookie(String str, String str2, long j, String str3, String str4, boolean z, boolean z2, boolean z3, boolean z4) {
        this.name = str;
        this.value = str2;
        this.expiresAt = j;
        this.domain = str3;
        this.path = str4;
        this.secure = z;
        this.httpOnly = z2;
        this.hostOnly = z3;
        this.persistent = z4;
    }

    Cookie(Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        } else if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        } else if (builder.domain != null) {
            this.name = builder.name;
            this.value = builder.value;
            this.expiresAt = builder.expiresAt;
            this.domain = builder.domain;
            this.path = builder.path;
            this.secure = builder.secure;
            this.httpOnly = builder.httpOnly;
            this.persistent = builder.persistent;
            this.hostOnly = builder.hostOnly;
        } else {
            throw new NullPointerException("builder.domain == null");
        }
    }

    private static int dateCharacterOffset(String str, int i, int i2, boolean z) {
        while (i < i2) {
            char charAt = str.charAt(i);
            if (((charAt < ' ' && charAt != 9) || charAt >= 127 || (charAt >= '0' && charAt <= '9') || ((charAt >= 'a' && charAt <= 'z') || ((charAt >= 'A' && charAt <= 'Z') || charAt == ':'))) == (!z)) {
                return i;
            }
            i++;
        }
        return i2;
    }

    private static boolean domainMatch(HttpUrl httpUrl, String str) {
        String host = httpUrl.host();
        if (!host.equals(str)) {
            return host.endsWith(str) && host.charAt((host.length() - str.length()) + -1) == '.' && !Util.verifyAsIpAddress(host);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x014e, code lost:
        if ((r6 <= okhttp3.internal.http.HttpDate.MAX_DATE) == false) goto L_0x0150;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static okhttp3.Cookie parse(long r20, okhttp3.HttpUrl r22, java.lang.String r23) {
        /*
            int r16 = r23.length()
            r2 = 0
            r3 = 59
            r0 = r23
            r1 = r16
            int r2 = okhttp3.internal.Util.delimiterOffset((java.lang.String) r0, (int) r2, (int) r1, (char) r3)
            r3 = 0
            r4 = 61
            r0 = r23
            int r3 = okhttp3.internal.Util.delimiterOffset((java.lang.String) r0, (int) r3, (int) r2, (char) r4)
            if (r3 == r2) goto L_0x006c
            r4 = 0
            r0 = r23
            java.lang.String r4 = okhttp3.internal.Util.trimSubstring(r0, r4, r3)
            boolean r5 = r4.isEmpty()
            if (r5 != 0) goto L_0x006e
            int r3 = r3 + 1
            r0 = r23
            java.lang.String r5 = okhttp3.internal.Util.trimSubstring(r0, r3, r2)
            r14 = 253402300799999(0xe677d21fdbff, double:1.251973714024093E-309)
            r6 = -1
            r8 = 0
            r3 = 0
            r10 = 0
            r11 = 0
            r12 = 1
            r13 = 0
            int r2 = r2 + 1
        L_0x003e:
            r0 = r16
            if (r2 < r0) goto L_0x0070
            r16 = -9223372036854775808
            int r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x0126
            r6 = -9223372036854775808
        L_0x004a:
            if (r8 == 0) goto L_0x0166
            r0 = r22
            boolean r2 = domainMatch(r0, r8)
            if (r2 == 0) goto L_0x016c
        L_0x0054:
            if (r3 != 0) goto L_0x016e
        L_0x0056:
            java.lang.String r2 = r22.encodedPath()
            r3 = 47
            int r3 = r2.lastIndexOf(r3)
            if (r3 != 0) goto L_0x017a
            java.lang.String r2 = "/"
        L_0x0065:
            r9 = r2
        L_0x0066:
            okhttp3.Cookie r3 = new okhttp3.Cookie
            r3.<init>(r4, r5, r6, r8, r9, r10, r11, r12, r13)
            return r3
        L_0x006c:
            r2 = 0
            return r2
        L_0x006e:
            r2 = 0
            return r2
        L_0x0070:
            r9 = 59
            r0 = r23
            r1 = r16
            int r17 = okhttp3.internal.Util.delimiterOffset((java.lang.String) r0, (int) r2, (int) r1, (char) r9)
            r9 = 61
            r0 = r23
            r1 = r17
            int r9 = okhttp3.internal.Util.delimiterOffset((java.lang.String) r0, (int) r2, (int) r1, (char) r9)
            r0 = r23
            java.lang.String r18 = okhttp3.internal.Util.trimSubstring(r0, r2, r9)
            r0 = r17
            if (r9 < r0) goto L_0x00e1
            java.lang.String r2 = ""
        L_0x0091:
            java.lang.String r9 = "expires"
            r0 = r18
            boolean r9 = r0.equalsIgnoreCase(r9)
            if (r9 != 0) goto L_0x00ec
            java.lang.String r9 = "max-age"
            r0 = r18
            boolean r9 = r0.equalsIgnoreCase(r9)
            if (r9 != 0) goto L_0x00fd
            java.lang.String r9 = "domain"
            r0 = r18
            boolean r9 = r0.equalsIgnoreCase(r9)
            if (r9 != 0) goto L_0x0108
            java.lang.String r9 = "path"
            r0 = r18
            boolean r9 = r0.equalsIgnoreCase(r9)
            if (r9 != 0) goto L_0x0119
            java.lang.String r2 = "secure"
            r0 = r18
            boolean r2 = r0.equalsIgnoreCase(r2)
            if (r2 != 0) goto L_0x011c
            java.lang.String r2 = "httponly"
            r0 = r18
            boolean r2 = r0.equalsIgnoreCase(r2)
            if (r2 != 0) goto L_0x0121
            r2 = r3
            r3 = r8
            r8 = r14
        L_0x00d6:
            int r14 = r17 + 1
            r19 = r2
            r2 = r14
            r14 = r8
            r8 = r3
            r3 = r19
            goto L_0x003e
        L_0x00e1:
            int r2 = r9 + 1
            r0 = r23
            r1 = r17
            java.lang.String r2 = okhttp3.internal.Util.trimSubstring(r0, r2, r1)
            goto L_0x0091
        L_0x00ec:
            int r9 = r2.length()     // Catch:{ IllegalArgumentException -> 0x0181 }
            r18 = 0
            r0 = r18
            long r14 = parseExpires(r2, r0, r9)     // Catch:{ IllegalArgumentException -> 0x0181 }
            r13 = 1
        L_0x00f9:
            r2 = r3
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x00fd:
            long r6 = parseMaxAge(r2)     // Catch:{ NumberFormatException -> 0x0106 }
            r13 = 1
        L_0x0102:
            r2 = r3
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x0106:
            r2 = move-exception
            goto L_0x0102
        L_0x0108:
            java.lang.String r2 = parseDomain(r2)     // Catch:{ IllegalArgumentException -> 0x0114 }
            r12 = 0
            r8 = r14
            r19 = r2
            r2 = r3
            r3 = r19
            goto L_0x00d6
        L_0x0114:
            r2 = move-exception
            r2 = r3
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x0119:
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x011c:
            r10 = 1
            r2 = r3
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x0121:
            r11 = 1
            r2 = r3
            r3 = r8
            r8 = r14
            goto L_0x00d6
        L_0x0126:
            r16 = -1
            int r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x0157
            r14 = 9223372036854775(0x20c49ba5e353f7, double:4.663754807431093E-308)
            int r2 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r2 <= 0) goto L_0x015a
            r2 = 1
        L_0x0136:
            if (r2 != 0) goto L_0x015c
            r14 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 * r14
        L_0x013b:
            long r6 = r6 + r20
            int r2 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r2 >= 0) goto L_0x0162
            r2 = 1
        L_0x0142:
            if (r2 != 0) goto L_0x0150
            r14 = 253402300799999(0xe677d21fdbff, double:1.251973714024093E-309)
            int r2 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r2 > 0) goto L_0x0164
            r2 = 1
        L_0x014e:
            if (r2 != 0) goto L_0x004a
        L_0x0150:
            r6 = 253402300799999(0xe677d21fdbff, double:1.251973714024093E-309)
            goto L_0x004a
        L_0x0157:
            r6 = r14
            goto L_0x004a
        L_0x015a:
            r2 = 0
            goto L_0x0136
        L_0x015c:
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            goto L_0x013b
        L_0x0162:
            r2 = 0
            goto L_0x0142
        L_0x0164:
            r2 = 0
            goto L_0x014e
        L_0x0166:
            java.lang.String r8 = r22.host()
            goto L_0x0054
        L_0x016c:
            r2 = 0
            return r2
        L_0x016e:
            java.lang.String r2 = "/"
            boolean r2 = r3.startsWith(r2)
            if (r2 == 0) goto L_0x0056
            r9 = r3
            goto L_0x0066
        L_0x017a:
            r9 = 0
            java.lang.String r2 = r2.substring(r9, r3)
            goto L_0x0065
        L_0x0181:
            r2 = move-exception
            goto L_0x00f9
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cookie.parse(long, okhttp3.HttpUrl, java.lang.String):okhttp3.Cookie");
    }

    public static Cookie parse(HttpUrl httpUrl, String str) {
        return parse(System.currentTimeMillis(), httpUrl, str);
    }

    public static List<Cookie> parseAll(HttpUrl httpUrl, Headers headers) {
        ArrayList arrayList;
        ArrayList arrayList2 = null;
        List<String> values = headers.values(HttpHeaders.SET_COOKIE);
        int size = values.size();
        int i = 0;
        while (i < size) {
            Cookie parse = parse(httpUrl, values.get(i));
            if (parse != null) {
                arrayList = arrayList2 != null ? arrayList2 : new ArrayList();
                arrayList.add(parse);
            } else {
                arrayList = arrayList2;
            }
            i++;
            arrayList2 = arrayList;
        }
        return arrayList2 == null ? Collections.emptyList() : Collections.unmodifiableList(arrayList2);
    }

    private static String parseDomain(String str) {
        if (!str.endsWith(".")) {
            if (str.startsWith(".")) {
                str = str.substring(1);
            }
            String domainToAscii = Util.domainToAscii(str);
            if (domainToAscii != null) {
                return domainToAscii;
            }
            throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException();
    }

    private static long parseExpires(String str, int i, int i2) {
        int dateCharacterOffset = dateCharacterOffset(str, i, i2, false);
        int i3 = -1;
        int i4 = -1;
        int i5 = -1;
        int i6 = -1;
        int i7 = -1;
        int i8 = -1;
        Matcher matcher = TIME_PATTERN.matcher(str);
        while (dateCharacterOffset < i2) {
            int dateCharacterOffset2 = dateCharacterOffset(str, dateCharacterOffset + 1, i2, true);
            matcher.region(dateCharacterOffset, dateCharacterOffset2);
            if (i3 == -1 && matcher.usePattern(TIME_PATTERN).matches()) {
                i3 = Integer.parseInt(matcher.group(1));
                i4 = Integer.parseInt(matcher.group(2));
                i5 = Integer.parseInt(matcher.group(3));
            } else if (i6 == -1 && matcher.usePattern(DAY_OF_MONTH_PATTERN).matches()) {
                i6 = Integer.parseInt(matcher.group(1));
            } else if (i7 == -1 && matcher.usePattern(MONTH_PATTERN).matches()) {
                i7 = MONTH_PATTERN.pattern().indexOf(matcher.group(1).toLowerCase(Locale.US)) / 4;
            } else if (i8 == -1 && matcher.usePattern(YEAR_PATTERN).matches()) {
                i8 = Integer.parseInt(matcher.group(1));
            }
            dateCharacterOffset = dateCharacterOffset(str, dateCharacterOffset2 + 1, i2, false);
        }
        if (i8 >= 70 && i8 <= 99) {
            i8 += 1900;
        }
        if (i8 >= 0 && i8 <= 69) {
            i8 += 2000;
        }
        if (i8 < 1601) {
            throw new IllegalArgumentException();
        } else if (i7 == -1) {
            throw new IllegalArgumentException();
        } else if (i6 < 1 || i6 > 31) {
            throw new IllegalArgumentException();
        } else if (i3 < 0 || i3 > 23) {
            throw new IllegalArgumentException();
        } else if (i4 < 0 || i4 > 59) {
            throw new IllegalArgumentException();
        } else if (i5 >= 0 && i5 <= 59) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(Util.UTC);
            gregorianCalendar.setLenient(false);
            gregorianCalendar.set(1, i8);
            gregorianCalendar.set(2, i7 - 1);
            gregorianCalendar.set(5, i6);
            gregorianCalendar.set(11, i3);
            gregorianCalendar.set(12, i4);
            gregorianCalendar.set(13, i5);
            gregorianCalendar.set(14, 0);
            return gregorianCalendar.getTimeInMillis();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static long parseMaxAge(String str) {
        boolean z = false;
        try {
            long parseLong = Long.parseLong(str);
            if (parseLong > 0) {
                z = true;
            }
            if (!z) {
                return Long.MIN_VALUE;
            }
            return parseLong;
        } catch (NumberFormatException e) {
            if (str.matches("-?\\d+")) {
                return !str.startsWith("-") ? Long.MAX_VALUE : Long.MIN_VALUE;
            }
            throw e;
        }
    }

    private static boolean pathMatch(HttpUrl httpUrl, String str) {
        String encodedPath = httpUrl.encodedPath();
        if (!encodedPath.equals(str)) {
            return encodedPath.startsWith(str) && (str.endsWith("/") || encodedPath.charAt(str.length()) == '/');
        }
        return true;
    }

    public String domain() {
        return this.domain;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Cookie)) {
            return false;
        }
        Cookie cookie = (Cookie) obj;
        return cookie.name.equals(this.name) && cookie.value.equals(this.value) && cookie.domain.equals(this.domain) && cookie.path.equals(this.path) && cookie.expiresAt == this.expiresAt && cookie.secure == this.secure && cookie.httpOnly == this.httpOnly && cookie.persistent == this.persistent && cookie.hostOnly == this.hostOnly;
    }

    public long expiresAt() {
        return this.expiresAt;
    }

    public int hashCode() {
        int i = 1;
        int hashCode = ((!this.persistent ? 1 : 0) + (((!this.httpOnly ? 1 : 0) + (((!this.secure ? 1 : 0) + ((((((((((this.name.hashCode() + 527) * 31) + this.value.hashCode()) * 31) + this.domain.hashCode()) * 31) + this.path.hashCode()) * 31) + ((int) (this.expiresAt ^ (this.expiresAt >>> 32)))) * 31)) * 31)) * 31)) * 31;
        if (this.hostOnly) {
            i = 0;
        }
        return hashCode + i;
    }

    public boolean hostOnly() {
        return this.hostOnly;
    }

    public boolean httpOnly() {
        return this.httpOnly;
    }

    public boolean matches(HttpUrl httpUrl) {
        if (!(!this.hostOnly ? domainMatch(httpUrl, this.domain) : httpUrl.host().equals(this.domain)) || !pathMatch(httpUrl, this.path)) {
            return false;
        }
        return !this.secure || httpUrl.isHttps();
    }

    public String name() {
        return this.name;
    }

    public String path() {
        return this.path;
    }

    public boolean persistent() {
        return this.persistent;
    }

    public boolean secure() {
        return this.secure;
    }

    public String toString() {
        return toString(false);
    }

    /* access modifiers changed from: package-private */
    public String toString(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append('=');
        sb.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                sb.append("; max-age=0");
            } else {
                sb.append("; expires=").append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            sb.append("; domain=");
            if (z) {
                sb.append(".");
            }
            sb.append(this.domain);
        }
        sb.append("; path=").append(this.path);
        if (this.secure) {
            sb.append("; secure");
        }
        if (this.httpOnly) {
            sb.append("; httponly");
        }
        return sb.toString();
    }

    public String value() {
        return this.value;
    }
}
