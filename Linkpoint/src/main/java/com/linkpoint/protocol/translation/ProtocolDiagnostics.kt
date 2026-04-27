package com.linkpoint.protocol.translation

import android.util.Log
import com.linkpoint.utils.InitializationTracker
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Comprehensive protocol diagnostics for debugging Linkpoint ↔ Second Life communication.
 * 
 * This provides detailed logging and statistics collection to help diagnose:
 * - Capability initialization failures
 * - UDP packet issues (sending but not receiving)
 * - Message ID decoding problems
 * - Protocol version mismatches
 * 
 * Based on the diagnostic needs identified in the original debug report:
 * - "PACKETS SENT BUT NONE RECEIVED!"
 * - "Seed capability returned empty response"
 * - "Unknown(0x-FE7D)" message types
 */
object ProtocolDiagnostics {
    
    private const val TAG = "ProtocolDiag"
    private const val EARLY_WARNING_GRACE_MS = 15_000L
    
    // Packet statistics
    private val packetsSent = AtomicLong(0)
    private val packetsReceived = AtomicLong(0)
    private val bytesSent = AtomicLong(0)
    private val bytesReceived = AtomicLong(0)
    private val parseErrors = AtomicInteger(0)
    private val unknownMessages = AtomicInteger(0)
    
    // Message type tracking
    private val messageTypeCounts = ConcurrentHashMap<Int, AtomicInteger>()
    private val unknownMessageIds = ConcurrentHashMap<Int, AtomicInteger>()
    
    // Recent packet history (using ArrayDeque for O(1) removeFirst operations)
    private const val MAX_HISTORY_SIZE = 100
    private val packetHistory = java.util.ArrayDeque<PacketRecord>(MAX_HISTORY_SIZE + 10)
    private val historyLock = Any()
    
    // Timing
    private var sessionStartTime: Long = 0
    private var lastPacketSentTime: Long = 0
    private var lastPacketReceivedTime: Long = 0
    
    // Connection state
    @Volatile
    var isConnected: Boolean = false
        private set
    
    @Volatile
    var connectionPhase: String = "DISCONNECTED"
        private set

    private fun formatUdpConnectedWarningContext(): Pair<Boolean, String> {
        val elapsedMs = InitializationTracker.getElapsedSincePhase(InitializationTracker.Phase.UDP_CONNECTED)
        val timing = if (elapsedMs != null) "${formatDuration(elapsedMs)} since UDP_CONNECTED" else "UDP_CONNECTED time unavailable"
        val provisional = elapsedMs != null && elapsedMs < EARLY_WARNING_GRACE_MS
        return provisional to timing
    }
    
    /**
     * Record for a single packet event.
     */
    data class PacketRecord(
        val timestamp: Long,
        val direction: PacketDirection,
        val messageId: Int,
        val messageName: String,
        val size: Int,
        val sequenceNumber: Int,
        val hexPreview: String? = null,
        val error: String? = null
    ) {
        val age: Long
            get() = System.currentTimeMillis() - timestamp
        
        fun formatAge(): String {
            val ageMs = age
            return when {
                ageMs < 1000 -> "${ageMs}ms"
                ageMs < 60000 -> String.format("%.1fs", ageMs / 1000.0)
                else -> String.format("%.1fm", ageMs / 60000.0)
            }
        }
    }
    
    enum class PacketDirection {
        SENT, RECEIVED
    }
    
