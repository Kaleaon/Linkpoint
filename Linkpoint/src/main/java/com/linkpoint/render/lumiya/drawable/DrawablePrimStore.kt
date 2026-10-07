package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.render.geometry.PrimMeshData
import com.linkpoint.render.geometry.PrimMeshGenerator
import com.linkpoint.render.geometry.PrimShape
import com.linkpoint.render.geometry.PrimShapeKey
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import com.linkpoint.render.materials.GlesMaterialTranslator
import com.linkpoint.render.materials.MaterialDescriptor
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all SL primitives in the scene and dispatches draw calls.
 */
class DrawablePrimStore {

    enum class ShapeKind {
        BOX, SPHERE, CYLINDER, TORUS, PRISM, RING;
    }

    /** Per-face material data. Populated from TextureEntryParser.parseFull. */
    data class FaceMaterial(
        var textureId: UUID = NULL_UUID,
        var textureHandle: Int = 0,
        var normalHandle: Int = 0,
        var metallicRoughnessHandle: Int = 0,
        var emissiveHandle: Int = 0,
        var occlusionHandle: Int = 0,
        var colorR: Float = 1f,
        var colorG: Float = 1f,
        var colorB: Float = 1f,
        var colorA: Float = 1f,
        var scaleS: Float = 1f,
        var scaleT: Float = 1f,
        var offsetS: Float = 0f,
        var offsetT: Float = 0f,
        var rotation: Float = 0f,
        var metallicFactor: Float = 0f,
        var roughnessFactor: Float = 0.5f,
        var descriptor: MaterialDescriptor? = null,
        var glow: Float = 0f
    )

    /** Per-prim instance snapshot data. */
    data class PrimInstance(
        val id: Long,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var shapeKey: PrimShapeKey = PrimShapeKey.defaultFor(PrimShape.BOX),
        var hollow: Boolean = false,
        var scaleX: Float = 1f, var scaleY: Float = 1f, var scaleZ: Float = 1f,
        val faces: MutableList<FaceMaterial> = mutableListOf(FaceMaterial()),
        var isTransparent: Boolean = false,
        var aabbHalfX: Float = 0.5f,
        var aabbHalfY: Float = 0.5f,
        var aabbHalfZ: Float = 0.5f
    ) {
        val shape: ShapeKind get() = when (shapeKey.kind) {
            PrimShape.BOX -> ShapeKind.BOX
            PrimShape.SPHERE -> ShapeKind.SPHERE
            PrimShape.CYLINDER -> ShapeKind.CYLINDER
            PrimShape.TORUS -> ShapeKind.TORUS
            PrimShape.PRISM -> ShapeKind.PRISM
            PrimShape.RING -> ShapeKind.RING
            PrimShape.TUBE -> ShapeKind.BOX
        }
    }

    companion object {
        private val NULL_UUID = UUID(0L, 0L)
        private val IDENTITY_TEX = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

        const val GLOW_THRESHOLD = 0.005f
        private const val DEFAULT_INITIAL_CAPACITY = 256
        private const val MAX_FACES_PER_PRIM = 16
    }

    // Thread-safe map of primId -> integer slot handle
    @JvmField
    internal val prims = ConcurrentHashMap<Long, Int>()

    private val lock = Any()

    // Flat primitive instance arrays indexed by integer slot handle (0 .. capacity - 1)
    private var capacity = DEFAULT_INITIAL_CAPACITY
    private var allocatedSlotCount = 0

    private var slotActive = BooleanArray(capacity)
    private var slotIds = LongArray(capacity)
    private var slotShape = IntArray(capacity) // ShapeKind ordinal
    private var slotHollow = BooleanArray(capacity)
    private var slotScaleX = FloatArray(capacity) { 1f }
    private var slotScaleY = FloatArray(capacity) { 1f }
    private var slotScaleZ = FloatArray(capacity) { 1f }
    private var slotAabbHalfX = FloatArray(capacity) { 0.5f }
    private var slotAabbHalfY = FloatArray(capacity) { 0.5f }
    private var slotAabbHalfZ = FloatArray(capacity) { 0.5f }
    private var slotIsTransparent = BooleanArray(capacity)
    private var slotModelMatrices = FloatArray(capacity * 16).also {
        for (i in 0 until capacity) {
            Matrix.setIdentityM(it, i * 16)
        }
    }

