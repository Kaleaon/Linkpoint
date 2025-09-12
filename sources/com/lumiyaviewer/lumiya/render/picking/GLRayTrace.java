// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class GLRayTrace
{
    public static class RayIntersectInfo
    {

        public final LLVector4 intersectPoint;
        public final float s;
        public final float t;

        public String toString()
        {
            return (new StringBuilder()).append("RayIntersectInfo{intersectPoint=").append(intersectPoint).append(", s=").append(s).append(", t=").append(t).append('}').toString();
        }

        RayIntersectInfo(LLVector4 llvector4, float f, float f1)
        {
            intersectPoint = llvector4;
            s = f;
            t = f1;
        }
    }


    public GLRayTrace()
    {
    }

    public static float getIntersectionDepth(RenderContext rendercontext, LLVector4 llvector4, float af[])
    {
        float af1[] = new float[8];
        Matrix.multiplyMV(af1, 0, af, 0, new float[] {
            llvector4.x, llvector4.y, llvector4.z, 1.0F
        }, 0);
        if (rendercontext.hasGL20)
        {
            Matrix.multiplyMV(af1, 4, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), af1, 0);
        } else
        {
            Matrix.multiplyMV(af1, 4, rendercontext.projectionMatrix.getMatrixData(), rendercontext.projectionMatrix.getMatrixDataOffset(), af1, 0);
        }
        return af1[6];
    }

    public static RayIntersectInfo intersect_RayTriangle(LLVector3 llvector3, LLVector3 llvector3_1, LLVector3 allvector3[], int i)
    {
        LLVector3 llvector3_2 = LLVector3.sub(allvector3[i + 1], allvector3[i + 0]);
        LLVector3 llvector3_3 = LLVector3.sub(allvector3[i + 2], allvector3[i + 0]);
        LLVector3 llvector3_4 = LLVector3.cross(llvector3_2, llvector3_3);
        if (llvector3_4.isZero())
        {
            return null;
        }
        llvector3_1 = LLVector3.sub(llvector3_1, llvector3);
        float f = -llvector3_4.dot(LLVector3.sub(llvector3, allvector3[i + 0]));
        float f1 = llvector3_4.dot(llvector3_1);
        if (Math.abs(f1) < 1E-07F)
        {
            return null;
        }
        f /= f1;
        if ((double)f < 0.0D)
        {
            return null;
        }
        llvector3_1 = new LLVector3(llvector3_1);
        llvector3_1.mul(f);
        llvector3_1.add(llvector3);
        f1 = llvector3_2.dot(llvector3_2);
        float f2 = llvector3_2.dot(llvector3_3);
        float f6 = llvector3_3.dot(llvector3_3);
        llvector3 = LLVector3.sub(llvector3_1, allvector3[i + 0]);
        float f3 = llvector3.dot(llvector3_2);
        float f4 = llvector3.dot(llvector3_3);
        float f5 = f2 * f2 - f1 * f6;
        if (Math.abs(f5) < 1E-07F)
        {
            return null;
        }
        f6 = (f2 * f4 - f6 * f3) / f5;
        if ((double)f6 < 0.0D || (double)f6 > 1.0D)
        {
            return null;
        }
        f1 = (f3 * f2 - f4 * f1) / f5;
        if ((double)f1 < 0.0D || (double)(f6 + f1) > 1.0D)
        {
            return null;
        } else
        {
            return new RayIntersectInfo(new LLVector4(llvector3_1.x, llvector3_1.y, llvector3_1.z, f), f6, f1);
        }
    }
}
