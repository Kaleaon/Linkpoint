package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class SLSkeleton {
    public SLSkeletonBone rootBone;
    public final Map<SLSkeletonBoneID, SLSkeletonBone> bones = new EnumMap(SLSkeletonBoneID.class);
    public final float[] jointMatrix = new float[SLSkeletonBoneID.VALUES.length * 16];
    public final float[] jointWorldMatrix = new float[(SLSkeletonBoneID.VALUES.length + 47) * 16];
    private final SLSkeletonBone[] updateBones = new SLSkeletonBone[SLSkeletonBoneID.VALUES.length];

    public void UpdateGlobalPositions(AnimationSkeletonData animationSkeletonData) {
        for (SLSkeletonBone sLSkeletonBone : this.updateBones) {
            sLSkeletonBone.updateGlobalPos(animationSkeletonData, this.jointMatrix, this.jointWorldMatrix);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyJointTranslations(MeshJointTranslations meshJointTranslations) {
        Iterator<T> it = this.bones.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
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
        return (float) ((sLSkeletonBone.getScaleZ() * sLSkeletonBone6.getPositionZ()) + (sLSkeletonBone4.getPositionZ() * sLSkeletonBone5.getScaleZ()) + getPelvisToFoot() + (Math.sqrt(2.0d) * sLSkeletonBone2.getPositionZ() * sLSkeletonBone3.getScaleZ()) + (sLSkeletonBone3.getPositionZ() * sLSkeletonBone4.getScaleZ()) + (sLSkeletonBone5.getPositionZ() * sLSkeletonBone6.getScaleZ()));
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

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareSkeleton() {
        this.rootBone.prepareSkeleton(this.updateBones, 0);
    }
}
