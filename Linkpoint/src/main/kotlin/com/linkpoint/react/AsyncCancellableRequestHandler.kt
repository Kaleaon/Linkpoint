package com.linkpoint.react

import java.util.Map
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.annotation.Nonnull

class AsyncCancellableRequestHandler<K> : RequestHandler<K> {
    private val Map<K, Future<?>> activeRequests = ConcurrentHashMap()
    private val RequestHandler<K> baseHandler
    private val ExecutorService executor
    private val Object lock = Object()

    public AsyncCancellableRequestHandler(ExecutorService executorService, RequestHandler<K> requestHandler) {
        this.executor = executorService
        this.baseHandler = requestHandler
    }

    /* access modifiers changed from: private */
    /* renamed from: handleCancellableAsyncRequestInternal */
    public /* synthetic */ Unit m16lambda$com_lumiyaviewer_lumiya_react_AsyncCancellableRequestHandler_993(Object obj) {
        try {
            this.baseHandler.onRequest(obj)
            synchronized (this.lock) {
                this.activeRequests.remove(obj)
            }
        } catch (Throwable th) {
            synchronized (this.lock) {
                this.activeRequests.remove(obj)
            }
        }
    }

    fun onRequest(K k) {
        synchronized (this.lock) {
            this.activeRequests.put(k, this.executor.submit(-$Lambda$W2IjgG3sQFB-K_ukBg8_XysJz_I(this, k)))
        }
    }

    fun onRequestCancelled(K k) {
        synchronized (this.lock) {
            Future future = (Future) this.activeRequests.remove(k)
            if (future != null) {
                future.cancel(true)
            }
        }
    }
}
