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
 *
 * Design lineage: Lumiya `DrawableStore.java` + `DrawableObject.java` +
 * `DrawablePrim.java`, modernised with VAO-per-shape sharing and
 * per-face material instances.
 *
 * Six primitive shapes (box, sphere, cylinder, torus, prism, ring) are
 * cached as shared VAOs.  Each prim instance stores its transform plus
 * an array of per-face textures + tints + UV transforms decoded from
 * the prim's TextureEntry.
 *
 * Faces per shape (matches LL viewer LLVolume face counts):
 *   box / cylinder / prism = 6 (sides + top/bottom + cuts)
 *   sphere / torus / ring  = 1 (sweep produces a single contiguous face)
 *
 * Note: the actual face count returned by `LLVolume::regenerate` varies
 * with path/profile cuts. We use the LL fallback face counts above and
 * silently re-bind the default texture for indices the parser doesn't
 * cover. This matches Lumiya's behaviour.
 */
class DrawablePrimStore {

    enum class ShapeKind {
        BOX, SPHERE, CYLINDER, TORUS, PRISM, RING;
    }

    /** Per-face material data. Populated from TextureEntryParser.parseFull. */
    data class FaceMaterial(
        var textureId: UUID = NULL_UUID,
        var textureHandle: Int = 0,
        var colorR: Float = 1f,
        var colorG: Float = 1f,
        var colorB: Float = 1f,
        var colorA: Float = 1f,
        var scaleS: Float = 1f,
        var scaleT: Float = 1f,
        var offsetS: Float = 0f,
        var offsetT: Float = 0f,
        var rotation: Float = 0f
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
    }

    private val prims = ConcurrentHashMap<Long, PrimInstance>()

    // Shared shape VAOs (lazily initialised on first draw)
    private var shapeVAOs: Map<ShapeKind, GLBufferManager.MeshVAO>? = null
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
     * Insert / update a prim with the full ObjectUpdate metadata. This is
     * what `Gles3RenderCommandConsumer` calls so the prim picks up its
     * shape, scale, per-face textures, and AABB at insertion time.
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
        instance.shape = shapeFromParams(shapeParams)
        // LL viewer treats any non-zero hollow as hollow; the profile
        // hollow shape (square / circle / triangle) only changes the
        // hole geometry, not the face count.
        instance.hollow = shapeParams.profileHollow > 0f
        instance.scaleX = scaleX
        instance.scaleY = scaleY
        instance.scaleZ = scaleZ
        instance.aabbHalfX = scaleX * 0.5f
        instance.aabbHalfY = scaleY * 0.5f
        instance.aabbHalfZ = scaleZ * 0.5f

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

    /**
     * Bind an uploaded GL texture to a prim's default face. Called from
     * the GL thread once a TextureManager fetch + GLTextureCache upload
     * has completed.
     */
    fun setPrimTexture(id: Long, textureHandle: Int) {
        val instance = prims[id] ?: return
        instance.faces.forEach { face ->
            if (face.textureHandle == 0) face.textureHandle = textureHandle
        }
    }

    /** Bind a texture handle to a specific face index. */
    fun setFaceTexture(id: Long, faceIndex: Int, textureHandle: Int) {
        val instance = prims[id] ?: return
        if (faceIndex < 0 || faceIndex >= instance.faces.size) return
        instance.faces[faceIndex].textureHandle = textureHandle
    }

    /**
     * Look up the current default-face texture UUID for [primId]. Used
     * by the consumer's texture-rebinder when a face's GL handle is
     * uploaded so it can also patch any other faces that referenced
     * the same UUID. Returns null UUID if the prim has no faces.
     */
    fun getDefaultTextureId(id: Long): UUID {
        val instance = prims[id] ?: return NULL_UUID
        return instance.faces.firstOrNull()?.textureId ?: NULL_UUID
    }

