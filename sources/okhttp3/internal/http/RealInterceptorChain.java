package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import okhttp3.Connection;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.StreamAllocation;

public final class RealInterceptorChain implements Interceptor.Chain {
    private int calls;
    private final Connection connection;
    private final HttpCodec httpCodec;
    private final int index;
    private final List<Interceptor> interceptors;
    private final Request request;
    private final StreamAllocation streamAllocation;

    public RealInterceptorChain(List<Interceptor> list, StreamAllocation streamAllocation2, HttpCodec httpCodec2, Connection connection2, int i, Request request2) {
        this.interceptors = list;
        this.connection = connection2;
        this.streamAllocation = streamAllocation2;
        this.httpCodec = httpCodec2;
        this.index = i;
        this.request = request2;
    }

    private boolean sameConnection(HttpUrl httpUrl) {
        return httpUrl.host().equals(this.connection.route().address().url().host()) && httpUrl.port() == this.connection.route().address().url().port();
    }

    public Connection connection() {
        return this.connection;
    }

    public HttpCodec httpStream() {
        return this.httpCodec;
    }

    public Response proceed(Request request2) throws IOException {
        return proceed(request2, this.streamAllocation, this.httpCodec, this.connection);
    }

    public Response proceed(Request request2, StreamAllocation streamAllocation2, HttpCodec httpCodec2, Connection connection2) throws IOException {
        if (this.index < this.interceptors.size()) {
            this.calls++;
            if (this.httpCodec != null && !sameConnection(request2.url())) {
                throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must retain the same host and port");
            } else if (this.httpCodec != null && this.calls > 1) {
                throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once");
            } else {
                RealInterceptorChain realInterceptorChain = new RealInterceptorChain(this.interceptors, streamAllocation2, httpCodec2, connection2, this.index + 1, request2);
                Interceptor interceptor = this.interceptors.get(this.index);
                Response intercept = interceptor.intercept(realInterceptorChain);
                if (httpCodec2 != null && this.index + 1 < this.interceptors.size() && realInterceptorChain.calls != 1) {
                    throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
                } else if (intercept != null) {
                    return intercept;
                } else {
                    throw new NullPointerException("interceptor " + interceptor + " returned null");
                }
            }
        } else {
            throw new AssertionError();
        }
    }

    public Request request() {
        return this.request;
    }

    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }
}
