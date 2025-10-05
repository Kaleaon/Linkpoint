package com.lumiyaviewer.lumiya.react

interface RequestHandler<K> {
    fun onRequest(key: K)
    fun onRequestCancelled(key: K)
}
