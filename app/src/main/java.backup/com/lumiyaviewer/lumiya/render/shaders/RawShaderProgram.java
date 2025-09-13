package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class RawShaderProgram extends ShaderProgram {
    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;
    public int vTextureTransformMatrix;

    public RawShaderProgram(boolean z) {
        super(z ? Shader.ExtTextureVertexShader : Shader.RawVertexShader, z ? Shader.ExtTextureFragmentShader : Shader.RawFragmentShader);
    }

    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "vTexture");
        this.vTextureTransformMatrix = GLES20.glGetUniformLocation(this.handle, "vTextureTransformMatrix");
    }
}
