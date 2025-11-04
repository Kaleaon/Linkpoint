// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.http.HttpHeaders;
import java.util.concurrent.TimeUnit;

public final class CacheControl
{
    public static final CacheControl FORCE_CACHE;
    public static final CacheControl FORCE_NETWORK;
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
    
    static {
        FORCE_NETWORK = new Builder().noCache().build();
        FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
    }
    
    CacheControl(final Builder builder) {
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
    
    private CacheControl(final boolean noCache, final boolean noStore, final int maxAgeSeconds, final int sMaxAgeSeconds, final boolean isPrivate, final boolean isPublic, final boolean mustRevalidate, final int maxStaleSeconds, final int minFreshSeconds, final boolean onlyIfCached, final boolean noTransform, final String headerValue) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.onlyIfCached = onlyIfCached;
        this.noTransform = noTransform;
        this.headerValue = headerValue;
    }
    
    private String headerValue() {
        final StringBuilder sb = new StringBuilder();
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
        if (sb.length() != 0) {
            sb.delete(sb.length() - 2, sb.length());
            return sb.toString();
        }
        return "";
    }
    
    public static CacheControl parse(final Headers headers) {
        boolean b = false;
        int seconds = -1;
        int seconds2 = -1;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        int seconds3 = -1;
        int seconds4 = -1;
        boolean b5 = false;
        boolean b6 = false;
        int n = 1;
        final int size = headers.size();
        int i = 0;
        String s = null;
        boolean b7 = false;
    Label_0124_Outer:
        while (i < size) {
            final String name = headers.name(i);
            final String value = headers.value(i);
            while (true) {
                Label_0139: {
                    if (!name.equalsIgnoreCase("Cache-Control")) {
                        if (name.equalsIgnoreCase("Pragma")) {
                            n = 0;
                            break Label_0139;
                        }
                    }
                    else {
                        if (s == null) {
                            s = value;
                            break Label_0139;
                        }
                        n = 0;
                        break Label_0139;
                    }
                    ++i;
                    continue Label_0124_Outer;
                }
                int j = 0;
                while (j < value.length()) {
                    int endIndex = HttpHeaders.skipUntil(value, j, "=,;");
                    final String trim = value.substring(j, endIndex).trim();
                    String s2;
                    if (endIndex != value.length() && value.charAt(endIndex) != ',' && value.charAt(endIndex) != ';') {
                        int skipWhitespace = HttpHeaders.skipWhitespace(value, endIndex + 1);
                        if (skipWhitespace < value.length() && value.charAt(skipWhitespace) == '\"') {
                            ++skipWhitespace;
                            endIndex = HttpHeaders.skipUntil(value, skipWhitespace, "\"");
                            s2 = value.substring(skipWhitespace, endIndex);
                            ++endIndex;
                        }
                        else {
                            endIndex = HttpHeaders.skipUntil(value, skipWhitespace, ",;");
                            s2 = value.substring(skipWhitespace, endIndex).trim();
                        }
                    }
                    else {
                        ++endIndex;
                        s2 = null;
                    }
                    if (!"no-cache".equalsIgnoreCase(trim)) {
                        if (!"no-store".equalsIgnoreCase(trim)) {
                            if (!"max-age".equalsIgnoreCase(trim)) {
                                if (!"s-maxage".equalsIgnoreCase(trim)) {
                                    if (!"private".equalsIgnoreCase(trim)) {
                                        if (!"public".equalsIgnoreCase(trim)) {
                                            if (!"must-revalidate".equalsIgnoreCase(trim)) {
                                                if (!"max-stale".equalsIgnoreCase(trim)) {
                                                    if (!"min-fresh".equalsIgnoreCase(trim)) {
                                                        if (!"only-if-cached".equalsIgnoreCase(trim)) {
                                                            j = endIndex;
                                                            if (!"no-transform".equalsIgnoreCase(trim)) {
                                                                continue Label_0124_Outer;
                                                            }
                                                            b6 = true;
                                                            j = endIndex;
                                                        }
                                                        else {
                                                            b5 = true;
                                                            j = endIndex;
                                                        }
                                                    }
                                                    else {
                                                        seconds4 = HttpHeaders.parseSeconds(s2, -1);
                                                        j = endIndex;
                                                    }
                                                }
                                                else {
                                                    seconds3 = HttpHeaders.parseSeconds(s2, Integer.MAX_VALUE);
                                                    j = endIndex;
                                                }
                                            }
                                            else {
                                                b4 = true;
                                                j = endIndex;
                                            }
                                        }
                                        else {
                                            b3 = true;
                                            j = endIndex;
                                        }
                                    }
                                    else {
                                        b2 = true;
                                        j = endIndex;
                                    }
                                }
                                else {
                                    seconds2 = HttpHeaders.parseSeconds(s2, -1);
                                    j = endIndex;
                                }
                            }
                            else {
                                seconds = HttpHeaders.parseSeconds(s2, -1);
                                j = endIndex;
                            }
                        }
                        else {
                            b = true;
                            j = endIndex;
                        }
                    }
                    else {
                        b7 = true;
                        j = endIndex;
                    }
                }
                continue;
            }
        }
        String s3;
        if (n != 0) {
            s3 = s;
        }
        else {
            s3 = null;
        }
        return new CacheControl(b7, b, seconds, seconds2, b2, b3, b4, seconds3, seconds4, b5, b6, s3);
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
    
    @Override
    public String toString() {
        String headerValue;
        if ((headerValue = this.headerValue) == null) {
            headerValue = this.headerValue();
            this.headerValue = headerValue;
        }
        return headerValue;
    }
    
    public static final class Builder
    {
        int maxAgeSeconds;
        int maxStaleSeconds;
        int minFreshSeconds;
        boolean noCache;
        boolean noStore;
        boolean noTransform;
        boolean onlyIfCached;
        
        public Builder() {
            this.maxAgeSeconds = -1;
            this.maxStaleSeconds = -1;
            this.minFreshSeconds = -1;
        }
        
        public CacheControl build() {
            return new CacheControl(this);
        }
        
        public Builder maxAge(int n, final TimeUnit timeUnit) {
            final int n2 = 0;
            if (n >= 0) {
                final long seconds = timeUnit.toSeconds(n);
                n = n2;
                if (seconds <= 2147483647L) {
                    n = 1;
                }
                if (n == 0) {
                    n = Integer.MAX_VALUE;
                }
                else {
                    n = (int)seconds;
                }
                this.maxAgeSeconds = n;
                return this;
            }
            throw new IllegalArgumentException("maxAge < 0: " + n);
        }
        
        public Builder maxStale(int n, final TimeUnit timeUnit) {
            final int n2 = 0;
            if (n >= 0) {
                final long seconds = timeUnit.toSeconds(n);
                n = n2;
                if (seconds <= 2147483647L) {
                    n = 1;
                }
                if (n == 0) {
                    n = Integer.MAX_VALUE;
                }
                else {
                    n = (int)seconds;
                }
                this.maxStaleSeconds = n;
                return this;
            }
            throw new IllegalArgumentException("maxStale < 0: " + n);
        }
        
        public Builder minFresh(int n, final TimeUnit timeUnit) {
            final int n2 = 0;
            if (n >= 0) {
                final long seconds = timeUnit.toSeconds(n);
                n = n2;
                if (seconds <= 2147483647L) {
                    n = 1;
                }
                if (n == 0) {
                    n = Integer.MAX_VALUE;
                }
                else {
                    n = (int)seconds;
                }
                this.minFreshSeconds = n;
                return this;
            }
            throw new IllegalArgumentException("minFresh < 0: " + n);
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
}
