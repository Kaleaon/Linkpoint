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

/**
 * MeshRiggingData - Handles rigged mesh skinning data
 * 
 * Contains joint mapping and inverse bind matrices for skinning.
 * Uses interning to share identical rigging data between meshes.
 * 
 * Based on Firestorm's mesh rigging implementation
 */
class MeshRiggingData private constructor(
    private val joints: IntArray,
    private val jointMatrices: FloatArray,
    private val hasExtendedBones: Boolean
) {
    
    companion object {
        // Intern pool for sharing identical rigging data
        private val riggingDataPool = InternPool<MeshRiggingData>()
        
        /**
         * Create rigging data (interned for sharing)
         */
        fun create(joints: IntArray, jointMatrices: FloatArray, hasExtended: Boolean): MeshRiggingData {
            return riggingDataPool.intern(MeshRiggingData(joints, jointMatrices, hasExtended))
        }
    }
    
    private var glRiggingDataBuffer: GLLoadableBuffer? = null
    private val hashCode: Int = calcHashCode()
    private var mappedJointMatrices: FloatArray? = null
    private var mappedJointVectors: FloatArray? = null
    
    /**
     * Calculate hash code for interning
     */
    private fun calcHashCode(): Int {
        return Arrays.hashCode(joints) * 31 + Arrays.hashCode(jointMatrices)
    }
    
    /**
     * Prepare rigging uniform buffer for OpenGL ES 3.0+
     */
    private fun prepareRiggingUniformBuffer(renderContext: RenderContext): DirectByteBuffer {
        val program = renderContext.currentRiggedMeshProgram
        val buffer = DirectByteBuffer(program.uRiggingDataBlockSize)
        
        // Upload joint map
        for (i in joints.indices) {
            buffer.putRawInt(
                program.uJointMapOffset + (program.uJointMapArrayStride * i),
                joints[i]
            )
        }
        
        // Upload joint matrices (column-major for OpenGL)
        for (i in joints.indices) {
            val baseOffset = (program.uJointMatricesOffset + 
                            (program.uJointMatricesArrayStride * i)) / 4
            
            // Convert row-major to column-major
            for (col in 0 until 4) {
                buffer.loadFromFloatArray(
                    ((program.uJointMatricesColumnStride * col) / 4) + baseOffset,
                    jointMatrices,
                    (i * 16) + (col * 4),
                    4
                )
            }
        }
        
        return buffer
    }
    
    /**
     * Prepare influence buffers for OpenGL ES 2.0 (legacy)
     */
    internal fun PrepareInfluenceBuffers(renderContext: RenderContext, bindShapeMatrix: FloatArray?) {
        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle())
        
        if (bindShapeMatrix != null) {
            GLES20.glUniformMatrix4fv(
                renderContext.riggedMeshProgram.uBindShapeMatrix,
                1,
                false,
                bindShapeMatrix,
                0
            )
        }
        
        mappedJointVectors?.let {
            GLES20.glUniform4fv(
                renderContext.riggedMeshProgram.uJointVectors,
                it.size / 4,
                it,
                0
            )
        }
    }
    
    /**
     * Setup uniform buffer for OpenGL ES 3.0+
     */
    fun SetupBuffers30(renderContext: RenderContext) {
        if (glRiggingDataBuffer == null) {
            glRiggingDataBuffer = GLLoadableBuffer(prepareRiggingUniformBuffer(renderContext))
        }
        glRiggingDataBuffer!!.BindUniform(renderContext, 2)
    }
    
    /**
     * Update rigged mesh vertices
     */
    internal fun UpdateRigged(
        meshFace: MeshFace?,
        bindShapeMatrix: FloatArray?,
        targetBuffer: DirectByteBuffer,
        bufferOffset: Int
    ) {
        if (meshFace != null && bindShapeMatrix != null && mappedJointMatrices != null) {
            meshFace.UpdateRigged(targetBuffer, bufferOffset, bindShapeMatrix, mappedJointMatrices!!)
        }
    }
    
    /**
     * Update rigged matrices from avatar skeleton
     * Called each frame to animate rigged meshes
     */
    internal fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        // Allocate on first use
        if (mappedJointMatrices == null) {
            mappedJointMatrices = FloatArray(joints.size * 16)
        }
        if (mappedJointVectors == null) {
            mappedJointVectors = FloatArray(joints.size * 3 * 4)
        }
        
        val skeletonMatrices = avatarSkeleton.jointWorldMatrix
        
        // For each joint in this mesh
        for (i in joints.indices) {
            if (joints[i] >= 0) {
                // Multiply: mapped[i] = skeleton[joints[i]] * inverseBindMatrix[i]
                Matrix.multiplyMM(
                    mappedJointMatrices,
                    i * 16,
                    skeletonMatrices,
                    joints[i] * 16,
                    jointMatrices,
                    i * 16
                )
            } else {
                // Invalid joint - use identity
                Matrix.setIdentityM(mappedJointMatrices, i * 16)
            }
            
            // Extract 3x4 matrix (position vectors) for shader
            for (row in 0 until 3) {
                mappedJointVectors!![i * 3 * 4 + row * 4 + 0] = mappedJointMatrices!![i * 16 + row + 0]
                mappedJointVectors!![i * 3 * 4 + row * 4 + 1] = mappedJointMatrices!![i * 16 + row + 4]
                mappedJointVectors!![i * 3 * 4 + row * 4 + 2] = mappedJointMatrices!![i * 16 + row + 8]
                mappedJointVectors!![i * 3 * 4 + row * 4 + 3] = mappedJointMatrices!![i * 16 + row + 12]
            }
        }
    }
    
    fun hasExtendedBones(): Boolean = hasExtendedBones
    
    fun fitsGL20(): Boolean {
        // Check if rigging complexity fits in OpenGL ES 2.0 limits
        return joints.size <= 64  // Typical uniform limit
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeshRiggingData) return false
        
        return Arrays.equals(joints, other.joints) &&
               Arrays.equals(jointMatrices, other.jointMatrices) &&
               hasExtendedBones == other.hasExtendedBones
    }
    
    override fun hashCode(): Int = hashCode
    
    override fun toString(): String {
        return "MeshRiggingData(joints=${joints.size}, extended=$hasExtendedBones)"
    }
}
