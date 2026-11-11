package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.RequestForwarder;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class RequestForwarder<Kup, Tup, Kdown, Tdown> implements RequestHandler<Kup> {
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<Kup, Tup> resultHandler;
    @Nonnull
    private final Subscribable<Kdown, Tdown> subscribable;
    private final Object lock = new Object();
    private final Map<Kup, RequestForwarder<Kup, Tup, Kdown, Tdown>.DownstreamSubscription> subscriptions = new HashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DownstreamSubscription implements Subscription.OnData<Tdown>, Subscription.OnError, UnsubscribableOne {
        private final Kup key;
        private final Subscription<Kdown, Tdown> subscription;

        private DownstreamSubscription(Kup kup, Kdown kdown) {
            this.key = kup;
            this.subscription = RequestForwarder.this.subscribable.subscribe(kdown, RequestForwarder.this.executor, this, this);
        }

        /* synthetic */ DownstreamSubscription(RequestForwarder requestForwarder, Object obj, Object obj2, DownstreamSubscription downstreamSubscription) {
            this(obj, obj2);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827  reason: not valid java name */
        public /* synthetic */ void m41x317e7a9e(Object obj) {
            RequestForwarder.this.processResultInternal(this.key, obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public void onData(final Tdown tdown) {
            if (RequestForwarder.this.executor != null) {
                RequestForwarder.this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU
                    private final /* synthetic */ void $m$0() {
                        ((RequestForwarder.DownstreamSubscription) this).m41x317e7a9e(tdown);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                RequestForwarder.this.processResultInternal(this.key, tdown);
            }
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnError
        public void onError(Throwable th) {
            RequestForwarder.this.resultHandler.onResultError(this.key, th);
        }

        @Override // com.lumiyaviewer.lumiya.react.UnsubscribableOne
        public void unsubscribe() {
            this.subscription.unsubscribe();
        }
    }

    public RequestForwarder(@Nonnull RequestSource<Kup, Tup> requestSource, @Nonnull Subscribable<Kdown, Tdown> subscribable, @Nullable Executor executor) {
        this.executor = executor;
        this.subscribable = subscribable;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: processRequestInternal */
    public void m40lambda$com_lumiyaviewer_lumiya_react_RequestForwarder_2672(@Nonnull Kup kup) {
        RequestForwarder<Kup, Tup, Kdown, Tdown>.DownstreamSubscription put;
        RequestForwarder<Kup, Tup, Kdown, Tdown>.DownstreamSubscription downstreamSubscription = new DownstreamSubscription(this, kup, getDownstreamKey(kup), null);
        synchronized (this.lock) {
            put = this.subscriptions.put(kup, downstreamSubscription);
        }
        if (put != null) {
            put.unsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processResultInternal(@Nonnull Kup kup, @Nullable Tdown tdown) {
        this.resultHandler.onResultData(kup, processResult(tdown));
    }

    protected abstract Kdown getDownstreamKey(@Nonnull Kup kup);

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequest(@Nonnull final Kup kup) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU.1
                private final /* synthetic */ void $m$0() {
                    ((RequestForwarder) this).m40lambda$com_lumiyaviewer_lumiya_react_RequestForwarder_2672(kup);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            m40lambda$com_lumiyaviewer_lumiya_react_RequestForwarder_2672(kup);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandler
    public void onRequestCancelled(@Nonnull Kup kup) {
        RequestForwarder<Kup, Tup, Kdown, Tdown>.DownstreamSubscription remove;
        synchronized (this.lock) {
            remove = this.subscriptions.remove(kup);
        }
        if (remove != null) {
            remove.unsubscribe();
        }
    }

    protected abstract Tup processResult(@Nullable Tdown tdown);
}
