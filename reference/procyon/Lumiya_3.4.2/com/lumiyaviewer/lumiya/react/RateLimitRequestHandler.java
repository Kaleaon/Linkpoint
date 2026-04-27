// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.HashSet;
import javax.annotation.Nonnull;
import java.util.Map;
import javax.annotation.Nullable;
import java.util.Set;

public class RateLimitRequestHandler<K, T> implements RequestHandler<K>, RequestQueue<K, T>, RequestSource<K, T>, ResultHandler<K, T>
{
    private boolean cancellable;
    private final Object lock;
    private int maxInFlight;
    private final Set<K> pendingRequests;
    @Nullable
    private RequestHandler<K> requestHandler;
    private long requestTimeout;
    private final Map<K, Long> requestsInFlight;
    @Nonnull
    private final ResultHandler<K, T> resultHandler;
    
    public RateLimitRequestHandler(@Nonnull final RequestSource<K, T> requestSource) {
        this.lock = new Object();
        this.pendingRequests = new HashSet<K>();
        this.requestsInFlight = new HashMap<K, Long>();
        this.cancellable = false;
        this.maxInFlight = Integer.MAX_VALUE;
        this.requestTimeout = Long.MAX_VALUE;
        this.requestHandler = null;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }
    
    private void runPendingRequests() {
        Label_0281: {
            final long currentTimeMillis;
            synchronized (this.lock) {
                Debug.Printf("UserPic: RateLimitHandler: pending count %d, in flight %d", this.pendingRequests.size(), this.requestsInFlight.size());
                if (this.requestHandler == null) {
                    break Label_0281;
                }
                currentTimeMillis = System.currentTimeMillis();
                if (this.requestsInFlight.size() >= this.maxInFlight && this.requestTimeout != Long.MAX_VALUE) {
                    final Iterator<Map.Entry<K, Long>> iterator = this.requestsInFlight.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry<K, Long> entry = (Map.Entry<K, Long>)iterator.next();
                        if (currentTimeMillis >= entry.getValue() + this.requestTimeout) {
                            final K key = entry.getKey();
                            iterator.remove();
                            if (this.cancellable) {
                                this.requestHandler.onRequestCancelled((K)key);
                            }
                            this.pendingRequests.remove(key);
                        }
                    }
                }
            }
            if (this.requestsInFlight.size() < this.maxInFlight) {
                final Iterator<K> iterator2 = this.pendingRequests.iterator();
                if (iterator2.hasNext()) {
                    final K next = iterator2.next();
                    iterator2.remove();
                    this.requestsInFlight.put(next, currentTimeMillis);
                    this.requestHandler.onRequest(next);
                }
            }
        }
        if (!this.pendingRequests.isEmpty()) {
            this.lock.notifyAll();
        }
        final Object o;
        monitorexit(o);
    }
    
    @Override
    public ResultHandler<K, T> attachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler != requestHandler) {
                if (this.requestHandler != null) {
                    this.requestHandler = null;
                    this.requestsInFlight.clear();
                }
                this.requestHandler = requestHandler;
                if (requestHandler instanceof RequestHandlerLimits) {
                    final RequestHandlerLimits requestHandlerLimits = (RequestHandlerLimits)requestHandler;
                    this.cancellable = requestHandlerLimits.isRequestCancellable();
                    this.maxInFlight = requestHandlerLimits.getMaxRequestsInFlight();
                    this.requestTimeout = requestHandlerLimits.getRequestTimeout();
                }
                else {
                    this.cancellable = true;
                    this.maxInFlight = Integer.MAX_VALUE;
                    this.requestTimeout = Long.MAX_VALUE;
                }
            }
            monitorexit(this.lock);
            this.runPendingRequests();
            return this;
        }
    }
    
    @Override
    public void detachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null;
                this.requestsInFlight.clear();
            }
        }
    }
    
    @Nullable
    @Override
    public K getNextRequest() {
        K next = null;
        synchronized (this.lock) {
            final Iterator<K> iterator = this.pendingRequests.iterator();
            if (iterator.hasNext()) {
                next = iterator.next();
                iterator.remove();
                this.requestsInFlight.put(next, System.currentTimeMillis());
            }
            return next;
        }
    }
    
    @Nonnull
    @Override
    public ResultHandler<K, T> getResultHandler() {
        return this;
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        Debug.Printf("UserPic: RateLimitHandler: new for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.add(k);
            monitorexit(this.lock);
            this.runPendingRequests();
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            if (this.requestHandler != null && this.cancellable && this.requestsInFlight.remove(k) != null) {
                this.requestHandler.onRequestCancelled(k);
            }
            monitorexit(this.lock);
            this.runPendingRequests();
        }
    }
    
    @Override
    public void onResultData(@Nonnull final K k, final T t) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
            monitorexit(this.lock);
            this.resultHandler.onResultData(k, t);
            this.runPendingRequests();
        }
    }
    
    @Override
    public void onResultError(@Nonnull final K k, final Throwable t) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
            monitorexit(this.lock);
            this.resultHandler.onResultError(k, t);
            this.runPendingRequests();
        }
    }
    
    @Override
    public void returnRequest(@Nonnull final K k) {
        synchronized (this.lock) {
            if (this.requestsInFlight.remove(k) != null) {
                this.pendingRequests.add(k);
            }
            monitorexit(this.lock);
            this.runPendingRequests();
        }
    }
    
    @Nullable
    @Override
    public K waitForRequest() throws InterruptedException {
        synchronized (this.lock) {
            Iterator<K> iterator;
            while (true) {
                iterator = this.pendingRequests.iterator();
                if (iterator.hasNext()) {
                    break;
                }
                this.lock.wait();
            }
            final K next = iterator.next();
            iterator.remove();
            return next;
        }
    }
}
