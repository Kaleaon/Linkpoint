package com.lumiyaviewer.lumiya.modern.graphics;
import java.util.*;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Modern OpenGL ES 3.0+ rendering pipeline
 * Implements PBR-style rendering for capable mobile devices
 */
public class ModernRenderPipeline {
    private static final String TAG = "ModernRenderPipeline";
    
    private boolean isES3Available = false;
    private int pbrShaderProgram = -1;
    private int legacyShaderProgram = -1;
    
    // Uniform locations for PBR shader
    private int uMVPMatrix;
    private int uModelMatrix; 
    private int uNormalMatrix;
    private int uCameraPos;
    private int uAlbedoTexture;
    private int uNormalTexture;
    private int uMetallicRoughnessTexture;
    
    // Lighting uniforms
    private int uDirectionalLight;
    private int uPointLights;
    private int uNumPointLights;
    
    private final float[] mvpMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    
    public boolean initialize() {
        // Check OpenGL ES version
        String version = GLES30.glGetString(GLES30.GL_VERSION);
        Log.i(TAG, "OpenGL ES version: " + version);
        
        isES3Available = version != null && (version.contains("OpenGL ES 3.") || version.contains("OpenGL ES 3."));
        
        if (isES3Available) {
            Log.i(TAG, "OpenGL ES 3.0+ detected, enabling modern rendering features");
            return initializeModernPipeline();
        } else {
            Log.i(TAG, "Using legacy OpenGL ES 2.0 rendering");
            return initializeLegacyPipeline();
        }
    }
    
    private boolean initializeModernPipeline() {
        // Create modern PBR shader program
        String vertexShader = getModernVertexShader();
        String fragmentShader = getModernFragmentShader();
        
        pbrShaderProgram = createShaderProgram(vertexShader, fragmentShader);
        if (pbrShaderProgram == -1) {
            Log.e(TAG, "Failed to create PBR shader program");
            return false;
        }
        
        // Get uniform locations
        GLES30.glUseProgram(pbrShaderProgram);
        uMVPMatrix = GLES30.glGetUniformLocation(pbrShaderProgram, "u_MVPMatrix");
        uModelMatrix = GLES30.glGetUniformLocation(pbrShaderProgram, "u_ModelMatrix");
        uNormalMatrix = GLES30.glGetUniformLocation(pbrShaderProgram, "u_NormalMatrix");
        uCameraPos = GLES30.glGetUniformLocation(pbrShaderProgram, "u_CameraPos");
        uAlbedoTexture = GLES30.glGetUniformLocation(pbrShaderProgram, "u_AlbedoTexture");
        uNormalTexture = GLES30.glGetUniformLocation(pbrShaderProgram, "u_NormalTexture");
        uMetallicRoughnessTexture = GLES30.glGetUniformLocation(pbrShaderProgram, "u_MetallicRoughnessTexture");
        
        uDirectionalLight = GLES30.glGetUniformLocation(pbrShaderProgram, "u_DirectionalLight");
        uPointLights = GLES30.glGetUniformLocation(pbrShaderProgram, "u_PointLights");
        uNumPointLights = GLES30.glGetUniformLocation(pbrShaderProgram, "u_NumPointLights");
        
        Log.i(TAG, "Modern PBR pipeline initialized successfully");
        return true;
    }
    
    private boolean initializeLegacyPipeline() {
        // Create legacy shader program for OpenGL ES 2.0
        String vertexShader = getLegacyVertexShader();
        String fragmentShader = getLegacyFragmentShader();
        
        legacyShaderProgram = createShaderProgram(vertexShader, fragmentShader);
        if (legacyShaderProgram == -1) {
            Log.e(TAG, "Failed to create legacy shader program");
            return false;
        }
        
        Log.i(TAG, "Legacy rendering pipeline initialized");
        return true;
    }
    
    public void renderFrame(RenderParams params) {
        if (isES3Available && pbrShaderProgram != -1) {
            renderModernFrame(params);
        } else if (legacyShaderProgram != -1) {
            renderLegacyFrame(params);
        }
    }
    
