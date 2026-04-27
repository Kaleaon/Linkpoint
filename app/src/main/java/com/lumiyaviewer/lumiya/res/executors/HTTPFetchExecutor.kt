package com.lumiyaviewer.lumiya.res.executors

import java.util.concurrent.PriorityBlockingQueue

/**
 * Executor for HTTP fetch operations with priority queue
 */
object HTTPFetchExecutor : WeakExecutor(
    "ResourceHTTPFetch",
    4,
    PriorityBlockingQueue()
) {
    
    /**
     * Get singleton instance
     */
    fun getInstance(): HTTPFetchExecutor = this
}
