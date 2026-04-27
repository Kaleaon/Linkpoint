// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.mesh;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import android.opengl.GLES20;
import java.util.Arrays;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.RenderContext;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.utils.InternPool;

public class MeshRiggingData
{
    private static final InternPool<MeshRiggingData> riggingDataPool;
    private GLLoadableBuffer glRiggingDataBuffer;
    private final boolean hasExtendedBones;
    private final int hashCode;
    @Nonnull
    private final float[] jointMatrices;
    @Nonnull
    private final int[] joints;
    private float[] mappedJointMatrices;
    private float[] mappedJointVectors;
    
    static {
        riggingDataPool = new InternPool<MeshRiggingData>();
    }
    
    private MeshRiggingData(@Nonnull final int[] joints, @Nonnull final float[] jointMatrices, final boolean hasExtendedBones) {
        this.glRiggingDataBuffer = null;
        this.joints = joints;
        this.jointMatrices = jointMatrices;
        this.hasExtendedBones = hasExtendedBones;
        this.hashCode = this.calcHashCode();
    }
    
    private DirectByteBuffer PrepareRiggingUniformBuffer(final RenderContext renderContext) {
        final RiggedMeshProgram30 currentRiggedMeshProgram = renderContext.currentRiggedMeshProgram;
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(currentRiggedMeshProgram.uRiggingDataBlockSize);
        for (int i = 0; i < this.joints.length; ++i) {
            directByteBuffer.putRawInt(currentRiggedMeshProgram.uJointMapOffset + currentRiggedMeshProgram.uJointMapArrayStride * i, this.joints[i]);
        }
        for (int j = 0; j < this.joints.length; ++j) {
            final int n = (currentRiggedMeshProgram.uJointMatricesOffset + currentRiggedMeshProgram.uJointMatricesArrayStride * j) / 4;
            for (int k = 0; k < 4; ++k) {
                directByteBuffer.loadFromFloatArray(currentRiggedMeshProgram.uJointMatricesColumnStride * k / 4 + n, this.jointMatrices, j * 16 + k * 4, 4);
            }
        }
        return directByteBuffer;
    }
    
    private int calcHashCode() {
        return Arrays.hashCode(this.joints) * 31 + Arrays.hashCode(this.jointMatrices);
    }
    
    public static MeshRiggingData create(@Nonnull final int[] array, @Nonnull final float[] array2, final boolean b) {
        return MeshRiggingData.riggingDataPool.intern(new MeshRiggingData(array, array2, b));
    }
    
    void PrepareInfluenceBuffers(final RenderContext renderContext, final float[] array) {
        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle());
        GLES20.glUniformMatrix4fv(renderContext.riggedMeshProgram.uBindShapeMatrix, 1, false, array, 0);
        GLES20.glUniform4fv(renderContext.riggedMeshProgram.uJointVectors, this.mappedJointVectors.length / 4, this.mappedJointVectors, 0);
    }
    
    public void SetupBuffers30(final RenderContext renderContext) {
        if (this.glRiggingDataBuffer == null) {
            this.glRiggingDataBuffer = new GLLoadableBuffer(this.PrepareRiggingUniformBuffer(renderContext));
        }
        this.glRiggingDataBuffer.BindUniform(renderContext, 2);
    }
    
    void UpdateRigged(final MeshFace meshFace, final float[] array, final DirectByteBuffer directByteBuffer, final int n) {
        meshFace.UpdateRigged(directByteBuffer, n, array, this.mappedJointMatrices);
    }
    
    void UpdateRiggedMatrices(final AvatarSkeleton avatarSkeleton) {
        if (this.mappedJointMatrices == null) {
            this.mappedJointMatrices = new float[this.joints.length * 16];
        }
        if (this.mappedJointVectors == null) {
            this.mappedJointVectors = new float[this.joints.length * 3 * 4];
        }
        final float[] jointWorldMatrix = avatarSkeleton.jointWorldMatrix;
        for (int i = 0; i < this.joints.length; ++i) {
            if (this.joints[i] >= 0) {
                Matrix.multiplyMM(this.mappedJointMatrices, i * 16, jointWorldMatrix, this.joints[i] * 16, this.jointMatrices, i * 16);
            }
            else {
                Matrix.setIdentityM(this.mappedJointMatrices, i * 16);
            }
            for (int j = 0; j < 3; ++j) {
                this.mappedJointVectors[i * 3 * 4 + j * 4 + 0] = this.mappedJointMatrices[i * 16 + j + 0];
                this.mappedJointVectors[i * 3 * 4 + j * 4 + 1] = this.mappedJointMatrices[i * 16 + j + 4];
                this.mappedJointVectors[i * 3 * 4 + j * 4 + 2] = this.mappedJointMatrices[i * 16 + j + 8];
                this.mappedJointVectors[i * 3 * 4 + j * 4 + 3] = this.mappedJointMatrices[i * 16 + j + 12];
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MeshRiggingData meshRiggingData = (MeshRiggingData)o;
        if (Arrays.equals(this.joints, meshRiggingData.joints)) {
            equals = Arrays.equals(this.jointMatrices, meshRiggingData.jointMatrices);
        }
        return equals;
    }
    
    final boolean fitsGL20() {
        return this.joints.length <= 52;
    }
    
    final boolean hasExtendedBones() {
        return this.hasExtendedBones;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
