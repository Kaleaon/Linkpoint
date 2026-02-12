package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all SL primitives in the scene and dispatches draw calls.
 *
 * Design lineage: Lumiya `DrawableStore.java` + `DrawableObject.java` +
 * `DrawablePrim.java`, modernised with VAO-per-shape sharing and
 * instanced transforms.
 *
 * Primitive shapes (box, sphere, cylinder, torus, prism, ring) are cached
 * as shared VAOs.  Each prim instance stores only its transform and colour.
 */
class DrawablePrimStore {

    /** Per-prim instance data. */
    data class PrimInstance(
        val id: Long,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        val texMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var colorR: Float = 1f, var colorG: Float = 1f,
        var colorB: Float = 1f, var colorA: Float = 1f,
        var textureHandle: Int = 0,
        var isTransparent: Boolean = false
    )

    private val prims = ConcurrentHashMap<Long, PrimInstance>()

    // Shared shape VAOs (lazily initialised on first draw)
    private var shapeVAOs: Map<String, GLBufferManager.MeshVAO>? = null
    private var bufferManager: GLBufferManager? = null

    // ── Mutation ─────────────────────────────────────────────────────────

    fun addPrim(id: Long, posX: Float, posY: Float, posZ: Float) {
        val instance = PrimInstance(id = id)
        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        prims[id] = instance
    }

    fun removePrim(id: Long) {
        prims.remove(id)
    }

    fun clear() {
        prims.clear()
    }

    fun destroy() {
        prims.clear()
        shapeVAOs?.values?.forEach { bufferManager?.destroyVAO(it) }
        shapeVAOs = null
    }

    // ── Draw ─────────────────────────────────────────────────────────────

    fun drawOpaque(ctx: LumiyaRenderContext) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        val boxVAO = shapeVAOs?.get("box") ?: return

        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        GLES32.glBindVertexArray(boxVAO.vao)
        for (prim in prims.values) {
            if (prim.isTransparent) continue
            program.setModelMatrix(prim.modelMatrix)
            program.setTexMatrix(prim.texMatrix)
            program.setColor(prim.colorR, prim.colorG, prim.colorB, prim.colorA)
            program.setUseTexture(prim.textureHandle != 0)
            if (prim.textureHandle != 0) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, prim.textureHandle)
                program.setTextureSampler(0)
            }
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, boxVAO.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    fun drawTransparent(ctx: LumiyaRenderContext) {
        ensureShapes(ctx)
        val program = ctx.primProgram ?: return
        val boxVAO = shapeVAOs?.get("box") ?: return

        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        // Sort back-to-front for correct alpha blending
        val sorted = prims.values.filter { it.isTransparent }
            .sortedByDescending { distanceToCamera(ctx, it) }

        GLES32.glBindVertexArray(boxVAO.vao)
        for (prim in sorted) {
            program.setModelMatrix(prim.modelMatrix)
            program.setTexMatrix(prim.texMatrix)
            program.setColor(prim.colorR, prim.colorG, prim.colorB, prim.colorA)
            program.setUseTexture(prim.textureHandle != 0)
            if (prim.textureHandle != 0) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, prim.textureHandle)
                program.setTextureSampler(0)
            }
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, boxVAO.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    // ── Shared shape geometry ────────────────────────────────────────────

    private fun ensureShapes(ctx: LumiyaRenderContext) {
        if (shapeVAOs != null) return
        val bm = GLBufferManager(ctx.resourceManager)
        bufferManager = bm
        shapeVAOs = mapOf(
            "box" to buildBox(bm),
            "sphere" to buildSphere(bm),
            "cylinder" to buildCylinder(bm)
        )
    }

    private fun distanceToCamera(ctx: LumiyaRenderContext, prim: PrimInstance): Float {
        val dx = prim.modelMatrix[12] - ctx.cameraPositionX
        val dy = prim.modelMatrix[13] - ctx.cameraPositionY
        val dz = prim.modelMatrix[14] - ctx.cameraPositionZ
        return dx * dx + dy * dy + dz * dz
    }

    // ── Box (unit cube) ──────────────────────────────────────────────────

    private fun buildBox(bm: GLBufferManager): GLBufferManager.MeshVAO {
        // 24 vertices (unique normals per face), 36 indices
        val h = 0.5f
        // Format: pos(3) + normal(3) + uv(2) = 8 floats per vertex
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

    // ── Sphere (UV sphere) ───────────────────────────────────────────────

    private fun buildSphere(bm: GLBufferManager, segments: Int = 16, rings: Int = 12): GLBufferManager.MeshVAO {
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
                // pos
                verts.add(x); verts.add(y); verts.add(z)
                // normal (same as pos for unit sphere)
                val nx = cosTheta * sinPhi; val ny = sinTheta * sinPhi; val nz = cosPhi
                verts.add(nx); verts.add(ny); verts.add(nz)
                // uv
                verts.add(s.toFloat() / segments)
                verts.add(r.toFloat() / rings)
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
        return bm.buildVAO(
            verts.toFloatArray(), indices.toShortArray(),
            listOf(0 to 3, 1 to 3, 2 to 2)
        )
    }

    // ── Cylinder ─────────────────────────────────────────────────────────

    private fun buildCylinder(bm: GLBufferManager, segments: Int = 16): GLBufferManager.MeshVAO {
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
            // Bottom vertex
            verts.addAll(listOf(x, y, -h, nx, ny, 0f, u, 0f))
            // Top vertex
            verts.addAll(listOf(x, y, h, nx, ny, 0f, u, 1f))
        }
        for (i in 0 until segments) {
            val b0 = (i * 2).toShort()
            val t0 = (i * 2 + 1).toShort()
            val b1 = (i * 2 + 2).toShort()
            val t1 = (i * 2 + 3).toShort()
            indices.addAll(listOf(b0, b1, t0, b1, t1, t0))
        }

        return bm.buildVAO(
            verts.toFloatArray(), indices.toShortArray(),
            listOf(0 to 3, 1 to 3, 2 to 2)
        )
    }
}
