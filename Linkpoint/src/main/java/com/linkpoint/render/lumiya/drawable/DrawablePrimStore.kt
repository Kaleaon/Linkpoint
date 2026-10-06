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
        var shape: ShapeKind = ShapeKind.BOX,
        var hollow: Boolean = false,
        var scaleX: Float = 1f, var scaleY: Float = 1f, var scaleZ: Float = 1f,
        val faces: MutableList<FaceMaterial> = mutableListOf(FaceMaterial()),
        var isTransparent: Boolean = false,
        var aabbHalfX: Float = 0.5f,
        var aabbHalfY: Float = 0.5f,
        var aabbHalfZ: Float = 0.5f
    )

    companion object {
        private val NULL_UUID = UUID(0L, 0L)
        private val IDENTITY_TEX = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

        const val GLOW_THRESHOLD = 0.005f
        private const val DEFAULT_INITIAL_CAPACITY = 256
        private const val MAX_FACES_PER_PRIM = 16
    }

    /** Key for grouping identical face material properties for instanced batching. */
    data class MaterialBatchKey(
        val textureHandle: Int,
        val normalHandle: Int,
        val metallicRoughnessHandle: Int,
        val emissiveHandle: Int,
        val occlusionHandle: Int,
        val colorR: Float,
        val colorG: Float,
        val colorB: Float,
        val colorA: Float,
        val scaleS: Float,
        val scaleT: Float,
        val offsetS: Float,
        val offsetT: Float,
        val rotation: Float,
        val metallicFactor: Float,
        val roughnessFactor: Float,
        val glow: Float
    )

    data class InstancedBatchKey(
        val shape: ShapeKind,
        val material: MaterialBatchKey
    )

    /**
     * Reusable pre-allocated dynamic VBO pool for streaming instance transform matrices
     * with zero per-frame runtime allocations.
     */
    class InstanceVboPool(private var resourceManager: com.linkpoint.render.lumiya.glres.GLResourceManager? = null) {
        private var vboHandle: Int = 0
        private var capacityInstances: Int = 1024
        var floatBuffer: java.nio.FloatBuffer = java.nio.ByteBuffer.allocateDirect(capacityInstances * 16 * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            private set

        fun setResourceManager(rm: com.linkpoint.render.lumiya.glres.GLResourceManager) {
            this.resourceManager = rm
        }

        fun getVboHandle(): Int {
            val rm = resourceManager
            if (vboHandle == 0 && rm != null) {
                vboHandle = rm.createBuffer()
            }
            return vboHandle
        }

        fun ensureCapacity(requiredInstances: Int) {
            if (requiredInstances > capacityInstances) {
                capacityInstances = (requiredInstances * 1.5).toInt().coerceAtLeast(1024)
                floatBuffer = java.nio.ByteBuffer.allocateDirect(capacityInstances * 16 * 4)
                    .order(java.nio.ByteOrder.nativeOrder())
                    .asFloatBuffer()
            }
        }

        fun destroy() {
            if (vboHandle != 0) {
                resourceManager?.deleteBuffer(vboHandle)
                vboHandle = 0
            }
        }
    }

    private val instanceVboPool = InstanceVboPool()
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

    private var transparentSlots = IntArray(capacity)
    private var transparentDepths = FloatArray(capacity)
    private var transparentCount = 0

    // Scratch matrices for zero-allocation GL matrix uniforms
    private val scratchTexMatrix = FloatArray(16)

    // Shared shape VAOs
    private var shapeVAOs: Map<ShapeKind, GLBufferManager.MeshVAO>? = null
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
        Matrix.scaleM(slotModelMatrices, offset, slotScaleX[slot], slotScaleY[slot], slotScaleZ[slot])
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
        val shape = shapeFromParams(shapeParams)
        slotShape[slot] = shape.ordinal
        slotHollow[slot] = shapeParams.profileHollow > 0f
        slotScaleX[slot] = scaleX
        slotScaleY[slot] = scaleY
        slotScaleZ[slot] = scaleZ

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
    }

    fun setPrimTransform(id: Long, matrix4x4: FloatArray) {
        val slot = prims[id] ?: return
        if (matrix4x4.size >= 16) {
            System.arraycopy(matrix4x4, 0, slotModelMatrices, slot * 16, 16)
        }
    }

    fun primCount(): Int = prims.size

    /** Snapshot of all live prims (for picking + culling queries outside render loop). */
    fun snapshot(): Collection<PrimInstance> {
        val list = ArrayList<PrimInstance>(prims.size)
        for ((id, slot) in prims) {
            if (!slotActive[slot]) continue
            val shape = ShapeKind.values()[slotShape[slot]]
            val instance = PrimInstance(
                id = id,
                shape = shape,
                hollow = slotHollow[slot],
                scaleX = slotScaleX[slot],
                scaleY = slotScaleY[slot],
                scaleZ = slotScaleZ[slot],
                isTransparent = slotIsTransparent[slot],
                aabbHalfX = slotAabbHalfX[slot],
                aabbHalfY = slotAabbHalfY[slot],
                aabbHalfZ = slotAabbHalfZ[slot]
            )
            System.arraycopy(slotModelMatrices, slot * 16, instance.modelMatrix, 0, 16)
            val faceCount = slotFaceCount[slot]
            instance.faces.clear()
            val base = slot * MAX_FACES_PER_PRIM
            for (f in 0 until faceCount) {
                val fIdx = base + f
                val face = FaceMaterial(
                    textureId = UUID(faceTextureIdMsb[fIdx], faceTextureIdLsb[fIdx]),
                    textureHandle = faceTextureHandle[fIdx],
                    normalHandle = faceNormalHandle[fIdx],
                    metallicRoughnessHandle = faceMetallicRoughnessHandle[fIdx],
                    emissiveHandle = faceEmissiveHandle[fIdx],
                    occlusionHandle = faceOcclusionHandle[fIdx],
                    colorR = faceColorR[fIdx],
                    colorG = faceColorG[fIdx],
                    colorB = faceColorB[fIdx],
                    colorA = faceColorA[fIdx],
                    scaleS = faceScaleS[fIdx],
                    scaleT = faceScaleT[fIdx],
                    offsetS = faceOffsetS[fIdx],
                    offsetT = faceOffsetT[fIdx],
                    rotation = faceRotation[fIdx],
                    metallicFactor = faceMetallicFactor[fIdx],
                    roughnessFactor = faceRoughnessFactor[fIdx],
                    descriptor = faceDescriptors[fIdx],
                    glow = faceGlow[fIdx]
                )
                instance.faces.add(face)
            }
            list.add(instance)
        }
        return list
    }

    fun clear() {
        prims.clear()
        synchronized(lock) {
            allocatedSlotCount = 0
            freeSlotCount = 0
            slotActive.fill(false)
        }
    }

    fun destroy() {
        clear()
        shapeVAOs?.values?.forEach { bufferManager?.destroyVAO(it) }
        shapeVAOs = null
        instanceVboPool.destroy()
    }

    // ── Draw Passes ──────────────────────────────────────────────────────

    private fun getMaterialBatchKey(slot: Int, faceIndex: Int = 0): MaterialBatchKey {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        return MaterialBatchKey(
            faceTextureHandle[fIdx],
            faceNormalHandle[fIdx],
            faceMetallicRoughnessHandle[fIdx],
            faceEmissiveHandle[fIdx],
            faceOcclusionHandle[fIdx],
            faceColorR[fIdx],
            faceColorG[fIdx],
            faceColorB[fIdx],
            faceColorA[fIdx],
            faceScaleS[fIdx],
            faceScaleT[fIdx],
            faceOffsetS[fIdx],
            faceOffsetT[fIdx],
            faceRotation[fIdx],
            faceMetallicFactor[fIdx],
            faceRoughnessFactor[fIdx],
            faceGlow[fIdx]
        )
    }

    fun drawOpaque(
        ctx: LumiyaRenderContext,
        occlusion: com.linkpoint.render.lumiya.spatial.OcclusionQuerySet? = null
    ) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        val bm = bufferManager ?: GLBufferManager(ctx.resourceManager).also { bufferManager = it }
        instanceVboPool.setResourceManager(ctx.resourceManager)

        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        opaqueCounts.fill(0)
        for (s in 0 until allocatedSlotCount) {
            if (!slotActive[s] || slotIsTransparent[s] || !primInFrustum(ctx, s)) continue
            val id = slotIds[s]
            if (occlusion != null && !occlusion.shouldDraw(id)) continue
            val shape = slotShape[s]
            val count = opaqueCounts[shape]
            if (count >= opaqueBuckets[shape].size) {
                opaqueBuckets[shape] = opaqueBuckets[shape].copyOf(opaqueBuckets[shape].size * 2)
            }
            opaqueBuckets[shape][count] = s
            opaqueCounts[shape] = count + 1
        }

        for (shape in 0 until 6) {
            val count = opaqueCounts[shape]
            if (count == 0) continue
            val shapeKind = ShapeKind.values()[shape]
            val vao = shapeVAOs?.get(shapeKind) ?: continue
            GLES32.glBindVertexArray(vao.vao)
            val bucket = opaqueBuckets[shape]
            val grouped = (0 until count).groupBy { i ->
                val s = bucket[i]
                getMaterialBatchKey(s, 0)
            }

            for ((_, indices) in grouped) {
                if (indices.size == 1) {
                    program.setUseInstancing(false)
                    val s = bucket[indices[0]]
                    val id = slotIds[s]
                    if (occlusion != null) occlusion.beginQuery(id)
                    drawPrimFaces(program, s, vao.indexCount)
                    if (occlusion != null) occlusion.endQuery()
                } else {
                    program.setUseInstancing(true)
                    val firstSlot = bucket[indices[0]]
                    val firstFaceIdx = firstSlot * MAX_FACES_PER_PRIM

                    val desc = faceDescriptors[firstFaceIdx] ?: buildFaceDescriptor(firstSlot, 0)
                    buildTexMatrix(firstSlot, 0, scratchTexMatrix)
                    program.setTexMatrix(scratchTexMatrix)
                    GlesMaterialTranslator.apply(program, desc, faceTextureBindings[firstFaceIdx])

                    val groupSize = indices.size
                    instanceVboPool.ensureCapacity(groupSize)
                    val floatBuffer = instanceVboPool.floatBuffer
                    floatBuffer.clear()
                    for (idx in indices) {
                        val s = bucket[idx]
                        val id = slotIds[s]
                        if (occlusion != null) occlusion.beginQuery(id)
                        floatBuffer.put(slotModelMatrices, s * 16, 16)
                        if (occlusion != null) occlusion.endQuery()
                    }
                    floatBuffer.flip()

                    val instanceVbo = instanceVboPool.getVboHandle()
                    GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, instanceVbo)
                    GLES32.glBufferData(
                        GLES32.GL_ARRAY_BUFFER,
                        groupSize * 16 * 4,
                        floatBuffer,
                        GLES32.GL_DYNAMIC_DRAW
                    )

                    bm.setupInstanceMatrixAttributes(instanceVbo, baseLocation = 3, divisor = 1)

                    GLES32.glDrawElementsInstanced(
                        GLES32.GL_TRIANGLES,
                        vao.indexCount,
                        GLES32.GL_UNSIGNED_SHORT,
                        0,
                        groupSize
                    )

                    bm.setupInstanceMatrixAttributes(instanceVbo, baseLocation = 3, divisor = 0)
                }
            }
        }
        GLES32.glBindVertexArray(0)
        program.setUseInstancing(false)
    }

    fun drawTransparent(ctx: LumiyaRenderContext) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        program.use()
        program.setUseInstancing(false)
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        transparentCount = 0
        for (s in 0 until allocatedSlotCount) {
            if (!slotActive[s] || !slotIsTransparent[s] || !primInFrustum(ctx, s)) continue
            if (transparentCount >= transparentSlots.size) {
                val newCap = transparentSlots.size * 2
                transparentSlots = transparentSlots.copyOf(newCap)
                transparentDepths = transparentDepths.copyOf(newCap)
            }
            transparentSlots[transparentCount] = s
            transparentDepths[transparentCount] = distanceToCamera(ctx, s)
            transparentCount++
        }

        if (transparentCount == 0) return

        quickSortTransparent(0, transparentCount - 1)

        var lastShapeOrdinal = -1
        for (i in 0 until transparentCount) {
            val slot = transparentSlots[i]
            val shapeOrdinal = slotShape[slot]
            if (shapeOrdinal != lastShapeOrdinal) {
                val shapeKind = ShapeKind.values()[shapeOrdinal]
                val vao = shapeVAOs?.get(shapeKind)
                if (vao != null) {
                    GLES32.glBindVertexArray(vao.vao)
                }
                lastShapeOrdinal = shapeOrdinal
            }
            val shapeKind = ShapeKind.values()[shapeOrdinal]
            val vao = shapeVAOs?.get(shapeKind) ?: continue
            drawPrimFaces(program, slot, vao.indexCount)
        }
        GLES32.glBindVertexArray(0)
        program.setUseInstancing(false)
    }

    fun drawEmissive(ctx: LumiyaRenderContext) {
        ensureShapes(ctx)
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
        program.setUseInstancing(false)
        program.setLighting(0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f)

        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glDepthMask(false)

        for (shape in 0 until 6) {
            val count = emissiveCounts[shape]
            if (count == 0) continue
            val shapeKind = ShapeKind.values()[shape]
            val vao = shapeVAOs?.get(shapeKind) ?: continue
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
        program.setUseInstancing(false)
    }

    // ── Internal Helpers ─────────────────────────────────────────────────

    private fun ensureShapes(ctx: LumiyaRenderContext) {
        if (shapeVAOs != null) return
        val bm = bufferManager ?: GLBufferManager(ctx.resourceManager).also { bufferManager = it }
        val map = mutableMapOf<ShapeKind, GLBufferManager.MeshVAO>()
        for (shape in ShapeKind.values()) {
            val key = when (shape) {
                ShapeKind.BOX -> PrimShapeKey.defaultFor(PrimShape.BOX)
                ShapeKind.SPHERE -> PrimShapeKey.defaultFor(PrimShape.SPHERE)
                ShapeKind.CYLINDER -> PrimShapeKey.defaultFor(PrimShape.CYLINDER)
                ShapeKind.TORUS -> PrimShapeKey.defaultFor(PrimShape.TORUS)
                ShapeKind.PRISM -> PrimShapeKey.defaultFor(PrimShape.PRISM)
                ShapeKind.RING -> PrimShapeKey.defaultFor(PrimShape.RING)
            }
            val meshData = PrimMeshGenerator.generateMesh(key)
            val interleaved = meshData.toInterleavedBuffer()
            map[shape] = bm.buildVAO(interleaved, meshData.indices, listOf(0 to 3, 1 to 3, 2 to 2))
        }
        shapeVAOs = map
    }

    private fun drawPrimFaces(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        slot: Int,
        totalIndexCount: Int
    ) {
        program.setUseInstancing(false)
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
        program.setUseInstancing(false)
        program.setModelMatrix(slotModelMatrices, slot * 16)
        val fIdx = slot * MAX_FACES_PER_PRIM
        buildTexMatrix(slot, 0, scratchTexMatrix)
        program.setTexMatrix(scratchTexMatrix)

        val g = faceGlow[fIdx]
        program.setColor(
            faceColorR[fIdx] * g,
            faceColorG[fIdx] * g,
            faceColorB[fIdx] * g,
            faceColorA[fIdx] * g
        )
        val handle = faceTextureHandle[fIdx]
        if (handle != 0) {
            program.setUseTexture(true)
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle)
            program.setTextureSampler(0)
        } else {
            program.setUseTexture(false)
        }
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

    private fun shapeFromParams(p: PrimShapeParams): ShapeKind {
        val isCircularPath = (p.pathCurve and 0x30) != 0
        val profile = p.profileType
        return when {
            p.pathCurve == PrimShapeParams.PATH_CIRCLE2 && profile == PrimShapeParams.PROFILE_CIRCLE -> ShapeKind.TORUS
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
        val faceCount = faceCountFor(shape, hollow)
        slotFaceCount[slot] = faceCount

        if (textureEntry == null || textureEntry.isEmpty()) return
        val parsed = try {
            TextureEntryParser.parseFull(textureEntry, faceCount)
        } catch (t: Throwable) {
            null
        } ?: return

        var transparent = false
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until faceCount) {
            val src = parsed.getOrNull(i) ?: continue
            val fIdx = base + i
            val oldMsb = faceTextureIdMsb[fIdx]
            val oldLsb = faceTextureIdLsb[fIdx]
            val newMsb = src.textureId.mostSignificantBits
            val newLsb = src.textureId.leastSignificantBits
            if (oldMsb != newMsb || oldLsb != newLsb) {
                faceTextureIdMsb[fIdx] = newMsb
                faceTextureIdLsb[fIdx] = newLsb
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

            if (src.colorA < 0.999f) transparent = true
            updateFaceMaterial(slot, i)
        }
        slotIsTransparent[slot] = transparent
    }

    private fun hasEmissive(slot: Int): Boolean {
        val count = slotFaceCount[slot]
        val base = slot * MAX_FACES_PER_PRIM
        for (i in 0 until count) {
            if (faceGlow[base + i] > GLOW_THRESHOLD) return true
        }
        return false
    }

    private fun updateFaceMaterial(slot: Int, faceIndex: Int) {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        val desc = buildFaceDescriptor(slot, faceIndex)
        faceDescriptors[fIdx] = desc
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
        val texHandle = faceTextureHandle[fIdx]
        val msb = faceTextureIdMsb[fIdx]
        val lsb = faceTextureIdLsb[fIdx]
        val texUuid = if (msb != 0L || lsb != 0L) UUID(msb, lsb) else NULL_UUID
        val glow = faceGlow[fIdx]

        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(
                faceColorR[fIdx],
                faceColorG[fIdx],
                faceColorB[fIdx],
                faceColorA[fIdx]
            ),
            baseColorTexture = if (texHandle != 0 || texUuid != NULL_UUID) {
                MaterialDescriptor.TextureRef(
                    texUuid,
                    texUuid,
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

    private fun faceCountFor(shape: ShapeKind, hollow: Boolean): Int {
        val (sides, caps) = when (shape) {
            ShapeKind.BOX -> 4 to 2
            ShapeKind.SPHERE -> 1 to 0
            ShapeKind.CYLINDER -> 1 to 2
            ShapeKind.PRISM -> 3 to 2
            ShapeKind.TORUS -> 1 to 0
            ShapeKind.RING -> 2 to 1
        }
        val effectiveCaps = if (hollow) caps * 2 else caps
        return sides + effectiveCaps
    }

    private fun buildTexMatrix(slot: Int, faceIndex: Int, outMatrix: FloatArray) {
        val fIdx = slot * MAX_FACES_PER_PRIM + faceIndex
        val offS = faceOffsetS[fIdx]
        val offT = faceOffsetT[fIdx]
        val scS = faceScaleS[fIdx]
        val scT = faceScaleT[fIdx]
        val rot = faceRotation[fIdx]

        Matrix.setIdentityM(outMatrix, 0)
        Matrix.translateM(outMatrix, 0, 0.5f + offS, 0.5f + offT, 0f)
        if (rot != 0f) {
            Matrix.rotateM(outMatrix, 0, Math.toDegrees(rot.toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(outMatrix, 0, scS, scT, 1f)
        Matrix.translateM(outMatrix, 0, -0.5f, -0.5f, 0f)
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
        if (low >= high) return
        val pivot = transparentDepths[(low + high) ushr 1]
        var i = low
        var j = high
        while (i <= j) {
            while (transparentDepths[i] > pivot) i++
            while (transparentDepths[j] < pivot) j--
            if (i <= j) {
                val tmpSlot = transparentSlots[i]
                transparentSlots[i] = transparentSlots[j]
                transparentSlots[j] = tmpSlot

                val tmpDepth = transparentDepths[i]
                transparentDepths[i] = transparentDepths[j]
                transparentDepths[j] = tmpDepth

                i++
                j--
            }
        }
        if (low < j) quickSortTransparent(low, j)
        if (i < high) quickSortTransparent(i, high)
    }
}
