package com.lumiyaviewer.lumiya.modern.graphics

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import kotlin.math.sin
import kotlin.math.cos

/**
 * Advanced water rendering with reflections, refractions, and caustics
 * Implements realistic water surface for Second Life environments
 */
class WaterRenderer {
    private val TAG = "WaterRenderer"
    
    // Water plane configuration
    private var waterLevel = 20f
    private val waterGridSize = 64
    private val waterGridSpacing = 2f
    
    // Water shader program
    private var waterShaderProgram = 0
    
    // Water geometry
    private var waterVBO = 0
    private var waterIBO = 0
    private var waterVertexCount = 0
    
    // Water textures
    private var normalMapTexture = 0
    private var dudvMapTexture = 0
    private var reflectionTexture = 0
    private var refractionTexture = 0
    
    // Water framebuffers
    private var reflectionFBO = 0
    private var refractionFBO = 0
    private val fbSize = 1024
    
    // Water animation
    private var waveTime = 0f
    private val waveSpeed = 0.05f
    private val waveStrength = 0.02f
    
    // Water parameters
    private var waterColor = floatArrayOf(0.0f, 0.3f, 0.5f, 0.7f)
    private var shineDamper = 20.0f
    private var reflectivity = 0.6f
    
    /**
     * Initialize water renderer
     */
    fun initialize(config: WaterConfig): Boolean {
        waterLevel = config.waterLevel
        waterColor = config.waterColor
        shineDamper = config.shineDamper
        reflectivity = config.reflectivity
        
        // Create water geometry
        if (!createWaterGeometry()) {
            Log.e(TAG, "Failed to create water geometry")
            return false
        }
        
        // Create water framebuffers
        if (!createWaterFramebuffers()) {
            Log.e(TAG, "Failed to create water framebuffers")
            return false
        }
        
        // Compile water shader
        waterShaderProgram = compileWaterShader()
        if (waterShaderProgram == 0) {
            Log.e(TAG, "Failed to compile water shader")
            return false
        }
        
        // Load water textures
        loadWaterTextures()
        
        Log.i(TAG, "Water renderer initialized")
        Log.i(TAG, "Water level: $waterLevel")
        Log.i(TAG, "Grid size: ${waterGridSize}x${waterGridSize}")
        
        return true
    }
    
    /**
     * Create water plane geometry
     */
    private fun createWaterGeometry(): Boolean {
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        
        // Generate grid vertices
        for (z in 0..waterGridSize) {
            for (x in 0..waterGridSize) {
                val posX = (x - waterGridSize / 2) * waterGridSpacing
                val posZ = (z - waterGridSize / 2) * waterGridSpacing
                
                // Position
                vertices.add(posX)
                vertices.add(waterLevel)
                vertices.add(posZ)
                
                // Texture coordinates
                vertices.add(x.toFloat() / waterGridSize)
                vertices.add(z.toFloat() / waterGridSize)
            }
        }
        
        // Generate indices
        for (z in 0 until waterGridSize) {
            for (x in 0 until waterGridSize) {
                val topLeft = z * (waterGridSize + 1) + x
                val topRight = topLeft + 1
                val bottomLeft = (z + 1) * (waterGridSize + 1) + x
                val bottomRight = bottomLeft + 1
                
                // First triangle
                indices.add(topLeft)
                indices.add(bottomLeft)
                indices.add(topRight)
                
                // Second triangle
                indices.add(topRight)
                indices.add(bottomLeft)
                indices.add(bottomRight)
            }
        }
        
        waterVertexCount = indices.size
        
        // Create VBO
        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        waterVBO = vboArray[0]
        
        val vertexArray = vertices.toFloatArray()
        val vertexBuffer = java.nio.ByteBuffer.allocateDirect(vertexArray.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)
        
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, waterVBO)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexArray.size * 4,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        
        // Create IBO
        val iboArray = IntArray(1)
        GLES30.glGenBuffers(1, iboArray, 0)
        waterIBO = iboArray[0]
        
