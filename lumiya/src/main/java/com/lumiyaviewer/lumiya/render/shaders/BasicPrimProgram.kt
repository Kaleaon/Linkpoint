package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset
import kotlin.math.abs

open class BasicPrimProgram(shader: Shader, shader2: Shader) : ShaderProgram(shader, shader2) {
    var LightAmbientColor: Int = 0
    var LightDiffuseColor: Int = 0
    var LightDiffuseDir: Int = 0
    var sTexture: Int = 0
    var uMVPMatrix: Int = 0
    var uObjCoordScale: Int = 0
    var uObjWorldMatrix: Int = 0
    var useTexture: Int = 0
    var vColor: Int = 0
    var vNormal: Int = 0
    var vPosition: Int = 0
    var vTexCoord: Int = 0
    var uTexMatrix: Int = 0 // Added

    fun SetupLighting(renderContext: RenderContext, windlightPreset: WindlightPreset?) {
        if (windlightPreset != null) {
            GLES20.glUniform3f(LightDiffuseDir, windlightPreset.lightnorm[0], windlightPreset.lightnorm[2], -windlightPreset.lightnorm[1])
            if (abs(windlightPreset.lightnorm[1]) > 0.1f) {
                val diffuse = if (renderContext.underWater) windlightPreset.sunlightBelowWater else windlightPreset.sunlight_color
                GLES20.glUniform3fv(LightDiffuseColor, 1, diffuse, 0)
            } else {
                GLES20.glUniform3f(LightDiffuseColor, 0.0f, 0.0f, 0.0f)
            }
            val ambient = if (renderContext.underWater) windlightPreset.ambientBelowWater else windlightPreset.ambient
            GLES20.glUniform3fv(LightAmbientColor, 1, ambient, 0)
            return
        }
        GLES20.glUniform3f(LightDiffuseDir, 0.0f, 1.0f, 0.0f)
        GLES20.glUniform3f(LightDiffuseColor, 0.0f, 0.0f, 0.0f)
        GLES20.glUniform3f(LightAmbientColor, 1.0f, 1.0f, 1.0f)
    }

    override fun bindVariables() {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        vNormal = GLES20.glGetAttribLocation(handle, "vNormal")
        vColor = GLES20.glGetUniformLocation(handle, "vColor")
        sTexture = GLES20.glGetUniformLocation(handle, "sTexture")
        useTexture = GLES20.glGetUniformLocation(handle, "useTexture")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix")
        uObjCoordScale = GLES20.glGetUniformLocation(handle, "uObjCoordScale")
        LightDiffuseDir = GLES20.glGetUniformLocation(handle, "LightDiffuseDir")
        LightDiffuseColor = GLES20.glGetUniformLocation(handle, "LightDiffuseColor")
        LightAmbientColor = GLES20.glGetUniformLocation(handle, "LightAmbientColor")
        uTexMatrix = GLES20.glGetUniformLocation(handle, "uTexMatrix")
    }

    fun setTextureEnabled(z: Boolean) {
        GLES20.glUniform1i(useTexture, if (z) 1 else 0)
    }
}
