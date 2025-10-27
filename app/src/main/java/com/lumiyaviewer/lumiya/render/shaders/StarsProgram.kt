package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext

/**
 * Shader program for rendering stars in the sky.
 * Applies Windlight atmospheric effects to star brightness.
 */
class StarsProgram : ShaderProgram(
    Shader.StarsVertexShader,
    Shader.StarsFragmentShader
) {
    var vPosition: Int = 0
    var uMVPMatrix: Int = 0
    var uStarColor: Int = 0

    /**
     * Apply Windlight environmental settings to star rendering.
     */
    fun applyWindlight(renderContext: RenderContext) {
        GLES20.glUniform4f(
            uStarColor,
            1.0f, 1.0f, 1.0f,
            renderContext.windlightPreset.star_brightness
        )
    }

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uStarColor = GLES20.glGetUniformLocation(handle, "uStarColor")
    }
}
