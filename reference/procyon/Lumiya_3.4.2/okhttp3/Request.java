// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.net.URL;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.Util;
import java.util.List;

public final class Request
{
    final RequestBody body;
    private volatile CacheControl cacheControl;
    final Headers headers;
    final String method;
    final Object tag;
    final HttpUrl url;
    
    Request(final Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        Object tag;
        if (builder.tag == null) {
            tag = this;
        }
        else {
            tag = builder.tag;
        }
        this.tag = tag;
    }
    
    public RequestBody body() {
        return this.body;
    }
    
    public CacheControl cacheControl() {
        CacheControl cacheControl;
        if ((cacheControl = this.cacheControl) == null) {
            cacheControl = CacheControl.parse(this.headers);
            this.cacheControl = cacheControl;
        }
        return cacheControl;
    }
    
    public String header(final String s) {
        return this.headers.get(s);
    }
    
    public List<String> headers(final String s) {
        return this.headers.values(s);
    }
    
    public Headers headers() {
        return this.headers;
    }
    
    public boolean isHttps() {
        return this.url.isHttps();
    }
    
    public String method() {
        return this.method;
    }
    
    public Builder newBuilder() {
        return new Builder(this);
    }
    
    public Object tag() {
        return this.tag;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("Request{method=").append(this.method).append(", url=").append(this.url).append(", tag=");
        Object tag;
        if (this.tag == this) {
            tag = null;
        }
        else {
            tag = this.tag;
        }
        return append.append(tag).append('}').toString();
    }
    
    public HttpUrl url() {
        return this.url;
    }
    
    public static class Builder
    {
        RequestBody body;
        Headers.Builder headers;
        String method;
        Object tag;
        HttpUrl url;
        
        public Builder() {
            this.method = "GET";
            this.headers = new Headers.Builder();
        }
        
        Builder(final Request request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tag = request.tag;
            this.headers = request.headers.newBuilder();
        }
        
        public Builder addHeader(final String s, final String s2) {
            this.headers.add(s, s2);
            return this;
        }
        
        public Request build() {
            if (this.url != null) {
                return new Request(this);
            }
            throw new IllegalStateException("url == null");
        }
        
        public Builder cacheControl(final CacheControl cacheControl) {
            final String string = cacheControl.toString();
            if (!string.isEmpty()) {
                return this.header("Cache-Control", string);
            }
            return this.removeHeader("Cache-Control");
        }
        
        public Builder delete() {
            return this.delete(Util.EMPTY_REQUEST);
        }
        
        public Builder delete(final RequestBody requestBody) {
            return this.method("DELETE", requestBody);
        }
        
        public Builder get() {
            return this.method("GET", null);
        }
        
        public Builder head() {
            return this.method("HEAD", null);
        }
        
        public Builder header(final String s, final String s2) {
            this.headers.set(s, s2);
            return this;
        }
        
        public Builder headers(final Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }
        
        public Builder method(final String method, final RequestBody body) {
            if (method == null) {
                throw new NullPointerException("method == null");
            }
            if (method.length() == 0) {
                throw new IllegalArgumentException("method.length() == 0");
            }
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            this.body = body;
            return this;
        }
        
        public Builder patch(final RequestBody requestBody) {
            return this.method("PATCH", requestBody);
        }
        
        public Builder post(final RequestBody requestBody) {
            return this.method("POST", requestBody);
        }
        
        public Builder put(final RequestBody requestBody) {
            return this.method("PUT", requestBody);
        }
        
        public Builder removeHeader(final String s) {
            this.headers.removeAll(s);
            return this;
        }
        
        public Builder tag(final Object tag) {
            this.tag = tag;
            return this;
        }
        
        public Builder url(String str) {
            if (str == null) {
                throw new NullPointerException("url == null");
            }
            if (!str.regionMatches(true, 0, "ws:", 0, 3)) {
                if (str.regionMatches(true, 0, "wss:", 0, 4)) {
                    str = "https:" + str.substring(4);
                }
            }
            else {
                str = "http:" + str.substring(3);
            }
            final HttpUrl parse = HttpUrl.parse(str);
            if (parse != null) {
                return this.url(parse);
            }
            throw new IllegalArgumentException("unexpected url: " + str);
        }
        
        public Builder url(final URL obj) {
            if (obj == null) {
                throw new NullPointerException("url == null");
            }
            final HttpUrl value = HttpUrl.get(obj);
            if (value != null) {
                return this.url(value);
            }
            throw new IllegalArgumentException("unexpected url: " + obj);
        }
        
        public Builder url(final HttpUrl url) {
            if (url != null) {
                this.url = url;
                return this;
            }
            throw new NullPointerException("url == null");
        }
    }
}
