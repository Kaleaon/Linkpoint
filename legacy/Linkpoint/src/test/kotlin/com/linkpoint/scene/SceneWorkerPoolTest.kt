package com.linkpoint.scene

import com.linkpoint.scene.worker.SceneWorkerPool
import com.linkpoint.scene.worker.TaskPriority
import com.linkpoint.scene.worker.TaskSubmissionResult
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SceneWorkerPoolTest {

    @Test
    fun testAdaptiveThreadCountCalculation() {
        // Core counts from low-end (2) to high-end mobile ARM CPUs (8, 12)
        assertEquals(2, SceneWorkerPool.calculateAdaptiveThreadCount(1))
        assertEquals(2, SceneWorkerPool.calculateAdaptiveThreadCount(2))
        assertEquals(3, SceneWorkerPool.calculateAdaptiveThreadCount(4))
        assertEquals(7, SceneWorkerPool.calculateAdaptiveThreadCount(8))
        assertEquals(11, SceneWorkerPool.calculateAdaptiveThreadCount(12))
    }

    @Test
    fun testTaskExecutionAndCounterIncrement() {
        val pool = SceneWorkerPool(2, 256)
        val latch = CountDownLatch(5)
        val counter = AtomicInteger(0)

        for (i in 0 until 5) {
            pool.submit(TaskPriority.HIGH, "TestTask-$i") {
                counter.incrementAndGet()
                latch.countDown()
            }
        }

        val completedInTime = latch.await(5, TimeUnit.SECONDS)
        assertTrue("Tasks should complete within timeout", completedInTime)
        assertEquals(5, counter.get())
        pool.shutdown()
    }

    @Test
    fun testPriorityOrderingAndQueueLimits() {
        val smallCapacityPool = SceneWorkerPool(2, 2)

        // Block worker threads with latch
        val blockLatch = CountDownLatch(1)
        val taskLatch = CountDownLatch(2)

        // Submit blocking tasks
        for (i in 0 until smallCapacityPool.threadCount) {
            smallCapacityPool.submit(TaskPriority.HIGH, "BlockingTask-$i") {
                blockLatch.await(5, TimeUnit.SECONDS)
            }
        }

        // Fill queue to capacity (max 2)
        val res1 = smallCapacityPool.submit(TaskPriority.HIGH, "QueuedHigh-1") { taskLatch.countDown() }
        val res2 = smallCapacityPool.submit(TaskPriority.HIGH, "QueuedHigh-2") { taskLatch.countDown() }

        assertEquals(TaskSubmissionResult.Accepted, res1)
        assertEquals(TaskSubmissionResult.Accepted, res2)

        // Submit low priority distant mesh decoding task when queue capacity is full
        val resDropped = smallCapacityPool.submit(TaskPriority.LOW, "DistantMeshDecode") {
            fail("Dropped task should not execute")
        }

        assertTrue("Low-priority task should be dropped when queue capacity limit reached", resDropped is TaskSubmissionResult.Dropped)
        assertTrue("Dropped low priority count should be > 0", smallCapacityPool.getDroppedLowPriorityCount() > 0)

        // Unblock workers
        blockLatch.countDown()
        val finishedInTime = taskLatch.await(5, TimeUnit.SECONDS)
        assertTrue("Queued high priority tasks should complete after unblock", finishedInTime)
        smallCapacityPool.shutdown()
    }

    @Test
    fun testCompletableFutureCallableSubmission() {
        val pool = SceneWorkerPool(2, 256)
        val future = pool.submitCallable(TaskPriority.HIGH, "AsyncCalculation") {
            40 + 2
        }

        val result = future.get(3, TimeUnit.SECONDS)
        assertEquals(42, result.toInt())
        pool.shutdown()
    }
}
