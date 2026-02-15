package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.messages.EnhancedPacketLogger
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
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
    
    // Buffer management
    private const val MAX_MEMORY_ENTRIES = 500
    private const val FLUSH_INTERVAL_MS = 10000L // Flush every 10 seconds
    
    // Recording state
    private val isRecording = AtomicBoolean(false)
    private val sessionStartTime = AtomicLong(0)
    private val entryCount = AtomicLong(0)
    
    // In-memory buffer for log entries
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()
    
    // File output
    private var currentLogFile: File? = null
    private var logWriter: BufferedWriter? = null
    
    // Context for file operations
    private var appContext: Context? = null
    
    // Coroutine scope for background operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var flushJob: Job? = null
    
    // Date formatters
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    
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
        DEBUG
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
        val logFileSizeBytes: Long
    )
    
    /**
     * Initialize the recorder with application context.
     * Must be called before starting recording.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        Log.i(TAG, "SessionLogRecorder initialized")
    }
    
    /**
     * Start recording session logs.
     * Creates a new log file and begins capturing all diagnostic output.
     */
    fun startRecording(): Boolean {
        if (isRecording.getAndSet(true)) {
            Log.w(TAG, "Recording already in progress")
            return false
        }
        
        try {
            sessionStartTime.set(System.currentTimeMillis())
            entryCount.set(0)
            logBuffer.clear()
            
            // Create log file
            val logDir = getLogDirectory() ?: run {
                Log.e(TAG, "Could not get log directory")
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
            
            // Start periodic flush
            startFlushJob()
            
            // Log start event
            log(EntryType.APP_LIFECYCLE, TAG, "Session recording STARTED")
            
            Log.i(TAG, "Session recording started: ${currentLogFile?.absolutePath}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            isRecording.set(false)
            return false
        }
    }
    
    /**
     * Stop recording and finalize the log file.
     */
    fun stopRecording(): File? {
        if (!isRecording.getAndSet(false)) {
            Log.w(TAG, "Recording not in progress")
            return null
        }
        
        try {
            // Log stop event
            log(EntryType.APP_LIFECYCLE, TAG, "Session recording STOPPED")
            
            // Stop flush job
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
            
            Log.i(TAG, "Session recording stopped: ${resultFile?.absolutePath}")
            return resultFile
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
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
            logFileSizeBytes = currentLogFile?.length() ?: 0
        )
    }
    
    /**
     * Log a general message
     */
    fun log(type: EntryType, tag: String, message: String) {
        if (!isRecording.get()) return
        
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            type = type,
            tag = tag,
            message = message
        )
        addEntry(entry)
    }
    
    /**
     * Log a message with hex dump (for packets)
     */
    fun logWithHex(type: EntryType, tag: String, message: String, data: ByteArray) {
        if (!isRecording.get()) return
        
        val context = appContext
        val includeHexDump = context != null && DiagnosticsLoggingConfig.isVerbosePacketLoggingEnabled(context)
        val hexDump = if (includeHexDump) data.joinToString(" ") { "%02X".format(it) } else null
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            type = type,
            tag = tag,
            message = message,
            hexDump = hexDump
        )
        addEntry(entry)
    }
    
    /**
     * Log an error with stack trace
     */
    fun logError(tag: String, message: String, error: Throwable? = null) {
        if (!isRecording.get()) return
        
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            type = EntryType.ERROR,
            tag = tag,
            message = message,
            stackTrace = error?.stackTraceToString()?.take(1000)
        )
        addEntry(entry)
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
        
        val message = buildString {
            append("→ SENT: $messageName")
            append(" (ID: 0x${messageId.toString(16).uppercase()}")
            append(", seq: $sequenceNumber")
            append(", size: ${data.size}B")
            if (reliable) append(", RELIABLE")
            append(")")
        }
        
        logWithHex(EntryType.PACKET_SENT, "UDP", message, data)
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
        
        val handlerStatus = if (handlerFound) "✓" else "⚠️ NO HANDLER"
        val message = buildString {
            append("← RECV: $messageName $handlerStatus")
            append(" (ID: 0x${messageId.toString(16).uppercase()}")
            append(", seq: $sequenceNumber")
            append(", size: ${data.size}B)")
        }
        
        logWithHex(EntryType.PACKET_RECEIVED, "UDP", message, data)
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
     * Log region change
     */
    fun logRegionChange(regionName: String, regionHandle: Long, position: String? = null) {
        if (!isRecording.get()) return
        
        val message = buildString {
            append("REGION: $regionName")
            append("\n  Handle: $regionHandle")
            position?.let { append("\n  Position: $it") }
        }
        log(EntryType.REGION, "REGION", message)
    }
    
    /**
     * Export current recording to a shareable file
     */
    fun exportLog(): File? {
        if (!isRecording.get()) {
            Log.w(TAG, "Cannot export - not recording")
            return currentLogFile
        }
        
        // Flush current buffer
        flushToFile()
        
        return currentLogFile
    }
    
    /**
     * Get the app-private path where logs are stored.
     */
    fun getLogDirectoryPath(): String {
        return getLogDirectory()?.absolutePath ?: "unavailable"
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private fun addEntry(entry: LogEntry) {
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
            Log.e(TAG, "Error flushing to file", e)
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
            Log.e(TAG, "Error writing header", e)
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
            Log.e(TAG, "Error writing footer", e)
        }
    }
    
    /**
     * Get the app-private diagnostics directory.
     */
    private fun getLogDirectory(): File? {
        val context = appContext ?: return null
        val logDir = File(DiagnosticsLoggingConfig.diagnosticsDirectory(context), LOG_DIR_NAME)

        try {
            if (!logDir.exists()) {
                logDir.mkdirs()
            }

            return logDir
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing app-private diagnostics directory: ${e.message}", e)
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
