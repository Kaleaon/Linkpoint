package com.linkpoint.render.shaders

import android.opengl.GLES20

open class PrimProgram : BasicPrimProgram {
    var uTexMatrix: Int = 0

    constructor(shader: Shader, shader2: Shader) : super(shader, shader2)

    constructor(z: Boolean) : super(
        Shader.PrimVertexShader,
        if (z) Shader.PrimOpaqueFragmentShader else Shader.PrimFragmentShader,
    )

    override fun bindVariables() {
        super.bindVariables()
        uTexMatrix = GLES20.glGetUniformLocation(handle, "uTexMatrix")
    }
}