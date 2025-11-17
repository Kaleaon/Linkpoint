package com.linkpoint.render.shaders

import android.opengl.GLES20

class QuadProgram : ShaderProgram {
    Int sTexture
    Int uColor
    Int uColorize
    Int uPostTranslate
    Int uPreTranslate
    Int uScale
    Int vPosition
    Int vTexCoord

    constructor() {
        super(Shader.QuadVertexShader, Shader.QuadFragmentShader)
    }

    protected fun bindVariables(): Unit {
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
