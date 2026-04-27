// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class RawShaderProgram extends ShaderProgram
{

    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;
    public int vTextureTransformMatrix;

    public RawShaderProgram(boolean flag)
    {
        Shader shader;
        Shader shader1;
        if (flag)
        {
            shader = Shader.ExtTextureVertexShader;
        } else
        {
            shader = Shader.RawVertexShader;
        }
        if (flag)
        {
            shader1 = Shader.ExtTextureFragmentShader;
        } else
        {
            shader1 = Shader.RawFragmentShader;
        }
        super(shader, shader1);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        textureSampler = GLES20.glGetUniformLocation(handle, "vTexture");
        vTextureTransformMatrix = GLES20.glGetUniformLocation(handle, "vTextureTransformMatrix");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
