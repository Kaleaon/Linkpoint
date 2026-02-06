package okhttp3.internal.cache;

import com.google.common.net.HttpHeaders;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpDate;
/* loaded from: classes.dex */
public final class CacheStrategy {
    public final Response cacheResponse;
    public final Request networkRequest;

    /* loaded from: classes.dex */
    public static class Factory {
        private int ageSeconds;
        final Response cacheResponse;
        private String etag;
        private Date expires;
        private Date lastModified;
        private String lastModifiedString;
        final long nowMillis;
        private long receivedResponseMillis;
        final Request request;
        private long sentRequestMillis;
        private Date servedDate;
        private String servedDateString;

        public Factory(long j, Request request, Response response) {
            this.ageSeconds = -1;
            this.nowMillis = j;
            this.request = request;
            this.cacheResponse = response;
            if (response == null) {
                return;
            }
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
            Headers headers = response.headers();
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                String name = headers.name(i);
                String value = headers.value(i);
                if (HttpHeaders.DATE.equalsIgnoreCase(name)) {
                    this.servedDate = HttpDate.parse(value);
                    this.servedDateString = value;
                } else if (HttpHeaders.EXPIRES.equalsIgnoreCase(name)) {
                    this.expires = HttpDate.parse(value);
                } else if (HttpHeaders.LAST_MODIFIED.equalsIgnoreCase(name)) {
                    this.lastModified = HttpDate.parse(value);
                    this.lastModifiedString = value;
                } else if (HttpHeaders.ETAG.equalsIgnoreCase(name)) {
                    this.etag = value;
                } else if (HttpHeaders.AGE.equalsIgnoreCase(name)) {
                    this.ageSeconds = okhttp3.internal.http.HttpHeaders.parseSeconds(value, -1);
                }
            }
        }

        private long cacheResponseAge() {
            long max = this.servedDate != null ? Math.max(0L, this.receivedResponseMillis - this.servedDate.getTime()) : 0L;
            if (this.ageSeconds != -1) {
                max = Math.max(max, TimeUnit.SECONDS.toMillis(this.ageSeconds));
            }
            return max + (this.receivedResponseMillis - this.sentRequestMillis) + (this.nowMillis - this.receivedResponseMillis);
        }

        private long computeFreshnessLifetime() {
            CacheControl cacheControl = this.cacheResponse.cacheControl();
            if (cacheControl.maxAgeSeconds() == -1) {
                if (this.expires != null) {
                    long time = this.expires.getTime() - (this.servedDate == null ? this.receivedResponseMillis : this.servedDate.getTime());
                    if (time <= 0) {
                        return 0L;
                    }
                    return time;
                } else if (this.lastModified != null && this.cacheResponse.request().url().query() == null) {
                    long time2 = (this.servedDate == null ? this.sentRequestMillis : this.servedDate.getTime()) - this.lastModified.getTime();
                    if (time2 <= 0) {
                        return 0L;
                    }
                    return time2 / 10;
                } else {
                    return 0L;
                }
            }
            return TimeUnit.SECONDS.toMillis(cacheControl.maxAgeSeconds());
        }

