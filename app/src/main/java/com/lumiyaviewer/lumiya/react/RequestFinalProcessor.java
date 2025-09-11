package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.-$Lambda$psFcS6-5kKyuCZBH4SbOZwtpXG8.AnonymousClass1;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RequestFinalProcessor<K, T> implements RequestHandler<K> {
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, T> resultHandler;

    public RequestFinalProcessor(@Nonnull RequestSource<K, T> requestSource, @Nullable Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    /* renamed from: cancelRequest */
    protected void handleRequestCancellation(@Nonnull K k) {
    }

    /* renamed from: handleRequestProcessing */
    /* synthetic */ void handleRequestProcessing(Object obj) {
        try {
            this.resultHandler.onResultData(obj, processRequest(obj));
        } catch (Throwable th) {
            this.resultHandler.onResultError(obj, th);
        }
    }

    public void onRequest(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(new -$Lambda$psFcS6-5kKyuCZBH4SbOZwtpXG8(this, k));
            return;
        }
        try {
            this.resultHandler.onResultData(k, processRequest(k));
        } catch (Throwable th) {
            this.resultHandler.onResultError(k, th);
        }
    }

    public void onRequestCancelled(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(new AnonymousClass1(this, k));
        } else {
            handleRequestCancellation(k);
        }
    }

    protected abstract T processRequest(@Nonnull K k) throws Throwable;
}
