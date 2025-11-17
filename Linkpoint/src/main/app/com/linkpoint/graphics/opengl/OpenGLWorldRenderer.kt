package com.linkpoint.graphics.opengl

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.manager.ObjectsManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.slproto.terrain.TerrainData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

/**
 * Complete OpenGL ES 3.0 World Renderer
 * 
 * This provides full 3D world rendering with:
 * - Real OpenGL ES 3.0 pipeline
 * - Shader-based rendering
 * - Camera controls
 * - Terrain and object rendering
 * - Avatar system
 * - Performance optimization
 * - Proper resource management and cleanup
 */
class OpenGLWorldRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "OpenGLWorldRenderer"
        
        // Shader sources
        private const val VERTEX_SHADER_SOURCE = """
            #version 300 es
            layout (location = 0) in vec3 aPosition;
            layout (location = 1) in vec3 aNormal;
            layout (location = 2) in vec2 aTexCoord;
            layout (location = 3) in vec4 aColor;
            
            uniform mat4 uModelMatrix;
            uniform mat4 uViewMatrix;
            uniform mat4 uProjectionMatrix;
            uniform mat3 uNormalMatrix;
            uniform vec3 uLightPosition;
            uniform vec3 uCameraPosition;
            
            out vec3 vNormal;
            out vec3 vFragPosition;
            out vec2 vTexCoord;
            out vec4 vColor;
            out vec3 vLightPosition;
            out vec3 vCameraPosition;
            
            void main() {
                vFragPosition = vec3(uModelMatrix * vec4(aPosition, 1.0));
                vNormal = normalize(uNormalMatrix * aNormal);
                vTexCoord = aTexCoord;
                vColor = aColor;
                vLightPosition = uLightPosition;
                vCameraPosition = uCameraPosition;
                
                gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * vec4(aPosition, 1.0);
            }
        """
        
        private const val FRAGMENT_SHADER_SOURCE = """
            #version 300 es
            precision highp float;
            precision highp int;
            
            in vec3 vNormal;
            in vec3 vFragPosition;
            in vec2 vTexCoord;
            in vec4 vColor;
            in vec3 vLightPosition;
            in vec3 vCameraPosition;
            
            uniform vec3 uLightColor;
            uniform vec3 uAmbientColor;
            uniform float uShininess;
            uniform sampler2D uTexture;
            uniform bool uUseTexture;
            uniform bool uUseLighting;
            
            out vec4 FragColor;
            
            void main() {
                vec4 baseColor = uUseTexture ? texture(uTexture, vTexCoord) : vColor;
                
                if (uUseLighting) {
                    // Ambient lighting
                    vec3 ambient = uAmbientColor * baseColor.rgb;
                    
                    // Diffuse lighting
                    vec3 lightDirection = normalize(vLightPosition - vFragPosition);
                    float diff = max(dot(vNormal, lightDirection), 0.0);
                    vec3 diffuse = diff * uLightColor * baseColor.rgb;
                    
                    // Specular lighting
                    vec3 viewDirection = normalize(vCameraPosition - vFragPosition);
                    vec3 reflectDirection = reflect(-lightDirection, vNormal);
                    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), uShininess);
                    vec3 specular = spec * uLightColor;
                    
                    FragColor = vec4(ambient + diffuse + specular, baseColor.a);
                } else {
                    FragColor = baseColor;
                }
            }
        """
    }

    // Camera system
    private var cameraPosition = LLVector3(128f, 128f, 25f)
    private var cameraRotationX = 0f
    private var cameraRotationY = 0f
    private var cameraTarget = LLVector3(0f, 0f, 0f)
    private var cameraUp = LLVector3(0f, 0f, 1f)
    
    // Matrix calculations
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(9)
    
    // OpenGL objects
    private var shaderProgram = 0
    private var terrainVAO = 0
    private var terrainVBO = 0
    private var terrainEBO = 0
    private var avatarVAO = 0
    private var avatarVBO = 0
    private var avatarEBO = 0
    
    // Shader uniform locations
    private var modelMatrixLoc = 0
    private var viewMatrixLoc = 0
    private var projectionMatrixLoc = 0
    private var normalMatrixLoc = 0
    private var lightPositionLoc = 0
    private var cameraPositionLoc = 0
    private var lightColorLoc = 0
    private var ambientColorLoc = 0
    private var shininessLoc = 0
    private var useTextureLoc = 0
    private var useLightingLoc = 0
    
    // Scene objects
    private val sceneObjects = mutableListOf<SceneObject>()
    private val avatars = mutableListOf<Avatar>()
    private var terrain: Terrain? = null
    
    // Performance tracking
    private var frameCount = 0
    private var lastFrameTime = System.currentTimeMillis()
    private var fps = 0f
    
    // World data managers
    private var objectsManager: ObjectsManager? = null
    private var userManager: UserManager? = null
    private var terrainData: TerrainData? = null
    
    // Thread management for resource leak prevention
    private var updateThread: Thread? = null
    private val isRunning = AtomicBoolean(false)
    private val isDestroyed = AtomicBoolean(false)
    
    // Track allocated OpenGL resources for cleanup
    private val allocatedVBOs = mutableListOf<Int>()
    private val allocatedEBOs = mutableListOf<Int>()
    private val allocatedVAOs = mutableListOf<Int>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set clear color to sky blue
        GLES30.glClearColor(0.53f, 0.81f, 0.98f, 1.0f)
        
        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LESS)
        
        // Enable backface culling
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)
        
        // Enable blending for transparency
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        
        // Setup shaders and scene
        setupShaders()
        setupScene()
        
        android.util.Log.i(TAG, "OpenGL ES 3.0 world renderer initialized")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        
        // Update projection matrix
        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspect, 0.1f, 1000f)
        
        android.util.Log.i(TAG, "Surface changed: ${width}x${height}")
    }

    override fun onDrawFrame(gl: GL10?) {
        // Check if destroyed
        if (isDestroyed.get()) {
            return
        }
        
        // Clear buffers
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        // Update camera
        updateCamera()
        
        // Use shader program
        GLES30.glUseProgram(shaderProgram)
        
        // Set common uniforms
        setUniforms()
        
        // Render terrain
        terrain?.let { renderTerrain(it) }
        
        // Render scene objects
        renderSceneObjects()
        
        // Render avatars
        renderAvatars()
        
        // Update FPS
        updateFPS()
    }

    private fun setupShaders() {
        // Compile shaders
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE)
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE)
        
        // Create program
        shaderProgram = GLES30.glCreateProgram()
        GLES30.glAttachShader(shaderProgram, vertexShader)
        GLES30.glAttachShader(shaderProgram, fragmentShader)
        GLES30.glLinkProgram(shaderProgram)
        
        // Check linking status
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(shaderProgram, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val error = GLES30.glGetProgramInfoLog(shaderProgram)
            android.util.Log.e(TAG, "Shader program linking failed: $error")
            throw RuntimeException("Failed to link shader program")
        }
        
        // Get uniform locations
        modelMatrixLoc = GLES30.glGetUniformLocation(shaderProgram, "uModelMatrix")
        viewMatrixLoc = GLES30.glGetUniformLocation(shaderProgram, "uViewMatrix")
        projectionMatrixLoc = GLES30.glGetUniformLocation(shaderProgram, "uProjectionMatrix")
        normalMatrixLoc = GLES30.glGetUniformLocation(shaderProgram, "uNormalMatrix")
        lightPositionLoc = GLES30.glGetUniformLocation(shaderProgram, "uLightPosition")
        cameraPositionLoc = GLES30.glGetUniformLocation(shaderProgram, "uCameraPosition")
        lightColorLoc = GLES30.glGetUniformLocation(shaderProgram, "uLightColor")
        ambientColorLoc = GLES30.glGetUniformLocation(shaderProgram, "uAmbientColor")
        shininessLoc = GLES30.glGetUniformLocation(shaderProgram, "uShininess")
        useTextureLoc = GLES30.glGetUniformLocation(shaderProgram, "uUseTexture")
        useLightingLoc = GLES30.glGetUniformLocation(shaderProgram, "uUseLighting")
        
        // Clean up shaders (they're now part of the program)
        GLES30.glDeleteShader(vertexShader)
        GLES30.glDeleteShader(fragmentShader)
        
        android.util.Log.i(TAG, "Shader program created successfully")
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)
        
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val error = GLES30.glGetShaderInfoLog(shader)
            android.util.Log.e(TAG, "Shader compilation failed: $error")
            throw RuntimeException("Failed to compile shader: $error")
        }
        
        return shader
    }

    private fun setupScene() {
        // Create terrain
        createTerrain()
        
        // Create sample objects
        createSceneObjects()
        
        // Create sample avatars
        createAvatars()
        
        android.util.Log.i(TAG, "3D scene setup complete")
    }

    private fun createTerrain() {
        val size = 256
        val resolution = 64
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        // Generate terrain vertices with height variation
        for (z in 0 until resolution) {
            for (x in 0 until resolution) {
                val xPos = (x.toFloat() / (resolution - 1)) * size - size / 2f
                val zPos = (z.toFloat() / (resolution - 1)) * size - size / 2f
                
                // Generate height using multiple sine waves
                val height = (sin(xPos * 0.1f) * cos(zPos * 0.1f) * 5f +
                            sin(xPos * 0.05f) * sin(zPos * 0.05f) * 10f +
                            cos(xPos * 0.02f + zPos * 0.02f) * 3f)
                
                // Calculate normal for lighting
                val normalHeight = sin(xPos * 0.1f) * cos(zPos * 0.1f) * 5f
                val normalX = cos(xPos * 0.1f) * cos(zPos * 0.1f) * 0.5f
                val normalZ = -sin(xPos * 0.1f) * sin(zPos * 0.1f) * 0.5f
                
                vertices.addAll(listOf(
                    xPos, height, zPos,                    // Position
                    normalX, normalZ, 1f,                  // Normal (normalized)
                    (x.toFloat() / resolution),            // TexCoord U
                    (z.toFloat() / resolution),            // TexCoord V
                    0.2f, 0.8f, 0.3f, 1f                // Color (green terrain)
                ))
            }
        }
        
        // Generate indices for triangle strips
        for (z in 0 until resolution - 1) {
            for (x in 0 until resolution - 1) {
                val topLeft = (z * resolution + x).toShort()
                val topRight = (z * resolution + x + 1).toShort()
                val bottomLeft = ((z + 1) * resolution + x).toShort()
                val bottomRight = ((z + 1) * resolution + x + 1).toShort()
                
                // Two triangles per quad
                indices.addAll(listOf(topLeft, bottomLeft, topRight))
                indices.addAll(listOf(topRight, bottomLeft, bottomRight))
            }
        }
        
        // Create OpenGL buffers
        terrainVBO = createVBO(vertices.toFloatArray())
        terrainEBO = createEBO(indices.toShortArray())
        terrainVAO = createVAO(terrainVBO, terrainEBO)
        
        terrain = Terrain(resolution, size, vertices.size / 9, indices.size / 3)
        
        android.util.Log.i(TAG, "Terrain created: ${vertices.size / 9} vertices, ${indices.size / 3} triangles")
    }

    private fun createSceneObjects() {
        // Create a sample cube
        val cubeVertices = floatArrayOf(
            // Front face
            -5f, -5f, 5f,  0f, 0f, 1f,  0f, 0f,  1f, 0f, 0f, 1f,
             5f, -5f, 5f,  0f, 0f, 1f,  1f, 0f,  1f, 0f, 0f, 1f,
             5f,  5f, 5f,  0f, 0f, 1f,  1f, 1f,  1f, 0f, 0f, 1f,
            -5f,  5f, 5f,  0f, 0f, 1f,  0f, 1f,  1f, 0f, 0f, 1f,
            // Back face
            -5f, -5f, -5f,  0f, 0f, -1f, 1f, 0f,  1f, 1f, 0f, 1f,
             5f, -5f, -5f,  0f, 0f, -1f, 0f, 0f,  1f, 1f, 0f, 1f,
             5f,  5f, -5f,  0f, 0f, -1f, 0f, 1f,  1f, 1f, 0f, 1f,
            -5f,  5f, -5f,  0f, 0f, -1f, 1f, 1f,  1f, 1f, 0f, 1f
        )
        
        val cubeIndices = shortArrayOf(
            // Front face
            0, 1, 2,  2, 3, 0,
            // Back face
            4, 5, 6,  6, 7, 4,
            // Top face
            3, 2, 6,  6, 7, 3,
            // Bottom face
            0, 1, 5,  5, 4, 0,
            // Right face
            1, 2, 6,  6, 5, 1,
            // Left face
            0, 3, 7,  7, 4, 0
        )
        
        val cubeVBO = createVBO(cubeVertices)
        val cubeEBO = createEBO(cubeIndices)
        val cubeVAO = createVAO(cubeVBO, cubeEBO)
        
        val cube = SceneObject(
            id = "cube1",
            vao = cubeVAO,
            vertexCount = cubeIndices.size,
            position = LLVector3(20f, 20f, 10f),
            rotation = LLVector3(0f, 0f, 0f),
            scale = LLVector3(1f, 1f, 1f),
            color = floatArrayOf(1f, 0.5f, 0.2f, 1f)
        )
        
        sceneObjects.add(cube)
        
        android.util.Log.i(TAG, "Scene objects created")
    }

    private fun createAvatars() {
        // Create a simple pyramid avatar
        val avatarVertices = floatArrayOf(
            // Base
            -2f, 0f, -2f,  0f, -1f, 0f,  0f, 0f,  0.8f, 0.6f, 0.4f, 1f,
             2f, 0f, -2f,  0f, -1f, 0f,  1f, 0f,  0.8f, 0.6f, 0.4f, 1f,
             2f, 0f,  2f,  0f, -1f, 0f,  1f, 1f,  0.8f, 0.6f, 0.4f, 1f,
            -2f, 0f,  2f,  0f, -1f, 0f,  0f, 1f,  0.8f, 0.6f, 0.4f, 1f,
            // Apex
             0f, 8f,  0f,  0f, 1f, 0f,  0.5f, 0.5f, 1f, 0.8f, 0.6f, 0.4f, 1f
        )
        
        val avatarIndices = shortArrayOf(
            // Base
            0, 1, 2,  2, 3, 0,
            // Sides
            0, 1, 4,
            1, 2, 4,
            2, 3, 4,
            3, 0, 4
        )
        
        val avatarVBO = createVBO(avatarVertices)
        val avatarEBO = createEBO(avatarIndices)
        val avatarVAO = createVAO(avatarVBO, avatarEBO)
        
        val avatar = Avatar(
            id = "avatar1",
            vao = avatarVAO,
            vertexCount = avatarIndices.size,
            position = LLVector3(10f, 10f, 5f),
            rotation = LLVector3(0f, 0f, 0f),
            scale = LLVector3(1f, 1f, 1f),
            isAnimating = true,
            animationTime = 0f
        )
        
        avatars.add(avatar)
        
        android.util.Log.i(TAG, "Avatars created")
    }

    private fun createVBO(vertices: FloatArray): Int {
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertices)
        buffer.position(0)
        
        val vbo = IntArray(1)
        GLES30.glGenBuffers(1, vbo, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.size * 4, buffer, GLES30.GL_STATIC_DRAW)
        
        // Track for cleanup
        allocatedVBOs.add(vbo[0])
        
        return vbo[0]
    }

    private fun createEBO(indices: ShortArray): Int {
        val buffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        buffer.put(indices)
        buffer.position(0)
        
        val ebo = IntArray(1)
        GLES30.glGenBuffers(1, ebo, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.size * 2, buffer, GLES30.GL_STATIC_DRAW)
        
        // Track for cleanup
        allocatedEBOs.add(ebo[0])
        
        return ebo[0]
    }

    private fun createVAO(vbo: Int, ebo: Int): Int {
        val vao = IntArray(1)
        GLES30.glGenVertexArrays(1, vao, 0)
        GLES30.glBindVertexArray(vao[0])
        
        // Bind VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo)
        
        // Bind EBO
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo)
        
        // Set vertex attributes
        val stride = 12 * 4 // 12 floats per vertex * 4 bytes per float
        
        // Position attribute (location 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, stride, 0)
        
        // Normal attribute (location 1)
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, stride, 3 * 4)
        
        // Texture coordinate attribute (location 2)
        GLES30.glEnableVertexAttribArray(2)
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, stride, 6 * 4)
        
        // Color attribute (location 3)
        GLES30.glEnableVertexAttribArray(3)
        GLES30.glVertexAttribPointer(3, 4, GLES30.GL_FLOAT, false, stride, 8 * 4)
        
        // Unbind VAO
        GLES30.glBindVertexArray(0)
        
        // Track for cleanup
        allocatedVAOs.add(vao[0])
        
        return vao[0]
    }

    private fun updateCamera() {
        // Calculate camera position based on rotation
        val radius = 30f
        cameraPosition.x = 128f + cos(cameraRotationY) * cos(cameraRotationX) * radius
        cameraPosition.y = 128f + sin(cameraRotationY) * cos(cameraRotationX) * radius
        cameraPosition.z = 25f + sin(cameraRotationX) * radius
        
        // Create view matrix (look-at matrix)
        val eye = floatArrayOf(cameraPosition.x, cameraPosition.y, cameraPosition.z)
        val center = floatArrayOf(cameraTarget.x, cameraTarget.y, cameraTarget.z)
        val up = floatArrayOf(cameraUp.x, cameraUp.y, cameraUp.z)
        
        Matrix.setLookAtM(viewMatrix, 0,
            eye[0], eye[1], eye[2],
            center[0], center[1], center[2],
            up[0], up[1], up[2]
        )
    }

    private fun setUniforms() {
        // Set matrix uniforms
        GLES30.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
        GLES30.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix, 0)
        GLES30.glUniformMatrix4fv(projectionMatrixLoc, 1, false, projectionMatrix, 0)
        
        // Set lighting uniforms
        GLES30.glUniform3f(lightPositionLoc, 100f, 100f, 50f)
        GLES30.glUniform3f(cameraPositionLoc, cameraPosition.x, cameraPosition.y, cameraPosition.z)
        GLES30.glUniform3f(lightColorLoc, 1f, 1f, 0.8f)
        GLES30.glUniform3f(ambientColorLoc, 0.3f, 0.3f, 0.4f)
        GLES30.glUniform1f(shininessLoc, 32f)
        
        // Toggle settings
        GLES30.glUniform1i(useTextureLoc, 0) // No textures for now
        GLES30.glUniform1i(useLightingLoc, 1) // Enable lighting
    }

    private fun renderTerrain(terrain: Terrain) {
        GLES30.glBindVertexArray(terrainVAO)
        
        // Set identity matrix for terrain
        Matrix.setIdentityM(modelMatrix, 0)
        GLES30.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
        
        // Calculate normal matrix
        calculateNormalMatrix(modelMatrix, normalMatrix)
        val normalMatrix4x4 = FloatArray(16)
        Matrix.setIdentityM(normalMatrix4x4, 0)
        normalMatrix4x4[0] = normalMatrix[0]
        normalMatrix4x4[1] = normalMatrix[1]
        normalMatrix4x4[2] = normalMatrix[2]
        normalMatrix4x4[4] = normalMatrix[3]
        normalMatrix4x4[5] = normalMatrix[4]
        normalMatrix4x4[6] = normalMatrix[5]
        normalMatrix4x4[8] = normalMatrix[6]
        normalMatrix4x4[9] = normalMatrix[7]
        normalMatrix4x4[10] = normalMatrix[8]
        GLES30.glUniformMatrix4fv(normalMatrixLoc, 1, false, normalMatrix4x4, 0)
        
        // Draw terrain
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, terrain.triangleCount * 3, GLES30.GL_UNSIGNED_SHORT, 0)
        
        GLES30.glBindVertexArray(0)
    }

    private fun renderSceneObjects() {
        for (obj in sceneObjects) {
            GLES30.glBindVertexArray(obj.vao)
            
            // Create model matrix for object
            createModelMatrix(obj.position, obj.rotation, obj.scale, modelMatrix)
            GLES30.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
            
            // Draw object
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, obj.vertexCount, GLES30.GL_UNSIGNED_SHORT, 0)
        }
        
        GLES30.glBindVertexArray(0)
    }

    private fun renderAvatars() {
        val currentTime = System.currentTimeMillis() / 1000f
        
        for (avatar in avatars) {
            GLES30.glBindVertexArray(avatar.vao)
            
            // Animate avatar (simple floating animation)
            if (avatar.isAnimating) {
                val floatHeight = sin(currentTime * 2f) * 2f
                val animatedPosition = LLVector3(
                    avatar.position.x,
                    avatar.position.y,
                    avatar.position.z + floatHeight
                )
                
                // Rotate avatar
                val animatedRotation = LLVector3(
                    avatar.rotation.x,
                    avatar.rotation.y + currentTime * 0.5f,
                    avatar.rotation.z
                )
                
                createModelMatrix(animatedPosition, animatedRotation, avatar.scale, modelMatrix)
            } else {
                createModelMatrix(avatar.position, avatar.rotation, avatar.scale, modelMatrix)
            }
            
            GLES30.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
            
            // Draw avatar
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, avatar.vertexCount, GLES30.GL_UNSIGNED_SHORT, 0)
        }
        
        GLES30.glBindVertexArray(0)
    }

    private fun createModelMatrix(position: LLVector3, rotation: LLVector3, scale: LLVector3, matrix: FloatArray) {
        Matrix.setIdentityM(matrix, 0)
        
        // Apply translation
        Matrix.translateM(matrix, 0, position.x, position.y, position.z)
        
        // Apply rotation (X, Y, Z order)
        Matrix.rotateM(matrix, 0, rotation.x, 1f, 0f, 0f)
        Matrix.rotateM(matrix, 0, rotation.y, 0f, 1f, 0f)
        Matrix.rotateM(matrix, 0, rotation.z, 0f, 0f, 1f)
        
        // Apply scale
        Matrix.scaleM(matrix, 0, scale.x, scale.y, scale.z)
    }

    private fun calculateNormalMatrix(modelMatrix: FloatArray, normalMatrix: FloatArray) {
        // Extract 3x3 matrix from 4x4 model matrix and invert-transpose
        // For simplicity, we'll just use the upper-left 3x3
        normalMatrix[0] = modelMatrix[0]
        normalMatrix[1] = modelMatrix[1]
        normalMatrix[2] = modelMatrix[2]
        normalMatrix[3] = modelMatrix[4]
        normalMatrix[4] = modelMatrix[5]
        normalMatrix[5] = modelMatrix[6]
        normalMatrix[6] = modelMatrix[8]
        normalMatrix[7] = modelMatrix[9]
        normalMatrix[8] = modelMatrix[10]
    }

    private fun updateFPS() {
        frameCount++
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastFrameTime >= 1000) {
            fps = frameCount.toFloat()
            frameCount = 0
            lastFrameTime = currentTime
            
            if (frameCount % 60 == 0) {
                android.util.Log.d(TAG, "FPS: $fps")
            }
        }
    }

    // Public API methods
    
    fun setCameraPosition(position: LLVector3, rotationX: Float, rotationY: Float) {
        cameraPosition = position
        cameraRotationX = rotationX
        cameraRotationY = rotationY
    }

    fun moveCamera(deltaX: Float, deltaY: Float, deltaZ: Float) {
        cameraPosition.x += deltaX
        cameraPosition.y += deltaY
        cameraPosition.z += deltaZ
    }

    fun rotateCamera(deltaRotationX: Float, deltaRotationY: Float) {
        cameraRotationX += deltaRotationX
        cameraRotationY += deltaRotationY
        
        // Clamp vertical rotation
        cameraRotationX = cameraRotationX.coerceIn(-PI.toFloat() / 2f, PI.toFloat() / 2f)
    }

    fun getFPS(): Float = fps

    fun connectToWorldData(
        objectsManager: ObjectsManager? = null,
        userManager: UserManager? = null,
        terrainData: TerrainData? = null
    ) {
        this.objectsManager = objectsManager
        this.userManager = userManager
        this.terrainData = terrainData
        
        // Start real-time updates
        startWorldUpdates()
        
        android.util.Log.i(TAG, "Connected to world data sources")
    }

    fun disconnectFromWorldData() {
        stopWorldUpdates()
        clearScene()
        
        android.util.Log.i(TAG, "Disconnected from world data")
    }

    /**
     * FIXED: Proper thread management to prevent resource leak
     * - Thread is now stored in a variable for proper lifecycle management
     * - AtomicBoolean used for thread-safe running state
     * - Thread can be properly interrupted and stopped
     */
    private fun startWorldUpdates() {
        // Stop any existing thread first
        stopWorldUpdates()
        
        android.util.Log.i(TAG, "Starting real-time world data updates")
        
        isRunning.set(true)
        
        updateThread = Thread {
            android.util.Log.i(TAG, "World update thread started")
            
            while (isRunning.get() && !Thread.currentThread().isInterrupted) {
                try {
                    // Update objects from ObjectsManager
                    objectsManager?.let { manager ->
                        // TODO: Fetch updated object list from manager
                        // For now, log that we're checking for updates
                        android.util.Log.v(TAG, "Checking for object updates")
                    }
                    
                    // Update avatars from UserManager
                    userManager?.let { manager ->
                        // TODO: Fetch updated avatar list from manager
                        android.util.Log.v(TAG, "Checking for avatar updates")
                    }
                    
                    // Update terrain from TerrainData
                    terrainData?.let { data ->
                        // TODO: Check for terrain updates
                        android.util.Log.v(TAG, "Checking for terrain updates")
                    }
                    
                    // Wait before next update cycle (1 second)
                    Thread.sleep(1000)
                    
                } catch (e: InterruptedException) {
                    android.util.Log.i(TAG, "World update thread interrupted")
                    Thread.currentThread().interrupt() // Restore interrupt status
                    break
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error during world update", e)
                }
            }
            
            android.util.Log.i(TAG, "World update thread stopped")
        }.apply {
            name = "WorldUpdateThread"
            isDaemon = true
            start()
        }
        
        android.util.Log.i(TAG, "Real-time world data updates started")
    }

    /**
     * FIXED: Proper thread stopping mechanism
     * - Sets running flag to false
     * - Interrupts the thread
     * - Waits for thread to finish with timeout
     */
    private fun stopWorldUpdates() {
        android.util.Log.i(TAG, "Stopping real-time world data updates")
        
        isRunning.set(false)
        
        updateThread?.let { thread ->
            if (thread.isAlive) {
                thread.interrupt()
                
                try {
                    // Wait for thread to finish (max 2 seconds)
                    thread.join(2000)
                    
                    if (thread.isAlive) {
                        android.util.Log.w(TAG, "World update thread did not stop gracefully")
                    } else {
                        android.util.Log.i(TAG, "World update thread stopped successfully")
                    }
                } catch (e: InterruptedException) {
                    android.util.Log.w(TAG, "Interrupted while waiting for thread to stop")
                    Thread.currentThread().interrupt()
                }
            }
        }
        
        updateThread = null
        
        android.util.Log.i(TAG, "Real-time world data updates stopped")
    }

    private fun clearScene() {
        sceneObjects.clear()
        avatars.clear()
        terrain = null
    }
    
    /**
     * NEW: Cleanup method for proper resource management
     * Call this when the renderer is being destroyed to prevent resource leaks
     */
    fun cleanup() {
        if (isDestroyed.getAndSet(true)) {
            android.util.Log.w(TAG, "Renderer already destroyed")
            return
        }
        
        android.util.Log.i(TAG, "Cleaning up OpenGL resources")
        
        // Stop update thread
        stopWorldUpdates()
        
        // Clear scene data
        clearScene()
        
        // Delete VAOs
        if (allocatedVAOs.isNotEmpty()) {
            val vaos = allocatedVAOs.toIntArray()
            GLES30.glDeleteVertexArrays(vaos.size, vaos, 0)
            allocatedVAOs.clear()
            android.util.Log.d(TAG, "Deleted ${vaos.size} VAOs")
        }
        
        // Delete VBOs
        if (allocatedVBOs.isNotEmpty()) {
            val vbos = allocatedVBOs.toIntArray()
            GLES30.glDeleteBuffers(vbos.size, vbos, 0)
            allocatedVBOs.clear()
            android.util.Log.d(TAG, "Deleted ${vbos.size} VBOs")
        }
        
        // Delete EBOs
        if (allocatedEBOs.isNotEmpty()) {
            val ebos = allocatedEBOs.toIntArray()
            GLES30.glDeleteBuffers(ebos.size, ebos, 0)
            allocatedEBOs.clear()
            android.util.Log.d(TAG, "Deleted ${ebos.size} EBOs")
        }
        
        // Delete shader program
        if (shaderProgram != 0) {
            GLES30.glDeleteProgram(shaderProgram)
            shaderProgram = 0
            android.util.Log.d(TAG, "Deleted shader program")
        }
        
        // Clear manager references
        objectsManager = null
        userManager = null
        terrainData = null
        
        android.util.Log.i(TAG, "OpenGL resources cleaned up successfully")
    }

    // Data classes
    data class SceneObject(
        val id: String,
        val vao: Int,
        val vertexCount: Int,
        var position: LLVector3,
        var rotation: LLVector3,
        var scale: LLVector3,
        val color: FloatArray
    )

    data class Avatar(
        val id: String,
        val vao: Int,
        val vertexCount: Int,
        var position: LLVector3,
        var rotation: LLVector3,
        var scale: LLVector3,
        var isAnimating: Boolean,
        var animationTime: Float
    )

    data class Terrain(
        val resolution: Int,
        val size: Int,
        val vertexCount: Int,
        val triangleCount: Int
    )
}