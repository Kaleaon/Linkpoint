package com.linkpoint.render.shaders
import java.util.*

import android.annotation.TargetApi
import android.opengl.GLES20
import android.opengl.GLES30

class RiggedMeshProgram30 : PrimProgram() {
    public Int uAnimationDataBlockIndex
    public Int uAnimationDataBlockSize
    public Int uBindShapeMatrix
    public Int uJointMapArrayStride
    public Int uJointMapOffset
    public Int uJointMatricesArrayStride
    public Int uJointMatricesColumnStride
    public Int uJointMatricesOffset
    public Int uRiggingDataBlockIndex
    public Int uRiggingDataBlockSize
    public Int vJoint
    public Int vWeight

    public RiggedMeshProgram30(Boolean z) {
        super(Shader.RiggedMeshVertexShader30, z ? Shader.PrimOpaqueFragmentShader30 : Shader.PrimFragmentShader30)
    }

    @TargetApi(18)
     protected fun bindVariables() {
        super.bindVariables()
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight")
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint")
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix")
        val iArr: IntArray = Int[1]
        this.uAnimationDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "AnimationData")
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uAnimationDataBlockIndex, 35392, iArr, 0)
        this.uAnimationDataBlockSize = iArr[0]
        GLES30.glUniformBlockBinding(this.handle, this.uAnimationDataBlockIndex, 1)
        this.uRiggingDataBlockIndex = GLES30.glGetUniformBlockIndex(this.handle, "RiggingData")
        GLES30.glGetActiveUniformBlockiv(this.handle, this.uRiggingDataBlockIndex, 35392, iArr, 0)
        this.uRiggingDataBlockSize = iArr[0]
        val iArr2: IntArray = Int[2]
        val iArr3: IntArray = Int[2]
        GLES30.glGetUniformIndices(this.handle, Array<String>{"jointMap", "jointMatrices"}, iArr2, 0)
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35387, iArr3, 0)
        this.uJointMapOffset = iArr3[0]
        this.uJointMatricesOffset = iArr3[1]
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35388, iArr3, 0)
        this.uJointMapArrayStride = iArr3[0]
        this.uJointMatricesArrayStride = iArr3[1]
        GLES30.glGetActiveUniformsiv(this.handle, 2, iArr2, 0, 35389, iArr3, 0)
        this.uJointMatricesColumnStride = iArr3[1]
        GLES30.glUniformBlockBinding(this.handle, this.uRiggingDataBlockIndex, 2)
    }
}
