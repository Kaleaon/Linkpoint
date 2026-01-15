package com.linkpoint.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Tracks initialization events for diagnostic purposes.
 * 
 * This allows us to understand the sequence of events during login
 * and post-login initialization, which is critical for debugging
 * issues like "world not loading" or "capabilities not ready".
 * 
 * Events are timestamped and stored for inclusion in debug reports.
 */
object InitializationTracker {
    private const val TAG = "InitTracker"
    private const val MAX_EVENTS = 200
    
    private val events = ConcurrentLinkedQueue<InitEvent>()
    @Volatile private var sessionStartTime: Long = 0
    
    // Phase tracking (thread-safe collections and volatile for cross-thread access)
    @Volatile private var currentPhase: Phase = Phase.NOT_STARTED
    private val phaseTimings = java.util.concurrent.ConcurrentHashMap<Phase, Long>()
    // Completed phases (true = completed)
    private val phaseCompletions = java.util.concurrent.ConcurrentHashMap<Phase, Boolean>()
    private val phasesFailed = java.util.concurrent.ConcurrentHashMap.newKeySet<Phase>()
    
    enum class Phase {
        NOT_STARTED,
        LOGIN_STARTING,
        LOGIN_HTTP_REQUEST,
        LOGIN_RESPONSE_PARSING,
        LOGIN_SUCCESS,
        SESSION_SETUP,
        UDP_CONNECTING,
        UDP_CONNECTED,
        CAPABILITIES_FETCHING,
        CAPABILITIES_READY,
        MESSAGE_HANDLERS_REGISTERING,
        MESSAGE_HANDLERS_READY,
        REGION_HANDSHAKE_RECEIVED,
        REGION_HANDSHAKE_REPLIED,
        AGENT_MOVEMENT_COMPLETE,
        WORLD_LOADING,
        FULLY_CONNECTED
    }
    
    enum class EventType {
        INFO,
        WARNING,
        ERROR,
        PHASE_START,
        PHASE_COMPLETE,
        CRITICAL
    }
    
    data class InitEvent(
        val timestamp: Long,
        val relativeMs: Long,
        val type: EventType,
        val phase: Phase,
        val message: String,
        val details: String? = null
    )
    
    /**
     * Start a new session tracking.
     * Call this at the beginning of login.
     */
    fun startSession() {
        events.clear()
        phaseTimings.clear()
        phaseCompletions.clear()
        phasesFailed.clear()
        sessionStartTime = System.currentTimeMillis()
        currentPhase = Phase.NOT_STARTED
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ INITIALIZATION TRACKING STARTED")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        logEvent(EventType.INFO, Phase.NOT_STARTED, "Session tracking started")
    }
    
    /**
     * Record a phase starting.
     */
    fun startPhase(phase: Phase, message: String = "") {
        currentPhase = phase
        phaseTimings[phase] = System.currentTimeMillis()
        phaseCompletions.remove(phase)
        
        val msg = if (message.isNotEmpty()) "$phase: $message" else "$phase started"
        logEvent(EventType.PHASE_START, phase, msg)
        Log.i(TAG, "[${getRelativeTimeString()}] ▶ $msg")
    }
    
    /**
     * Record a phase completing successfully.
     */
    fun completePhase(phase: Phase, message: String = "") {
        phaseCompletions[phase] = true // true = completed
        val duration = phaseTimings[phase]?.let { System.currentTimeMillis() - it } ?: 0
        
        val msg = if (message.isNotEmpty()) "$phase: $message (${duration}ms)" else "$phase completed (${duration}ms)"
        logEvent(EventType.PHASE_COMPLETE, phase, msg)
        Log.i(TAG, "[${getRelativeTimeString()}] ✓ $msg")
    }
    
    /**
     * Record a phase failing.
     */
    fun failPhase(phase: Phase, reason: String) {
        phaseCompletions.remove(phase)
        phasesFailed.add(phase)
        val duration = phaseTimings[phase]?.let { System.currentTimeMillis() - it } ?: 0
        
        val msg = "$phase FAILED after ${duration}ms: $reason"
        logEvent(EventType.ERROR, phase, msg)
        Log.e(TAG, "[${getRelativeTimeString()}] ✗ $msg")
    }
    
    /**
     * Log an informational event.
     */
    fun logInfo(message: String, details: String? = null) {
        logEvent(EventType.INFO, currentPhase, message, details)
        Log.d(TAG, "[${getRelativeTimeString()}] $message")
    }
    
    /**
     * Log a warning event.
     */
    fun logWarning(message: String, details: String? = null) {
        logEvent(EventType.WARNING, currentPhase, message, details)
        Log.w(TAG, "[${getRelativeTimeString()}] ⚠ $message")
    }
    
