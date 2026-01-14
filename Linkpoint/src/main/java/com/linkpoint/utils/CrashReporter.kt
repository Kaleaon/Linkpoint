package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Automated crash reporter for Linkpoint.
 * 
 * Captures uncaught exceptions, stores crash logs, and provides
 * crash reports for debugging. Integrates with LinkpointApp for
 * automatic crash detection and reporting.
 */
class CrashReporter private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "CrashReporter"
        private const val CRASH_LOG_DIR = "crash_logs"
        private const val MAX_CRASH_LOGS = 10
        private const val CRASH_LOG_PREFIX = "crash_"
        private const val CRASH_LOG_SUFFIX = ".txt"
        
        @Volatile
        private var instance: CrashReporter? = null
        
        /**
         * Initialize the crash reporter. Should be called once in Application.onCreate()
         */
        fun initialize(context: Context): CrashReporter {
            return instance ?: synchronized(this) {
                instance ?: CrashReporter(context.applicationContext).also {
                    instance = it
                    it.installUncaughtExceptionHandler()
                    Log.i(TAG, "CrashReporter initialized")
                }
            }
        }
        
        /**
         * Get the singleton instance. Must call initialize() first.
         */
        fun getInstance(): CrashReporter {
            return instance ?: throw IllegalStateException(
                "CrashReporter not initialized! Call initialize() in Application.onCreate()"
            )
        }
        
        /**
         * Check if CrashReporter has been initialized
         */
        fun isInitialized(): Boolean = instance != null
    }
    
    // Original exception handler to chain calls
    private var originalHandler: Thread.UncaughtExceptionHandler? = null
    
    // In-memory crash log queue for recent crashes
    private val recentCrashes = ConcurrentLinkedQueue<CrashReport>()
    
    // Coroutine scope for async operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Last crash info for display
    private var lastCrash: CrashReport? = null
    
    /**
     * Install the uncaught exception handler
     */
    private fun installUncaughtExceptionHandler() {
        originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                handleUncaughtException(thread, throwable)
            } catch (e: Exception) {
                Log.e(TAG, "Error in crash handler", e)
            } finally {
                // Chain to original handler (usually terminates the app)
                originalHandler?.uncaughtException(thread, throwable)
            }
        }
    }
    
    /**
     * Handle an uncaught exception
     */
    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        Log.e(TAG, "Uncaught exception on thread ${thread.name}", throwable)
        
        val crashReport = createCrashReport(thread, throwable)
        lastCrash = crashReport
        recentCrashes.add(crashReport)
        
        // Limit queue size
        while (recentCrashes.size > MAX_CRASH_LOGS) {
            recentCrashes.remove()
        }
        
        // Save crash log to file synchronously (since app is crashing)
        saveCrashLogSync(crashReport)
    }
    
    /**
     * Report a handled exception (non-fatal)
     */
    fun reportException(throwable: Throwable, context: String = "") {
        Log.w(TAG, "Reporting handled exception: $context", throwable)
        
        scope.launch {
            try {
                val report = createCrashReport(
                    thread = Thread.currentThread(),
                    throwable = throwable,
                    isFatal = false,
                    context = context
                )
                recentCrashes.add(report)
                saveCrashLogAsync(report)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to report exception", e)
            }
        }
    }
    
    /**
     * Create a crash report with device and app information
     */
    private fun createCrashReport(
        thread: Thread,
        throwable: Throwable,
        isFatal: Boolean = true,
        context: String = ""
    ): CrashReport {
        val timestamp = System.currentTimeMillis()
        val stackTrace = getStackTraceString(throwable)
        
        return CrashReport(
            timestamp = timestamp,
            threadName = thread.name,
            exceptionType = throwable.javaClass.name,
            exceptionMessage = throwable.message ?: "No message",
            stackTrace = stackTrace,
            deviceInfo = getDeviceInfo(),
            appInfo = getAppInfo(),
            isFatal = isFatal,
            context = context,
            memoryInfo = getMemoryInfo()
        )
    }
    
    /**
     * Get the full stack trace as a string
     */
    private fun getStackTraceString(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }
    
    /**
     * Get device information for crash report
     */
    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            device = Build.DEVICE,
            product = Build.PRODUCT,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            fingerprint = Build.FINGERPRINT
        )
    }
    
    /**
     * Get app information for crash report
     */
    private fun getAppInfo(): AppInfo {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            AppInfo(
                packageName = context.packageName,
                versionName = packageInfo.versionName ?: "unknown",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }
            )
        } catch (e: Exception) {
            AppInfo(
                packageName = context.packageName,
                versionName = "unknown",
                versionCode = 0
            )
        }
    }
    
    /**
     * Get memory information for crash report
     */
    private fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        return MemoryInfo(
            totalMemory = runtime.totalMemory(),
            freeMemory = runtime.freeMemory(),
            maxMemory = runtime.maxMemory(),
            usedMemory = runtime.totalMemory() - runtime.freeMemory()
        )
    }
    
    /**
     * Save crash log to file synchronously (for fatal crashes)
     */
    private fun saveCrashLogSync(report: CrashReport) {
        try {
            val crashDir = getCrashLogDirectory()
            val crashFile = File(crashDir, generateCrashLogFilename(report.timestamp))
            
            crashFile.writeText(report.toFormattedString())
            Log.i(TAG, "Crash log saved to: ${crashFile.absolutePath}")
            
            // Clean up old crash logs
            cleanupOldCrashLogs(crashDir)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        }
    }
    
    /**
     * Save crash log to file asynchronously (for non-fatal reports)
     */
    private suspend fun saveCrashLogAsync(report: CrashReport) {
        withContext(Dispatchers.IO) {
            saveCrashLogSync(report)
        }
    }
    
    /**
     * Get the crash log directory
     */
    private fun getCrashLogDirectory(): File {
        val crashDir = File(context.filesDir, CRASH_LOG_DIR)
        if (!crashDir.exists()) {
            crashDir.mkdirs()
        }
        return crashDir
    }
    
    /**
     * Generate a unique filename for the crash log
     */
    private fun generateCrashLogFilename(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val dateString = dateFormat.format(Date(timestamp))
        return "$CRASH_LOG_PREFIX$dateString$CRASH_LOG_SUFFIX"
    }
    
    /**
     * Clean up old crash logs, keeping only the most recent ones
     */
    private fun cleanupOldCrashLogs(crashDir: File) {
        val crashFiles = crashDir.listFiles { file ->
            file.name.startsWith(CRASH_LOG_PREFIX) && file.name.endsWith(CRASH_LOG_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: return
        
        if (crashFiles.size > MAX_CRASH_LOGS) {
            crashFiles.drop(MAX_CRASH_LOGS).forEach { file ->
                file.delete()
                Log.d(TAG, "Deleted old crash log: ${file.name}")
            }
        }
    }
    
    /**
     * Get all stored crash logs
     */
    fun getCrashLogs(): List<File> {
        val crashDir = getCrashLogDirectory()
        return crashDir.listFiles { file ->
            file.name.startsWith(CRASH_LOG_PREFIX) && file.name.endsWith(CRASH_LOG_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * Get the most recent crash report
     */
    fun getLastCrash(): CrashReport? = lastCrash
    
    /**
     * Get recent crashes from memory
     */
    fun getRecentCrashes(): List<CrashReport> = recentCrashes.toList()
    
    /**
     * Read a crash log file content
     */
    fun readCrashLog(file: File): String? {
        return try {
            file.readText()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read crash log: ${file.name}", e)
            null
        }
    }
    
    /**
     * Clear all crash logs
     */
    fun clearCrashLogs() {
        val crashDir = getCrashLogDirectory()
        crashDir.listFiles()?.forEach { it.delete() }
        recentCrashes.clear()
        lastCrash = null
        Log.i(TAG, "All crash logs cleared")
    }
    
    /**
     * Generate a crash report summary for sharing
     */
    fun generateCrashSummary(): String {
        val crashes = getCrashLogs()
        val recentCount = crashes.size
        
        return buildString {
            appendLine("=== Linkpoint Crash Report Summary ===")
            appendLine()
            appendLine("Total crash logs: $recentCount")
            appendLine()
            
            if (recentCount > 0) {
                appendLine("Most recent crashes:")
                crashes.take(3).forEachIndexed { index, file ->
                    appendLine("${index + 1}. ${file.name}")
                }
            }
            
            lastCrash?.let { crash ->
                appendLine()
                appendLine("Last crash details:")
                appendLine("- Time: ${formatTimestamp(crash.timestamp)}")
                appendLine("- Exception: ${crash.exceptionType}")
                appendLine("- Message: ${crash.exceptionMessage}")
                appendLine("- Thread: ${crash.threadName}")
            }
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Shutdown the crash reporter
     */
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Data class for crash reports
 */
data class CrashReport(
    val timestamp: Long,
    val threadName: String,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
    val deviceInfo: DeviceInfo,
    val appInfo: AppInfo,
    val isFatal: Boolean,
    val context: String,
    val memoryInfo: MemoryInfo
) {
    fun toFormattedString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US)
        
        return buildString {
            appendLine("╔══════════════════════════════════════════════════════════════════╗")
            appendLine("║               LINKPOINT CRASH REPORT                             ║")
            appendLine("╚══════════════════════════════════════════════════════════════════╝")
            appendLine()
            appendLine("Timestamp: ${dateFormat.format(Date(timestamp))}")
            appendLine("Fatal: $isFatal")
            if (context.isNotEmpty()) {
                appendLine("Context: $context")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ EXCEPTION DETAILS                                                 │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Thread: $threadName")
            appendLine("Exception: $exceptionType")
            appendLine("Message: $exceptionMessage")
            appendLine()
            appendLine("Stack Trace:")
            appendLine(stackTrace)
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ DEVICE INFORMATION                                                │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Manufacturer: ${deviceInfo.manufacturer}")
            appendLine("Model: ${deviceInfo.model}")
            appendLine("Device: ${deviceInfo.device}")
            appendLine("Product: ${deviceInfo.product}")
            appendLine("Android Version: ${deviceInfo.androidVersion}")
            appendLine("SDK Version: ${deviceInfo.sdkVersion}")
            appendLine("Build ID: ${deviceInfo.buildId}")
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ APP INFORMATION                                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Package: ${appInfo.packageName}")
            appendLine("Version: ${appInfo.versionName} (${appInfo.versionCode})")
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ MEMORY INFORMATION                                                │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Total Memory: ${formatBytes(memoryInfo.totalMemory)}")
            appendLine("Free Memory: ${formatBytes(memoryInfo.freeMemory)}")
            appendLine("Used Memory: ${formatBytes(memoryInfo.usedMemory)}")
            appendLine("Max Memory: ${formatBytes(memoryInfo.maxMemory)}")
            appendLine()
            
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine("End of Crash Report")
            appendLine("═══════════════════════════════════════════════════════════════════")
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) {
            String.format(Locale.US, "%.2f MB", mb)
        } else {
            String.format(Locale.US, "%.2f KB", kb)
        }
    }
}

/**
 * Device information for crash reports
 */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val device: String,
    val product: String,
    val androidVersion: String,
    val sdkVersion: Int,
    val buildId: String,
    val fingerprint: String
)

/**
 * App information for crash reports
 */
data class AppInfo(
    val packageName: String,
    val versionName: String,
    val versionCode: Long
)

/**
 * Memory information for crash reports
 */
data class MemoryInfo(
    val totalMemory: Long,
    val freeMemory: Long,
    val maxMemory: Long,
    val usedMemory: Long
)
