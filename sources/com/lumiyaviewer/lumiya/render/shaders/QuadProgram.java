package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class QuadProgram extends ShaderProgram {
    public int sTexture;
    public int uColor;
    public int uColorize;
    public int uPostTranslate;
    public int uPreTranslate;
    public int uScale;
    public int vPosition;
    public int vTexCoord;

    public QuadProgram() {
        super(Shader.QuadVertexShader, Shader.QuadFragmentShader);
    }

    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    /* access modifiers changed from: protected */
    public void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.sTexture = GLES20.glGetUniformLocation(this.handle, "sTexture");
        this.uColor = GLES20.glGetUniformLocation(this.handle, "uColor");
        this.uColorize = GLES20.glGetUniformLocation(this.handle, "uColorize");
        this.uPreTranslate = GLES20.glGetUniformLocation(this.handle, "uPreTranslate");
        this.uScale = GLES20.glGetUniformLocation(this.handle, "uScale");
        this.uPostTranslate = GLES20.glGetUniformLocation(this.handle, "uPostTranslate");
    }

    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }
}
