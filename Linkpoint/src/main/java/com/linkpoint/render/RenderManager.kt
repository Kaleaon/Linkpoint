package com.linkpoint.render

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.linkpoint.render.environment.SLDefaultEnvironment
import com.linkpoint.diagnostics.ScenePopulationDiagnostics
import com.linkpoint.render.materials.MaterialLoader
import com.linkpoint.render.prims.MeshPrimRenderer
import com.linkpoint.render.prims.PrimRenderer
import com.linkpoint.render.scene.SceneManager
import com.linkpoint.render.terrain.TerrainRenderer
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.xr.XRFrameData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Manages rendering using Google Filament
 * Supports both standard and XR rendering modes
 * 
 * Initializes with Second Life default environment settings to ensure
 * something visible renders even before world data is loaded.
 */
class RenderManager(private val context: Context) {
    
    companion object {
        private const val TAG = "RenderManager"
        private const val FIRST_FRAME_COUNT = 1L
        
        init {
            // Load Filament native libraries before any Engine operations
            Filament.init()
        }
    }
    
    // Filament components
    private var engine: Engine? = null
    private var renderer: Renderer? = null
    private var scene: Scene? = null
    private var view: View? = null
    private var camera: Camera? = null
    private var swapChain: SwapChain? = null
    
    // Lock for swapchain synchronization to prevent race conditions
    // between UiHelper callbacks and manual recreateSwapChain() calls
    private val swapChainLock = Any()
    
    // Scene management
    private var sceneManager: SceneManager? = null
    private val sceneGraph = SceneGraph()

    // Material and prim rendering
    private var materialLoader: MaterialLoader? = null
    private var primRenderer: PrimRenderer? = null
    private var meshPrimRenderer: MeshPrimRenderer? = null
    private var terrainRenderer: TerrainRenderer? = null
    private var primMeshDataRequester: PrimRenderer.MeshDataRequester? = null
    private var primMeshGeometryBuilder: PrimRenderer.MeshGeometryBuilder? = null

    // Helpers
    private var uiHelper: UiHelper? = null
    private var displayHelper: DisplayHelper? = null
    private var surfaceView: SurfaceView? = null

    // Ground plane entity for fallback rendering
    private var groundPlaneEntity: Int = 0
    private var groundPlaneVertexBuffer: VertexBuffer? = null
    private var groundPlaneIndexBuffer: IndexBuffer? = null
    private var groundPlaneMaterial: MaterialInstance? = null
    
    // State
    private var isInitialized = false
    private var isXRMode = false
    private var hasWorldData = false

    val dispatcher = RenderThreadDispatcher()
    private val renderQueue = RenderQueue()

    /**
     * Drawing gate — when false, [renderFrame] / [renderXRFrame] skip the
     * Filament `beginFrame/render/endFrame` calls but the render thread
     * stays alive and the Filament Engine is preserved. The gate is the
     * Lumiya parity for "the world view is covered by a panel": the 3D
     * renderer goes to background instead of being torn down and
     * re-initialized (which previously raced the SwapChain lifecycle and
     * crash-looped on every panel open).
     *
     * Mirrors `WorldViewRenderer.drawingEnabled` from the original Lumiya
     * source (see `lumiya_decompiled_source/.../render/WorldViewRenderer.java`).
     */
    private val drawingEnabled = AtomicBoolean(true)

    // Camera matrices
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    // Camera controller drives the lookAt every frame in renderFrame() based
    // on user input (gestures) and agent position. Exposed so WorldViewActivity
    // can attach gesture detectors and the agent-position updater.
    val cameraController: CameraController = CameraController()
    private val cameraEye = FloatArray(6)

    /**
     * Optional per-frame hook invoked just before render(). The app installs
     * a callback that ticks AvatarAnimator + pushes bone matrices into
     * SceneManager.applyAvatarPose() for each visible avatar. Kept as a
     * pluggable lambda so RenderManager doesn't depend on AvatarManager
     * directly — the dependency points the right way.
     */
    @Volatile
    var avatarPoseProvider: (() -> Unit)? = null
    
