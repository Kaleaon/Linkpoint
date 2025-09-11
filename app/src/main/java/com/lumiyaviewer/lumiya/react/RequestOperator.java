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

    /* synthetic */ void processRequestAsync(Object obj) {
        Object result = processRequest(obj);
        if (result != null) {
            this.resultHandler.onResultData(obj, result);
        } else {
            this.toHandler.onRequest(obj);
        }
    }

    /* synthetic */ void cancelRequestAsync(Object obj) {
        this.toHandler.onRequestCancelled(obj);
    }

    public void onRequest(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(() -> processRequestAsync(k));
            return;
        }
        Object processResult = processRequest(k);
        if (processResult != null) {
            this.resultHandler.onResultData(k, processResult);
        } else {
            this.toHandler.onRequest(k);
        }
    }

    public void onRequestCancelled(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(() -> cancelRequestAsync(k));
        } else {
            this.toHandler.onRequestCancelled(k);
        }
    }

    protected abstract T processRequest(@Nonnull K k);
}
