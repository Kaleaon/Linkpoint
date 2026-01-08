package com.linkpoint.graphics.filament

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceView

/**
 * FilamentSurfaceView - A SurfaceView that manages Filament rendering
 * 
 * This view handles the rendering loop using Choreographer for frame callbacks,
 * similar to how GLSurfaceView works for OpenGL.
 */
class FilamentSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {
    
    companion object {
        private const val TAG = "FilamentSurfaceView"
    }
    
    // Filament rendering context
    private val renderContext = FilamentRenderContext(context)
    
    // World renderer
    private var worldRenderer: FilamentWorldRenderer? = null
    
    // Choreographer for frame scheduling
    private val choreographer = Choreographer.getInstance()
    
    // Frame callback for rendering
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isAttached && !isPaused) {
                try {
                    renderer?.onDrawFrame(frameTimeNanos)
                } catch (t: Throwable) {
                    Log.e(TAG, "Renderer draw callback failed", t)
                }

                worldRenderer?.onFrame(frameTimeNanos)
                renderContext.renderFrame(frameTimeNanos)

                choreographer.postFrameCallback(this)
            }
        }
    }
    
    // Lifecycle state
    private var isAttached = false
    private var isPaused = true  // Start paused, will be resumed in onAttachedToWindow
    
    /**
     * Renderer callback interface for custom rendering
     */
    interface Renderer {
        fun onSurfaceCreated(context: FilamentRenderContext)
        fun onSurfaceChanged(width: Int, height: Int)
        fun onDrawFrame(frameTimeNanos: Long)
    }
    
    private var renderer: Renderer? = null
    
    init {
        // Initialize Filament context
        try {
            renderContext.initialize()
            Log.i(TAG, "FilamentSurfaceView initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize FilamentSurfaceView", e)
        }
    }
    
    /**
     * Set a custom renderer
     */
    fun setRenderer(renderer: Renderer) {
        this.renderer = renderer
    }
    
    /**
     * Get the Filament render context
     */
    fun getRenderContext(): FilamentRenderContext = renderContext
    
    /**
     * Get the world renderer
     */
    fun getWorldRenderer(): FilamentWorldRenderer? = worldRenderer
    
    /**
     * Initialize the world renderer
     */
    fun initializeWorldRenderer() {
        worldRenderer = FilamentWorldRenderer(context, renderContext).apply {
            initialize()
        }
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        
        try {
            // Attach render context to this surface
            renderContext.attachToSurface(this)
            
            isAttached = true
            
            // Auto-resume rendering when attached to window
            if (isPaused) {
                isPaused = false
                choreographer.postFrameCallback(frameCallback)
                Log.i(TAG, "Auto-started rendering on attach")
            }
            
            Log.i(TAG, "Attached to window")
        } catch (e: Exception) {
            Log.e(TAG, "Error attaching to window", e)
        }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        
        try {
            // Stop rendering
            isAttached = false
            choreographer.removeFrameCallback(frameCallback)
            
            // Detach from surface
            renderContext.detachFromSurface()
            
            Log.i(TAG, "Detached from window")
        } catch (e: Exception) {
            Log.e(TAG, "Error detaching from window", e)
        }
    }
    
    /**
     * Resume rendering (call from Activity.onResume())
     */
    fun onResume() {
        if (!isPaused) {
            Log.d(TAG, "Already resumed, skipping")
            return
        }
        
        isPaused = false
        
        // Start frame callbacks if attached
        if (isAttached) {
            choreographer.postFrameCallback(frameCallback)
            Log.i(TAG, "Resumed")
        } else {
            Log.d(TAG, "Not attached yet, will resume when attached")
        }
    }
    
    /**
     * Pause rendering (call from Activity.onPause())
     */
    fun onPause() {
        if (isPaused) return
        
        isPaused = true
        
        // Stop frame callbacks
        choreographer.removeFrameCallback(frameCallback)
        
        Log.i(TAG, "Paused")
    }
    
    /**
     * Cleanup resources (call from Activity.onDestroy())
     */
    fun destroy() {
        try {
            // Stop rendering
            choreographer.removeFrameCallback(frameCallback)
            
            // Destroy world renderer
            worldRenderer?.destroy()
            worldRenderer = null
            
            // Destroy render context
            renderContext.destroy()
            
            Log.i(TAG, "Destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying FilamentSurfaceView", e)
        }
    }
}
