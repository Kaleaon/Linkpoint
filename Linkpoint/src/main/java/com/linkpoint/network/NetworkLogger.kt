package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.*

/**
 * Comprehensive network activity logger for debugging connection issues.
 * 
 * Based on Lumiya's network logging patterns. Provides automatic logging of:
 * - All HTTP requests/responses with timing
 * - Network errors with full stack traces
 * - Connection state changes
 * - Retry attempts with backoff timing
 * - SSL/TLS handshake information
 * - DNS resolution results
 * - Automatic saving to Downloads/Lumiya Logs/ directory
 * 
 * All logs are tagged for easy filtering in logcat:
 * - `adb logcat NetworkLogger:D *:S` - Only network logs
 * - `adb logcat NetworkLogger:V *:S` - Verbose network logs
 */
object NetworkLogger {
    
    private const val TAG = "NetworkLogger"
    private const val MAX_LOG_ENTRIES = 1000
    private const val LOG_DIR_NAME = "Lumiya Logs"
    private const val AUTO_SAVE_INTERVAL_MS = 30000L // Auto-save every 30 seconds
    
    // Context for file operations
    private var appContext: Context? = null
    
    // Auto-save job
    private var autoSaveJob: Job? = null
    private val autoSaveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Log levels matching Lumiya's verbosity
    enum class Level {
        VERBOSE,  // Every detail including request/response bodies
        DEBUG,    // Request/response headers and timing
        INFO,     // High-level operations (login, connect, disconnect)
        WARN,     // Recoverable issues (retries, timeouts)
        ERROR     // Failures requiring user attention
    }
    
    // Current log level - can be changed at runtime
    @Volatile
    var logLevel: Level = Level.DEBUG
    
    // In-memory log buffer for export/debugging
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()
    
    // Date formatter for timestamps
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    
    // Current log file
    private var currentLogFile: File? = null
    private var logFileWriter: java.io.BufferedWriter? = null
    
