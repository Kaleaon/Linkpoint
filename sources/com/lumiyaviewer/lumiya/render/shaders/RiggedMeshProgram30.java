// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import android.opengl.GLES30;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            PrimProgram, Shader

public class RiggedMeshProgram30 extends PrimProgram
{

    public int uAnimationDataBlockIndex;
    public int uAnimationDataBlockSize;
    public int uBindShapeMatrix;
    public int uJointMapArrayStride;
    public int uJointMapOffset;
    public int uJointMatricesArrayStride;
    public int uJointMatricesColumnStride;
    public int uJointMatricesOffset;
    public int uRiggingDataBlockIndex;
    public int uRiggingDataBlockSize;
    public int vJoint;
    public int vWeight;

    public RiggedMeshProgram30(boolean flag)
    {
        Shader shader1 = Shader.RiggedMeshVertexShader30;
        Shader shader;
        if (flag)
        {
            shader = Shader.PrimOpaqueFragmentShader30;
        } else
        {
            shader = Shader.PrimFragmentShader30;
        }
        super(shader1, shader);
    }

    protected void bindVariables()
    {
        super.bindVariables();
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight");
        vJoint = GLES20.glGetAttribLocation(handle, "vJoint");
        uBindShapeMatrix = GLES20.glGetUniformLocation(handle, "uBindShapeMatrix");
        int ai[] = new int[1];
        uAnimationDataBlockIndex = GLES30.glGetUniformBlockIndex(handle, "AnimationData");
        GLES30.glGetActiveUniformBlockiv(handle, uAnimationDataBlockIndex, 35392, ai, 0);
        uAnimationDataBlockSize = ai[0];
        GLES30.glUniformBlockBinding(handle, uAnimationDataBlockIndex, 1);
        uRiggingDataBlockIndex = GLES30.glGetUniformBlockIndex(handle, "RiggingData");
        GLES30.glGetActiveUniformBlockiv(handle, uRiggingDataBlockIndex, 35392, ai, 0);
        uRiggingDataBlockSize = ai[0];
        ai = new int[2];
        int ai1[] = new int[2];
        GLES30.glGetUniformIndices(handle, new String[] {
            "jointMap", "jointMatrices"
        }, ai, 0);
        GLES30.glGetActiveUniformsiv(handle, 2, ai, 0, 35387, ai1, 0);
        uJointMapOffset = ai1[0];
        uJointMatricesOffset = ai1[1];
        GLES30.glGetActiveUniformsiv(handle, 2, ai, 0, 35388, ai1, 0);
        uJointMapArrayStride = ai1[0];
        uJointMatricesArrayStride = ai1[1];
        GLES30.glGetActiveUniformsiv(handle, 2, ai, 0, 35389, ai1, 0);
        uJointMatricesColumnStride = ai1[1];
        GLES30.glUniformBlockBinding(handle, uRiggingDataBlockIndex, 2);
    }
}
