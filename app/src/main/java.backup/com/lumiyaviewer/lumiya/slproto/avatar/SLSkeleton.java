package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.EnumMap;
import java.util.Map;

public class SLSkeleton {
    public final Map<SLSkeletonBoneID, SLSkeletonBone> bones = new EnumMap(SLSkeletonBoneID.class);
    public final float[] jointMatrix = new float[(SLSkeletonBoneID.VALUES.length * 16)];
    public final float[] jointWorldMatrix = new float[((SLSkeletonBoneID.VALUES.length + 47) * 16)];
    public SLSkeletonBone rootBone;
    private final SLSkeletonBone[] updateBones = new SLSkeletonBone[SLSkeletonBoneID.VALUES.length];

    public void UpdateGlobalPositions(AnimationSkeletonData animationSkeletonData) {
        for (SLSkeletonBone updateGlobalPos : this.updateBones) {
            updateGlobalPos.updateGlobalPos(animationSkeletonData, this.jointMatrix, this.jointWorldMatrix);
        }
    }

    /* access modifiers changed from: protected */
    public void applyJointTranslations(MeshJointTranslations meshJointTranslations) {
        for (Map.Entry entry : this.bones.entrySet()) {
            float[] fArr = meshJointTranslations.jointTranslations.get(entry.getKey());
            if (fArr != null) {
                ((SLSkeletonBone) entry.getValue()).setPositionOverride(new LLVector3(fArr[0], fArr[1], fArr[2]));
            }
        }
    }

    public float getBodySize() {
        SLSkeletonBone sLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis);
        SLSkeletonBone sLSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mSkull);
        SLSkeletonBone sLSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mHead);
        SLSkeletonBone sLSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mNeck);
        SLSkeletonBone sLSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mChest);
        SLSkeletonBone sLSkeletonBone6 = this.bones.get(SLSkeletonBoneID.mTorso);
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null || sLSkeletonBone6 == null) {
            return 0.0f;
        }
        double positionZ = (double) (sLSkeletonBone4.getPositionZ() * sLSkeletonBone5.getScaleZ());
        return (float) (((double) (sLSkeletonBone.getScaleZ() * sLSkeletonBone6.getPositionZ())) + positionZ + ((double) getPelvisToFoot()) + (Math.sqrt(2.0d) * ((double) (sLSkeletonBone2.getPositionZ() * sLSkeletonBone3.getScaleZ()))) + ((double) (sLSkeletonBone3.getPositionZ() * sLSkeletonBone4.getScaleZ())) + ((double) (sLSkeletonBone5.getPositionZ() * sLSkeletonBone6.getScaleZ())));
    }

    public float getPelvisToFoot() {
        SLSkeletonBone sLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis);
        SLSkeletonBone sLSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mHipLeft);
        SLSkeletonBone sLSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mKneeLeft);
        SLSkeletonBone sLSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mAnkleLeft);
        SLSkeletonBone sLSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mFootLeft);
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null) {
            return 0.0f;
        }
        return (((sLSkeletonBone.getScaleZ() * sLSkeletonBone2.getPositionZ()) - (sLSkeletonBone2.getScaleZ() * sLSkeletonBone3.getPositionZ())) - (sLSkeletonBone4.getPositionZ() * sLSkeletonBone3.getScaleZ())) - (sLSkeletonBone5.getPositionZ() * sLSkeletonBone4.getScaleZ());
    }

    /* access modifiers changed from: protected */
    public void prepareSkeleton() {
        this.rootBone.prepareSkeleton(this.updateBones, 0);
    }
}
