// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import javax.net.ssl.SSLSocketFactory;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Stream;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import okhttp3.internal.Version;
import okio.Source;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.ConnectionSpec;
import okhttp3.Address;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.tls.OkHostnameVerifier;
import java.security.cert.Certificate;
import okhttp3.CertificatePinner;
import java.security.cert.X509Certificate;
import okio.Okio;
import okhttp3.internal.platform.Platform;
import javax.net.ssl.SSLSocket;
import okhttp3.HttpUrl;
import okhttp3.Request;
import java.net.ProtocolException;
import okhttp3.internal.Util;
import java.io.IOException;
import java.util.ArrayList;
import okio.BufferedSource;
import okio.BufferedSink;
import okhttp3.Route;
import java.net.Socket;
import okhttp3.Protocol;
import okhttp3.Handshake;
import java.lang.ref.Reference;
import java.util.List;
import okhttp3.Connection;
import okhttp3.internal.http2.Http2Connection;

public final class RealConnection extends Listener implements Connection
{
    public int allocationLimit;
    public final List<Reference<StreamAllocation>> allocations;
    private Handshake handshake;
    public volatile Http2Connection http2Connection;
    public long idleAtNanos;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    public BufferedSink sink;
    public Socket socket;
    public BufferedSource source;
    public int successCount;
    
    public RealConnection(final Route route) {
        this.allocations = new ArrayList<Reference<StreamAllocation>>();
        this.idleAtNanos = Long.MAX_VALUE;
        this.route = route;
    }
    
    private void buildConnection(final int n, final int n2, final int n3, final ConnectionSpecSelector connectionSpecSelector) throws IOException {
        this.connectSocket(n, n2);
        this.establishProtocol(n2, n3, connectionSpecSelector);
    }
    
    private void buildTunneledConnection(final int n, final int n2, final int n3, final ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Request request = this.createTunnelRequest();
        final HttpUrl url = request.url();
        int n4 = 0;
        while (++n4 <= 21) {
            this.connectSocket(n, n2);
            request = this.createTunnel(n2, n3, request, url);
            if (request == null) {
                this.establishProtocol(n2, n3, connectionSpecSelector);
                return;
            }
            Util.closeQuietly(this.rawSocket);
            this.rawSocket = null;
            this.sink = null;
            this.source = null;
        }
        throw new ProtocolException("Too many tunnel connections attempted: " + 21);
    }
    
