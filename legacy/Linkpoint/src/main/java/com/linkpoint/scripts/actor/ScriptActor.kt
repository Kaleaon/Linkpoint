package com.linkpoint.scripts.actor

import android.util.Log
import com.linkpoint.scripts.ScriptEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Isolated coroutine actor for OpenSim / Second Life script instances.
 *
 * Each script instance is modeled as an isolated actor with its own event Channel mailbox.
 * Event processing is strictly sequential per script actor, ensuring state integrity,
 * while suspension points (delays, HTTP requests, state changes) yield cooperatively without
 * blocking thread pool executors or stalling region simulation ticks.
 */
class ScriptActor(
    val scriptId: UUID,
    val objectId: UUID,
    val ownerId: UUID,
    val runtime: String = "lsl",
    val permissions: Int = 0,
    val mailboxCapacity: Int = DEFAULT_MAILBOX_CAPACITY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    companion object {
        private const val TAG = "ScriptActor"
        const val DEFAULT_MAILBOX_CAPACITY = 1000
    }

    @Volatile
    var state: String = "default"
        private set

    @Volatile
    var isDestroyed: Boolean = false
        private set

    // Bounded Kotlin Channel mailbox to prevent lockless memory leaks under burst traffic
    private val mailbox = Channel<ScriptEvent>(
        capacity = mailboxCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val job = SupervisorJob()
    private val actorScope = CoroutineScope(dispatcher + job)

    val pendingHttpRequests: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    private val processedEvents = AtomicLong(0)
    private val overflowEvents = AtomicLong(0)

    // Optional event listener for script execution
    var eventHandler: (suspend (ScriptActor, ScriptEvent) -> Unit)? = null

    // Actor loop coroutine job
    private val actorJob: Job = actorScope.launch {
        runActorLoop()
    }

    private suspend fun runActorLoop() {
        for (event in mailbox) {
            if (isDestroyed) break
            try {
                // Execute handler cooperatively
                eventHandler?.invoke(this@ScriptActor, event)
                processedEvents.incrementAndGet()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Log.e(TAG, "Unhandled exception in ScriptActor $scriptId for event ${event.name}", e)
            }
            // Cooperative yield to keep thread pool balanced
            yield()
        }
    }

    /**
     * Send an event into the actor's channel mailbox without taking blocking thread locks.
     * Uses non-blocking trySend. Returns true if queued, false if destroyed or channel closed.
     */
    fun sendEvent(event: ScriptEvent): Boolean {
        if (isDestroyed) return false
        val result = mailbox.trySend(event)
        return if (result.isSuccess) {
            true
        } else {
            if (result.isFailure && !result.isClosed) {
                overflowEvents.incrementAndGet()
                Log.w(TAG, "Mailbox buffer overflow for script $scriptId, dropped oldest event")
            }
            !result.isClosed
        }
    }

    /**
     * Suspending event dispatch for scenarios requiring backpressure instead of dropping events.
     */
    suspend fun sendEventSuspending(event: ScriptEvent) {
        if (isDestroyed) return
        mailbox.send(event)
    }

    /**
     * Transition script state safely with entry/exit handler sequencing.
     * Suspends cooperatively without holding OS threads.
     */
    suspend fun transitionState(
        newState: String,
        onExit: (suspend (ScriptActor) -> Unit)? = null,
        onEntry: (suspend (ScriptActor) -> Unit)? = null
    ) {
        if (isDestroyed || state == newState) return

        val oldState = state

        // Execute state_exit
        try {
            onExit?.invoke(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error in state_exit for script $scriptId state $oldState", e)
        }

        yield()

        state = newState

        yield()

        // Execute state_entry
        try {
            onEntry?.invoke(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error in state_entry for script $scriptId state $newState", e)
        }
    }

    /**
     * Cleanly destroy script actor, cancel coroutine job, and clear channel queues.
     */
    fun destroy() {
        if (isDestroyed) return
        isDestroyed = true

        // Cancel job and close mailbox
        job.cancel()
        mailbox.close()

        pendingHttpRequests.clear()
        Log.d(TAG, "Destroyed ScriptActor $scriptId for object $objectId")
    }

    fun getProcessedCount(): Long = processedEvents.get()
    fun getOverflowCount(): Long = overflowEvents.get()
}
