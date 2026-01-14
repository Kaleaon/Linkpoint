package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.LinkpointApp
import com.linkpoint.assets.CacheManager
import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Debug Report Service for Linkpoint.
 * 
 * Captures the current app state including:
 * - Connection status
 * - Session information
 * - Memory usage
 * - Device info
 * - Recent network activity and packet issues
 * - Cache statistics (textures, sounds, meshes, animations)
 * - Current region/avatar info
 * - Error logs for debugging loading issues
 * 
 * Reports are saved to the debug_reports directory for loading and sharing.
 */
class DebugReportService private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "DebugReportService"
        private const val DEBUG_REPORT_DIR = "debug_reports"
        private const val MAX_REPORTS = 20
        private const val REPORT_PREFIX = "debug_report_"
        private const val REPORT_SUFFIX = ".txt"
        
        @Volatile
        private var instance: DebugReportService? = null
        
        fun getInstance(context: Context): DebugReportService {
            return instance ?: synchronized(this) {
                instance ?: DebugReportService(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        fun getInstanceOrNull(): DebugReportService? = instance
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reportDirectory: File? = null
    
    init {
        initializeStorage()
    }
    
    private fun initializeStorage() {
        try {
            val dir = File(context.filesDir, DEBUG_REPORT_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            reportDirectory = dir
            Log.i(TAG, "Debug report directory initialized: ${dir.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize debug report storage", e)
        }
    }
    
    /**
     * Capture a debug report of the current app state.
     * Returns the file path of the saved report, or null if capture failed.
     * This is a suspend function to avoid blocking the main thread.
     */
    suspend fun captureDebugReport(userNote: String = ""): File? {
        return try {
            val report = generateDebugReport(userNote)
            val file = saveReport(report)
            cleanupOldReports()
            Log.i(TAG, "Debug report captured: ${file?.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to capture debug report", e)
            null
        }
    }
    
    /**
     * Capture debug report asynchronously
     */
    fun captureDebugReportAsync(userNote: String = "", callback: (File?) -> Unit) {
        scope.launch {
            val file = captureDebugReport(userNote)
            withContext(Dispatchers.Main) {
                callback(file)
            }
        }
    }
    
    /**
     * Generate the debug report content
     */
    private suspend fun generateDebugReport(userNote: String): String {
        val timestamp = System.currentTimeMillis()
        val app = try { LinkpointApp.getInstance() } catch (e: Exception) { null }
        
        return buildString {
            appendLine("╔══════════════════════════════════════════════════════════════════╗")
            appendLine("║               LINKPOINT DEBUG REPORT                              ║")
            appendLine("╚══════════════════════════════════════════════════════════════════╝")
            appendLine()
            appendLine("Timestamp: ${formatTimestamp(timestamp)}")
            appendLine("Report ID: ${UUID.randomUUID()}")
            appendLine()
            
            if (userNote.isNotEmpty()) {
                appendLine("┌──────────────────────────────────────────────────────────────────┐")
                appendLine("│ USER NOTE                                                         │")
                appendLine("└──────────────────────────────────────────────────────────────────┘")
                appendLine()
                appendLine(userNote)
                appendLine()
            }
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ CONNECTION STATUS                                                 │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                appendLine("Connected: ${app.isConnected()}")
                appendLine("Current Region: ${app.getCurrentRegion() ?: "None"}")
                appendLine("Agent ID: ${app.agentId ?: "Not logged in"}")
                try {
                    appendLine("Avatar Name: ${app.sessionManager.getAvatarName()}")
                    appendLine("Connection State: ${app.sessionManager.connectionState.value}")
                } catch (e: Exception) {
                    appendLine("Session Info: Unable to retrieve - ${e.message}")
                }
            } else {
                appendLine("App instance not available")
            }
            appendLine()
            
            // Network activity and potential packet issues
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ NETWORK ACTIVITY & PACKET STATUS                                  │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val networkStats = NetworkLogger.getStatistics()
                appendLine("HTTP Requests: ${networkStats.requestCount}")
                appendLine("HTTP Responses: ${networkStats.responseCount}")
                appendLine("Errors: ${networkStats.errorCount}")
                appendLine("Warnings: ${networkStats.warningCount}")
                appendLine("Retries: ${networkStats.retryCount}")
                appendLine("Timeouts: ${networkStats.timeoutCount}")
                appendLine("Redirects: ${networkStats.redirectCount}")
                appendLine()
                
                // Include recent network errors
                val recentLogs = NetworkLogger.getRecentLogs(20)
                val errorLines = recentLogs.lines().filter { 
                    it.contains("ERROR", ignoreCase = true) || 
                    it.contains("WARN", ignoreCase = true) ||
                    it.contains("failed", ignoreCase = true) ||
                    it.contains("timeout", ignoreCase = true)
                }.take(15)
                
                if (errorLines.isNotEmpty()) {
                    appendLine("Recent Network Issues:")
                    errorLines.forEach { appendLine("  $it") }
                } else {
                    appendLine("No recent network errors detected")
                }
            } catch (e: Exception) {
                appendLine("Network stats unavailable: ${e.message}")
            }
            appendLine()
            
            // Cache statistics - comprehensive breakdown
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ CACHE STATISTICS                                                  │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val cacheManager = CacheManager(context)
                // Get cache stats using withContext since generateDebugReport is now a suspend function
                val cacheStats = withContext(Dispatchers.IO) {
                    cacheManager.getCacheStats()
                }
                appendLine("Total Cache Size: ${cacheStats.getFormattedTotalSize()} / ${cacheStats.getFormattedMaxSize()} (${cacheStats.usagePercent}%)")
                appendLine("Total Files: ${cacheStats.totalFileCount}")
                appendLine("Available Space: ${formatBytes(cacheStats.availableSpaceBytes)}")
                appendLine("Low Space Warning: ${if (cacheStats.isLowSpace) "YES ⚠️" else "No"}")
                appendLine()
                appendLine("Cache Breakdown:")
                appendLine("  Textures: ${formatBytes(cacheStats.texturesSizeBytes)} (${cacheStats.texturesCount} files)")
                appendLine("  Meshes: ${formatBytes(cacheStats.meshesSizeBytes)} (${cacheStats.meshesCount} files)")
                appendLine("  Sounds: ${formatBytes(cacheStats.soundsSizeBytes)} (${cacheStats.soundsCount} files)")
                appendLine("  Animations: ${formatBytes(cacheStats.animationsSizeBytes)} (${cacheStats.animationsCount} files)")
                appendLine("  General: ${formatBytes(cacheStats.generalSizeBytes)} (${cacheStats.generalCount} files)")
            } catch (e: Exception) {
                appendLine("Cache statistics unavailable: ${e.message}")
            }
            appendLine()
            
            // Asset cache memory statistics
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ ASSET CACHE MEMORY                                                │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val assetCacheStats = app.assetCache.getStats()
                    appendLine("Memory Cache:")
                    appendLine("  Size: ${formatBytes(assetCacheStats.memorySizeBytes)} / ${formatBytes(assetCacheStats.memoryMaxBytes)}")
                    appendLine("  Hit Count: ${assetCacheStats.memoryHitCount}")
                    appendLine("  Miss Count: ${assetCacheStats.memoryMissCount}")
                    appendLine("  Hit Rate: ${String.format("%.1f%%", assetCacheStats.memoryHitRate * 100)}")
                    appendLine()
                    appendLine("Disk Cache:")
                    appendLine("  Size: ${formatBytes(assetCacheStats.diskSizeBytes)}")
                    appendLine("  Asset Count: ${assetCacheStats.diskAssetCount}")
                } catch (e: Exception) {
                    appendLine("Asset cache stats unavailable: ${e.message}")
                }
            } else {
                appendLine("Asset cache: App not initialized")
            }
            appendLine()
            
            // Texture manager statistics
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ TEXTURE LOADING STATUS                                            │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val textureStats = app.textureManager.stats.value
                    appendLine("Pending Downloads: ${textureStats.pendingDownloads}")
                    appendLine("Downloaded: ${textureStats.downloadedCount}")
                    appendLine("Downloaded Bytes: ${formatBytes(textureStats.downloadedBytes)}")
                    appendLine("Failed Downloads: ${textureStats.failedCount}")
                    appendLine("Decoded: ${textureStats.decodedCount}")
                    appendLine("Decode Failures: ${textureStats.decodeFailedCount}")
                    
                    if (textureStats.failedCount > 0 || textureStats.decodeFailedCount > 0) {
                        appendLine()
                        appendLine("⚠️ Texture loading issues detected - may cause missing textures")
                    }
                } catch (e: Exception) {
                    appendLine("Texture stats unavailable: ${e.message}")
                }
            } else {
                appendLine("Texture manager: App not initialized")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ DEVICE INFORMATION                                                │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Android Version: ${Build.VERSION.RELEASE}")
            appendLine("SDK Version: ${Build.VERSION.SDK_INT}")
            appendLine("Build ID: ${Build.ID}")
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ APP INFORMATION                                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                appendLine("Package: ${context.packageName}")
                appendLine("Version: ${packageInfo.versionName}")
                appendLine("Version Code: ${getVersionCode(packageInfo)}")
            } catch (e: Exception) {
                appendLine("App Info: Unable to retrieve")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ MEMORY USAGE                                                      │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory
            val maxMemory = runtime.maxMemory()
            appendLine("Total Memory: ${formatBytes(totalMemory)}")
            appendLine("Used Memory: ${formatBytes(usedMemory)}")
            appendLine("Free Memory: ${formatBytes(freeMemory)}")
            appendLine("Max Memory: ${formatBytes(maxMemory)}")
            appendLine("Memory Usage: ${(usedMemory * 100 / maxMemory)}%")
            if (usedMemory * 100 / maxMemory > 80) {
                appendLine("⚠️ High memory usage - may cause performance issues")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ XR STATUS                                                         │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                appendLine("XR Available: ${app.isXRAvailable()}")
            } else {
                appendLine("XR Status: Unable to determine")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ CRASH REPORTER STATUS                                             │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            val crashReporter = CrashReporter.getInstanceOrNull()
            if (crashReporter != null) {
                val diagnostics = crashReporter.getDiagnostics()
                appendLine("Status: ${diagnostics.status}")
                appendLine("Crash Logs: ${crashReporter.getCrashLogs().size}")
                appendLine("Storage: ${crashReporter.getStorageInfo()}")
            } else {
                appendLine("Crash Reporter: Not initialized")
            }
            appendLine()
            
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ THREAD INFORMATION                                                │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            appendLine("Active Thread Count: ${Thread.activeCount()}")
            appendLine("Current Thread: ${Thread.currentThread().name}")
            appendLine()
            
            // Recent network log excerpt for debugging loading issues
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ RECENT NETWORK LOG (Last 30 entries)                              │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val recentLogs = NetworkLogger.getRecentLogs(30)
                appendLine(recentLogs)
            } catch (e: Exception) {
                appendLine("Network logs unavailable: ${e.message}")
            }
            appendLine()
            
            appendLine("═══════════════════════════════════════════════════════════════════")
            appendLine("End of Debug Report")
            appendLine("═══════════════════════════════════════════════════════════════════")
        }
    }
    
    private fun saveReport(content: String): File? {
        val dir = reportDirectory ?: return null
        val filename = generateReportFilename()
        val file = File(dir, filename)
        
        return try {
            file.writeText(content)
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save debug report", e)
            null
        }
    }
    
    private fun generateReportFilename(): String {
        val timestamp = System.currentTimeMillis()
        val dateString = formatDateWithPattern(timestamp, "yyyy-MM-dd_HH-mm-ss")
        return "$REPORT_PREFIX$dateString$REPORT_SUFFIX"
    }
    
    /**
     * Get version code from package info, handling API level differences
     */
    private fun getVersionCode(packageInfo: android.content.pm.PackageInfo): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }
    
    private fun cleanupOldReports() {
        val dir = reportDirectory ?: return
        val reports = dir.listFiles { file ->
            file.name.startsWith(REPORT_PREFIX) && file.name.endsWith(REPORT_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: return
        
        if (reports.size > MAX_REPORTS) {
            reports.drop(MAX_REPORTS).forEach { file ->
                file.delete()
                Log.d(TAG, "Deleted old debug report: ${file.name}")
            }
        }
    }
    
    /**
     * Get all stored debug reports
     */
    fun getDebugReports(): List<File> {
        val dir = reportDirectory ?: return emptyList()
        return dir.listFiles { file ->
            file.name.startsWith(REPORT_PREFIX) && file.name.endsWith(REPORT_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * Read a debug report file
     */
    fun readReport(file: File): String? {
        return try {
            file.readText()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read debug report", e)
            null
        }
    }
    
    /**
     * Clear all debug reports
     */
    fun clearReports() {
        val dir = reportDirectory ?: return
        dir.listFiles()?.forEach { it.delete() }
        Log.i(TAG, "All debug reports cleared")
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        return formatDateWithPattern(timestamp, "yyyy-MM-dd HH:mm:ss.SSS Z")
    }
    
    /**
     * Helper method to format a timestamp with a given pattern.
     * Handles API level differences for date formatting.
     */
    private fun formatDateWithPattern(timestamp: Long, pattern: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern(pattern))
        } else {
            SimpleDateFormat(pattern, Locale.US).format(Date(timestamp))
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1 -> String.format(Locale.US, "%.2f MB", mb)
            kb >= 1 -> String.format(Locale.US, "%.2f KB", kb)
            else -> "$bytes B"
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}
