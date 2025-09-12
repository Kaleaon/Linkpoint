package com.lumiyaviewer.lumiya.slproto.mesh;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.utils.InternPool;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class MeshRiggingData {
    private static final InternPool<MeshRiggingData> riggingDataPool = new InternPool<>();
    private GLLoadableBuffer glRiggingDataBuffer = null;
    private final boolean hasExtendedBones;
    private final int hashCode;
    @Nonnull
    private final float[] jointMatrices;
    @Nonnull
    private final int[] joints;
    private float[] mappedJointMatrices;
    private float[] mappedJointVectors;

    private MeshRiggingData(@Nonnull int[] iArr, @Nonnull float[] fArr, boolean z) {
        this.joints = iArr;
        this.jointMatrices = fArr;
        this.hasExtendedBones = z;
        this.hashCode = calcHashCode();
    }

    private DirectByteBuffer PrepareRiggingUniformBuffer(RenderContext renderContext) {
        RiggedMeshProgram30 riggedMeshProgram30 = renderContext.currentRiggedMeshProgram;
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(riggedMeshProgram30.uRiggingDataBlockSize);
        for (int i = 0; i < this.joints.length; i++) {
            directByteBuffer.putRawInt(riggedMeshProgram30.uJointMapOffset + (riggedMeshProgram30.uJointMapArrayStride * i), this.joints[i]);
        }
        for (int i2 = 0; i2 < this.joints.length; i2++) {
            int i3 = (riggedMeshProgram30.uJointMatricesOffset + (riggedMeshProgram30.uJointMatricesArrayStride * i2)) / 4;
            for (int i4 = 0; i4 < 4; i4++) {
                directByteBuffer.loadFromFloatArray(((riggedMeshProgram30.uJointMatricesColumnStride * i4) / 4) + i3, this.jointMatrices, (i2 * 16) + (i4 * 4), 4);
            }
        }
        return directByteBuffer;
    }

    private int calcHashCode() {
        return (Arrays.hashCode(this.joints) * 31) + Arrays.hashCode(this.jointMatrices);
    }

    public static MeshRiggingData create(@Nonnull int[] iArr, @Nonnull float[] fArr, boolean z) {
        return riggingDataPool.intern(new MeshRiggingData(iArr, fArr, z));
    }

    /* access modifiers changed from: package-private */
    public void PrepareInfluenceBuffers(RenderContext renderContext, float[] fArr) {
        GLES20.glUseProgram(renderContext.riggedMeshProgram.getHandle());
        GLES20.glUniformMatrix4fv(renderContext.riggedMeshProgram.uBindShapeMatrix, 1, false, fArr, 0);
        GLES20.glUniform4fv(renderContext.riggedMeshProgram.uJointVectors, this.mappedJointVectors.length / 4, this.mappedJointVectors, 0);
    }

    public void SetupBuffers30(RenderContext renderContext) {
        if (this.glRiggingDataBuffer == null) {
            this.glRiggingDataBuffer = new GLLoadableBuffer(PrepareRiggingUniformBuffer(renderContext));
        }
        this.glRiggingDataBuffer.BindUniform(renderContext, 2);
    }

    /* access modifiers changed from: package-private */
    public void UpdateRigged(MeshFace meshFace, float[] fArr, DirectByteBuffer directByteBuffer, int i) {
        meshFace.UpdateRigged(directByteBuffer, i, fArr, this.mappedJointMatrices);
    }

    /* access modifiers changed from: package-private */
    public void UpdateRiggedMatrices(AvatarSkeleton avatarSkeleton) {
        if (this.mappedJointMatrices == null) {
            this.mappedJointMatrices = new float[(this.joints.length * 16)];
        }
        if (this.mappedJointVectors == null) {
            this.mappedJointVectors = new float[(this.joints.length * 3 * 4)];
        }
        float[] fArr = avatarSkeleton.jointWorldMatrix;
        for (int i = 0; i < this.joints.length; i++) {
            if (this.joints[i] >= 0) {
                Matrix.multiplyMM(this.mappedJointMatrices, i * 16, fArr, this.joints[i] * 16, this.jointMatrices, i * 16);
            } else {
                Matrix.setIdentityM(this.mappedJointMatrices, i * 16);
            }
            for (int i2 = 0; i2 < 3; i2++) {
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 0] = this.mappedJointMatrices[(i * 16) + i2 + 0];
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 1] = this.mappedJointMatrices[(i * 16) + i2 + 4];
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 2] = this.mappedJointMatrices[(i * 16) + i2 + 8];
                this.mappedJointVectors[(i * 3 * 4) + (i2 * 4) + 3] = this.mappedJointMatrices[(i * 16) + i2 + 12];
            }
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MeshRiggingData meshRiggingData = (MeshRiggingData) obj;
        if (Arrays.equals(this.joints, meshRiggingData.joints)) {
            return Arrays.equals(this.jointMatrices, meshRiggingData.jointMatrices);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final boolean fitsGL20() {
        return this.joints.length <= 52;
    }

    /* access modifiers changed from: package-private */
    public final boolean hasExtendedBones() {
        return this.hasExtendedBones;
    }

    public int hashCode() {
        return this.hashCode;
    }
}