    // Per-face flat primitive arrays (size = capacity * MAX_FACES_PER_PRIM)
    private var slotFaceCount = IntArray(capacity) { 1 }
    private var faceTextureIdMsb = LongArray(capacity * MAX_FACES_PER_PRIM)
    private var faceTextureIdLsb = LongArray(capacity * MAX_FACES_PER_PRIM)
    private var faceTextureHandle = IntArray(capacity * MAX_FACES_PER_PRIM)
    private var faceNormalHandle = IntArray(capacity * MAX_FACES_PER_PRIM)
    private var faceMetallicRoughnessHandle = IntArray(capacity * MAX_FACES_PER_PRIM)
    private var faceEmissiveHandle = IntArray(capacity * MAX_FACES_PER_PRIM)
    private var faceOcclusionHandle = IntArray(capacity * MAX_FACES_PER_PRIM)
    private var faceColorR = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceColorG = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceColorB = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceColorA = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceScaleS = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceScaleT = FloatArray(capacity * MAX_FACES_PER_PRIM) { 1f }
    private var faceOffsetS = FloatArray(capacity * MAX_FACES_PER_PRIM)
    private var faceOffsetT = FloatArray(capacity * MAX_FACES_PER_PRIM)
    private var faceRotation = FloatArray(capacity * MAX_FACES_PER_PRIM)
    private var faceMetallicFactor = FloatArray(capacity * MAX_FACES_PER_PRIM)
    private var faceRoughnessFactor = FloatArray(capacity * MAX_FACES_PER_PRIM) { 0.5f }
    private var faceGlow = FloatArray(capacity * MAX_FACES_PER_PRIM)
    private var faceDescriptors = Array<MaterialDescriptor?>(capacity * MAX_FACES_PER_PRIM) { null }
    private var faceTextureBindings = Array<GlesMaterialTranslator.TextureBindings>(capacity * MAX_FACES_PER_PRIM) {
        GlesMaterialTranslator.TextureBindings()
    }

    // Stack for O(1) slot recycling
    private var freeSlots = IntArray(capacity)
    private var freeSlotCount = 0

    // Render pass integer index buckets
    private var opaqueBuckets = Array(6) { IntArray(capacity) }
    private var opaqueCounts = IntArray(6)

    private var emissiveBuckets = Array(6) { IntArray(capacity) }
    private var emissiveCounts = IntArray(6)

    // Shared shape VAOs cached by PrimShapeKey
    private val shapeVAOs = ConcurrentHashMap<PrimShapeKey, GLBufferManager.MeshVAO>()
    private var bufferManager: GLBufferManager? = null

    // ── Slot Allocation & Capacity Management ────────────────────────────

    private fun getOrAllocateSlot(id: Long): Int {
        val existing = prims[id]
        if (existing != null) return existing
        synchronized(lock) {
            val recheck = prims[id]
            if (recheck != null) return recheck
            val slot: Int
            if (freeSlotCount > 0) {
                slot = freeSlots[--freeSlotCount]
            } else {
                slot = allocatedSlotCount++
                ensureCapacity(allocatedSlotCount)
            }
            slotActive[slot] = true
            slotIds[slot] = id
            resetSlot(slot)
            prims[id] = slot
            return slot
        }
    }

