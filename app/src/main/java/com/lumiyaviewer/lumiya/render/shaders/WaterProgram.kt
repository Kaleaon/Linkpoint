package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class WaterProgram : ShaderProgram(Shader.WaterVertexShader, Shader.WaterFragmentShader) {

    var vPosition: Int = 0
    var vColor: Int = 0
    var uMVPMatrix: Int = 0
    var uObjWorldMatrix: Int = 0
    var uTime: Int = 0
    var uFrequency: Int = 0
    var uPhase: Int = 0
    var uAmplitude: Int = 0
    var uDirection: Int = 0

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vColor = GLES20.glGetUniformLocation(handle, "vColor")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix")
        uTime = GLES20.glGetUniformLocation(handle, "time")
        uFrequency = GLES20.glGetUniformLocation(handle, "frequency")
        uPhase = GLES20.glGetUniformLocation(handle, "phase")
        uAmplitude = GLES20.glGetUniformLocation(handle, "amplitude")
        uDirection = GLES20.glGetUniformLocation(handle, "direction")
    }
}