    /**
     * Log an error event.
     */
    fun logError(message: String, details: String? = null) {
        logEvent(EventType.ERROR, currentPhase, message, details)
        Log.e(TAG, "[${getRelativeTimeString()}] ✗ $message")
    }
    
    /**
     * Log a critical event (major milestone or issue).
     */
    fun logCritical(message: String, details: String? = null) {
        logEvent(EventType.CRITICAL, currentPhase, message, details)
        Log.w(TAG, "[${getRelativeTimeString()}] ⭐ $message")
    }
    
    private fun logEvent(type: EventType, phase: Phase, message: String, details: String? = null) {
        val now = System.currentTimeMillis()
        val relative = if (sessionStartTime > 0) now - sessionStartTime else 0
        
        events.add(InitEvent(now, relative, type, phase, message, details))
        
        // Keep queue size bounded
        while (events.size > MAX_EVENTS) {
            events.poll()
        }
    }
    
    private fun getRelativeTimeString(): String {
        val relative = if (sessionStartTime > 0) System.currentTimeMillis() - sessionStartTime else 0
        return "${relative}ms"
    }
    
    /**
     * Get diagnostic summary for debug reports.
     */
    fun getDiagnostics(): InitializationDiagnostics {
        val now = System.currentTimeMillis()
        val sessionDuration = if (sessionStartTime > 0) now - sessionStartTime else 0
        
        // Count events by type
        val infos = events.count { it.type == EventType.INFO }
        val warnings = events.count { it.type == EventType.WARNING }
        val errors = events.count { it.type == EventType.ERROR }
        
        // Get completed phases (phaseCompletions[phase] == true)
        val completedPhases = phaseCompletions.keys.toList()
        
        // Get failed phases (tracked in phasesFailed set)
        val failedPhasesList = phasesFailed.toList()
        
        // Get pending phases (started but not completed or failed)
        val pendingPhases = phaseTimings.keys.filter { phase ->
            !phasesFailed.contains(phase) && !phaseCompletions.containsKey(phase)
        }
        
        return InitializationDiagnostics(
            sessionStartTime = sessionStartTime,
            sessionDurationMs = sessionDuration,
            currentPhase = currentPhase,
            totalEvents = events.size,
            infoCount = infos,
            warningCount = warnings,
            errorCount = errors,
            completedPhases = completedPhases,
            failedPhases = failedPhasesList,
            pendingPhases = pendingPhases,
            recentEvents = events.toList().takeLast(30)
        )
    }
    
    /**
     * Get formatted timeline for debug report.
     */
    fun getTimelineReport(): String {
        return buildString {
            appendLine("Session Start: ${formatTimestamp(sessionStartTime)}")
            appendLine("Current Phase: $currentPhase")
            appendLine()
            appendLine("Event Timeline:")
            appendLine("─".repeat(60))
            
            events.toList().forEach { event ->
                val icon = when (event.type) {
                    EventType.PHASE_START -> "▶"
                    EventType.PHASE_COMPLETE -> "✓"
                    EventType.ERROR -> "✗"
                    EventType.WARNING -> "⚠"
                    EventType.CRITICAL -> "⭐"
                    EventType.INFO -> "•"
                }
                appendLine("[${event.relativeMs}ms] $icon ${event.message}")
                event.details?.let { appendLine("         $it") }
            }
            
            appendLine("─".repeat(60))
            appendLine()
            
            // Phase summary - simplified to avoid O(n²) complexity
            appendLine("Phase Summary:")
            Phase.values().forEach { phase ->
                val started = phaseTimings.containsKey(phase)
                val failed = phasesFailed.contains(phase)
                
                val status = when {
                    phaseCompletions.containsKey(phase) -> "✓ Complete"
                    failed -> "✗ Failed"
                    started -> "⏳ In Progress"
                    else -> "○ Not Started"
                }
                
                appendLine("  $phase: $status")
            }
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp == 0L) return "Not started"
        // Use DateTimeFormatter for API 26+ (thread-safe), fallback to creating new SimpleDateFormat (also thread-safe since we create new instance each call)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
        }
    }
    
    /**
     * Diagnostic data class for initialization tracking.
     */
    data class InitializationDiagnostics(
        val sessionStartTime: Long,
        val sessionDurationMs: Long,
        val currentPhase: Phase,
        val totalEvents: Int,
        val infoCount: Int,
        val warningCount: Int,
        val errorCount: Int,
        val completedPhases: List<Phase>,
        val failedPhases: List<Phase>,
        val pendingPhases: List<Phase>,
        val recentEvents: List<InitEvent>
    )
}
