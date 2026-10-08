package com.linkpoint.protocol.capabilities

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Single-threaded dispatcher for capability event queue handlers.
 *
 * Ensures capability events are processed in order on a dedicated thread,
 * avoiding contention with Default/IO dispatchers.
 */
object EventQueueDispatcher {
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "EventQueueThread").apply { isDaemon = true }
    }

    val dispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()

    fun shutdown() {
        dispatcher.close()
    }
}
