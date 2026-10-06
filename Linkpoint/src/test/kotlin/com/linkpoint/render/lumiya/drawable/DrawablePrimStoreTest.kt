package com.linkpoint.render.lumiya.drawable

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

class DrawablePrimStoreTest {

    private lateinit var store: DrawablePrimStore

    @Before
    fun setUp() {
        store = DrawablePrimStore()
    }

    @Test
    fun testAddAndRemovePrim() {
        assertEquals(0, store.primCount())

        store.addPrim(101L, 10f, 20f, 30f)
        store.addPrim(102L, 5f, 5f, 5f)
        assertEquals(2, store.primCount())

        val snapshot1 = store.snapshot()
        assertTrue(snapshot1.any { it.id == 101L })
        assertTrue(snapshot1.any { it.id == 102L })

        store.removePrim(101L)
        assertEquals(1, store.primCount())
        val snapshot2 = store.snapshot()
        assertFalse(snapshot2.any { it.id == 101L })

        store.addPrim(103L, 1f, 1f, 1f)
        assertEquals(2, store.primCount())
        assertTrue(store.snapshot().any { it.id == 103L })
    }

    @Test
    fun testUpsertAndShapeClassification() {
        val sphereParams = PrimShapeParams(
            pathCurve = PrimShapeParams.PATH_CIRCLE,
            profileCurve = PrimShapeParams.PROFILE_HALF_CIRCLE
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
    fun testTransparentPrimFiltering() {
        store.addPrim(401L, 0f, 0f, 10f)
        store.addPrim(402L, 0f, 0f, 50f)
        store.addPrim(403L, 0f, 0f, 5f)

        store.setPrimTransparent(401L, true)
        store.setPrimTransparent(402L, true)

        val transparentPrims = store.snapshot().filter { it.isTransparent }
        assertEquals(2, transparentPrims.size)
        assertTrue(transparentPrims.any { it.id == 401L })
        assertTrue(transparentPrims.any { it.id == 402L })
        assertFalse(transparentPrims.any { it.id == 403L })
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

        val snapshot = store.snapshot()
        assertEquals(store.primCount(), snapshot.size)
    }
}
