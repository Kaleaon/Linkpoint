package com.linkpoint.render.lumiya.drawable

import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class DrawableMeshStoreTest {

    private lateinit var store: DrawableMeshStore
    private lateinit var ctx: LumiyaRenderContext

    @Before
    fun setUp() {
        store = DrawableMeshStore()
        ctx = LumiyaRenderContext()
    }

    @Test
    fun testTransparentMeshDepthOrdering() {
        val meshId = UUID.randomUUID()

        // Create instances at different distances along Z axis:
        // Instance 1: pos (0, 0, 10)  -> distSq = 100
        // Instance 2: pos (0, 0, 50)  -> distSq = 2500
        // Instance 3: pos (0, 0, 5)   -> distSq = 25
        val inst1 = DrawableMeshStore.MeshInstance(id = 1L, meshId = meshId, isTransparent = true).apply {
            modelMatrix[12] = 0f; modelMatrix[13] = 0f; modelMatrix[14] = 10f
        }
        val inst2 = DrawableMeshStore.MeshInstance(id = 2L, meshId = meshId, isTransparent = true).apply {
            modelMatrix[12] = 0f; modelMatrix[13] = 0f; modelMatrix[14] = 50f
        }
        val inst3 = DrawableMeshStore.MeshInstance(id = 3L, meshId = meshId, isTransparent = true).apply {
            modelMatrix[12] = 0f; modelMatrix[13] = 0f; modelMatrix[14] = 5f
        }

        val instancesField = DrawableMeshStore::class.java.getDeclaredField("instances").apply { isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        val instances = instancesField.get(store) as MutableMap<Long, DrawableMeshStore.MeshInstance>
        instances[1L] = inst1
        instances[2L] = inst2
        instances[3L] = inst3

        ctx.cameraPositionX = 0f
        ctx.cameraPositionY = 0f
        ctx.cameraPositionZ = 0f

        store.drawTransparent(ctx)

        // Verify sorted descending order: inst2 (distSq=2500), inst1 (distSq=100), inst3 (distSq=25)
        val transparentList = store.transparentInstancesList
        assertEquals(3, transparentList.size)
        assertEquals(2L, transparentList[0].id)
        assertEquals(1L, transparentList[1].id)
        assertEquals(3L, transparentList[2].id)
    }

    @Test
    fun testReusableTransparentListZeroAllocationAcrossFrames() {
        val meshId = UUID.randomUUID()
        val inst1 = DrawableMeshStore.MeshInstance(id = 101L, meshId = meshId, isTransparent = true)
        val inst2 = DrawableMeshStore.MeshInstance(id = 102L, meshId = meshId, isTransparent = true)

        val instancesField = DrawableMeshStore::class.java.getDeclaredField("instances").apply { isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        val instances = instancesField.get(store) as MutableMap<Long, DrawableMeshStore.MeshInstance>
        instances[101L] = inst1
        instances[102L] = inst2

        val initialListRef = store.transparentInstancesList

        // Frame 1
        store.drawTransparent(ctx)
        assertSame("transparentInstancesList reference must be preserved across frame 1", initialListRef, store.transparentInstancesList)
        assertEquals(2, store.transparentInstancesList.size)

        // Frame 2
        store.drawTransparent(ctx)
        assertSame("transparentInstancesList reference must be preserved across frame 2", initialListRef, store.transparentInstancesList)
        assertEquals(2, store.transparentInstancesList.size)
    }

    @Test
    fun testEqualDistanceAndEmptyCases() {
        // Empty store
        store.drawTransparent(ctx)
        assertTrue(store.transparentInstancesList.isEmpty())

        // Equal distance objects
        val meshId = UUID.randomUUID()
        val instA = DrawableMeshStore.MeshInstance(id = 201L, meshId = meshId, isTransparent = true).apply {
            modelMatrix[12] = 10f; modelMatrix[13] = 0f; modelMatrix[14] = 0f
        }
        val instB = DrawableMeshStore.MeshInstance(id = 202L, meshId = meshId, isTransparent = true).apply {
            modelMatrix[12] = -10f; modelMatrix[13] = 0f; modelMatrix[14] = 0f
        }

        val instancesField = DrawableMeshStore::class.java.getDeclaredField("instances").apply { isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        val instances = instancesField.get(store) as MutableMap<Long, DrawableMeshStore.MeshInstance>
        instances[201L] = instA
        instances[202L] = instB

        store.drawTransparent(ctx)
        assertEquals(2, store.transparentInstancesList.size)
    }
}
