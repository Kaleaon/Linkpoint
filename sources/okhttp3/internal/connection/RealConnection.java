package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.ConnectionSpec;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public final class RealConnection extends Http2Connection.Listener implements Connection {
    public int allocationLimit;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private Handshake handshake;
    public volatile Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    public BufferedSink sink;
    public Socket socket;
    public BufferedSource source;
    public int successCount;

    public RealConnection(Route route2) {
        this.route = route2;
    }

    private void buildConnection(int i, int i2, int i3, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        connectSocket(i, i2);
        establishProtocol(i2, i3, connectionSpecSelector);
    }

    private void buildTunneledConnection(int i, int i2, int i3, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Request createTunnelRequest = createTunnelRequest();
        HttpUrl url = createTunnelRequest.url();
        int i4 = 0;
        while (true) {
            i4++;
            if (i4 <= 21) {
                connectSocket(i, i2);
                createTunnelRequest = createTunnel(i2, i3, createTunnelRequest, url);
                if (createTunnelRequest != null) {
                    Util.closeQuietly(this.rawSocket);
                    this.rawSocket = null;
                    this.sink = null;
                    this.source = null;
                } else {
                    establishProtocol(i2, i3, connectionSpecSelector);
                    return;
                }
            } else {
                throw new ProtocolException("Too many tunnel connections attempted: " + 21);
            }
        }
    }

    private void connectSocket(int i, int i2) throws IOException {
        Proxy proxy = this.route.proxy();
        this.rawSocket = (proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP) ? this.route.address().socketFactory().createSocket() : new Socket(proxy);
        this.rawSocket.setSoTimeout(i2);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), i);
            this.source = Okio.buffer(Okio.source(this.rawSocket));
            this.sink = Okio.buffer(Okio.sink(this.rawSocket));
        } catch (ConnectException e) {
            ConnectException connectException = new ConnectException("Failed to connect to " + this.route.socketAddress());
            connectException.initCause(e);
            throw connectException;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: javax.net.ssl.SSLSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: javax.net.ssl.SSLSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: javax.net.ssl.SSLSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: javax.net.ssl.SSLSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ab A[Catch:{ all -> 0x00ac }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x012f A[SYNTHETIC, Splitter:B:34:0x012f] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0135  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void connectTls(int r10, int r11, okhttp3.internal.connection.ConnectionSpecSelector r12) throws java.io.IOException {
        /*
            r9 = this;
            r1 = 0
            okhttp3.Route r0 = r9.route
            okhttp3.Address r2 = r0.address()
            javax.net.ssl.SSLSocketFactory r0 = r2.sslSocketFactory()
            java.net.Socket r3 = r9.rawSocket     // Catch:{ AssertionError -> 0x013e }
            okhttp3.HttpUrl r4 = r2.url()     // Catch:{ AssertionError -> 0x013e }
            java.lang.String r4 = r4.host()     // Catch:{ AssertionError -> 0x013e }
            okhttp3.HttpUrl r5 = r2.url()     // Catch:{ AssertionError -> 0x013e }
            int r5 = r5.port()     // Catch:{ AssertionError -> 0x013e }
            r6 = 1
            java.net.Socket r0 = r0.createSocket(r3, r4, r5, r6)     // Catch:{ AssertionError -> 0x013e }
            javax.net.ssl.SSLSocket r0 = (javax.net.ssl.SSLSocket) r0     // Catch:{ AssertionError -> 0x013e }
            okhttp3.ConnectionSpec r3 = r12.configureSecureSocket(r0)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            boolean r4 = r3.supportsTlsExtensions()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            if (r4 != 0) goto L_0x008d
        L_0x002e:
            r0.startHandshake()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            javax.net.ssl.SSLSession r4 = r0.getSession()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okhttp3.Handshake r4 = okhttp3.Handshake.get(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            javax.net.ssl.HostnameVerifier r5 = r2.hostnameVerifier()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okhttp3.HttpUrl r6 = r2.url()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r6 = r6.host()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            javax.net.ssl.SSLSession r7 = r0.getSession()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            boolean r5 = r5.verify(r6, r7)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            if (r5 == 0) goto L_0x00b3
            okhttp3.CertificatePinner r5 = r2.certificatePinner()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okhttp3.HttpUrl r2 = r2.url()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r2 = r2.host()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.util.List r6 = r4.peerCertificates()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r5.check((java.lang.String) r2, (java.util.List<java.security.cert.Certificate>) r6)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            boolean r2 = r3.supportsTlsExtensions()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            if (r2 != 0) goto L_0x0116
        L_0x0068:
            r9.socket = r0     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.net.Socket r2 = r9.socket     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okio.Source r2 = okio.Okio.source((java.net.Socket) r2)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okio.BufferedSource r2 = okio.Okio.buffer((okio.Source) r2)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r9.source = r2     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.net.Socket r2 = r9.socket     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okio.Sink r2 = okio.Okio.sink((java.net.Socket) r2)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okio.BufferedSink r2 = okio.Okio.buffer((okio.Sink) r2)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r9.sink = r2     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r9.handshake = r4     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            if (r1 != 0) goto L_0x0120
            okhttp3.Protocol r1 = okhttp3.Protocol.HTTP_1_1     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
        L_0x0088:
            r9.protocol = r1     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            if (r0 != 0) goto L_0x0126
        L_0x008c:
            return
        L_0x008d:
            okhttp3.internal.platform.Platform r4 = okhttp3.internal.platform.Platform.get()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okhttp3.HttpUrl r5 = r2.url()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r5 = r5.host()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.util.List r6 = r2.protocols()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r4.configureTlsExtensions(r0, r5, r6)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            goto L_0x002e
        L_0x00a1:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
        L_0x00a5:
            boolean r2 = okhttp3.internal.Util.isAndroidGetsocknameError(r0)     // Catch:{ all -> 0x00ac }
            if (r2 != 0) goto L_0x012f
            throw r0     // Catch:{ all -> 0x00ac }
        L_0x00ac:
            r0 = move-exception
        L_0x00ad:
            if (r1 != 0) goto L_0x0135
        L_0x00af:
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r1)
            throw r0
        L_0x00b3:
            java.util.List r1 = r4.peerCertificates()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r3 = 0
            java.lang.Object r1 = r1.get(r3)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.security.cert.X509Certificate r1 = (java.security.cert.X509Certificate) r1     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            javax.net.ssl.SSLPeerUnverifiedException r3 = new javax.net.ssl.SSLPeerUnverifiedException     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r4.<init>()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r5 = "Hostname "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            okhttp3.HttpUrl r2 = r2.url()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r2 = r2.host()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.StringBuilder r2 = r4.append(r2)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r4 = " not verified:\n    certificate: "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r4 = okhttp3.CertificatePinner.pin(r1)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r4 = "\n    DN: "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.security.Principal r4 = r1.getSubjectDN()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r4 = r4.getName()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r4 = "\n    subjectAltNames: "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.util.List r1 = okhttp3.internal.tls.OkHostnameVerifier.allSubjectAltNames(r1)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.StringBuilder r1 = r2.append(r1)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r1 = r1.toString()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            r3.<init>(r1)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            throw r3     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
        L_0x0111:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
            goto L_0x00ad
        L_0x0116:
            okhttp3.internal.platform.Platform r1 = okhttp3.internal.platform.Platform.get()     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            java.lang.String r1 = r1.getSelectedProtocol(r0)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            goto L_0x0068
        L_0x0120:
            okhttp3.Protocol r1 = okhttp3.Protocol.get(r1)     // Catch:{ AssertionError -> 0x00a1, all -> 0x0111 }
            goto L_0x0088
        L_0x0126:
            okhttp3.internal.platform.Platform r1 = okhttp3.internal.platform.Platform.get()
            r1.afterHandshake(r0)
            goto L_0x008c
        L_0x012f:
            java.io.IOException r2 = new java.io.IOException     // Catch:{ all -> 0x00ac }
            r2.<init>(r0)     // Catch:{ all -> 0x00ac }
            throw r2     // Catch:{ all -> 0x00ac }
        L_0x0135:
            okhttp3.internal.platform.Platform r2 = okhttp3.internal.platform.Platform.get()
            r2.afterHandshake(r1)
            goto L_0x00af
        L_0x013e:
            r0 = move-exception
            goto L_0x00a5
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connectTls(int, int, okhttp3.internal.connection.ConnectionSpecSelector):void");
    }

    private Request createTunnel(int i, int i2, Request request, HttpUrl httpUrl) throws IOException {
        Response build;
        String str = "CONNECT " + Util.hostHeader(httpUrl, true) + " HTTP/1.1";
        do {
            Http1Codec http1Codec = new Http1Codec((OkHttpClient) null, (StreamAllocation) null, this.source, this.sink);
            this.source.timeout().timeout((long) i, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) i2, TimeUnit.MILLISECONDS);
            http1Codec.writeRequest(request.headers(), str);
            http1Codec.finishRequest();
            build = http1Codec.readResponse().request(request).build();
            long contentLength = HttpHeaders.contentLength(build);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source newFixedLengthSource = http1Codec.newFixedLengthSource(contentLength);
            Util.skipAll(newFixedLengthSource, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            newFixedLengthSource.close();
            switch (build.code()) {
                case 200:
                    if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                        return null;
                    }
                    throw new IOException("TLS tunnel buffered too many bytes!");
                case 407:
                    request = this.route.address().proxyAuthenticator().authenticate(this.route, build);
                    if (request != null) {
                        break;
                    } else {
                        throw new IOException("Failed to authenticate with proxy");
                    }
                default:
                    throw new IOException("Unexpected response code for CONNECT: " + build.code());
            }
        } while (!"close".equalsIgnoreCase(build.header(com.google.common.net.HttpHeaders.CONNECTION)));
        return request;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header(com.google.common.net.HttpHeaders.HOST, Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header(com.google.common.net.HttpHeaders.USER_AGENT, Version.userAgent()).build();
    }

    private void establishProtocol(int i, int i2, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
        } else {
            connectTls(i, i2, connectionSpecSelector);
        }
        if (this.protocol != Protocol.HTTP_2) {
            this.allocationLimit = 1;
            return;
        }
        this.socket.setSoTimeout(0);
        Http2Connection build = new Http2Connection.Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).build();
        build.start();
        this.allocationLimit = build.maxConcurrentStreams();
        this.http2Connection = build;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public void connect(int i, int i2, int i3, List<ConnectionSpec> list, boolean z) {
        RouteException routeException;
        if (this.protocol == null) {
            ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(list);
            if (this.route.address().sslSocketFactory() != null) {
                routeException = null;
            } else if (list.contains(ConnectionSpec.CLEARTEXT)) {
                String host = this.route.address().url().host();
                if (Platform.get().isCleartextTrafficPermitted(host)) {
                    routeException = null;
                } else {
                    throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
                }
            } else {
                throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
            }
            while (this.protocol == null) {
                try {
                    if (!this.route.requiresTunnel()) {
                        buildConnection(i, i2, i3, connectionSpecSelector);
                    } else {
                        buildTunneledConnection(i, i2, i3, connectionSpecSelector);
                    }
                } catch (IOException e) {
                    Util.closeQuietly(this.socket);
                    Util.closeQuietly(this.rawSocket);
                    this.socket = null;
                    this.rawSocket = null;
                    this.source = null;
                    this.sink = null;
                    this.handshake = null;
                    this.protocol = null;
                    if (routeException != null) {
                        routeException.addConnectException(e);
                    } else {
                        routeException = new RouteException(e);
                    }
                    if (!z || !connectionSpecSelector.connectionFailed(e)) {
                        throw routeException;
                    }
                }
            }
            return;
        }
        throw new IllegalStateException("already connected");
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isHealthy(boolean z) {
        int soTimeout;
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            return !this.http2Connection.isShutdown();
        }
        if (z) {
            try {
                soTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (!this.source.exhausted()) {
                    this.socket.setSoTimeout(soTimeout);
                    return true;
                }
                this.socket.setSoTimeout(soTimeout);
                return false;
            } catch (SocketTimeoutException e) {
            } catch (IOException e2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(soTimeout);
                throw th;
            }
        }
        return true;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public void onSettings(Http2Connection http2Connection2) {
        this.allocationLimit = http2Connection2.maxConcurrentStreams();
    }

    public void onStream(Http2Stream http2Stream) throws IOException {
        http2Stream.close(ErrorCode.REFUSED_STREAM);
    }

    public Protocol protocol() {
        return this.http2Connection != null ? Protocol.HTTP_2 : this.protocol == null ? Protocol.HTTP_1_1 : this.protocol;
    }

    public Route route() {
        return this.route;
    }

    public Socket socket() {
        return this.socket;
    }

    public String toString() {
        return "Connection{" + this.route.address().url().host() + ":" + this.route.address().url().port() + ", proxy=" + this.route.proxy() + " hostAddress=" + this.route.socketAddress() + " cipherSuite=" + (this.handshake == null ? "none" : this.handshake.cipherSuite()) + " protocol=" + this.protocol + '}';
    }
}
