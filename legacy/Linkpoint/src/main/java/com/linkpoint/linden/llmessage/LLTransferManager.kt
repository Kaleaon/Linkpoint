package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID
import java.util.concurrent.ConcurrentHashMap

enum class TransferChannelType(val value: Int) {
    MISC(0), ASSET(1), INVENTORY(2), ESTATES(3), DIRECT(4), AVATAR(5)
}

typealias TransferCallback = (id: LLUUID, status: TransferStatus, data: ByteArray?) -> Unit

enum class TransferStatus { PENDING, IN_PROGRESS, COMPLETE, FAILED, ABORTED }

class LLTransferSource(
    val id: LLUUID,
    val channel: TransferChannelType
) {
    val chunks: MutableList<ByteArray> = mutableListOf()
    var status: TransferStatus = TransferStatus.PENDING
    var totalSize: Int = 0
    var bytesReceived: Int = 0
    var callback: TransferCallback? = null

    fun addChunk(data: ByteArray) {
        chunks.add(data)
        bytesReceived += data.size
    }

    fun isComplete(): Boolean = totalSize > 0 && bytesReceived >= totalSize

    fun assembleData(): ByteArray {
        val result = ByteArray(bytesReceived)
        var pos = 0
        for (chunk in chunks) { chunk.copyInto(result, pos); pos += chunk.size }
        return result
    }
}

class LLTransferManager {
    private val transfers: MutableMap<LLUUID, LLTransferSource> = ConcurrentHashMap()

    fun requestTransfer(
        sourceId: LLUUID,
        channel: TransferChannelType = TransferChannelType.ASSET,
        callback: TransferCallback? = null
    ): LLUUID {
        val transfer = LLTransferSource(sourceId, channel).also {
            it.callback = callback
            it.status = TransferStatus.PENDING
        }
        transfers[sourceId] = transfer
        return sourceId
    }

    fun processIncomingData(id: LLUUID, chunkData: ByteArray, totalSize: Int, lastChunk: Boolean) {
        val transfer = transfers[id] ?: return
        if (transfer.totalSize == 0) transfer.totalSize = totalSize
        transfer.addChunk(chunkData)
        transfer.status = TransferStatus.IN_PROGRESS
        if (lastChunk || transfer.isComplete()) {
            transfer.status = TransferStatus.COMPLETE
            transfer.callback?.invoke(id, TransferStatus.COMPLETE, transfer.assembleData())
            transfers.remove(id)
        }
    }

    fun abortTransfer(id: LLUUID) {
        transfers[id]?.let { it.status = TransferStatus.ABORTED; it.callback?.invoke(id, TransferStatus.ABORTED, null) }
        transfers.remove(id)
    }

    fun getTransfer(id: LLUUID): LLTransferSource? = transfers[id]

    fun getPendingCount(): Int = transfers.count { it.value.status == TransferStatus.PENDING }

    fun clear() = transfers.clear()
}
