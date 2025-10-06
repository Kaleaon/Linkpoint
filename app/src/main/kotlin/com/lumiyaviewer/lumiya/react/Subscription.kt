package com.lumiyaviewer.lumiya.react

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class Subscription<K, T>(
    private val key: K,
    private val subscriptionPool: Unsubscribable<K, T>,
    private val executor: Executor?,
    private val onData: OnData<T>,
    private val onError: OnError?,
    referenceQueue: ReferenceQueue<Subscription<K, T>>?
) : RefreshableOne {

    interface OnData<T> {
        fun onData(data: T)
    }

    interface OnError {
        fun onError(error: Throwable)
    }

    internal class SubscriptionReference<K, T>(
        subscription: Subscription<K, T>,
        referenceQueue: ReferenceQueue<in Subscription<K, T>>?
    ) : WeakReference<Subscription<K, T>>(subscription, referenceQueue) {
        val key: K = subscription.getKey()
    }

    private val reference: SubscriptionReference<K, T> = SubscriptionReference(this, referenceQueue)

    fun getKey(): K = key

    internal fun getReference(): SubscriptionReference<K, T> = reference

    internal fun onData(data: T) {
        if (executor != null) {
            executor.execute { onData.onData(data) }
        } else {
            onData.onData(data)
        }
    }

    internal fun onError(error: Throwable) {
        onError?.let { errorHandler ->
            if (executor != null) {
                executor.execute { errorHandler.onError(error) }
            } else {
                errorHandler.onError(error)
            }
        }
    }

    override fun requestRefresh() {
        if (subscriptionPool is Refreshable<*>) {
            @Suppress("UNCHECKED_CAST")
            (subscriptionPool as Refreshable<K>).requestUpdate(key)
        }
    }

    fun unsubscribe() {
        subscriptionPool.unsubscribe(this)
    }
}