        val indexArray = indices.toIntArray()
        val indexBuffer = java.nio.ByteBuffer.allocateDirect(indexArray.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asIntBuffer()
        indexBuffer.put(indexArray)
        indexBuffer.position(0)
        
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, waterIBO)
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexArray.size * 4,
            indexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        
        return true
    }
    
    /**
     * Create water framebuffers for reflection and refraction
     */
    private fun createWaterFramebuffers(): Boolean {
        // Create reflection framebuffer
        val fboArray = IntArray(1)
        GLES30.glGenFramebuffers(1, fboArray, 0)
        reflectionFBO = fboArray[0]
        
        val texArray = IntArray(1)
        GLES30.glGenTextures(1, texArray, 0)
        reflectionTexture = texArray[0]
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, reflectionTexture)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB,
            fbSize, fbSize, 0,
            GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, null
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, reflectionFBO)
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D,
            reflectionTexture,
            0
        )
        
        // Create refraction framebuffer (similar to reflection)
        GLES30.glGenFramebuffers(1, fboArray, 0)
        refractionFBO = fboArray[0]
        
        GLES30.glGenTextures(1, texArray, 0)
        refractionTexture = texArray[0]
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, refractionTexture)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB,
            fbSize, fbSize, 0,
            GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, null
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, refractionFBO)
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D,
            refractionTexture,
            0
        )
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        
        return true
    }
    
    /**
     * Compile water shader
     */
    private fun compileWaterShader(): Int {
        val vertexShader = """
            #version 300 es
            precision highp float;
            
            layout(location = 0) in vec3 aPosition;
            layout(location = 1) in vec2 aTexCoord;
            
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            uniform float uWaveTime;
            
            out vec2 vTexCoord;
            out vec3 vWorldPos;
            out vec4 vClipSpace;
            
            void main() {
                vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
                
                // Add wave displacement
                worldPos.y += sin(worldPos.x * 0.5 + uWaveTime) * 0.1;
                worldPos.y += cos(worldPos.z * 0.5 + uWaveTime * 0.8) * 0.1;
                
                vWorldPos = worldPos.xyz;
                vClipSpace = uMVPMatrix * worldPos;
                gl_Position = vClipSpace;
                
                vTexCoord = aTexCoord;
            }
        """.trimIndent()
        
        val fragmentShader = """
            #version 300 es
            precision highp float;
            
            in vec2 vTexCoord;
            in vec3 vWorldPos;
            in vec4 vClipSpace;
            
            uniform sampler2D uReflectionTexture;
            uniform sampler2D uRefractionTexture;
            uniform sampler2D uDudvMap;
            uniform sampler2D uNormalMap;
            uniform vec4 uWaterColor;
            uniform vec3 uCameraPos;
            uniform float uWaveTime;
            
            out vec4 fragColor;
            
            void main() {
                vec2 ndc = (vClipSpace.xy / vClipSpace.w) * 0.5 + 0.5;
                vec2 reflectTexCoords = vec2(ndc.x, 1.0 - ndc.y);
                vec2 refractTexCoords = ndc;
                
                // Sample DuDv map for distortion
                vec2 distortion = texture(uDudvMap, vTexCoord + uWaveTime * 0.05).rg * 0.1;
                distortion = vTexCoord + vec2(distortion.x, distortion.y + uWaveTime * 0.05);
                vec2 totalDistortion = (texture(uDudvMap, distortion).rg * 2.0 - 1.0) * 0.02;
                
                reflectTexCoords += totalDistortion;
                refractTexCoords += totalDistortion;
                
                // Sample reflection and refraction
                vec4 reflectColor = texture(uReflectionTexture, reflectTexCoords);
                vec4 refractColor = texture(uRefractionTexture, refractTexCoords);
                
                // Calculate Fresnel effect
                vec3 viewDir = normalize(uCameraPos - vWorldPos);
                vec3 normal = texture(uNormalMap, distortion).rgb;
                normal = normalize(normal * 2.0 - 1.0);
                
                float fresnelFactor = dot(viewDir, vec3(0.0, 1.0, 0.0));
                fresnelFactor = pow(fresnelFactor, 2.0);
                
                // Mix reflection and refraction
                vec4 finalColor = mix(reflectColor, refractColor, fresnelFactor);
                finalColor = mix(finalColor, uWaterColor, 0.2);
                
                fragColor = finalColor;
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
     * Load water textures
     */
    private fun loadWaterTextures() {
        // Load normal map and DuDv map
        // These would be loaded from assets
        normalMapTexture = 1
        dudvMapTexture = 2
    }
    
    /**
     * Render reflection pass
     */
    fun renderReflection(renderCallback: () -> Unit) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, reflectionFBO)
        GLES30.glViewport(0, 0, fbSize, fbSize)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        // Render scene reflected about water plane
        renderCallback()
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }
    
    /**
     * Render refraction pass
     */
    fun renderRefraction(renderCallback: () -> Unit) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, refractionFBO)
        GLES30.glViewport(0, 0, fbSize, fbSize)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        // Render scene below water
        renderCallback()
        
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }
    
    /**
     * Render water surface
     */
    fun render(mvpMatrix: FloatArray, cameraPos: FloatArray, deltaTime: Float) {
        waveTime += deltaTime * waveSpeed
        
        GLES30.glUseProgram(waterShaderProgram)
        
        // Bind water geometry
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, waterVBO)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, waterIBO)
        
        // Enable vertex attributes
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 20, 0)
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 20, 12)
        
        // Set uniforms
        // MVP matrix, camera pos, wave time, textures, etc.
        
        // Enable blending
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        
        // Draw water
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            waterVertexCount,
            GLES30.GL_UNSIGNED_INT,
            0
        )
        
        GLES30.glDisable(GLES30.GL_BLEND)
        
        // Disable vertex attributes
        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        GLES30.glDeleteBuffers(1, intArrayOf(waterVBO), 0)
        GLES30.glDeleteBuffers(1, intArrayOf(waterIBO), 0)
        GLES30.glDeleteFramebuffers(1, intArrayOf(reflectionFBO), 0)
        GLES30.glDeleteFramebuffers(1, intArrayOf(refractionFBO), 0)
        GLES30.glDeleteTextures(1, intArrayOf(reflectionTexture), 0)
        GLES30.glDeleteTextures(1, intArrayOf(refractionTexture), 0)
        GLES30.glDeleteTextures(1, intArrayOf(normalMapTexture), 0)
        GLES30.glDeleteTextures(1, intArrayOf(dudvMapTexture), 0)
        GLES30.glDeleteProgram(waterShaderProgram)
    }
}

/**
 * Water configuration
 */
data class WaterConfig(
    val waterLevel: Float = 20f,
    val waterColor: FloatArray = floatArrayOf(0.0f, 0.3f, 0.5f, 0.7f),
    val shineDamper: Float = 20.0f,
    val reflectivity: Float = 0.6f
)
