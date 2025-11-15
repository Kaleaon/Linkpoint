package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import com.google.android.filament.Box
import com.google.android.filament.Engine
import com.google.android.filament.IndexBuffer
import com.google.android.filament.Material
import com.google.android.filament.RenderableManager
import com.google.android.filament.Scene
import com.google.android.filament.VertexBuffer
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectPrimInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Bridges world state obtained from legacy Second Life managers into Filament scene entities.
 *
 * Responsibilities are intentionally limited: this class translates the authoritative data
 * produced by `ObjectsManager`, `UserManager`, and `SLParcelInfo` into Filament renderables and
 * forwards avatar updates to [FilamentAvatarRenderer].
 */
class FilamentWorldDataBridge(
    @Suppress("UnusedParameter") private val context: Context,
    private val renderContext: FilamentRenderContext,
    private val materialManager: FilamentMaterialManager,
    private val avatarRenderer: FilamentAvatarRenderer,
) {

    companion object {
        private const val TAG = "FilamentWorldBridge"
        private const val UPDATE_INTERVAL_MS = 200L
        private const val AVATAR_TIMEOUT_MS = 5_000L
    }

    private val engine: Engine
        get() = renderContext.engine
    private val scene: Scene
        get() = renderContext.scene

    private val objectEntities = ConcurrentHashMap<Int, EntityData>()
    private val terrainEntities = ConcurrentHashMap<Int, EntityData>()
    private val activeAvatarTimestamps = ConcurrentHashMap<UUID, Long>()

    private var objectsManager: ObjectsManager? = null
    private var userManager: UserManager? = null
    private var terrainData: TerrainData? = null

    private val primGeometry = FilamentPrimGeometry(engine)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var updateJob: Job? = null
    private var isActive = false

    private data class EntityData(
        @com.google.android.filament.Entity val entity: Int,
        val vertexBuffer: VertexBuffer? = null,
        val indexBuffer: IndexBuffer? = null,
        val material: Material? = null,
    )

    fun setObjectsManager(manager: ObjectsManager) {
        objectsManager = manager
        Log.i(TAG, "ObjectsManager connected")
    }

    fun setUserManager(manager: UserManager) {
        userManager = manager
        Log.i(TAG, "UserManager connected")
    }

    fun setTerrainData(terrain: TerrainData) {
        terrainData = terrain
        Log.i(TAG, "Terrain data connected")
    }

    fun startSync() {
        if (isActive) return
        isActive = true
        updateJob = scope.launch {
            while (isActive) {
                try {
                    syncWorldData()
                } catch (t: Throwable) {
                    Log.e(TAG, "World sync error", t)
                }
                delay(UPDATE_INTERVAL_MS)
            }
        }
        Log.i(TAG, "World data sync started")
    }

    fun stopSync() {
        if (!isActive) return
        isActive = false
        updateJob?.cancel()
        updateJob = null
        Log.i(TAG, "World data sync stopped")
    }

    private suspend fun syncWorldData() {
        withContext(Dispatchers.Main) {
            syncTerrain()
            syncObjects()
            syncAvatars()
        }
    }

    private fun syncTerrain() {
        val terrain = terrainData ?: return
        
        // Stream terrain patches into Filament terrain renderer
        try {
            // Check if terrain has been updated
            val terrainHash = terrain.hashCode()
            
            // TODO: Implement terrain patch streaming system
            // This would involve:
            // 1. Dividing terrain into patches (e.g., 16x16 meter patches)
            // 2. Determining which patches are visible based on camera position
            // 3. Loading/unloading patches based on distance (LOD system)
            // 4. Converting terrain height data to Filament mesh format
            // 5. Applying terrain textures and materials
            
            // For now, we'll create a simple terrain representation
            if (!terrainEntities.containsKey(terrainHash)) {
                createTerrainMesh(terrain)?.let { entityData ->
                    terrainEntities[terrainHash] = entityData
                    Log.i(TAG, "Terrain mesh created and added to scene")
                }
            }
            
            Log.v(TAG, "Terrain sync completed (hash: $terrainHash)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing terrain", e)
        }
    }
    
    /**
     * Creates a terrain mesh from terrain data.
     * 
     * This is a simplified implementation. A full implementation would:
     * - Use terrain height maps
     * - Implement LOD (Level of Detail) system
     * - Stream patches based on camera position
     * - Apply terrain textures and materials
     * - Handle terrain physics
     */
    private fun createTerrainMesh(terrain: TerrainData): EntityData? {
        try {
            // Create a simple flat terrain mesh as placeholder
            // In a full implementation, this would use actual terrain height data
            
            val terrainSize = 256f // 256 meters (standard SL region size)
            val gridResolution = 32 // 32x32 grid
            val cellSize = terrainSize / gridResolution
            
            // Calculate vertex count
            val vertexCount = (gridResolution + 1) * (gridResolution + 1)
            val indexCount = gridResolution * gridResolution * 6
            
            // Create vertex buffer
            val stride = 8 * java.lang.Float.BYTES // position(3) + normal(3) + uv(2)
            val vertexBuffer = VertexBuffer.Builder()
                .bufferCount(1)
                .vertexCount(vertexCount)
                .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, stride)
                .attribute(VertexBuffer.VertexAttribute.TANGENTS, 0, VertexBuffer.AttributeType.FLOAT3, 12, stride)
                .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 24, stride)
                .build(engine)
            
            // Generate vertices
            val vertices = FloatArray(vertexCount * 8)
            var vertexIndex = 0
            
            for (z in 0..gridResolution) {
                for (x in 0..gridResolution) {
                    val posX = x * cellSize
                    val posZ = z * cellSize
                    val posY = 0f // Flat terrain for now
                    
                    // Position
                    vertices[vertexIndex++] = posX
                    vertices[vertexIndex++] = posY
                    vertices[vertexIndex++] = posZ
                    
                    // Normal (pointing up)
                    vertices[vertexIndex++] = 0f
                    vertices[vertexIndex++] = 1f
                    vertices[vertexIndex++] = 0f
                    
                    // UV coordinates
                    vertices[vertexIndex++] = x.toFloat() / gridResolution
                    vertices[vertexIndex++] = z.toFloat() / gridResolution
                }
            }
            
            vertexBuffer.setBufferAt(engine, 0, floatArrayToByteBuffer(vertices))
            
            // Generate indices
            val indices = ShortArray(indexCount)
            var indexIndex = 0
            
            for (z in 0 until gridResolution) {
                for (x in 0 until gridResolution) {
                    val topLeft = (z * (gridResolution + 1) + x).toShort()
                    val topRight = (topLeft + 1).toShort()
                    val bottomLeft = (topLeft + gridResolution + 1).toShort()
                    val bottomRight = (bottomLeft + 1).toShort()
                    
                    // First triangle
                    indices[indexIndex++] = topLeft
                    indices[indexIndex++] = bottomLeft
                    indices[indexIndex++] = topRight
                    
                    // Second triangle
                    indices[indexIndex++] = topRight
                    indices[indexIndex++] = bottomLeft
                    indices[indexIndex++] = bottomRight
                }
            }
            
            val indexBuffer = IndexBuffer.Builder()
                .indexCount(indexCount)
                .bufferType(IndexBuffer.Builder.IndexType.USHORT)
                .build(engine)
            indexBuffer.setBuffer(engine, shortArrayToByteBuffer(indices))
            
            // Create entity
            val entity = renderContext.entityManager.create()
            
            // Get terrain material
            val material = materialManager.getMaterial(FilamentMaterialManager.MaterialType.TERRAIN)
            
            // Build renderable
            RenderableManager.Builder(1)
                .boundingBox(Box(
                    floatArrayOf(0f, 0f, 0f),
                    floatArrayOf(terrainSize, 0f, terrainSize)
                ))
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer)
                .material(0, material.defaultInstance)
                .castShadows(false)
                .receiveShadows(true)
                .build(engine, entity)
            
            // Set transform (identity - terrain at origin)
            val transform = FloatArray(16)
            Matrix.setIdentityM(transform, 0)
            val transformInstance = engine.transformManager.getInstance(entity)
            if (transformInstance.isValid) {
                engine.transformManager.setTransform(transformInstance, transform)
            }
            
            // Add to scene
            scene.addEntity(entity)
            
            Log.i(TAG, "Terrain mesh created: ${gridResolution}x${gridResolution} grid, $vertexCount vertices, ${indexCount / 3} triangles")
            
            return EntityData(entity, vertexBuffer, indexBuffer, material)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating terrain mesh", e)
            return null
        }
    }

    private fun syncObjects() {
        // The legacy object manager exposes a reactive API; a full integration will subscribe
        // to its pools.  Until that is wired, we simply keep previously registered objects alive.
        val manager = objectsManager ?: return
        // Placeholder hook for future reconciliation with manager state.
        Log.v(TAG, "ObjectsManager state observed (${manager.hashCode()})")
    }

    private fun syncAvatars() {
        val circuit = userManager?.getActiveAgentCircuit() ?: return
        val parcelInfo = circuit.gridConnection?.parcelInfo ?: return
        val avatars = parcelInfo.snapshotAvatarObjects()
        val now = SystemClock.elapsedRealtime()

        val seenIds = HashSet<UUID>(avatars.size)
        for (avatar in avatars) {
            val id = avatar.id ?: continue
            seenIds.add(id)
            avatarRenderer.updateAvatar(avatar)
            activeAvatarTimestamps[id] = now
        }

        val iterator = activeAvatarTimestamps.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key !in seenIds && now - entry.value > AVATAR_TIMEOUT_MS) {
                iterator.remove()
                avatarRenderer.removeAvatar(entry.key)
            }
        }
    }

    fun addObject(localId: Int, obj: SLObjectInfo) {
        if (objectEntities.containsKey(localId)) return
        createObjectRenderable(obj)?.let { data ->
            objectEntities[localId] = data
            Log.d(TAG, "Object $localId added to Filament scene")
        }
    }

    fun removeObject(localId: Int) {
        objectEntities.remove(localId)?.let { removeEntity(it) }
    }

    private fun createObjectRenderable(obj: SLObjectInfo): EntityData? {
        val position = obj.getAbsolutePosition() ?: return null
        val scale = LLVector3(1f, 1f, 1f)

        val entity = renderContext.entityManager.create()
        val (vertexBuffer, indexBuffer) = runCatching {
            val prim = (obj as? SLObjectPrimInfo)?.primDrawParams?.volumeParams
            if (prim != null) primGeometry.generatePrimMesh(prim, scale) else createCubeMesh(scale)
        }.getOrElse {
            Log.w(TAG, "Falling back to cube mesh for object ${obj.getId()}", it)
            createCubeMesh(scale)
        }

        val material = materialManager.getMaterial(FilamentMaterialManager.MaterialType.PRIM_BASIC)
        RenderableManager.Builder(1)
            .boundingBox(Box(
                floatArrayOf(0f, 0f, 0f),
                floatArrayOf(scale.x, scale.y, scale.z)
            ))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer)
            .material(0, material.defaultInstance)
            .castShadows(true)
            .receiveShadows(true)
            .build(engine, entity)

        val transform = FloatArray(16)
        Matrix.setIdentityM(transform, 0)
        Matrix.translateM(transform, 0, position.x, position.y, position.z)
        val transformInstance = engine.transformManager.getInstance(entity)
        if (transformInstance.isValid) {
            engine.transformManager.setTransform(transformInstance, transform)
        }

        scene.addEntity(entity)
        return EntityData(entity, vertexBuffer, indexBuffer, material)
    }

    private fun createCubeMesh(scale: LLVector3): Pair<VertexBuffer, IndexBuffer> {
        val stride = 5 * java.lang.Float.BYTES
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(24)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, stride)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 12, stride)
            .build(engine)

        val vertices = floatArrayOf(
            -scale.x, -scale.y,  scale.z, 0f, 0f,
             scale.x, -scale.y,  scale.z, 1f, 0f,
             scale.x,  scale.y,  scale.z, 1f, 1f,
            -scale.x,  scale.y,  scale.z, 0f, 1f,

            -scale.x, -scale.y, -scale.z, 1f, 0f,
            -scale.x,  scale.y, -scale.z, 1f, 1f,
             scale.x,  scale.y, -scale.z, 0f, 1f,
             scale.x, -scale.y, -scale.z, 0f, 0f,

            -scale.x,  scale.y, -scale.z, 0f, 1f,
            -scale.x,  scale.y,  scale.z, 0f, 0f,
             scale.x,  scale.y,  scale.z, 1f, 0f,
             scale.x,  scale.y, -scale.z, 1f, 1f,

            -scale.x, -scale.y, -scale.z, 1f, 1f,
             scale.x, -scale.y, -scale.z, 0f, 1f,
             scale.x, -scale.y,  scale.z, 0f, 0f,
            -scale.x, -scale.y,  scale.z, 1f, 0f,

             scale.x, -scale.y, -scale.z, 1f, 0f,
             scale.x,  scale.y, -scale.z, 1f, 1f,
             scale.x,  scale.y,  scale.z, 0f, 1f,
             scale.x, -scale.y,  scale.z, 0f, 0f,

            -scale.x, -scale.y, -scale.z, 0f, 0f,
            -scale.x, -scale.y,  scale.z, 1f, 0f,
            -scale.x,  scale.y,  scale.z, 1f, 1f,
            -scale.x,  scale.y, -scale.z, 0f, 1f,
        )
        vertexBuffer.setBufferAt(engine, 0, floatArrayToByteBuffer(vertices))

        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23,
        )
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        indexBuffer.setBuffer(engine, shortArrayToByteBuffer(indices))

        return vertexBuffer to indexBuffer
    }

    private fun floatArrayToByteBuffer(values: FloatArray): java.nio.ByteBuffer {
        val buffer = java.nio.ByteBuffer.allocateDirect(values.size * java.lang.Float.BYTES)
            .order(java.nio.ByteOrder.nativeOrder())
        buffer.asFloatBuffer().put(values)
        buffer.rewind()
        return buffer
    }

    private fun shortArrayToByteBuffer(values: ShortArray): java.nio.ByteBuffer {
        val buffer = java.nio.ByteBuffer.allocateDirect(values.size * java.lang.Short.BYTES)
            .order(java.nio.ByteOrder.nativeOrder())
        buffer.asShortBuffer().put(values)
        buffer.rewind()
        return buffer
    }

    private fun removeEntity(entityData: EntityData) {
        try {
            scene.removeEntity(entityData.entity)
            engine.destroyEntity(entityData.entity)
            renderContext.entityManager.destroy(entityData.entity)
            entityData.vertexBuffer?.let { engine.destroyVertexBuffer(it) }
            entityData.indexBuffer?.let { engine.destroyIndexBuffer(it) }
        } catch (t: Throwable) {
            Log.w(TAG, "Failed removing entity", t)
        }
    }

    fun clearAll() {
        objectEntities.values.forEach { removeEntity(it) }
        objectEntities.clear()

        terrainEntities.values.forEach { removeEntity(it) }
        terrainEntities.clear()

        activeAvatarTimestamps.clear()
        avatarRenderer.clearAll()
        Log.i(TAG, "Filament world cleared")
    }

    fun destroy() {
        stopSync()
        clearAll()
        scope.cancel()
    }
}
