package com.linkpoint.scene.proxy

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Material attributes for a single face in a render proxy.
 */
data class FaceMaterialProxy(
    val textureId: UUID = UUID(0L, 0L),
    val textureHandle: Int = 0,
    val colorR: Float = 1f,
    val colorG: Float = 1f,
    val colorB: Float = 1f,
    val colorA: Float = 1f,
    val scaleS: Float = 1f,
    val scaleT: Float = 1f,
    val offsetS: Float = 0f,
    val offsetT: Float = 0f,
    val rotation: Float = 0f
)

/**
 * Thread-safe, lightweight render proxy for a 3D mesh instance.
 * Decoupled from heavy background decoding structures.
 */
data class MeshRenderProxy(
    val id: Long,
    val meshId: UUID,
    val modelMatrix: FloatArray,
    val aabbMin: FloatArray,
    val aabbMax: FloatArray,
    val faceMaterials: List<FaceMaterialProxy>,
    val distanceToCamera: Float = 0f,
    val isTransparent: Boolean = false,
    val isAvatar: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MeshRenderProxy
        return id == other.id && meshId == other.meshId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + meshId.hashCode()
        return result
    }
}

/**
 * Thread-safe render proxy for scene object metadata and transforms.
 */
data class ObjectRenderProxy(
    val id: UUID,
    val localId: Int,
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val rotW: Float,
    val rotX: Float,
    val rotY: Float,
    val rotZ: Float,
    val scaleX: Float,
    val scaleY: Float,
    val scaleZ: Float,
    val name: String = "",
    val pCode: Int = 9
)

/**
 * Thread-safe render proxy for terrain heightmap data.
 */
data class TerrainRenderProxy(
    val heightmap: FloatArray,
    val width: Int = 256,
    val height: Int = 256
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TerrainRenderProxy
        return width == other.width && height == other.height && heightmap.contentEquals(other.heightmap)
    }

    override fun hashCode(): Int {
        var result = heightmap.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}

/**
 * Immutable, thread-safe snapshot of scene rendering state.
 * Produced asynchronously by background workers and consumed on frame boundaries
 * by the main display/GL rendering thread via atomic reference swap.
 */
data class SceneRenderProxySnapshot(
    val snapshotId: Long,
    val timestampMs: Long,
    val meshProxies: List<MeshRenderProxy> = emptyList(),
    val objectProxies: Map<UUID, ObjectRenderProxy> = emptyMap(),
    val terrainProxy: TerrainRenderProxy? = null,
    val cameraPosition: FloatArray = floatArrayOf(128f, 128f, 30f),
    val cameraTarget: FloatArray = floatArrayOf(128f, 128f, 0f)
)

/**
 * Double-buffered Atomic Reference container for lock-free render proxy snapshot swaps.
 */
class SceneRenderProxyManager {

    private val snapshotCounter = AtomicLong(0L)
    private val currentSnapshot = AtomicReference<SceneRenderProxySnapshot>(
        SceneRenderProxySnapshot(
            snapshotId = 0L,
            timestampMs = System.currentTimeMillis()
        )
    )

    private val pendingSnapshot = AtomicReference<SceneRenderProxySnapshot?>(null)

    /**
     * Called by background worker threads to publish a newly constructed scene snapshot.
     */
    fun publishSnapshot(snapshot: SceneRenderProxySnapshot) {
        pendingSnapshot.set(snapshot)
    }

    /**
     * Called by background worker threads to build and publish a snapshot atomically.
     */
    fun publishNewSnapshot(
        meshProxies: List<MeshRenderProxy>,
        objectProxies: Map<UUID, ObjectRenderProxy>,
        terrainProxy: TerrainRenderProxy? = null,
        cameraPosition: FloatArray = floatArrayOf(128f, 128f, 30f),
        cameraTarget: FloatArray = floatArrayOf(128f, 128f, 0f)
    ): SceneRenderProxySnapshot {
        val nextId = snapshotCounter.incrementAndGet()
        val snapshot = SceneRenderProxySnapshot(
            snapshotId = nextId,
            timestampMs = System.currentTimeMillis(),
            meshProxies = meshProxies,
            objectProxies = objectProxies,
            terrainProxy = terrainProxy,
            cameraPosition = cameraPosition,
            cameraTarget = cameraTarget
        )
        publishSnapshot(snapshot)
        return snapshot
    }

    /**
     * Swaps the pending snapshot to current atomically if available.
     * Called by the main display/GL thread at frame boundaries before rendering.
     * Guaranteed lock-free and non-blocking.
     */
    fun swapRenderProxyOnFrameBoundary(): SceneRenderProxySnapshot {
        val pending = pendingSnapshot.getAndSet(null)
        if (pending != null) {
            currentSnapshot.set(pending)
            return pending
        }
        return currentSnapshot.get()
    }

    /**
     * Get active snapshot for current frame (lock-free read).
     */
    fun getCurrentSnapshot(): SceneRenderProxySnapshot = currentSnapshot.get()
}
