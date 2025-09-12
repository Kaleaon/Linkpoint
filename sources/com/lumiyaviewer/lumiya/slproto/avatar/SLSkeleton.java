// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLSkeletonBoneID, SLSkeletonBone

public class SLSkeleton
{

    public final Map bones = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID);
    public final float jointMatrix[];
    public final float jointWorldMatrix[];
    public SLSkeletonBone rootBone;
    private final SLSkeletonBone updateBones[];

    public SLSkeleton()
    {
        jointMatrix = new float[SLSkeletonBoneID.VALUES.length * 16];
        jointWorldMatrix = new float[(SLSkeletonBoneID.VALUES.length + 47) * 16];
        updateBones = new SLSkeletonBone[SLSkeletonBoneID.VALUES.length];
    }

    public void UpdateGlobalPositions(AnimationSkeletonData animationskeletondata)
    {
        SLSkeletonBone aslskeletonbone[] = updateBones;
        int i = 0;
        for (int j = aslskeletonbone.length; i < j; i++)
        {
            aslskeletonbone[i].updateGlobalPos(animationskeletondata, jointMatrix, jointWorldMatrix);
        }

    }

    protected void applyJointTranslations(MeshJointTranslations meshjointtranslations)
    {
        Iterator iterator = bones.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            float af[] = (float[])meshjointtranslations.jointTranslations.get(entry.getKey());
            if (af != null)
            {
                ((SLSkeletonBone)entry.getValue()).setPositionOverride(new LLVector3(af[0], af[1], af[2]));
            }
        } while (true);
    }

    public float getBodySize()
    {
        SLSkeletonBone slskeletonbone = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mPelvis);
        SLSkeletonBone slskeletonbone1 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mSkull);
        SLSkeletonBone slskeletonbone2 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mHead);
        SLSkeletonBone slskeletonbone3 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mNeck);
        SLSkeletonBone slskeletonbone4 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mChest);
        SLSkeletonBone slskeletonbone5 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mTorso);
        if (slskeletonbone != null && slskeletonbone1 != null && slskeletonbone2 != null && slskeletonbone3 != null && slskeletonbone4 != null && slskeletonbone5 != null)
        {
            double d = getPelvisToFoot();
            double d1 = Math.sqrt(2D);
            double d2 = slskeletonbone1.getPositionZ() * slskeletonbone2.getScaleZ();
            double d3 = slskeletonbone2.getPositionZ() * slskeletonbone3.getScaleZ();
            double d4 = slskeletonbone3.getPositionZ() * slskeletonbone4.getScaleZ();
            double d5 = slskeletonbone4.getPositionZ() * slskeletonbone5.getScaleZ();
            float f = slskeletonbone5.getPositionZ();
            return (float)((double)(slskeletonbone.getScaleZ() * f) + (d4 + (d + d1 * d2 + d3) + d5));
        } else
        {
            return 0.0F;
        }
    }

    public float getPelvisToFoot()
    {
        SLSkeletonBone slskeletonbone = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mPelvis);
        SLSkeletonBone slskeletonbone1 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mHipLeft);
        SLSkeletonBone slskeletonbone2 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mKneeLeft);
        SLSkeletonBone slskeletonbone3 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mAnkleLeft);
        SLSkeletonBone slskeletonbone4 = (SLSkeletonBone)bones.get(SLSkeletonBoneID.mFootLeft);
        if (slskeletonbone != null && slskeletonbone1 != null && slskeletonbone2 != null && slskeletonbone3 != null && slskeletonbone4 != null)
        {
            float f = slskeletonbone1.getPositionZ();
            float f1 = slskeletonbone.getScaleZ();
            float f2 = slskeletonbone2.getPositionZ();
            return f1 * f - slskeletonbone1.getScaleZ() * f2 - slskeletonbone3.getPositionZ() * slskeletonbone2.getScaleZ() - slskeletonbone4.getPositionZ() * slskeletonbone3.getScaleZ();
        } else
        {
            return 0.0F;
        }
    }

    protected void prepareSkeleton()
    {
        rootBone.prepareSkeleton(updateBones, 0);
    }
}
