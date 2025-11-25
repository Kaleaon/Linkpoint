package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext

class SkyCloudsProgram : SkyProgram(Shader.SkyFragmentShader) {
    var cloudAdd: Int = 0
    var cloudColor: Int = 0
    var cloudGamma: Int = 0
    var textureSampler: Int = 0

    override fun ApplyWindlight(renderContext: RenderContext) {
        super.ApplyWindlight(renderContext)
        val windlightPreset = renderContext.windlightPreset ?: return
        GLES20.glUniform3f(cloudColor, windlightPreset.cloud_color[0], windlightPreset.cloud_color[1], windlightPreset.cloud_color[2])
        GLES20.glUniform1f(cloudGamma, windlightPreset.cloud_pos_density1[2])
        GLES20.glUniform1f(cloudAdd, windlightPreset.cloud_shadow[0] - 0.5f)
        GLES20.glUniform1i(textureSampler, 0)
    }

    override fun bindVariables() {
        super.bindVariables()
        textureSampler = GLES20.glGetUniformLocation(handle, "textureSampler")
        cloudColor = GLES20.glGetUniformLocation(handle, "cloudColor")
        cloudGamma = GLES20.glGetUniformLocation(handle, "cloudGamma")
        cloudAdd = GLES20.glGetUniformLocation(handle, "cloudAdd")
    }

    override fun hasCloudsTexture(): Boolean {
        return true
    }
}
