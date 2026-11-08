package com.linkpoint.assets.pool

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.assets.TexturePriority
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FrustumPriorityLoadingQueueTest {

    @Test
    fun testPriorityEnumOrdering() {
        val queue = FrustumPriorityLoadingQueue()
        val lowReq = FrustumPriorityLoadingQueue.AssetLoadingRequest(
            assetId = UUID.randomUUID(),
            type = FrustumPriorityLoadingQueue.AssetCategory.TEXTURE,
            priority = TexturePriority.LOW
        )
        val criticalReq = FrustumPriorityLoadingQueue.AssetLoadingRequest(
            assetId = UUID.randomUUID(),
            type = FrustumPriorityLoadingQueue.AssetCategory.TEXTURE,
            priority = TexturePriority.CRITICAL
        )

        queue.enqueue(lowReq)
        queue.enqueue(criticalReq)

        val polledFirst = queue.poll()
        assertNotNull(polledFirst)
        assertEquals(criticalReq.assetId, polledFirst!!.assetId)

        val polledSecond = queue.poll()
        assertNotNull(polledSecond)
        assertEquals(lowReq.assetId, polledSecond!!.assetId)
    }

    @Test
    fun testFrustumDistanceSorting() {
        val queue = FrustumPriorityLoadingQueue()
        queue.updateCameraPosition(0f, 0f, 0f)

        val farReq = FrustumPriorityLoadingQueue.AssetLoadingRequest(
            assetId = UUID.randomUUID(),
            type = FrustumPriorityLoadingQueue.AssetCategory.TEXTURE,
            priority = TexturePriority.NORMAL,
            worldPositionX = 100f,
            worldPositionY = 0f,
            worldPositionZ = 0f
        )

        val nearReq = FrustumPriorityLoadingQueue.AssetLoadingRequest(
            assetId = UUID.randomUUID(),
            type = FrustumPriorityLoadingQueue.AssetCategory.TEXTURE,
            priority = TexturePriority.NORMAL,
            worldPositionX = 5f,
            worldPositionY = 0f,
            worldPositionZ = 0f
        )

        queue.enqueue(farReq)
        queue.enqueue(nearReq)

        val polledFirst = queue.poll()
        assertEquals(nearReq.assetId, polledFirst!!.assetId)
    }

    @Test
    fun testCancelAllOnTeleport() {
        val queue = FrustumPriorityLoadingQueue()
        repeat(10) {
            queue.enqueue(
                FrustumPriorityLoadingQueue.AssetLoadingRequest(
                    assetId = UUID.randomUUID(),
                    type = FrustumPriorityLoadingQueue.AssetCategory.TEXTURE,
                    priority = TexturePriority.NORMAL
                )
            )
        }

        assertEquals(10, queue.size)
        val cancelled = queue.cancelAll()

        assertEquals(10, cancelled)
        assertTrue(queue.isEmpty())
    }
}
