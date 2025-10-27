package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20

/**
 * Shader program for rendering textured quads.
 * Used for UI elements, HUDs, and 2D overlays.
 */
class QuadProgram : ShaderProgram(
    Shader.QuadVertexShader,
    Shader.QuadFragmentShader
) {
    // Attribute locations
    var vPosition: Int = 0
    var vTexCoord: Int = 0
    
    // Uniform locations
    var sTexture: Int = 0
    var uColor: Int = 0
    var uColorize: Int = 0
    var uPreTranslate: Int = 0
    var uScale: Int = 0
    var uPostTranslate: Int = 0

    override fun bindVariables() {
        // Bind vertex attributes
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition")
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord")
        
        // Bind uniforms
        sTexture = GLES20.glGetUniformLocation(handle, "sTexture")
        uColor = GLES20.glGetUniformLocation(handle, "uColor")
        uColorize = GLES20.glGetUniformLocation(handle, "uColorize")
        uPreTranslate = GLES20.glGetUniformLocation(handle, "uPreTranslate")
        uScale = GLES20.glGetUniformLocation(handle, "uScale")
        uPostTranslate = GLES20.glGetUniformLocation(handle, "uPostTranslate")
    }
}
