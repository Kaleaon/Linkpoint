package com.linkpoint.protocol.messages

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.Closeable

import com.linkpoint.network.NetworkLogger

/**
 * Serial task queue for circuit-level message handling and idle processing.
 */
class CircuitTaskQueue(
    private val scope: CoroutineScope,
    private val idleIntervalMs: Long,
    private val queueName: String
) : Closeable {
    private val channel = Channel<suspend () -> Unit>(Channel.UNLIMITED)
    private var idleHandler: (suspend () -> Unit)? = null

    private val job = scope.launch {
        while (isActive) {
            val task = withTimeoutOrNull(idleIntervalMs) { channel.receive() }
            if (task != null) {
                try {
                    task()
                } catch (e: Exception) {
                    NetworkLogger.log(
                        NetworkLogger.Level.ERROR,
                        NetworkLogger.Category.UDP,
                        "Circuit queue $queueName task error: ${e.message}"
                    )
                }
            } else {
                try {
                    idleHandler?.invoke()
                } catch (e: Exception) {
                    NetworkLogger.log(
                        NetworkLogger.Level.ERROR,
                        NetworkLogger.Category.UDP,
                        "Circuit queue $queueName idle error: ${e.message}"
                    )
                }
            }
        }
    }

    fun enqueue(task: suspend () -> Unit) {
        val result = channel.trySend(task)
        if (!result.isSuccess) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "Circuit queue $queueName dropped task: ${result.exceptionOrNull()?.message}"
            )
        }
    }

    fun setIdleHandler(handler: suspend () -> Unit) {
        idleHandler = handler
    }

    fun clearPending() {
        while (channel.tryReceive().isSuccess) {
            // Drain pending tasks.
        }
    }

    override fun close() {
        job.cancel()
        channel.close()
    }
}
