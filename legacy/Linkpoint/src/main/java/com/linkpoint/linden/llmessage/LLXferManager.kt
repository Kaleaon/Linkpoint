package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

typealias XferCallback = (xferId: ULong, data: ByteArray?) -> Unit

class LLXferIncoming(
    val xferId: ULong,
    val fileName: String,
    val callback: XferCallback
) {
    val chunks: MutableMap<UInt, ByteArray> = sortedMapOf()
    var expectedSize: Int = 0
    var receivedBytes: Int = 0
    var nextPacket: UInt = 0u

    fun addPacket(packetNum: UInt, data: ByteArray) {
        chunks[packetNum] = data
        receivedBytes += data.size
        while (nextPacket in chunks) nextPacket++
    }

    fun isComplete(): Boolean = expectedSize > 0 && receivedBytes >= expectedSize

    fun assembleData(): ByteArray {
        val result = ByteArray(receivedBytes)
        var pos = 0
        for ((_, chunk) in chunks.entries.sortedBy { it.key }) {
            chunk.copyInto(result, pos); pos += chunk.size
        }
        return result
    }
}

class LLXferOutgoing(
    val xferId: ULong,
    val data: ByteArray,
    val fileName: String,
    val callback: XferCallback?
) {
    var sentBytes: Int = 0
    var nextPacket: UInt = 0u
    val chunkSize: Int = 1000

    fun nextChunk(): ByteArray? {
        if (sentBytes >= data.size) return null
        val end = minOf(sentBytes + chunkSize, data.size)
        return data.copyOfRange(sentBytes, end).also { sentBytes = end; nextPacket++ }
    }

    fun isComplete(): Boolean = sentBytes >= data.size
}

class LLXferManager {
    private val incomingXfers: MutableMap<ULong, LLXferIncoming> = ConcurrentHashMap()
    private val outgoingXfers: MutableMap<ULong, LLXferOutgoing> = ConcurrentHashMap()
    private val xferIdGen = AtomicLong(1L)

    fun requestFile(fileName: String, callback: XferCallback): ULong {
        val id = xferIdGen.getAndIncrement().toULong()
        incomingXfers[id] = LLXferIncoming(id, fileName, callback)
        return id
    }

    fun sendFile(data: ByteArray, fileName: String, callback: XferCallback? = null): ULong {
        val id = xferIdGen.getAndIncrement().toULong()
        outgoingXfers[id] = LLXferOutgoing(id, data, fileName, callback)
        return id
    }

    fun processIncomingPacket(xferId: ULong, packetNum: UInt, data: ByteArray, totalSize: Int) {
        val xfer = incomingXfers[xferId] ?: return
        if (xfer.expectedSize == 0) xfer.expectedSize = totalSize
        xfer.addPacket(packetNum, data)
        if (xfer.isComplete()) {
            xfer.callback(xferId, xfer.assembleData())
            incomingXfers.remove(xferId)
        }
    }

    fun getNextOutgoingChunk(xferId: ULong): ByteArray? {
        val xfer = outgoingXfers[xferId] ?: return null
        val chunk = xfer.nextChunk()
        if (xfer.isComplete()) {
            xfer.callback?.invoke(xferId, xfer.data)
            outgoingXfers.remove(xferId)
        }
        return chunk
    }

    fun abortXfer(xferId: ULong) {
        incomingXfers.remove(xferId)?.callback?.invoke(xferId, null)
        outgoingXfers.remove(xferId)?.callback?.invoke(xferId, null)
    }

    fun pendingIncoming(): Int = incomingXfers.size
    fun pendingOutgoing(): Int = outgoingXfers.size
}
