package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.-$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU.AnonymousClass1;
import com.lumiyaviewer.lumiya.react.Subscription.OnData;
import com.lumiyaviewer.lumiya.react.Subscription.OnError;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RequestForwarder<Kup, Tup, Kdown, Tdown> implements RequestHandler<Kup> {
    @Nullable
    private final Executor executor;
    private final Object lock = new Object();
    @Nonnull
    private final ResultHandler<Kup, Tup> resultHandler;
    @Nonnull
    private final Subscribable<Kdown, Tdown> subscribable;
    private final Map<Kup, DownstreamSubscription> subscriptions = new HashMap();

    private class DownstreamSubscription implements OnData<Tdown>, OnError, UnsubscribableOne {
        private final Kup key;
        private final Subscription<Kdown, Tdown> subscription;

        private DownstreamSubscription(Kup kup, Kdown kdown) {
            this.key = kup;
            this.subscription = RequestForwarder.this.subscribable.subscribe(kdown, RequestForwarder.this.executor, this, this);
        }

        /* synthetic */ DownstreamSubscription(RequestForwarder requestForwarder, Object obj, Object obj2, DownstreamSubscription downstreamSubscription) {
            this(obj, obj2);
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827  reason: not valid java name */
        public /* synthetic */ void m30lambda$com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827(Object obj) {
            RequestForwarder.this.processResultInternal(this.key, obj);
        }

        public void onData(Tdown tdown) {
            if (RequestForwarder.this.executor != null) {
                RequestForwarder.this.executor.execute(new -$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU(this, tdown));
            } else {
                RequestForwarder.this.processResultInternal(this.key, tdown);
            }
        }

        public void onError(Throwable th) {
            RequestForwarder.this.resultHandler.onResultError(this.key, th);
        }

        public void unsubscribe() {
            this.subscription.unsubscribe();
        }
    }

    public RequestForwarder(@Nonnull RequestSource<Kup, Tup> requestSource, @Nonnull Subscribable<Kdown, Tdown> subscribable, @Nullable Executor executor) {
        this.executor = executor;
        this.subscribable = subscribable;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    private void processRequestInternal(@Nonnull Kup kup) {
        DownstreamSubscription downstreamSubscription = new DownstreamSubscription(this, kup, getDownstreamKey(kup), null);
        synchronized (this.lock) {
            downstreamSubscription = (DownstreamSubscription) this.subscriptions.put(kup, downstreamSubscription);
        }
        if (downstreamSubscription != null) {
            downstreamSubscription.unsubscribe();
        }
    }

    private void processResultInternal(@Nonnull Kup kup, @Nullable Tdown tdown) {
        this.resultHandler.onResultData(kup, processResult(tdown));
    }

    protected abstract Kdown getDownstreamKey(@Nonnull Kup kup);

    /* synthetic */ void handleRequestProcessing(Object obj) {
        processRequestInternal(obj);
    }

    public void onRequest(@Nonnull Kup kup) {
        if (this.executor != null) {
            this.executor.execute(new AnonymousClass1(this, kup));
        } else {
            processRequestInternal(kup);
        }
    }

    public void onRequestCancelled(@Nonnull Kup kup) {
        DownstreamSubscription downstreamSubscription;
        synchronized (this.lock) {
            downstreamSubscription = (DownstreamSubscription) this.subscriptions.remove(kup);
        }
        if (downstreamSubscription != null) {
            downstreamSubscription.unsubscribe();
        }
    }

    protected abstract Tup processResult(@Nullable Tdown tdown);
}
