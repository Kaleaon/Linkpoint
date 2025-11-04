// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;
import com.lumiyaviewer.lumiya.render.RenderContext;

public class BasicPrimProgram extends ShaderProgram
{
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
    
    public BasicPrimProgram(final Shader shader, final Shader shader2) {
        super(shader, shader2);
    }
    
    public void SetupLighting(final RenderContext renderContext, final WindlightPreset windlightPreset) {
        if (windlightPreset != null) {
            GLES20.glUniform3f(this.LightDiffuseDir, windlightPreset.lightnorm[0], windlightPreset.lightnorm[2], -windlightPreset.lightnorm[1]);
            if (Math.abs(windlightPreset.lightnorm[1]) > 0.1f) {
                final int lightDiffuseColor = this.LightDiffuseColor;
                float[] array;
                if (renderContext.underWater) {
                    array = windlightPreset.sunlightBelowWater;
                }
                else {
                    array = windlightPreset.sunlight_color;
                }
                GLES20.glUniform3fv(lightDiffuseColor, 1, array, 0);
            }
            else {
                GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f);
            }
            final int lightAmbientColor = this.LightAmbientColor;
            float[] array2;
            if (renderContext.underWater) {
                array2 = windlightPreset.ambientBelowWater;
            }
            else {
                array2 = windlightPreset.ambient;
            }
            GLES20.glUniform3fv(lightAmbientColor, 1, array2, 0);
        }
        else {
            GLES20.glUniform3f(this.LightDiffuseDir, 0.0f, 1.0f, 0.0f);
            GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f);
            GLES20.glUniform3f(this.LightAmbientColor, 1.0f, 1.0f, 1.0f);
        }
    }
    
    @Override
    protected void bindVariables() {
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
    
    public void setTextureEnabled(final boolean b) {
        final int useTexture = this.useTexture;
        int n;
        if (b) {
            n = 1;
        }
        else {
            n = 0;
        }
        GLES20.glUniform1i(useTexture, n);
    }
}
