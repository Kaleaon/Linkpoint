package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper

class FilamentRenderContext(private val context: Context) {
    
    companion object {
        private const val TAG = "FilamentRenderContext"
        
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
    
    val entityManager: EntityManager
        get() = EntityManager.get()
    
    var swapChain: SwapChain? = null
        private set
    
    private var displayHelper: DisplayHelper? = null
    
    var uiHelper: UiHelper? = null
        private set
    
    private var surfaceView: SurfaceView? = null
    
    var isInitialized = false
        private set
    
    fun initialize() {
        if (isInitialized) {
            Log.w(TAG, "Filament context already initialized")
            return
        }
        
        try {
            val config = Engine.Config()
            engine = Engine.Builder()
                .config(config)
                .featureLevel(Engine.FeatureLevel.FEATURE_LEVEL_1)
                .build()
            
            renderer = engine.createRenderer()
            scene = engine.createScene()
            view = engine.createView()
            
            val cameraEntity = entityManager.create()
            camera = engine.createCamera(cameraEntity)
            
            setupView()
            
            isInitialized = true
            Log.i(TAG, "Filament rendering context initialized successfully")
            Log.i(TAG, "Feature level: ${engine.activeFeatureLevel}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Filament context", e)
            throw e
        }
    }
    
    private fun setupView() {
        scene.skybox = Skybox.Builder()
            .color(0.1f, 0.125f, 0.25f, 1.0f)
            .build(engine)
        
        if (engine.activeFeatureLevel <= Engine.FeatureLevel.FEATURE_LEVEL_1) {
            view.isPostProcessingEnabled = false
        }
        
        view.camera = camera
        view.scene = scene
        
        camera.setProjection(
            45.0,
            16.0 / 9.0,
            0.1,
            1000.0,
            Camera.Fov.VERTICAL
        )
    }
    
    fun attachToSurface(surfaceView: SurfaceView) {
        if (!isInitialized) {
            throw IllegalStateException("Filament context not initialized")
        }
        
        this.surfaceView = surfaceView
        
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
            renderCallback = FilamentSurfaceCallback()
            attachTo(surfaceView)
        }
        
        displayHelper = DisplayHelper(context)
    }
    
    fun detachFromSurface() {
        uiHelper?.detach()
        displayHelper?.detach()
        
        swapChain?.let {
            engine.destroySwapChain(it)
            engine.flushAndWait()
            swapChain = null
        }
    }
    
    fun renderFrame(frameTimeNanos: Long): Boolean {
        if (!isInitialized) return false
        
        val swapChain = this.swapChain ?: return false
        
        if (uiHelper?.isReadyToRender != true) {
            return false
        }
        
        if (!renderer.beginFrame(swapChain, frameTimeNanos)) {
            return false
        }
        
        renderer.render(view)
        renderer.endFrame()
        
        return true
    }
    
    fun onSurfaceResized(width: Int, height: Int) {
        if (!isInitialized) return
        
        val aspect = width.toDouble() / height.toDouble()
        camera.setProjection(
            45.0,
            aspect,
            0.1,
            1000.0,
            Camera.Fov.VERTICAL
        )
        
        view.viewport = Viewport(0, 0, width, height)
    }
    
    fun destroy() {
        if (!isInitialized) return
        
        try {
            detachFromSurface()
            
            engine.destroyRenderer(renderer)
            engine.destroyView(view)
            engine.destroyScene(scene)
            engine.destroyCameraComponent(camera.entity)
            entityManager.destroy(camera.entity)
            engine.destroy()
            
            isInitialized = false
            Log.i(TAG, "Filament rendering context destroyed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying Filament context", e)
        }
    }
    
    inner class FilamentSurfaceCallback : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            swapChain?.let { engine.destroySwapChain(it) }
            
            var flags = uiHelper?.swapChainFlags ?: 0
            
            if (engine.activeFeatureLevel == Engine.FeatureLevel.FEATURE_LEVEL_0) {
                if (SwapChain.isSRGBSwapChainSupported(engine)) {
                    flags = flags or SwapChainFlags.CONFIG_SRGB_COLORSPACE
                }
            }
            
            swapChain = engine.createSwapChain(surface, flags)
            val display = surfaceView?.display ?: return
            displayHelper?.attach(renderer, display)
            
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
