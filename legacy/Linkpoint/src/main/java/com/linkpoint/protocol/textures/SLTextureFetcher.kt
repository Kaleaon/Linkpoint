package com.linkpoint.protocol.textures

import android.util.Log
import com.linkpoint.network.MeteredAssetGate
import java.io.File
import java.util.PriorityQueue
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Lifecycle-aware texture fetcher and queue manager.
 * Manages UDP and HTTP texture download queues, supporting pause and resume controls
 * to eliminate network power consumption during background or full-screen overlay states.
 */
open class SLTextureFetcher : TextureQueueController {

    companion object {
        private const val TAG = "SLTextureFetcher"
        const val MAX_UDP_TRANSFERS = 2

        @Volatile
        @JvmStatic
        var shared: SLTextureFetcher? = null
    }

    data class TextureFetchRequest(
        val textureId: UUID,
        val priority: Int = 0,
        val destFile: File? = null,
        val onComplete: ((Boolean) -> Unit)? = null
    ) : Comparable<TextureFetchRequest> {
        override fun compareTo(other: TextureFetchRequest): Int {
            // Higher priority integer value comes first
            return other.priority.compareTo(this.priority)
        }
    }

    @Volatile
    private var isPausedState: Boolean = false

    private val udpQueue = PriorityQueue<TextureFetchRequest>()
    private val udpTransfers = ConcurrentHashMap<UUID, TextureFetchRequest>()

    init {
        shared = this
    }

    override val isFetchingPaused: Boolean
        get() = isPausedState

    @Synchronized
    override fun pauseFetching() {
        if (isPausedState) return
        isPausedState = true
        Log.i(TAG, "Texture queue paused - stopping RunUDPQueue and canceling active transfers")

        // Retain pending request descriptors in udpQueue while clearing active transfers
        for (activeReq in udpTransfers.values) {
            if (!udpQueue.contains(activeReq)) {
                udpQueue.add(activeReq)
            }
        }
        udpTransfers.clear()

        // Release active permits in MeteredAssetGate
        MeteredAssetGate.shared.releaseActivePermits()
    }

    @Synchronized
    override fun resumeFetching() {
        if (!isPausedState) return
        isPausedState = false
        Log.i(TAG, "Texture queue resumed - starting queue polling in priority order")
        RunUDPQueue()
    }

    @Synchronized
    fun beginFetch(request: TextureFetchRequest) {
        val destFile = request.destFile
        if (destFile != null && destFile.exists()) {
            request.onComplete?.invoke(true)
            return
        }

        udpQueue.add(request)
        if (!isPausedState) {
            RunUDPQueue()
        }
    }

    @Synchronized
    fun cancelFetch(textureId: UUID) {
        udpQueue.removeIf { it.textureId == textureId }
        udpTransfers.remove(textureId)
        if (!isPausedState) {
            RunUDPQueue()
        }
    }

    @Synchronized
    fun clearQueue() {
        udpQueue.clear()
        udpTransfers.clear()
    }

    val pendingQueueSize: Int
        get() = udpQueue.size

    val activeTransferCount: Int
        get() = udpTransfers.size

    @Synchronized
    private fun RunUDPQueue() {
        if (isPausedState) return

        while (udpTransfers.size < MAX_UDP_TRANSFERS) {
            val nextReq = udpQueue.poll() ?: break
            udpTransfers[nextReq.textureId] = nextReq
            Log.d(TAG, "Dispatched texture transfer for ID: ${nextReq.textureId} (priority=${nextReq.priority})")
        }
    }

    @Synchronized
    fun notifyTransferCompleted(textureId: UUID, success: Boolean) {
        val completed = udpTransfers.remove(textureId)
        completed?.onComplete?.invoke(success)
        if (!isPausedState) {
            RunUDPQueue()
        }
    }
}
