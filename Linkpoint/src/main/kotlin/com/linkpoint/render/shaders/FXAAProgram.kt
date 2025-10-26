package com.linkpoint.render.shaders

import android.opengl.GLES20

class FXAAProgram : ShaderProgram() {
    public Int noAAtextureSampler
    public Int texcoordOffset
    public Int textureSampler
    public Int uMVPMatrix
    public Int vPosition
    public Int vTexCoord

    public FXAAProgram() {
        super(Shader.FXAAVertexShader, Shader.FXAAFragmentShader)
    }

     protected fun bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord")
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "textureSampler")
        this.noAAtextureSampler = GLES20.glGetUniformLocation(this.handle, "noAAtextureSampler")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.texcoordOffset = GLES20.glGetUniformLocation(this.handle, "texcoordOffset")
    }
}
