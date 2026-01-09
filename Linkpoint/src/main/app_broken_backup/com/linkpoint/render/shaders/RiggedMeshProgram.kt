package com.linkpoint.render.shaders

import android.opengl.GLES20

class RiggedMeshProgram(z: Boolean) : PrimProgram(
    Shader.RiggedMeshVertexShader,
    if (z) Shader.PrimOpaqueFragmentShader else Shader.PrimFragmentShader,
) {
    var uBindShapeMatrix: Int = 0
    var uJointVectors: Int = 0
    var vJoint: Int = 0
    var vWeight: Int = 0

    override fun bindVariables() {
        super.bindVariables()
        uBindShapeMatrix = GLES20.glGetUniformLocation(handle, "uBindShapeMatrix")
        uJointVectors = GLES20.glGetUniformLocation(handle, "uJointVectors")
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight")
        vJoint = GLES20.glGetAttribLocation(handle, "vJoint")
    }
}