    /**
     * Start a new diagnostic session.
     */
    fun startSession() {
        resetCounters()
        sessionStartTime = System.currentTimeMillis()
        connectionPhase = "INITIALIZING"
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ PROTOCOL DIAGNOSTICS SESSION STARTED")
        Log.i(TAG, "║ Time: ${formatTimestamp(sessionStartTime)}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    /**
     * Reset all counters.
     */
    fun resetCounters() {
        packetsSent.set(0)
        packetsReceived.set(0)
        bytesSent.set(0)
        bytesReceived.set(0)
        parseErrors.set(0)
        unknownMessages.set(0)
        messageTypeCounts.clear()
        unknownMessageIds.clear()
        synchronized(historyLock) {
            packetHistory.clear()
        }
        lastPacketSentTime = 0
        lastPacketReceivedTime = 0
    }
    
    /**
     * Format raw bytes as a hex string preview.
     * 
     * @param bytes The raw bytes
     * @param maxBytes Maximum number of bytes to include (default 24)
     * @return Hex string like "00 01 02 03"
     */
    private fun formatHexPreview(bytes: ByteArray?, maxBytes: Int = 24): String? {
        if (bytes == null || bytes.isEmpty()) return null
        return bytes.take(maxBytes).joinToString(" ") { "%02X".format(it) }
    }
    
    /**
     * Update connection state.
     */
    fun setConnectionState(connected: Boolean, phase: String) {
        isConnected = connected
        connectionPhase = phase
        Log.d(TAG, "Connection state: $phase (connected=$connected)")
    }
    
    /**
     * Record a packet being sent.
     */
    fun recordPacketSent(
        messageId: Int,
        size: Int,
        sequenceNumber: Int,
        rawBytes: ByteArray? = null
    ) {
        packetsSent.incrementAndGet()
        bytesSent.addAndGet(size.toLong())
        lastPacketSentTime = System.currentTimeMillis()
        
        incrementMessageCount(messageId)
        
        val record = PacketRecord(
            timestamp = lastPacketSentTime,
            direction = PacketDirection.SENT,
            messageId = messageId,
            messageName = MessageTranslation.KnownMessages.getName(messageId),
            size = size,
            sequenceNumber = sequenceNumber,
            hexPreview = formatHexPreview(rawBytes)
        )
        
        addToHistory(record)
        
        if (LinkpointTranslationLayer.config.verboseLogging) {
            Log.v(TAG, "→ ${record.messageName} (seq=$sequenceNumber, ${size}B)")
        }
    }
    
    /**
     * Record a packet being received.
     */
    fun recordPacketReceived(
        messageId: Int,
        size: Int,
        sequenceNumber: Int,
        rawBytes: ByteArray? = null
    ) {
        packetsReceived.incrementAndGet()
        bytesReceived.addAndGet(size.toLong())
        lastPacketReceivedTime = System.currentTimeMillis()
        
        incrementMessageCount(messageId)
        
        val record = PacketRecord(
            timestamp = lastPacketReceivedTime,
            direction = PacketDirection.RECEIVED,
            messageId = messageId,
            messageName = MessageTranslation.KnownMessages.getName(messageId),
            size = size,
            sequenceNumber = sequenceNumber,
            hexPreview = formatHexPreview(rawBytes)
        )
        
        addToHistory(record)
        
        if (LinkpointTranslationLayer.config.verboseLogging) {
            Log.v(TAG, "← ${record.messageName} (seq=$sequenceNumber, ${size}B)")
        }
    }
    
    /**
     * Record an unknown message ID.
     */
    fun recordUnknownMessage(messageId: Int, rawBytes: ByteArray? = null) {
        unknownMessages.incrementAndGet()
        unknownMessageIds.getOrPut(messageId) { AtomicInteger(0) }.incrementAndGet()
        
        Log.w(TAG, "Unknown message ID: $messageId (0x${messageId.toString(16).uppercase()})")
        
        // Log additional context for debugging
        if (rawBytes != null && rawBytes.size >= 4) {
            val hexPreview = formatHexPreview(rawBytes, 16)
            Log.d(TAG, "  Raw header: $hexPreview")
            
            // Try Linkpoint decoding for comparison
            val protocolDecoded = MessageTranslation.decodeMessageId(rawBytes, 0)
            if (protocolDecoded != null) {
                val (decodedId, _) = protocolDecoded
                Log.d(TAG, "  SL protocol decode: $decodedId (0x${decodedId.toString(16).uppercase()})")
                Log.d(TAG, "  SL protocol name: ${MessageTranslation.KnownMessages.getName(decodedId)}")
            }
        }
    }
    
    /**
     * Record a parse error.
     */
    fun recordParseError(context: String, error: Throwable? = null) {
        parseErrors.incrementAndGet()
        Log.e(TAG, "Parse error: $context", error)
    }
    
    private fun incrementMessageCount(messageId: Int) {
        messageTypeCounts.getOrPut(messageId) { AtomicInteger(0) }.incrementAndGet()
    }
    
    private fun addToHistory(record: PacketRecord) {
        synchronized(historyLock) {
            packetHistory.addLast(record)
            while (packetHistory.size > MAX_HISTORY_SIZE) {
                packetHistory.removeFirst() // O(1) operation with ArrayDeque
            }
        }
    }
    
    /**
     * Generate a comprehensive diagnostic report.
     */
    fun generateReport(): String {
        val sb = StringBuilder()
        val now = System.currentTimeMillis()
        
        sb.appendLine("╔══════════════════════════════════════════════════════════════════╗")
        sb.appendLine("║               PROTOCOL DIAGNOSTICS REPORT                         ║")
        sb.appendLine("╚══════════════════════════════════════════════════════════════════╝")
        sb.appendLine()
        
        // Session info
        sb.appendLine("┌──────────────────────────────────────────────────────────────────┐")
        sb.appendLine("│ SESSION INFORMATION                                               │")
        sb.appendLine("└──────────────────────────────────────────────────────────────────┘")
        sb.appendLine("Report Time: ${formatTimestamp(now)}")
        sb.appendLine("Session Start: ${if (sessionStartTime > 0) formatTimestamp(sessionStartTime) else "N/A"}")
        sb.appendLine("Session Duration: ${formatDuration(now - sessionStartTime)}")
        sb.appendLine("Connection Phase: $connectionPhase")
        sb.appendLine("Is Connected: $isConnected")
        sb.appendLine()
        
        // Packet statistics
        sb.appendLine("┌──────────────────────────────────────────────────────────────────┐")
        sb.appendLine("│ PACKET STATISTICS                                                 │")
        sb.appendLine("└──────────────────────────────────────────────────────────────────┘")
        sb.appendLine("Packets Sent: ${packetsSent.get()}")
        sb.appendLine("Packets Received: ${packetsReceived.get()}")
        sb.appendLine("Bytes Sent: ${formatBytes(bytesSent.get())}")
        sb.appendLine("Bytes Received: ${formatBytes(bytesReceived.get())}")
        sb.appendLine("Last Sent: ${formatTimeAgo(lastPacketSentTime)}")
        sb.appendLine("Last Received: ${formatTimeAgo(lastPacketReceivedTime)}")
        sb.appendLine()
        
        // Check for common issues
        if (packetsSent.get() > 0 && packetsReceived.get() == 0L) {
            val (provisional, timing) = formatUdpConnectedWarningContext()
            if (provisional) {
                sb.appendLine("⚠️ PROVISIONAL: PACKETS SENT BUT NONE RECEIVED ($timing)")
            } else {
                sb.appendLine("⚠️ PACKETS SENT BUT NONE RECEIVED ($timing)")
            }
            sb.appendLine("   Possible causes:")
            sb.appendLine("   - Firewall blocking UDP")
            sb.appendLine("   - NAT traversal issue")
            sb.appendLine("   - Wrong port/address")
            sb.appendLine("   - Simulator not responding")
            sb.appendLine()
        }
        
        // Error statistics
        sb.appendLine("┌──────────────────────────────────────────────────────────────────┐")
        sb.appendLine("│ ERROR STATISTICS                                                  │")
        sb.appendLine("└──────────────────────────────────────────────────────────────────┘")
        sb.appendLine("Parse Errors: ${parseErrors.get()}")
        sb.appendLine("Unknown Messages: ${unknownMessages.get()}")
        
        if (unknownMessageIds.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("Unknown Message IDs:")
            unknownMessageIds.entries
                .sortedByDescending { it.value.get() }
                .take(10)
                .forEach { (id, count) ->
                    val hex = id.toString(16).uppercase()
                    sb.appendLine("  0x$hex ($id): ${count.get()} occurrences")
                }
        }
        sb.appendLine()
        
        // Message type breakdown
        sb.appendLine("┌──────────────────────────────────────────────────────────────────┐")
        sb.appendLine("│ MESSAGE TYPE BREAKDOWN (top 15)                                   │")
        sb.appendLine("└──────────────────────────────────────────────────────────────────┘")
        messageTypeCounts.entries
            .sortedByDescending { it.value.get() }
            .take(15)
            .forEach { (id, count) ->
                val name = MessageTranslation.KnownMessages.getName(id)
                sb.appendLine("  $name: ${count.get()}")
            }
        sb.appendLine()
        
        // Recent packet history
        sb.appendLine("┌──────────────────────────────────────────────────────────────────┐")
        sb.appendLine("│ RECENT PACKET HISTORY (last 20)                                   │")
        sb.appendLine("└──────────────────────────────────────────────────────────────────┘")
        synchronized(historyLock) {
            packetHistory.toList().takeLast(20).forEach { record ->
                val arrow = if (record.direction == PacketDirection.SENT) "→" else "←"
                sb.appendLine("  [${record.formatAge()} ago] $arrow ${record.messageName} " +
                    "(seq=${record.sequenceNumber}, ${record.size}B)")
                record.hexPreview?.let { hex ->
                    sb.appendLine("     Hex: $hex")
                }
            }
        }
        
        sb.appendLine()
        sb.appendLine("═══════════════════════════════════════════════════════════════════")
        sb.appendLine("End of Protocol Diagnostics Report")
        sb.appendLine("═══════════════════════════════════════════════════════════════════")
        
        return sb.toString()
    }
    
    /**
     * Log the diagnostic report to the console.
     */
    fun logReport() {
        val report = generateReport()
        report.lines().forEach { line ->
            Log.i(TAG, line)
        }
    }
    
    /**
     * Get summary diagnostics data.
     */
    fun getSummary(): DiagnosticsSummary {
        return DiagnosticsSummary(
            sessionDurationMs = if (sessionStartTime > 0) System.currentTimeMillis() - sessionStartTime else 0,
            connectionPhase = connectionPhase,
            isConnected = isConnected,
            packetsSent = packetsSent.get(),
            packetsReceived = packetsReceived.get(),
            bytesSent = bytesSent.get(),
            bytesReceived = bytesReceived.get(),
            parseErrors = parseErrors.get(),
            unknownMessages = unknownMessages.get(),
            lastPacketSentMs = lastPacketSentTime,
            lastPacketReceivedMs = lastPacketReceivedTime,
            messageTypeCount = messageTypeCounts.size,
            topMessageTypes = messageTypeCounts.entries
                .sortedByDescending { it.value.get() }
                .take(5)
                .associate { MessageTranslation.KnownMessages.getName(it.key) to it.value.get() }
        )
    }
    
    /**
     * Summary data class for diagnostics.
     */
    data class DiagnosticsSummary(
        val sessionDurationMs: Long,
        val connectionPhase: String,
        val isConnected: Boolean,
        val packetsSent: Long,
        val packetsReceived: Long,
        val bytesSent: Long,
        val bytesReceived: Long,
        val parseErrors: Int,
        val unknownMessages: Int,
        val lastPacketSentMs: Long,
        val lastPacketReceivedMs: Long,
        val messageTypeCount: Int,
        val topMessageTypes: Map<String, Int>
    )
    
    // Formatting helpers
    
    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(Date(timestamp))
    }
    
    private fun formatDuration(durationMs: Long): String {
        if (durationMs < 0) return "N/A"
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m ${seconds % 60}s"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
    
    private fun formatTimeAgo(timestamp: Long): String {
        if (timestamp == 0L) return "Never"
        val ago = System.currentTimeMillis() - timestamp
        return when {
            ago < 1000 -> "${ago}ms ago"
            ago < 60000 -> String.format("%.1fs ago", ago / 1000.0)
            ago < 3600000 -> String.format("%.1fm ago", ago / 60000.0)
            else -> String.format("%.1fh ago", ago / 3600000.0)
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
            else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
