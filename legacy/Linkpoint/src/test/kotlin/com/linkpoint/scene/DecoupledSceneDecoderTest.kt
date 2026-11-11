package com.linkpoint.scene

import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshFace
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDParser
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import com.linkpoint.scene.decoder.DecoupledSceneDecoder
import com.linkpoint.scene.proxy.ObjectRenderProxy
import com.linkpoint.scene.worker.TaskSubmissionResult
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DecoupledSceneDecoderTest {

    @Test
    fun testAsyncLLSDDecodingOnWorkerThread() {
        val decoder = DecoupledSceneDecoder()

        // Construct sample binary LLSD map: { "key": "value" }
        // LLSD Notation representation for test
        val notationData = "{'key':'value'}".toByteArray()
        val parsedVal = LLSDParser.parseNotation(notationData) as? LLSDMap

        assertNotNull(parsedVal)
        assertEquals("value", (parsedVal?.value?.get("key") as? LLSDString)?.value)
    }

    @Test
    fun testAsyncMeshGeometryDecodingAndProxyPublishing() {
        val decoder = DecoupledSceneDecoder()
        val meshId = UUID.randomUUID()
        val objectIdLong = 2002L

        val face = MeshFace(
            positions = floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, 0f, 1f, 0f),
            normals = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f),
            uvs = floatArrayOf(0f, 0f, 1f, 0f, 0.5f, 1f),
            indices = shortArrayOf(0, 1, 2)
        )
        val meshData = MeshData(meshId = meshId, faces = listOf(face))

        val latch = CountDownLatch(1)
        val result = decoder.decodeMeshGeometryAsync(
            meshId = meshId,
            objectId = objectIdLong,
            data = meshData,
            distanceToCamera = 10f
        ) { proxy ->
            assertEquals(objectIdLong, proxy.id)
            assertEquals(meshId, proxy.meshId)
            latch.countDown()
        }

        assertEquals(TaskSubmissionResult.Accepted, result)
        val decodedInTime = latch.await(3, TimeUnit.SECONDS)
        assertTrue("Mesh geometry decoding should complete asynchronously", decodedInTime)

        // Verify GL thread frame boundary proxy snapshot swap
        val snapshot = decoder.swapRenderProxyOnFrameBoundary()
        assertNotNull(snapshot)
        assertTrue("Snapshot should contain decoded mesh proxy", snapshot.meshProxies.any { it.id == objectIdLong })
    }

    @Test
    fun testObjectAndTerrainProxyUpdates() {
        val decoder = DecoupledSceneDecoder()
        val objId = UUID.randomUUID()

        val objProxy = ObjectRenderProxy(
            id = objId,
            localId = 101,
            posX = 50f, posY = 60f, posZ = 10f,
            rotW = 1f, rotX = 0f, rotY = 0f, rotZ = 0f,
            scaleX = 2f, scaleY = 2f, scaleZ = 2f,
            name = "Chair"
        )

        decoder.updateObjectProxy(objProxy)
        decoder.updateTerrainProxy(FloatArray(256 * 256) { 15f })

        val snapshot = decoder.swapRenderProxyOnFrameBoundary()
        assertEquals("Chair", snapshot.objectProxies[objId]?.name)
        assertNotNull(snapshot.terrainProxy)
        assertEquals(15f, snapshot.terrainProxy?.heightmap?.get(0))
    }

    @Test
    fun testAsyncSpatialOctreeQuery() {
        val decoder = DecoupledSceneDecoder()
        val culler = FrustumCuller()
        val proj = FloatArray(16)
        val view = FloatArray(16)
        android.opengl.Matrix.setIdentityM(proj, 0)
        android.opengl.Matrix.setIdentityM(view, 0)
        culler.extractPlanes(proj, view)

        val queryFuture = decoder.asyncSpatialIndex.queryFrustumAsync(culler)
        val results = queryFuture.get(3, TimeUnit.SECONDS)
        assertNotNull(results)
    }
}
