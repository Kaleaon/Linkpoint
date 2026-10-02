package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Shader program for rendering Second Life primitives (prims) and glTF materials.
 *
 * Design lineage: Lumiya `PrimProgram.java` + `BasicPrimProgram.java`,
 * updated for GLSL ES 3.20 with Blinn-Phong specular & normal map approximation.
 *
 * Supports:
 *  - Per-vertex position, normal, texcoord
 *  - Texture + diffuse color + Blinn-Phong specular color and power
 *  - Normal map sampling (texture unit 1)
 *  - Windlight directional + ambient lighting
 *  - Texture matrix for SL face UV offsets / repeats / rotations
 *  - Shared global UBO (binding 0) for projection/view/camera
 */
class PrimShaderProgram : BaseShaderProgram() {

    // Uniform locations
    private var uModelMatrix = -1
    private var uTexMatrix = -1
    private var uColor = -1
    private var uSpecularColor = -1
    private var uSpecularExponent = -1
    private var uUseTexture = -1
    private var uUseNormalMap = -1
    private var uLightDir = -1
    private var uLightDiffuse = -1
    private var uLightAmbient = -1
    private var uTextureSampler = -1
    private var uNormalMapSampler = -1

    override val vertexSource = """
        #version 320 es
        precision highp float;

        layout(std140, binding = 0) uniform GlobalData {
            mat4 uProjection;
            mat4 uView;
            mat4 _pad_model;
            vec4 uCameraPos;
            vec4 uSunDir;
        };

        layout(location = 0) in vec3 aPosition;
        layout(location = 1) in vec3 aNormal;
        layout(location = 2) in vec2 aTexCoord;

        uniform mat4 uModelMatrix;
        uniform mat4 uTexMatrix;

        out vec3 vWorldPos;
        out vec3 vNormal;
        out vec2 vTexCoord;
        out float vFogFactor;

        void main() {
            vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
            vWorldPos = worldPos.xyz;
            vNormal = normalize(mat3(uModelMatrix) * aNormal);
            vTexCoord = (uTexMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;

            float dist = length(uCameraPos.xyz - worldPos.xyz);
            vFogFactor = clamp(dist / 256.0, 0.0, 1.0);

            gl_Position = uProjection * uView * worldPos;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        layout(std140, binding = 0) uniform GlobalData {
            mat4 uProjection;
            mat4 uView;
            mat4 _pad_model;
            vec4 uCameraPos;
            vec4 uSunDir;
        };

        in vec3 vWorldPos;
        in vec3 vNormal;
        in vec2 vTexCoord;
        in float vFogFactor;

        uniform sampler2D uTexture;
        uniform sampler2D uNormalMap;
        uniform vec4 uColor;
        uniform vec3 uSpecularColor;
        uniform float uSpecularExponent;
        uniform int uUseTexture;
        uniform int uUseNormalMap;
        uniform vec3 uLightDir;
        uniform vec3 uLightDiffuse;
        uniform vec3 uLightAmbient;

        out vec4 fragColor;

        void main() {
            vec4 baseColor;
            if (uUseTexture != 0) {
                baseColor = texture(uTexture, vTexCoord) * uColor;
            } else {
                baseColor = uColor;
            }

            // Discard near-transparent fragments (SL behaviour)
            if (baseColor.a < 0.004) discard;

            vec3 N = normalize(vNormal);
            if (uUseNormalMap != 0) {
                vec3 mapN = texture(uNormalMap, vTexCoord).xyz * 2.0 - 1.0;
                N = normalize(N + mapN * 0.5);
            }

            vec3 L = normalize(uLightDir);
            float NdotL = max(dot(N, L), 0.0);

            // Blinn-Phong specular calculation
            vec3 V = normalize(uCameraPos.xyz - vWorldPos);
            vec3 H = normalize(L + V);
            float NdotH = max(dot(N, H), 0.0);
            float specPower = max(uSpecularExponent, 1.0);
            vec3 specular = uLightDiffuse * uSpecularColor * pow(NdotH, specPower);

            vec3 lit = baseColor.rgb * (uLightAmbient + uLightDiffuse * NdotL) + specular;

            // Fog blend towards sky colour
            vec3 fogColor = vec3(0.24, 0.44, 0.76);
            lit = mix(lit, fogColor, vFogFactor * vFogFactor);

            fragColor = vec4(lit, baseColor.a);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uTexMatrix = loc("uTexMatrix")
        uColor = loc("uColor")
        uSpecularColor = loc("uSpecularColor")
        uSpecularExponent = loc("uSpecularExponent")
        uUseTexture = loc("uUseTexture")
        uUseNormalMap = loc("uUseNormalMap")
        uLightDir = loc("uLightDir")
        uLightDiffuse = loc("uLightDiffuse")
        uLightAmbient = loc("uLightAmbient")
        uTextureSampler = loc("uTexture")
        uNormalMapSampler = loc("uNormalMap")

        // Bind the GlobalData UBO to binding point 0
        val idx = uboIndex("GlobalData")
        if (idx != GLES32.GL_INVALID_INDEX) {
            GLES32.glUniformBlockBinding(handle, idx, 0)
        }
    }

    // ── Uniform setters ──────────────────────────────────────────────────

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setTexMatrix(m: FloatArray)   = GLES32.glUniformMatrix4fv(uTexMatrix, 1, false, m, 0)
    fun setColor(r: Float, g: Float, b: Float, a: Float) = GLES32.glUniform4f(uColor, r, g, b, a)
    fun setSpecularColor(r: Float, g: Float, b: Float) = GLES32.glUniform3f(uSpecularColor, r, g, b)
    fun setSpecularExponent(exp: Float) = GLES32.glUniform1f(uSpecularExponent, exp)
    fun setUseTexture(use: Boolean) = GLES32.glUniform1i(uUseTexture, if (use) 1 else 0)
    fun setUseNormalMap(use: Boolean) = GLES32.glUniform1i(uUseNormalMap, if (use) 1 else 0)
    fun setTextureSampler(unit: Int)  = GLES32.glUniform1i(uTextureSampler, unit)
    fun setNormalMapSampler(unit: Int) = GLES32.glUniform1i(uNormalMapSampler, unit)

    fun setLighting(
        dirX: Float, dirY: Float, dirZ: Float,
        diffR: Float, diffG: Float, diffB: Float,
        ambR: Float, ambG: Float, ambB: Float
    ) {
        GLES32.glUniform3f(uLightDir, dirX, dirY, dirZ)
        GLES32.glUniform3f(uLightDiffuse, diffR, diffG, diffB)
        GLES32.glUniform3f(uLightAmbient, ambR, ambG, ambB)
    }
}
