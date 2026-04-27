package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.LinkpointApp
import com.linkpoint.assets.CacheManager
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.core.ConnectionQualityManager
import com.linkpoint.network.core.NetworkStateManager
import com.linkpoint.protocol.messages.EnhancedPacketLogger
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.render.RenderDiagnostics
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
        
        // Number of recent malformed packets to show in debug reports
        private const val MALFORMED_PACKET_HISTORY_COUNT = 5
        private const val EARLY_WARNING_GRACE_MS = 15_000L
        
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
        
        /**
         * Phases that indicate the connection is still in progress.
         * Used to provide more accurate diagnostic messages.
         */
        private val CONNECTING_PHASES = setOf(
            InitializationTracker.Phase.NOT_STARTED,
            InitializationTracker.Phase.LOGIN_STARTING,
            InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
            InitializationTracker.Phase.LOGIN_SUCCESS,
            InitializationTracker.Phase.SESSION_SETUP,
            InitializationTracker.Phase.UDP_CONNECTING,
            InitializationTracker.Phase.UDP_CONNECTED,
            InitializationTracker.Phase.CAPABILITIES_FETCHING
        )
        
        /**
         * Check if the current initialization phase indicates connection is still in progress.
         */
        private fun isStillConnecting(phase: InitializationTracker.Phase): Boolean {
            return phase in CONNECTING_PHASES
        }
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reportDirectory: File? = null

    private fun udpConnectedElapsedMs(): Long? {
        return InitializationTracker.getElapsedSincePhase(InitializationTracker.Phase.UDP_CONNECTED)
    }

    private fun formatUdpConnectedElapsed(elapsedMs: Long?): String {
        return if (elapsedMs != null) "${formatDuration(elapsedMs)} since UDP_CONNECTED" else "UDP_CONNECTED time unavailable"
    }
    
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
            if (file != null) {
                CodexUploadService.getInstance(context).uploadLatestReportsAsync("debug_report")
            }
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

            // LinkpointTexture / mmap accounting (populated once the
            // LinkpointTexture path is on the hot path; until then values
            // stay at zero, which is the honest signal that the new
            // pipeline isn't carrying any traffic yet).
            val textureMemSnap = com.linkpoint.assets.TextureMemoryTracker.snapshot()
            appendLine("Texture Memory Tracker:")
            appendLine("  Live Textures: ${textureMemSnap.liveTextures}")
            appendLine("  Native Heap: ${formatBytes(textureMemSnap.nativeHeapBytes)}")
            appendLine("  Mmapped: ${formatBytes(textureMemSnap.mmappedBytes)}")
            appendLine("  GPU: ${formatBytes(textureMemSnap.gpuBytes)}")
            appendLine("  Total: ${formatBytes(textureMemSnap.totalBytes)}")
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
                    appendLine("  Startup Resends (before first inbound): ${udpDiag.startupResendCount}")
                    appendLine("  Selector Wakeups: ${udpDiag.selectorWakeupCount}")
                    appendLine("  Selector Ready Keys: ${udpDiag.selectorReadyKeyCount}")
                    appendLine("  Selector Readable Keys: ${udpDiag.selectorReadableKeyCount}")
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
                    
                    // NEW: Socket details for detailed debugging
                    appendLine()
                    val socketDetails = app.udpConnection.getSocketDetails()
                    appendLine("Socket Details:")
                    appendLine("  Local Bind Address: ${socketDetails.localBindAddress ?: "Unknown"}")
                    appendLine("  Local Bind Port: ${if (socketDetails.localBindPort > 0) socketDetails.localBindPort else "Unknown"}")
                    appendLine("  Remote Address: ${socketDetails.remoteAddress.ifEmpty { "Not set" }}")
                    appendLine("  Remote Port: ${if (socketDetails.remotePort > 0) socketDetails.remotePort else "Not set"}")
                    appendLine("  Channel Connected: ${socketDetails.isConnected}")
                    appendLine("  Channel Open: ${socketDetails.isOpen}")
                    
                    // Timing information
                    appendLine()
                    appendLine("Timing:")
                    if (socketDetails.connectionAttemptTime > 0) {
                        val connectionAge = System.currentTimeMillis() - socketDetails.connectionAttemptTime
                        appendLine("  Connection Attempted: ${formatDuration(connectionAge)} ago")
                    } else {
                        appendLine("  Connection Attempted: Never")
                    }
                    if (socketDetails.lastSendAttemptTime > 0) {
                        val sendAge = System.currentTimeMillis() - socketDetails.lastSendAttemptTime
                        appendLine("  Last Send Attempt: ${formatDuration(sendAge)} ago")
                    } else {
                        appendLine("  Last Send Attempt: Never")
                    }
                    if (socketDetails.lastReceiveTime > 0) {
                        val receiveAge = System.currentTimeMillis() - socketDetails.lastReceiveTime
                        appendLine("  Last Packet Received: ${formatDuration(receiveAge)} ago")
                    } else {
                        appendLine("  Last Packet Received: Never ⚠️")
                    }
                    if (socketDetails.connectToFirstInboundMs != null) {
                        appendLine("  UDP Connect → First Inbound: ${formatDuration(socketDetails.connectToFirstInboundMs)}")
                    } else {
                        appendLine("  UDP Connect → First Inbound: Not observed yet")
                    }
                    if (socketDetails.lastPingTime > 0) {
                        val pingAge = System.currentTimeMillis() - socketDetails.lastPingTime
                        appendLine("  Last Ping Check: ${formatDuration(pingAge)} ago")
                        appendLine("  Unanswered Pings: ${socketDetails.unansweredPings}")
                    } else {
                        appendLine("  Last Ping Check: Never")
                    }
                    if (socketDetails.lastConnectionError != null) {
                        appendLine()
                        appendLine("  ⚠️ Last Connection Error: ${socketDetails.lastConnectionError}")
                    }
                    appendLine("  Last Reliable Send: ${
                        if (socketDetails.lastReliableSendAt > 0L) {
                            "${formatDuration(System.currentTimeMillis() - socketDetails.lastReliableSendAt)} ago " +
                                "(seq=${socketDetails.lastReliableSendSequence})"
                        } else {
                            "Never"
                        }
                    }")
                    appendLine("  Last Reliable Deadline: ${
                        if (socketDetails.lastReliableSendDeadlineAt > 0L) {
                            "${formatDuration(
                                kotlin.math.abs(System.currentTimeMillis() - socketDetails.lastReliableSendDeadlineAt)
                            )} ${if (System.currentTimeMillis() <= socketDetails.lastReliableSendDeadlineAt) "from now" else "ago"}"
                        } else {
                            "N/A"
                        }
                    }")

                    if (socketDetails.receiveLoopExceptions.isNotEmpty()) {
                        appendLine()
                        appendLine("Receive Loop Exceptions (recent):")
                        socketDetails.receiveLoopExceptions.takeLast(5).forEach { ex ->
                            appendLine("  - $ex")
                        }
                    }

                    // Reconnect health — surfaces the dead-socket-after-reconnect
                    // failure mode that previously hid behind "Connected: true".
                    appendLine()
                    appendLine("Reconnect Health:")
                    val socketReconnects = app.udpConnection.getSocketReconnectCount()
                    appendLine("  Socket Reconnects (this circuit): $socketReconnects")
                    val lastReconnect = app.udpConnection.getLastSocketReconnectTime()
                    if (lastReconnect > 0) {
                        val ageMs = System.currentTimeMillis() - lastReconnect
                        val ok = app.udpConnection.getLastSocketReconnectSucceeded()
                        appendLine("  Last Reconnect: ${formatDuration(ageMs)} ago (${if (ok) "succeeded" else "failed"})")
                        val postRx = app.udpConnection.getPostReconnectPacketsReceived()
                        appendLine("  Packets Received Since Last Reconnect: $postRx")
                        // The classic dead-socket-after-reconnect signature:
                        // we report Connected, the reconnect "succeeded", but
                        // not a single packet has come back.
                        if (udpDiag.isConnected && ok && postRx == 0 && ageMs > 5_000L) {
                            appendLine("  ⚠️ No packets received since reconnect — socket may be silently dead.")
                        }
                    } else {
                        appendLine("  Last Reconnect: Never")
                    }

                    // I/O thread liveness. The receive loop name encodes which
                    // generation we're on (initial vs. reconnected), which is
                    // useful when chasing zombie threads.
                    val ioName = app.udpConnection.getIoThreadName()
                    val ioAlive = udpDiag.receiveLoopActive
                    appendLine()
                    appendLine("I/O Thread:")
                    appendLine("  Alive: $ioAlive")
                    appendLine("  Name: ${ioName ?: "(none)"}")
                    if (udpDiag.isConnected && !ioAlive) {
                        appendLine("  ⚠️ Connected but I/O thread is dead — packets won't be processed!")
                    }

                    // "Connected but silent" check. When the receive loop is
                    // alive but no packet has come in for >10s while we're
                    // still firing AgentUpdates, something is wrong on the
                    // network path even though _isConnected reads true.
                    if (udpDiag.isConnected && socketDetails.lastReceiveTime > 0) {
                        val rxAge = System.currentTimeMillis() - socketDetails.lastReceiveTime
                        if (rxAge > 10_000L) {
                            appendLine()
                            appendLine("⚠️ Connected but no packet received for ${formatDuration(rxAge)} — circuit may be one-way (NAT idle / dead receive loop).")
                        }
                    }
                } catch (e: Exception) {
                    appendLine("UDP diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("UDP connection: App not initialized")
            }
            appendLine()

            // NEW: Packet History section for detailed packet tracing
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ UDP PACKET HISTORY (Recent Activity)                              │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val packetHistory = app.udpConnection.getPacketHistory()
                    if (packetHistory.isNotEmpty()) {
                        appendLine("Recent Packet Events (last ${packetHistory.size}):")
                        appendLine()

                        fun renderEntry(entry: UDPConnectionFixed.PacketHistoryEntry) {
                            val relativeTime = System.currentTimeMillis() - entry.timestamp
                            val icon = when (entry.type) {
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_SUCCESS -> "→"
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_FAILED -> "✗→"
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RECEIVE -> "←"
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RESEND -> "⟳"
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_RECEIVED -> "✓←"
                                UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_TIMEOUT -> "⏱"
                            }
                            val statusIcon = if (entry.success) "" else " ⚠️"
                            appendLine("  [${formatDuration(relativeTime)} ago] $icon ${entry.messageName} (seq=${entry.sequenceNumber}, ${entry.size}B)$statusIcon")
                            if (!entry.success && entry.errorMessage != null) {
                                appendLine("     Error: ${entry.errorMessage}")
                            }
                            // Show full hex dump for all packets (complete packet data for diagnosis)
                            if (entry.fullHexDump.isNotEmpty()) {
                                appendLine("     Full packet: ${entry.fullHexDump}")
                            }
                        }

                        // Show incoming events first so they're never drowned
                        // out by the 10-Hz AgentUpdate stream that dominates
                        // the bounded history window.
                        val incomingEntries = packetHistory.filter {
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RECEIVE ||
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_RECEIVED ||
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_TIMEOUT
                        }.takeLast(15)
                        if (incomingEntries.isNotEmpty()) {
                            appendLine("Incoming (last ${incomingEntries.size}):")
                            incomingEntries.forEach(::renderEntry)
                            appendLine()
                        } else {
                            appendLine("Incoming: none in history window ⚠️")
                            appendLine()
                        }

                        val outgoingEntries = packetHistory.filter {
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_SUCCESS ||
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_FAILED ||
                            it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RESEND
                        }.takeLast(15)
                        if (outgoingEntries.isNotEmpty()) {
                            appendLine("Outgoing (last ${outgoingEntries.size}):")
                            outgoingEntries.forEach(::renderEntry)
                        }
                        
                        // Summary statistics (counted within the bounded history window)
                        val sendSuccessCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_SUCCESS }
                        val sendFailedCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.SEND_FAILED }
                        val receiveCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RECEIVE }
                        val resendCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.RESEND }
                        val ackReceivedCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_RECEIVED }
                        val ackTimeoutCount = packetHistory.count { it.type == UDPConnectionFixed.PacketHistoryEntry.PacketEventType.ACK_TIMEOUT }

                        appendLine()
                        appendLine("History Summary (recent ${packetHistory.size}-packet window):")
                        appendLine("  Sent Successfully: $sendSuccessCount")
                        appendLine("  Send Failures: $sendFailedCount")
                        appendLine("  Received: $receiveCount")
                        appendLine("  ACKs Received: $ackReceivedCount")
                        appendLine("  ACK Timeouts: $ackTimeoutCount")
                        appendLine("  Resends: $resendCount")

                        // Determine whether any packets have EVER been received using the
                        // absolute counter, not the bounded history window. AgentUpdate
                        // sends ~10/sec quickly evict receive entries from a 50-entry
                        // buffer, which previously produced a false "none received"
                        // warning even after a successful handshake.
                        val totalReceivedEver = try {
                            app.udpConnection.getMessageStatistics().totalPacketsReceived
                        } catch (e: Exception) {
                            receiveCount
                        }
                        val totalSentEver = try {
                            app.udpConnection.getDiagnostics().sequenceNumber
                        } catch (e: Exception) {
                            sendSuccessCount
                        }
                        if (totalReceivedEver == 0 && totalSentEver > 0) {
                            val udpElapsedMs = udpConnectedElapsedMs()
                            val isProvisional = udpElapsedMs != null && udpElapsedMs < EARLY_WARNING_GRACE_MS
                            val timing = formatUdpConnectedElapsed(udpElapsedMs)
                            appendLine()
                            if (isProvisional) {
                                appendLine("⚠️ PROVISIONAL: PACKETS SENT BUT NONE RECEIVED ($timing)")
                            } else {
                                appendLine("⚠️ PACKETS SENT BUT NONE RECEIVED ($timing)")
                            }
                            appendLine("   Possible causes:")
                            appendLine("   - Firewall blocking UDP")
                            appendLine("   - NAT traversal issue")
                            appendLine("   - Simulator not responding")
                            appendLine("   - Wrong port/address")
                        }
                    } else {
                        appendLine("No packet history recorded yet")
                    }
                } catch (e: Exception) {
                    appendLine("Packet history unavailable: ${e.message}")
                }
            } else {
                appendLine("Packet history: App not initialized")
            }
            appendLine()

            // ==================== INCOMING PACKET DETAILS ====================
            // Pulls from EnhancedPacketLogger's 200-entry history filtered to
            // inbound only, so you can actually see what the simulator sent us
            // even when 10 Hz AgentUpdate sends would otherwise dominate the
            // smaller per-connection history window.
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ INCOMING PACKET DETAILS (Receives + ACKs)                         │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val incoming = EnhancedPacketLogger.getIncomingPacketHistory(30)
                if (incoming.isEmpty()) {
                    appendLine("No incoming packets recorded yet.")
                } else {
                    val receiveCount = incoming.count { it.direction == EnhancedPacketLogger.PacketLogEntry.Direction.RECEIVED }
                    val ackCount = incoming.count { it.direction == EnhancedPacketLogger.PacketLogEntry.Direction.ACK_RECEIVED }
                    appendLine("Showing last ${incoming.size} inbound entries (receives=$receiveCount, acks=$ackCount):")
                    appendLine()
                    incoming.forEach { entry ->
                        val ago = System.currentTimeMillis() - entry.timestamp
                        val arrow = when (entry.direction) {
                            EnhancedPacketLogger.PacketLogEntry.Direction.RECEIVED -> "←"
                            EnhancedPacketLogger.PacketLogEntry.Direction.ACK_RECEIVED -> "✓←"
                            else -> "?"
                        }
                        val handlerInfo = if (entry.direction == EnhancedPacketLogger.PacketLogEntry.Direction.RECEIVED && !entry.handlerDispatched) " [NO HANDLER]" else ""
                        val flagStr = entry.flags.toShortString().let { if (it == "-") "" else " [$it]" }
                        appendLine("  [${formatDuration(ago)} ago] $arrow ${entry.messageName} (seq=${entry.sequenceNumber}, ${entry.size}B)$flagStr$handlerInfo")
                        entry.hexPreview?.let { hex ->
                            if (hex.isNotBlank()) appendLine("     Hex: $hex")
                        }
                        entry.error?.let { appendLine("     Error: $it") }
                    }
                }

                // Recent ACK sequence list — quick at-a-glance view of which
                // sends the simulator has confirmed.
                val recentAckSeqs = EnhancedPacketLogger.getIncomingPacketHistory(60)
                    .filter { it.direction == EnhancedPacketLogger.PacketLogEntry.Direction.ACK_RECEIVED }
                    .takeLast(20)
                    .map { it.sequenceNumber }
                if (recentAckSeqs.isNotEmpty()) {
                    appendLine()
                    appendLine("Recently ACK'd sequence numbers (last ${recentAckSeqs.size}):")
                    appendLine("  ${recentAckSeqs.joinToString(", ")}")
                }
            } catch (e: Exception) {
                appendLine("Incoming packet details unavailable: ${e.message}")
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
                            // Check initialization phase to provide better context
                            val initPhase = InitializationTracker.getDiagnostics().currentPhase
                            if (isStillConnecting(initPhase)) {
                                appendLine("ℹ️ NO OBJECTS YET - Connection still in progress (phase: $initPhase)")
                                appendLine("   Objects will appear after RegionHandshake is processed.")
                            } else {
                                appendLine("⚠️ NO OBJECTS IN SCENE - World may not be loading!")
                                appendLine("   Check if OBJECT_UPDATE messages are being received.")
                            }
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
                            // Check initialization phase to provide better context
                            val initPhase = InitializationTracker.getDiagnostics().currentPhase
                            if (isStillConnecting(initPhase)) {
                                appendLine("ℹ️ NO AVATARS YET - Connection still in progress (phase: $initPhase)")
                                appendLine("   Avatar data will appear after CoarseLocationUpdate is received.")
                            } else {
                                appendLine("⚠️ NO AVATARS IN SCENE - Avatar data may not be loading!")
                                appendLine("   Check if COARSE_LOCATION_UPDATE messages are being received.")
                            }
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
                            // Check initialization phase to provide better context
                            val initPhase = InitializationTracker.getDiagnostics().currentPhase
                            if (isStillConnecting(initPhase)) {
                                appendLine("ℹ️ REGION NAME PENDING - Waiting for RegionHandshake (phase: $initPhase)")
                                appendLine("   The actual region name will be received from the simulator.")
                            } else {
                                appendLine("⚠️ REGION NAME UNKNOWN - RegionHandshake may not have been received!")
                                appendLine("   Check if REGION_HANDSHAKE is in the message statistics.")
                            }
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
                        appendLine("  Error States: ${texDiag.textureErrorStateCount}")
                        val decodeFailureCount = (texDiag.j2kDecodeAttempts - texDiag.j2kDecodeSuccesses).coerceAtLeast(0)
                        appendLine("  Success Rate: ${formatRate(texDiag.j2kDecodeSuccesses, texDiag.j2kDecodeAttempts)}")
                        appendLine("  Failure Rate: ${formatRate(decodeFailureCount, texDiag.j2kDecodeAttempts)}")
                        val transferAttempts = texDiag.downloadedCount + texDiag.failedCount
                        appendLine("  Transfer Success Rate: ${formatRate(texDiag.downloadedCount, transferAttempts)}")
                        appendLine("  Transfer Failure Rate: ${formatRate(texDiag.failedCount, transferAttempts)}")
                        val decoderStatus = com.linkpoint.assets.JPEG2000Decoder.getStartupStatus()
                        appendLine("  Backend: ${decoderStatus.activeBackend}")
                        appendLine("  Native Loaded: ${if (decoderStatus.nativeLoaded) "✓" else "✗"}")
                        appendLine("  Native Healthy: ${if (decoderStatus.nativeHealthy) "✓" else "✗"}")
                        appendLine("  JP2ForAndroid Fallback: ${if (decoderStatus.jp2ForAndroidAvailable) "✓" else "✗"}")
                        // Surface the underlying load/health failure when the backend
                        // ended up as "none" - this is the difference between "we don't
                        // know why" and "UnsatisfiedLinkError: dlopen failed: cannot
                        // locate symbol opj_create_decompress" (NDK / OpenJPEG mismatch).
                        decoderStatus.nativeError?.let {
                            appendLine("  Native Load Error: $it")
                        }
                        decoderStatus.nativeHealthError?.let {
                            appendLine("  Native Health Error: $it")
                        }
                        if (decoderStatus.warningMessage != null) {
                            appendLine("  Warning: ${decoderStatus.warningMessage}")
                        }
                        
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
                        val renderTimeline = RenderDiagnostics.diagnosticsSnapshot()
                        appendLine("Active Backend: ${renderTimeline.activeBackend}")
                        appendLine()
                        appendLine("Filament Components:")
                        appendLine("  Engine: ${if (renderDiag.hasEngine) "✓" else "✗"}")
                        appendLine("  Renderer: ${if (renderDiag.hasRenderer) "✓" else "✗"}")
                        appendLine("  Scene: ${if (renderDiag.hasScene) "✓" else "✗"}")
                        appendLine("  View: ${if (renderDiag.hasView) "✓" else "✗"}")
                        appendLine("  Camera: ${if (renderDiag.hasCamera) "✓" else "✗"}")
                        appendLine("  SwapChain: ${if (renderDiag.hasSwapChain) "✓" else "✗"}")
                        appendLine("  Surface Ready: ${if (renderDiag.isSurfaceReady) "✓" else "✗ (e.g. on Settings, app backgrounded)"}")
                        appendLine("  Drawing Enabled: ${if (renderDiag.isDrawingEnabled) "✓" else "✗ (paused — panel open / activity backgrounded)"}")
                        appendLine()
                        appendLine("Visible Entity Counts:")
                        appendLine("  Avatars: ${renderDiag.visibleAvatarCount}")
                        appendLine("  Prims: ${renderDiag.visiblePrimCount}")
                        appendLine("  Terrain Patches: ${renderDiag.visibleTerrainPatchCount}")
                        appendLine()
                        appendLine("Frame Time Percentiles (ms):")
                        if (renderTimeline.frameTimePercentiles.isEmpty()) {
                            appendLine("  (insufficient frame history)")
                        } else {
                            renderTimeline.frameTimePercentiles.forEach { (backend, p) ->
                                appendLine(
                                    "  $backend: p50=${p.p50Ms ?: "-"} p90=${p.p90Ms ?: "-"} p99=${p.p99Ms ?: "-"}"
                                )
                            }
                        }
                        appendLine()
                        appendLine("Swapchain / Context Restarts:")
                        appendLine("  Filament SwapChain Creates: ${renderTimeline.swapChainCreateCount}")
                        appendLine("  Filament SwapChain Destroys: ${renderTimeline.swapChainDestroyCount}")
                        appendLine("  Filament SwapChain Failures: ${renderTimeline.swapChainFailureCount}")
                        appendLine("  Filament SwapChain Restarts: ${renderTimeline.swapChainRestartCount}")
                        appendLine("  Lumiya Context Creates: ${renderTimeline.glContextCreateCount}")
                        appendLine("  Lumiya Context Restarts: ${renderTimeline.glContextRestartCount}")

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
                        // Only flag a missing SwapChain as a problem when the Surface is
                        // actually attached and ready - otherwise it just means the world
                        // view isn't on screen right now (Settings, backgrounded, etc.),
                        // which is the normal state when capturing a debug report from
                        // any non-WorldView screen.
                        if (renderDiag.isInitialized && !renderDiag.hasSwapChain && renderDiag.isSurfaceReady) {
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
            
            // ==================== PROTOCOL MESSAGE STATISTICS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ PROTOCOL MESSAGE STATISTICS                                       │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    val udpDiag = app.udpConnection.getDiagnostics()
                    val msgStats = app.udpConnection.getMessageStatistics()
                    
                    appendLine("Total Packets Received: ${msgStats.totalPacketsReceived}")
                    appendLine("Total Bytes Received: ${formatBytes(msgStats.totalBytesReceived)}")
                    appendLine("Packets Sent: ${udpDiag.sequenceNumber}")
                    appendLine("Packets Resent: ${msgStats.packetsResent}")
                    appendLine()
                    
                    if (msgStats.messageTypeCounts.isNotEmpty()) {
                        appendLine("Messages by Type (top 10):")
                        msgStats.messageTypeCounts.entries
                            .sortedByDescending { it.value }
                            .take(10)
                            .forEach { (type, count) ->
                                appendLine("  $type: $count")
                            }
                        appendLine()
                    }
                    
                    if (msgStats.lastMessageTimes.isNotEmpty()) {
                        appendLine("Last Received (key messages):")
                        val keyMessages = listOf(
                            "RegionHandshake", "AgentMovementComplete",
                            "ObjectUpdate", "CoarseLocationUpdate"
                        )
                        keyMessages.forEach { msgType ->
                            val lastTime = msgStats.lastMessageTimes[msgType]
                            if (lastTime != null && lastTime > 0) {
                                val ago = System.currentTimeMillis() - lastTime
                                appendLine("  $msgType: ${formatDuration(ago)} ago")
                            } else {
                                appendLine("  $msgType: Never received")
                            }
                        }
                        appendLine()
                    }
                    
                    if (msgStats.inboundRateBuckets.isNotEmpty()) {
                        appendLine("Inbound Rate (last ${msgStats.inboundRateBuckets.size}s, oldest first):")
                        appendLine("  second  packets    bytes")
                        val nowSec = System.currentTimeMillis() / 1000L
                        msgStats.inboundRateBuckets.forEach { bucket ->
                            val ago = nowSec - bucket.epochSecond
                            // Mark the silent buckets so a 24-second hole jumps out visually.
                            val marker = if (bucket.packets == 0L) " ⚠" else ""
                            appendLine(
                                "  -%3ds  %7d  %7s%s".format(
                                    ago,
                                    bucket.packets,
                                    formatBytes(bucket.bytes),
                                    marker
                                )
                            )
                        }
                        val totalPackets = msgStats.inboundRateBuckets.sumOf { it.packets }
                        val totalBytes = msgStats.inboundRateBuckets.sumOf { it.bytes }
                        val silentBuckets = msgStats.inboundRateBuckets.count { it.packets == 0L }
                        appendLine(
                            "  window total: $totalPackets pkts / ${formatBytes(totalBytes)} " +
                                "($silentBuckets silent seconds)"
                        )
                        appendLine()
                    }

                    // Warning if critical messages haven't been received
                    val regionHandshakeTime = msgStats.lastMessageTimes["RegionHandshake"]
                    val agentMovementTime = msgStats.lastMessageTimes["AgentMovementComplete"]
                    
                    if (regionHandshakeTime == null || regionHandshakeTime == 0L) {
                        val udpElapsedMs = udpConnectedElapsedMs()
                        val isProvisional = udpElapsedMs != null && udpElapsedMs < EARLY_WARNING_GRACE_MS
                        val timing = formatUdpConnectedElapsed(udpElapsedMs)
                        if (isProvisional) {
                            appendLine("⚠️ PROVISIONAL: RegionHandshake never received ($timing) - world data may not load yet.")
                        } else {
                            appendLine("⚠️ RegionHandshake never received ($timing) - world data won't load!")
                        }
                    }
                    if (agentMovementTime == null || agentMovementTime == 0L) {
                        appendLine("⚠️ AgentMovementComplete never received - agent not in world!")
                    }
                } catch (e: Exception) {
                    appendLine("Protocol statistics unavailable: ${e.message}")
                }
            } else {
                appendLine("Protocol statistics: App not initialized")
            }
            appendLine()
            
            // ==================== HTTP/2 PROTOCOL STATISTICS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ HTTP/2 PROTOCOL STATISTICS                                        │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val protocolStats = NetworkLogger.getProtocolStatistics()
                appendLine("Protocol Usage Summary:")
                appendLine("  HTTP/2 Requests: ${protocolStats.http2Requests} (${String.format("%.1f", protocolStats.getHttp2Percentage())}%)")
                appendLine("  HTTP/1.1 Requests: ${protocolStats.http11Requests}")
                appendLine("  HTTP/1.0 Requests: ${protocolStats.http10Requests}")
                appendLine()
                appendLine("By Request Type:")
                appendLine("  Textures: HTTP/2=${protocolStats.textureHttp2Count}, HTTP/1.1=${protocolStats.textureHttp11Count}")
                appendLine("  Meshes: HTTP/2=${protocolStats.meshHttp2Count}, HTTP/1.1=${protocolStats.meshHttp11Count}")
                appendLine("  Capabilities: HTTP/2=${protocolStats.capabilityHttp2Count}, HTTP/1.1=${protocolStats.capabilityHttp11Count}")
                appendLine()
                appendLine("Last Protocols Used:")
                appendLine("  Texture Requests: ${protocolStats.lastTextureProtocol}")
                appendLine("  Mesh Requests: ${protocolStats.lastMeshProtocol}")
                appendLine("  Capability Requests: ${protocolStats.lastCapabilityProtocol}")
                
                if (protocolStats.http2Requests == 0 && (protocolStats.textureHttp11Count > 0 || protocolStats.meshHttp11Count > 0)) {
                    appendLine()
                    appendLine("ℹ️ HTTP/2 NOT USED - All requests using HTTP/1.1")
                    appendLine("   HTTP/2 provides better performance for asset loading.")
                }
            } catch (e: Exception) {
                appendLine("HTTP/2 statistics unavailable: ${e.message}")
            }
            appendLine()
            
            // ==================== FRIENDS MANAGER STATUS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ FRIENDS MANAGER STATUS                                            │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null) {
                try {
                    if (app.isFriendsManagerInitialized()) {
                        val friendsDiag = app.friendsManager.getDiagnostics()
                        appendLine("Total Friends: ${friendsDiag.totalFriends}")
                        appendLine("Online Friends: ${friendsDiag.onlineFriends}")
                        appendLine("Pending Offers: ${friendsDiag.pendingOffers}")
                        
                        if (friendsDiag.totalFriends > 0 && friendsDiag.friendsList.isNotEmpty()) {
                            appendLine()
                            appendLine("Friends List (showing up to 10):")
                            friendsDiag.friendsList.take(10).forEach { friend ->
                                val status = if (friend.isOnline) "🟢" else "🔴"
                                appendLine("  $status ${friend.name}")
                                appendLine("      ID: ${friend.agentId}")
                                appendLine("      Rights Given: ${friend.rightsGiven}")
                                appendLine("      Rights Has: ${friend.rightsHas}")
                            }
                            if (friendsDiag.friendsList.size > 10) {
                                appendLine("  ... and ${friendsDiag.friendsList.size - 10} more")
                            }
                        }
                        
                        if (friendsDiag.totalFriends == 0) {
                            appendLine()
                            appendLine("ℹ️ NO FRIENDS LOADED - Friend list may not have been fetched yet")
                            appendLine("   Friends are populated from login response and OnlineNotification events.")
                        }
                    } else {
                        appendLine("Friends manager not initialized (not logged in)")
                    }
                } catch (e: Exception) {
                    appendLine("Friends diagnostics unavailable: ${e.message}")
                }
            } else {
                appendLine("Friends manager: App not initialized")
            }
            appendLine()

            // ==================== APPEARANCE PIPELINE ====================
            // Surfaces the local agent's bake → upload → AgentSetAppearance
            // round trip so it's visible whether the pipeline ran at all,
            // how many channels were baked, and whether any error fell out.
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ APPEARANCE PIPELINE                                               │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            if (app != null && app.isAppearanceManagerInitialized()) {
                try {
                    val ad = app.appearanceManager.getDiagnostics()
                    appendLine("AgentSetAppearance updates sent: ${ad.updatesSent}")
                    appendLine("Updates failed: ${ad.updatesFailed}")
                    appendLine("Last serial: ${ad.lastSerialSent}")
                    appendLine("Last bake channel count: ${ad.lastBakedTextureCount}")
                    if (ad.lastUpdateAgoMs != null) {
                        appendLine("Last update: ${formatDuration(ad.lastUpdateAgoMs)} ago (took ${ad.lastUpdateDurationMs}ms)")
                    } else {
                        appendLine("Last update: never (no AgentWearablesUpdate or manual trigger yet)")
                    }
                    if (ad.lastUpdateError != null) {
                        appendLine("Last error: ${ad.lastUpdateError}")
                    }
                } catch (e: Exception) {
                    appendLine("AppearanceManager diagnostics unavailable: ${e.message}")
                }
            } else {
                val connected = try { app?.isConnected() == true } catch (_: Exception) { false }
                appendLine(
                    if (connected) "AppearanceManager: not yet initialized (local Avatar not ready — waiting on simulator)"
                    else "AppearanceManager: not yet initialized (not logged in)"
                )
            }
            appendLine()

            // ==================== ENHANCED PACKET LOGGER STATISTICS ====================
            appendLine("┌──────────────────────────────────────────────────────────────────┐")
            appendLine("│ ENHANCED PACKET LOGGER STATISTICS                                 │")
            appendLine("└──────────────────────────────────────────────────────────────────┘")
            appendLine()
            try {
                val packetStats = com.linkpoint.protocol.messages.EnhancedPacketLogger.getStatistics()
                appendLine("Session Duration: ${formatDuration(packetStats.sessionDurationMs)}")
                appendLine()
                appendLine("Packet Statistics:")
                appendLine("  Packets Sent: ${packetStats.packetsSent}")
                appendLine("  Packets Received: ${packetStats.packetsReceived}")
                appendLine("  Bytes Sent: ${formatBytes(packetStats.bytesSent)}")
                appendLine("  Bytes Received: ${formatBytes(packetStats.bytesReceived)}")
                appendLine()
                appendLine("ACK Statistics:")
                appendLine("  ACKs Sent: ${packetStats.acksSent}")
                appendLine("  ACKs Received: ${packetStats.acksReceived}")
                appendLine("  Resend Count: ${packetStats.resendCount}")
                appendLine()
                appendLine("Error Statistics:")
                appendLine("  Parse Errors: ${packetStats.parseErrors}")
                appendLine("  Handler Misses: ${packetStats.handlerMisses}")
                appendLine()
                appendLine("Handler Information:")
                appendLine("  Registered Handlers: ${packetStats.registeredHandlerCount}")
                appendLine("  Unique Message Types Sent: ${packetStats.uniqueMessageTypesSent}")
                appendLine("  Unique Message Types Received: ${packetStats.uniqueMessageTypesReceived}")
                
                if (packetStats.lastPacketSentMs >= 0) {
                    appendLine()
                    appendLine("  Last Packet Sent: ${formatDuration(packetStats.lastPacketSentMs)} ago")
                }
                if (packetStats.lastPacketReceivedMs >= 0) {
                    appendLine("  Last Packet Received: ${formatDuration(packetStats.lastPacketReceivedMs)} ago")
                } else if (packetStats.packetsSent > 0) {
                    appendLine("  Last Packet Received: Never ⚠️")
                }
                
                if (packetStats.packetsSent > 0 && packetStats.packetsReceived == 0L) {
                    val udpElapsedMs = udpConnectedElapsedMs()
                    val isProvisional = udpElapsedMs != null && udpElapsedMs < EARLY_WARNING_GRACE_MS
                    val timing = formatUdpConnectedElapsed(udpElapsedMs)
                    appendLine()
                    if (isProvisional) {
                        appendLine("⚠️ PROVISIONAL: PACKETS SENT BUT NONE RECEIVED ($timing)")
                    } else {
                        appendLine("⚠️ PACKETS SENT BUT NONE RECEIVED ($timing)")
                    }
                    appendLine("   Possible causes:")
                    appendLine("   - Firewall blocking UDP")
                    appendLine("   - NAT traversal issue")
                    appendLine("   - Simulator not responding")
                }
                
                if (packetStats.handlerMisses > 0) {
                    appendLine()
                    appendLine("⚠️ ${packetStats.handlerMisses} messages had no registered handler!")
                }
                
                // Malformed packet statistics - NEW SECTION
                appendLine()
                appendLine("Malformed Packet Statistics:")
                if (packetStats.malformedPackets > 0) {
                    appendLine("  ⚠️ MALFORMED PACKETS DETECTED!")
                    appendLine("  Total Malformed: ${packetStats.malformedPackets}")
                    if (packetStats.truncatedPackets > 0) appendLine("    Truncated (too small): ${packetStats.truncatedPackets}")
                    if (packetStats.invalidFlagsPackets > 0) appendLine("    Invalid Flags: ${packetStats.invalidFlagsPackets}")
                    if (packetStats.invalidMessageIdPackets > 0) appendLine("    Invalid Message ID: ${packetStats.invalidMessageIdPackets}")
                    if (packetStats.corruptedPayloadPackets > 0) appendLine("    Corrupted Payload: ${packetStats.corruptedPayloadPackets}")
                    if (packetStats.zeroDecodeFailures > 0) appendLine("    Zero-Decode Failures: ${packetStats.zeroDecodeFailures}")
                    if (packetStats.oversizedPackets > 0) appendLine("    Oversized Packets: ${packetStats.oversizedPackets}")
                    
                    // Show recent malformed packet details
                    val malformedHistory = com.linkpoint.protocol.messages.EnhancedPacketLogger.getMalformedPacketHistory(MALFORMED_PACKET_HISTORY_COUNT)
                    if (malformedHistory.isNotEmpty()) {
                        appendLine()
                        appendLine("  Recent Malformed Packets:")
                        malformedHistory.forEach { entry ->
                            val relativeTime = System.currentTimeMillis() - entry.timestamp
                            appendLine("    [${formatDuration(relativeTime)} ago] ${entry.reason}")
                            appendLine("      Details: ${entry.details}")
                            appendLine("      Hex: ${entry.hexPreview}...")
                        }
                    }
                } else {
                    appendLine("  ✓ No malformed packets detected")
                }
                
                // Show sent message breakdown
                val sentBreakdown = com.linkpoint.protocol.messages.EnhancedPacketLogger.getSentMessageBreakdown(10)
                if (sentBreakdown.isNotEmpty()) {
                    appendLine()
                    appendLine("Sent Message Types (top 10):")
                    sentBreakdown.forEach { (name, count) ->
                        appendLine("  $name: $count")
                    }
                }
                
                // Show received message breakdown
                val receivedBreakdown = com.linkpoint.protocol.messages.EnhancedPacketLogger.getReceivedMessageBreakdown(10)
                if (receivedBreakdown.isNotEmpty()) {
                    appendLine()
                    appendLine("Received Message Types (top 10):")
                    receivedBreakdown.forEach { (name, count) ->
                        appendLine("  $name: $count")
                    }
                }
            } catch (e: Exception) {
                appendLine("Enhanced packet logger unavailable: ${e.message}")
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

    private fun formatRate(successes: Int, attempts: Int): String {
        if (attempts <= 0) return "n/a (0/0)"
        val pct = (successes.toDouble() * 100.0) / attempts.toDouble()
        return String.format(Locale.US, "%.1f%% (%d/%d)", pct, successes, attempts)
    }
    
    fun shutdown() {
        scope.cancel()
    }
}