    private void connectSocket(final int p0, final int p1) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        okhttp3/internal/connection/RealConnection.route:Lokhttp3/Route;
        //     4: invokevirtual   okhttp3/Route.proxy:()Ljava/net/Proxy;
        //     7: astore_3       
        //     8: aload_0        
        //     9: getfield        okhttp3/internal/connection/RealConnection.route:Lokhttp3/Route;
        //    12: invokevirtual   okhttp3/Route.address:()Lokhttp3/Address;
        //    15: astore          4
        //    17: aload_3        
        //    18: invokevirtual   java/net/Proxy.type:()Ljava/net/Proxy$Type;
        //    21: getstatic       java/net/Proxy$Type.DIRECT:Ljava/net/Proxy$Type;
        //    24: if_acmpne       96
        //    27: aload           4
        //    29: invokevirtual   okhttp3/Address.socketFactory:()Ljavax/net/SocketFactory;
        //    32: invokevirtual   javax/net/SocketFactory.createSocket:()Ljava/net/Socket;
        //    35: astore_3       
        //    36: aload_0        
        //    37: aload_3        
        //    38: putfield        okhttp3/internal/connection/RealConnection.rawSocket:Ljava/net/Socket;
        //    41: aload_0        
        //    42: getfield        okhttp3/internal/connection/RealConnection.rawSocket:Ljava/net/Socket;
        //    45: iload_2        
        //    46: invokevirtual   java/net/Socket.setSoTimeout:(I)V
        //    49: invokestatic    okhttp3/internal/platform/Platform.get:()Lokhttp3/internal/platform/Platform;
        //    52: aload_0        
        //    53: getfield        okhttp3/internal/connection/RealConnection.rawSocket:Ljava/net/Socket;
        //    56: aload_0        
        //    57: getfield        okhttp3/internal/connection/RealConnection.route:Lokhttp3/Route;
        //    60: invokevirtual   okhttp3/Route.socketAddress:()Ljava/net/InetSocketAddress;
        //    63: iload_1        
        //    64: invokevirtual   okhttp3/internal/platform/Platform.connectSocket:(Ljava/net/Socket;Ljava/net/InetSocketAddress;I)V
        //    67: aload_0        
        //    68: aload_0        
        //    69: getfield        okhttp3/internal/connection/RealConnection.rawSocket:Ljava/net/Socket;
        //    72: invokestatic    okio/Okio.source:(Ljava/net/Socket;)Lokio/Source;
        //    75: invokestatic    okio/Okio.buffer:(Lokio/Source;)Lokio/BufferedSource;
        //    78: putfield        okhttp3/internal/connection/RealConnection.source:Lokio/BufferedSource;
        //    81: aload_0        
        //    82: aload_0        
        //    83: getfield        okhttp3/internal/connection/RealConnection.rawSocket:Ljava/net/Socket;
        //    86: invokestatic    okio/Okio.sink:(Ljava/net/Socket;)Lokio/Sink;
        //    89: invokestatic    okio/Okio.buffer:(Lokio/Sink;)Lokio/BufferedSink;
        //    92: putfield        okhttp3/internal/connection/RealConnection.sink:Lokio/BufferedSink;
        //    95: return         
        //    96: aload_3        
        //    97: invokevirtual   java/net/Proxy.type:()Ljava/net/Proxy$Type;
        //   100: getstatic       java/net/Proxy$Type.HTTP:Ljava/net/Proxy$Type;
        //   103: if_acmpeq       27
        //   106: new             Ljava/net/Socket;
        //   109: dup            
        //   110: aload_3        
        //   111: invokespecial   java/net/Socket.<init>:(Ljava/net/Proxy;)V
        //   114: astore_3       
        //   115: goto            36
        //   118: astore_3       
        //   119: new             Ljava/net/ConnectException;
        //   122: dup            
        //   123: new             Ljava/lang/StringBuilder;
        //   126: dup            
        //   127: invokespecial   java/lang/StringBuilder.<init>:()V
        //   130: ldc             "Failed to connect to "
        //   132: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   135: aload_0        
        //   136: getfield        okhttp3/internal/connection/RealConnection.route:Lokhttp3/Route;
        //   139: invokevirtual   okhttp3/Route.socketAddress:()Ljava/net/InetSocketAddress;
        //   142: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   145: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   148: invokespecial   java/net/ConnectException.<init>:(Ljava/lang/String;)V
        //   151: astore          4
        //   153: aload           4
        //   155: aload_3        
        //   156: invokevirtual   java/net/ConnectException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   159: pop            
        //   160: aload           4
        //   162: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                       
        //  -----  -----  -----  -----  ---------------------------
        //  49     67     118    163    Ljava/net/ConnectException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(invokevirtual:Type(Proxy::type, var_3_07:Proxy), getstatic:Type(Type::HTTP))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void connectTls(final int n, final int n2, final ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Object o = null;
        Object value = null;
        final String s = null;
        final Address address = this.route.address();
        Object sslSocketFactory = address.sslSocketFactory();
        while (true) {
            try {
                ConnectionSpec configureSecureSocket;
                String selectedProtocol;
                Protocol protocol;
                Object o2;
                final String s2;
                Label_0190_Outer:Label_0257_Outer:
                while (true) {
                    while (true) {
                    Label_0397:
                        while (true) {
                        Label_0389:
                            while (true) {
                                try {
                                    sslSocketFactory = ((SSLSocketFactory)sslSocketFactory).createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
                                    Label_0264: {
                                        try {
                                            configureSecureSocket = connectionSpecSelector.configureSecureSocket((SSLSocket)sslSocketFactory);
                                            if (configureSecureSocket.supportsTlsExtensions()) {
                                                Platform.get().configureTlsExtensions((SSLSocket)sslSocketFactory, address.url().host(), address.protocols());
                                            }
                                            ((SSLSocket)sslSocketFactory).startHandshake();
                                            value = Handshake.get(((SSLSocket)sslSocketFactory).getSession());
                                            if (!address.hostnameVerifier().verify(address.url().host(), ((SSLSocket)sslSocketFactory).getSession())) {
                                                break Label_0264;
                                            }
                                            address.certificatePinner().check(address.url().host(), ((Handshake)value).peerCertificates());
                                            if (configureSecureSocket.supportsTlsExtensions()) {}
                                            selectedProtocol = s;
                                            this.socket = (Socket)sslSocketFactory;
                                            this.source = Okio.buffer(Okio.source(this.socket));
                                            this.sink = Okio.buffer(Okio.sink(this.socket));
                                            this.handshake = (Handshake)value;
                                            if (selectedProtocol != null) {
                                                break Label_0389;
                                            }
                                            protocol = Protocol.HTTP_1_1;
                                            this.protocol = protocol;
                                            if (sslSocketFactory == null) {
                                                return;
                                            }
                                            break Label_0397;
                                        }
                                        catch (final AssertionError value) {
                                            o2 = sslSocketFactory;
                                            sslSocketFactory = value;
                                            value = o2;
                                            if (!Util.isAndroidGetsocknameError((AssertionError)sslSocketFactory)) {
                                                value = o2;
                                                throw sslSocketFactory;
                                            }
                                            break;
                                            o = ((Handshake)value).peerCertificates().get(0);
                                            value = new StringBuilder();
                                            throw new SSLPeerUnverifiedException(((StringBuilder)value).append("Hostname ").append(address.url().host()).append(" not verified:\n    certificate: ").append(CertificatePinner.pin((Certificate)o)).append("\n    DN: ").append(((X509Certificate)o).getSubjectDN().getName()).append("\n    subjectAltNames: ").append(OkHostnameVerifier.allSubjectAltNames((X509Certificate)o)).toString());
                                            Util.closeQuietly((Socket)value);
                                            throw s2;
                                            iftrue(Label_0432:)(value != null);
                                        }
                                        finally {
                                            value = sslSocketFactory;
                                        }
                                    }
                                }
                                finally {}
                                selectedProtocol = Platform.get().getSelectedProtocol((SSLSocket)sslSocketFactory);
                                continue Label_0190_Outer;
                            }
                            protocol = Protocol.get(s2);
                            continue Label_0257_Outer;
                        }
                        Platform.get().afterHandshake((SSLSocket)sslSocketFactory);
                        return;
                        Label_0432: {
                            Platform.get().afterHandshake((SSLSocket)value);
                        }
                        continue;
                    }
                }
                value = s2;
                o = new(java.io.IOException.class)();
                value = s2;
                new IOException((Throwable)sslSocketFactory);
                value = s2;
                throw o;
            }
            catch (final AssertionError sslSocketFactory) {
                final Object o2 = o;
                continue;
            }
            break;
        }
    }
    
    private Request createTunnel(final int n, final int n2, Request request, final HttpUrl httpUrl) throws IOException {
        final String string = "CONNECT " + Util.hostHeader(httpUrl, true) + " HTTP/1.1";
        Response build;
        Request authenticate = null;
        do {
            final Http1Codec http1Codec = new Http1Codec(null, null, this.source, this.sink);
            this.source.timeout().timeout(n, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout(n2, TimeUnit.MILLISECONDS);
            http1Codec.writeRequest(request.headers(), string);
            http1Codec.finishRequest();
            build = http1Codec.readResponse().request(request).build();
            long contentLength;
            if ((contentLength = HttpHeaders.contentLength(build)) == -1L) {
                contentLength = 0L;
            }
            final Source fixedLengthSource = http1Codec.newFixedLengthSource(contentLength);
            Util.skipAll(fixedLengthSource, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            fixedLengthSource.close();
            switch (build.code()) {
                default: {
                    throw new IOException("Unexpected response code for CONNECT: " + build.code());
                }
                case 200: {
                    if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                        return null;
                    }
                    throw new IOException("TLS tunnel buffered too many bytes!");
                }
                case 407: {
                    authenticate = this.route.address().proxyAuthenticator().authenticate(this.route, build);
                    if (authenticate != null) {
                        request = authenticate;
                        continue;
                    }
                    throw new IOException("Failed to authenticate with proxy");
                }
            }
        } while (!"close".equalsIgnoreCase(build.header("Connection")));
        return authenticate;
    }
    
    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }
    
    private void establishProtocol(final int n, final int n2, final ConnectionSpecSelector connectionSpecSelector) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
        }
        else {
            this.connectTls(n, n2, connectionSpecSelector);
        }
        if (this.protocol != Protocol.HTTP_2) {
            this.allocationLimit = 1;
        }
        else {
            this.socket.setSoTimeout(0);
            final Http2Connection build = new Http2Connection.Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).build();
            build.start();
            this.allocationLimit = build.maxConcurrentStreams();
            this.http2Connection = build;
        }
    }
    
    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }
    
    public void connect(final int n, final int n2, final int n3, List<ConnectionSpec> o, final boolean b) {
        if (this.protocol == null) {
            final ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector((List<ConnectionSpec>)o);
            if (this.route.address().sslSocketFactory() != null) {
                o = null;
            }
            else {
                if (!((List)o).contains(ConnectionSpec.CLEARTEXT)) {
                    throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
                }
                final String host = this.route.address().url().host();
                if (!Platform.get().isCleartextTrafficPermitted(host)) {
                    throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
                }
                o = null;
            }
            while (this.protocol == null) {
                Label_0246: {
                    try {
                        if (this.route.requiresTunnel()) {
                            break Label_0246;
                        }
                        this.buildConnection(n, n2, n3, connectionSpecSelector);
                    }
                    catch (final IOException ex) {
                        Util.closeQuietly(this.socket);
                        Util.closeQuietly(this.rawSocket);
                        this.socket = null;
                        this.rawSocket = null;
                        this.source = null;
                        this.sink = null;
                        this.handshake = null;
                        this.protocol = null;
                        if (o != null) {
                            ((RouteException)o).addConnectException(ex);
                        }
                        else {
                            o = new RouteException(ex);
                        }
                        if (b && connectionSpecSelector.connectionFailed(ex)) {
                            continue;
                        }
                        throw o;
                        this.buildTunneledConnection(n, n2, n3, connectionSpecSelector);
                    }
                }
            }
            return;
        }
        throw new IllegalStateException("already connected");
    }
    
    @Override
    public Handshake handshake() {
        return this.handshake;
    }
    
    public boolean isHealthy(final boolean b) {
        final boolean b2 = false;
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection == null) {
            if (b) {
                try {
                    final int soTimeout = this.socket.getSoTimeout();
                    try {
                        this.socket.setSoTimeout(1);
                        return !this.source.exhausted();
                    }
                    finally {
                        this.socket.setSoTimeout(soTimeout);
                    }
                }
                catch (final SocketTimeoutException ex) {}
                catch (final IOException ex2) {
                    return false;
                }
            }
            return true;
        }
        return !this.http2Connection.isShutdown() || b2;
    }
    
    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }
    
    @Override
    public void onSettings(final Http2Connection http2Connection) {
        this.allocationLimit = http2Connection.maxConcurrentStreams();
    }
    
    @Override
    public void onStream(final Http2Stream http2Stream) throws IOException {
        http2Stream.close(ErrorCode.REFUSED_STREAM);
    }
    
    @Override
    public Protocol protocol() {
        if (this.http2Connection != null) {
            return Protocol.HTTP_2;
        }
        Protocol protocol;
        if (this.protocol == null) {
            protocol = Protocol.HTTP_1_1;
        }
        else {
            protocol = this.protocol;
        }
        return protocol;
    }
    
    @Override
    public Route route() {
        return this.route;
    }
    
    @Override
    public Socket socket() {
        return this.socket;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("Connection{").append(this.route.address().url().host()).append(":").append(this.route.address().url().port()).append(", proxy=").append(this.route.proxy()).append(" hostAddress=").append(this.route.socketAddress()).append(" cipherSuite=");
        Object cipherSuite;
        if (this.handshake == null) {
            cipherSuite = "none";
        }
        else {
            cipherSuite = this.handshake.cipherSuite();
        }
        return append.append(cipherSuite).append(" protocol=").append(this.protocol).append('}').toString();
    }
}
