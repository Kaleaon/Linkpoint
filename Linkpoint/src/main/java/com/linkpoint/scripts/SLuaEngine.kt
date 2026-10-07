package com.linkpoint.scripts

import android.util.Log
import com.linkpoint.scripts.actor.ScriptActor
import com.linkpoint.scripts.actor.ScriptChannelDispatcher
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * SLua Script Engine
 *
 * Uses [ScriptChannelDispatcher] and [ScriptActor] to execute SLua / Luau script events
 * in isolated coroutines using non-blocking Kotlin Channels.
 */
class SLuaEngine(
    val dispatcher: ScriptChannelDispatcher = ScriptChannelDispatcher()
) {

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
        const val EVENT_STATE_ENTRY = ScriptChannelDispatcher.EVENT_STATE_ENTRY
        const val EVENT_TIMER = ScriptChannelDispatcher.EVENT_TIMER
        const val EVENT_LISTEN = ScriptChannelDispatcher.EVENT_LISTEN
        const val EVENT_TOUCH_START = ScriptChannelDispatcher.EVENT_TOUCH_START
        const val EVENT_HTTP_RESPONSE = ScriptChannelDispatcher.EVENT_HTTP_RESPONSE
        const val EVENT_LINK_MESSAGE = ScriptChannelDispatcher.EVENT_LINK_MESSAGE
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

    /**
     * Register a script with its runtime information as an isolated actor.
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

        val actor = dispatcher.registerScript(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            runtime = runtime,
            permissions = permissions,
            eventHandler = { _, event ->
                instance.eventQueue.add(SLuaScriptEvent(event.name, event.data))
            }
        )

        instance.actor = actor
        scripts[scriptId] = instance

        // Track runtime info for UI display
        scriptRuntimeInfo[scriptId] = ScriptRuntimeInfo(
            scriptId = scriptId,
            runtime = runtime,
            isLuau = runtime == RUNTIME_LUAU
        )

        Log.i(TAG, "Registered script actor $scriptId with runtime: $runtime")
        return instance
    }

    /**
     * Unregister a script and cleanly destroy its coroutine actor.
     */
    fun unregisterScript(scriptId: UUID) {
        scripts.remove(scriptId)
        scriptRuntimeInfo.remove(scriptId)
        dispatcher.unregisterScript(scriptId)

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
     * Handle touch event on object
     */
    fun handleTouch(objectId: UUID, toucherId: UUID, position: Triple<Float, Float, Float>) {
        dispatcher.handleTouch(objectId, toucherId, position)
    }

    /**
     * Handle listen event
     */
    fun handleListen(channel: Int, name: String, id: UUID, message: String) {
        dispatcher.handleListen(channel, name, id, message)
    }

    /**
     * Handle HTTP response event
     */
    fun handleHttpResponse(requestId: UUID, status: Int, metadata: Map<String, String>, body: String) {
        dispatcher.handleHttpResponse(requestId, status, metadata, body)
    }

    /**
     * Handle experience permissions event
     */
    fun handleExperiencePermissions(scriptId: UUID, agentId: UUID, permissions: Int) {
        dispatcher.dispatchToScript(
            scriptId,
            EVENT_EXPERIENCE_PERMISSIONS,
            mapOf(
                "agent_id" to agentId.toString(),
                "permissions" to permissions
            )
        )
    }

    /**
     * llListen - Start listening on channel
     */
    fun llListen(scriptId: UUID, channel: Int, name: String, id: UUID?, msg: String): Int {
        return dispatcher.registerListen(scriptId, channel, name, id, msg)
    }

    /**
     * llListenRemove - Stop listening
     */
    fun llListenRemove(handle: Int) {
        dispatcher.removeListen(handle)
    }

    /**
     * llSetTimerEvent - Set a repeating timer
     */
    fun llSetTimerEvent(scriptId: UUID, sec: Float) {
        dispatcher.setTimerEvent(scriptId, sec)
    }

    /**
     * Get all scripts for an object with their runtime types
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
        dispatcher.shutdown()
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
    val pendingHttpRequests: MutableSet<UUID> = mutableSetOf(),
    var actor: ScriptActor? = null
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
 */
data class ScriptRuntimeInfo(
    val scriptId: UUID,
    val runtime: String,
    val isLuau: Boolean,
    val memoryUsage: Long = 0,
    val executionTime: Long = 0
)
