package com.linkpoint.render.shaders

import android.opengl.GLES20
import com.linkpoint.render.RenderContext

class StarsProgram : ShaderProgram() {
    public Int uMVPMatrix
    public Int uStarColor
    public Int vPosition

    public StarsProgram() {
        super(Shader.StarsVertexShader, Shader.StarsFragmentShader)
    }

    public Unit ApplyWindlight(RenderContext renderContext) {
        GLES20.glUniform4f(this.uStarColor, 1.0f, 1.0f, 1.0f, renderContext.windlightPreset.star_brightness)
    }

    protected Unit bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.uStarColor = GLES20.glGetUniformLocation(this.handle, "uStarColor")
    }
}
