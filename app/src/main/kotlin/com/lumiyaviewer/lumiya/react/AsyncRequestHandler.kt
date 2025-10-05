package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

open class AsyncRequestHandler<K>(
    private val executor: Executor,
    private val baseHandler: RequestHandler<K>
) : RequestHandler<K> {

    override fun onRequest(key: K) {
        executor.execute {
            baseHandler.onRequest(key)
        }
    }

    override fun onRequestCancelled(key: K) {
        executor.execute {
            baseHandler.onRequestCancelled(key)
        }
    }
}
