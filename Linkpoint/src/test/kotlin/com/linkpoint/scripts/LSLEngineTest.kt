package com.linkpoint.scripts

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LSLEngineTest {

    @Test
    fun testLSLEngineRegisterAndUnregisterScript() = runTest {
        val engine = LSLEngine()
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val script = engine.registerScript(scriptId, objectId, ownerId)

        assertNotNull(script)
        assertEquals(scriptId, script.scriptId)
        assertNotNull(script.actor)
        assertFalse(script.actor!!.isDestroyed)

        engine.unregisterScript(scriptId)

        assertTrue(script.actor!!.isDestroyed)
        engine.shutdown()
    }

    @Test
    fun testLSLEngineHandleTouchAndListenRouting() = runTest {
        val engine = LSLEngine()
        val scriptId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val script = engine.registerScript(scriptId, objectId, ownerId)

        val handle = engine.llListen(scriptId, 0, "", null, "hello")
        assertTrue(handle > 0)

        engine.handleTouch(objectId, UUID.randomUUID(), Triple(128f, 128f, 30f))
        engine.handleListen(0, "Avatar", UUID.randomUUID(), "hello world")

        // Wait brief moment for coroutine actor loop to process
        val latch = CountDownLatch(1)
        latch.await(200, TimeUnit.MILLISECONDS)

        val events = script.eventQueue.map { it.name }
        assertTrue("Event queue should contain state_entry", events.contains(LSLEngine.EVENT_STATE_ENTRY))
        assertTrue("Event queue should contain touch_start", events.contains(LSLEngine.EVENT_TOUCH_START))
        assertTrue("Event queue should contain listen", events.contains(LSLEngine.EVENT_LISTEN))

        engine.shutdown()
    }
}
