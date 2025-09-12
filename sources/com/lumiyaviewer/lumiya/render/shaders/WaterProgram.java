// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class WaterProgram extends ShaderProgram
{

    public int uAmplitude;
    public int uDirection;
    public int uFrequency;
    public int uMVPMatrix;
    public int uObjWorldMatrix;
    public int uPhase;
    public int uTime;
    public int vColor;
    public int vPosition;

    public WaterProgram()
    {
        super(Shader.WaterVertexShader, Shader.WaterFragmentShader);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        vColor = GLES20.glGetUniformLocation(handle, "vColor");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix");
        uTime = GLES20.glGetUniformLocation(handle, "time");
        uFrequency = GLES20.glGetUniformLocation(handle, "frequency");
        uPhase = GLES20.glGetUniformLocation(handle, "phase");
        uAmplitude = GLES20.glGetUniformLocation(handle, "amplitude");
        uDirection = GLES20.glGetUniformLocation(handle, "direction");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
