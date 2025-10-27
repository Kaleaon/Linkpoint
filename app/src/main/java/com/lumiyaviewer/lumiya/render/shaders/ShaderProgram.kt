package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.Debug

/**
 * Base class for GLSL shader programs.
 * Manages compilation, linking, and variable binding for OpenGL ES shaders.
 * 
 * Based on Second Life's LLGLSLShader system.
 */
abstract class ShaderProgram(
    private val vertexShader: Shader,
    private val fragmentShader: Shader
) {
    protected var handle: Int = 0
        private set

    /**
     * Compile and link the shader program.
     * @param shaderPreprocessor Preprocessor for shader source code
     * @return The OpenGL program handle
     * @throws ShaderCompileException if compilation or linking fails
     */
    @Throws(ShaderCompileException::class)
    fun compile(shaderPreprocessor: ShaderPreprocessor): Int {
        // Compile individual shaders
        vertexShader.compile(shaderPreprocessor)
        fragmentShader.compile(shaderPreprocessor)
        
        Debug.printf("Shaders: Linking...", emptyArray())
        
        // Create and link program
        handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(handle, vertexShader.getHandle())
        GLES20.glAttachShader(handle, fragmentShader.getHandle())
        GLES20.glLinkProgram(handle)
        
        // Check link status
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, linkStatus, 0)
        
        if (linkStatus[0] != GLES20.GL_TRUE) {
            val errorLog = GLES20.glGetProgramInfoLog(handle)
            throw ShaderCompileException("Shader link error: '$errorLog'")
        }
        
        Debug.printf("Shaders: Binding variables...", emptyArray())
        bindVariables()
        
        Debug.printf("Shaders: Compiled, handle %d", handle)
        return handle
    }

    /**
     * Bind shader variables (attributes and uniforms).
     * Subclasses must implement this to bind their specific variables.
     */
    protected abstract fun bindVariables()

    /**
     * Get the OpenGL program handle.
     */
    fun getHandle(): Int {
        return handle
    }
}

