package com.linkpoint.render.lumiya.core

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.linkpoint.render.RenderDiagnostics
import com.linkpoint.render.lumiya.shaders.*
import com.linkpoint.render.lumiya.glres.GLResourceManager
import com.linkpoint.render.lumiya.glres.GLTextureCache
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Central render-state container – the Lumiya equivalent of Filament's Engine + View.
 *
 * Holds all compiled shader programs, global matrix stacks, resource caches,
 * GPU capabilities, and frame-level state that individual drawables read from.
 *
 * Design lineage:  Lumiya `RenderContext.java`, modernised to GL ES 3.2 with
 * UBOs, VAOs, and structured buffer bindings.
 */
class LumiyaRenderContext(private val glThreadGuard: ((String) -> Unit)? = null) {

    companion object {
        private const val TAG = "LumiyaRenderContext"

        /** Default field of view in degrees. */
        const val DEFAULT_FOV = 60.0f

        /** Near clip plane (metres). */
        const val DEFAULT_NEAR = 0.25f

        /** Default draw distance (metres). */
        const val DEFAULT_DRAW_DISTANCE = 256.0f

        /**
         * Water FBOs render at viewport / [WATER_FBO_DIVISOR] on each
         * axis. Half-res (divisor = 2) matches LL viewer
         * `RenderReflectionDetail` defaults and is invisible at the
         * pixel level once the wave normal perturbs the sample UV.
         */
        const val WATER_FBO_DIVISOR = 2
    }

    // ── GPU capability flags ─────────────────────────────────────────────

    var glVersion: Int = 0
        private set
    var gpuRenderer: String = ""
        private set
    var gpuVendor: String = ""
        private set
    var supportsComputeShaders: Boolean = false
        private set
    var maxTextureSize: Int = 2048
        private set
    var maxUniformBlockSize: Int = 16384
        private set

    // ── Shader programs ──────────────────────────────────────────────────

    var primProgram: PrimShaderProgram? = null;         private set
    var avatarProgram: AvatarShaderProgram? = null;     private set
    var riggedMeshProgram: RiggedMeshShaderProgram? = null; private set
    var waterProgram: WaterShaderProgram? = null;       private set
    var skyProgram: SkyShaderProgram? = null;           private set
    var starsProgram: StarsShaderProgram? = null;       private set
    var fxaaProgram: FXAAShaderProgram? = null;         private set
    var terrainProgram: TerrainShaderProgram? = null;   private set
    var particleProgram: ParticleShaderProgram? = null;  private set

    // ── Matrix stacks (column-major, compatible with GLES) ───────────────

    val projectionMatrix = FloatArray(16)
    val viewMatrix = FloatArray(16)
    val modelMatrix = FloatArray(16)
    val mvpMatrix = FloatArray(16)
    val normalMatrix = FloatArray(9)           // 3×3

    // Intermediate concatenation scratch-pad
    private val tempMV = FloatArray(16)

    // ── Camera state ─────────────────────────────────────────────────────

    var cameraPositionX = 128.0f
    var cameraPositionY = 128.0f
    var cameraPositionZ = 30.0f
    var cameraTargetX = 128.0f
    var cameraTargetY = 140.0f
    var cameraTargetZ = 20.0f
    var fovDegrees = DEFAULT_FOV
    var nearPlane = DEFAULT_NEAR
    var drawDistance = DEFAULT_DRAW_DISTANCE
    var aspectRatio = 1.0f

    // ── Windlight / environment ──────────────────────────────────────────

    var sunDirectionX = 0.0f
    var sunDirectionY = 0.9f
    var sunDirectionZ = 0.4f
    var sunColorR = 1.0f; var sunColorG = 0.97f; var sunColorB = 0.88f
    var ambientColorR = 0.25f; var ambientColorG = 0.25f; var ambientColorB = 0.28f
    var skyColorR = 0.24f; var skyColorG = 0.44f; var skyColorB = 0.76f
    var hazeHorizon = 0.19f
    var hazeColorR = 0.7f; var hazeColorG = 0.76f; var hazeColorB = 0.83f
    var waterHeight = 20.0f
    var waterTime = 0.0f

    // ── Resource managers ────────────────────────────────────────────────

    lateinit var resourceManager: GLResourceManager
        private set
    lateinit var frustumCuller: FrustumCuller
        private set
    /**
     * UUID-keyed LRU texture cache. Producers (Object/Layer/Asset
     * managers) hand bitmaps to [LumiyaRenderer.uploadTextureForPrim]
     * which routes through here so duplicate texture entries across
     * multiple prims share a single GL handle.
     */
    lateinit var textureCache: GLTextureCache
        private set

