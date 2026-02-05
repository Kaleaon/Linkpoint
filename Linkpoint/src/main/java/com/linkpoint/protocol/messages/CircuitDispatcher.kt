package com.linkpoint.protocol.messages

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Single-threaded dispatcher for circuit/network work.
 *
 * Keeps UDP receive, ACK, and timeout handling serialized on a dedicated thread,
 * matching Lumiya's deterministic circuit processing model.
 */
object CircuitDispatcher {
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "CircuitThread").apply { isDaemon = true }
    }

    val dispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()

    fun shutdown() {
        dispatcher.close()
    }
}
