package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

public class BasicPrimProgram extends ShaderProgram {
    public int LightAmbientColor;
    public int LightDiffuseColor;
    public int LightDiffuseDir;
    public int sTexture;
    public int uMVPMatrix;
    public int uObjCoordScale;
    public int uObjWorldMatrix;
    public int useTexture;
    public int vColor;
    public int vNormal;
    public int vPosition;
    public int vTexCoord;

    public BasicPrimProgram(Shader shader, Shader shader2) {
        super(shader, shader2);
    }

    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    public void SetupLighting(RenderContext renderContext, WindlightPreset windlightPreset) {
        if (windlightPreset != null) {
            GLES20.glUniform3f(this.LightDiffuseDir, windlightPreset.lightnorm[0], windlightPreset.lightnorm[2], -windlightPreset.lightnorm[1]);
            if (Math.abs(windlightPreset.lightnorm[1]) > 0.1f) {
                GLES20.glUniform3fv(this.LightDiffuseColor, 1, renderContext.underWater ? windlightPreset.sunlightBelowWater : windlightPreset.sunlight_color, 0);
            } else {
                GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f);
            }
            GLES20.glUniform3fv(this.LightAmbientColor, 1, renderContext.underWater ? windlightPreset.ambientBelowWater : windlightPreset.ambient, 0);
            return;
        }
        GLES20.glUniform3f(this.LightDiffuseDir, 0.0f, 1.0f, 0.0f);
        GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f);
        GLES20.glUniform3f(this.LightAmbientColor, 1.0f, 1.0f, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.vNormal = GLES20.glGetAttribLocation(this.handle, "vNormal");
        this.vColor = GLES20.glGetUniformLocation(this.handle, "vColor");
        this.sTexture = GLES20.glGetUniformLocation(this.handle, "sTexture");
        this.useTexture = GLES20.glGetUniformLocation(this.handle, "useTexture");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix");
        this.uObjCoordScale = GLES20.glGetUniformLocation(this.handle, "uObjCoordScale");
        this.LightDiffuseDir = GLES20.glGetUniformLocation(this.handle, "LightDiffuseDir");
        this.LightDiffuseColor = GLES20.glGetUniformLocation(this.handle, "LightDiffuseColor");
        this.LightAmbientColor = GLES20.glGetUniformLocation(this.handle, "LightAmbientColor");
    }

    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }

    public void setTextureEnabled(boolean z) {
        GLES20.glUniform1i(this.useTexture, z ? 1 : 0);
    }
}
