package com.lumiyaviewer.lumiya.react

import com.lumiyaviewer.lumiya.react.Subscription

/**
 * Interface for objects that can unsubscribe from subscriptions.
 * Converted from Java to Kotlin for better type safety.
 */
interface Unsubscribable<K, T> {
    fun unsubscribe(subscription: Subscription)
}
