// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            BasicPrimProgram, Shader

public class AvatarProgram extends BasicPrimProgram
{

    public int uJointMap;
    public int uJointMapLength;
    public int uJointMatrix;
    public int uUseWeight;
    public int vWeight;

    public AvatarProgram()
    {
        super(Shader.AvatarVertexShader, Shader.PrimFragmentShader);
    }

    protected void bindVariables()
    {
        super.bindVariables();
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight");
        uJointMatrix = GLES20.glGetUniformLocation(handle, "uJointMatrix");
        uJointMap = GLES20.glGetUniformLocation(handle, "uJointMap");
        uJointMapLength = GLES20.glGetUniformLocation(handle, "uJointMapLength");
        uUseWeight = GLES20.glGetUniformLocation(handle, "uUseWeight");
    }
}