    // ── Frame metrics ────────────────────────────────────────────────────

    var frameNumber: Long = 0L; private set
    var deltaTime: Float = 0.0f; private set
    private var lastFrameNanos: Long = 0L

    // ── FXAA framebuffer ─────────────────────────────────────────────────

    var fxaaEnabled = true
    var fxaaFramebuffer = 0; private set
    var fxaaColorTexture = 0; private set
    var fxaaDepthRenderbuffer = 0; private set
    var fxaaWidth = 0; private set
    var fxaaHeight = 0; private set

    // ── Water reflection / refraction FBOs ───────────────────────────────
    //
    // Names mirror the LL viewer's `LLPipeline::mWaterRef` (planar
    // reflection target) and `mWaterDis` (refraction / "distortion"
    // target). They are sized to a fraction of the screen — the
    // reflection texture is sampled through a perturbed UV in the
    // water shader so a half-resolution target is invisible at the
    // pixel level and saves substantial fill rate. Disabled by
    // default; enable via [setWaterPlanarReflectionsEnabled] when the
    // device is known to handle the extra two passes.

    var waterPlanarReflectionsEnabled: Boolean = false
        private set
    /** Planar reflection FBO. Equivalent to LLPipeline::mWaterRef. */
    var mWaterRef: Int = 0; private set
    var waterReflectionTexture: Int = 0; private set
    var waterReflectionDepthRb: Int = 0; private set
    /** Refraction (under-water) FBO. Equivalent to LLPipeline::mWaterDis. */
    var mWaterDis: Int = 0; private set
    var waterRefractionTexture: Int = 0; private set
    var waterRefractionDepthRb: Int = 0; private set
    var waterFboWidth: Int = 0; private set
    var waterFboHeight: Int = 0; private set

    // ── Global UBO for shared matrices ───────────────────────────────────

    var globalUBO = 0; private set
    private val globalUBOData = FloatArray(16 + 16 + 16 + 4 + 4) // proj + view + model + camPos(4) + sunDir(4)
    private val globalUBOSizeBytes = globalUBOData.size * 4
    private val globalUBOUploadBuffer: FloatBuffer = ByteBuffer.allocateDirect(globalUBOSizeBytes)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    private var globalUBOPersistentFloatBuffer: FloatBuffer? = null

    // =====================================================================
    // Initialisation
    // =====================================================================

    fun initialize(): Boolean {
        glThreadGuard?.invoke("initialize")
        return try {
            queryGPUCapabilities()
            resourceManager = GLResourceManager(glThreadGuard)
            frustumCuller = FrustumCuller()
            textureCache = GLTextureCache(resourceManager)
            createGlobalUBO()
            compileShaders()
            Matrix.setIdentityM(modelMatrix, 0)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Context initialisation failed", e)
            false
        }
    }

    private fun queryGPUCapabilities() {
        gpuRenderer = GLES32.glGetString(GLES32.GL_RENDERER) ?: "unknown"
        gpuVendor = GLES32.glGetString(GLES32.GL_VENDOR) ?: "unknown"
        val versionStr = GLES32.glGetString(GLES32.GL_VERSION) ?: ""
        glVersion = when {
            versionStr.contains("3.2") -> 32
            versionStr.contains("3.1") -> 31
            versionStr.contains("3.0") -> 30
            else -> 20
        }
        val buf = IntArray(1)
        GLES32.glGetIntegerv(GLES32.GL_MAX_TEXTURE_SIZE, buf, 0)
        maxTextureSize = buf[0]
        GLES32.glGetIntegerv(GLES32.GL_MAX_UNIFORM_BLOCK_SIZE, buf, 0)
        maxUniformBlockSize = buf[0]
        supportsComputeShaders = glVersion >= 31
        Log.i(TAG, "GPU: $gpuVendor $gpuRenderer  GL ES $glVersion  maxTex=$maxTextureSize")
    }

