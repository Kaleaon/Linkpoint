package com.linkpoint.render.lumiya.drawable

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.linkpoint.protocol.messages.PrimShapeParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class DrawablePrimStoreTest {

    private lateinit var store: DrawablePrimStore

    @Before
    fun setUp() {
        store = DrawablePrimStore()
    }

    @Test
    fun testSlotAllocationAndRecycling() {
        assertEquals(0, store.primCount())

        store.addPrim(101L, 10f, 20f, 30f)
        store.addPrim(102L, 5f, 5f, 5f)
        assertEquals(2, store.primCount())

        val initialSlot101 = store.prims[101L]
        val initialSlot102 = store.prims[102L]
        assertNotNull(initialSlot101)
        assertNotNull(initialSlot102)

        store.removePrim(101L)
        assertEquals(1, store.primCount())
        assertFalse(store.prims.containsKey(101L))

        // Reuse recycled slot
        store.addPrim(103L, 1f, 1f, 1f)
        assertEquals(2, store.primCount())
        assertEquals(initialSlot101, store.prims[103L])
    }

    @Test
    fun testUpsertAndShapeClassification() {
        val sphereParams = PrimShapeParams(
            pathCurve = PrimShapeParams.PATH_CIRCLE,
            profileCurve = PrimShapeParams.PROFILE_CIRCLE
        )
        store.upsertPrim(
            id = 201L,
            posX = 1f, posY = 2f, posZ = 3f,
            scaleX = 2f, scaleY = 2f, scaleZ = 2f,
            shapeParams = sphereParams
        )

        val snapshot = store.snapshot()
        assertEquals(1, snapshot.size)
        val prim = snapshot.first()
        assertEquals(201L, prim.id)
        assertEquals(DrawablePrimStore.ShapeKind.SPHERE, prim.shape)
        assertEquals(1f, prim.modelMatrix[12], 0.001f)
        assertEquals(2f, prim.modelMatrix[13], 0.001f)
        assertEquals(3f, prim.modelMatrix[14], 0.001f)
    }

    @Test
    fun testTransparentFlagAndDefaultTexture() {
        store.addPrim(301L, 0f, 0f, 0f)
        store.setPrimTransparent(301L, true)

        val snapshot = store.snapshot()
        val prim = snapshot.first { it.id == 301L }
        assertTrue(prim.isTransparent)

        val testUuid = UUID.randomUUID()
        store.bindTextureToMatchingFaces(301L, testUuid, 42)
        store.setFaceTexture(301L, 0, 42)
        assertEquals(42, store.snapshot().first().faces.first().textureHandle)
    }

    @Test
    fun testInPlaceTransparentSorting() {
        // Add transparent prims at different distances
        store.addPrim(401L, 0f, 0f, 10f) // Dist = 100
        store.addPrim(402L, 0f, 0f, 50f) // Dist = 2500
        store.addPrim(403L, 0f, 0f, 5f)  // Dist = 25

        store.setPrimTransparent(401L, true)
        store.setPrimTransparent(402L, true)
        store.setPrimTransparent(403L, true)

        val slotsField = DrawablePrimStore::class.java.getDeclaredField("transparentSlots").apply { isAccessible = true }
        val depthsField = DrawablePrimStore::class.java.getDeclaredField("transparentDepths").apply { isAccessible = true }

        val sortMethod = DrawablePrimStore::class.java.getDeclaredMethod("quickSortTransparent", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType).apply { isAccessible = true }

        val slots = slotsField.get(store) as IntArray
        val depths = depthsField.get(store) as FloatArray

        val slot401 = store.prims[401L]!!
        val slot402 = store.prims[402L]!!
        val slot403 = store.prims[403L]!!

        slots[0] = slot401; depths[0] = 100f
        slots[1] = slot402; depths[1] = 2500f
        slots[2] = slot403; depths[2] = 25f

        sortMethod.invoke(store, 0, 2)

        // Descending order sort check (farthest object 2500f first)
        assertEquals(2500f, depths[0], 0.001f)
        assertEquals(100f, depths[1], 0.001f)
        assertEquals(25f, depths[2], 0.001f)

        assertEquals(slot402, slots[0])
        assertEquals(slot401, slots[1])
        assertEquals(slot403, slots[2])
    }

    @Test
    fun testThreadSafetyConcurrentMutations() {
        val threadCount = 8
        val opsPerThread = 200
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        for (t in 0 until threadCount) {
            val threadId = t.toLong()
            executor.submit {
                try {
                    for (i in 0 until opsPerThread) {
                        val id = threadId * 1000L + i
                        store.addPrim(id, i.toFloat(), i.toFloat(), i.toFloat())
                        store.setPrimTransparent(id, i % 2 == 0)
                        if (i % 3 == 0) {
                            store.removePrim(id)
                        }
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS))
        executor.shutdown()

        // Verify state consistency
        val snapshot = store.snapshot()
        assertEquals(store.primCount(), snapshot.size)
    }
}
