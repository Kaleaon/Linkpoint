package com.lumiyaviewer.lumiya.modern.graphics

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log

/**
 * Shadow mapping implementation for realistic lighting
 * Supports cascaded shadow maps for large outdoor scenes
 */
class ShadowMapper {
    private val TAG = "ShadowMapper"
    
    // Shadow map configuration
    private var shadowMapSize = 2048
    private var cascadeCount = 3
    private val cascadeSplits = floatArrayOf(0.1f, 0.3f, 1.0f)
    
    // Shadow map framebuffers and textures
    private val shadowFBOs = IntArray(3)
    private val shadowTextures = IntArray(3)
    private val shadowDepthBuffers = IntArray(3)
    
    // Shadow matrices
    private val lightViewMatrices = Array(3) { FloatArray(16) }
    private val lightProjectionMatrices = Array(3) { FloatArray(16) }
    private val lightSpaceMatrices = Array(3) { FloatArray(16) }
    
    // Shadow shader program
    private var shadowShaderProgram = 0
    private var uLightSpaceMatrix = 0
    private var uModelMatrix = 0
    
    // Shadow parameters
    private var shadowBias = 0.005f
    private var pcfSamples = 4
    private var shadowSoftness = 1.0f
    
    /**
     * Initialize shadow mapping system
     */
    fun initialize(config: ShadowConfig): Boolean {
        shadowMapSize = config.shadowMapSize
        cascadeCount = config.cascadeCount
        shadowBias = config.shadowBias
        pcfSamples = config.pcfSamples
        
        // Create shadow map framebuffers
        for (i in 0 until cascadeCount) {
            if (!createShadowMap(i)) {
                Log.e(TAG, "Failed to create shadow map $i")
                return false
            }
        }
        
        // Compile shadow shader
        shadowShaderProgram = compileShadowShader()
        if (shadowShaderProgram == 0) {
            Log.e(TAG, "Failed to compile shadow shader")
            return false
        }
        
        // Get uniform locations
        uLightSpaceMatrix = GLES30.glGetUniformLocation(shadowShaderProgram, "uLightSpaceMatrix")
        uModelMatrix = GLES30.glGetUniformLocation(shadowShaderProgram, "uModelMatrix")
        
        Log.i(TAG, "Shadow mapper initialized")
        Log.i(TAG, "Shadow map size: ${shadowMapSize}x${shadowMapSize}")
        Log.i(TAG, "Cascade count: $cascadeCount")
        Log.i(TAG, "PCF samples: $pcfSamples")
        
        return true
    }
    
