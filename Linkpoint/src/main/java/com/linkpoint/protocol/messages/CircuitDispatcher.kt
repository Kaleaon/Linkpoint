package com.linkpoint.protocol.messages

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Single-threaded dispatcher for circuit/network work.
 *
 * Keeps UDP receive, ACK, and timeout handling serialized on a dedicated thread,
 * matching the proven deterministic circuit processing model.
 */
object CircuitDispatcher {
    @Volatile
    private var currentExecutor: ExecutorService = createExecutor()

    @Volatile
    private var currentDispatcher: ExecutorCoroutineDispatcher = currentExecutor.asCoroutineDispatcher()

    private fun createExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "CircuitThread").apply { isDaemon = true }
        }
    }

    val dispatcher: CoroutineDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            getOrRefreshDispatcher().dispatch(context, block)
        }

        override fun isDispatchNeeded(context: CoroutineContext): Boolean {
            return getOrRefreshDispatcher().isDispatchNeeded(context)
        }
    }

    @Synchronized
    private fun getOrRefreshDispatcher(): ExecutorCoroutineDispatcher {
        if (currentExecutor.isShutdown || currentExecutor.isTerminated) {
            currentExecutor = createExecutor()
            currentDispatcher = currentExecutor.asCoroutineDispatcher()
        }
        return currentDispatcher
    }

    @Synchronized
    fun shutdown() {
        if (!currentExecutor.isShutdown) {
            currentDispatcher.close()
        }
    }

    @Synchronized
    fun reset() {
        shutdown()
        currentExecutor = createExecutor()
        currentDispatcher = currentExecutor.asCoroutineDispatcher()
    }
}
