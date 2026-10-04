package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.messages.EnhancedPacketLogger
import com.linkpoint.protocol.messages.MessageIdNameRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Session Log Recorder - Comprehensive logging from app startup to close.
 *
 * Records all diagnostic output including:
 * - All UDP packets (sent and received) with full hex dumps
 * - HTTP requests and responses
 * - Connection state changes
 * - Message handler registrations and dispatches
 * - Error conditions and warnings
 * - Capability events
 * - Login/logout events
 * - Region transitions
 *
 * Designed for full diagnostic output to help debug connection and protocol issues.
 * Offloads string formatting, hex dumps, and SharedPreferences reads to a dedicated
 * background Coroutine channel worker to keep network processing threads 100% non-blocking.
 *
 * IMPORTANT: Logs are saved to app-private internal storage by default.
 * Public sharing is only done through an explicit export action.
 *
 * Usage:
 * - Call `startRecording()` at app startup or when diagnostic logging is needed
 * - Call `stopRecording()` to stop and finalize the log file
 * - Use `exportLog()` to save logs to external storage for sharing
 *
 * The recorder automatically manages memory by periodically flushing to disk.
 */
object SessionLogRecorder {

    private const val TAG = "SessionLogRecorder"

    // Directory and file names
    private const val LOG_DIR_NAME = "Linkpoint Logs"
    private const val SESSION_LOG_PREFIX = "session_log_"
    private const val SESSION_LOG_SUFFIX = ".txt"

    // Buffer and Channel management
    private const val MAX_MEMORY_ENTRIES = 500
    private const val FLUSH_INTERVAL_MS = 10000L // Flush every 10 seconds
    private const val BOUNDED_CHANNEL_CAPACITY = 2000
    private const val BACKPRESSURE_WATERMARK = 1500

    // Recording state
    private val isRecording = AtomicBoolean(false)
    private val sessionStartTime = AtomicLong(0)
    private val entryCount = AtomicLong(0)

    // Channel for async non-blocking log events
    private var logChannel: Channel<LogEvent>? = null
    private var workerJob: Job? = null
    private val pendingQueueSize = AtomicInteger(0)
    private val droppedEntriesCount = AtomicLong(0)

    // In-memory buffer for log entries
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()

    // File output
    private var currentLogFile: File? = null
    private var logWriter: BufferedWriter? = null

    // Context and directory for file operations
    private var appContext: Context? = null
    private var testLogDir: File? = null

    // Coroutine scope for background operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var flushJob: Job? = null

