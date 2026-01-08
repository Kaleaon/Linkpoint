package com.linkpoint.react

abstract class SimpleRequestHandler<K> : RequestHandler<K> {
    override fun onRequestCancelled(key: K) {
        // Default implementation does nothing
    }
}
