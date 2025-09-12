// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimPathParams, PrimMath

public class PrimPath
{
    public static class PathPoint
    {

        float TexT;
        LLVector3 pos;
        LLQuaternion rot;
        LLVector2 scale;

        public PathPoint()
        {
            pos = new LLVector3();
            scale = new LLVector2();
            rot = new LLQuaternion();
            TexT = 0.0F;
        }
    }


    private static final int MIN_DETAIL_FACES = 6;
    private static float tableScale[] = {
        1.0F, 1.0F, 1.0F, 0.5F, 0.707107F, 0.53F, 0.525F, 0.5F
    };
    boolean Dirty;
    boolean Open;
    ArrayList Path;
    float Step;
    int Total;

    public PrimPath()
    {
        Open = false;
        Total = 0;
        Dirty = true;
        Step = 1.0F;
        Path = new ArrayList();
    }

    private void genNGon(PrimPathParams primpathparams, int i, float f, float f1, float f2)
    {
        float f12 = primpathparams.Revolutions;
        float f9 = primpathparams.Skew;
        float f13 = Math.abs(f9);
        float f10 = primpathparams.ScaleX * (1.0F - f13);
        float f11 = primpathparams.ScaleY;
        float f3 = 1.0F - primpathparams.TaperX;
        float f4 = 1.0F - primpathparams.TaperY;
        double d;
        float f5;
        float f7;
        float f8;
        float f14;
        float f15;
        float f16;
        float f17;
        LLQuaternion llquaternion;
        LLQuaternion llquaternion1;
        LLVector3 llvector3;
        PathPoint pathpoint;
        LLVector3 llvector3_1;
        boolean flag;
        if (f3 > 1.0F)
        {
            f = 1.0F;
            f3 = 2.0F - f3;
        } else
        {
            f = f3;
            f3 = 1.0F;
        }
        if (f4 > 1.0F)
        {
            float f6 = 1.0F;
            f5 = 2.0F - f4;
            f4 = f6;
        } else
        {
            f5 = 1.0F;
        }
        f7 = 0.5F;
        if (i < 8)
        {
            f7 = tableScale[i];
        }
        f7 *= 1.0F - f11;
        f14 = primpathparams.RadiusOffset;
        if (f14 < 0.0F)
        {
            f8 = (f14 + 1.0F) * f7;
        } else
        {
            f8 = f7;
            f7 = (1.0F - f14) * f7;
        }
        break MISSING_BLOCK_LABEL_135;
        if (primpathparams.End * f1 - primpathparams.Begin < 1.0F || f13 > 0.001F || Math.abs(f - f3) > 0.001F || Math.abs(f4 - f5) > 0.001F)
        {
            flag = true;
        } else
        if (Math.abs(f7 - f8) > 0.001F)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        Open = flag;
        llquaternion = new LLQuaternion();
        llquaternion1 = new LLQuaternion();
        llvector3 = new LLVector3(1.0F, 0.0F, 0.0F);
        f13 = primpathparams.TwistBegin * f2;
        f2 = primpathparams.TwistEnd * f2;
        f14 = 1.0F / (float)i;
        f1 = primpathparams.Begin;
        pathpoint = new PathPoint();
        f15 = 6.283185F * f12 * f1;
        f16 = (float)(Math.sin(f15) * (double)PrimMath.lerp(f8, f7, f1));
        f17 = (float)(Math.cos(f15) * (double)PrimMath.lerp(f8, f7, f1));
        pathpoint.pos.set(PrimMath.lerp(0.0F, primpathparams.ShearX, f16) + 0.0F + PrimMath.lerp(-f9, f9, f1) * 0.5F, f17 + PrimMath.lerp(0.0F, primpathparams.ShearY, f16), f16);
        pathpoint.scale.x = PrimMath.lerp(f3, f, f1) * f10;
        pathpoint.scale.y = PrimMath.lerp(f5, f4, f1) * f11;
        pathpoint.TexT = f1;
        llquaternion.setQuat(PrimMath.lerp(f13, f2, f1) * 2.0F * 3.141593F - 3.141593F, 0.0F, 0.0F, 1.0F);
        llquaternion1.setQuat(f15, llvector3);
        pathpoint.rot.setMul(llquaternion, llquaternion1);
        Path.add(pathpoint);
        for (f1 = (float)(int)((f1 + f14) * (float)i) / (float)i; f1 < primpathparams.End; f1 += f14)
        {
            pathpoint = new PathPoint();
            f15 = 6.283185F * f12 * f1;
            f16 = (float)(Math.cos(f15) * (double)PrimMath.lerp(f8, f7, f1));
            f17 = (float)(Math.sin(f15) * (double)PrimMath.lerp(f8, f7, f1));
            pathpoint.pos.set(PrimMath.lerp(0.0F, primpathparams.ShearX, f17) + 0.0F + PrimMath.lerp(-f9, f9, f1) * 0.5F, f16 + PrimMath.lerp(0.0F, primpathparams.ShearY, f17), f17);
            pathpoint.scale.x = PrimMath.lerp(f3, f, f1) * f10;
            pathpoint.scale.y = PrimMath.lerp(f5, f4, f1) * f11;
            pathpoint.TexT = f1;
            llquaternion.setQuat(PrimMath.lerp(f13, f2, f1) * 2.0F * 3.141593F - 3.141593F, 0.0F, 0.0F, 1.0F);
            llquaternion1.setQuat(f15, llvector3);
            pathpoint.rot.setMul(llquaternion, llquaternion1);
            Path.add(pathpoint);
        }

        break MISSING_BLOCK_LABEL_753;
        f1 = primpathparams.End;
        pathpoint = new PathPoint();
        f12 = f12 * 6.283185F * f1;
        f14 = (float)(Math.cos(f12) * (double)PrimMath.lerp(f8, f7, f1));
        d = Math.sin(f12);
        f7 = (float)((double)PrimMath.lerp(f8, f7, f1) * d);
        llvector3_1 = pathpoint.pos;
        f8 = PrimMath.lerp(0.0F, primpathparams.ShearX, f7);
        llvector3_1.set(PrimMath.lerp(-f9, f9, f1) * 0.5F + (f8 + 0.0F), f14 + PrimMath.lerp(0.0F, primpathparams.ShearY, f7), f7);
        pathpoint.scale.x = PrimMath.lerp(f3, f, f1) * f10;
        pathpoint.scale.y = PrimMath.lerp(f5, f4, f1) * f11;
        pathpoint.TexT = f1;
        llquaternion.setQuat(PrimMath.lerp(f13, f2, f1) * 2.0F * 3.141593F - 3.141593F, 0.0F, 0.0F, 1.0F);
        llquaternion1.setQuat(f12, llvector3);
        pathpoint.rot.setMul(llquaternion, llquaternion1);
        Path.add(pathpoint);
        Total = Path.size();
        return;
    }

