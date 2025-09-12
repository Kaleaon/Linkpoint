// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLDefaultSkeleton;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.avatar.SLVisualParamID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarShapeParams, AnimationSkeletonData

public class AvatarSkeleton extends SLDefaultSkeleton
{
    private static class AttachmentPoint
    {

        final SLSkeletonBone bone;
        public final float matrix[];
        public final SLAttachmentPoint point;

        private AttachmentPoint(SLSkeletonBone slskeletonbone, SLAttachmentPoint slattachmentpoint)
        {
            matrix = new float[16];
            bone = slskeletonbone;
            point = slattachmentpoint;
        }

        AttachmentPoint(SLSkeletonBone slskeletonbone, SLAttachmentPoint slattachmentpoint, AttachmentPoint attachmentpoint)
        {
            this(slskeletonbone, slattachmentpoint);
        }
    }


    private final SLSkeletonBone animatedBones[] = new SLSkeletonBone[133];
    private final AttachmentPoint attachmentPoints[] = new AttachmentPoint[56];
    private final float bodySize = super.getBodySize();
    private final AtomicBoolean forceAnimate = new AtomicBoolean(true);
    private final boolean hasExtendedBones;
    private final Map partMorphParams = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/MeshIndex);
    private final float pelvisOffset;
    private final float pelvisToFoot = super.getPelvisToFoot();

    AvatarSkeleton(AvatarShapeParams avatarshapeparams, MeshJointTranslations meshjointtranslations, boolean flag)
    {
        hasExtendedBones = flag;
        prepareSkeleton();
        Object obj = bones.entrySet().iterator();
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)((Iterator) (obj)).next();
            int i = ((SLSkeletonBoneID)entry.getKey()).animatedIndex;
            if (i >= 0 && i < 133)
            {
                animatedBones[i] = (SLSkeletonBone)entry.getValue();
            }
        } while (true);
        obj = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID);
        SLBaseAvatar slbaseavatar = SLBaseAvatar.getInstance();
        applyJointTranslations(meshjointtranslations);
        pelvisOffset = meshjointtranslations.pelvisOffset;
        meshjointtranslations = MeshIndex.VALUES;
        int j = 0;
        for (int k = meshjointtranslations.length; j < k; j++)
        {
            MeshIndex meshindex = meshjointtranslations[j];
            float af[] = new float[slbaseavatar.getMeshEntry(meshindex).polyMesh.getNumMorphs()];
            Arrays.fill(af, 0.0F);
            partMorphParams.put(meshindex, af);
        }

        meshjointtranslations = SLSkeletonBoneID.VALUES;
        int l = meshjointtranslations.length;
        for (j = 0; j < l; j++)
        {
            Object obj1 = meshjointtranslations[j];
            ((Map) (obj)).put(obj1, new com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue(new LLVector3(), new LLVector3()));
            ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue)((Map) (obj)).get(obj1)).scale.set(1.0F, 1.0F, 1.0F);
            ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue)((Map) (obj)).get(obj1)).offset.set(0.0F, 0.0F, 0.0F);
        }

        l = avatarshapeparams.getParamCount();
        j = 0;
        do
        {
            if (j >= l)
            {
                break;
            }
            meshjointtranslations = SLAvatarParams.paramDefs[j];
            for (Iterator iterator = ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (meshjointtranslations)).params.iterator(); iterator.hasNext();)
            {
                com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)iterator.next();
                float f = ((float)avatarshapeparams.getParamValue(j) * (avatarparam.maxValue - avatarparam.minValue)) / 255F + avatarparam.minValue;
                ApplyMorphParam(slbaseavatar, ((Map) (obj)), avatarparam, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (meshjointtranslations)).name, f);
                if (avatarparam.drivenParams != null)
                {
                    Iterator iterator1 = avatarparam.drivenParams.iterator();
                    while (iterator1.hasNext()) 
                    {
                        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam drivenparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam)iterator1.next();
                        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet paramset = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenparam.drivenID));
                        if (paramset != null)
                        {
                            Iterator iterator2 = paramset.params.iterator();
                            while (iterator2.hasNext()) 
                            {
                                com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam1 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)iterator2.next();
                                float f1 = getDrivenWeight(f, avatarparam, drivenparam, avatarparam1);
                                ApplyMorphParam(slbaseavatar, ((Map) (obj)), avatarparam1, paramset.name, f1);
                            }
                        }
                    }
                }
            }

            j++;
        } while (true);
        avatarshapeparams = SLSkeletonBoneID.VALUES;
        l = avatarshapeparams.length;
        for (j = 0; j < l; j++)
        {
            meshjointtranslations = avatarshapeparams[j];
            ((SLSkeletonBone)bones.get(meshjointtranslations)).deformHierarchy(((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue)((Map) (obj)).get(meshjointtranslations)).offset, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue)((Map) (obj)).get(meshjointtranslations)).scale);
        }

        j = 0;
        while (j < 56) 
        {
            avatarshapeparams = SLAttachmentPoint.attachmentPoints[j];
            if (avatarshapeparams != null && !((SLAttachmentPoint) (avatarshapeparams)).isHUD)
            {
                meshjointtranslations = ((SLAttachmentPoint) (avatarshapeparams)).bone;
                if (meshjointtranslations != null)
                {
                    meshjointtranslations = (SLSkeletonBone)bones.get(meshjointtranslations);
                    if (meshjointtranslations != null)
                    {
                        attachmentPoints[j] = new AttachmentPoint(meshjointtranslations, avatarshapeparams, null);
                    }
                } else
                {
                    attachmentPoints[j] = new AttachmentPoint(null, avatarshapeparams, null);
                }
            }
            j++;
        }
        updateAttachmentMatrix();
    }

    private void ApplyMorphParam(SLBaseAvatar slbaseavatar, Map map, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam, SLVisualParamID slvisualparamid, float f)
    {
        if (avatarparam.morph && avatarparam.meshIndex != null)
        {
            float af[] = (float[])partMorphParams.get(avatarparam.meshIndex);
            if (af != null)
            {
                int i = slbaseavatar.getMeshEntry(avatarparam.meshIndex).polyMesh.getMorphIndex(slvisualparamid);
                if (i != -1)
                {
                    af[i] = af[i] + f;
                }
            }
        }
        if (avatarparam.skeletonParams != null)
        {
            slbaseavatar = avatarparam.skeletonParams.entrySet().iterator();
            do
            {
                if (!slbaseavatar.hasNext())
                {
                    break;
                }
                slvisualparamid = (java.util.Map.Entry)slbaseavatar.next();
                avatarparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue)map.get(slvisualparamid.getKey());
                slvisualparamid = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition)slvisualparamid.getValue();
                if (((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition) (slvisualparamid)).scale != null)
                {
                    ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue) (avatarparam)).scale.mulWeighted(((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition) (slvisualparamid)).scale, f);
                }
                if (((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition) (slvisualparamid)).offset != null)
                {
                    ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue) (avatarparam)).offset.addMul(((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition) (slvisualparamid)).offset, f);
                }
            } while (true);
        }
    }

    public static float getDrivenWeight(float f, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam drivenparam, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam1)
    {
        float f1;
        float f2;
        float f3;
        float f4;
        f1 = avatarparam.minValue;
        f2 = avatarparam.maxValue;
        f3 = avatarparam1.minValue;
        f4 = avatarparam1.maxValue;
        if (f > drivenparam.min1) goto _L2; else goto _L1
_L1:
        if (drivenparam.min1 != drivenparam.max1 || drivenparam.min1 > f1) goto _L4; else goto _L3
_L3:
        return f4;
_L4:
        return f3;
_L2:
        if (f <= drivenparam.max1)
        {
            return (f4 - f3) * ((f - drivenparam.min1) / (drivenparam.max1 - drivenparam.min1)) + f3;
        }
        if (f > drivenparam.max2)
        {
            if (f <= drivenparam.min2)
            {
                return f4 + (f3 - f4) * ((f - drivenparam.max2) / (drivenparam.min2 - drivenparam.max2));
            }
            if (drivenparam.max2 < f2)
            {
                return f3;
            }
        }
        if (true) goto _L3; else goto _L5
_L5:
    }

    private void updateAttachmentMatrix()
    {
        float af[] = new float[16];
        int i = 0;
        while (i < 56) 
        {
            AttachmentPoint attachmentpoint = attachmentPoints[i];
            if (attachmentpoint == null)
            {
                continue;
            }
            SLSkeletonBone slskeletonbone = attachmentpoint.bone;
            int j;
            if (slskeletonbone != null)
            {
                Matrix.translateM(af, 0, slskeletonbone.getGlobalMatrix(), 0, attachmentpoint.point.position.x * slskeletonbone.getScaleX(), attachmentpoint.point.position.y * slskeletonbone.getScaleY(), attachmentpoint.point.position.z * slskeletonbone.getScaleZ());
                Matrix.multiplyMM(attachmentpoint.matrix, 0, af, 0, attachmentpoint.point.rotation.getInverseMatrix(), 0);
            } else
            {
                Matrix.setIdentityM(af, 0);
                Matrix.translateM(af, 0, rootBone.getPositionX(), rootBone.getPositionY(), rootBone.getPositionZ());
                Matrix.translateM(af, 0, attachmentpoint.point.position.x, attachmentpoint.point.position.y, attachmentpoint.point.position.z);
                Matrix.multiplyMM(attachmentpoint.matrix, 0, af, 0, attachmentpoint.point.rotation.getInverseMatrix(), 0);
            }
            j = SLAttachmentPoint.attachmentPoints[i].nonHUDindex;
            if (j >= 0)
            {
                System.arraycopy(attachmentpoint.matrix, 0, jointWorldMatrix, (j + SLSkeletonBoneID.VALUES.length) * 16, 16);
            }
            i++;
        }
    }

    public void UpdateGlobalPositions(AnimationSkeletonData animationskeletondata)
    {
        super.UpdateGlobalPositions(animationskeletondata);
        updateAttachmentMatrix();
    }

    public SLSkeletonBone getAnimatedBone(int i)
    {
        return animatedBones[i];
    }

    final float[] getAttachmentMatrix(int i)
    {
        if (i >= 0 && i < attachmentPoints.length)
        {
            AttachmentPoint attachmentpoint = attachmentPoints[i];
            if (attachmentpoint != null)
            {
                return attachmentpoint.matrix;
            }
        }
        return null;
    }

    public final float getBodySize()
    {
        return bodySize;
    }

    final float[] getMorphParams(MeshIndex meshindex)
    {
        return (float[])partMorphParams.get(meshindex);
    }

    final float getPelvisOffset()
    {
        return pelvisOffset;
    }

    public final float getPelvisToFoot()
    {
        return pelvisToFoot;
    }

    public boolean hasExtendedBones()
    {
        return hasExtendedBones;
    }

    public boolean needForceAnimate()
    {
        return forceAnimate.getAndSet(false);
    }

    public void setForceAnimate()
    {
        forceAnimate.set(true);
    }
}
