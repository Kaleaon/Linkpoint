package com.linkpoint.render.lumiya.core

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import com.linkpoint.render.lumiya.drawable.*

/**
 * Main renderer implementing the Lumiya render pipeline on modern GL ES 3.2.
 *
 * Design lineage: Lumiya `WorldViewRenderer.java`
 *
 * Render passes (per frame):
 *   1. Preparation – update camera, frustum, animations, delta-time
 *   2. Begin FXAA FBO (if enabled)
 *   3. Clear buffers
 *   4. Opaque pass – terrain, opaque prims (front-to-back)
 *   5. Avatar pass – skeletal meshes
 *   6. Sky pass – dome + stars (drawn *behind* everything via depth tricks)
 *   7. Transparent pass – alpha prims (back-to-front)
 *   8. Water pass – animated water plane
 *   9. Particle pass – billboard particles
 *  10. HUD pass – orthographic overlays
 *  11. FXAA resolve to default FBO (if enabled)
 *  12. Post-frame cleanup
 */
class LumiyaRenderer : RenderEngineProvider {

    companion object {
        private const val TAG = "LumiyaRenderer"
    }

    override val engineName = "Lumiya GL ES 3.2"
    override var isInitialized = false; private set
    override var viewportWidth = 0; private set
    override var viewportHeight = 0; private set
    override var frameCount: Long = 0L; private set

    // ── Internals ────────────────────────────────────────────────────────

    private lateinit var ctx: LumiyaRenderContext

    // Drawable subsystems
    private var terrainDrawable: DrawableTerrain? = null
    private var waterDrawable: DrawableWater? = null
    private var skyDrawable: DrawableSky? = null
    private var primStore = DrawablePrimStore()
    private var avatarStore = DrawableAvatarStore()
    private var particleManager: DrawableParticleManager? = null

    // Full-screen quad VAO for FXAA resolve
    private var quadVAO = 0
    private var quadVBO = 0

    // =====================================================================
    // Lifecycle
    // =====================================================================

    override fun initialize(context: Context, surface: Surface, width: Int, height: Int): Boolean {
        if (isInitialized) return true
        Log.i(TAG, "Initialising Lumiya renderer ($width x $height)")

        try {
            ctx = LumiyaRenderContext()
            if (!ctx.initialize()) {
                Log.e(TAG, "Render context initialisation failed")
                return false
            }

            viewportWidth = width
            viewportHeight = height
            ctx.aspectRatio = width.toFloat() / height.toFloat()

            // Global GL state
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
            GLES32.glDepthFunc(GLES32.GL_LEQUAL)
            GLES32.glEnable(GLES32.GL_CULL_FACE)
            GLES32.glCullFace(GLES32.GL_BACK)
            GLES32.glFrontFace(GLES32.GL_CCW)
            GLES32.glEnable(GLES32.GL_BLEND)
            GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)

            // Clear colour – SL default sky blue
            GLES32.glClearColor(0.24f, 0.44f, 0.76f, 1.0f)

            // Build FXAA FBO
            if (ctx.fxaaEnabled) {
                ctx.createFXAAFramebuffer(width, height)
            }

            // Build full-screen quad for post-processing
            createFullScreenQuad()

            avatarStore = DrawableAvatarStore { AvatarMeshAssetLoader.loadDefaultAvatarMesh(context) }

            // Initialise subsystems
            terrainDrawable = DrawableTerrain(ctx)
            waterDrawable = DrawableWater(ctx)
            skyDrawable = DrawableSky(ctx)
            particleManager = DrawableParticleManager(ctx)

            ctx.updateCamera()

            isInitialized = true
            Log.i(TAG, "Lumiya renderer initialised  (GPU: ${ctx.gpuRenderer})")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialise Lumiya renderer", e)
            return false
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        ctx.aspectRatio = width.toFloat() / height.toFloat()
        GLES32.glViewport(0, 0, width, height)
        if (ctx.fxaaEnabled) ctx.createFXAAFramebuffer(width, height)
    }

    override fun onSurfaceDestroyed() {
        // Surface lost but engine not destroyed – resources remain valid
        Log.w(TAG, "Surface destroyed")
    }

    // =====================================================================
    // Main render loop
    // =====================================================================

