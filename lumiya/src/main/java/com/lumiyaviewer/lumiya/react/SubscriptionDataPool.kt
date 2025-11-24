package com.lumiyaviewer.lumiya.react

class SubscriptionDataPool<K, V> : Subscribable<K, V> {
    private val data = mutableMapOf<K, V?>()
    private var canContainNulls = false

    fun setCanContainNulls(value: Boolean): SubscriptionDataPool<K, V> {
        this.canContainNulls = value
        return this
    }

    fun setData(key: K, value: V?) {
        if (value == null && !canContainNulls) return
        data[key] = value
    }
    
    fun getData(key: K): V? = data[key]
}