    private void renderModernFrame(RenderParams params) {
        GLES30.glUseProgram(pbrShaderProgram);
        
        // Set up matrices
        Matrix.multiplyMM(mvpMatrix, 0, params.viewMatrix, 0, params.modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, params.projectionMatrix, 0, mvpMatrix, 0);
        
        // Upload uniforms
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES30.glUniformMatrix4fv(uModelMatrix, 1, false, params.modelMatrix, 0);
        GLES30.glUniform3fv(uCameraPos, 1, params.cameraPosition, 0);
        
        // Bind textures
        if (params.albedoTexture > 0) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, params.albedoTexture);
            GLES30.glUniform1i(uAlbedoTexture, 0);
        }
        
        if (params.normalTexture > 0) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, params.normalTexture);
            GLES30.glUniform1i(uNormalTexture, 1);
        }
        
        if (params.metallicRoughnessTexture > 0) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, params.metallicRoughnessTexture);
            GLES30.glUniform1i(uMetallicRoughnessTexture, 2);
        }
        
        // Set lighting parameters
        GLES30.glUniform3fv(uDirectionalLight, 4, params.directionalLight, 0);
        GLES30.glUniform1i(uNumPointLights, Math.min(params.numPointLights, 4)); // Limit to 4 point lights
        
        // Draw geometry
        if (params.vertexBuffer > 0) {
            // TODO: Implement actual geometry rendering
            Log.d(TAG, "Modern rendering: drawing geometry");
        }
        
        checkGLError("renderModernFrame");
    }
    
    private void renderLegacyFrame(RenderParams params) {
        GLES30.glUseProgram(legacyShaderProgram);
        
        // Basic legacy rendering
        Log.d(TAG, "Legacy rendering: basic draw call");
        
        checkGLError("renderLegacyFrame");
    }
    
    private int createShaderProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return -1;
        }
        
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return -1;
        }
        
        int program = GLES30.glCreateProgram();
        if (program == 0) {
            Log.e(TAG, "Error creating shader program");
            return -1;
        }
        
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES30.GL_TRUE) {
            Log.e(TAG, "Error linking program: " + GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            return -1;
        }
        
        // Clean up shaders
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        if (shader == 0) {
            Log.e(TAG, "Error creating shader");
            return 0;
        }
        
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }
    
    private void checkGLError(String operation) {
        int error = GLES30.glGetError();
        if (error != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error in " + operation + ": " + error);
        }
    }
    
    // Modern PBR shaders
    private String getModernVertexShader() {
        return "#version 300 es\n" +
               "layout(location = 0) in vec3 a_Position;\n" +
               "layout(location = 1) in vec3 a_Normal;\n" +
               "layout(location = 2) in vec2 a_TexCoord;\n" +
               "layout(location = 3) in vec3 a_Tangent;\n" +
               "\n" +
               "uniform mat4 u_MVPMatrix;\n" +
               "uniform mat4 u_ModelMatrix;\n" +
               "uniform mat3 u_NormalMatrix;\n" +
               "\n" +
               "out vec3 v_WorldPos;\n" +
               "out vec3 v_Normal;\n" +
               "out vec2 v_TexCoord;\n" +
               "out vec3 v_Tangent;\n" +
               "out vec3 v_Bitangent;\n" +
               "\n" +
               "void main() {\n" +
               "    v_WorldPos = (u_ModelMatrix * vec4(a_Position, 1.0)).xyz;\n" +
               "    v_Normal = normalize(u_NormalMatrix * a_Normal);\n" +
               "    v_TexCoord = a_TexCoord;\n" +
               "    v_Tangent = normalize(u_NormalMatrix * a_Tangent);\n" +
               "    v_Bitangent = cross(v_Normal, v_Tangent);\n" +
               "    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);\n" +
               "}";
    }
    
    private String getModernFragmentShader() {
        return "#version 300 es\n" +
               "precision mediump float;\n" +
               "\n" +
               "in vec3 v_WorldPos;\n" +
               "in vec3 v_Normal;\n" +
               "in vec2 v_TexCoord;\n" +
               "in vec3 v_Tangent;\n" +
               "in vec3 v_Bitangent;\n" +
               "\n" +
               "uniform vec3 u_CameraPos;\n" +
               "uniform sampler2D u_AlbedoTexture;\n" +
               "uniform sampler2D u_NormalTexture;\n" +
               "uniform sampler2D u_MetallicRoughnessTexture;\n" +
               "\n" +
               "uniform vec4 u_DirectionalLight[4]; // direction.xyz, intensity.w\n" +
               "uniform int u_NumPointLights;\n" +
               "\n" +
               "out vec4 fragColor;\n" +
               "\n" +
               "vec3 calculatePBR(vec3 albedo, float metallic, float roughness, vec3 normal, vec3 viewDir, vec3 lightDir, vec3 lightColor) {\n" +
               "    // Simplified PBR calculation for mobile\n" +
               "    float NdotL = max(dot(normal, lightDir), 0.0);\n" +
               "    vec3 diffuse = albedo * lightColor * NdotL;\n" +
               "    return diffuse; // Simplified for now\n" +
               "}\n" +
               "\n" +
               "void main() {\n" +
               "    vec3 albedo = texture(u_AlbedoTexture, v_TexCoord).rgb;\n" +
               "    vec3 normalMap = texture(u_NormalTexture, v_TexCoord).rgb * 2.0 - 1.0;\n" +
               "    vec2 metallicRoughness = texture(u_MetallicRoughnessTexture, v_TexCoord).bg;\n" +
               "    \n" +
               "    // Transform normal from tangent space to world space\n" +
               "    mat3 TBN = mat3(v_Tangent, v_Bitangent, v_Normal);\n" +
               "    vec3 normal = normalize(TBN * normalMap);\n" +
               "    \n" +
               "    vec3 viewDir = normalize(u_CameraPos - v_WorldPos);\n" +
               "    \n" +
               "    vec3 color = vec3(0.0);\n" +
               "    \n" +
               "    // Directional light\n" +
               "    vec3 lightDir = normalize(-u_DirectionalLight[0].xyz);\n" +
               "    vec3 lightColor = vec3(u_DirectionalLight[0].w);\n" +
               "    color += calculatePBR(albedo, metallicRoughness.x, metallicRoughness.y, normal, viewDir, lightDir, lightColor);\n" +
               "    \n" +
               "    // Ambient light\n" +
               "    color += albedo * 0.1;\n" +
               "    \n" +
               "    fragColor = vec4(color, 1.0);\n" +
               "}";
    }
    
    // Legacy shaders for OpenGL ES 2.0
    private String getLegacyVertexShader() {
        return "attribute vec3 a_Position;\n" +
               "attribute vec2 a_TexCoord;\n" +
               "uniform mat4 u_MVPMatrix;\n" +
               "varying vec2 v_TexCoord;\n" +
               "void main() {\n" +
               "    v_TexCoord = a_TexCoord;\n" +
               "    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);\n" +
               "}";
    }
    
    private String getLegacyFragmentShader() {
        return "precision mediump float;\n" +
               "varying vec2 v_TexCoord;\n" +
               "uniform sampler2D u_Texture;\n" +
               "void main() {\n" +
               "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
               "}";
    }
    
    public boolean isModernPipelineAvailable() {
        return isES3Available;
    }
    
    public void cleanup() {
        if (pbrShaderProgram != -1) {
            GLES30.glDeleteProgram(pbrShaderProgram);
        }
        if (legacyShaderProgram != -1) {
            GLES30.glDeleteProgram(legacyShaderProgram);
        }
    }
    
    /**
     * Rendering parameters container
     */
    public static class RenderParams {
        public float[] modelMatrix = new float[16];
        public float[] viewMatrix = new float[16];
        public float[] projectionMatrix = new float[16];
        public float[] cameraPosition = new float[3];
        
        // Texture handles
        public int albedoTexture = 0;
        public int normalTexture = 0;
        public int metallicRoughnessTexture = 0;
        
        // Lighting
        public float[] directionalLight = new float[16]; // 4 lights * 4 components
        public int numPointLights = 0;
        
        // Geometry
        public int vertexBuffer = 0;
        public int indexBuffer = 0;
        public int vertexCount = 0;
    }
}