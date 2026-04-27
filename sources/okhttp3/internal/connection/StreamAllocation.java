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

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation streamAllocation, Object obj) {
            super(streamAllocation);
            this.callStackTrace = obj;
        }
    }

    static {
        boolean z = false;
        if (!StreamAllocation.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
    }

    public StreamAllocation(ConnectionPool connectionPool2, Address address2, Object obj) {
        this.connectionPool = connectionPool2;
        this.address = address2;
        this.routeSelector = new RouteSelector(address2, routeDatabase());
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
                if (this.codec == null) {
                    if (this.released || this.connection.noNewStreams) {
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
        }
        if (realConnection != null) {
            Util.closeQuietly(realConnection.socket());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0022, code lost:
        if (r0 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0024, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0025, code lost:
        r0 = new okhttp3.internal.connection.RealConnection(r1);
        r1 = r6.connectionPool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002c, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        acquire(r0);
        okhttp3.internal.Internal.instance.put(r6.connectionPool, r0);
        r6.connection = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        if (r6.canceled != false) goto L_0x0093;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        r0.connect(r7, r8, r9, r6.address.connectionSpecs(), r10);
        routeDatabase().connected(r0.route());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0056, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007f, code lost:
        r0 = r6.routeSelector.next();
        r1 = r6.connectionPool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0087, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        r6.route = r0;
        r6.refusedStreamCount = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x008d, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x008e, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x009b, code lost:
        throw new java.io.IOException("Canceled");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r7, int r8, int r9, boolean r10) throws java.io.IOException {
        /*
            r6 = this;
            okhttp3.ConnectionPool r1 = r6.connectionPool
            monitor-enter(r1)
            boolean r0 = r6.released     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x0057
            okhttp3.internal.http.HttpCodec r0 = r6.codec     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x0063
            boolean r0 = r6.canceled     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x006c
            okhttp3.internal.connection.RealConnection r0 = r6.connection     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x0075
        L_0x0013:
            okhttp3.internal.Internal r0 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x0060 }
            okhttp3.ConnectionPool r2 = r6.connectionPool     // Catch:{ all -> 0x0060 }
            okhttp3.Address r3 = r6.address     // Catch:{ all -> 0x0060 }
            okhttp3.internal.connection.RealConnection r0 = r0.get(r2, r3, r6)     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x007b
            okhttp3.Route r0 = r6.route     // Catch:{ all -> 0x0060 }
            monitor-exit(r1)     // Catch:{ all -> 0x0060 }
            if (r0 == 0) goto L_0x007f
            r1 = r0
        L_0x0025:
            okhttp3.internal.connection.RealConnection r0 = new okhttp3.internal.connection.RealConnection
            r0.<init>(r1)
            okhttp3.ConnectionPool r1 = r6.connectionPool
            monitor-enter(r1)
            r6.acquire(r0)     // Catch:{ all -> 0x009c }
            okhttp3.internal.Internal r2 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x009c }
            okhttp3.ConnectionPool r3 = r6.connectionPool     // Catch:{ all -> 0x009c }
            r2.put(r3, r0)     // Catch:{ all -> 0x009c }
            r6.connection = r0     // Catch:{ all -> 0x009c }
            boolean r2 = r6.canceled     // Catch:{ all -> 0x009c }
            if (r2 != 0) goto L_0x0093
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            okhttp3.Address r1 = r6.address
            java.util.List r4 = r1.connectionSpecs()
            r1 = r7
            r2 = r8
            r3 = r9
            r5 = r10
            r0.connect(r1, r2, r3, r4, r5)
            okhttp3.internal.connection.RouteDatabase r1 = r6.routeDatabase()
            okhttp3.Route r2 = r0.route()
            r1.connected(r2)
            return r0
        L_0x0057:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0060 }
            java.lang.String r2 = "released"
            r0.<init>(r2)     // Catch:{ all -> 0x0060 }
            throw r0     // Catch:{ all -> 0x0060 }
        L_0x0060:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0060 }
            throw r0
        L_0x0063:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0060 }
            java.lang.String r2 = "codec != null"
            r0.<init>(r2)     // Catch:{ all -> 0x0060 }
            throw r0     // Catch:{ all -> 0x0060 }
        L_0x006c:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0060 }
            java.lang.String r2 = "Canceled"
            r0.<init>(r2)     // Catch:{ all -> 0x0060 }
            throw r0     // Catch:{ all -> 0x0060 }
        L_0x0075:
            boolean r2 = r0.noNewStreams     // Catch:{ all -> 0x0060 }
            if (r2 != 0) goto L_0x0013
            monitor-exit(r1)     // Catch:{ all -> 0x0060 }
            return r0
        L_0x007b:
            r6.connection = r0     // Catch:{ all -> 0x0060 }
            monitor-exit(r1)     // Catch:{ all -> 0x0060 }
            return r0
        L_0x007f:
            okhttp3.internal.connection.RouteSelector r0 = r6.routeSelector
            okhttp3.Route r0 = r0.next()
            okhttp3.ConnectionPool r1 = r6.connectionPool
            monitor-enter(r1)
            r6.route = r0     // Catch:{ all -> 0x0090 }
            r2 = 0
            r6.refusedStreamCount = r2     // Catch:{ all -> 0x0090 }
            monitor-exit(r1)     // Catch:{ all -> 0x0090 }
            r1 = r0
            goto L_0x0025
        L_0x0090:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0090 }
            throw r0
        L_0x0093:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x009c }
            java.lang.String r2 = "Canceled"
            r0.<init>(r2)     // Catch:{ all -> 0x009c }
            throw r0     // Catch:{ all -> 0x009c }
        L_0x009c:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0010, code lost:
        if (r0.isHealthy(r8) == false) goto L_0x0018;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0012, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findHealthyConnection(int r4, int r5, int r6, boolean r7, boolean r8) throws java.io.IOException {
        /*
            r3 = this;
        L_0x0000:
            okhttp3.internal.connection.RealConnection r0 = r3.findConnection(r4, r5, r6, r7)
            okhttp3.ConnectionPool r1 = r3.connectionPool
            monitor-enter(r1)
            int r2 = r0.successCount     // Catch:{ all -> 0x0015 }
            if (r2 == 0) goto L_0x0013
            monitor-exit(r1)     // Catch:{ all -> 0x0015 }
            boolean r1 = r0.isHealthy(r8)
            if (r1 == 0) goto L_0x0018
            return r0
        L_0x0013:
            monitor-exit(r1)     // Catch:{ all -> 0x0015 }
            return r0
        L_0x0015:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0015 }
            throw r0
        L_0x0018:
            r3.noNewStreams()
            goto L_0x0000
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findHealthyConnection(int, int, int, boolean, boolean):okhttp3.internal.connection.RealConnection");
    }

    private void release(RealConnection realConnection) {
        int size = realConnection.allocations.size();
        int i = 0;
        while (i < size) {
            if (realConnection.allocations.get(i).get() != this) {
                i++;
            } else {
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
        } else if (realConnection != null) {
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
                findHealthyConnection.source.timeout().timeout((long) readTimeoutMillis, TimeUnit.MILLISECONDS);
                findHealthyConnection.sink.timeout().timeout((long) writeTimeoutMillis, TimeUnit.MILLISECONDS);
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
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    if (this.refusedStreamCount <= 1) {
                        z = false;
                    }
                }
                this.route = null;
                z = true;
            } else if ((this.connection == null || this.connection.isMultiplexed()) && !(iOException instanceof ConnectionShutdownException)) {
                z = false;
            } else if (this.connection.successCount != 0) {
                z = true;
            } else {
                if (!(this.route == null || iOException == null)) {
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
