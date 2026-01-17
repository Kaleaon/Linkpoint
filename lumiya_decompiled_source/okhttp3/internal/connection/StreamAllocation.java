package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.StreamResetException;
/* loaded from: classes.dex */
public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled;
    public final Address address;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private int refusedStreamCount;
    private boolean released;
    private Route route;
    private final RouteSelector routeSelector;

    /* loaded from: classes.dex */
    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation streamAllocation, Object obj) {
            super(streamAllocation);
            this.callStackTrace = obj;
        }
    }

    static {
        $assertionsDisabled = StreamAllocation.class.desiredAssertionStatus() ? false : true;
    }

    public StreamAllocation(ConnectionPool connectionPool, Address address, Object obj) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.routeSelector = new RouteSelector(address, routeDatabase());
        this.callStackTrace = obj;
    }

    private void deallocate(boolean z, boolean z2, boolean z3) {
        RealConnection realConnection = null;
        synchronized (this.connectionPool) {
            if (z3) {
                this.codec = null;
            }
            if (z2) {
                this.released = true;
            }
            if (this.connection != null) {
                if (z) {
                    this.connection.noNewStreams = true;
                }
                if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                    release(this.connection);
                    if (this.connection.allocations.isEmpty()) {
                        this.connection.idleAtNanos = System.nanoTime();
                        if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                            realConnection = this.connection;
                        }
                    }
                    this.connection = null;
                }
            }
        }
        if (realConnection == null) {
            return;
        }
        Util.closeQuietly(realConnection.socket());
    }

    private RealConnection findConnection(int i, int i2, int i3, boolean z) throws IOException {
        Route route;
        synchronized (this.connectionPool) {
            if (this.released) {
                throw new IllegalStateException("released");
            }
            if (this.codec == null) {
                if (this.canceled) {
                    throw new IOException("Canceled");
                }
                RealConnection realConnection = this.connection;
                if (realConnection == null || realConnection.noNewStreams) {
                    RealConnection realConnection2 = Internal.instance.get(this.connectionPool, this.address, this);
                    if (realConnection2 != null) {
                        this.connection = realConnection2;
                        return realConnection2;
                    }
                    Route route2 = this.route;
                    if (route2 != null) {
                        route = route2;
                    } else {
                        Route next = this.routeSelector.next();
                        synchronized (this.connectionPool) {
                            this.route = next;
                            this.refusedStreamCount = 0;
                        }
                        route = next;
                    }
                    RealConnection realConnection3 = new RealConnection(route);
                    synchronized (this.connectionPool) {
                        acquire(realConnection3);
                        Internal.instance.put(this.connectionPool, realConnection3);
                        this.connection = realConnection3;
                        if (this.canceled) {
                            throw new IOException("Canceled");
                        }
                    }
                    realConnection3.connect(i, i2, i3, this.address.connectionSpecs(), z);
                    routeDatabase().connected(realConnection3.route());
                    return realConnection3;
                }
                return realConnection;
            }
            throw new IllegalStateException("codec != null");
        }
    }

    private RealConnection findHealthyConnection(int i, int i2, int i3, boolean z, boolean z2) throws IOException {
        while (true) {
            RealConnection findConnection = findConnection(i, i2, i3, z);
            synchronized (this.connectionPool) {
                if (findConnection.successCount == 0) {
                    return findConnection;
                }
                if (findConnection.isHealthy(z2)) {
                    return findConnection;
                }
                noNewStreams();
            }
        }
    }

    private void release(RealConnection realConnection) {
        int size = realConnection.allocations.size();
        for (int i = 0; i < size; i++) {
            if (realConnection.allocations.get(i).get() == this) {
                realConnection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public void acquire(RealConnection realConnection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.connectionPool)) {
            throw new AssertionError();
        }
        realConnection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
    }

    public void cancel() {
        HttpCodec httpCodec;
        RealConnection realConnection;
        synchronized (this.connectionPool) {
            this.canceled = true;
            httpCodec = this.codec;
            realConnection = this.connection;
        }
        if (httpCodec != null) {
            httpCodec.cancel();
        } else if (realConnection == null) {
        } else {
            realConnection.cancel();
        }
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public boolean hasMoreRoutes() {
        return this.route != null || this.routeSelector.hasNext();
    }

    public HttpCodec newStream(OkHttpClient okHttpClient, boolean z) {
        HttpCodec http2Codec;
        int connectTimeoutMillis = okHttpClient.connectTimeoutMillis();
        int readTimeoutMillis = okHttpClient.readTimeoutMillis();
        int writeTimeoutMillis = okHttpClient.writeTimeoutMillis();
        try {
            RealConnection findHealthyConnection = findHealthyConnection(connectTimeoutMillis, readTimeoutMillis, writeTimeoutMillis, okHttpClient.retryOnConnectionFailure(), z);
            if (findHealthyConnection.http2Connection == null) {
                findHealthyConnection.socket().setSoTimeout(readTimeoutMillis);
                findHealthyConnection.source.timeout().timeout(readTimeoutMillis, TimeUnit.MILLISECONDS);
                findHealthyConnection.sink.timeout().timeout(writeTimeoutMillis, TimeUnit.MILLISECONDS);
                http2Codec = new Http1Codec(okHttpClient, this, findHealthyConnection.source, findHealthyConnection.sink);
            } else {
                http2Codec = new Http2Codec(okHttpClient, this, findHealthyConnection.http2Connection);
            }
            synchronized (this.connectionPool) {
                this.codec = http2Codec;
            }
            return http2Codec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    public void noNewStreams() {
        deallocate(true, false, false);
    }

    public void release() {
        deallocate(false, true, false);
    }

    public void streamFailed(IOException iOException) {
        boolean z;
        synchronized (this.connectionPool) {
            if (iOException instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) iOException;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM && this.refusedStreamCount <= 1) {
                    z = false;
                } else {
                    this.route = null;
                    z = true;
                }
            } else if ((this.connection == null || this.connection.isMultiplexed()) && !(iOException instanceof ConnectionShutdownException)) {
                z = false;
            } else if (this.connection.successCount != 0) {
                z = true;
            } else {
                if (this.route != null && iOException != null) {
                    this.routeSelector.connectFailed(this.route, iOException);
                }
                this.route = null;
                z = true;
            }
        }
        deallocate(z, false, true);
    }

    public void streamFinished(boolean z, HttpCodec httpCodec) {
        synchronized (this.connectionPool) {
            if (httpCodec != null) {
                if (httpCodec == this.codec) {
                    if (!z) {
                        this.connection.successCount++;
                    }
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + httpCodec);
        }
        deallocate(z, false, true);
    }

    public String toString() {
        return this.address.toString();
    }
}
