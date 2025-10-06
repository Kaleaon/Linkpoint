package com.lumiyaviewer.lumiya.react

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class AsyncCancellableRequestHandler<K>(
    private val executor: ExecutorService,
    private val baseHandler: RequestHandler<K>
) : RequestHandler<K> {

    private val activeRequests = ConcurrentHashMap<K, Future<*>>()
    private val lock = Any()

    override fun onRequest(key: K) {
        synchronized(lock) {
            val future = executor.submit {
                try {
                    baseHandler.onRequest(key)
                } finally {
                    synchronized(lock) {
                        activeRequests.remove(key)
                    }
                }
            }
            activeRequests[key] = future
        }
    }

    override fun onRequestCancelled(key: K) {
        synchronized(lock) {
            activeRequests.remove(key)?.cancel(true)
        }
    }
}
