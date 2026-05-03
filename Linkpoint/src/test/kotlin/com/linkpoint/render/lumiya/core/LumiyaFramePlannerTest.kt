package com.linkpoint.render.lumiya.core

import com.linkpoint.render.lumiya.drawable.DrawableHudStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LumiyaFramePlannerTest {

    @Test
    fun `emissive pass runs after transparent and before water in normal frames`() {
        val plan = LumiyaFramePlanner.createPlan(
            worldPassEnabled = true,
            hasHudElements = false,
            interactiveThrottle = false
        )
        val emissiveIdx = plan.indexOf(LumiyaRenderPass.WORLD_EMISSIVE)
        val transparentIdx = plan.indexOf(LumiyaRenderPass.WORLD_TRANSPARENT)
        val waterIdx = plan.indexOf(LumiyaRenderPass.WORLD_WATER)
        assertTrue("emissive present", emissiveIdx >= 0)
        assertTrue("emissive after transparent", emissiveIdx > transparentIdx)
        assertTrue("emissive before water", emissiveIdx < waterIdx)
    }

    @Test
    fun `interactive throttle drops the emissive pass`() {
        val plan = LumiyaFramePlanner.createPlan(
            worldPassEnabled = true,
            hasHudElements = false,
            interactiveThrottle = true
        )
        assertFalse(plan.contains(LumiyaRenderPass.WORLD_EMISSIVE))
    }

    @Test
    fun `includes HUD pass when world pass is disabled`() {
        val plan = LumiyaFramePlanner.createPlan(
            worldPassEnabled = false,
            hasHudElements = true
        )
        assertEquals(listOf(LumiyaRenderPass.HUD), plan)
    }

    @Test
    fun `orders overlapping HUDs deterministically by layer then attachment then id`() {
        val store = DrawableHudStore()
        store.addHudPrim(id = 7L, attachmentPoint = 38, posX = 20f, posY = 20f, posZ = 0f, layer = 2)
        store.addHudPrim(id = 4L, attachmentPoint = 31, posX = 20f, posY = 20f, posZ = 0f, layer = 1)
        store.addHudPrim(id = 3L, attachmentPoint = 31, posX = 20f, posY = 20f, posZ = 0f, layer = 1)

        assertEquals(listOf(3L, 4L, 7L), store.debugSortedIds())
        assertTrue(store.hasElements())
    }

    @Test
    fun `hud layout follows viewport changes for resize and orientation`() {
        val store = DrawableHudStore()
        store.setViewportSize(width = 1000, height = 500)
        store.addHudPrim(id = 9L, attachmentPoint = 32, posX = 0f, posY = 0f, posZ = 0f, layer = 0)

        val landscapePosition = store.debugScreenPosition(9L)
        assertEquals(950f, landscapePosition?.first ?: -1f, 0.01f)
        assertEquals(25f, landscapePosition?.second ?: -1f, 0.01f)

        store.setViewportSize(width = 500, height = 1000)
        val portraitPosition = store.debugScreenPosition(9L)
        assertEquals(475f, portraitPosition?.first ?: -1f, 0.01f)
        assertEquals(50f, portraitPosition?.second ?: -1f, 0.01f)
    }

    @Test
    fun `hud z-order is deterministic for overlapping elements`() {
        val store = DrawableHudStore()
        store.setViewportSize(width = 800, height = 600)
        store.addHudPrim(id = 3L, attachmentPoint = 31, posX = 0f, posY = 0f, posZ = 0f, layer = 1)
        store.addHudPrim(id = 4L, attachmentPoint = 31, posX = 0f, posY = 0f, posZ = 0f, layer = 1)

        val firstZ = store.debugModelZ(3L) ?: 0f
        val secondZ = store.debugModelZ(4L) ?: 0f
        assertTrue("Second HUD should render above first HUD", secondZ > firstZ)
    }

    @Test
    fun `attachment point append flag still routes to HUD pipeline`() {
        val renderer = LumiyaRenderer()
        renderer.addAttachmentObject(
            id = 123L,
            attachmentPoint = 32 or 0x80,
            posX = 0f,
            posY = 0f,
            posZ = 0f,
            layer = 0
        )

        val hudStoreField = LumiyaRenderer::class.java.getDeclaredField("hudStore").apply { isAccessible = true }
        val hudStore = hudStoreField.get(renderer) as DrawableHudStore
        assertEquals(listOf(123L), hudStore.debugSortedIds())

        val primStoreField = LumiyaRenderer::class.java.getDeclaredField("primStore").apply { isAccessible = true }
        val primStore = primStoreField.get(renderer)
        val primsField = primStore.javaClass.getDeclaredField("prims").apply { isAccessible = true }
        val prims = primsField.get(primStore) as Map<*, *>
        assertFalse(prims.containsKey(123L))
    }
}
