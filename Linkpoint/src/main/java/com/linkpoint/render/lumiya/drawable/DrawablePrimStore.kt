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

    /** Per-prim instance data. */
    data class PrimInstance(
        val id: Long,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var shapeKey: PrimShapeKey = PrimShapeKey.defaultFor(PrimShape.BOX),
        var hollow: Boolean = false,
        var scaleX: Float = 1f, var scaleY: Float = 1f, var scaleZ: Float = 1f,
        val faces: MutableList<FaceMaterial> = mutableListOf(FaceMaterial()),
        var isTransparent: Boolean = false,
        /** Cached AABB half-extents in world units, used for picking + culling. */
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
    }

    private val prims = ConcurrentHashMap<Long, PrimInstance>()

    // Shared shape VAOs cached by PrimShapeKey
    private val shapeVAOs = ConcurrentHashMap<PrimShapeKey, GLBufferManager.MeshVAO>()
    private var bufferManager: GLBufferManager? = null

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
        rotation: FloatArray? = null,   // 4x4 rotation matrix or null
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
            instance.aabbHalfX = wx
            instance.aabbHalfY = wy
            instance.aabbHalfZ = wz
        } else {
            instance.aabbHalfX = localHalfX
            instance.aabbHalfY = localHalfY
            instance.aabbHalfZ = localHalfZ
        }

        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        if (rotation != null && rotation.size >= 16) {
            val tmp = FloatArray(16)
            Matrix.multiplyMM(tmp, 0, instance.modelMatrix, 0, rotation, 0)
            System.arraycopy(tmp, 0, instance.modelMatrix, 0, 16)
        }
        Matrix.scaleM(instance.modelMatrix, 0, scaleX, scaleY, scaleZ)
        instance.modelMatrix[12] = posX
        instance.modelMatrix[13] = posY
        instance.modelMatrix[14] = posZ

        applyTextureEntry(instance, textureEntry)
    }

    fun removePrim(id: Long) {
        prims.remove(id)
    }

    fun setPrimTexture(id: Long, textureHandle: Int) {
        val instance = prims[id] ?: return
        instance.faces.forEach { face ->
            if (face.textureHandle == 0) face.textureHandle = textureHandle
        }
    }

    fun setFaceTexture(id: Long, faceIndex: Int, textureHandle: Int) {
        val instance = prims[id] ?: return
        if (faceIndex < 0 || faceIndex >= instance.faces.size) return
        instance.faces[faceIndex].textureHandle = textureHandle
    }

    fun getDefaultTextureId(id: Long): UUID {
        val instance = prims[id] ?: return NULL_UUID
        return instance.faces.firstOrNull()?.textureId ?: NULL_UUID
    }

    fun bindTextureToMatchingFaces(id: Long, textureId: UUID, textureHandle: Int) {
        val instance = prims[id] ?: return
        instance.faces.forEach { face ->
            if (face.textureId == textureId) face.textureHandle = textureHandle
        }
    }

    fun setPrimTransparent(id: Long, transparent: Boolean) {
        val instance = prims[id] ?: return
        instance.isTransparent = transparent
    }

    fun setPrimTransform(id: Long, matrix4x4: FloatArray) {
        val instance = prims[id] ?: return
        if (matrix4x4.size >= 16) {
            System.arraycopy(matrix4x4, 0, instance.modelMatrix, 0, 16)
        }
    }

    fun primCount(): Int = prims.size

    fun snapshot(): Collection<PrimInstance> = prims.values

    fun clear() {
        prims.clear()
    }

    fun destroy() {
        prims.clear()
        shapeVAOs.values.forEach { bufferManager?.destroyVAO(it) }
        shapeVAOs.clear()
    }

    // ── Draw ─────────────────────────────────────────────────────────────

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
            for (prim in list) {
                if (occlusion != null && !occlusion.shouldDraw(prim.id)) continue
                occlusion?.beginQuery(prim.id)
                drawPrimFaces(program, prim, vao.indexCount)
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
            val v = lastVao ?: continue
            drawPrimFaces(program, prim, v.indexCount)
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
        return false
    }

    fun drawEmissive(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return

        val emissivePrims = prims.values.filter { primInFrustum(ctx, it) && hasEmissive(it) }
        if (emissivePrims.isEmpty()) return

        program.use()
        program.setLighting(0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f)

        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glDepthMask(false)

        val byShape = emissivePrims.groupBy { it.shapeKey }
        for ((shapeKey, list) in byShape) {
            val vao = getOrCreateVAO(ctx, shapeKey)
            GLES32.glBindVertexArray(vao.vao)
            for (prim in list) {
                drawPrimEmissive(program, prim, vao.indexCount)
            }
        }
        GLES32.glBindVertexArray(0)

        GLES32.glBlendFuncSeparate(
            GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
            GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
        )
        GLES32.glDepthMask(true)
    }

    private fun drawPrimEmissive(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        prim: PrimInstance,
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
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, face.textureHandle)
            program.setTextureSampler(0)
        } else {
            program.setUseTexture(false)
        }
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
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

    private fun buildTexMatrix(face: FaceMaterial): FloatArray {
        val m = FloatArray(16)
        Matrix.setIdentityM(m, 0)
        Matrix.translateM(m, 0, 0.5f + face.offsetS, 0.5f + face.offsetT, 0f)
        if (face.rotation != 0f) {
            Matrix.rotateM(m, 0, Math.toDegrees(face.rotation.toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(m, 0, face.scaleS, face.scaleT, 1f)
        Matrix.translateM(m, 0, -0.5f, -0.5f, 0f)
        return m
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

    private fun primInFrustum(ctx: LumiyaRenderContext, prim: PrimInstance): Boolean {
        val cx = prim.modelMatrix[12]
        val cy = prim.modelMatrix[13]
        val cz = prim.modelMatrix[14]
        return ctx.frustumCuller.isAABBVisible(
            cx - prim.aabbHalfX, cy - prim.aabbHalfY, cz - prim.aabbHalfZ,
            cx + prim.aabbHalfX, cy + prim.aabbHalfY, cz + prim.aabbHalfZ
        )
    }
}
