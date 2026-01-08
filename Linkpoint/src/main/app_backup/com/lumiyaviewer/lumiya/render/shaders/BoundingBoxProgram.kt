package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class BoundingBoxProgram : ShaderProgram(
    Shader.BoundingBoxVertexShader,
    Shader.BoundingBoxFragmentShader,
) {
    var uMVPMatrix: Int = 0
    var uObjCoordScale: Int = 0
    var uObjWorldMatrix: Int = 0
    var vPosition: Int = 0

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix")
        uObjCoordScale = GLES20.glGetUniformLocation(handle, "uObjCoordScale")
    }
}