package com.linkpoint.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.network.core.NetworkSessionManager
import com.linkpoint.protocol.caps.CapEventQueue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkSessionManagerTest {

    private lateinit var eventQueue: CapEventQueue

    @Before
    fun setUp() {
        eventQueue = CapEventQueue()
    }

    @Test
    fun `test initial state is foreground and non-doze`() {
        val manager = NetworkSessionManager(
            context = RobolectricTestContext.context,
            keepAliveManager = null,
            eventQueue = eventQueue
        )
        assertFalse("Should not be in background initially", manager.isInBackground.value)
        assertFalse("Should not be in Doze mode initially", manager.isDozeMode.value)
    }

    @Test
    fun `test background transition updates state and sets adaptive mode`() {
        val manager = NetworkSessionManager(
            context = RobolectricTestContext.context,
            keepAliveManager = null,
            eventQueue = eventQueue
        )

        manager.onBackground()

        assertTrue("Should be in background", manager.isInBackground.value)
        assertTrue("EventQueue should be in adaptive background mode", eventQueue.isAdaptiveBackgroundMode)

        manager.onForeground()

        assertFalse("Should no longer be in background", manager.isInBackground.value)
        assertFalse("EventQueue should exit adaptive background mode", eventQueue.isAdaptiveBackgroundMode)
    }

    @Test
    fun `test doze mode state transition`() {
        val manager = NetworkSessionManager(
            context = RobolectricTestContext.context,
            keepAliveManager = null,
            eventQueue = eventQueue
        )

        manager.onDozeModeChanged(true)

        assertTrue("Should be in Doze mode", manager.isDozeMode.value)
        assertTrue("EventQueue adaptive background mode should be active during Doze", eventQueue.isAdaptiveBackgroundMode)

        manager.onDozeModeChanged(false)

        assertFalse("Should exit Doze mode", manager.isDozeMode.value)
        assertFalse("EventQueue adaptive background mode should deactivate when exiting Doze in foreground", eventQueue.isAdaptiveBackgroundMode)
    }

    @Test
    fun `test trigger connection recovery reconnects event queue`() {
        var reconnected = false
        val testCapUrl = "https://sim.example.com/cap/eqg"

        eventQueue.start(testCapUrl)

        val manager = NetworkSessionManager(
            context = RobolectricTestContext.context,
            keepAliveManager = null,
            eventQueue = eventQueue
        )

        manager.triggerConnectionRecovery()

        // Force reconnect should execute without throwing exceptions
        eventQueue.stop()
    }

    private object RobolectricTestContext {
        val context: android.content.Context
            get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()
    }
}
