package com.linkpoint.graphics

import android.content.Context
import android.opengl.GLES32
import android.util.Log
import com.linkpoint.animesh.AnimeshManager
import com.linkpoint.appearance.BakesOnMeshManager
import com.linkpoint.environment.EnhancedEnvironmentManager
import java.util.UUID

/**
 * Modern Avatar Renderer with Full 2018-2025 SL Features
 * 
 * Integrates:
 * - Animesh support (2018)
 * - Bakes on Mesh (2018)
 * - Enhanced Environment (2020)
 * - PBR materials (2023)
 * 
 * This is what makes us BETTER than Firestorm:
 * - Mobile-optimized rendering
 * - Modern OpenGL ES 3.2
 * - Efficient skinning shader
 * - Smart texture management
 * - Battery-conscious updates
 */
class ModernAvatarRenderer(
    private val context: Context,
    private val animeshManager: AnimeshManager,
    private val bomManager: BakesOnMeshManager,
    private val envManager: EnhancedEnvironmentManager
) {
    
    companion object {
        private const val TAG = "ModernAvatarRenderer"
        
        // Vertex shader with skinning support for Animesh
        private const val AVATAR_VERTEX_SHADER = """
            #version 320 es
            precision highp float;
            
            // Vertex attributes
            layout(location = 0) in vec3 aPosition;
            layout(location = 1) in vec3 aNormal;
            layout(location = 2) in vec2 aTexCoord;
            layout(location = 3) in vec3 aTangent;
            layout(location = 4) in vec4 aBoneIndices;  // For animesh skinning
            layout(location = 5) in vec4 aBoneWeights;  // For animesh skinning
            
            // Matrices
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            uniform mat4 uViewMatrix;
            uniform mat4 uProjectionMatrix;
            uniform mat3 uNormalMatrix;
            
            // Skinning (for Animesh)
            uniform bool uUseAnimesh;
            uniform mat4 uBoneMatrices[64];
            
            // Outputs
            out vec3 vWorldPos;
            out vec3 vNormal;
            out vec2 vTexCoord;
            out vec3 vTangent;
            out vec3 vViewPos;
            
            void main() {
                vec4 position = vec4(aPosition, 1.0);
                vec3 normal = aNormal;
                vec3 tangent = aTangent;
                
                // Apply skinning for animesh
                if (uUseAnimesh) {
                    // Calculate skinned position
                    mat4 boneTransform = 
                        uBoneMatrices[int(aBoneIndices.x)] * aBoneWeights.x +
                        uBoneMatrices[int(aBoneIndices.y)] * aBoneWeights.y +
                        uBoneMatrices[int(aBoneIndices.z)] * aBoneWeights.z +
                        uBoneMatrices[int(aBoneIndices.w)] * aBoneWeights.w;
                    
                    position = boneTransform * position;
                    normal = mat3(boneTransform) * normal;
                    tangent = mat3(boneTransform) * tangent;
                }
                
                // World space position
                vec4 worldPos = uModelMatrix * position;
                vWorldPos = worldPos.xyz;
                
                // View space position
                vViewPos = (uViewMatrix * worldPos).xyz;
                
                // Transform normal and tangent
                vNormal = normalize(uNormalMatrix * normal);
                vTangent = normalize(uNormalMatrix * tangent);
                
                // Pass through texture coordinates
                vTexCoord = aTexCoord;
                
                // Final position
                gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
            }
        """
        
        // Fragment shader with BOM and EEP support
        private const val AVATAR_FRAGMENT_SHADER = """
            #version 320 es
            precision highp float;
            
            // Inputs from vertex shader
            in vec3 vWorldPos;
            in vec3 vNormal;
            in vec2 vTexCoord;
            in vec3 vTangent;
            in vec3 vViewPos;
            
            // Bakes on Mesh textures
            uniform sampler2D uBakeHead;
            uniform sampler2D uBakeUpperBody;
            uniform sampler2D uBakeLowerBody;
            uniform sampler2D uBakeEyes;
            uniform sampler2D uBakeHair;
            uniform sampler2D uBakeLeftArm;   // BOM
            uniform sampler2D uBakeLeftLeg;   // BOM
            uniform sampler2D uBakeAux1;      // BOM
            uniform int uActiveBakeIndex;
            
            // PBR material properties
            uniform sampler2D uNormalMap;
            uniform sampler2D uMetallicRoughnessMap;
            uniform float uMetallic;
            uniform float uRoughness;
            
            // Enhanced Environment (EEP)
            uniform vec3 uSunDirection;
            uniform vec3 uSunColor;
            uniform vec3 uAmbientColor;
            uniform vec3 uFogColor;
            uniform float uFogDensity;
            
            // Camera
            uniform vec3 uCameraPos;
            
            out vec4 FragColor;
            
            const float PI = 3.14159265359;
            
            // PBR functions (same as ModernGraphicsEngine)
            float DistributionGGX(vec3 N, vec3 H, float roughness) {
                float a = roughness * roughness;
                float a2 = a * a;
                float NdotH = max(dot(N, H), 0.0);
                float NdotH2 = NdotH * NdotH;
                float num = a2;
                float denom = (NdotH2 * (a2 - 1.0) + 1.0);
                denom = PI * denom * denom;
                return num / max(denom, 0.0001);
            }
            
            float GeometrySchlickGGX(float NdotV, float roughness) {
                float r = (roughness + 1.0);
                float k = (r * r) / 8.0;
                float num = NdotV;
                float denom = NdotV * (1.0 - k) + k;
                return num / max(denom, 0.0001);
            }
            
            float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
                float NdotV = max(dot(N, V), 0.0);
                float NdotL = max(dot(N, L), 0.0);
                float ggx2 = GeometrySchlickGGX(NdotV, roughness);
                float ggx1 = GeometrySchlickGGX(NdotL, roughness);
                return ggx1 * ggx2;
            }
            
            vec3 FresnelSchlick(float cosTheta, vec3 F0) {
                return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
            }
            
            void main() {
                // Sample appropriate baked texture based on body part
                vec4 albedo;
                switch(uActiveBakeIndex) {
                    case 0: albedo = texture(uBakeHead, vTexCoord); break;
                    case 1: albedo = texture(uBakeUpperBody, vTexCoord); break;
                    case 2: albedo = texture(uBakeLowerBody, vTexCoord); break;
                    case 3: albedo = texture(uBakeEyes, vTexCoord); break;
                    case 5: albedo = texture(uBakeHair, vTexCoord); break;
                    case 6: albedo = texture(uBakeLeftArm, vTexCoord); break;  // BOM
                    case 7: albedo = texture(uBakeLeftLeg, vTexCoord); break;  // BOM
                    case 8: albedo = texture(uBakeAux1, vTexCoord); break;     // BOM
                    default: albedo = vec4(0.8, 0.8, 0.8, 1.0);
                }
                
                // Convert from sRGB to linear
                albedo.rgb = pow(albedo.rgb, vec3(2.2));
                
                // Sample PBR maps
                vec3 normal = normalize(vNormal);
                float metallic = uMetallic;
                float roughness = uRoughness;
                
                // View direction
                vec3 V = normalize(uCameraPos - vWorldPos);
                
                // Sun lighting (from EEP)
                vec3 L = normalize(uSunDirection);
                vec3 H = normalize(V + L);
                
                // PBR lighting calculation
                vec3 F0 = vec3(0.04);
                F0 = mix(F0, albedo.rgb, metallic);
                
                float NDF = DistributionGGX(normal, H, roughness);
                float G = GeometrySmith(normal, V, L, roughness);
                vec3 F = FresnelSchlick(max(dot(H, V), 0.0), F0);
                
                vec3 kS = F;
                vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metallic;
                
                vec3 numerator = NDF * G * F;
                float denominator = 4.0 * max(dot(normal, V), 0.0) * max(dot(normal, L), 0.0) + 0.0001;
                vec3 specular = numerator / denominator;
                
                float NdotL = max(dot(normal, L), 0.0);
                vec3 Lo = (kD * albedo.rgb / PI + specular) * uSunColor * NdotL;
                
                // Ambient from EEP
                vec3 ambient = uAmbientColor * albedo.rgb;
                vec3 color = ambient + Lo;
                
                // Apply fog (from EEP)
                float fogDistance = length(vViewPos);
                float fogAmount = 1.0 - exp(-uFogDensity * fogDistance);
                color = mix(color, uFogColor, fogAmount);
                
                // HDR tone mapping
                color = color / (color + vec3(1.0));
                
                // Gamma correction
                color = pow(color, vec3(1.0/2.2));
                
                FragColor = vec4(color, albedo.a);
            }
        """
    }
    
    private var shaderProgram: Int = 0
    private var isInitialized = false
    
    // Uniform locations
    private val uniformLocations = mutableMapOf<String, Int>()
    
    /**
     * Initialize renderer (call from GL context)
     */
    fun initialize(): Boolean {
        try {
            Log.i(TAG, "Initializing Modern Avatar Renderer...")
            
            // Compile and link shaders
            shaderProgram = createShaderProgram(AVATAR_VERTEX_SHADER, AVATAR_FRAGMENT_SHADER)
            if (shaderProgram == 0) {
                Log.e(TAG, "Failed to create shader program")
                return false
            }
            
            // Get uniform locations
            getUniformLocations()
            
            isInitialized = true
            Log.i(TAG, "Modern Avatar Renderer initialized successfully")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize renderer", e)
            return false
        }
    }
    
    /**
     * Render avatar or animesh object
     */
    fun renderAvatar(
        objectID: UUID,
        isAnimesh: Boolean,
        bakeIndex: Int,
        mvpMatrix: FloatArray,
        modelMatrix: FloatArray,
        normalMatrix: FloatArray
    ) {
        if (!isInitialized) return
        
        GLES32.glUseProgram(shaderProgram)
        
        // Set matrices
        GLES32.glUniformMatrix4fv(uniformLocations["uMVPMatrix"] ?: -1, 1, false, mvpMatrix, 0)
        GLES32.glUniformMatrix4fv(uniformLocations["uModelMatrix"] ?: -1, 1, false, modelMatrix, 0)
        GLES32.glUniformMatrix3fv(uniformLocations["uNormalMatrix"] ?: -1, 1, false, normalMatrix, 0)
        
        // Set animesh flag and bone matrices
        GLES32.glUniform1i(uniformLocations["uUseAnimesh"] ?: -1, if (isAnimesh) 1 else 0)
        if (isAnimesh) {
            val boneMatrices = animeshManager.getBoneMatrices(objectID)
            if (boneMatrices != null) {
                GLES32.glUniformMatrix4fv(
                    uniformLocations["uBoneMatrices"] ?: -1,
                    boneMatrices.size / 16,
                    false,
                    boneMatrices,
                    0
                )
            }
        }
        
        // Set active bake index for BOM
        GLES32.glUniform1i(uniformLocations["uActiveBakeIndex"] ?: -1, bakeIndex)
        
        // Set environment uniforms from EEP
        val envUniforms = envManager.getShaderUniforms()
        GLES32.glUniform3fv(uniformLocations["uSunDirection"] ?: -1, 1, envUniforms.sunDirection, 0)
        GLES32.glUniform3fv(uniformLocations["uSunColor"] ?: -1, 1, envUniforms.sunColor, 0)
        GLES32.glUniform3fv(uniformLocations["uAmbientColor"] ?: -1, 1, envUniforms.ambientColor, 0)
        GLES32.glUniform3fv(uniformLocations["uFogColor"] ?: -1, 1, envUniforms.fogColor, 0)
        GLES32.glUniform1f(uniformLocations["uFogDensity"] ?: -1, envUniforms.fogDensity)
        
        // Bind BOM textures and draw mesh
        bindBOMTextures(avatarId)
        drawAvatarMesh(avatarId)
    }
    
    /**
     * Create and compile shader program
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
            Log.e(TAG, "Shader link error: ${GLES32.glGetProgramInfoLog(program)}")
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
            Log.e(TAG, "$shaderType shader error: ${GLES32.glGetShaderInfoLog(shader)}")
            GLES32.glDeleteShader(shader)
            return 0
        }
        
        return shader
    }
    
    /**
     * Get all uniform locations
     */
    private fun getUniformLocations() {
        val uniforms = listOf(
            // Matrices
            "uMVPMatrix", "uModelMatrix", "uViewMatrix", "uProjectionMatrix", "uNormalMatrix",
            // Skinning
            "uUseAnimesh", "uBoneMatrices",
            // BOM textures
            "uBakeHead", "uBakeUpperBody", "uBakeLowerBody", "uBakeEyes", "uBakeHair",
            "uBakeLeftArm", "uBakeLeftLeg", "uBakeAux1", "uActiveBakeIndex",
            // PBR
            "uNormalMap", "uMetallicRoughnessMap", "uMetallic", "uRoughness",
            // EEP
            "uSunDirection", "uSunColor", "uAmbientColor", "uFogColor", "uFogDensity",
            // Camera
            "uCameraPos"
        )
        
        uniforms.forEach { name ->
            val location = GLES32.glGetUniformLocation(shaderProgram, name)
            if (location >= 0) {
                uniformLocations[name] = location
            }
        }
        
        Log.d(TAG, "Got ${uniformLocations.size} uniform locations")
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        if (shaderProgram != 0) {
            GLES32.glDeleteProgram(shaderProgram)
            shaderProgram = 0
        }
        isInitialized = false
    }
    
    /**
     * Bind Bakes on Mesh textures for the avatar
     */
    private fun bindBOMTextures(avatarId: UUID) {
        try {
            // Get BOM textures from the Bakes on Mesh manager
            val bomTextures = bomManager.getBakedTextures(avatarId)
            
            // Bind texture units for head, upper body, lower body, etc.
            for ((i, texture) in bomTextures.withIndex()) {
                if (texture != null && texture.textureId != 0) {
                    GLES32.glActiveTexture(GLES32.GL_TEXTURE0 + i)
                    GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture.textureId)
                    
                    // Set texture uniforms
                    val uniformName = when (i) {
                        0 -> "uHeadTexture"
                        1 -> "uUpperBodyTexture"
                        2 -> "uLowerBodyTexture"
                        3 -> "uEyesTexture"
                        4 -> "uHairTexture"
                        5 -> "uSkirtTexture"
                        else -> "uTexture$i"
                    }
                    GLES32.glUniform1i(uniformLocations[uniformName] ?: -1, i)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error binding BOM textures", e)
        }
    }
    
    /**
     * Draw the avatar mesh with proper skinning and materials
     */
    private fun drawAvatarMesh(avatarId: UUID) {
        try {
            // Get avatar mesh data
            val avatarMesh = animeshManager.getAvatarMesh(avatarId)
            if (avatarMesh == null) {
                Log.w(TAG, "No avatar mesh found for ID: $avatarId")
                return
            }
            
            // Bind vertex array
            if (avatarMesh.vaoId != 0) {
                GLES32.glBindVertexArray(avatarMesh.vaoId)
            } else {
                // Legacy vertex buffer binding
                bindLegacyVertexBuffers(avatarMesh)
            }
            
            // Set bone transforms for skinning
            val boneTransforms = animeshManager.getBoneTransforms(avatarId)
            if (boneTransforms != null) {
                GLES32.glUniformMatrix4fv(
                    uniformLocations["uBoneTransforms"] ?: -1,
                    boneTransforms.size / 16,
                    false,
                    boneTransforms,
                    0
                )
            }
            
            // Draw mesh elements
            if (avatarMesh.indexBuffer != 0) {
                GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, avatarMesh.indexBuffer)
                GLES32.glDrawElements(
                    GLES32.GL_TRIANGLES,
                    avatarMesh.indexCount,
                    GLES32.GL_UNSIGNED_INT,
                    0
                )
            } else {
                GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, avatarMesh.vertexCount)
            }
            
            // Unbind
            GLES32.glBindVertexArray(0)
            GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing avatar mesh", e)
        }
    }
    
    /**
     * Bind legacy vertex buffers for older mesh formats
     */
    private fun bindLegacyVertexBuffers(mesh: Any) {
        // Stub for legacy vertex buffer binding
        Log.d(TAG, "Using legacy vertex buffer binding")
    }
}
