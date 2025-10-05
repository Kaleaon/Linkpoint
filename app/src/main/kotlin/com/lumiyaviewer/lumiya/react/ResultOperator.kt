package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

abstract class ResultOperator<K, TIn, TOut>(
    private val toHandler: ResultHandler<K, TOut>,
    private val executor: Executor? = null
) : ResultHandler<K, TIn> {

    protected abstract fun onData(data: TIn): TOut

    override fun onResultData(key: K, data: TIn) {
        if (executor != null) {
            executor.execute {
                toHandler.onResultData(key, onData(data))
            }
        } else {
            toHandler.onResultData(key, onData(data))
        }
    }

    override fun onResultError(key: K, error: Throwable) {
        if (executor != null) {
            executor.execute {
                toHandler.onResultError(key, error)
            }
        } else {
            toHandler.onResultError(key, error)
        }
    }
}
