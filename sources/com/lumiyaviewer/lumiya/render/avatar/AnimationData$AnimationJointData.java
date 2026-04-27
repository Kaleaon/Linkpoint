// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.LittleEndianDataInputStream;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationData

private static class e
{

    private final int Priority;
    private final e posKeyframes[];
    private final e rotKeyframes[];

    static int _2D_get0(e e)
    {
        return e.Priority;
    }

    private static boolean animateArray(float f, float f1, Object obj, Priority apriority[])
    {
        if (apriority.length == 1)
        {
            apriority[0].etTransform(obj);
            return true;
        }
        for (int i = 0; i < apriority.length; i++)
        {
            if (f1 <= apriority[i].ime)
            {
                if (f1 == apriority[i].ime)
                {
                    apriority[i].etTransform(obj);
                    return true;
                }
                int k = i - 1;
                int j = k;
                if (k < 0)
                {
                    j = 0;
                }
                if (j == i)
                {
                    apriority[i].etTransform(obj);
                    return true;
                }
                float f4 = apriority[i].ime;
                float f3 = apriority[j].ime;
                float f2 = f3;
                if (f3 > f4)
                {
                    f2 = f3 - f;
                }
                if (f2 == f4)
                {
                    apriority[i].etTransform(obj);
                    return true;
                } else
                {
                    f = (f4 - f1) / (f4 - f2);
                    f1 = (f1 - f2) / (f4 - f2);
                    apriority[j].etInterpolated(obj, f, apriority[i], f1);
                    return true;
                }
            }
        }

        return false;
    }

    private static float uint16ToFloat(int i, float f, float f1)
    {
        float f3 = i;
        float f2 = f1 - f;
        f1 = f3 * 1.525902E-05F * f2 + f;
        f = f1;
        if (Math.abs(f1) < f2 * 1.525902E-05F)
        {
            f = 0.0F;
        }
        return f;
    }

    void animate(SLSkeletonBone slskeletonbone, float f, float f1, LLQuaternion allquaternion[], LLVector3 allvector3[], int i, float f2, 
            float af[], float af1[], LLQuaternion llquaternion, LLVector3 llvector3)
    {
        if (posKeyframes.length != 0)
        {
            float f3 = af1[i] * f2;
            animateArray(f, f1, llvector3, posKeyframes);
            if (slskeletonbone != null && slskeletonbone.boneID != SLSkeletonBoneID.mPelvis)
            {
                llvector3.sub(slskeletonbone.getBasePosition());
            }
            allvector3[i].addMul(llvector3, f3);
            af1[i] = af1[i] - f3;
        }
        if (rotKeyframes.length != 0)
        {
            f2 = af[i] * f2;
            animateArray(f, f1, llquaternion, rotKeyframes);
            allquaternion[i].addMul(llquaternion, f2);
            af[i] = af[i] - f2;
        }
    }

    public String toString()
    {
        boolean flag = false;
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("Priority ").append(Priority);
        stringbuilder.append(", pos frames ").append(posKeyframes.length).append("[");
        Object aobj[] = posKeyframes;
        int k = aobj.length;
        for (int i = 0; i < k; i++)
        {
            stringbuilder.append(aobj[i].toString());
        }

        stringbuilder.append("], rot frames ").append(rotKeyframes.length).append("[");
        aobj = rotKeyframes;
        k = aobj.length;
        for (int j = ((flag) ? 1 : 0); j < k; j++)
        {
            stringbuilder.append(aobj[j].toString());
        }

        stringbuilder.append("]");
        return stringbuilder.toString();
    }

    e(LittleEndianDataInputStream littleendiandatainputstream, float f)
        throws IOException
    {
        int i;
label0:
        {
            super();
            Priority = littleendiandatainputstream.readInt();
            int j = littleendiandatainputstream.readInt();
            if (j >= 0)
            {
                i = j;
                if (j <= 10000)
                {
                    break label0;
                }
            }
            i = 0;
        }
label1:
        {
            rotKeyframes = new e[i];
            for (int k = 0; k < i; k++)
            {
                float f1 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), 0.0F, f);
                float f3 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                float f5 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                float f7 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                rotKeyframes[k] = new e(f1, LLQuaternion.unpackFromVector3(new LLVector3(f3, f5, f7)));
            }

            int l = littleendiandatainputstream.readInt();
            if (l >= 0)
            {
                i = l;
                if (l <= 10000)
                {
                    break label1;
                }
            }
            i = 0;
        }
        posKeyframes = new e[i];
        for (int i1 = 0; i1 < i; i1++)
        {
            float f2 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), 0.0F, f);
            float f4 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
            float f6 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
            float f8 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
            posKeyframes[i1] = new e(f2, new LLVector3(f4, f6, f8));
        }

    }
}
