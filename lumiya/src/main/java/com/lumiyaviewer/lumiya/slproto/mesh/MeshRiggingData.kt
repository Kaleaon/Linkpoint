package com.lumiyaviewer.lumiya.slproto.mesh

import android.opengl.GLES20
import android.opengl.Matrix
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.utils.InternPool
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.util.Arrays
import androidx.annotation.NonNull

class MeshRiggingData private constructor(@NonNull val joints: IntArray, @NonNull val jointMatrices: FloatArray, val hasExtendedBones: Boolean) {
    
    private var glRiggingDataBuffer: GLLoadableBuffer? = null
    private var hashCode: Int = 0
    private var mappedJointMatrices: FloatArray? = null
    private var mappedJointVectors: FloatArray? = null

    init {
        this.hashCode = calcHashCode()
    }

    companion object {
        private val riggingDataPool = InternPool<MeshRiggingData>()

        fun create(@NonNull joints: IntArray, @NonNull jointMatrices: FloatArray, hasExtendedBones: Boolean): MeshRiggingData {
            return riggingDataPool.intern(MeshRiggingData(joints, jointMatrices, hasExtendedBones))
        }
    }

    private fun PrepareRiggingUniformBuffer(renderContext: RenderContext): DirectByteBuffer {
        val riggedMeshProgram30 = renderContext.currentRiggedMeshProgram
        val directByteBuffer = DirectByteBuffer(riggedMeshProgram30.uRiggingDataBlockSize)
        
        for (i in this.joints.indices) {
            directByteBuffer.putRawInt(riggedMeshProgram30.uJointMapOffset + (riggedMeshProgram30.uJointMapArrayStride * i), this.joints[i])
        }
        
        for (i in this.joints.indices) {
            val i3 = (riggedMeshProgram30.uJointMatricesOffset + (riggedMeshProgram30.uJointMatricesArrayStride * i)) / 4
            for (i4 in 0 until 4) {
                directByteBuffer.loadFromFloatArray(((riggedMeshProgram30.uJointMatricesColumnStride * i4) / 4) + i3, this.jointMatrices, (i * 16) + (i4 * 4), 4)
            }
        }
        return directByteBuffer
    }

    private fun calcHashCode(): Int {
        return (Arrays.hashCode(this.joints) * 31) + Arrays.hashCode(this.jointMatrices)
    }

    internal fun PrepareInfluenceBuffers(renderContext: RenderContext, bindShapeMatrix: FloatArray) {
        if (mappedJointVectors == null) return // Should ensure initialized

        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle())
        GLES20.glUniformMatrix4fv(renderContext.riggedMeshProgram.uBindShapeMatrix, 1, false, bindShapeMatrix, 0)
        GLES20.glUniform4fv(renderContext.riggedMeshProgram.uJointVectors, this.mappedJointVectors!!.size / 4, this.mappedJointVectors, 0)
    }

    internal fun SetupBuffers30(renderContext: RenderContext) {
        if (this.glRiggingDataBuffer == null) {
            this.glRiggingDataBuffer = GLLoadableBuffer(PrepareRiggingUniformBuffer(renderContext))
        }
        this.glRiggingDataBuffer!!.BindUniform(renderContext, 2)
    }

    internal fun UpdateRigged(meshFace: MeshFace, bindShapeMatrix: FloatArray, directByteBuffer: DirectByteBuffer, i: Int) {
        if (mappedJointMatrices != null) {
            meshFace.UpdateRigged(directByteBuffer, i, bindShapeMatrix, this.mappedJointMatrices!!)
        }
    }

    internal fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        if (this.mappedJointMatrices == null) {
            this.mappedJointMatrices = FloatArray(this.joints.size * 16)
        }
        if (this.mappedJointVectors == null) {
            this.mappedJointVectors = FloatArray(this.joints.size * 3 * 4)
        }
        
        // Access jointWorldMatrix from AvatarSkeleton - assuming it's accessible or we need a getter
        // AvatarSkeleton likely inherits jointWorldMatrix from SLDefaultSkeleton.
        // If private, we need a public getter in SLDefaultSkeleton/AvatarSkeleton.
        // Assuming getJointWorldMatrix() exists or making it public.
        // The decompiled code accessed field directly, implying package private or protected + same package.
        val jointWorldMatrix = avatarSkeleton.getJointWorldMatrix()
        
        for (i in this.joints.indices) {
            if (this.joints[i] >= 0) {
                Matrix.multiplyMM(this.mappedJointMatrices, i * 16, jointWorldMatrix, this.joints[i] * 16, this.jointMatrices, i * 16)
            } else {
                Matrix.setIdentityM(this.mappedJointMatrices, i * 16)
            }
            for (i2 in 0 until 3) {
                this.mappedJointVectors!![(i * 3 * 4) + (i2 * 4) + 0] = this.mappedJointMatrices!![(i * 16) + i2 + 0]
                this.mappedJointVectors!![(i * 3 * 4) + (i2 * 4) + 1] = this.mappedJointMatrices!![(i * 16) + i2 + 4]
                this.mappedJointVectors!![(i * 3 * 4) + (i2 * 4) + 2] = this.mappedJointMatrices!![(i * 16) + i2 + 8]
                this.mappedJointVectors!![(i * 3 * 4) + (i2 * 4) + 3] = this.mappedJointMatrices!![(i * 16) + i2 + 12]
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as MeshRiggingData
        if (!Arrays.equals(this.joints, that.joints)) return false
        return Arrays.equals(this.jointMatrices, that.jointMatrices)
    }

    internal fun fitsGL20(): Boolean {
        return this.joints.size <= 52
    }

    override fun hashCode(): Int {
        return this.hashCode
    }
}
