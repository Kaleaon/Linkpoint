package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.Debug
import java.io.IOException
import java.nio.channels.CancelledKeyException
import java.nio.channels.ClosedSelectorException
import java.nio.channels.Selector
import java.util.Timer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

/**
 * Core selector loop for Lumiya's UDP circuits.
 */
open class SLConnection : Runnable {

    companion object {
        private const val DEFAULT_IDLE_INTERVAL_MS = 1_000
    }

    private val selector: Selector = Selector.open()
    @Volatile private var timer: Timer? = null
    @Volatile private var workingThread: Thread? = null
    private val running = AtomicBoolean(false)

    init {
        System.setProperty("java.net.preferIPv4Stack", "true")
        System.setProperty("java.net.preferIPv6Addresses", "false")
    }

    fun addCircuit(circuit: SLCircuit) {
        synchronized(this) {
            if (workingThread == null || !running.get()) {
                running.set(true)
                workingThread = Thread(this, "SLConnection").apply { start() }
            }
        }
        selector.wakeup()
    }

    fun getSelector(): Selector = selector

    fun getTimer(): Timer {
        synchronized(this) {
            if (timer == null) {
                timer = Timer("SLConnectionTimer", true)
            }
            return timer!!
        }
    }

    override fun run() {
        Debug.Log("SLConnection: worker thread started")
        try {
            while (running.get()) {
                val keySnapshot = try {
                    selector.keys().toList()
                } catch (_: ClosedSelectorException) {
                    break
                }

                if (keySnapshot.isEmpty()) {
                    break
                }

                var idleInterval = DEFAULT_IDLE_INTERVAL_MS
                for (key in keySnapshot) {
                    val circuit = key.attachment() as? SLCircuit ?: continue
                    if (!key.isValid) continue
                    try {
                        circuit.processWakeup()
                        idleInterval = min(idleInterval, circuit.getIdleInterval())
                    } catch (ex: Exception) {
                        Debug.Warning(ex)
                    }
                }

                val timeout = idleInterval.toLong().coerceAtLeast(1L)

                val readyCount = try {
                    selector.select(timeout)
                } catch (_: ClosedSelectorException) {
                    break
                } catch (ioe: IOException) {
                    handleSelectorIOException(ioe)
                    continue
                }

                if (readyCount > 0) {
                    processSelectedKeys()
                }
            }
        } finally {
            running.set(false)
            cleanup()
            Debug.Log("SLConnection: worker thread exiting")
        }
    }

    private fun processSelectedKeys() {
        val iterator = try {
            selector.selectedKeys().iterator()
        } catch (_: ClosedSelectorException) {
            return
        }
        while (iterator.hasNext()) {
            val key = iterator.next()
            iterator.remove()

            val circuit = key.attachment() as? SLCircuit ?: continue

            try {
                if (key.isValid && key.isReadable) {
                    circuit.processReceive()
                }
                if (key.isValid && key.isWritable) {
                    circuit.processTransmit()
                }
                if (key.isValid) {
                    circuit.updateSelectorOps()
                    circuit.tryProcessIdle()
                }
            } catch (_: CancelledKeyException) {
                // key cancelled; skip
            } catch (ex: Exception) {
                Debug.Warning(ex)
                circuit.processNetworkError()
            }
        }
    }

    private fun handleSelectorIOException(exception: IOException) {
        Debug.Warning(exception)
        val keySnapshot = try {
            selector.keys().toList()
        } catch (_: Exception) {
            emptyList()
        }
        keySnapshot.forEach { key ->
            val circuit = key.attachment() as? SLCircuit
            if (circuit != null && key.isValid) {
                circuit.processNetworkError()
            }
        }
    }

    private fun cleanup() {
        synchronized(this) {
            timer?.cancel()
            timer = null
            workingThread = null
        }
    }

    fun shutdown() {
        running.set(false)
        selector.wakeup()
    }

    // Legacy aliases ------------------------------------------------------

    fun AddCircuit(circuit: SLCircuit) = addCircuit(circuit)

    fun GetSelector(): Selector = getSelector()

    fun GetTimer(): Timer = getTimer()
}
