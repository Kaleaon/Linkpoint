package com.linkpoint.render

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.linkpoint.xr.XRFrameData

/**
 * Manages rendering using Google Filament
 * Supports both standard and XR rendering modes
 */
class RenderManager(private val context: Context) {
    
    companion object {
        private const val TAG = "RenderManager"
        
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
    
    // Helpers
    private var uiHelper: UiHelper? = null
    private var displayHelper: DisplayHelper? = null
    private var surfaceView: SurfaceView? = null
    
    // State
    private var isInitialized = false
    private var isXRMode = false
    
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
            
            view!!.scene = scene
            view!!.camera = camera
            
            // Setup UI helper for surface management
            uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
                renderCallback = object : UiHelper.RendererCallback {
                    override fun onNativeWindowChanged(surface: Surface) {
                        swapChain?.let { engine?.destroySwapChain(it) }
                        swapChain = engine?.createSwapChain(surface)
                        attachDisplayHelper()
                    }
                    
                    override fun onDetachedFromSurface() {
                        displayHelper?.detach()
                        swapChain?.let {
                            engine?.destroySwapChain(it)
                            swapChain = null
                        }
                    }
                    
                    override fun onResized(width: Int, height: Int) {
                        view?.viewport = Viewport(0, 0, width, height)
                        viewportWidth = width
                        viewportHeight = height
                        updateProjection(width, height)
                    }
                }
                attachTo(surfaceView)
            }
            
            // Setup display helper
            displayHelper = DisplayHelper(context)
            
            // Configure renderer
            renderer!!.clearOptions = renderer!!.clearOptions.apply {
                clear = true
            }
            
            // Setup default lighting
            setupDefaultLighting()
            
            isInitialized = true
            initializationTime = System.currentTimeMillis()
            Log.i(TAG, "Filament engine initialized successfully")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Filament", e)
            lastInitializationError = "${e.javaClass.simpleName}: ${e.message}"
            shutdown()
            return false
        }
    }
    
    private fun setupDefaultLighting() {
        // Create a simple directional light (sun)
        val sunlight = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.SUN)
            .color(1.0f, 0.95f, 0.9f)
            .intensity(100000.0f)
            .direction(-0.5f, -1.0f, -0.5f)
            .castShadows(true)
            .build(engine!!, sunlight)
        scene!!.addEntity(sunlight)
        
        // Add ambient light
        scene!!.indirectLight = IndirectLight.Builder()
            .intensity(30000.0f)
            .build(engine!!)
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
        swapChain?.let { return it }
        val surface = surfaceView?.holder?.surface
        if (surface == null || !surface.isValid) {
            if (!swapChainWarningLogged) {
                Log.w(TAG, "SwapChain unavailable - surface not ready (surface=${surface != null}, valid=${surface?.isValid})")
                swapChainWarningLogged = true
            }
            return null
        }
        try {
            swapChain = engine.createSwapChain(surface)
            if (swapChain == null) {
                if (!swapChainWarningLogged) {
                    Log.w(TAG, "SwapChain creation failed - engine.createSwapChain returned null")
                    swapChainWarningLogged = true
                }
            } else {
                swapChainWarningLogged = false
                Log.i(TAG, "SwapChain created successfully")
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
    
    /**
     * Force recreation of the SwapChain.
     * Call this when the surface becomes available or after a surface change.
     */
    fun recreateSwapChain() {
        val engine = this.engine ?: return
        val surface = surfaceView?.holder?.surface ?: return
        
        if (!surface.isValid) {
            Log.w(TAG, "Cannot recreate SwapChain - surface not valid")
            return
        }
        
        // Destroy existing swap chain
        swapChain?.let { 
            engine.destroySwapChain(it)
            swapChain = null
        }
        
        // Create new swap chain
        try {
            swapChain = engine.createSwapChain(surface)
            if (swapChain != null) {
                Log.i(TAG, "SwapChain recreated successfully")
                attachDisplayHelper()
                val width = surfaceView?.width ?: 0
                val height = surfaceView?.height ?: 0
                if (width > 0 && height > 0) {
                    view?.viewport = Viewport(0, 0, width, height)
                    viewportWidth = width
                    viewportHeight = height
                    updateProjection(width, height)
                }
            } else {
                Log.e(TAG, "SwapChain recreation failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "SwapChain recreation threw exception", e)
        }
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
            frameCount.incrementAndGet()
            lastFrameTime = System.currentTimeMillis()
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
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking for diagnostics (volatile for thread safety)
    private var frameCount = java.util.concurrent.atomic.AtomicLong(0)
    @Volatile private var lastFrameTime: Long = 0
    @Volatile private var initializationTime: Long = 0
    @Volatile private var lastInitializationError: String? = null
    @Volatile private var viewportWidth: Int = 0
    @Volatile private var viewportHeight: Int = 0
    @Volatile private var swapChainWarningLogged: Boolean = false
    @Volatile private var displayAttachWarningLogged: Boolean = false
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): RenderManagerDiagnostics {
        return RenderManagerDiagnostics(
            isInitialized = isInitialized,
            isXRMode = isXRMode,
            hasEngine = engine != null,
            hasRenderer = renderer != null,
            hasScene = scene != null,
            hasView = view != null,
            hasCamera = camera != null,
            hasSwapChain = swapChain != null,
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
            swapChain?.let { eng.destroySwapChain(it) }
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
        swapChain = null
        uiHelper = null
        displayHelper = null
        surfaceView = null
        
        isInitialized = false
    }
    
    private fun FloatArray.toDoubleArray(): DoubleArray {
        return DoubleArray(this.size) { this[it].toDouble() }
    }
}
