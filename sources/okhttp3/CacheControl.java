package okhttp3;

import java.util.concurrent.TimeUnit;

public final class CacheControl {
    public static final CacheControl FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
    public static final CacheControl FORCE_NETWORK = new Builder().noCache().build();
    String headerValue;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    public static final class Builder {
        int maxAgeSeconds = -1;
        int maxStaleSeconds = -1;
        int minFreshSeconds = -1;
        boolean noCache;
        boolean noStore;
        boolean noTransform;
        boolean onlyIfCached;

        public CacheControl build() {
            return new CacheControl(this);
        }

        public Builder maxAge(int i, TimeUnit timeUnit) {
            boolean z = false;
            if (i >= 0) {
                long seconds = timeUnit.toSeconds((long) i);
                if (seconds <= 2147483647L) {
                    z = true;
                }
                this.maxAgeSeconds = !z ? Integer.MAX_VALUE : (int) seconds;
                return this;
            }
            throw new IllegalArgumentException("maxAge < 0: " + i);
        }

        public Builder maxStale(int i, TimeUnit timeUnit) {
            boolean z = false;
            if (i >= 0) {
                long seconds = timeUnit.toSeconds((long) i);
                if (seconds <= 2147483647L) {
                    z = true;
                }
                this.maxStaleSeconds = !z ? Integer.MAX_VALUE : (int) seconds;
                return this;
            }
            throw new IllegalArgumentException("maxStale < 0: " + i);
        }

        public Builder minFresh(int i, TimeUnit timeUnit) {
            boolean z = false;
            if (i >= 0) {
                long seconds = timeUnit.toSeconds((long) i);
                if (seconds <= 2147483647L) {
                    z = true;
                }
                this.minFreshSeconds = !z ? Integer.MAX_VALUE : (int) seconds;
                return this;
            }
            throw new IllegalArgumentException("minFresh < 0: " + i);
        }

        public Builder noCache() {
            this.noCache = true;
            return this;
        }

        public Builder noStore() {
            this.noStore = true;
            return this;
        }

        public Builder noTransform() {
            this.noTransform = true;
            return this;
        }

        public Builder onlyIfCached() {
            this.onlyIfCached = true;
            return this;
        }
    }

    CacheControl(Builder builder) {
        this.noCache = builder.noCache;
        this.noStore = builder.noStore;
        this.maxAgeSeconds = builder.maxAgeSeconds;
        this.sMaxAgeSeconds = -1;
        this.isPrivate = false;
        this.isPublic = false;
        this.mustRevalidate = false;
        this.maxStaleSeconds = builder.maxStaleSeconds;
        this.minFreshSeconds = builder.minFreshSeconds;
        this.onlyIfCached = builder.onlyIfCached;
        this.noTransform = builder.noTransform;
    }

    private CacheControl(boolean z, boolean z2, int i, int i2, boolean z3, boolean z4, boolean z5, int i3, int i4, boolean z6, boolean z7, String str) {
        this.noCache = z;
        this.noStore = z2;
        this.maxAgeSeconds = i;
        this.sMaxAgeSeconds = i2;
        this.isPrivate = z3;
        this.isPublic = z4;
        this.mustRevalidate = z5;
        this.maxStaleSeconds = i3;
        this.minFreshSeconds = i4;
        this.onlyIfCached = z6;
        this.noTransform = z7;
        this.headerValue = str;
    }

