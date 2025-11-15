package com.lumiyaviewer.lumiya.slproto.mesh

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID
import java.util.EnumMap

class MeshJointTranslations {
    val jointTranslations: EnumMap<SLSkeletonBoneID, FloatArray> = EnumMap(SLSkeletonBoneID::class.java)
    var pelvisOffset: Float = 0.0f
}