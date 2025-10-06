package com.lumiyaviewer.lumiya.react

import java.util.concurrent.Executor

class AsyncLimitsRequestHandler<K>(
    executor: Executor,
    baseHandler: RequestHandler<K>,
    private val isCancellable: Boolean,
    private val maxRequests: Int,
    private val requestTimeout: Long
) : AsyncRequestHandler<K>(executor, baseHandler), RequestHandlerLimits {

    override fun getMaxRequestsInFlight(): Int = maxRequests

    override fun getRequestTimeout(): Long = requestTimeout

    override fun isRequestCancellable(): Boolean = isCancellable
}
