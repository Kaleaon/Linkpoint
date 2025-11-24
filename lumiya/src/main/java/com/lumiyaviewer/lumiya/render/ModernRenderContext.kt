package com.lumiyaviewer.lumiya.render

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline

/**
 * Modernized rendering context for OpenGL ES 3.0+
 * Removes legacy ES 1.1 support and establishes ES 3.0 as minimum baseline
 * Implements the modernization plan from Graphics_Engine_Modernization_Plan.md
 */
class ModernRenderContext {
    private val TAG = "ModernRenderContext"

    // OpenGL ES version requirements
    private val MIN_GL_VERSION = 30 // ES 3.0 minimum
    val NEAR_PLANE = 0.5f

    // Modern capabilities
    private var hasComputeShaders = false // ES 3.1+
    private var hasTessellation = false // ES 3.2+
    private var hasGeometryShaders = false // ES 3.2+

    // Modern rendering pipeline
    private val renderPipeline: ModernRenderPipeline

    // Matrix management (modern shader-based approach)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // Rendering state
    var FOVAngle = 60.0f
    var aspectRatio = 16.0f / 9.0f
    var drawDistance = 256.0f
    var frameCount = 0

    // Camera and viewport
    var scaleX = 1.0f
    var scaleY = 1.0f
    var scaleZ = 1.0f
    private val viewport = IntArray(4)

    init {
        Log.i(TAG, "Initializing Modern Render Context for OpenGL ES 3.0+")

        // Initialize matrices to identity
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(mvpMatrix, 0)

        // Initialize modern rendering pipeline
        renderPipeline = ModernRenderPipeline()
    }

    /**
     * Initialize the modern graphics system
     * Replaces legacy OpenGL initialization with ES 3.0+ features
     */
    fun initialize(): Boolean {
        // Verify OpenGL ES 3.0+ support - this is now mandatory
        if (!checkOpenGLVersion()) {
            Log.e(TAG, "OpenGL ES 3.0+ required but not available - device not supported")
            return false
        }

        // Detect advanced capabilities
        detectAdvancedCapabilities()

        // Initialize modern rendering pipeline
        val success = renderPipeline.initialize()
        if (!success) {
            Log.e(TAG, "Failed to initialize modern rendering pipeline")
            return false
        }

        // Enable modern OpenGL features
        enableModernFeatures()

        Log.i(TAG, "Modern render context initialized successfully")
        logCapabilities()
        return true
    }

    /**
     * Check for OpenGL ES 3.0+ support (mandatory requirement)
     */
    private fun checkOpenGLVersion(): Boolean {
        val version = GLES30.glGetString(GLES30.GL_VERSION)
        Log.i(TAG, "OpenGL ES version: $version")

        return if (version == null) {
            false
        } else {
            // Require ES 3.0 minimum - no fallback to legacy versions
            version.contains("OpenGL ES 3.")
        }
    }

    /**
     * Detect advanced OpenGL ES capabilities beyond 3.0
     */
    private fun detectAdvancedCapabilities() {
        val version = GLES30.glGetString(GLES30.GL_VERSION) ?: ""
        
        // Check for compute shader support (ES 3.1+)
        if (version.contains("OpenGL ES 3.1") || version.contains("OpenGL ES 3.2")) {
            hasComputeShaders = true
            Log.i(TAG, "Compute shaders supported")
        }

        // Check for tessellation support (ES 3.2+)
        if (version.contains("OpenGL ES 3.2")) {
            hasTessellation = true
            hasGeometryShaders = true
            Log.i(TAG, "Tessellation and geometry shaders supported")
        }
    }

    /**
     * Enable modern OpenGL features that improve performance
     */
    private fun enableModernFeatures() {
        // Enable depth testing (standard for 3D rendering)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL)

        // Enable backface culling for performance
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)
        GLES30.glFrontFace(GLES30.GL_CCW)

