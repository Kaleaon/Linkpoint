package com.linkpoint.slproto.mesh

import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import java.util.EnumMap

class MeshJointTranslations {
    val EnumMap<SLSkeletonBoneID, Float[]> jointTranslations = EnumMap<>(SLSkeletonBoneID.class)
    public Float pelvisOffset = 0.0f
}
