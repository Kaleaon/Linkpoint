// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import android.opengl.GLES20;

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
    
    public RiggedMeshProgram30(final boolean b) {
        final Shader riggedMeshVertexShader30 = Shader.RiggedMeshVertexShader30;
        Shader shader;
        if (b) {
            shader = Shader.PrimOpaqueFragmentShader30;
        }
        else {
            shader = Shader.PrimFragmentShader30;
        }
        super(riggedMeshVertexShader30, shader);
    }
    
    @TargetApi(18)
    @Override
    protected void bindVariables() {
        super.bindVariables();
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight");
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint");
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix");
        final int[] array = { 0 };
        this.uAnimationDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "AnimationData");
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uAnimationDataBlockIndex, 35392, array, 0);
        this.uAnimationDataBlockSize = array[0];
        GLES30.glUniformBlockBinding(this.handle, this.uAnimationDataBlockIndex, 1);
        this.uRiggingDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "RiggingData");
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uRiggingDataBlockIndex, 35392, array, 0);
        this.uRiggingDataBlockSize = array[0];
        final int[] array2 = new int[2];
        final int[] array3 = new int[2];
        GLES30.glGetUniformIndices(this.handle, new String[] { "jointMap", "jointMatrices" }, array2, 0);
        GLES30.glGetActiveUniformsiv(this.handle, 2, array2, 0, 35387, array3, 0);
        this.uJointMapOffset = array3[0];
        this.uJointMatricesOffset = array3[1];
        GLES30.glGetActiveUniformsiv(this.handle, 2, array2, 0, 35388, array3, 0);
        this.uJointMapArrayStride = array3[0];
        this.uJointMatricesArrayStride = array3[1];
        GLES30.glGetActiveUniformsiv(this.handle, 2, array2, 0, 35389, array3, 0);
        this.uJointMatricesColumnStride = array3[1];
        GLES30.glUniformBlockBinding(this.handle, this.uRiggingDataBlockIndex, 2);
    }
}
