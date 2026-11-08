package com.linkpoint.assets.pool

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.assets.MeshLOD
import com.linkpoint.assets.TexturePriority
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AssetDecodingWorkerPoolTest {

    @Test
    fun testThreadCountIsBoundedToNMinus1() {
        val pool = AssetDecodingWorkerPool()
        val cores = Runtime.getRuntime().availableProcessors()
        val expected = minOf(4, maxOf(1, cores - 1))

        assertEquals(expected, pool.threadCount)
        assertTrue(pool.threadCount in 1..4)
        pool.shutdown()
    }

    @Test
    fun testThreadCountOverride() {
        val pool = AssetDecodingWorkerPool(maxWorkerThreadsOverride = 2)
        assertEquals(2, pool.threadCount)
        pool.shutdown()
    }

    @Test
    fun testTaskCancellationByUuid() {
        val pool = AssetDecodingWorkerPool(maxWorkerThreadsOverride = 1)
        val testId = UUID.randomUUID()
        val dummyData = ByteArray(1024)
        val latch = java.util.concurrent.CountDownLatch(1)

        pool.parseMeshAsync(
            meshId = testId,
            data = dummyData,
            lod = MeshLOD.HIGH,
            parseBlock = { _, _ ->
                latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                null
            }
        ) {}

        pool.cancelTask(testId)
        latch.countDown()
        Thread.sleep(100)

        val diag = pool.getDiagnostics()
        assertEquals(1, diag.cancelledDecodes)
        pool.shutdown()
    }

    @Test
    fun testCancelAllPendingTasks() {
        val pool = AssetDecodingWorkerPool(maxWorkerThreadsOverride = 2)
        val latch = java.util.concurrent.CountDownLatch(1)
        repeat(5) {
            pool.parseMeshAsync(
                meshId = UUID.randomUUID(),
                data = ByteArray(512),
                lod = MeshLOD.HIGH,
                parseBlock = { _, _ ->
                    latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                    null
                }
            ) {}
        }

        pool.cancelAllPending()
        latch.countDown()

        val diag = pool.getDiagnostics()
        assertEquals(0, diag.activeTaskCount)
        assertEquals(5, diag.cancelledDecodes)
        pool.shutdown()
    }

    @Test
    fun testMeshParsingAsyncExecution() {
        val pool = AssetDecodingWorkerPool(maxWorkerThreadsOverride = 2)
        val meshId = UUID.randomUUID()
        var executedOffThread = false
        var threadName = ""

        val job = pool.parseMeshAsync(
            meshId = meshId,
            data = ByteArray(100),
            lod = MeshLOD.HIGH,
            parseBlock = { _, _ ->
                threadName = Thread.currentThread().name
                executedOffThread = threadName.contains("AssetDecoderWorker")
                null
            }
        ) {}

        runBlocking {
            job.join()
        }

        assertTrue(executedOffThread)
        assertTrue(threadName.contains("AssetDecoderWorker"))
        pool.shutdown()
    }
}