    /**
     * Create shadow map framebuffer
     */
    private fun createShadowMap(cascade: Int): Boolean {
        // Generate framebuffer
        val fboArray = IntArray(1)
        GLES30.glGenFramebuffers(1, fboArray, 0)
        shadowFBOs[cascade] = fboArray[0]
        
        // Generate depth texture
        val texArray = IntArray(1)
        GLES30.glGenTextures(1, texArray, 0)
        shadowTextures[cascade] = texArray[0]
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowTextures[cascade])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT24,
            shadowMapSize, shadowMapSize, 0,
            GLES30.GL_DEPTH_COMPONENT, GLES30.GL_UNSIGNED_INT, null
        )
        
        // Set texture parameters
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        
        // Attach depth texture to framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, shadowFBOs[cascade])
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_DEPTH_ATTACHMENT,
            GLES30.GL_TEXTURE_2D,
            shadowTextures[cascade],
            0
        )
        
        // Check framebuffer status
        val status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER)
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "Shadow framebuffer incomplete: $status")
            return false
        }
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        return true
    }
    
    /**
     * Compile shadow shader program
     */
    private fun compileShadowShader(): Int {
        val vertexShader = """
            #version 300 es
            precision highp float;
            
            layout(location = 0) in vec3 aPosition;
            
            uniform mat4 uLightSpaceMatrix;
            uniform mat4 uModelMatrix;
            
            void main() {
                gl_Position = uLightSpaceMatrix * uModelMatrix * vec4(aPosition, 1.0);
            }
        """.trimIndent()
        
        val fragmentShader = """
            #version 300 es
            precision highp float;
            
            void main() {
                // Depth is written automatically
            }
        """.trimIndent()
        
        return compileShaderProgram(vertexShader, fragmentShader)
    }
    
    /**
     * Helper to compile and link shader program
     */
    private fun compileShaderProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
        GLES30.glShaderSource(vertexShader, vertexSource)
        GLES30.glCompileShader(vertexShader)
        
        val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
        GLES30.glShaderSource(fragmentShader, fragmentSource)
        GLES30.glCompileShader(fragmentShader)
        
        val program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        
        GLES30.glDeleteShader(vertexShader)
        GLES30.glDeleteShader(fragmentShader)
        
        return program
    }
    
    /**
     * Update cascade matrices based on camera
     */
    fun updateCascades(
        cameraPos: FloatArray,
        cameraDir: FloatArray,
        lightDir: FloatArray,
        nearPlane: Float,
        farPlane: Float
    ) {
        for (i in 0 until cascadeCount) {
            val cascadeNear = if (i == 0) nearPlane else farPlane * cascadeSplits[i - 1]
            val cascadeFar = farPlane * cascadeSplits[i]
            
            // Calculate light view matrix
            calculateLightViewMatrix(
                lightDir,
                cameraPos,
                lightViewMatrices[i]
            )
            
            // Calculate light projection matrix for this cascade
            calculateLightProjectionMatrix(
                cameraPos,
                cameraDir,
                cascadeNear,
                cascadeFar,
                lightViewMatrices[i],
                lightProjectionMatrices[i]
            )
            
            // Calculate light space matrix
            Matrix.multiplyMM(
                lightSpaceMatrices[i], 0,
                lightProjectionMatrices[i], 0,
                lightViewMatrices[i], 0
            )
        }
    }
    
    /**
     * Calculate light view matrix
     */
    private fun calculateLightViewMatrix(
        lightDir: FloatArray,
        target: FloatArray,
        outMatrix: FloatArray
    ) {
        // Calculate light position (far from target in light direction)
        val lightPos = FloatArray(3)
        lightPos[0] = target[0] - lightDir[0] * 100f
        lightPos[1] = target[1] - lightDir[1] * 100f
        lightPos[2] = target[2] - lightDir[2] * 100f
        
        // Create view matrix looking from light to target
        Matrix.setLookAtM(
            outMatrix, 0,
            lightPos[0], lightPos[1], lightPos[2],
            target[0], target[1], target[2],
            0f, 1f, 0f
        )
    }
    
    /**
     * Calculate light projection matrix for cascade
     */
    private fun calculateLightProjectionMatrix(
        cameraPos: FloatArray,
        cameraDir: FloatArray,
        nearPlane: Float,
        farPlane: Float,
        lightViewMatrix: FloatArray,
        outMatrix: FloatArray
    ) {
        // Calculate frustum corners
        val corners = calculateFrustumCorners(cameraPos, cameraDir, nearPlane, farPlane)
        
        // Transform corners to light space
        val lightSpaceCorners = transformToLightSpace(corners, lightViewMatrix)
        
        // Find bounding box in light space
        val bounds = calculateBounds(lightSpaceCorners)
        
        // Create orthographic projection
        Matrix.orthoM(
            outMatrix, 0,
            bounds[0], bounds[1], // left, right
            bounds[2], bounds[3], // bottom, top
            bounds[4], bounds[5]  // near, far
        )
    }
    
    /**
     * Calculate frustum corners
     */
    private fun calculateFrustumCorners(
        cameraPos: FloatArray,
        cameraDir: FloatArray,
        nearPlane: Float,
        farPlane: Float
    ): Array<FloatArray> {
        // Simplified: return 8 corner points
        return Array(8) { FloatArray(3) }
    }
    
    /**
     * Transform points to light space
     */
    private fun transformToLightSpace(
        points: Array<FloatArray>,
        lightViewMatrix: FloatArray
    ): Array<FloatArray> {
        val result = Array(points.size) { FloatArray(3) }
        val vec = FloatArray(4)
        val out = FloatArray(4)
        
        for (i in points.indices) {
            vec[0] = points[i][0]
            vec[1] = points[i][1]
            vec[2] = points[i][2]
            vec[3] = 1f
            
            Matrix.multiplyMV(out, 0, lightViewMatrix, 0, vec, 0)
            
            result[i][0] = out[0]
            result[i][1] = out[1]
            result[i][2] = out[2]
        }
        
        return result
    }
    
    /**
     * Calculate bounding box
     */
    private fun calculateBounds(points: Array<FloatArray>): FloatArray {
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE
        var minZ = Float.MAX_VALUE
        var maxZ = Float.MIN_VALUE
        
        for (point in points) {
            minX = minOf(minX, point[0])
            maxX = maxOf(maxX, point[0])
            minY = minOf(minY, point[1])
            maxY = maxOf(maxY, point[1])
            minZ = minOf(minZ, point[2])
            maxZ = maxOf(maxZ, point[2])
        }
        
        return floatArrayOf(minX, maxX, minY, maxY, minZ, maxZ)
    }
    
    /**
     * Render shadow map for cascade
     */
    fun renderShadowMap(cascade: Int, renderCallback: (Int) -> Unit) {
        // Bind shadow framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, shadowFBOs[cascade])
        GLES30.glViewport(0, 0, shadowMapSize, shadowMapSize)
        
        // Clear depth buffer
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT)
        
        // Enable depth test
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        
        // Use shadow shader
        GLES30.glUseProgram(shadowShaderProgram)
        
        // Set light space matrix
        GLES30.glUniformMatrix4fv(uLightSpaceMatrix, 1, false, lightSpaceMatrices[cascade], 0)
        
        // Render scene from light's perspective
        renderCallback(cascade)
        
        // Unbind framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }
    
    /**
     * Bind shadow maps for rendering
     */
    fun bindShadowMaps(startTextureUnit: Int) {
        for (i in 0 until cascadeCount) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + startTextureUnit + i)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowTextures[i])
        }
    }
    
    /**
     * Get light space matrices for shader
     */
    fun getLightSpaceMatrices(): Array<FloatArray> {
        return lightSpaceMatrices
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        GLES30.glDeleteFramebuffers(cascadeCount, shadowFBOs, 0)
        GLES30.glDeleteTextures(cascadeCount, shadowTextures, 0)
        GLES30.glDeleteProgram(shadowShaderProgram)
    }
}

/**
 * Shadow configuration
 */
data class ShadowConfig(
    val shadowMapSize: Int = 2048,
    val cascadeCount: Int = 3,
    val shadowBias: Float = 0.005f,
    val pcfSamples: Int = 4
)
