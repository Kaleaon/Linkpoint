package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

class FlexiPrimProgram(z: Boolean) : PrimProgram(
    Shader.FlexiVertexShader,
    if (z) Shader.PrimOpaqueFragmentShader else Shader.PrimFragmentShader
) {
    var uNumSectionMatrices: Int = 0
    var uSectionMatrices: Int = 0

    override fun bindVariables() {
        super.bindVariables()
        uSectionMatrices = GLES20.glGetUniformLocation(handle, "uSectionMatrices")
        uNumSectionMatrices = GLES20.glGetUniformLocation(handle, "uNumSectionMatrices")
    }
}
