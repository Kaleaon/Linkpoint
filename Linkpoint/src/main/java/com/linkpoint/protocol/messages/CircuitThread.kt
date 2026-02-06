package com.linkpoint.protocol.messages

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import java.io.Closeable
import java.util.concurrent.Executors

/**
 * Single-threaded circuit dispatcher for ordered UDP/circuit processing.
 * Mirrors Linkpoint circuit threading for deterministic packet handling.
 */
class CircuitThread(threadName: String) : Closeable {
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, threadName).apply {
            isDaemon = true
        }
    }

    private val executorDispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()
    val dispatcher: CoroutineDispatcher = executorDispatcher
    val scope: CoroutineScope = CoroutineScope(executorDispatcher + SupervisorJob())

    override fun close() {
        scope.cancel()
        executorDispatcher.close()
        executor.shutdown()
    }
}
