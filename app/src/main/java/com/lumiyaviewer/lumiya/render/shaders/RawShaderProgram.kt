package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

/**
 * Basic shader program for raw texture rendering.
 * Supports both standard and external textures (for camera/video).
 */
class RawShaderProgram(useExternalTexture: Boolean) : ShaderProgram(
    if (useExternalTexture) Shader.ExtTextureVertexShader else Shader.RawVertexShader,
    if (useExternalTexture) Shader.ExtTextureFragmentShader else Shader.RawFragmentShader
) {
    var vPosition: Int = 0
    var vTexCoord: Int = 0
    var uMVPMatrix: Int = 0
    var textureSampler: Int = 0
    var vTextureTransformMatrix: Int = 0

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        textureSampler = GLES20.glGetUniformLocation(handle, "vTexture")
        vTextureTransformMatrix = GLES20.glGetUniformLocation(handle, "vTextureTransformMatrix")
    }
}
