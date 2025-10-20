package com.lumiyaviewer.lumiya.animesh

import android.opengl.GLES32
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.UUID

/**
 * Animesh Renderer
 * Renders animated mesh attachments with GPU skinning
 */
class AnimeshRenderer {
    companion object {
        private const val TAG = "AnimeshRenderer"
        
        // Vertex shader with skinning support
        private const val VERTEX_SHADER = """
            #version 320 es
            precision highp float;
            
            layout(location = 0) in vec3 aPosition;
            layout(location = 1) in vec3 aNormal;
            layout(location = 2) in vec2 aTexCoord;
            layout(location = 3) in vec4 aBoneIndices;
            layout(location = 4) in vec4 aBoneWeights;
            
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            uniform mat4 uNormalMatrix;
            uniform mat4 uBoneMatrices[256];
            
            out vec3 vWorldPos;
            out vec3 vNormal;
            out vec2 vTexCoord;
            
            void main() {
                // GPU skinning calculation
                mat4 boneTransform = mat4(0.0);
                
                boneTransform += uBoneMatrices[int(aBoneIndices.x)] * aBoneWeights.x;
                boneTransform += uBoneMatrices[int(aBoneIndices.y)] * aBoneWeights.y;
                boneTransform += uBoneMatrices[int(aBoneIndices.z)] * aBoneWeights.z;
                boneTransform += uBoneMatrices[int(aBoneIndices.w)] * aBoneWeights.w;
                
                vec4 skinnedPosition = boneTransform * vec4(aPosition, 1.0);
                vec4 skinnedNormal = boneTransform * vec4(aNormal, 0.0);
                
                vec4 worldPos = uModelMatrix * skinnedPosition;
                vWorldPos = worldPos.xyz;
                vNormal = mat3(uNormalMatrix) * skinnedNormal.xyz;
                vTexCoord = aTexCoord;
                
                gl_Position = uMVPMatrix * skinnedPosition;
            }
        """
        
        // Fragment shader (same as regular mesh)
        private const val FRAGMENT_SHADER = """
            #version 320 es
            precision highp float;
            
            in vec3 vWorldPos;
            in vec3 vNormal;
            in vec2 vTexCoord;
            
            uniform sampler2D uTexture;
            uniform vec3 uLightPos;
            uniform vec3 uLightColor;
            uniform vec3 uCameraPos;
            
            out vec4 FragColor;
            
            void main() {
                vec3 N = normalize(vNormal);
                vec3 L = normalize(uLightPos - vWorldPos);
                vec3 V = normalize(uCameraPos - vWorldPos);
                vec3 H = normalize(L + V);
                
                // Sample texture
                vec4 texColor = texture(uTexture, vTexCoord);
                
                // Simple lighting
                float diff = max(dot(N, L), 0.0);
                float spec = pow(max(dot(N, H), 0.0), 32.0);
                
                vec3 ambient = vec3(0.2);
                vec3 diffuse = diff * uLightColor;
                vec3 specular = spec * vec3(0.3);
                
                vec3 color = texColor.rgb * (ambient + diffuse) + specular;
                
                FragColor = vec4(color, texColor.a);
            }
        """
        
        const val MAX_BONES = 256
    }
    
    private var shaderProgram: Int = 0
    private val boneMatrices = FloatArray(MAX_BONES * 16) // 4x4 matrices
    
    // Uniform locations
    private var mvpMatrixLoc = 0
    private var modelMatrixLoc = 0
    private var normalMatrixLoc = 0
    private var boneMatricesLoc = 0
    private var textureLoc = 0
    private var lightPosLoc = 0
    private var lightColorLoc = 0
    private var cameraPosLoc = 0
    
    /**
     * Initialize renderer
     */
    fun initialize(): Boolean {
        try {
            shaderProgram = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
            if (shaderProgram == 0) {
                Log.e(TAG, "Failed to create shader program")
                return false
            }
            
            // Get uniform locations
            mvpMatrixLoc = GLES32.glGetUniformLocation(shaderProgram, "uMVPMatrix")
            modelMatrixLoc = GLES32.glGetUniformLocation(shaderProgram, "uModelMatrix")
            normalMatrixLoc = GLES32.glGetUniformLocation(shaderProgram, "uNormalMatrix")
            boneMatricesLoc = GLES32.glGetUniformLocation(shaderProgram, "uBoneMatrices")
            textureLoc = GLES32.glGetUniformLocation(shaderProgram, "uTexture")
            lightPosLoc = GLES32.glGetUniformLocation(shaderProgram, "uLightPos")
            lightColorLoc = GLES32.glGetUniformLocation(shaderProgram, "uLightColor")
            cameraPosLoc = GLES32.glGetUniformLocation(shaderProgram, "uCameraPos")
            
            Log.i(TAG, "Animesh renderer initialized successfully")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize animesh renderer", e)
            return false
        }
    }
    
