package com.linkpoint.scripts.actor

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ScriptActorDispatcherTest {

    @Test
    fun testMassiveConcurrentScriptHandlingWithoutLockContention() = runTest {
        val dispatcher = ScriptChannelDispatcher()
        val scriptCount = 1000
        val eventCounter = AtomicInteger(0)
        val latch = CountDownLatch(scriptCount)

        val scriptIds = ArrayList<UUID>(scriptCount)
        val objectId = UUID.randomUUID()

        // Register 1000 scripts concurrently
        for (i in 0 until scriptCount) {
            val scriptId = UUID.randomUUID()
            scriptIds.add(scriptId)
            dispatcher.registerScript(
                scriptId = scriptId,
                objectId = objectId,
                ownerId = UUID.randomUUID(),
                eventHandler = { _, event ->
                    if (event.name == ScriptChannelDispatcher.EVENT_TOUCH_START) {
                        eventCounter.incrementAndGet()
                        latch.countDown()
                    }
                }
            )
        }

        assertEquals(scriptCount, dispatcher.getActiveActorCount())

        // Dispatch touch event to all 1000 scripts simultaneously without blocking
        val startTime = System.currentTimeMillis()
        dispatcher.handleTouch(objectId, UUID.randomUUID(), Triple(128f, 128f, 30f))
        val dispatchDuration = System.currentTimeMillis() - startTime

        // Dispatching should be virtually instantaneous (non-blocking trySend)
        assertTrue("Dispatch duration ($dispatchDuration ms) should be non-blocking (<500ms)", dispatchDuration < 500)

        val success = latch.await(5, TimeUnit.SECONDS)
        assertTrue("All 1000 concurrent script actors should process touch event", success)
        assertEquals(scriptCount, eventCounter.get())

        dispatcher.shutdown()
    }

    @Test
    fun testAsynchronousHttpResponseSuspendsAndResumesScriptActor() = runTest {
        val dispatcher = ScriptChannelDispatcher()
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val requestId = UUID.randomUUID()

        val responseReceivedLatch = CountDownLatch(1)
        val receivedData = ConcurrentHashMap<String, Any>()

        val actor = dispatcher.registerScript(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = UUID.randomUUID(),
            eventHandler = { _, event ->
                if (event.name == ScriptChannelDispatcher.EVENT_HTTP_RESPONSE) {
                    receivedData.putAll(event.data)
                    responseReceivedLatch.countDown()
                }
            }
        )

        // Mark pending HTTP request
        actor.pendingHttpRequests.add(requestId)

        // Simulate asynchronous HTTP response returning from web service
        dispatcher.handleHttpResponse(
            requestId = requestId,
            status = 200,
            metadata = mapOf("content-type" to "application/json"),
            body = "{\"status\":\"ok\"}"
        )

        val success = responseReceivedLatch.await(2, TimeUnit.SECONDS)
        assertTrue("Script actor should receive and resume on HTTP response", success)
        assertEquals(200, receivedData["status"])
        assertEquals("{\"status\":\"ok\"}", receivedData["body"])

        dispatcher.shutdown()
    }

    @Test
    fun testRepeatingTimerDispatch() = runTest {
        val dispatcher = ScriptChannelDispatcher()
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()

        val timerLatch = CountDownLatch(3)

        dispatcher.registerScript(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = UUID.randomUUID(),
            eventHandler = { _, event ->
                if (event.name == ScriptChannelDispatcher.EVENT_TIMER) {
                    timerLatch.countDown()
                }
            }
        )

        // Set 0.05s timer
        dispatcher.setTimerEvent(scriptId, 0.05f)

        val success = timerLatch.await(2, TimeUnit.SECONDS)
        assertTrue("Timer event should fire repeatedly for script actor", success)

        // Stop timer
        dispatcher.setTimerEvent(scriptId, 0f)

        dispatcher.shutdown()
    }

    @Test
    fun testCleanUnregistrationCancelsActorAndClearsQueues() = runTest {
        val dispatcher = ScriptChannelDispatcher()
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()

        val actor = dispatcher.registerScript(
            scriptId = scriptId,
            objectId = objectId,
            ownerId = UUID.randomUUID()
        )

        assertEquals(1, dispatcher.getActiveActorCount())
        assertFalse(actor.isDestroyed)

        dispatcher.unregisterScript(scriptId)

        assertEquals(0, dispatcher.getActiveActorCount())
        assertTrue(actor.isDestroyed)

        dispatcher.shutdown()
    }
}
