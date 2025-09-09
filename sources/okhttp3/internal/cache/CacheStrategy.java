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

public final class CacheStrategy {
    public final Response cacheResponse;
    public final Request networkRequest;

    public static class Factory {
        private int ageSeconds = -1;
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

        public Factory(long j, Request request2, Response response) {
            this.nowMillis = j;
            this.request = request2;
            this.cacheResponse = response;
            if (response != null) {
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
        }

        private long cacheResponseAge() {
            long j = 0;
            if (this.servedDate != null) {
                j = Math.max(0, this.receivedResponseMillis - this.servedDate.getTime());
            }
            if (this.ageSeconds != -1) {
                j = Math.max(j, TimeUnit.SECONDS.toMillis((long) this.ageSeconds));
            }
            return j + (this.receivedResponseMillis - this.sentRequestMillis) + (this.nowMillis - this.receivedResponseMillis);
        }

        private long computeFreshnessLifetime() {
            boolean z = true;
            CacheControl cacheControl = this.cacheResponse.cacheControl();
            if (cacheControl.maxAgeSeconds() != -1) {
                return TimeUnit.SECONDS.toMillis((long) cacheControl.maxAgeSeconds());
            }
            if (this.expires != null) {
                long time = this.expires.getTime() - (this.servedDate == null ? this.receivedResponseMillis : this.servedDate.getTime());
                if (time > 0) {
                    z = false;
                }
                if (!z) {
                    return time;
                }
                return 0;
            } else if (this.lastModified == null || this.cacheResponse.request().url().query() != null) {
                return 0;
            } else {
                long time2 = (this.servedDate == null ? this.sentRequestMillis : this.servedDate.getTime()) - this.lastModified.getTime();
                if (!(time2 <= 0)) {
                    return time2 / 10;
                }
                return 0;
            }
        }

        private CacheStrategy getCandidate() {
            String str;
            String str2;
            if (this.cacheResponse == null) {
                return new CacheStrategy(this.request, (Response) null);
            }
            if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
                return new CacheStrategy(this.request, (Response) null);
            }
            if (!CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                return new CacheStrategy(this.request, (Response) null);
            }
            CacheControl cacheControl = this.request.cacheControl();
            if (cacheControl.noCache() || hasConditions(this.request)) {
                return new CacheStrategy(this.request, (Response) null);
            }
            long cacheResponseAge = cacheResponseAge();
            long computeFreshnessLifetime = computeFreshnessLifetime();
            if (cacheControl.maxAgeSeconds() != -1) {
                computeFreshnessLifetime = Math.min(computeFreshnessLifetime, TimeUnit.SECONDS.toMillis((long) cacheControl.maxAgeSeconds()));
            }
            long j = 0;
            if (cacheControl.minFreshSeconds() != -1) {
                j = TimeUnit.SECONDS.toMillis((long) cacheControl.minFreshSeconds());
            }
            long j2 = 0;
            CacheControl cacheControl2 = this.cacheResponse.cacheControl();
            if (!cacheControl2.mustRevalidate() && cacheControl.maxStaleSeconds() != -1) {
                j2 = TimeUnit.SECONDS.toMillis((long) cacheControl.maxStaleSeconds());
            }
            if (!cacheControl2.noCache()) {
                if (!(cacheResponseAge + j >= j2 + computeFreshnessLifetime)) {
                    Response.Builder newBuilder = this.cacheResponse.newBuilder();
                    if (!(j + cacheResponseAge < computeFreshnessLifetime)) {
                        newBuilder.addHeader(HttpHeaders.WARNING, "110 HttpURLConnection \"Response is stale\"");
                    }
                    if (!(cacheResponseAge <= 86400000) && isFreshnessLifetimeHeuristic()) {
                        newBuilder.addHeader(HttpHeaders.WARNING, "113 HttpURLConnection \"Heuristic expiration\"");
                    }
                    return new CacheStrategy((Request) null, newBuilder.build());
                }
            }
            if (this.etag != null) {
                str = HttpHeaders.IF_NONE_MATCH;
                str2 = this.etag;
            } else if (this.lastModified != null) {
                str = HttpHeaders.IF_MODIFIED_SINCE;
                str2 = this.lastModifiedString;
            } else if (this.servedDate == null) {
                return new CacheStrategy(this.request, (Response) null);
            } else {
                str = HttpHeaders.IF_MODIFIED_SINCE;
                str2 = this.servedDateString;
            }
            Headers.Builder newBuilder2 = this.request.headers().newBuilder();
            Internal.instance.addLenient(newBuilder2, str, str2);
            return new CacheStrategy(this.request.newBuilder().headers(newBuilder2.build()).build(), this.cacheResponse);
        }

        private static boolean hasConditions(Request request2) {
            return (request2.header(HttpHeaders.IF_MODIFIED_SINCE) == null && request2.header(HttpHeaders.IF_NONE_MATCH) == null) ? false : true;
        }

        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }

        public CacheStrategy get() {
            CacheStrategy candidate = getCandidate();
            return (candidate.networkRequest != null && this.request.cacheControl().onlyIfCached()) ? new CacheStrategy((Request) null, (Response) null) : candidate;
        }
    }

    CacheStrategy(Request request, Response response) {
        this.networkRequest = request;
        this.cacheResponse = response;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003a, code lost:
        if (r3.cacheControl().isPrivate() != false) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0045, code lost:
        if (r4.cacheControl().noStore() != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0047, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if (r3.cacheControl().noStore() == false) goto L_0x003d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isCacheable(okhttp3.Response r3, okhttp3.Request r4) {
        /*
            r0 = 0
            int r1 = r3.code()
            switch(r1) {
                case 200: goto L_0x0012;
                case 203: goto L_0x0012;
                case 204: goto L_0x0012;
                case 300: goto L_0x0012;
                case 301: goto L_0x0012;
                case 302: goto L_0x0009;
                case 307: goto L_0x0009;
                case 308: goto L_0x0012;
                case 404: goto L_0x0012;
                case 405: goto L_0x0012;
                case 410: goto L_0x0012;
                case 414: goto L_0x0012;
                case 501: goto L_0x0012;
                default: goto L_0x0008;
            }
        L_0x0008:
            return r0
        L_0x0009:
            java.lang.String r1 = "Expires"
            java.lang.String r1 = r3.header(r1)
            if (r1 == 0) goto L_0x001d
        L_0x0012:
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.noStore()
            if (r1 == 0) goto L_0x003d
        L_0x001c:
            return r0
        L_0x001d:
            okhttp3.CacheControl r1 = r3.cacheControl()
            int r1 = r1.maxAgeSeconds()
            r2 = -1
            if (r1 != r2) goto L_0x0012
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.isPublic()
            if (r1 != 0) goto L_0x0012
            okhttp3.CacheControl r1 = r3.cacheControl()
            boolean r1 = r1.isPrivate()
            if (r1 != 0) goto L_0x0012
            goto L_0x0008
        L_0x003d:
            okhttp3.CacheControl r1 = r4.cacheControl()
            boolean r1 = r1.noStore()
            if (r1 != 0) goto L_0x001c
            r0 = 1
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.CacheStrategy.isCacheable(okhttp3.Response, okhttp3.Request):boolean");
    }
}
