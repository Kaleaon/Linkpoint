package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RateLimitRequestHandler<K, T> implements RequestHandler<K>, RequestQueue<K, T>, RequestSource<K, T>, ResultHandler<K, T> {
    private boolean cancellable = false;
    private final Object lock = new Object();
    private int maxInFlight = Integer.MAX_VALUE;
    private final Set<K> pendingRequests = new HashSet();
    @Nullable
    private RequestHandler<K> requestHandler = null;
    private long requestTimeout = Long.MAX_VALUE;
    private final Map<K, Long> requestsInFlight = new HashMap();
    @Nonnull
    private final ResultHandler<K, T> resultHandler;

    public RateLimitRequestHandler(@Nonnull RequestSource<K, T> requestSource) {
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    private void runPendingRequests() {
        synchronized (this.lock) {
            Debug.Printf("UserPic: RateLimitHandler: pending count %d, in flight %d", Integer.valueOf(this.pendingRequests.size()), Integer.valueOf(this.requestsInFlight.size()));
            if (this.requestHandler != null) {
                long currentTimeMillis = System.currentTimeMillis();
                if (this.requestsInFlight.size() >= this.maxInFlight && this.requestTimeout != Long.MAX_VALUE) {
                    Iterator<Map.Entry<K, Long>> it = this.requestsInFlight.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry next = it.next();
                        if (currentTimeMillis >= ((Long) next.getValue()).longValue() + this.requestTimeout) {
                            Object key = next.getKey();
                            it.remove();
                            if (this.cancellable) {
                                this.requestHandler.onRequestCancelled(key);
                            }
                            this.pendingRequests.remove(key);
                        }
                    }
                }
                if (this.requestsInFlight.size() < this.maxInFlight) {
                    Iterator<K> it2 = this.pendingRequests.iterator();
                    if (it2.hasNext()) {
                        K next2 = it2.next();
                        it2.remove();
                        this.requestsInFlight.put(next2, Long.valueOf(currentTimeMillis));
                        this.requestHandler.onRequest(next2);
                    }
                }
            }
            if (!this.pendingRequests.isEmpty()) {
                this.lock.notifyAll();
            }
        }
    }

    public ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler2) {
        synchronized (this.lock) {
            if (this.requestHandler != requestHandler2) {
                if (this.requestHandler != null) {
                    this.requestHandler = null;
                    this.requestsInFlight.clear();
                }
                this.requestHandler = requestHandler2;
                if (requestHandler2 instanceof RequestHandlerLimits) {
                    RequestHandlerLimits requestHandlerLimits = (RequestHandlerLimits) requestHandler2;
                    this.cancellable = requestHandlerLimits.isRequestCancellable();
                    this.maxInFlight = requestHandlerLimits.getMaxRequestsInFlight();
                    this.requestTimeout = requestHandlerLimits.getRequestTimeout();
                } else {
                    this.cancellable = true;
                    this.maxInFlight = Integer.MAX_VALUE;
                    this.requestTimeout = Long.MAX_VALUE;
                }
            }
        }
        runPendingRequests();
        return this;
    }

    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler2) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler2) {
                this.requestHandler = null;
                this.requestsInFlight.clear();
            }
        }
    }

    @Nullable
    public K getNextRequest() {
        K k = null;
        synchronized (this.lock) {
            Iterator<K> it = this.pendingRequests.iterator();
            if (it.hasNext()) {
                k = it.next();
                it.remove();
                this.requestsInFlight.put(k, Long.valueOf(System.currentTimeMillis()));
            }
        }
        return k;
    }

    @Nonnull
    public ResultHandler<K, T> getResultHandler() {
        return this;
    }

    public void onRequest(@Nonnull K k) {
        Debug.Printf("UserPic: RateLimitHandler: new for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.add(k);
        }
        runPendingRequests();
    }

    public void onRequestCancelled(@Nonnull K k) {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            if (!(this.requestHandler == null || !this.cancellable || this.requestsInFlight.remove(k) == null)) {
                this.requestHandler.onRequestCancelled(k);
            }
        }
        runPendingRequests();
    }

    public void onResultData(@Nonnull K k, T t) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
        }
        this.resultHandler.onResultData(k, t);
        runPendingRequests();
    }

    public void onResultError(@Nonnull K k, Throwable th) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
        }
        this.resultHandler.onResultError(k, th);
        runPendingRequests();
    }

    public void returnRequest(@Nonnull K k) {
        synchronized (this.lock) {
            if (this.requestsInFlight.remove(k) != null) {
                this.pendingRequests.add(k);
            }
        }
        runPendingRequests();
    }

    @Nullable
    public K waitForRequest() throws InterruptedException {
        K next;
        synchronized (this.lock) {
            while (true) {
                Iterator<K> it = this.pendingRequests.iterator();
                if (it.hasNext()) {
                    next = it.next();
                    it.remove();
                } else {
                    this.lock.wait();
                }
            }
        }
        return next;
    }
}
