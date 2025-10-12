package com.lumiyaviewer.lumiya.utils.reqset

/**
 * Listener interface for request completion notifications
 * Modernized Kotlin version with generic type support
 */
fun interface RequestCompleteListener<T> {
    fun onRequestComplete(result: T)
}