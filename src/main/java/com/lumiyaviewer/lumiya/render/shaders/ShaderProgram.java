package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;

abstract class ShaderProgram {
    private final Shader fragmentShader;
    protected int handle;
    private final Shader vertexShader;

    ShaderProgram(Shader shader, Shader shader2) {
        this.vertexShader = shader;
        this.fragmentShader = shader2;
    }

    public int Compile(ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
        this.vertexShader.Compile(shaderPreprocessor);
        this.fragmentShader.Compile(shaderPreprocessor);
        Debug.Printf("Shaders: Linking...", new Object[0]);
        this.handle = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.handle, this.vertexShader.getHandle());
        GLES20.glAttachShader(this.handle, this.fragmentShader.getHandle());
        GLES20.glLinkProgram(this.handle);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(this.handle, 35714, iArr, 0);
        if (iArr[0] != 1) {
            throw new ShaderCompileException(String.format("Shader link error: '%s'", new Object[]{GLES20.glGetProgramInfoLog(this.handle)}));
        }
        Debug.Printf("Shaders: Binding variables...", new Object[0]);
        bindVariables();
        Debug.Printf("Shaders: Compiled, handle %d", Integer.valueOf(this.handle));
        return this.handle;
    }

    protected abstract void bindVariables();

    public int getHandle() {
        return this.handle;
    }
}
