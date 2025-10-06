package com.linkpoint.render.shaders

import android.opengl.GLES20
import com.linkpoint.render.RenderContext
import com.linkpoint.slproto.windlight.WindlightPreset

class BasicPrimProgram : ShaderProgram() {
    public Int LightAmbientColor
    public Int LightDiffuseColor
    public Int LightDiffuseDir
    public Int sTexture
    public Int uMVPMatrix
    public Int uObjCoordScale
    public Int uObjWorldMatrix
    public Int useTexture
    public Int vColor
    public Int vNormal
    public Int vPosition
    public Int vTexCoord

    public BasicPrimProgram(Shader shader, Shader shader2) {
        super(shader, shader2)
    }

    fun SetupLighting(RenderContext renderContext, WindlightPreset windlightPreset) {
        if (windlightPreset != null) {
            GLES20.glUniform3f(this.LightDiffuseDir, windlightPreset.lightnorm[0], windlightPreset.lightnorm[2], -windlightPreset.lightnorm[1])
            if (Math.abs(windlightPreset.lightnorm[1]) > 0.1f) {
                GLES20.glUniform3fv(this.LightDiffuseColor, 1, renderContext.underWater ? windlightPreset.sunlightBelowWater : windlightPreset.sunlight_color, 0)
            } else {
                GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f)
            }
            GLES20.glUniform3fv(this.LightAmbientColor, 1, renderContext.underWater ? windlightPreset.ambientBelowWater : windlightPreset.ambient, 0)
            return
        }
        GLES20.glUniform3f(this.LightDiffuseDir, 0.0f, 1.0f, 0.0f)
        GLES20.glUniform3f(this.LightDiffuseColor, 0.0f, 0.0f, 0.0f)
        GLES20.glUniform3f(this.LightAmbientColor, 1.0f, 1.0f, 1.0f)
    }

    protected Unit bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition")
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord")
        this.vNormal = GLES20.glGetAttribLocation(this.handle, "vNormal")
        this.vColor = GLES20.glGetUniformLocation(this.handle, "vColor")
        this.sTexture = GLES20.glGetUniformLocation(this.handle, "sTexture")
        this.useTexture = GLES20.glGetUniformLocation(this.handle, "useTexture")
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix")
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix")
        this.uObjCoordScale = GLES20.glGetUniformLocation(this.handle, "uObjCoordScale")
        this.LightDiffuseDir = GLES20.glGetUniformLocation(this.handle, "LightDiffuseDir")
        this.LightDiffuseColor = GLES20.glGetUniformLocation(this.handle, "LightDiffuseColor")
        this.LightAmbientColor = GLES20.glGetUniformLocation(this.handle, "LightAmbientColor")
    }

    fun setTextureEnabled(Boolean z) {
        GLES20.glUniform1i(this.useTexture, z ? 1 : 0)
    }
}
