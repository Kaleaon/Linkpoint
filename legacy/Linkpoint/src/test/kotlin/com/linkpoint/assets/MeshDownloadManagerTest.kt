package com.linkpoint.assets

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class MeshDownloadManagerTest {

    private lateinit var fetcher: LLMeshFetcher
    private lateinit var meshManager: FakeMeshManager
    private lateinit var downloadManager: MeshDownloadManager

    private class FakeMeshManager : MeshManager(
        RuntimeEnvironment.getApplication(),
        AssetCache(RuntimeEnvironment.getApplication()),
        CapabilityManager()
    ) {
        var mockDataMap = mutableMapOf<UUID, MeshData?>()
        val getMeshCalls = mutableListOf<Pair<UUID, MeshLOD>>()

        override suspend fun getMesh(meshId: UUID, lod: MeshLOD): MeshData? {
            getMeshCalls.add(meshId to lod)
            return mockDataMap[meshId]
        }
    }

    @Before
    fun setUp() {
        fetcher = LLMeshFetcher()
        meshManager = FakeMeshManager()
        downloadManager = MeshDownloadManager(fetcher, meshManager)
    }

    @Test
    fun testRequestMeshNearObjectNotDeferred() = runBlocking {
        val meshId = UUID.randomUUID()
        val mockData = MeshData(meshId = meshId, faces = emptyList())
        meshManager.mockDataMap[meshId] = mockData

        // Close object at 5m
        val result = downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(5f, 0f, 0f),
            boundingRadius = 1.0f
        )

        assertNotNull(result)
        assertTrue(meshManager.getMeshCalls.contains(meshId to MeshLOD.HIGHEST))

        val diag = downloadManager.getDiagnostics()
        assertEquals(1, diag.totalRequests)
        assertEquals(0, diag.deferredRequestsCount)
        assertEquals(1, diag.lod0Requests)
    }

    @Test
    fun testRequestMeshDistantAvatarGated() = runBlocking {
        val meshId = UUID.randomUUID()
        val mockData = MeshData(meshId = meshId, faces = emptyList())
        meshManager.mockDataMap[meshId] = mockData

        // Avatar at 60m (>50m threshold)
        val result = downloadManager.requestMesh(
            meshId = meshId,
            objectPos = LLVector3(60f, 0f, 0f),
            boundingRadius = 3.0f,
            isAvatar = true
        )

        assertNotNull(result)
        // Verify gated to MEDIUM (LOD2)
        assertTrue(meshManager.getMeshCalls.contains(meshId to MeshLOD.MEDIUM))

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
        assertTrue(meshManager.getMeshCalls.isEmpty())

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

    @Test
    fun testEvaluateLodChangeWithHysteresisAndDuplicateGuardrails() {
        val meshId = UUID.randomUUID()
        val objectPos = LLVector3(10f, 0f, 0f)

        // 1. Initial evaluation at 10m -> target HIGH
        val selection1 = downloadManager.evaluateLodChange(
            meshId = meshId,
            objectPos = objectPos,
            boundingRadius = 1.0f,
            currentLod = MeshLOD.HIGH,
            pendingLod = null,
            lastEvaluatedDistance = 10f
        )
        // Distance did not change beyond 15% -> returns null
        assertNull(selection1)

        // 2. Small distance change from 10m to 10.5m (5% change < 15%) -> returns null
        val selection2 = downloadManager.evaluateLodChange(
            meshId = meshId,
            objectPos = LLVector3(10.5f, 0f, 0f),
            boundingRadius = 1.0f,
            currentLod = MeshLOD.HIGH,
            pendingLod = null,
            lastEvaluatedDistance = 10f
        )
        assertNull(selection2)

        // 3. Significant distance move from 10m to 100m (> 15% change) -> evaluates new LOD
        val selection3 = downloadManager.evaluateLodChange(
            meshId = meshId,
            objectPos = LLVector3(100f, 0f, 0f),
            boundingRadius = 1.0f,
            currentLod = MeshLOD.HIGH,
            pendingLod = null,
            lastEvaluatedDistance = 10f
        )
        assertNotNull(selection3)
        assertNotEquals(MeshLOD.HIGH, selection3?.targetLod)

        // 4. Duplicate request guardrail: if targetLod equals pendingLod -> returns null
        val target = selection3!!.targetLod
        val selection4 = downloadManager.evaluateLodChange(
            meshId = meshId,
            objectPos = LLVector3(100f, 0f, 0f),
            boundingRadius = 1.0f,
            currentLod = MeshLOD.HIGH,
            pendingLod = target,
            lastEvaluatedDistance = 10f
        )
        assertNull(selection4)
    }

    @Test
    fun testUpgradeTaskThrottling() {
        assertTrue(downloadManager.canStartUpgradeTask())
        for (i in 0 until 4) {
            downloadManager.onUpgradeTaskStarted()
        }
        assertFalse("Should throttle when max concurrent upgrade tasks (4) reached", downloadManager.canStartUpgradeTask())
        downloadManager.onUpgradeTaskCompleted()
        assertTrue(downloadManager.canStartUpgradeTask())
    }
}