    private String headerValue() {
        StringBuilder sb = new StringBuilder();
        if (this.noCache) {
            sb.append("no-cache, ");
        }
        if (this.noStore) {
            sb.append("no-store, ");
        }
        if (this.maxAgeSeconds != -1) {
            sb.append("max-age=").append(this.maxAgeSeconds).append(", ");
        }
        if (this.sMaxAgeSeconds != -1) {
            sb.append("s-maxage=").append(this.sMaxAgeSeconds).append(", ");
        }
        if (this.isPrivate) {
            sb.append("private, ");
        }
        if (this.isPublic) {
            sb.append("public, ");
        }
        if (this.mustRevalidate) {
            sb.append("must-revalidate, ");
        }
        if (this.maxStaleSeconds != -1) {
            sb.append("max-stale=").append(this.maxStaleSeconds).append(", ");
        }
        if (this.minFreshSeconds != -1) {
            sb.append("min-fresh=").append(this.minFreshSeconds).append(", ");
        }
        if (this.onlyIfCached) {
            sb.append("only-if-cached, ");
        }
        if (this.noTransform) {
            sb.append("no-transform, ");
        }
        if (sb.length() == 0) {
            return "";
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0066  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static okhttp3.CacheControl parse(okhttp3.Headers r23) {
        /*
            r14 = 0
            r4 = 0
            r5 = -1
            r6 = -1
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = -1
            r11 = -1
            r12 = 0
            r13 = 0
            r15 = 1
            r3 = 0
            r2 = 0
            int r19 = r23.size()
            r18 = r2
            r2 = r3
            r3 = r14
        L_0x0016:
            r0 = r18
            r1 = r19
            if (r0 < r1) goto L_0x0025
            if (r15 == 0) goto L_0x01bd
            r14 = r2
        L_0x001f:
            okhttp3.CacheControl r2 = new okhttp3.CacheControl
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            return r2
        L_0x0025:
            r0 = r23
            r1 = r18
            java.lang.String r16 = r0.name(r1)
            r0 = r23
            r1 = r18
            java.lang.String r14 = r0.value(r1)
            java.lang.String r17 = "Cache-Control"
            boolean r17 = r16.equalsIgnoreCase(r17)
            if (r17 != 0) goto L_0x004c
            java.lang.String r17 = "Pragma"
            boolean r16 = r16.equalsIgnoreCase(r17)
            if (r16 != 0) goto L_0x0064
        L_0x0047:
            int r14 = r18 + 1
            r18 = r14
            goto L_0x0016
        L_0x004c:
            if (r2 != 0) goto L_0x0062
            r2 = r14
        L_0x004f:
            r16 = 0
            r22 = r16
            r16 = r3
            r3 = r22
        L_0x0057:
            int r17 = r14.length()
            r0 = r17
            if (r3 < r0) goto L_0x0066
            r3 = r16
            goto L_0x0047
        L_0x0062:
            r15 = 0
            goto L_0x004f
        L_0x0064:
            r15 = 0
            goto L_0x004f
        L_0x0066:
            java.lang.String r17 = "=,;"
            r0 = r17
            int r17 = okhttp3.internal.http.HttpHeaders.skipUntil(r14, r3, r0)
            r0 = r17
            java.lang.String r3 = r14.substring(r3, r0)
            java.lang.String r20 = r3.trim()
            int r3 = r14.length()
            r0 = r17
            if (r0 != r3) goto L_0x011c
        L_0x0081:
            int r17 = r17 + 1
            r3 = 0
            r22 = r3
            r3 = r17
            r17 = r22
        L_0x008a:
            java.lang.String r21 = "no-cache"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x0184
            java.lang.String r21 = "no-store"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x0188
            java.lang.String r21 = "max-age"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x018b
            java.lang.String r21 = "s-maxage"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x0194
            java.lang.String r21 = "private"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x019d
            java.lang.String r21 = "public"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x01a0
            java.lang.String r21 = "must-revalidate"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x01a3
            java.lang.String r21 = "max-stale"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x01a6
            java.lang.String r21 = "min-fresh"
            r0 = r21
            r1 = r20
            boolean r21 = r0.equalsIgnoreCase(r1)
            if (r21 != 0) goto L_0x01b1
            java.lang.String r17 = "only-if-cached"
            r0 = r17
            r1 = r20
            boolean r17 = r0.equalsIgnoreCase(r1)
            if (r17 != 0) goto L_0x01ba
            java.lang.String r17 = "no-transform"
            r0 = r17
            r1 = r20
            boolean r17 = r0.equalsIgnoreCase(r1)
            if (r17 == 0) goto L_0x0057
            r13 = 1
            goto L_0x0057
        L_0x011c:
            r0 = r17
            char r3 = r14.charAt(r0)
            r21 = 44
            r0 = r21
            if (r3 == r0) goto L_0x0081
            r0 = r17
            char r3 = r14.charAt(r0)
            r21 = 59
            r0 = r21
            if (r3 == r0) goto L_0x0081
            int r3 = r17 + 1
            int r3 = okhttp3.internal.http.HttpHeaders.skipWhitespace(r14, r3)
            int r17 = r14.length()
            r0 = r17
            if (r3 < r0) goto L_0x015d
        L_0x0142:
            java.lang.String r17 = ",;"
            r0 = r17
            int r17 = okhttp3.internal.http.HttpHeaders.skipUntil(r14, r3, r0)
            r0 = r17
            java.lang.String r3 = r14.substring(r3, r0)
            java.lang.String r3 = r3.trim()
            r22 = r3
            r3 = r17
            r17 = r22
            goto L_0x008a
        L_0x015d:
            char r17 = r14.charAt(r3)
            r21 = 34
            r0 = r17
            r1 = r21
            if (r0 != r1) goto L_0x0142
            int r3 = r3 + 1
            java.lang.String r17 = "\""
            r0 = r17
            int r17 = okhttp3.internal.http.HttpHeaders.skipUntil(r14, r3, r0)
            r0 = r17
            java.lang.String r3 = r14.substring(r3, r0)
            int r17 = r17 + 1
            r22 = r3
            r3 = r17
            r17 = r22
            goto L_0x008a
        L_0x0184:
            r16 = 1
            goto L_0x0057
        L_0x0188:
            r4 = 1
            goto L_0x0057
        L_0x018b:
            r5 = -1
            r0 = r17
            int r5 = okhttp3.internal.http.HttpHeaders.parseSeconds(r0, r5)
            goto L_0x0057
        L_0x0194:
            r6 = -1
            r0 = r17
            int r6 = okhttp3.internal.http.HttpHeaders.parseSeconds(r0, r6)
            goto L_0x0057
        L_0x019d:
            r7 = 1
            goto L_0x0057
        L_0x01a0:
            r8 = 1
            goto L_0x0057
        L_0x01a3:
            r9 = 1
            goto L_0x0057
        L_0x01a6:
            r10 = 2147483647(0x7fffffff, float:NaN)
            r0 = r17
            int r10 = okhttp3.internal.http.HttpHeaders.parseSeconds(r0, r10)
            goto L_0x0057
        L_0x01b1:
            r11 = -1
            r0 = r17
            int r11 = okhttp3.internal.http.HttpHeaders.parseSeconds(r0, r11)
            goto L_0x0057
        L_0x01ba:
            r12 = 1
            goto L_0x0057
        L_0x01bd:
            r14 = 0
            goto L_0x001f
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.CacheControl.parse(okhttp3.Headers):okhttp3.CacheControl");
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public boolean noCache() {
        return this.noCache;
    }

    public boolean noStore() {
        return this.noStore;
    }

    public boolean noTransform() {
        return this.noTransform;
    }

    public boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    public String toString() {
        String str = this.headerValue;
        if (str != null) {
            return str;
        }
        String headerValue2 = headerValue();
        this.headerValue = headerValue2;
        return headerValue2;
    }
}