        private CacheStrategy getCandidate() {
            String str;
            String str2;
            if (this.cacheResponse != null) {
                if ((!this.request.isHttps() || this.cacheResponse.handshake() != null) && CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                    CacheControl cacheControl = this.request.cacheControl();
                    if (cacheControl.noCache() || hasConditions(this.request)) {
                        return new CacheStrategy(this.request, null);
                    }
                    long cacheResponseAge = cacheResponseAge();
                    long computeFreshnessLifetime = computeFreshnessLifetime();
                    if (cacheControl.maxAgeSeconds() != -1) {
                        computeFreshnessLifetime = Math.min(computeFreshnessLifetime, TimeUnit.SECONDS.toMillis(cacheControl.maxAgeSeconds()));
                    }
                    long millis = cacheControl.minFreshSeconds() != -1 ? TimeUnit.SECONDS.toMillis(cacheControl.minFreshSeconds()) : 0L;
                    long j = 0;
                    CacheControl cacheControl2 = this.cacheResponse.cacheControl();
                    if (!cacheControl2.mustRevalidate() && cacheControl.maxStaleSeconds() != -1) {
                        j = TimeUnit.SECONDS.toMillis(cacheControl.maxStaleSeconds());
                    }
                    if (!cacheControl2.noCache()) {
                        if (!(cacheResponseAge + millis >= j + computeFreshnessLifetime)) {
                            Response.Builder newBuilder = this.cacheResponse.newBuilder();
                            if (!(millis + cacheResponseAge < computeFreshnessLifetime)) {
                                newBuilder.addHeader(HttpHeaders.WARNING, "110 HttpURLConnection \"Response is stale\"");
                            }
                            if (!(cacheResponseAge <= 86400000) && isFreshnessLifetimeHeuristic()) {
                                newBuilder.addHeader(HttpHeaders.WARNING, "113 HttpURLConnection \"Heuristic expiration\"");
                            }
                            return new CacheStrategy(null, newBuilder.build());
                        }
                    }
                    if (this.etag != null) {
                        str = HttpHeaders.IF_NONE_MATCH;
                        str2 = this.etag;
                    } else if (this.lastModified != null) {
                        str = HttpHeaders.IF_MODIFIED_SINCE;
                        str2 = this.lastModifiedString;
                    } else if (this.servedDate == null) {
                        return new CacheStrategy(this.request, null);
                    } else {
                        str = HttpHeaders.IF_MODIFIED_SINCE;
                        str2 = this.servedDateString;
                    }
                    Headers.Builder newBuilder2 = this.request.headers().newBuilder();
                    Internal.instance.addLenient(newBuilder2, str, str2);
                    return new CacheStrategy(this.request.newBuilder().headers(newBuilder2.build()).build(), this.cacheResponse);
                }
                return new CacheStrategy(this.request, null);
            }
            return new CacheStrategy(this.request, null);
        }

        private static boolean hasConditions(Request request) {
            return (request.header(HttpHeaders.IF_MODIFIED_SINCE) == null && request.header(HttpHeaders.IF_NONE_MATCH) == null) ? false : true;
        }

        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }

        public CacheStrategy get() {
            CacheStrategy candidate = getCandidate();
            return (candidate.networkRequest != null && this.request.cacheControl().onlyIfCached()) ? new CacheStrategy(null, null) : candidate;
        }
    }

    CacheStrategy(Request request, Response response) {
        this.networkRequest = request;
        this.cacheResponse = response;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x003a, code lost:
        if (r3.cacheControl().isPrivate() != false) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isCacheable(okhttp3.Response r3, okhttp3.Request r4) {
        /*
            r0 = 0
            int r1 = r3.code()
            switch(r1) {
                case 200: goto L12;
                case 203: goto L12;
                case 204: goto L12;
                case 300: goto L12;
                case 301: goto L12;
                case 302: goto L9;
                case 307: goto L9;
                case 308: goto L12;
                case 404: goto L12;
                case 405: goto L12;
                case 410: goto L12;
                case 414: goto L12;
                case 501: goto L12;
                default: goto L8;
            }
        L8:
            return r0
        L9:
            java.lang.String r1 = "Expires"
            java.lang.String r1 = r3.header(r1)
            if (r1 == 0) goto L1d
        L12:
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.noStore()
            if (r1 == 0) goto L3d
        L1c:
            return r0
        L1d:
            okhttp3.CacheControl r1 = r3.cacheControl()
            int r1 = r1.maxAgeSeconds()
            r2 = -1
            if (r1 != r2) goto L12
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.isPublic()
            if (r1 != 0) goto L12
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.isPrivate()
            if (r1 != 0) goto L12
            goto L8
        L3d:
            okhttp3.CacheControl r1 = r4.cacheControl()
            boolean r1 = r1.noStore()
            if (r1 != 0) goto L1c
            r0 = 1
            goto L1c
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.CacheStrategy.isCacheable(okhttp3.Response, okhttp3.Request):boolean");
    }
}
