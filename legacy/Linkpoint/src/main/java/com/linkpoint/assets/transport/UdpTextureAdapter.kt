package com.linkpoint.assets.transport

import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.protocol.types.getUUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Transport adapter for downloading textures over UDP via RequestImage packets.
 */
class UdpTextureAdapter(
    private val packetSender: ((assetId: UUID, discardLevel: Int) -> Unit)? = null,
    private val timeoutMs: Long = 15000L
) : AssetFetcherAdapter {

    override val name: String = "UdpTextureAdapter"
    override val priority: Int = 30

    companion object {
        private const val TAG = "UdpTextureAdapter"

        private fun logD(msg: String) {
            try { Log.d(TAG, msg) } catch (_: Throwable) {}
        }

        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }

        private fun logE(msg: String, e: Throwable? = null) {
            try { Log.e(TAG, msg, e) } catch (_: Throwable) {}
        }
    }

    private class TransferBuffer(val expectedSize: Int, val totalPackets: Int) {
        val stream = ByteArrayOutputStream()
        var receivedPackets = 0
    }

    private val activeTransfers = ConcurrentHashMap<UUID, TransferBuffer>()
    private val pendingCompletions = ConcurrentHashMap<UUID, CompletableDeferred<ByteArray?>>()

    override fun canFetch(assetType: AssetType): Boolean {
        return assetType == AssetType.TEXTURE || assetType == AssetType.SNAPSHOT
    }

    override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? = withContext(Dispatchers.IO) {
        if (!canFetch(assetType)) return@withContext null

        val deferred = CompletableDeferred<ByteArray?>()
        pendingCompletions[assetId] = deferred

        try {
            logD("Requesting UDP texture fetch for $assetId")
            if (packetSender != null) {
                packetSender.invoke(assetId, 0)
            } else {
                logW("No UDP packetSender registered for UdpTextureAdapter; waiting for incoming packets")
            }

            val result = withTimeoutOrNull(timeoutMs) {
                deferred.await()
            }

            if (result == null) {
                logW("UDP texture fetch timed out for $assetId after ${timeoutMs}ms")
            }
            return@withContext result
        } catch (e: Throwable) {
            logE("Error during UDP texture fetch for $assetId", e)
            return@withContext null
        } finally {
            pendingCompletions.remove(assetId)
            activeTransfers.remove(assetId)
        }
    }

    /**
     * Called when ImageData (first UDP packet of texture) is received.
     */
    fun onImageData(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            val assetId = buffer.getUUID()
            val codec = buffer.get().toInt() and 0xFF
            val size = buffer.int
            val totalPackets = buffer.short.toInt() and 0xFFFF

            logD("ImageData received for $assetId: codec=$codec, size=$size, totalPackets=$totalPackets")

            val transfer = TransferBuffer(size, totalPackets)
            if (buffer.remaining() > 0) {
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                transfer.stream.write(data)
                transfer.receivedPackets = 1
            }

            activeTransfers[assetId] = transfer

            if (totalPackets <= 1 || (size > 0 && transfer.stream.size() >= size)) {
                completeTransfer(assetId)
            }
        } catch (e: Throwable) {
            logE("Error handling ImageData in UdpTextureAdapter", e)
        }
    }

    /**
     * Called when ImagePacket (subsequent UDP packets of texture) is received.
     */
    fun onImagePacket(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            val assetId = buffer.getUUID()
            val packetNum = buffer.short.toInt() and 0xFFFF

            val transfer = activeTransfers[assetId] ?: return

            if (buffer.remaining() > 0) {
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                synchronized(transfer.stream) {
                    transfer.stream.write(data)
                }
                transfer.receivedPackets++
            }

            logD("ImagePacket #$packetNum received for $assetId (${transfer.stream.size()}/${transfer.expectedSize} bytes)")

            if (transfer.receivedPackets >= transfer.totalPackets ||
                (transfer.expectedSize > 0 && transfer.stream.size() >= transfer.expectedSize)) {
                completeTransfer(assetId)
            }
        } catch (e: Throwable) {
            logE("Error handling ImagePacket in UdpTextureAdapter", e)
        }
    }

    /**
     * Completes an active UDP texture transfer manually or when all packets arrive.
     */
    fun completeTransfer(assetId: UUID, data: ByteArray? = null) {
        val bytes = data ?: activeTransfers.remove(assetId)?.stream?.toByteArray()
        val deferred = pendingCompletions.remove(assetId)
        if (deferred != null) {
            deferred.complete(bytes)
        } else {
            logD("Completed UDP texture transfer for $assetId with no pending deferred awaiter")
        }
    }

    /**
     * Cancels an active UDP texture transfer (e.g., ImageNotInDatabase).
     */
    fun cancelTransfer(assetId: UUID) {
        activeTransfers.remove(assetId)
        pendingCompletions.remove(assetId)?.complete(null)
    }
}
