package com.linkpoint.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thread-safe Session Log Recorder using coroutines.
 * 
 * This implementation replaces unsafe threading with coroutines
 * for thread-safe, efficient logging operations.
 * 
 * Features:
 * - Thread-safe logging operations
 * - Asynchronous file writes
 * - Automatic log rotation
 * - Memory-efficient buffering
 * - Graceful shutdown
 * 
 * Thread-safe: All operations are thread-safe
 * 
 * @property context Application context
 */
class SessionLogRecorder(private val context: Context) {
    
    companion object {
        private const val TAG = "SessionLogRecorder"
        private const val LOG_FILE = "session_log.txt"
        private const val MAX_LOG_SIZE = 5 * 1024 * 1024 // 5MB
        private const val LOG_RETENTION_DAYS = 7
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
        private const val LOG_CHANNEL_CAPACITY = 2048
    }
    
    // Log entry data class
    data class LogEntry(
        val timestamp: Long,
        val level: Int,
        val tag: String,
        val message: String,
        val threadName: String
    )
    
    // Channel for thread-safe log submission
    private val logChannel = Channel<LogEntry>(
        capacity = LOG_CHANNEL_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    // Coroutine scope for log processing
    private val scope = CoroutineScope(
        Dispatchers.IO + 
        SupervisorJob() + 
        CoroutineName("SessionLogRecorder")
    )
    
    // State tracking
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording
    
    private val isInitialized = AtomicBoolean(false)
    private val isShuttingDown = AtomicBoolean(false)
    
    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
    
    init {
        if (isInitialized.compareAndSet(false, true)) {
            startLogProcessor()
            _isRecording.value = true
            Log.d(TAG, "SessionLogRecorder initialized")
        }
    }
    
    /**
     * Start the log processing coroutine
     */
    private fun startLogProcessor() {
        scope.launch {
            processLogs()
        }
    }
    
    /**
     * Process log entries from channel and write to file
     */
    private suspend fun processLogs() {
        for (entry in logChannel) {
            if (isShuttingDown.get()) {
                break
            }
            
            try {
                writeLogEntry(entry)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write log entry", e)
            }
        }
        
        Log.d(TAG, "Log processor stopped")
    }
    
    /**
     * Write a log entry to file
     */
    private suspend fun writeLogEntry(entry: LogEntry) = withContext(Dispatchers.IO) {
        val logFile = getLogFile()
        
        // Check file size and rotate if needed
        if (logFile.length() > MAX_LOG_SIZE) {
            rotateLogs()
        }
        
        // Format log entry
        val formattedDate = dateFormat.format(Date(entry.timestamp))
        val logLine = "$formattedDate ${entry.level}/${entry.tag} [${entry.threadName}]: ${entry.message}\n"
        
        // Write to file
        try {
            FileWriter(logFile, true).use { writer ->
                writer.write(logLine)
                writer.flush()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    /**
     * Add a log entry (thread-safe)
     */
    fun addLog(level: Int, tag: String, message: String) {
        if (isShuttingDown.get() || !_isRecording.value) {
            return
        }
        
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            tag = tag,
            message = message,
            threadName = Thread.currentThread().name
        )
        
        val result = logChannel.trySend(entry)
        if (!result.isSuccess) {
            Log.w(TAG, "Dropped log entry due to channel backpressure")
        }
    }
    
    /**
     * Add a debug log entry
     */
    fun debug(tag: String, message: String) {
        addLog(android.util.Log.DEBUG, tag, message)
    }
    
    /**
     * Add an info log entry
     */
    fun info(tag: String, message: String) {
        addLog(android.util.Log.INFO, tag, message)
    }
    
    /**
     * Add a warning log entry
     */
    fun warn(tag: String, message: String) {
        addLog(android.util.Log.WARN, tag, message)
    }
    
    /**
     * Add an error log entry
     */
    fun error(tag: String, message: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) {
            "$message\n${Log.getStackTraceString(throwable)}"
        } else {
            message
        }
        addLog(android.util.Log.ERROR, tag, fullMessage)
    }
    
    /**
     * Get the log file
     */
    private fun getLogFile(): File {
        return File(context.filesDir, LOG_FILE)
    }
    
    /**
     * Rotate log files (keep old logs)
     */
    private suspend fun rotateLogs() = withContext(Dispatchers.IO) {
        try {
            val currentFile = getLogFile()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val rotatedFile = File(context.filesDir, "session_log_$timestamp.txt")
            
            currentFile.renameTo(rotatedFile)
            
            // Clean up old log files
            cleanOldLogs()
            
            Log.d(TAG, "Log rotated to ${rotatedFile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rotate logs", e)
        }
    }
    
    /**
     * Clean up old log files
     */
    private suspend fun cleanOldLogs() = withContext(Dispatchers.IO) {
        try {
            val cutoffTime = System.currentTimeMillis() - (LOG_RETENTION_DAYS * 24 * 60 * 60 * 1000L)
            val filesDir = context.filesDir
            
            filesDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("session_log_") && 
                    file.lastModified() < cutoffTime) {
                    file.delete()
                    Log.d(TAG, "Deleted old log file: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clean old logs", e)
        }
    }
    
    /**
     * Get all log file contents
     */
    suspend fun getLogContents(): String = withContext(Dispatchers.IO) {
        try {
            val logFile = getLogFile()
            if (logFile.exists()) {
                logFile.readText()
            } else {
                "No log file exists"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read log file", e)
            "Error reading log file: ${e.message}"
        }
    }
    
    /**
     * Clear the current log file
     */
    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        try {
            val logFile = getLogFile()
            if (logFile.exists()) {
                logFile.delete()
                Log.d(TAG, "Log file cleared")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear log file", e)
        }
    }
    
    /**
     * Stop recording and cleanup
     */
    fun shutdown() {
        if (isShuttingDown.compareAndSet(false, true)) {
            _isRecording.value = false
            
            // Cancel scope
            scope.cancel()
            
            // Close channel
            logChannel.close()
            
            Log.d(TAG, "SessionLogRecorder shut down")
        }
    }
    
    /**
     * Get current log file size in bytes
     */
    fun getLogFileSize(): Long {
        return try {
            val logFile = getLogFile()
            if (logFile.exists()) {
                logFile.length()
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get log file size", e)
            0L
        }
    }
}
