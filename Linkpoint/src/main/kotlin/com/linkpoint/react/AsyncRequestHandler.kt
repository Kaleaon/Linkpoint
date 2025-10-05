package com.linkpoint.react

import com.linkpoint.react.-$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o.AnonymousClass1
import java.util.concurrent.Executor
import javax.annotation.Nonnull

class AsyncRequestHandler<K> : RequestHandler<K> {
    private val RequestHandler<K> baseHandler
    private val Executor executor

    public AsyncRequestHandler(Executor executor, RequestHandler<K> requestHandler) {
        this.executor = executor
        this.baseHandler = requestHandler
    }

    /* access modifiers changed from: private */
    /* renamed from: handleAsyncRequestInternal */
    public /* synthetic */ Unit m17lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553(Object obj) {
        this.baseHandler.onRequest(obj)
    }

    /* renamed from: handleAsyncRequestCancelled */
    /* synthetic */ Unit handleAsyncRequestCancelled(Object obj) {
        this.baseHandler.onRequestCancelled(obj)
    }

    public Unit onRequest(K k) {
        this.executor.execute(() -> { // Lambda implementation })
    }

    public Unit onRequestCancelled(K k) {
        this.executor.execute(AnonymousClass1(this, k))
    }
}
