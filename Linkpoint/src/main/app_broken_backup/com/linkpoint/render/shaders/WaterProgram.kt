package com.linkpoint.render.shaders

import android.opengl.GLES20

class WaterProgram : ShaderProgram {
    Int uAmplitude
    Int uDirection
    Int uFrequency
    Int uMVPMatrix
    Int uObjWorldMatrix
    Int uPhase
    Int uTime
    Int vColor
    Int vPosition

    constructor() {
        super(Shader.WaterVertexShader, Shader.WaterFragmentShader)
    }

    protected fun bindVariables(): Unit {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.vColor = GLES20.glGetUniformLocation(this.handle, "vColor")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix")
        this.uTime = GLES20.glGetUniformLocation(this.handle, "time")
        this.uFrequency = GLES20.glGetUniformLocation(this.handle, "frequency")
        this.uPhase = GLES20.glGetUniformLocation(this.handle, "phase")
        this.uAmplitude = GLES20.glGetUniformLocation(this.handle, "amplitude")
        this.uDirection = GLES20.glGetUniformLocation(this.handle, "direction")
    }
}
