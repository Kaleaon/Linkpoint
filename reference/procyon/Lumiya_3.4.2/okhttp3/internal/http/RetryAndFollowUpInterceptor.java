// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import java.net.HttpRetryException;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.connection.RouteException;
import java.io.Closeable;
import okhttp3.internal.Util;
import okhttp3.Connection;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.io.IOException;
import okhttp3.Route;
import okhttp3.internal.connection.RealConnection;
import okhttp3.RequestBody;
import java.net.ProtocolException;
import java.net.Proxy;
import okhttp3.Request;
import okhttp3.Response;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HostnameVerifier;
import okhttp3.CertificatePinner;
import okhttp3.Address;
import okhttp3.HttpUrl;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;

public final class RetryAndFollowUpInterceptor implements Interceptor
{
    private static final int MAX_FOLLOW_UPS = 20;
    private Object callStackTrace;
    private volatile boolean canceled;
    private final OkHttpClient client;
    private final boolean forWebSocket;
    private StreamAllocation streamAllocation;
    
    public RetryAndFollowUpInterceptor(final OkHttpClient client, final boolean forWebSocket) {
        this.client = client;
        this.forWebSocket = forWebSocket;
    }
    
    private Address createAddress(final HttpUrl httpUrl) {
        CertificatePinner certificatePinner = null;
        HostnameVerifier hostnameVerifier;
        SSLSocketFactory sslSocketFactory;
        if (!httpUrl.isHttps()) {
            hostnameVerifier = null;
            sslSocketFactory = null;
        }
        else {
            sslSocketFactory = this.client.sslSocketFactory();
            hostnameVerifier = this.client.hostnameVerifier();
            certificatePinner = this.client.certificatePinner();
        }
        return new Address(httpUrl.host(), httpUrl.port(), this.client.dns(), this.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
    }
    
    private Request followUpRequest(final Response response) throws IOException {
        final RequestBody requestBody = null;
        if (response == null) {
            throw new IllegalStateException();
        }
        final RealConnection connection = this.streamAllocation.connection();
        Route route;
        if (connection == null) {
            route = null;
        }
        else {
            route = connection.route();
        }
        final int code = response.code();
        final String method = response.request().method();
        switch (code) {
            default: {
                return null;
            }
            case 407: {
                Proxy proxy;
                if (route == null) {
                    proxy = this.client.proxy();
                }
                else {
                    proxy = route.proxy();
                }
                if (proxy.type() == Proxy.Type.HTTP) {
                    return this.client.proxyAuthenticator().authenticate(route, response);
                }
                throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            }
            case 401: {
                return this.client.authenticator().authenticate(route, response);
            }
            case 307:
            case 308: {
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    return null;
                }
            }
            case 300:
            case 301:
            case 302:
            case 303: {
                if (!this.client.followRedirects()) {
                    return null;
                }
                final String header = response.header("Location");
                if (header == null) {
                    return null;
                }
                final HttpUrl resolve = response.request().url().resolve(header);
                if (resolve == null) {
                    return null;
                }
                if (!resolve.scheme().equals(response.request().url().scheme()) && !this.client.followSslRedirects()) {
                    return null;
                }
                final Request.Builder builder = response.request().newBuilder();
                if (HttpMethod.permitsRequestBody(method)) {
                    final boolean redirectsWithBody = HttpMethod.redirectsWithBody(method);
                    if (!HttpMethod.redirectsToGet(method)) {
                        RequestBody body;
                        if (!redirectsWithBody) {
                            body = requestBody;
                        }
                        else {
                            body = response.request().body();
                        }
                        builder.method(method, body);
                    }
                    else {
                        builder.method("GET", null);
                    }
                    if (!redirectsWithBody) {
                        builder.removeHeader("Transfer-Encoding");
                        builder.removeHeader("Content-Length");
                        builder.removeHeader("Content-Type");
                    }
                }
                if (!this.sameConnection(response, resolve)) {
                    builder.removeHeader("Authorization");
                }
                return builder.url(resolve).build();
            }
            case 408: {
                if (!(response.request().body() instanceof UnrepeatableRequestBody)) {
                    return response.request();
                }
                return null;
            }
        }
    }
    
