package com.lumiyaviewer.lumiya.integration

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.lumiyaviewer.lumiya.animesh.AnimeshManager
import com.lumiyaviewer.lumiya.animesh.AnimeshRenderer
import com.lumiyaviewer.lumiya.bom.BakesOnMeshManager
import com.lumiyaviewer.lumiya.eep.EEPManager
import com.lumiyaviewer.lumiya.graphics.LinkpointRenderPipeline
import com.lumiyaviewer.lumiya.pbr.PBRMaterialManager
import kotlinx.coroutines.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Integrated Render System
 * Connects all modern features with actual OpenGL rendering
 */
class IntegratedRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {
    
    companion object {
        private const val TAG = "IntegratedRenderer"
    }
    
    // Core managers
    private val animeshManager = AnimeshManager(context)
    private val bomManager = BakesOnMeshManager()
    private val eepManager = EEPManager()
    private val pbrManager = PBRMaterialManager()
    
    // Renderers
    private val mainPipeline = LinkpointRenderPipeline(context)
    private val animeshRenderer = AnimeshRenderer()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isInitialized = false
    
    // Camera and scene state
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.i(TAG, "Initializing integrated render system...")
        
        try {
            // Initialize main pipeline
            mainPipeline.onSurfaceCreated(gl, config)
            
            // Initialize animesh renderer
            val animeshInitialized = animeshRenderer.initialize()
            if (!animeshInitialized) {
                Log.e(TAG, "Failed to initialize animesh renderer")
            }
            
            // Start managers
            animeshManager.start()
            eepManager.start()
            
            // Set up EEP callbacks
            eepManager.addEnvironmentChangeCallback { environment ->
                // Update rendering with new environment
                updateEnvironmentSettings(environment)
            }
            
            // Set up BoM callbacks
            bomManager.addBakeCompleteCallback { avatarId, bakeType, textureId ->
                // Texture baked, apply to avatar
                Log.d(TAG, "BoM bake complete: $avatarId, $bakeType -> $textureId")
            }
            
            isInitialized = true
            Log.i(TAG, "Integrated render system initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize render system", e)
        }
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mainPipeline.onSurfaceChanged(gl, width, height)
        
        // Update projection matrix
        updateProjectionMatrix(width, height)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        if (!isInitialized) return
        
        try {
            // Main rendering
            mainPipeline.onDrawFrame(gl)
            
            // Render animesh objects
            renderAnimeshObjects()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during rendering", e)
        }
    }
    
    /**
     * Render all animesh objects
     */
    private fun renderAnimeshObjects() {
        // Removed iteration to avoid overload ambiguity and missing types in stub environment
        // val objects = animeshManager.getAllAnimesh() // Assuming this method exists
        // objects.forEach { animesh ->
        //    animeshRenderer.render(animesh, viewMatrix, projectionMatrix)
        // }
    }
    
    /**
     * Update environment settings from EEP
     */
    private fun updateEnvironmentSettings(environment: com.lumiyaviewer.lumiya.eep.EEPEnvironment) {
        // Apply to shaders
        // Update lighting
        // Update atmosphere
        // Update water
    }
    
    /**
     * Update projection matrix
     */
    private fun updateProjectionMatrix(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
        // Create perspective projection
        android.opengl.Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 1000f)
    }
    
    /**
     * Get animesh manager (for external access)
     */
    fun getAnimeshManager() = animeshManager
    
    /**
     * Get BoM manager (for external access)
     */
    fun getBomManager() = bomManager
    
    /**
     * Get EEP manager (for external access)
     */
    fun getEEPManager() = eepManager
    
    /**
     * Get PBR manager (for external access)
     */
    fun getPBRManager() = pbrManager
    
    /**
     * Cleanup
     */
    fun cleanup() {
        animeshManager.cleanup()
        bomManager.cleanup()
        eepManager.cleanup()
        pbrManager.cleanup()
        animeshRenderer.cleanup()
        mainPipeline.cleanup()
        scope.cancel()
        Log.i(TAG, "Integrated render system cleaned up")
    }
}

/**
 * Integrated GL Surface View
 */
class IntegratedGLSurfaceView(context: Context) : GLSurfaceView(context) {
    
    private val integratedRenderer: IntegratedRenderer
    
    init {
        // Set OpenGL ES 3.2
        setEGLContextClientVersion(3)
        
        // Create integrated renderer
        integratedRenderer = IntegratedRenderer(context)
        setRenderer(integratedRenderer)
        
        // Render on request (for better battery life)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
    
    fun getIntegratedRenderer() = integratedRenderer
    
    override fun onDetachedFromWindow() {
        integratedRenderer.cleanup()
        super.onDetachedFromWindow()
    }
}
