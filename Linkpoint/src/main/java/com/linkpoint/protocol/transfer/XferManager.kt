package com.linkpoint.protocol.transfer

import android.util.Log
import com.linkpoint.protocol.messages.UDPConnection
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Xfer Manager - Handles file transfers via the Xfer protocol.
 * 
 * Based on Lumiya's SLXferManager.java
 * 
 * Xfer is used for:
 * - Task inventory listings
 * - Notecard contents
 * - Script source code
 * - Animation files
 * - Large data that doesn't fit in single packets
 * 
 * Xfer flow:
 * 1. Server sends RequestXfer with xfer ID and filename
 * 2. Client sends ConfirmXferPacket for each received packet
 * 3. Server sends SendXferPacket messages with data chunks
 * 4. Transfer complete when final packet received (packet | 0x80000000)
 */
class XferManager(
    private val udpConnection: UDPConnection
) {
    companion object {
        private const val TAG = "XferManager"
        
        // Message IDs
        const val MSG_REQUEST_XFER = 0xFF009C
        const val MSG_CONFIRM_XFER_PACKET = 0x12
        const val MSG_SEND_XFER_PACKET = 0x13
        const val MSG_ABORT_XFER = 0xFF009D
        
        // Xfer type
        const val XFER_FILE = 1
        const val XFER_ASSET = 2
        const val XFER_ESTATE_FILE = 3
        
        // Packet size
        const val MAX_PACKET_SIZE = 1000
        
        // Final packet marker
        const val FINAL_PACKET_FLAG = 0x80000000.toInt()
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Active xfers by xfer ID
    private val activeXfers = ConcurrentHashMap<Long, Xfer>()
    
    // Xfer request handlers
    private val xferHandlers = ConcurrentHashMap<String, XferHandler>()
    
    // Xfer ID counter
    private val xferIdCounter = AtomicLong(System.currentTimeMillis())
    
    /**
     * Register a handler for xfer requests by filename pattern.
     * 
     * @param filenamePattern Pattern to match. Can be:
     *   - "*" to match all files
     *   - A prefix string (e.g., "task_inv" matches "task_inv_abc.txt")
     *   - A regex pattern starting with "^" (e.g., "^mute_.*\\.txt$")
     * @param handler Handler to call when matching xfer completes
     */
    fun registerHandler(filenamePattern: String, handler: XferHandler) {
        xferHandlers[filenamePattern] = handler
        Log.d(TAG, "Registered xfer handler for pattern: $filenamePattern")
    }
    
    /**
     * Handle RequestXfer message from server.
     * Server is requesting that we prepare to receive a file.
     */
    fun handleRequestXfer(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val xferId = buffer.long
            
            // Read filename (variable length string)
            val filenameLen = buffer.get().toInt() and 0xFF
            val filenameBytes = ByteArray(filenameLen)
            buffer.get(filenameBytes)
            val filename = String(filenameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Skip remaining fields (FilePath, DeleteOnCompletion, etc.)
            
            Log.d(TAG, "RequestXfer: id=$xferId filename=$filename")
            
            val xfer = Xfer(
                xferId = xferId,
                filename = filename
            )
            
            activeXfers[xferId] = xfer
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing RequestXfer", e)
        }
    }
    
    /**
     * Handle SendXferPacket message from server.
     */
    fun handleSendXferPacket(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val xferId = buffer.long
            val packetNum = buffer.int
            
            // Read data (variable length)
            val dataLen = buffer.short.toInt() and 0xFFFF
            val data = ByteArray(dataLen)
            buffer.get(data)
            
            val xfer = activeXfers[xferId]
            if (xfer == null) {
                Log.w(TAG, "Received SendXferPacket for unknown xfer: $xferId")
                return
            }
            
            // Check if final packet
            val isFinal = (packetNum and FINAL_PACKET_FLAG) != 0
            val actualPacketNum = packetNum and 0x7FFFFFFF
            
            Log.v(TAG, "SendXferPacket: id=$xferId packet=$actualPacketNum final=$isFinal size=${data.size}")
            
            // First packet may contain size info
            val actualData = if (actualPacketNum == 0 && data.size >= 4) {
                // First 4 bytes of first packet is total size (little-endian)
                xfer.expectedSize = ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).int
                data.copyOfRange(4, data.size)
            } else {
                data
            }
            
            xfer.addPacket(actualPacketNum, actualData)
            
            // Send confirmation
            scope.launch {
                sendConfirmXferPacket(xferId, packetNum)
            }
            
            // Check if complete
            if (isFinal) {
                completeXfer(xfer)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing SendXferPacket", e)
        }
    }
    
    /**
     * Handle AbortXfer message from server.
     */
    fun handleAbortXfer(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val xferId = buffer.long
            val result = buffer.int
            
            Log.w(TAG, "AbortXfer: id=$xferId result=$result")
            
            val xfer = activeXfers.remove(xferId)
            if (xfer != null) {
                notifyHandlers(xfer, XferResult.Error(result, "Xfer aborted by server"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AbortXfer", e)
        }
    }
    
    /**
     * Send ConfirmXferPacket to acknowledge received packet.
     */
    private suspend fun sendConfirmXferPacket(xferId: Long, packetNum: Int) {
        val payload = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        payload.putLong(xferId)
        payload.putInt(packetNum)
        
        try {
            udpConnection.sendPacket(MSG_CONFIRM_XFER_PACKET, payload.array(), reliable = false)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send ConfirmXferPacket", e)
        }
    }
    
    /**
     * Complete an xfer and notify handlers.
     */
    private fun completeXfer(xfer: Xfer) {
        activeXfers.remove(xfer.xferId)
        
        val data = xfer.assembleData()
        Log.i(TAG, "Xfer completed: ${xfer.filename} size=${data.size}")
        
        notifyHandlers(xfer, XferResult.Success(data))
    }
    
    /**
     * Notify registered handlers of xfer completion.
     * Patterns are matched as prefixes (filename.startsWith(pattern)) for more predictable behavior.
     * Use "*" to match all files.
     */
    private fun notifyHandlers(xfer: Xfer, result: XferResult) {
        // Find matching handler using prefix matching for more predictable behavior
        for ((pattern, handler) in xferHandlers) {
            val matches = when {
                pattern == "*" -> true
                pattern.startsWith("^") -> {
                    // Regex pattern if starts with ^
                    try {
                        Regex(pattern).containsMatchIn(xfer.filename)
                    } catch (e: Exception) {
                        false
                    }
                }
                else -> xfer.filename.startsWith(pattern, ignoreCase = true)
            }
            
            if (matches) {
                try {
                    handler.onXferComplete(xfer.filename, result)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in xfer handler for ${xfer.filename}", e)
                }
            }
        }
    }
    
    /**
     * Request a file from the server via Xfer.
     * Note: This is typically server-initiated, but can be client-requested for some file types.
     */
    suspend fun requestFile(
        filename: String,
        vFileId: UUID,
        vFileType: Int,
        deleteOnCompletion: Boolean = false
    ): Long {
        val xferId = xferIdCounter.incrementAndGet()
        
        val filenameBytes = filename.toByteArray(Charsets.UTF_8)
        val payload = ByteBuffer.allocate(24 + 1 + filenameBytes.size + 1 + 1)
            .order(ByteOrder.LITTLE_ENDIAN)
        
        // XferID block
        payload.putLong(xferId)
        
        // VFile block
        writeUUID(payload, vFileId)
        payload.putShort(vFileType.toShort())
        
        // FilePath block
        payload.put(0) // FilePath - unused
        
        // Filename
        payload.put(filenameBytes.size.toByte())
        payload.put(filenameBytes)
        
        // DeleteOnCompletion
        payload.put(if (deleteOnCompletion) 1 else 0)
        
        // UseBigPackets
        payload.put(0)
        
        val xfer = Xfer(xferId, filename)
        activeXfers[xferId] = xfer
        
        udpConnection.sendPacket(MSG_REQUEST_XFER, payload.array(), reliable = true)
        
        Log.d(TAG, "Requested xfer for file: $filename xferId=$xferId")
        
        return xferId
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    /**
     * Get diagnostics information.
     */
    fun getDiagnostics(): XferDiagnostics {
        return XferDiagnostics(
            activeXfers = activeXfers.size,
            registeredHandlers = xferHandlers.size
        )
    }
    
    /**
     * Shutdown the xfer manager.
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down XferManager")
        activeXfers.clear()
        xferHandlers.clear()
        scope.cancel()
    }
}

/**
 * Internal xfer state.
 */
internal class Xfer(
    val xferId: Long,
    val filename: String
) {
    var expectedSize: Int = 0
    
    private val packets = mutableMapOf<Int, ByteArray>()
    
    fun addPacket(packetNum: Int, data: ByteArray) {
        packets[packetNum] = data
    }
    
    fun assembleData(): ByteArray {
        if (packets.isEmpty()) return ByteArray(0)
        
        val totalSize = packets.values.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        
        // Assemble packets in order
        packets.keys.sorted().forEach { packetNum ->
            val data = packets[packetNum]!!
            System.arraycopy(data, 0, result, offset, data.size)
            offset += data.size
        }
        
        return result
    }
}

/**
 * Xfer result sealed class.
 */
sealed class XferResult {
    data class Success(val data: ByteArray) : XferResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return data.contentEquals(other.data)
        }
        
        override fun hashCode(): Int = data.contentHashCode()
    }
    
    data class Error(val code: Int, val message: String) : XferResult()
}

/**
 * Handler for xfer completions.
 */
fun interface XferHandler {
    fun onXferComplete(filename: String, result: XferResult)
}

/**
 * Xfer diagnostics.
 */
data class XferDiagnostics(
    val activeXfers: Int,
    val registeredHandlers: Int
)
