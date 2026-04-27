// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.platform.Platform;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import okhttp3.internal.tls.OkHostnameVerifier;
import java.util.ArrayList;
import java.util.Random;
import okhttp3.internal.ws.RealWebSocket;
import java.util.Arrays;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.util.Iterator;
import okhttp3.internal.connection.RouteDatabase;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.connection.RealConnection;
import javax.net.ssl.SSLSocket;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import javax.net.ssl.SSLSocketFactory;
import javax.net.SocketFactory;
import java.net.ProxySelector;
import java.net.Proxy;
import okhttp3.internal.cache.InternalCache;
import javax.net.ssl.HostnameVerifier;
import okhttp3.internal.tls.CertificateChainCleaner;
import java.util.List;

public class OkHttpClient implements Cloneable, Call.Factory, WebSocket.Factory
{
    static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS;
    static final List<Protocol> DEFAULT_PROTOCOLS;
    final Authenticator authenticator;
    final Cache cache;
    final CertificateChainCleaner certificateChainCleaner;
    final CertificatePinner certificatePinner;
    final int connectTimeout;
    final ConnectionPool connectionPool;
    final List<ConnectionSpec> connectionSpecs;
    final CookieJar cookieJar;
    final Dispatcher dispatcher;
    final Dns dns;
    final boolean followRedirects;
    final boolean followSslRedirects;
    final HostnameVerifier hostnameVerifier;
    final List<Interceptor> interceptors;
    final InternalCache internalCache;
    final List<Interceptor> networkInterceptors;
    final int pingInterval;
    final List<Protocol> protocols;
    final Proxy proxy;
    final Authenticator proxyAuthenticator;
    final ProxySelector proxySelector;
    final int readTimeout;
    final boolean retryOnConnectionFailure;
    final SocketFactory socketFactory;
    final SSLSocketFactory sslSocketFactory;
    final int writeTimeout;
    
