package com.linkpoint.scripts.actor

import com.linkpoint.scripts.ScriptEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ScriptActorTest {

    @Test
    fun testScriptActorReceivesAndProcessesEventsSequentially() = runTest {
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val processedEvents = CopyOnWriteArrayList<String>()
        val latch = CountDownLatch(3)

        val actor = ScriptActor(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId
        ).apply {
            eventHandler = { _, event ->
                processedEvents.add(event.name)
                latch.countDown()
            }
        }

        actor.sendEvent(ScriptEvent("event_1", emptyMap()))
        actor.sendEvent(ScriptEvent("event_2", emptyMap()))
        actor.sendEvent(ScriptEvent("event_3", emptyMap()))

        val success = latch.await(2, TimeUnit.SECONDS)
        assertTrue("All events should be processed by actor", success)
        assertEquals(listOf("event_1", "event_2", "event_3"), processedEvents)

        actor.destroy()
        assertTrue(actor.isDestroyed)
    }

    @Test
    fun testScriptActorMailboxOverflowHandling() = runTest {
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        // Create actor with small mailbox capacity
        val smallCapacity = 5
        val gate = CompletableDeferred<Unit>()

        val actor = ScriptActor(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId,
            mailboxCapacity = smallCapacity
        ).apply {
            eventHandler = { _, _ ->
                // Cooperatively suspend handler
                gate.await()
            }
        }

        // Send 20 events rapidly into bounded channel
        for (i in 1..20) {
            actor.sendEvent(ScriptEvent("event_$i", emptyMap()))
        }

        gate.complete(Unit)

        // Wait for actor to process events
        withTimeoutOrNull(2000) {
            while (actor.getProcessedCount() == 0L) {
                delay(10)
            }
        }

        // Verify that actor processed events safely
        assertTrue(actor.getProcessedCount() > 0)
        actor.destroy()
    }

    @Test
    fun testScriptActorCleanDestructionCancelsJobAndClosesChannel() = runTest {
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val actor = ScriptActor(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId
        )

        assertFalse(actor.isDestroyed)

        actor.destroy()

        assertTrue(actor.isDestroyed)
        // Sending after destruction should return false
        val sent = actor.sendEvent(ScriptEvent("after_destroy", emptyMap()))
        assertFalse("Sending to destroyed actor should return false", sent)
    }

    @Test
    fun testStateTransitionSequencing() = runTest {
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val transitionLogs = CopyOnWriteArrayList<String>()

        val actor = ScriptActor(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = ownerId
        )

        assertEquals("default", actor.state)

        actor.transitionState(
            newState = "running",
            onExit = { transitionLogs.add("exit_${it.state}") },
            onEntry = { transitionLogs.add("entry_${it.state}") }
        )

        assertEquals("running", actor.state)
        assertEquals(listOf("exit_default", "entry_running"), transitionLogs)

        actor.destroy()
    }
}
