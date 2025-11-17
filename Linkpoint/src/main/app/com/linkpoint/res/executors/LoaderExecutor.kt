package com.linkpoint.res.executors

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Simplified loader executor for disk-bound tasks (texture/mesh loading).
 */
object LoaderExecutor : ThreadPoolExecutor(
    1,
    Runtime.getRuntime().availableProcessors().coerceAtLeast(2),
    60L,
    TimeUnit.SECONDS,
    LinkedBlockingQueue(),
    { runnable -> Thread(runnable, "ResourceLoader").apply { isDaemon = true } },
) {
    init {
        allowCoreThreadTimeOut(true)
    }
}
