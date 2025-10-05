package com.linkpoint.res.executors

import java.util.concurrent.ScheduledThreadPoolExecutor

class ResourceCleanupExecutor private constructor() : ScheduledThreadPoolExecutor(
    1,
    { runnable -> Thread(runnable, "ResourceCleanup") }
) {
    companion object {
        @JvmStatic
        val instance: ResourceCleanupExecutor by lazy { ResourceCleanupExecutor() }
    }
}