package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

abstract class RequestOperator<K, T> : RequestHandler<K> {
    private val executor: Executor?
    private val resultHandler: ResultHandler<K, T>
    private val toHandler: RequestHandler<K>

    constructor(requestHandler: RequestHandler<K>, resultHandler: ResultHandler<K, T>) {
        this.toHandler = requestHandler
        this.resultHandler = resultHandler
        this.executor = null
    }

    constructor(
        requestHandler: RequestHandler<K>,
        resultHandler: ResultHandler<K, T>,
        executor: Executor?,
    ) {
        this.toHandler = requestHandler
        this.resultHandler = resultHandler
        this.executor = executor
    }

    override fun onRequest(k: K) {
        if (executor != null) {
            executor.execute {
                val processedRequest = processRequest(k)
                if (processedRequest != null) {
                    resultHandler.onResultData(k, processedRequest)
                } else {
                    toHandler.onRequest(k)
                }
            }
        } else {
            val processedRequest = processRequest(k)
            if (processedRequest != null) {
                resultHandler.onResultData(k, processedRequest)
            } else {
                toHandler.onRequest(k)
            }
        }
    }

    override fun onRequestCancelled(k: K) {
        if (executor != null) {
            executor.execute {
                toHandler.onRequestCancelled(k)
            }
        } else {
            toHandler.onRequestCancelled(k)
        }
    }

    protected abstract fun processRequest(k: K): T?
}