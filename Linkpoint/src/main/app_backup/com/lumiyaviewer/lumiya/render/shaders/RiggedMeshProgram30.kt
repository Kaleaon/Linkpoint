package com.lumiyaviewer.lumiya.render.shaders

import android.annotation.TargetApi
import android.opengl.GLES20
import android.opengl.GLES30

class RiggedMeshProgram30(z: Boolean) : PrimProgram(
    Shader.RiggedMeshVertexShader30,
    if (z) Shader.PrimOpaqueFragmentShader30 else Shader.PrimFragmentShader30
) {
    var uAnimationDataBlockIndex: Int = 0
    var uAnimationDataBlockSize: Int = 0
    var uBindShapeMatrix: Int = 0
    var uJointMapArrayStride: Int = 0
    var uJointMapOffset: Int = 0
    var uJointMatricesArrayStride: Int = 0
    var uJointMatricesColumnStride: Int = 0
    var uJointMatricesOffset: Int = 0
    var uRiggingDataBlockIndex: Int = 0
    var uRiggingDataBlockSize: Int = 0
    var vJoint: Int = 0
    var vWeight: Int = 0

    @TargetApi(18)
    override fun bindVariables() {
        super.bindVariables()
        vWeight = GLES20.glGetAttribLocation(handle, "vWeight")
        vJoint = GLES20.glGetAttribLocation(handle, "vJoint")
        uBindShapeMatrix = GLES20.glGetUniformLocation(handle, "uBindShapeMatrix")
        val iArr = IntArray(1)
        uAnimationDataBlockIndex = GLES30.glGetUniformBlockIndex(handle, "AnimationData")
        GLES30.glGetActiveUniformBlockiv(handle, uAnimationDataBlockIndex, GLES30.GL_UNIFORM_BLOCK_DATA_SIZE, iArr, 0)
        uAnimationDataBlockSize = iArr[0]
        GLES30.glUniformBlockBinding(handle, uAnimationDataBlockIndex, 1)
        uRiggingDataBlockIndex = GLES30.glGetUniformBlockIndex(handle, "RiggingData")
        GLES30.glGetActiveUniformBlockiv(handle, uRiggingDataBlockIndex, GLES30.GL_UNIFORM_BLOCK_DATA_SIZE, iArr, 0)
        uRiggingDataBlockSize = iArr[0]
        val iArr2 = IntArray(2)
        val iArr3 = IntArray(2)
        GLES30.glGetUniformIndices(handle, arrayOf("jointMap", "jointMatrices"), iArr2, 0)
        GLES30.glGetActiveUniformsiv(handle, 2, iArr2, 0, GLES30.GL_UNIFORM_OFFSET, iArr3, 0)
        uJointMapOffset = iArr3[0]
        uJointMatricesOffset = iArr3[1]
        GLES30.glGetActiveUniformsiv(handle, 2, iArr2, 0, GLES30.GL_UNIFORM_ARRAY_STRIDE, iArr3, 0)
        uJointMapArrayStride = iArr3[0]
        uJointMatricesArrayStride = iArr3[1]
        GLES30.glGetActiveUniformsiv(handle, 2, iArr2, 0, GLES30.GL_UNIFORM_MATRIX_STRIDE, iArr3, 0)
        uJointMatricesColumnStride = iArr3[1]
        GLES30.glUniformBlockBinding(handle, uRiggingDataBlockIndex, 2)
    }
}
