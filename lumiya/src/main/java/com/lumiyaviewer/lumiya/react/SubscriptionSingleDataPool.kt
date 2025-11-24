package com.lumiyaviewer.lumiya.react

class SubscriptionSingleDataPool<T> {
    private var value: T? = null
    private val subscribers = mutableListOf<(T) -> Unit>()

    fun setData(key: SubscriptionSingleKey, value: T) {
        this.value = value
        notifySubscribers(value)
    }

    fun getData(): T? {
        return value
    }
    
    fun addSubscriber(subscriber: (T) -> Unit) {
        subscribers.add(subscriber)
        value?.let { subscriber(it) }
    }
    
    fun removeSubscriber(subscriber: (T) -> Unit) {
        subscribers.remove(subscriber)
    }
    
    private fun notifySubscribers(value: T) {
        subscribers.forEach { it(value) }
    }
}
