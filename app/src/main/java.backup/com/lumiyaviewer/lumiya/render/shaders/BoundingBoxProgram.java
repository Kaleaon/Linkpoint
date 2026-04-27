package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class BoundingBoxProgram extends ShaderProgram {
    public int uMVPMatrix;
    public int uObjCoordScale;
    public int uObjWorldMatrix;
    public int vPosition;

    public BoundingBoxProgram() {
        super(Shader.BoundingBoxVertexShader, Shader.BoundingBoxFragmentShader);
    }

    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix");
        this.uObjCoordScale = GLES20.glGetUniformLocation(this.handle, "uObjCoordScale");
    }
}
