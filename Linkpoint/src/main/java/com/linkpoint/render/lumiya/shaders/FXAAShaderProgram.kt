package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * FXAA (Fast Approximate Anti-Aliasing) post-processing shader.
 *
 * Design lineage: Lumiya `FXAAProgram.java`, modernised to GLSL ES 3.20.
 *
 * Operates on the colour texture rendered into the off-screen FBO and
 * resolves to the default framebuffer.
 */
class FXAAShaderProgram : BaseShaderProgram() {

    private var uTextureSampler = -1
    private var uTexelSize = -1

    override val vertexSource = """
        #version 320 es
        precision highp float;

        layout(location = 0) in vec2 aPosition;
        layout(location = 1) in vec2 aTexCoord;

        out vec2 vTexCoord;

        void main() {
            vTexCoord = aTexCoord;
            gl_Position = vec4(aPosition, 0.0, 1.0);
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec2 vTexCoord;

        uniform sampler2D uTexture;
        uniform vec2 uTexelSize;

        out vec4 fragColor;

        // Simplified FXAA 3.11
        void main() {
            vec3 rgbNW = texture(uTexture, vTexCoord + vec2(-1.0, -1.0) * uTexelSize).rgb;
            vec3 rgbNE = texture(uTexture, vTexCoord + vec2( 1.0, -1.0) * uTexelSize).rgb;
            vec3 rgbSW = texture(uTexture, vTexCoord + vec2(-1.0,  1.0) * uTexelSize).rgb;
            vec3 rgbSE = texture(uTexture, vTexCoord + vec2( 1.0,  1.0) * uTexelSize).rgb;
            vec3 rgbM  = texture(uTexture, vTexCoord).rgb;

            vec3 luma = vec3(0.299, 0.587, 0.114);
            float lumaNW = dot(rgbNW, luma);
            float lumaNE = dot(rgbNE, luma);
            float lumaSW = dot(rgbSW, luma);
            float lumaSE = dot(rgbSE, luma);
            float lumaM  = dot(rgbM, luma);

            float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
            float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
            float lumaRange = lumaMax - lumaMin;

            // Skip anti-aliasing if contrast is low
            if (lumaRange < max(0.0312, lumaMax * 0.125)) {
                fragColor = vec4(rgbM, 1.0);
                return;
            }

            vec2 dir;
            dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
            dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

            float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * 0.25, 1.0/128.0);
            float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
            dir = min(vec2(8.0), max(vec2(-8.0), dir * rcpDirMin)) * uTexelSize;

            vec3 rgbA = 0.5 * (
                texture(uTexture, vTexCoord + dir * (1.0/3.0 - 0.5)).rgb +
                texture(uTexture, vTexCoord + dir * (2.0/3.0 - 0.5)).rgb
            );
            vec3 rgbB = rgbA * 0.5 + 0.25 * (
                texture(uTexture, vTexCoord + dir * -0.5).rgb +
                texture(uTexture, vTexCoord + dir *  0.5).rgb
            );

            float lumaB = dot(rgbB, luma);
            if (lumaB < lumaMin || lumaB > lumaMax) {
                fragColor = vec4(rgbA, 1.0);
            } else {
                fragColor = vec4(rgbB, 1.0);
            }
        }
    """.trimIndent()

    override fun onBind() {
        uTextureSampler = loc("uTexture")
        uTexelSize = loc("uTexelSize")
    }

    fun setTextureSampler(unit: Int) = GLES32.glUniform1i(uTextureSampler, unit)
    fun setTexelSize(x: Float, y: Float) = GLES32.glUniform2f(uTexelSize, x, y)
}
