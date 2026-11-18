package com.linkpoint.render.lumiya.drawable

import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.shaders.AvatarShaderProgram
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DrawableAvatarStoreTest {

    private lateinit var store: DrawableAvatarStore

    @Before
    fun setUp() {
        store = DrawableAvatarStore()
    }

    @Test
    fun testAvatarInstancePersistentBufferAllocation() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        store.addAvatar(id1, 10f, 10f, 10f)
        store.addAvatar(id2, 20f, 20f, 20f)

        val avatar1 = store.getAvatar(id1)
        val avatar2 = store.getAvatar(id2)

        assertNotNull(avatar1)
        assertNotNull(avatar2)

        assertNotNull(avatar1?.jointBuffer)
        assertTrue("jointBuffer must be direct", avatar1!!.jointBuffer.isDirect)
        assertEquals(AvatarShaderProgram.MAX_JOINTS * 16, avatar1.jointBuffer.capacity())

        assertNotNull(avatar2?.jointBuffer)
        assertTrue("jointBuffer must be direct", avatar2!!.jointBuffer.isDirect)
        assertEquals(AvatarShaderProgram.MAX_JOINTS * 16, avatar2.jointBuffer.capacity())

        assertFalse("Avatar instances must allocate independent buffers", avatar1.jointBuffer === avatar2.jointBuffer)
    }

    @Test
    fun testDistanceCulling() {
        val ctx = LumiyaRenderContext()
        ctx.cameraPositionX = 128.0f
        ctx.cameraPositionY = 128.0f
        ctx.cameraPositionZ = 30.0f
        ctx.cameraTargetX = 128.0f
        ctx.cameraTargetY = 140.0f
        ctx.cameraTargetZ = 30.0f
        ctx.drawDistance = 50.0f

        // Install permissive frustum planes to isolate distance threshold testing
        ctx.frustumCuller.setPlaneForTest(0, 1.0f, 0.0f, 0.0f, 1000.0f)
        ctx.frustumCuller.setPlaneForTest(1, -1.0f, 0.0f, 0.0f, 1000.0f)
        ctx.frustumCuller.setPlaneForTest(2, 0.0f, 0.0f, 1.0f, 1000.0f)
        ctx.frustumCuller.setPlaneForTest(3, 0.0f, 0.0f, -1.0f, 1000.0f)
        ctx.frustumCuller.setPlaneForTest(4, 0.0f, 1.0f, 0.0f, 1000.0f)
        ctx.frustumCuller.setPlaneForTest(5, 0.0f, -1.0f, 0.0f, 1000.0f)

        val nearId = UUID.randomUUID()
        val farId = UUID.randomUUID()

        store.addAvatar(nearId, 128.0f, 135.0f, 30.0f) // Distance = 7m (<= 50m)
        store.addAvatar(farId, 128.0f, 200.0f, 30.0f)  // Distance = 72m (> 50m)

        val nearAvatar = store.getAvatar(nearId)!!
        val farAvatar = store.getAvatar(farId)!!

        assertTrue("Near avatar should be visible within draw distance", store.isAvatarVisible(nearAvatar, ctx))
        assertFalse("Far avatar should be culled beyond draw distance", store.isAvatarVisible(farAvatar, ctx))
    }

    @Test
    fun testFrustumCulling() {
        val ctx = LumiyaRenderContext()
        ctx.cameraPositionX = 0.0f
        ctx.cameraPositionY = 0.0f
        ctx.cameraPositionZ = 0.0f
        ctx.cameraTargetX = 0.0f
        ctx.cameraTargetY = 10.0f
        ctx.cameraTargetZ = 0.0f
        ctx.drawDistance = 100.0f

        // Install frustum planes for forward (+Y) view volume
        ctx.frustumCuller.setPlaneForTest(0, 1.0f, 0.0f, 0.0f, 50.0f)   // Left
        ctx.frustumCuller.setPlaneForTest(1, -1.0f, 0.0f, 0.0f, 50.0f)  // Right
        ctx.frustumCuller.setPlaneForTest(2, 0.0f, 0.0f, 1.0f, 50.0f)   // Bottom
        ctx.frustumCuller.setPlaneForTest(3, 0.0f, 0.0f, -1.0f, 50.0f)  // Top
        ctx.frustumCuller.setPlaneForTest(4, 0.0f, 1.0f, 0.0f, 0.0f)    // Near plane (Y >= 0)
        ctx.frustumCuller.setPlaneForTest(5, 0.0f, -1.0f, 0.0f, 100.0f) // Far plane (Y <= 100)

        val inFrustumId = UUID.randomUUID()
        val behindFrustumId = UUID.randomUUID()

        store.addAvatar(inFrustumId, 0.0f, 5.0f, 0.0f)     // Directly ahead in FOV (Y = 5)
        store.addAvatar(behindFrustumId, 0.0f, -10.0f, 0.0f) // Behind camera plane (Y = -10)

        val inFrustumAvatar = store.getAvatar(inFrustumId)!!
        val behindFrustumAvatar = store.getAvatar(behindFrustumId)!!

        assertTrue("Avatar in front of camera should be visible", store.isAvatarVisible(inFrustumAvatar, ctx))
        assertFalse("Avatar behind camera should be culled by frustum", store.isAvatarVisible(behindFrustumAvatar, ctx))
    }

    @Test
    fun testJointUpdateReusesPersistentBuffer() {
        val avatarId = UUID.randomUUID()
        store.addAvatar(avatarId, 0f, 0f, 0f)

        val avatar = store.getAvatar(avatarId)!!
        val initialBufferRef = avatar.jointBuffer

        val testMatrices = FloatArray(AvatarShaderProgram.MAX_JOINTS * 16) { it.toFloat() }
        store.updateJoints(avatarId, testMatrices, 26)

        assertEquals(26, avatar.jointCount)
        assertSame("Persistent jointBuffer reference must be reused on updateJoints", initialBufferRef, avatar.jointBuffer)

        // Verify copied joint matrices
        assertEquals(0.0f, avatar.jointMatrices[0], 0.001f)
        assertEquals(15.0f, avatar.jointMatrices[15], 0.001f)

        // Perform second update
        val secondMatrices = FloatArray(AvatarShaderProgram.MAX_JOINTS * 16) { (it + 100).toFloat() }
        store.updateJoints(avatarId, secondMatrices, 32)

        assertEquals(32, avatar.jointCount)
        assertSame("Persistent jointBuffer reference must remain identical after multiple updates", initialBufferRef, avatar.jointBuffer)
        assertEquals(100.0f, avatar.jointMatrices[0], 0.001f)
    }
}
