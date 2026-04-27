// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            BasicPrimProgram, Shader

public class PrimProgram extends BasicPrimProgram
{

    public int uTexMatrix;

    public PrimProgram(Shader shader, Shader shader1)
    {
        super(shader, shader1);
    }

    public PrimProgram(boolean flag)
    {
        Shader shader1 = Shader.PrimVertexShader;
        Shader shader;
        if (flag)
        {
            shader = Shader.PrimOpaqueFragmentShader;
        } else
        {
            shader = Shader.PrimFragmentShader;
        }
        super(shader1, shader);
    }

    protected void bindVariables()
    {
        super.bindVariables();
        uTexMatrix = GLES20.glGetUniformLocation(handle, "uTexMatrix");
    }
}
