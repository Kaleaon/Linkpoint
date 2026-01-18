package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.resume

/**
 * UDP Connection handler for Second Life protocol.
 *
 * Packet headers (flags/sequence/message number) are big-endian network order, while message bodies
 * are little-endian per SL message templates; UUIDs remain raw big-endian bytes.
 */
class UDPConnection {
    
    private var simIP: String = ""
    private var simPort: Int = 0
    private var circuitCode: Int = 0
    private var sessionId: UUID = UUID(0, 0)
    private var agentId: UUID = UUID(0, 0)
    
    constructor()
    
    constructor(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    fun configure(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    /**
     * Set session information for circuit establishment
     */
    fun setSessionInfo(sessionId: UUID, agentId: UUID) {
        this.sessionId = sessionId
        this.agentId = agentId
    }
    
    /**
     * Get the agent ID for this connection
     */
    fun getAgentId(): UUID = agentId
    
    /**
     * Get the session ID for this connection
     */
    fun getSessionId(): UUID = sessionId
    
    /**
     * Get the circuit code for this connection
     */
    fun getCircuitCode(): Int = circuitCode
    
    // Use DatagramChannel (NIO) instead of DatagramSocket for better mobile network compatibility
    // This matches Lumiya's approach which works reliably on cellular networks
    private var datagramChannel: DatagramChannel? = null
    private var selector: Selector? = null
    private var selectionKey: SelectionKey? = null
    private var receiveJob: Job? = null
    private var agentUpdateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // ==================== CONFIGURABLE PARALLEL PROCESSING ====================
    // These settings control CPU and RAM usage for packet processing.
    // Users can adjust these via UI sliders for performance tuning.
    
    companion object {
        private const val TAG = "UDPConnection"
        private const val BUFFER_SIZE = 65535
        private const val ACK_TIMEOUT_MS = 1000L
        private const val MAX_RETRIES = 5
        private const val PACKET_HEADER_SIZE = 6
        private val HEADER_BYTE_ORDER = ByteOrder.BIG_ENDIAN
        private val BODY_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN
        
        // Message name prefix for critical messages (used in logging)
        private const val CRITICAL_MESSAGE_PREFIX = "⭐ "
        
        // Packet flags
        const val FLAG_ZEROCODED = 0x80
        const val FLAG_RELIABLE = 0x40
        const val FLAG_RESENT = 0x20
        const val FLAG_ACK = 0x10
        
        // Agent update interval (matches official viewers - 10 updates/sec)
        private const val AGENT_UPDATE_INTERVAL_MS = 100L
        
        // ACK batching configuration
        private const val MAX_ACKS_PER_PACKET = 10
        private const val ACK_FLUSH_INTERVAL_MS = 100L
        
        // ==================== MESSAGE FREQUENCY CONSTANTS ====================
        // Per Lumiya's DecodeMessageID(), message IDs are decoded as follows:
        // - High frequency: signed byte value (-128 to 254, excluding -1/0xFF)
        // - Medium frequency: (byte | 65280) = 65280-65534
        // - Low frequency: (short | -65536) = negative values < -128
        
        // Invalid message ID - used as error indicator
        const val INVALID_MESSAGE_ID = Int.MIN_VALUE
        
        // Medium frequency range (byte | 65280)
        private const val MEDIUM_FREQUENCY_BASE = 65280
        private const val MEDIUM_FREQUENCY_MIN = 65280
        private const val MEDIUM_FREQUENCY_MAX = 65534
        
        // Low frequency base (-65536 in Lumiya)
        private const val LOW_FREQUENCY_BASE = -65536
        
        // Threshold for distinguishing high vs low frequency
        // Values < -128 are low frequency (result of short | -65536)
        private const val HIGH_FREQUENCY_THRESHOLD = -128
        
        // ==================== STANDARD MEMORY SIZES ====================
        // Standard computer memory measurements for user-friendly configuration.
        // These align with how users typically think about memory allocation.
        
        // Memory size constants in bytes
        const val KB = 1024L
        const val MB = 1024L * KB
        const val GB = 1024L * MB
        
        // Average packet size estimate for calculating packet counts from memory
        private const val AVG_PACKET_SIZE_BYTES = 1024L  // ~1KB average packet
        
        /**
         * Standard memory sizes for buffer allocation.
         * Users select memory amounts, which are converted to packet counts internally.
         */
        enum class MemorySize(val bytes: Long, val displayName: String) {
            MB_64(64 * MB, "64 MB"),
            MB_128(128 * MB, "128 MB"),
            MB_256(256 * MB, "256 MB"),
            MB_512(512 * MB, "512 MB"),
            GB_1(1 * GB, "1 GB"),
            GB_2(2 * GB, "2 GB"),
            GB_4(4 * GB, "4 GB"),
            GB_8(8 * GB, "8 GB");
            
            /** Convert memory size to approximate packet count */
            fun toPacketCount(): Int = (bytes / AVG_PACKET_SIZE_BYTES).toInt().coerceIn(64, Int.MAX_VALUE)
            
            companion object {
                /** Get all available memory sizes for UI display */
                fun getDisplayNames(): List<String> = values().map { it.displayName }
                
                /** Find memory size by display name */
                fun fromDisplayName(name: String): MemorySize? = values().find { it.displayName == name }
                
                /** Snap a byte value to the nearest standard memory size */
                fun snapToNearest(bytes: Long): MemorySize {
                    var closest = values().first()
                    var minDiff = kotlin.math.abs(bytes - closest.bytes)
                    
                    for (size in values()) {
                        val diff = kotlin.math.abs(bytes - size.bytes)
                        if (diff < minDiff) {
                            minDiff = diff
                            closest = size
                        }
                    }
                    return closest
                }
            }
        }
        
        /**
         * Standard batch sizes for parallel processing.
         * Smaller batches = more responsive, larger batches = more throughput.
         */
        enum class BatchSize(val count: Int, val displayName: String) {
            SMALL(16, "Small (16)"),
            MEDIUM(64, "Medium (64)"),
            LARGE(256, "Large (256)"),
            EXTRA_LARGE(1024, "Extra Large (1024)");
            
            companion object {
                fun getDisplayNames(): List<String> = values().map { it.displayName }
                fun fromDisplayName(name: String): BatchSize? = values().find { it.displayName == name }
            }
        }
    }
    
    /**
     * Processing configuration that can be adjusted at runtime.
     * Controls the balance between processing speed and resource usage.
     * Uses standard computer memory measurements (MB, GB) for user-friendly configuration.
     */
    data class ProcessingConfig(
        /** Number of threads for parallel packet processing (1-16, default: CPU cores) */
        val threadCount: Int = Runtime.getRuntime().availableProcessors().coerceIn(1, 16),
        /** Maximum memory for packet buffer - uses standard sizes (64 MB - 8 GB) */
        val bufferMemory: MemorySize = MemorySize.MB_256,
        /** Batch size for parallel processing */
        val batchSize: BatchSize = BatchSize.MEDIUM,
        /** Processing priority level affecting resource allocation */
        val priority: ProcessingPriority = ProcessingPriority.BALANCED
    ) {
        /** Get buffer size as packet count based on memory allocation */
        fun getBufferPacketCount(): Int = bufferMemory.toPacketCount()
        
        /** Get batch size count */
        fun getBatchCount(): Int = batchSize.count
        
        /** Get human-readable description of current config */
        fun getDescription(): String = 
            "Threads: $threadCount, Buffer: ${bufferMemory.displayName}, Batch: ${batchSize.displayName}, Priority: $priority"
    }
    
    /**
     * Processing priority levels for resource allocation.
     * Each level has recommended memory and batch settings.
     */
    enum class ProcessingPriority(
        val threadMultiplier: Float,
        val recommendedMemory: MemorySize,
        val recommendedBatch: BatchSize,
        val displayName: String
    ) {
        /** Minimal resource usage - conserve battery and memory */
        LOW(0.5f, MemorySize.MB_64, BatchSize.SMALL, "Low (Battery Saver)"),
        /** Balanced resource usage - good for most situations */
        BALANCED(1.0f, MemorySize.MB_256, BatchSize.MEDIUM, "Balanced"),
        /** High performance - faster loading, more memory usage */
        HIGH(1.5f, MemorySize.MB_512, BatchSize.LARGE, "High Performance"),
        /** Maximum performance - use all available resources */
        MAXIMUM(2.0f, MemorySize.GB_1, BatchSize.EXTRA_LARGE, "Maximum");
        
        companion object {
            fun getDisplayNames(): List<String> = values().map { it.displayName }
            fun fromDisplayName(name: String): ProcessingPriority? = values().find { it.displayName == name }
        }
    }
    
    // Current processing configuration - can be updated at runtime
    @Volatile
    private var processingConfig = ProcessingConfig()
    
    // Dedicated thread pool for parallel packet processing
    // Dynamically sized based on configuration
    // Note: Initialized with default thread count to avoid accessing processingConfig before it's ready
    private var packetProcessorExecutor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors().coerceIn(1, 16)
    )
    private var packetProcessorDispatcher = packetProcessorExecutor.asCoroutineDispatcher()
    
    /**
     * Calculate effective thread count based on config and priority
     */
    private fun getEffectiveThreadCount(): Int {
        val base = processingConfig.threadCount
        val multiplier = processingConfig.priority.threadMultiplier
        return (base * multiplier).toInt().coerceIn(1, 16)
    }
    
    /**
     * Calculate effective buffer size (packet count) based on memory config.
     * Converts user-friendly memory size to internal packet count.
     */
    private fun getEffectiveBufferSize(): Int {
        return processingConfig.bufferMemory.toPacketCount()
    }
    
    /**
     * Calculate effective batch size based on config.
     */
    private fun getEffectiveBatchSize(): Int {
        return processingConfig.batchSize.count
    }
    
    /**
     * Get effective memory allocation in bytes for diagnostics.
     */
    fun getEffectiveMemoryBytes(): Long {
        return processingConfig.bufferMemory.bytes
    }
    
    /**
     * Update processing configuration at runtime.
     * This allows users to adjust CPU/RAM usage via UI sliders.
     * Uses standard computer memory measurements for intuitive configuration.
     * 
     * @param config New processing configuration
     */
    fun updateProcessingConfig(config: ProcessingConfig) {
        val oldConfig = processingConfig
        processingConfig = config
        
        val effectiveBuffer = getEffectiveBufferSize()
        val effectiveBatch = getEffectiveBatchSize()
        val memoryBytes = config.bufferMemory.bytes
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ PROCESSING CONFIG UPDATED")
        Log.i(TAG, "║ Threads: ${getEffectiveThreadCount()} (was ${(oldConfig.threadCount * oldConfig.priority.threadMultiplier).toInt()})")
        Log.i(TAG, "║ Buffer Memory: ${config.bufferMemory.displayName} (~$effectiveBuffer packets)")
        Log.i(TAG, "║ Batch Size: ${config.batchSize.displayName}")
        Log.i(TAG, "║ Priority: ${config.priority.displayName}")
        Log.i(TAG, "║ Available memory sizes: ${MemorySize.getDisplayNames()}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        // Recreate thread pool if thread count changed
        val newThreadCount = getEffectiveThreadCount()
        val oldThreadCount = (oldConfig.threadCount * oldConfig.priority.threadMultiplier).toInt().coerceIn(1, 16)
        if (newThreadCount != oldThreadCount) {
            rebuildThreadPool(newThreadCount)
        }
    }
    
    /**
     * Rebuild the thread pool with new thread count.
     * Gracefully shuts down old pool and creates new one.
     */
    private fun rebuildThreadPool(threadCount: Int) {
        Log.i(TAG, "   Rebuilding thread pool with $threadCount threads...")
        val oldDispatcher = packetProcessorDispatcher
        val oldExecutor = packetProcessorExecutor
        
        // Create new pool first
        packetProcessorExecutor = Executors.newFixedThreadPool(threadCount)
        packetProcessorDispatcher = packetProcessorExecutor.asCoroutineDispatcher()
        
        // Shutdown old pool gracefully with timeout
        oldDispatcher.close()
        oldExecutor.shutdown()
        try {
            // Wait up to 5 seconds for tasks to complete
            if (!oldExecutor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                Log.w(TAG, "   Thread pool did not terminate gracefully, forcing shutdown")
                oldExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            Log.w(TAG, "   Thread pool shutdown interrupted", e)
            oldExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
        
        Log.i(TAG, "   ✓ Thread pool rebuilt")
    }
    
    /**
     * Get current processing configuration for UI display
     */
    fun getProcessingConfig(): ProcessingConfig = processingConfig
    
    private val sequenceNumber = AtomicInteger(0)
    private val pendingAcks = ConcurrentHashMap<Int, PendingPacket>()
    private val messageHandlers = ConcurrentHashMap<Int, MessageHandler>()
    
    // ACK callback mechanism - notified when specific packets are acknowledged
    // Key is sequence number, value is callback to invoke when ACK received
    private val ackCallbacks = ConcurrentHashMap<Int, () -> Unit>()
    
    // ==================== PACKET BUFFER ====================
    // Buffer to store incoming packets that arrive before handlers are registered.
    // This prevents packet loss during the race condition between UDP connection
    // establishment and message handler registration.
    private val packetBuffer = ConcurrentLinkedQueue<BufferedPacket>()
    private val handlersReady = AtomicBoolean(false)
    
    /**
     * Data class to hold buffered packet information
     */
    private data class BufferedPacket(
        val data: ByteArray,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    // ACK batching for efficiency (matches official viewer behavior)
    private val pendingAckIds = mutableListOf<Int>()
    @Volatile private var lastAckFlush = System.currentTimeMillis()
    
    // Current agent state for AgentUpdate messages
    @Volatile private var currentPosition = floatArrayOf(128f, 128f, 25f)
    @Volatile private var currentRotation = floatArrayOf(0f, 0f, 0f, 1f) // quaternion
    @Volatile private var currentLookAt = floatArrayOf(1f, 0f, 0f)
    @Volatile private var agentState: Int = 0
    @Volatile private var controlFlags: Int = 0
    
    private var isConnected = false
    
    // ==================== MESSAGE STATISTICS TRACKING ====================
    // These track detailed message statistics for diagnostics
    private val totalPacketsReceived = AtomicInteger(0)
    private val totalBytesReceived = AtomicLong(0)
    private val packetsResentCount = AtomicInteger(0)
    private val messageTypeCounts = ConcurrentHashMap<String, AtomicInteger>()
    private val lastMessageTimes = ConcurrentHashMap<String, Long>()
    
    // ==================== PACKET HISTORY FOR DETAILED DEBUGGING ====================
    // Track recent packet activity for debug reports - helps diagnose UDP issues
    private val recentPacketHistory = ConcurrentLinkedQueue<PacketHistoryEntry>()
    private val maxPacketHistorySize = 50  // Keep last 50 packet events
    @Volatile private var lastSendAttemptTime: Long = 0
    @Volatile private var lastReceiveTime: Long = 0
    @Volatile private var connectionAttemptTime: Long = 0
    @Volatile private var lastConnectionError: String? = null
    @Volatile private var localBindAddress: String? = null
    @Volatile private var localBindPort: Int = 0
    
    /**
     * Packet history entry for debugging UDP issues.
     * Records detailed information about each packet event.
     */
    data class PacketHistoryEntry(
        val timestamp: Long,
        val type: PacketEventType,
        val messageId: Int,
        val messageName: String,
        val size: Int,
        val sequenceNumber: Int,
        val hexPreview: String,
        val success: Boolean,
        val errorMessage: String? = null
    ) {
        enum class PacketEventType {
            SEND_SUCCESS,
            SEND_FAILED,
            RECEIVE,
            RESEND,
            ACK_RECEIVED,
            ACK_TIMEOUT
        }
    }
    
    /**
     * Record a packet event in the history for debugging.
     */
    private fun recordPacketEvent(
        type: PacketHistoryEntry.PacketEventType,
        messageId: Int,
        data: ByteArray,
        sequenceNumber: Int,
        success: Boolean = true,
        errorMessage: String? = null
    ) {
        val entry = PacketHistoryEntry(
            timestamp = System.currentTimeMillis(),
            type = type,
            messageId = messageId,
            messageName = getMessageName(messageId),
            size = data.size,
            sequenceNumber = sequenceNumber,
            hexPreview = data.take(24).joinToString(" ") { "%02X".format(it) },
            success = success,
            errorMessage = errorMessage
        )
        
        recentPacketHistory.offer(entry)
        
        // Keep bounded size
        while (recentPacketHistory.size > maxPacketHistorySize) {
            recentPacketHistory.poll()
        }
    }
    
    /**
     * Extract the sequence number from raw packet data.
     * Packet header format: flags (1 byte), sequence (4 bytes big-endian), extra (1 byte)
     */
    private fun extractSequenceNumber(data: ByteArray): Int {
        if (data.size < 5) return -1
        val buffer = ByteBuffer.wrap(data, 1, 4).order(HEADER_BYTE_ORDER)
        return buffer.int
    }
    
    /**
     * Extract message ID from raw packet data using Lumiya-style decoding.
     * Used for packet history and diagnostics.
     */
    private fun extractMessageId(data: ByteArray): Int {
        if (data.size < PACKET_HEADER_SIZE + 1) return INVALID_MESSAGE_ID
        
        // Skip header (6 bytes): flags (1) + sequence (4) + extra (1)
        val result = decodeMessageIdLumiyaStyle(data, PACKET_HEADER_SIZE)
        return result?.first ?: INVALID_MESSAGE_ID
    }
    
    /**
     * Get the recent packet history for diagnostic reports.
     */
    fun getPacketHistory(): List<PacketHistoryEntry> {
        return recentPacketHistory.toList()
    }
    
    /**
     * Get detailed socket/channel information for diagnostics.
     */
    fun getSocketDetails(): SocketDetails {
        return SocketDetails(
            localBindAddress = localBindAddress,
            localBindPort = localBindPort,
            remoteAddress = simIP,
            remotePort = simPort,
            isConnected = datagramChannel?.isConnected == true,
            isOpen = datagramChannel?.isOpen == true,
            connectionAttemptTime = connectionAttemptTime,
            lastSendAttemptTime = lastSendAttemptTime,
            lastReceiveTime = lastReceiveTime,
            lastConnectionError = lastConnectionError
        )
    }
    
    /**
     * Socket details for diagnostics.
     */
    data class SocketDetails(
        val localBindAddress: String?,
        val localBindPort: Int,
        val remoteAddress: String,
        val remotePort: Int,
        val isConnected: Boolean,
        val isOpen: Boolean,
        val connectionAttemptTime: Long,
        val lastSendAttemptTime: Long,
        val lastReceiveTime: Long,
        val lastConnectionError: String?
    )
    
    /**
     * Connect to the simulator.
     * 
     * Follows Lumiya's connection sequence:
     * 1. Send UseCircuitCode (reliable)
     * 2. Wait for ACK before proceeding (critical!)
     * 3. Send CompleteAgentMovement
     * 4. Simulator responds with AgentMovementComplete and RegionHandshake
     * 
     * This sequence is based on Lumiya's SLAgentCircuit.SendUseCode() which uses
     * an event listener to wait for the ACK before sending CompleteAgentMovement.
     * 
     * CRITICAL: Uses DatagramChannel with connect() for proper mobile/cellular network
     * support, exactly like Lumiya does. This "connects" the UDP socket to only 
     * send/receive from the specific simulator address.
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Record connection attempt time for diagnostics
            connectionAttemptTime = System.currentTimeMillis()
            lastConnectionError = null
            
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ INITIATING UDP CONNECTION (Lumiya-style with DatagramChannel)    ║")
            Log.i(TAG, "║ Target: $simIP:$simPort                                          ║")
            Log.i(TAG, "║ Circuit Code: $circuitCode                                       ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            
            // Set IPv4 preference like Lumiya does - important for mobile networks
            System.setProperty("java.net.preferIPv4Stack", "true")
            System.setProperty("java.net.preferIPv6Addresses", "false")
            Log.i(TAG, "✓ IPv4 stack preference set")
            
            // Reset message statistics for new connection
            resetMessageStatistics()
            
            // Also reset packet history for new connection
            recentPacketHistory.clear()
            
            // Create and configure DatagramChannel (NIO) like Lumiya
            // Using DatagramChannel instead of DatagramSocket is CRITICAL for mobile networks
            val address = InetSocketAddress(simIP, simPort)
            
            datagramChannel = DatagramChannel.open().apply {
                // Configure as non-blocking for NIO
                configureBlocking(false)
                // Connect to the simulator address - this is KEY for receiving responses!
                // Unlike DatagramSocket, connecting a DatagramChannel ensures we only
                // receive packets from the connected address
                connect(address)
            }
            
            // Capture local bind information for diagnostics
            try {
                val localAddr = datagramChannel?.localAddress as? InetSocketAddress
                localBindAddress = localAddr?.address?.hostAddress
                localBindPort = localAddr?.port ?: 0
                Log.i(TAG, "✓ Local bind: $localBindAddress:$localBindPort")
            } catch (e: Exception) {
                Log.w(TAG, "Could not determine local bind address: ${e.message}")
            }
            
            Log.i(TAG, "✓ DatagramChannel created and connected to $simIP:$simPort")
            Log.i(TAG, "   Channel connected: ${datagramChannel?.isConnected}")
            Log.i(TAG, "   Channel open: ${datagramChannel?.isOpen}")
            
            // Create selector for NIO operations
            selector = Selector.open()
            selectionKey = datagramChannel?.register(selector, SelectionKey.OP_READ)
            
            Log.i(TAG, "✓ NIO Selector registered for read operations")
            
            // CRITICAL: Set isConnected BEFORE starting receive loop
            // The receive loop checks this flag in its while condition,
            // so it must be true before the loop starts or it exits immediately
            isConnected = true
            
            Log.i(TAG, "✓ isConnected flag set to true")
            
            // Start receive loop using NIO
            receiveJob = scope.launch {
                receiveLoopNIO()
            }
            
            Log.i(TAG, "✓ NIO Receive loop started")
            
            // Send UseCircuitCode and wait for ACK (Lumiya-style)
            // This is CRITICAL - we must wait for the circuit to be acknowledged
            // before sending CompleteAgentMovement
            Log.i(TAG, "→ Sending UseCircuitCode (waiting for ACK)...")
            val useCircuitAcked = sendUseCircuitCodeAndWait(timeoutMs = 5000)
            
            if (useCircuitAcked) {
                Log.i(TAG, "✓ UseCircuitCode acknowledged by simulator")
            } else {
                Log.w(TAG, "⚠️ UseCircuitCode ACK timeout - proceeding anyway (may fail)")
            }
            
            // Send CompleteAgentMovement to tell the simulator we're ready
            // Lumiya sends this immediately after UseCircuitCode ACK
            Log.i(TAG, "→ Sending CompleteAgentMovement...")
            sendCompleteAgentMovement()
            Log.i(TAG, "✓ CompleteAgentMovement sent")
            
            // Give a small delay for the simulator to process
            delay(100)
            
            // Send AgentThrottle to set bandwidth allocation
            // Note: Lumiya sends this after receiving RegionHandshake, but
            // sending it early shouldn't hurt and helps ensure bandwidth is set
            Log.i(TAG, "→ Sending AgentThrottle (bandwidth configuration)...")
            sendAgentThrottle()
            Log.i(TAG, "✓ AgentThrottle sent")
            
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ UDP CONNECTION ESTABLISHED (DatagramChannel)                       ║")
            Log.i(TAG, "║ Waiting for simulator to send RegionHandshake...                    ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Connection failed", e)
            lastConnectionError = e.message ?: e.javaClass.simpleName
            isConnected = false
            receiveJob?.cancel()
            try {
                datagramChannel?.close()
                selector?.close()
            } catch (closeEx: Exception) {
                Log.w(TAG, "Error closing channel/selector", closeEx)
            }
            datagramChannel = null
            selector = null
            selectionKey = null
            false
        }
    }
    
    /**
     * Send CompleteAgentMovement message.
     * This tells the simulator we're ready to receive world data.
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     */
    suspend fun sendCompleteAgentMovement() {
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - CircuitCode (4 bytes, U32) - little-endian
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Circuit code
        payload.putInt(circuitCode)
        
        Log.d(TAG, "Sending CompleteAgentMovement")
        sendPacket(MessageIds.COMPLETE_AGENT_MOVEMENT, payload.array(), reliable = true)
    }
    
    /**
     * Send RegionHandshakeReply message.
     * This acknowledges the RegionHandshake and is REQUIRED for the simulator
     * to start sending world data (objects, textures, etc.).
     * 
     * Based on Lumiya's SLAgentCircuit.sendRegionHandshakeReply()
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     * NOTE: This message is zero-coded per Lumiya's RegionHandshakeReply.java (zeroCoded = true)
     */
    suspend fun sendRegionHandshakeReply(flags: Int = 0) {
        // RegionHandshakeReply message format:
        // AgentData block:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // RegionInfo block:
        // - Flags (4 bytes, U32) - little-endian
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Flags (typically 0)
        payload.putInt(flags)
        
        Log.d(TAG, "Sending RegionHandshakeReply")
        // Zero-coded per Lumiya's implementation
        sendPacket(MessageIds.REGION_HANDSHAKE_REPLY, payload.array(), reliable = true, zerocoded = true)
    }
    
    /**
     * Send AgentThrottle message to set bandwidth allocations.
     * This tells the simulator how much bandwidth we want for different data types.
     * 
     * Based on Lumiya's SLAgentCircuit.sendAgentThrottle()
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     */
    suspend fun sendAgentThrottle(
        resend: Float = 50000f,
        land: Float = 100000f,
        wind: Float = 10000f,
        cloud: Float = 10000f,
        task: Float = 200000f,
        texture: Float = 200000f,
        asset: Float = 100000f
    ) {
        // AgentThrottle message format:
        // AgentData block:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - CircuitCode (4 bytes, U32) - little-endian
        // Throttle block:
        // - GenCounter (4 bytes, U32) - little-endian
        // - Throttles (28 bytes, 7 floats) - little-endian
        val payload = ByteBuffer.allocate(36 + 4 + 28).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Circuit code
        payload.putInt(circuitCode)
        
        // GenCounter (increment each time we send)
        payload.putInt(1)
        
        // Throttles - 7 float values for bandwidth allocation
        payload.putFloat(resend)    // Resend packets
        payload.putFloat(land)      // Land/terrain data
        payload.putFloat(wind)      // Wind data
        payload.putFloat(cloud)     // Cloud data
        payload.putFloat(task)      // Object updates (tasks)
        payload.putFloat(texture)   // Texture data
        payload.putFloat(asset)     // Asset data
        
        Log.d(TAG, "Sending AgentThrottle")
        sendPacket(MessageIds.AGENT_THROTTLE, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentUpdate message.
     * 
     * This is CRITICAL for movement and interaction in Second Life.
     * Official viewers send this approximately 10 times per second.
     * The simulator uses this to update the agent's position and camera.
     * 
     * Based on LibreMetaverse AgentManager.SendAgentUpdate() and
     * Firestorm LLAgent::sendAgentUpdate()
     */
    suspend fun sendAgentUpdate(
        bodyRotation: FloatArray = currentRotation,
        headRotation: FloatArray = currentRotation,
        state: Int = agentState,
        cameraCenter: FloatArray = currentPosition,
        cameraAtAxis: FloatArray = currentLookAt,
        cameraLeftAxis: FloatArray = floatArrayOf(-currentLookAt[1], currentLookAt[0], 0f),
        cameraUpAxis: FloatArray = floatArrayOf(0f, 0f, 1f),
        far: Float = 128f,
        controlFlags: Int = this.controlFlags,
        flags: Int = 0
    ) {
        // AgentUpdate message format (High frequency, ID 0x04):
        // AgentData block:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - BodyRotation (12 bytes, packed quaternion - 3 floats, w computed)
        // - HeadRotation (12 bytes, packed quaternion)
        // - State (1 byte)
        // - CameraCenter (12 bytes, Vector3)
        // - CameraAtAxis (12 bytes, Vector3)
        // - CameraLeftAxis (12 bytes, Vector3)
        // - CameraUpAxis (12 bytes, Vector3)
        // - Far (4 bytes, F32)
        // - ControlFlags (4 bytes, U32)
        // - Flags (1 byte)
        
        val payload = ByteBuffer.allocate(114).order(BODY_BYTE_ORDER)
        
        // AgentID
        payload.putUUID(agentId)
        
        // SessionID
        payload.putUUID(sessionId)
        
        // Body rotation (packed quaternion - x, y, z, w computed on server)
        payload.putFloat(bodyRotation[0])
        payload.putFloat(bodyRotation[1])
        payload.putFloat(bodyRotation[2])
        
        // Head rotation
        payload.putFloat(headRotation[0])
        payload.putFloat(headRotation[1])
        payload.putFloat(headRotation[2])
        
        // State (0 = standing, 1 = sitting, etc.)
        payload.put(state.toByte())
        
        // Camera center
        payload.putFloat(cameraCenter[0])
        payload.putFloat(cameraCenter[1])
        payload.putFloat(cameraCenter[2])
        
        // Camera at axis (look direction)
        payload.putFloat(cameraAtAxis[0])
        payload.putFloat(cameraAtAxis[1])
        payload.putFloat(cameraAtAxis[2])
        
        // Camera left axis
        payload.putFloat(cameraLeftAxis[0])
        payload.putFloat(cameraLeftAxis[1])
        payload.putFloat(cameraLeftAxis[2])
        
        // Camera up axis
        payload.putFloat(cameraUpAxis[0])
        payload.putFloat(cameraUpAxis[1])
        payload.putFloat(cameraUpAxis[2])
        
        // Far distance
        payload.putFloat(far)
        
        // Control flags (movement, etc.)
        payload.putInt(controlFlags)
        
        // Flags
        payload.put(flags.toByte())
        
        sendPacket(MessageIds.AGENT_UPDATE, payload.array(), reliable = false)
    }
    
    /**
     * Update agent position for AgentUpdate messages.
     * Call this when the avatar moves.
     */
    fun updateAgentPosition(x: Float, y: Float, z: Float) {
        currentPosition = floatArrayOf(x, y, z)
    }
    
    /**
     * Update agent rotation for AgentUpdate messages.
     * @param rotation Quaternion as [x, y, z, w]
     */
    fun updateAgentRotation(rotation: FloatArray) {
        if (rotation.size >= 4) {
            currentRotation = rotation.copyOf()
        }
    }
    
    /**
     * Update camera look-at direction.
     */
    fun updateLookAt(x: Float, y: Float, z: Float) {
        currentLookAt = floatArrayOf(x, y, z)
    }
    
    /**
     * Set control flags (for movement).
     */
    fun setControlFlags(flags: Int) {
        controlFlags = flags
    }
    
    /**
     * Start sending periodic AgentUpdate messages.
     * This is required for proper operation in Second Life.
     */
    fun startAgentUpdates() {
        agentUpdateJob?.cancel()
        agentUpdateJob = scope.launch {
            Log.d(TAG, "Starting periodic AgentUpdate messages")
            while (isConnected) {
                sendAgentUpdate()
                delay(AGENT_UPDATE_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Stop sending periodic AgentUpdate messages.
     */
    fun stopAgentUpdates() {
        agentUpdateJob?.cancel()
        agentUpdateJob = null
    }
    
    /**
     * Handle StartPingCheck message from simulator.
     * We must respond with CompletePingCheck to maintain the connection.
     * 
     * Based on LibreMetaverse NetworkManager.HandleStartPingCheck()
     */
    suspend fun handleStartPingCheck(pingId: Byte, oldestUnacked: Int) {
        val payload = ByteBuffer.allocate(5).order(BODY_BYTE_ORDER)
        payload.put(pingId)
        payload.putInt(getOldestUnackedSequence())
        
        Log.d(TAG, "Responding to ping check $pingId")
        sendPacket(MessageIds.COMPLETE_PING_CHECK, payload.array(), reliable = false)
    }
    
    /**
     * Get oldest unacknowledged sequence number for ping response.
     */
    private fun getOldestUnackedSequence(): Int {
        return pendingAcks.keys.minOrNull() ?: 0
    }
    
    /**
     * Queue an ACK for batching (more efficient than individual ACKs).
     * This matches official viewer behavior.
     */
    fun queueAck(sequenceNumber: Int) {
        synchronized(pendingAckIds) {
            pendingAckIds.add(sequenceNumber)
            
            // Flush if we have enough ACKs or enough time has passed
            if (pendingAckIds.size >= MAX_ACKS_PER_PACKET || 
                System.currentTimeMillis() - lastAckFlush > ACK_FLUSH_INTERVAL_MS) {
                scope.launch { flushAcks() }
            }
        }
    }
    
    /**
     * Send batched ACKs.
     */
    private suspend fun flushAcks() {
        val acksToSend: List<Int>
        synchronized(pendingAckIds) {
            if (pendingAckIds.isEmpty()) return
            acksToSend = pendingAckIds.toList()
            pendingAckIds.clear()
            lastAckFlush = System.currentTimeMillis()
        }
        
        val payload = ByteBuffer.allocate(1 + acksToSend.size * 4).order(BODY_BYTE_ORDER)
        payload.put(acksToSend.size.toByte())
        acksToSend.forEach { payload.putInt(it) }
        
        Log.d(TAG, "Sending ${acksToSend.size} batched ACKs")
        sendPacket(MessageIds.PACKET_ACK, payload.array(), reliable = false)
    }
    
    /**
     * Disconnect from the simulator
     */
    fun disconnect() {
        isConnected = false
        agentUpdateJob?.cancel()
        receiveJob?.cancel()
        
        // Flush any remaining ACKs
        scope.launch { flushAcks() }
        
        // Close DatagramChannel and Selector (NIO)
        try {
            selectionKey?.cancel()
            datagramChannel?.close()
            selector?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing channel/selector", e)
        }
        datagramChannel = null
        selector = null
        selectionKey = null
        
        pendingAcks.clear()
        ackCallbacks.clear()  // Clear ACK callbacks to prevent memory leaks
        synchronized(pendingAckIds) { pendingAckIds.clear() }
        
        // Clear packet buffer and reset handlers ready flag for next connection
        packetBuffer.clear()
        handlersReady.set(false)
        
        // Note: Message statistics are preserved for post-disconnect diagnostics.
        // They will be reset when connect() is called for a new session.
        
        Log.i(TAG, "Disconnected")
    }
    
    /**
     * Register a handler for a message type
     */
    fun registerHandler(messageId: Int, handler: MessageHandler) {
        messageHandlers[messageId] = handler
    }
    
    /**
     * Mark handlers as ready and process any buffered packets.
     * This should be called after all critical message handlers have been registered.
     * 
     * This method ensures packets that arrived before handlers were registered
     * are not lost and are processed in order.
     */
    fun setHandlersReady() {
        if (handlersReady.compareAndSet(false, true)) {
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ HANDLERS MARKED READY - Processing buffered packets")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            processBufferedPackets()
        }
    }
    
    /**
     * Process all buffered packets using parallel processing for efficiency.
     * Called when handlers become ready.
     * 
     * Uses coroutines with a dedicated thread pool to process multiple packets
     * concurrently, maximizing throughput on multi-core devices.
     * Processing is done in configurable batches to balance throughput and resource usage.
     */
    private fun processBufferedPackets() {
        val bufferedCount = packetBuffer.size
        if (bufferedCount == 0) {
            Log.i(TAG, "   No buffered packets to process")
            return
        }
        
        val config = processingConfig
        val effectiveThreads = getEffectiveThreadCount()
        val batchSize = getEffectiveBatchSize()
        
        Log.i(TAG, "   Processing $bufferedCount buffered packet(s) in parallel...")
        Log.i(TAG, "   Config: ${effectiveThreads} threads, batch size $batchSize, memory ${config.bufferMemory.displayName}")
        
        val processedCount = AtomicInteger(0)
        val errorCount = AtomicInteger(0)
        
        // Collect all buffered packets
        val packetsToProcess = mutableListOf<BufferedPacket>()
        while (true) {
            val packet = packetBuffer.poll() ?: break
            packetsToProcess.add(packet)
        }
        
        // Process packets in parallel batches using coroutines
        scope.launch(packetProcessorDispatcher) {
            // Process in batches for better resource management
            packetsToProcess.chunked(batchSize).forEach { batch ->
                batch.map { bufferedPacket ->
                    async {
                        try {
                            val age = System.currentTimeMillis() - bufferedPacket.timestamp
                            Log.d(TAG, "   Processing buffered packet (age: ${age}ms, size: ${bufferedPacket.data.size} bytes)")
                            dispatchPacket(bufferedPacket.data)
                            processedCount.incrementAndGet()
                        } catch (e: Exception) {
                            Log.e(TAG, "   Error processing buffered packet", e)
                            errorCount.incrementAndGet()
                        }
                    }
                }.awaitAll()
            }
            
            Log.i(TAG, "   ✓ Processed ${processedCount.get()} buffered packets (${errorCount.get()} errors)")
        }
    }
    
    /**
     * Get the number of packets currently in the buffer.
     * Useful for diagnostics.
     */
    fun getBufferedPacketCount(): Int = packetBuffer.size
    
    /**
     * Check if handlers are ready to process packets.
     */
    fun areHandlersReady(): Boolean = handlersReady.get()
    
    /**
     * Send a packet using NIO DatagramChannel.
     *
     * The payload must already be encoded in little-endian message order; the header and message
     * number are written in network (big-endian) order.
     * 
     * Uses DatagramChannel.write() for connected channel (Lumiya-style).
     * 
     * @param onAck Optional callback to invoke when this packet is acknowledged.
     *              Only invoked if the packet is reliable.
     * @return The sequence number assigned to this packet
     */
    suspend fun sendPacket(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean = false,
        zerocoded: Boolean = false,
        onAck: (() -> Unit)? = null
    ): Int = withContext(Dispatchers.IO) {
        val channel = this@UDPConnection.datagramChannel ?: return@withContext -1
        
        val seqNum = sequenceNumber.getAndIncrement()
        
        // Build packet header (BIG_ENDIAN per protocol)
        var flags = 0
        if (reliable) flags = flags or FLAG_RELIABLE
        if (zerocoded) flags = flags or FLAG_ZEROCODED
        
        val header = ByteBuffer.allocate(PACKET_HEADER_SIZE).order(HEADER_BYTE_ORDER)
        header.put(flags.toByte())
        header.putInt(seqNum)
        header.put(0.toByte()) // Extra header byte (no extra data)
        
        // Encode message ID for transmission (network order).
        // This must be the INVERSE of decodeMessageIdLumiyaStyle().
        //
        // Lumiya-style message IDs:
        // - High frequency: signed byte (-128 to 254, excluding -1)
        //   -> Encoded as single byte
        // - Medium frequency: MEDIUM_FREQUENCY_MIN to MEDIUM_FREQUENCY_MAX (65280-65534)
        //   -> Encoded as 0xFF followed by (id & 0xFF)
        // - Low frequency: negative values < HIGH_FREQUENCY_THRESHOLD (-128)
        //   -> Encoded as 0xFF 0xFF followed by big-endian short
        val messageBytes = when {
            // Low frequency: negative values less than HIGH_FREQUENCY_THRESHOLD
            // e.g., -65533 for UseCircuitCode, -65388 for RegionHandshake
            messageId < HIGH_FREQUENCY_THRESHOLD -> {
                // Extract the original short value: messageId = short | LOW_FREQUENCY_BASE
                // So short = messageId & 0xFFFF
                val shortValue = messageId and 0xFFFF
                byteArrayOf(
                    0xFF.toByte(),
                    0xFF.toByte(),
                    ((shortValue shr 8) and 0xFF).toByte(),
                    (shortValue and 0xFF).toByte()
                )
            }
            // Medium frequency: MEDIUM_FREQUENCY_MIN to MEDIUM_FREQUENCY_MAX
            // e.g., 65286 for CoarseLocationUpdate
            messageId in MEDIUM_FREQUENCY_MIN..MEDIUM_FREQUENCY_MAX -> {
                byteArrayOf(0xFF.toByte(), (messageId and 0xFF).toByte())
            }
            // High frequency: signed byte range (excluding -1/0xFF which is prefix)
            // e.g., 12 for ObjectUpdate, -5 for PacketAck
            else -> {
                byteArrayOf(messageId.toByte())
            }
        }
        
        val packet = header.array() + messageBytes + payload
        val finalPacket = if (zerocoded) zeroencode(packet) else packet
        
        try {
            // Record send attempt time
            lastSendAttemptTime = System.currentTimeMillis()
            
            // Use DatagramChannel.write() for connected channel (like Lumiya)
            val buffer = ByteBuffer.wrap(finalPacket)
            val bytesWritten = channel.write(buffer)
            
            if (bytesWritten > 0) {
                // Record successful send in packet history
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_SUCCESS,
                    messageId = messageId,
                    data = finalPacket,
                    sequenceNumber = seqNum,
                    success = true
                )
                
                if (reliable) {
                    pendingAcks[seqNum] = PendingPacket(seqNum, finalPacket, System.currentTimeMillis())
                    // Register ACK callback if provided
                    if (onAck != null) {
                        ackCallbacks[seqNum] = onAck
                    }
                }
            } else {
                Log.w(TAG, "DatagramChannel.write() returned 0 bytes - packet may not have been sent")
                // Record failed send
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                    messageId = messageId,
                    data = finalPacket,
                    sequenceNumber = seqNum,
                    success = false,
                    errorMessage = "DatagramChannel.write() returned 0 bytes"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send packet", e)
            // Record failed send with error
            recordPacketEvent(
                type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                messageId = messageId,
                data = finalPacket,
                sequenceNumber = seqNum,
                success = false,
                errorMessage = e.message
            )
        }
        
        seqNum
    }
    
    /**
     * NIO-based receive loop using DatagramChannel with Selector (Lumiya-style).
     * 
     * This approach provides better support for mobile/cellular networks by:
     * 1. Using a connected DatagramChannel that only receives from the simulator
     * 2. Using non-blocking I/O with a Selector for efficient packet reception
     * 3. Allowing proper timeout handling without blocking
     */
    private suspend fun receiveLoopNIO() {
        val buffer = ByteBuffer.allocate(BUFFER_SIZE)
        var packetsReceived = 0
        var localBytesReceived = 0L
        
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ NIO UDP RECEIVE LOOP STARTED (DatagramChannel)                   ║")
        Log.i(TAG, "║ Will log all incoming packets for debugging                       ║")
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        
        val localSelector = selector
        val localChannel = datagramChannel
        
        if (localSelector == null || localChannel == null) {
            Log.e(TAG, "❌ Selector or DatagramChannel is null - cannot start receive loop")
            return
        }
        
        while (isConnected && localChannel.isOpen) {
            try {
                // Use select with timeout for periodic resend checks
                val readyKeys = localSelector.select(1000) // 1 second timeout
                
                if (readyKeys > 0) {
                    val iterator = localSelector.selectedKeys().iterator()
                    while (iterator.hasNext()) {
                        val key = iterator.next()
                        iterator.remove()
                        
                        if (key.isReadable) {
                            buffer.clear()
                            
                            // Read from connected channel - no need to specify address
                            val bytesRead = localChannel.read(buffer)
                            
                            if (bytesRead > 0) {
                                packetsReceived++
                                localBytesReceived += bytesRead
                                
                                // Update global statistics for diagnostics
                                totalPacketsReceived.incrementAndGet()
                                totalBytesReceived.addAndGet(bytesRead.toLong())
                                lastReceiveTime = System.currentTimeMillis()
                                
                                buffer.flip()
                                val data = ByteArray(bytesRead)
                                buffer.get(data)
                                
                                // Log packet reception
                                Log.i(TAG, "📦 PACKET RECEIVED #${packetsReceived}: $bytesRead bytes (NIO)")
                                
                                // Log raw hex for first few packets (for debugging)
                                if (packetsReceived <= 10) {
                                    val hexPreview = data.take(32).joinToString(" ") { "%02X".format(it) }
                                    Log.d(TAG, "   Raw preview: $hexPreview")
                                }
                                
                                // Extract message ID from packet for history
                                val msgId = extractMessageId(data)
                                
                                // Record receive event in packet history
                                recordPacketEvent(
                                    type = PacketHistoryEntry.PacketEventType.RECEIVE,
                                    messageId = msgId,
                                    data = data,
                                    sequenceNumber = extractSequenceNumber(data),
                                    success = true
                                )
                                
                                processPacket(data)
                            }
                        }
                    }
                } else {
                    // Timeout - check for resends
                    if (packetsReceived == 0) {
                        Log.w(TAG, "⚠️ No packets received yet (NIO), waiting...")
                    }
                }
                
                // Always try to resend pending packets after processing
                resendPendingPackets()
                
            } catch (e: java.nio.channels.ClosedChannelException) {
                Log.i(TAG, "Channel closed - stopping receive loop")
                break
            } catch (e: Exception) {
                if (isConnected) {
                    Log.e(TAG, "❌ NIO Receive error", e)
                }
            }
        }
        
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ NIO UDP RECEIVE LOOP STOPPED                                      ║")
        Log.i(TAG, "║ Total packets received: $packetsReceived                                 ║")
        Log.i(TAG, "║ Total bytes received: $localBytesReceived                                   ║")
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
    }
    
    /**
     * Legacy receive loop using blocking DatagramSocket (kept for fallback/reference)
     */
    @Deprecated("Use receiveLoopNIO() instead for better mobile network support")
    private suspend fun receiveLoop() {
        Log.w(TAG, "Using legacy receiveLoop - consider using receiveLoopNIO instead")
        // This method is now unused but kept for reference
    }
    
    private fun processPacket(data: ByteArray) {
        if (data.size < PACKET_HEADER_SIZE) {
            Log.w(TAG, "⚠️ Packet too small: ${data.size} bytes (minimum $PACKET_HEADER_SIZE)")
            return
        }
        
        val flags = data[0].toInt() and 0xFF
        val seqNum = ByteBuffer.wrap(data, 1, 4).order(HEADER_BYTE_ORDER).int
        
        val isZerocoded = (flags and FLAG_ZEROCODED) != 0
        val isReliable = (flags and FLAG_RELIABLE) != 0
        val hasAcks = (flags and FLAG_ACK) != 0
        
        // Log packet details
        val flagsStr = buildString {
            if (isZerocoded) append("[ZERO]")
            if (isReliable) append("[RELIABLE]")
            if (hasAcks) append("[ACK]")
        }
        Log.d(TAG, "   Packet #$seqNum $flagsStr - ${data.size} bytes")
        
        var decoded = data
        if (isZerocoded) {
            decoded = zerodecode(data)
            Log.d(TAG, "   Zero-decoded: ${decoded.size} bytes")
        }
        
        // Handle acks at end of packet - ALWAYS process these immediately (time-sensitive)
        // CRITICAL: Appended ACKs are BIG_ENDIAN (network order), same as packet header
        // This is different from the message payload which is LITTLE_ENDIAN
        // See Lumiya's SLMessage.Pack() and AppendPendingAcks() for reference
        if (hasAcks && decoded.size > 1) {
            val numAcks = decoded[decoded.size - 1].toInt() and 0xFF
            if (numAcks > 0) {
                Log.d(TAG, "   Contains $numAcks ACK(s)")
                val ackStart = decoded.size - 1 - numAcks * 4
                if (ackStart > PACKET_HEADER_SIZE) {
                    for (i in 0 until numAcks) {
                        val offset = ackStart + i * 4
                        // Appended ACKs are BIG_ENDIAN (network order) - NOT little-endian!
                        val ackSeq = ByteBuffer.wrap(decoded, offset, 4).order(HEADER_BYTE_ORDER).int
                        pendingAcks.remove(ackSeq)
                        Log.d(TAG, "   ACK for packet #$ackSeq")
                        // Invoke ACK callback if registered (for sequence-dependent operations)
                        ackCallbacks.remove(ackSeq)?.invoke()
                    }
                }
            }
        }
        
        // Send ack if reliable - ALWAYS send immediately (time-sensitive)
        if (isReliable) {
            scope.launch {
                sendAck(seqNum)
            }
        }
        
        // If handlers are ready, dispatch immediately; otherwise buffer for later processing
        if (handlersReady.get()) {
            dispatchPacket(decoded)
        } else {
            // Buffer the packet for later processing (using configurable buffer size)
            if (packetBuffer.size < getEffectiveBufferSize()) {
                packetBuffer.offer(BufferedPacket(decoded.copyOf()))
                Log.d(TAG, "   📥 Packet buffered (handlers not ready yet) - buffer size: ${packetBuffer.size}/${getEffectiveBufferSize()}")
            } else {
                Log.w(TAG, "   ⚠️ Packet buffer full (${getEffectiveBufferSize()}), dropping packet")
            }
        }
    }
    
    /**
     * Dispatch a packet to its message handler.
     * This handles the actual message parsing and handler invocation.
     * Called directly when handlers are ready, or from processBufferedPackets() for buffered packets.
     * 
     * Note: This method intentionally re-reads the flags byte from the decoded packet data
     * because buffered packets store the decoded (zero-expanded) data, not raw data.
     * The minimal duplication (flags parsing) is acceptable to keep the buffer simple
     * and avoid storing additional metadata per packet.
     * 
     * Handler execution is dispatched asynchronously on the packet processor thread pool
     * to enable parallel processing and prevent blocking the receive loop.
     */
    private fun dispatchPacket(decoded: ByteArray) {
        if (decoded.size < PACKET_HEADER_SIZE) {
            Log.w(TAG, "   ⚠️ Decoded packet too small for dispatch: ${decoded.size} bytes")
            return
        }
        
        val flags = decoded[0].toInt() and 0xFF
        val hasAcks = (flags and FLAG_ACK) != 0
        
        // Parse message ID using Lumiya-compatible decoding
        // This matches Lumiya's SLMessage.DecodeMessageID() exactly:
        //   byte b = byteBuffer.get();
        //   if (b != -1) return b;  // High frequency - returns SIGNED byte
        //   byte b2 = byteBuffer.get();
        //   return b2 != -1 ? b2 | 65280 : byteBuffer.getShort() | (-65536);
        var offset = PACKET_HEADER_SIZE
        val messageId = decodeMessageIdLumiyaStyle(decoded, offset)
        if (messageId == null) {
            Log.w(TAG, "   ⚠️ Could not parse message ID")
            return
        }
        offset = messageId.second
        val msgId = messageId.first
        
        // Get message name for logging
        val messageName = getMessageName(msgId)
        Log.i(TAG, "   📨 Message: $messageName (0x${msgId.toString(16).uppercase()})")
        
        // Track message statistics for diagnostics
        val cleanMessageName = messageName.replace(CRITICAL_MESSAGE_PREFIX, "") // Remove prefix for stats key
        messageTypeCounts.getOrPut(cleanMessageName) { AtomicInteger(0) }.incrementAndGet()
        lastMessageTimes[cleanMessageName] = System.currentTimeMillis()
        
        // Dispatch to handler
        val payload = decoded.copyOfRange(offset, decoded.size - if (hasAcks) 1 + (decoded[decoded.size - 1].toInt() and 0xFF) * 4 else 0)
        
        // Handle PacketAck messages internally (acknowledges our sent packets) - process synchronously as it's time-critical
        if (msgId == MessageIds.PACKET_ACK) {
            handlePacketAck(payload)
            return
        }
        
        val handler = messageHandlers[msgId]
        if (handler != null) {
            Log.d(TAG, "   → Dispatching to handler asynchronously (${payload.size} bytes payload)")
            // Dispatch handler execution on thread pool for parallel processing
            scope.launch(packetProcessorDispatcher) {
                try {
                    handler.onMessage(msgId, payload)
                    Log.d(TAG, "   ✓ Handler executed successfully for $messageName")
                } catch (e: Exception) {
                    Log.e(TAG, "   ✗ Handler failed for $messageName", e)
                }
            }
        } else {
            Log.w(TAG, "   ⚠️ No handler registered for message: $messageName")
        }
    }
    
    /**
     * Handle standalone PacketAck message from simulator.
     * Format: 1 byte count, then N x 4-byte sequence numbers (little-endian).
     */
    private fun handlePacketAck(payload: ByteArray) {
        if (payload.isEmpty()) return
        
        val buffer = ByteBuffer.wrap(payload).order(BODY_BYTE_ORDER)
        val count = buffer.get().toInt() and 0xFF
        
        Log.d(TAG, "   Processing $count ACKs from PacketAck message")
        
        for (i in 0 until count) {
            if (buffer.remaining() >= 4) {
                val ackSeq = buffer.int
                val removedPacket = pendingAcks.remove(ackSeq)
                Log.d(TAG, "   ACK for packet #$ackSeq")
                
                // Record ACK received event in packet history
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.ACK_RECEIVED,
                    messageId = MessageIds.PACKET_ACK,
                    data = payload,
                    sequenceNumber = ackSeq,
                    success = true
                )
                
                // Invoke ACK callback if registered (for sequence-dependent operations)
                ackCallbacks.remove(ackSeq)?.invoke()
            }
        }
    }
    
    /**
     * Decode message ID using Lumiya-compatible method.
     * 
     * This exactly matches Lumiya's SLMessage.DecodeMessageID():
     * ```java
     * public static int DecodeMessageID(ByteBuffer byteBuffer) {
     *     byte b = byteBuffer.get();
     *     if (b != -1) {
     *         return b;  // High frequency: returns SIGNED byte (-128 to 127)
     *     }
     *     byte b2 = byteBuffer.get();
     *     return b2 != -1 ? b2 | 65280 : byteBuffer.getShort() | (-65536);
     * }
     * ```
     * 
     * Key insight: Lumiya uses SIGNED bytes and shorts, which affects the resulting IDs:
     * - High frequency: signed byte (0x00-0xFE = 0-254, but 0xFB = -5)
     * - Medium frequency: (signed byte | MEDIUM_FREQUENCY_BASE) gives MEDIUM_FREQUENCY_MIN-MEDIUM_FREQUENCY_MAX
     * - Low frequency: (signed short | LOW_FREQUENCY_BASE) gives negative values < HIGH_FREQUENCY_THRESHOLD
     * 
     * @param data The packet data
     * @param startOffset The offset where the message ID starts (after header)
     * @return Pair of (messageId, newOffset) or null if parsing failed
     */
    private fun decodeMessageIdLumiyaStyle(data: ByteArray, startOffset: Int): Pair<Int, Int>? {
        if (data.size <= startOffset) return null
        
        var offset = startOffset
        
        // First byte - check if it's 0xFF (which is -1 as signed byte)
        val b1 = data[offset].toInt() // Signed byte, -128 to 127
        offset++
        
        if (b1 != -1) {
            // High frequency message - return the signed byte value directly
            // This matches Lumiya: if (b != -1) return b;
            // e.g., 0x0C (12) = ObjectUpdate, 0xFB (-5) = PacketAck
            return Pair(b1, offset)
        }
        
        // Second byte
        if (data.size <= offset) return null
        val b2 = data[offset].toInt() // Signed byte
        offset++
        
        if (b2 != -1) {
            // Medium frequency message - byte OR MEDIUM_FREQUENCY_BASE
            // This matches Lumiya: b2 | 65280
            // e.g., 0x06 (6) | 65280 = 65286 = CoarseLocationUpdate
            return Pair(b2 or MEDIUM_FREQUENCY_BASE, offset)
        }
        
        // Low frequency message - next two bytes as signed short OR LOW_FREQUENCY_BASE
        if (data.size <= offset + 1) return null
        
        // Big-endian to signed short conversion:
        // 1. Read two bytes as unsigned (byte3=high, byte4=low) in network/big-endian order
        // 2. Combine into a 16-bit value: (byte3 << 8) | byte4
        // 3. Convert to signed short via .toShort().toInt() to sign-extend
        // This matches Java's ByteBuffer.getShort() in BIG_ENDIAN order
        val byte3 = data[offset].toInt() and 0xFF
        val byte4 = data[offset + 1].toInt() and 0xFF
        offset += 2
        
        val shortValue = ((byte3 shl 8) or byte4).toShort().toInt()
        
        // This matches Lumiya: byteBuffer.getShort() | (-65536)
        // e.g., 0x0094 (148) | -65536 = -65388 = RegionHandshake
        return Pair(shortValue or LOW_FREQUENCY_BASE, offset)
    }
    
    /**
     * Get human-readable message name from ID for debugging.
     */
    private fun getMessageName(messageId: Int): String {
        return when (messageId) {
            MessageIds.USE_CIRCUIT_CODE -> "UseCircuitCode"
            MessageIds.COMPLETE_AGENT_MOVEMENT -> "CompleteAgentMovement"
            MessageIds.LOGOUT_REQUEST -> "LogoutRequest"
            MessageIds.REGION_HANDSHAKE -> "${CRITICAL_MESSAGE_PREFIX}RegionHandshake"
            MessageIds.REGION_HANDSHAKE_REPLY -> "RegionHandshakeReply"
            MessageIds.AGENT_THROTTLE -> "AgentThrottle"
            MessageIds.AGENT_MOVEMENT_COMPLETE -> "AgentMovementComplete"
            MessageIds.CHAT_FROM_SIMULATOR -> "ChatFromSimulator"
            MessageIds.IMPROVED_INSTANT_MESSAGE -> "ImprovedInstantMessage"
            MessageIds.OBJECT_UPDATE -> "ObjectUpdate"
            MessageIds.OBJECT_UPDATE_COMPRESSED -> "ObjectUpdateCompressed"
            MessageIds.IMPROVED_TERSE_OBJECT_UPDATE -> "ImprovedTerseObjectUpdate"
            MessageIds.AVATAR_ANIMATION -> "AvatarAnimation"
            MessageIds.AGENT_ANIMATION -> "AgentAnimation"
            MessageIds.COARSE_LOCATION_UPDATE -> "CoarseLocationUpdate"
            MessageIds.KILL_OBJECT -> "KillObject"
            MessageIds.PACKET_ACK -> "PacketAck"
            MessageIds.START_PING_CHECK -> "StartPingCheck"
            MessageIds.COMPLETE_PING_CHECK -> "CompletePingCheck"
            MessageIds.AGENT_UPDATE -> "AgentUpdate"
            else -> "Unknown(0x${messageId.toString(16).uppercase()})"
        }
    }
    
    private suspend fun sendAck(seqNum: Int) {
        // PacketAck message format per SL protocol:
        // - Count (U8): Number of ACKs (1 in this case)
        // - ID (U32): Sequence number, little-endian per message template
        val payload = ByteBuffer.allocate(5).order(BODY_BYTE_ORDER)
        payload.put(1.toByte())  // Count: 1 ACK
        payload.putInt(seqNum)
        sendPacket(MessageIds.PACKET_ACK, payload.array(), reliable = false)
    }
    
    /**
     * Create the UseCircuitCode payload buffer.
     * 
     * UseCircuitCode message format:
     * - CircuitCode (4 bytes, U32) - little-endian
     * - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
     * - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     */
    private fun createUseCircuitCodePayload(): ByteArray {
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        payload.putInt(circuitCode)
        payload.putUUID(sessionId)
        payload.putUUID(agentId)
        return payload.array()
    }
    
    private suspend fun sendUseCircuitCode() {
        Log.d(TAG, "Sending UseCircuitCode: circuit=$circuitCode, agent=${agentId.toString().take(8)}...")
        sendPacket(MessageIds.USE_CIRCUIT_CODE, createUseCircuitCodePayload(), reliable = true)
    }
    
    /**
     * Send UseCircuitCode and wait for its ACK.
     * 
     * This follows Lumiya's SLAgentCircuit.SendUseCode() pattern which uses
     * an event listener to wait for the ACK before proceeding.
     * 
     * @param timeoutMs Maximum time to wait for ACK
     * @return true if ACK was received, false if timeout
     */
    private suspend fun sendUseCircuitCodeAndWait(timeoutMs: Long = 5000): Boolean {
        val payload = createUseCircuitCodePayload()
        
        Log.d(TAG, "Sending UseCircuitCode: circuit=$circuitCode, agent=${agentId.toString().take(8)}...")
        
        // Use suspendCancellableCoroutine to wait for ACK with proper exception handling
        return try {
            withTimeout(timeoutMs) {
                suspendCancellableCoroutine { continuation ->
                    // Track the sequence number so we can clean up on cancellation
                    var seqNum = -1
                    
                    continuation.invokeOnCancellation {
                        // Clean up the callback if the continuation is cancelled
                        if (seqNum >= 0) {
                            ackCallbacks.remove(seqNum)
                        }
                    }
                    
                    scope.launch {
                        try {
                            seqNum = sendPacket(
                                MessageIds.USE_CIRCUIT_CODE,
                                payload,
                                reliable = true,
                                onAck = {
                                    Log.d(TAG, "UseCircuitCode ACK received!")
                                    if (continuation.isActive) {
                                        continuation.resume(true)
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to send UseCircuitCode", e)
                            if (continuation.isActive) {
                                continuation.resume(false)
                            }
                        }
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "UseCircuitCode ACK timeout after ${timeoutMs}ms")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error waiting for UseCircuitCode ACK", e)
            false
        }
    }
    
    private suspend fun resendPendingPackets() {
        val now = System.currentTimeMillis()
        val channel = datagramChannel
        
        pendingAcks.values.filter { now - it.timestamp > ACK_TIMEOUT_MS }.forEach { pending ->
            if (pending.retries < MAX_RETRIES) {
                // Resend with resent flag
                val resendPacket = pending.data.copyOf()
                resendPacket[0] = (resendPacket[0].toInt() or FLAG_RESENT).toByte()
                
                try {
                    // Use DatagramChannel.write() for connected channel
                    if (channel != null && channel.isConnected) {
                        val buffer = ByteBuffer.wrap(resendPacket)
                        channel.write(buffer)
                        pending.retries++
                        pending.timestamp = now
                        packetsResentCount.incrementAndGet() // Track resent packets for diagnostics
                        
                        // Record resend event in packet history
                        val msgId = extractMessageId(resendPacket)
                        recordPacketEvent(
                            type = PacketHistoryEntry.PacketEventType.RESEND,
                            messageId = msgId,
                            data = resendPacket,
                            sequenceNumber = pending.seqNum,
                            success = true
                        )
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Resend failed for seq ${pending.seqNum}")
                    // Record failed resend - extract message ID for better diagnostics
                    val msgId = extractMessageId(resendPacket)
                    recordPacketEvent(
                        type = PacketHistoryEntry.PacketEventType.RESEND,
                        messageId = msgId,
                        data = resendPacket,
                        sequenceNumber = pending.seqNum,
                        success = false,
                        errorMessage = e.message
                    )
                }
            } else {
                pendingAcks.remove(pending.seqNum)
                Log.w(TAG, "Packet ${pending.seqNum} dropped after max retries")
                // Record ACK timeout in packet history - extract message ID for better diagnostics
                val msgId = extractMessageId(pending.data)
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.ACK_TIMEOUT,
                    messageId = msgId,
                    data = pending.data,
                    sequenceNumber = pending.seqNum,
                    success = false,
                    errorMessage = "Dropped after $MAX_RETRIES retries"
                )
            }
        }
    }
    
    private fun zeroencode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        // Skip header (packet header is always network order)
        while (i < PACKET_HEADER_SIZE && i < data.size) {
            result.add(data[i])
            i++
        }
        while (i < data.size) {
            if (data[i] == 0.toByte()) {
                var count = 0
                while (i < data.size && data[i] == 0.toByte() && count < 255) {
                    count++
                    i++
                }
                result.add(0)
                result.add(count.toByte())
            } else {
                result.add(data[i])
                i++
            }
        }
        return result.toByteArray()
    }
    
    private fun zerodecode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        // Skip header (packet header is always network order)
        while (i < PACKET_HEADER_SIZE && i < data.size) {
            result.add(data[i])
            i++
        }
        while (i < data.size) {
            if (data[i] == 0.toByte() && i + 1 < data.size) {
                val count = data[i + 1].toInt() and 0xFF
                repeat(count) { result.add(0) }
                i += 2
            } else {
                result.add(data[i])
                i++
            }
        }
        return result.toByteArray()
    }
    
    data class PendingPacket(
        val seqNum: Int,
        val data: ByteArray,
        var timestamp: Long,
        var retries: Int = 0
    )
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Reset message statistics for a new connection session.
     */
    private fun resetMessageStatistics() {
        totalPacketsReceived.set(0)
        totalBytesReceived.set(0)
        packetsResentCount.set(0)
        messageTypeCounts.clear()
        lastMessageTimes.clear()
        Log.d(TAG, "Message statistics reset for new connection")
    }
    
    /**
     * Check if the UDP connection is currently connected
     */
    fun isCurrentlyConnected(): Boolean = isConnected
    
    /**
     * Get the current sequence number (packets sent)
     */
    fun getCurrentSequenceNumber(): Int = sequenceNumber.get()
    
    /**
     * Get the number of pending acknowledgments (packets awaiting ACK)
     */
    fun getPendingAckCount(): Int = pendingAcks.size
    
    /**
     * Get the number of registered message handlers
     */
    fun getRegisteredHandlerCount(): Int = messageHandlers.size
    
    /**
     * Get list of registered message handler IDs for diagnostics
     */
    fun getRegisteredHandlerIds(): List<String> {
        return messageHandlers.keys.map { id ->
            when (id) {
                MessageIds.REGION_HANDSHAKE -> "REGION_HANDSHAKE"
                MessageIds.AGENT_MOVEMENT_COMPLETE -> "AGENT_MOVEMENT_COMPLETE"
                MessageIds.CHAT_FROM_SIMULATOR -> "CHAT_FROM_SIMULATOR"
                MessageIds.OBJECT_UPDATE -> "OBJECT_UPDATE"
                MessageIds.OBJECT_UPDATE_COMPRESSED -> "OBJECT_UPDATE_COMPRESSED"
                MessageIds.AVATAR_ANIMATION -> "AVATAR_ANIMATION"
                MessageIds.IMPROVED_TERSE_OBJECT_UPDATE -> "IMPROVED_TERSE_OBJECT_UPDATE"
                MessageIds.KILL_OBJECT -> "KILL_OBJECT"
                MessageIds.COARSE_LOCATION_UPDATE -> "COARSE_LOCATION_UPDATE"
                MessageIds.START_PING_CHECK -> "START_PING_CHECK"
                MessageIds.PACKET_ACK -> "PACKET_ACK"
                else -> "0x${id.toString(16).uppercase()}"
            }
        }
    }
    
    /**
     * Get detailed message statistics for diagnostics.
     * Provides information about which messages have been received and when.
     */
    fun getMessageStatistics(): MessageStatistics {
        return MessageStatistics(
            totalPacketsReceived = totalPacketsReceived.get(),
            totalBytesReceived = totalBytesReceived.get(),
            packetsResent = packetsResentCount.get(),
            messageTypeCounts = messageTypeCounts.mapValues { it.value.get() },
            lastMessageTimes = lastMessageTimes.toMap()
        )
    }
    
    /**
     * Detailed message statistics for diagnostics
     */
    data class MessageStatistics(
        val totalPacketsReceived: Int,
        val totalBytesReceived: Long,
        val packetsResent: Int,
        val messageTypeCounts: Map<String, Int>,
        val lastMessageTimes: Map<String, Long>
    )
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): UDPDiagnostics {
        val pendingPacketInfo = pendingAcks.values.map { packet ->
            PendingPacketInfo(
                seqNum = packet.seqNum,
                retries = packet.retries,
                ageMs = System.currentTimeMillis() - packet.timestamp
            )
        }
        
        return UDPDiagnostics(
            isConnected = isConnected,
            simIP = simIP,
            simPort = simPort,
            circuitCode = circuitCode,
            agentId = agentId,
            sessionId = sessionId,
            sequenceNumber = sequenceNumber.get(),
            pendingAckCount = pendingAcks.size,
            registeredHandlerCount = messageHandlers.size,
            registeredHandlers = getRegisteredHandlerIds(),
            pendingPackets = pendingPacketInfo,
            // DatagramChannel status instead of socket
            socketOpen = datagramChannel?.isOpen ?: false,
            receiveLoopActive = receiveJob?.isActive == true,
            bufferedPacketCount = packetBuffer.size,
            handlersReady = handlersReady.get(),
            processorThreadCount = getEffectiveThreadCount(),
            bufferMemorySize = processingConfig.bufferMemory.displayName,
            batchSize = getEffectiveBatchSize(),
            processingPriority = processingConfig.priority.displayName
        )
    }
    
    /**
     * Diagnostic data class for UDP connection state
     */
    data class UDPDiagnostics(
        val isConnected: Boolean,
        val simIP: String,
        val simPort: Int,
        val circuitCode: Int,
        val agentId: UUID,
        val sessionId: UUID,
        val sequenceNumber: Int,
        val pendingAckCount: Int,
        val registeredHandlerCount: Int,
        val registeredHandlers: List<String>,
        val pendingPackets: List<PendingPacketInfo>,
        val socketOpen: Boolean,
        val receiveLoopActive: Boolean,
        val bufferedPacketCount: Int = 0,
        val handlersReady: Boolean = false,
        val processorThreadCount: Int = 0,
        val bufferMemorySize: String = "256 MB",
        val batchSize: Int = 64,
        val processingPriority: String = "Balanced"
    )
    
    /**
     * Info about a pending packet for diagnostics
     */
    data class PendingPacketInfo(
        val seqNum: Int,
        val retries: Int,
        val ageMs: Long
    )
}

fun interface MessageHandler {
    fun onMessage(messageId: Int, payload: ByteArray)
}

/**
 * Common SL message IDs
 */
object MessageIds {
    // Low frequency (0xFFFFxxxx)
    const val USE_CIRCUIT_CODE: Int = 0xFFFF0003.toInt()
    const val COMPLETE_AGENT_MOVEMENT: Int = 0xFFFF00F9.toInt()
    const val LOGOUT_REQUEST: Int = 0xFFFF00FC.toInt()
    const val CHAT_FROM_SIMULATOR: Int = 0xFFFF00A3.toInt()
    const val IMPROVED_INSTANT_MESSAGE: Int = 0xFFFF00FE.toInt()
    const val REGION_HANDSHAKE: Int = 0xFFFF0094.toInt()
    const val AGENT_MOVEMENT_COMPLETE: Int = 0xFFFF00FA.toInt()
    const val OBJECT_LINK: Int = 0xFFFF0066.toInt()
    const val OBJECT_DELINK: Int = 0xFFFF0067.toInt()
    const val REZ_OBJECT: Int = 0xFFFF0068.toInt()
    const val DEREZ_OBJECT: Int = 0xFFFF0069.toInt()
    const val OBJECT_DELETE: Int = 0xFFFF0089.toInt()
    const val OBJECT_NAME: Int = 0xFFFF008C.toInt()
    const val OBJECT_DESCRIPTION: Int = 0xFFFF008D.toInt()
    const val OBJECT_GRAB: Int = 0xFFFF0117.toInt()
    const val OBJECT_DEGRAB: Int = 0xFFFF0118.toInt()
    const val AGENT_REQUEST_SIT: Int = 0xFFFF00F5.toInt()
    const val AGENT_SIT: Int = 0xFFFF00F7.toInt()
    const val OFFER_FRIENDSHIP: Int = 0xFFFF0039.toInt()
    const val TERMINATE_FRIENDSHIP: Int = 0xFFFF003A.toInt()
    const val GRANT_USER_RIGHTS: Int = 0xFFFF003B.toInt()
    const val FIND_AGENT: Int = 0xFFFF001F.toInt()
    const val START_LURE: Int = 0xFFFF0040.toInt()
    const val TELEPORT_LURE_REQUEST: Int = 0xFFFF0041.toInt()
    const val AVATAR_PROPERTIES_UPDATE: Int = 0xFFFF00A6.toInt()
    const val GROUP_ROLE_DATA_REQUEST: Int = 0xFFFF0176.toInt()
    const val ACTIVATE_GROUP: Int = 0xFFFF0177.toInt()
    const val JOIN_GROUP_REQUEST: Int = 0xFFFF0178.toInt()
    const val LEAVE_GROUP_REQUEST: Int = 0xFFFF0179.toInt()
    const val GROUP_PROFILE_REQUEST: Int = 0xFFFF017A.toInt()
    const val GROUP_MEMBERS_REQUEST: Int = 0xFFFF017B.toInt()
    const val ACCEPT_FRIENDSHIP: Int = 0xFFFF003C.toInt()
    const val DECLINE_FRIENDSHIP: Int = 0xFFFF003D.toInt()
    const val IM_VIA_EMAIL: Int = 0xFFFF00FF.toInt()
    
    // Teleport messages
    const val TELEPORT_LOCATION_REQUEST: Int = 0xFFFF0042.toInt()
    const val TELEPORT_LANDMARK_REQUEST: Int = 0xFFFF0043.toInt()
    const val TELEPORT_HOME_REQUEST: Int = 0xFFFF0044.toInt()
    const val TELEPORT_CANCEL: Int = 0xFFFF0045.toInt()
    const val TELEPORT_LOCAL: Int = 0xFFFF0046.toInt()
    const val TELEPORT_REQUEST: Int = 0xFFFF0047.toInt()
    const val TELEPORT_FINISH: Int = 0xFFFF0048.toInt()
    const val TELEPORT_FAILED: Int = 0xFFFF0049.toInt()
    const val TELEPORT_PROGRESS: Int = 0xFFFF004A.toInt()
    
    // Parcel messages
    const val PARCEL_BUY: Int = 0xFFFF00D1.toInt()
    const val PARCEL_DEED_TO_GROUP: Int = 0xFFFF00D2.toInt()
    const val PARCEL_RELEASE: Int = 0xFFFF00D3.toInt()
    const val PARCEL_PROPERTIES_UPDATE: Int = 0xFFFF00D4.toInt()
    const val PARCEL_RETURN_OBJECTS: Int = 0xFFFF00C9.toInt()
    const val PARCEL_ACCESS_LIST_UPDATE: Int = 0xFFFF00CF.toInt()
    const val PARCEL_DISABLE_OBJECTS: Int = 0xFFFF00C9.toInt()
    
    // Medium frequency (0xFFxx)
    // Format: 0xFF prefix followed by message byte, decoded as (byte | 65280)
    // Per Lumiya's DecodeMessageID: b2 | 65280
    const val INSTANT_MESSAGE: Int = 65355        // 0xFF4B = (75 | 65280) = 65355
    const val OBJECT_SELECT: Int = 65289          // 0xFF09 = (9 | 65280) = 65289
    const val OBJECT_DESELECT: Int = 65290        // 0xFF0A = (10 | 65280) = 65290
    const val MULTIPLE_OBJECT_UPDATE: Int = 65282 // 0xFF02 = per Lumiya SLMessageFactory
    const val SOUND_TRIGGER: Int = 65309          // 0xFF1D = (29 | 65280) = 65309
    
    // Low frequency chat message (0xFFFFxxxx)
    // ChatFromViewer is Low 80 = 0xFFFF0050 per message_template.msg
    const val CHAT_FROM_VIEWER: Int = 0xFFFF0050.toInt()
    
    // High frequency (0x00 - 0xFE) - single byte, returned as-is
    // These match Lumiya's SLMessageFactory exactly
    const val START_PING_CHECK: Int = 1          // 0x01
    const val COMPLETE_PING_CHECK: Int = 2       // 0x02
    const val AGENT_UPDATE: Int = 4              // 0x04 - High frequency per Lumiya
    const val AGENT_ANIMATION: Int = 5           // 0x05 - High frequency per Lumiya
    const val OBJECT_UPDATE: Int = 12            // 0x0C
    const val OBJECT_UPDATE_COMPRESSED: Int = 13 // 0x0D
    const val OBJECT_UPDATE_CACHED: Int = 14     // 0x0E
    const val IMPROVED_TERSE_OBJECT_UPDATE: Int = 15 // 0x0F
    const val KILL_OBJECT: Int = 16              // 0x10 - High frequency per Lumiya
    const val AVATAR_ANIMATION: Int = 20         // 0x14
    
    // Medium frequency (0xFF00 - 0xFFFE) - 0xFF prefix + byte, returned as (byte | 65280)
    // These match Lumiya's SLMessageFactory (e.g., CoarseLocationUpdate = 65286)
    const val COARSE_LOCATION_UPDATE: Int = 65286 // 0xFF06 = (6 | 65280)
    
    // Region/Connection messages
    const val REGION_HANDSHAKE_REPLY: Int = 0xFFFF0095.toInt()
    const val AGENT_THROTTLE: Int = 0xFFFF0099.toInt()
    
    // PacketAck - Lumiya treats this as signed byte 0xFB = -5
    // Per SLMessageFactory: case -5: return new PacketAck();
    const val PACKET_ACK: Int = -5
    
    // Inventory messages
    const val MOVE_INVENTORY_ITEM: Int = 0xFFFF0109.toInt()
    const val COPY_INVENTORY_ITEM: Int = 0xFFFF010A.toInt()
    const val REMOVE_INVENTORY_ITEM: Int = 0xFFFF010B.toInt()
    const val CREATE_INVENTORY_FOLDER: Int = 0xFFFF010C.toInt()
    const val UPDATE_INVENTORY_FOLDER: Int = 0xFFFF010D.toInt()
    const val MOVE_INVENTORY_FOLDER: Int = 0xFFFF010E.toInt()
    const val REMOVE_INVENTORY_FOLDER: Int = 0xFFFF010F.toInt()
    const val UPDATE_INVENTORY_ITEM: Int = 0xFFFF0107.toInt()
    const val FETCH_INVENTORY_REPLY: Int = 0xFFFF0120.toInt()
    const val PURGE_INVENTORY_DESCENDENTS: Int = 0xFFFF0110.toInt()
    
    // Attachment messages
    const val REZ_SINGLE_ATTACHMENT_FROM_INV: Int = 0xFFFF0184.toInt()
    const val REZ_MULTIPLE_ATTACHMENTS_FROM_INV: Int = 0xFFFF0185.toInt()
    const val DETACH_ATTACHMENT_INTO_INV: Int = 0xFFFF0186.toInt()
    
    // Agent appearance
    const val AGENT_SET_APPEARANCE: Int = 0xFFFF0054.toInt()
    const val AGENT_WEARABLES_UPDATE: Int = 0xFFFF00A1.toInt()
    const val AGENT_IS_NOW_WEARING: Int = 0xFFFF00A2.toInt()
    
    // Estate/Region management
    const val ESTATE_OWNER_MESSAGE: Int = 0xFFFF00B0.toInt()
    const val FREEZE_USER: Int = 0xFFFF00B1.toInt()
    const val EJECT_USER: Int = 0xFFFF00B2.toInt()
    const val ESTATE_COVENANT_REQUEST: Int = 0xFFFF00B3.toInt()
    const val ESTATE_COVENANT_REPLY: Int = 0xFFFF00B4.toInt()
}
