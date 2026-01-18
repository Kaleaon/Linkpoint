package com.linkpoint.protocol.messages

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Enhanced Packet Logger for comprehensive UDP protocol debugging.
 * 
 * Provides detailed logging of:
 * - All sent/received UDP packets with full message decoding
 * - Handler registration and dispatch events
 * - ACK tracking and resend statistics
 * - Message-specific timing and frequency analysis
 * - Hex dumps for packet inspection
 * 
 * This addresses the debugging needs identified in the debug report:
 * - "PACKETS SENT BUT NONE RECEIVED!"
 * - Tracking which handlers are registered
 * - Understanding message flow timing
 */
object EnhancedPacketLogger {
    
    private const val TAG = "PacketLogger"
    
    // Message names that should not count as handler misses
    // These are internal protocol messages or handled specially
    private val EXPECTED_UNHANDLED_MESSAGES = setOf(
        "PacketAck",
        "PacketAckResponse", 
        "StartPingCheck",
        "CompletePingCheck",
        "Unknown" // Unknown messages are expected and tracked separately
    )
    
    // Configuration
    @Volatile
    var isEnabled: Boolean = true
    
    @Volatile
    var verboseMode: Boolean = false
    
    @Volatile
    var logHexDumps: Boolean = true
    
    @Volatile
    var maxHexDumpBytes: Int = 64
    
    // Packet history for debug reports
    private const val MAX_PACKET_HISTORY = 200
    private val packetHistory = ConcurrentLinkedQueue<PacketLogEntry>()
    
    // Statistics
    private val packetsSent = AtomicLong(0)
    private val packetsReceived = AtomicLong(0)
    private val bytesSent = AtomicLong(0)
    private val bytesReceived = AtomicLong(0)
    private val acksSent = AtomicLong(0)
    private val acksReceived = AtomicLong(0)
    private val resendCount = AtomicLong(0)
    private val parseErrors = AtomicInteger(0)
    private val handlerMisses = AtomicInteger(0)
    
    // Message type tracking
    private val sentMessageCounts = ConcurrentHashMap<String, AtomicLong>()
    private val receivedMessageCounts = ConcurrentHashMap<String, AtomicLong>()
    private val lastMessageTimes = ConcurrentHashMap<String, Long>()
    
    // Handler tracking
    private val registeredHandlers = ConcurrentHashMap<Int, String>()
    private val handlerDispatchCounts = ConcurrentHashMap<String, AtomicLong>()
    
    // Session tracking
    @Volatile
    private var sessionStartTime: Long = 0
    
    @Volatile
    private var lastPacketSentTime: Long = 0
    
    @Volatile
    private var lastPacketReceivedTime: Long = 0
    
    // Date formatter
    private val timestampFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    
    /**
     * Log entry for a packet event.
     */
    data class PacketLogEntry(
        val timestamp: Long,
        val direction: Direction,
        val messageId: Int,
        val messageName: String,
        val sequenceNumber: Int,
        val size: Int,
        val flags: PacketFlags,
        val hexPreview: String?,
        val handlerDispatched: Boolean = false,
        val error: String? = null,
        val processingTimeMs: Long? = null
    ) {
        enum class Direction {
            SENT, RECEIVED, RESENT, ACK_SENT, ACK_RECEIVED
        }
        
        fun formatForDisplay(): String {
            val arrow = when (direction) {
                Direction.SENT -> "→"
                Direction.RECEIVED -> "←"
                Direction.RESENT -> "⟳"
                Direction.ACK_SENT -> "✓→"
                Direction.ACK_RECEIVED -> "✓←"
            }
            val flagStr = flags.toShortString()
            val handlerStr = if (direction == Direction.RECEIVED && !handlerDispatched) " [NO HANDLER]" else ""
            val errorStr = error?.let { " ERROR: $it" } ?: ""
            val timeStr = timestampFormat.format(Date(timestamp))
            
            return "[$timeStr] $arrow $messageName (seq=$sequenceNumber, ${size}B) $flagStr$handlerStr$errorStr"
        }
    }
    
