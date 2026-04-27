package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Windlight sky dome shader program.
 *
 * Design lineage: Lumiya `SkyProgram.java` + `SkyCloudsProgram.java`,
 * unified into one program with a `uUseClouds` toggle.
 *
 * Renders an icosahedron-based sky dome with:
 *  - Haze horizon blending
 *  - Sun glow
 *  - Cloud cubemap sampling (when available)
 *  - Windlight colour parameterisation
 */
class SkyShaderProgram : BaseShaderProgram() {

    private var uModelMatrix = -1
    private var uSkyColor = -1
    private var uHazeHorizon = -1
    private var uHazeColor = -1
    private var uSunDirection = -1
    private var uSunColor = -1
    private var uUseClouds = -1
    private var uCloudTexture = -1
    private var uCloudColor = -1

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

        uniform mat4 uModelMatrix;

        out vec3 vDirection;
        out float vHorizonFactor;

        void main() {
            vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
            vDirection = normalize(aPosition);
            // How close to the horizon (0 = zenith, 1 = horizon)
            vHorizonFactor = 1.0 - abs(vDirection.z);

            // Remove translation from view matrix so sky moves with camera
            mat4 viewNoTranslate = uView;
            viewNoTranslate[3] = vec4(0.0, 0.0, 0.0, 1.0);
            gl_Position = uProjection * viewNoTranslate * worldPos;
            // Push to far plane
            gl_Position.z = gl_Position.w;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec3 vDirection;
        in float vHorizonFactor;

        uniform vec3 uSkyColor;
        uniform float uHazeHorizon;
        uniform vec3 uHazeColor;
        uniform vec3 uSunDirection;
        uniform vec3 uSunColor;
        uniform int uUseClouds;
        uniform samplerCube uCloudTexture;
        uniform vec3 uCloudColor;

        out vec4 fragColor;

        void main() {
            vec3 dir = normalize(vDirection);

            // Base gradient: sky colour → haze at horizon
            float horizonMix = smoothstep(0.0, uHazeHorizon, vHorizonFactor);
            vec3 color = mix(uSkyColor, uHazeColor, horizonMix);

            // Sun glow
            float sunDot = max(dot(dir, normalize(uSunDirection)), 0.0);
            float sunGlow = pow(sunDot, 128.0) * 1.5;
            float sunDisc = pow(sunDot, 2048.0) * 4.0;
            color += uSunColor * (sunGlow + sunDisc);

            // Cloud overlay
            if (uUseClouds != 0) {
                vec4 cloud = texture(uCloudTexture, dir);
                color = mix(color, uCloudColor * cloud.rgb, cloud.a * 0.6);
            }

            fragColor = vec4(color, 1.0);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uSkyColor = loc("uSkyColor")
        uHazeHorizon = loc("uHazeHorizon")
        uHazeColor = loc("uHazeColor")
        uSunDirection = loc("uSunDirection")
        uSunColor = loc("uSunColor")
        uUseClouds = loc("uUseClouds")
        uCloudTexture = loc("uCloudTexture")
        uCloudColor = loc("uCloudColor")

        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setSkyColor(r: Float, g: Float, b: Float) = GLES32.glUniform3f(uSkyColor, r, g, b)
    fun setHaze(horizon: Float, r: Float, g: Float, b: Float) {
        GLES32.glUniform1f(uHazeHorizon, horizon)
        GLES32.glUniform3f(uHazeColor, r, g, b)
    }
    fun setSunDirection(x: Float, y: Float, z: Float) = GLES32.glUniform3f(uSunDirection, x, y, z)
    fun setSunColor(r: Float, g: Float, b: Float) = GLES32.glUniform3f(uSunColor, r, g, b)
    fun setUseClouds(use: Boolean) = GLES32.glUniform1i(uUseClouds, if (use) 1 else 0)
    fun setCloudTexture(unit: Int) = GLES32.glUniform1i(uCloudTexture, unit)
    fun setCloudColor(r: Float, g: Float, b: Float) = GLES32.glUniform3f(uCloudColor, r, g, b)
}