    /**
     * Patch every face whose [FaceMaterial.textureId] matches [textureId]
     * to point at [textureHandle]. Lets a single texture upload cover
     * all faces sharing that UUID, even when per-face overrides differ.
     */
    fun bindTextureToMatchingFaces(id: Long, textureId: UUID, textureHandle: Int) {
        val instance = prims[id] ?: return
        instance.faces.forEach { face ->
            if (face.textureId == textureId) face.textureHandle = textureHandle
        }
    }

    /** Mark a prim as transparent (flips into the alpha-sorted draw list). */
    fun setPrimTransparent(id: Long, transparent: Boolean) {
        val instance = prims[id] ?: return
        instance.isTransparent = transparent
    }

    /** Update only the model matrix (cheap path for ObjectUpdateCompressed). */
    fun setPrimTransform(id: Long, matrix4x4: FloatArray) {
        val instance = prims[id] ?: return
        if (matrix4x4.size >= 16) {
            System.arraycopy(matrix4x4, 0, instance.modelMatrix, 0, 16)
        }
    }

    fun primCount(): Int = prims.size

    /** Read-only snapshot of all live prims (for picking + culling). */
    fun snapshot(): Collection<PrimInstance> = prims.values

    fun clear() {
        prims.clear()
    }

    fun destroy() {
        prims.clear()
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

        // Group draws by shape so we minimise VAO binds (six shapes).
        val byShape = prims.values
            .filter { !it.isTransparent && primInFrustum(ctx, it) }
            .groupBy { it.shape }
        for ((shape, list) in byShape) {
            val vao = shapeVAOs?.get(shape) ?: continue
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

        // Sort back-to-front for correct alpha blending.
        val sorted = prims.values
            .filter { it.isTransparent && primInFrustum(ctx, it) }
            .sortedByDescending { distanceToCamera(ctx, it) }

        var lastShape: ShapeKind? = null
        var lastVao: GLBufferManager.MeshVAO? = null
        for (prim in sorted) {
            if (prim.shape != lastShape) {
                lastVao = shapeVAOs?.get(prim.shape)
                lastShape = prim.shape
                lastVao?.let { GLES32.glBindVertexArray(it.vao) }
            }
            val v = lastVao ?: continue
            drawPrimFaces(program, prim, v.indexCount)
        }
        GLES32.glBindVertexArray(0)
    }

    private fun drawPrimFaces(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        prim: PrimInstance,
        totalIndexCount: Int
    ) {
        program.setModelMatrix(prim.modelMatrix)

        // For first pass: SL prims have variable face counts driven by
        // path/profile cuts, but the shared VAOs are unsplit. We bind
        // the first face's material and draw the whole shape — this
        // matches what most prims look like in practice (one tint, one
        // texture). A future per-face draw split lives on the same hook
        // when the geometry is per-face.
        val face = prim.faces.firstOrNull() ?: return
        val descriptor = face.toMaterialDescriptor()

        // Build a UV transform matrix for SL face offset / scale / rotation.
        val texMatrix = buildTexMatrix(face)
        program.setTexMatrix(texMatrix)

        GlesMaterialTranslator.apply(
            program,
            descriptor,
            GlesMaterialTranslator.TextureBindings(baseColorHandle = face.textureHandle)
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
        val faceCount = faceCountFor(instance.shape, instance.hollow)
        // Resize face list to match shape (preserve any already-uploaded handles).
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
            // Preserve texture handle if the UUID didn't change so we
            // don't blank out an already-uploaded GL texture between
            // material updates.
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
        }
        // Auto-flip transparent if any face has alpha < 1.
        instance.isTransparent = instance.faces.any { it.colorA < 0.999f }
    }

    /**
     * Face counts that match LL viewer `LLVolume::generate` + `addHole`
     * (indra/llmath/llvolume.cpp). Side count comes from the profile,
     * cap count comes from the path being open. Hollow doubles the
     * cap count because `LLProfile::addHole` runs after the base
     * profile has emitted its caps:
     *
     *   for (i=0; i<mFaces.size(); i++) { if (mFaces[i].mCap)
     *                                     mFaces[i].mCount *= 2; }
     *
     * Sides:  box=4   prism=3   cylinder/sphere/torus=1   ring=2
     * Caps:   box/cylinder/prism = 2 (top + bottom)
     *         sphere/torus       = 0 (closed sweep)
     *         ring               = 1 (only the major-axis caps differ)
     */
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

    private fun buildTexMatrix(face: FaceMaterial): FloatArray {
        val m = FloatArray(16)
        Matrix.setIdentityM(m, 0)
        // Translate to UV center, rotate, scale, translate back, then apply offset.
        Matrix.translateM(m, 0, 0.5f + face.offsetS, 0.5f + face.offsetT, 0f)
        if (face.rotation != 0f) {
            Matrix.rotateM(m, 0, Math.toDegrees(face.rotation.toDouble()).toFloat(), 0f, 0f, 1f)
        }
        Matrix.scaleM(m, 0, face.scaleS, face.scaleT, 1f)
        Matrix.translateM(m, 0, -0.5f, -0.5f, 0f)
        return m
    }

    private fun FaceMaterial.toMaterialDescriptor(): MaterialDescriptor {
        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(colorR, colorG, colorB, colorA),
            baseColorTexture = if (textureHandle != 0) {
                MaterialDescriptor.TextureRef(
                    textureId,
                    textureId,
                    isDownloadable = true
                )
            } else null
        )
    }

