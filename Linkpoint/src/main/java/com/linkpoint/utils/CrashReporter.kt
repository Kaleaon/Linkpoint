package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.os.Environment
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
 * 
 * Storage Strategy:
 * 1. Primary: App-internal files directory (always available, no permissions needed)
 * 2. Secondary: External files directory (for user access via file manager)
 * 3. Fallback: Cache directory (always works but may be cleared by system)
 */
class CrashReporter private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "CrashReporter"
        private const val CRASH_LOG_DIR = "crash_logs"
        private const val EXTERNAL_CRASH_LOG_DIR = "Lumiya Logs"
        private const val MAX_CRASH_LOGS = 10
        private const val CRASH_LOG_PREFIX = "crash_"
        private const val CRASH_LOG_SUFFIX = ".txt"
        private const val INIT_TEST_FILE = ".crash_reporter_initialized"
        
        @Volatile
        private var instance: CrashReporter? = null
        
        /**
         * Initialize the crash reporter. Should be called once in Application.onCreate()
         */
        fun initialize(context: Context): CrashReporter {
            return instance ?: synchronized(this) {
                instance ?: CrashReporter(context.applicationContext).also { reporter ->
                    instance = reporter
                    reporter.initializeStorage()
                    reporter.installUncaughtExceptionHandler()
                    Log.i(TAG, "CrashReporter initialized successfully")
                    Log.i(TAG, "Crash log directories: ${reporter.getStorageInfo()}")
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
         * Safely get the singleton instance, or null if not initialized.
         */
        fun getInstanceOrNull(): CrashReporter? = instance
        
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
    
    // Initialization status
    private var initializationStatus: InitializationStatus = InitializationStatus.NOT_INITIALIZED
    private var primaryStoragePath: String? = null
    private var externalStoragePath: String? = null
    private var initializationError: String? = null
    
    /**
     * Initialization status enum
     */
    enum class InitializationStatus {
        NOT_INITIALIZED,
        INITIALIZED_PRIMARY_ONLY,
        INITIALIZED_WITH_EXTERNAL,
        INITIALIZATION_FAILED
    }
    
    /**
     * Initialize storage directories and verify they are writable.
     * Uses multiple fallback locations to ensure crash logs can always be saved.
     */
    private fun initializeStorage() {
        try {
            // Primary storage: App-internal files directory (always available)
            val primaryDir = File(context.filesDir, CRASH_LOG_DIR)
            if (createAndVerifyDirectory(primaryDir)) {
                primaryStoragePath = primaryDir.absolutePath
                Log.i(TAG, "Primary crash log storage initialized: $primaryStoragePath")
            } else {
                // Fallback to cache directory
                val cacheDir = File(context.cacheDir, CRASH_LOG_DIR)
                if (createAndVerifyDirectory(cacheDir)) {
                    primaryStoragePath = cacheDir.absolutePath
                    Log.w(TAG, "Using cache directory for crash logs: $primaryStoragePath")
                }
            }
            
            // External storage: User-accessible directory (may not be available)
            try {
                val externalDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ - use app-specific external directory
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
                        File(it, EXTERNAL_CRASH_LOG_DIR)
                    }
                } else {
                    // Android 9 and below - use public Downloads directory
                    @Suppress("DEPRECATION")
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), EXTERNAL_CRASH_LOG_DIR)
                }
                
                if (externalDir != null && createAndVerifyDirectory(externalDir)) {
                    externalStoragePath = externalDir.absolutePath
                    Log.i(TAG, "External crash log storage initialized: $externalStoragePath")
                }
            } catch (e: Exception) {
                Log.w(TAG, "External storage not available: ${e.message}")
            }
            
            // Determine initialization status
            initializationStatus = when {
                primaryStoragePath != null && externalStoragePath != null -> {
                    InitializationStatus.INITIALIZED_WITH_EXTERNAL
                }
                primaryStoragePath != null -> {
                    InitializationStatus.INITIALIZED_PRIMARY_ONLY
                }
                else -> {
                    initializationError = "Could not create any crash log directory"
                    InitializationStatus.INITIALIZATION_FAILED
                }
            }
            
            // Write test file to verify storage works
            if (primaryStoragePath != null) {
                writeInitializationTestFile()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash log storage", e)
            initializationStatus = InitializationStatus.INITIALIZATION_FAILED
            initializationError = e.message
        }
    }
    
    /**
     * Create a directory and verify it's writable.
     */
    private fun createAndVerifyDirectory(dir: File): Boolean {
        return try {
            if (!dir.exists()) {
                val created = dir.mkdirs()
                if (!created) {
                    Log.w(TAG, "Failed to create directory: ${dir.absolutePath}")
                    return false
                }
            }
            
            // Verify directory is writable
            if (!dir.canWrite()) {
                Log.w(TAG, "Directory is not writable: ${dir.absolutePath}")
                return false
            }
            
            // Try to write a test file
            val testFile = File(dir, ".write_test")
            try {
                testFile.writeText("test")
                testFile.delete()
                true
            } catch (e: Exception) {
                Log.w(TAG, "Cannot write to directory: ${dir.absolutePath} - ${e.message}")
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Directory verification failed: ${e.message}")
            false
        }
    }
    
    /**
     * Write a test file to verify initialization worked.
     */
    private fun writeInitializationTestFile() {
        try {
            val testFile = File(primaryStoragePath!!, INIT_TEST_FILE)
            val content = buildString {
                appendLine("CrashReporter Initialization Test")
                appendLine("Timestamp: ${Date()}")
                appendLine("Primary Storage: $primaryStoragePath")
                appendLine("External Storage: ${externalStoragePath ?: "Not available"}")
                appendLine("Status: $initializationStatus")
            }
            testFile.writeText(content)
            Log.d(TAG, "Initialization test file written: ${testFile.absolutePath}")
        } catch (e: Exception) {
            Log.w(TAG, "Could not write initialization test file: ${e.message}")
        }
    }
    
    /**
     * Get storage information for debugging.
     */
    fun getStorageInfo(): String = buildString {
        append("Primary: ${primaryStoragePath ?: "NOT SET"}")
        externalStoragePath?.let { append(", External: $it") }
        append(" (Status: $initializationStatus)")
    }
    
    /**
     * Get detailed initialization diagnostics.
     */
    fun getDiagnostics(): CrashReporterDiagnostics {
        val primaryLogs = primaryStoragePath?.let { File(it).listFiles()?.size ?: 0 } ?: 0
        val externalLogs = externalStoragePath?.let { File(it).listFiles()?.size ?: 0 } ?: 0
        
        return CrashReporterDiagnostics(
            status = initializationStatus,
            primaryStoragePath = primaryStoragePath,
            externalStoragePath = externalStoragePath,
            initializationError = initializationError,
            crashLogsInPrimary = primaryLogs,
            crashLogsInExternal = externalLogs,
            recentCrashesInMemory = recentCrashes.size,
            lastCrashTimestamp = lastCrash?.timestamp
        )
    }
    
    /**
     * Force re-initialization of storage (useful for debugging).
     */
    fun reinitializeStorage(): Boolean {
        Log.i(TAG, "Reinitializing crash log storage...")
        initializeStorage()
        return initializationStatus != InitializationStatus.INITIALIZATION_FAILED
    }
    
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
     * Saves to both primary (internal) and external storage if available.
     */
    private fun saveCrashLogSync(report: CrashReport) {
        val filename = generateCrashLogFilename(report.timestamp)
        val content = report.toFormattedString()
        var savedToPrimary = false
        var savedToExternal = false
        
        // Save to primary storage (internal - always available)
        primaryStoragePath?.let { path ->
            try {
                val crashDir = File(path)
                if (!crashDir.exists()) crashDir.mkdirs()
                
                val crashFile = File(crashDir, filename)
                crashFile.writeText(content)
                Log.i(TAG, "Crash log saved to primary: ${crashFile.absolutePath}")
                savedToPrimary = true
                
                // Clean up old crash logs in primary
                cleanupOldCrashLogs(crashDir)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save crash log to primary storage", e)
            }
        }
        
        // Also save to external storage (user-accessible) if available
        externalStoragePath?.let { path ->
            try {
                val crashDir = File(path)
                if (!crashDir.exists()) crashDir.mkdirs()
                
                val crashFile = File(crashDir, filename)
                crashFile.writeText(content)
                Log.i(TAG, "Crash log saved to external: ${crashFile.absolutePath}")
                savedToExternal = true
                
                // Clean up old crash logs in external
                cleanupOldCrashLogs(crashDir)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to save crash log to external storage: ${e.message}")
            }
        }
        
        // Fallback: if both failed, try cache directory
        if (!savedToPrimary && !savedToExternal) {
            try {
                val cacheDir = File(context.cacheDir, CRASH_LOG_DIR)
                if (!cacheDir.exists()) cacheDir.mkdirs()
                
                val crashFile = File(cacheDir, filename)
                crashFile.writeText(content)
                Log.w(TAG, "Crash log saved to cache (fallback): ${crashFile.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "CRITICAL: Could not save crash log anywhere!", e)
            }
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
     * Get the primary crash log directory.
     * Uses the initialized storage path or falls back to internal storage.
     */
    private fun getCrashLogDirectory(): File {
        // Use the pre-initialized path if available
        primaryStoragePath?.let { path ->
            val dir = File(path)
            if (dir.exists() || dir.mkdirs()) {
                return dir
            }
        }
        
        // Fallback to internal files directory
        val fallbackDir = File(context.filesDir, CRASH_LOG_DIR)
        if (!fallbackDir.exists()) {
            fallbackDir.mkdirs()
        }
        
        Log.d(TAG, "Crash log directory: ${fallbackDir.absolutePath}")
        return fallbackDir
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
     * Get all stored crash logs from all storage locations.
     * Returns a combined list sorted by last modified time.
     */
    fun getCrashLogs(): List<File> {
        val allLogs = mutableListOf<File>()
        
        // Get logs from primary storage
        primaryStoragePath?.let { path ->
            val dir = File(path)
            dir.listFiles { file ->
                file.name.startsWith(CRASH_LOG_PREFIX) && file.name.endsWith(CRASH_LOG_SUFFIX)
            }?.let { allLogs.addAll(it) }
        }
        
        // Get logs from external storage (if different from primary)
        externalStoragePath?.let { path ->
            if (path != primaryStoragePath) {
                val dir = File(path)
                dir.listFiles { file ->
                    file.name.startsWith(CRASH_LOG_PREFIX) && file.name.endsWith(CRASH_LOG_SUFFIX)
                }?.let { allLogs.addAll(it) }
            }
        }
        
        // Also check cache directory for any fallback logs
        val cacheDir = File(context.cacheDir, CRASH_LOG_DIR)
        if (cacheDir.exists()) {
            cacheDir.listFiles { file ->
                file.name.startsWith(CRASH_LOG_PREFIX) && file.name.endsWith(CRASH_LOG_SUFFIX)
            }?.let { allLogs.addAll(it) }
        }
        
        // Remove duplicates (same filename) and sort by last modified
        return allLogs
            .distinctBy { it.name }
            .sortedByDescending { it.lastModified() }
    }
    
    /**
     * Get all crash log directories that are in use.
     */
    fun getCrashLogDirectories(): List<File> {
        val dirs = mutableListOf<File>()
        primaryStoragePath?.let { dirs.add(File(it)) }
        externalStoragePath?.let { if (it != primaryStoragePath) dirs.add(File(it)) }
        return dirs
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

/**
 * Diagnostics information for the crash reporter.
 * Used for debugging crash reporter initialization and storage issues.
 */
data class CrashReporterDiagnostics(
    val status: CrashReporter.InitializationStatus,
    val primaryStoragePath: String?,
    val externalStoragePath: String?,
    val initializationError: String?,
    val crashLogsInPrimary: Int,
    val crashLogsInExternal: Int,
    val recentCrashesInMemory: Int,
    val lastCrashTimestamp: Long?
) {
    /**
     * Generate a human-readable diagnostic report.
     */
    fun toReport(): String = buildString {
        appendLine("=== CrashReporter Diagnostics ===")
        appendLine()
        appendLine("Status: $status")
        appendLine()
        appendLine("Storage Locations:")
        appendLine("  Primary: ${primaryStoragePath ?: "NOT CONFIGURED"}")
        appendLine("  External: ${externalStoragePath ?: "NOT AVAILABLE"}")
        appendLine()
        appendLine("Crash Logs:")
        appendLine("  In Primary Storage: $crashLogsInPrimary")
        appendLine("  In External Storage: $crashLogsInExternal")
        appendLine("  In Memory Queue: $recentCrashesInMemory")
        appendLine()
        if (lastCrashTimestamp != null) {
            // Use thread-safe formatting
            val formattedDate = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.time.Instant.ofEpochMilli(lastCrashTimestamp)
                    .atZone(java.time.ZoneId.systemDefault())
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } else {
                // SimpleDateFormat for older Android versions (only used locally, not shared)
                @Suppress("SimpleDateFormat")
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
                    .format(java.util.Date(lastCrashTimestamp))
            }
            appendLine("Last Crash: $formattedDate")
        } else {
            appendLine("Last Crash: None recorded")
        }
        appendLine()
        if (initializationError != null) {
            appendLine("⚠️ Initialization Error: $initializationError")
        }
        appendLine("================================")
    }
    
    /**
     * Check if crash reporting is working properly.
     */
    fun isWorking(): Boolean = status != CrashReporter.InitializationStatus.INITIALIZATION_FAILED &&
        status != CrashReporter.InitializationStatus.NOT_INITIALIZED
}
