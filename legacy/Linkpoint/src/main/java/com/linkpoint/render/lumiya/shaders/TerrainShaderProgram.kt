package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Terrain shader with 4-layer texture splatting.
 *
 * Design lineage: Lumiya terrain rendering in `TerrainPatchGeometry.java`,
 * modernised with explicit splatting, normal-based lighting, and fog.
 *
 * Each terrain patch is 16×16 metres; the heightmap provides Z values.
 * Four detail textures are blended based on altitude thresholds (SL standard:
 * grass → dirt → rock → snow).
 */
class TerrainShaderProgram : BaseShaderProgram() {

    private var uModelMatrix = -1
    private var uTexScale = -1
    private var uHeightThresholds = -1
    private var uLightDir = -1
    private var uLightDiffuse = -1
    private var uLightAmbient = -1
    private var uTexture0 = -1
    private var uTexture1 = -1
    private var uTexture2 = -1
    private var uTexture3 = -1

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
        uniform float uTexScale;

        out vec3 vWorldPos;
        out vec3 vNormal;
        out vec2 vTexCoord;
        out float vHeight;
        out float vFogFactor;

        void main() {
            vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
            vWorldPos = worldPos.xyz;
            vNormal = normalize(mat3(uModelMatrix) * aNormal);
            vTexCoord = aTexCoord * uTexScale;
            vHeight = worldPos.z;

            float dist = length(uCameraPos.xyz - worldPos.xyz);
            vFogFactor = clamp(dist / 256.0, 0.0, 1.0);

            gl_Position = uProjection * uView * worldPos;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec3 vWorldPos;
        in vec3 vNormal;
        in vec2 vTexCoord;
        in float vHeight;
        in float vFogFactor;

        uniform sampler2D uTexture0;  // grass / low
        uniform sampler2D uTexture1;  // dirt / mid-low
        uniform sampler2D uTexture2;  // rock / mid-high
        uniform sampler2D uTexture3;  // snow / high
        uniform vec4 uHeightThresholds; // (t0, t1, t2, t3)
        uniform vec3 uLightDir;
        uniform vec3 uLightDiffuse;
        uniform vec3 uLightAmbient;

        out vec4 fragColor;

        void main() {
            // Altitude-based blend weights
            float h = vHeight;
            float w0 = clamp(1.0 - smoothstep(uHeightThresholds.x, uHeightThresholds.y, h), 0.0, 1.0);
            float w1 = clamp(smoothstep(uHeightThresholds.x, uHeightThresholds.y, h) -
                             smoothstep(uHeightThresholds.y, uHeightThresholds.z, h), 0.0, 1.0);
            float w2 = clamp(smoothstep(uHeightThresholds.y, uHeightThresholds.z, h) -
                             smoothstep(uHeightThresholds.z, uHeightThresholds.w, h), 0.0, 1.0);
            float w3 = clamp(smoothstep(uHeightThresholds.z, uHeightThresholds.w, h), 0.0, 1.0);

            // Normalise
            float total = w0 + w1 + w2 + w3;
            if (total > 0.0) { w0 /= total; w1 /= total; w2 /= total; w3 /= total; }
            else { w0 = 1.0; }

            vec4 tex = w0 * texture(uTexture0, vTexCoord)
                     + w1 * texture(uTexture1, vTexCoord)
                     + w2 * texture(uTexture2, vTexCoord)
                     + w3 * texture(uTexture3, vTexCoord);

            // Lighting
            float NdotL = max(dot(vNormal, normalize(uLightDir)), 0.0);
            vec3 lit = tex.rgb * (uLightAmbient + uLightDiffuse * NdotL);

            // Fog
            vec3 fogColor = vec3(0.24, 0.44, 0.76);
            lit = mix(lit, fogColor, vFogFactor * vFogFactor);

            fragColor = vec4(lit, 1.0);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uTexScale = loc("uTexScale")
        uHeightThresholds = loc("uHeightThresholds")
        uLightDir = loc("uLightDir")
        uLightDiffuse = loc("uLightDiffuse")
        uLightAmbient = loc("uLightAmbient")
        uTexture0 = loc("uTexture0")
        uTexture1 = loc("uTexture1")
        uTexture2 = loc("uTexture2")
        uTexture3 = loc("uTexture3")

        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setTexScale(s: Float) = GLES32.glUniform1f(uTexScale, s)
    fun setHeightThresholds(t0: Float, t1: Float, t2: Float, t3: Float) =
        GLES32.glUniform4f(uHeightThresholds, t0, t1, t2, t3)
    fun setTextureSamplers(u0: Int, u1: Int, u2: Int, u3: Int) {
        GLES32.glUniform1i(uTexture0, u0)
        GLES32.glUniform1i(uTexture1, u1)
        GLES32.glUniform1i(uTexture2, u2)
        GLES32.glUniform1i(uTexture3, u3)
    }
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
