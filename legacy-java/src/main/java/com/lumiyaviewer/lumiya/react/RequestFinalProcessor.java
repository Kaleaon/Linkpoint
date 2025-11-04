package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class RequestFinalProcessor<K, T> implements RequestHandler<K> {

    @Nullable
    private final Executor executor;

    @Nonnull
    private final ResultHandler<K, T> resultHandler;

    public RequestFinalProcessor(@Nonnull RequestSource<K, T> requestSource, @Nullable Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: cancelRequest, reason: merged with bridge method [inline-methods] */
    public void m33lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_1437(@Nonnull K k) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestFinalProcessor_673, reason: not valid java name */
    public /* synthetic */ void m34lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_673(Object obj) {
        try {
            this.resultHandler.onResultData(obj, processRequest(obj));
        } catch (Throwable th) {
            this.resultHandler.onResultError(obj, th);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$psFcS6-5kKyuCZBH4SbOZwtpXG8
                private final /* synthetic */ void $m$0() {
                    ((RequestFinalProcessor) this).m34lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_673(k);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
            return;
        }
        try {
            this.resultHandler.onResultData(k, processRequest(k));
        } catch (Throwable th) {
            this.resultHandler.onResultError(k, th);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$psFcS6-5kKyuCZBH4SbOZwtpXG8.1
                private final /* synthetic */ void $m$0() {
                    ((RequestFinalProcessor) this).m33lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_1437(k);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            m33lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_1437(k);
        }
    }

    protected abstract T processRequest(@Nonnull K k) throws Throwable;
}
