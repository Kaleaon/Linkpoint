package com.lumiyaviewer.lumiya.react

import com.lumiyaviewer.lumiya.Debug

class RateLimitRequestHandler<K, T>(
    requestSource: RequestSource<K, T>
) : RequestHandler<K>, RequestQueue<K, T>, RequestSource<K, T>, ResultHandler<K, T> {

    private val lock = Any()
    private var cancellable = false
    private var maxInFlight = Int.MAX_VALUE
    private var requestTimeout = Long.MAX_VALUE
    
    private val pendingRequests = mutableSetOf<K>()
    private val requestsInFlight = mutableMapOf<K, Long>()
    private val resultHandler: ResultHandler<K, T> = requestSource.attachRequestHandler(this)
    private var requestHandler: RequestHandler<K>? = null

    private fun runPendingRequests() {
        synchronized(lock) {
            Debug.Printf("UserPic: RateLimitHandler: pending count %d, in flight %d", 
                pendingRequests.size, requestsInFlight.size)
            
            requestHandler?.let { handler ->
                val currentTime = System.currentTimeMillis()
                
                // Remove timed out requests
                if (requestsInFlight.size >= maxInFlight && requestTimeout != Long.MAX_VALUE) {
                    val iterator = requestsInFlight.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (currentTime >= entry.value + requestTimeout) {
                            val key = entry.key
                            iterator.remove()
                            if (cancellable) {
                                handler.onRequestCancelled(key)
                            }
                            pendingRequests.remove(key)
                        }
                    }
                }
                
                // Start pending requests
                if (requestsInFlight.size < maxInFlight) {
                    val iterator = pendingRequests.iterator()
                    if (iterator.hasNext()) {
                        val next = iterator.next()
                        iterator.remove()
                        requestsInFlight[next] = currentTime
                        handler.onRequest(next)
                    }
                }
            }
            
            if (pendingRequests.isNotEmpty()) {
                (lock as java.lang.Object).notifyAll()
            }
        }
    }

    override fun attachRequestHandler(requestHandler: RequestHandler<K>): ResultHandler<K, T> {
        synchronized(lock) {
            if (this.requestHandler != requestHandler) {
                if (this.requestHandler != null) {
                    this.requestHandler = null
                    requestsInFlight.clear()
                }
                
                this.requestHandler = requestHandler
                
                if (requestHandler is RequestHandlerLimits) {
                    cancellable = requestHandler.isRequestCancellable()
                    maxInFlight = requestHandler.getMaxRequestsInFlight()
                    requestTimeout = requestHandler.getRequestTimeout()
                } else {
                    cancellable = true
                    maxInFlight = Int.MAX_VALUE
                    requestTimeout = Long.MAX_VALUE
                }
            }
        }
        runPendingRequests()
        return this
    }

    override fun detachRequestHandler(requestHandler: RequestHandler<K>) {
        synchronized(lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null
                requestsInFlight.clear()
            }
        }
    }

    override fun getNextRequest(): K? {
        synchronized(lock) {
            val iterator = pendingRequests.iterator()
            if (iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()
                requestsInFlight[key] = System.currentTimeMillis()
                return key
            }
        }
        return null
    }

    override fun getResultHandler(): ResultHandler<K, T> = this

    override fun onRequest(key: K) {
        Debug.Printf("UserPic: RateLimitHandler: new for %s", key.toString())
        synchronized(lock) {
            pendingRequests.add(key)
        }
        runPendingRequests()
    }

    override fun onRequestCancelled(key: K) {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", key.toString())
        synchronized(lock) {
            pendingRequests.remove(key)
            if (requestHandler != null && cancellable && requestsInFlight.remove(key) != null) {
                requestHandler?.onRequestCancelled(key)
            }
        }
        runPendingRequests()
    }

    override fun onResultData(key: K, data: T) {
        synchronized(lock) {
            pendingRequests.remove(key)
            requestsInFlight.remove(key)
        }
        resultHandler.onResultData(key, data)
        runPendingRequests()
    }

    override fun onResultError(key: K, error: Throwable) {
        synchronized(lock) {
            pendingRequests.remove(key)
            requestsInFlight.remove(key)
        }
        resultHandler.onResultError(key, error)
        runPendingRequests()
    }

    override fun returnRequest(key: K) {
        synchronized(lock) {
            if (requestsInFlight.remove(key) != null) {
                pendingRequests.add(key)
            }
        }
        runPendingRequests()
    }

    @Throws(InterruptedException::class)
    override fun waitForRequest(): K? {
        synchronized(lock) {
            while (true) {
                val iterator = pendingRequests.iterator()
                if (iterator.hasNext()) {
                    val next = iterator.next()
                    iterator.remove()
                    return next
                } else {
                    (lock as java.lang.Object).wait()
                }
            }
        }
    }
}