    /**
     * Render animesh object
     */
    fun render(
        animesh: AnimeshData,
        meshData: AnimeshMeshData,
        mvpMatrix: FloatArray,
        modelMatrix: FloatArray,
        normalMatrix: FloatArray,
        cameraPos: FloatArray,
        lightPos: FloatArray,
        lightColor: FloatArray
    ) {
        GLES32.glUseProgram(shaderProgram)
        
        // Update bone matrices from skeleton
        updateBoneMatrices(animesh.skeleton)
        
        // Set uniforms
        GLES32.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0)
        GLES32.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
        GLES32.glUniformMatrix4fv(normalMatrixLoc, 1, false, normalMatrix, 0)
        GLES32.glUniformMatrix4fv(boneMatricesLoc, MAX_BONES, false, boneMatrices, 0)
        
        GLES32.glUniform3fv(cameraPosLoc, 1, cameraPos, 0)
        GLES32.glUniform3fv(lightPosLoc, 1, lightPos, 0)
        GLES32.glUniform3fv(lightColorLoc, 1, lightColor, 0)
        
        // Bind texture
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, meshData.textureId)
        GLES32.glUniform1i(textureLoc, 0)
        
        // Bind vertex data
        bindMeshData(meshData)
        
        // Draw
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            meshData.indexCount,
            GLES32.GL_UNSIGNED_SHORT,
            0
        )
        
        // Unbind
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    
    /**
     * Update bone matrices from skeleton
     */
    private fun updateBoneMatrices(skeleton: AnimeshSkeleton) {
        skeleton.bones.forEachIndexed { index, bone ->
            if (index >= MAX_BONES) return@forEachIndexed
            
            // Convert bone transform to 4x4 matrix
            val matrix = boneToMatrix(bone)
            
            // Copy to bone matrices array
            System.arraycopy(matrix, 0, boneMatrices, index * 16, 16)
        }
    }
    
    /**
     * Convert bone to transformation matrix
     */
    private fun boneToMatrix(bone: AnimeshBone): FloatArray {
        val matrix = FloatArray(16)
        
        // Create rotation matrix from quaternion
        val q = bone.currentRotation
        val x2 = q.x * q.x
        val y2 = q.y * q.y
        val z2 = q.z * q.z
        val xy = q.x * q.y
        val xz = q.x * q.z
        val yz = q.y * q.z
        val wx = q.w * q.x
        val wy = q.w * q.y
        val wz = q.w * q.z
        
        matrix[0] = 1f - 2f * (y2 + z2)
        matrix[1] = 2f * (xy + wz)
        matrix[2] = 2f * (xz - wy)
        matrix[3] = 0f
        
        matrix[4] = 2f * (xy - wz)
        matrix[5] = 1f - 2f * (x2 + z2)
        matrix[6] = 2f * (yz + wx)
        matrix[7] = 0f
        
        matrix[8] = 2f * (xz + wy)
        matrix[9] = 2f * (yz - wx)
        matrix[10] = 1f - 2f * (x2 + y2)
        matrix[11] = 0f
        
        // Add translation
        matrix[12] = bone.currentPosition.x
        matrix[13] = bone.currentPosition.y
        matrix[14] = bone.currentPosition.z
        matrix[15] = 1f
        
        return matrix
    }
    
    /**
     * Bind mesh data for rendering
     */
    private fun bindMeshData(meshData: AnimeshMeshData) {
        // Bind vertex buffer
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, meshData.vbo)
        
        // Position attribute
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 3, GLES32.GL_FLOAT, false, meshData.stride, 0)
        
        // Normal attribute
        GLES32.glEnableVertexAttribArray(1)
        GLES32.glVertexAttribPointer(1, 3, GLES32.GL_FLOAT, false, meshData.stride, 12)
        
        // TexCoord attribute
        GLES32.glEnableVertexAttribArray(2)
        GLES32.glVertexAttribPointer(2, 2, GLES32.GL_FLOAT, false, meshData.stride, 24)
        
        // Bone indices attribute
        GLES32.glEnableVertexAttribArray(3)
        GLES32.glVertexAttribPointer(3, 4, GLES32.GL_FLOAT, false, meshData.stride, 32)
        
        // Bone weights attribute
        GLES32.glEnableVertexAttribArray(4)
        GLES32.glVertexAttribPointer(4, 4, GLES32.GL_FLOAT, false, meshData.stride, 48)
        
        // Bind index buffer
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, meshData.ibo)
    }
    
    /**
     * Create shader program
     */
    private fun createShaderProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = compileShader(GLES32.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GLES32.GL_FRAGMENT_SHADER, fragmentSource)
        
        if (vertexShader == 0 || fragmentShader == 0) {
            return 0
        }
        
        val program = GLES32.glCreateProgram()
        GLES32.glAttachShader(program, vertexShader)
        GLES32.glAttachShader(program, fragmentShader)
        GLES32.glLinkProgram(program)
        
        val linkStatus = IntArray(1)
        GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
        
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Error linking program: ${GLES32.glGetProgramInfoLog(program)}")
            GLES32.glDeleteProgram(program)
            return 0
        }
        
        GLES32.glDeleteShader(vertexShader)
        GLES32.glDeleteShader(fragmentShader)
        
        return program
    }
    
    /**
     * Compile shader
     */
    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES32.glCreateShader(type)
        GLES32.glShaderSource(shader, source)
        GLES32.glCompileShader(shader)
        
        val compileStatus = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)
        
        if (compileStatus[0] == 0) {
            val shaderType = if (type == GLES32.GL_VERTEX_SHADER) "vertex" else "fragment"
            Log.e(TAG, "Error compiling $shaderType shader: ${GLES32.glGetShaderInfoLog(shader)}")
            GLES32.glDeleteShader(shader)
            return 0
        }
        
        return shader
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        if (shaderProgram != 0) {
            GLES32.glDeleteProgram(shaderProgram)
            shaderProgram = 0
        }
        Log.i(TAG, "Animesh renderer cleaned up")
    }
}

