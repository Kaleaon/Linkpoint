package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Camera-aligned billboard particle shader.
 *
 * Design lineage: Lumiya particle rendering, modernised for GL ES 3.20.
 *
 * Each particle is a camera-facing quad with colour/alpha interpolation
 * over its lifetime.
 */
class ParticleShaderProgram : BaseShaderProgram() {

    private var uModelMatrix = -1
    private var uTextureSampler = -1
    private var uUseTexture = -1

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

        layout(location = 0) in vec3 aPosition;   // world-space billboard vertex
        layout(location = 1) in vec2 aTexCoord;
        layout(location = 2) in vec4 aColor;       // per-vertex interpolated colour

        uniform mat4 uModelMatrix;

        out vec2 vTexCoord;
        out vec4 vColor;

        void main() {
            vTexCoord = aTexCoord;
            vColor = aColor;
            gl_Position = uProjection * uView * uModelMatrix * vec4(aPosition, 1.0);
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec2 vTexCoord;
        in vec4 vColor;

        uniform sampler2D uTexture;
        uniform int uUseTexture;

        out vec4 fragColor;

        void main() {
            vec4 texColor;
            if (uUseTexture != 0) {
                texColor = texture(uTexture, vTexCoord);
            } else {
                texColor = vec4(1.0);
            }
            fragColor = texColor * vColor;
            if (fragColor.a < 0.004) discard;
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uTextureSampler = loc("uTexture")
        uUseTexture = loc("uUseTexture")

        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setTextureSampler(unit: Int) = GLES32.glUniform1i(uTextureSampler, unit)
    fun setUseTexture(use: Boolean) = GLES32.glUniform1i(uUseTexture, if (use) 1 else 0)
}
