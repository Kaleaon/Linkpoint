package com.linkpoint.react

interface RequestHandler<K> {
    fun onRequest(request: K)
    fun onRequestCancelled(request: K)
}