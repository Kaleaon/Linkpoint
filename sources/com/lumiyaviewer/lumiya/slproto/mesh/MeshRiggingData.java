// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.mesh;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.utils.InternPool;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.Arrays;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.mesh:
//            MeshFace

public class MeshRiggingData
{

    private static final InternPool riggingDataPool = new InternPool();
    private GLLoadableBuffer glRiggingDataBuffer;
    private final boolean hasExtendedBones;
    private final int hashCode = calcHashCode();
    private final float jointMatrices[];
    private final int joints[];
    private float mappedJointMatrices[];
    private float mappedJointVectors[];

    private MeshRiggingData(int ai[], float af[], boolean flag)
    {
        glRiggingDataBuffer = null;
        joints = ai;
        jointMatrices = af;
        hasExtendedBones = flag;
    }

    private DirectByteBuffer PrepareRiggingUniformBuffer(RenderContext rendercontext)
    {
        rendercontext = rendercontext.currentRiggedMeshProgram;
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(((RiggedMeshProgram30) (rendercontext)).uRiggingDataBlockSize);
        for (int i = 0; i < joints.length; i++)
        {
            directbytebuffer.putRawInt(((RiggedMeshProgram30) (rendercontext)).uJointMapOffset + ((RiggedMeshProgram30) (rendercontext)).uJointMapArrayStride * i, joints[i]);
        }

        for (int j = 0; j < joints.length; j++)
        {
            int l = (((RiggedMeshProgram30) (rendercontext)).uJointMatricesOffset + ((RiggedMeshProgram30) (rendercontext)).uJointMatricesArrayStride * j) / 4;
            for (int k = 0; k < 4; k++)
            {
                directbytebuffer.loadFromFloatArray((((RiggedMeshProgram30) (rendercontext)).uJointMatricesColumnStride * k) / 4 + l, jointMatrices, j * 16 + k * 4, 4);
            }

        }

        return directbytebuffer;
    }

    private int calcHashCode()
    {
        return Arrays.hashCode(joints) * 31 + Arrays.hashCode(jointMatrices);
    }

    public static MeshRiggingData create(int ai[], float af[], boolean flag)
    {
        return (MeshRiggingData)riggingDataPool.intern(new MeshRiggingData(ai, af, flag));
    }

    void PrepareInfluenceBuffers(RenderContext rendercontext, float af[])
    {
        GLES20.glUseProgram(rendercontext.riggedMeshProgram.getHandle());
        GLES20.glUniformMatrix4fv(rendercontext.riggedMeshProgram.uBindShapeMatrix, 1, false, af, 0);
        GLES20.glUniform4fv(rendercontext.riggedMeshProgram.uJointVectors, mappedJointVectors.length / 4, mappedJointVectors, 0);
    }

    public void SetupBuffers30(RenderContext rendercontext)
    {
        if (glRiggingDataBuffer == null)
        {
            glRiggingDataBuffer = new GLLoadableBuffer(PrepareRiggingUniformBuffer(rendercontext));
        }
        glRiggingDataBuffer.BindUniform(rendercontext, 2);
    }

    void UpdateRigged(MeshFace meshface, float af[], DirectByteBuffer directbytebuffer, int i)
    {
        meshface.UpdateRigged(directbytebuffer, i, af, mappedJointMatrices);
    }

    void UpdateRiggedMatrices(AvatarSkeleton avatarskeleton)
    {
        if (mappedJointMatrices == null)
        {
            mappedJointMatrices = new float[joints.length * 16];
        }
        if (mappedJointVectors == null)
        {
            mappedJointVectors = new float[joints.length * 3 * 4];
        }
        avatarskeleton = avatarskeleton.jointWorldMatrix;
        for (int i = 0; i < joints.length; i++)
        {
            int j;
            if (joints[i] >= 0)
            {
                Matrix.multiplyMM(mappedJointMatrices, i * 16, avatarskeleton, joints[i] * 16, jointMatrices, i * 16);
            } else
            {
                Matrix.setIdentityM(mappedJointMatrices, i * 16);
            }
            for (j = 0; j < 3; j++)
            {
                mappedJointVectors[i * 3 * 4 + j * 4 + 0] = mappedJointMatrices[i * 16 + j + 0];
                mappedJointVectors[i * 3 * 4 + j * 4 + 1] = mappedJointMatrices[i * 16 + j + 4];
                mappedJointVectors[i * 3 * 4 + j * 4 + 2] = mappedJointMatrices[i * 16 + j + 8];
                mappedJointVectors[i * 3 * 4 + j * 4 + 3] = mappedJointMatrices[i * 16 + j + 12];
            }

        }

    }

    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        obj = (MeshRiggingData)obj;
        if (Arrays.equals(joints, ((MeshRiggingData) (obj)).joints))
        {
            flag = Arrays.equals(jointMatrices, ((MeshRiggingData) (obj)).jointMatrices);
        }
        return flag;
    }

    final boolean fitsGL20()
    {
        return joints.length <= 52;
    }

    final boolean hasExtendedBones()
    {
        return hasExtendedBones;
    }

    public int hashCode()
    {
        return hashCode;
    }

}
