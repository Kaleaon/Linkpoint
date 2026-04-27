// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import java.util.EnumMap;
import java.util.Map;

public class SLSkeleton
{
    public final Map<SLSkeletonBoneID, SLSkeletonBone> bones;
    public final float[] jointMatrix;
    public final float[] jointWorldMatrix;
    public SLSkeletonBone rootBone;
    private final SLSkeletonBone[] updateBones;
    
    public SLSkeleton() {
        this.bones = new EnumMap<SLSkeletonBoneID, SLSkeletonBone>(SLSkeletonBoneID.class);
        this.jointMatrix = new float[SLSkeletonBoneID.VALUES.length * 16];
        this.jointWorldMatrix = new float[(SLSkeletonBoneID.VALUES.length + 47) * 16];
        this.updateBones = new SLSkeletonBone[SLSkeletonBoneID.VALUES.length];
    }
    
    public void UpdateGlobalPositions(final AnimationSkeletonData animationSkeletonData) {
        final SLSkeletonBone[] updateBones = this.updateBones;
        for (int i = 0; i < updateBones.length; ++i) {
            updateBones[i].updateGlobalPos(animationSkeletonData, this.jointMatrix, this.jointWorldMatrix);
        }
    }
    
    protected void applyJointTranslations(final MeshJointTranslations meshJointTranslations) {
        for (final Map.Entry<Object, V> entry : this.bones.entrySet()) {
            final float[] array = meshJointTranslations.jointTranslations.get(entry.getKey());
            if (array != null) {
                ((SLSkeletonBone)entry.getValue()).setPositionOverride(new LLVector3(array[0], array[1], array[2]));
            }
        }
    }
    
    public float getBodySize() {
        final SLSkeletonBone slSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis);
        final SLSkeletonBone slSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mSkull);
        final SLSkeletonBone slSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mHead);
        final SLSkeletonBone slSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mNeck);
        final SLSkeletonBone slSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mChest);
        final SLSkeletonBone slSkeletonBone6 = this.bones.get(SLSkeletonBoneID.mTorso);
        if (slSkeletonBone != null && slSkeletonBone2 != null && slSkeletonBone3 != null && slSkeletonBone4 != null && slSkeletonBone5 != null && slSkeletonBone6 != null) {
            return (float)(slSkeletonBone.getScaleZ() * slSkeletonBone6.getPositionZ() + (slSkeletonBone4.getPositionZ() * slSkeletonBone5.getScaleZ() + (this.getPelvisToFoot() + Math.sqrt(2.0) * (slSkeletonBone2.getPositionZ() * slSkeletonBone3.getScaleZ()) + slSkeletonBone3.getPositionZ() * slSkeletonBone4.getScaleZ()) + slSkeletonBone5.getPositionZ() * slSkeletonBone6.getScaleZ()));
        }
        return 0.0f;
    }
    
    public float getPelvisToFoot() {
        final SLSkeletonBone slSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis);
        final SLSkeletonBone slSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mHipLeft);
        final SLSkeletonBone slSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mKneeLeft);
        final SLSkeletonBone slSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mAnkleLeft);
        final SLSkeletonBone slSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mFootLeft);
        if (slSkeletonBone != null && slSkeletonBone2 != null && slSkeletonBone3 != null && slSkeletonBone4 != null && slSkeletonBone5 != null) {
            return slSkeletonBone.getScaleZ() * slSkeletonBone2.getPositionZ() - slSkeletonBone2.getScaleZ() * slSkeletonBone3.getPositionZ() - slSkeletonBone4.getPositionZ() * slSkeletonBone3.getScaleZ() - slSkeletonBone5.getPositionZ() * slSkeletonBone4.getScaleZ();
        }
        return 0.0f;
    }
    
    protected void prepareSkeleton() {
        this.rootBone.prepareSkeleton(this.updateBones, 0);
    }
}