    /**
     * Packet flags for debugging.
     */
    data class PacketFlags(
        val reliable: Boolean,
        val resent: Boolean,
        val zerocoded: Boolean,
        val hasAcks: Boolean
    ) {
        fun toShortString(): String {
            return buildString {
                if (reliable) append("R")
                if (resent) append("S")
                if (zerocoded) append("Z")
                if (hasAcks) append("A")
            }.ifEmpty { "-" }
        }
    }
    
    /**
     * Start a new logging session.
     */
    fun startSession() {
        resetStatistics()
        sessionStartTime = System.currentTimeMillis()
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ ENHANCED PACKET LOGGING SESSION STARTED")
        Log.i(TAG, "║ Time: ${formatTimestamp(sessionStartTime)}")
        Log.i(TAG, "║ Verbose: $verboseMode, HexDumps: $logHexDumps")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    /**
     * Reset all statistics.
     */
    fun resetStatistics() {
        packetsSent.set(0)
        packetsReceived.set(0)
        bytesSent.set(0)
        bytesReceived.set(0)
        acksSent.set(0)
        acksReceived.set(0)
        resendCount.set(0)
        parseErrors.set(0)
        handlerMisses.set(0)
        sentMessageCounts.clear()
        receivedMessageCounts.clear()
        lastMessageTimes.clear()
        handlerDispatchCounts.clear()
        packetHistory.clear()
        lastPacketSentTime = 0
        lastPacketReceivedTime = 0
    }
    
    /**
     * Log a packet being sent.
     */
    fun logPacketSent(
        messageId: Int,
        messageName: String,
        sequenceNumber: Int,
        data: ByteArray,
        flags: PacketFlags
    ) {
        if (!isEnabled) return
        
        packetsSent.incrementAndGet()
        bytesSent.addAndGet(data.size.toLong())
        lastPacketSentTime = System.currentTimeMillis()
        
        sentMessageCounts.getOrPut(messageName) { AtomicLong(0) }.incrementAndGet()
        lastMessageTimes[messageName] = lastPacketSentTime
        
        val hexPreview = if (logHexDumps) formatHexPreview(data) else null
        
        val entry = PacketLogEntry(
            timestamp = lastPacketSentTime,
            direction = PacketLogEntry.Direction.SENT,
            messageId = messageId,
            messageName = messageName,
            sequenceNumber = sequenceNumber,
            size = data.size,
            flags = flags,
            hexPreview = hexPreview
        )
        
        addToHistory(entry)
        
        if (verboseMode) {
            Log.v(TAG, entry.formatForDisplay())
            hexPreview?.let { Log.v(TAG, "    Hex: $it") }
        } else {
            Log.d(TAG, "→ $messageName (seq=$sequenceNumber, ${data.size}B)")
        }
    }
    
    /**
     * Log a packet being received.
     */
    fun logPacketReceived(
        messageId: Int,
        messageName: String,
        sequenceNumber: Int,
        data: ByteArray,
        flags: PacketFlags,
        handlerFound: Boolean,
        processingTimeMs: Long? = null
    ) {
        if (!isEnabled) return
        
        packetsReceived.incrementAndGet()
        bytesReceived.addAndGet(data.size.toLong())
        lastPacketReceivedTime = System.currentTimeMillis()
        
        receivedMessageCounts.getOrPut(messageName) { AtomicLong(0) }.incrementAndGet()
        lastMessageTimes[messageName] = lastPacketReceivedTime
        
        // Check if this is an expected unhandled message type
        val isExpectedUnhandled = EXPECTED_UNHANDLED_MESSAGES.any { expectedName ->
            messageName.contains(expectedName, ignoreCase = true)
        }
        
        if (!handlerFound && !isExpectedUnhandled) {
            handlerMisses.incrementAndGet()
        }
        
        val hexPreview = if (logHexDumps) formatHexPreview(data) else null
        
        val entry = PacketLogEntry(
            timestamp = lastPacketReceivedTime,
            direction = PacketLogEntry.Direction.RECEIVED,
            messageId = messageId,
            messageName = messageName,
            sequenceNumber = sequenceNumber,
            size = data.size,
            flags = flags,
            hexPreview = hexPreview,
            handlerDispatched = handlerFound,
            processingTimeMs = processingTimeMs
        )
        
        addToHistory(entry)
        
        val handlerInfo = if (!handlerFound) " [NO HANDLER]" else ""
        val procTimeInfo = processingTimeMs?.let { " (${it}ms)" } ?: ""
        
        if (verboseMode) {
            Log.v(TAG, entry.formatForDisplay())
            hexPreview?.let { Log.v(TAG, "    Hex: $it") }
        } else {
            Log.d(TAG, "← $messageName (seq=$sequenceNumber, ${data.size}B)$handlerInfo$procTimeInfo")
        }
    }
    
    /**
     * Log a packet resend.
     */
    fun logPacketResent(
        messageId: Int,
        messageName: String,
        sequenceNumber: Int,
        retryCount: Int,
        ageMs: Long
    ) {
        if (!isEnabled) return
        
        resendCount.incrementAndGet()
        
        val entry = PacketLogEntry(
            timestamp = System.currentTimeMillis(),
            direction = PacketLogEntry.Direction.RESENT,
            messageId = messageId,
            messageName = messageName,
            sequenceNumber = sequenceNumber,
            size = 0,
            flags = PacketFlags(reliable = true, resent = true, zerocoded = false, hasAcks = false),
            hexPreview = null,
            error = "Retry #$retryCount after ${ageMs}ms"
        )
        
        addToHistory(entry)
        
        Log.w(TAG, "⟳ RESEND $messageName (seq=$sequenceNumber) retry #$retryCount, age=${ageMs}ms")
    }
    
    /**
     * Log ACK sent.
     */
    fun logAckSent(sequenceNumber: Int) {
        if (!isEnabled) return
        
        acksSent.incrementAndGet()
        
        if (verboseMode) {
            Log.v(TAG, "✓→ ACK sent for seq=$sequenceNumber")
        }
    }
    
    /**
     * Log ACK received.
     */
    fun logAckReceived(sequenceNumber: Int, messageName: String?) {
        if (!isEnabled) return
        
        acksReceived.incrementAndGet()
        
        if (verboseMode) {
            val msgInfo = messageName?.let { " ($it)" } ?: ""
            Log.v(TAG, "✓← ACK received for seq=$sequenceNumber$msgInfo")
        }
    }
    
    /**
     * Log handler registration.
     */
    fun logHandlerRegistered(messageId: Int, messageName: String) {
        registeredHandlers[messageId] = messageName
        Log.d(TAG, "📝 Handler registered: $messageName (0x${messageId.toString(16).uppercase()})")
    }
    
    /**
     * Log handler dispatch.
     */
    fun logHandlerDispatched(messageName: String, payloadSize: Int, processingTimeMs: Long) {
        if (!isEnabled) return
        
        handlerDispatchCounts.getOrPut(messageName) { AtomicLong(0) }.incrementAndGet()
        
        if (verboseMode) {
            Log.v(TAG, "📨 Handler dispatched: $messageName (${payloadSize}B, ${processingTimeMs}ms)")
        }
    }
    
    /**
     * Log parse error.
     */
    fun logParseError(context: String, error: Throwable? = null) {
        parseErrors.incrementAndGet()
        Log.e(TAG, "❌ Parse error: $context", error)
        
        val entry = PacketLogEntry(
            timestamp = System.currentTimeMillis(),
            direction = PacketLogEntry.Direction.RECEIVED,
            messageId = -1,
            messageName = "PARSE_ERROR",
            sequenceNumber = -1,
            size = 0,
            flags = PacketFlags(false, false, false, false),
            hexPreview = null,
            error = "$context: ${error?.message ?: "Unknown"}"
        )
        
        addToHistory(entry)
    }
    
    /**
     * Log critical message.
     */
    fun logCriticalMessage(messageName: String, details: String) {
        Log.i(TAG, "⭐ CRITICAL: $messageName - $details")
    }
    
    private fun addToHistory(entry: PacketLogEntry) {
        packetHistory.offer(entry)
        while (packetHistory.size > MAX_PACKET_HISTORY) {
            packetHistory.poll()
        }
    }
    
    private fun formatHexPreview(data: ByteArray): String {
        return data.take(maxHexDumpBytes).joinToString(" ") { "%02X".format(it) }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
    }
    
    private fun formatDuration(durationMs: Long): String {
        return when {
            durationMs < 1000 -> "${durationMs}ms"
            durationMs < 60000 -> String.format(Locale.US, "%.1fs", durationMs / 1000.0)
            durationMs < 3600000 -> String.format(Locale.US, "%.1fm", durationMs / 60000.0)
            else -> String.format(Locale.US, "%.1fh", durationMs / 3600000.0)
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.US, "%.2f KB", bytes / 1024.0)
            else -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
    
    /**
     * Get statistics summary.
     */
    fun getStatistics(): PacketStatistics {
        val now = System.currentTimeMillis()
        return PacketStatistics(
            sessionDurationMs = if (sessionStartTime > 0) now - sessionStartTime else 0,
            packetsSent = packetsSent.get(),
            packetsReceived = packetsReceived.get(),
            bytesSent = bytesSent.get(),
            bytesReceived = bytesReceived.get(),
            acksSent = acksSent.get(),
            acksReceived = acksReceived.get(),
            resendCount = resendCount.get(),
            parseErrors = parseErrors.get(),
            handlerMisses = handlerMisses.get(),
            lastPacketSentMs = if (lastPacketSentTime > 0) now - lastPacketSentTime else -1,
            lastPacketReceivedMs = if (lastPacketReceivedTime > 0) now - lastPacketReceivedTime else -1,
            uniqueMessageTypesSent = sentMessageCounts.size,
            uniqueMessageTypesReceived = receivedMessageCounts.size,
            registeredHandlerCount = registeredHandlers.size
        )
    }
    
    /**
     * Get packet history for debug reports.
     */
    fun getPacketHistory(count: Int = 50): List<PacketLogEntry> {
        return packetHistory.toList().takeLast(count)
    }
    
    /**
     * Get registered handlers.
     */
    fun getRegisteredHandlers(): Map<Int, String> {
        return registeredHandlers.toMap()
    }
    
    /**
     * Get sent message breakdown.
     */
    fun getSentMessageBreakdown(limit: Int = 15): List<Pair<String, Long>> {
        return sentMessageCounts.entries
            .sortedByDescending { it.value.get() }
            .take(limit)
            .map { it.key to it.value.get() }
    }
    
    /**
     * Get received message breakdown.
     */
    fun getReceivedMessageBreakdown(limit: Int = 15): List<Pair<String, Long>> {
        return receivedMessageCounts.entries
            .sortedByDescending { it.value.get() }
            .take(limit)
            .map { it.key to it.value.get() }
    }
    
    /**
     * Generate a comprehensive diagnostic report.
     */
    fun generateReport(): String {
        val stats = getStatistics()
        val now = System.currentTimeMillis()
        
        return buildString {
            appendLine("╔══════════════════════════════════════════════════════════════════╗")
            appendLine("║               ENHANCED PACKET LOGGER REPORT                       ║")
            appendLine("╚══════════════════════════════════════════════════════════════════╝")
            appendLine()
            
            // Session info
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ SESSION INFORMATION                                               │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine("Report Time: ${formatTimestamp(now)}")
            appendLine("Session Start: ${if (sessionStartTime > 0) formatTimestamp(sessionStartTime) else "N/A"}")
            appendLine("Session Duration: ${formatDuration(stats.sessionDurationMs)}")
            appendLine("Logging Enabled: $isEnabled")
            appendLine("Verbose Mode: $verboseMode")
            appendLine()
            
            // Packet statistics
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ PACKET STATISTICS                                                 │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine("Packets Sent: ${stats.packetsSent}")
            appendLine("Packets Received: ${stats.packetsReceived}")
            appendLine("Bytes Sent: ${formatBytes(stats.bytesSent)}")
            appendLine("Bytes Received: ${formatBytes(stats.bytesReceived)}")
            appendLine("ACKs Sent: ${stats.acksSent}")
            appendLine("ACKs Received: ${stats.acksReceived}")
            appendLine("Resends: ${stats.resendCount}")
            appendLine("Parse Errors: ${stats.parseErrors}")
            appendLine("Handler Misses: ${stats.handlerMisses}")
            appendLine()
            
            if (stats.lastPacketSentMs >= 0) {
                appendLine("Last Packet Sent: ${formatDuration(stats.lastPacketSentMs)} ago")
            } else {
                appendLine("Last Packet Sent: Never")
            }
            
            if (stats.lastPacketReceivedMs >= 0) {
                appendLine("Last Packet Received: ${formatDuration(stats.lastPacketReceivedMs)} ago")
            } else {
                appendLine("Last Packet Received: Never ⚠️")
            }
            appendLine()
            
            // Warning checks
            if (stats.packetsSent > 0 && stats.packetsReceived == 0L) {
                appendLine("⚠️ PACKETS SENT BUT NONE RECEIVED!")
                appendLine("   Possible causes:")
                appendLine("   - Firewall blocking UDP")
                appendLine("   - NAT traversal issue")
                appendLine("   - Wrong port/address")
                appendLine("   - Simulator not responding")
                appendLine()
            }
            
            if (stats.handlerMisses > 0) {
                appendLine("⚠️ ${stats.handlerMisses} messages had no registered handler!")
                appendLine()
            }
            
            // Registered handlers
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ REGISTERED HANDLERS (${stats.registeredHandlerCount})                                     │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            registeredHandlers.entries.forEach { (id, name) ->
                appendLine("  - $name (0x${id.toString(16).uppercase()})")
            }
            if (registeredHandlers.isEmpty()) {
                appendLine("  ⚠️ No handlers registered!")
            }
            appendLine()
            
            // Sent message breakdown
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ SENT MESSAGE TYPES (top 15)                                       │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            getSentMessageBreakdown().forEach { (name, count) ->
                appendLine("  $name: $count")
            }
            if (sentMessageCounts.isEmpty()) {
                appendLine("  No messages sent")
            }
            appendLine()
            
            // Received message breakdown
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ RECEIVED MESSAGE TYPES (top 15)                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            getReceivedMessageBreakdown().forEach { (name, count) ->
                appendLine("  $name: $count")
            }
            if (receivedMessageCounts.isEmpty()) {
                appendLine("  No messages received ⚠️")
            }
            appendLine()
            
            // Recent packet history
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ RECENT PACKET HISTORY (last 30)                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            getPacketHistory(30).forEach { entry ->
                appendLine("  ${entry.formatForDisplay()}")
                if (entry.hexPreview != null && entry.error == null) {
                    appendLine("     Hex: ${entry.hexPreview}")
                }
            }
            if (packetHistory.isEmpty()) {
                appendLine("  No packet history")
            }
            appendLine()
            
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine("End of Enhanced Packet Logger Report")
            appendLine("═══════════════════════════════════════════════════════════════════")
        }
    }
    
    /**
     * Log the report to console.
     */
    fun logReport() {
        val report = generateReport()
        report.lines().forEach { line ->
            Log.i(TAG, line)
        }
    }
    
    /**
     * Packet statistics data class.
     */
    data class PacketStatistics(
        val sessionDurationMs: Long,
        val packetsSent: Long,
        val packetsReceived: Long,
        val bytesSent: Long,
        val bytesReceived: Long,
        val acksSent: Long,
        val acksReceived: Long,
        val resendCount: Long,
        val parseErrors: Int,
        val handlerMisses: Int,
        val lastPacketSentMs: Long,
        val lastPacketReceivedMs: Long,
        val uniqueMessageTypesSent: Int,
        val uniqueMessageTypesReceived: Int,
        val registeredHandlerCount: Int
    )
}