    private boolean isRecoverable(final IOException ex, final boolean b) {
        boolean b2 = true;
        if (ex instanceof ProtocolException) {
            return false;
        }
        if (!(ex instanceof InterruptedIOException)) {
            return (!(ex instanceof SSLHandshakeException) || !(ex.getCause() instanceof CertificateException)) && !(ex instanceof SSLPeerUnverifiedException);
        }
        if (!(ex instanceof SocketTimeoutException) || b) {
            b2 = false;
        }
        return b2;
    }
    
    private boolean recover(final IOException ex, final boolean b, final Request request) {
        this.streamAllocation.streamFailed(ex);
        return this.client.retryOnConnectionFailure() && (!b || !(request.body() instanceof UnrepeatableRequestBody)) && this.isRecoverable(ex, b) && this.streamAllocation.hasMoreRoutes();
    }
    
    private boolean sameConnection(final Response response, final HttpUrl httpUrl) {
        final boolean b = false;
        final HttpUrl url = response.request().url();
        boolean b2;
        if (!url.host().equals(httpUrl.host())) {
            b2 = b;
        }
        else {
            b2 = b;
            if (url.port() == httpUrl.port()) {
                b2 = b;
                if (url.scheme().equals(httpUrl.scheme())) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public void cancel() {
        this.canceled = true;
        final StreamAllocation streamAllocation = this.streamAllocation;
        if (streamAllocation != null) {
            streamAllocation.cancel();
        }
    }
    
    @Override
    public Response intercept(final Chain chain) throws IOException {
        Object o = chain.request();
        this.streamAllocation = new StreamAllocation(this.client.connectionPool(), this.createAddress(((Request)o).url()), this.callStackTrace);
        Object build = null;
        int i = 0;
    Label_0136_Outer:
        while (true) {
            while (true) {
                if (!this.canceled) {
                    Label_0308: {
                        Label_0272: {
                        Label_0133_Outer:
                            while (true) {
                                while (true) {
                                Label_0330:
                                    while (true) {
                                        Label_0228: {
                                            try {
                                                o = ((RealInterceptorChain)chain).proceed((Request)o, this.streamAllocation, null, null);
                                                if (build != null) {
                                                    break Label_0228;
                                                }
                                                build = o;
                                                o = this.followUpRequest((Response)build);
                                                if (o == null) {
                                                    break;
                                                }
                                                Util.closeQuietly(((Response)build).body());
                                                if (++i > 20) {
                                                    break Label_0272;
                                                }
                                                if (((Request)o).body() instanceof UnrepeatableRequestBody) {
                                                    break Label_0308;
                                                }
                                                if (!this.sameConnection((Response)build, ((Request)o).url())) {
                                                    break Label_0330;
                                                }
                                                if (this.streamAllocation.codec() == null) {
                                                    continue Label_0136_Outer;
                                                }
                                                throw new IllegalStateException("Closing the body of " + build + " didn't close its backing stream. Bad interceptor?");
                                                this.streamAllocation.release();
                                                throw new IOException("Canceled");
                                            }
                                            catch (final RouteException ex) {
                                                if (!this.recover(ex.getLastConnectException(), false, (Request)o)) {
                                                    throw ex.getLastConnectException();
                                                }
                                                continue Label_0136_Outer;
                                            }
                                            catch (final IOException ex2) {
                                                if (!this.recover(ex2, !(ex2 instanceof ConnectionShutdownException), (Request)o)) {
                                                    throw ex2;
                                                }
                                                continue Label_0136_Outer;
                                            }
                                            finally {
                                                this.streamAllocation.streamFailed(null);
                                                this.streamAllocation.release();
                                            }
                                        }
                                        build = ((Response)o).newBuilder().priorResponse(((Response)build).newBuilder().body(null).build()).build();
                                        continue Label_0133_Outer;
                                    }
                                    this.streamAllocation.release();
                                    this.streamAllocation = new StreamAllocation(this.client.connectionPool(), this.createAddress(((Request)o).url()), this.callStackTrace);
                                    continue Label_0136_Outer;
                                }
                            }
                            if (!this.forWebSocket) {
                                this.streamAllocation.release();
                            }
                            return (Response)build;
                        }
                        this.streamAllocation.release();
                        throw new ProtocolException("Too many follow-up requests: " + i);
                    }
                    this.streamAllocation.release();
                    throw new HttpRetryException("Cannot retry streamed HTTP body", ((Response)build).code());
                }
                continue;
            }
        }
    }
    
    public boolean isCanceled() {
        return this.canceled;
    }
    
    public void setCallStackTrace(final Object callStackTrace) {
        this.callStackTrace = callStackTrace;
    }
    
    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }
}