    /**
     * Initialize the logger with application context.
     * This must be called before any logging occurs, preferably in Application.onCreate()
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        startAutoSave()
        Log.i(TAG, "NetworkLogger initialized, auto-save enabled to Downloads/$LOG_DIR_NAME/")
    }
    
    /**
     * Start automatic log saving
     */
    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = autoSaveScope.launch {
            while (isActive) {
                delay(AUTO_SAVE_INTERVAL_MS)
                try {
                    saveLogsToFile()
                } catch (e: Exception) {
                    Log.e(TAG, "Auto-save failed: ${e.message}", e)
                }
            }
        }
    }
    
    /**
     * Get the log directory in Downloads folder
     */
    private fun getLogDirectory(): File? {
        val context = appContext ?: return null
        
        // For Android 10+ (API 29+), use MediaStore or app-specific directory
        // For now, use the public Downloads directory which doesn't require runtime permissions on Android 10+
        val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - use app-specific directory in Downloads
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), LOG_DIR_NAME)
        } else {
            // Android 9 and below - use public Downloads directory
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), LOG_DIR_NAME)
        }
        
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        return downloadsDir
    }
    
    /**
     * Save current logs to a file
     */
    fun saveLogsToFile(): File? {
        val logDir = getLogDirectory() ?: run {
            Log.w(TAG, "Cannot get log directory - context not initialized")
            return null
        }
        
        if (logBuffer.isEmpty()) {
            return null // Nothing to save
        }
        
        try {
            // Create new log file if needed
            if (currentLogFile == null || !currentLogFile!!.exists()) {
                val timestamp = fileNameFormat.format(Date())
                currentLogFile = File(logDir, "network_log_$timestamp.txt")
                logFileWriter?.close()
                logFileWriter = currentLogFile!!.bufferedWriter()
                
                // Write header
                logFileWriter?.apply {
                    write("=== Linkpoint Network Activity Log ===\n")
                    write("Started: ${timestampFormat.format(Date())}\n")
                    write("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                    write("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                    write("Log Level: $logLevel\n")
                    write("=".repeat(60) + "\n\n")
                    flush()
                }
            }
            
            // Append new log entries
            logFileWriter?.apply {
                logBuffer.forEach { entry ->
                    val timestamp = timestampFormat.format(Date(entry.timestamp))
                    write("[$timestamp] [${entry.level}] [${entry.category}]\n")
                    write("${entry.message}\n")
                    entry.exception?.let { e ->
                        write("Exception: ${e.javaClass.simpleName}: ${e.message}\n")
                        write("${e.stackTraceToString()}\n")
                    }
                    write("\n")
                }
                flush()
            }
            
            Log.d(TAG, "Saved ${logBuffer.size} log entries to ${currentLogFile?.absolutePath}")
            return currentLogFile
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save logs to file: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Close current log file and start a new one
     */
    fun rotateLogFile() {
        try {
            logFileWriter?.close()
            logFileWriter = null
            currentLogFile = null
            Log.i(TAG, "Log file rotated")
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating log file: ${e.message}", e)
        }
    }
    
    /**
     * Clean up old log files (keep last N files)
     */
    fun cleanOldLogs(keepCount: Int = 10) {
        val logDir = getLogDirectory() ?: return
        
        val logFiles = logDir.listFiles { file ->
            file.name.startsWith("network_log_") && file.name.endsWith(".txt")
        } ?: return
        
        // Sort by last modified, newest first
        val sortedFiles = logFiles.sortedByDescending { it.lastModified() }
        
        // Delete old files beyond keepCount
        sortedFiles.drop(keepCount).forEach { file ->
            try {
                if (file.delete()) {
                    Log.d(TAG, "Deleted old log file: ${file.name}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete old log: ${file.name}", e)
            }
        }
    }
    
    /**
     * Get path to current log file
     */
    fun getCurrentLogFilePath(): String? {
        return currentLogFile?.absolutePath
    }
    
    /**
     * Log entry for in-memory storage
     */
    data class LogEntry(
        val timestamp: Long,
        val level: Level,
        val category: String,
        val message: String,
        val exception: Throwable? = null
    )
    
    /**
     * Log categories for filtering
     */
    object Category {
        const val HTTP_REQUEST = "HTTP_REQ"
        const val HTTP_RESPONSE = "HTTP_RESP"
        const val HTTP_ERROR = "HTTP_ERR"
        const val CONNECTION = "CONN"
        const val SSL_TLS = "SSL"
        const val DNS = "DNS"
        const val RETRY = "RETRY"
        const val REDIRECT = "REDIRECT"
        const val TIMEOUT = "TIMEOUT"
        const val AUTHENTICATION = "AUTH"
        const val PROTOCOL = "PROTOCOL"
    }
    
    /**
     * Log an HTTP request being sent
     */
    fun logRequest(request: Request, attempt: Int = 0) {
        if (!shouldLog(Level.DEBUG)) return
        
        val message = buildString {
            if (attempt > 0) {
                append("[Attempt $attempt] ")
            }
            append("→ ${request.method} ${request.url}\n")
            append("Headers:\n")
            request.headers.forEach { (name, value) ->
                // Don't log sensitive headers in full
                val safeValue = when {
                    name.equals("Authorization", ignoreCase = true) -> "***REDACTED***"
                    name.equals("Cookie", ignoreCase = true) -> "***REDACTED***"
                    else -> value
                }
                append("  $name: $safeValue\n")
            }
            request.body?.contentLength()?.let { length ->
                append("Content-Length: $length bytes\n")
            }
            request.body?.contentType()?.let { type ->
                append("Content-Type: $type\n")
            }
        }
        
        log(Level.DEBUG, Category.HTTP_REQUEST, message.trimEnd())
    }
    
    /**
     * Log an HTTP response received
     */
    fun logResponse(response: Response, durationMs: Long) {
        if (!shouldLog(Level.DEBUG)) return
        
        val message = buildString {
            append("← ${response.code} ${response.message} (${durationMs}ms)\n")
            append("URL: ${response.request.url}\n")
            append("Protocol: ${response.protocol}\n")
            append("Headers:\n")
            response.headers.forEach { (name, value) ->
                append("  $name: $value\n")
            }
            response.body?.contentLength()?.let { length ->
                if (length >= 0) {
                    append("Content-Length: $length bytes\n")
                }
            }
            response.body?.contentType()?.let { type ->
                append("Content-Type: $type\n")
            }
        }
        
        log(Level.DEBUG, Category.HTTP_RESPONSE, message.trimEnd())
    }
    
    /**
     * Log response body (only in VERBOSE mode to avoid log spam)
     */
    fun logResponseBody(url: String, body: String) {
        if (!shouldLog(Level.VERBOSE)) return
        
        val preview = if (body.length > 500) {
            body.take(500) + "... (${body.length} total chars)"
        } else {
            body
        }
        
        log(Level.VERBOSE, Category.HTTP_RESPONSE, "Response body from $url:\n$preview")
    }
    
    /**
     * Log an HTTP error
     */
    fun logError(url: String, error: IOException, attempt: Int = 0) {
        val message = buildString {
            if (attempt > 0) {
                append("[Attempt $attempt] ")
            }
            append("✗ HTTP Error: ${error.javaClass.simpleName}\n")
            append("URL: $url\n")
            append("Message: ${error.message}\n")
            append("Stack trace:\n${error.stackTraceToString().take(500)}")
        }
        
        log(Level.ERROR, Category.HTTP_ERROR, message, error)
    }
    
    /**
     * Log a retry attempt
     */
    fun logRetry(url: String, attempt: Int, delayMs: Long, reason: String) {
        val message = "⟳ Retry $attempt for $url after ${delayMs}ms - Reason: $reason"
        log(Level.WARN, Category.RETRY, message)
    }
    
    /**
     * Log a redirect
     */
    fun logRedirect(fromUrl: String, toUrl: String, redirectCount: Int) {
        val message = "↪ Redirect #$redirectCount: $fromUrl → $toUrl"
        log(Level.INFO, Category.REDIRECT, message)
    }
    
    /**
     * Log a timeout
     */
    fun logTimeout(url: String, timeoutMs: Long, type: String) {
        val message = "⏱ Timeout ($type) after ${timeoutMs}ms for $url"
        log(Level.WARN, Category.TIMEOUT, message)
    }
    
    /**
     * Log connection state change
     */
    fun logConnectionState(oldState: String, newState: String, reason: String? = null) {
        val message = buildString {
            append("Connection: $oldState → $newState")
            reason?.let { append(" ($it)") }
        }
        log(Level.INFO, Category.CONNECTION, message)
    }
    
    /**
     * Log SSL/TLS information
     */
    fun logSSL(url: String, protocol: String, cipherSuite: String? = null) {
        val message = buildString {
            append("🔒 SSL/TLS for $url\n")
            append("Protocol: $protocol\n")
            cipherSuite?.let { append("Cipher Suite: $it\n") }
        }
        log(Level.DEBUG, Category.SSL_TLS, message.trimEnd())
    }
    
    /**
     * Log DNS resolution
     */
    fun logDNS(hostname: String, addresses: List<String>? = null, error: Throwable? = null) {
        val message = if (error != null) {
            "DNS Failed for $hostname: ${error.message}"
        } else {
            "DNS Resolved: $hostname → ${addresses?.joinToString(", ") ?: "unknown"}"
        }
        
        log(if (error != null) Level.ERROR else Level.DEBUG, Category.DNS, message, error)
    }
    
    /**
     * Log authentication details (without sensitive data)
     */
    fun logAuth(operation: String, details: Map<String, String>) {
        val message = buildString {
            append("🔑 Auth: $operation\n")
            details.forEach { (key, value) ->
                // Redact sensitive values
                val safeValue = when {
                    key.contains("password", ignoreCase = true) -> "***REDACTED***"
                    key.contains("hash", ignoreCase = true) -> "***REDACTED***"
                    key.contains("token", ignoreCase = true) -> "***REDACTED***"
                    key.contains("secret", ignoreCase = true) -> "***REDACTED***"
                    else -> value
                }
                append("  $key: $safeValue\n")
            }
        }
        log(Level.INFO, Category.AUTHENTICATION, message.trimEnd())
    }
    
    /**
     * Log protocol-level information
     */
    fun logProtocol(operation: String, details: String) {
        val message = "📡 Protocol: $operation - $details"
        log(Level.DEBUG, Category.PROTOCOL, message)
    }
    
    /**
     * Main logging function - now public for direct use
     */
    fun log(level: Level, category: String, message: String, exception: Throwable? = null) {
        if (!shouldLog(level)) return
        
        // Create log entry
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            category = category,
            message = message,
            exception = exception
        )
        
        // Add to buffer (with size limit)
        logBuffer.offer(entry)
        while (logBuffer.size > MAX_LOG_ENTRIES) {
            logBuffer.poll()
        }
        
        // Format for logcat
        val timestamp = timestampFormat.format(Date(entry.timestamp))
        val formattedMessage = "[$timestamp] [$category] $message"
        
        // Write to logcat
        when (level) {
            Level.VERBOSE -> Log.v(TAG, formattedMessage, exception)
            Level.DEBUG -> Log.d(TAG, formattedMessage, exception)
            Level.INFO -> Log.i(TAG, formattedMessage, exception)
            Level.WARN -> Log.w(TAG, formattedMessage, exception)
            Level.ERROR -> Log.e(TAG, formattedMessage, exception)
        }
    }
    
    /**
     * Check if we should log at this level
     */
    private fun shouldLog(level: Level): Boolean {
        return level.ordinal >= logLevel.ordinal
    }
    
    /**
     * Get recent logs as formatted string for export/debugging
     */
    fun getRecentLogs(maxEntries: Int = 100): String {
        return buildString {
            appendLine("=== Network Activity Log ===")
            appendLine("Log Level: $logLevel")
            appendLine("Total Entries: ${logBuffer.size}")
            appendLine("Showing last $maxEntries entries:")
            appendLine()
            
            logBuffer.toList().takeLast(maxEntries).forEach { entry ->
                val timestamp = timestampFormat.format(Date(entry.timestamp))
                appendLine("[$timestamp] [${entry.level}] [${entry.category}]")
                appendLine(entry.message)
                entry.exception?.let { e ->
                    appendLine("Exception: ${e.javaClass.simpleName}: ${e.message}")
                }
                appendLine()
            }
        }
    }
    
    /**
     * Clear the log buffer
     */
    fun clearLogs() {
        logBuffer.clear()
        Log.i(TAG, "Network logs cleared")
    }
    
    /**
     * Get statistics about logged network activity
     */
    fun getStatistics(): NetworkStatistics {
        val stats = NetworkStatistics()
        
        logBuffer.forEach { entry ->
            when (entry.level) {
                Level.ERROR -> stats.errorCount++
                Level.WARN -> stats.warningCount++
                else -> {}
            }
            
            when (entry.category) {
                Category.HTTP_REQUEST -> stats.requestCount++
                Category.HTTP_RESPONSE -> stats.responseCount++
                Category.RETRY -> stats.retryCount++
                Category.TIMEOUT -> stats.timeoutCount++
                Category.REDIRECT -> stats.redirectCount++
                else -> {}
            }
        }
        
        return stats
    }
    
    /**
     * Statistics about network activity
     */
    data class NetworkStatistics(
        var requestCount: Int = 0,
        var responseCount: Int = 0,
        var errorCount: Int = 0,
        var warningCount: Int = 0,
        var retryCount: Int = 0,
        var timeoutCount: Int = 0,
        var redirectCount: Int = 0
    ) {
        override fun toString(): String {
            return buildString {
                appendLine("Network Statistics:")
                appendLine("  Requests: $requestCount")
                appendLine("  Responses: $responseCount")
                appendLine("  Errors: $errorCount")
                appendLine("  Warnings: $warningCount")
                appendLine("  Retries: $retryCount")
                appendLine("  Timeouts: $timeoutCount")
                appendLine("  Redirects: $redirectCount")
            }
        }
    }
}