    public boolean generate(PrimPathParams primpathparams, float f, int i, boolean flag, int j)
    {
        float f1;
        if (!Dirty && flag ^ true)
        {
            return false;
        }
        f1 = f;
        if (f < 0.0F)
        {
            f1 = 0.0F;
        }
        Dirty = false;
        Path.clear();
        Open = true;
        primpathparams.CurveType & 0xf0;
        JVM INSTR lookupswitch 4: default 96
    //                   16: 96
    //                   32: 337
    //                   48: 415
    //                   64: 531;
           goto _L1 _L1 _L2 _L3 _L4
_L1:
        int k = (int)Math.floor(Math.abs(primpathparams.TwistBegin - primpathparams.TwistEnd) * 3.5F * (f1 - 0.5F)) + 2;
        j = k;
        if (k < i + 2)
        {
            j = i + 2;
        }
        Step = 1.0F / (float)(j - 1);
        Path.ensureCapacity(j);
        LLVector2 llvector2 = primpathparams.getBeginScale();
        LLVector2 llvector2_1 = primpathparams.getEndScale();
        for (i = 0; i < j; i++)
        {
            f = PrimMath.lerp(primpathparams.Begin, primpathparams.End, (float)i * Step);
            PathPoint pathpoint1 = new PathPoint();
            pathpoint1.pos.set(PrimMath.lerp(0.0F, primpathparams.ShearX, f), PrimMath.lerp(0.0F, primpathparams.ShearY, f), f - 0.5F);
            pathpoint1.rot.setQuat(PrimMath.lerp(primpathparams.TwistBegin * 3.141593F, primpathparams.TwistEnd * 3.141593F, f), 0.0F, 0.0F, 1.0F);
            pathpoint1.scale.x = PrimMath.lerp(llvector2.x, llvector2_1.x, f);
            pathpoint1.scale.y = PrimMath.lerp(llvector2.y, llvector2_1.y, f);
            pathpoint1.TexT = f;
            Path.add(pathpoint1);
        }

        break; /* Loop/switch isn't completed */
_L2:
        i = (int)Math.floor(Math.floor(Math.abs(primpathparams.TwistBegin - primpathparams.TwistEnd) * 3.5F * (f1 - 0.5F) + 6F * f1) * (double)primpathparams.Revolutions);
        if (flag)
        {
            i = j;
        }
        genNGon(primpathparams, i, 0.0F, 1.0F, 1.0F);
_L6:
        if (primpathparams.TwistEnd != primpathparams.TwistBegin)
        {
            Open = true;
        }
        return true;
_L3:
        if (primpathparams.End - primpathparams.Begin >= 0.99F && primpathparams.ScaleX >= 0.99F)
        {
            Open = false;
        }
        genNGon(primpathparams, (int)Math.floor(6F * f1), 0.0F, 1.0F, 1.0F);
        f = 1.0F / (float)Path.size();
        f = 0.5F;
        i = 0;
        while (i < Path.size()) 
        {
            ((PathPoint)Path.get(i)).pos.x = f;
            if (f == 0.5F)
            {
                f = -0.5F;
            } else
            {
                f = 0.5F;
            }
            i++;
        }
        if (true)
        {
            continue; /* Loop/switch isn't completed */
        }
_L4:
        Step = 1.0F / (float)4;
        Path.ensureCapacity(5);
        i = 0;
        while (i < 5) 
        {
            f = (float)i * Step;
            PathPoint pathpoint = new PathPoint();
            pathpoint.pos.set(0.0F, PrimMath.lerp(0.0F, (float)(-Math.sin(primpathparams.TwistEnd * 3.141593F * f) * 0.5D), f), PrimMath.lerp(-0.5F, (float)(Math.cos(primpathparams.TwistEnd * 3.141593F * f) * 0.5D), f));
            pathpoint.scale.x = PrimMath.lerp(1.0F, primpathparams.ScaleX, f);
            pathpoint.scale.y = PrimMath.lerp(1.0F, primpathparams.ScaleY, f);
            pathpoint.TexT = f;
            pathpoint.rot.setQuat(f * (primpathparams.TwistEnd * 3.141593F), 1.0F, 0.0F, 0.0F);
            Path.add(pathpoint);
            i++;
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

}