    // Date formatters
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)

    /**
     * Internal event hierarchy processed asynchronously off network threads
     */
    sealed interface LogEvent {
        val timestamp: Long

        data class RawPacket(val rawEvent: RawPacketEvent) : LogEvent {
            override val timestamp: Long get() = rawEvent.timestamp
        }

        data class Text(
            override val timestamp: Long,
            val type: EntryType,
            val tag: String,
            val message: String,
            val stackTrace: String? = null
        ) : LogEvent
    }

    /**
     * Log entry types for categorization
     */
    enum class EntryType {
        /** Application lifecycle event */
        APP_LIFECYCLE,
        /** UDP packet sent */
        PACKET_SENT,
        /** UDP packet received */
        PACKET_RECEIVED,
        /** UDP packet resent (reliable delivery) */
        PACKET_RESENT,
        /** HTTP request */
        HTTP_REQUEST,
        /** HTTP response */
        HTTP_RESPONSE,
        /** Connection state change */
        CONNECTION_STATE,
        /** Message handler event */
        MESSAGE_HANDLER,
        /** Capability event */
        CAPABILITY,
        /** Login/logout event */
        AUTH,
        /** Region transition */
        REGION,
        /** Error condition */
        ERROR,
        /** Warning */
        WARNING,
        /** General info */
        INFO,
        /** Debug detail */
        DEBUG,
        /** Renderer lifecycle / OpenGL / Filament event */
        RENDER
    }

    /**
     * Single log entry with timestamp, type, and content
     */
    data class LogEntry(
        val timestamp: Long,
        val type: EntryType,
        val tag: String,
        val message: String,
        val hexDump: String? = null,
        val stackTrace: String? = null
    ) {
        fun format(): String = buildString {
            val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
            append("[$timeStr] [${type.name}] [$tag]")
            appendLine()
            append(message)
            hexDump?.let {
                appendLine()
                append("Hex: $it")
            }
            stackTrace?.let {
                appendLine()
                append("Stack: $it")
            }
            appendLine()
        }
    }

    /**
     * Recording statistics
     */
    data class RecordingStats(
        val isRecording: Boolean,
        val sessionStartTime: Long,
        val durationMs: Long,
        val totalEntries: Long,
        val entriesInMemory: Int,
        val currentLogFile: String?,
        val logFileSizeBytes: Long,
        val pendingChannelEvents: Int = 0,
        val droppedEntries: Long = 0
    )

    /**
     * Initialize the recorder with application context.
     * Must be called before starting recording.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        safeLogI("SessionLogRecorder initialized")
    }

    /**
     * Initialize the recorder for testing with a target log directory.
     */
    fun initializeForTest(dir: File) {
        testLogDir = dir
    }

    /**
     * Start recording session logs.
     * Creates a new log file and begins capturing all diagnostic output.
     */
    fun startRecording(): Boolean {
        if (isRecording.getAndSet(true)) {
            safeLogW("Recording already in progress")
            return false
        }

        try {
            sessionStartTime.set(System.currentTimeMillis())
            entryCount.set(0)
            droppedEntriesCount.set(0)
            pendingQueueSize.set(0)
            logBuffer.clear()

            // Initialize bounded Coroutine Channel
            val channel = Channel<LogEvent>(BOUNDED_CHANNEL_CAPACITY, BufferOverflow.SUSPEND)
            logChannel = channel

            // Create log file
            val logDir = getLogDirectory() ?: run {
                safeLogE("Could not get log directory")
                isRecording.set(false)
                return false
            }

            val context = appContext
            if (context != null) {
                DiagnosticsLoggingConfig.purgeExpiredLogs(
                    logDir = logDir,
                    retentionDays = DiagnosticsLoggingConfig.getRetentionDays(context)
                )
            }

            val timestamp = fileNameFormat.format(Date())
            currentLogFile = File(logDir, "$SESSION_LOG_PREFIX$timestamp$SESSION_LOG_SUFFIX")
            logWriter = BufferedWriter(FileWriter(currentLogFile, true))

            // Write header
            writeHeader()

            // Start worker job and periodic flush
            startWorkerJob(channel)
            startFlushJob()

            // Log start event
            log(EntryType.APP_LIFECYCLE, TAG, "Session recording STARTED")

            safeLogI("Session recording started: ${currentLogFile?.absolutePath}")
            return true

        } catch (e: Exception) {
            safeLogE("Failed to start recording", e)
            isRecording.set(false)
            return false
        }
    }

    /**
     * Stop recording and finalize the log file.
     * Drains all remaining channel events to guarantee log completeness.
     */
    fun stopRecording(): File? {
        if (!isRecording.getAndSet(false)) {
            safeLogW("Recording not in progress")
            return null
        }

        try {
            // Log stop event
            log(EntryType.APP_LIFECYCLE, TAG, "Session recording STOPPED")

            // Close channel so consumer drains all remaining events
            val channel = logChannel
            logChannel = null
            channel?.close()

            // Wait for background worker to consume all remaining channel elements
            runBlocking {
                workerJob?.join()
            }
            workerJob = null

            // Stop periodic flush job
            flushJob?.cancel()
            flushJob = null

            // Final flush
            flushToFile()

            // Write footer
            writeFooter()

            // Close writer
            logWriter?.close()
            logWriter = null

            val resultFile = currentLogFile
            currentLogFile = null

            safeLogI("Session recording stopped: ${resultFile?.absolutePath}")
            return resultFile

        } catch (e: Exception) {
            safeLogE("Error stopping recording", e)
            return null
        }
    }

    /**
     * Check if recording is active
     */
    fun isRecording(): Boolean = isRecording.get()

    /**
     * Get current recording statistics
     */
    fun getStats(): RecordingStats {
        val now = System.currentTimeMillis()
        return RecordingStats(
            isRecording = isRecording.get(),
            sessionStartTime = sessionStartTime.get(),
            durationMs = if (sessionStartTime.get() > 0) now - sessionStartTime.get() else 0,
            totalEntries = entryCount.get(),
            entriesInMemory = logBuffer.size,
            currentLogFile = currentLogFile?.absolutePath,
            logFileSizeBytes = currentLogFile?.length() ?: 0,
            pendingChannelEvents = pendingQueueSize.get(),
            droppedEntries = droppedEntriesCount.get()
        )
    }

    /**
     * Enqueue a raw packet event to the non-blocking channel.
     * Enforces queue backpressure bounds to protect heap memory.
     */
    fun enqueueRawPacket(rawEvent: RawPacketEvent) {
        if (!isRecording.get()) return

        val channel = logChannel ?: return
        val currentSize = pendingQueueSize.get()

        // Guard memory under backpressure by dropping optional hex dump byte array
        val candidate = if (currentSize > BACKPRESSURE_WATERMARK && rawEvent.payload != null) {
            rawEvent.copy(payload = null)
        } else {
            rawEvent
        }

        val result = channel.trySend(LogEvent.RawPacket(candidate))
        if (result.isSuccess) {
            pendingQueueSize.incrementAndGet()
        } else {
            // Channel full: try dropping payload if not already dropped
            if (candidate.payload != null) {
                val payloadless = candidate.copy(payload = null)
                val fallbackResult = channel.trySend(LogEvent.RawPacket(payloadless))
                if (fallbackResult.isSuccess) {
                    pendingQueueSize.incrementAndGet()
                    return
                }
            }
            droppedEntriesCount.incrementAndGet()
        }
    }

    private fun enqueueText(event: LogEvent.Text) {
        if (!isRecording.get()) return

        val channel = logChannel ?: return
        val result = channel.trySend(event)
        if (result.isSuccess) {
            pendingQueueSize.incrementAndGet()
        } else {
            droppedEntriesCount.incrementAndGet()
        }
    }

    /**
     * Log a general message
     */
    fun log(type: EntryType, tag: String, message: String) {
        if (!isRecording.get()) return

        enqueueText(
            LogEvent.Text(
                timestamp = System.currentTimeMillis(),
                type = type,
                tag = tag,
                message = message
            )
        )
    }

    /**
     * Log a message with hex dump (for packets)
     */
    fun logWithHex(type: EntryType, tag: String, message: String, data: ByteArray) {
        if (!isRecording.get()) return

        val payloadCopy = if (data.isNotEmpty()) data.copyOf() else null
        val event = RawPacketEvent(
            timestamp = System.currentTimeMillis(),
            type = type,
            tag = tag,
            customMessage = message,
            payload = payloadCopy
        )
        enqueueRawPacket(event)
    }

    /**
     * Log an error with stack trace
     */
    fun logError(tag: String, message: String, error: Throwable? = null) {
        if (!isRecording.get()) {
            return
        }

        enqueueText(
            LogEvent.Text(
                timestamp = System.currentTimeMillis(),
                type = EntryType.ERROR,
                tag = tag,
                message = message,
                stackTrace = error?.stackTraceToString()?.take(1000)
            )
        )
    }

    /**
     * Log a UDP packet sent
     */
    fun logPacketSent(
        messageId: Int,
        messageName: String,
        sequenceNumber: Int,
        data: ByteArray,
        reliable: Boolean
    ) {
        if (!isRecording.get()) return

        val payloadCopy = if (data.isNotEmpty()) data.copyOf() else null
        val event = RawPacketEvent(
            timestamp = System.currentTimeMillis(),
            type = EntryType.PACKET_SENT,
            tag = "UDP",
            messageId = messageId,
            messageName = messageName,
            sequenceNumber = sequenceNumber,
            reliable = reliable,
            payload = payloadCopy
        )
        enqueueRawPacket(event)
    }

    /**
     * Log a UDP packet received
     */
    fun logPacketReceived(
        messageId: Int,
        messageName: String,
        sequenceNumber: Int,
        data: ByteArray,
        handlerFound: Boolean
    ) {
        if (!isRecording.get()) return

        val payloadCopy = if (data.isNotEmpty()) data.copyOf() else null
        val event = RawPacketEvent(
            timestamp = System.currentTimeMillis(),
            type = EntryType.PACKET_RECEIVED,
            tag = "UDP",
            messageId = messageId,
            messageName = messageName,
            sequenceNumber = sequenceNumber,
            handlerFound = handlerFound,
            payload = payloadCopy
        )
        enqueueRawPacket(event)
    }

    /**
     * Log HTTP request
     */
    fun logHttpRequest(method: String, url: String, headers: Map<String, String>? = null) {
        if (!isRecording.get()) return

        val message = buildString {
            append("→ HTTP $method $url")
            headers?.let {
                append("\nHeaders:")
                it.forEach { (k, v) ->
                    // Sanitize sensitive headers to prevent credential leakage
                    val sensitiveHeaders = listOf(
                        "auth", "authorization", "cookie", "token", "bearer",
                        "api-key", "apikey", "secret", "password", "credential"
                    )
                    val safeValue = if (sensitiveHeaders.any { sensitive ->
                            k.contains(sensitive, ignoreCase = true)
                        }
                    ) "***REDACTED***" else v
                    append("\n  $k: $safeValue")
                }
            }
        }
        log(EntryType.HTTP_REQUEST, "HTTP", message)
    }

    /**
     * Log HTTP response
     */
    fun logHttpResponse(url: String, statusCode: Int, durationMs: Long, protocol: String? = null) {
        if (!isRecording.get()) return

        val message = buildString {
            append("← HTTP $statusCode (${durationMs}ms)")
            protocol?.let { append(" [$it]") }
            append("\n  URL: $url")
        }
        log(EntryType.HTTP_RESPONSE, "HTTP", message)
    }

    /**
     * Log connection state change
     */
    fun logConnectionState(oldState: String, newState: String, details: String? = null) {
        if (!isRecording.get()) return

        val message = buildString {
            append("Connection: $oldState → $newState")
            details?.let { append("\n  Details: $it") }
        }
        log(EntryType.CONNECTION_STATE, "CONN", message)
    }

    /**
     * Log capability event
     */
    fun logCapability(capName: String, available: Boolean, url: String? = null) {
        if (!isRecording.get()) return

        val status = if (available) "✓ AVAILABLE" else "✗ UNAVAILABLE"
        val message = buildString {
            append("$capName: $status")
            url?.let { append("\n  URL: ${it.take(80)}...") }
        }
        log(EntryType.CAPABILITY, "CAP", message)
    }

    /**
     * Log a renderer lifecycle / OpenGL / Filament event.
     */
    fun logRender(subsystem: String, event: String, details: String? = null) {
        if (!isRecording.get()) return
        val message = if (details.isNullOrBlank()) event else "$event — $details"
        log(EntryType.RENDER, "RENDER/$subsystem", message)
    }

    /**
     * Log login event
     */
    fun logLogin(success: Boolean, grid: String, username: String, error: String? = null) {
        if (!isRecording.get()) return

        val status = if (success) "✓ SUCCESS" else "✗ FAILED"
        val message = buildString {
            append("LOGIN $status")
            append("\n  Grid: $grid")
            append("\n  User: $username")
            error?.let { append("\n  Error: $it") }
        }
        log(EntryType.AUTH, "AUTH", message)
    }

    /**
     * Log region change.
     */
    fun logRegionChange(regionName: String, regionHandle: Long?, position: String? = null) {
        if (!isRecording.get()) return

        val message = buildString {
            append("REGION: $regionName")
            if (regionHandle != null) append("\n  Handle: $regionHandle")
            position?.let { append("\n  Position: $it") }
        }
        log(EntryType.REGION, "REGION", message)
    }

    /**
     * Export current recording to a shareable file
     */
    fun exportLog(): File? {
        if (!isRecording.get()) {
            safeLogW("Cannot export - not recording")
            return currentLogFile
        }

        flushChannelAndFile()
        return currentLogFile
    }

    /**
     * Get the app-private path where logs are stored.
     */
    fun getLogDirectoryPath(): String {
        return getLogDirectory()?.absolutePath ?: "unavailable"
    }

    // ==================== PRIVATE METHODS ====================

    private fun startWorkerJob(channel: Channel<LogEvent>) {
        workerJob = scope.launch(Dispatchers.IO + CoroutineName("SessionLogRecorder-Worker")) {
            for (event in channel) {
                pendingQueueSize.decrementAndGet()
                processLogEvent(event)
            }
        }
    }

    private fun processLogEvent(event: LogEvent) {
        when (event) {
            is LogEvent.RawPacket -> {
                val raw = event.rawEvent
                val formattedMessage = raw.customMessage ?: when (raw.type) {
                    EntryType.PACKET_SENT -> {
                        buildString {
                            append("→ SENT: ${raw.messageName ?: "UNKNOWN"}")
                            append(" (ID: 0x${MessageIdNameRegistry.formatHex(raw.messageId ?: 0)}")
                            append(", seq: ${raw.sequenceNumber ?: 0}")
                            append(", size: ${raw.payload?.size ?: 0}B")
                            if (raw.reliable == true) append(", RELIABLE")
                            append(")")
                        }
                    }
                    EntryType.PACKET_RECEIVED -> {
                        val handlerStatus = if (raw.handlerFound == true) "✓" else "⚠️ NO HANDLER"
                        buildString {
                            append("← RECV: ${raw.messageName ?: "UNKNOWN"} $handlerStatus")
                            append(" (ID: 0x${MessageIdNameRegistry.formatHex(raw.messageId ?: 0)}")
                            append(", seq: ${raw.sequenceNumber ?: 0}")
                            append(", size: ${raw.payload?.size ?: 0}B)")
                        }
                    }
                    else -> raw.customMessage ?: ""
                }

                val context = appContext
                val includeHexDump = context != null && DiagnosticsLoggingConfig.isVerbosePacketLoggingEnabled(context)
                val hexDump = if (includeHexDump && raw.payload != null) {
                    raw.payload.joinToString(" ") { "%02X".format(it) }
                } else null

                val entry = LogEntry(
                    timestamp = raw.timestamp,
                    type = raw.type,
                    tag = raw.tag,
                    message = formattedMessage,
                    hexDump = hexDump
                )
                addEntryInternal(entry)
            }
            is LogEvent.Text -> {
                val entry = LogEntry(
                    timestamp = event.timestamp,
                    type = event.type,
                    tag = event.tag,
                    message = event.message,
                    stackTrace = event.stackTrace
                )
                addEntryInternal(entry)
            }
        }
    }

    private fun addEntryInternal(entry: LogEntry) {
        val sanitizedEntry = entry.copy(
            message = DiagnosticsLogSanitizer.sanitize(entry.message),
            hexDump = entry.hexDump,
            stackTrace = entry.stackTrace?.let { DiagnosticsLogSanitizer.sanitize(it) }
        )
        logBuffer.offer(sanitizedEntry)
        entryCount.incrementAndGet()

        // Trigger flush if buffer is getting large
        if (logBuffer.size > MAX_MEMORY_ENTRIES) {
            scope.launch {
                flushToFile()
            }
        }
    }

    private fun startFlushJob() {
        flushJob = scope.launch {
            while (isActive && isRecording.get()) {
                delay(FLUSH_INTERVAL_MS)
                flushToFile()
            }
        }
    }

    private fun flushChannelAndFile() {
        val startMs = System.currentTimeMillis()
        while (pendingQueueSize.get() > 0 && System.currentTimeMillis() - startMs < 1000) {
            Thread.sleep(10)
        }
        flushToFile()
    }

    private fun flushToFile() {
        val writer = logWriter ?: return

        try {
            synchronized(writer) {
                while (logBuffer.isNotEmpty()) {
                    val entry = logBuffer.poll() ?: break
                    writer.write(entry.format())
                }
                writer.flush()
            }
        } catch (e: Exception) {
            safeLogE("Error flushing to file", e)
        }
    }

    private fun writeHeader() {
        val writer = logWriter ?: return
        val now = System.currentTimeMillis()

        try {
            writer.write("╔══════════════════════════════════════════════════════════════════╗\n")
            writer.write("║               LINKPOINT SESSION LOG                               ║\n")
            writer.write("╚══════════════════════════════════════════════════════════════════╝\n")
            writer.write("\n")
            writer.write("Session Start: ${timestampFormat.format(Date(now))}\n")
            writer.write("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
            writer.write("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            writer.write("Build: ${Build.ID}\n")
            writer.write("\n")
            writer.write("This log captures all diagnostic output including:\n")
            writer.write("- All UDP packets (sent/received) with full hex dumps\n")
            writer.write("- HTTP requests and responses\n")
            writer.write("- Connection state changes\n")
            writer.write("- Capability events\n")
            writer.write("- Error conditions\n")
            writer.write("\n")
            writer.write("═".repeat(70) + "\n\n")
            writer.flush()
        } catch (e: Exception) {
            safeLogE("Error writing header", e)
        }
    }

    private fun writeFooter() {
        val writer = logWriter ?: return
        val now = System.currentTimeMillis()
        val duration = now - sessionStartTime.get()

        try {
            writer.write("\n")
            writer.write("═".repeat(70) + "\n")
            writer.write("\n")
            writer.write("Session End: ${timestampFormat.format(Date(now))}\n")
            writer.write("Duration: ${formatDuration(duration)}\n")
            writer.write("Total Entries: ${entryCount.get()}\n")
            if (droppedEntriesCount.get() > 0) {
                writer.write("Dropped Entries (Queue Backpressure): ${droppedEntriesCount.get()}\n")
            }
            writer.write("\n")

            // Include EnhancedPacketLogger statistics
            try {
                val packetStats = EnhancedPacketLogger.getStatistics()
                writer.write("Packet Statistics:\n")
                writer.write("  Packets Sent: ${packetStats.packetsSent}\n")
                writer.write("  Packets Received: ${packetStats.packetsReceived}\n")
                writer.write("  Bytes Sent: ${formatBytes(packetStats.bytesSent)}\n")
                writer.write("  Bytes Received: ${formatBytes(packetStats.bytesReceived)}\n")
                writer.write("  Resends: ${packetStats.resendCount}\n")
                writer.write("  Parse Errors: ${packetStats.parseErrors}\n")
                writer.write("  Handler Misses: ${packetStats.handlerMisses}\n")
            } catch (e: Exception) {
                writer.write("Packet Statistics: unavailable\n")
            }

            writer.write("\n")
            writer.write("╔══════════════════════════════════════════════════════════════════╗\n")
            writer.write("║               END OF SESSION LOG                                  ║\n")
            writer.write("╚══════════════════════════════════════════════════════════════════╝\n")
            writer.flush()
        } catch (e: Exception) {
            safeLogE("Error writing footer", e)
        }
    }

    /**
     * Get the app-private diagnostics directory.
     */
    private fun getLogDirectory(): File? {
        val testDir = testLogDir
        if (testDir != null) {
            if (!testDir.exists()) {
                testDir.mkdirs()
            }
            return testDir
        }
        val context = appContext ?: return null
        val logDir = File(DiagnosticsLoggingConfig.diagnosticsDirectory(context), LOG_DIR_NAME)

        try {
            if (!logDir.exists()) {
                logDir.mkdirs()
            }

            return logDir
        } catch (e: Exception) {
            safeLogE("Error accessing app-private diagnostics directory: ${e.message}", e)
        }

        return null
    }

    private fun formatDuration(ms: Long): String {
        return when {
            ms < 1000 -> "${ms}ms"
            ms < 60000 -> String.format(Locale.US, "%.1fs", ms / 1000.0)
            ms < 3600000 -> String.format(Locale.US, "%.1fm", ms / 60000.0)
            else -> String.format(Locale.US, "%.1fh", ms / 3600000.0)
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.US, "%.2f KB", bytes / 1024.0)
            else -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }

    private fun safeLogI(msg: String) {
        try { Log.i(TAG, msg) } catch (_: Throwable) {}
    }

    private fun safeLogW(msg: String) {
        try { Log.w(TAG, msg) } catch (_: Throwable) {}
    }

    private fun safeLogE(msg: String, e: Throwable? = null) {
        try {
            if (e != null) Log.e(TAG, msg, e) else Log.e(TAG, msg)
        } catch (_: Throwable) {}
    }

    /**
     * Shutdown the recorder and release resources
     */
    fun shutdown() {
        if (isRecording.get()) {
            stopRecording()
        }
        scope.cancel()
    }
}