    /**
     * Initialize the rendering engine
     */
    fun initialize(surfaceView: SurfaceView): Boolean {
        requireFilamentThread("initialize")
        if (isInitialized) {
            // Activity may have created a fresh SurfaceView (e.g. after a renderer
            // toggle or configuration change) while we're already initialized.
            // Adopt the new SurfaceView and rebuild the SwapChain against it,
            // otherwise we keep rendering against a stale (or destroyed) surface.
            if (this.surfaceView !== surfaceView) {
                Log.i(TAG, "RenderManager.initialize: adopting new SurfaceView, rebuilding SwapChain")
                this.surfaceView = surfaceView
                uiHelper?.detach()
                uiHelper?.attachTo(surfaceView)
                rebuildSwapChainForCurrentSurface()
            }
            return true
        }
        this.surfaceView = surfaceView

        val initStartedAt = System.currentTimeMillis()
        RenderDiagnostics.filamentInitStart(
            "surfaceSize=${surfaceView.width}x${surfaceView.height}"
        )

        try {
            Log.d(TAG, "Initializing Filament engine...")

            engine = Engine.create()
            val filamentEngine = engine ?: throw IllegalStateException("Failed to create Filament Engine")
            RenderDiagnostics.filamentEngineCreated("backend=default")

            renderer = filamentEngine.createRenderer()
            scene = filamentEngine.createScene()
            view = filamentEngine.createView()
            camera = filamentEngine.createCamera(filamentEngine.entityManager.create())
            
            // Initialize scene manager
            val filamentScene = scene ?: throw IllegalStateException("Failed to create Filament Scene")
            sceneManager = SceneManager(filamentEngine, filamentScene, context)
            Log.d(TAG, "SceneManager initialized")

            // Initialize material loader
            materialLoader = MaterialLoader(context, filamentEngine)
            if (materialLoader?.initialize() != true) {
                Log.w(TAG, "MaterialLoader initialization failed - prims will not render")
            } else {
                Log.d(TAG, "MaterialLoader initialized")

                // Initialize prim renderer with lit material
                val litMaterial = materialLoader?.getLitMaterial()
                if (litMaterial != null) {
                    primRenderer = PrimRenderer(filamentEngine, filamentScene)
                    primRenderer?.initialize(litMaterial)
                    primRenderer?.setMeshDataRequester(primMeshDataRequester)
                    primRenderer?.setMeshGeometryBuilder(primMeshGeometryBuilder)
                    Log.d(TAG, "PrimRenderer initialized")

                    // Mesh-asset prim renderer: shares the lit material with
                    // PrimRenderer for now; per-face material binding (TextureEntry,
                    // BoM substitution) is a follow-up.
                    meshPrimRenderer = MeshPrimRenderer(filamentEngine, filamentScene).also {
                        it.initialize(litMaterial)
                    }

                    // Wire avatar placeholder geometry. Without this avatar
                    // entities have no RenderableComponent and stay invisible
                    // even after AvatarUpdate flow runs.
                    sceneManager?.setAvatarMaterial(litMaterial)

                    // Use the dedicated terrain splatting material if it
                    // compiled; otherwise fall back to the lit material so we
                    // still get visible (untextured) terrain rather than
                    // black geometry.
                    val terrainMat = materialLoader?.getTerrainMaterial() ?: litMaterial
                    terrainRenderer = TerrainRenderer(filamentEngine, filamentScene).also {
                        it.initialize(terrainMat)
                    }
                    Log.d(TAG, "TerrainRenderer initialized with ${if (terrainMat === litMaterial) "lit fallback" else "splat material"}")
                } else {
                    Log.w(TAG, "No lit material available - PrimRenderer not initialized")
                }
            }
            
            view?.scene = filamentScene
            view?.camera = camera
            
            // Setup UI helper for surface management
            uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
                renderCallback = object : UiHelper.RendererCallback {
                    override fun onNativeWindowChanged(surface: Surface) {
                        dispatcher.post(Runnable {
                            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                            Log.i(TAG, "║ ⭐ UiHelper.onNativeWindowChanged - Surface available")
                            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                            RenderDiagnostics.filamentSurfaceAttached(
                                surfaceView.width, surfaceView.height
                            )
                            // Use synchronized to prevent race conditions with recreateSwapChain()
                            synchronized(swapChainLock) {
                                swapChain?.let {
                                    Log.d(TAG, "Destroying old SwapChain")
                                    waitForGpuIdle("native_window_changed")
                                    engine?.destroySwapChain(it)
                                    RenderDiagnostics.filamentSwapChainDestroyed("native_window_changed")
                                }
                                swapChain = engine?.createSwapChain(surface)
                                Log.i(TAG, "✓ SwapChain created: ${swapChain != null}")
                                if (swapChain != null) {
                                    RenderDiagnostics.filamentSwapChainCreated(
                                        surfaceView.width, surfaceView.height
                                    )
                                } else {
                                    RenderDiagnostics.filamentSwapChainFailed(
                                        "engine?.createSwapChain returned null"
                                    )
                                }
                            }
                            attachDisplayHelper()
                        })
                    }

                    override fun onDetachedFromSurface() {
                        dispatcher.post(Runnable {
                            Log.w(TAG, "UiHelper.onDetachedFromSurface - Surface lost")
                            RenderDiagnostics.filamentSurfaceDetached("UiHelper.onDetachedFromSurface")
                            displayHelper?.detach()
                            synchronized(swapChainLock) {
                                swapChain?.let {
                                    waitForGpuIdle("surface_lost")
                                    engine?.destroySwapChain(it)
                                    swapChain = null
                                    RenderDiagnostics.filamentSwapChainDestroyed("surface_lost")
                                }
                            }
                        })
                    }

                    override fun onResized(width: Int, height: Int) {
                        dispatcher.post(Runnable {
                            Log.d(TAG, "onResized: ${width}x${height}")
                            view?.viewport = Viewport(0, 0, width, height)
                            viewportWidth = width
                            viewportHeight = height
                            updateProjection(width, height)
                            RenderDiagnostics.filamentSurfaceChanged(width, height)
                        })
                    }
                }
                attachTo(surfaceView)
                Log.d(TAG, "UiHelper attached to SurfaceView")
            }

            // Setup display helper BEFORE the eager swapchain rebuild so the
            // attachDisplayHelper() call inside it can succeed on first try.
            displayHelper = DisplayHelper(context)

            // UiHelper.attachTo does not always re-fire onNativeWindowChanged for
            // surfaces that were already created before we attached - which is the
            // common case here, because the SurfaceHolder.Callback in the activity
            // dispatches us onto the render thread while the surface is already valid.
            // Without this eager creation the SwapChain stays null forever and frames
            // never composite, producing the "world refuses to load" symptom.
            rebuildSwapChainForCurrentSurface()
            
            // Configure renderer with SL default clear color (sky blue)
            renderer?.clearOptions?.apply {
                clear = true
                // Set clear color to SL default sky blue
                clearColor = floatArrayOf(
                    SLDefaultEnvironment.DEFAULT_BLUE_HORIZON.r,
                    SLDefaultEnvironment.DEFAULT_BLUE_HORIZON.g,
                    SLDefaultEnvironment.DEFAULT_BLUE_HORIZON.b,
                    1.0f
                )
            }
            
            // Setup default lighting using SL defaults
            setupDefaultLighting()
            
            // Setup fallback ground plane so something is visible
            setupFallbackGroundPlane()
            
            // Set default camera position (elevated, looking at center)
            setupDefaultCamera()
            
            // Log that defaults are being applied
            SLDefaultEnvironment.logDefaults()
            
            isInitialized = true
            initializationTime = System.currentTimeMillis()
            Log.i(TAG, "Filament engine initialized successfully with SL defaults")
            RenderDiagnostics.filamentInitDone(
                durationMs = initializationTime - initStartedAt,
                components = "engine,renderer,scene,view,camera,sceneManager," +
                        (if (materialLoader != null) "materialLoader," else "") +
                        (if (primRenderer != null) "primRenderer," else "") +
                        (if (meshPrimRenderer != null) "meshPrimRenderer" else "")
            )
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Filament", e)
            lastInitializationError = "${e.javaClass.simpleName}: ${e.message}"
            RenderDiagnostics.filamentInitFailed("initialize", e)
            shutdown()
            return false
        }
    }

    fun initializeOnRenderThread(surfaceView: SurfaceView): Boolean {
        return dispatcher.runBlocking { initialize(surfaceView) }
    }
    
    private fun setupDefaultLighting() {
        // Create sun light using SL default settings
        val sunDirection = SLDefaultEnvironment.DEFAULT_SUN_DIRECTION
        val sunColor = SLDefaultEnvironment.DEFAULT_SUN_COLOR
        
        val sunlight = EntityManager.get().create()
        val filamentEngine = engine ?: throw IllegalStateException("Filament Engine not initialized")
        
        LightManager.Builder(LightManager.Type.SUN)
            .color(sunColor.r, sunColor.g, sunColor.b)
            .intensity(SLDefaultEnvironment.DEFAULT_SUN_INTENSITY)
            .direction(sunDirection.x, sunDirection.y, sunDirection.z)
            .castShadows(true)
            .sunAngularRadius(0.545f)  // Sun angular radius in degrees
            .sunHaloSize(10.0f)
            .sunHaloFalloff(80.0f)
            .build(filamentEngine, sunlight)
        
        scene?.addEntity(sunlight)
        
        // Add ambient/indirect light using SL defaults
        scene?.indirectLight = IndirectLight.Builder()
            .intensity(SLDefaultEnvironment.DEFAULT_AMBIENT_INTENSITY)
            .build(filamentEngine)
        
        Log.d(TAG, "Default SL lighting applied - Sun: ${SLDefaultEnvironment.DEFAULT_SUN_INTENSITY} lux")
    }
    
    /**
     * Setup a fallback ground plane visible before terrain loads.
     * This prevents showing a black void.
     */
    private fun setupFallbackGroundPlane() {
        val eng = engine ?: return
        val sc = scene ?: return
        val loader = materialLoader

        try {
            val groundColor = SLDefaultEnvironment.GroundPlane.DEFAULT_COLOR
            val groundSize = SLDefaultEnvironment.GroundPlane.DEFAULT_SIZE
            val halfSize = groundSize / 2f
            val z = SLDefaultEnvironment.GroundPlane.DEFAULT_HEIGHT

            // Two triangles spanning the standard SL region at water level.
            // Position + normal (Z up) + UV; same vertex layout as PrimRenderer
            // / TerrainRenderer so the lit material consumes it directly.
            val vertices = floatArrayOf(
                -halfSize, -halfSize, 0f, 0f, 0f, 1f, 0f, 0f,
                 halfSize, -halfSize, 0f, 0f, 0f, 1f, 1f, 0f,
                 halfSize,  halfSize, 0f, 0f, 0f, 1f, 1f, 1f,
                -halfSize,  halfSize, 0f, 0f, 0f, 1f, 0f, 1f
            )
            val indices = shortArrayOf(0, 1, 2, 0, 2, 3)
            val stride = 8 * 4
            val vBytes = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
            vertices.forEach { vBytes.putFloat(it) }; vBytes.flip()
            val iBytes = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
            indices.forEach { iBytes.putShort(it) }; iBytes.flip()

            val vb = VertexBuffer.Builder()
                .vertexCount(4)
                .bufferCount(1)
                .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
                .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
                .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
                .build(eng)
            vb.setBufferAt(eng, 0, vBytes)
            val ib = IndexBuffer.Builder()
                .indexCount(indices.size)
                .bufferType(IndexBuffer.Builder.IndexType.USHORT)
                .build(eng)
            ib.setBuffer(eng, iBytes)
            groundPlaneVertexBuffer = vb
            groundPlaneIndexBuffer = ib

            // Tinted material instance so the void-fallback shows SL grass green
            // instead of the lit material's default light grey.
            val matInstance = loader?.createLitInstance(
                groundColor.r, groundColor.g, groundColor.b, groundColor.a,
                metallic = 0f,
                roughness = 0.95f
            )
            if (matInstance == null) {
                Log.w(TAG, "Ground plane material unavailable; using untinted lit material")
            }
            groundPlaneMaterial = matInstance

            groundPlaneEntity = EntityManager.get().create()
            val builder = RenderableManager.Builder(1)
                .boundingBox(Box(-halfSize, -halfSize, -0.1f, halfSize, halfSize, 0.1f))
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
                .culling(false)
                .receiveShadows(true)
                .castShadows(false)
            if (matInstance != null) {
                builder.material(0, matInstance)
            } else {
                val fallbackMat = loader?.getLitMaterial()
                    ?: throw IllegalStateException("No material available for ground plane")
                builder.material(0, fallbackMat.defaultInstance)
            }
            builder.build(eng, groundPlaneEntity)

            // Centre on standard SL region (128, 128) at water level so the
            // user lands on visible ground until terrain LayerData arrives.
            val tm = eng.transformManager
            tm.create(groundPlaneEntity)
            val ti = tm.getInstance(groundPlaneEntity)
            tm.setTransform(
                ti, floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    128f, 128f, z, 1f
                )
            )

            sc.addEntity(groundPlaneEntity)
            Log.d(TAG, "Fallback ground plane created at z=$z")
        } catch (e: Exception) {
            Log.w(TAG, "Could not create fallback ground plane: ${e.message}", e)
        }
    }
    
    /**
     * Setup default camera position for initial view.
     */
    private fun setupDefaultCamera() {
        // Position camera at a reasonable height looking at the center of the
        // region. Standard SL avatar spawn position is often around (128,128,25);
        // the controller will take over once the protocol layer pushes the
        // first agent-position update.
        camera?.lookAt(
            128.0, 128.0, 30.0,
            128.0, 140.0, 20.0,
            0.0, 0.0, 1.0
        )

        camera?.setProjection(
            currentFovDegrees.toDouble(),
            1.0, // Will be updated when viewport is set
            SLDefaultEnvironment.Render.DEFAULT_NEAR_CLIP.toDouble(),
            currentFarClip.toDouble(),
            Camera.Fov.VERTICAL
        )
    }
    
    /**
     * Notify the render manager that world data has been loaded.
     * This can hide fallback elements.
     */
    fun onWorldDataLoaded() {
        hasWorldData = true
        // Once terrain is rendered we no longer want the flat fallback bleeding
        // through gaps between terrain patches. Removing the entity from the
        // scene is cheap and reversible if the user teleports to a region whose
        // LayerData is still pending.
        if (groundPlaneEntity != 0) {
            scene?.removeEntity(groundPlaneEntity)
        }
        Log.i(TAG, "World data loaded - fallback ground plane removed")
    }

    /**
     * Get the terrain renderer for protocol-side wiring
     * (TerrainManager.setTerrainRenderer).
     */
    fun getTerrainRenderer(): TerrainRenderer? = terrainRenderer

    /**
     * Upload a Bitmap as a Filament Texture and hand it to the TerrainRenderer
     * as detail texture [index] (0..3). Must run on the render thread.
     * Bitmap pixels are copied into a native byte buffer; the Bitmap can be
     * recycled by the caller after this returns.
     */
    fun uploadTerrainDetailTexture(index: Int, bitmap: android.graphics.Bitmap) {
        requireFilamentThread("uploadTerrainDetailTexture")
        val eng = engine ?: return
        val tr = terrainRenderer ?: return
        try {
            val w = bitmap.width
            val h = bitmap.height
            val pixels = IntArray(w * h)
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
            val rgba = java.nio.ByteBuffer.allocateDirect(w * h * 4)
                .order(java.nio.ByteOrder.nativeOrder())
            for (i in 0 until w * h) {
                val argb = pixels[i]
                rgba.put(((argb shr 16) and 0xFF).toByte()) // R
                rgba.put(((argb shr 8) and 0xFF).toByte())  // G
                rgba.put((argb and 0xFF).toByte())          // B
                rgba.put(((argb shr 24) and 0xFF).toByte()) // A
            }
            rgba.flip()
            val tex = Texture.Builder()
                .width(w).height(h)
                .levels(1)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.RGBA8)
                .build(eng)
            val pixelBuffer = Texture.PixelBufferDescriptor(
                rgba, Texture.Format.RGBA, Texture.Type.UBYTE
            )
            tex.setImage(eng, 0, pixelBuffer)
            tex.generateMipmaps(eng)
            tr.setDetailTexture(index, tex, scale = 16f)
            Log.d(TAG, "Uploaded terrain detail texture $index (${w}x${h})")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upload terrain detail $index: ${e.message}", e)
        }
    }
    
    private fun updateProjection(width: Int, height: Int) {
        val aspect = width.toFloat() / height.toFloat()
        camera?.setProjection(
            currentFovDegrees.toDouble(),
            aspect.toDouble(),
            SLDefaultEnvironment.Render.DEFAULT_NEAR_CLIP.toDouble(),
            currentFarClip.toDouble(),
            Camera.Fov.VERTICAL
        )
    }

    /**
     * Push the camera controller's computed eye/target into the Filament
     * camera each frame so user input (gestures, mode toggle) and agent
     * movement propagate visibly. Cheap: 12 floats and one lookAt() call.
     */
    private fun applyCameraController() {
        val cam = camera ?: return
        cameraController.computeView(cameraEye)
        cam.lookAt(
            cameraEye[0].toDouble(), cameraEye[1].toDouble(), cameraEye[2].toDouble(),
            cameraEye[3].toDouble(), cameraEye[4].toDouble(), cameraEye[5].toDouble(),
            0.0, 0.0, 1.0
        )
    }

    /**
     * Update the FOV and far-clip used for the projection matrix and refresh
     * the projection if the viewport is already known. Called from the
     * Settings screen when the user moves the FOV / draw-distance sliders.
     */
    fun setFieldOfView(fovDegrees: Float) {
        currentFovDegrees = fovDegrees.coerceIn(30f, 120f)
        if (viewportWidth > 0 && viewportHeight > 0) {
            dispatcher.post(Runnable { updateProjection(viewportWidth, viewportHeight) })
        }
    }

    fun setFarClip(meters: Float) {
        currentFarClip = meters.coerceIn(32f, 4096f)
        if (viewportWidth > 0 && viewportHeight > 0) {
            dispatcher.post(Runnable { updateProjection(viewportWidth, viewportHeight) })
        }
    }

    @Volatile private var currentFovDegrees: Float = SLDefaultEnvironment.Render.DEFAULT_FOV
    @Volatile private var currentFarClip: Float = SLDefaultEnvironment.Render.DEFAULT_FAR_CLIP

    private fun rebuildSwapChainForCurrentSurface() {
        val eng = engine ?: return
        val sv = surfaceView ?: return
        val surface = sv.holder?.surface
        if (surface == null || !surface.isValid) {
            return
        }
        synchronized(swapChainLock) {
            swapChain?.let {
                try {
                    waitForGpuIdle("rebuildSwapChainForCurrentSurface")
                    eng.destroySwapChain(it)
                } catch (_: Exception) {}
            }
            swapChain = try {
                eng.createSwapChain(surface)
            } catch (e: Exception) {
                Log.e(TAG, "rebuildSwapChainForCurrentSurface: createSwapChain threw", e)
                null
            }
            if (swapChain != null) {
                Log.i(TAG, "✓ SwapChain created eagerly during initialize")
                attachDisplayHelper()
                applyViewportFromSurface("rebuildSwapChainForCurrentSurface")
            }
        }
    }

    /**
     * Apply the current SurfaceView dimensions to the Filament `View` viewport.
     *
     * Resolves dimensions in priority order:
     *   1. `surfaceView.width` / `height`        (post-layout View bounds)
     *   2. `surfaceView.holder.surfaceFrame`     (always set once the surface
     *                                             is sized by SurfaceFlinger,
     *                                             even before View layout)
     *
     * If both yield 0 (surface created but not yet sized) we log a warning and
     * post a deferred retry via `surfaceView.post {}` so the next layout pass
     * re-runs the viewport apply. Previously this code returned silently on
     * 0×0, leaving Filament's `View` with the default `Viewport(0,0,0,0)` so
     * frames rendered into nothing — visible to the user as a black screen
     * even though `renderer.beginFrame()` succeeded at full FPS.
     *
     * @return true if a non-zero viewport was applied, false if a retry was scheduled.
     */
    private fun applyViewportFromSurface(callSite: String): Boolean {
        val sv = surfaceView ?: return false
        var width = sv.width
        var height = sv.height
        var source = "View.width/height"
        if (width <= 0 || height <= 0) {
            val frame = sv.holder?.surfaceFrame
            if (frame != null && frame.width() > 0 && frame.height() > 0) {
                width = frame.width()
                height = frame.height()
                source = "SurfaceHolder.surfaceFrame"
            }
        }
        if (width > 0 && height > 0) {
            view?.viewport = Viewport(0, 0, width, height)
            viewportWidth = width
            viewportHeight = height
            updateProjection(width, height)
            Log.d(TAG, "Viewport applied (${source}) from $callSite: ${width}x${height}")
            return true
        }
        Log.w(TAG, "⚠ Viewport not applied from $callSite (size 0×0); deferring until SurfaceView layout")
        sv.post {
            dispatcher.post(Runnable {
                if (!applyViewportFromSurface("$callSite/post")) {
                    Log.w(TAG, "Viewport retry from $callSite still 0×0 — UiHelper.onResized will need to set it")
                }
            })
        }
        return false
    }

    /**
     * Apply an explicitly known viewport size (e.g. from
     * SurfaceHolder.Callback.surfaceChanged) without relying on
     * `surfaceView.width`, which lags actual surface size during the very
     * first frames after the activity is created.
     */
    private fun applyViewportSize(width: Int, height: Int, callSite: String) {
        if (width <= 0 || height <= 0) {
            Log.w(TAG, "applyViewportSize($callSite): refusing to set ${width}x${height}")
            RenderDiagnostics.filamentViewport(width, height, "$callSite/refused")
            return
        }
        view?.viewport = Viewport(0, 0, width, height)
        viewportWidth = width
        viewportHeight = height
        updateProjection(width, height)
        Log.d(TAG, "Viewport applied explicitly from $callSite: ${width}x${height}")
        RenderDiagnostics.filamentViewport(width, height, callSite)
    }

    private fun ensureSwapChain(engine: Engine): SwapChain? {
        // Use synchronized block to prevent race conditions with UiHelper callbacks
        synchronized(swapChainLock) {
            swapChain?.let { return it }
            
            val surface = surfaceView?.holder?.surface
            if (surface == null || !surface.isValid) {
                // Throttle warnings to at most once per second (avoids log spam)
                if (shouldLogWarning()) {
                    Log.w(TAG, "SwapChain unavailable - surface not ready (surface=${surface != null}, valid=${surface?.isValid}, frame=${frameCount.get()})")
                }
                return null
            }
            try {
                swapChain = engine.createSwapChain(surface)
                if (swapChain == null) {
                    // Also throttle this warning to avoid log spam
                    if (shouldLogWarning()) {
                        Log.w(TAG, "SwapChain creation failed - engine.createSwapChain returned null")
                    }
                } else {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ ✓ SwapChain created successfully via ensureSwapChain!")
                    Log.i(TAG, "║ Frame count: ${frameCount.get()}")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                    attachDisplayHelper()
                    applyViewportFromSurface("ensureSwapChain")
                }
            } catch (e: Exception) {
                Log.e(TAG, "SwapChain creation threw exception", e)
                lastInitializationError = "SwapChain: ${e.message}"
            }
            return swapChain
        }
    }
    
    /**
     * Force recreation of the SwapChain.
     * Call this when the surface becomes available or after a surface change.
     * 
     * Thread Safety:
     * - This method uses swapChainLock for synchronization with UiHelper callbacks.
     * - Safe to call from SurfaceHolder.Callback methods (surfaceCreated, surfaceChanged).
     * - Do NOT call this from within UiHelper.RendererCallback methods (onNativeWindowChanged, 
     *   onDetachedFromSurface) as those already hold the lock and calling this would cause deadlock.
     * - If you need to recreate the SwapChain from a UiHelper callback context, the callback
     *   already handles this automatically via the synchronized block.
     */
    fun recreateSwapChain() {
        recreateSwapChainInternal(explicitWidth = -1, explicitHeight = -1)
    }

    /**
     * Variant that accepts the known dimensions from a
     * SurfaceHolder.Callback.surfaceChanged() event so the viewport reflects
     * the actual surface size even before the SurfaceView itself has been
     * laid out.
     */
    fun recreateSwapChain(width: Int, height: Int) {
        recreateSwapChainInternal(explicitWidth = width, explicitHeight = height)
    }

    private fun recreateSwapChainInternal(explicitWidth: Int, explicitHeight: Int) {
        requireFilamentThread("recreateSwapChain")
        val engine = this.engine ?: return
        val surface = surfaceView?.holder?.surface ?: run {
            RenderDiagnostics.filamentSwapChainFailed("no surface")
            return
        }

        if (!surface.isValid) {
            Log.w(TAG, "Cannot recreate SwapChain - surface not valid")
            RenderDiagnostics.filamentSwapChainFailed("surface invalid")
            return
        }

        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ 🔄 Recreating SwapChain (manual call)...")

        // Use swapChainLock for synchronization with UiHelper callbacks
        synchronized(swapChainLock) {
            // Destroy old SwapChain if it exists (it might be stale from a previous surface)
            swapChain?.let { oldSwapChain ->
                Log.i(TAG, "║ Destroying existing SwapChain before recreation")
                try {
                    waitForGpuIdle("recreateSwapChain")
                    engine.destroySwapChain(oldSwapChain)
                    RenderDiagnostics.filamentSwapChainDestroyed("recreate")
                } catch (e: Exception) {
                    Log.w(TAG, "║ Warning: Error destroying old SwapChain: ${e.message}")
                    RenderDiagnostics.filamentSwapChainFailed("destroy: ${e.message}")
                }
                swapChain = null
            }

            // Create new swap chain
            try {
                swapChain = engine.createSwapChain(surface)
                if (swapChain != null) {
                    Log.i(TAG, "║ ✓ SwapChain recreated successfully")
                    RenderDiagnostics.filamentSwapChainCreated(explicitWidth, explicitHeight)
                    attachDisplayHelper()
                    if (explicitWidth > 0 && explicitHeight > 0) {
                        applyViewportSize(explicitWidth, explicitHeight, "recreateSwapChain(explicit)")
                    } else {
                        applyViewportFromSurface("recreateSwapChain")
                    }
                } else {
                    Log.e(TAG, "║ ✗ SwapChain recreation failed - createSwapChain returned null")
                    RenderDiagnostics.filamentSwapChainFailed("createSwapChain returned null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "║ ✗ SwapChain recreation threw exception: ${e.message}", e)
                RenderDiagnostics.filamentSwapChainFailed("create exception: ${e.message}")
            }
        }
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }

    private fun attachDisplayHelper() {
        val render = renderer
        val display = surfaceView?.display
        val helper = displayHelper
        if (render == null || display == null || helper == null) {
            if (!displayAttachWarningLogged) {
                Log.w(TAG, "DisplayHelper attach skipped - renderer/display/helper not ready")
                displayAttachWarningLogged = true
            }
            return
        }
        displayAttachWarningLogged = false
        helper.attach(render, display)
    }
    
    /**
     * Check if enough time has passed since last warning to log again.
     * Prevents log spam by throttling warnings to at most once per second.
     */
    private fun shouldLogWarning(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastSwapChainWarningTime > 1000) {
            lastSwapChainWarningTime = now
            return true
        }
        return false
    }
    
    /**
     * Render a frame.
     *
     * Wrapped in try/catch because this runs on the dispatcher's
     * HandlerThread: an uncaught exception here previously killed the
     * render thread, which let `Thread.setDefaultUncaughtExceptionHandler`
     * tear down the process — observed as the "open any panel and the
     * app crash-restarts forever" loop. Errors are surfaced to
     * [RenderDiagnostics] and the next frame retries.
     *
     * Honors [drawingEnabled]: when the activity is covered by a panel
     * the gate is closed and we skip the Filament `beginFrame/render/
     * endFrame` triplet, but the queue draining + camera/avatar pose
     * tick still run so the world keeps simulating in the background.
     */
    fun renderFrame() {
        requireFilamentThread("renderFrame")
        if (!isInitialized) return

        try {
            applyRenderUpdates()
            applyCameraController()
            avatarPoseProvider?.invoke()

            if (!drawingEnabled.get()) return

            val engine = this.engine ?: return
            val renderer = this.renderer ?: return
            val view = this.view ?: return
            val swapChain = ensureSwapChain(engine) ?: return

            if (renderer.beginFrame(swapChain, System.nanoTime())) {
                renderer.render(view)
                renderer.endFrame()
                val count = frameCount.incrementAndGet()
                lastFrameTime = System.currentTimeMillis()
                RenderDiagnostics.filamentFrame()

                // Log successful rendering milestone exactly once (thread-safe with compareAndSet)
                if (count == FIRST_FRAME_COUNT && firstFrameLogged.compareAndSet(false, true)) {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ 🎉 FIRST FRAME RENDERED!")
                    Log.i(TAG, "║ SwapChain is working correctly")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                }
            }
        } catch (e: Throwable) {
            // Don't let a transient Filament/JNI failure (e.g. the surface
            // disappeared mid-frame because a panel just opened) propagate
            // out of the render thread. Log via diagnostics so the timeline
            // still shows the failure, then return so the loop can try the
            // next frame after the surface stabilizes.
            Log.e(TAG, "renderFrame threw: ${e.message}", e)
            RenderDiagnostics.filamentFrameError(e)
        }
    }

    /**
     * Render a frame in XR mode (stereo rendering)
     */
    fun renderXRFrame(xrData: XRFrameData) {
        requireFilamentThread("renderXRFrame")
        if (!isInitialized) return

        try {
            applyRenderUpdates()

            if (!drawingEnabled.get()) return

            val engine = this.engine ?: return
            val renderer = this.renderer ?: return
            val view = this.view ?: return
            val swapChain = ensureSwapChain(engine) ?: return

            if (renderer.beginFrame(swapChain, xrData.predictedDisplayTime)) {
                // Left eye
                camera?.setCustomProjection(
                    xrData.leftProjection.toDoubleArray(),
                    0.1, 1000.0
                )
                camera?.setModelMatrix(xrData.leftEyeMatrix)
                renderer.render(view)

                // Right eye
                camera?.setCustomProjection(
                    xrData.rightProjection.toDoubleArray(),
                    0.1, 1000.0
                )
                camera?.setModelMatrix(xrData.rightEyeMatrix)
                renderer.render(view)

                renderer.endFrame()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "renderXRFrame threw: ${e.message}", e)
            RenderDiagnostics.filamentFrameError(e)
        }
    }

    /**
     * Pause the GPU-facing portion of the frame loop. The render thread
     * keeps ticking — queue draining, camera follow, avatar pose tick all
     * still run — but Filament `beginFrame` is skipped, so no GPU work is
     * issued and the SurfaceView can be hidden / detached without racing
     * the swap chain.
     *
     * Safe to call from any thread (the gate is an [AtomicBoolean]).
     * Idempotent.
     */
    fun pauseDrawing(reason: String = "panel_open") {
        if (drawingEnabled.compareAndSet(true, false)) {
            Log.i(TAG, "Drawing paused: $reason")
            RenderDiagnostics.filamentDrawingPaused(reason)
        }
    }

    /**
     * Re-enable the GPU portion of the frame loop. Counterpart to
     * [pauseDrawing]. Resets the first-frame log flag so the resume is
     * visible in the diagnostics timeline.
     */
    fun resumeDrawing(reason: String = "panel_close") {
        if (drawingEnabled.compareAndSet(false, true)) {
            Log.i(TAG, "Drawing resumed: $reason")
            RenderDiagnostics.filamentDrawingResumed(reason)
        }
    }

    /** True iff [renderFrame] is currently issuing GPU work. */
    fun isDrawingEnabled(): Boolean = drawingEnabled.get()

    /**
     * Drain and apply all currently queued [RenderableUpdate] items immediately.
     * Must run on the render thread.
     */
    fun flushPendingRenderUpdates() {
        check(dispatcher.isRenderThread()) {
            "flushPendingRenderUpdates must run on the render thread"
        }
        applyRenderUpdates()
    }
    
    /**
     * Set camera position and orientation
     */
    fun setCameraTransform(
        posX: Float, posY: Float, posZ: Float,
        targetX: Float, targetY: Float, targetZ: Float
    ) {
        camera?.lookAt(
            posX.toDouble(), posY.toDouble(), posZ.toDouble(),
            targetX.toDouble(), targetY.toDouble(), targetZ.toDouble(),
            0.0, 0.0, 1.0  // Up vector (Z-up for SL)
        )
    }
    
    /**
     * Get the Filament engine
     */
    fun getEngine(): Engine? = engine
    
    /**
     * Get the Filament scene
     */
    fun getScene(): Scene? = scene
    
    /**
     * Get the scene manager for adding/removing objects and avatars
     */
    fun getSceneManager(): SceneManager? = sceneManager

    fun getSceneGraph(): SceneGraph = sceneGraph

    fun enqueueUpdate(update: RenderableUpdate): Boolean {
        val accepted = renderQueue.enqueue(update)
        when (update) {
            is RenderableUpdate.PrimUpdate -> ScenePopulationDiagnostics.markRendererSubmitted(ScenePopulationDiagnostics.EntityType.OBJECT, accepted)
            is RenderableUpdate.AvatarUpdate -> ScenePopulationDiagnostics.markRendererSubmitted(ScenePopulationDiagnostics.EntityType.AVATAR, accepted)
            else -> Unit
        }
        return accepted
    }

    /**
     * Get the prim renderer for rendering Second Life primitives
     */
    fun getPrimRenderer(): PrimRenderer? = primRenderer

    fun getMeshPrimRenderer(): MeshPrimRenderer? = meshPrimRenderer

    fun configurePrimMeshPipeline(
        requester: PrimRenderer.MeshDataRequester?,
        geometryBuilder: PrimRenderer.MeshGeometryBuilder?
    ) {
        primMeshDataRequester = requester
        primMeshGeometryBuilder = geometryBuilder
        primRenderer?.setMeshDataRequester(requester)
        primRenderer?.setMeshGeometryBuilder(geometryBuilder)
    }

    /**
     * Compile a parsed [com.linkpoint.assets.MeshData] and attach it to the
     * existing prim entity for [localId], replacing the path/profile
     * fallback geometry. Must run on the render thread.
     *
     * No-op if either renderer is not initialised, the mesh has no usable
     * faces, or the prim doesn't exist (the prim may have been deleted in
     * the time between fetch start and parse complete).
     */
    fun attachMeshAsset(
        localId: Int,
        meshData: com.linkpoint.assets.MeshData,
        textureEntry: ByteArray? = null,
        binder: com.linkpoint.render.prims.MeshPrimRenderer.TextureBinder? = null,
        bomResolver: ((java.util.UUID) -> java.util.UUID)? = null
    ) {
        requireFilamentThread("attachMeshAsset")
        val mp = meshPrimRenderer ?: return
        val pr = primRenderer ?: return
        val compiled = mp.getOrCompile(meshData) ?: return
        pr.replaceGeometry(localId) { entity ->
            engine?.renderableManager?.let { rm ->
                val inst = rm.getInstance(entity)
                if (inst != 0) rm.destroy(entity)
            }
            mp.attach(entity, compiled, textureEntry, binder, bomResolver)
        }
    }

    /**
     * Tracked overload of [uploadBitmapAsTexture] that routes through
     * [com.linkpoint.assets.LinkpointTexture] so the upload participates
     * in the LinkpointTexture lifecycle: native+GPU memory accounting
     * (TextureMemoryTracker), Cleaner-driven leak detection, and a
     * stable `releaseFilamentTexture(engine)` teardown path. Callers
     * with a UUID (which is everyone going through TextureManager)
     * should prefer this path. Returns both the Filament `Texture` (so
     * existing render binding code keeps working) and the wrapping
     * `LinkpointTexture` (so the caller can drive teardown via
     * `releaseFilamentTexture` rather than leaking the Filament handle).
     */
    fun uploadBitmapAsLinkpointTexture(
        uuid: java.util.UUID,
        bitmap: android.graphics.Bitmap
    ): Pair<Texture, com.linkpoint.assets.LinkpointTexture>? {
        requireFilamentThread("uploadBitmapAsLinkpointTexture")
        val eng = engine ?: return null
        val lpTex = com.linkpoint.assets.LinkpointTexture.fromBitmap(uuid, bitmap)
        val tex = lpTex.uploadToFilament(eng) ?: run {
            lpTex.close()
            return null
        }
        return tex to lpTex
    }

    /**
     * Upload an Android Bitmap as a Filament Texture. Used by the
     * TextureBinder when fetching per-face mesh-prim textures via
     * TextureManager. Must run on the render thread (Filament resource
     * creation is single-threaded).
     *
     * NOTE: This path does NOT participate in LinkpointTexture
     * lifecycle/accounting — prefer [uploadBitmapAsLinkpointTexture]
     * when you have a stable UUID for the texture (every caller from
     * TextureManager does). Kept for the Bitmap-only paths that don't
     * have a UUID (HUD, snapshot preview).
     */
    fun uploadBitmapAsTexture(bitmap: android.graphics.Bitmap): Texture? {
        requireFilamentThread("uploadBitmapAsTexture")
        val eng = engine ?: return null
        return try {
            val w = bitmap.width
            val h = bitmap.height
            // Generate a full mipmap chain (1.33× pixel count) — Lumiya's
            // Filament wrapper does the same, and Filament has been
            // observed to render the highest-priority world textures only
            // when mips are present (otherwise lower-LOD samplers fall
            // back to undefined data).
            val levels = com.linkpoint.assets.TextureFormatPolicy.mipLevelsFor(w, h)
            val pixels = IntArray(w * h)
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
            val rgba = java.nio.ByteBuffer.allocateDirect(w * h * 4)
                .order(java.nio.ByteOrder.nativeOrder())
            for (i in 0 until w * h) {
                val argb = pixels[i]
                rgba.put(((argb shr 16) and 0xFF).toByte())
                rgba.put(((argb shr 8) and 0xFF).toByte())
                rgba.put((argb and 0xFF).toByte())
                rgba.put(((argb shr 24) and 0xFF).toByte())
            }
            rgba.flip()
            val tex = Texture.Builder()
                .width(w).height(h)
                .levels(levels)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.SRGB8_A8)
                .build(eng)
            val pixelBuffer = Texture.PixelBufferDescriptor(
                rgba, Texture.Format.RGBA, Texture.Type.UBYTE
            )
            tex.setImage(eng, 0, pixelBuffer)
            tex.generateMipmaps(eng)
            // Track GPU memory so the debug report's `GPU` line and the
            // VRAM-pressure heuristics in TextureMemoryTracker reflect
            // the live working set instead of always reading 0 B.
            val approxBytes = (w.toLong() * h.toLong() * 4L * 4L) / 3L
            com.linkpoint.assets.TextureMemoryTracker.allocGpu(approxBytes)
            tex
        } catch (e: Exception) {
            Log.w(TAG, "uploadBitmapAsTexture failed: ${e.message}", e)
            null
        }
    }

    /**
     * Get the material loader for creating material instances
     */
    fun getMaterialLoader(): MaterialLoader? = materialLoader

    /**
     * Update or add a prim from ObjectUpdate data.
     * This is the primary entry point for rendering prims from the network.
     */
    fun updatePrim(data: ObjectUpdateData): Boolean {
        requireFilamentThread("updatePrim")
        val renderer = primRenderer ?: return false
        return renderer.updatePrim(data)
    }

    /**
     * Remove a prim by its local ID.
     */
    fun removePrim(localId: Int) {
        requireFilamentThread("removePrim")
        primRenderer?.removePrim(localId)
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking for diagnostics (volatile for thread safety)
    private var frameCount = AtomicLong(0)
    @Volatile private var lastFrameTime: Long = 0
    @Volatile private var initializationTime: Long = 0
    @Volatile private var lastInitializationError: String? = null
    @Volatile private var viewportWidth: Int = 0
    @Volatile private var viewportHeight: Int = 0
    @Volatile private var displayAttachWarningLogged: Boolean = false
    @Volatile private var lastSwapChainWarningTime: Long = 0
    private val firstFrameLogged = AtomicBoolean(false)
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): RenderManagerDiagnostics {
        // Use synchronized to get consistent swapchain status
        val hasSwapChainNow = synchronized(swapChainLock) { swapChain != null }
        // A SwapChain only makes sense when the SurfaceView is actually attached
        // and the underlying Surface is valid. When the user is on Settings (or any
        // non-WorldView screen), the SurfaceView is detached and `surface` is null
        // or invalid - that is normal, not an error.
        val surface = surfaceView?.holder?.surface
        val surfaceReady = surface != null && surface.isValid
        return RenderManagerDiagnostics(
            isInitialized = isInitialized,
            isXRMode = isXRMode,
            hasEngine = engine != null,
            hasRenderer = renderer != null,
            hasScene = scene != null,
            hasView = view != null,
            hasCamera = camera != null,
            hasSwapChain = hasSwapChainNow,
            isSurfaceReady = surfaceReady,
            isDrawingEnabled = drawingEnabled.get(),
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            frameCount = frameCount.get(),
            timeSinceLastFrame = if (lastFrameTime > 0) System.currentTimeMillis() - lastFrameTime else null,
            initializationTime = initializationTime,
            lastInitializationError = lastInitializationError,
            visiblePrimCount = sceneManager?.getVisibleObjects()?.size ?: 0,
            visibleAvatarCount = sceneManager?.getVisibleAvatars()?.size ?: 0,
            visibleTerrainPatchCount = terrainRenderer?.getDiagnostics()?.visiblePatchCount ?: 0,
            scenePopulationSnapshot = ScenePopulationDiagnostics.snapshot()
        )
    }

    /**
     * Diagnostic data class for render manager state
     */
    data class RenderManagerDiagnostics(
        val isInitialized: Boolean,
        val isXRMode: Boolean,
        val hasEngine: Boolean,
        val hasRenderer: Boolean,
        val hasScene: Boolean,
        val hasView: Boolean,
        val hasCamera: Boolean,
        val hasSwapChain: Boolean,
        val isSurfaceReady: Boolean,
        val isDrawingEnabled: Boolean,
        val viewportWidth: Int,
        val viewportHeight: Int,
        val frameCount: Long,
        val timeSinceLastFrame: Long?,
        val initializationTime: Long,
        val lastInitializationError: String?,
        val visiblePrimCount: Int,
        val visibleAvatarCount: Int,
        val visibleTerrainPatchCount: Int,
        val scenePopulationSnapshot: ScenePopulationDiagnostics.Snapshot
    )
    
    /**
     * Shutdown rendering
     */
    fun shutdown() {
        requireFilamentThread("shutdown")
        Log.i(TAG, "Shutting down render manager")
        RenderDiagnostics.filamentShutdown(
            "frames=${frameCount.get()} viewport=${viewportWidth}x${viewportHeight}"
        )

        // Shutdown prim renderer first (depends on materials)
        primRenderer?.shutdown()
        primRenderer = null

        // Shutdown material loader
        materialLoader?.shutdown()
        materialLoader = null

        uiHelper?.detach()
        displayHelper?.detach()

        engine?.let { eng ->
            synchronized(swapChainLock) {
                swapChain?.let {
                    waitForGpuIdle("shutdown")
                    eng.destroySwapChain(it)
                }
                swapChain = null
            }
            view?.let { eng.destroyView(it) }
            scene?.let { eng.destroyScene(it) }
            camera?.let { eng.destroyCameraComponent(eng.entityManager.create()) }
            renderer?.let { eng.destroyRenderer(it) }
            eng.destroy()
        }
        
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        uiHelper = null
        displayHelper = null
        surfaceView = null
        
        isInitialized = false
    }

    fun shutdownOnRenderThread() {
        dispatcher.runBlocking {
            shutdown()
        }
        dispatcher.shutdown()
    }

    private fun applyRenderUpdates() {
        val updates = renderQueue.drain()
        if (updates.isEmpty()) return

        val sceneUpdates = mutableListOf<RenderableUpdate>()
        updates.forEach { update ->
            when (update) {
                is RenderableUpdate.PrimUpdate -> updatePrim(update.data)
                is RenderableUpdate.AvatarUpdate -> {
                    val manager = sceneManager
                    if (manager != null) {
                        manager.updateAvatar(
                            agentId = update.agentId,
                            position = update.position,
                            rotation = update.rotation,
                            animations = update.animations
                        )
                        ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.AVATAR, true)
                    } else {
                        ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.AVATAR, false)
                    }
                }
                is RenderableUpdate.SceneObjectUpdate,
                is RenderableUpdate.TerrainUpdate,
                is RenderableUpdate.RemoveObject,
                RenderableUpdate.ClearScene -> sceneUpdates.add(update)
            }
        }

        if (sceneUpdates.isNotEmpty()) {
            sceneGraph.applyUpdates(sceneUpdates)
        }
    }

    private fun requireFilamentThread(apiName: String) {
        if (!dispatcher.isRenderThread()) {
            val message = "RenderManager.$apiName must run on Filament render thread (${dispatcher.renderThreadName}); " +
                "current=${Thread.currentThread().name}"
            Log.e(TAG, message)
            throw IllegalStateException(message)
        }
    }

    private fun waitForGpuIdle(reason: String) {
        val eng = engine ?: return
        try {
            eng.flushAndWait()
            Log.d(TAG, "Filament GPU queue drained before teardown: $reason")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to flush Filament GPU queue ($reason): ${e.message}")
        }
    }
    
    private fun FloatArray.toDoubleArray(): DoubleArray {
        return DoubleArray(this.size) { this[it].toDouble() }
    }
}
