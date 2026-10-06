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

    private val instanceSnapshots = ConcurrentHashMap<Long, PrimInstance>()

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

    private var transparentSlots = IntArray(capacity)
    private var transparentDepths = FloatArray(capacity)
    private var transparentCount = 0

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
        val slot = getOrAllocateSlot(id)
        val offset = slot * 16
        Matrix.setIdentityM(slotModelMatrices, offset)
        Matrix.translateM(slotModelMatrices, offset, posX, posY, posZ)

        val inst = instanceSnapshots.getOrPut(id) { PrimInstance(id = id) }
        System.arraycopy(slotModelMatrices, offset, inst.modelMatrix, 0, 16)
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
        val slot = getOrAllocateSlot(id)
        slotScaleX[slot] = scaleX
        slotScaleY[slot] = scaleY
        slotScaleZ[slot] = scaleZ

        val localHalfX = scaleX * 0.5f
        val localHalfY = scaleY * 0.5f
        val localHalfZ = scaleZ * 0.5f
        slotAabbHalfX[slot] = localHalfX
        slotAabbHalfY[slot] = localHalfY
        slotAabbHalfZ[slot] = localHalfZ

        val offset = slot * 16
        Matrix.setIdentityM(slotModelMatrices, offset)
        Matrix.translateM(slotModelMatrices, offset, posX, posY, posZ)
        if (rotation != null && rotation.size >= 16) {
            val tmp = FloatArray(16)
            Matrix.multiplyMM(tmp, 0, slotModelMatrices, offset, rotation, 0)
            System.arraycopy(tmp, 0, slotModelMatrices, offset, 16)
        }
        Matrix.scaleM(slotModelMatrices, offset, scaleX, scaleY, scaleZ)

        val kind = shapeFromParams(shapeParams)
        val primShape = when (kind) {
            ShapeKind.BOX -> PrimShape.BOX
            ShapeKind.SPHERE -> PrimShape.SPHERE
            ShapeKind.CYLINDER -> PrimShape.CYLINDER
            ShapeKind.TORUS -> PrimShape.TORUS
            ShapeKind.PRISM -> PrimShape.PRISM
            ShapeKind.RING -> PrimShape.RING
        }
        val key = PrimShapeKey.defaultFor(primShape)
        slotShape[slot] = kind.ordinal

        applyTextureEntry(slot, shapeParams, shapeParams.profileHollow > 0f, textureEntry)

        val inst = instanceSnapshots.getOrPut(id) { PrimInstance(id = id) }
        inst.shapeKey = key
        inst.hollow = shapeParams.profileHollow > 0f
        inst.scaleX = scaleX
        inst.scaleY = scaleY
        inst.scaleZ = scaleZ
        inst.isTransparent = slotIsTransparent[slot]
        System.arraycopy(slotModelMatrices, offset, inst.modelMatrix, 0, 16)
    }

    fun removePrim(id: Long) {
        val slot = prims.remove(id) ?: return
        instanceSnapshots.remove(id)
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
        instanceSnapshots[id]?.faces?.firstOrNull()?.textureHandle = textureHandle
    }

    fun setFaceTexture(id: Long, faceIndex: Int, textureHandle: Int) {
        val slot = prims[id] ?: return
        if (faceIndex < 0 || faceIndex >= slotFaceCount[slot]) return
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        faceTextureHandle[fIdx] = textureHandle
        updateFaceMaterial(slot, faceIndex)

        instanceSnapshots[id]?.let { inst ->
            if (faceIndex < inst.faces.size) {
                inst.faces[faceIndex].textureHandle = textureHandle
            }
        }
    }

    fun getDefaultTextureId(id: Long): UUID {
        val slot = prims[id] ?: return NULL_UUID
        if (slotFaceCount[slot] == 0) return NULL_UUID
        val fIdx = slot * MAX_FACES_PER_PRIM
        return UUID(faceTextureIdMsb[fIdx], faceTextureIdLsb[fIdx])
    }

    fun bindTextureToMatchingFaces(id: Long, textureId: UUID, textureHandle: Int) {
        val slot = prims[id] ?: return
        val msb = textureId.mostSignificantBits
        val lsb = textureId.leastSignificantBits
        val count = slotFaceCount[slot]
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until count) {
            val fIdx = base + i
            if (faceTextureIdMsb[fIdx] == msb && faceTextureIdLsb[fIdx] == lsb) {
                faceTextureHandle[fIdx] = textureHandle
                updateFaceMaterial(slot, i)
            }
        }
    }

    fun setPrimTransparent(id: Long, transparent: Boolean) {
        val slot = prims[id] ?: return
        slotIsTransparent[slot] = transparent
        instanceSnapshots[id]?.isTransparent = transparent
    }

    fun setPrimTransform(id: Long, matrix4x4: FloatArray) {
        val slot = prims[id] ?: return
        if (matrix4x4.size >= 16) {
            System.arraycopy(matrix4x4, 0, slotModelMatrices, slot * 16, 16)
            instanceSnapshots[id]?.let { inst ->
                System.arraycopy(matrix4x4, 0, inst.modelMatrix, 0, 16)
            }
        }
    }

    fun primCount(): Int = prims.size

    fun snapshot(): Collection<PrimInstance> {
        val list = mutableListOf<PrimInstance>()
        for (slot in 0 until allocatedSlotCount) {
            if (slotActive[slot]) {
                val id = slotIds[slot]
                val inst = instanceSnapshots.getOrPut(id) { PrimInstance(id = id) }
                inst.isTransparent = slotIsTransparent[slot]
                System.arraycopy(slotModelMatrices, slot * 16, inst.modelMatrix, 0, 16)
                list.add(inst)
            }
        }
        return list
    }

    fun clear() {
        prims.clear()
        instanceSnapshots.clear()
        allocatedSlotCount = 0
        freeSlotCount = 0
    }

    fun destroy() {
        clear()
        shapeVAOs.clear()
        bufferManager = null
    }

    // ── Drawing ──────────────────────────────────────────────────────────

    fun drawOpaque(ctx: LumiyaRenderContext, occlusion: Any? = null) {
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        for (slot in 0 until allocatedSlotCount) {
            if (!slotActive[slot] || slotIsTransparent[slot] || !primInFrustum(ctx, slot)) continue
            val shapeKind = PrimShape.entries.getOrElse(slotShape[slot]) { PrimShape.BOX }
            val vao = getOrCreateVAO(ctx, PrimShapeKey.defaultFor(shapeKind))
            GLES32.glBindVertexArray(vao.vao)
            drawPrimFaces(program, slot, vao.indexCount)
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

        for (slot in 0 until allocatedSlotCount) {
            if (!slotActive[slot] || !slotIsTransparent[slot] || !primInFrustum(ctx, slot)) continue
            val shapeKind = PrimShape.entries.getOrElse(slotShape[slot]) { PrimShape.BOX }
            val vao = getOrCreateVAO(ctx, PrimShapeKey.defaultFor(shapeKind))
            GLES32.glBindVertexArray(vao.vao)
            drawPrimFaces(program, slot, vao.indexCount)
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
        slot: Int,
        totalIndexCount: Int
    ) {
        program.setModelMatrix(slotModelMatrices, slot * 16)
        val faceIdx = slot * MAX_FACES_PER_PRIM
        buildTexMatrix(slot, 0, IDENTITY_TEX)
        program.setTexMatrix(IDENTITY_TEX)

        val desc = faceDescriptors[faceIdx] ?: buildFaceDescriptor(slot, 0)
        val bindings = faceTextureBindings[faceIdx]

        GlesMaterialTranslator.apply(program, desc, bindings)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

    // ── Shape selection ──────────────────────────────────────────────────

    private fun shapeFromParams(p: PrimShapeParams): ShapeKind {
        val isCircularPath = (p.pathCurve and 0x30) != 0
        val profile = p.profileType
        return when {
            p.pathCurve == PrimShapeParams.PATH_CIRCLE2 && (profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE) -> ShapeKind.TORUS
            p.pathCurve == PrimShapeParams.PATH_CIRCLE && (profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE) -> ShapeKind.SPHERE
            isCircularPath && profile == PrimShapeParams.PROFILE_SQUARE -> ShapeKind.RING
            p.pathCurve == PrimShapeParams.PATH_LINE && (profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE) -> ShapeKind.CYLINDER
            p.pathCurve == PrimShapeParams.PATH_LINE && (
                profile == PrimShapeParams.PROFILE_EQUAL_TRI ||
                profile == PrimShapeParams.PROFILE_ISO_TRI ||
                profile == PrimShapeParams.PROFILE_RIGHT_TRI
            ) -> ShapeKind.PRISM
            else -> ShapeKind.BOX
        }
    }

    // ── Per-face material assembly ───────────────────────────────────────

    private fun applyTextureEntry(slot: Int, shapeParams: PrimShapeParams, hollow: Boolean, textureEntry: ByteArray?) {
        val shapeKind = shapeFromParams(shapeParams)
        val faceCount = faceCountFor(PrimShape.entries.getOrElse(shapeKind.ordinal) { PrimShape.BOX }, hollow)
        slotFaceCount[slot] = faceCount
        if (textureEntry == null || textureEntry.isEmpty()) return
        val parsed = try {
            TextureEntryParser.parseFull(textureEntry, faceCount)
        } catch (_: Throwable) {
            null
        } ?: return
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until faceCount) {
            val src = parsed.getOrNull(i) ?: continue
            val fIdx = base + i
            faceTextureIdMsb[fIdx] = src.textureId.mostSignificantBits
            faceTextureIdLsb[fIdx] = src.textureId.leastSignificantBits
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
            updateFaceMaterial(slot, i)
        }
    }

    private fun updateFaceMaterial(slot: Int, faceIndex: Int) {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        faceDescriptors[fIdx] = buildFaceDescriptor(slot, faceIndex)
    }

    private fun buildFaceDescriptor(slot: Int, faceIndex: Int): MaterialDescriptor {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        val r = faceColorR[fIdx]
        val g = faceColorG[fIdx]
        val b = faceColorB[fIdx]
        val a = faceColorA[fIdx]
        val glow = faceGlow[fIdx]
        val msb = faceTextureIdMsb[fIdx]
        val lsb = faceTextureIdLsb[fIdx]
        val textureId = UUID(msb, lsb)
        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(r, g, b, a),
            baseColorTexture = if (faceTextureHandle[fIdx] != 0) {
                MaterialDescriptor.TextureRef(
                    textureId,
                    textureId,
                    isDownloadable = true
                )
            } else null,
            metallicFactor = faceMetallicFactor[fIdx],
            roughnessFactor = faceRoughnessFactor[fIdx],
            emissiveFactor = if (glow > 0f) MaterialDescriptor.Float3(glow, glow, glow) else MaterialDescriptor.Float3.ZERO,
            uvTransform = MaterialDescriptor.UvTransform(faceScaleS[fIdx], faceScaleT[fIdx], faceOffsetS[fIdx], faceOffsetT[fIdx], faceRotation[fIdx])
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

    fun drawEmissive(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return
        for (slot in 0 until allocatedSlotCount) {
            if (!slotActive[slot] || !primInFrustum(ctx, slot) || !hasEmissive(slot)) continue
            val shapeKind = PrimShape.entries.getOrElse(slotShape[slot]) { PrimShape.BOX }
            val vao = getOrCreateVAO(ctx, PrimShapeKey.defaultFor(shapeKind))
            GLES32.glBindVertexArray(vao.vao)
            drawPrimEmissive(program, slot, vao.indexCount)
        }
        GLES32.glBindVertexArray(0)
    }

    private fun drawPrimEmissive(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        slot: Int,
        totalIndexCount: Int
    ) {
        drawPrimFaces(program, slot, totalIndexCount)
    }

    private fun faceCountFor(shape: PrimShape, hollow: Boolean): Int {
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

    private fun buildTexMatrix(slot: Int, faceIndex: Int, outMatrix: FloatArray): FloatArray {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        Matrix.setIdentityM(outMatrix, 0)
        Matrix.translateM(outMatrix, 0, 0.5f + faceOffsetS[fIdx], 0.5f + faceOffsetT[fIdx], 0f)
        if (faceRotation[fIdx] != 0f) {
            Matrix.rotateM(outMatrix, 0, Math.toDegrees(faceRotation[fIdx].toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(outMatrix, 0, faceScaleS[fIdx], faceScaleT[fIdx], 1f)
        Matrix.translateM(outMatrix, 0, -0.5f, -0.5f, 0f)
        return outMatrix
    }

    private fun distanceToCamera(ctx: LumiyaRenderContext, slot: Int): Float {
        val offset = slot * 16
        val dx = slotModelMatrices[offset + 12] - ctx.cameraPositionX
        val dy = slotModelMatrices[offset + 13] - ctx.cameraPositionY
        val dz = slotModelMatrices[offset + 14] - ctx.cameraPositionZ
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

    private fun quickSortTransparent(low: Int, high: Int) {
        if (low < high) {
            val pivot = transparentDepths[high]
            var i = low - 1
            for (j in low until high) {
                if (transparentDepths[j] >= pivot) {
                    i++
                    val tmpS = transparentSlots[i]
                    transparentSlots[i] = transparentSlots[j]
                    transparentSlots[j] = tmpS
                    val tmpD = transparentDepths[i]
                    transparentDepths[i] = transparentDepths[j]
                    transparentDepths[j] = tmpD
                }
            }
            val tmpS = transparentSlots[i + 1]
            transparentSlots[i + 1] = transparentSlots[high]
            transparentSlots[high] = tmpS
            val tmpD = transparentDepths[i + 1]
            transparentDepths[i + 1] = transparentDepths[high]
            transparentDepths[high] = tmpD
            val pi = i + 1
            quickSortTransparent(low, pi - 1)
            quickSortTransparent(pi + 1, high)
        }
    }
}
