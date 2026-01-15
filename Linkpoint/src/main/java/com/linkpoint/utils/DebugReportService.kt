package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.LinkpointApp
import com.linkpoint.assets.CacheManager
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.core.ConnectionQualityManager
import com.linkpoint.network.core.NetworkStateManager
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
        
        // Truncation length for URLs in debug reports
        private const val DIAGNOSTIC_URL_TRUNCATE_LENGTH = 50
        
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
            
            // ==================== NEW DETAILED DIAGNOSTIC SECTIONS ====================
            
            // UDP Connection Status - CRITICAL for understanding why world data isn't loading
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ UDP CONNECTION STATUS (Simulator Protocol)                        │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val udpDiag = app.udpConnection.getDiagnostics()
                    appendLine("UDP Connected: ${udpDiag.isConnected}")
                    appendLine("Simulator IP: ${udpDiag.simIP.ifEmpty { "Not configured" }}")
                    appendLine("Simulator Port: ${if (udpDiag.simPort > 0) udpDiag.simPort else "Not configured"}")
                    appendLine("Circuit Code: ${if (udpDiag.circuitCode != 0) udpDiag.circuitCode else "Not set"}")
                    appendLine("Socket Open: ${udpDiag.socketOpen}")
                    appendLine("Receive Loop Active: ${udpDiag.receiveLoopActive}")
                    appendLine()
                    appendLine("Packet Statistics:")
                    appendLine("  Sequence Number (packets sent): ${udpDiag.sequenceNumber}")
                    appendLine("  Pending ACKs: ${udpDiag.pendingAckCount}")
                    appendLine("  Registered Handlers: ${udpDiag.registeredHandlerCount}")
                    appendLine()
                    if (udpDiag.registeredHandlers.isNotEmpty()) {
                        appendLine("Registered Message Handlers:")
                        udpDiag.registeredHandlers.forEach { handler ->
                            appendLine("  - $handler")
                        }
                    } else {
                        appendLine("⚠️ No message handlers registered - UDP messages won't be processed!")
                    }
                    appendLine()
                    if (udpDiag.pendingAckCount > 0) {
                        appendLine("Pending Packets (awaiting ACK):")
                        udpDiag.pendingPackets.take(5).forEach { packet ->
                            appendLine("  - Seq ${packet.seqNum}: ${packet.retries} retries, ${packet.ageMs}ms old")
                        }
                        if (udpDiag.pendingPackets.size > 5) {
                            appendLine("  ... and ${udpDiag.pendingPackets.size - 5} more")
                        }
                    }
                    
                    // Diagnostic warnings
                    if (!udpDiag.isConnected) {
                        appendLine()
                        appendLine("⚠️ UDP NOT CONNECTED - World data, objects, and textures won't load!")
                    }
                    if (udpDiag.isConnected && !udpDiag.receiveLoopActive) {
                        appendLine()
                        appendLine("⚠️ Receive loop not active - Packets from simulator won't be processed!")
                    }
                    if (udpDiag.registeredHandlerCount == 0) {
                        appendLine()
                        appendLine("⚠️ No handlers registered - RegionHandshake won't be processed!")
                    }
                } catch (e: Exception) {
                    appendLine("UDP diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("UDP connection: App not initialized")
            }
            appendLine()
            
            // Capability Manager Status - CRITICAL for texture/mesh/inventory loading
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ CAPABILITY STATUS (HTTP Services)                                 │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val capDiag = app.capabilityManager.getDiagnostics()
                    appendLine("Capabilities Ready: ${capDiag.isReady}")
                    appendLine("Total Capabilities: ${capDiag.capabilityCount}")
                    appendLine("Seed Capability: ${capDiag.seedCapability ?: "Not set"}")
                    appendLine()
                    
                    // New initialization tracking section
                    appendLine("Initialization Status:")
                    appendLine("  Completed: ${capDiag.initializationComplete}")
                    appendLine("  Duration: ${capDiag.initializationDurationMs}ms")
                    appendLine("  Attempts: ${capDiag.initializationAttempts}")
                    if (capDiag.lastInitializationError != null) {
                        appendLine("  Last Error: ${capDiag.lastInitializationError}")
                    }
                    appendLine()
                    
                    appendLine("Critical Capabilities:")
                    appendLine("  GetTexture: ${if (capDiag.hasGetTexture) "✓ Available" else "✗ Missing - textures won't load!"}")
                    appendLine("  GetMesh: ${if (capDiag.hasGetMesh) "✓ Available" else "✗ Missing - meshes won't load!"}")
                    appendLine("  FetchInventory: ${if (capDiag.hasFetchInventory) "✓ Available" else "✗ Missing"}")
                    appendLine("  EventQueue: ${if (capDiag.hasEventQueue) "✓ Available" else "✗ Missing"}")
                    appendLine()
                    appendLine("Event Queue:")
                    appendLine("  Active: ${capDiag.eventQueueActive}")
                    appendLine("  Registered Event Handlers: ${capDiag.eventHandlerCount}")
                    if (capDiag.registeredEventTypes.isNotEmpty()) {
                        appendLine("  Event Types: ${capDiag.registeredEventTypes.joinToString(", ")}")
                    }
                    appendLine()
                    if (capDiag.availableCapabilities.isNotEmpty()) {
                        appendLine("All Available Capabilities:")
                        capDiag.availableCapabilities.forEach { cap ->
                            appendLine("  - $cap")
                        }
                    } else {
                        appendLine("⚠️ No capabilities loaded - HTTP services unavailable!")
                    }
                    
                    // Diagnostic warnings
                    if (!capDiag.isReady) {
                        appendLine()
                        appendLine("⚠️ CAPABILITIES NOT READY - Textures, meshes, and inventory won't load!")
                        if (capDiag.lastInitializationError != null) {
                            appendLine("⚠️ LAST ERROR: ${capDiag.lastInitializationError}")
                        }
                    }
                } catch (e: Exception) {
                    appendLine("Capability diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Capabilities: App not initialized")
            }
            appendLine()
            
            // Network Quality Manager Status
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ NETWORK QUALITY                                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val qualityReport = app.protocol.qualityManager.getQualityReport()
                    appendLine("Quality Level: ${qualityReport.quality}")
                    appendLine("Network Connected: ${qualityReport.isConnected}")
                    appendLine("Network Type: ${qualityReport.networkType}")
                    appendLine("Average Latency: ${qualityReport.averageLatencyMs}ms")
                    appendLine("Estimated Bandwidth: ${qualityReport.estimatedBandwidthKbps} kbps")
                    appendLine("Error Rate: ${String.format("%.1f%%", qualityReport.errorRate * 100)}")
                    appendLine("Latency Samples: ${qualityReport.sampleCount}")
                    appendLine("Timeout Multiplier: ${qualityReport.timeoutMultiplier}x")
                    
                    if (qualityReport.quality == ConnectionQualityManager.Quality.POOR) {
                        appendLine()
                        appendLine("⚠️ Poor network quality - connection issues likely!")
                    }
                } catch (e: Exception) {
                    appendLine("Network quality unavailable: ${e.message}")
                }
            } else {
                appendLine("Network quality: App not initialized")
            }
            appendLine()
            
            // Network State Manager Status
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ NETWORK STATE                                                     │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val stateDetails = app.protocol.stateManager.getConnectionDetails()
                    appendLine("Connection Status: ${stateDetails.status}")
                    appendLine("Is Reconnecting: ${stateDetails.isReconnecting}")
                    appendLine("Connection Faulted: ${stateDetails.isFaulted}")
                    appendLine("Reset Requested: ${stateDetails.isResetRequested}")
                    appendLine("Force Reconnect: ${stateDetails.forceReconnect}")
                    appendLine("Always Reconnect: ${stateDetails.alwaysReconnect}")
                    appendLine("Logout In Progress: ${stateDetails.logoutInProgress}")
                    appendLine("Reconnect Count: ${stateDetails.reconnectCount}")
                    appendLine("Connection Duration: ${formatDuration(stateDetails.connectionDurationMs)}")
                    appendLine("Last Status Change: ${formatDuration(stateDetails.lastStatusChangeMs)} ago")
                    appendLine("Connection Instance ID: ${stateDetails.connectionInstanceId.ifEmpty { "Not set" }}")
                    
                    if (stateDetails.isFaulted) {
                        appendLine()
                        appendLine("⚠️ CONNECTION FAULTED - Manual reconnect may be required!")
                    }
                    if (stateDetails.status == NetworkStateManager.ConnectionStatus.ERROR) {
                        appendLine()
                        appendLine("⚠️ CONNECTION ERROR STATE")
                    }
                } catch (e: Exception) {
                    appendLine("Network state unavailable: ${e.message}")
                }
            } else {
                appendLine("Network state: App not initialized")
            }
            appendLine()
            
            // Object Manager Status
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ OBJECT MANAGER STATUS                                             │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isObjectManagerInitialized()) {
                        val objDiag = app.objectManager.getDiagnostics()
                        appendLine("Total Objects in Scene: ${objDiag.totalObjects}")
                        appendLine("Objects by UUID: ${objDiag.objectsByUUID}")
                        appendLine("Selected Objects: ${objDiag.selectedCount}")
                        appendLine("Is Editing: ${objDiag.isEditing}")
                        appendLine("Edit Mode: ${objDiag.editMode}")
                        appendLine("Recently Updated (last 5s): ${objDiag.recentlyUpdatedCount}")
                        appendLine("Scripted Objects: ${objDiag.scriptedObjectCount}")
                        appendLine("Physical Objects: ${objDiag.physicalObjectCount}")
                        
                        if (objDiag.totalObjects == 0) {
                            appendLine()
                            appendLine("⚠️ NO OBJECTS IN SCENE - World may not be loading!")
                        }
                    } else {
                        appendLine("Object manager not initialized (not logged in)")
                    }
                } catch (e: Exception) {
                    appendLine("Object manager diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Object manager: App not initialized")
            }
            appendLine()
            
            // Avatar Manager Status
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ AVATAR MANAGER STATUS                                             │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isAvatarManagerInitialized()) {
                        val avatarDiag = app.avatarManager.getDiagnostics()
                        appendLine("Total Avatars in Scene: ${avatarDiag.totalAvatars}")
                        appendLine("My Agent ID: ${avatarDiag.myAgentId ?: "Not set"}")
                        appendLine("My Avatar Loaded: ${avatarDiag.myAvatarLoaded}")
                        appendLine("Recently Updated (last 5s): ${avatarDiag.recentlyUpdatedCount}")
                        appendLine("Flying: ${avatarDiag.flyingCount}")
                        appendLine("Sitting: ${avatarDiag.sittingCount}")
                        appendLine("Typing: ${avatarDiag.typingCount}")
                        
                        if (avatarDiag.totalAvatars == 0) {
                            appendLine()
                            appendLine("⚠️ NO AVATARS IN SCENE - Avatar data may not be loading!")
                        }
                    } else {
                        appendLine("Avatar manager not initialized (not logged in)")
                    }
                } catch (e: Exception) {
                    appendLine("Avatar manager diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Avatar manager: App not initialized")
            }
            appendLine()
            
            // Inventory Manager Status
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ INVENTORY STATUS                                                  │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isInventoryManagerInitialized()) {
                        val invDiag = app.inventoryManager.getDiagnostics()
                        appendLine("Folders Cached: ${invDiag.folderCount}")
                        appendLine("Items Cached: ${invDiag.itemCount}")
                        appendLine("Root Folder ID: ${invDiag.rootFolderId ?: "Not set"}")
                        appendLine("System Folders: ${invDiag.systemFolderCount}")
                        appendLine("Currently Loading: ${invDiag.isLoading}")
                        appendLine("Current Folder: ${invDiag.currentFolderId ?: "None"}")
                    } else {
                        appendLine("Inventory manager not initialized (not logged in)")
                    }
                } catch (e: Exception) {
                    appendLine("Inventory diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Inventory: App not initialized")
            }
            appendLine()
            
            // Region Info (detailed)
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ REGION DETAILS                                                    │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val regionInfo = app.sessionManager.currentRegion.value
                    if (regionInfo != null) {
                        appendLine("Region Name: ${regionInfo.name}")
                        appendLine("Region Handle: ${regionInfo.handle}")
                        appendLine("Position: (${regionInfo.x}, ${regionInfo.y})")
                        appendLine("Sim IP: ${regionInfo.simIP.ifEmpty { "Not set" }}")
                        appendLine("Sim Port: ${if (regionInfo.simPort > 0) regionInfo.simPort else "Not set"}")
                        val seedCapDisplay = regionInfo.seedCapability?.let { 
                            if (it.length > DIAGNOSTIC_URL_TRUNCATE_LENGTH) it.take(DIAGNOSTIC_URL_TRUNCATE_LENGTH) + "..." else it 
                        } ?: "Not set"
                        appendLine("Seed Capability: $seedCapDisplay")
                        
                        if (regionInfo.name == "Unknown") {
                            appendLine()
                            appendLine("⚠️ REGION NAME UNKNOWN - RegionHandshake may not have been received!")
                        }
                    } else {
                        appendLine("No region info available - not connected to a region")
                    }
                } catch (e: Exception) {
                    appendLine("Region info unavailable: ${e.message}")
                }
            } else {
                appendLine("Region info: App not initialized")
            }
            appendLine()
            
            // ==================== MESH MANAGER STATUS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ MESH MANAGER STATUS                                               │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isMeshManagerInitialized()) {
                        val meshDiag = app.meshManager.getDiagnostics()
                        appendLine("Pending Downloads: ${meshDiag.pendingDownloads}")
                        appendLine("Downloaded: ${meshDiag.downloadedCount}")
                        appendLine("Downloaded Bytes: ${formatBytes(meshDiag.downloadedBytes)}")
                        appendLine("Download Failed: ${meshDiag.downloadFailedCount}")
                        appendLine("Parse Failed: ${meshDiag.parseFailedCount}")
                        appendLine("Has Mesh Capability: ${if (meshDiag.hasMeshCapability) "✓ Yes" else "✗ No"}")
                        
                        if (meshDiag.lastError != null) {
                            appendLine()
                            appendLine("Last Error: ${meshDiag.lastError}")
                            meshDiag.lastErrorTimeAgo?.let { 
                                appendLine("Error Time: ${formatDuration(it)} ago")
                            }
                        }
                        
                        if (!meshDiag.hasMeshCapability) {
                            appendLine()
                            appendLine("⚠️ NO MESH CAPABILITY - Mesh objects won't load!")
                        }
                        if (meshDiag.downloadFailedCount > 0 || meshDiag.parseFailedCount > 0) {
                            appendLine()
                            appendLine("⚠️ MESH ERRORS DETECTED - Some meshes may not display!")
                        }
                    } else {
                        appendLine("Mesh manager not initialized")
                    }
                } catch (e: Exception) {
                    appendLine("Mesh diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Mesh manager: App not initialized")
            }
            appendLine()
            
            // ==================== TEXTURE MANAGER DETAILED STATUS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ TEXTURE MANAGER DETAILED STATUS                                   │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isTextureManagerInitialized()) {
                        val texDiag = app.textureManager.getDiagnostics()
                        appendLine("Has Texture Capability: ${if (texDiag.hasTextureCapability) "✓ Yes" else "✗ No"}")
                        appendLine("Pending Downloads: ${texDiag.pendingDownloads}")
                        appendLine("Downloaded: ${texDiag.downloadedCount}")
                        appendLine("Downloaded Bytes: ${formatBytes(texDiag.downloadedBytes)}")
                        appendLine("Download Failed: ${texDiag.failedCount}")
                        appendLine("Decoded: ${texDiag.decodedCount}")
                        appendLine("Decode Failed: ${texDiag.decodeFailedCount}")
                        appendLine()
                        appendLine("Cache Status:")
                        appendLine("  Cached Textures: ${texDiag.cachedTextureCount}")
                        appendLine("  Pending Requests: ${texDiag.pendingRequestCount}")
                        appendLine("  Download Queue: ${texDiag.downloadQueueSize}")
                        appendLine("  Active Downloads: ${texDiag.activeDownloads}")
                        appendLine()
                        appendLine("JPEG2000 Decoding:")
                        appendLine("  Attempts: ${texDiag.j2kDecodeAttempts}")
                        appendLine("  Successes: ${texDiag.j2kDecodeSuccesses}")
                        
                        if (texDiag.lastError != null) {
                            appendLine()
                            appendLine("Last Error: ${texDiag.lastError}")
                            texDiag.lastErrorTimeAgo?.let { 
                                appendLine("Error Time: ${formatDuration(it)} ago")
                            }
                        }
                        
                        if (!texDiag.hasTextureCapability) {
                            appendLine()
                            appendLine("⚠️ NO TEXTURE CAPABILITY - Using fallback asset server!")
                        }
                        if (texDiag.failedCount > 5 || texDiag.decodeFailedCount > 5) {
                            appendLine()
                            appendLine("⚠️ HIGH TEXTURE FAILURE RATE - Connection or decode issues!")
                        }
                    } else {
                        appendLine("Texture manager not initialized")
                    }
                } catch (e: Exception) {
                    appendLine("Texture diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Texture manager: App not initialized")
            }
            appendLine()
            
            // ==================== RENDER MANAGER STATUS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ RENDER MANAGER STATUS (Filament)                                  │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isRenderManagerInitialized()) {
                        val renderDiag = app.renderManager.getDiagnostics()
                        appendLine("Initialized: ${renderDiag.isInitialized}")
                        appendLine("XR Mode: ${renderDiag.isXRMode}")
                        appendLine("Viewport: ${renderDiag.viewportWidth} x ${renderDiag.viewportHeight}")
                        appendLine("Frame Count: ${renderDiag.frameCount}")
                        renderDiag.timeSinceLastFrame?.let {
                            appendLine("Time Since Last Frame: ${formatDuration(it)}")
                        }
                        appendLine()
                        appendLine("Filament Components:")
                        appendLine("  Engine: ${if (renderDiag.hasEngine) "✓" else "✗"}")
                        appendLine("  Renderer: ${if (renderDiag.hasRenderer) "✓" else "✗"}")
                        appendLine("  Scene: ${if (renderDiag.hasScene) "✓" else "✗"}")
                        appendLine("  View: ${if (renderDiag.hasView) "✓" else "✗"}")
                        appendLine("  Camera: ${if (renderDiag.hasCamera) "✓" else "✗"}")
                        appendLine("  SwapChain: ${if (renderDiag.hasSwapChain) "✓" else "✗"}")
                        
                        if (renderDiag.initializationTime > 0) {
                            appendLine()
                            appendLine("Initialization Time: ${formatTimestamp(renderDiag.initializationTime)}")
                        }
                        
                        if (renderDiag.lastInitializationError != null) {
                            appendLine()
                            appendLine("⚠️ INITIALIZATION ERROR: ${renderDiag.lastInitializationError}")
                        }
                        
                        if (!renderDiag.isInitialized) {
                            appendLine()
                            appendLine("⚠️ RENDERER NOT INITIALIZED - No 3D rendering!")
                        }
                        if (renderDiag.isInitialized && !renderDiag.hasSwapChain) {
                            appendLine()
                            appendLine("⚠️ NO SWAP CHAIN - Rendering not visible!")
                        }
                    } else {
                        appendLine("Render manager not initialized")
                    }
                } catch (e: Exception) {
                    appendLine("Render diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Render manager: App not initialized")
            }
            appendLine()
            
            // ==================== END OF NEW DIAGNOSTIC SECTIONS ====================
            
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
            
            // Initialization Timeline - CRITICAL for diagnosing "world not loading" issues
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ INITIALIZATION TIMELINE                                           │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val initDiag = InitializationTracker.getDiagnostics()
                appendLine("Session Duration: ${formatDuration(initDiag.sessionDurationMs)}")
                appendLine("Current Phase: ${initDiag.currentPhase}")
                appendLine("Total Events: ${initDiag.totalEvents}")
                appendLine("Warnings: ${initDiag.warningCount}")
                appendLine("Errors: ${initDiag.errorCount}")
                appendLine()
                
                if (initDiag.completedPhases.isNotEmpty()) {
                    appendLine("Completed Phases:")
                    initDiag.completedPhases.forEach { phase ->
                        appendLine("  ✓ $phase")
                    }
                    appendLine()
                }
                
                if (initDiag.failedPhases.isNotEmpty()) {
                    appendLine("Failed Phases:")
                    initDiag.failedPhases.forEach { phase ->
                        appendLine("  ✗ $phase")
                    }
                    appendLine()
                }
                
                if (initDiag.pendingPhases.isNotEmpty()) {
                    appendLine("Pending Phases:")
                    initDiag.pendingPhases.forEach { phase ->
                        appendLine("  ⏳ $phase")
                    }
                    appendLine()
                }
                
                // Recent events (show last 20 from the available events)
                appendLine("Recent Events (last 20):")
                initDiag.recentEvents.takeLast(20).forEach { event ->
                    val icon = when (event.type) {
                        InitializationTracker.EventType.PHASE_START -> "▶"
                        InitializationTracker.EventType.PHASE_COMPLETE -> "✓"
                        InitializationTracker.EventType.ERROR -> "✗"
                        InitializationTracker.EventType.WARNING -> "⚠"
                        InitializationTracker.EventType.CRITICAL -> "⭐"
                        InitializationTracker.EventType.INFO -> "•"
                    }
                    appendLine("[${event.relativeMs}ms] $icon ${event.message}")
                }
            } catch (e: Exception) {
                appendLine("Initialization timeline unavailable: ${e.message}")
            }
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
    
    /**
     * Format a duration in milliseconds to a human-readable string.
     * 
     * @param ms Duration in milliseconds
     * @return Formatted string with appropriate unit:
     *         - "Xms" for durations under 1 second
     *         - "X.Xs" for durations under 1 minute
     *         - "X.Xm" for durations under 1 hour
     *         - "X.Xh" for longer durations
     */
    private fun formatDuration(ms: Long): String {
        return when {
            ms < 1000 -> "${ms}ms"
            ms < 60000 -> String.format(Locale.US, "%.1fs", ms / 1000.0)
            ms < 3600000 -> String.format(Locale.US, "%.1fm", ms / 60000.0)
            else -> String.format(Locale.US, "%.1fh", ms / 3600000.0)
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}
