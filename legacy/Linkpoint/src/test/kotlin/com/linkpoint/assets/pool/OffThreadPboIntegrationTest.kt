package com.linkpoint.assets.pool

import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.MeshManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class OffThreadPboIntegrationTest {

    @Test
    fun testTextureAndMeshManagerWorkerPoolIntegration() {
        val mockContext = RuntimeEnvironment.getApplication()
        val mockCache = AssetCache(mockContext)
        val mockCapManager = CapabilityManager()

        val textureManager = TextureManager(mockContext, mockCache, mockCapManager)
        val meshManager = MeshManager(mockContext, mockCache, mockCapManager)

        assertNotNull(textureManager.decodingWorkerPool)
        assertNotNull(meshManager.decodingWorkerPool)
        assertTrue(textureManager.decodingWorkerPool.threadCount in 1..4)
        assertTrue(meshManager.decodingWorkerPool.threadCount in 1..4)

        // Test teleport/region crossing cancellation
        textureManager.onTeleportOrRegionTransfer()
        meshManager.onTeleportOrRegionTransfer()

        val texDiag = textureManager.decodingWorkerPool.getDiagnostics()
        val meshDiag = meshManager.decodingWorkerPool.getDiagnostics()

        assertEquals(0, texDiag.activeTaskCount)
        assertEquals(0, meshDiag.activeTaskCount)

        textureManager.shutdown()
        meshManager.decodingWorkerPool.shutdown()
    }
}
