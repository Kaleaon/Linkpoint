package com.linkpoint.slproto.mesh;

import com.linkpoint.slproto.avatar.SLSkeletonBoneID;
import java.util.EnumMap;

public class MeshJointTranslations {
    public final EnumMap<SLSkeletonBoneID, float[]> jointTranslations = new EnumMap<>(SLSkeletonBoneID.class);
    public float pelvisOffset = 0.0f;
}