    private fun resetSlot(slot: Int) {
        slotShape[slot] = ShapeKind.BOX.ordinal
        slotHollow[slot] = false
        slotScaleX[slot] = 1f
        slotScaleY[slot] = 1f
        slotScaleZ[slot] = 1f
        slotAabbHalfX[slot] = 0.5f
        slotAabbHalfY[slot] = 0.5f
        slotAabbHalfZ[slot] = 0.5f
        slotIsTransparent[slot] = false
        Matrix.setIdentityM(slotModelMatrices, slot * 16)

        slotFaceCount[slot] = 1
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until MAX_FACES_PER_PRIM) {
            val fIdx = base + i
            faceTextureIdMsb[fIdx] = 0L
            faceTextureIdLsb[fIdx] = 0L
            faceTextureHandle[fIdx] = 0
            faceNormalHandle[fIdx] = 0
            faceMetallicRoughnessHandle[fIdx] = 0
            faceEmissiveHandle[fIdx] = 0
            faceOcclusionHandle[fIdx] = 0
            faceColorR[fIdx] = 1f
            faceColorG[fIdx] = 1f
            faceColorB[fIdx] = 1f
            faceColorA[fIdx] = 1f
            faceScaleS[fIdx] = 1f
            faceScaleT[fIdx] = 1f
            faceOffsetS[fIdx] = 0f
            faceOffsetT[fIdx] = 0f
            faceRotation[fIdx] = 0f
            faceMetallicFactor[fIdx] = 0f
            faceRoughnessFactor[fIdx] = 0.5f
            faceGlow[fIdx] = 0f
            faceDescriptors[fIdx] = null
            faceTextureBindings[fIdx] = GlesMaterialTranslator.TextureBindings()
        }
        updateFaceMaterial(slot, 0)
    }

    private fun ensureCapacity(required: Int) {
        if (required <= capacity) return
        var newCap = capacity * 2
        while (newCap < required) newCap *= 2

        slotActive = slotActive.copyOf(newCap)
        slotIds = slotIds.copyOf(newCap)
        slotShape = slotShape.copyOf(newCap)
        slotHollow = slotHollow.copyOf(newCap)
        slotScaleX = slotScaleX.copyOf(newCap)
        slotScaleY = slotScaleY.copyOf(newCap)
        slotScaleZ = slotScaleZ.copyOf(newCap)
        slotAabbHalfX = slotAabbHalfX.copyOf(newCap)
        slotAabbHalfY = slotAabbHalfY.copyOf(newCap)
        slotAabbHalfZ = slotAabbHalfZ.copyOf(newCap)
        slotIsTransparent = slotIsTransparent.copyOf(newCap)

        val oldModelMat = slotModelMatrices
        slotModelMatrices = FloatArray(newCap * 16)
        System.arraycopy(oldModelMat, 0, slotModelMatrices, 0, capacity * 16)
        for (i in capacity until newCap) {
            Matrix.setIdentityM(slotModelMatrices, i * 16)
        }

        slotFaceCount = slotFaceCount.copyOf(newCap)

        val newFaceCap = newCap * MAX_FACES_PER_PRIM
        faceTextureIdMsb = faceTextureIdMsb.copyOf(newFaceCap)
        faceTextureIdLsb = faceTextureIdLsb.copyOf(newFaceCap)
        faceTextureHandle = faceTextureHandle.copyOf(newFaceCap)
        faceNormalHandle = faceNormalHandle.copyOf(newFaceCap)
        faceMetallicRoughnessHandle = faceMetallicRoughnessHandle.copyOf(newFaceCap)
        faceEmissiveHandle = faceEmissiveHandle.copyOf(newFaceCap)
        faceOcclusionHandle = faceOcclusionHandle.copyOf(newFaceCap)
        faceColorR = faceColorR.copyOf(newFaceCap)
        faceColorG = faceColorG.copyOf(newFaceCap)
        faceColorB = faceColorB.copyOf(newFaceCap)
        faceColorA = faceColorA.copyOf(newFaceCap)
        faceScaleS = faceScaleS.copyOf(newFaceCap)
        faceScaleT = faceScaleT.copyOf(newFaceCap)
        faceOffsetS = faceOffsetS.copyOf(newFaceCap)
        faceOffsetT = faceOffsetT.copyOf(newFaceCap)
        faceRotation = faceRotation.copyOf(newFaceCap)
        faceMetallicFactor = faceMetallicFactor.copyOf(newFaceCap)
        faceRoughnessFactor = faceRoughnessFactor.copyOf(newFaceCap)
        faceGlow = faceGlow.copyOf(newFaceCap)
        faceDescriptors = faceDescriptors.copyOf(newFaceCap)
        @Suppress("UNCHECKED_CAST")
        faceTextureBindings = java.util.Arrays.copyOf(faceTextureBindings, newFaceCap) as Array<GlesMaterialTranslator.TextureBindings>
        for (i in (capacity * MAX_FACES_PER_PRIM) until newFaceCap) {
            faceTextureBindings[i] = GlesMaterialTranslator.TextureBindings()
        }

        freeSlots = freeSlots.copyOf(newCap)

        for (shape in 0 until 6) {
            opaqueBuckets[shape] = opaqueBuckets[shape].copyOf(newCap)
            emissiveBuckets[shape] = emissiveBuckets[shape].copyOf(newCap)
        }
        transparentSlots = transparentSlots.copyOf(newCap)
        transparentDepths = transparentDepths.copyOf(newCap)

        capacity = newCap
    }

    // ── Mutation ─────────────────────────────────────────────────────────

    fun addPrim(id: Long, posX: Float, posY: Float, posZ: Float) {
        val existing = prims[id]
        if (existing != null) {
            Matrix.setIdentityM(existing.modelMatrix, 0)
            Matrix.translateM(existing.modelMatrix, 0, posX, posY, posZ)
            Matrix.scaleM(existing.modelMatrix, 0, existing.scaleX, existing.scaleY, existing.scaleZ)
            return
        }
        val instance = PrimInstance(id = id)
        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        prims[id] = instance
    }

    /**
     * Insert / update a prim with the full ObjectUpdate metadata.
     */
    fun upsertPrim(
        id: Long,
        posX: Float, posY: Float, posZ: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        rotation: FloatArray? = null,
        shapeParams: PrimShapeParams = PrimShapeParams.DEFAULT,
        textureEntry: ByteArray? = null
    ) {
        val instance = prims.getOrPut(id) { PrimInstance(id = id) }
        instance.shapeKey = PrimShapeKey.from(shapeParams)
        instance.hollow = shapeParams.profileHollow > 0f
        instance.scaleX = scaleX
        instance.scaleY = scaleY
        instance.scaleZ = scaleZ

        val localHalfX = scaleX * 0.5f
        val localHalfY = scaleY * 0.5f
        val localHalfZ = scaleZ * 0.5f
        if (rotation != null && rotation.size >= 16) {
            val (wx, wy, wz) = com.linkpoint.render.lumiya.spatial.SpatialEntry
                .conservativeWorldHalfExtents(localHalfX, localHalfY, localHalfZ, rotation)
            slotAabbHalfX[slot] = wx
            slotAabbHalfY[slot] = wy
            slotAabbHalfZ[slot] = wz
        } else {
            slotAabbHalfX[slot] = localHalfX
            slotAabbHalfY[slot] = localHalfY
            slotAabbHalfZ[slot] = localHalfZ
        }

        val offset = slot * 16
        Matrix.setIdentityM(slotModelMatrices, offset)
        Matrix.translateM(slotModelMatrices, offset, posX, posY, posZ)
        if (rotation != null && rotation.size >= 16) {
            val tmp = FloatArray(16)
            Matrix.multiplyMM(tmp, 0, slotModelMatrices, offset, rotation, 0)
            System.arraycopy(tmp, 0, slotModelMatrices, offset, 16)
        }
        Matrix.scaleM(slotModelMatrices, offset, scaleX, scaleY, scaleZ)
        slotModelMatrices[offset + 12] = posX
        slotModelMatrices[offset + 13] = posY
        slotModelMatrices[offset + 14] = posZ

        applyTextureEntry(slot, shape, slotHollow[slot], textureEntry)
    }

    fun removePrim(id: Long) {
        val slot = prims.remove(id) ?: return
        synchronized(lock) {
            slotActive[slot] = false
            freeSlots[freeSlotCount++] = slot
        }
    }

    fun setPrimTexture(id: Long, textureHandle: Int) {
        val slot = prims[id] ?: return
        val count = slotFaceCount[slot]
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until count) {
            val fIdx = base + i
            if (faceTextureHandle[fIdx] == 0) {
                faceTextureHandle[fIdx] = textureHandle
                updateFaceMaterial(slot, i)
            }
        }
    }

    fun setFaceTexture(id: Long, faceIndex: Int, textureHandle: Int) {
        val slot = prims[id] ?: return
        if (faceIndex < 0 || faceIndex >= slotFaceCount[slot]) return
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        faceTextureHandle[fIdx] = textureHandle
        updateFaceMaterial(slot, faceIndex)
    }

    fun getDefaultTextureId(id: Long): UUID {
        val slot = prims[id] ?: return NULL_UUID
        if (slotFaceCount[slot] == 0) return NULL_UUID
        val fIdx = slot * MAX_FACES_PER_PRIM
        return UUID(faceTextureIdMsb[fIdx], faceTextureIdLsb[fIdx])
    }

    /**
     * Patch every face whose texture ID matches [textureId]
     * to point at [textureHandle] for the specified [semantic]. Lets a single texture upload cover
     * all faces sharing that UUID, even when per-face overrides differ.
     */
    fun bindTextureToMatchingFaces(
        id: Long,
        textureId: UUID,
        textureHandle: Int,
        semantic: com.linkpoint.assets.TextureFormatPolicy.TextureSemantic = com.linkpoint.assets.TextureFormatPolicy.TextureSemantic.ALBEDO
    ) {
        val slot = prims[id] ?: return
        val msb = textureId.mostSignificantBits
        val lsb = textureId.leastSignificantBits
        val count = slotFaceCount[slot]
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until count) {
            val fIdx = base + i
            if (faceTextureIdMsb[fIdx] == msb && faceTextureIdLsb[fIdx] == lsb) {
                when (semantic) {
                    com.linkpoint.assets.TextureFormatPolicy.TextureSemantic.NORMAL -> faceNormalHandle[fIdx] = textureHandle
                    com.linkpoint.assets.TextureFormatPolicy.TextureSemantic.METALLIC_ROUGHNESS -> faceMetallicRoughnessHandle[fIdx] = textureHandle
                    com.linkpoint.assets.TextureFormatPolicy.TextureSemantic.EMISSIVE -> faceEmissiveHandle[fIdx] = textureHandle
                    com.linkpoint.assets.TextureFormatPolicy.TextureSemantic.OCCLUSION -> faceOcclusionHandle[fIdx] = textureHandle
                    else -> faceTextureHandle[fIdx] = textureHandle
                }
                updateFaceMaterial(slot, i)
            }
        }
    }

    fun setPrimTransparent(id: Long, transparent: Boolean) {
        val slot = prims[id] ?: return
        slotIsTransparent[slot] = transparent
    }

    fun setPrimTransform(id: Long, matrix4x4: FloatArray) {
        val slot = prims[id] ?: return
        if (matrix4x4.size >= 16) {
            System.arraycopy(matrix4x4, 0, slotModelMatrices, slot * 16, 16)
        }
    }

    fun primCount(): Int = prims.size

    fun snapshot(): Collection<PrimInstance> = prims.values

    fun clear() {
        prims.clear()
        synchronized(lock) {
            allocatedSlotCount = 0
            freeSlotCount = 0
            slotActive.fill(false)
        }
    }

    fun destroy() {
        prims.clear()
        shapeVAOs.values.forEach { bufferManager?.destroyVAO(it) }
        shapeVAOs.clear()
    }

    // ── Draw Passes ──────────────────────────────────────────────────────

    fun drawOpaque(
        ctx: LumiyaRenderContext,
        occlusion: com.linkpoint.render.lumiya.spatial.OcclusionQuerySet? = null
    ) {
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        val byShape = prims.values
            .filter { !it.isTransparent && primInFrustum(ctx, it) }
            .groupBy { it.shapeKey }
        for ((shapeKey, list) in byShape) {
            val vao = getOrCreateVAO(ctx, shapeKey)
            GLES32.glBindVertexArray(vao.vao)
            val bucket = opaqueBuckets[shape]
            for (i in 0 until count) {
                val slot = bucket[i]
                val id = slotIds[slot]
                occlusion?.beginQuery(id)
                drawPrimFaces(program, slot, vao.indexCount)
                occlusion?.endQuery()
            }
        }
        GLES32.glBindVertexArray(0)
    }

    fun drawTransparent(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        val sorted = prims.values
            .filter { it.isTransparent && primInFrustum(ctx, it) }
            .sortedByDescending { distanceToCamera(ctx, it) }

        var lastShapeKey: PrimShapeKey? = null
        var lastVao: GLBufferManager.MeshVAO? = null
        for (prim in sorted) {
            if (prim.shapeKey != lastShapeKey) {
                lastVao = getOrCreateVAO(ctx, prim.shapeKey)
                lastShapeKey = prim.shapeKey
                GLES32.glBindVertexArray(lastVao.vao)
            }
            transparentSlots[transparentCount] = s
            transparentDepths[transparentCount] = distanceToCamera(ctx, s)
            transparentCount++
        }
        GLES32.glBindVertexArray(0)
    }

    private fun getOrCreateVAO(ctx: LumiyaRenderContext, key: PrimShapeKey): GLBufferManager.MeshVAO {
        return shapeVAOs.getOrPut(key) {
            val bm = bufferManager ?: GLBufferManager(ctx.resourceManager).also { bufferManager = it }
            val meshData = PrimMeshGenerator.generateMesh(key)
            val interleaved = meshData.toInterleavedBuffer()
            bm.buildVAO(interleaved, meshData.indices, listOf(0 to 3, 1 to 3, 2 to 2))
        }
    }

    private fun drawPrimFaces(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        prim: PrimInstance,
        totalIndexCount: Int
    ) {
        program.setModelMatrix(prim.modelMatrix)

        val face = prim.faces.firstOrNull() ?: return
        val descriptor = face.toMaterialDescriptor()

        val texMatrix = buildTexMatrix(face)
        program.setTexMatrix(texMatrix)

        GlesMaterialTranslator.apply(
            program,
            descriptor,
            GlesMaterialTranslator.TextureBindings(
                baseColorHandle = face.textureHandle,
                normalHandle = face.normalHandle,
                metallicRoughnessHandle = face.metallicRoughnessHandle,
                emissiveHandle = face.emissiveHandle,
                occlusionHandle = face.occlusionHandle
            )
        )
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

    // ── Shape selection ──────────────────────────────────────────────────

    /**
     * Map [PrimShapeParams] to one of our six primitive shapes.
     * Mirrors LL viewer's `LLVolumeParams::getSculptType`-adjacent logic
     * for the non-sculpt prim case.
     */
    private fun shapeFromParams(p: PrimShapeParams): ShapeKind {
        val isCircularPath = (p.pathCurve and 0x30) != 0  // CIRCLE or CIRCLE2
        val profile = p.profileType
        return when {
            // PATH_CIRCLE2 + PROFILE_CIRCLE = torus
            p.pathCurve == PrimShapeParams.PATH_CIRCLE2 && profile == PrimShapeParams.PROFILE_CIRCLE -> ShapeKind.TORUS
            // PATH_CIRCLE + PROFILE_CIRCLE = sphere (default sphere)
            p.pathCurve == PrimShapeParams.PATH_CIRCLE && profile == PrimShapeParams.PROFILE_CIRCLE -> ShapeKind.SPHERE
            // PATH_CIRCLE + PROFILE_SQUARE/TRI = ring (linear sweep)
            isCircularPath && profile == PrimShapeParams.PROFILE_SQUARE -> ShapeKind.RING
            // PATH_LINE + PROFILE_CIRCLE = cylinder
            p.pathCurve == PrimShapeParams.PATH_LINE && profile == PrimShapeParams.PROFILE_CIRCLE -> ShapeKind.CYLINDER
            // PATH_LINE + PROFILE_EQUAL_TRI = prism
            p.pathCurve == PrimShapeParams.PATH_LINE && (
                profile == PrimShapeParams.PROFILE_EQUAL_TRI ||
                profile == PrimShapeParams.PROFILE_ISO_TRI ||
                profile == PrimShapeParams.PROFILE_RIGHT_TRI
            ) -> ShapeKind.PRISM
            // PATH_LINE + PROFILE_SQUARE = box (the SL default)
            else -> ShapeKind.BOX
        }
    }

    // ── Per-face material assembly ───────────────────────────────────────

    private fun applyTextureEntry(instance: PrimInstance, textureEntry: ByteArray?) {
        val faceCount = faceCountFor(instance.shapeKey.kind, instance.hollow)
        while (instance.faces.size < faceCount) instance.faces.add(FaceMaterial())
        while (instance.faces.size > faceCount) instance.faces.removeAt(instance.faces.lastIndex)

        if (textureEntry == null || textureEntry.isEmpty()) return
        val parsed = try {
            TextureEntryParser.parseFull(textureEntry, faceCount)
        } catch (t: Throwable) {
            null
        } ?: return

        for (i in 0 until faceCount) {
            val src = parsed.getOrNull(i) ?: continue
            val dst = instance.faces[i]
            if (dst.textureId != src.textureId) {
                dst.textureId = src.textureId
                dst.textureHandle = 0
            }
            dst.colorR = src.colorR
            dst.colorG = src.colorG
            dst.colorB = src.colorB
            dst.colorA = src.colorA
            dst.scaleS = src.scaleS
            dst.scaleT = src.scaleT
            dst.offsetS = src.offsetS
            dst.offsetT = src.offsetT
            dst.rotation = src.rotation
            dst.glow = src.glow
        }
        instance.isTransparent = instance.faces.any { it.colorA < 0.999f }
    }

    private fun hasEmissive(instance: PrimInstance): Boolean {
        for (face in instance.faces) {
            if (face.glow > GLOW_THRESHOLD) return true
        }
    }

    fun drawEmissive(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return

        emissiveCounts.fill(0)
        for (s in 0 until allocatedSlotCount) {
            if (!slotActive[s] || !primInFrustum(ctx, s) || !hasEmissive(s)) continue
            val shape = slotShape[s]
            val count = emissiveCounts[shape]
            if (count >= emissiveBuckets[shape].size) {
                emissiveBuckets[shape] = emissiveBuckets[shape].copyOf(emissiveBuckets[shape].size * 2)
            }
            emissiveBuckets[shape][count] = s
            emissiveCounts[shape] = count + 1
        }

        var totalEmissive = 0
        for (c in emissiveCounts) totalEmissive += c
        if (totalEmissive == 0) return

        program.use()
        program.setLighting(0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f)

        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glDepthMask(false)

        val byShape = emissivePrims.groupBy { it.shapeKey }
        for ((shapeKey, list) in byShape) {
            val vao = getOrCreateVAO(ctx, shapeKey)
            GLES32.glBindVertexArray(vao.vao)
            val bucket = emissiveBuckets[shape]
            for (i in 0 until count) {
                val slot = bucket[i]
                drawPrimEmissive(program, slot, vao.indexCount)
            }
        }
        GLES32.glBindVertexArray(0)

        GLES32.glBlendFuncSeparate(
            GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
            GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
        )
        GLES32.glDepthMask(true)
    }

    // ── Internal Helpers ─────────────────────────────────────────────────

    private fun drawPrimFaces(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        slot: Int,
        totalIndexCount: Int
    ) {
        program.setModelMatrix(slotModelMatrices, slot * 16)
        val faceIdx = slot * MAX_FACES_PER_PRIM
        buildTexMatrix(slot, 0, scratchTexMatrix)
        program.setTexMatrix(scratchTexMatrix)

        val desc = faceDescriptors[faceIdx] ?: buildFaceDescriptor(slot, 0)
        val bindings = faceTextureBindings[faceIdx]

        GlesMaterialTranslator.apply(program, desc, bindings)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

    private fun drawPrimEmissive(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        slot: Int,
        totalIndexCount: Int
    ) {
        val face = prim.faces.firstOrNull { it.glow > GLOW_THRESHOLD } ?: return
        program.setModelMatrix(prim.modelMatrix)
        program.setTexMatrix(buildTexMatrix(face))

        val g = face.glow
        program.setColor(face.colorR * g, face.colorG * g, face.colorB * g, face.colorA * g)
        if (face.textureHandle != 0) {
            program.setUseTexture(true)
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle)
            program.setTextureSampler(0)
        } else {
            program.setUseTexture(false)
        }
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

<<<<<<< HEAD
    private fun quickSortTransparent(low: Int, high: Int) {
        if (low >= high) return
        val pivot = transparentDepths[(low + high) / 2]
        var i = low
        var j = high
        while (i <= j) {
            while (transparentDepths[i] > pivot) i++
            while (transparentDepths[j] < pivot) j--
            if (i <= j) {
                val tmpDepth = transparentDepths[i]
                transparentDepths[i] = transparentDepths[j]
                transparentDepths[j] = tmpDepth

                val tmpSlot = transparentSlots[i]
                transparentSlots[i] = transparentSlots[j]
                transparentSlots[j] = tmpSlot

                i++
                j--
            }
        }
        if (low < j) quickSortTransparent(low, j)
        if (i < high) quickSortTransparent(i, high)
    }

    private fun shapeFromParams(p: PrimShapeParams): ShapeKind {
        val isCircularPath = (p.pathCurve and 0x30) != 0
        val profile = p.profileType
        return when {
            p.pathCurve == PrimShapeParams.PATH_CIRCLE2 && (profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE) -> ShapeKind.TORUS
            p.pathCurve == PrimShapeParams.PATH_CIRCLE && (profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE) -> ShapeKind.SPHERE
            isCircularPath && profile == PrimShapeParams.PROFILE_SQUARE -> ShapeKind.RING
            p.pathCurve == PrimShapeParams.PATH_LINE && profile == PrimShapeParams.PROFILE_CIRCLE -> ShapeKind.CYLINDER
            p.pathCurve == PrimShapeParams.PATH_LINE && (
                profile == PrimShapeParams.PROFILE_EQUAL_TRI ||
                profile == PrimShapeParams.PROFILE_ISO_TRI ||
                profile == PrimShapeParams.PROFILE_RIGHT_TRI
            ) -> ShapeKind.PRISM
            else -> ShapeKind.BOX
        }
    }

    private fun applyTextureEntry(slot: Int, shape: ShapeKind, hollow: Boolean, textureEntry: ByteArray?) {
        val faceCount = faceCountFor(shape, hollow).coerceAtMost(MAX_FACES_PER_PRIM)
        slotFaceCount[slot] = faceCount

        if (textureEntry != null && textureEntry.isNotEmpty()) {
            val parsed = try {
                TextureEntryParser.parseFull(textureEntry, faceCount)
            } catch (t: Throwable) {
                null
            }
            if (parsed != null) {
                val base = slot * MAX_FACES_PER_PRIM
                var hasAlpha = false
                for (i in 0 until faceCount) {
                    val src = parsed.getOrNull(i) ?: continue
                    val fIdx = base + i
                    val msb = src.textureId.mostSignificantBits
                    val lsb = src.textureId.leastSignificantBits
                    if (faceTextureIdMsb[fIdx] != msb || faceTextureIdLsb[fIdx] != lsb) {
                        faceTextureIdMsb[fIdx] = msb
                        faceTextureIdLsb[fIdx] = lsb
                        faceTextureHandle[fIdx] = 0
                    }
                    faceColorR[fIdx] = src.colorR
                    faceColorG[fIdx] = src.colorG
                    faceColorB[fIdx] = src.colorB
                    faceColorA[fIdx] = src.colorA
                    faceScaleS[fIdx] = src.scaleS
                    faceScaleT[fIdx] = src.scaleT
                    faceOffsetS[fIdx] = src.offsetS
                    faceOffsetT[fIdx] = src.offsetT
                    faceRotation[fIdx] = src.rotation
                    faceGlow[fIdx] = src.glow

                    if (src.colorA < 0.999f) hasAlpha = true
                    updateFaceMaterial(slot, i)
                }
                slotIsTransparent[slot] = hasAlpha
                return
            }
        }

        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until faceCount) {
            updateFaceMaterial(slot, i)
        }
    }

    private fun updateFaceMaterial(slot: Int, faceIndex: Int) {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        faceDescriptors[fIdx] = buildFaceDescriptor(slot, faceIndex)
        faceTextureBindings[fIdx] = GlesMaterialTranslator.TextureBindings(
            baseColorHandle = faceTextureHandle[fIdx],
            normalHandle = faceNormalHandle[fIdx],
            metallicRoughnessHandle = faceMetallicRoughnessHandle[fIdx],
            emissiveHandle = faceEmissiveHandle[fIdx],
            occlusionHandle = faceOcclusionHandle[fIdx]
        )
    }

    private fun buildFaceDescriptor(slot: Int, faceIndex: Int): MaterialDescriptor {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        val handle = faceTextureHandle[fIdx]
        val texId = UUID(faceTextureIdMsb[fIdx], faceTextureIdLsb[fIdx])
        val glow = faceGlow[fIdx]
        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(
                faceColorR[fIdx],
                faceColorG[fIdx],
                faceColorB[fIdx],
                faceColorA[fIdx]
            ),
            baseColorTexture = if (handle != 0) {
                MaterialDescriptor.TextureRef(
                    texId,
                    texId,
                    isDownloadable = true
                )
            } else null,
            metallicFactor = faceMetallicFactor[fIdx],
            roughnessFactor = faceRoughnessFactor[fIdx],
            emissiveFactor = if (glow > 0f) MaterialDescriptor.Float3(glow, glow, glow) else MaterialDescriptor.Float3.ZERO,
            uvTransform = MaterialDescriptor.UvTransform(
                faceScaleS[fIdx],
                faceScaleT[fIdx],
                faceOffsetS[fIdx],
                faceOffsetT[fIdx],
                faceRotation[fIdx]
            )
        )
    }

    private fun hasEmissive(slot: Int): Boolean {
        val count = slotFaceCount[slot]
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until count) {
            if (faceGlow[base + i] > GLOW_THRESHOLD) return true
        }
        return false
    }

    private fun faceCountFor(shape: ShapeKind, hollow: Boolean): Int {
=======
    private fun faceCountFor(shape: PrimShape, hollow: Boolean): Int {
>>>>>>> 4387b98c4 (fix(render): align DrawablePrimStore bindTextureToMatchingFaces for PBR semantics)
        val (sides, caps) = when (shape) {
            PrimShape.BOX -> 4 to 2
            PrimShape.SPHERE -> 1 to 0
            PrimShape.CYLINDER -> 1 to 2
            PrimShape.PRISM -> 3 to 2
            PrimShape.TORUS -> 1 to 0
            PrimShape.RING -> 2 to 1
            PrimShape.TUBE -> 4 to 2
        }
        val effectiveCaps = if (hollow) caps * 2 else caps
        return sides + effectiveCaps
    }

    private fun buildTexMatrix(face: FaceMaterial): FloatArray {
        val m = FloatArray(16)
        Matrix.setIdentityM(m, 0)
        Matrix.translateM(m, 0, 0.5f + face.offsetS, 0.5f + face.offsetT, 0f)
        if (face.rotation != 0f) {
            Matrix.rotateM(m, 0, Math.toDegrees(face.rotation.toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(outMatrix, 0, scS, scT, 1f)
        Matrix.translateM(outMatrix, 0, -0.5f, -0.5f, 0f)
    }

    private fun FaceMaterial.toMaterialDescriptor(): MaterialDescriptor {
        return descriptor ?: MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(colorR, colorG, colorB, colorA),
            baseColorTexture = if (textureHandle != 0) {
                MaterialDescriptor.TextureRef(
                    textureId,
                    textureId,
                    isDownloadable = true
                )
            } else null,
            metallicFactor = metallicFactor,
            roughnessFactor = roughnessFactor,
            emissiveFactor = if (glow > 0f) MaterialDescriptor.Float3(glow, glow, glow) else MaterialDescriptor.Float3.ZERO,
            uvTransform = MaterialDescriptor.UvTransform(scaleS, scaleT, offsetS, offsetT, rotation)
        )
    }

    private fun distanceToCamera(ctx: LumiyaRenderContext, prim: PrimInstance): Float {
        val dx = prim.modelMatrix[12] - ctx.cameraPositionX
        val dy = prim.modelMatrix[13] - ctx.cameraPositionY
        val dz = prim.modelMatrix[14] - ctx.cameraPositionZ
        return dx * dx + dy * dy + dz * dz
    }

    private fun primInFrustum(ctx: LumiyaRenderContext, slot: Int): Boolean {
        val offset = slot * 16
        val cx = slotModelMatrices[offset + 12]
        val cy = slotModelMatrices[offset + 13]
        val cz = slotModelMatrices[offset + 14]
        val hx = slotAabbHalfX[slot]
        val hy = slotAabbHalfY[slot]
        val hz = slotAabbHalfZ[slot]
        return ctx.frustumCuller.isAABBVisible(
            cx - hx, cy - hy, cz - hz,
            cx + hx, cy + hy, cz + hz
        )
    }
}
