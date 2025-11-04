// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache;

import okhttp3.internal.Internal;
import okhttp3.CacheControl;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;
import java.util.Date;
import okhttp3.Request;
import okhttp3.Response;

public final class CacheStrategy
{
    public final Response cacheResponse;
    public final Request networkRequest;
    
    CacheStrategy(final Request networkRequest, final Response cacheResponse) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
    }
    
    public static boolean isCacheable(final Response response, final Request request) {
        boolean b = false;
        switch (response.code()) {
            case 302:
            case 307: {
                if (response.header("Expires") == null && response.cacheControl().maxAgeSeconds() == -1 && !response.cacheControl().isPublic() && !response.cacheControl().isPrivate()) {
                    break;
                }
            }
            case 200:
            case 203:
            case 204:
            case 300:
            case 301:
            case 308:
            case 404:
            case 405:
            case 410:
            case 414:
            case 501: {
                if (!response.cacheControl().noStore() && !request.cacheControl().noStore()) {
                    b = true;
                }
                return b;
            }
        }
        return false;
    }
    
    public static class Factory
    {
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
        
        public Factory(final long nowMillis, final Request request, final Response cacheResponse) {
            int i = 0;
            this.ageSeconds = -1;
            this.nowMillis = nowMillis;
            this.request = request;
            this.cacheResponse = cacheResponse;
            if (cacheResponse != null) {
                this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
                this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
                for (Headers headers = cacheResponse.headers(); i < headers.size(); ++i) {
                    final String name = headers.name(i);
                    final String value = headers.value(i);
                    if (!"Date".equalsIgnoreCase(name)) {
                        if (!"Expires".equalsIgnoreCase(name)) {
                            if (!"Last-Modified".equalsIgnoreCase(name)) {
                                if (!"ETag".equalsIgnoreCase(name)) {
                                    if ("Age".equalsIgnoreCase(name)) {
                                        this.ageSeconds = HttpHeaders.parseSeconds(value, -1);
                                    }
                                }
                                else {
                                    this.etag = value;
                                }
                            }
                            else {
                                this.lastModified = HttpDate.parse(value);
                                this.lastModifiedString = value;
                            }
                        }
                        else {
                            this.expires = HttpDate.parse(value);
                        }
                    }
                    else {
                        this.servedDate = HttpDate.parse(value);
                        this.servedDateString = value;
                    }
                }
            }
        }
        
        private long cacheResponseAge() {
            long a = 0L;
            if (this.servedDate != null) {
                a = Math.max(0L, this.receivedResponseMillis - this.servedDate.getTime());
            }
            if (this.ageSeconds != -1) {
                a = Math.max(a, TimeUnit.SECONDS.toMillis(this.ageSeconds));
            }
            return a + (this.receivedResponseMillis - this.sentRequestMillis) + (this.nowMillis - this.receivedResponseMillis);
        }
        
        private long computeFreshnessLifetime() {
            int n = 1;
            final long n2 = 0L;
            final CacheControl cacheControl = this.cacheResponse.cacheControl();
            if (cacheControl.maxAgeSeconds() != -1) {
                return TimeUnit.SECONDS.toMillis(cacheControl.maxAgeSeconds());
            }
            if (this.expires != null) {
                long n3;
                if (this.servedDate == null) {
                    n3 = this.receivedResponseMillis;
                }
                else {
                    n3 = this.servedDate.getTime();
                }
                long n4 = this.expires.getTime() - n3;
                if (n4 > 0L) {
                    n = 0;
                }
                if (n != 0) {
                    n4 = 0L;
                }
                return n4;
            }
            if (this.lastModified != null && this.cacheResponse.request().url().query() == null) {
                long n5;
                if (this.servedDate == null) {
                    n5 = this.sentRequestMillis;
                }
                else {
                    n5 = this.servedDate.getTime();
                }
                final long n6 = n5 - this.lastModified.getTime();
                int n7;
                if (n6 <= 0L) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                long n8 = n2;
                if (n7 == 0) {
                    n8 = n6 / 10L;
                }
                return n8;
            }
            return 0L;
        }
        
        private CacheStrategy getCandidate() {
            if (this.cacheResponse == null) {
                return new CacheStrategy(this.request, null);
            }
            if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
                return new CacheStrategy(this.request, null);
            }
            if (!CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                return new CacheStrategy(this.request, null);
            }
            final CacheControl cacheControl = this.request.cacheControl();
            if (!cacheControl.noCache() && !hasConditions(this.request)) {
                final long cacheResponseAge = this.cacheResponseAge();
                long a = this.computeFreshnessLifetime();
                if (cacheControl.maxAgeSeconds() != -1) {
                    a = Math.min(a, TimeUnit.SECONDS.toMillis(cacheControl.maxAgeSeconds()));
                }
                long millis = 0L;
                if (cacheControl.minFreshSeconds() != -1) {
                    millis = TimeUnit.SECONDS.toMillis(cacheControl.minFreshSeconds());
                }
                long millis2 = 0L;
                final CacheControl cacheControl2 = this.cacheResponse.cacheControl();
                if (!cacheControl2.mustRevalidate() && cacheControl.maxStaleSeconds() != -1) {
                    millis2 = TimeUnit.SECONDS.toMillis(cacheControl.maxStaleSeconds());
                }
                if (!cacheControl2.noCache()) {
                    int n;
                    if (cacheResponseAge + millis >= millis2 + a) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    if (n == 0) {
                        final Response.Builder builder = this.cacheResponse.newBuilder();
                        int n2;
                        if (millis + cacheResponseAge < a) {
                            n2 = 1;
                        }
                        else {
                            n2 = 0;
                        }
                        if (n2 == 0) {
                            builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                        }
                        boolean b;
                        if (cacheResponseAge <= 86400000L) {
                            b = true;
                        }
                        else {
                            b = false;
                        }
                        if (!b && this.isFreshnessLifetimeHeuristic()) {
                            builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                        }
                        return new CacheStrategy(null, builder.build());
                    }
                }
                String s;
                String s2;
                if (this.etag == null) {
                    if (this.lastModified == null) {
                        if (this.servedDate == null) {
                            return new CacheStrategy(this.request, null);
                        }
                        s = "If-Modified-Since";
                        s2 = this.servedDateString;
                    }
                    else {
                        s = "If-Modified-Since";
                        s2 = this.lastModifiedString;
                    }
                }
                else {
                    s = "If-None-Match";
                    s2 = this.etag;
                }
                final Headers.Builder builder2 = this.request.headers().newBuilder();
                Internal.instance.addLenient(builder2, s, s2);
                return new CacheStrategy(this.request.newBuilder().headers(builder2.build()).build(), this.cacheResponse);
            }
            return new CacheStrategy(this.request, null);
        }
        
        private static boolean hasConditions(final Request request) {
            return request.header("If-Modified-Since") != null || request.header("If-None-Match") != null;
        }
        
        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }
        
        public CacheStrategy get() {
            final CacheStrategy candidate = this.getCandidate();
            if (candidate.networkRequest != null && this.request.cacheControl().onlyIfCached()) {
                return new CacheStrategy(null, null);
            }
            return candidate;
        }
    }
}
