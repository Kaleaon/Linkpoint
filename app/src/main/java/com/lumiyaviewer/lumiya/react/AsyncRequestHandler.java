package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.-$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o.AnonymousClass1;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class AsyncRequestHandler<K> implements RequestHandler<K> {
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final Executor executor;

    public AsyncRequestHandler(@Nonnull Executor executor, @Nonnull RequestHandler<K> requestHandler) {
        this.executor = executor;
        this.baseHandler = requestHandler;
    }

    /* access modifiers changed from: private */
    /* renamed from: handleAsyncRequestInternal */
    public /* synthetic */ void m17lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553(Object obj) {
        this.baseHandler.onRequest(obj);
    }

    /* renamed from: handleAsyncRequestCancelled */
    /* synthetic */ void handleAsyncRequestCancelled(Object obj) {
        this.baseHandler.onRequestCancelled(obj);
    }

    public void onRequest(@Nonnull K k) {
        this.executor.execute(() -> { /* TODO: fix lambda */ });
    }

    public void onRequestCancelled(@Nonnull K k) {
        this.executor.execute(new AnonymousClass1(this, k));
    }
}
