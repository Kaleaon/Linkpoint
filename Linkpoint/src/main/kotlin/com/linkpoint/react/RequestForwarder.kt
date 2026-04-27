package com.linkpoint.react

import com.linkpoint.react.-$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU.AnonymousClass1
import com.linkpoint.react.Subscription.OnData
import com.linkpoint.react.Subscription.OnError
import java.util.HashMap
import java.util.Map
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class RequestForwarder<Kup, Tup, Kdown, Tdown> : RequestHandler<Kup> {
    private val Executor executor
    private val Object lock = Object()
    private val ResultHandler<Kup, Tup> resultHandler
    private val Subscribable<Kdown, Tdown> subscribable
    private val Map<Kup, DownstreamSubscription> subscriptions = HashMap()

    private class DownstreamSubscription : OnData<Tdown>, OnError, UnsubscribableOne {
        private val Kup key
        private val Subscription<Kdown, Tdown> subscription

        private DownstreamSubscription(Kup kup, Kdown kdown) {
            this.key = kup
            this.subscription = RequestForwarder.this.subscribable.subscribe(kdown, RequestForwarder.this.executor, this, this)
        }

        /* synthetic */ DownstreamSubscription(RequestForwarder requestForwarder, Object obj, Object obj2, DownstreamSubscription downstreamSubscription) {
            this(obj, obj2)
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827  reason: not valid java name */
        public /* synthetic */ Unit m30lambda$com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827(Object obj) {
            RequestForwarder.this.processResultInternal(this.key, obj)
        }

        fun onData(tdown: Tdown) {
            if (RequestForwarder.this.executor != null) {
                RequestForwarder.this.executor.execute(-$Lambda$swF2K5wPKI2_xA-bWP-XwHVnywU(this, tdown))
            } else {
                RequestForwarder.this.processResultInternal(this.key, tdown)
            }
        }

        fun onError(th: Throwable) {
            RequestForwarder.this.resultHandler.onResultError(this.key, th)
        }

        fun unsubscribe() {
            this.subscription.unsubscribe()
        }
    }

    public RequestForwarder(RequestSource<Kup, Tup> requestSource, Subscribable<Kdown, Tdown> subscribable, Executor executor) {
        this.executor = executor
        this.subscribable = subscribable
        this.resultHandler = requestSource.attachRequestHandler(this)
    }

     private fun processRequestInternal(kup: Kup) {
        val downstreamSubscription: DownstreamSubscription = DownstreamSubscription(this, kup, getDownstreamKey(kup), null)
        synchronized (this.lock) {
            downstreamSubscription = (DownstreamSubscription) this.subscriptions.put(kup, downstreamSubscription)
        }
        if (downstreamSubscription != null) {
            downstreamSubscription.unsubscribe()
        }
    }

     private fun processResultInternal(kup: Kup, tdown: Tdown) {
        this.resultHandler.onResultData(kup, processResult(tdown))
    }

    protected abstract Kdown getDownstreamKey(Kup kup)

    /* synthetic */ Unit handleRequestProcessing(Object obj) {
        processRequestInternal(obj)
    }

    fun onRequest(kup: Kup) {
        if (this.executor != null) {
            this.executor.execute(AnonymousClass1(this, kup))
        } else {
            processRequestInternal(kup)
        }
    }

    fun onRequestCancelled(kup: Kup) {
        DownstreamSubscription downstreamSubscription
        synchronized (this.lock) {
            downstreamSubscription = (DownstreamSubscription) this.subscriptions.remove(kup)
        }
        if (downstreamSubscription != null) {
            downstreamSubscription.unsubscribe()
        }
    }

    protected abstract Tup processResult(Tdown tdown)
}
