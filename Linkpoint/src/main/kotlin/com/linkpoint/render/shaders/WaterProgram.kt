package com.linkpoint.render.shaders

import android.opengl.GLES20

class WaterProgram : ShaderProgram() {
    public Int uAmplitude
    public Int uDirection
    public Int uFrequency
    public Int uMVPMatrix
    public Int uObjWorldMatrix
    public Int uPhase
    public Int uTime
    public Int vColor
    public Int vPosition

    public WaterProgram() {
        super(Shader.WaterVertexShader, Shader.WaterFragmentShader)
    }

     protected fun bindVariables() {
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
