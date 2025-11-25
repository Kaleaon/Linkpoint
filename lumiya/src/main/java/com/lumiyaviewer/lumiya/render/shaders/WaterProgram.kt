package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class WaterProgram : ShaderProgram(Shader.SkyVertexShader, Shader.SkyFragmentShader) {
    var uTime: Int = 0
    var uFrequency: Int = 0
    var uPhase: Int = 0
    var uAmplitude: Int = 0
    var uDirection: Int = 0
    var uMVPMatrix: Int = 0
    var uObjWorldMatrix: Int = 0
    var vColor: Int = 0
    var vPosition: Int = 0
    
    override fun bindVariables() {
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix")
        vColor = GLES20.glGetUniformLocation(handle, "vColor")
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        
        uTime = GLES20.glGetUniformLocation(handle, "uTime")
        uFrequency = GLES20.glGetUniformLocation(handle, "uFrequency")
        uPhase = GLES20.glGetUniformLocation(handle, "uPhase")
        uAmplitude = GLES20.glGetUniformLocation(handle, "uAmplitude")
        uDirection = GLES20.glGetUniformLocation(handle, "uDirection")
    }
}
