package com.linkpoint.render.shaders

import android.opengl.GLES20

class PrimProgram : BasicPrimProgram() {
    public Int uTexMatrix

    public PrimProgram(Shader shader, Shader shader2) {
        super(shader, shader2)
    }

    public PrimProgram(Boolean z) {
        super(Shader.PrimVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader)
    }

    protected Unit bindVariables() {
        super.bindVariables()
        this.uTexMatrix = GLES20.glGetUniformLocation(this.handle, "uTexMatrix")
    }
}
