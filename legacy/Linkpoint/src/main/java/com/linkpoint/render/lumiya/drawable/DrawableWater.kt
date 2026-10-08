package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager

/**
 * Animated water surface covering the SL region at water height.
 *
 * Design lineage: Lumiya `TerrainPatchGeometry.java` water rendering +
 * `WaterProgram.java`, modernised with Gerstner-wave vertex displacement
 * in GLSL ES 3.20.
 *
 * The water is a single 256×256 m grid subdivided into 64×64 quads for
 * sufficient wave resolution.
 */
class DrawableWater(private val ctx: LumiyaRenderContext) {

    companion object {
        private const val GRID_SIZE = 64   // quads per axis
        private const val REGION_SIZE = 256.0f
    }

    private val bufferManager = GLBufferManager(ctx.resourceManager)
    private var mesh: GLBufferManager.MeshVAO? = null
    private val modelMatrix = FloatArray(16)

    // Default Gerstner wave parameters (4 layers)
    private val frequencies = floatArrayOf(0.08f, 0.12f, 0.06f, 0.15f)
    private val amplitudes = floatArrayOf(0.3f, 0.2f, 0.15f, 0.1f)
    private val phases = floatArrayOf(1.2f, 0.8f, 1.5f, 0.6f)
    private val directions = floatArrayOf(
        0.7f, 0.7f,    // wave 0
        -0.5f, 0.86f,  // wave 1
        1.0f, 0.0f,    // wave 2
        -0.3f, -0.95f  // wave 3
    )

    init {
        buildMesh()
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun draw(ctx: LumiyaRenderContext) {
        val m = mesh ?: return
        val program = ctx.waterProgram ?: return

        // Position water at configured height
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, ctx.waterHeight)

        program.use()
        program.setModelMatrix(modelMatrix)
        program.setColor(0.05f, 0.18f, 0.35f, 0.75f)
        program.setTime(ctx.waterTime)
        program.setWaveParams(frequencies, amplitudes, phases, directions)
        program.setSunDirection(ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ)
        program.setCameraPosition(ctx.cameraPositionX, ctx.cameraPositionY, ctx.cameraPositionZ)

        GLES32.glEnable(GLES32.GL_BLEND)
        // LL viewer LLDrawPoolWater uses LLGLDepthTest(GL_TRUE, GL_FALSE)
        // for transparent water: depth test on, depth write off so any
        // transparent geometry behind the water plane still composites
        // correctly. Without disabling depth-write, alpha prims under
        // water would be punched out by the water's depth.
        GLES32.glDepthMask(false)
        GLES32.glBlendFuncSeparate(
            GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
            GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
        )

        GLES32.glBindVertexArray(m.vao)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, m.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        GLES32.glBindVertexArray(0)
        GLES32.glDepthMask(true)
    }

    fun destroy() {
        mesh?.let { bufferManager.destroyVAO(it) }
        mesh = null
    }

    // ── Mesh generation ──────────────────────────────────────────────────

    private fun buildMesh() {
        val vertsPerAxis = GRID_SIZE + 1
        val step = REGION_SIZE / GRID_SIZE
        // 3 pos floats per vertex (Y displacement computed in shader)
        val verts = FloatArray(vertsPerAxis * vertsPerAxis * 3)
        var vi = 0
        for (z in 0..GRID_SIZE) {
            for (x in 0..GRID_SIZE) {
                verts[vi++] = x * step   // X
                verts[vi++] = z * step   // Y
                verts[vi++] = 0f         // Z (displaced by shader)
            }
        }

        val indices = ShortArray(GRID_SIZE * GRID_SIZE * 6)
        var ii = 0
        for (z in 0 until GRID_SIZE) {
            for (x in 0 until GRID_SIZE) {
                val bl = (z * vertsPerAxis + x).toShort()
                val br = (z * vertsPerAxis + x + 1).toShort()
                val tl = ((z + 1) * vertsPerAxis + x).toShort()
                val tr = ((z + 1) * vertsPerAxis + x + 1).toShort()
                indices[ii++] = bl; indices[ii++] = br; indices[ii++] = tl
                indices[ii++] = br; indices[ii++] = tr; indices[ii++] = tl
            }
        }

        mesh = bufferManager.buildVAO(
            verts, indices,
            listOf(0 to 3)  // position only; normals computed in shader
        )
    }
}
