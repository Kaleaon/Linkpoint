package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset

/**
 * Basic primitive shader program with lighting support.
 * Used for rendering textured objects with Windlight atmospheric lighting.
 */
open class BasicPrimProgram(
    vertexShader: Shader,
    fragmentShader: Shader
) : ShaderProgram(vertexShader, fragmentShader) {
    
    // Vertex attributes
    var vPosition: Int = 0
    var vTexCoord: Int = 0
    var vNormal: Int = 0
    
    // Uniforms
    var vColor: Int = 0
    var sTexture: Int = 0
    var useTexture: Int = 0
    var uMVPMatrix: Int = 0
    var uObjWorldMatrix: Int = 0
    var uObjCoordScale: Int = 0
    
    // Lighting uniforms
    var LightDiffuseDir: Int = 0
    var LightDiffuseColor: Int = 0
    var LightAmbientColor: Int = 0

    /**
     * Setup Windlight atmospheric lighting parameters.
     */
    fun setupLighting(renderContext: RenderContext, windlightPreset: WindlightPreset?) {
        if (windlightPreset != null) {
            // Set light direction (swap Y and Z, negate Z for coordinate system)
            GLES20.glUniform3f(
                LightDiffuseDir,
                windlightPreset.lightnorm[0],
                windlightPreset.lightnorm[2],
                -windlightPreset.lightnorm[1]
            )
            
            // Set diffuse color based on sun angle
            if (Math.abs(windlightPreset.lightnorm[1]) > 0.1f) {
                val diffuseColor = if (renderContext.underWater) {
                    windlightPreset.sunlightBelowWater
                } else {
                    windlightPreset.sunlight_color
                }
                GLES20.glUniform3fv(LightDiffuseColor, 1, diffuseColor, 0)
            } else {
                // No sunlight (sun below horizon)
                GLES20.glUniform3f(LightDiffuseColor, 0.0f, 0.0f, 0.0f)
            }
            
            // Set ambient color
            val ambientColor = if (renderContext.underWater) {
                windlightPreset.ambientBelowWater
            } else {
                windlightPreset.ambient
            }
            GLES20.glUniform3fv(LightAmbientColor, 1, ambientColor, 0)
        } else {
            // Default lighting (no Windlight)
            GLES20.glUniform3f(LightDiffuseDir, 0.0f, 1.0f, 0.0f)
            GLES20.glUniform3f(LightDiffuseColor, 0.0f, 0.0f, 0.0f)
            GLES20.glUniform3f(LightAmbientColor, 1.0f, 1.0f, 1.0f)
        }
    }

    override fun bindVariables() {
        // Bind vertex attributes
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        vNormal = GLES20.glGetAttribLocation(handle, "vNormal")
        
        // Bind uniforms
        vColor = GLES20.glGetUniformLocation(handle, "vColor")
        sTexture = GLES20.glGetUniformLocation(handle, "sTexture")
        useTexture = GLES20.glGetUniformLocation(handle, "useTexture")
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix")
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix")
        uObjCoordScale = GLES20.glGetUniformLocation(handle, "uObjCoordScale")
        
        // Bind lighting uniforms
        LightDiffuseDir = GLES20.glGetUniformLocation(handle, "LightDiffuseDir")
        LightDiffuseColor = GLES20.glGetUniformLocation(handle, "LightDiffuseColor")
        LightAmbientColor = GLES20.glGetUniformLocation(handle, "LightAmbientColor")
    }

    /**
     * Enable or disable texture mapping.
     */
    fun setTextureEnabled(enabled: Boolean) {
        GLES20.glUniform1i(useTexture, if (enabled) 1 else 0)
    }
}

