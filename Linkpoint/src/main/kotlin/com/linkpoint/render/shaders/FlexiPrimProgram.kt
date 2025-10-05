package com.linkpoint.render.shaders

import android.opengl.GLES20

class FlexiPrimProgram : PrimProgram() {
    public Int uNumSectionMatrices
    public Int uSectionMatrices

    public FlexiPrimProgram(Boolean z) {
        super(Shader.FlexiVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader)
    }

    protected Unit bindVariables() {
        super.bindVariables()
        this.uSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uSectionMatrices")
        this.uNumSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uNumSectionMatrices")
    }
}
