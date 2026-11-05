package com.linkpoint.scripts

import android.util.Log
import com.linkpoint.scripts.actor.ScriptActor
import com.linkpoint.scripts.actor.ScriptChannelDispatcher
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Basic LSL (Linden Scripting Language) runtime engine
 *
 * Uses [ScriptChannelDispatcher] and [ScriptActor] to execute script event handling
 * within isolated coroutines communicating via non-blocking Kotlin Channels.
 */
class LSLEngine(
    val dispatcher: ScriptChannelDispatcher = ScriptChannelDispatcher()
) {

    companion object {
        private const val TAG = "LSLEngine"

        // Script states
        const val STATE_DEFAULT = "default"
        const val STATE_WAITING = "waiting"
        const val STATE_RUNNING = "running"
        const val STATE_STOPPED = "stopped"

        // Event types
        const val EVENT_STATE_ENTRY = ScriptChannelDispatcher.EVENT_STATE_ENTRY
        const val EVENT_STATE_EXIT = ScriptChannelDispatcher.EVENT_STATE_EXIT
        const val EVENT_TOUCH_START = ScriptChannelDispatcher.EVENT_TOUCH_START
        const val EVENT_TOUCH = ScriptChannelDispatcher.EVENT_TOUCH
        const val EVENT_TOUCH_END = ScriptChannelDispatcher.EVENT_TOUCH_END
        const val EVENT_COLLISION_START = ScriptChannelDispatcher.EVENT_COLLISION_START
        const val EVENT_COLLISION = ScriptChannelDispatcher.EVENT_COLLISION
        const val EVENT_COLLISION_END = ScriptChannelDispatcher.EVENT_COLLISION_END
        const val EVENT_TIMER = ScriptChannelDispatcher.EVENT_TIMER
        const val EVENT_LISTEN = ScriptChannelDispatcher.EVENT_LISTEN
        const val EVENT_MONEY = ScriptChannelDispatcher.EVENT_MONEY
        const val EVENT_HTTP_RESPONSE = ScriptChannelDispatcher.EVENT_HTTP_RESPONSE
        const val EVENT_LINK_MESSAGE = ScriptChannelDispatcher.EVENT_LINK_MESSAGE
        const val EVENT_CHANGED = ScriptChannelDispatcher.EVENT_CHANGED
        const val EVENT_DATASERVER = ScriptChannelDispatcher.EVENT_DATASERVER
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Legacy script instances lookup map
    private val scripts = ConcurrentHashMap<UUID, ScriptInstance>()

    /**
     * Register a script actor.
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

        val actor = dispatcher.registerScript(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            runtime = "lsl",
            permissions = permissions,
            eventHandler = { _, event ->
                // Maintain event history on legacy instance for backward compatibility
                instance.eventQueue.add(event)
            }
        )

        instance.actor = actor
        scripts[scriptId] = instance
        return instance
    }

    /**
     * Unregister a script actor.
     */
    fun unregisterScript(scriptId: UUID) {
        scripts.remove(scriptId)
        dispatcher.unregisterScript(scriptId)
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
     * Handle link message
     */
    fun handleLinkMessage(objectId: UUID, senderNum: Int, num: Int, str: String, id: UUID) {
        dispatcher.handleLinkMessage(objectId, senderNum, num, str, id)
    }

    /**
     * Handle HTTP response
     */
    fun handleHttpResponse(requestId: UUID, status: Int, metadata: Map<String, String>, body: String) {
        dispatcher.handleHttpResponse(requestId, status, metadata, body)
    }

    /**
     * Queue event into a script's actor channel mailbox.
     */
    fun queueEvent(scriptId: UUID, eventName: String, data: Map<String, Any>) {
        dispatcher.dispatchToScript(scriptId, eventName, data)
    }

    // LSL Functions implementation (called by script handlers)

    /**
     * llSay - Say message on channel
     */
    fun llSay(scriptId: UUID, channel: Int, message: String) {
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
        return dispatcher.registerListen(scriptId, channel, name, id, msg)
    }

    /**
     * llListenRemove - Stop listening
     */
    fun llListenRemove(handle: Int) {
        dispatcher.removeListen(handle)
    }

    /**
     * llSetTimerEvent - Set repeating timer
     */
    fun llSetTimerEvent(scriptId: UUID, sec: Float) {
        dispatcher.setTimerEvent(scriptId, sec)
    }

    /**
     * llGetPos - Get object position
     */
    fun llGetPos(scriptId: UUID): Triple<Float, Float, Float>? {
        val script = scripts[scriptId] ?: return null
        return Triple(128f, 128f, 30f)
    }

    /**
     * llSetPos - Set object position
     */
    fun llSetPos(scriptId: UUID, pos: Triple<Float, Float, Float>) {
        val script = scripts[scriptId] ?: return
    }

    /**
     * llGetRot - Get object rotation
     */
    fun llGetRot(scriptId: UUID): FloatArray? {
        val script = scripts[scriptId] ?: return null
        return floatArrayOf(0f, 0f, 0f, 1f)
    }

    /**
     * llSetRot - Set object rotation
     */
    fun llSetRot(scriptId: UUID, rot: FloatArray) {
        val script = scripts[scriptId] ?: return
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
        dispatcher.getActor(scriptId)?.pendingHttpRequests?.add(requestId)

        scope.launch {
            // Asynchronous HTTP request execution placeholder
        }

        return requestId
    }

    /**
     * llGiveInventory - Give inventory item
     */
    fun llGiveInventory(scriptId: UUID, destinationId: UUID, inventoryName: String) {
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
        dispatcher.shutdown()
        scripts.clear()
    }
}

data class ScriptInstance(
    val scriptId: UUID,
    val objectId: UUID,
    val ownerId: UUID,
    val permissions: Int,
    var state: String = LSLEngine.STATE_DEFAULT,
    val eventQueue: MutableList<ScriptEvent> = mutableListOf(),
    val pendingHttpRequests: MutableSet<UUID> = mutableSetOf(),
    var actor: ScriptActor? = null
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
