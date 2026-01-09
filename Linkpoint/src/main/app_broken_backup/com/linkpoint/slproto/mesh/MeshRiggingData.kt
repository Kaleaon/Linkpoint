package com.linkpoint.slproto.mesh

import android.opengl.GLES20
import android.opengl.Matrix
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.AvatarSkeleton
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.render.shaders.RiggedMeshProgram30
import com.linkpoint.utils.InternPool
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.util.Arrays

class MeshRiggingData private constructor(
    private val joints: IntArray,
    private val jointMatrices: FloatArray,
    private val hasExtendedBones: Boolean
) {
    private var glRiggingDataBuffer: GLLoadableBuffer? = null
    private val hashCode: Int = calcHashCode()
    private var mappedJointMatrices: FloatArray? = null
    private var mappedJointVectors: FloatArray? = null

    companion object {
        private val riggingDataPool: InternPool<MeshRiggingData> = InternPool()

        fun create(joints: IntArray, jointMatrices: FloatArray, hasExtendedBones: Boolean): MeshRiggingData {
            return riggingDataPool.intern(MeshRiggingData(joints, jointMatrices, hasExtendedBones))
        }
    }

    private fun PrepareRiggingUniformBuffer(renderContext: RenderContext): DirectByteBuffer {
        val riggedMeshProgram30 = renderContext.currentRiggedMeshProgram
        val directByteBuffer = DirectByteBuffer(riggedMeshProgram30.uRiggingDataBlockSize)
        for (i in 0 until joints.size) {
            directByteBuffer.putRawInt(riggedMeshProgram30.uJointMapOffset + (riggedMeshProgram30.uJointMapArrayStride * i), joints[i])
        }
        for (i2 in 0 until joints.size) {
            val i3 = (riggedMeshProgram30.uJointMatricesOffset + (riggedMeshProgram30.uJointMatricesArrayStride * i2)) / 4
            for (i4 in 0 until 4) {
                directByteBuffer.loadFromFloatArray(((riggedMeshProgram30.uJointMatricesColumnStride * i4) / 4) + i3, jointMatrices, (i2 * 16) + (i4 * 4), 4)
            }
        }
        return directByteBuffer
    }

    private fun calcHashCode(): Int {
        return (Arrays.hashCode(joints) * 31) + Arrays.hashCode(jointMatrices)
    }

    fun PrepareInfluenceBuffers(renderContext: RenderContext, fArr: FloatArray) {
        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle())
        GLES20.glUniformMatrix4fv(renderContext.riggedMeshProgram.uBindShapeMatrix, 1, false, fArr, 0)
        val mappedVectors = mappedJointVectors
        if (mappedVectors != null) {
            GLES20.glUniform4fv(renderContext.riggedMeshProgram.uJointVectors, mappedVectors.size / 4, mappedVectors, 0)
        }
    }

    fun SetupBuffers30(renderContext: RenderContext) {
        if (glRiggingDataBuffer == null) {
            glRiggingDataBuffer = GLLoadableBuffer(PrepareRiggingUniformBuffer(renderContext))
        }
        glRiggingDataBuffer?.BindUniform(renderContext, 2)
    }

    fun UpdateRigged(meshFace: MeshFace, fArr: FloatArray, directByteBuffer: DirectByteBuffer, i: Int) {
        val matrices = mappedJointMatrices
        if (matrices != null) {
            meshFace.UpdateRigged(directByteBuffer, i, fArr, matrices)
        }
    }

    fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        if (mappedJointMatrices == null) {
            mappedJointMatrices = FloatArray(joints.size * 16)
        }
        if (mappedJointVectors == null) {
            mappedJointVectors = FloatArray(joints.size * 3 * 4)
        }
        val fArr = avatarSkeleton.jointWorldMatrix
        val mappedMatrices = mappedJointMatrices!!
        val mappedVectors = mappedJointVectors!!
        for (i in joints.indices) {
            if (joints[i] >= 0) {
                Matrix.multiplyMM(mappedMatrices, i * 16, fArr, joints[i] * 16, jointMatrices, i * 16)
            } else {
                Matrix.setIdentityM(mappedMatrices, i * 16)
            }
            for (i2 in 0 until 3) {
                mappedVectors[(i * 3 * 4) + (i2 * 4) + 0] = mappedMatrices[(i * 16) + i2 + 0]
                mappedVectors[(i * 3 * 4) + (i2 * 4) + 1] = mappedMatrices[(i * 16) + i2 + 4]
                mappedVectors[(i * 3 * 4) + (i2 * 4) + 2] = mappedMatrices[(i * 16) + i2 + 8]
                mappedVectors[(i * 3 * 4) + (i2 * 4) + 3] = mappedMatrices[(i * 16) + i2 + 12]
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val meshRiggingData = other as MeshRiggingData
        if (Arrays.equals(joints, meshRiggingData.joints)) {
            return Arrays.equals(jointMatrices, meshRiggingData.jointMatrices)
        }
        return false
    }

    fun fitsGL20(): Boolean {
        return joints.size <= 52
    }

    fun hasExtendedBones(): Boolean {
        return hasExtendedBones
    }

    override fun hashCode(): Int {
        return hashCode
    }
}
