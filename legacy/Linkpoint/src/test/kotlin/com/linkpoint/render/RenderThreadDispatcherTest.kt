package com.linkpoint.render

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class RenderThreadDispatcherTest {

    private lateinit var dispatcher: RenderThreadDispatcher

    @Before
    fun setUp() {
        dispatcher = RenderThreadDispatcher("TestRenderThread")
    }

    @After
    fun tearDown() {
        dispatcher.shutdown()
    }

    @Test
    fun `execute suspending function runs task on dedicated render thread`() = runBlocking {
        var executedOnRenderThread = false
        val result = dispatcher.execute {
            executedOnRenderThread = dispatcher.isRenderThread()
            "success"
        }

        assertEquals("success", result)
        assertTrue(executedOnRenderThread)
    }

    @Test
    fun `isRenderThread returns correct thread check`() = runBlocking {
        assertFalse(dispatcher.isRenderThread())

        var isRenderThreadInsideExecute = false
        dispatcher.execute {
            isRenderThreadInsideExecute = dispatcher.isRenderThread()
        }

        assertTrue(isRenderThreadInsideExecute)
    }

    @Test
    fun `postAsync schedules runnable on render thread`() {
        val latch = CountDownLatch(1)
        var executedOnThread = false

        dispatcher.postAsync {
            executedOnThread = dispatcher.isRenderThread()
            latch.countDown()
        }

        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertTrue(executedOnThread)
    }
}
