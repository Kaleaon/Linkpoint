// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;

abstract class ShaderProgram
{
    private final Shader fragmentShader;
    protected int handle;
    private final Shader vertexShader;
    
    ShaderProgram(final Shader vertexShader, final Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }
    
    public int Compile(final ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
        this.vertexShader.Compile(shaderPreprocessor);
        this.fragmentShader.Compile(shaderPreprocessor);
        Debug.Printf("Shaders: Linking...", new Object[0]);
        GLES20.glAttachShader(this.handle = GLES20.glCreateProgram(), this.vertexShader.getHandle());
        GLES20.glAttachShader(this.handle, this.fragmentShader.getHandle());
        GLES20.glLinkProgram(this.handle);
        final int[] array = { 0 };
        GLES20.glGetProgramiv(this.handle, 35714, array, 0);
        if (array[0] != 1) {
            throw new ShaderCompileException(String.format("Shader link error: '%s'", GLES20.glGetProgramInfoLog(this.handle)));
        }
        Debug.Printf("Shaders: Binding variables...", new Object[0]);
        this.bindVariables();
        Debug.Printf("Shaders: Compiled, handle %d", this.handle);
        return this.handle;
    }
    
    protected abstract void bindVariables();
    
    public int getHandle() {
        return this.handle;
    }
}
