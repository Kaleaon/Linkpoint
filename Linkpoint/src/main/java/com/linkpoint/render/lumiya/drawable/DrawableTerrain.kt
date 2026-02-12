package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager

/**
 * Drawable terrain with 16×16 m patch subdivision.
 *
 * Design lineage: Lumiya `DrawableTerrainPatch.java` + `TerrainPatchGeometry.java`,
 * modernised with VAO-based rendering and 4-layer texture splatting.
 *
 * The SL region is 256×256 m, divided into 16×16 patches (= 256 patches total).
 * Each patch has a 17×17 vertex grid (shared edges with neighbours).
 */
class DrawableTerrain(private val ctx: LumiyaRenderContext) {

    companion object {
        private const val TAG = "DrawableTerrain"

        /** Vertices per patch edge (17 so neighbouring patches share edge). */
        const val PATCH_VERTS = 17

        /** Metres per patch. */
        const val PATCH_SIZE = 16.0f

        /** Number of patches per axis (256 / 16 = 16). */
        const val PATCHES_PER_AXIS = 16

        /** Default height thresholds for texture splatting. */
        val DEFAULT_HEIGHT_THRESHOLDS = floatArrayOf(10f, 30f, 60f, 100f)
    }

    private val bufferManager = GLBufferManager(ctx.resourceManager)
    private val patches = arrayOfNulls<GLBufferManager.MeshVAO>(PATCHES_PER_AXIS * PATCHES_PER_AXIS)
    private val patchModelMatrices = Array(PATCHES_PER_AXIS * PATCHES_PER_AXIS) { FloatArray(16) }

    private var heightmap: FloatArray? = null
    private var hmWidth = 0
    private var hmDepth = 0
    private var isDirty = false

    init {
        // Pre-compute per-patch model matrices
        for (pz in 0 until PATCHES_PER_AXIS) {
            for (px in 0 until PATCHES_PER_AXIS) {
                val idx = pz * PATCHES_PER_AXIS + px
                Matrix.setIdentityM(patchModelMatrices[idx], 0)
                Matrix.translateM(
                    patchModelMatrices[idx], 0,
                    px * PATCH_SIZE, pz * PATCH_SIZE, 0f
                )
            }
        }
    }

    fun updateHeightmap(heightmap: FloatArray, width: Int, depth: Int) {
        this.heightmap = heightmap
        this.hmWidth = width
        this.hmDepth = depth
        this.isDirty = true
    }

    fun draw(ctx: LumiyaRenderContext) {
        if (isDirty) {
            rebuildPatches()
            isDirty = false
        }

        val program = ctx.terrainProgram ?: return
        program.use()
        program.setTexScale(1.0f / PATCH_SIZE)
        program.setHeightThresholds(
            DEFAULT_HEIGHT_THRESHOLDS[0],
            DEFAULT_HEIGHT_THRESHOLDS[1],
            DEFAULT_HEIGHT_THRESHOLDS[2],
            DEFAULT_HEIGHT_THRESHOLDS[3]
        )
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )
        // Bind default terrain texture samplers (0-3)
        program.setTextureSamplers(0, 1, 2, 3)

