package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshFace
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders SL mesh-asset prims (sculptType 5 / modern uploaded mesh content).
 *
 * Design lineage: `render/prims/MeshPrimRenderer.kt` (Filament path) ported
 * to GL ES 3 VAOs. Each unique mesh asset is compiled into one VAO per
 * face; per-prim instances reference the shared VAOs and carry their own
 * model matrix + per-face material array.
 *
 * Rigged-mesh skinning is captured but currently rendered in bind pose,
 * matching the Filament path. GPU skinning lands when `RiggedMeshShaderProgram`
 * is wired through here.
 */
class DrawableMeshStore {

    companion object {
        private const val TAG = "DrawableMeshStore"
    }

    /** Per-mesh-asset GPU buffers, one VAO per face. */
    private data class CompiledMesh(
        val faces: List<GLBufferManager.MeshVAO>,
        /** Per-face AABB extents in mesh-local space (max half-extent). */
        val faceBounds: List<Float>
    )

    /** Per-face per-prim material data. */
    data class FaceMaterial(
        var textureId: UUID = UUID(0L, 0L),
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

    data class MeshInstance(
        val id: Long,
        val meshId: UUID,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        val faces: MutableList<FaceMaterial> = mutableListOf(),
        var aabbHalfX: Float = 0.5f,
        var aabbHalfY: Float = 0.5f,
        var aabbHalfZ: Float = 0.5f,
        var isTransparent: Boolean = false
    )

    private val compiled = ConcurrentHashMap<UUID, CompiledMesh>()
    private val instances = ConcurrentHashMap<Long, MeshInstance>()
    private var bufferManager: GLBufferManager? = null

    // ── Compilation ──────────────────────────────────────────────────────

    fun upsertMeshPrim(
        id: Long,
        ctx: LumiyaRenderContext,
        posX: Float, posY: Float, posZ: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        rotation: FloatArray? = null,
        meshData: MeshData,
        textureEntry: ByteArray? = null
    ): Boolean {
        val mesh = getOrCompile(ctx, meshData) ?: return false

        val instance = instances.getOrPut(id) { MeshInstance(id, meshData.meshId) }
        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        if (rotation != null && rotation.size >= 16) {
            val tmp = FloatArray(16)
            Matrix.multiplyMM(tmp, 0, instance.modelMatrix, 0, rotation, 0)
            System.arraycopy(tmp, 0, instance.modelMatrix, 0, 16)
        }
        Matrix.scaleM(instance.modelMatrix, 0, scaleX, scaleY, scaleZ)
        instance.aabbHalfX = scaleX * 0.5f
        instance.aabbHalfY = scaleY * 0.5f
        instance.aabbHalfZ = scaleZ * 0.5f

        applyTextureEntry(instance, mesh, textureEntry)
        return true
    }

    private fun getOrCompile(ctx: LumiyaRenderContext, data: MeshData): CompiledMesh? {
        compiled[data.meshId]?.let { return it }
        if (data.faces.isEmpty()) return null
        val bm = bufferManager ?: GLBufferManager(ctx.resourceManager).also { bufferManager = it }
        val vaos = mutableListOf<GLBufferManager.MeshVAO>()
        val bounds = mutableListOf<Float>()
        try {
            for (face in data.faces) {
                if (face.vertexCount == 0 || face.indexCount == 0) continue
                val vao = uploadFace(bm, face) ?: continue
                vaos.add(vao)
                bounds.add(faceMaxExtent(face))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Mesh compile failed for ${data.meshId}: ${e.message}")
            return null
        }
        if (vaos.isEmpty()) return null
        val cm = CompiledMesh(vaos, bounds)
        compiled[data.meshId] = cm
        return cm
    }

    private fun uploadFace(bm: GLBufferManager, face: MeshFace): GLBufferManager.MeshVAO? {
        if (face.positions.size != face.vertexCount * 3) return null
        if (face.normals.size != face.vertexCount * 3) return null
        if (face.uvs.size != face.vertexCount * 2) return null

        // Interleave POS(3) + NORMAL(3) + UV(2) — matches the existing
        // PrimShaderProgram input layout (locations 0/1/2).
        val data = FloatArray(face.vertexCount * 8)
        for (i in 0 until face.vertexCount) {
            data[i * 8 + 0] = face.positions[i * 3 + 0]
            data[i * 8 + 1] = face.positions[i * 3 + 1]
            data[i * 8 + 2] = face.positions[i * 3 + 2]
            data[i * 8 + 3] = face.normals[i * 3 + 0]
            data[i * 8 + 4] = face.normals[i * 3 + 1]
            data[i * 8 + 5] = face.normals[i * 3 + 2]
            data[i * 8 + 6] = face.uvs[i * 2 + 0]
            data[i * 8 + 7] = face.uvs[i * 2 + 1]
        }
        return bm.buildVAO(data, face.indices, listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun faceMaxExtent(face: MeshFace): Float {
        var maxAbs = 0f
        for (i in face.positions.indices) {
            val a = Math.abs(face.positions[i])
            if (a > maxAbs) maxAbs = a
        }
        return maxAbs
    }

    // ── Mutation ─────────────────────────────────────────────────────────

    fun bindTextureToMatchingFaces(id: Long, textureId: UUID, textureHandle: Int) {
        val instance = instances[id] ?: return
        instance.faces.forEach { f ->
            if (f.textureId == textureId) f.textureHandle = textureHandle
        }
    }

    fun removePrim(id: Long) {
        instances.remove(id)
    }

    fun clear() {
        instances.clear()
    }

    fun destroy() {
        instances.clear()
        compiled.values.forEach { mesh ->
            mesh.faces.forEach { vao -> bufferManager?.destroyVAO(vao) }
        }
        compiled.clear()
    }

    fun snapshot(): Collection<MeshInstance> = instances.values

    // ── Draw ─────────────────────────────────────────────────────────────

    fun drawOpaque(ctx: LumiyaRenderContext) {
        if (instances.isEmpty()) return
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )
        for (instance in instances.values) {
            if (instance.isTransparent) continue
            if (!instanceInFrustum(ctx, instance)) continue
            drawInstance(program, instance)
        }
        GLES32.glBindVertexArray(0)
    }

    fun drawTransparent(ctx: LumiyaRenderContext) {
        if (instances.isEmpty()) return
        val program = ctx.primProgram ?: return
        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )
        val sorted = instances.values
            .filter { it.isTransparent && instanceInFrustum(ctx, it) }
            .sortedByDescending {
                val dx = it.modelMatrix[12] - ctx.cameraPositionX
                val dy = it.modelMatrix[13] - ctx.cameraPositionY
                val dz = it.modelMatrix[14] - ctx.cameraPositionZ
                dx * dx + dy * dy + dz * dz
            }
        for (instance in sorted) drawInstance(program, instance)
        GLES32.glBindVertexArray(0)
    }

    private fun drawInstance(
        program: com.linkpoint.render.lumiya.shaders.PrimShaderProgram,
        instance: MeshInstance
    ) {
        val mesh = compiled[instance.meshId] ?: return
        program.setModelMatrix(instance.modelMatrix)
        for ((faceIdx, vao) in mesh.faces.withIndex()) {
            val face = instance.faces.getOrNull(faceIdx) ?: continue
            val texMatrix = buildTexMatrix(face)
            program.setTexMatrix(texMatrix)
            program.setColor(face.colorR, face.colorG, face.colorB, face.colorA)
            program.setUseTexture(face.textureHandle != 0)
            if (face.textureHandle != 0) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, face.textureHandle)
                program.setTextureSampler(0)
            }
            GLES32.glBindVertexArray(vao.vao)
            val type = if (vao.useIntIndices) GLES32.GL_UNSIGNED_INT else GLES32.GL_UNSIGNED_SHORT
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, vao.indexCount, type, 0)
        }
    }

    private fun instanceInFrustum(ctx: LumiyaRenderContext, instance: MeshInstance): Boolean {
        val cx = instance.modelMatrix[12]
        val cy = instance.modelMatrix[13]
        val cz = instance.modelMatrix[14]
        return ctx.frustumCuller.isAABBVisible(
            cx - instance.aabbHalfX, cy - instance.aabbHalfY, cz - instance.aabbHalfZ,
            cx + instance.aabbHalfX, cy + instance.aabbHalfY, cz + instance.aabbHalfZ
        )
    }

    // ── Per-face material assembly ───────────────────────────────────────

    private fun applyTextureEntry(
        instance: MeshInstance,
        mesh: CompiledMesh,
        textureEntry: ByteArray?
    ) {
        val faceCount = mesh.faces.size
        while (instance.faces.size < faceCount) instance.faces.add(FaceMaterial())
        while (instance.faces.size > faceCount) instance.faces.removeAt(instance.faces.lastIndex)

        if (textureEntry == null || textureEntry.isEmpty()) return
        val parsed = try {
            TextureEntryParser.parseFull(textureEntry, faceCount)
        } catch (t: Throwable) { null } ?: return

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
        }
        instance.isTransparent = instance.faces.any { it.colorA < 0.999f }
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
}
