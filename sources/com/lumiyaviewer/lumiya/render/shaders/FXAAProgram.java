// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class FXAAProgram extends ShaderProgram
{

    public int noAAtextureSampler;
    public int texcoordOffset;
    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;

    public FXAAProgram()
    {
        super(Shader.FXAAVertexShader, Shader.FXAAFragmentShader);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord");
        textureSampler = GLES20.glGetUniformLocation(handle, "textureSampler");
        noAAtextureSampler = GLES20.glGetUniformLocation(handle, "noAAtextureSampler");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        texcoordOffset = GLES20.glGetUniformLocation(handle, "texcoordOffset");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
