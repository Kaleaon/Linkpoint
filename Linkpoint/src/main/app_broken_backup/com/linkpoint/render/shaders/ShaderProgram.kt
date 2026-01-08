package com.linkpoint.render.shaders

import android.opengl.GLES20
import com.linkpoint.Debug

abstract class ShaderProgram {
    private Shader fragmentShader
    protected int handle
    private Shader vertexShader

    ShaderProgram(Shader shader, Shader shader2) {
        this.vertexShader = shader
        this.fragmentShader = shader2
    }

    int Compile(ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
        this.vertexShader.Compile(shaderPreprocessor)
        this.fragmentShader.Compile(shaderPreprocessor)
        Debug.Printf("Shaders: Linking...", Array<Object>(0))
        this.handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(this.handle, this.vertexShader.getHandle())
        GLES20.glAttachShader(this.handle, this.fragmentShader.getHandle())
        GLES20.glLinkProgram(this.handle)
        int[] iArr = IntArray(1)
        GLES20.glGetProgramiv(this.handle, 35714, iArr, 0)
        if (iArr[0] != 1) {
            throw ShaderCompileException(String.format("Shader link error: '%s'", new Array<Any>{GLES20.glGetProgramInfoLog(this.handle)}))
        }
        Debug.Printf("Shaders: Binding variables...", Array<Object>(0))
        bindVariables()
        Debug.Printf("Shaders: Compiled, handle %d", Integer.valueOf(this.handle))
        return this.handle
    }

    protected abstract fun bindVariables(): Unit

    fun getHandle(): Int {
        return this.handle
    }
}
