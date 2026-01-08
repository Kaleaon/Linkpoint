package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.EnumMap

abstract class SLSkeleton {
    protected val bones = EnumMap<SLSkeletonBoneID, SLSkeletonBone>(SLSkeletonBoneID::class.java)
    
    // jointWorldMatrix exposed via getter for MeshRiggingData
    protected val jointWorldMatrix = FloatArray(SLSkeletonBoneID.values().size * 16)
    
    protected lateinit var rootBone: SLSkeletonBone

    open fun applyJointTranslations(meshJointTranslations: MeshJointTranslations) {
        for ((key, value) in meshJointTranslations.jointTranslations) {
            val bone = bones[key]
            if (bone != null && value.size == 3) {
                bone.setBasePosition(LLVector3(value[0], value[1], value[2]))
            }
        }
    }

    open fun getBodySize(): Float = 0.0f

    open fun getJointWorldMatrix(): FloatArray = jointWorldMatrix

    open fun getPelvisToFoot(): Float = 0.0f

    open fun prepareSkeleton() {
        // Re-link hierarchy if needed or reset transforms
        for (bone in bones.values) {
            bone.reset()
        }
    }

    open fun UpdateGlobalPositions(animationSkeletonData: com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData) {
        // Apply animation matrices to bones
        val animMatrix = animationSkeletonData.getAnimMatrix()
        val animOffsets = animationSkeletonData.getAnimOffsets()
        
        // Update root
        rootBone.updateGlobalMatrix(animMatrix, animOffsets, jointWorldMatrix)
    }
}