    override fun renderFrame() {
        if (!isInitialized) return

        // ── 1. Preparation ───────────────────────────────────────────────
        ctx.beginFrame()
        ctx.updateCamera()
        ctx.uploadGlobalUBO()

        // ── 2. Begin FXAA FBO ────────────────────────────────────────────
        if (ctx.fxaaEnabled && ctx.fxaaFramebuffer != 0) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, ctx.fxaaFramebuffer)
            GLES32.glViewport(0, 0, ctx.fxaaWidth, ctx.fxaaHeight)
        }

        // ── 3. Clear ─────────────────────────────────────────────────────
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_STENCIL_BUFFER_BIT)

        // ── 4. Opaque pass ───────────────────────────────────────────────
        GLES32.glDepthMask(true)
        GLES32.glDisable(GLES32.GL_BLEND)

        terrainDrawable?.draw(ctx)

        primStore.drawOpaque(ctx)

        // ── 5. Avatar pass ───────────────────────────────────────────────
        avatarStore.draw(ctx)

        // ── 6. Sky pass (render behind everything) ───────────────────────
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glDepthMask(false)
        skyDrawable?.draw(ctx)
        GLES32.glDepthMask(true)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)

        // ── 7. Transparent pass ──────────────────────────────────────────
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)
        primStore.drawTransparent(ctx)

        // ── 8. Water pass ────────────────────────────────────────────────
        waterDrawable?.draw(ctx)

        // ── 9. Particle pass ─────────────────────────────────────────────
        particleManager?.draw(ctx)

        // ── 10. HUD pass (not implemented yet) ───────────────────────────

        // ── 11. FXAA resolve ─────────────────────────────────────────────
        if (ctx.fxaaEnabled && ctx.fxaaFramebuffer != 0) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
            GLES32.glViewport(0, 0, viewportWidth, viewportHeight)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
            GLES32.glDisable(GLES32.GL_DEPTH_TEST)
            resolveFXAA()
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        }

        // ── 12. Post-frame ───────────────────────────────────────────────
        ctx.resourceManager.cleanup()
        frameCount++
    }

    // =====================================================================
    // Camera
    // =====================================================================

    override fun setCameraPosition(x: Float, y: Float, z: Float) {
        ctx.cameraPositionX = x; ctx.cameraPositionY = y; ctx.cameraPositionZ = z
    }

    override fun setCameraTarget(x: Float, y: Float, z: Float) {
        ctx.cameraTargetX = x; ctx.cameraTargetY = y; ctx.cameraTargetZ = z
    }

    override fun setFieldOfView(fovDegrees: Float) {
        ctx.fovDegrees = fovDegrees
    }

    override fun setDrawDistance(distance: Float) {
        ctx.drawDistance = distance
    }

    // =====================================================================
    // Scene manipulation
    // =====================================================================

    override fun addObject(id: Long, posX: Float, posY: Float, posZ: Float) {
        primStore.addPrim(id, posX, posY, posZ)
    }

    override fun removeObject(id: Long) {
        primStore.removePrim(id)
    }

    override fun updateTerrain(heightmap: FloatArray, width: Int, depth: Int) {
        terrainDrawable?.updateHeightmap(heightmap, width, depth)
    }

    override fun clearScene() {
        primStore.clear()
        avatarStore.clear()
        terrainDrawable?.clear()
    }

    // =====================================================================
    // Shutdown
    // =====================================================================

    override fun shutdown() {
        if (!isInitialized) return
        Log.i(TAG, "Shutting down Lumiya renderer")
        terrainDrawable?.destroy()
        waterDrawable?.destroy()
        skyDrawable?.destroy()
        particleManager?.destroy()
        primStore.destroy()
        avatarStore.destroy()
        destroyFullScreenQuad()
        ctx.shutdown()
        isInitialized = false
    }

    // =====================================================================
    // FXAA helpers
    // =====================================================================

    private fun resolveFXAA() {
        val program = ctx.fxaaProgram ?: return
        program.use()
        program.setTexelSize(1.0f / ctx.fxaaWidth, 1.0f / ctx.fxaaHeight)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, ctx.fxaaColorTexture)
        program.setTextureSampler(0)

        GLES32.glBindVertexArray(quadVAO)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, 4)
        GLES32.glBindVertexArray(0)
    }

    private fun createFullScreenQuad() {
        // NDC quad: position (x,y), texcoord (u,v)
        val vertices = floatArrayOf(
            -1f, -1f, 0f, 0f,
             1f, -1f, 1f, 0f,
            -1f,  1f, 0f, 1f,
             1f,  1f, 1f, 1f
        )
        val buffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        buffer.flip()

        val vaoBuf = IntArray(1)
        GLES32.glGenVertexArrays(1, vaoBuf, 0)
        quadVAO = vaoBuf[0]
        val vboBuf = IntArray(1)
        GLES32.glGenBuffers(1, vboBuf, 0)
        quadVBO = vboBuf[0]

        GLES32.glBindVertexArray(quadVAO)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, quadVBO)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, vertices.size * 4, buffer, GLES32.GL_STATIC_DRAW)
        // position
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 2, GLES32.GL_FLOAT, false, 16, 0)
        // texcoord
        GLES32.glEnableVertexAttribArray(1)
        GLES32.glVertexAttribPointer(1, 2, GLES32.GL_FLOAT, false, 16, 8)
        GLES32.glBindVertexArray(0)
    }

    private fun destroyFullScreenQuad() {
        if (quadVAO != 0) { GLES32.glDeleteVertexArrays(1, intArrayOf(quadVAO), 0); quadVAO = 0 }
        if (quadVBO != 0) { GLES32.glDeleteBuffers(1, intArrayOf(quadVBO), 0); quadVBO = 0 }
    }
}
