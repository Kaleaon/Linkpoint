package com.linkpoint.render

import android.opengl.GLSurfaceView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RenderStateManagerTest {

    private lateinit var manager: RenderStateManager
    private lateinit var fakeGlSurfaceView: FakeGLSurfaceView

    class FakeGLSurfaceView : GLSurfaceView(RuntimeEnvironment.getApplication()) {
        var requestRenderCount = 0
        var lastRenderMode = -1

        override fun requestRender() {
            requestRenderCount++
        }

        override fun setRenderMode(renderMode: Int) {
            lastRenderMode = renderMode
        }

        override fun getRenderMode(): Int {
            return lastRenderMode
        }
    }

    @Before
    fun setUp() {
        manager = RenderStateManager()
        fakeGlSurfaceView = FakeGLSurfaceView()
        manager.attachGlSurfaceView(fakeGlSurfaceView)
    }

    @Test
    fun `initial state is active navigation 60 FPS`() {
        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, manager.currentMode)
        assertEquals(60, manager.currentMode.targetFps)
        assertEquals(16L, manager.currentMode.targetFrameIntervalMs)
        assertFalse(manager.isOverlayActive)
    }

    @Test
    fun `camera or avatar motion maintains or restores 60 FPS mode`() {
        val now = System.currentTimeMillis()
        manager.notifyCameraOrAvatarMotion(now)

        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, manager.currentMode)
        assertEquals(now, manager.lastActivityTimeMs)
    }

    @Test
    fun `stationary camera triggers 15 FPS throttling after 2 seconds of inactivity`() {
        val startMs = 1000L
        manager.notifyCameraOrAvatarMotion(startMs)

        // Before 2 seconds (1.9s) -> remains 60 FPS
        val modeBefore = manager.evaluateInactivity(startMs + 1900L)
        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, modeBefore)

        // After 2 seconds (2.0s) -> transitions to 15 FPS
        val modeAfter = manager.evaluateInactivity(startMs + 2000L)
        assertEquals(RenderStateManager.RenderMode.STATIONARY_IDLE_15FPS, modeAfter)
        assertEquals(15, manager.currentMode.targetFps)
        assertEquals(66L, manager.currentMode.targetFrameIntervalMs)
    }

    @Test
    fun `camera motion triggers instant transition from 15 FPS back to 60 FPS within 1 frame cycle`() {
        val startMs = 1000L
        manager.notifyCameraOrAvatarMotion(startMs)

        // Trigger 15 FPS mode
        manager.evaluateInactivity(startMs + 2500L)
        assertEquals(RenderStateManager.RenderMode.STATIONARY_IDLE_15FPS, manager.currentMode)

        // Camera motion arrives at startMs + 2501ms (<16ms frame cycle)
        val motionTimeMs = startMs + 2501L
        manager.notifyCameraOrAvatarMotion(motionTimeMs)

        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, manager.currentMode)
        assertEquals(60, manager.currentMode.targetFps)
        assertEquals(motionTimeMs, manager.lastActivityTimeMs)
    }

    @Test
    fun `fullscreen 2D chat overlay configures dirty-flag rendering mode`() {
        var listenerModeChanged = false
        var lastNewMode: RenderStateManager.RenderMode? = null

        manager.addListener(object : RenderStateManager.RenderStateListener {
            override fun onRenderModeChanged(
                previousMode: RenderStateManager.RenderMode,
                newMode: RenderStateManager.RenderMode
            ) {
                listenerModeChanged = true
                lastNewMode = newMode
            }

            override fun onSurfaceInvalidated(reason: String) {}
        })

        // Activate full-screen chat overlay
        manager.setFullScreenOverlayActive(true)

        assertTrue(manager.isOverlayActive)
        assertEquals(RenderStateManager.RenderMode.DIRTY_FLAG_OVERLAY, manager.currentMode)
        assertTrue(listenerModeChanged)
        assertEquals(RenderStateManager.RenderMode.DIRTY_FLAG_OVERLAY, lastNewMode)

        // Verify GLSurfaceView mode changed to RENDERMODE_WHEN_DIRTY
        assertEquals(GLSurfaceView.RENDERMODE_WHEN_DIRTY, fakeGlSurfaceView.lastRenderMode)

        // GPU power estimate in telemetry is under 0.3W (<0.3W)
        assertTrue(manager.currentMode.estimatedGpuPowerW < 0.3f)
    }

    @Test
    fun `dismissing overlay restores continuous 60 FPS rendering`() {
        manager.setFullScreenOverlayActive(true)
        assertEquals(RenderStateManager.RenderMode.DIRTY_FLAG_OVERLAY, manager.currentMode)

        manager.setFullScreenOverlayActive(false)
        assertFalse(manager.isOverlayActive)
        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, manager.currentMode)

        // RENDERMODE_CONTINUOUSLY was set when restoring mode
        assertEquals(GLSurfaceView.RENDERMODE_CONTINUOUSLY, fakeGlSurfaceView.lastRenderMode)
    }

    @Test
    fun `world updates trigger surface invalidation requestRender calls`() {
        var invalidatedReason: String? = null

        manager.addListener(object : RenderStateManager.RenderStateListener {
            override fun onRenderModeChanged(
                previousMode: RenderStateManager.RenderMode,
                newMode: RenderStateManager.RenderMode
            ) {}

            override fun onSurfaceInvalidated(reason: String) {
                invalidatedReason = reason
            }
        })

        manager.setFullScreenOverlayActive(true)
        val initialRenders = fakeGlSurfaceView.requestRenderCount

        // World update or chat message arrives
        manager.invalidateSurface("chat_message_received")

        assertEquals(initialRenders + 1, fakeGlSurfaceView.requestRenderCount)
        assertEquals("chat_message_received", invalidatedReason)
    }

    @Test
    fun `camera controller integration triggers motion on orbit`() {
        val controller = CameraController()
        controller.renderStateManager = manager

        // Force idle 15 FPS
        val startMs = System.currentTimeMillis()
        manager.notifyCameraOrAvatarMotion(startMs)
        manager.evaluateInactivity(startMs + 2100L)
        assertEquals(RenderStateManager.RenderMode.STATIONARY_IDLE_15FPS, manager.currentMode)

        // Apply orbit gesture
        controller.applyOrbit(10f, 5f)

        assertEquals(RenderStateManager.RenderMode.ACTIVE_NAVIGATION_60FPS, manager.currentMode)
    }
}
