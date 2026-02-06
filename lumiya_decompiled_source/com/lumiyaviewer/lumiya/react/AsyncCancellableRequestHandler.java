package com.lumiyaviewer.lumiya.react;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class AsyncCancellableRequestHandler<K> implements RequestHandler<K> {
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final ExecutorService executor;
    @Nonnull
    private final Map<K, Future<?>> activeRequests = new ConcurrentHashMap();
    private final Object lock = new Object();

    public AsyncCancellableRequestHandler(@Nonnull ExecutorService executorService, @Nonnull RequestHandler<K> requestHandler) {
        this.executor = executorService;
        this.baseHandler = requestHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_AsyncCancellableRequestHandler_993  reason: not valid java name */
    public /* synthetic */ void m27x869f759f(Object obj) {
        try {
            this.baseHandler.onRequest(obj);
            synchronized (this.lock) {
                this.activeRequests.remove(obj);
            }
        } catch (Throwable th) {
            synchronized (this.lock) {
                this.activeRequests.remove(obj);
                throw th;
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull final K k) {
        synchronized (this.lock) {
            this.activeRequests.put(k, this.executor.submit(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$W2IjgG3sQFB-K_ukBg8_XysJz_I
                private final /* synthetic */ void $m$0() {
                    ((AsyncCancellableRequestHandler) this).m27x869f759f(k);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            }));
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull K k) {
        synchronized (this.lock) {
            Future<?> remove = this.activeRequests.remove(k);
            if (remove != null) {
                remove.cancel(true);
            }
        }
    }
}
