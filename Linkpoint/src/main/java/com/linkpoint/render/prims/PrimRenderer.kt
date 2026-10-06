package com.linkpoint.render.prims

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshLOD
import com.linkpoint.avatar.BakesOnMesh
import com.linkpoint.diagnostics.ScenePopulationDiagnostics
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.render.geometry.PrimMeshData
import com.linkpoint.render.geometry.PrimMeshGenerator
import com.linkpoint.render.geometry.PrimShape
import com.linkpoint.render.geometry.PrimShapeKey
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

/**
 * Renders Second Life primitives (prims)
 * Supports box, cylinder, sphere, torus, and sculpted prims
 */
class PrimRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "PrimRenderer"

        // Pcode types
        const val PCODE_PRIM = 9
        const val PCODE_AVATAR = 47
        const val PCODE_GRASS = 95
        const val PCODE_NEW_TREE = 111
        const val PCODE_PARTICLE = 143
        const val PCODE_TREE = 255
    }

    private val prims = ConcurrentHashMap<Int, PrimInstance>()
    // Meshes are cached by a coarse "shape key" that captures the visible
    // distinctions (path/profile curve + path/profile cuts + hollow). Path
    // taper/twist/skew aren't part of the cache key yet — they'd explode the
    // cardinality. Treat them as a uniform-scale or post-process for now.
    private val primMeshes = ConcurrentHashMap<PrimShapeKey, PrimMesh>()

    private var defaultMaterial: Material? = null
    private val transformManager = engine.transformManager
    private val pendingMeshLoads = ConcurrentHashMap<Int, UUID>()
    private val loadedMeshIds = ConcurrentHashMap<Int, UUID>()

    /**
     * Optional Bakes-on-Mesh resolver. When set, a TextureEntry face that
     * references a BoM sentinel UUID (e.g. IMG_USE_BAKED_HEAD) is rewritten
     * to the avatar's actual baked texture UUID before being requested from
     * TextureManager. Wired by RenderManager.initialize() to point at
     * AvatarManager's local-agent baker.
     */
    @Volatile
    private var bomResolver: ((Int) -> UUID?)? = null
    @Volatile
    private var meshDataRequester: MeshDataRequester? = null
    @Volatile
    private var meshGeometryBuilder: MeshGeometryBuilder? = null

    fun interface TextureBinder {
        fun bind(textureId: UUID, onLoaded: (Texture) -> Unit)
    }

    @Volatile
    private var textureBinder: TextureBinder? = null

    fun setBomResolver(resolver: ((Int) -> UUID?)?) {
        bomResolver = resolver
    }

    fun setTextureBinder(binder: TextureBinder?) {
        textureBinder = binder
    }

    fun interface MeshDataRequester {
        fun request(localId: Int, meshId: UUID, lod: MeshLOD, onResolved: (MeshLoadResult) -> Unit)
    }

    fun interface MeshGeometryBuilder {
        fun attach(entity: Int, meshData: MeshData, textureEntry: ByteArray)
    }

    sealed class MeshLoadResult {
        data class Success(val meshData: MeshData) : MeshLoadResult()
        data class ParseFailure(val reason: String? = null) : MeshLoadResult()
        data class MissingAsset(val reason: String? = null) : MeshLoadResult()
    }

    fun setMeshDataRequester(requester: MeshDataRequester?) {
        meshDataRequester = requester
    }

    fun setMeshGeometryBuilder(builder: MeshGeometryBuilder?) {
        meshGeometryBuilder = builder
    }

    /**
     * Initialize with default material
     */
    fun initialize(material: Material) {
        defaultMaterial = material
        // Pre-generate the default-shape mesh for each base shape kind so the
        // common case (no path/profile customisation) doesn't tessellate on
        // the first frame after each prim arrives.
        listOf(
            PrimShape.BOX, PrimShape.CYLINDER, PrimShape.PRISM,
            PrimShape.SPHERE, PrimShape.TORUS, PrimShape.TUBE, PrimShape.RING
        ).forEach { kind ->
            getOrCreateMesh(PrimShapeKey.defaultFor(kind))
        }
        Log.i(TAG, "PrimRenderer initialized with ${primMeshes.size} default meshes")
    }

    /**
     * Add or update a prim from ObjectUpdate
     */
    fun updatePrim(data: ObjectUpdateData): Boolean {
        if (data.pcode != PCODE_PRIM) {
            ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.OBJECT, false)
            return false
        }

        val prim = prims.getOrPut(data.localId) {
            createPrim(data)
        }

        val meshId = data.getMeshAssetId()
        if (meshId != null) {
            prim.meshId = meshId
            val requestChanged = pendingMeshLoads[data.localId] != meshId && loadedMeshIds[data.localId] != meshId
            if (requestChanged) {
                pendingMeshLoads[data.localId] = meshId
                prim.pendingLod = MeshLOD.HIGH
                logMeshResolution(
                    event = "pending_load",
                    localId = data.localId,
                    meshId = meshId
                )
                val requester = meshDataRequester
                if (requester == null) {
                    pendingMeshLoads.remove(data.localId, meshId)
                    prim.pendingLod = null
                    logMeshResolution(
                        event = "missing_asset",
                        localId = data.localId,
                        meshId = meshId,
                        detail = "mesh_requester_unavailable"
                    )
                } else {
                    val textureEntrySnapshot = data.textureEntry.copyOf()
                    requester.request(data.localId, meshId, MeshLOD.HIGH) { result ->
                        when (result) {
                            is MeshLoadResult.Success -> {
                                pendingMeshLoads.remove(data.localId, meshId)
                                loadedMeshIds[data.localId] = meshId
                                prim.activeLod = result.meshData.lod
                                prim.pendingLod = null
                                replaceGeometry(data.localId, result.meshData.lod) { entity ->
                                    val builder = meshGeometryBuilder
                                    if (builder == null) {
                                        logMeshResolution(
                                            event = "missing_asset",
                                            localId = data.localId,
                                            meshId = meshId,
                                            detail = "mesh_geometry_builder_unavailable"
                                        )
                                    } else {
                                        builder.attach(entity, result.meshData, textureEntrySnapshot)
                                    }
                                }
                            }
                            is MeshLoadResult.ParseFailure -> {
                                pendingMeshLoads.remove(data.localId, meshId)
                                prim.pendingLod = null
                                logMeshResolution(
                                    event = "parse_failure",
                                    localId = data.localId,
                                    meshId = meshId,
                                    detail = result.reason
                                )
                            }
                            is MeshLoadResult.MissingAsset -> {
                                pendingMeshLoads.remove(data.localId, meshId)
                                prim.pendingLod = null
                                logMeshResolution(
                                    event = "missing_asset",
                                    localId = data.localId,
                                    meshId = meshId,
                                    detail = result.reason
                                )
                            }
                        }
                    }
                }
            }
        }

        // Update transform
        prim.position = data.position
        prim.rotation = data.rotation
        prim.scale = data.scale
        updateTransform(prim)

        // Update material if texture changed
        if (!data.textureEntry.contentEquals(prim.textureEntry)) {
            prim.textureEntry = data.textureEntry
            updatePrimMaterial(prim, data.textureEntry)
        }

        ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.OBJECT, true)
        return true
    }

    /**
     * Remove a prim
     */
    /**
     * Replace the geometry on an existing prim entity. Used by the
     * mesh-asset path: when MeshManager finishes parsing the LLM asset,
     * RenderManager hands us the entity back so we can swap its
     * RenderableComponent for the parsed mesh primitives. Caller's
     * [attach] block is invoked with the prim's entity ID and is
     * responsible for destroying the existing renderable and building the
     * new one (we don't know the new geometry's shape from here).
     *
     * No-op if no prim with this localId is tracked.
     */
    fun replaceGeometry(localId: Int, newLod: MeshLOD? = null, attach: (entity: Int) -> Unit) {
        val prim = prims[localId] ?: return
        try {
            attach(prim.entity)
            if (newLod != null) {
                prim.activeLod = newLod
                prim.pendingLod = null
            }
        } catch (e: Exception) {
            Log.w(TAG, "replaceGeometry failed for $localId", e)
        }
    }

    /**
     * Issue an asynchronous request to fetch a new LOD level for a registered primitive.
     * Updates prim.pendingLod during the fetch, and updates prim.activeLod upon success.
     */
    fun requestLodChange(
        localId: Int,
        meshId: UUID,
        targetLod: MeshLOD,
        requester: MeshDataRequester?,
        onCompleted: (MeshLoadResult) -> Unit
    ) {
        val prim = prims[localId] ?: run {
            onCompleted(MeshLoadResult.MissingAsset("prim_not_found"))
            return
        }
        prim.pendingLod = targetLod
        val textureEntrySnapshot = prim.textureEntry.copyOf()
        requester?.request(localId, meshId, targetLod) { result ->
            when (result) {
                is MeshLoadResult.Success -> {
                    prim.activeLod = result.meshData.lod
                    prim.pendingLod = null
                    replaceGeometry(localId, result.meshData.lod) { entity ->
                        meshGeometryBuilder?.attach(entity, result.meshData, textureEntrySnapshot)
                    }
                }
                else -> {
                    prim.pendingLod = null
                }
            }
            onCompleted(result)
        } ?: run {
            prim.pendingLod = null
            onCompleted(MeshLoadResult.MissingAsset("requester_unavailable"))
        }
    }

    /**
     * Get a map of active primitive instances that are mesh assets.
     */
    fun getRegisteredMeshPrims(): Map<Int, PrimInstance> {
        return prims.filterValues { it.meshId != null }
    }

    fun removePrim(localId: Int) {
        pendingMeshLoads.remove(localId)
        loadedMeshIds.remove(localId)
        prims.remove(localId)?.let { prim ->
            scene.removeEntity(prim.entity)
            engine.destroyMaterialInstance(prim.materialInstance)
            engine.destroyEntity(prim.entity)
        }
    }

    private fun logMeshResolution(event: String, localId: Int, meshId: UUID, detail: String? = null) {
        val suffix = detail?.let { """, "detail":"$it"""" } ?: ""
        Log.i(TAG, """{"event":"mesh_asset_resolution","state":"$event","localId":$localId,"meshId":"$meshId"$suffix}""")
    }

    /**
     * Get prim by local ID
     */
    fun getPrim(localId: Int): PrimInstance? = prims[localId]

    /**
     * Get all prims
     */
    fun getAllPrims(): Collection<PrimInstance> = prims.values

    private fun createPrim(data: ObjectUpdateData): PrimInstance {
        val entity = EntityManager.get().create()

        // Use the protocol-decoded shape params instead of a hardcoded BOX.
        val shapeKey = PrimShapeKey.from(data.shapeParams)
        val mesh = getOrCreateMesh(shapeKey)

        val material = defaultMaterial?.createInstance() ?: throw IllegalStateException(
            "Default material not initialized. Call initialize() first."
        )
        material.setParameter("baseColor", 1f, 1f, 1f, 1f)
        material.setParameter("texScale", 1f, 1f)
        material.setParameter("texOffset", 0f, 0f)
        material.setParameter("texRotation", 0f)
        material.setParameter("metallic", 0f)
        material.setParameter("roughness", 0.5f)
        material.setParameter("hasTexture", 0f)

        // The base mesh is built in unit space (-0.5..0.5 cube envelope). The
        // prim's actual scale comes from the protocol's `scale` vector, so the
        // bounding box stays unit-sized here and the transform handles size.
        RenderableManager.Builder(1)
            .boundingBox(Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, mesh.vertexBuffer, mesh.indexBuffer)
            .material(0, material)
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
            .build(engine, entity)

        val ti = transformManager.create(entity)

        scene.addEntity(entity)

        return PrimInstance(
            localId = data.localId,
            fullId = data.fullId,
            entity = entity,
            transformInstance = ti,
            shape = shapeKey.kind,
            position = data.position,
            rotation = data.rotation,
            scale = data.scale,
            textureEntry = data.textureEntry,
            materialInstance = material
        )
    }

    private fun getOrCreateMesh(key: PrimShapeKey): PrimMesh {
        return primMeshes.getOrPut(key) {
            val meshData = PrimMeshGenerator.generateMesh(key)
            createMesh(meshData)
        }
    }

    private fun createMesh(meshData: PrimMeshData): PrimMesh {
        return createMesh(meshData.toInterleavedBuffer(), meshData.indices)
    }

    private fun createMesh(vertices: FloatArray, indices: ShortArray): PrimMesh {
        val stride = 8 * 4 // 3 pos + 3 normal + 2 uv, all floats
        val vertexCount = vertices.size / 8

        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
        for (v in vertices) {
            vertexData.putFloat(v)
        }
        vertexData.flip()

        val indexData = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
        for (i in indices) {
            indexData.putShort(i)
        }
        indexData.flip()

        val vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .build(engine)

        vertexBuffer.setBufferAt(engine, 0, vertexData)

        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)

        indexBuffer.setBuffer(engine, indexData)

        return PrimMesh(vertexBuffer, indexBuffer)
    }

    private fun updateTransform(prim: PrimInstance) {
        val m = FloatArray(16)

        // Build transform matrix: T * R * S
        val r = prim.rotation
        r.toMatrix(m)

        // Apply scale
        m[0] *= prim.scale.x; m[1] *= prim.scale.x; m[2] *= prim.scale.x
        m[4] *= prim.scale.y; m[5] *= prim.scale.y; m[6] *= prim.scale.y
        m[8] *= prim.scale.z; m[9] *= prim.scale.z; m[10] *= prim.scale.z

        // Apply translation
        m[12] = prim.position.x
        m[13] = prim.position.y
        m[14] = prim.position.z

        transformManager.setTransform(prim.transformInstance, m)
    }

    private fun updatePrimMaterial(prim: PrimInstance, textureEntry: ByteArray) {
        // Parse texture entry to extract texture UUIDs and material properties
        // Texture entry format:
        // - Default face texture UUID (16 bytes)
        // - Face-specific texture overrides (bitfield + UUID pairs)
        // - RGBA color
        // - Repeat U/V, Offset U/V, Rotation

        if (textureEntry.isEmpty()) return

        try {
            val buffer = java.nio.ByteBuffer.wrap(textureEntry)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)

            // Extract default texture UUID (first 16 bytes)
            if (buffer.remaining() >= 16) {
                val uuidBytes = ByteArray(16)
                buffer.get(uuidBytes)
                val defaultTextureId = bytesToUUID(uuidBytes)

                if (defaultTextureId != UUID(0, 0)) {
                    // Bakes-on-Mesh: if the prim references one of the magic
                    // bake-slot sentinels, substitute the avatar's actual
                    // baked texture UUID via the registered resolver. Logged
                    // either way so we can see how many BoM faces a typical
                    // mesh outfit references.
                    val resolved = bomResolver?.let {
                        BakesOnMesh.resolve(defaultTextureId, it)
                    } ?: defaultTextureId
                    if (resolved != defaultTextureId) {
                        Log.d(TAG, "Prim ${prim.localId} BoM face $defaultTextureId -> baked $resolved")
                    } else if (BakesOnMesh.isBakeSentinel(defaultTextureId)) {
                        Log.v(TAG, "Prim ${prim.localId} BoM sentinel $defaultTextureId — no resolver")
                    }
                    if (TextureEntryParser.shouldDownload(resolved)) {
                        textureBinder?.bind(resolved) { tex ->
                            try {
                                prim.materialInstance.setParameter(
                                    "baseColorMap", tex,
                                    TextureSampler(
                                        TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                                        TextureSampler.MagFilter.LINEAR,
                                        TextureSampler.WrapMode.REPEAT
                                    )
                                )
                                prim.materialInstance.setParameter("hasTexture", 1f)
                            } catch (e: Exception) {
                                prim.materialInstance.setParameter("hasTexture", 0f)
                                Log.w(TAG, "Failed to bind prim texture ${prim.localId} ($resolved)", e)
                            }
                        } ?: run {
                            prim.materialInstance.setParameter("hasTexture", 0f)
                        }
                    } else {
                        prim.materialInstance.setParameter("hasTexture", 0f)
                    }
                }
            }

            // Skip face-specific textures for now (complex bitfield parsing)
            // A full implementation would parse the complete texture entry

        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse texture entry for prim ${prim.localId}", e)
        }
    }

    private fun bytesToUUID(bytes: ByteArray): UUID {
        val buffer = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN)
        return UUID(buffer.long, buffer.long)
    }

    fun shutdown() {
        for (prim in prims.values) {
            scene.removeEntity(prim.entity)
            engine.destroyMaterialInstance(prim.materialInstance)
            engine.destroyEntity(prim.entity)
        }
        prims.clear()

        for (mesh in primMeshes.values) {
            engine.destroyVertexBuffer(mesh.vertexBuffer)
            engine.destroyIndexBuffer(mesh.indexBuffer)
        }
        primMeshes.clear()

    }
}

data class PrimMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)

data class PrimInstance(
    val localId: Int,
    val fullId: UUID,
    val entity: Int,
    val transformInstance: Int,
    val shape: PrimShape,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var textureEntry: ByteArray,
    val materialInstance: MaterialInstance,
    var meshId: UUID? = null,
    var activeLod: MeshLOD? = null,
    var pendingLod: MeshLOD? = null,
    var lastEvaluatedDistance: Float = 0f
)
