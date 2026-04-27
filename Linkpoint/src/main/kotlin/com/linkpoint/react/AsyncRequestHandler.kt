package com.linkpoint.react

import java.util.concurrent.Executor

open class AsyncRequestHandler<K>(
    private val executor: Executor,
    private val baseHandler: RequestHandler<K>
) : RequestHandler<K> {

    override fun onRequest(request: K) {
        executor.execute { 
            baseHandler.onRequest(request) 
        }
    }

    override fun onRequestCancelled(request: K) {
        executor.execute { 
            baseHandler.onRequestCancelled(request) 
        }
    }
}
