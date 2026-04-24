package com.linkpoint.render.lumiya.core

import com.linkpoint.render.lumiya.drawable.DrawableHudStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LumiyaFramePlannerTest {

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
}
