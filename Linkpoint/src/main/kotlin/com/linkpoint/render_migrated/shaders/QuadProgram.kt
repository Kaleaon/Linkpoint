package com.linkpoint.render.shaders

import android.opengl.GLES20

class QuadProgram : ShaderProgram() {
    public Int sTexture
    public Int uColor
    public Int uColorize
    public Int uPostTranslate
    public Int uPreTranslate
    public Int uScale
    public Int vPosition
    public Int vTexCoord

    public QuadProgram() {
        super(Shader.QuadVertexShader, Shader.QuadFragmentShader)
    }

    protected Unit bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord")
        this.sTexture = GLES20.glGetUniformLocation(this.handle, "sTexture")
        this.uColor = GLES20.glGetUniformLocation(this.handle, "uColor")
        this.uColorize = GLES20.glGetUniformLocation(this.handle, "uColorize")
        this.uPreTranslate = GLES20.glGetUniformLocation(this.handle, "uPreTranslate")
        this.uScale = GLES20.glGetUniformLocation(this.handle, "uScale")
        this.uPostTranslate = GLES20.glGetUniformLocation(this.handle, "uPostTranslate")
    }
}
