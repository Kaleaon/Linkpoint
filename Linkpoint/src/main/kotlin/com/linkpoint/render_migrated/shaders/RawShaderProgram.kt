package com.linkpoint.render.shaders

import android.opengl.GLES20

class RawShaderProgram : ShaderProgram() {
    public Int textureSampler
    public Int uMVPMatrix
    public Int vPosition
    public Int vTexCoord
    public Int vTextureTransformMatrix

    public RawShaderProgram(Boolean z) {
        super(z ? Shader.ExtTextureVertexShader : Shader.RawVertexShader, z ? Shader.ExtTextureFragmentShader : Shader.RawFragmentShader)
    }

    protected Unit bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "vTexture")
        this.vTextureTransformMatrix = GLES20.glGetUniformLocation(this.handle, "vTextureTransformMatrix")
    }
}
