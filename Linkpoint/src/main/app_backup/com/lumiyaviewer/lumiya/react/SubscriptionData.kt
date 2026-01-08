package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

class SubscriptionData<K, V>(private val executor: Executor, private val callback: Any?) {
    private var data: V? = null
    private var sourcePool: SubscriptionSingleDataPool<V>? = null

    fun getData(): V? = data
    
    fun subscribe(pool: SubscriptionSingleDataPool<V>, key: K) {
        this.sourcePool = pool
        pool.addSubscriber { value ->
             this.data = value
             notifyCallback(value)
        }
    }
    
    fun subscribe(source: Any?, key: K) {
        // Placeholder for other sources
    }
    
    fun unsubscribe() {
        // Placeholder
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun notifyCallback(value: V) {
        if (callback is Subscription.OnDataCallback<*>) {
             executor.execute {
                 (callback as Subscription.OnDataCallback<V>).onData(value)
             }
        }
    }
}
