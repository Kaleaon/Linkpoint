package com.lumiyaviewer.lumiya.react

interface RequestQueue<K, T> : RequestSource<K, T> {
    fun getNextRequest(): K?

    fun getResultHandler(): ResultHandler<K, T>

    fun returnRequest(k: K)

    @Throws(InterruptedException::class)
    fun waitForRequest(): K?
}