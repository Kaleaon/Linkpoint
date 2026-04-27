package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

/* loaded from: classes.dex */
public class BoundingBoxProgram extends ShaderProgram {
    public int uMVPMatrix;
    public int uObjCoordScale;
    public int uObjWorldMatrix;
    public int vPosition;

    public BoundingBoxProgram() {
        super(Shader.BoundingBoxVertexShader, Shader.BoundingBoxFragmentShader);
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix");
        this.uObjCoordScale = GLES20.glGetUniformLocation(this.handle, "uObjCoordScale");
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }
}
