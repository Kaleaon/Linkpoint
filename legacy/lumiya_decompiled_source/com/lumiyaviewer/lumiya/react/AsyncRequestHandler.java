package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class AsyncRequestHandler<K> implements RequestHandler<K> {
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final Executor executor;

    public AsyncRequestHandler(@Nonnull Executor executor, @Nonnull RequestHandler<K> requestHandler) {
        this.executor = executor;
        this.baseHandler = requestHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553  reason: not valid java name */
    public /* synthetic */ void m28lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553(Object obj) {
        this.baseHandler.onRequest(obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_AsyncRequestHandler_690  reason: not valid java name */
    public /* synthetic */ void m29lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_690(Object obj) {
        this.baseHandler.onRequestCancelled(obj);
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull final K k) {
        this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o
            private final /* synthetic */ void $m$0() {
                ((AsyncRequestHandler) this).m28lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553(k);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull final K k) {
        this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o.1
            private final /* synthetic */ void $m$0() {
                ((AsyncRequestHandler) this).m29lambda$com_lumiyaviewer_lumiya_react_AsyncRequestHandler_690(k);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }
}
