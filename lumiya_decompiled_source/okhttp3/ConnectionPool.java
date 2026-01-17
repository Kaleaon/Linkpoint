package okhttp3;

import java.lang.ref.Reference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.platform.Platform;
/* loaded from: classes.dex */
public final class ConnectionPool {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final Executor executor;
    private final Runnable cleanupRunnable;
    boolean cleanupRunning;
    private final Deque<RealConnection> connections;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;

    static {
        $assertionsDisabled = !ConnectionPool.class.desiredAssertionStatus();
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
    }

    public ConnectionPool() {
        this(5, 5L, TimeUnit.MINUTES);
    }

    public ConnectionPool(int i, long j, TimeUnit timeUnit) {
        this.cleanupRunnable = new Runnable() { // from class: okhttp3.ConnectionPool.1
            @Override // java.lang.Runnable
            public void run() {
                while (true) {
                    long cleanup = ConnectionPool.this.cleanup(System.nanoTime());
                    if (cleanup == -1) {
                        return;
                    }
                    if (!(cleanup <= 0)) {
                        long j2 = cleanup / 1000000;
                        long j3 = cleanup - (j2 * 1000000);
                        synchronized (ConnectionPool.this) {
                            try {
                                ConnectionPool.this.wait(j2, (int) j3);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        };
        this.connections = new ArrayDeque();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = i;
        this.keepAliveDurationNs = timeUnit.toNanos(j);
        if (!(j > 0)) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + j);
        }
    }

    private int pruneAndGetAllocationCount(RealConnection realConnection, long j) {
        List<Reference<StreamAllocation>> list = realConnection.allocations;
        int i = 0;
        while (i < list.size()) {
            Reference<StreamAllocation> reference = list.get(i);
            if (reference.get() == null) {
                Platform.get().logCloseableLeak("A connection to " + realConnection.route().address().url() + " was leaked. Did you forget to close a response body?", ((StreamAllocation.StreamAllocationReference) reference).callStackTrace);
                list.remove(i);
                realConnection.noNewStreams = true;
                if (list.isEmpty()) {
                    realConnection.idleAtNanos = j - this.keepAliveDurationNs;
                    return 0;
                }
            } else {
                i++;
            }
        }
        return list.size();
    }

    long cleanup(long j) {
        RealConnection realConnection;
        long j2;
        RealConnection realConnection2 = null;
        long j3 = Long.MIN_VALUE;
        synchronized (this) {
            int i = 0;
            int i2 = 0;
            for (RealConnection realConnection3 : this.connections) {
                if (pruneAndGetAllocationCount(realConnection3, j) <= 0) {
                    int i3 = i2 + 1;
                    long j4 = j - realConnection3.idleAtNanos;
                    if (j4 <= j3) {
                        realConnection = realConnection2;
                        j2 = j3;
                    } else {
                        realConnection = realConnection3;
                        j2 = j4;
                    }
                    j3 = j2;
                    realConnection2 = realConnection;
                    i2 = i3;
                } else {
                    i++;
                }
            }
            if ((j3 >= this.keepAliveDurationNs) || i2 > this.maxIdleConnections) {
                this.connections.remove(realConnection2);
                Util.closeQuietly(realConnection2.socket());
                return 0L;
            } else if (i2 > 0) {
                return this.keepAliveDurationNs - j3;
            } else if (i > 0) {
                return this.keepAliveDurationNs;
            } else {
                this.cleanupRunning = false;
                return -1L;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean connectionBecameIdle(RealConnection realConnection) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            if (realConnection.noNewStreams || this.maxIdleConnections == 0) {
                this.connections.remove(realConnection);
                return true;
            }
            notifyAll();
            return false;
        }
        throw new AssertionError();
    }

    public synchronized int connectionCount() {
        return this.connections.size();
    }

    public void evictAll() {
        ArrayList<RealConnection> arrayList = new ArrayList();
        synchronized (this) {
            Iterator<RealConnection> it = this.connections.iterator();
            while (it.hasNext()) {
                RealConnection next = it.next();
                if (next.allocations.isEmpty()) {
                    next.noNewStreams = true;
                    arrayList.add(next);
                    it.remove();
                }
            }
        }
        for (RealConnection realConnection : arrayList) {
            Util.closeQuietly(realConnection.socket());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RealConnection get(Address address, StreamAllocation streamAllocation) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            for (RealConnection realConnection : this.connections) {
                if (realConnection.allocations.size() < realConnection.allocationLimit && address.equals(realConnection.route().address) && !realConnection.noNewStreams) {
                    streamAllocation.acquire(realConnection);
                    return realConnection;
                }
            }
            return null;
        }
        throw new AssertionError();
    }

    public synchronized int idleConnectionCount() {
        int i = 0;
        synchronized (this) {
            for (RealConnection realConnection : this.connections) {
                i = !realConnection.allocations.isEmpty() ? i : i + 1;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(RealConnection realConnection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (!this.cleanupRunning) {
            this.cleanupRunning = true;
            executor.execute(this.cleanupRunnable);
        }
        this.connections.add(realConnection);
    }
}
