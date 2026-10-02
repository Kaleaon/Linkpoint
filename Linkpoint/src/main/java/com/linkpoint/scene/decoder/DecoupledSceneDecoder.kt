package com.linkpoint.scene.decoder

import android.util.Log
import com.linkpoint.assets.MeshData
import com.linkpoint.protocol.llsd.LLSDParser
import com.linkpoint.protocol.llsd.LLSDValue
import com.linkpoint.protocol.scenery.SceneDataHandler
import com.linkpoint.render.lumiya.spatial.AsyncSpatialIndex
import com.linkpoint.render.lumiya.spatial.SpatialEntry
import com.linkpoint.scene.proxy.*
import com.linkpoint.scene.worker.SceneWorkerPool
import com.linkpoint.scene.worker.TaskPriority
import com.linkpoint.scene.worker.TaskSubmissionResult
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * High-level coordinator for decoupled scene decoding and rendering.
 *
 * Offloads network packet parsing, LLSD deserialization, mesh vertex unpacking,
 * and spatial octree node updates into [SceneWorkerPool] background tasks.
 *
 * Publishes thread-safe [SceneRenderProxySnapshot] objects to [SceneRenderProxyManager]
 * for lock-free snapshot swaps on frame boundaries on the main GL rendering thread.
 */
class DecoupledSceneDecoder(
    val workerPool: SceneWorkerPool = SceneWorkerPool.getInstance(),
    val renderProxyManager: SceneRenderProxyManager = SceneRenderProxyManager(),
    val asyncSpatialIndex: AsyncSpatialIndex = AsyncSpatialIndex(workerPool)
) {
    companion object {
        private const val TAG = "DecoupledSceneDecoder"
        /** Distance threshold (meters) past which mesh decoding is assigned LOW priority */
        const val DISTANT_MESH_THRESHOLD = 64f
    }

    private val activeMeshProxies = ConcurrentHashMap<Long, MeshRenderProxy>()
    private val activeObjectProxies = ConcurrentHashMap<UUID, ObjectRenderProxy>()
    @Volatile private var activeTerrainProxy: TerrainRenderProxy? = null

    /**
     * Decode an incoming binary or XML LLSD payload asynchronously on background worker pool.
     */
    fun decodeLLSDAsync(
        payload: ByteArray,
        priority: TaskPriority = TaskPriority.HIGH
    ): CompletableFuture<LLSDValue?> {
        return workerPool.submitCallable(priority, "LLSDDecode") {
            LLSDParser.parseBinary(payload)
        }
    }

    /**
     * Process an ObjectUpdate / LayerData packet asynchronously on background worker thread.
     */
    fun processPacketAsync(
        handler: SceneDataHandler,
        packetBytes: ByteArray,
        packetType: PacketType,
        priority: TaskPriority = TaskPriority.HIGH
    ): CompletableFuture<Boolean> {
        return workerPool.submitCallable(priority, "PacketProcess-${packetType.name}") {
            when (packetType) {
                PacketType.OBJECT_UPDATE -> handler.handleObjectUpdate(packetBytes)
                PacketType.LAYER_DATA -> handler.handleLayerData(packetBytes)
                PacketType.OBJECT_PROPERTIES -> handler.handleObjectProperties(packetBytes)
            }
        }
    }

    enum class PacketType {
        OBJECT_UPDATE,
        LAYER_DATA,
        OBJECT_PROPERTIES
    }

    /**
     * Submit mesh geometry unpacking / LOD generation task to background worker pool.
     * Automatically assigns TaskPriority.LOW and applies queue capacity drop/defer logic
     * for distant meshes.
     */
    fun decodeMeshGeometryAsync(
        meshId: UUID,
        objectId: Long,
        data: MeshData,
        distanceToCamera: Float,
        onDecoded: (MeshRenderProxy) -> Unit
    ): TaskSubmissionResult {
        val priority = if (distanceToCamera > DISTANT_MESH_THRESHOLD) TaskPriority.LOW else TaskPriority.HIGH

        return workerPool.submit(priority, "MeshGeometryDecode-$meshId") {
            // Unpack mesh face geometry and calculate AABB
            val faceMaterials = data.faces.map {
                FaceMaterialProxy()
            }

            var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE; var minZ = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE; var maxZ = -Float.MAX_VALUE

            for (face in data.faces) {
                val pos = face.positions
                for (i in pos.indices step 3) {
                    if (i + 2 < pos.size) {
                        val x = pos[i]; val y = pos[i + 1]; val z = pos[i + 2]
                        if (x < minX) minX = x; if (x > maxX) maxX = x
                        if (y < minY) minY = y; if (y > maxY) maxY = y
                        if (z < minZ) minZ = z; if (z > maxZ) maxZ = z
                    }
                }
            }

            if (minX == Float.MAX_VALUE) {
                minX = -0.5f; minY = -0.5f; minZ = -0.5f
                maxX = 0.5f; maxY = 0.5f; maxZ = 0.5f
            }

            val identityMatrix = floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )

            val proxy = MeshRenderProxy(
                id = objectId,
                meshId = meshId,
                modelMatrix = identityMatrix,
                aabbMin = floatArrayOf(minX, minY, minZ),
                aabbMax = floatArrayOf(maxX, maxY, maxZ),
                faceMaterials = faceMaterials,
                distanceToCamera = distanceToCamera
            )

            activeMeshProxies[objectId] = proxy

            // Asynchronously update spatial index entry
            val spatialEntry = SpatialEntry(
                id = objectId,
                posX = (minX + maxX) / 2f,
                posY = (minY + maxY) / 2f,
                posZ = (minZ + maxZ) / 2f,
                halfExtentX = (maxX - minX) / 2f,
                halfExtentY = (maxY - minY) / 2f,
                halfExtentZ = (maxZ - minZ) / 2f,
                distanceToCamera = distanceToCamera
            )
            asyncSpatialIndex.updateAsync(spatialEntry)

            // Publish updated snapshot before invoking callback
            publishCurrentState()
            onDecoded(proxy)
        }
    }

    /**
     * Register or update an object proxy.
     */
    fun updateObjectProxy(proxy: ObjectRenderProxy) {
        activeObjectProxies[proxy.id] = proxy
        publishCurrentState()
    }

    /**
     * Remove an object proxy.
     */
    fun removeObjectProxy(id: UUID, objectIdLong: Long? = null) {
        activeObjectProxies.remove(id)
        if (objectIdLong != null) {
            activeMeshProxies.remove(objectIdLong)
            asyncSpatialIndex.removeAsync(objectIdLong)
        }
        publishCurrentState()
    }

    /**
     * Update terrain proxy heightmap.
     */
    fun updateTerrainProxy(heightmap: FloatArray) {
        activeTerrainProxy = TerrainRenderProxy(heightmap.copyOf())
        publishCurrentState()
    }

    /**
     * Publish snapshot of current scene state to renderProxyManager.
     */
    fun publishCurrentState() {
        renderProxyManager.publishNewSnapshot(
            meshProxies = ArrayList(activeMeshProxies.values),
            objectProxies = HashMap(activeObjectProxies),
            terrainProxy = activeTerrainProxy
        )
    }

    /**
     * Swap render proxy snapshot at frame boundary (call on GL thread).
     */
    fun swapRenderProxyOnFrameBoundary(): SceneRenderProxySnapshot {
        return renderProxyManager.swapRenderProxyOnFrameBoundary()
    }
}
