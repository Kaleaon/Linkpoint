package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

abstract class RequestFinalProcessor<K, T>(
    requestSource: RequestSource<K, T>,
    private val executor: Executor?
) : RequestHandler<K> {

    private val resultHandler: ResultHandler<K, T> = requestSource.attachRequestHandler(this)

    protected abstract fun processRequest(key: K): T

    protected open fun handleRequestCancellation(key: K) {
        // Default empty implementation
    }

    private fun handleRequestProcessingInternal(key: K) {
        try {
            val result = processRequest(key)
            resultHandler.onResultData(key, result)
        } catch (error: Throwable) {
            resultHandler.onResultError(key, error)
        }
    }

    override fun onRequest(key: K) {
        if (executor != null) {
            executor.execute {
                handleRequestProcessingInternal(key)
            }
        } else {
            handleRequestProcessingInternal(key)
        }
    }

    override fun onRequestCancelled(key: K) {
        if (executor != null) {
            executor.execute {
                handleRequestCancellation(key)
            }
        } else {
            handleRequestCancellation(key)
        }
    }
}
