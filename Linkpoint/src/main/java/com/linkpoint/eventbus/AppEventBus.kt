package com.linkpoint.eventbus

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Interface representing an active event bus subscription.
 */
interface Subscription {
    fun unsubscribe()
}

/**
 * Application-wide event bus for decoupling packet handlers from core managers.
 */
object AppEventBus {
    private val listeners = ConcurrentHashMap<Class<*>, CopyOnWriteArrayList<(Any) -> Unit>>()

    /**
     * Publishes an event to all registered listeners.
     */
    fun publish(event: Any) {
        val eventType = event.javaClass
        listeners.forEach { (registeredType, listenerList) ->
            if (registeredType.isAssignableFrom(eventType)) {
                listenerList.forEach { listener ->
                    try {
                        listener(event)
                    } catch (e: Exception) {
                        // Suppress listener exceptions to keep event bus dispatching reliable
                    }
                }
            }
        }
    }

    /**
     * Subscribes a listener function for events of type [T].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> subscribe(eventType: Class<T>, listener: (T) -> Unit): Subscription {
        val listenerWrapper: (Any) -> Unit = { event ->
            if (eventType.isInstance(event)) {
                listener(event as T)
            }
        }
        val listenerList = listeners.getOrPut(eventType) { CopyOnWriteArrayList() }
        listenerList.add(listenerWrapper)

        return object : Subscription {
            override fun unsubscribe() {
                listenerList.remove(listenerWrapper)
            }
        }
    }

    /**
     * Inline reified helper for subscribing.
     */
    inline fun <reified T : Any> subscribe(noinline listener: (T) -> Unit): Subscription {
        return subscribe(T::class.java, listener)
    }

    /**
     * Clears all registered subscriptions. Used on logout / session termination.
     */
    fun clear() {
        listeners.clear()
    }
}
