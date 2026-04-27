package com.linkpoint.render.shaders
import java.util.*

import android.opengl.GLES20

class AvatarProgram : BasicPrimProgram() {
    public Int uJointMap
    public Int uJointMapLength
    public Int uJointMatrix
    public Int uUseWeight
    public Int vWeight

    public AvatarProgram() {
        super(Shader.AvatarVertexShader, Shader.PrimFragmentShader)
    }

     protected fun bindVariables() {
        super.bindVariables()
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight")
        this.uJointMatrix = GLES20.glGetUniformLocation(this.handle, "uJointMatrix")
        this.uJointMap = GLES20.glGetUniformLocation(this.handle, "uJointMap")
        this.uJointMapLength = GLES20.glGetUniformLocation(this.handle, "uJointMapLength")
        this.uUseWeight = GLES20.glGetUniformLocation(this.handle, "uUseWeight")
    }
}
