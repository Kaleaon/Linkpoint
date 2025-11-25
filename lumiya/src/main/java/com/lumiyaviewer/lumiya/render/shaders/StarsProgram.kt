package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext

class StarsProgram : ShaderProgram(
    Shader.StarsVertexShader,
    Shader.StarsFragmentShader
) {
    var uMVPMatrix: Int = 0
    var uStarColor: Int = 0
    var vPosition: Int = 0

    fun ApplyWindlight(renderContext: RenderContext) {
        renderContext.windlightPreset?.let { preset ->
            GLES20.glUniform4f(uStarColor, 1.0f, 1.0f, 1.0f, preset.star_brightness)
        }
    }

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uStarColor = GLES20.glGetUniformLocation(handle, "uStarColor")
    }
}
