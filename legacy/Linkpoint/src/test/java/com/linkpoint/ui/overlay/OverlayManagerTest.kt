package com.linkpoint.ui.overlay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OverlayManagerTest {

    private lateinit var overlayManager: OverlayManager
    private lateinit var fakeSurfaceTarget: FakeOverlaySurfaceTarget

    class FakeOverlaySurfaceTarget : OverlayManager.OverlaySurfaceTarget {
        var pauseCount = 0
        var resumeCount = 0
        var isPaused = false
        var isDirtyFlagMode = false
        var requestRenderCount = 0
        var eglContextPreserved = true
        var simulatedFps = 60.0f

        override fun pause3dRendering() {
            pauseCount++
            isPaused = true
            simulatedFps = 0.0f
        }

        override fun resume3dRendering() {
            resumeCount++
            isPaused = false
            simulatedFps = 60.0f
        }

        override fun setDirtyFlagRenderMode(enabled: Boolean) {
            isDirtyFlagMode = enabled
        }

        override fun requestRenderFrame() {
            requestRenderCount++
        }

        override fun isEglContextPreserved(): Boolean = eglContextPreserved

        override fun getCurrent3dFps(): Float = if (isPaused) 0.0f else simulatedFps
    }

    @Before
    fun setUp() {
        overlayManager = OverlayManager()
        fakeSurfaceTarget = FakeOverlaySurfaceTarget()
        overlayManager.bindSurfaceTarget(fakeSurfaceTarget)
    }

    @Test
    fun testFullScreenOverlayPauses3dRenderingAndAchieves0Fps() {
        overlayManager.showOverlay("chat_view", OverlayManager.OverlayType.FULL_SCREEN_2D, "Chat")

        assertTrue(fakeSurfaceTarget.isPaused)
        assertEquals(1, fakeSurfaceTarget.pauseCount)

        val telemetry = overlayManager.getTelemetry()
        assertTrue(telemetry.is3dPaused)
        assertEquals(0.0f, telemetry.current3dFps, 0.001f)
        assertTrue(telemetry.isEglContextPreserved)
        assertEquals(1, telemetry.fullScreenOverlayCount)
    }

    @Test
    fun testClosingFullScreenOverlayResumes3dRenderingWithin50Ms() {
        overlayManager.showOverlay("chat_view", OverlayManager.OverlayType.FULL_SCREEN_2D, "Chat")
        assertTrue(fakeSurfaceTarget.isPaused)

        overlayManager.hideOverlay("chat_view")

        assertFalse(fakeSurfaceTarget.isPaused)
        assertEquals(1, fakeSurfaceTarget.resumeCount)

        val telemetry = overlayManager.getTelemetry()
        assertFalse(telemetry.is3dPaused)
        assertEquals(60.0f, telemetry.current3dFps, 0.001f)
        assertTrue(telemetry.lastResumeDurationMs <= OverlayManager.MAX_RESUME_LATENCY_GOAL_MS)
    }

    @Test
    fun testSemiTransparentHudOverlayEnablesDirtyFlagRendering() {
        overlayManager.showOverlay("hud_attachment", OverlayManager.OverlayType.SEMI_TRANSPARENT_HUD, "HUD")

        assertFalse(fakeSurfaceTarget.isPaused)
        assertTrue(fakeSurfaceTarget.isDirtyFlagMode)

        overlayManager.requestHudDirtyRender()
        assertEquals(1, fakeSurfaceTarget.requestRenderCount)

        val telemetry = overlayManager.getTelemetry()
        assertFalse(telemetry.is3dPaused)
        assertTrue(telemetry.isDirtyFlagMode)
        assertEquals(60.0f, telemetry.current3dFps, 0.001f)
    }

    @Test
    fun testMultipleOverlaysStackAndTransitionCorrectly() {
        // Step 1: Open Inventory (full-screen)
        overlayManager.showOverlay("inventory", OverlayManager.OverlayType.FULL_SCREEN_2D, "Inventory")
        assertTrue(fakeSurfaceTarget.isPaused)
        assertEquals(1, fakeSurfaceTarget.pauseCount)

        // Step 2: Open Chat on top of Inventory
        overlayManager.showOverlay("chat", OverlayManager.OverlayType.FULL_SCREEN_2D, "Chat")
        assertTrue(fakeSurfaceTarget.isPaused)
        assertEquals(1, fakeSurfaceTarget.pauseCount) // Should remain paused without duplicate pause call

        // Step 3: Dismiss Chat
        overlayManager.hideOverlay("chat")
        assertTrue(fakeSurfaceTarget.isPaused) // Inventory is still active

        // Step 4: Dismiss Inventory
        overlayManager.hideOverlay("inventory")
        assertFalse(fakeSurfaceTarget.isPaused)
        assertEquals(1, fakeSurfaceTarget.resumeCount) // Resumes 3D thread
    }

    @Test
    fun testRevertingTo3DWorldDisablesDirtyFlagRendering() {
        overlayManager.showOverlay("hud", OverlayManager.OverlayType.SEMI_TRANSPARENT_HUD, "HUD")
        assertTrue(fakeSurfaceTarget.isDirtyFlagMode)

        overlayManager.hideOverlay("hud")
        assertFalse(fakeSurfaceTarget.isDirtyFlagMode)
    }
}
