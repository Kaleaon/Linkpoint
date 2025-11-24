package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class WaterProgram : ShaderProgram {
    var uAmplitude: Int = 0
    var uDirection: Int = 0
    var uFrequency: Int = 0
    var uMVPMatrix: Int = 0
    var uObjWorldMatrix: Int = 0
    var uPhase: Int = 0
    var uTime: Int = 0
    var vColor: Int = 0
    var vPosition: Int = 0

    constructor() : super(Shader.WaterVertexShader, Shader.WaterFragmentShader)

    override fun bindVariables() {
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
