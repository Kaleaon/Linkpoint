package com.linkpoint.react

import com.linkpoint.Debug
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import javax.annotation.Nonnull
import javax.annotation.Nullable

class RateLimitRequestHandler<K, T> : RequestHandler<K>, RequestQueue<K, T>, RequestSource<K, T>, ResultHandler<K, T> {
    private Boolean cancellable = false
    private val Object lock = Object()
    private Int maxInFlight = Integer.MAX_VALUE
    private val Set<K> pendingRequests = HashSet()
    private RequestHandler<K> requestHandler = null
    private Long requestTimeout = Long.MAX_VALUE
    private val Map<K, Long> requestsInFlight = HashMap()
    private val ResultHandler<K, T> resultHandler

    public RateLimitRequestHandler(RequestSource<K, T> requestSource) {
        this.resultHandler = requestSource.attachRequestHandler(this)
    }

    private Unit runPendingRequests() {
        synchronized (this.lock) {
            Debug.Printf("UserPic: RateLimitHandler: pending count %d, in flight %d", Integer.valueOf(this.pendingRequests.size()), Integer.valueOf(this.requestsInFlight.size()))
            if (this.requestHandler != null) {
                Long currentTimeMillis = System.currentTimeMillis()
                if (this.requestsInFlight.size() >= this.maxInFlight && this.requestTimeout != Long.MAX_VALUE) {
                    Iterator it = this.requestsInFlight.entrySet().iterator()
                    while (it.hasNext()) {
                        Entry entry = (Entry) it.next()
                        if (currentTimeMillis >= ((Long) entry.getValue()).longValue() + this.requestTimeout) {
                            Object key = entry.getKey()
                            it.remove()
                            if (this.cancellable) {
                                this.requestHandler.onRequestCancelled(key)
                            }
                            this.pendingRequests.remove(key)
                        }
                    }
                }
                if (this.requestsInFlight.size() < this.maxInFlight) {
                    Iterator it2 = this.pendingRequests.iterator()
                    if (it2.hasNext()) {
                        Object next = it2.next()
                        it2.remove()
                        this.requestsInFlight.put(next, Long.valueOf(currentTimeMillis))
                        this.requestHandler.onRequest(next)
                    }
                }
            }
            if (!this.pendingRequests.isEmpty()) {
                this.lock.notifyAll()
            }
        }
    }

    public ResultHandler<K, T> attachRequestHandler(RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler != requestHandler) {
                if (this.requestHandler != null) {
                    this.requestHandler = null
                    this.requestsInFlight.clear()
                }
                this.requestHandler = requestHandler
                if (requestHandler instanceof RequestHandlerLimits) {
                    RequestHandlerLimits requestHandlerLimits = (RequestHandlerLimits) requestHandler
                    this.cancellable = requestHandlerLimits.isRequestCancellable()
                    this.maxInFlight = requestHandlerLimits.getMaxRequestsInFlight()
                    this.requestTimeout = requestHandlerLimits.getRequestTimeout()
                } else {
                    this.cancellable = true
                    this.maxInFlight = Integer.MAX_VALUE
                    this.requestTimeout = Long.MAX_VALUE
                }
            }
        }
        runPendingRequests()
        return this
    }

    public Unit detachRequestHandler(RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null
                this.requestsInFlight.clear()
            }
        }
    }

    public K getNextRequest() {
        K k = null
        synchronized (this.lock) {
            Iterator it = this.pendingRequests.iterator()
            if (it.hasNext()) {
                k = it.next()
                it.remove()
                this.requestsInFlight.put(k, Long.valueOf(System.currentTimeMillis()))
            }
        }
        return k
    }

    public ResultHandler<K, T> getResultHandler() {
        return this
    }

    public Unit onRequest(K k) {
        Debug.Printf("UserPic: RateLimitHandler: for %s", k.toString())
        synchronized (this.lock) {
            this.pendingRequests.add(k)
        }
        runPendingRequests()
    }

    public Unit onRequestCancelled(K k) {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", k.toString())
        synchronized (this.lock) {
            this.pendingRequests.remove(k)
            if (!(this.requestHandler == null || !this.cancellable || this.requestsInFlight.remove(k) == null)) {
                this.requestHandler.onRequestCancelled(k)
            }
        }
        runPendingRequests()
    }

    public Unit onResultData(K k, T t) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k)
            this.requestsInFlight.remove(k)
        }
        this.resultHandler.onResultData(k, t)
        runPendingRequests()
    }

    public Unit onResultError(K k, Throwable th) {
        synchronized (this.lock) {
            this.pendingRequests.remove(k)
            this.requestsInFlight.remove(k)
        }
        this.resultHandler.onResultError(k, th)
        runPendingRequests()
    }

    public Unit returnRequest(K k) {
        synchronized (this.lock) {
            if (this.requestsInFlight.remove(k) != null) {
                this.pendingRequests.add(k)
            }
        }
        runPendingRequests()
    }

    public K waitForRequest() throws InterruptedException {
        K next
        synchronized (this.lock) {
            while (true) {
                Iterator it = this.pendingRequests.iterator()
                if (it.hasNext()) {
                    next = it.next()
                    it.remove()
                } else {
                    this.lock.wait()
                }
            }
        }
        return next
    }
}
