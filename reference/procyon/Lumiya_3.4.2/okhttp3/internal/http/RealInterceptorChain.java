// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import java.io.IOException;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.Request;
import java.util.List;
import okhttp3.Connection;
import okhttp3.Interceptor;

public final class RealInterceptorChain implements Chain
{
    private int calls;
    private final Connection connection;
    private final HttpCodec httpCodec;
    private final int index;
    private final List<Interceptor> interceptors;
    private final Request request;
    private final StreamAllocation streamAllocation;
    
    public RealInterceptorChain(final List<Interceptor> interceptors, final StreamAllocation streamAllocation, final HttpCodec httpCodec, final Connection connection, final int index, final Request request) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.streamAllocation = streamAllocation;
        this.httpCodec = httpCodec;
        this.index = index;
        this.request = request;
    }
    
    private boolean sameConnection(final HttpUrl httpUrl) {
        boolean b = false;
        if (httpUrl.host().equals(this.connection.route().address().url().host()) && httpUrl.port() == this.connection.route().address().url().port()) {
            b = true;
        }
        return b;
    }
    
    @Override
    public Connection connection() {
        return this.connection;
    }
    
    public HttpCodec httpStream() {
        return this.httpCodec;
    }
    
    @Override
    public Response proceed(final Request request) throws IOException {
        return this.proceed(request, this.streamAllocation, this.httpCodec, this.connection);
    }
    
    public Response proceed(final Request request, final StreamAllocation streamAllocation, final HttpCodec httpCodec, final Connection connection) throws IOException {
        if (this.index >= this.interceptors.size()) {
            throw new AssertionError();
        }
        ++this.calls;
        if (this.httpCodec != null && !this.sameConnection(request.url())) {
            throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must retain the same host and port");
        }
        if (this.httpCodec != null && this.calls > 1) {
            throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once");
        }
        final RealInterceptorChain realInterceptorChain = new RealInterceptorChain(this.interceptors, streamAllocation, httpCodec, connection, this.index + 1, request);
        final Interceptor interceptor = this.interceptors.get(this.index);
        final Response intercept = interceptor.intercept((Interceptor.Chain)realInterceptorChain);
        if (httpCodec != null && this.index + 1 < this.interceptors.size() && realInterceptorChain.calls != 1) {
            throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
        }
        if (intercept != null) {
            return intercept;
        }
        throw new NullPointerException("interceptor " + interceptor + " returned null");
    }
    
    @Override
    public Request request() {
        return this.request;
    }
    
    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }
}