    private fun compileShaders(): Boolean {
        val compiler = ShaderCompiler()
        primProgram = PrimShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "PrimProgram compilation failed")
        }
        avatarProgram = AvatarShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "AvatarProgram compilation failed")
        }
        riggedMeshProgram = RiggedMeshShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "RiggedMeshProgram compilation failed")
        }
        waterProgram = WaterShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "WaterProgram compilation failed")
        }
        skyProgram = SkyShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "SkyProgram compilation failed")
        }
        starsProgram = StarsShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "StarsProgram compilation failed")
        }
        fxaaProgram = FXAAShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "FXAAProgram compilation failed")
        }
        terrainProgram = TerrainShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "TerrainProgram compilation failed")
        }
        particleProgram = ParticleShaderProgram().also {
            if (!it.compile(compiler)) Log.e(TAG, "ParticleProgram compilation failed")
        }
        Log.i(TAG, "All shader programs compiled")
        return true
    }

    // ── Global UBO ───────────────────────────────────────────────────────

    private fun createGlobalUBO() {
        val buf = IntArray(1)
        GLES32.glGenBuffers(1, buf, 0)
        globalUBO = buf[0]
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, globalUBO)
        if (!tryCreatePersistentMappedUBO()) {
            GLES32.glBufferData(
                GLES32.GL_UNIFORM_BUFFER,
                globalUBOSizeBytes,
                null,
                GLES32.GL_DYNAMIC_DRAW
            )
        }
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, 0)
        // Bind to binding point 0
        GLES32.glBindBufferBase(GLES32.GL_UNIFORM_BUFFER, 0, globalUBO)
    }

    private fun tryCreatePersistentMappedUBO(): Boolean {
        val extensions = GLES32.glGetString(GLES32.GL_EXTENSIONS).orEmpty()
        if (!extensions.contains("GL_EXT_buffer_storage")) {
            return false
        }
        if (!GlExtHelper.isAvailable()) {
            return false
        }
        return try {
            val flags = GlExtHelper.GL_MAP_WRITE_BIT_EXT or
                GlExtHelper.GL_MAP_PERSISTENT_BIT_EXT or
                GlExtHelper.GL_MAP_COHERENT_BIT_EXT or
                GlExtHelper.GL_DYNAMIC_STORAGE_BIT_EXT
            GlExtHelper.glBufferStorageExt(GLES32.GL_UNIFORM_BUFFER, globalUBOSizeBytes, null, flags)
            val mapped = GLES32.glMapBufferRange(
                GLES32.GL_UNIFORM_BUFFER,
                0,
                globalUBOSizeBytes,
                GLES32.GL_MAP_WRITE_BIT or
                    GlExtHelper.GL_MAP_PERSISTENT_BIT_EXT or
                    GlExtHelper.GL_MAP_COHERENT_BIT_EXT
            )
            if (mapped is ByteBuffer) {
                mapped.order(ByteOrder.nativeOrder())
                globalUBOPersistentFloatBuffer = mapped.asFloatBuffer()
                Log.i(TAG, "Using persistent mapped global UBO")
                true
            } else {
                globalUBOPersistentFloatBuffer = null
                false
            }
        } catch (t: Throwable) {
            globalUBOPersistentFloatBuffer = null
            Log.w(TAG, "Persistent mapped global UBO unavailable, using reusable upload buffer", t)
            false
        }
    }

    fun uploadGlobalUBO() {
        val uploadStartNs = System.nanoTime()
        System.arraycopy(projectionMatrix, 0, globalUBOData, 0, 16)
        System.arraycopy(viewMatrix, 0, globalUBOData, 16, 16)
        System.arraycopy(modelMatrix, 0, globalUBOData, 32, 16)
        globalUBOData[48] = cameraPositionX
        globalUBOData[49] = cameraPositionY
        globalUBOData[50] = cameraPositionZ
        globalUBOData[51] = 1.0f
        globalUBOData[52] = sunDirectionX
        globalUBOData[53] = sunDirectionY
        globalUBOData[54] = sunDirectionZ
        globalUBOData[55] = 0.0f

        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, globalUBO)
        val persistentBuffer = globalUBOPersistentFloatBuffer
        if (persistentBuffer != null) {
            persistentBuffer.clear()
            persistentBuffer.put(globalUBOData)
            persistentBuffer.flip()
        } else {
            globalUBOUploadBuffer.clear()
            globalUBOUploadBuffer.put(globalUBOData)
            globalUBOUploadBuffer.flip()
            GLES32.glBufferSubData(GLES32.GL_UNIFORM_BUFFER, 0, globalUBOSizeBytes, globalUBOUploadBuffer)
        }
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, 0)
        RenderDiagnostics.glGlobalUboUpload(
            uploadTimeNs = System.nanoTime() - uploadStartNs,
            frameAllocations = 0
        )
    }

    // ── FXAA framebuffer ─────────────────────────────────────────────────

    fun createFXAAFramebuffer(width: Int, height: Int) {
        destroyFXAAFramebuffer()
        fxaaWidth = width
        fxaaHeight = height

        val fboBuf = IntArray(1)
        GLES32.glGenFramebuffers(1, fboBuf, 0)
        fxaaFramebuffer = fboBuf[0]
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fxaaFramebuffer)

        // Color attachment
        val texBuf = IntArray(1)
        GLES32.glGenTextures(1, texBuf, 0)
        fxaaColorTexture = texBuf[0]
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, fxaaColorTexture)
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA8,
            width, height, 0,
            GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, null
        )
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D, fxaaColorTexture, 0
        )

        // Depth-stencil renderbuffer
        val rboBuf = IntArray(1)
        GLES32.glGenRenderbuffers(1, rboBuf, 0)
        fxaaDepthRenderbuffer = rboBuf[0]
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, fxaaDepthRenderbuffer)
        GLES32.glRenderbufferStorage(GLES32.GL_RENDERBUFFER, GLES32.GL_DEPTH24_STENCIL8, width, height)
        GLES32.glFramebufferRenderbuffer(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_DEPTH_STENCIL_ATTACHMENT,
            GLES32.GL_RENDERBUFFER, fxaaDepthRenderbuffer
        )

        val status = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER)
        if (status != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "FXAA framebuffer incomplete: 0x${Integer.toHexString(status)}")
        }
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }

    fun destroyFXAAFramebuffer() {
        if (fxaaFramebuffer != 0) {
            GLES32.glDeleteFramebuffers(1, intArrayOf(fxaaFramebuffer), 0)
            fxaaFramebuffer = 0
        }
        if (fxaaColorTexture != 0) {
            GLES32.glDeleteTextures(1, intArrayOf(fxaaColorTexture), 0)
            fxaaColorTexture = 0
        }
        if (fxaaDepthRenderbuffer != 0) {
            GLES32.glDeleteRenderbuffers(1, intArrayOf(fxaaDepthRenderbuffer), 0)
            fxaaDepthRenderbuffer = 0
        }
    }

    // ── Water reflection / refraction FBOs ───────────────────────────────

    /**
     * Toggle planar water reflection / refraction passes. When enabled,
     * the renderer maintains two off-screen FBOs sized to half the
     * primary viewport ([WATER_FBO_DIVISOR]). Producers (water pass)
     * sample these textures to produce the mirror-reflection +
     * underwater-refraction look that LL/Singularity ship.
     */
    fun setWaterPlanarReflectionsEnabled(enabled: Boolean, viewportWidth: Int, viewportHeight: Int) {
        if (waterPlanarReflectionsEnabled == enabled) return
        waterPlanarReflectionsEnabled = enabled
        if (enabled) createWaterFramebuffers(viewportWidth, viewportHeight)
        else destroyWaterFramebuffers()
    }

    /**
     * Recreate the water FBOs to match a new viewport. Cheap no-op
     * when reflections are disabled. Call from [onSurfaceChanged] in
     * the renderer alongside the FXAA FBO recreation.
     */
    fun resizeWaterFramebuffers(viewportWidth: Int, viewportHeight: Int) {
        if (!waterPlanarReflectionsEnabled) return
        createWaterFramebuffers(viewportWidth, viewportHeight)
    }

    private fun createWaterFramebuffers(viewportWidth: Int, viewportHeight: Int) {
        destroyWaterFramebuffers()
        val w = (viewportWidth / WATER_FBO_DIVISOR).coerceAtLeast(64)
        val h = (viewportHeight / WATER_FBO_DIVISOR).coerceAtLeast(64)
        waterFboWidth = w
        waterFboHeight = h

        val pair = createOffscreenColorDepth(w, h)
        mWaterRef = pair.fbo
        waterReflectionTexture = pair.color
        waterReflectionDepthRb = pair.depth

        val pair2 = createOffscreenColorDepth(w, h)
        mWaterDis = pair2.fbo
        waterRefractionTexture = pair2.color
        waterRefractionDepthRb = pair2.depth

        Log.i(TAG, "Water FBOs created: ${w}x${h} ref=$mWaterRef dis=$mWaterDis")
    }

    private fun destroyWaterFramebuffers() {
        intArrayOf(mWaterRef, mWaterDis).filter { it != 0 }.forEach { fbo ->
            GLES32.glDeleteFramebuffers(1, intArrayOf(fbo), 0)
        }
        intArrayOf(waterReflectionTexture, waterRefractionTexture).filter { it != 0 }.forEach { tex ->
            GLES32.glDeleteTextures(1, intArrayOf(tex), 0)
        }
        intArrayOf(waterReflectionDepthRb, waterRefractionDepthRb).filter { it != 0 }.forEach { rb ->
            GLES32.glDeleteRenderbuffers(1, intArrayOf(rb), 0)
        }
        mWaterRef = 0; waterReflectionTexture = 0; waterReflectionDepthRb = 0
        mWaterDis = 0; waterRefractionTexture = 0; waterRefractionDepthRb = 0
        waterFboWidth = 0; waterFboHeight = 0
    }

    private data class OffscreenAttachments(val fbo: Int, val color: Int, val depth: Int)

    private fun createOffscreenColorDepth(width: Int, height: Int): OffscreenAttachments {
        val fboBuf = IntArray(1); GLES32.glGenFramebuffers(1, fboBuf, 0)
        val fbo = fboBuf[0]
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fbo)

        val texBuf = IntArray(1); GLES32.glGenTextures(1, texBuf, 0)
        val color = texBuf[0]
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, color)
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA8,
            width, height, 0,
            GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, null
        )
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D, color, 0
        )

        val rbBuf = IntArray(1); GLES32.glGenRenderbuffers(1, rbBuf, 0)
        val depth = rbBuf[0]
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, depth)
        GLES32.glRenderbufferStorage(GLES32.GL_RENDERBUFFER, GLES32.GL_DEPTH_COMPONENT16, width, height)
        GLES32.glFramebufferRenderbuffer(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_DEPTH_ATTACHMENT,
            GLES32.GL_RENDERBUFFER, depth
        )

        val status = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER)
        if (status != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "Water FBO incomplete: 0x${Integer.toHexString(status)}")
        }
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
        return OffscreenAttachments(fbo, color, depth)
    }

    // ── Per-frame helpers ────────────────────────────────────────────────

    fun beginFrame() {
        frameNumber++
        val now = System.nanoTime()
        deltaTime = if (lastFrameNanos == 0L) 0.016f else (now - lastFrameNanos) / 1_000_000_000.0f
        lastFrameNanos = now

        // Advance water animation
        waterTime += deltaTime
    }

    /**
     * Compute MVP from current projection, view, and model matrices.
     * Result stored in [mvpMatrix].
     */
    fun computeMVP() {
        Matrix.multiplyMM(tempMV, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMV, 0)
    }

    /**
     * Extract the upper-left 3×3 from the model-view for normal transformation.
     */
    fun computeNormalMatrix() {
        Matrix.multiplyMM(tempMV, 0, viewMatrix, 0, modelMatrix, 0)
        normalMatrix[0] = tempMV[0]; normalMatrix[1] = tempMV[1]; normalMatrix[2] = tempMV[2]
        normalMatrix[3] = tempMV[4]; normalMatrix[4] = tempMV[5]; normalMatrix[5] = tempMV[6]
        normalMatrix[6] = tempMV[8]; normalMatrix[7] = tempMV[9]; normalMatrix[8] = tempMV[10]
    }

    fun updateCamera() {
        Matrix.setLookAtM(
            viewMatrix, 0,
            cameraPositionX, cameraPositionY, cameraPositionZ,
            cameraTargetX, cameraTargetY, cameraTargetZ,
            0f, 0f, 1f   // SL uses Z-up
        )
        Matrix.perspectiveM(
            projectionMatrix, 0,
            fovDegrees, aspectRatio,
            nearPlane, drawDistance
        )
        frustumCuller.extractPlanes(projectionMatrix, viewMatrix)
    }

    // ── Shutdown ─────────────────────────────────────────────────────────

    fun shutdown() {
        destroyFXAAFramebuffer()
        destroyWaterFramebuffers()
        if (globalUBO != 0) {
            GLES32.glDeleteBuffers(1, intArrayOf(globalUBO), 0)
            globalUBO = 0
        }
        if (::textureCache.isInitialized) {
            textureCache.clear()
        }
        primProgram?.destroy(); primProgram = null
        avatarProgram?.destroy(); avatarProgram = null
        riggedMeshProgram?.destroy(); riggedMeshProgram = null
        waterProgram?.destroy(); waterProgram = null
        skyProgram?.destroy(); skyProgram = null
        starsProgram?.destroy(); starsProgram = null
        fxaaProgram?.destroy(); fxaaProgram = null
        terrainProgram?.destroy(); terrainProgram = null
        particleProgram?.destroy(); particleProgram = null
        resourceManager.flush()
        Log.i(TAG, "Render context shut down")
    }
}
