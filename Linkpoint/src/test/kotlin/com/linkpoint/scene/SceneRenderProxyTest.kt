package com.linkpoint.scene

import com.linkpoint.scene.proxy.*
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class SceneRenderProxyTest {

    @Test
    fun testSnapshotPublishAndAtomicSwap() {
        val manager = SceneRenderProxyManager()

        val initialSnapshot = manager.getCurrentSnapshot()
        assertEquals(0L, initialSnapshot.snapshotId)
        assertTrue(initialSnapshot.meshProxies.isEmpty())

        val testUuid = UUID.randomUUID()
        val meshProxy = MeshRenderProxy(
            id = 1001L,
            meshId = testUuid,
            modelMatrix = FloatArray(16),
            aabbMin = floatArrayOf(-1f, -1f, -1f),
            aabbMax = floatArrayOf(1f, 1f, 1f),
            faceMaterials = listOf(FaceMaterialProxy(colorR = 0.8f, colorG = 0.2f, colorB = 0.2f))
        )

        val objectProxy = ObjectRenderProxy(
            id = testUuid,
            localId = 42,
            posX = 128f, posY = 128f, posZ = 25f,
            rotW = 1f, rotX = 0f, rotY = 0f, rotZ = 0f,
            scaleX = 1f, scaleY = 1f, scaleZ = 1f,
            name = "TestPrim"
        )

        // Background worker publishes new snapshot
        manager.publishNewSnapshot(
            meshProxies = listOf(meshProxy),
            objectProxies = mapOf(testUuid to objectProxy),
            terrainProxy = TerrainRenderProxy(FloatArray(256 * 256) { 10f })
        )

        // GL Thread swaps snapshot on frame boundary
        val swappedSnapshot = manager.swapRenderProxyOnFrameBoundary()
        assertEquals(1L, swappedSnapshot.snapshotId)
        assertEquals(1, swappedSnapshot.meshProxies.size)
        assertEquals(1001L, swappedSnapshot.meshProxies[0].id)
        assertEquals(1, swappedSnapshot.objectProxies.size)
        assertEquals("TestPrim", swappedSnapshot.objectProxies[testUuid]?.name)
        assertNotNull(swappedSnapshot.terrainProxy)

        // Second swap on same frame returns current snapshot without pending
        val reSwapped = manager.swapRenderProxyOnFrameBoundary()
        assertEquals(1L, reSwapped.snapshotId)
    }

    @Test
    fun testThreadSafetyUnderConcurrentSwapsAndPublishes() {
        val manager = SceneRenderProxyManager()
        val startLatch = CountDownLatch(1)
        val stopFlag = AtomicBoolean(false)
        val publishCounter = AtomicBoolean(true)

        // Producer thread
        val producer = Thread {
            startLatch.await()
            var count = 0L
            while (!stopFlag.get()) {
                count++
                val objectUuid = UUID.randomUUID()
                manager.publishNewSnapshot(
                    meshProxies = emptyList(),
                    objectProxies = mapOf(
                        objectUuid to ObjectRenderProxy(
                            id = objectUuid, localId = count.toInt(),
                            posX = count.toFloat(), posY = 0f, posZ = 0f,
                            rotW = 1f, rotX = 0f, rotY = 0f, rotZ = 0f,
                            scaleX = 1f, scaleY = 1f, scaleZ = 1f
                        )
                    )
                )
                Thread.sleep(1)
            }
        }

        // Consumer (GL) thread
        var readSuccessCount = 0
        val consumer = Thread {
            startLatch.await()
            while (!stopFlag.get()) {
                val snapshot = manager.swapRenderProxyOnFrameBoundary()
                assertNotNull(snapshot)
                readSuccessCount++
                Thread.sleep(1)
            }
        }

        producer.start()
        consumer.start()
        startLatch.countDown()

        Thread.sleep(200)
        stopFlag.set(true)

        producer.join(1000)
        consumer.join(1000)

        assertTrue("Consumer should perform multiple lock-free frame swaps", readSuccessCount > 10)
    }
}
