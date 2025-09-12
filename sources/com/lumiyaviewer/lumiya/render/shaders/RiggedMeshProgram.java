// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            PrimProgram, Shader

public class RiggedMeshProgram extends PrimProgram
{

    public int uBindShapeMatrix;
    public int uJointVectors;
    public int vJoint;
    public int vWeight;

    public RiggedMeshProgram(boolean flag)
    {
        Shader shader1 = Shader.RiggedMeshVertexShader;
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
        uBindShapeMatrix = GLES20.glGetUniformLocation(handle, "uBindShapeMatrix");
        uJointVectors = GLES20.glGetUniformLocation(handle, "uJointVectors");
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight");
        vJoint = GLES20.glGetAttribLocation(handle, "vJoint");
    }
}
