package com.linkpoint.render.shaders

import android.opengl.GLES20
import com.linkpoint.render.RenderContext
import com.linkpoint.slproto.windlight.WindlightPreset

class SkyCloudsProgram : SkyProgram() {
    public Int cloudAdd
    public Int cloudColor
    public Int cloudGamma
    public Int textureSampler

    public SkyCloudsProgram() {
        super(Shader.SkyFragmentShader)
    }

    fun ApplyWindlight(renderContext: RenderContext) {
        super.ApplyWindlight(renderContext)
        val windlightPreset: WindlightPreset = renderContext.windlightPreset
        GLES20.glUniform3f(this.cloudColor, windlightPreset.cloud_color[0], windlightPreset.cloud_color[1], windlightPreset.cloud_color[2])
        GLES20.glUniform1f(this.cloudGamma, windlightPreset.cloud_pos_density1[2])
        GLES20.glUniform1f(this.cloudAdd, windlightPreset.cloud_shadow[0] - 0.5f)
        GLES20.glUniform1i(this.textureSampler, 0)
    }

     protected fun bindVariables() {
        super.bindVariables()
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "textureSampler")
        this.cloudColor = GLES20.glGetUniformLocation(this.handle, "cloudColor")
        this.cloudGamma = GLES20.glGetUniformLocation(this.handle, "cloudGamma")
        this.cloudAdd = GLES20.glGetUniformLocation(this.handle, "cloudAdd")
    }

     public fun hasCloudsTexture(): Boolean {
        return true
    }
}