        for (i in patches.indices) {
            val vao = patches[i] ?: continue
            // Frustum cull per patch
            val px = (i % PATCHES_PER_AXIS) * PATCH_SIZE
            val pz = (i / PATCHES_PER_AXIS) * PATCH_SIZE
            if (!ctx.frustumCuller.isAABBVisible(
                    px, pz, -100f,
                    px + PATCH_SIZE, pz + PATCH_SIZE, 500f
                )) continue

            program.setModelMatrix(patchModelMatrices[i])
            GLES32.glBindVertexArray(vao.vao)
            GLES32.glDrawElements(
                GLES32.GL_TRIANGLES,
                vao.indexCount,
                if (vao.useIntIndices) GLES32.GL_UNSIGNED_INT else GLES32.GL_UNSIGNED_SHORT,
                0
            )
        }
        GLES32.glBindVertexArray(0)
    }

    fun clear() {
        destroyPatches()
        heightmap = null
    }

    fun destroy() {
        destroyPatches()
    }

    // ── Mesh generation ──────────────────────────────────────────────────

    private fun rebuildPatches() {
        destroyPatches()
        val hm = heightmap ?: return

        for (pz in 0 until PATCHES_PER_AXIS) {
            for (px in 0 until PATCHES_PER_AXIS) {
                patches[pz * PATCHES_PER_AXIS + px] = buildPatch(hm, hmWidth, hmDepth, px, pz)
            }
        }
        Log.d(TAG, "Rebuilt ${PATCHES_PER_AXIS * PATCHES_PER_AXIS} terrain patches")
    }

    private fun buildPatch(
        hm: FloatArray, hmW: Int, hmD: Int,
        patchX: Int, patchZ: Int
    ): GLBufferManager.MeshVAO {
        val vertCount = PATCH_VERTS * PATCH_VERTS
        // 3 pos + 3 normal + 2 uv = 8 floats per vertex
        val verts = FloatArray(vertCount * 8)
        val step = PATCH_SIZE / (PATCH_VERTS - 1)

        var vi = 0
        for (z in 0 until PATCH_VERTS) {
            for (x in 0 until PATCH_VERTS) {
                val worldX = patchX * PATCH_SIZE + x * step
                val worldZ = patchZ * PATCH_SIZE + z * step
                val height = sampleHeight(hm, hmW, hmD, worldX, worldZ)

                // Position (local to patch, Z is up)
                verts[vi++] = x * step
                verts[vi++] = z * step
                verts[vi++] = height

                // Normal (computed from height gradient)
                val hL = sampleHeight(hm, hmW, hmD, worldX - step, worldZ)
                val hR = sampleHeight(hm, hmW, hmD, worldX + step, worldZ)
                val hD = sampleHeight(hm, hmW, hmD, worldX, worldZ - step)
                val hU = sampleHeight(hm, hmW, hmD, worldX, worldZ + step)
                var nx = (hL - hR) / (2f * step)
                var ny = (hD - hU) / (2f * step)
                var nz = 1.0f
                val len = Math.sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toFloat()
                nx /= len; ny /= len; nz /= len
                verts[vi++] = nx
                verts[vi++] = ny
                verts[vi++] = nz

                // UV
                verts[vi++] = worldX / PATCH_SIZE
                verts[vi++] = worldZ / PATCH_SIZE
            }
        }

        // Indices (two triangles per quad)
        val quadsPerAxis = PATCH_VERTS - 1
        val indices = ShortArray(quadsPerAxis * quadsPerAxis * 6)
        var ii = 0
        for (z in 0 until quadsPerAxis) {
            for (x in 0 until quadsPerAxis) {
                val bl = (z * PATCH_VERTS + x).toShort()
                val br = (z * PATCH_VERTS + x + 1).toShort()
                val tl = ((z + 1) * PATCH_VERTS + x).toShort()
                val tr = ((z + 1) * PATCH_VERTS + x + 1).toShort()
                indices[ii++] = bl; indices[ii++] = br; indices[ii++] = tl
                indices[ii++] = br; indices[ii++] = tr; indices[ii++] = tl
            }
        }

        return bufferManager.buildVAO(
            verts, indices,
            listOf(0 to 3, 1 to 3, 2 to 2)  // pos(3), normal(3), uv(2)
        )
    }

    private fun sampleHeight(hm: FloatArray, w: Int, d: Int, worldX: Float, worldZ: Float): Float {
        val fx = (worldX / 256f * (w - 1)).coerceIn(0f, (w - 1).toFloat())
        val fz = (worldZ / 256f * (d - 1)).coerceIn(0f, (d - 1).toFloat())
        val ix = fx.toInt().coerceIn(0, w - 2)
        val iz = fz.toInt().coerceIn(0, d - 2)
        val fracX = fx - ix
        val fracZ = fz - iz
        // Bilinear interpolation
        val h00 = hm[iz * w + ix]
        val h10 = hm[iz * w + ix + 1]
        val h01 = hm[(iz + 1) * w + ix]
        val h11 = hm[(iz + 1) * w + ix + 1]
        return (h00 * (1 - fracX) * (1 - fracZ) +
                h10 * fracX * (1 - fracZ) +
                h01 * (1 - fracX) * fracZ +
                h11 * fracX * fracZ)
    }

    private fun destroyPatches() {
        for (i in patches.indices) {
            patches[i]?.let { bufferManager.destroyVAO(it) }
            patches[i] = null
        }
    }
}
