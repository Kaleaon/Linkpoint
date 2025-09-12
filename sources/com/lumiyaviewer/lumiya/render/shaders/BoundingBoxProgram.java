// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class BoundingBoxProgram extends ShaderProgram
{

    public int uMVPMatrix;
    public int uObjCoordScale;
    public int uObjWorldMatrix;
    public int vPosition;

    public BoundingBoxProgram()
    {
        super(Shader.BoundingBoxVertexShader, Shader.BoundingBoxFragmentShader);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix");
        uObjCoordScale = GLES20.glGetUniformLocation(handle, "uObjCoordScale");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
