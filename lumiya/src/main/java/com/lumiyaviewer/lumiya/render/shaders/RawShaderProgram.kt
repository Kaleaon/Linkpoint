package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class RawShaderProgram(z: Boolean) : ShaderProgram(
    if (z) Shader.ExtTextureVertexShader else Shader.RawVertexShader,
    if (z) Shader.ExtTextureFragmentShader else Shader.RawFragmentShader,
) {
    var textureSampler: Int = 0
    var uMVPMatrix: Int = 0
    var vPosition: Int = 0
    var vTexCoord: Int = 0
    var vTextureTransformMatrix: Int = 0

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        textureSampler = GLES20.glGetUniformLocation(handle, "vTexture")
        vTextureTransformMatrix = GLES20.glGetUniformLocation(handle, "vTextureTransformMatrix")
    }
}