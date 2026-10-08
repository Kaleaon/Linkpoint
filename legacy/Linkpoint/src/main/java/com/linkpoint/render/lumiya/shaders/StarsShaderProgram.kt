package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Simple star-field renderer (point sprites).
 *
 * Design lineage: Lumiya `StarsProgram.java`, modernised.
 */
class StarsShaderProgram : BaseShaderProgram() {

    private var uModelMatrix = -1
    private var uStarColor = -1

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

        void main() {
            mat4 viewNoTranslate = uView;
            viewNoTranslate[3] = vec4(0.0, 0.0, 0.0, 1.0);
            gl_Position = uProjection * viewNoTranslate * uModelMatrix * vec4(aPosition, 1.0);
            gl_Position.z = gl_Position.w;  // push to far plane
            gl_PointSize = 1.5;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        uniform vec4 uStarColor;

        out vec4 fragColor;

        void main() {
            fragColor = uStarColor;
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uStarColor = loc("uStarColor")
        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setStarColor(r: Float, g: Float, b: Float, a: Float) = GLES32.glUniform4f(uStarColor, r, g, b, a)
}
