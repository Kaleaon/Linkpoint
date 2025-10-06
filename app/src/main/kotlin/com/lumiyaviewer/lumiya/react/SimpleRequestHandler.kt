package com.lumiyaviewer.lumiya.react

abstract class SimpleRequestHandler<K> : RequestHandler<K> {
    override fun onRequestCancelled(key: K) {
        // Default empty implementation
    }
}
