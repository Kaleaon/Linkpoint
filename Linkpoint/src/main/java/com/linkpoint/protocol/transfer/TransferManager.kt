package com.linkpoint.protocol.transfer

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.PacketCodec
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Transfer Manager - Handles asset transfers via UDP protocol.
 * 
 * Based on the reference viewer's SLTransferManager.java
 * 
 * Transfer types:
 * - Asset transfers (textures, animations, sounds, etc.)
 * - Inventory item transfers
 * - Notecard/script content transfers
 * 
 * Transfer flow:
 * 1. Client sends TransferRequest
 * 2. Server sends TransferInfo with status and size
 * 3. Server sends multiple TransferPacket messages with data
 * 4. Transfer complete when all packets received or error status
 */
class TransferManager(
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID,
    private val sessionId: UUID
) {
    companion object {
        private const val TAG = "TransferManager"
        
        // Transfer channel types
        const val CHANNEL_ASSET = 2
        const val CHANNEL_MISC = 1
        
        // Transfer source types
        const val SOURCE_ASSET = 2
        const val SOURCE_SIM_INV_ITEM = 3
        const val SOURCE_SIM_ESTATE = 4
        
        // Transfer status codes
        const val STATUS_OK = 0
        const val STATUS_DONE = 1
        const val STATUS_SKIP = 2
        const val STATUS_ABORT = 3
        const val STATUS_ERROR = -1
        const val STATUS_UNKNOWN_SOURCE = -2
        const val STATUS_INSUFFICIENT_PERMISSIONS = -3
        const val STATUS_ASSET_NOT_FOUND = -4
        
        // Default priority for transfers
        const val DEFAULT_PRIORITY = 10000.0f
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Active transfers by transfer UUID
    private val activeTransfers = ConcurrentHashMap<UUID, Transfer>()
    
    // Mapping of asset keys to transfer UUIDs
    private val assetToTransfer = ConcurrentHashMap<AssetKey, UUID>()
    
    // Pending callbacks for transfer completion
    private val transferCallbacks = ConcurrentHashMap<UUID, TransferCallback>()
    
    // Transfer statistics
    private val _stats = MutableStateFlow(TransferStats())
    val stats: StateFlow<TransferStats> = _stats
    
    // Pending completions for suspend functions
    private val pendingCompletions = ConcurrentHashMap<UUID, CompletableDeferred<ByteArray?>>()
    
    /**
     * Fetch an asset synchronously (suspend function).
     * 
     * @param assetId The UUID of the asset
     * @param assetType The asset type code
     * @return The asset data or null if failed
     */
    suspend fun fetchAsset(assetId: UUID, assetType: Int): ByteArray? {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<ByteArray?>()
            val type = AssetType.fromCode(assetType) ?: AssetType.OBJECT
            
            val transferId = requestAssetTransfer(
                assetId = assetId,
                assetType = type,
                callback = { key, result ->
                    when (result) {
                        is TransferResult.Success -> deferred.complete(result.data)
                        is TransferResult.Error -> deferred.complete(null)
                    }
                }
            )
            
            pendingCompletions[transferId] = deferred
            
            try {
                // Wait with timeout
                withTimeout(30_000) {
                    deferred.await()
                }
            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "Asset transfer timed out: $assetId")
                null
            } finally {
                pendingCompletions.remove(transferId)
            }
        }
    }
    
    /**
     * Request an asset transfer.
     * 
     * @param assetId The UUID of the asset to transfer
     * @param assetType The type of asset (texture, animation, etc.)
     * @param callback Callback for transfer completion
     * @return The transfer UUID
     */
    fun requestAssetTransfer(
        assetId: UUID,
        assetType: AssetType,
        priority: Float = DEFAULT_PRIORITY,
        callback: TransferCallback? = null
    ): UUID {
        val assetKey = AssetKey(assetId, assetType)
        
        // Check if already transferring
        assetToTransfer[assetKey]?.let { existingId ->
            Log.d(TAG, "Asset $assetId already being transferred: $existingId")
            callback?.let { transferCallbacks[existingId] = it }
            return existingId
        }
        
        val transfer = Transfer(
            transferId = UUID.randomUUID(),
            assetKey = assetKey,
            channelType = CHANNEL_ASSET,
            sourceType = SOURCE_ASSET,
            priority = priority,
            agentId = agentId,
            sessionId = sessionId
        )
        
        activeTransfers[transfer.transferId] = transfer
        assetToTransfer[assetKey] = transfer.transferId
        callback?.let { transferCallbacks[transfer.transferId] = it }
        
        Log.d(TAG, "Starting asset transfer: ${assetKey.assetId} type=${assetKey.assetType}")
        
        scope.launch {
            sendTransferRequest(transfer)
        }
        
        updateStats { it.copy(activeTransfers = activeTransfers.size) }
        
        return transfer.transferId
    }
    
    /**
     * Request an inventory item asset transfer.
     */
    fun requestInventoryItemTransfer(
        itemId: UUID,
        assetId: UUID,
        ownerId: UUID,
        taskId: UUID?,
        assetType: AssetType,
        callback: TransferCallback? = null
    ): UUID {
        val assetKey = AssetKey(assetId, assetType)
        
        val transfer = Transfer(
            transferId = UUID.randomUUID(),
            assetKey = assetKey,
            channelType = CHANNEL_ASSET,
            sourceType = if (taskId != null) SOURCE_SIM_INV_ITEM else SOURCE_ASSET,
            priority = DEFAULT_PRIORITY,
            agentId = agentId,
            sessionId = sessionId,
            itemId = itemId,
            ownerId = ownerId,
            taskId = taskId
        )
        
        activeTransfers[transfer.transferId] = transfer
        assetToTransfer[assetKey] = transfer.transferId
        callback?.let { transferCallbacks[transfer.transferId] = it }
        
        Log.d(TAG, "Starting inventory item transfer: itemId=$itemId assetId=$assetId")
        
        scope.launch {
            sendTransferRequest(transfer)
        }
        
        return transfer.transferId
    }
    
    /**
     * Cancel an active transfer.
     */
    fun cancelTransfer(transferId: UUID) {
        val transfer = activeTransfers.remove(transferId) ?: return
        
        assetToTransfer.remove(transfer.assetKey)
        transferCallbacks.remove(transferId)
        
        Log.d(TAG, "Cancelling transfer: $transferId")
        
        scope.launch {
            sendTransferAbort(transfer)
        }
        
        updateStats { it.copy(activeTransfers = activeTransfers.size, cancelledTransfers = it.cancelledTransfers + 1) }
    }
    
    /**
     * Handle TransferInfo message from server.
     */
    fun handleTransferInfo(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val transferId = PacketCodec.fromBuffer(buffer).readUuid()
            val channelType = buffer.int
            val targetType = buffer.int
            val status = buffer.int
            val size = buffer.int
            
            val transfer = activeTransfers[transferId]
            if (transfer == null) {
                Log.w(TAG, "Received TransferInfo for unknown transfer: $transferId")
                return
            }
            
            Log.d(TAG, "TransferInfo: id=$transferId status=$status size=$size")
            
            transfer.status = status
            transfer.expectedSize = size
            
            if (status != STATUS_OK && status != STATUS_DONE) {
                // Transfer failed
                completeTransfer(transfer, TransferResult.Error(status, getStatusMessage(status)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing TransferInfo", e)
        }
    }
    
    /**
     * Handle TransferInfo message from parsed data.
     */
    fun handleTransferInfo(data: com.linkpoint.protocol.messages.AdditionalMessageParsers.TransferInfoData) {
        val transfer = activeTransfers[data.transferID]
        if (transfer == null) {
            Log.w(TAG, "Received TransferInfo for unknown transfer: ${data.transferID}")
            return
        }
        
        Log.d(TAG, "TransferInfo: id=${data.transferID} status=${data.status} size=${data.size}")
        
        transfer.status = data.status
        transfer.expectedSize = data.size
        
        if (data.status != STATUS_OK && data.status != STATUS_DONE) {
            // Transfer failed
            completeTransfer(transfer, TransferResult.Error(data.status, getStatusMessage(data.status)))
        }
    }
    
    /**
     * Handle TransferPacket message from server.
     */
    fun handleTransferPacket(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val transferId = PacketCodec.fromBuffer(buffer).readUuid()
            val channelType = buffer.int
            val packetNum = buffer.int
            val status = buffer.int
            
            // Read variable-length data
            val dataLen = buffer.short.toInt() and 0xFFFF
            val data = ByteArray(dataLen)
            buffer.get(data)
            
            val transfer = activeTransfers[transferId]
            if (transfer == null) {
                Log.w(TAG, "Received TransferPacket for unknown transfer: $transferId")
                return
            }
            
            Log.v(TAG, "TransferPacket: id=$transferId packet=$packetNum status=$status size=${data.size}")
            
            // Add data to transfer
            transfer.addPacket(packetNum, data)
            transfer.status = status
            
            // Check if transfer is complete
            if (status == STATUS_DONE || 
                (transfer.expectedSize > 0 && transfer.receivedSize >= transfer.expectedSize)) {
                completeTransfer(transfer, TransferResult.Success(transfer.assembleData()))
            } else if (status != STATUS_OK) {
                completeTransfer(transfer, TransferResult.Error(status, getStatusMessage(status)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing TransferPacket", e)
        }
    }
    
    /**
     * Handle TransferPacket message from parsed data.
     */
    fun handleTransferPacket(data: com.linkpoint.protocol.messages.AdditionalMessageParsers.TransferPacketData) {
        val transfer = activeTransfers[data.transferID]
        if (transfer == null) {
            Log.w(TAG, "Received TransferPacket for unknown transfer: ${data.transferID}")
            return
        }
        
        Log.v(TAG, "TransferPacket: id=${data.transferID} packet=${data.packet} status=${data.status} size=${data.data.size}")
        
        // Add data to transfer
        transfer.addPacket(data.packet, data.data)
        transfer.status = data.status
        
        // Check if transfer is complete
        if (data.status == STATUS_DONE || 
            (transfer.expectedSize > 0 && transfer.receivedSize >= transfer.expectedSize)) {
            completeTransfer(transfer, TransferResult.Success(transfer.assembleData()))
        } else if (data.status != STATUS_OK) {
            completeTransfer(transfer, TransferResult.Error(data.status, getStatusMessage(data.status)))
        }
    }
    
    /**
     * Send TransferRequest message.
     */
    private suspend fun sendTransferRequest(transfer: Transfer) {
        val params = buildTransferParams(transfer)
        
        // Build the message
        // Header: FFFF 00 99 (high frequency, ID 0x99)
        val payload = ByteBuffer.allocate(34 + params.size).order(ByteOrder.LITTLE_ENDIAN)
        
        // TransferInfo block
        PacketCodec.fromBuffer(payload).writeUuid(transfer.transferId)
        payload.putInt(transfer.channelType)
        payload.putInt(transfer.sourceType)
        payload.putFloat(transfer.priority)
        
        // Variable-length params
        payload.putShort(params.size.toShort())
        payload.put(params)
        
        try {
            udpConnection.sendPacket(MessageIds.TRANSFER_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Sent TransferRequest for ${transfer.assetKey.assetId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send TransferRequest", e)
            completeTransfer(transfer, TransferResult.Error(STATUS_ERROR, "Failed to send request: ${e.message}"))
        }
    }
    
    /**
     * Send TransferAbort message.
     */
    private suspend fun sendTransferAbort(transfer: Transfer) {
        val payload = ByteBuffer.allocate(20).order(ByteOrder.LITTLE_ENDIAN)
        
        PacketCodec.fromBuffer(payload).writeUuid(transfer.transferId)
        payload.putInt(transfer.channelType)
        
        try {
            udpConnection.sendPacket(MessageIds.TRANSFER_ABORT, payload.array(), reliable = true)
            Log.d(TAG, "Sent TransferAbort for ${transfer.transferId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send TransferAbort", e)
        }
    }
    
    /**
     * Build transfer parameters based on source type.
     */
    private fun buildTransferParams(transfer: Transfer): ByteArray {
        return when (transfer.sourceType) {
            SOURCE_ASSET -> {
                // Asset transfer params: AgentID (16) + SessionID (16) + OwnerID (16) + AssetID (16) + AssetType (4)
                val params = ByteBuffer.allocate(68).order(ByteOrder.LITTLE_ENDIAN)
                val codec = PacketCodec.fromBuffer(params)
                codec.writeUuid(transfer.agentId)
                codec.writeUuid(transfer.sessionId)
                codec.writeUuid(transfer.ownerId ?: transfer.agentId)
                codec.writeUuid(transfer.assetKey.assetId)
                params.putInt(transfer.assetKey.assetType.code)
                params.array()
            }
            SOURCE_SIM_INV_ITEM -> {
                // Sim inventory item params: AgentID + SessionID + OwnerID + TaskID + ItemID + AssetID + AssetType
                val params = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN)
                val codec = PacketCodec.fromBuffer(params)
                codec.writeUuid(transfer.agentId)
                codec.writeUuid(transfer.sessionId)
                codec.writeUuid(transfer.ownerId ?: transfer.agentId)
                codec.writeUuid(transfer.taskId ?: UUID(0, 0))
                codec.writeUuid(transfer.itemId ?: UUID(0, 0))
                codec.writeUuid(transfer.assetKey.assetId)
                params.putInt(transfer.assetKey.assetType.code)
                params.array()
            }
            else -> ByteArray(0)
        }
    }
    
    /**
     * Complete a transfer and notify callback.
     */
    private fun completeTransfer(transfer: Transfer, result: TransferResult) {
        activeTransfers.remove(transfer.transferId)
        assetToTransfer.remove(transfer.assetKey)
        
        val callback = transferCallbacks.remove(transfer.transferId)
        
        when (result) {
            is TransferResult.Success -> {
                Log.i(TAG, "Transfer completed: ${transfer.assetKey.assetId} size=${result.data.size}")
                updateStats { it.copy(
                    activeTransfers = activeTransfers.size,
                    completedTransfers = it.completedTransfers + 1,
                    bytesReceived = it.bytesReceived + result.data.size
                )}
            }
            is TransferResult.Error -> {
                Log.w(TAG, "Transfer failed: ${transfer.assetKey.assetId} status=${result.status} msg=${result.message}")
                updateStats { it.copy(
                    activeTransfers = activeTransfers.size,
                    failedTransfers = it.failedTransfers + 1
                )}
            }
        }
        
        callback?.onComplete(transfer.assetKey, result)
    }
    
    private fun getStatusMessage(status: Int): String {
        return when (status) {
            STATUS_OK -> "OK"
            STATUS_DONE -> "Done"
            STATUS_SKIP -> "Skipped"
            STATUS_ABORT -> "Aborted"
            STATUS_ERROR -> "Error"
            STATUS_UNKNOWN_SOURCE -> "Unknown source"
            STATUS_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            STATUS_ASSET_NOT_FOUND -> "Asset not found"
            else -> "Unknown status: $status"
        }
    }
    
    private inline fun updateStats(update: (TransferStats) -> TransferStats) {
        _stats.value = update(_stats.value)
    }
    
    /**
     * Get diagnostics information.
     */
    fun getDiagnostics(): TransferDiagnostics {
        return TransferDiagnostics(
            activeTransfers = activeTransfers.size,
            pendingCallbacks = transferCallbacks.size,
            stats = _stats.value
        )
    }
    
    /**
     * Shutdown the transfer manager.
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down TransferManager")
        
        // Cancel all active transfers
        activeTransfers.keys.toList().forEach { cancelTransfer(it) }
        
        scope.cancel()
    }
}

/**
 * Asset key for transfer identification.
 */
data class AssetKey(
    val assetId: UUID,
    val assetType: AssetType
)

/**
 * Asset types that can be transferred.
 */
enum class AssetType(val code: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLING_CARD(2),
    LANDMARK(3),
    SCRIPT(4),          // Legacy LSL text
    CLOTHING(5),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),
    ROOT_CATEGORY(9),
    LSL_TEXT(10),
    LSL_BYTECODE(11),
    TEXTURE_TGA(12),
    BODYPART(13),
    TRASH(14),
    SNAPSHOT_CATEGORY(15),
    LOST_AND_FOUND(16),
    SOUND_WAV(17),
    IMAGE_TGA(18),
    IMAGE_JPEG(19),
    ANIMATION(20),
    GESTURE(21),
    SIMSTATE(22),
    LINK(24),
    LINK_FOLDER(25),
    MARKETPLACE_FOLDER(26),
    WIDGET(40),
    PERSON(45),
    MESH(49),
    SETTINGS(56),
    MATERIAL(57);
    
    companion object {
        fun fromCode(code: Int): AssetType? = values().find { it.code == code }
    }
}

/**
 * Internal transfer state.
 */
internal class Transfer(
    val transferId: UUID,
    val assetKey: AssetKey,
    val channelType: Int,
    val sourceType: Int,
    val priority: Float,
    val agentId: UUID,
    val sessionId: UUID,
    val itemId: UUID? = null,
    val ownerId: UUID? = null,
    val taskId: UUID? = null
) {
    var status: Int = TransferManager.STATUS_OK
    var expectedSize: Int = 0
    var receivedSize: Int = 0
    
    private val packets = mutableMapOf<Int, ByteArray>()
    
    fun addPacket(packetNum: Int, data: ByteArray) {
        packets[packetNum] = data
        receivedSize += data.size
    }
    
    fun assembleData(): ByteArray {
        if (packets.isEmpty()) return ByteArray(0)
        
        val totalSize = packets.values.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        
        // Assemble packets in order
        packets.keys.sorted().forEach { packetNum ->
            val data = packets[packetNum] 
                ?: throw IllegalStateException("Packet $packetNum not found in transfer data")
            System.arraycopy(data, 0, result, offset, data.size)
            offset += data.size
        }
        
        return result
    }
}

/**
 * Transfer result sealed class.
 */
sealed class TransferResult {
    data class Success(val data: ByteArray) : TransferResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return data.contentEquals(other.data)
        }
        
        override fun hashCode(): Int = data.contentHashCode()
    }
    
    data class Error(val status: Int, val message: String) : TransferResult()
}

/**
 * Callback for transfer completion.
 */
fun interface TransferCallback {
    fun onComplete(assetKey: AssetKey, result: TransferResult)
}

/**
 * Transfer statistics.
 */
data class TransferStats(
    val activeTransfers: Int = 0,
    val completedTransfers: Int = 0,
    val failedTransfers: Int = 0,
    val cancelledTransfers: Int = 0,
    val bytesReceived: Long = 0
)

/**
 * Transfer diagnostics.
 */
data class TransferDiagnostics(
    val activeTransfers: Int,
    val pendingCallbacks: Int,
    val stats: TransferStats
)
