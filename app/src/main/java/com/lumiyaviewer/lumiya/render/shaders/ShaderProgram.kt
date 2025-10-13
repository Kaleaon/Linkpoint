package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.Debug

abstract class ShaderProgram(
    private val vertexShader: Shader,
    private val fragmentShader: Shader
) {
    protected var handle: Int = 0
        private set

    @Throws(ShaderCompileException::class)
    fun Compile(preprocessor: ShaderPreprocessor): Int {
        vertexShader.Compile(preprocessor)
        fragmentShader.Compile(preprocessor)
        
        Debug.Printf("Shaders: Linking...")
        
        handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(handle, vertexShader.handle)
        GLES20.glAttachShader(handle, fragmentShader.handle)
        GLES20.glLinkProgram(handle)
        
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, linkStatus, 0)
        
        if (linkStatus[0] != GLES20.GL_TRUE) {
            val errorLog = GLES20.glGetProgramInfoLog(handle)
            throw ShaderCompileException("Shader link error: '$errorLog'")
        }
        
        Debug.Printf("Shaders: Binding variables...")
        bindVariables()
        Debug.Printf("Shaders: Compiled, handle %d", handle)
        
        return handle
    }

    protected abstract fun bindVariables()

    fun getHandle(): Int = handle
}
