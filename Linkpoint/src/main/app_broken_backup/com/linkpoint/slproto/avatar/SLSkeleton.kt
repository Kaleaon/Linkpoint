package com.linkpoint.slproto.avatar

import kotlin.math.*

import com.linkpoint.render.avatar.AnimationSkeletonData
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.types.LLVector3
import java.util.EnumMap
import java.util.Map

class SLSkeleton {
    Map<SLSkeletonBoneID, SLSkeletonBone> bones = EnumMap(SLSkeletonBoneID.class)
    FloatArray jointMatrix = float[(SLSkeletonBoneID.VALUES.size * 16)]
    FloatArray jointWorldMatrix = float[((SLSkeletonBoneID.VALUES.size + 47) * 16)]
    SLSkeletonBone rootBone
    private SLSkeletonBone[] updateBones = SLSkeletonBone[SLSkeletonBoneID.VALUES.size]

    fun UpdateGlobalPositions(AnimationSkeletonData animationSkeletonData): Unit {
        for (SLSkeletonBone updateGlobalPos : this.updateBones) {
            updateGlobalPos.updateGlobalPos(animationSkeletonData, this.jointMatrix, this.jointWorldMatrix)
        }
    }

    /* access modifiers changed from: protected */
    fun applyJointTranslations(MeshJointTranslations meshJointTranslations): Unit {
        for (Map.Entry entry : this.bones.entrySet()) {
            FloatArray fArr = meshJointTranslations.jointTranslations.get(entry.getKey())
            if (fArr != null) {
                ((entry as SLSkeletonBone).getValue()).setPositionOverride(LLVector3(fArr[0], fArr[1], fArr[2]))
            }
        }
    }

    float getBodySize() {
        SLSkeletonBone sLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis)
        SLSkeletonBone sLSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mSkull)
        SLSkeletonBone sLSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mHead)
        SLSkeletonBone sLSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mNeck)
        SLSkeletonBone sLSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mChest)
        SLSkeletonBone sLSkeletonBone6 = this.bones.get(SLSkeletonBoneID.mTorso)
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null || sLSkeletonBone6 == null) {
            return 0.0f
        }
        double positionZ = (double) (sLSkeletonBone4.getPositionZ() * sLSkeletonBone5.getScaleZ())
        return (float) (((double) (sLSkeletonBone.getScaleZ() * sLSkeletonBone6.getPositionZ())) + positionZ + ((double) getPelvisToFoot()) + (sqrt(2.0d) * ((double) (sLSkeletonBone2.getPositionZ() * sLSkeletonBone3.getScaleZ()))) + ((double) (sLSkeletonBone3.getPositionZ() * sLSkeletonBone4.getScaleZ())) + ((double) (sLSkeletonBone5.getPositionZ() * sLSkeletonBone6.getScaleZ())))
    }

    float getPelvisToFoot() {
        SLSkeletonBone sLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis)
        SLSkeletonBone sLSkeletonBone2 = this.bones.get(SLSkeletonBoneID.mHipLeft)
        SLSkeletonBone sLSkeletonBone3 = this.bones.get(SLSkeletonBoneID.mKneeLeft)
        SLSkeletonBone sLSkeletonBone4 = this.bones.get(SLSkeletonBoneID.mAnkleLeft)
        SLSkeletonBone sLSkeletonBone5 = this.bones.get(SLSkeletonBoneID.mFootLeft)
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null) {
            return 0.0f
        }
        return (((sLSkeletonBone.getScaleZ() * sLSkeletonBone2.getPositionZ()) - (sLSkeletonBone2.getScaleZ() * sLSkeletonBone3.getPositionZ())) - (sLSkeletonBone4.getPositionZ() * sLSkeletonBone3.getScaleZ())) - (sLSkeletonBone5.getPositionZ() * sLSkeletonBone4.getScaleZ())
    }

    /* access modifiers changed from: protected */
    fun prepareSkeleton(): Unit {
        this.rootBone.prepareSkeleton(this.updateBones, 0)
    }
}
