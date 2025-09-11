package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.-$Lambda$3htMVvcf7XlS6QCgMv3cESjj4go.AnonymousClass1;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RequestOperator<K, T> implements RequestHandler<K> {
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, T> resultHandler;
    @Nonnull
    private final RequestHandler<K> toHandler;

    public RequestOperator(@Nonnull RequestHandler<K> requestHandler, @Nonnull ResultHandler<K, T> resultHandler) {
        this.toHandler = requestHandler;
        this.resultHandler = resultHandler;
        this.executor = null;
    }

    public RequestOperator(@Nonnull RequestHandler<K> requestHandler, @Nonnull ResultHandler<K, T> resultHandler, @Nullable Executor executor) {
        this.toHandler = requestHandler;
        this.resultHandler = resultHandler;
        this.executor = executor;
    }

    /* synthetic */ void handleRequestCancellation(Object obj) {
        this.toHandler.onRequestCancelled(obj);
    }

    /* synthetic */ void processRequestData(Object obj) {
        Object processRequest = processRequest(obj);
        if (processRequest != null) {
            this.resultHandler.onResultData(obj, processRequest);
        } else {
            this.toHandler.onRequest(obj);
        }
    }

    public void onRequest(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(new -$Lambda$3htMVvcf7XlS6QCgMv3cESjj4go(this, k));
            return;
        }
        Object processRequest = processRequest(k);
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest);
        } else {
            this.toHandler.onRequest(k);
        }
    }

    public void onRequestCancelled(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(new AnonymousClass1(this, k));
        } else {
            this.toHandler.onRequestCancelled(k);
        }
    }

    protected abstract T processRequest(@Nonnull K k);
}
