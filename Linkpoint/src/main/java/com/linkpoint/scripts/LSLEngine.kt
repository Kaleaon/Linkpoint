package com.linkpoint.scripts

import android.util.Log
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Basic LSL (Linden Scripting Language) runtime engine
 * This is a simplified implementation for handling incoming script events
 * and managing script state
 */
class LSLEngine {
    
    companion object {
        private const val TAG = "LSLEngine"
        
        // Script states
        const val STATE_DEFAULT = "default"
        const val STATE_WAITING = "waiting"
        const val STATE_RUNNING = "running"
        const val STATE_STOPPED = "stopped"
        
        // Event types
        const val EVENT_STATE_ENTRY = "state_entry"
        const val EVENT_STATE_EXIT = "state_exit"
        const val EVENT_TOUCH_START = "touch_start"
        const val EVENT_TOUCH = "touch"
        const val EVENT_TOUCH_END = "touch_end"
        const val EVENT_COLLISION_START = "collision_start"
        const val EVENT_COLLISION = "collision"
        const val EVENT_COLLISION_END = "collision_end"
        const val EVENT_TIMER = "timer"
        const val EVENT_LISTEN = "listen"
        const val EVENT_MONEY = "money"
        const val EVENT_HTTP_RESPONSE = "http_response"
        const val EVENT_LINK_MESSAGE = "link_message"
        const val EVENT_CHANGED = "changed"
        const val EVENT_DATASERVER = "dataserver"
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Running scripts
    private val scripts = ConcurrentHashMap<UUID, ScriptInstance>()
    
    // Listeners
    private val listenHandles = ConcurrentHashMap<Int, ListenHandle>()
    private var nextListenHandle = 0
    
    // Timers
    private val timers = ConcurrentHashMap<UUID, Job>()
    
    /**
     * Register a script
     */
    fun registerScript(
        scriptId: UUID,
        objectId: UUID,
        ownerId: UUID,
        permissions: Int = 0
    ): ScriptInstance {
        val instance = ScriptInstance(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            permissions = permissions
        )
        scripts[scriptId] = instance
        return instance
    }
    
    /**
     * Unregister a script
     */
    fun unregisterScript(scriptId: UUID) {
        scripts.remove(scriptId)
        timers[scriptId]?.cancel()
        timers.remove(scriptId)
        
        // Remove listeners
        listenHandles.entries.filter { it.value.scriptId == scriptId }
            .forEach { listenHandles.remove(it.key) }
    }
    
    /**
     * Handle touch event on object
     */
    fun handleTouch(objectId: UUID, toucherId: UUID, position: Triple<Float, Float, Float>) {
        // Find scripts in this object
        scripts.values.filter { it.objectId == objectId }.forEach { script ->
            queueEvent(script.scriptId, EVENT_TOUCH_START, mapOf(
                "detected_id" to toucherId.toString(),
                "pos" to position
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
     * Handle link message
     */
    fun handleLinkMessage(objectId: UUID, senderNum: Int, num: Int, str: String, id: UUID) {
        scripts.values.filter { it.objectId == objectId }.forEach { script ->
            queueEvent(script.scriptId, EVENT_LINK_MESSAGE, mapOf(
                "sender_num" to senderNum,
                "num" to num,
                "str" to str,
                "id" to id.toString()
            ))
        }
    }
    
    /**
     * Handle timer event
     */
    private fun handleTimer(scriptId: UUID) {
        queueEvent(scriptId, EVENT_TIMER, emptyMap())
    }
    
    /**
     * Handle HTTP response
     */
    fun handleHttpResponse(requestId: UUID, status: Int, metadata: Map<String, String>, body: String) {
        // Find script that made this request
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
    
    private fun queueEvent(scriptId: UUID, eventName: String, data: Map<String, Any>) {
        val script = scripts[scriptId] ?: return
        script.eventQueue.add(ScriptEvent(eventName, data))
    }
    
    // LSL Functions implementation (called by script handlers)
    
    /**
     * llSay - Say message on channel
     */
    fun llSay(scriptId: UUID, channel: Int, message: String) {
        // Would send ChatFromViewer
        Log.d(TAG, "llSay($channel, $message)")
    }
    
    /**
     * llWhisper - Whisper message on channel
     */
    fun llWhisper(scriptId: UUID, channel: Int, message: String) {
        Log.d(TAG, "llWhisper($channel, $message)")
    }
    
    /**
     * llShout - Shout message on channel
     */
    fun llShout(scriptId: UUID, channel: Int, message: String) {
        Log.d(TAG, "llShout($channel, $message)")
    }
    
    /**
     * llListen - Start listening on channel
     */
    fun llListen(scriptId: UUID, channel: Int, name: String, id: UUID?, msg: String): Int {
        val handle = ++nextListenHandle
        listenHandles[handle] = ListenHandle(
            handle = handle,
            scriptId = scriptId,
            channel = channel,
            name = name,
            id = id,
            msg = msg
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
                handleTimer(scriptId)
            }
        }
    }
    
    /**
     * llGetPos - Get object position
     */
    fun llGetPos(scriptId: UUID): Triple<Float, Float, Float>? {
        val script = scripts[scriptId] ?: return null
        // Would query object position
        return Triple(128f, 128f, 30f)
    }
    
    /**
     * llSetPos - Set object position
     */
    fun llSetPos(scriptId: UUID, pos: Triple<Float, Float, Float>) {
        val script = scripts[scriptId] ?: return
        // Would send ObjectPosition update
    }
    
    /**
     * llGetRot - Get object rotation
     */
    fun llGetRot(scriptId: UUID): FloatArray? {
        val script = scripts[scriptId] ?: return null
        // Would query object rotation
        return floatArrayOf(0f, 0f, 0f, 1f) // Identity quaternion
    }
    
    /**
     * llSetRot - Set object rotation
     */
    fun llSetRot(scriptId: UUID, rot: FloatArray) {
        val script = scripts[scriptId] ?: return
        // Would send ObjectRotation update
    }
    
    /**
     * llGetOwner - Get object owner
     */
    fun llGetOwner(scriptId: UUID): UUID? {
        return scripts[scriptId]?.ownerId
    }
    
    /**
     * llHTTPRequest - Make HTTP request
     */
    fun llHTTPRequest(scriptId: UUID, url: String, params: List<String>, body: String): UUID {
        val requestId = UUID.randomUUID()
        scripts[scriptId]?.pendingHttpRequests?.add(requestId)
        
        scope.launch {
            // Would make actual HTTP request
            // Then call handleHttpResponse
        }
        
        return requestId
    }
    
    /**
     * llGiveInventory - Give inventory item
     */
    fun llGiveInventory(scriptId: UUID, destinationId: UUID, inventoryName: String) {
        // Would send inventory give
    }
    
    /**
     * llMessageLinked - Send message to linked prims
     */
    fun llMessageLinked(scriptId: UUID, linkNum: Int, num: Int, str: String, id: UUID) {
        val script = scripts[scriptId] ?: return
        handleLinkMessage(script.objectId, linkNum, num, str, id)
    }
    
    fun shutdown() {
        scope.cancel()
        timers.values.forEach { it.cancel() }
    }
}

data class ScriptInstance(
    val scriptId: UUID,
    val objectId: UUID,
    val ownerId: UUID,
    val permissions: Int,
    var state: String = LSLEngine.STATE_DEFAULT,
    val eventQueue: MutableList<ScriptEvent> = mutableListOf(),
    val pendingHttpRequests: MutableSet<UUID> = mutableSetOf()
)

data class ScriptEvent(
    val name: String,
    val data: Map<String, Any>
)

data class ListenHandle(
    val handle: Int,
    val scriptId: UUID,
    val channel: Int,
    val name: String,
    val id: UUID?,
    val msg: String
)
