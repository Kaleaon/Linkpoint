package com.linkpoint.assets

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MeshDownloadManagerTest {

    private lateinit var fetcher: LLMeshFetcher
    private lateinit var meshManager: MeshManager
    private lateinit var downloadManager: MeshDownloadManager

    @Before
    fun setUp() {
        fetcher = LLMeshFetcher()
        meshManager = mock()
        downloadManager = MeshDownloadManager(fetcher, meshManager)
    }

    @Test
    fun testRequestMeshNearObjectNotDeferred() = runBlocking {
        val meshId = UUID.randomUUID()
        val mockData = MeshData(meshId = meshId, faces = emptyList())
        whenever(meshManager.getMesh(eq(meshId), any())).thenReturn(mockData)

        // Close object at 5m
        val result = downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(5f, 0f, 0f),
            boundingRadius = 1.0f
        )

        assertNotNull(result)
        verify(meshManager).getMesh(meshId, MeshLOD.HIGHEST)

        val diag = downloadManager.getDiagnostics()
        assertEquals(1, diag.totalRequests)
        assertEquals(0, diag.deferredRequestsCount)
        assertEquals(1, diag.lod0Requests)
    }

    @Test
    fun testRequestMeshDistantAvatarGated() = runBlocking {
        val meshId = UUID.randomUUID()
        val mockData = MeshData(meshId = meshId, faces = emptyList())
        whenever(meshManager.getMesh(eq(meshId), any())).thenReturn(mockData)

        // Avatar at 60m (>50m threshold)
        val result = downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(60f, 0f, 0f),
            boundingRadius = 3.0f,
            isAvatar = true
        )

        assertNotNull(result)
        // Verify gated to MEDIUM (LOD2)
        verify(meshManager).getMesh(meshId, MeshLOD.MEDIUM)

        val diag = downloadManager.getDiagnostics()
        assertEquals(1, diag.lod2Requests)
        assertTrue(diag.estimatedBytesSaved > 0)
    }

    @Test
    fun testRequestMeshSubpixelAttachmentDeferred() = runBlocking {
        val meshId = UUID.randomUUID()

        // Far small attachment (50m, radius 0.1m) -> projected ~1.87px < 8.0px -> deferred
        val result = downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(50f, 0f, 0f),
            boundingRadius = 0.1f,
            isAttachment = true
        )

        assertNull("Sub-pixel attachment should return null when deferred", result)
        verify(meshManager, never()).getMesh(any(), any())

        val diag = downloadManager.getDiagnostics()
        assertEquals(1, diag.deferredRequestsCount)
        assertEquals(1, diag.activeDeferredQueueSize)
    }

    @Test
    fun testUpdateCameraPromotesDeferredRequests() = runBlocking {
        val meshId = UUID.randomUUID()

        // 1. Initial position far away (50m) -> attachment is deferred
        downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(50f, 0f, 0f),
            boundingRadius = 0.1f,
            isAttachment = true
        )
        assertEquals(1, downloadManager.getDiagnostics().activeDeferredQueueSize)

        // 2. Move camera close to object (45m closer: camera at x=45m, object at x=50m -> dist=5m)
        val newCamera = LLMeshFetcher.CameraParams(
            position = LLVector3(45f, 0f, 0f),
            fovRad = Math.toRadians(60.0).toFloat(),
            screenHeightPx = 1080
        )
        val promoted = downloadManager.updateCamera(newCamera)

        // 3. Verify deferred queue promoted the request
        assertEquals(1, promoted.size)
        assertEquals(meshId, promoted[0])
        assertEquals(0, downloadManager.getDiagnostics().activeDeferredQueueSize)
    }
}
