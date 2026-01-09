package com.linkpoint.slproto.mesh

import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import java.util.EnumMap

class MeshJointTranslations {
    val jointTranslations: EnumMap<SLSkeletonBoneID, FloatArray> = EnumMap(SLSkeletonBoneID::class.java)
    var pelvisOffset: Float = 0.0f
}