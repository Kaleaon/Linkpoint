package com.linkpoint.render.lumiya.core

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import com.linkpoint.assets.TextureFormatPolicy
import com.linkpoint.avatar.AttachmentPoints
import com.linkpoint.render.RenderDiagnostics
import com.linkpoint.render.lumiya.drawable.*
import com.linkpoint.render.lumiya.glres.GLTextureCache
import com.linkpoint.render.lumiya.spatial.OcclusionQuerySet
import java.util.UUID

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

    @Volatile private var glThreadId: Long = Long.MIN_VALUE
    @Volatile private var glThreadName: String = "<unset>"

    // Drawable subsystems
    private var terrainDrawable: DrawableTerrain? = null
    private var waterDrawable: DrawableWater? = null
    private var skyDrawable: DrawableSky? = null
    private var primStore = DrawablePrimStore()
    private var meshStore = DrawableMeshStore()
    private var hudStore = DrawableHudStore()
    private var hoverTextStore = DrawableHoverText()
    private var avatarStore = DrawableAvatarStore()
    private var particleManager: DrawableParticleManager? = null

    // Full-screen quad VAO for FXAA resolve
    private var quadVAO = 0
    private var quadVBO = 0
    private val hudViewMatrix = FloatArray(16)
    private val hudProjectionMatrix = FloatArray(16)
    private var worldPassEnabled = true

    /**
     * Hook invoked at the top of every [renderFrame] on the GL thread,
     * before any draw passes. Receives the seconds elapsed since the
     * previous frame. Use it for per-frame pose ticks, animation
     * advances, or scene mutations that need to land before draws.
     */
    @Volatile
    var onBeforeFrame: ((deltaTimeSeconds: Float) -> Unit)? = null

    /**
     * When true, expensive optional passes (particles, FXAA resolve)
     * are skipped to keep the frame budget under control during touch
     * gestures. Mirrors Lumiya's "responsive mode" used while flinging.
     * Toggled by [beginInteractiveThrottle] / [endInteractiveThrottle].
     */
    @Volatile
    private var interactiveThrottle: Boolean = false

    /** Toggle the responsive throttle on. Safe to call from any thread. */
    fun beginInteractiveThrottle() { interactiveThrottle = true }

    /** Toggle the responsive throttle off. Safe to call from any thread. */
    fun endInteractiveThrottle() { interactiveThrottle = false }

    /** Read-only diagnostic. */
    fun isInteractiveThrottling(): Boolean = interactiveThrottle

    /**
     * Hardware occlusion-query pool. Off by default — the renderer is
     * already CPU-bound on most mobile GPUs and adding query overhead
     * to every draw can be a net loss. Toggle on for scenes with heavy
     * indoor/cluttered geometry where overdraw dominates.
     */
    val occlusionQueries = OcclusionQuerySet()

    // =====================================================================
    // Lifecycle
    // =====================================================================

    override fun initialize(context: Context, surface: Surface, width: Int, height: Int): Boolean {
        requireGlThread("initialize")
        if (isInitialized) return true
        Log.i(TAG, "Initialising Lumiya renderer ($width x $height)")

        val initStartedAt = System.currentTimeMillis()
        try {
            // Probe the device's graphics drivers before we even
            // touch GL state. The probe is purely PackageManager /
            // ActivityManager — no GL calls — so a misconfigured
            // device can't crash us here. The result is fed into
            // the context so the resolved feature flags reflect
            // both the pre-context platform info and the
            // post-context extension list.
            val driverProfile = com.linkpoint.render.driver.GraphicsDriverProbe.probe(context)
            ctx = LumiyaRenderContext { apiName -> requireGlThread("LumiyaRenderContext.$apiName") }
            ctx.driverProfile = driverProfile
            if (!ctx.initialize()) {
                Log.e(TAG, "Render context initialisation failed")
                RenderDiagnostics.glInitFailed(
                    RuntimeException("LumiyaRenderContext.initialize returned false")
                )
                return false
            }
            // Surface the GPU vendor/renderer/version strings the context
            // captured during queryGPUCapabilities() — useful for spotting
            // device-specific driver issues from the session log alone.
            RenderDiagnostics.glContextInfo(
                vendor = ctx.gpuVendor,
                renderer = ctx.gpuRenderer,
                version = "GL ES ${ctx.glVersion}",
                maxTextureSize = ctx.maxTextureSize
            )
            // Detailed driver-capability summary (Vulkan, AEP,
            // extension count) goes to its own diagnostic so the
            // session log captures the device profile separately
            // from the GL context info.
            RenderDiagnostics.glDriverCapabilities(
                profileSummary = driverProfile.summary(),
                capsSummary = ctx.gpuCapabilities.summary(),
                featureFlags = ctx.featureFlags.toString()
            )

            // Hard floor: refuse to spin up the renderer on a device
            // that can't reach our minimum GLES level. The caller
            // can fall back to a software path or display an
            // unsupported-device message rather than ship a black
            // frame.
            if (ctx.gpuCapabilities.glVersion < 30) {
                Log.e(TAG, "Device GLES version (${ctx.gpuCapabilities.glVersion}) below required 3.0; aborting")
                RenderDiagnostics.glInitFailed(
                    RuntimeException("GLES 3.0+ required; device reports ${ctx.gpuCapabilities.glVersion}")
                )
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
            // Use separate alpha blend so rendering to an off-screen
            // FBO (FXAA, post-process) preserves alpha channel
            // correctly. Mirrors LLDrawPoolAlpha's blend setup in the
            // LL viewer / Singularity:
            //   color: (SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
            //   alpha: (ZERO,      ONE_MINUS_SRC_ALPHA)
            // glBlendFunc would clobber the alpha channel and the FXAA
            // resolve would composite incorrectly.
            GLES32.glBlendFuncSeparate(
                GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
                GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
            )

            // Clear colour – SL default sky blue
            GLES32.glClearColor(0.24f, 0.44f, 0.76f, 1.0f)

            // Build FXAA FBO. The user toggle ([fxaaEnabled]) is the
            // intent; the resolved feature flag is the floor — on a
            // driver that can't deliver FXAA (no FBOs, tiny max
            // texture), we silently disable the user's preference
            // and leave the buffer never-created. The flag winning
            // over the user toggle is intentional: enabling FXAA on
            // a driver that can't do it would produce a black
            // screen, which is strictly worse than aliased pixels.
            if (ctx.fxaaEnabled && ctx.featureFlags.fxaa) {
                ctx.createFXAAFramebuffer(width, height)
            } else if (ctx.fxaaEnabled && !ctx.featureFlags.fxaa) {
                Log.i(TAG, "FXAA requested but unsupported on this driver; running without")
                ctx.fxaaEnabled = false
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
            rebuildHudMatrices(width, height)
            hudStore.setViewportSize(width, height)

            isInitialized = true
            Log.i(TAG, "Lumiya renderer initialised  (GPU: ${ctx.gpuRenderer})")
            RenderDiagnostics.glInitDone(System.currentTimeMillis() - initStartedAt)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialise Lumiya renderer", e)
            RenderDiagnostics.glInitFailed(e)
            return false
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        requireGlThread("onSurfaceChanged")
        viewportWidth = width
        viewportHeight = height
        ctx.aspectRatio = width.toFloat() / height.toFloat()
        GLES32.glViewport(0, 0, width, height)
        if (ctx.fxaaEnabled) ctx.createFXAAFramebuffer(width, height)
        ctx.resizeWaterFramebuffers(width, height)
        rebuildHudMatrices(width, height)
        hudStore.setViewportSize(width, height)
    }

    /**
     * Toggle planar water reflection / refraction at runtime. When
     * enabled, [LumiyaRenderContext.mWaterRef] / [mWaterDis] become
     * non-zero and the water pass is free to bind them as samplers
     * for a mirror-reflection look. Mirrors LL viewer's
     * `RenderWaterReflections` debug setting.
     *
     * Returns true when the toggle took effect, false when the
     * device's [com.linkpoint.render.driver.FeatureFlags] floor
     * blocks the feature. UI surfaces should check the return value
     * and reflect "unavailable" rather than silently lying to the
     * user.
     */
    fun setWaterPlanarReflectionsEnabled(enabled: Boolean): Boolean {
        requireGlThread("setWaterPlanarReflectionsEnabled")
        if (enabled && !ctx.featureFlags.waterPlanarReflections) {
            Log.i(TAG, "Water planar reflections requested but unsupported on this driver; ignoring")
            return false
        }
        ctx.setWaterPlanarReflectionsEnabled(enabled, viewportWidth, viewportHeight)
        return true
    }

    /**
     * Snapshot of the resolved driver capabilities. Read-only mirror
     * of [LumiyaRenderContext.featureFlags] / [gpuCapabilities] so
     * callers (settings UI, telemetry) can introspect what features
     * are live without reaching into the context directly.
     */
    fun driverCapabilities(): com.linkpoint.render.driver.GpuCapabilities = ctx.gpuCapabilities

    /**
     * Snapshot of the resolved feature flags for this context.
     * @see setWaterPlanarReflectionsEnabled for an example of how
     *      a UI gate should consult these.
     */
    fun featureFlags(): com.linkpoint.render.driver.FeatureFlags = ctx.featureFlags

    override fun onSurfaceDestroyed() {
        requireGlThread("onSurfaceDestroyed")
        // Surface lost but engine not destroyed – resources remain valid
        Log.w(TAG, "Surface destroyed")
        RenderDiagnostics.glSurfaceDestroyed()
    }

    // =====================================================================
    // Main render loop
    // =====================================================================

    override fun renderFrame() {
        requireGlThread("renderFrame")
        if (!isInitialized) return

        // ── 1. Preparation ───────────────────────────────────────────────
        ctx.beginFrame()
        // Producer hook: animator ticks, joint UBO uploads, scene
        // mutations queued from non-GL threads. Runs before camera/UBO
        // refresh so pose updates are visible to the first pass.
        try {
            onBeforeFrame?.invoke(ctx.deltaTime)
        } catch (t: Throwable) {
            Log.v(TAG, "onBeforeFrame hook threw: ${t.message}")
        }
        ctx.updateCamera()
        ctx.uploadGlobalUBO()

        // ── 2. Begin FXAA FBO ────────────────────────────────────────────
        // Skip FXAA during interactive throttle so we save the offscreen
        // pass + resolve cost — aliasing during a fling is barely visible.
        val fxaaActive = ctx.fxaaEnabled && ctx.fxaaFramebuffer != 0 && !interactiveThrottle
        if (fxaaActive) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, ctx.fxaaFramebuffer)
            GLES32.glViewport(0, 0, ctx.fxaaWidth, ctx.fxaaHeight)
        }

        // ── 3. Clear ─────────────────────────────────────────────────────
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_STENCIL_BUFFER_BIT)

        val framePlan = LumiyaFramePlanner.createPlan(
            worldPassEnabled = worldPassEnabled,
            hasHudElements = hudStore.hasElements(),
            interactiveThrottle = interactiveThrottle
        )
        framePlan.forEach { pass ->
            when (pass) {
                LumiyaRenderPass.WORLD_OPAQUE -> {
                    GLES32.glDepthMask(true)
                    GLES32.glDisable(GLES32.GL_BLEND)
                    terrainDrawable?.draw(ctx)
                    primStore.drawOpaque(
                        ctx,
                        occlusion = if (occlusionQueries.isEnabled()) occlusionQueries else null
                    )
                    meshStore.drawOpaque(ctx)
                }
                LumiyaRenderPass.WORLD_AVATAR -> avatarStore.draw(ctx)
                LumiyaRenderPass.WORLD_SKY -> {
                    GLES32.glDepthFunc(GLES32.GL_LEQUAL)
                    GLES32.glDepthMask(false)
                    skyDrawable?.draw(ctx)
                    GLES32.glDepthMask(true)
                    GLES32.glDepthFunc(GLES32.GL_LEQUAL)
                }
                LumiyaRenderPass.WORLD_TRANSPARENT -> {
                    GLES32.glEnable(GLES32.GL_BLEND)
                    GLES32.glBlendFuncSeparate(
                        GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
                        GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
                    )
                    primStore.drawTransparent(ctx)
                    meshStore.drawTransparent(ctx)
                    // Hover text overlays the world but underneath HUD;
                    // depth-test true so labels behind objects partially
                    // occlude, depth-write false so labels don't break
                    // following passes.
                    GLES32.glDepthMask(false)
                    hoverTextStore.draw(ctx, viewportWidth, viewportHeight)
                    GLES32.glDepthMask(true)
                }
                LumiyaRenderPass.WORLD_EMISSIVE -> primStore.drawEmissive(ctx)
                LumiyaRenderPass.WORLD_WATER -> waterDrawable?.draw(ctx)
                LumiyaRenderPass.WORLD_PARTICLES -> particleManager?.draw(ctx)
                LumiyaRenderPass.HUD -> renderHudPass()
            }
        }

        // ── 11. FXAA resolve ─────────────────────────────────────────────
        if (fxaaActive) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
            GLES32.glViewport(0, 0, viewportWidth, viewportHeight)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
            GLES32.glDisable(GLES32.GL_DEPTH_TEST)
            resolveFXAA()
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        }

        // ── 12. Post-frame ───────────────────────────────────────────────
        // Harvest any occlusion-query results that became available this
        // frame; the next frame's draw decisions will read them.
        occlusionQueries.harvest()
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
        hudStore.removeHudPrim(id)
    }

    override fun updateTerrain(heightmap: FloatArray, width: Int, depth: Int) {
        terrainDrawable?.updateHeightmap(heightmap, width, depth)
    }

    override fun clearScene() {
        primStore.clear()
        meshStore.clear()
        hudStore.clear()
        hoverTextStore.clear()
        avatarStore.clear()
        terrainDrawable?.clear()
    }

    /** Set / update the hover text shown above [primId]. Empty string clears. */
    fun setHoverText(primId: Long, text: String, r: Float = 1f, g: Float = 1f, b: Float = 1f, a: Float = 1f) {
        requireGlThread("setHoverText")
        if (text.isBlank()) {
            hoverTextStore.removeLabel(primId)
            return
        }
        val center = primStore.snapshot().firstOrNull { it.id == primId }
        if (center != null) {
            hoverTextStore.upsertLabel(
                primId, text,
                center.modelMatrix[12],
                center.modelMatrix[13],
                center.modelMatrix[14] + center.aabbHalfZ + 0.2f,
                r, g, b, a
            )
        }
    }

    // =====================================================================
    // Producer-side entry points (called from non-render threads via
    // LumiyaGLSurfaceView.runOnGlThread { ... }, or directly when already
    // on the GL thread). These let the protocol layer feed scene state
    // into the engine without the producer needing to know about VAOs,
    // UBOs, shaders, or sRGB.
    // =====================================================================

    /**
     * Upload [bitmap] keyed by the SL asset [textureId] and bind the
     * resulting GL handle to every face on [primId] that referenced
     * this UUID. If the texture is already cached the upload is
     * skipped. Must be called on the GL thread.
     */
    fun uploadTextureForPrim(
        primId: Long,
        textureId: UUID,
        bitmap: Bitmap,
        semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO
    ) {
        requireGlThread("uploadTextureForPrim")
        if (!isInitialized) return
        val handle = ctx.textureCache.put(textureId, bitmap, semantic)
        primStore.bindTextureToMatchingFaces(primId, textureId, handle)
        meshStore.bindTextureToMatchingFaces(primId, textureId, handle)
    }

    /**
     * Insert / update a prim with full ObjectUpdate metadata. Routes
     * shape, scale, rotation, and per-face texture entries through to
     * the prim store so the prim picks up its real geometry instead of
     * a default unit box.
     */
    fun upsertPrim(
        id: Long,
        posX: Float, posY: Float, posZ: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        rotation: FloatArray? = null,
        shapeParams: com.linkpoint.protocol.messages.PrimShapeParams =
            com.linkpoint.protocol.messages.PrimShapeParams.DEFAULT,
        textureEntry: ByteArray? = null
    ): Boolean {
        requireGlThread("upsertPrim")
        if (!isInitialized) return false
        primStore.upsertPrim(
            id = id,
            posX = posX, posY = posY, posZ = posZ,
            scaleX = scaleX, scaleY = scaleY, scaleZ = scaleZ,
            rotation = rotation,
            shapeParams = shapeParams,
            textureEntry = textureEntry
        )
        return true
    }

    /**
     * Insert / update a mesh-asset prim. Compiles the mesh data into
     * VAOs (cached by mesh UUID so duplicate references share GPU
     * buffers) and binds per-face texture entries. Returns true if the
     * mesh was attached, false if compilation failed (e.g. malformed
     * mesh asset).
     */
    fun upsertMeshPrim(
        id: Long,
        posX: Float, posY: Float, posZ: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        rotation: FloatArray? = null,
        meshData: com.linkpoint.assets.MeshData,
        textureEntry: ByteArray? = null
    ): Boolean {
        requireGlThread("upsertMeshPrim")
        if (!isInitialized) return false
        return meshStore.upsertMeshPrim(
            id = id,
            ctx = ctx,
            posX = posX, posY = posY, posZ = posZ,
            scaleX = scaleX, scaleY = scaleY, scaleZ = scaleZ,
            rotation = rotation,
            meshData = meshData,
            textureEntry = textureEntry
        )
    }

    /** Look up the default-face texture UUID currently bound to a prim. */
    fun primDefaultTextureId(primId: Long): java.util.UUID =
        primStore.getDefaultTextureId(primId)

    /** Pick the closest visible prim under [screenX], [screenY] (px). */
    fun pickPrim(screenX: Float, screenY: Float): Long? {
        requireGlThread("pickPrim")
        if (!isInitialized) return null
        val (origin, dir) = com.linkpoint.render.lumiya.picking.GLRayTrace.screenToWorldRay(
            screenX, screenY,
            viewportWidth, viewportHeight,
            ctx.viewMatrix, ctx.projectionMatrix
        )
        var bestId: Long? = null
        var bestT = Float.MAX_VALUE
        for (prim in primStore.snapshot()) {
            val cx = prim.modelMatrix[12]
            val cy = prim.modelMatrix[13]
            val cz = prim.modelMatrix[14]
            val t = rayAabbDistance(
                origin, dir,
                cx - prim.aabbHalfX, cy - prim.aabbHalfY, cz - prim.aabbHalfZ,
                cx + prim.aabbHalfX, cy + prim.aabbHalfY, cz + prim.aabbHalfZ
            )
            if (t < bestT) { bestT = t; bestId = prim.id }
        }
        return bestId
    }

    private fun rayAabbDistance(
        origin: FloatArray, dir: FloatArray,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): Float {
        // Slab method.
        var tMin = -Float.MAX_VALUE
        var tMax = Float.MAX_VALUE
        for (axis in 0..2) {
            val o = origin[axis]
            val d = dir[axis]
            val mn = if (axis == 0) minX else if (axis == 1) minY else minZ
            val mx = if (axis == 0) maxX else if (axis == 1) maxY else maxZ
            if (Math.abs(d) < 1e-6f) {
                if (o < mn || o > mx) return Float.MAX_VALUE
            } else {
                val inv = 1f / d
                var t1 = (mn - o) * inv
                var t2 = (mx - o) * inv
                if (t1 > t2) { val tmp = t1; t1 = t2; t2 = tmp }
                if (t1 > tMin) tMin = t1
                if (t2 < tMax) tMax = t2
                if (tMin > tMax) return Float.MAX_VALUE
            }
        }
        return if (tMin >= 0f) tMin else if (tMax >= 0f) tMax else Float.MAX_VALUE
    }

    /** Mark a prim as transparent (flips it into the alpha-sorted draw list). */
    fun setPrimTransparent(primId: Long, transparent: Boolean) {
        requireGlThread("setPrimTransparent")
        primStore.setPrimTransparent(primId, transparent)
    }

    /**
     * Push a fresh joint-matrix palette for an avatar. Producers compute
     * the world-space joint matrices (root motion + animator pose) on a
     * worker thread and queue this onto the GL thread.
     */
    fun updateAvatarJoints(id: UUID, matrices: FloatArray, count: Int) {
        requireGlThread("updateAvatarJoints")
        avatarStore.updateJoints(id, matrices, count)
    }

    /** Insert or update an avatar at a world position. */
    fun upsertAvatar(id: UUID, posX: Float, posY: Float, posZ: Float) {
        requireGlThread("upsertAvatar")
        avatarStore.addAvatar(id, posX, posY, posZ)
    }

    /** Remove a tracked avatar (also frees its joint UBO). */
    fun removeAvatar(id: UUID) {
        requireGlThread("removeAvatar")
        avatarStore.removeAvatar(id)
    }

    /** Read-only diagnostic counts for the live scene. */
    fun primCount(): Int = primStore.primCount()

    // =====================================================================
    // Shutdown
    // =====================================================================


    override fun waitForGpuIdle(reason: String) {
        requireGlThread("waitForGpuIdle")
        GLES32.glFinish()
        Log.d(TAG, "GL queue drained before backend transition: $reason")
    }

    override fun shutdown() {
        requireGlThread("shutdown")
        if (!isInitialized) return
        Log.i(TAG, "Shutting down Lumiya renderer")
        RenderDiagnostics.glShutdown(
            "frames=$frameCount viewport=${viewportWidth}x${viewportHeight}"
        )
        terrainDrawable?.destroy()
        waterDrawable?.destroy()
        skyDrawable?.destroy()
        particleManager?.destroy()
        primStore.destroy()
        meshStore.destroy()
        hudStore.destroy()
        hoverTextStore.destroy()
        avatarStore.destroy()
        occlusionQueries.destroy()
        destroyFullScreenQuad()
        ctx.shutdown()
        isInitialized = false
    }


    private fun requireGlThread(apiName: String) {
        val current = Thread.currentThread()
        if (glThreadId == Long.MIN_VALUE) {
            glThreadId = current.id
            glThreadName = current.name
            Log.i(TAG, "Bound GL thread affinity to ${current.name} (${current.id})")
            return
        }
        if (current.id != glThreadId) {
            val message = "LumiyaRenderer.$apiName must run on GL thread $glThreadName ($glThreadId); current=${current.name} (${current.id})"
            Log.e(TAG, message)
            throw IllegalStateException(message)
        }
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

    fun addAttachmentObject(
        id: Long,
        attachmentPoint: Int,
        posX: Float,
        posY: Float,
        posZ: Float,
        layer: Int = 0
    ) {
        val normalizedAttachmentPoint = normalizeAttachmentPoint(attachmentPoint)
        if (AttachmentPoints.isHudPoint(normalizedAttachmentPoint)) {
            hudStore.addHudPrim(
                id = id,
                attachmentPoint = normalizedAttachmentPoint,
                posX = posX,
                posY = posY,
                posZ = posZ,
                layer = layer
            )
        } else {
            primStore.addPrim(id, posX, posY, posZ)
        }
    }

    fun setWorldPassEnabled(enabled: Boolean) {
        worldPassEnabled = enabled
    }

    private fun renderHudPass() {
        val previousProjection = ctx.projectionMatrix.clone()
        val previousView = ctx.viewMatrix.clone()
        try {
            System.arraycopy(hudProjectionMatrix, 0, ctx.projectionMatrix, 0, 16)
            System.arraycopy(hudViewMatrix, 0, ctx.viewMatrix, 0, 16)
            ctx.uploadGlobalUBO()

            // Isolate HUD depth behavior from world geometry.
            GLES32.glDisable(GLES32.GL_DEPTH_TEST)
            GLES32.glDepthMask(false)
            GLES32.glEnable(GLES32.GL_BLEND)
            GLES32.glBlendFuncSeparate(
                GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
                GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
            )
            hudStore.draw(ctx)
        } finally {
            System.arraycopy(previousProjection, 0, ctx.projectionMatrix, 0, 16)
            System.arraycopy(previousView, 0, ctx.viewMatrix, 0, 16)
            ctx.uploadGlobalUBO()
            GLES32.glDepthMask(true)
            GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        }
    }

    private fun rebuildHudMatrices(width: Int, height: Int) {
        Matrix.setIdentityM(hudViewMatrix, 0)
        Matrix.orthoM(
            hudProjectionMatrix,
            0,
            0f,
            width.toFloat(),
            height.toFloat(),
            0f,
            -1f,
            1f
        )
    }

    private fun normalizeAttachmentPoint(attachmentPoint: Int): Int {
        // Attachment points on the wire may carry APPEND flag in the upper bit (0x80).
        return attachmentPoint and 0x7F
    }
}
