package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okio.BufferedSink;
import okio.Okio;
/* loaded from: classes.dex */
public final class CallServerInterceptor implements Interceptor {
    private final boolean forWebSocket;

    public CallServerInterceptor(boolean z) {
        this.forWebSocket = z;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        HttpCodec httpStream = ((RealInterceptorChain) chain).httpStream();
        StreamAllocation streamAllocation = ((RealInterceptorChain) chain).streamAllocation();
        Request request = chain.request();
        long currentTimeMillis = System.currentTimeMillis();
        httpStream.writeRequestHeaders(request);
        if (HttpMethod.permitsRequestBody(request.method()) && request.body() != null) {
            BufferedSink buffer = Okio.buffer(httpStream.createRequestBody(request, request.body().contentLength()));
            request.body().writeTo(buffer);
            buffer.close();
        }
        httpStream.finishRequest();
        Response build = httpStream.readResponseHeaders().request(request).handshake(streamAllocation.connection().handshake()).sentRequestAtMillis(currentTimeMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
        int code = build.code();
        Response build2 = (this.forWebSocket && code == 101) ? build.newBuilder().body(Util.EMPTY_RESPONSE).build() : build.newBuilder().body(httpStream.openResponseBody(build)).build();
        if ("close".equalsIgnoreCase(build2.request().header(com.google.common.net.HttpHeaders.CONNECTION)) || "close".equalsIgnoreCase(build2.header(com.google.common.net.HttpHeaders.CONNECTION))) {
            streamAllocation.noNewStreams();
        }
        if (code == 204 || code == 205) {
            if (!(build2.body().contentLength() <= 0)) {
                throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + build2.body().contentLength());
            }
        }
        return build2;
    }
}
