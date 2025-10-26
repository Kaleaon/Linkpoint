package com.linkpoint.slproto.avatar

import com.linkpoint.render.avatar.AnimationSkeletonData
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.types.LLVector3
import java.util.EnumMap
import java.util.Map

class SLSkeleton {
    val Map<SLSkeletonBoneID, SLSkeletonBone> bones = EnumMap(SLSkeletonBoneID.class)
    val FloatArray jointMatrix = Float[(SLSkeletonBoneID.VALUES.length * 16)]
    val FloatArray jointWorldMatrix = Float[((SLSkeletonBoneID.VALUES.length + 47) * 16)]
    public SLSkeletonBone rootBone
    private val Array<SLSkeletonBone> updateBones = SLSkeletonBone[SLSkeletonBoneID.VALUES.length]

    fun UpdateGlobalPositions(animationSkeletonData: AnimationSkeletonData) {
        for (SLSkeletonBone updateGlobalPos : this.updateBones) {
            updateGlobalPos.updateGlobalPos(animationSkeletonData, this.jointMatrix, this.jointWorldMatrix)
        }
    }

    /* access modifiers changed from: protected */
    fun applyJointTranslations(meshJointTranslations: MeshJointTranslations) {
        for (Map.Entry entry : this.bones.entrySet()) {
            val fArr: FloatArray = meshJointTranslations.jointTranslations.get(entry.getKey())
            if (fArr != null) {
                ((SLSkeletonBone) entry.getValue()).setPositionOverride(LLVector3(fArr[0], fArr[1], fArr[2]))
            }
        }
    }

     public fun getBodySize(): Float {
        val sLSkeletonBone: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis)
        val sLSkeletonBone2: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mSkull)
        val sLSkeletonBone3: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mHead)
        val sLSkeletonBone4: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mNeck)
        val sLSkeletonBone5: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mChest)
        val sLSkeletonBone6: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mTorso)
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null || sLSkeletonBone6 == null) {
            return 0.0f
        }
        val positionZ: Double = (Double) (sLSkeletonBone4.getPositionZ() * sLSkeletonBone5.getScaleZ())
        return (Float) (((Double) (sLSkeletonBone.getScaleZ() * sLSkeletonBone6.getPositionZ())) + positionZ + ((Double) getPelvisToFoot()) + (Math.sqrt(2.0d) * ((Double) (sLSkeletonBone2.getPositionZ() * sLSkeletonBone3.getScaleZ()))) + ((Double) (sLSkeletonBone3.getPositionZ() * sLSkeletonBone4.getScaleZ())) + ((Double) (sLSkeletonBone5.getPositionZ() * sLSkeletonBone6.getScaleZ())))
    }

     public fun getPelvisToFoot(): Float {
        val sLSkeletonBone: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mPelvis)
        val sLSkeletonBone2: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mHipLeft)
        val sLSkeletonBone3: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mKneeLeft)
        val sLSkeletonBone4: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mAnkleLeft)
        val sLSkeletonBone5: SLSkeletonBone = this.bones.get(SLSkeletonBoneID.mFootLeft)
        if (sLSkeletonBone == null || sLSkeletonBone2 == null || sLSkeletonBone3 == null || sLSkeletonBone4 == null || sLSkeletonBone5 == null) {
            return 0.0f
        }
        return (((sLSkeletonBone.getScaleZ() * sLSkeletonBone2.getPositionZ()) - (sLSkeletonBone2.getScaleZ() * sLSkeletonBone3.getPositionZ())) - (sLSkeletonBone4.getPositionZ() * sLSkeletonBone3.getScaleZ())) - (sLSkeletonBone5.getPositionZ() * sLSkeletonBone4.getScaleZ())
    }

    /* access modifiers changed from: protected */
    fun prepareSkeleton() {
        this.rootBone.prepareSkeleton(this.updateBones, 0)
    }
}
