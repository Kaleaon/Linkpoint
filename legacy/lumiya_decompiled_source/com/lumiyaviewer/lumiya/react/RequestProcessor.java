package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class RequestProcessor<K, Tup, Tdown> implements RequestHandler<K>, RequestSource<K, Tdown>, ResultHandler<K, Tdown>, Refreshable<K> {
    @Nullable
    private final Executor executor;
    @Nullable
    private RequestHandler<K> requestHandler;
    @Nonnull
    private final ResultHandler<K, Tup> resultHandler;

    public RequestProcessor(@Nonnull RequestSource<K, Tup> requestSource, @Nullable Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: processRequestInternal */
    public void m46lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(@Nonnull K k) {
        Tup processRequest = processRequest(k);
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest);
        }
        if (isRequestComplete(k, processRequest) || this.requestHandler == null) {
            return;
        }
        this.requestHandler.onRequest(k);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestUpdateInternal */
    public void m44lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(@Nonnull K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequest(k);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public ResultHandler<K, Tdown> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        if (this.requestHandler == requestHandler) {
            this.requestHandler = null;
        }
    }

    protected boolean isRequestComplete(@Nonnull K k, Tup tup) {
        return tup != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestProcessor_2159  reason: not valid java name */
    public /* synthetic */ void m45lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_2159(Object obj, Object obj2) {
        this.resultHandler.onResultData(obj, processResult(obj, obj2));
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w
                private final /* synthetic */ void $m$0() {
                    ((RequestProcessor) this).m46lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            m46lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequestCancelled(k);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultData(@Nonnull final K k, final Tdown tdown) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w.2
                private final /* synthetic */ void $m$0() {
                    ((RequestProcessor) this).m45lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_2159(k, tdown);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            this.resultHandler.onResultData(k, processResult(k, tdown));
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultError(@Nonnull K k, Throwable th) {
        this.resultHandler.onResultError(k, th);
    }

    @Nullable
    protected abstract Tup processRequest(@Nonnull K k);

    protected abstract Tup processResult(@Nonnull K k, Tdown tdown);

    @Override // com.lumiyaviewer.lumiya.react.Refreshable
    public void requestUpdate(final K k) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w.1
                private final /* synthetic */ void $m$0() {
                    ((RequestProcessor) this).m44lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            m44lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k);
        }
    }
}
