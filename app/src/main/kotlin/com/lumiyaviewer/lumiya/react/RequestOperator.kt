package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

abstract class RequestOperator<K, T>(
    private val toHandler: RequestHandler<K>,
    private val resultHandler: ResultHandler<K, T>,
    private val executor: Executor? = null
) : RequestHandler<K> {

    protected abstract fun processRequest(key: K): T?

    override fun onRequest(key: K) {
        if (executor != null) {
            executor.execute {
                val result = processRequest(key)
                if (result != null) {
                    resultHandler.onResultData(key, result)
                } else {
                    toHandler.onRequest(key)
                }
            }
        } else {
            val result = processRequest(key)
            if (result != null) {
                resultHandler.onResultData(key, result)
            } else {
                toHandler.onRequest(key)
            }
        }
    }

    override fun onRequestCancelled(key: K) {
        if (executor != null) {
            executor.execute {
                toHandler.onRequestCancelled(key)
            }
        } else {
            toHandler.onRequestCancelled(key)
        }
    }
}