    static {
        DEFAULT_PROTOCOLS = Util.immutableList(Protocol.HTTP_2, Protocol.HTTP_1_1);
        DEFAULT_CONNECTION_SPECS = Util.immutableList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT);
        Internal.instance = new Internal() {
            @Override
            public void addLenient(final Headers.Builder builder, final String s) {
                builder.addLenient(s);
            }
            
            @Override
            public void addLenient(final Headers.Builder builder, final String s, final String s2) {
                builder.addLenient(s, s2);
            }
            
            @Override
            public void apply(final ConnectionSpec connectionSpec, final SSLSocket sslSocket, final boolean b) {
                connectionSpec.apply(sslSocket, b);
            }
            
            @Override
            public boolean connectionBecameIdle(final ConnectionPool connectionPool, final RealConnection realConnection) {
                return connectionPool.connectionBecameIdle(realConnection);
            }
            
            @Override
            public RealConnection get(final ConnectionPool connectionPool, final Address address, final StreamAllocation streamAllocation) {
                return connectionPool.get(address, streamAllocation);
            }
            
            @Override
            public HttpUrl getHttpUrlChecked(final String s) throws MalformedURLException, UnknownHostException {
                return HttpUrl.getChecked(s);
            }
            
            @Override
            public Call newWebSocketCall(final OkHttpClient okHttpClient, final Request request) {
                return new RealCall(okHttpClient, request, true);
            }
            
            @Override
            public void put(final ConnectionPool connectionPool, final RealConnection realConnection) {
                connectionPool.put(realConnection);
            }
            
            @Override
            public RouteDatabase routeDatabase(final ConnectionPool connectionPool) {
                return connectionPool.routeDatabase;
            }
            
            @Override
            public void setCache(final Builder builder, final InternalCache internalCache) {
                builder.setInternalCache(internalCache);
            }
            
            @Override
            public StreamAllocation streamAllocation(final Call call) {
                return ((RealCall)call).streamAllocation();
            }
        };
    }
    
    public OkHttpClient() {
        this(new Builder());
    }
    
    OkHttpClient(final Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.proxy = builder.proxy;
        this.protocols = builder.protocols;
        this.connectionSpecs = builder.connectionSpecs;
        this.interceptors = Util.immutableList(builder.interceptors);
        this.networkInterceptors = Util.immutableList(builder.networkInterceptors);
        this.proxySelector = builder.proxySelector;
        this.cookieJar = builder.cookieJar;
        this.cache = builder.cache;
        this.internalCache = builder.internalCache;
        this.socketFactory = builder.socketFactory;
        final Iterator<ConnectionSpec> iterator = this.connectionSpecs.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final ConnectionSpec connectionSpec = iterator.next();
            if (n == 0 && !connectionSpec.isTls()) {
                n = 0;
            }
            else {
                n = 1;
            }
        }
        if (builder.sslSocketFactory == null && n != 0) {
            final X509TrustManager systemDefaultTrustManager = this.systemDefaultTrustManager();
            this.sslSocketFactory = this.systemDefaultSslSocketFactory(systemDefaultTrustManager);
            this.certificateChainCleaner = CertificateChainCleaner.get(systemDefaultTrustManager);
        }
        else {
            this.sslSocketFactory = builder.sslSocketFactory;
            this.certificateChainCleaner = builder.certificateChainCleaner;
        }
        this.hostnameVerifier = builder.hostnameVerifier;
        this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(this.certificateChainCleaner);
        this.proxyAuthenticator = builder.proxyAuthenticator;
        this.authenticator = builder.authenticator;
        this.connectionPool = builder.connectionPool;
        this.dns = builder.dns;
        this.followSslRedirects = builder.followSslRedirects;
        this.followRedirects = builder.followRedirects;
        this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.pingInterval = builder.pingInterval;
    }
    
    private SSLSocketFactory systemDefaultSslSocketFactory(final X509TrustManager x509TrustManager) {
        try {
            final SSLContext instance = SSLContext.getInstance("TLS");
            instance.init(null, new TrustManager[] { x509TrustManager }, null);
            return instance.getSocketFactory();
        }
        catch (final GeneralSecurityException ex) {
            throw new AssertionError();
        }
    }
    
    private X509TrustManager systemDefaultTrustManager() {
        TrustManager[] trustManagers;
        while (true) {
            while (true) {
                try {
                    final TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    instance.init((KeyStore)null);
                    trustManagers = instance.getTrustManagers();
                    if (trustManagers.length != 1) {
                        throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                    }
                }
                catch (final GeneralSecurityException ex) {
                    throw new AssertionError();
                }
                if (trustManagers[0] instanceof X509TrustManager) {
                    break;
                }
                continue;
            }
        }
        return (X509TrustManager)trustManagers[0];
    }
    
    public Authenticator authenticator() {
        return this.authenticator;
    }
    
    public Cache cache() {
        return this.cache;
    }
    
    public CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }
    
    public int connectTimeoutMillis() {
        return this.connectTimeout;
    }
    
    public ConnectionPool connectionPool() {
        return this.connectionPool;
    }
    
    public List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }
    
    public CookieJar cookieJar() {
        return this.cookieJar;
    }
    
    public Dispatcher dispatcher() {
        return this.dispatcher;
    }
    
    public Dns dns() {
        return this.dns;
    }
    
    public boolean followRedirects() {
        return this.followRedirects;
    }
    
    public boolean followSslRedirects() {
        return this.followSslRedirects;
    }
    
    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public List<Interceptor> interceptors() {
        return this.interceptors;
    }
    
    InternalCache internalCache() {
        InternalCache internalCache;
        if (this.cache == null) {
            internalCache = this.internalCache;
        }
        else {
            internalCache = this.cache.internalCache;
        }
        return internalCache;
    }
    
    public List<Interceptor> networkInterceptors() {
        return this.networkInterceptors;
    }
    
    public Builder newBuilder() {
        return new Builder(this);
    }
    
    @Override
    public Call newCall(final Request request) {
        return new RealCall(this, request, false);
    }
    
    @Override
    public WebSocket newWebSocket(final Request request, final WebSocketListener webSocketListener) {
        final RealWebSocket realWebSocket = new RealWebSocket(request, webSocketListener, new SecureRandom());
        realWebSocket.connect(this);
        return realWebSocket;
    }
    
    public int pingIntervalMillis() {
        return this.pingInterval;
    }
    
    public List<Protocol> protocols() {
        return this.protocols;
    }
    
    public Proxy proxy() {
        return this.proxy;
    }
    
    public Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }
    
    public ProxySelector proxySelector() {
        return this.proxySelector;
    }
    
    public int readTimeoutMillis() {
        return this.readTimeout;
    }
    
    public boolean retryOnConnectionFailure() {
        return this.retryOnConnectionFailure;
    }
    
    public SocketFactory socketFactory() {
        return this.socketFactory;
    }
    
    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }
    
    public int writeTimeoutMillis() {
        return this.writeTimeout;
    }
    
    public static final class Builder
    {
        Authenticator authenticator;
        Cache cache;
        CertificateChainCleaner certificateChainCleaner;
        CertificatePinner certificatePinner;
        int connectTimeout;
        ConnectionPool connectionPool;
        List<ConnectionSpec> connectionSpecs;
        CookieJar cookieJar;
        Dispatcher dispatcher;
        Dns dns;
        boolean followRedirects;
        boolean followSslRedirects;
        HostnameVerifier hostnameVerifier;
        final List<Interceptor> interceptors;
        InternalCache internalCache;
        final List<Interceptor> networkInterceptors;
        int pingInterval;
        List<Protocol> protocols;
        Proxy proxy;
        Authenticator proxyAuthenticator;
        ProxySelector proxySelector;
        int readTimeout;
        boolean retryOnConnectionFailure;
        SocketFactory socketFactory;
        SSLSocketFactory sslSocketFactory;
        int writeTimeout;
        
        public Builder() {
            this.interceptors = new ArrayList<Interceptor>();
            this.networkInterceptors = new ArrayList<Interceptor>();
            this.dispatcher = new Dispatcher();
            this.protocols = OkHttpClient.DEFAULT_PROTOCOLS;
            this.connectionSpecs = OkHttpClient.DEFAULT_CONNECTION_SPECS;
            this.proxySelector = ProxySelector.getDefault();
            this.cookieJar = CookieJar.NO_COOKIES;
            this.socketFactory = SocketFactory.getDefault();
            this.hostnameVerifier = OkHostnameVerifier.INSTANCE;
            this.certificatePinner = CertificatePinner.DEFAULT;
            this.proxyAuthenticator = Authenticator.NONE;
            this.authenticator = Authenticator.NONE;
            this.connectionPool = new ConnectionPool();
            this.dns = Dns.SYSTEM;
            this.followSslRedirects = true;
            this.followRedirects = true;
            this.retryOnConnectionFailure = true;
            this.connectTimeout = 10000;
            this.readTimeout = 10000;
            this.writeTimeout = 10000;
            this.pingInterval = 0;
        }
        
        Builder(final OkHttpClient okHttpClient) {
            this.interceptors = new ArrayList<Interceptor>();
            this.networkInterceptors = new ArrayList<Interceptor>();
            this.dispatcher = okHttpClient.dispatcher;
            this.proxy = okHttpClient.proxy;
            this.protocols = okHttpClient.protocols;
            this.connectionSpecs = okHttpClient.connectionSpecs;
            this.interceptors.addAll(okHttpClient.interceptors);
            this.networkInterceptors.addAll(okHttpClient.networkInterceptors);
            this.proxySelector = okHttpClient.proxySelector;
            this.cookieJar = okHttpClient.cookieJar;
            this.internalCache = okHttpClient.internalCache;
            this.cache = okHttpClient.cache;
            this.socketFactory = okHttpClient.socketFactory;
            this.sslSocketFactory = okHttpClient.sslSocketFactory;
            this.certificateChainCleaner = okHttpClient.certificateChainCleaner;
            this.hostnameVerifier = okHttpClient.hostnameVerifier;
            this.certificatePinner = okHttpClient.certificatePinner;
            this.proxyAuthenticator = okHttpClient.proxyAuthenticator;
            this.authenticator = okHttpClient.authenticator;
            this.connectionPool = okHttpClient.connectionPool;
            this.dns = okHttpClient.dns;
            this.followSslRedirects = okHttpClient.followSslRedirects;
            this.followRedirects = okHttpClient.followRedirects;
            this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure;
            this.connectTimeout = okHttpClient.connectTimeout;
            this.readTimeout = okHttpClient.readTimeout;
            this.writeTimeout = okHttpClient.writeTimeout;
            this.pingInterval = okHttpClient.pingInterval;
        }
        
        private static int checkDuration(final String str, final long duration, final TimeUnit timeUnit) {
            final int n = 1;
            int n2;
            if (duration >= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                throw new IllegalArgumentException(str + " < 0");
            }
            if (timeUnit == null) {
                throw new NullPointerException("unit == null");
            }
            final long millis = timeUnit.toMillis(duration);
            int n3;
            if (millis <= 2147483647L) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                throw new IllegalArgumentException(str + " too large.");
            }
            if (millis == 0L) {
                int n4;
                if (duration <= 0L) {
                    n4 = n;
                }
                else {
                    n4 = 0;
                }
                if (n4 == 0) {
                    throw new IllegalArgumentException(str + " too small.");
                }
            }
            return (int)millis;
        }
        
        public Builder addInterceptor(final Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }
        
        public Builder addNetworkInterceptor(final Interceptor interceptor) {
            this.networkInterceptors.add(interceptor);
            return this;
        }
        
        public Builder authenticator(final Authenticator authenticator) {
            if (authenticator != null) {
                this.authenticator = authenticator;
                return this;
            }
            throw new NullPointerException("authenticator == null");
        }
        
        public OkHttpClient build() {
            return new OkHttpClient(this);
        }
        
        public Builder cache(final Cache cache) {
            this.cache = cache;
            this.internalCache = null;
            return this;
        }
        
        public Builder certificatePinner(final CertificatePinner certificatePinner) {
            if (certificatePinner != null) {
                this.certificatePinner = certificatePinner;
                return this;
            }
            throw new NullPointerException("certificatePinner == null");
        }
        
        public Builder connectTimeout(final long n, final TimeUnit timeUnit) {
            this.connectTimeout = checkDuration("timeout", n, timeUnit);
            return this;
        }
        
        public Builder connectionPool(final ConnectionPool connectionPool) {
            if (connectionPool != null) {
                this.connectionPool = connectionPool;
                return this;
            }
            throw new NullPointerException("connectionPool == null");
        }
        
        public Builder connectionSpecs(final List<ConnectionSpec> list) {
            this.connectionSpecs = Util.immutableList(list);
            return this;
        }
        
        public Builder cookieJar(final CookieJar cookieJar) {
            if (cookieJar != null) {
                this.cookieJar = cookieJar;
                return this;
            }
            throw new NullPointerException("cookieJar == null");
        }
        
        public Builder dispatcher(final Dispatcher dispatcher) {
            if (dispatcher != null) {
                this.dispatcher = dispatcher;
                return this;
            }
            throw new IllegalArgumentException("dispatcher == null");
        }
        
        public Builder dns(final Dns dns) {
            if (dns != null) {
                this.dns = dns;
                return this;
            }
            throw new NullPointerException("dns == null");
        }
        
        public Builder followRedirects(final boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }
        
        public Builder followSslRedirects(final boolean followSslRedirects) {
            this.followSslRedirects = followSslRedirects;
            return this;
        }
        
        public Builder hostnameVerifier(final HostnameVerifier hostnameVerifier) {
            if (hostnameVerifier != null) {
                this.hostnameVerifier = hostnameVerifier;
                return this;
            }
            throw new NullPointerException("hostnameVerifier == null");
        }
        
        public List<Interceptor> interceptors() {
            return this.interceptors;
        }
        
        public List<Interceptor> networkInterceptors() {
            return this.networkInterceptors;
        }
        
        public Builder pingInterval(final long n, final TimeUnit timeUnit) {
            this.pingInterval = checkDuration("interval", n, timeUnit);
            return this;
        }
        
        public Builder protocols(final List<Protocol> c) {
            final ArrayList list = new ArrayList((Collection<? extends E>)c);
            if (!list.contains(Protocol.HTTP_1_1)) {
                throw new IllegalArgumentException("protocols doesn't contain http/1.1: " + list);
            }
            if (list.contains(Protocol.HTTP_1_0)) {
                throw new IllegalArgumentException("protocols must not contain http/1.0: " + list);
            }
            if (!list.contains(null)) {
                if (list.contains(Protocol.SPDY_3)) {
                    list.remove(Protocol.SPDY_3);
                }
                this.protocols = (List<Protocol>)Collections.unmodifiableList((List<?>)list);
                return this;
            }
            throw new IllegalArgumentException("protocols must not contain null");
        }
        
        public Builder proxy(final Proxy proxy) {
            this.proxy = proxy;
            return this;
        }
        
        public Builder proxyAuthenticator(final Authenticator proxyAuthenticator) {
            if (proxyAuthenticator != null) {
                this.proxyAuthenticator = proxyAuthenticator;
                return this;
            }
            throw new NullPointerException("proxyAuthenticator == null");
        }
        
        public Builder proxySelector(final ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            return this;
        }
        
        public Builder readTimeout(final long n, final TimeUnit timeUnit) {
            this.readTimeout = checkDuration("timeout", n, timeUnit);
            return this;
        }
        
        public Builder retryOnConnectionFailure(final boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }
        
        void setInternalCache(final InternalCache internalCache) {
            this.internalCache = internalCache;
            this.cache = null;
        }
        
        public Builder socketFactory(final SocketFactory socketFactory) {
            if (socketFactory != null) {
                this.socketFactory = socketFactory;
                return this;
            }
            throw new NullPointerException("socketFactory == null");
        }
        
        public Builder sslSocketFactory(final SSLSocketFactory sslSocketFactory) {
            if (sslSocketFactory == null) {
                throw new NullPointerException("sslSocketFactory == null");
            }
            final X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
            if (trustManager != null) {
                this.sslSocketFactory = sslSocketFactory;
                this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
                return this;
            }
            throw new IllegalStateException("Unable to extract the trust manager on " + Platform.get() + ", sslSocketFactory is " + sslSocketFactory.getClass());
        }
        
        public Builder sslSocketFactory(final SSLSocketFactory sslSocketFactory, final X509TrustManager x509TrustManager) {
            if (sslSocketFactory == null) {
                throw new NullPointerException("sslSocketFactory == null");
            }
            if (x509TrustManager != null) {
                this.sslSocketFactory = sslSocketFactory;
                this.certificateChainCleaner = CertificateChainCleaner.get(x509TrustManager);
                return this;
            }
            throw new NullPointerException("trustManager == null");
        }
        
        public Builder writeTimeout(final long n, final TimeUnit timeUnit) {
            this.writeTimeout = checkDuration("timeout", n, timeUnit);
            return this;
        }
    }
}
