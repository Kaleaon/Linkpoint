package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class RateLimitRequestHandler<K, T> implements RequestHandler<K>, RequestQueue<K, T>, RequestSource<K, T>, ResultHandler<K, T> {

    @Nonnull
    private final ResultHandler<K, T> resultHandler;
    private final Object lock = new Object();
    private final Set<K> pendingRequests = new HashSet();
    private final Map<K, Long> requestsInFlight = new HashMap();
    private boolean cancellable = false;
    private int maxInFlight = Integer.MAX_VALUE;
    private long requestTimeout = Long.MAX_VALUE;

    @Nullable
    private RequestHandler<K> requestHandler = null;

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
                        Map.Entry<K, Long> next = it.next();
                        if (currentTimeMillis >= next.getValue().longValue() + this.requestTimeout) {
                            K key = next.getKey();
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

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler != requestHandler) {
                if (this.requestHandler != null) {
                    this.requestHandler = null;
                    this.requestsInFlight.clear();
                }
                this.requestHandler = requestHandler;
                if (requestHandler instanceof RequestHandlerLimits) {
                    RequestHandlerLimits requestHandlerLimits = (RequestHandlerLimits) requestHandler;
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

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null;
                this.requestsInFlight.clear();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestQueue
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

    @Override // com.lumiyaviewer.lumiya.react.RequestQueue
    @Nonnull
    public ResultHandler<K, T> getResultHandler() {
        return this;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull K k) {
        Debug.Printf("UserPic: RateLimitHandler: new for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.add(k);
        }
        runPendingRequests();
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull K k) {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", k.toString());
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            if (this.requestHandler != null && this.cancellable && this.requestsInFlight.remove(k) != null) {
                this.requestHandler.onRequestCancelled(k);
            }
        }
        runPendingRequests();
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultData(@Nonnull K k, T t) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
        }
        this.resultHandler.onResultData(k, t);
        runPendingRequests();
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultError(@Nonnull K k, Throwable th) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k);
            this.requestsInFlight.remove(k);
        }
        this.resultHandler.onResultError(k, th);
        runPendingRequests();
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestQueue
    public void returnRequest(@Nonnull K k) {
        synchronized (this.lock) {
            if (this.requestsInFlight.remove(k) != null) {
                this.pendingRequests.add(k);
            }
        }
        runPendingRequests();
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestQueue
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