/**
 * Animesh mesh data for rendering
 */
data class AnimeshMeshData(
    val vbo: Int,              // Vertex buffer object
    val ibo: Int,              // Index buffer object
    val textureId: Int,        // Texture ID
    val vertexCount: Int,      // Number of vertices
    val indexCount: Int,       // Number of indices
    val stride: Int = 64       // Vertex stride (pos:3 + norm:3 + tex:2 + boneIdx:4 + boneWeight:4) * 4 bytes
) {
    companion object {
        /**
         * Create mesh data from vertex and index data
         */
        fun create(
            vertices: FloatArray,
            indices: ShortArray,
            textureId: Int
        ): AnimeshMeshData {
            val vbo = IntArray(1)
            val ibo = IntArray(1)
            
            // Create VBO
            GLES32.glGenBuffers(1, vbo, 0)
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo[0])
            
            val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            vertexBuffer.put(vertices)
            vertexBuffer.position(0)
            
            GLES32.glBufferData(
                GLES32.GL_ARRAY_BUFFER,
                vertices.size * 4,
                vertexBuffer,
                GLES32.GL_STATIC_DRAW
            )
            
            // Create IBO
            GLES32.glGenBuffers(1, ibo, 0)
            GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, ibo[0])
            
            val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
            indexBuffer.put(indices)
            indexBuffer.position(0)
            
            GLES32.glBufferData(
                GLES32.GL_ELEMENT_ARRAY_BUFFER,
                indices.size * 2,
                indexBuffer,
                GLES32.GL_STATIC_DRAW
            )
            
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
            GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
            
            return AnimeshMeshData(
                vbo = vbo[0],
                ibo = ibo[0],
                textureId = textureId,
                vertexCount = vertices.size / 16, // 16 floats per vertex
                indexCount = indices.size
            )
        }
    }
    
    /**
     * Cleanup buffers
     */
    fun cleanup() {
        GLES32.glDeleteBuffers(1, intArrayOf(vbo), 0)
        GLES32.glDeleteBuffers(1, intArrayOf(ibo), 0)
    }
}