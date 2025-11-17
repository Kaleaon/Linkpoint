package com.linkpoint.res.executors

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory

/**
 * Singleton executor used by [com.linkpoint.res.ResourceManager] to
 * periodically prune weak caches and cancel orphaned requests.
 */
class ResourceCleanupExecutor private constructor() :
    ScheduledThreadPoolExecutor(1, ThreadFactory { task ->
        Thread(task, "ResourceCleanup").apply { isDaemon = true }
    }) {

    companion object {
        private val instance = ResourceCleanupExecutor()

        @JvmStatic
        fun getInstance(): ResourceCleanupExecutor = instance
    }
}
