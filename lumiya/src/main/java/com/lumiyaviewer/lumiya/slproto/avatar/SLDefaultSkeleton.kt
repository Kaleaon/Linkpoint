package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.slproto.types.LLVector3

open class SLDefaultSkeleton : SLSkeleton() {
    init {
        val skull = SLSkeletonBone(SLSkeletonBoneID.mSkull, LLVector3(0.0f, 0.0f, 0.079f), LLVector3(0.0f, 0.0f, 0.079f), null, null)
        bones[SLSkeletonBoneID.mSkull] = skull
        
        val eyeRight = SLSkeletonBone(SLSkeletonBoneID.mEyeRight, LLVector3(0.098f, -0.036f, 0.079f), LLVector3(0.098466f, -0.036f, 0.079f), null, null)
        bones[SLSkeletonBoneID.mEyeRight] = eyeRight
        
        val eyeLeft = SLSkeletonBone(SLSkeletonBoneID.mEyeLeft, LLVector3(0.098f, 0.036f, 0.079f), LLVector3(0.098461f, 0.036f, 0.079f), null, null)
        bones[SLSkeletonBoneID.mEyeLeft] = eyeLeft
        
        // Add root bone logic
        val pelvis = SLSkeletonBone(SLSkeletonBoneID.mPelvis, LLVector3(0.0f, 0.0f, 1.067f), LLVector3(0.0f, 0.0f, 1.067015f), null, null)
        rootBone = pelvis
        bones[SLSkeletonBoneID.mPelvis] = pelvis
    }
}
