package com.lumiyaviewer.lumiya.react;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public class AsyncCancellableRequestHandler<K> implements RequestHandler<K> {
    @Nonnull
    private final Map<K, Future<?>> activeRequests = new ConcurrentHashMap();
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final ExecutorService executor;
    private final Object lock = new Object();

    public AsyncCancellableRequestHandler(@Nonnull ExecutorService executorService, @Nonnull RequestHandler<K> requestHandler) {
        this.executor = executorService;
        this.baseHandler = requestHandler;
    }

    /* access modifiers changed from: private */
    /* renamed from: handleCancellableAsyncRequestInternal */
    public /* synthetic */ void m16lambda$com_lumiyaviewer_lumiya_react_AsyncCancellableRequestHandler_993(Object obj) {
        try {
            this.baseHandler.onRequest(obj);
            synchronized (this.lock) {
                this.activeRequests.remove(obj);
            }
        } catch (Throwable th) {
            synchronized (this.lock) {
                this.activeRequests.remove(obj);
            }
        }
    }

    public void onRequest(@Nonnull K k) {
        synchronized (this.lock) {
            this.activeRequests.put(k, this.executor.submit(new -$Lambda$W2IjgG3sQFB-K_ukBg8_XysJz_I(this, k)));
        }
    }

    public void onRequestCancelled(@Nonnull K k) {
        synchronized (this.lock) {
            Future future = (Future) this.activeRequests.remove(k);
            if (future != null) {
                future.cancel(true);
            }
        }
    }
}
