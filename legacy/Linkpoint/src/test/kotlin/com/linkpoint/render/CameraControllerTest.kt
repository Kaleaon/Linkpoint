package com.linkpoint.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CameraControllerTest {

    private lateinit var controller: CameraController

    @Before
    fun setUp() {
        controller = CameraController()
    }

    @Test
    fun `initial camera controller state is follow mode without pan`() {
        assertEquals(CameraController.Mode.FOLLOW, controller.mode)
        assertFalse(controller.panMode)
        assertEquals(0f, controller.panX, 0.001f)
        assertEquals(0f, controller.panY, 0.001f)
        assertEquals(5f, controller.followDistance, 0.001f)
    }

    @Test
    fun `applyOrbit updates yaw and pitch within clamped bounds`() {
        controller.applyOrbit(10f, 60f)
        assertEquals(-2.5f, controller.yawDeg, 0.001f)
        assertEquals(0f, controller.pitchDeg, 0.001f) // -15 + 60*0.25 = 0

        // Test pitch clamping
        controller.applyOrbit(0f, 1000f)
        assertEquals(85f, controller.pitchDeg, 0.001f)
    }

    @Test
    fun `applyZoom adjusts follow distance within min and max limits`() {
        val initialDist = controller.followDistance
        controller.applyZoom(2.0f) // Zoom in
        assertEquals(initialDist / 2.0f, controller.followDistance, 0.001f)

        controller.applyZoom(0.01f) // Zoom way out -> clamped to maxFollowDistance
        assertEquals(controller.maxFollowDistance, controller.followDistance, 0.001f)
    }

    @Test
    fun `panMode toggle and applyPan update camera offsets and view calculation`() {
        assertFalse(controller.panMode)
        assertTrue(controller.togglePanMode())
        assertTrue(controller.panMode)

        val viewBefore = FloatArray(6)
        controller.computeView(viewBefore)

        controller.applyPan(10f, -20f)
        // dx=10 -> -10 * 0.015 = -0.15, dy=-20 -> -20 * 0.015 = -0.30
        assertEquals(-0.15f, controller.panX, 0.001f)
        assertEquals(-0.30f, controller.panY, 0.001f)

        controller.applyPan(-10f, 20f) // Reset pan
        assertEquals(0f, controller.panX, 0.001f)
        assertEquals(0f, controller.panY, 0.001f)

        val viewAfter = FloatArray(6)
        controller.computeView(viewAfter)

        // View before and after applying and resetting pan offset should match
        for (i in 0 until 6) {
            assertEquals(viewAfter[i], viewBefore[i], 0.01f)
        }
    }

    @Test
    fun `reset restores default orbit angles panMode and offsets`() {
        controller.togglePanMode()
        controller.applyOrbit(20f, 30f)
        controller.applyPan(50f, 50f)

        controller.reset()

        assertFalse(controller.panMode)
        assertEquals(0f, controller.yawDeg, 0.001f)
        assertEquals(-15f, controller.pitchDeg, 0.001f)
        assertEquals(0f, controller.panX, 0.001f)
        assertEquals(0f, controller.panY, 0.001f)
    }
}
