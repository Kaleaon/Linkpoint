package com.linkpoint.render.lumiya.picking

import android.opengl.Matrix
import com.linkpoint.render.lumiya.drawable.DrawableHudStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DualPassHudRaycastingTest {

    @Test
    fun testOrthoScreenToWorldRay_ComputesRayOriginAndDirection() {
        val viewportWidth = 1000
        val viewportHeight = 1000
        val viewMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
        val projMatrix = FloatArray(16)
        Matrix.orthoM(projMatrix, 0, 0f, viewportWidth.toFloat(), viewportHeight.toFloat(), 0f, -1f, 1f)

        // Center tap (500, 500)
        val (rayOrigin, rayDir) = GLRayTrace.orthoScreenToWorldRay(
            500f, 500f,
            viewportWidth, viewportHeight,
            viewMatrix, projMatrix
        )

        assertNotNull(rayOrigin)
        assertNotNull(rayDir)
        assertEquals(500f, rayOrigin[0], 0.1f)
        assertEquals(500f, rayOrigin[1], 0.1f)
        assertEquals(1.0f, rayOrigin[2], 0.1f)

        // Ray direction points along -Z from near (+1) to far (-1) plane
        assertEquals(0f, rayDir[0], 0.01f)
        assertEquals(0f, rayDir[1], 0.01f)
        assertEquals(-1.0f, rayDir[2], 0.01f)
    }

    @Test
    fun testOrthoScreenToWorldRay_HandlesSingularAndDegenerateCases() {
        val zeroViewMatrix = FloatArray(16)
        val zeroProjMatrix = FloatArray(16)

        val (rayOrigin, rayDir) = GLRayTrace.orthoScreenToWorldRay(
            100f, 100f,
            0, 0,
            zeroViewMatrix, zeroProjMatrix
        )

        assertNotNull(rayOrigin)
        assertNotNull(rayDir)
        assertEquals(3, rayOrigin.size)
        assertEquals(3, rayDir.size)
    }

    @Test
    fun testDrawableHudStore_PickHudPrim_HitAndMiss() {
        val hudStore = DrawableHudStore()
        val viewportWidth = 1000
        val viewportHeight = 1000
        hudStore.setViewportSize(viewportWidth, viewportHeight)

        // Add a HUD prim at center (attachment point 35 -> anchor 500, 500) with size 100x100
        val hudId = 99001L
        hudStore.addHudPrim(
            id = hudId,
            attachmentPoint = 35,
            posX = 0f,
            posY = 0f,
            posZ = 0f,
            layer = 0,
            width = 100f,
            height = 100f,
            depth = 1f
        )

        val viewMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
        val projMatrix = FloatArray(16)
        Matrix.orthoM(projMatrix, 0, 0f, viewportWidth.toFloat(), viewportHeight.toFloat(), 0f, -1f, 1f)

        // 1. Ray at center (500, 500) -> Should HIT HUD prim
        val (hitOrigin, hitDir) = GLRayTrace.orthoScreenToWorldRay(
            500f, 500f,
            viewportWidth, viewportHeight,
            viewMatrix, projMatrix
        )
        val hitId = hudStore.pickHudPrim(hitOrigin, hitDir)
        assertEquals(hudId, hitId)

        // 2. Ray at top-left corner (10, 10) -> Should MISS HUD prim (outside 100x100 bounds at 500,500)
        val (missOrigin, missDir) = GLRayTrace.orthoScreenToWorldRay(
            10f, 10f,
            viewportWidth, viewportHeight,
            viewMatrix, projMatrix
        )
        val missId = hudStore.pickHudPrim(missOrigin, missDir)
        assertNull(missId)
    }
}
