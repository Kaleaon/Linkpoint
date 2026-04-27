package com.linkpoint.render.shaders

import android.opengl.GLES20

class RiggedMeshProgram : PrimProgram() {
    public Int uBindShapeMatrix
    public Int uJointVectors
    public Int vJoint
    public Int vWeight

    public RiggedMeshProgram(Boolean z) {
        super(Shader.RiggedMeshVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader)
    }

     protected fun bindVariables() {
        super.bindVariables()
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix")
        this.uJointVectors = GLES20.glGetUniformLocation(this.handle, "uJointVectors")
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight")
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint")
    }
}
