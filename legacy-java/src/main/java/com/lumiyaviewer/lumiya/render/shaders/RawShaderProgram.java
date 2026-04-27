package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

/* loaded from: classes.dex */
public class RawShaderProgram extends ShaderProgram {
    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;
    public int vTextureTransformMatrix;

    public RawShaderProgram(boolean z) {
        super(z ? Shader.ExtTextureVertexShader : Shader.RawVertexShader, z ? Shader.ExtTextureFragmentShader : Shader.RawFragmentShader);
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "vTexture");
        this.vTextureTransformMatrix = GLES20.glGetUniformLocation(this.handle, "vTextureTransformMatrix");
    }

    @Override // com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }
}
