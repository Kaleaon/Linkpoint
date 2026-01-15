package com.linkpoint.scripts

import android.util.Log
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * SLua (Server-side Lua) Script Engine
 * 
 * Provides support for the new Luau-based scripting in Second Life.
 * SLua is based on Luau (Roblox's Lua variant) and offers:
 * - Better performance than LSL/Mono (up to 50% less memory)
 * - Modern language features (coroutines, tables, type inference)
 * - Dynamic event handling
 * - Gradual typing support
 * 
 * This engine handles incoming script events from SLua-compiled scripts
 * running on the server. The viewer doesn't execute Lua code directly -
 * it receives events and state updates from the simulator.
 * 
 * @see https://wiki.secondlife.com/wiki/SLua_Alpha
 * @see https://wiki.secondlife.com/wiki/Luau_Alpha
 */
class SLuaEngine {
    
    companion object {
        private const val TAG = "SLuaEngine"
        
        // Script runtime types
        const val RUNTIME_LSL = "lsl"
        const val RUNTIME_MONO = "mono"
        const val RUNTIME_LUAU = "luau"
        
        // Script states (same as LSL but with Luau-specific additions)
        const val STATE_DEFAULT = "default"
        const val STATE_WAITING = "waiting"
        const val STATE_RUNNING = "running"
        const val STATE_STOPPED = "stopped"
        const val STATE_SUSPENDED = "suspended"  // Luau coroutine suspended
        
        // SLua-specific event types
        const val EVENT_STATE_ENTRY = "state_entry"
        const val EVENT_TIMER = "timer"
        const val EVENT_LISTEN = "listen"
        const val EVENT_TOUCH_START = "touch_start"
        const val EVENT_HTTP_RESPONSE = "http_response"
        const val EVENT_LINK_MESSAGE = "link_message"
        const val EVENT_EXPERIENCE_PERMISSIONS = "experience_permissions"
        const val EVENT_EXPERIENCE_PERMISSIONS_DENIED = "experience_permissions_denied"
        const val EVENT_TRANSACTION_RESULT = "transaction_result"
        
        // Luau-specific capabilities
        const val FEATURE_COROUTINES = "coroutines"
        const val FEATURE_TABLES = "tables"
        const val FEATURE_GRADUAL_TYPING = "gradual_typing"
        const val FEATURE_MULTIPLE_HANDLERS = "multiple_handlers"
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Running scripts (both LSL and SLua)
    private val scripts = ConcurrentHashMap<UUID, SLuaScriptInstance>()
    
    // Script info cache (to display runtime type in UI)
    private val scriptRuntimeInfo = ConcurrentHashMap<UUID, ScriptRuntimeInfo>()
    
    // Listeners (shared with LSL engine)
    private val listenHandles = ConcurrentHashMap<Int, SLuaListenHandle>()
    private var nextListenHandle = 0
    
    // Timers
    private val timers = ConcurrentHashMap<UUID, Job>()
    
    /**
     * Register a script with its runtime information
     */
    fun registerScript(
        scriptId: UUID,
        objectId: UUID,
        ownerId: UUID,
        runtime: String = RUNTIME_LSL,
        permissions: Int = 0
    ): SLuaScriptInstance {
        val instance = SLuaScriptInstance(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            runtime = runtime,
            permissions = permissions
        )
        scripts[scriptId] = instance
        
        // Track runtime info for UI display
        scriptRuntimeInfo[scriptId] = ScriptRuntimeInfo(
            scriptId = scriptId,
            runtime = runtime,
            isLuau = runtime == RUNTIME_LUAU
        )
        
        Log.i(TAG, "Registered script $scriptId with runtime: $runtime")
        return instance
    }
    
    /**
     * Unregister a script
     */
    fun unregisterScript(scriptId: UUID) {
        scripts.remove(scriptId)
        scriptRuntimeInfo.remove(scriptId)
        timers[scriptId]?.cancel()
        timers.remove(scriptId)
        
        // Remove listeners for this script
        listenHandles.entries.filter { it.value.scriptId == scriptId }
            .forEach { listenHandles.remove(it.key) }
        
        Log.i(TAG, "Unregistered script $scriptId")
    }
    
    /**
     * Get runtime info for a script
     */
    fun getScriptRuntimeInfo(scriptId: UUID): ScriptRuntimeInfo? {
        return scriptRuntimeInfo[scriptId]
    }
    
    /**
     * Check if a script is using Luau runtime
     */
    fun isLuauScript(scriptId: UUID): Boolean {
        return scriptRuntimeInfo[scriptId]?.isLuau == true
    }
    
    /**
     * Handle script runtime info from ObjectProperties message
     * The simulator tells us which runtime each script uses
     */
    fun updateScriptRuntime(scriptId: UUID, runtime: String) {
        val info = scriptRuntimeInfo[scriptId]
        if (info != null) {
            scriptRuntimeInfo[scriptId] = info.copy(
                runtime = runtime,
                isLuau = runtime == RUNTIME_LUAU
            )
        } else {
            scriptRuntimeInfo[scriptId] = ScriptRuntimeInfo(
                scriptId = scriptId,
                runtime = runtime,
                isLuau = runtime == RUNTIME_LUAU
            )
        }
    }
    
    /**
     * Handle touch event on object (works for both LSL and SLua)
     */
    fun handleTouch(objectId: UUID, toucherId: UUID, position: Triple<Float, Float, Float>) {
        scripts.values.filter { it.objectId == objectId }.forEach { script ->
            queueEvent(script.scriptId, EVENT_TOUCH_START, mapOf(
                "detected_id" to toucherId.toString(),
                "pos" to position,
                "runtime" to script.runtime
            ))
        }
    }
    
    /**
     * Handle listen event
     */
    fun handleListen(channel: Int, name: String, id: UUID, message: String) {
        listenHandles.values
            .filter { it.channel == channel }
            .filter { it.name.isEmpty() || it.name == name }
            .filter { it.id == null || it.id == id }
            .filter { it.msg.isEmpty() || message.contains(it.msg) }
            .forEach { handle ->
                queueEvent(handle.scriptId, EVENT_LISTEN, mapOf(
                    "channel" to channel,
                    "name" to name,
                    "id" to id.toString(),
                    "message" to message
                ))
            }
    }
    
    /**
     * Handle HTTP response event
     */
    fun handleHttpResponse(requestId: UUID, status: Int, metadata: Map<String, String>, body: String) {
        scripts.values.find { it.pendingHttpRequests.contains(requestId) }?.let { script ->
            script.pendingHttpRequests.remove(requestId)
            queueEvent(script.scriptId, EVENT_HTTP_RESPONSE, mapOf(
                "request_id" to requestId.toString(),
                "status" to status,
                "metadata" to metadata,
                "body" to body
            ))
        }
    }
    
    /**
     * Handle experience permissions event (SLua enhancement)
     */
    fun handleExperiencePermissions(scriptId: UUID, agentId: UUID, permissions: Int) {
        queueEvent(scriptId, EVENT_EXPERIENCE_PERMISSIONS, mapOf(
            "agent_id" to agentId.toString(),
            "permissions" to permissions
        ))
    }
    
    private fun queueEvent(scriptId: UUID, eventName: String, data: Map<String, Any>) {
        val script = scripts[scriptId] ?: return
        script.eventQueue.add(SLuaScriptEvent(eventName, data))
        
        // Log with runtime type for debugging
        Log.d(TAG, "Queued event $eventName for ${script.runtime} script $scriptId")
    }
    
    // =====================================================================
    // SLua-specific functions
    // These may behave differently than LSL equivalents
    // =====================================================================
    
    /**
     * llListen - Start listening on channel
     * SLua allows multiple handlers for the same event
     */
    fun llListen(scriptId: UUID, channel: Int, name: String, id: UUID?, msg: String): Int {
        val handle = ++nextListenHandle
        val script = scripts[scriptId]
        
        listenHandles[handle] = SLuaListenHandle(
            handle = handle,
            scriptId = scriptId,
            channel = channel,
            name = name,
            id = id,
            msg = msg,
            isLuau = script?.runtime == RUNTIME_LUAU
        )
        return handle
    }
    
    /**
     * llListenRemove - Stop listening
     */
    fun llListenRemove(handle: Int) {
        listenHandles.remove(handle)
    }
    
    /**
     * llSetTimerEvent - Set timer
     */
    fun llSetTimerEvent(scriptId: UUID, sec: Float) {
        timers[scriptId]?.cancel()
        
        if (sec <= 0) {
            timers.remove(scriptId)
            return
        }
        
        timers[scriptId] = scope.launch {
            while (isActive) {
                delay((sec * 1000).toLong())
                queueEvent(scriptId, EVENT_TIMER, emptyMap())
            }
        }
    }
    
    /**
     * Get all scripts for an object with their runtime types
     * Useful for showing runtime badges in the UI
     */
    fun getScriptsForObject(objectId: UUID): List<ScriptRuntimeInfo> {
        return scripts.values
            .filter { it.objectId == objectId }
            .mapNotNull { scriptRuntimeInfo[it.scriptId] }
    }
    
    /**
     * Get statistics about script runtimes in current region
     */
    fun getRuntimeStatistics(): Map<String, Int> {
        val stats = mutableMapOf(
            RUNTIME_LSL to 0,
            RUNTIME_MONO to 0,
            RUNTIME_LUAU to 0
        )
        
        scripts.values.forEach { script ->
            stats[script.runtime] = (stats[script.runtime] ?: 0) + 1
        }
        
        return stats
    }
    
    fun shutdown() {
        scope.cancel()
        timers.values.forEach { it.cancel() }
        scripts.clear()
        scriptRuntimeInfo.clear()
    }
}

/**
 * Script instance that supports both LSL and SLua runtimes
 */
data class SLuaScriptInstance(
    val scriptId: UUID,
    val objectId: UUID,
    val ownerId: UUID,
    val runtime: String,  // "lsl", "mono", or "luau"
    val permissions: Int,
    var state: String = SLuaEngine.STATE_DEFAULT,
    val eventQueue: MutableList<SLuaScriptEvent> = mutableListOf(),
    val pendingHttpRequests: MutableSet<UUID> = mutableSetOf()
)

/**
 * Script event with runtime-aware data
 */
data class SLuaScriptEvent(
    val name: String,
    val data: Map<String, Any>
)

/**
 * Listen handle with runtime info
 */
data class SLuaListenHandle(
    val handle: Int,
    val scriptId: UUID,
    val channel: Int,
    val name: String,
    val id: UUID?,
    val msg: String,
    val isLuau: Boolean = false
)

/**
 * Runtime information for UI display
 * Shows which runtime a script is using (LSL/Mono vs Luau)
 */
data class ScriptRuntimeInfo(
    val scriptId: UUID,
    val runtime: String,
    val isLuau: Boolean,
    val memoryUsage: Long = 0,
    val executionTime: Long = 0
)