    // ── Shared shape geometry ────────────────────────────────────────────

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

    // ── Shape builders (unit-sized; per-prim scale lives in modelMatrix) ─

    private fun buildBox(bm: GLBufferManager): GLBufferManager.MeshVAO {
        val h = 0.5f
        val v = floatArrayOf(
            // Front (+Y)
            -h,-h, h,  0f,0f,1f,  0f,0f,   h,-h, h,  0f,0f,1f,  1f,0f,
             h, h, h,  0f,0f,1f,  1f,1f,  -h, h, h,  0f,0f,1f,  0f,1f,
            // Back (-Y)
             h,-h,-h,  0f,0f,-1f, 0f,0f,  -h,-h,-h,  0f,0f,-1f, 1f,0f,
            -h, h,-h,  0f,0f,-1f, 1f,1f,   h, h,-h,  0f,0f,-1f, 0f,1f,
            // Top (+Z)
            -h, h, h,  0f,1f,0f,  0f,0f,   h, h, h,  0f,1f,0f,  1f,0f,
             h, h,-h,  0f,1f,0f,  1f,1f,  -h, h,-h,  0f,1f,0f,  0f,1f,
            // Bottom (-Z)
            -h,-h,-h,  0f,-1f,0f, 0f,0f,   h,-h,-h,  0f,-1f,0f, 1f,0f,
             h,-h, h,  0f,-1f,0f, 1f,1f,  -h,-h, h,  0f,-1f,0f, 0f,1f,
            // Right (+X)
             h,-h, h,  1f,0f,0f,  0f,0f,   h,-h,-h,  1f,0f,0f,  1f,0f,
             h, h,-h,  1f,0f,0f,  1f,1f,   h, h, h,  1f,0f,0f,  0f,1f,
            // Left (-X)
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
        // Side
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
        // Caps. Center vertex + fan.
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
        // Equilateral triangle profile extruded along Z, unit-sized.
        val h = 0.5f
        val s = 0.5f
        val v = floatArrayOf(
            // Front face (+Y triangle)
            -s,-s,-h,  0f,-1f,0f, 0f,0f,
             s,-s,-h,  0f,-1f,0f, 1f,0f,
             0f, s,-h,  0f,1f,0f, 0.5f,1f,
            // Back face (-Y triangle)
            -s,-s, h,  0f,-1f,0f, 0f,0f,
             s,-s, h,  0f,-1f,0f, 1f,0f,
             0f, s, h,  0f,1f,0f, 0.5f,1f,
            // Bottom (-y rectangle)
            -s,-s,-h,  0f,-1f,0f, 0f,0f,
             s,-s,-h,  0f,-1f,0f, 1f,0f,
             s,-s, h,  0f,-1f,0f, 1f,1f,
            -s,-s, h,  0f,-1f,0f, 0f,1f,
            // +x slant face
             s,-s,-h,  0.7f,0.7f,0f, 0f,0f,
             0f, s,-h,  0.7f,0.7f,0f, 1f,0f,
             0f, s, h,  0.7f,0.7f,0f, 1f,1f,
             s,-s, h,  0.7f,0.7f,0f, 0f,1f,
            // -x slant face
             0f, s,-h, -0.7f,0.7f,0f, 0f,0f,
            -s,-s,-h, -0.7f,0.7f,0f, 1f,0f,
            -s,-s, h, -0.7f,0.7f,0f, 1f,1f,
             0f, s, h, -0.7f,0.7f,0f, 0f,1f
        )
        val idx = shortArrayOf(
            0,2,1,           // front (CCW from -Z)
            3,4,5,           // back
            6,7,8, 6,8,9,    // bottom
            10,11,12, 10,12,13, // +x slant
            14,15,16, 14,16,17  // -x slant
        )
        return bm.buildVAO(v, idx, listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun buildRing(bm: GLBufferManager, segments: Int = 24): GLBufferManager.MeshVAO {
        // Ring = square profile swept around major axis. We approximate
        // it as a cylinder with a square cross-section (4 facets).
        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        val majorRadius = 0.35f
        val sideHalf = 0.15f
        for (i in 0..segments) {
            val theta = 2.0 * Math.PI * i / segments
            val cosT = Math.cos(theta).toFloat()
            val sinT = Math.sin(theta).toFloat()
            // Centre of cross-section
            val cx = majorRadius * cosT
            val cy = majorRadius * sinT
            // Tangent + normal in XY plane
            val u = i.toFloat() / segments
            // Four corners of the square cross-section: outer-top, outer-bottom, inner-bottom, inner-top
            verts.addAll(listOf(cx + sideHalf * cosT, cy + sideHalf * sinT, sideHalf, cosT, sinT, 0f, u, 0f))
            verts.addAll(listOf(cx + sideHalf * cosT, cy + sideHalf * sinT, -sideHalf, cosT, sinT, 0f, u, 1f))
            verts.addAll(listOf(cx - sideHalf * cosT, cy - sideHalf * sinT, -sideHalf, -cosT, -sinT, 0f, u, 0f))
            verts.addAll(listOf(cx - sideHalf * cosT, cy - sideHalf * sinT, sideHalf, -cosT, -sinT, 0f, u, 1f))
        }
        val stride = 4
        for (i in 0 until segments) {
            val a = i * stride
            val b = (i + 1) * stride
            // Outer face
            indices.addAll(listOf(a.toShort(), (a + 1).toShort(), b.toShort(),
                                   (a + 1).toShort(), (b + 1).toShort(), b.toShort()))
            // Top face
            indices.addAll(listOf((a + 3).toShort(), a.toShort(), (b + 3).toShort(),
                                   a.toShort(), b.toShort(), (b + 3).toShort()))
            // Bottom face
            indices.addAll(listOf((a + 1).toShort(), (a + 2).toShort(), (b + 1).toShort(),
                                   (a + 2).toShort(), (b + 2).toShort(), (b + 1).toShort()))
            // Inner face
            indices.addAll(listOf((a + 2).toShort(), (a + 3).toShort(), (b + 2).toShort(),
                                   (a + 3).toShort(), (b + 3).toShort(), (b + 2).toShort()))
        }
        return bm.buildVAO(verts.toFloatArray(), indices.toShortArray(), listOf(0 to 3, 1 to 3, 2 to 2))
    }
}
