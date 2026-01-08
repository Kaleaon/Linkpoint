package com.linkpoint.render.shaders

import android.opengl.GLES20

class FXAAProgram : ShaderProgram(
    Shader.FXAAVertexShader,
    Shader.FXAAFragmentShader,
) {
    var noAAtextureSampler: Int = 0
    var texcoordOffset: Int = 0
    var textureSampler: Int = 0
    var uMVPMatrix: Int = 0
    var vPosition: Int = 0
    var vTexCoord: Int = 0

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        textureSampler = GLES20.glGetUniformLocation(handle, "textureSampler")
        noAAtextureSampler = GLES20.glGetUniformLocation(handle, "noAAtextureSampler")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        texcoordOffset = GLES20.glGetUniformLocation(handle, "texcoordOffset")
    }
}