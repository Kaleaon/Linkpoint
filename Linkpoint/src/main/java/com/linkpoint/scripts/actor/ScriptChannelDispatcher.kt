package com.linkpoint.scripts.actor

import android.util.Log
import com.linkpoint.scripts.ListenHandle
import com.linkpoint.scripts.ScriptEvent
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread-safe, non-blocking event channel dispatcher for script actors.
 *
 * Routes simulator events (touches, chat listen, HTTP responses, link messages, timers)
 * directly into script actor channel mailboxes without taking coarse-grained blocking locks.
 */
class ScriptChannelDispatcher(
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    companion object {
        private const val TAG = "ScriptChannelDispatcher"

        // Script events
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

    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    // Map of active script actors by scriptId
    private val actors = ConcurrentHashMap<UUID, ScriptActor>()

    // Listen handles mapped by handle id
    private val listenHandles = ConcurrentHashMap<Int, ListenHandle>()
    private val nextListenHandle = AtomicInteger(0)

    // Active timers
    private val timers = ConcurrentHashMap<UUID, Job>()

    /**
     * Register a new script instance as an isolated coroutine actor.
     */
    fun registerScript(
        scriptId: UUID,
        objectId: UUID,
        ownerId: UUID,
        runtime: String = "lsl",
        permissions: Int = 0,
        mailboxCapacity: Int = ScriptActor.DEFAULT_MAILBOX_CAPACITY,
        eventHandler: (suspend (ScriptActor, ScriptEvent) -> Unit)? = null
    ): ScriptActor {
        // Destroy existing actor if re-registering
        actors[scriptId]?.destroy()

        val actor = ScriptActor(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            runtime = runtime,
            permissions = permissions,
            mailboxCapacity = mailboxCapacity,
            dispatcher = dispatcher
        ).apply {
            this.eventHandler = eventHandler
        }

        actors[scriptId] = actor
        Log.i(TAG, "Registered script actor $scriptId for object $objectId (runtime=$runtime)")

        // Dispatch initial state_entry event
        dispatchToScript(scriptId, EVENT_STATE_ENTRY, emptyMap())

        return actor
    }

    /**
     * Unregister a script instance, cleanly cancelling its actor job and clearing channel queues.
     */
    fun unregisterScript(scriptId: UUID) {
        val actor = actors.remove(scriptId)
        if (actor != null) {
            // Cancel timer
            timers[scriptId]?.cancel()
            timers.remove(scriptId)

            // Remove listen handles
            listenHandles.entries.filter { it.value.scriptId == scriptId }
                .forEach { listenHandles.remove(it.key) }

            // Destroy actor
            actor.destroy()
            Log.i(TAG, "Unregistered script actor $scriptId")
        }
    }

    /**
     * Get a registered script actor by scriptId.
     */
    fun getActor(scriptId: UUID): ScriptActor? = actors[scriptId]

    /**
     * Get all registered script actors for a given objectId.
     */
    fun getActorsForObject(objectId: UUID): List<ScriptActor> {
        return actors.values.filter { it.objectId == objectId && !it.isDestroyed }
    }

    /**
     * Dispatch an event directly to a specific script actor without thread locks.
     */
    fun dispatchToScript(scriptId: UUID, eventName: String, data: Map<String, Any>): Boolean {
        val actor = actors[scriptId] ?: return false
        return actor.sendEvent(ScriptEvent(eventName, data))
    }

    /**
     * Dispatch an event to all script actors attached to an object.
     */
    fun dispatchToObject(objectId: UUID, eventName: String, data: Map<String, Any>): Int {
        var count = 0
        getActorsForObject(objectId).forEach { actor ->
            if (actor.sendEvent(ScriptEvent(eventName, data))) {
                count++
            }
        }
        return count
    }

    /**
     * Handle touch event on an object.
     */
    fun handleTouch(objectId: UUID, toucherId: UUID, position: Triple<Float, Float, Float>) {
        dispatchToObject(
            objectId,
            EVENT_TOUCH_START,
            mapOf(
                "detected_id" to toucherId.toString(),
                "pos" to position
            )
        )
    }

    /**
     * Handle chat listen event.
     */
    fun handleListen(channel: Int, name: String, id: UUID, message: String) {
        listenHandles.values
            .filter { it.channel == channel }
            .filter { it.name.isEmpty() || it.name == name }
            .filter { it.id == null || it.id == id }
            .filter { it.msg.isEmpty() || message.contains(it.msg) }
            .forEach { handle ->
                dispatchToScript(
                    handle.scriptId,
                    EVENT_LISTEN,
                    mapOf(
                        "channel" to channel,
                        "name" to name,
                        "id" to id.toString(),
                        "message" to message
                    )
                )
            }
    }

    /**
     * Handle link message event between linked prims.
     */
    fun handleLinkMessage(objectId: UUID, senderNum: Int, num: Int, str: String, id: UUID) {
        dispatchToObject(
            objectId,
            EVENT_LINK_MESSAGE,
            mapOf(
                "sender_num" to senderNum,
                "num" to num,
                "str" to str,
                "id" to id.toString()
            )
        )
    }

    /**
     * Handle HTTP response event. Resumes suspended script actor asynchronously without region tick stalling.
     */
    fun handleHttpResponse(requestId: UUID, status: Int, metadata: Map<String, String>, body: String) {
        actors.values.find { it.pendingHttpRequests.contains(requestId) }?.let { actor ->
            actor.pendingHttpRequests.remove(requestId)
            actor.sendEvent(
                ScriptEvent(
                    EVENT_HTTP_RESPONSE,
                    mapOf(
                        "request_id" to requestId.toString(),
                        "status" to status,
                        "metadata" to metadata,
                        "body" to body
                    )
                )
            )
        }
    }

    /**
     * Handle dataserver reply event.
     */
    fun handleDataserverResponse(requestId: UUID, data: String) {
        actors.values.find { it.pendingHttpRequests.contains(requestId) }?.let { actor ->
            actor.pendingHttpRequests.remove(requestId)
            actor.sendEvent(
                ScriptEvent(
                    EVENT_DATASERVER,
                    mapOf(
                        "query_id" to requestId.toString(),
                        "data" to data
                    )
                )
            )
        }
    }

    /**
     * Set a repeating timer event for a script actor.
     */
    fun setTimerEvent(scriptId: UUID, sec: Float) {
        timers[scriptId]?.cancel()

        if (sec <= 0) {
            timers.remove(scriptId)
            return
        }

        timers[scriptId] = scope.launch {
            val delayMs = (sec * 1000).toLong()
            while (isActive) {
                delay(delayMs)
                dispatchToScript(scriptId, EVENT_TIMER, emptyMap())
            }
        }
    }

    /**
     * Register a listen handle for llListen.
     */
    fun registerListen(scriptId: UUID, channel: Int, name: String, id: UUID?, msg: String): Int {
        val handle = nextListenHandle.incrementAndGet()
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
     * Remove a listen handle for llListenRemove.
     */
    fun removeListen(handle: Int) {
        listenHandles.remove(handle)
    }

    fun getActiveActorCount(): Int = actors.size

    fun getTotalProcessedEvents(): Long = actors.values.sumOf { it.getProcessedCount() }

    fun getTotalOverflowEvents(): Long = actors.values.sumOf { it.getOverflowCount() }

    /**
     * Shutdown dispatcher and destroy all script actors cleanly.
     */
    fun shutdown() {
        scope.cancel()
        timers.values.forEach { it.cancel() }
        timers.clear()
        listenHandles.clear()
        actors.values.forEach { it.destroy() }
        actors.clear()
        Log.i(TAG, "ScriptChannelDispatcher shutdown completed")
    }
}
