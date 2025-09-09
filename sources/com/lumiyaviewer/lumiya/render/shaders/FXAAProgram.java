package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class FXAAProgram extends ShaderProgram {
    public int noAAtextureSampler;
    public int texcoordOffset;
    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;

    public FXAAProgram() {
        super(Shader.FXAAVertexShader, Shader.FXAAFragmentShader);
    }

    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    /* access modifiers changed from: protected */
    public void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "textureSampler");
        this.noAAtextureSampler = GLES20.glGetUniformLocation(this.handle, "noAAtextureSampler");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.texcoordOffset = GLES20.glGetUniformLocation(this.handle, "texcoordOffset");
    }

    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }
}
