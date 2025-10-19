package com.lumiyaviewer.lumiya.res.executors

import com.lumiyaviewer.lumiya.GlobalOptions
import java.util.concurrent.PriorityBlockingQueue

/**
 * Executor for HTTP fetch operations with priority queue
 */
object HTTPFetchExecutor : WeakExecutor(
    "ResourceHTTPFetch",
    GlobalOptions.getMaxTextureDownloads(),
    PriorityBlockingQueue()
) {
    
    /**
     * Get singleton instance
     */
    fun getInstance(): HTTPFetchExecutor = this
}
