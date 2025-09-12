// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class QuadProgram extends ShaderProgram
{

    public int sTexture;
    public int uColor;
    public int uColorize;
    public int uPostTranslate;
    public int uPreTranslate;
    public int uScale;
    public int vPosition;
    public int vTexCoord;

    public QuadProgram()
    {
        super(Shader.QuadVertexShader, Shader.QuadFragmentShader);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord");
        sTexture = GLES20.glGetUniformLocation(handle, "sTexture");
        uColor = GLES20.glGetUniformLocation(handle, "uColor");
        uColorize = GLES20.glGetUniformLocation(handle, "uColorize");
        uPreTranslate = GLES20.glGetUniformLocation(handle, "uPreTranslate");
        uScale = GLES20.glGetUniformLocation(handle, "uScale");
        uPostTranslate = GLES20.glGetUniformLocation(handle, "uPostTranslate");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
