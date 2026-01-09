package com.linkpoint.react

import java.util.concurrent.Executor

abstract class ResultOperator<K, Tin, Tout> : ResultHandler<K, Tin> {
    private val executor: Executor?
    private val toHandler: ResultHandler<K, Tout>

    constructor(resultHandler: ResultHandler<K, Tout>) {
        this.toHandler = resultHandler
        this.executor = null
    }

    constructor(resultHandler: ResultHandler<K, Tout>, executor: Executor?) {
        this.toHandler = resultHandler
        this.executor = executor
    }

    protected abstract fun onData(tin: Tin): Tout

    override fun onResultData(k: K, tin: Tin) {
        if (executor != null) {
            executor.execute {
                toHandler.onResultData(k, onData(tin))
            }
        } else {
            toHandler.onResultData(k, onData(tin))
        }
    }

    override fun onResultError(k: K, th: Throwable) {
        if (executor != null) {
            executor.execute {
                toHandler.onResultError(k, th)
            }
        } else {
            toHandler.onResultError(k, th)
        }
    }
}