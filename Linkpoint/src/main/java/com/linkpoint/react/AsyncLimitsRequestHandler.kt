package com.linkpoint.react

import java.util.concurrent.Executor

class AsyncLimitsRequestHandler<K>(
    executor: Executor,
    requestHandler: RequestHandler<K>,
    private val isCancellable: Boolean,
    private val maxRequests: Int,
    private val requestTimeout: Long
) : AsyncRequestHandler<K>(executor, requestHandler), RequestHandlerLimits, RequestHandler<K> {

    override fun getMaxRequestsInFlight(): Int {
        return maxRequests
    }

    override fun getRequestTimeout(): Long {
        return requestTimeout
    }

    override fun isRequestCancellable(): Boolean {
        return isCancellable
    }
}