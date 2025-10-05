package com.linkpoint.react

abstract class SimpleRequestHandler<K> : RequestHandler<K> {
    override fun onRequestCancelled(request: K) {
        // Default empty implementation
    }
}