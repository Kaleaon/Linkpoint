// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.platform.Platform;
import java.lang.ref.Reference;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.internal.Util;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.RealConnection;
import java.util.Deque;
import java.util.concurrent.Executor;

public final class ConnectionPool
{
    private static final Executor executor;
    private final Runnable cleanupRunnable;
    boolean cleanupRunning;
    private final Deque<RealConnection> connections;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;
    
    static {
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp ConnectionPool", true));
    }
    
    public ConnectionPool() {
        this(5, 5L, TimeUnit.MINUTES);
    }
    
    public ConnectionPool(int maxIdleConnections, final long n, final TimeUnit timeUnit) {
        this.cleanupRunnable = new Runnable() {
            @Override
            public void run() {
            Label_0028_Outer:
                while (true) {
                    final long cleanup = ConnectionPool.this.cleanup(System.nanoTime());
                    if (cleanup == -1L) {
                        break;
                    }
                    Label_0080: {
                        if (cleanup > 0L) {
                            break Label_0080;
                        }
                        int n = 1;
                    Label_0066_Outer:
                        while (true) {
                            if (n != 0) {
                                continue Label_0028_Outer;
                            }
                            final long timeoutMillis = cleanup / 1000000L;
                            final ConnectionPool this$0 = ConnectionPool.this;
                            monitorenter(this$0);
                            while (true) {
                                try {
                                    try {
                                        ConnectionPool.this.wait(timeoutMillis, (int)(cleanup - timeoutMillis * 1000000L));
                                        continue Label_0028_Outer;
                                    }
                                    finally {
                                        monitorexit(this$0);
                                    }
                                    n = 0;
                                    continue Label_0066_Outer;
                                }
                                catch (final InterruptedException ex) {
                                    continue;
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        };
        this.connections = new ArrayDeque<RealConnection>();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(n);
        if (n > 0L) {
            maxIdleConnections = 1;
        }
        else {
            maxIdleConnections = 0;
        }
        if (maxIdleConnections == 0) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + n);
        }
    }
    
    private int pruneAndGetAllocationCount(final RealConnection realConnection, final long n) {
        final List<Reference<StreamAllocation>> allocations = realConnection.allocations;
        int i = 0;
        while (i < allocations.size()) {
            final Reference reference = allocations.get(i);
            if (reference.get() == null) {
                Platform.get().logCloseableLeak("A connection to " + realConnection.route().address().url() + " was leaked. Did you forget to close a response body?", ((StreamAllocation.StreamAllocationReference)reference).callStackTrace);
                allocations.remove(i);
                realConnection.noNewStreams = true;
                if (allocations.isEmpty()) {
                    realConnection.idleAtNanos = n - this.keepAliveDurationNs;
                    return 0;
                }
                continue;
            }
            else {
                ++i;
            }
        }
        return allocations.size();
    }
    
    long cleanup(long n) {
        RealConnection realConnection = null;
        long n2 = Long.MIN_VALUE;
        synchronized (this) {
            final Iterator<RealConnection> iterator = this.connections.iterator();
            int n3 = 0;
            int n4 = 0;
            while (iterator.hasNext()) {
                final RealConnection realConnection2 = iterator.next();
                if (this.pruneAndGetAllocationCount(realConnection2, n) <= 0) {
                    final long n5 = n - realConnection2.idleAtNanos;
                    int n6;
                    if (n5 <= n2) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    if (n6 == 0) {
                        realConnection = realConnection2;
                        n2 = n5;
                    }
                    ++n4;
                }
                else {
                    ++n3;
                }
            }
            boolean b;
            if (n2 >= this.keepAliveDurationNs) {
                b = true;
            }
            else {
                b = false;
            }
            if (b || n4 > this.maxIdleConnections) {
                this.connections.remove(realConnection);
                monitorexit(this);
                Util.closeQuietly(realConnection.socket());
                return 0L;
            }
            if (n4 > 0) {
                n = this.keepAliveDurationNs;
                monitorexit(this);
                return n - n2;
            }
            if (n3 <= 0) {
                this.cleanupRunning = false;
                return -1L;
            }
            n = this.keepAliveDurationNs;
            return n;
        }
    }
    
    boolean connectionBecameIdle(final RealConnection realConnection) {
        assert Thread.holdsLock(this);
        if (!realConnection.noNewStreams && this.maxIdleConnections != 0) {
            this.notifyAll();
            return false;
        }
        this.connections.remove(realConnection);
        return true;
    }
    
    public int connectionCount() {
        synchronized (this) {
            return this.connections.size();
        }
    }
    
    public void evictAll() {
        while (true) {
            final ArrayList list = new ArrayList();
            while (true) {
                final Iterator iterator2;
                synchronized (this) {
                    final Iterator<RealConnection> iterator = this.connections.iterator();
                    while (iterator.hasNext()) {
                        final RealConnection realConnection = iterator.next();
                        if (realConnection.allocations.isEmpty()) {
                            realConnection.noNewStreams = true;
                            list.add(realConnection);
                            iterator.remove();
                        }
                    }
                    monitorexit(this);
                    iterator2 = list.iterator();
                    if (!iterator2.hasNext()) {
                        return;
                    }
                }
                Util.closeQuietly(((RealConnection)iterator2.next()).socket());
                continue;
            }
        }
    }
    
    RealConnection get(final Address address, final StreamAllocation streamAllocation) {
        assert Thread.holdsLock(this);
        for (final RealConnection realConnection : this.connections) {
            if (realConnection.allocations.size() < realConnection.allocationLimit && address.equals(realConnection.route().address) && !realConnection.noNewStreams) {
                streamAllocation.acquire(realConnection);
                return realConnection;
            }
        }
        return null;
    }
    
    public int idleConnectionCount() {
        int n = 0;
        synchronized (this) {
            final Iterator<RealConnection> iterator = this.connections.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().allocations.isEmpty()) {
                    continue;
                }
                ++n;
            }
            return n;
        }
    }
    
    void put(final RealConnection realConnection) {
        assert Thread.holdsLock(this);
        if (!this.cleanupRunning) {
            this.cleanupRunning = true;
            ConnectionPool.executor.execute(this.cleanupRunnable);
        }
        this.connections.add(realConnection);
    }
}