        // Set clear color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // Enable blending for transparency
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
    }

    /**
     * Set up projection matrix for perspective rendering
     */
    fun setupProjection(width: Int, height: Int, fov: Float, near: Float, far: Float) {
        this.aspectRatio = width.toFloat() / height.toFloat()
        this.FOVAngle = fov

        // Store viewport dimensions
        viewport[0] = 0
        viewport[1] = 0
        viewport[2] = width
        viewport[3] = height

        GLES30.glViewport(0, 0, width, height)

        // Create perspective projection matrix
        Matrix.perspectiveM(projectionMatrix, 0, fov, aspectRatio, near, far)
    }

    /**
     * Set up view matrix for camera positioning
     */
    fun setupCamera(eyePos: FloatArray, lookAt: FloatArray, up: FloatArray) {
        Matrix.setLookAtM(
            viewMatrix, 0,
            eyePos[0], eyePos[1], eyePos[2],
            lookAt[0], lookAt[1], lookAt[2],
            up[0], up[1], up[2]
        )
    }

    /**
     * Begin frame rendering
     */
    fun beginFrame() {
        frameCount++

        // Clear frame buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // Reset model matrix to identity for frame
        Matrix.setIdentityM(modelMatrix, 0)
    }

    /**
     * End frame rendering
     */
    fun endFrame() {
        // Force completion of OpenGL commands
        GLES30.glFlush()

        // Check for OpenGL errors
        checkGLError("endFrame")
    }

    /**
     * Modern matrix operations (replaces legacy GLES10 matrix stack)
     */
    fun pushMatrix() {
        // Store current model matrix state
        // In modern OpenGL, we manage matrix stack manually
        Log.d(TAG, "Matrix push - managed in application code")
    }

    fun popMatrix() {
        // Restore previous model matrix state
        Log.d(TAG, "Matrix pop - managed in application code")
    }

    fun setModelMatrix(matrix: FloatArray) {
        System.arraycopy(matrix, 0, modelMatrix, 0, 16)
    }

    fun multiplyMatrix(matrix: FloatArray) {
        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, modelMatrix, 0, matrix, 0)
        System.arraycopy(temp, 0, modelMatrix, 0, 16)
    }

    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(modelMatrix, 0, x, y, z)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z)
    }

    fun scale(x: Float, y: Float, z: Float) {
        Matrix.scaleM(modelMatrix, 0, x, y, z)
        this.scaleX = x
        this.scaleY = y
        this.scaleZ = z
    }

    /**
     * Calculate and get the Model-View-Projection matrix
     */
    fun getMVPMatrix(): FloatArray {
        // Calculate MVP = Projection * View * Model
        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, temp, 0)
        return mvpMatrix
    }

    fun getModelMatrix(): FloatArray {
        return modelMatrix
    }

    fun getViewMatrix(): FloatArray {
        return viewMatrix
    }

    fun getProjectionMatrix(): FloatArray {
        return projectionMatrix
    }

    /**
     * Render using modern pipeline
     */
    fun renderWithModernPipeline(params: ModernRenderPipeline.RenderParams) {
        // Set matrices in render params
        System.arraycopy(modelMatrix, 0, params.modelMatrix, 0, 16)
        System.arraycopy(viewMatrix, 0, params.viewMatrix, 0, 16)
        System.arraycopy(projectionMatrix, 0, params.projectionMatrix, 0, 16)

        // Render using modern PBR pipeline
        renderPipeline.renderFrame(params)
    }

    /**
     * Check OpenGL error and log if found
     */
    fun checkGLError(operation: String) {
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error in $operation: $error")
        }
    }

    /**
     * Log detected capabilities
     */
    private fun logCapabilities() {
        Log.i(TAG, "=== Modern Render Context Capabilities ===")
        Log.i(TAG, "OpenGL ES 3.0 baseline: YES (mandatory)")
        Log.i(TAG, "Compute shaders (ES 3.1+): $hasComputeShaders")
        Log.i(TAG, "Tessellation (ES 3.2+): $hasTessellation")
        Log.i(TAG, "Geometry shaders (ES 3.2+): $hasGeometryShaders")
        Log.i(TAG, "Modern PBR pipeline: " + renderPipeline.isModernPipelineAvailable())
        Log.i(TAG, "==========================================")
    }

    /**
     * Getters for capabilities
     */
    fun hasComputeShaders(): Boolean = hasComputeShaders
    fun hasTessellation(): Boolean = hasTessellation
    fun hasGeometryShaders(): Boolean = hasGeometryShaders
    fun getRenderPipeline(): ModernRenderPipeline = renderPipeline
    fun getViewport(): IntArray = viewport

    /**
     * Cleanup resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up Modern Render Context")
        renderPipeline.cleanup()
    }
}
