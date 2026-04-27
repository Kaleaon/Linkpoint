// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            PrimProgram, Shader

public class FlexiPrimProgram extends PrimProgram
{

    public int uNumSectionMatrices;
    public int uSectionMatrices;

    public FlexiPrimProgram(boolean flag)
    {
        Shader shader1 = Shader.FlexiVertexShader;
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
        uSectionMatrices = GLES20.glGetUniformLocation(handle, "uSectionMatrices");
        uNumSectionMatrices = GLES20.glGetUniformLocation(handle, "uNumSectionMatrices");
    }
}
