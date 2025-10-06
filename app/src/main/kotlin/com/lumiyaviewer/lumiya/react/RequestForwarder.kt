package com.lumiyaviewer.lumiya.react

import com.lumiyaviewer.lumiya.react.Subscription.OnData
import com.lumiyaviewer.lumiya.react.Subscription.OnError
import java.util.HashMap
import java.util.concurrent.Executor

abstract class RequestForwarder<KUp, TUp, KDown, TDown>(
    requestSource: RequestSource<KUp, TUp>,
    private val subscribable: Subscribable<KDown, TDown>,
    private val executor: Executor?
) : RequestHandler<KUp> {

    private val lock = Any()
    private val resultHandler: ResultHandler<KUp, TUp> = requestSource.attachRequestHandler(this)
    private val subscriptions = HashMap<KUp, DownstreamSubscription>()

    private inner class DownstreamSubscription(
        private val key: KUp,
        downstreamKey: KDown
    ) : OnData<TDown>, OnError, UnsubscribableOne {
        
        private val subscription: Subscription<KDown, TDown> = 
            subscribable.subscribe(downstreamKey, executor, this, this)

        override fun onData(data: TDown) {
            if (executor != null) {
                executor.execute {
                    processResultInternal(key, data)
                }
            } else {
                processResultInternal(key, data)
            }
        }

        override fun onError(error: Throwable) {
            resultHandler.onResultError(key, error)
        }

        override fun unsubscribe() {
            subscription.unsubscribe()
        }
    }

    protected abstract fun getDownstreamKey(upstreamKey: KUp): KDown
    
    protected abstract fun processResult(downstreamData: TDown?): TUp

    private fun processRequestInternal(key: KUp) {
        val newSubscription = DownstreamSubscription(key, getDownstreamKey(key))
        val oldSubscription = synchronized(lock) {
            subscriptions.put(key, newSubscription)
        }
        oldSubscription?.unsubscribe()
    }

    private fun processResultInternal(key: KUp, data: TDown?) {
        resultHandler.onResultData(key, processResult(data))
    }

    override fun onRequest(key: KUp) {
        if (executor != null) {
            executor.execute {
                processRequestInternal(key)
            }
        } else {
            processRequestInternal(key)
        }
    }

    override fun onRequestCancelled(key: KUp) {
        val subscription = synchronized(lock) {
            subscriptions.remove(key)
        }
        subscription?.unsubscribe()
    }
}
