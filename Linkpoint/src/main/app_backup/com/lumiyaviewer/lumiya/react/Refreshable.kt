package com.lumiyaviewer.lumiya.react

/**
 * Interface for objects that can request updates with a key.
 * Converted from Java to Kotlin for better type safety.
 */
interface Refreshable<K> {
    fun requestUpdate(key: K)
}
