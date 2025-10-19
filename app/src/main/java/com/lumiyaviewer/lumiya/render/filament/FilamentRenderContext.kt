package com.lumiyaviewer.lumiya.render.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import android.view.Surface
import android.view.SurfaceView

/**
 * FilamentRenderContext - Manages the Filament rendering engine lifecycle
 * 
 * This class wraps all Filament core components and provides a clean interface
 * for initialization, rendering, and cleanup of the Filament rendering engine.
 */
class FilamentRenderContext(private val context: Context) {
    
    companion object {
        private const val TAG = "FilamentRenderContext"
        
        // Initialize Filament JNI library
        init {
            try {
                Filament.init()
                Log.i(TAG, "Filament initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Filament", e)
                throw e
            }
        }
    }
    
    // Core Filament components
    lateinit var engine: Engine
        private set
    lateinit var renderer: Renderer
        private set
    lateinit var scene: Scene
        private set
    lateinit var view: View
        private set
    lateinit var camera: Camera
        private set
    
    // Entity manager for creating entities
    val entityManager: EntityManager
        get() = EntityManager.get()
    
    // SwapChain for rendering to surface
    var swapChain: SwapChain? = null
        private set
    
    // Display helper for managing display refresh
    private var displayHelper: DisplayHelper? = null
    
    // UI helper for managing surface
    var uiHelper: UiHelper? = null
        private set
    
    // Initialization state
    var isInitialized = false
        private set
    
    /**
     * Initialize the Filament rendering engine
     */
    fun initialize() {
        if (isInitialized) {
            Log.w(TAG, "Filament context already initialized")
            return
        }
        
        try {
            // Create engine with configuration
            val config = Engine.Config()
            // Use FEATURE_LEVEL_0 for maximum compatibility (OpenGL ES 3.0+)
            engine = Engine.Builder()
                .config(config)
                .featureLevel(Engine.FeatureLevel.FEATURE_LEVEL_1)
                .build()
            
            // Create renderer
            renderer = engine.createRenderer()
            
            // Create scene
            scene = engine.createScene()
            
            // Create view
            view = engine.createView()
            
            // Create camera
            val cameraEntity = entityManager.create()
            camera = engine.createCamera(cameraEntity)
            
            // Configure view
            setupView()
            
            isInitialized = true
            Log.i(TAG, "Filament rendering context initialized successfully")
            Log.i(TAG, "Feature level: ${engine.activeFeatureLevel}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Filament context", e)
            throw e
        }
    }
    
    /**
     * Setup view configuration
     */
    private fun setupView() {
        // Create a simple skybox
        scene.skybox = Skybox.Builder()
            .color(0.1f, 0.125f, 0.25f, 1.0f)  // Dark blue background
            .build(engine)
        
        // Disable post-processing for FEATURE_LEVEL_0/1 for better compatibility
        if (engine.activeFeatureLevel <= Engine.FeatureLevel.FEATURE_LEVEL_1) {
            view.isPostProcessingEnabled = false
        }
        
        // Link camera and scene to view
        view.camera = camera
        view.scene = scene
        
        // Set default camera projection
        camera.setProjection(
            Camera.Projection.PERSPECTIVE,
            45.0,  // fov in degrees
            16.0 / 9.0,  // aspect ratio (will be updated on resize)
            0.1,   // near plane
            1000.0 // far plane
        )
    }
    
    /**
     * Attach to a SurfaceView for rendering
     */
    fun attachToSurface(surfaceView: SurfaceView) {
        if (!isInitialized) {
            throw IllegalStateException("Filament context not initialized")
        }
        
        // Create UI helper
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
            renderCallback = FilamentSurfaceCallback()
            attachTo(surfaceView)
        }
        
        // Create display helper
        displayHelper = DisplayHelper(context)
    }
    
    /**
     * Detach from surface
     */
    fun detachFromSurface() {
        uiHelper?.detach()
        displayHelper?.detach()
        
        swapChain?.let {
            engine.destroySwapChain(it)
            engine.flushAndWait()
            swapChain = null
        }
    }
    
    /**
     * Render a frame
     */
    fun renderFrame(frameTimeNanos: Long): Boolean {
        if (!isInitialized) return false
        
        val swapChain = this.swapChain ?: return false
        
        // Check if we can render
        if (uiHelper?.isReadyToRender != true) {
            return false
        }
        
        // Begin frame
        if (!renderer.beginFrame(swapChain, frameTimeNanos)) {
            return false
        }
        
        // Render the view
        renderer.render(view)
        
        // End frame
        renderer.endFrame()
        
        return true
    }
    
    /**
     * Update camera projection on surface size change
     */
    fun onSurfaceResized(width: Int, height: Int) {
        if (!isInitialized) return
        
        val aspect = width.toDouble() / height.toDouble()
        camera.setProjection(
            Camera.Projection.PERSPECTIVE,
            45.0,
            aspect,
            0.1,
            1000.0
        )
        
        view.viewport = Viewport(0, 0, width, height)
    }
    
    /**
     * Cleanup all Filament resources
     */
    fun destroy() {
        if (!isInitialized) return
        
        try {
            // Detach from surface first
            detachFromSurface()
            
            // Destroy Filament resources
            engine.destroyRenderer(renderer)
            engine.destroyView(view)
            engine.destroyScene(scene)
            engine.destroyCameraComponent(camera.entity)
            
            // Destroy camera entity
            entityManager.destroy(camera.entity)
            
            // Destroy the engine (this will clean up remaining resources)
            engine.destroy()
            
            isInitialized = false
            Log.i(TAG, "Filament rendering context destroyed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying Filament context", e)
        }
    }
    
    /**
     * Surface callback for managing swap chain lifecycle
     */
    inner class FilamentSurfaceCallback : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            // Destroy old swap chain if exists
            swapChain?.let { engine.destroySwapChain(it) }
            
            // Create new swap chain
            var flags = uiHelper?.swapChainFlags ?: 0
            
            // For FEATURE_LEVEL_0, use sRGB colorspace if supported
            if (engine.activeFeatureLevel == Engine.FeatureLevel.FEATURE_LEVEL_0) {
                if (SwapChain.isSRGBSwapChainSupported(engine)) {
                    flags = flags or SwapChainFlags.CONFIG_SRGB_COLORSPACE
                }
            }
            
            swapChain = engine.createSwapChain(surface, flags)
            
            // Attach display helper
            displayHelper?.attach(renderer, uiHelper?.surfaceView?.display)
            
            Log.i(TAG, "SwapChain created for surface")
        }
        
        override fun onDetachedFromSurface() {
            displayHelper?.detach()
            
            swapChain?.let {
                engine.destroySwapChain(it)
                engine.flushAndWait()
                swapChain = null
            }
            
            Log.i(TAG, "Detached from surface")
        }
        
        override fun onResized(width: Int, height: Int) {
            onSurfaceResized(width, height)
            Log.i(TAG, "Surface resized: ${width}x${height}")
        }
    }
}
