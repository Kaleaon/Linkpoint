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
    
    // Malformed packet tracking - detailed categorization for debugging
    private val malformedPacketCount = AtomicInteger(0)
    private val truncatedPacketCount = AtomicInteger(0)
    private val invalidFlagsCount = AtomicInteger(0)
    private val invalidMessageIdCount = AtomicInteger(0)
    private val corruptedPayloadCount = AtomicInteger(0)
    private val zeroDecodeFailureCount = AtomicInteger(0)
    private val oversizedPacketCount = AtomicInteger(0)
    
    // Recent malformed packet history for detailed debugging
    private const val MAX_MALFORMED_HISTORY = 50
    private val malformedPacketHistory = ConcurrentLinkedQueue<MalformedPacketEntry>()
    
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
     * Malformed packet entry for tracking broken/invalid packets.
     * These help diagnose protocol issues and network corruption.
     */
    data class MalformedPacketEntry(
        val timestamp: Long,
        val reason: MalformedReason,
        val size: Int,
        val hexPreview: String,
        val details: String,
        val rawFlags: Int? = null,
        val sequenceNumber: Int? = null,
        val messageId: Int? = null
    ) {
        enum class MalformedReason {
            /** Packet smaller than minimum header size */
            TRUNCATED,
            /** Invalid flag combination */
            INVALID_FLAGS,
            /** Message ID could not be decoded */
            INVALID_MESSAGE_ID,
            /** Payload structure doesn't match expected format */
            CORRUPTED_PAYLOAD,
            /** Zero-decode expansion failed */
            ZERO_DECODE_FAILURE,
            /** Packet exceeds maximum expected size */
            OVERSIZED,
            /** ACK count doesn't match available bytes */
            ACK_COUNT_MISMATCH,
            /** Sequence number is invalid or out of expected range */
            INVALID_SEQUENCE,
            /** General parse failure */
            PARSE_ERROR
        }
        
        fun formatForDisplay(): String {
            val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
            val seqInfo = sequenceNumber?.let { " seq=$it" } ?: ""
            val msgIdInfo = messageId?.let { " msgId=0x${it.toString(16).uppercase()}" } ?: ""
            return "[$timeStr] ⚠️ MALFORMED: $reason (${size}B)$seqInfo$msgIdInfo\n    Details: $details\n    Hex: $hexPreview"
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
        malformedPacketCount.set(0)
        truncatedPacketCount.set(0)
        invalidFlagsCount.set(0)
        invalidMessageIdCount.set(0)
        corruptedPayloadCount.set(0)
        zeroDecodeFailureCount.set(0)
        oversizedPacketCount.set(0)
        sentMessageCounts.clear()
        receivedMessageCounts.clear()
        lastMessageTimes.clear()
        handlerDispatchCounts.clear()
        packetHistory.clear()
        malformedPacketHistory.clear()
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
        lastPacketReceivedTime = System.currentTimeMillis()

        // Record the ACK in the packet history so it surfaces in debug reports
        // alongside sends/receives. Previously the counter was incremented but
        // history showed zero ACK_RECEIVED entries, making it impossible to see
        // which sequence numbers the simulator was acknowledging.
        val entry = PacketLogEntry(
            timestamp = lastPacketReceivedTime,
            direction = PacketLogEntry.Direction.ACK_RECEIVED,
            messageId = -1,
            messageName = messageName ?: "ACK",
            sequenceNumber = sequenceNumber,
            size = 4,
            flags = PacketFlags(reliable = false, resent = false, zerocoded = false, hasAcks = true),
            hexPreview = null,
            handlerDispatched = true
        )
        addToHistory(entry)

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
    
    // ==================== MALFORMED PACKET LOGGING ====================
    
    /**
     * Log a malformed packet with detailed information for debugging.
     * This is the main entry point for tracking broken/invalid packets.
     * 
     * @param reason The category of malformation detected
     * @param data The raw packet data (or partial data if truncated)
     * @param details Human-readable description of the issue
     * @param rawFlags The raw flags byte (if available)
     * @param sequenceNumber The sequence number (if parseable)
     * @param messageId The message ID (if parseable)
     */
    fun logMalformedPacket(
        reason: MalformedPacketEntry.MalformedReason,
        data: ByteArray,
        details: String,
        rawFlags: Int? = null,
        sequenceNumber: Int? = null,
        messageId: Int? = null
    ) {
        if (!isEnabled) return
        
        // Update overall malformed count
        malformedPacketCount.incrementAndGet()
        
        // Update specific category counts
        when (reason) {
            MalformedPacketEntry.MalformedReason.TRUNCATED -> truncatedPacketCount.incrementAndGet()
            MalformedPacketEntry.MalformedReason.INVALID_FLAGS -> invalidFlagsCount.incrementAndGet()
            MalformedPacketEntry.MalformedReason.INVALID_MESSAGE_ID -> invalidMessageIdCount.incrementAndGet()
            MalformedPacketEntry.MalformedReason.CORRUPTED_PAYLOAD -> corruptedPayloadCount.incrementAndGet()
            MalformedPacketEntry.MalformedReason.ZERO_DECODE_FAILURE -> zeroDecodeFailureCount.incrementAndGet()
            MalformedPacketEntry.MalformedReason.OVERSIZED -> oversizedPacketCount.incrementAndGet()
            else -> {} // Other categories tracked by overall count only
        }
        
        // Create entry for history
        val hexPreview = data.take(48).joinToString(" ") { "%02X".format(it) }
        val entry = MalformedPacketEntry(
            timestamp = System.currentTimeMillis(),
            reason = reason,
            size = data.size,
            hexPreview = hexPreview,
            details = details,
            rawFlags = rawFlags,
            sequenceNumber = sequenceNumber,
            messageId = messageId
        )
        
        // Add to malformed history
        addToMalformedHistory(entry)
        
        // Log to console with full details
        Log.w(TAG, "⚠️ MALFORMED PACKET DETECTED ⚠️")
        Log.w(TAG, "  Reason: $reason")
        Log.w(TAG, "  Size: ${data.size} bytes")
        Log.w(TAG, "  Details: $details")
        rawFlags?.let { Log.w(TAG, "  Raw Flags: 0x${it.toString(16).uppercase().padStart(2, '0')}") }
        sequenceNumber?.let { Log.w(TAG, "  Sequence: $it") }
        messageId?.let { Log.w(TAG, "  Message ID: 0x${it.toString(16).uppercase()}") }
        Log.w(TAG, "  Hex Preview: $hexPreview")
    }
    
    /**
     * Log a truncated packet (too small to contain required header).
     */
    fun logTruncatedPacket(data: ByteArray, expectedMinSize: Int) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.TRUNCATED,
            data = data,
            details = "Packet size ${data.size} bytes is smaller than minimum required $expectedMinSize bytes"
        )
    }
    
    /**
     * Log invalid flags in packet header.
     */
    fun logInvalidFlags(data: ByteArray, flags: Int, issue: String) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.INVALID_FLAGS,
            data = data,
            details = "Invalid flags: $issue",
            rawFlags = flags,
            sequenceNumber = extractSequenceNumber(data)
        )
    }
    
    /**
     * Log failed message ID decoding.
     */
    fun logInvalidMessageId(data: ByteArray, offset: Int, issue: String) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.INVALID_MESSAGE_ID,
            data = data,
            details = "Failed to decode message ID at offset $offset: $issue",
            rawFlags = extractFlags(data),
            sequenceNumber = extractSequenceNumber(data)
        )
    }
    
    /**
     * Log zero-decode failure.
     */
    fun logZeroDecodeFailure(data: ByteArray, error: String) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.ZERO_DECODE_FAILURE,
            data = data,
            details = "Zero-decode expansion failed: $error",
            rawFlags = extractFlags(data),
            sequenceNumber = extractSequenceNumber(data)
        )
    }
    
    /**
     * Log ACK count mismatch (reported ACK count doesn't match available bytes).
     */
    fun logAckCountMismatch(data: ByteArray, reportedCount: Int, availableBytes: Int) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.ACK_COUNT_MISMATCH,
            data = data,
            details = "ACK count $reportedCount requires ${reportedCount * 4} bytes but only $availableBytes available",
            rawFlags = extractFlags(data),
            sequenceNumber = extractSequenceNumber(data)
        )
    }
    
    /**
     * Log corrupted payload structure.
     */
    fun logCorruptedPayload(data: ByteArray, messageId: Int, issue: String) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.CORRUPTED_PAYLOAD,
            data = data,
            details = "Payload structure invalid: $issue",
            rawFlags = extractFlags(data),
            sequenceNumber = extractSequenceNumber(data),
            messageId = messageId
        )
    }
    
    /**
     * Log oversized packet.
     */
    fun logOversizedPacket(data: ByteArray, maxExpectedSize: Int) {
        logMalformedPacket(
            reason = MalformedPacketEntry.MalformedReason.OVERSIZED,
            data = data,
            details = "Packet size ${data.size} exceeds maximum expected $maxExpectedSize bytes",
            rawFlags = extractFlags(data),
            sequenceNumber = extractSequenceNumber(data)
        )
    }
    
    private fun addToMalformedHistory(entry: MalformedPacketEntry) {
        malformedPacketHistory.offer(entry)
        while (malformedPacketHistory.size > MAX_MALFORMED_HISTORY) {
            malformedPacketHistory.poll()
        }
    }
    
    /**
     * Get malformed packet history.
     */
    fun getMalformedPacketHistory(count: Int = 20): List<MalformedPacketEntry> {
        return malformedPacketHistory.toList().takeLast(count)
    }
    
    /**
     * Get malformed packet statistics.
     */
    fun getMalformedStatistics(): MalformedPacketStatistics {
        return MalformedPacketStatistics(
            totalMalformed = malformedPacketCount.get(),
            truncated = truncatedPacketCount.get(),
            invalidFlags = invalidFlagsCount.get(),
            invalidMessageId = invalidMessageIdCount.get(),
            corruptedPayload = corruptedPayloadCount.get(),
            zeroDecodeFailure = zeroDecodeFailureCount.get(),
            oversized = oversizedPacketCount.get()
        )
    }
    
    /**
     * Statistics for malformed packets.
     */
    data class MalformedPacketStatistics(
        val totalMalformed: Int,
        val truncated: Int,
        val invalidFlags: Int,
        val invalidMessageId: Int,
        val corruptedPayload: Int,
        val zeroDecodeFailure: Int,
        val oversized: Int
    ) {
        fun hasIssues(): Boolean = totalMalformed > 0
        
        fun toFormattedString(): String {
            if (totalMalformed == 0) return "No malformed packets detected"
            
            return buildString {
                appendLine("Total Malformed: $totalMalformed")
                if (truncated > 0) appendLine("  Truncated: $truncated")
                if (invalidFlags > 0) appendLine("  Invalid Flags: $invalidFlags")
                if (invalidMessageId > 0) appendLine("  Invalid Message ID: $invalidMessageId")
                if (corruptedPayload > 0) appendLine("  Corrupted Payload: $corruptedPayload")
                if (zeroDecodeFailure > 0) appendLine("  Zero-Decode Failures: $zeroDecodeFailure")
                if (oversized > 0) appendLine("  Oversized: $oversized")
            }
        }
    }
    
    private fun addToHistory(entry: PacketLogEntry) {
        packetHistory.offer(entry)
        while (packetHistory.size > MAX_PACKET_HISTORY) {
            packetHistory.poll()
        }
    }
    
    /**
     * Extract sequence number from raw packet data.
     * Packet header format: flags (1 byte), sequence (4 bytes big-endian), extra (1 byte)
     * 
     * Uses manual byte operations for efficiency (avoids ByteBuffer allocation).
     * 
     * @param data The raw packet data
     * @return The sequence number, or null if data is too small
     */
    private fun extractSequenceNumber(data: ByteArray): Int? {
        if (data.size < 5) return null
        // Big-endian: bytes 1-4 contain sequence number (byte 0 is flags)
        return ((data[1].toInt() and 0xFF) shl 24) or
               ((data[2].toInt() and 0xFF) shl 16) or
               ((data[3].toInt() and 0xFF) shl 8) or
               (data[4].toInt() and 0xFF)
    }
    
    /**
     * Extract flags byte from raw packet data.
     * 
     * @param data The raw packet data
     * @return The flags byte as Int (0-255), or null if data is empty
     */
    private fun extractFlags(data: ByteArray): Int? {
        if (data.isEmpty()) return null
        return data[0].toInt() and 0xFF
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
            registeredHandlerCount = registeredHandlers.size,
            malformedPackets = malformedPacketCount.get(),
            truncatedPackets = truncatedPacketCount.get(),
            invalidFlagsPackets = invalidFlagsCount.get(),
            invalidMessageIdPackets = invalidMessageIdCount.get(),
            corruptedPayloadPackets = corruptedPayloadCount.get(),
            zeroDecodeFailures = zeroDecodeFailureCount.get(),
            oversizedPackets = oversizedPacketCount.get()
        )
    }
    
    /**
     * Get packet history for debug reports.
     */
    fun getPacketHistory(count: Int = 50): List<PacketLogEntry> {
        return packetHistory.toList().takeLast(count)
    }

    /**
     * Get only inbound packet history (RECEIVED + ACK_RECEIVED) for debug
     * reports. This is needed because the bounded packet history is
     * dominated by outbound AgentUpdates at ~10/sec, which evicts incoming
     * entries from any small "tail" view. Filtering to inbound first lets
     * the debug report show real receive activity even under heavy send load.
     */
    fun getIncomingPacketHistory(count: Int = 30): List<PacketLogEntry> {
        return packetHistory.toList()
            .asReversed()
            .asSequence()
            .filter {
                it.direction == PacketLogEntry.Direction.RECEIVED ||
                it.direction == PacketLogEntry.Direction.ACK_RECEIVED
            }
            .take(count)
            .toList()
            .asReversed()
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
            
            // Malformed packet section
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ MALFORMED PACKET STATISTICS                                       │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            val malformedStats = getMalformedStatistics()
            if (malformedStats.hasIssues()) {
                appendLine("⚠️ MALFORMED PACKETS DETECTED!")
                appendLine("  Total: ${malformedStats.totalMalformed}")
                if (malformedStats.truncated > 0) appendLine("  Truncated (too small): ${malformedStats.truncated}")
                if (malformedStats.invalidFlags > 0) appendLine("  Invalid Flags: ${malformedStats.invalidFlags}")
                if (malformedStats.invalidMessageId > 0) appendLine("  Invalid Message ID: ${malformedStats.invalidMessageId}")
                if (malformedStats.corruptedPayload > 0) appendLine("  Corrupted Payload: ${malformedStats.corruptedPayload}")
                if (malformedStats.zeroDecodeFailure > 0) appendLine("  Zero-Decode Failures: ${malformedStats.zeroDecodeFailure}")
                if (malformedStats.oversized > 0) appendLine("  Oversized: ${malformedStats.oversized}")
                appendLine()
                
                // Show recent malformed packet history
                val recentMalformed = getMalformedPacketHistory(10)
                if (recentMalformed.isNotEmpty()) {
                    appendLine("Recent Malformed Packets:")
                    recentMalformed.forEach { entry ->
                        appendLine("  ${entry.formatForDisplay()}")
                    }
                }
            } else {
                appendLine("  ✓ No malformed packets detected")
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
        val registeredHandlerCount: Int,
        val malformedPackets: Int = 0,
        val truncatedPackets: Int = 0,
        val invalidFlagsPackets: Int = 0,
        val invalidMessageIdPackets: Int = 0,
        val corruptedPayloadPackets: Int = 0,
        val zeroDecodeFailures: Int = 0,
        val oversizedPackets: Int = 0
    )
}
