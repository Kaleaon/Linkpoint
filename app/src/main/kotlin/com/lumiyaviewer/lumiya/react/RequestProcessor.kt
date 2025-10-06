package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

abstract class RequestProcessor<K, TUp, TDown>(
    requestSource: RequestSource<K, TUp>,
    private val executor: Executor?
) : RequestHandler<K>, RequestSource<K, TDown>, ResultHandler<K, TDown>, Refreshable<K> {

    private val resultHandler: ResultHandler<K, TUp> = requestSource.attachRequestHandler(this)
    private var requestHandler: RequestHandler<K>? = null

    protected abstract fun processRequest(key: K): TUp?
    
    protected abstract fun processResult(key: K, downstreamData: TDown): TUp

    protected open fun isRequestComplete(key: K, data: TUp?): Boolean = data != null

    private fun processRequestInternal(key: K) {
        val result = processRequest(key)
        if (result != null) {
            resultHandler.onResultData(key, result)
        }
        if (!isRequestComplete(key, result)) {
            requestHandler?.onRequest(key)
        }
    }

    private fun requestUpdateInternal(key: K) {
        requestHandler?.onRequest(key)
    }

    override fun attachRequestHandler(requestHandler: RequestHandler<K>): ResultHandler<K, TDown> {
        this.requestHandler = requestHandler
        return this
    }

    override fun detachRequestHandler(requestHandler: RequestHandler<K>) {
        if (this.requestHandler == requestHandler) {
            this.requestHandler = null
        }
    }

    override fun onRequest(key: K) {
        if (executor != null) {
            executor.execute {
                processRequestInternal(key)
            }
        } else {
            processRequestInternal(key)
        }
    }

    override fun onRequestCancelled(key: K) {
        requestHandler?.onRequestCancelled(key)
    }

    override fun onResultData(key: K, data: TDown) {
        if (executor != null) {
            executor.execute {
                resultHandler.onResultData(key, processResult(key, data))
            }
        } else {
            resultHandler.onResultData(key, processResult(key, data))
        }
    }

    override fun onResultError(key: K, error: Throwable) {
        resultHandler.onResultError(key, error)
    }

    override fun requestUpdate(key: K) {
        if (executor != null) {
            executor.execute {
                requestUpdateInternal(key)
            }
        } else {
            requestUpdateInternal(key)
        }
    }
}
