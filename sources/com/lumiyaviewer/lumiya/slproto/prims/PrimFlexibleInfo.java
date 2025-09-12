// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimFlexibleParams

public class PrimFlexibleInfo
{
    private static class FlexibleSection
    {

        LLVector3 Direction;
        LLVector3 Position;
        LLQuaternion Rotation;
        LLVector3 Velocity;

        private FlexibleSection()
        {
        }

        FlexibleSection(FlexibleSection flexiblesection)
        {
            this();
        }
    }


    private static final float FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE = 0.99F;
    private static final long MIN_UPDATE_INTERVAL = 200L;
    private int NumSections;
    private long lastUpdateMillis;
    private volatile boolean needVertexBufferUpdate;
    private float sectionData[];
    private float sectionMatrices[];
    private FlexibleSection sections[];
    private GLLoadableBuffer vertexBuffer;

    public PrimFlexibleInfo()
    {
        NumSections = 0;
        needVertexBufferUpdate = false;
        vertexBuffer = null;
    }

    public boolean doFlexibleUpdate(PrimFlexibleParams primflexibleparams, float af[], int i, float f, float f1, float f2)
    {
        long l = System.currentTimeMillis();
        if (l < lastUpdateMillis + 200L)
        {
            return false;
        }
        if (primflexibleparams.NumFlexiSections != NumSections)
        {
            sections = null;
            sectionMatrices = null;
            sectionData = null;
            NumSections = primflexibleparams.NumFlexiSections;
        }
        if (NumSections == 0)
        {
            return false;
        }
        float f3 = (float)(l - lastUpdateMillis) / 1000F;
        lastUpdateMillis = l;
        boolean flag = false;
        if (sectionData == null)
        {
            sectionData = new float[OpenJPEG.getFlexiDataSize(NumSections)];
            sectionMatrices = new float[NumSections * 16];
            flag = true;
        }
        OpenJPEG.calcFlexiSections(sectionData, NumSections, sectionMatrices, af, i, f, f1, f2, f3 * 5F, primflexibleparams.Tension, primflexibleparams.AirFriction, primflexibleparams.Gravity, primflexibleparams.UserForce.x, primflexibleparams.UserForce.y, primflexibleparams.UserForce.z, flag);
        needVertexBufferUpdate = true;
        return true;
    }

