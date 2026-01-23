package com.linkpoint.render

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.linkpoint.render.environment.SLDefaultEnvironment
import com.linkpoint.render.scene.SceneManager
import com.linkpoint.xr.XRFrameData
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
    
    // Helpers
    private var uiHelper: UiHelper? = null
    private var displayHelper: DisplayHelper? = null
    private var surfaceView: SurfaceView? = null
    
    // Ground plane entity for fallback rendering
    private var groundPlaneEntity: Int = 0
    
    // State
    private var isInitialized = false
    private var isXRMode = false
    private var hasWorldData = false
    
    // Camera matrices
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    
    /**
     * Initialize the rendering engine
     */
    fun initialize(surfaceView: SurfaceView): Boolean {
        if (isInitialized) return true
        this.surfaceView = surfaceView
        
        try {
            Log.d(TAG, "Initializing Filament engine...")
            
            engine = Engine.create()
            renderer = engine!!.createRenderer()
            scene = engine!!.createScene()
            view = engine!!.createView()
            camera = engine!!.createCamera(engine!!.entityManager.create())
            
            // Initialize scene manager
            sceneManager = SceneManager(engine!!, scene!!)
            Log.d(TAG, "SceneManager initialized")
            
            view!!.scene = scene
            view!!.camera = camera
            
            // Setup UI helper for surface management
            uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
                renderCallback = object : UiHelper.RendererCallback {
                    override fun onNativeWindowChanged(surface: Surface) {
                        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                        Log.i(TAG, "║ ⭐ UiHelper.onNativeWindowChanged - Surface available")
                        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                        // Use synchronized to prevent race conditions with recreateSwapChain()
                        synchronized(swapChainLock) {
                            swapChain?.let { 
                                Log.d(TAG, "Destroying old SwapChain")
                                engine?.destroySwapChain(it) 
                            }
                            swapChain = engine?.createSwapChain(surface)
                            Log.i(TAG, "✓ SwapChain created: ${swapChain != null}")
                        }
                        attachDisplayHelper()
                    }
                    
                    override fun onDetachedFromSurface() {
                        Log.w(TAG, "UiHelper.onDetachedFromSurface - Surface lost")
                        displayHelper?.detach()
                        synchronized(swapChainLock) {
                            swapChain?.let {
                                engine?.destroySwapChain(it)
                                swapChain = null
                            }
                        }
                    }
                    
                    override fun onResized(width: Int, height: Int) {
                        Log.d(TAG, "onResized: ${width}x${height}")
                        view?.viewport = Viewport(0, 0, width, height)
                        viewportWidth = width
                        viewportHeight = height
                        updateProjection(width, height)
                    }
                }
                attachTo(surfaceView)
                Log.d(TAG, "UiHelper attached to SurfaceView")
            }
            
            // Setup display helper
            displayHelper = DisplayHelper(context)
            
            // Configure renderer with SL default clear color (sky blue)
            renderer!!.clearOptions = renderer!!.clearOptions.apply {
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
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Filament", e)
            lastInitializationError = "${e.javaClass.simpleName}: ${e.message}"
            shutdown()
            return false
        }
    }
    
    private fun setupDefaultLighting() {
        // Create sun light using SL default settings
        val sunDirection = SLDefaultEnvironment.DEFAULT_SUN_DIRECTION
        val sunColor = SLDefaultEnvironment.DEFAULT_SUN_COLOR
        
        val sunlight = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.SUN)
            .color(sunColor.r, sunColor.g, sunColor.b)
            .intensity(SLDefaultEnvironment.DEFAULT_SUN_INTENSITY)
            .direction(sunDirection.x, sunDirection.y, sunDirection.z)
            .castShadows(true)
            .sunAngularRadius(0.545f)  // Sun angular radius in degrees
            .sunHaloSize(10.0f)
            .sunHaloFalloff(80.0f)
            .build(engine!!, sunlight)
        scene!!.addEntity(sunlight)
        
        // Add ambient/indirect light using SL defaults
        scene!!.indirectLight = IndirectLight.Builder()
            .intensity(SLDefaultEnvironment.DEFAULT_AMBIENT_INTENSITY)
            .build(engine!!)
        
        Log.d(TAG, "Default SL lighting applied - Sun: ${SLDefaultEnvironment.DEFAULT_SUN_INTENSITY} lux")
    }
    
    /**
     * Setup a fallback ground plane visible before terrain loads.
     * This prevents showing a black void.
     */
    private fun setupFallbackGroundPlane() {
        val eng = engine ?: return
        val sc = scene ?: return
        
        try {
            // Create a simple quad for the ground
            val groundColor = SLDefaultEnvironment.GroundPlane.DEFAULT_COLOR
            val groundSize = SLDefaultEnvironment.GroundPlane.DEFAULT_SIZE
            val halfSize = groundSize / 2f
            
            // Create ground plane entity
            groundPlaneEntity = EntityManager.get().create()
            
            // Build a simple lit material for the ground
            // Note: In a full implementation, this would use a proper grass texture
            // For now, we create a simple colored ground plane
            
            // Position the ground plane at the center of the region
            val tm = eng.transformManager
            tm.create(groundPlaneEntity)
            val ti = tm.getInstance(groundPlaneEntity)
            // Position at center of standard SL region (128, 128) at water level
            tm.setTransform(ti, floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                128f, 128f, SLDefaultEnvironment.Water.DEFAULT_WATER_HEIGHT, 1f
            ))
            
            Log.d(TAG, "Fallback ground plane created at water level (${SLDefaultEnvironment.Water.DEFAULT_WATER_HEIGHT}m)")
        } catch (e: Exception) {
            Log.w(TAG, "Could not create fallback ground plane: ${e.message}")
        }
    }
    
    /**
     * Setup default camera position for initial view.
     */
    private fun setupDefaultCamera() {
        // Position camera at a reasonable height looking at the center of the region
        // Standard SL avatar spawn position is often around (128, 128, 25)
        camera?.lookAt(
            128.0, 128.0, 30.0,    // Camera position
            128.0, 140.0, 20.0,    // Look at point (slightly ahead and down)
            0.0, 0.0, 1.0         // Up vector (Z-up for SL)
        )
        
        // Set default FOV
        camera?.setProjection(
            SLDefaultEnvironment.Render.DEFAULT_FOV.toDouble(),
            1.0, // Will be updated when viewport is set
            SLDefaultEnvironment.Render.DEFAULT_NEAR_CLIP.toDouble(),
            SLDefaultEnvironment.Render.DEFAULT_FAR_CLIP.toDouble(),
            Camera.Fov.VERTICAL
        )
    }
    
    /**
     * Notify the render manager that world data has been loaded.
     * This can hide fallback elements.
     */
    fun onWorldDataLoaded() {
        hasWorldData = true
        // In the future, this could hide the fallback ground plane
        // once actual terrain is loaded
        Log.i(TAG, "World data loaded - fallback elements can be hidden")
    }
    
    private fun updateProjection(width: Int, height: Int) {
        val aspect = width.toFloat() / height.toFloat()
        camera?.setProjection(
            45.0,           // FOV in degrees
            aspect.toDouble(),
            0.1,            // near plane
            1000.0,         // far plane
            Camera.Fov.VERTICAL
        )
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
                    val width = surfaceView?.width ?: 0
                    val height = surfaceView?.height ?: 0
                    if (width > 0 && height > 0) {
                        view?.viewport = Viewport(0, 0, width, height)
                        viewportWidth = width
                        viewportHeight = height
                        updateProjection(width, height)
                        Log.d(TAG, "Viewport set to ${width}x${height}")
                    }
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
        val engine = this.engine ?: return
        val surface = surfaceView?.holder?.surface ?: return
        
        if (!surface.isValid) {
            Log.w(TAG, "Cannot recreate SwapChain - surface not valid")
            return
        }
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ 🔄 Recreating SwapChain (manual call)...")
        
        // Use swapChainLock for synchronization with UiHelper callbacks
        synchronized(swapChainLock) {
            // Check if UiHelper already created a valid swapchain
            if (swapChain != null) {
                Log.i(TAG, "║ SwapChain already exists (likely created by UiHelper)")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                return
            }
            
            // Create new swap chain
            try {
                swapChain = engine.createSwapChain(surface)
                if (swapChain != null) {
                    Log.i(TAG, "║ ✓ SwapChain recreated successfully")
                    attachDisplayHelper()
                    val width = surfaceView?.width ?: 0
                    val height = surfaceView?.height ?: 0
                    if (width > 0 && height > 0) {
                        view?.viewport = Viewport(0, 0, width, height)
                        viewportWidth = width
                        viewportHeight = height
                        updateProjection(width, height)
                        Log.i(TAG, "║ Viewport: ${width}x${height}")
                    }
                } else {
                    Log.e(TAG, "║ ✗ SwapChain recreation failed - createSwapChain returned null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "║ ✗ SwapChain recreation threw exception: ${e.message}", e)
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
     * Render a frame
     */
    fun renderFrame() {
        if (!isInitialized) return
        
        val engine = this.engine ?: return
        val renderer = this.renderer ?: return
        val view = this.view ?: return
        val swapChain = ensureSwapChain(engine) ?: return
        
        if (renderer.beginFrame(swapChain, System.nanoTime())) {
            renderer.render(view)
            renderer.endFrame()
            val count = frameCount.incrementAndGet()
            lastFrameTime = System.currentTimeMillis()
            
            // Log successful rendering milestone exactly once (thread-safe with compareAndSet)
            if (count == FIRST_FRAME_COUNT && firstFrameLogged.compareAndSet(false, true)) {
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                Log.i(TAG, "║ 🎉 FIRST FRAME RENDERED!")
                Log.i(TAG, "║ SwapChain is working correctly")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            }
        }
    }
    
    /**
     * Render a frame in XR mode (stereo rendering)
     */
    fun renderXRFrame(xrData: XRFrameData) {
        if (!isInitialized) return
        
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
        return RenderManagerDiagnostics(
            isInitialized = isInitialized,
            isXRMode = isXRMode,
            hasEngine = engine != null,
            hasRenderer = renderer != null,
            hasScene = scene != null,
            hasView = view != null,
            hasCamera = camera != null,
            hasSwapChain = hasSwapChainNow,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            frameCount = frameCount.get(),
            timeSinceLastFrame = if (lastFrameTime > 0) System.currentTimeMillis() - lastFrameTime else null,
            initializationTime = initializationTime,
            lastInitializationError = lastInitializationError
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
        val viewportWidth: Int,
        val viewportHeight: Int,
        val frameCount: Long,
        val timeSinceLastFrame: Long?,
        val initializationTime: Long,
        val lastInitializationError: String?
    )
    
    /**
     * Shutdown rendering
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down render manager")
        
        uiHelper?.detach()
        displayHelper?.detach()
        
        engine?.let { eng ->
            synchronized(swapChainLock) {
                swapChain?.let { eng.destroySwapChain(it) }
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
    
    private fun FloatArray.toDoubleArray(): DoubleArray {
        return DoubleArray(this.size) { this[it].toDouble() }
    }
}
