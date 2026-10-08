package okhttp3.internal.http;

import com.google.common.logging.nano.Vr;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpRetryException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteException;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http2.ConnectionShutdownException;
/* loaded from: classes.dex */
public final class RetryAndFollowUpInterceptor implements Interceptor {
    private static final int MAX_FOLLOW_UPS = 20;
    private Object callStackTrace;
    private volatile boolean canceled;
    private final OkHttpClient client;
    private final boolean forWebSocket;
    private StreamAllocation streamAllocation;

    public RetryAndFollowUpInterceptor(OkHttpClient okHttpClient, boolean z) {
        this.client = okHttpClient;
        this.forWebSocket = z;
    }

    private Address createAddress(HttpUrl httpUrl) {
        SSLSocketFactory sslSocketFactory;
        HostnameVerifier hostnameVerifier;
        CertificatePinner certificatePinner = null;
        if (httpUrl.isHttps()) {
            sslSocketFactory = this.client.sslSocketFactory();
            hostnameVerifier = this.client.hostnameVerifier();
            certificatePinner = this.client.certificatePinner();
        } else {
            hostnameVerifier = null;
            sslSocketFactory = null;
        }
        return new Address(httpUrl.host(), httpUrl.port(), this.client.dns(), this.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Request followUpRequest(Response response) throws IOException {
        String header;
        HttpUrl resolve;
        if (response != null) {
            RealConnection connection = this.streamAllocation.connection();
            Route route = connection == null ? null : connection.route();
            int code = response.code();
            String method = response.request().method();
            switch (code) {
                case 300:
                case Vr.VREvent.VrCore.ErrorCode.NO_ZEN_RULE /* 301 */:
                case 302:
                case 303:
                    break;
                case StatusLine.HTTP_TEMP_REDIRECT /* 307 */:
                case StatusLine.HTTP_PERM_REDIRECT /* 308 */:
                    if (!method.equals("GET") && !method.equals("HEAD")) {
                        return null;
                    }
                    break;
                case Vr.VREvent.VrCore.ErrorCode.INVALID_READ /* 401 */:
                    return this.client.authenticator().authenticate(route, response);
                case 407:
                    if ((route == null ? this.client.proxy() : route.proxy()).type() == Proxy.Type.HTTP) {
                        return this.client.proxyAuthenticator().authenticate(route, response);
                    }
                    throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
                case 408:
                    if (response.request().body() instanceof UnrepeatableRequestBody) {
                        return null;
                    }
                    return response.request();
                default:
                    return null;
            }
            if (!this.client.followRedirects() || (header = response.header(com.google.common.net.HttpHeaders.LOCATION)) == null || (resolve = response.request().url().resolve(header)) == null) {
                return null;
            }
            if (resolve.scheme().equals(response.request().url().scheme()) || this.client.followSslRedirects()) {
                Request.Builder newBuilder = response.request().newBuilder();
                if (HttpMethod.permitsRequestBody(method)) {
                    boolean redirectsWithBody = HttpMethod.redirectsWithBody(method);
                    if (HttpMethod.redirectsToGet(method)) {
                        newBuilder.method("GET", null);
                    } else {
                        newBuilder.method(method, redirectsWithBody ? response.request().body() : null);
                    }
                    if (!redirectsWithBody) {
                        newBuilder.removeHeader(com.google.common.net.HttpHeaders.TRANSFER_ENCODING);
                        newBuilder.removeHeader(com.google.common.net.HttpHeaders.CONTENT_LENGTH);
                        newBuilder.removeHeader(com.google.common.net.HttpHeaders.CONTENT_TYPE);
                    }
                }
                if (!sameConnection(response, resolve)) {
                    newBuilder.removeHeader(com.google.common.net.HttpHeaders.AUTHORIZATION);
                }
                return newBuilder.url(resolve).build();
            }
            return null;
        }
        throw new IllegalStateException();
    }

    private boolean isRecoverable(IOException iOException, boolean z) {
        if (iOException instanceof ProtocolException) {
            return false;
        }
        return !(iOException instanceof InterruptedIOException) ? (((iOException instanceof SSLHandshakeException) && (iOException.getCause() instanceof CertificateException)) || (iOException instanceof SSLPeerUnverifiedException)) ? false : true : (iOException instanceof SocketTimeoutException) && !z;
    }

    private boolean recover(IOException iOException, boolean z, Request request) {
        this.streamAllocation.streamFailed(iOException);
        if (this.client.retryOnConnectionFailure()) {
            return !(z && (request.body() instanceof UnrepeatableRequestBody)) && isRecoverable(iOException, z) && this.streamAllocation.hasMoreRoutes();
        }
        return false;
    }

    private boolean sameConnection(Response response, HttpUrl httpUrl) {
        HttpUrl url = response.request().url();
        return url.host().equals(httpUrl.host()) && url.port() == httpUrl.port() && url.scheme().equals(httpUrl.scheme());
    }

    public void cancel() {
        this.canceled = true;
        StreamAllocation streamAllocation = this.streamAllocation;
        if (streamAllocation == null) {
            return;
        }
        streamAllocation.cancel();
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response proceed;
        Request request = chain.request();
        this.streamAllocation = new StreamAllocation(this.client.connectionPool(), createAddress(request.url()), this.callStackTrace);
        Response response = null;
        int i = 0;
        Request request2 = request;
        while (!this.canceled) {
            try {
                try {
                    try {
                        proceed = ((RealInterceptorChain) chain).proceed(request2, this.streamAllocation, null, null);
                        if (response != null) {
                            proceed = proceed.newBuilder().priorResponse(response.newBuilder().body(null).build()).build();
                        }
                        request2 = followUpRequest(proceed);
                    } catch (RouteException e) {
                        if (!recover(e.getLastConnectException(), false, request2)) {
                            throw e.getLastConnectException();
                        }
                    }
                } catch (IOException e2) {
                    if (!recover(e2, !(e2 instanceof ConnectionShutdownException), request2)) {
                        throw e2;
                    }
                }
                if (request2 == null) {
                    if (!this.forWebSocket) {
                        this.streamAllocation.release();
                    }
                    return proceed;
                }
                Util.closeQuietly(proceed.body());
                int i2 = i + 1;
                if (i2 > 20) {
                    this.streamAllocation.release();
                    throw new ProtocolException("Too many follow-up requests: " + i2);
                } else if (request2.body() instanceof UnrepeatableRequestBody) {
                    this.streamAllocation.release();
                    throw new HttpRetryException("Cannot retry streamed HTTP body", proceed.code());
                } else {
                    if (!sameConnection(proceed, request2.url())) {
                        this.streamAllocation.release();
                        this.streamAllocation = new StreamAllocation(this.client.connectionPool(), createAddress(request2.url()), this.callStackTrace);
                    } else if (this.streamAllocation.codec() != null) {
                        throw new IllegalStateException("Closing the body of " + proceed + " didn't close its backing stream. Bad interceptor?");
                    }
                    i = i2;
                    response = proceed;
                }
            } catch (Throwable th) {
                this.streamAllocation.streamFailed(null);
                this.streamAllocation.release();
                throw th;
            }
        }
        this.streamAllocation.release();
        throw new IOException("Canceled");
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCallStackTrace(Object obj) {
        this.callStackTrace = obj;
    }

    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }
}