    public boolean doFlexibleUpdateSlow(PrimFlexibleParams primflexibleparams, float af[], int i, float f, float f1, float f2)
    {
        long l = System.currentTimeMillis();
        if (l < lastUpdateMillis + 200L)
        {
            return false;
        }
        LLVector3 llvector3_1 = new LLVector3(af[i + 12], af[i + 13], af[i + 14]);
        LLVector3 llvector3 = new LLVector3(f, f1, f2);
        float af2[] = new float[32];
        Matrix.invertM(af2, 0, af, i);
        LLQuaternion llquaternion = new LLQuaternion(af2);
        if (primflexibleparams.NumFlexiSections != NumSections)
        {
            sections = null;
            sectionMatrices = null;
            NumSections = primflexibleparams.NumFlexiSections;
        }
        if (NumSections == 0)
        {
            return false;
        }
        f = (float)(l - lastUpdateMillis) / 1000F;
        lastUpdateMillis = l;
        f2 = f * 5F;
        LLQuaternion llquaternion1 = new LLQuaternion(llquaternion);
        af = new LLVector3(LLVector3.z_axis);
        af.mul(llquaternion1);
        float f3 = llvector3.z / (float)NumSections;
        LLVector3 llvector3_2 = new LLVector3(af);
        llvector3_2.mul(llvector3.z / 2.0F);
        llvector3_2 = LLVector3.sub(llvector3_1, llvector3_2);
        if (sections == null)
        {
            sections = new FlexibleSection[NumSections];
            for (i = 0; i < NumSections; i++)
            {
                sections[i] = new FlexibleSection(null);
                sections[i].Position = new LLVector3(llvector3_2);
                sections[i].Position.addMul(af, (float)i * f3);
                sections[i].Direction = new LLVector3(af);
                sections[i].Rotation = new LLQuaternion(llquaternion);
                sections[i].Velocity = new LLVector3();
            }

        }
        sections[0].Position.set(llvector3_2);
        sections[0].Direction.set(af);
        sections[0].Rotation.set(llquaternion);
        f = primflexibleparams.Tension * 0.1F * (1.0F - (float)Math.pow(0.84999999999999998D, (double)f2 * 30D));
        f1 = f;
        if (f > 0.99F)
        {
            f1 = 0.99F;
        }
        f = (float)Math.pow(10D, (primflexibleparams.AirFriction * 2.0F + 1.0F) * f2);
        float f4;
        float f5;
        float f6;
        LLVector3 llvector3_3;
        LLQuaternion llquaternion2;
        LLQuaternion llquaternion3;
        LLQuaternion llquaternion4;
        if (f <= 1.0F)
        {
            f = 1.0F;
        }
        f4 = 1.0F / f;
        f5 = (float)Math.atan(2.0F * f3);
        f6 = f3 * f2;
        llvector3_2 = new LLVector3();
        llvector3_3 = new LLVector3();
        llquaternion2 = new LLQuaternion();
        llquaternion3 = new LLQuaternion();
        llquaternion4 = new LLQuaternion();
        i = 1;
        while (i < NumSections) 
        {
            llvector3_2.set(sections[i].Position);
            af = sections[i].Position;
            af.z = ((LLVector3) (af)).z - primflexibleparams.Gravity * f6;
            sections[i].Position.addMul(primflexibleparams.UserForce, f6);
            LLVector3 llvector3_4 = sections[i - 1].Position;
            LLVector3 llvector3_5 = sections[i - 1].Direction;
            LLVector3 llvector3_6;
            if (i == 1)
            {
                af = sections[0].Direction;
            } else
            {
                af = sections[i - 2].Direction;
            }
            llvector3_6 = LLVector3.sub(sections[i].Position, llvector3_4);
            af = new LLVector3(af);
            af.mul(f3);
            af.sub(llvector3_6);
            sections[i].Position.addMul(af, f1);
            sections[i].Position.addMul(sections[i].Velocity, f4);
            sections[i].Direction.setSub(sections[i].Position, llvector3_4);
            sections[i].Direction.normVec();
            af = LLQuaternion.shortestArc(llvector3_5, sections[i].Direction);
            f2 = af.getAngleAxis(llvector3_3);
            f = f2;
            if (f2 > 3.141593F)
            {
                f = f2 - 6.283185F;
            }
            f2 = f;
            if (f < -3.141593F)
            {
                f2 = f + 6.283185F;
            }
            if (f2 > f5)
            {
                af.setQuat(f5, llvector3_3);
            } else
            if (f2 < -f5)
            {
                af.setQuat(-f5, llvector3_3);
            }
            llquaternion2.setMul(llquaternion1, af);
            llquaternion1.set(llquaternion2);
            sections[i].Direction.set(llvector3_5);
            sections[i].Direction.mul(af);
            sections[i].Position.set(llvector3_4);
            sections[i].Position.addMul(sections[i].Direction, f3);
            sections[i].Rotation.set(llquaternion2);
            if (i > 1)
            {
                llquaternion3.setQuat(f2 / 2.0F, llvector3_3);
                llquaternion4.setMul(sections[i - 1].Rotation, llquaternion3);
                sections[i - 1].Rotation.set(llquaternion4);
            }
            sections[i].Velocity.setSub(sections[i].Position, llvector3_2);
            if (sections[i].Velocity.magVecSquared() > 1.0F)
            {
                sections[i].Velocity.normVec();
            }
            i++;
        }
        primflexibleparams = new float[32];
        Matrix.setIdentityM(primflexibleparams, 16);
        Matrix.scaleM(primflexibleparams, 16, 1.0F / llvector3.x, 1.0F / llvector3.y, 1.0F / llvector3.z);
        Matrix.multiplyMM(primflexibleparams, 0, primflexibleparams, 16, llquaternion.getMatrix(), 0);
        Matrix.translateM(primflexibleparams, 0, -llvector3_1.x, -llvector3_1.y, -llvector3_1.z);
        if (sectionMatrices == null)
        {
            sectionMatrices = new float[NumSections * 16];
        }
        af = new float[8];
        for (i = 0; i < NumSections; i++)
        {
            af[0] = sections[i].Position.x;
            af[1] = sections[i].Position.y;
            af[2] = sections[i].Position.z;
            af[3] = 1.0F;
            Matrix.multiplyMV(af, 4, primflexibleparams, 0, af, 0);
            f = (float)i / (float)NumSections - 0.5F;
            float af1[] = new float[32];
            Matrix.setIdentityM(af1, 16);
            Matrix.translateM(af1, 16, af[4], af[5], af[6] - f);
            Matrix.translateM(af1, 16, 0.0F, 0.0F, f);
            Matrix.scaleM(af1, 16, 1.0F / llvector3.x, 1.0F / llvector3.y, 1.0F / llvector3.z);
            Matrix.multiplyMM(af1, 0, af1, 16, llquaternion.getMatrix(), 0);
            Matrix.multiplyMM(af1, 16, af1, 0, sections[i].Rotation.getInverseMatrix(), 0);
            Matrix.scaleM(af1, 16, llvector3.x, llvector3.y, llvector3.z);
            Matrix.translateM(af1, 16, 0.0F, 0.0F, -f);
            System.arraycopy(af1, 16, sectionMatrices, i * 16, 16);
        }

        needVertexBufferUpdate = true;
        return true;
    }

    public GLLoadableBuffer getFlexedVertexBuffer(RenderContext rendercontext, GLLoadableBuffer glloadablebuffer, int i)
    {
        if (sectionMatrices != null)
        {
            if (needVertexBufferUpdate)
            {
                DirectByteBuffer directbytebuffer = glloadablebuffer.getRawBuffer();
                if (vertexBuffer == null)
                {
                    vertexBuffer = new GLLoadableBuffer(new DirectByteBuffer(directbytebuffer));
                }
                OpenJPEG.applyFlexibleMorph(vertexBuffer.getRawBuffer().asByteBuffer(), directbytebuffer.asByteBuffer(), i, sectionMatrices);
                vertexBuffer.Reload(rendercontext);
                needVertexBufferUpdate = false;
            }
            if (vertexBuffer != null)
            {
                return vertexBuffer;
            }
        }
        return glloadablebuffer;
    }

    public float[] getMatrices()
    {
        return sectionMatrices;
    }
}
