package com.linkpoint.res.executors

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

open class WeakExecutor @JvmOverloads constructor(
    name: String,
    maxThreads: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
    queue: BlockingQueue<Runnable> = LinkedBlockingQueue(),
) : ThreadPoolExecutor(
    maxThreads,
    maxThreads,
    60L,
    TimeUnit.SECONDS,
    queue,
    threadFactory(name),
) {

    init {
        allowCoreThreadTimeOut(true)
    }

    companion object {
        private fun threadFactory(name: String): ThreadFactory = ThreadFactory { runnable ->
            Thread(runnable, name).apply { isDaemon = true }
        }
    }
}
