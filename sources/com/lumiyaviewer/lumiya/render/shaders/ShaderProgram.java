// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderCompileException, Shader, ShaderPreprocessor

abstract class ShaderProgram
{

    private final Shader fragmentShader;
    protected int handle;
    private final Shader vertexShader;

    ShaderProgram(Shader shader, Shader shader1)
    {
        vertexShader = shader;
        fragmentShader = shader1;
    }

    public int Compile(ShaderPreprocessor shaderpreprocessor)
        throws ShaderCompileException
    {
        vertexShader.Compile(shaderpreprocessor);
        fragmentShader.Compile(shaderpreprocessor);
        Debug.Printf("Shaders: Linking...", new Object[0]);
        handle = GLES20.glCreateProgram();
        GLES20.glAttachShader(handle, vertexShader.getHandle());
        GLES20.glAttachShader(handle, fragmentShader.getHandle());
        GLES20.glLinkProgram(handle);
        shaderpreprocessor = new int[1];
        GLES20.glGetProgramiv(handle, 35714, shaderpreprocessor, 0);
        if (shaderpreprocessor[0] != 1)
        {
            throw new ShaderCompileException(String.format("Shader link error: '%s'", new Object[] {
                GLES20.glGetProgramInfoLog(handle)
            }));
        } else
        {
            Debug.Printf("Shaders: Binding variables...", new Object[0]);
            bindVariables();
            Debug.Printf("Shaders: Compiled, handle %d", new Object[] {
                Integer.valueOf(handle)
            });
            return handle;
        }
    }

    protected abstract void bindVariables();

    public int getHandle()
    {
        return handle;
    }
}
