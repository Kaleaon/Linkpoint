package com.linkpoint.render.shaders

import android.opengl.GLES20

class BoundingBoxProgram : ShaderProgram() {
    public Int uMVPMatrix
    public Int uObjCoordScale
    public Int uObjWorldMatrix
    public Int vPosition

    public BoundingBoxProgram() {
        super(Shader.BoundingBoxVertexShader, Shader.BoundingBoxFragmentShader)
    }

    protected Unit bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix")
        this.uObjCoordScale = GLES20.glGetUniformLocation(this.handle, "uObjCoordScale")
    }
}
