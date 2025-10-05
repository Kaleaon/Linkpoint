package com.lumiyaviewer.lumiya.react

interface RequestSource<K, T> {
    fun attachRequestHandler(requestHandler: RequestHandler<K>): ResultHandler<K, T>
    fun detachRequestHandler(requestHandler: RequestHandler<K>)
}
