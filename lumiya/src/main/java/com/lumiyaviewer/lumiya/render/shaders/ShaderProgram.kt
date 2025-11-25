package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.Debug

abstract class ShaderProgram(
    private val vertexShader: Shader,
    private val fragmentShader: Shader
) {
    protected var handle: Int = 0

    @Throws(ShaderCompileException::class)
    fun Compile(shaderPreprocessor: ShaderPreprocessor): Int {
        vertexShader.Compile(shaderPreprocessor)
        fragmentShader.Compile(shaderPreprocessor)
        Debug.Log("Shaders: Linking...")
        handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(handle, vertexShader.getHandle())
        GLES20.glAttachShader(handle, fragmentShader.getHandle())
        GLES20.glLinkProgram(handle)
        val iArr = IntArray(1)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, iArr, 0)
        if (iArr[0] != 1) {
            throw ShaderCompileException("Shader link error: " + GLES20.glGetProgramInfoLog(handle))
        }
        Debug.Log("Shaders: Binding variables...")
        bindVariables()
        Debug.Printf("Shaders: Compiled, handle %d", arrayOf(handle))
        return handle
    }

    protected abstract fun bindVariables()

    fun getHandle(): Int {
        return handle
    }
}
