package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset

class SkyProgram : ShaderProgram {
    var hazeColor: Int = 0
    var hazeHorizon: Int = 0
    var skyColor: Int = 0
    var uMVPMatrix: Int = 0
    var vPosition: Int = 0

    constructor() : super(Shader.SkyVertexShader, Shader.SkyNoCloudsFragmentShader)

    constructor(shader: Shader) : super(Shader.SkyVertexShader, shader)

    fun ApplyWindlight(renderContext: RenderContext) {
        val windlightPreset = renderContext.windlightPreset
        GLES20.glUniform3f(this.skyColor, 
            ((windlightPreset.blue_horizon[0] + windlightPreset.sunlight_color[0]) + windlightPreset.ambient[0]) * windlightPreset.blue_density[0], 
            ((windlightPreset.blue_horizon[1] + windlightPreset.sunlight_color[1]) + windlightPreset.ambient[1]) * windlightPreset.blue_density[1], 
            ((windlightPreset.blue_horizon[2] + windlightPreset.sunlight_color[2]) + windlightPreset.ambient[2]) * windlightPreset.blue_density[2]
        )
        GLES20.glUniform1f(this.hazeHorizon, windlightPreset.haze_horizon[0])
        GLES20.glUniform3f(this.hazeColor, 
            windlightPreset.haze_density[0] * windlightPreset.ambient[0], 
            windlightPreset.haze_density[0] * windlightPreset.ambient[1], 
            windlightPreset.ambient[2] * windlightPreset.haze_density[0]
        )
    }

    override fun bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.skyColor = GLES20.glGetUniformLocation(this.handle, "skyColor")
        this.hazeHorizon = GLES20.glGetUniformLocation(this.handle, "hazeHorizon")
        this.hazeColor = GLES20.glGetUniformLocation(this.handle, "hazeColor")
    }

    fun hasCloudsTexture(): Boolean {
        return false
    }
}
