package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Circuit implementation that processes inbound messages on a dedicated worker thread.
 */
class SLThreadingCircuit @JvmOverloads constructor(
    gridConnection: SLGridConnection,
    circuitInfo: SLCircuitInfo,
    authReply: SLAuthReply,
    previousCircuit: SLCircuit? = null,
) : SLCircuit(gridConnection, circuitInfo, authReply, previousCircuit), Executor {

    companion object {
        private const val POLL_TIMEOUT_MS = 1_000L
    }

    private val workEnabled = AtomicBoolean(true)
    private val queue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private val workerThread = Thread({ runWorker() }, "SLCircuit-${circuitInfo.circuitCode}").apply { start() }

    private fun runWorker() {
        Debug.Log("SLThreadingCircuit: worker thread started")
        try {
            while (workEnabled.get() && !Thread.currentThread().isInterrupted) {
                val task = queue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                if (task != null) {
                    task.run()
                } else {
                    try {
                        invokeProcessIdle()
                    } catch (ex: Exception) {
                        Debug.Warning(ex)
                    }
                }
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            Debug.Log("SLThreadingCircuit: worker thread exiting")
        }
    }

    private fun stopWorker() {
        if (workEnabled.compareAndSet(true, false)) {
            workerThread.interrupt()
        }
    }

    override fun handleMessage(message: SLMessage) {
        if (!queue.offer(Runnable { super.handleMessage(message) })) {
            super.handleMessage(message)
        }
    }

    override fun processCloseCircuit() {
        stopWorker()
        super.processCloseCircuit()
    }

    override fun processNetworkError() {
        stopWorker()
        super.processNetworkError()
    }

    override fun processTimeout() {
        stopWorker()
        super.processTimeout()
    }

    override fun execute(command: Runnable) {
        if (!queue.offer(command)) {
            command.run()
        }
    }
}
