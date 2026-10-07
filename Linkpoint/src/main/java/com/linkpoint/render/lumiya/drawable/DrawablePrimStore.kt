package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.protocol.textures.TextureEntryParser
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
        var shape: ShapeKind = ShapeKind.BOX,
        var hollow: Boolean = false,
        var scaleX: Float = 1f, var scaleY: Float = 1f, var scaleZ: Float = 1f,
        val faces: MutableList<FaceMaterial> = mutableListOf(FaceMaterial()),
        var isTransparent: Boolean = false,
        /** Cached AABB half-extents in world units, used for picking + culling. */
        var aabbHalfX: Float = 0.5f,
        var aabbHalfY: Float = 0.5f,
        var aabbHalfZ: Float = 0.5f
    )

    companion object {
        private val NULL_UUID = UUID(0L, 0L)
        private val IDENTITY_TEX = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

        const val GLOW_THRESHOLD = 0.005f
    }

    private val prims = ConcurrentHashMap<Long, PrimInstance>()

    // Scratch matrices for zero-allocation GL matrix uniforms
    private val scratchTexMatrix = FloatArray(16)

    // Shared shape VAOs
    private var shapeVAOs: Map<ShapeKind, GLBufferManager.MeshVAO>? = null
    private var bufferManager: GLBufferManager? = null

    // ── Mutation ─────────────────────────────────────────────────────────

    fun addPrim(id: Long, posX: Float, posY: Float, posZ: Float) {
        val instance = prims.getOrPut(id) { PrimInstance(id) }
        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
    }

    fun upsertPrim(
        id: Long,
        posX: Float, posY: Float, posZ: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        rotation: FloatArray? = null,   // 4x4 rotation matrix or null
        shapeParams: PrimShapeParams = PrimShapeParams.DEFAULT,
        textureEntry: ByteArray? = null
    ) {
        val instance = prims.getOrPut(id) { PrimInstance(id) }
        instance.shape = shapeFromParams(shapeParams)
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

    /** Snapshot of all live prims (for picking + culling queries outside render loop). */
    fun snapshot(): Collection<PrimInstance> = ArrayList(prims.values)

    fun clear() {
        prims.clear()
    }

    fun destroy() {
        clear()
        shapeVAOs?.values?.forEach { bufferManager?.destroyVAO(it) }
        shapeVAOs = null
    }

    // ── Draw ─────────────────────────────────────────────────────────────

    fun drawOpaque(
        ctx: LumiyaRenderContext,
        occlusion: com.linkpoint.render.lumiya.spatial.OcclusionQuerySet? = null
    ) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        val opaquePrims = prims.values.filter { !it.isTransparent && primInFrustum(ctx, it) }
        val byShape = opaquePrims.groupBy { it.shape }

        for ((shapeKind, list) in byShape) {
            val vao = shapeVAOs?.get(shapeKind) ?: continue
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
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        val transparentPrims = prims.values
            .filter { it.isTransparent && primInFrustum(ctx, it) }
            .sortedByDescending { distanceToCamera(ctx, it) }

        var lastShape: ShapeKind? = null
        for (prim in transparentPrims) {
            if (prim.shape != lastShape) {
                lastShape = prim.shape
                val vao = shapeVAOs?.get(prim.shape) ?: continue
                GLES32.glBindVertexArray(vao.vao)
            }
            val vao = shapeVAOs?.get(prim.shape) ?: continue
            drawPrimFaces(program, prim, vao.indexCount)
        }
        GLES32.glBindVertexArray(0)
    }

    fun drawEmissive(ctx: LumiyaRenderContext) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return

        val emissivePrims = prims.values.filter { primInFrustum(ctx, it) && hasEmissive(it) }
        if (emissivePrims.isEmpty()) return

        program.use()
        program.setLighting(0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f)

        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_ONE, GLES32.GL_ONE)
        GLES32.glDepthMask(false)

        val byShape = emissivePrims.groupBy { it.shape }
        for ((shapeKind, list) in byShape) {
            val vao = shapeVAOs?.get(shapeKind) ?: continue
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

    private fun drawPrimFaces(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        prim: PrimInstance,
        totalIndexCount: Int
    ) {
        program.setModelMatrix(prim.modelMatrix, 0)
        val face = prim.faces.firstOrNull()
        if (face != null) {
            buildTexMatrix(face, scratchTexMatrix)
            program.setTexMatrix(scratchTexMatrix)
            val desc = face.descriptor ?: face.toMaterialDescriptor()
            val bindings = GlesMaterialTranslator.TextureBindings(
                baseColorHandle = face.textureHandle,
                normalHandle = face.normalHandle,
                metallicRoughnessHandle = face.metallicRoughnessHandle,
                emissiveHandle = face.emissiveHandle,
                occlusionHandle = face.occlusionHandle
            )
            GlesMaterialTranslator.apply(program, desc, bindings)
        } else {
            program.setTexMatrix(IDENTITY_TEX)
        }
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, totalIndexCount, GLES32.GL_UNSIGNED_SHORT, 0)
    }

    private fun drawPrimEmissive(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        prim: PrimInstance,
        totalIndexCount: Int
    ) {
        val face = prim.faces.firstOrNull { it.glow > GLOW_THRESHOLD } ?: return
        program.setModelMatrix(prim.modelMatrix, 0)
        buildTexMatrix(face, scratchTexMatrix)
        program.setTexMatrix(scratchTexMatrix)

        val g = face.glow
        program.setColor(
            face.colorR * g,
            face.colorG * g,
            face.colorB * g,
            face.colorA * g
        )
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

    private fun shapeFromParams(p: PrimShapeParams): ShapeKind {
        val isCircularPath = (p.pathCurve and 0x30) != 0 || p.pathCurve == PrimShapeParams.PATH_CIRCLE
        val profile = p.profileType
        val isCircleProfile = profile == PrimShapeParams.PROFILE_CIRCLE || profile == PrimShapeParams.PROFILE_HALF_CIRCLE
        return when {
            p.pathCurve == PrimShapeParams.PATH_CIRCLE2 && isCircleProfile -> ShapeKind.TORUS
            p.pathCurve == PrimShapeParams.PATH_CIRCLE && isCircleProfile -> ShapeKind.SPHERE
            isCircularPath && profile == PrimShapeParams.PROFILE_SQUARE -> ShapeKind.RING
            p.pathCurve == PrimShapeParams.PATH_LINE && isCircleProfile -> ShapeKind.CYLINDER
            p.pathCurve == PrimShapeParams.PATH_LINE && (
                profile == PrimShapeParams.PROFILE_EQUAL_TRI ||
                profile == PrimShapeParams.PROFILE_ISO_TRI ||
                profile == PrimShapeParams.PROFILE_RIGHT_TRI
            ) -> ShapeKind.PRISM
            else -> ShapeKind.BOX
        }
    }

    private fun applyTextureEntry(instance: PrimInstance, textureEntry: ByteArray?) {
        val faceCount = faceCountFor(instance.shape, instance.hollow)
        if (textureEntry != null && textureEntry.isNotEmpty()) {
            val parsed = try {
                TextureEntryParser.parseFull(textureEntry, faceCount)
            } catch (t: Throwable) {
                null
            }
            if (parsed != null) {
                instance.faces.clear()
                var hasAlpha = false
                for (src in parsed) {
                    val face = FaceMaterial(
                        textureId = src.textureId,
                        colorR = src.colorR,
                        colorG = src.colorG,
                        colorB = src.colorB,
                        colorA = src.colorA,
                        scaleS = src.scaleS,
                        scaleT = src.scaleT,
                        offsetS = src.offsetS,
                        offsetT = src.offsetT,
                        rotation = src.rotation,
                        glow = src.glow
                    )
                    face.descriptor = face.toMaterialDescriptor()
                    if (src.colorA < 0.999f) hasAlpha = true
                    instance.faces.add(face)
                }
                instance.isTransparent = hasAlpha
                return
            }
        }
        if (instance.faces.isEmpty()) {
            val defaultFace = FaceMaterial()
            defaultFace.descriptor = defaultFace.toMaterialDescriptor()
            instance.faces.add(defaultFace)
        }
    }

    private fun hasEmissive(prim: PrimInstance): Boolean {
        for (face in prim.faces) {
            if (face.glow > GLOW_THRESHOLD) return true
        }
        return false
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

    private fun buildTexMatrix(face: FaceMaterial, outMatrix: FloatArray) {
        Matrix.setIdentityM(outMatrix, 0)
        Matrix.translateM(outMatrix, 0, 0.5f + face.offsetS, 0.5f + face.offsetT, 0f)
        if (face.rotation != 0f) {
            Matrix.rotateM(outMatrix, 0, Math.toDegrees(face.rotation.toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(outMatrix, 0, face.scaleS, face.scaleT, 1f)
        Matrix.translateM(outMatrix, 0, -0.5f, -0.5f, 0f)
    }

    private fun FaceMaterial.toMaterialDescriptor(): MaterialDescriptor {
        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(colorR, colorG, colorB, colorA),
            baseColorTexture = if (textureHandle != 0) {
                MaterialDescriptor.TextureRef(textureId, textureId, isDownloadable = true)
            } else null,
            metallicFactor = metallicFactor,
            roughnessFactor = roughnessFactor,
            emissiveFactor = if (glow > 0f) MaterialDescriptor.Float3(glow, glow, glow) else MaterialDescriptor.Float3.ZERO,
            uvTransform = MaterialDescriptor.UvTransform(scaleS, scaleT, offsetS, offsetT, rotation)
        )
    }

    private fun ensureShapes(ctx: LumiyaRenderContext) {
        if (shapeVAOs != null) return
        val bm = GLBufferManager(ctx.resourceManager)
        bufferManager = bm
        shapeVAOs = mapOf(
            ShapeKind.BOX to buildBox(bm),
            ShapeKind.SPHERE to buildSphere(bm),
            ShapeKind.CYLINDER to buildCylinder(bm),
            ShapeKind.TORUS to buildTorus(bm),
            ShapeKind.PRISM to buildPrism(bm),
            ShapeKind.RING to buildRing(bm)
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

    // ── Shared Shape Geometry Builders ───────────────────────────────────

    private fun buildBox(bm: GLBufferManager): GLBufferManager.MeshVAO {
        val h = 0.5f
        val v = floatArrayOf(
            -h,-h, h,  0f,0f,1f,  0f,0f,   h,-h, h,  0f,0f,1f,  1f,0f,
             h, h, h,  0f,0f,1f,  1f,1f,  -h, h, h,  0f,0f,1f,  0f,1f,
             h,-h,-h,  0f,0f,-1f, 0f,0f,  -h,-h,-h,  0f,0f,-1f, 1f,0f,
            -h, h,-h,  0f,0f,-1f, 1f,1f,   h, h,-h,  0f,0f,-1f, 0f,1f,
            -h, h, h,  0f,1f,0f,  0f,0f,   h, h, h,  0f,1f,0f,  1f,0f,
             h, h,-h,  0f,1f,0f,  1f,1f,  -h, h,-h,  0f,1f,0f,  0f,1f,
            -h,-h,-h,  0f,-1f,0f, 0f,0f,   h,-h,-h,  0f,-1f,0f, 1f,0f,
             h,-h, h,  0f,-1f,0f, 1f,1f,  -h,-h, h,  0f,-1f,0f, 0f,1f,
             h,-h, h,  1f,0f,0f,  0f,0f,   h,-h,-h,  1f,0f,0f,  1f,0f,
             h, h,-h,  1f,0f,0f,  1f,1f,   h, h, h,  1f,0f,0f,  0f,1f,
            -h,-h,-h, -1f,0f,0f,  0f,0f,  -h,-h, h, -1f,0f,0f,  1f,0f,
            -h, h, h, -1f,0f,0f,  1f,1f,  -h, h,-h, -1f,0f,0f,  0f,1f
        )
        val idx = shortArrayOf(
            0,1,2, 0,2,3,   4,5,6, 4,6,7,   8,9,10, 8,10,11,
            12,13,14, 12,14,15, 16,17,18, 16,18,19, 20,21,22, 20,22,23
        )
        return bm.buildVAO(v, idx, listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildSphere(bm: GLBufferManager, segments: Int = 24, rings: Int = 16): GLBufferManager.MeshVAO {
        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        for (r in 0..rings) {
            val phi = Math.PI * r / rings
            val sinPhi = Math.sin(phi).toFloat()
            val cosPhi = Math.cos(phi).toFloat()
            for (s in 0..segments) {
                val theta = 2.0 * Math.PI * s / segments
                val sinTheta = Math.sin(theta).toFloat()
                val cosTheta = Math.cos(theta).toFloat()
                val x = cosTheta * sinPhi * 0.5f
                val y = sinTheta * sinPhi * 0.5f
                val z = cosPhi * 0.5f
                verts.add(x); verts.add(y); verts.add(z)
                verts.add(cosTheta * sinPhi); verts.add(sinTheta * sinPhi); verts.add(cosPhi)
                verts.add(s.toFloat() / segments); verts.add(r.toFloat() / rings)
            }
        }
        for (r in 0 until rings) {
            for (s in 0 until segments) {
                val cur = r * (segments + 1) + s
                val next = cur + segments + 1
                indices.add(cur.toShort()); indices.add(next.toShort()); indices.add((cur + 1).toShort())
                indices.add((cur + 1).toShort()); indices.add(next.toShort()); indices.add((next + 1).toShort())
            }
        }
        return bm.buildVAO(verts.toFloatArray(), indices.toShortArray(), listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildCylinder(bm: GLBufferManager, segments: Int = 24): GLBufferManager.MeshVAO {
        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        val h = 0.5f
        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = Math.cos(angle).toFloat() * 0.5f
            val y = Math.sin(angle).toFloat() * 0.5f
            val nx = Math.cos(angle).toFloat()
            val ny = Math.sin(angle).toFloat()
            val u = i.toFloat() / segments
            verts.addAll(listOf(x, y, -h, nx, ny, 0f, u, 0f))
            verts.addAll(listOf(x, y, h, nx, ny, 0f, u, 1f))
        }
        for (i in 0 until segments) {
            val b0 = (i * 2).toShort()
            val t0 = (i * 2 + 1).toShort()
            val b1 = (i * 2 + 2).toShort()
            val t1 = (i * 2 + 3).toShort()
            indices.addAll(listOf(b0, b1, t0, b1, t1, t0))
        }
        val baseTop = verts.size / 8
        verts.addAll(listOf(0f, 0f, h, 0f, 0f, 1f, 0.5f, 0.5f))
        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = Math.cos(angle).toFloat() * 0.5f
            val y = Math.sin(angle).toFloat() * 0.5f
            verts.addAll(listOf(x, y, h, 0f, 0f, 1f, x + 0.5f, y + 0.5f))
        }
        for (i in 0 until segments) {
            indices.addAll(listOf(baseTop.toShort(), (baseTop + 1 + i).toShort(), (baseTop + 2 + i).toShort()))
        }
        val baseBottom = verts.size / 8
        verts.addAll(listOf(0f, 0f, -h, 0f, 0f, -1f, 0.5f, 0.5f))
        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = Math.cos(angle).toFloat() * 0.5f
            val y = Math.sin(angle).toFloat() * 0.5f
            verts.addAll(listOf(x, y, -h, 0f, 0f, -1f, x + 0.5f, y + 0.5f))
        }
        for (i in 0 until segments) {
            indices.addAll(listOf(baseBottom.toShort(), (baseBottom + 2 + i).toShort(), (baseBottom + 1 + i).toShort()))
        }
        return bm.buildVAO(verts.toFloatArray(), indices.toShortArray(), listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildTorus(bm: GLBufferManager, majorSegments: Int = 24, minorSegments: Int = 12): GLBufferManager.MeshVAO {
        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        val majorRadius = 0.35f
        val minorRadius = 0.15f
        for (i in 0..majorSegments) {
            val u = i.toDouble() / majorSegments
            val theta = u * 2.0 * Math.PI
            val cosTheta = Math.cos(theta).toFloat()
            val sinTheta = Math.sin(theta).toFloat()
            for (j in 0..minorSegments) {
                val v = j.toDouble() / minorSegments
                val phi = v * 2.0 * Math.PI
                val cosPhi = Math.cos(phi).toFloat()
                val sinPhi = Math.sin(phi).toFloat()
                val x = (majorRadius + minorRadius * cosPhi) * cosTheta
                val y = (majorRadius + minorRadius * cosPhi) * sinTheta
                val z = minorRadius * sinPhi
                val nx = cosPhi * cosTheta
                val ny = cosPhi * sinTheta
                val nz = sinPhi
                verts.addAll(listOf(x, y, z, nx, ny, nz, u.toFloat(), v.toFloat()))
            }
        }
        val rowStride = minorSegments + 1
        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val a = (i * rowStride + j).toShort()
                val b = ((i + 1) * rowStride + j).toShort()
                val c = ((i + 1) * rowStride + j + 1).toShort()
                val d = (i * rowStride + j + 1).toShort()
                indices.addAll(listOf(a, b, c, a, c, d))
            }
        }
        return bm.buildVAO(verts.toFloatArray(), indices.toShortArray(), listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildPrism(bm: GLBufferManager): GLBufferManager.MeshVAO {
        val h = 0.5f
        val s = 0.5f
        val v = floatArrayOf(
            -s,-s,-h,  0f,-1f,0f, 0f,0f,
             s,-s,-h,  0f,-1f,0f, 1f,0f,
             0f, s,-h,  0f,1f,0f, 0.5f,1f,
            -s,-s, h,  0f,-1f,0f, 0f,0f,
             s,-s, h,  0f,-1f,0f, 1f,0f,
             0f, s, h,  0f,1f,0f, 0.5f,1f,
            -s,-s,-h,  0f,-1f,0f, 0f,0f,
             s,-s,-h,  0f,-1f,0f, 1f,0f,
             s,-s, h,  0f,-1f,0f, 1f,1f,
            -s,-s, h,  0f,-1f,0f, 0f,1f,
             s,-s,-h,  0.7f,0.7f,0f, 0f,0f,
             0f, s,-h,  0.7f,0.7f,0f, 1f,0f,
             0f, s, h,  0.7f,0.7f,0f, 1f,1f,
             s,-s, h,  0.7f,0.7f,0f, 0f,1f,
             0f, s,-h, -0.7f,0.7f,0f, 0f,0f,
            -s,-s,-h, -0.7f,0.7f,0f, 1f,0f,
            -s,-s, h, -0.7f,0.7f,0f, 1f,1f,
             0f, s, h, -0.7f,0.7f,0f, 0f,1f
        )
        val idx = shortArrayOf(
            0,2,1,
            3,4,5,
            6,7,8, 6,8,9,
            10,11,12, 10,12,13,
            14,15,16, 14,16,17
        )
        return bm.buildVAO(v, idx, listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildRing(bm: GLBufferManager, segments: Int = 24): GLBufferManager.MeshVAO {
        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        val majorRadius = 0.35f
        val sideHalf = 0.15f
        for (i in 0..segments) {
            val theta = 2.0 * Math.PI * i / segments
            val cosT = Math.cos(theta).toFloat()
            val sinT = Math.sin(theta).toFloat()
            val cx = majorRadius * cosT
            val cy = majorRadius * sinT
            val u = i.toFloat() / segments
            verts.addAll(listOf(cx + sideHalf * cosT, cy + sideHalf * sinT, sideHalf, cosT, sinT, 0f, u, 0f))
            verts.addAll(listOf(cx + sideHalf * cosT, cy + sideHalf * sinT, -sideHalf, cosT, sinT, 0f, u, 1f))
            verts.addAll(listOf(cx - sideHalf * cosT, cy - sideHalf * sinT, -sideHalf, -cosT, -sinT, 0f, u, 0f))
            verts.addAll(listOf(cx - sideHalf * cosT, cy - sideHalf * sinT, sideHalf, -cosT, -sinT, 0f, u, 1f))
        }
        val stride = 4
        for (i in 0 until segments) {
            val a = i * stride
            val b = (i + 1) * stride
            indices.addAll(listOf(a.toShort(), (a + 1).toShort(), b.toShort(),
                                   (a + 1).toShort(), (b + 1).toShort(), b.toShort()))
            indices.addAll(listOf((a + 3).toShort(), a.toShort(), (b + 3).toShort(),
                                   a.toShort(), b.toShort(), (b + 3).toShort()))
            indices.addAll(listOf((a + 1).toShort(), (a + 2).toShort(), (b + 1).toShort(),
                                   (a + 2).toShort(), (b + 2).toShort(), (b + 1).toShort()))
            indices.addAll(listOf((a + 2).toShort(), (a + 3).toShort(), (b + 2).toShort(),
                                   (a + 3).toShort(), (b + 3).toShort(), (b + 2).toShort()))
        }
        return bm.buildVAO(verts.toFloatArray(), indices.toShortArray(), listOf(0 to 3, 1 to 3, 2 to 2))
    }
}
