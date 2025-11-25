package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class AvatarProgram : BasicPrimProgram(
    Shader.AvatarVertexShader,
    Shader.PrimFragmentShader,
) {
    var uJointMap: Int = 0
    var uJointMapLength: Int = 0
    var uJointMatrix: Int = 0
    var uUseWeight: Int = 0
    var vWeight: Int = 0

    override fun bindVariables() {
        super.bindVariables()
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight")
        uJointMatrix = GLES20.glGetUniformLocation(handle, "uJointMatrix")
        uJointMap = GLES20.glGetUniformLocation(handle, "uJointMap")
        uJointMapLength = GLES20.glGetUniformLocation(handle, "uJointMapLength")
        uUseWeight = GLES20.glGetUniformLocation(handle, "uUseWeight")
    }
}
