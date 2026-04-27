package com.lumiyaviewer.lumiya.render.shaders;
import java.util.*;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.GLES30;

public class RiggedMeshProgram30 extends PrimProgram {
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

    public RiggedMeshProgram30(boolean z) {
        super(Shader.RiggedMeshVertexShader30, z ? Shader.PrimOpaqueFragmentShader30 : Shader.PrimFragmentShader30);
    }

    @TargetApi(18)
    protected void bindVariables() {
        super.bindVariables();
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight");
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint");
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix");
        int[] iArr = new int[1];
        this.uAnimationDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "AnimationData");
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uAnimationDataBlockIndex, 35392, iArr, 0);
        this.uAnimationDataBlockSize = iArr[0];
        GLES30.glUniformBlockBinding(this.handle, this.uAnimationDataBlockIndex, 1);
        this.uRiggingDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "RiggingData");
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uRiggingDataBlockIndex, 35392, iArr, 0);
        this.uRiggingDataBlockSize = iArr[0];
        int[] iArr2 = new int[2];
        int[] iArr3 = new int[2];
        GLES30.glGetUniformIndices(this.handle, new String[]{"jointMap", "jointMatrices"}, iArr2, 0);
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35387, iArr3, 0);
        this.uJointMapOffset = iArr3[0];
        this.uJointMatricesOffset = iArr3[1];
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35388, iArr3, 0);
        this.uJointMapArrayStride = iArr3[0];
        this.uJointMatricesArrayStride = iArr3[1];
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35389, iArr3, 0);
        this.uJointMatricesColumnStride = iArr3[1];
        GLES30.glUniformBlockBinding(this.handle, this.uRiggingDataBlockIndex, 2);
    }
}
