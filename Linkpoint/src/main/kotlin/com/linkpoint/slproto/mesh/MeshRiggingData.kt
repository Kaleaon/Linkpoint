package com.linkpoint.slproto.mesh

import android.opengl.GLES20
import android.opengl.Matrix
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.AvatarSkeleton
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.render.shaders.RiggedMeshProgram30
import com.linkpoint.utils.InternPool
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.util.Arrays
import javax.annotation.Nonnull

class MeshRiggingData {
    private const val InternPool<MeshRiggingData> riggingDataPool = InternPool<>()
    private GLLoadableBuffer glRiggingDataBuffer = null
    private val Boolean hasExtendedBones
    private val Int hashCode
    private val Float[] jointMatrices
    private val Int[] joints
    private Float[] mappedJointMatrices
    private Float[] mappedJointVectors

    private MeshRiggingData(Int[] iArr, Float[] fArr, Boolean z) {
        this.joints = iArr
        this.jointMatrices = fArr
        this.hasExtendedBones = z
        this.hashCode = calcHashCode()
    }

    private DirectByteBuffer PrepareRiggingUniformBuffer(RenderContext renderContext) {
        RiggedMeshProgram30 riggedMeshProgram30 = renderContext.currentRiggedMeshProgram
        DirectByteBuffer directByteBuffer = DirectByteBuffer(riggedMeshProgram30.uRiggingDataBlockSize)
        for (Int i = 0; i < this.joints.length; i++) {
            directByteBuffer.putRawInt(riggedMeshProgram30.uJointMapOffset + (riggedMeshProgram30.uJointMapArrayStride * i), this.joints[i])
        }
        for (Int i2 = 0; i2 < this.joints.length; i2++) {
            Int i3 = (riggedMeshProgram30.uJointMatricesOffset + (riggedMeshProgram30.uJointMatricesArrayStride * i2)) / 4
            for (Int i4 = 0; i4 < 4; i4++) {
                directByteBuffer.loadFromFloatArray(((riggedMeshProgram30.uJointMatricesColumnStride * i4) / 4) + i3, this.jointMatrices, (i2 * 16) + (i4 * 4), 4)
            }
        }
        return directByteBuffer
    }

    private Int calcHashCode() {
        return (Arrays.hashCode(this.joints) * 31) + Arrays.hashCode(this.jointMatrices)
    }

    @JvmStatic
    MeshRiggingData create(Int[] iArr, Float[] fArr, Boolean z) {
        return riggingDataPool.intern(MeshRiggingData(iArr, fArr, z))
    }

    /* access modifiers changed from: package-private */
    public Unit PrepareInfluenceBuffers(RenderContext renderContext, Float[] fArr) {
        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle())
        GLES20.glUniformMatrix4fv(renderContext.riggedMeshProgram.uBindShapeMatrix, 1, false, fArr, 0)
        GLES20.glUniform4fv(renderContext.riggedMeshProgram.uJointVectors, this.mappedJointVectors.length / 4, this.mappedJointVectors, 0)
    }

    public Unit SetupBuffers30(RenderContext renderContext) {
        if (this.glRiggingDataBuffer == null) {
            this.glRiggingDataBuffer = GLLoadableBuffer(PrepareRiggingUniformBuffer(renderContext))
        }
        this.glRiggingDataBuffer.BindUniform(renderContext, 2)
    }

    /* access modifiers changed from: package-private */
    public Unit UpdateRigged(MeshFace meshFace, Float[] fArr, DirectByteBuffer directByteBuffer, Int i) {
        meshFace.UpdateRigged(directByteBuffer, i, fArr, this.mappedJointMatrices)
    }

    /* access modifiers changed from: package-private */
    public Unit UpdateRiggedMatrices(AvatarSkeleton avatarSkeleton) {
        if (this.mappedJointMatrices == null) {
            this.mappedJointMatrices = Float[(this.joints.length * 16)]
        }
        if (this.mappedJointVectors == null) {
            this.mappedJointVectors = Float[(this.joints.length * 3 * 4)]
        }
        Float[] fArr = avatarSkeleton.jointWorldMatrix
        for (Int i = 0; i < this.joints.length; i++) {
            if (this.joints[i] >= 0) {
                Matrix.multiplyMM(this.mappedJointMatrices, i * 16, fArr, this.joints[i] * 16, this.jointMatrices, i * 16)
            } else {
                Matrix.setIdentityM(this.mappedJointMatrices, i * 16)
            }
            for (Int i2 = 0; i2 < 3; i2++) {
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 0] = this.mappedJointMatrices[(i * 16) + i2 + 0]
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 1] = this.mappedJointMatrices[(i * 16) + i2 + 4]
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 2] = this.mappedJointMatrices[(i * 16) + i2 + 8]
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 3] = this.mappedJointMatrices[(i * 16) + i2 + 12]
            }
        }
    }

    public Boolean equals(Object obj) {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        MeshRiggingData meshRiggingData = (MeshRiggingData) obj
        if (Arrays.equals(this.joints, meshRiggingData.joints)) {
            return Arrays.equals(this.jointMatrices, meshRiggingData.jointMatrices)
        }
        return false
    }

    /* access modifiers changed from: package-private */
    val Boolean fitsGL20() {
        return this.joints.length <= 52
    }

    /* access modifiers changed from: package-private */
    val Boolean hasExtendedBones() {
        return this.hasExtendedBones
    }

    public Int hashCode() {
        return this.hashCode
    }
}
